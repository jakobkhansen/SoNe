package com.SoNe.Client;

import java.util.HashMap;
import java.util.Scanner;

import org.json.simple.JSONObject;

public class ClientCLI {

    static Scanner scan = new Scanner(System.in);

    // Session tokens instead? This seems unsafe
    static String username = null;
    static String password = null;

    public static void main( String[] args ) {
        mainMenu();



        //Socket socket = ServerComm.connectToServer();
        //DataInputStream in = ServerComm.getInputStream(socket);
        //DataOutputStream out = ServerComm.getOutputStream(socket);

        //out.writeUTF("Hello");
    }

    public static void mainMenu() {
        String inp = "";

        while (!inp.equals("3")) {
            clearScreen();
            System.out.println("--- Welcome to SoNe ---");
            System.out.println("1. Register\n2. Login\n3. Exit");
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
        clearScreen();
    }

    public static void registerMenu() {
        HashMap<String, String> hashVal = new HashMap<>();
        hashVal.put("type", "register");

        clearScreen();
        System.out.println("--- Register user ---");

        System.out.print("Enter desired username: ");
        String regUsername = scan.nextLine();

        System.out.print("(Warning, program does not use encrypted communication between client and server yet, do not use a sensitive password)\nEnter password: ");
        String regPassword = scan.nextLine();

        hashVal.put("username", regUsername);
        hashVal.put("password", regPassword);

        JSONObject json = new JSONObject(hashVal);

        JSONObject response = ServerComm.sendJSONToServer(json);
        clearScreen();
        System.out.println((String) response.get("message"));
        System.out.println("Press enter to continue.");
        scan.nextLine();
    }

    public static void login() {
        HashMap<String, String> hashVal = new HashMap<>();
        hashVal.put("type", "authenticate");

        clearScreen();
        System.out.println("--- Login ---");

        System.out.print("Username: ");
        username = scan.nextLine();

        System.out.print("Password: ");
        password = scan.nextLine();

        hashVal.put("username", username);
        hashVal.put("password", password);

        JSONObject json = new JSONObject(hashVal);

        JSONObject response = ServerComm.sendJSONToServer(json);
        clearScreen();
        System.out.println((String) response.get("message"));
        System.out.println("Press enter to continue.");
        scan.nextLine();

        if (response.get("jj"))
    }

    public static void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }
}
