package com.SoNe.Server;

import java.util.HashMap;

import org.json.simple.JSONArray;
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
            case "follow":
                response = followUser(values);
                break;
            case "unfollow":
                response = unfollowUser(values);
                break;
            case "follow_check":
                response = checkFollow(values);
                break;
            case "add_post":
                response = addPost(values);
                break;
            case "all_users":
                response = getAllUsers(values);
                break;
            case "global_posts":
            case "user_posts":
            case "followed_posts":
                response = getPosts(values);
                break;
        }

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
            returnHash.put("message", "Successfully logged in.");

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

        boolean authenticated = authenticate(values);

        HashMap<String, String> returnHash = new HashMap<>();
        
        if (!authenticated) {
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

    @SuppressWarnings("unchecked")
    public static JSONObject getAllUsers(JSONObject values) {
        JSONObject ret = new JSONObject();
        JSONArray users = Database.getAllUsers();

        if (users == null) {
            ret.put("status", "FAILED");
        } else {
            ret.put("status", "SUCCESS");
            ret.put("users", users);
        }

        return ret;

    }

    public static JSONObject followUser(JSONObject values) {

        boolean authenticated = authenticate(values);

        HashMap<String, String> returnHash = new HashMap<>();

        if (!authenticated) {
            returnHash.put("status", "FAILED");
            returnHash.put("message", "Could not authenticate...");
        } else {
            ResponseEnum followResponse = Database.follow(values);

            if (followResponse == ResponseEnum.SUCCESS) {
                returnHash.put("status", "SUCCESS");
            }
            
            if (followResponse == ResponseEnum.SQL_ERROR) {
                returnHash.put("status", "FAILED");
                returnHash.put("message", "Database could not process this request...");
            }
        }

        return new JSONObject(returnHash);
    }

    public static JSONObject unfollowUser(JSONObject values) {

        boolean authenticated = authenticate(values);

        HashMap<String, String> returnHash = new HashMap<>();

        if (!authenticated) {
            returnHash.put("status", "FAILED");
            returnHash.put("message", "Could not authenticate...");
        } else {
            ResponseEnum followResponse = Database.unfollow(values);

            if (followResponse == ResponseEnum.SUCCESS) {
                returnHash.put("status", "SUCCESS");
            }
            
            if (followResponse == ResponseEnum.SQL_ERROR) {
                returnHash.put("status", "FAILED");
                returnHash.put("message", "Database could not process this request...");
            }
        }

        return new JSONObject(returnHash);

    }

    // Should users be authenticated before checking follow? Probably unnecessary
    public static JSONObject checkFollow(JSONObject values) {
        HashMap<String, String> returnHash = new HashMap<>();
        ResponseEnum check = Database.followCheck(values);
        
        if (check == ResponseEnum.SQL_ERROR) {
            returnHash.put("status", "FAILED");
            returnHash.put("message", "Trouble connecting to the database, try later.");
        } else {
            returnHash.put("status", "SUCCESS");
            returnHash.put("follow_check", check.name());
        } 

        return new JSONObject(returnHash);
    }

    @SuppressWarnings("unchecked")
    public static JSONObject getPosts(JSONObject values) {
        JSONObject ret = new JSONObject();
        String type = (String) values.get("type");
        String[][] posts = null;
        JSONArray postsJSON = new JSONArray();

        switch(type) {
            case "global_posts":
                posts = Database.getGlobalPosts();
                break;
            case "user_posts":
                posts = Database.getUserPosts((String) values.get("username"));
                break;
            case "followed_posts":
                posts = Database.getFollowedPosts((String) values.get("username"));
                break;
        }

        if (posts == null) {
            ret.put("status", "FAILED");
            ret.put("message", "Could not gather posts.");
            return ret;
        }

        ret.put("status", "SUCCESS");

        for (String[] post : posts) {
            JSONArray postArr = new JSONArray();
            postArr.add(0, post[0]);
            postArr.add(1, post[1]);
            postArr.add(2, post[2]);
            postsJSON.add(postArr);
        }

        ret.put("posts", postsJSON);

        return ret;
    }

    public static JSONObject genAuthenticateObject(JSONObject values) {
        HashMap<String, String> hashVal = new HashMap<>();

        hashVal.put("type", "authenticate");
        hashVal.put("username", (String) values.get("username_auth"));
        hashVal.put("password", (String) values.get("password_auth"));

        return new JSONObject(hashVal);
    }

    public static boolean authenticate(JSONObject values) {
        JSONObject authJSON = genAuthenticateObject(values);
        JSONObject authResponse = authenticateUser(authJSON);
        System.out.println(authResponse.get("status"));

        return authResponse.get("status").equals("SUCCESS"); 
    }
}
