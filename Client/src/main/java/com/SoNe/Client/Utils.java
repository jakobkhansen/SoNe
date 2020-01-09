package com.SoNe.Client;

import org.json.simple.JSONArray;

public class Utils {

    static String os = System.getProperty("os.name").toLowerCase();

    public static void clearScreen() {  
        try {
            if (os.indexOf("win") >= 0) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");  
                System.out.flush();  
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Check if input is a string or not
    public static boolean isNumeral(String input) {
        try {
            Integer.parseInt(input);

        } catch (NumberFormatException e) {
            return false;

        }

        return true;
    }

    public static boolean arrContains(String string, String[] arr) {
        for (String elem : arr) {
            if (elem.equals(string)) {
                return true;
            }
        }

        return false;
    }

    public static boolean arrContains(String string, JSONArray arr) {
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).equals(string)) {
                return true;
            }
        }

        return false;
    }

    public static boolean containsUsername(String username, JSONArray arr) {
        for (int i = 0; i < arr.size(); i++) {
            JSONArray user = (JSONArray) arr.get(i);
            if (user.get(0).equals(username)) {
                return true;
            }
        }
        return false;
    }
    
    public static String formatUname(String formatName) {
        if (formatName.length() == 0) {
            return formatName;
        }

        if (formatName.length() == 1) {
            return formatName.toUpperCase();
        }

        String firstLetter = formatName.substring(0,1).toUpperCase();
        String rest = formatName.substring(1).toLowerCase();
        return firstLetter + rest;
    }
}
