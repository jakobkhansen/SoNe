package com.SoNe.Server;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Database {

    static Properties settings = initializeSettings();
    static Connection con = initializeDatabase(settings);

    public static String[][] execRsQuery(String query, Object[] parameters) {
        ResultSet rs = null;

        try {
            PreparedStatement statement = con.prepareStatement(query);

            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i+1, parameters[i]);
            }

            rs = statement.executeQuery();
            return rsToArray(rs);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String[][] execRsQuery(String query) {
        ResultSet rs = null;

        try {
            Statement statement = con.createStatement();

            rs = statement.executeQuery(query);
            return rsToArray(rs);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean execInsertQuery(String query, Object[] parameters) {
        try {
            PreparedStatement statement = con.prepareStatement(query);

            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i+1, parameters[i]);
            }

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String[][] rsToArray(ResultSet rs) throws SQLException {
        ArrayList<String[]> arrList = new ArrayList<>();
        int numColumns = rs.getMetaData().getColumnCount();

        while (rs.next()) {
            String[] row = new String[numColumns];
            for (int i = 0; i < numColumns; i++) {
                row[i] = rs.getString(i+1);
            }
            arrList.add(row);
        }

        String[][] ret = new String[arrList.size()][numColumns];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = arrList.get(i);
        }
        return ret;
    }


    public static ResponseEnum registerUser(JSONObject values) {

        // Gather values
        String username = (String) values.get("username");
        String password = (String) values.get("password");
        String salt = PasswordHandling.generateSalt();
        String hashed_password = PasswordHandling.hashWithSalt(password, salt);

        String usernameQuery = "SELECT username FROM users WHERE username = ?";

        String insertQuery = "INSERT INTO users (username, hashed_password, salt) ";
        insertQuery += "VALUES (?,?,?)";
        Object[] insertParams = new Object[]{username, hashed_password, salt};

        String[][] usernames = execRsQuery(usernameQuery, new Object[]{username});

        if (usernames.length > 0) {
            return ResponseEnum.USERNAME_TAKEN;
        }

        boolean successful = execInsertQuery(insertQuery, insertParams);

        return successful ? ResponseEnum.SUCCESS : ResponseEnum.SQL_ERROR;
    } 

    public static ResponseEnum addPost(JSONObject values) {

        // Gather values
        int userId = getUserId((String) values.get("username_auth"));
        String content = (String) values.get("content");

        if (userId == -1) {
            return ResponseEnum.USER_NON_EXISTENT;
        }

        // Execute query
        String query = "INSERT INTO posts (postedbyuser, content) VALUES (?,?)";
        boolean successful = execInsertQuery(query, new Object[]{userId, content});

        return successful ? ResponseEnum.SUCCESS : ResponseEnum.SQL_ERROR;
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
        String[][] salt = execRsQuery(query, new Object[]{userId});

        if (salt.length == 1) {
            return salt[0][0];
        }

        return null;
    }

    public static String getHashedPass(int userId) {
        String query = "SELECT hashed_password FROM users WHERE userId = ?";

        String[][] hashed_password = execRsQuery(query, new Object[]{userId});

        if (hashed_password.length == 1) {
            return hashed_password[0][0];
        }

        return null;
    }

    public static int getUserId(String username) {
        String query = "SELECT userId FROM users WHERE username = ?";

        String[][] userId = execRsQuery(query, new Object[]{username});

        if (userId.length == 1) {
            return Integer.parseInt(userId[0][0]);
        }

        return -1;
    }

    public static String getUsername(int userId) {
        String query = "SELECT username FROM users WHERE userId = ?";

        String[][] username = execRsQuery(query, new Object[]{userId});

        if (username.length == 1) {
            return username[0][0];
        }

        return null; 
    }

    @SuppressWarnings("unchecked")
    public static JSONArray getAllUsers() {
        String query = "SELECT username FROM users";

        String[][] users = execRsQuery(query);

        JSONArray ret = new JSONArray();
        for (String[] user : users) {
            ret.add(user[0]);
        }

        return ret;
    }  

    public static String[][] getGlobalPosts() {
        String query = "SELECT u.username, p.content, p.posted_at FROM posts AS p ";
        query += "INNER JOIN users AS u ON (p.postedbyuser = u.userid) ";
        query += "ORDER BY p.posted_at ASC ";
        query += "LIMIT 50";

        String[][] ret = execRsQuery(query);

        return ret;
    }

    public static String[][] getUserPosts(String username) {
        String query = "SELECT u.username, p.content, p.posted_at FROM posts AS p ";
        query += "INNER JOIN users AS u ON (p.postedbyuser = u.userid) ";
        query += "WHERE postedbyuser = ?";

        String[][] ret = execRsQuery(query, new Object[]{getUserId(username)});
        
        return ret;
    }

    public static String[][] getFollowedPosts(String username) {
        String query = "SELECT u2.username, p.content, p.posted_at FROM users AS u1 ";
        query += "INNER JOIN following AS f ON (u1.userid = f.user1id) ";
        query += "INNER JOIN users AS u2 ON (f.user2id = u2.userid) ";
        query += "INNER JOIN posts AS p ON ";
        query += "(u2.userid = p.postedbyuser) ";
        query += "WHERE u1.username = ?\n";
        query += "UNION\n";
        query += "SELECT u.username, p.content, p.posted_at FROM posts AS p ";
        query += "INNER JOIN users AS u ON (p.postedbyuser = u.userid) ";
        query += "WHERE u.username = ?";

        String[][] ret = execRsQuery(query, new Object[]{username, username});

        return ret;   
    }

    public static ResponseEnum follow(JSONObject values) {
        int user1id = getUserId((String) values.get("username_auth"));
        int user2id = getUserId((String) values.get("user_to_follow"));

        String query = "INSERT INTO following (user1id, user2id) VALUES (?,?)";

        boolean success = execInsertQuery(query, new Object[]{user1id, user2id});

        
        return success ? ResponseEnum.SUCCESS : ResponseEnum.SQL_ERROR;
        
    }

    public static ResponseEnum unfollow(JSONObject values) {
        
        int user1id = getUserId((String) values.get("username_auth"));
        int user2id = getUserId((String) values.get("user_to_unfollow"));

        String query = "DELETE FROM following WHERE user1id = ? AND user2id = ?";

        boolean successful = execInsertQuery(query, new Object[]{user1id, user2id});

        
        return successful ? ResponseEnum.SUCCESS : ResponseEnum.SQL_ERROR;
    }

    public static ResponseEnum followCheck(JSONObject values) {

        int userId1 = getUserId((String) values.get("username1"));
        int userId2 = getUserId((String) values.get("username2"));

        if (userId1 == userId2) {
            return ResponseEnum.SAME_USER;
        }

        String q = "SELECT DISTINCT * FROM following WHERE user1id = ? AND user2id = ?";

        String[][] follows = execRsQuery(q, new Object[]{userId1, userId2});


        if (follows.length == 1) {
            return ResponseEnum.FOLLOWS;
        }

        return ResponseEnum.NOT_FOLLOWS;
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
