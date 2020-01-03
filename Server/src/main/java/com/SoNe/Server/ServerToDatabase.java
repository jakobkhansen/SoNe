package com.SoNe.Server;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class ServerToDatabase {

    public static JSONObject identifyFunction(JSONObject values) {
        JSONObject response = null;
        String type = (String) values.get("type");

        switch(type) {
            case "register":
                response = registerUser(values);
                break;
            case "authenticate":
                response = authenticateUser(values);
                break;
            case "add_post":
                response = addPost(values);
                break;
        }

        System.out.println(response.toJSONString());
        return response;
    }

    public static JSONObject registerUser(JSONObject values) {

        ResponseEnum response = Database.registerUser(values);
        HashMap<String, String> returnHash = new HashMap<>();

        if (response == ResponseEnum.SUCCESS) {
            returnHash.put("status", "SUCCESS");
            returnHash.put("message", "Successfully registered user.");

        } else if (response == ResponseEnum.USERNAME_TAKEN) {
            returnHash.put("status", "FAILED");
            returnHash.put("message", "Username unavailable, please try another.");

        } else {
            returnHash.put("status", "FAILED");
            returnHash.put("message", "Unexpected error occured.");
        }

        return new JSONObject(returnHash);
    } 

    public static JSONObject authenticateUser(JSONObject values) {

        ResponseEnum response = Database.authenticateUser(values);
        HashMap<String, String> returnHash = new HashMap<>();

        if (response == ResponseEnum.AUTHENTICATED) {
            returnHash.put("status", "SUCCESS");
            returnHash.put("message", "Successfully authenticated user");

        } else if (response == ResponseEnum.NOT_AUTHENTICATED) {
            returnHash.put("status", "FAILED");
            returnHash.put("message", "Wrong user-pass combination.");

        } else {
            returnHash.put("status", "FAILED");
            returnHash.put("message", "Unexpected error occured.");
        }

        return new JSONObject(returnHash);
    }

    public static JSONObject addPost(JSONObject values) {
        HashMap<String, String> returnHash = new HashMap<>();
        ResponseEnum authResponse = Database.authenticateUser(values);
        
        if (authResponse != ResponseEnum.AUTHENTICATED) {
            returnHash.put("status", "FAILED");
            returnHash.put("message", "You are not authenticated.");
        } else {

            ResponseEnum postResponse = Database.addPost(values);

            if (postResponse == ResponseEnum.SUCCESS) {
                returnHash.put("status", "SUCCESS");
                returnHash.put("message", "Successfully added post.");

            } else {
                returnHash.put("status", "FAILED");
                returnHash.put("message", "Unexpected error occured.");
            }
        }
        return new JSONObject(returnHash);
    }
}
