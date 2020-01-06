package com.SoNe.Client;

import org.json.simple.JSONArray;

public class Utils {
    public static void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
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
}
