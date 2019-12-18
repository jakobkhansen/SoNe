package com.SoNe.Server;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import org.json.simple.JSONObject;

public class Database {

    static Properties settings = initializeSettings();
    static Connection con = initializeDatabase(settings);

    public static void main(String[] args) {
        HashMap<String, String> testuser = new HashMap<>();
        testuser.put("type", "register");
        testuser.put("username", "hanoi");
        testuser.put("password", "hanoipassword");
        testuser.put("salt", "testsalt");
        JSONObject testuser_json = new JSONObject(testuser);

        HashMap<String, String> testpost = new HashMap<>();
        testpost.put("type", "add_post");
        testpost.put("username", "hanoi");
        testpost.put("content", "hællæ vloggen");

        JSONObject testpost_json = new JSONObject(testpost);

        System.out.println(registerUser(testuser_json));
        System.out.println(addPost(testpost_json));

        HashMap<String, String> testauthentication = new HashMap<>();
        testauthentication.put("type", "authenticate");
        testauthentication.put("username", "banoi");
        testauthentication.put("password", "hanoipassword");

        JSONObject testauthentication_json = new JSONObject(testauthentication);

        System.out.println("AUTHENTICATED: " + authenticateUser(testauthentication_json));


    }

    public static ResponseEnum registerUser(JSONObject values) {
        // Correct JSON type
        if (values.get("type") != "register") {
            return ResponseEnum.UNEXPECTED_ERROR;
        }

        // Gather values
        String username = (String) values.get("username");
        String password = (String) values.get("password");
        String salt = PasswordHandling.generateSalt();
        String hashed_password = PasswordHandling.hashWithSalt(password, salt);

        String query = "INSERT INTO users (username, hashed_password, salt) ";
        query += "VALUES (?,?,?)";
        try {

            // Add user
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, hashed_password);
            statement.setString(3, salt);

            int numAffected = statement.executeUpdate();
            return numAffected == 1 ? ResponseEnum.SUCCESS : ResponseEnum.SQL_ERROR;

        // Will return false if something goes wrong or username is taken
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEnum.SQL_ERROR;
        }
    } 

    // TODO how to authenticate a new post? Only call this after authentication
    public static ResponseEnum addPost(JSONObject values) {

        // Check for malformed JSON
        if (values.get("type") != "add_post") {
            return ResponseEnum.UNEXPECTED_ERROR;
        }

        // Gather values
        int userId = getUserId((String) values.get("username"));
        System.out.println("ID" + userId);
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

        // Check for malformed JSON
        if (values.get("type") != "authenticate") {
            return ResponseEnum.UNEXPECTED_ERROR;
        }

        // Gather values
        String username = (String) values.get("username");
        String userGivenPassword = (String) values.get("password");
        int userId = getUserId(username);
        String salt = getSalt(userId);
        String userHashedPass = getHashedPass(userId);
        System.out.println("IDTEST: " + userId);
        System.out.println("salt: " + salt);

        // Test if values are correct
        if (userId == -1) {
            return ResponseEnum.USER_NON_EXISTENT;
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

            System.out.println("BEFORE QUERY");
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
