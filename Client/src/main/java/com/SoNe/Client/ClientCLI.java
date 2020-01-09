package com.SoNe.Client;

import java.util.ArrayList;
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
        String regUsername = Utils.formatUname(scan.nextLine());

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
        username = Utils.formatUname(scan.nextLine());

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
                    displayFeed();
                    break;
                case "2":
                    globalPosts();
                    break;
                case "3":
                    displayAllUsers();
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
        int endKey = -1;
        int option = 0;

        while (option != endKey) {

            Utils.clearScreen();
            System.out.println("--- " + wall_username + "'s profile ---");

            ArrayList<String> options = new ArrayList<>();

            options.add("View posts.");
            options.add("View followers.");

            String follows = checkFollow(wall_username);

            if (follows.equals("FOLLOWS")) {
                options.add("Unfollow.");
            }

            if (follows.equals("NOT_FOLLOWS")) {
                options.add("Follow.");
            }

            options.add("Back.");
            endKey = options.size();

            for (int i = 0; i < options.size(); i++) {
                System.out.println((i + 1) + ": " + options.get(i));
            }

            System.out.print("\nEnter number: ");
            option = Integer.parseInt(scan.nextLine());

            if (option >= endKey) {
                continue;
            }

            String choice = options.get(option - 1);
            System.out.println(choice);

            switch (choice) {
                case "View posts.":
                    userPosts(wall_username);
                    break;
                case "View followers.":
                    displayFollowers(wall_username);
                    break;
                case "Follow.":
                    System.out.print("Following...");
                    followUser(wall_username);
                    break;
                case "Unfollow.":
                    System.out.print("Unfollowing...");
                    unfollowUser(wall_username);
                    break;
            }
        }
    }

    public static void displayFeed() {
        Utils.clearScreen();
        HashMap<String, String> request = new HashMap<>();
        request.put("type", "followed_posts");
        request.put("username", username);

        JSONObject response = ServerComm.sendJSONToServer(new JSONObject(request));
        JSONArray posts = (JSONArray) response.get("posts");

        if (posts.size() == 0) {
            System.out.println("No posts found. Maybe you don't follow anyone?");
        }

        for (int i = 0; i < posts.size(); i++) {
            JSONArray post = (JSONArray) posts.get(i);
            printPost(post);
        }
        System.out.print("Enter to go back: ");
        scan.nextLine();
    }

    public static void displayAllUsers() {
        HashMap<String, String> hashVal = new HashMap<>();
        hashVal.put("type", "all_users");
        JSONObject response = ServerComm.sendJSONToServer(new JSONObject(hashVal));

        displayUsers(response);
    }

    public static void displayFollowers(String followed_username) {
        HashMap<String, String> hashVal = new HashMap<>();
        hashVal.put("type", "followers");
        hashVal.put("username", followed_username);
        JSONObject response = ServerComm.sendJSONToServer(new JSONObject(hashVal));

        displayUsers(response);
    }

    public static void displayUsers(JSONObject serverResponse) {
        Utils.clearScreen();
        
        if (serverResponse.get("status").equals("FAILED")) {
            System.out.print("An unexpected error occured\nPress Enter to go back.");
            scan.nextLine();

        } else {

            JSONArray users = (JSONArray) serverResponse.get("users");

            for (int i = 0; i < users.size(); i++) {
                JSONArray user = (JSONArray) users.get(i);
                String username = (String) user.get(0);
                String followers = (String) user.get(1);
                String out = (i + 1) + ": " + username + ", " + followers + " followers.";
                System.out.println(out);
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
                JSONArray user = (JSONArray) users.get(Integer.parseInt(inp) - 1);
                displayWall((String) user.get(0));
            } else if (isNum && !numInRange) {
                System.out.println("Number not in range... Press enter to go back");
                scan.nextLine();
            } else {
                inp = Utils.formatUname(inp);
                System.out.println(inp);
                if (Utils.containsUsername(inp, users)) {
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


        System.out.println(content);

        if (content.length() > 280) {
            System.out.println("Post too long, 280 chars max.");

        } else {
            hashVal.put("content", content);
            System.out.print("Submitting post...");
            JSONObject response = ServerComm.sendJSONToServer(new JSONObject(hashVal));
            Utils.clearScreen();
            System.out.println(response.get("message"));
        }
        System.out.print("Press enter to go back: ");
        scan.nextLine();
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

        System.out.print("Enter to go back: ");
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

        System.out.print("Enter to go back: ");
        scan.nextLine();
    }

    public static void printPost(JSONArray post) {
        String postUsername = (String) post.get(0);
        String content = (String) post.get(1);
        String date = (String) post.get(2);

        System.out.println(postUsername + " at " + date + ": ");
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
