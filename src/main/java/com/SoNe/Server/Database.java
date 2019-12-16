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
        testpost.put("userId", "1");
        testpost.put("content", "hællæ vloggen");

        JSONObject testpost_json = new JSONObject(testpost);

        System.out.println(registerUser(testuser_json));
        System.out.println(addPost(testpost_json));


    }

    public static boolean registerUser(JSONObject values) {
        // Correct JSON type
        if (values.get("type") != "register") {
            return false;
        }

        // Gather values
        String username = (String) values.get("username");
        String hashed_password = (String) values.get("password");
        String salt = (String) values.get("salt");
        String query = "INSERT INTO users (username, hashed_password, salt) ";
        query += "VALUES (?,?,?)";

        // Check if username is avalable
        String testAvailable = "SELECT * FROM users WHERE username = ?";
        try {
            PreparedStatement availStatement = con.prepareStatement(testAvailable);
            availStatement.setString(1, username);
            ResultSet available = availStatement.executeQuery();

            if (available.next()) {
                System.out.println("Username not available");
                return false;
            }


            // Add user
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, hashed_password);
            statement.setString(3, salt);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addPost(JSONObject values) {
        if (values.get("type") != "add_post") {
            return false;
        }

        int userId = Integer.parseInt((String) values.get("userId"));
        String content = (String) values.get("content");

        String query = "INSERT INTO posts (postedbyuser, content) VALUES (?,?)";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setString(2, content);

            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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
