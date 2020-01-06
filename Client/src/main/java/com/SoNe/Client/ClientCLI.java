package com.SoNe.Client;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ClientCLI {

    static Scanner scan = new Scanner(System.in, "UTF-8");

    // Session tokens instead? This seems unsafe
    static String username = null;
    static String password = null;

    public static void main( String[] args ) {
        mainMenu();
    }

    public static void mainMenu() {
        String inp = "";

        while (!inp.equals("3")) {
            Utils.clearScreen();
            System.out.println("--- Welcome to SoNe ---");
            System.out.println("1. Register\n2. Login\n3. Exit\n");
            System.out.print("Enter number: ");
            inp = scan.nextLine();

            switch(inp) {
                case "1":
                    registerMenu();
                    break;
                case "2":
                    login();
                    break;

            }
        }
        Utils.clearScreen();
    }

    public static void registerMenu() {
        HashMap<String, String> hashVal = new HashMap<>();
        hashVal.put("type", "register");

        Utils.clearScreen();
        System.out.println("--- Register user ---");

        System.out.print("Enter desired username: ");
        String regUsername = scan.nextLine();

        System.out.print("(Warning, program does not use encrypted communication between client and server yet, do not use a sensitive password)\nEnter password: ");
        String regPassword = scan.nextLine();

        hashVal.put("username", regUsername);
        hashVal.put("password", regPassword);

        JSONObject json = new JSONObject(hashVal);

        JSONObject response = ServerComm.sendJSONToServer(json);

        Utils.clearScreen();
        System.out.println((String) response.get("message"));
        System.out.println("Press enter to continue.");
        scan.nextLine();
    }

    public static void login() {
        HashMap<String, String> hashVal = new HashMap<>();
        hashVal.put("type", "authenticate");

        Utils.clearScreen();
        System.out.println("--- Login ---");

        System.out.print("Username: ");
        username = scan.nextLine();

        System.out.print("Password: ");
        password = scan.nextLine();

        hashVal.put("username", username);
        hashVal.put("password", password);

        JSONObject json = new JSONObject(hashVal);

        JSONObject response = ServerComm.sendJSONToServer(json);
        Utils.clearScreen();
        System.out.println((String) response.get("message"));
        System.out.println("Press enter to continue.");
        scan.nextLine();

        if (response.get("status").equals("SUCCESS")) {
            dashboard();
        }
    }

    public static void dashboard() {
        String inp = "";

        while (!inp.equals("6")) {
            String menu = "1. Feed\n";
            menu += "2. Latest posts global\n";
            menu += "3. Users\n";
            menu += "4. My profile\n";
            menu += "5. New Post\n";
            menu += "6. Logout";
            Utils.clearScreen();
            System.out.println("--- Welcome to SoNe, " + username + " ---");
            System.out.println(menu);
            System.out.print("\nEnter number: ");

            inp = scan.nextLine();

            switch (inp) {
                case "1":
                    break;
                case "2":
                    globalPosts();
                    break;
                case "3":
                    displayUsers();
                    break;
                case "4":
                    displayWall(username);
                    break;
                case "5":
                    writePost();
                    break;
            }
        }
        
    }

    public static void displayWall(String wall_username) {
        String inp = "";
        String endKey = "-1";

        while (!inp.equals(endKey)) {

            Utils.clearScreen();
            System.out.println("--- " + wall_username + "'s profile ---");

            String follows = checkFollow(wall_username);

            String extraOptions = "";

            switch (follows) {
                case "FOLLOWS":
                    extraOptions += "\n2. Unfollow\n3. Back";
                    endKey = "3";
                    break;
                case "NOT_FOLLOWS":
                    extraOptions += "\n2. Follow\n3. Back";
                    endKey = "3";
                    break;
                default:
                    extraOptions += "\n2. Back";
                    endKey = "2";
            }

            System.out.println("1. View posts" + extraOptions);
            System.out.print("\nEnter number: ");
            inp = scan.nextLine();

            switch (inp) {
                case "1":
                    userPosts(wall_username);
                    break;
                case "2":
                    if (!username.equals(wall_username)) {
                        String followRes = null;
                        if (follows.equals("NOT_FOLLOWS")) {

                            System.out.print("Following...");
                            followRes = followUser(wall_username);
                        } else if (follows.equals("FOLLOWS")) {

                            System.out.print("Unfollowing...");
                            followRes = unfollowUser(wall_username);
                        }

                        if (!followRes.equals("SUCCESS")) {
                            System.out.println(followRes);
                            scan.nextLine();
                        }
                    }
                    
                    break;
            }
        }
    }

    public static void displayUsers() {
        Utils.clearScreen();
        HashMap<String, String> hashVal = new HashMap<>();
        hashVal.put("type", "all_users");
        JSONObject response = ServerComm.sendJSONToServer(new JSONObject(hashVal));

        if (response.get("status").equals("FAILED")) {
            System.out.print("An unexpected error occured\nPress Enter to go back.");
            scan.nextLine();

        } else {

            JSONArray users = (JSONArray) response.get("users");

            for (int i = 0; i < users.size(); i++) {
                System.out.println("" + (i + 1) + ": " + users.get(i));
            }
            System.out.println("\nUsername or number to go to profile");
            System.out.print("Enter only to go back: ");
            String inp = scan.nextLine();

            boolean isNum = Utils.isNumeral(inp);
            boolean numInRange = false;

            if (inp.equals("")) {
                return;
            }

            if (isNum) {
                int num = Integer.parseInt(inp);
                numInRange = num > 0 && num < users.size() + 1;
            }

            if (isNum && numInRange) {
                displayWall((String) users.get(Integer.parseInt(inp) - 1));
            } else if (isNum && !numInRange) {
                System.out.println("Number not in range... Press enter to go back");
                scan.nextLine();
            } else {
                if (Utils.arrContains(inp, users)) {
                    displayWall(inp);
                }
                else {
                    System.out.println("Invalid username entered. Press enter to go back");
                    scan.nextLine();
                }
            }
        }
    }

    public static void writePost() {
        Utils.clearScreen();
        HashMap<String, String> hashVal = new HashMap<>();
        hashVal.put("type", "add_post");
        hashVal.put("username_auth", username);
        hashVal.put("password_auth", password);
        System.out.println("Write post: ");
        String content = scan.nextLine();

        try {
            content = new String(content.getBytes(), "CP850");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Utils.clearScreen();
        if (content.length() > 280) {
            System.out.println("Post too long, 280 chars max.");

        } else {
            hashVal.put("content", content);
            JSONObject response = ServerComm.sendJSONToServer(new JSONObject(hashVal));
            System.out.println(response.get("message"));
            scan.nextLine();
        }
    }

    public static void globalPosts() {
        Utils.clearScreen();
        HashMap<String, String> request = new HashMap<>();
        request.put("type", "global_posts");
        JSONObject response = ServerComm.sendJSONToServer(new JSONObject(request));
        JSONArray posts = (JSONArray) response.get("posts");

        for (int i = 0; i < posts.size(); i++) {
            JSONArray post = (JSONArray) posts.get(i);
            printPost(post);
        }

        scan.nextLine();
    }

    public static void userPosts(String posts_username) {
        Utils.clearScreen();
        HashMap<String, String> request = new HashMap<>();
        request.put("type", "user_posts");
        request.put("username", posts_username);

        JSONObject response = ServerComm.sendJSONToServer(new JSONObject(request));
        JSONArray posts = (JSONArray) response.get("posts");

        for (int i = 0; i < posts.size(); i++) {
            JSONArray post = (JSONArray) posts.get(i);
            printPost(post);
        }

        scan.nextLine();
    }

    public static void printPost(JSONArray post) {
        String postUsername = (String) post.get(0);
        String content = (String) post.get(1);
        String date = (String) post.get(2);

        System.out.println(date + " " + postUsername + ": ");
        System.out.println(content + "\n");
    }

    public static String checkFollow(String other_username) {
        HashMap<String, String> hashVal = new HashMap<>();

        hashVal.put("type", "follow_check");
        hashVal.put("username1", username);
        hashVal.put("username2", other_username);

        JSONObject response = ServerComm.sendJSONToServer(new JSONObject(hashVal));
        if (response.get("status").equals("FAILED")) {
            return "ERROR";
        }

        return (String) response.get("follow_check");
    }

    public static String followUser(String other_user) {
        HashMap<String, String> hashVal = new HashMap<>();

        hashVal.put("type", "follow");
        hashVal.put("username_auth", username);
        hashVal.put("password_auth", password);
        hashVal.put("user_to_follow", other_user);

        JSONObject response = ServerComm.sendJSONToServer(new JSONObject(hashVal));

        String status = (String) response.get("status");

        if (status.equals("SUCCESS")) {
            return "SUCCESS";
        }

        return (String) response.get("message");
    }

    public static String unfollowUser(String other_user) {
        HashMap<String, String> hashVal = new HashMap<>();

        hashVal.put("type", "unfollow");
        hashVal.put("username_auth", username);
        hashVal.put("password_auth", password);
        hashVal.put("user_to_unfollow", other_user);

        JSONObject response = ServerComm.sendJSONToServer(new JSONObject(hashVal));

        String status = (String) response.get("status");

        if (status.equals("SUCCESS")) {
            return "SUCCESS";
        }

        return (String) response.get("message");
    }
}
