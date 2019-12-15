package com.SoNe.Server;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

public class Database {

    static Properties settings = initializeSettings();
    static Connection databaseConnection = initializeDatabase(settings);

    public static void main(String[] args) {
        Set<String> test = settings.stringPropertyNames();
        for (String string : test) {
            System.out.println(string);
        }
    }

    public static boolean addUser


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
