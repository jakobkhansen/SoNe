package com.SoNe.Server;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.sql.Statement;

import org.json.simple.JSONObject;

public class Database {

    static Properties settings = initializeSettings();
    static Connection con = initializeDatabase(settings);


    public static ResponseEnum registerUser(JSONObject values) {

        // Gather values
        String username = (String) values.get("username");
        String password = (String) values.get("password");
        String salt = PasswordHandling.generateSalt();
        String hashed_password = PasswordHandling.hashWithSalt(password, salt);

        String usernameQuery = "SELECT username FROM users WHERE username = ?";

        String insertQuery = "INSERT INTO users (username, hashed_password, salt) ";
        insertQuery += "VALUES (?,?,?)";

        try {

            // Check if username taken
            PreparedStatement usernameStatement = con.prepareStatement(usernameQuery);
            usernameStatement.setString(1, username);
            ResultSet users = usernameStatement.executeQuery();

            if (users.next()) {
                return ResponseEnum.USERNAME_TAKEN;
            }

            // Username not taken, add user
            PreparedStatement statement = con.prepareStatement(insertQuery);
            statement.setString(1, username);
            statement.setString(2, hashed_password);
            statement.setString(3, salt);

            int numAffected = statement.executeUpdate();

            if (numAffected == 1) {
                System.out.println("User " + username + " successfully registered.");
                return ResponseEnum.SUCCESS;
            } else {
                return ResponseEnum.SQL_ERROR;
            }

        } catch (SQLException e) {
            return ResponseEnum.SQL_ERROR;
        }
    } 

    public static ResponseEnum addPost(JSONObject values) {

        // Gather values
        int userId = getUserId((String) values.get("username"));
        String content = (String) values.get("content");

        if (userId == -1) {
            return ResponseEnum.USER_NON_EXISTENT;
        }

        // Execute query
        String query = "INSERT INTO posts (postedbyuser, content) VALUES (?,?)";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setLong(1, userId);
            statement.setString(2, content);

            int numAffected = statement.executeUpdate();

            return numAffected == 1 ? ResponseEnum.SUCCESS : ResponseEnum.SQL_ERROR;

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEnum.SQL_ERROR;
        }
    }

    public static ResponseEnum authenticateUser(JSONObject values) {

        // Gather values
        String username = (String) values.get("username");
        String userGivenPassword = (String) values.get("password");
        int userId = getUserId(username);
        String salt = getSalt(userId);
        String userHashedPass = getHashedPass(userId);

        // Test if values are correct
        if (userId == -1) {
            return ResponseEnum.NOT_AUTHENTICATED;
        }

        if (salt == null || userHashedPass == null) {
            return ResponseEnum.SQL_ERROR;
        }

        // Authenticate
        if (PasswordHandling.authenticatePass(userGivenPassword, salt, userHashedPass)) {
            return ResponseEnum.AUTHENTICATED;
        } 

        return ResponseEnum.NOT_AUTHENTICATED;
    }

    public static String getSalt(int userId) {
        String query = "SELECT salt FROM users WHERE userId = ?";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, userId);

            ResultSet salt = statement.executeQuery();

            if (salt.next() && salt.isLast()) {
                return salt.getString(1);
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public static String getHashedPass(int userId) {
        String query = "SELECT hashed_password FROM users WHERE userId = ?";

        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, userId);

            ResultSet hashed_password = statement.executeQuery();

            if (hashed_password.next() && hashed_password.isLast()) {
                return hashed_password.getString(1);
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public static int getUserId(String username) {
        String query = "SELECT userId FROM users WHERE username = ?";

        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, username);

            ResultSet userId = statement.executeQuery();

            if (userId.next() && userId.isLast()) {
                return userId.getInt(1);
            } else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String getAllUsers() {
        String query = "SELECT username FROM users";
        Statement statement = null;
        String users = null;

        try {
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery(query); 

            users = "";

            while (rs.next()) {
                users += rs.getString(1) + ";";
            }
            if (users.length() > 0) {
                users = users.substring(0, users.length() - 1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } 

        return users;
    }  

    public static ResponseEnum follow(JSONObject values) {
        int user1id = getUserId((String) values.get("username_auth"));
        int user2id = getUserId((String) values.get("user_to_follow"));

        String query = "INSERT INTO following (user1id, user2id) VALUES (?,?)";
        
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, user1id);
            statement.setInt(2, user2id);

            int numAffected = statement.executeUpdate();

            return numAffected == 1 ? ResponseEnum.SUCCESS : ResponseEnum.SQL_ERROR;

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEnum.SQL_ERROR;
        }
    }

    public static ResponseEnum unfollow(JSONObject values) {
        
        int user1id = getUserId((String) values.get("username_auth"));
        int user2id = getUserId((String) values.get("user_to_unfollow"));

        String query = "DELETE FROM following WHERE user1id = ? AND user2id = ?";
        
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, user1id);
            statement.setInt(2, user2id);

            int numAffected = statement.executeUpdate();

            return numAffected == 1 ? ResponseEnum.SUCCESS : ResponseEnum.SQL_ERROR;

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEnum.SQL_ERROR;
        }
    }

    public static ResponseEnum followCheck(JSONObject values) {

        int userId1 = getUserId((String) values.get("username1"));
        int userId2 = getUserId((String) values.get("username2"));

        if (userId1 == userId2) {
            return ResponseEnum.SAME_USER;
        }

        String query = "SELECT * FROM following WHERE user1id = ? AND user2id = ?";
        ResultSet rs = null;
        
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, userId1);
            statement.setInt(2, userId2);

            rs = statement.executeQuery(); 

            if (rs.next()) {
                if (rs.isLast()) {
                    return ResponseEnum.FOLLOWS;
                }
                return ResponseEnum.UNEXPECTED_ERROR;

            }
            return ResponseEnum.NOT_FOLLOWS;


        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEnum.SQL_ERROR;
        }
    }


    // Boring initialization methods
    public static Properties initializeSettings() {
        
        InputStream settings_file = null;
        Properties props = new Properties();
        try {
            ClassLoader classLoader = Database.class.getClassLoader();
            settings_file = classLoader.getResource("settings.txt").openStream();
            props.load(settings_file);

        } catch (IOException e) {
            System.out.println("Cannot read settings file");
            System.exit(1);
        }
        return props;
    } 

    public static Connection initializeDatabase(Properties settings) {
        String url = settings.getProperty("database_url");
        String username = settings.getProperty("database_user");
        String password = settings.getProperty("database_password");

        try {
            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Connection con = null;
        try {
            con = DriverManager.getConnection(url, username, password);

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Successfully connected to database!");
        return con;
    }

}
