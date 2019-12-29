package com.SoNe.Client;

import java.util.HashMap;
import java.util.Scanner;

import org.json.simple.JSONObject;

public class ClientCLI {

    static Scanner scan = new Scanner(System.in);
    public static void main( String[] args ) {
        mainMenu();



        //Socket socket = ServerComm.connectToServer();
        //DataInputStream in = ServerComm.getInputStream(socket);
        //DataOutputStream out = ServerComm.getOutputStream(socket);

        //out.writeUTF("Hello");
    }

    public static void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }

    public static void mainMenu() {
        String inp = "";

        while (!inp.equals("3")) {
            //clearScreen();
            System.out.println("--- Welcome to SoNe ---");
            System.out.println("1. Register\n2. Login\n3. Exit");
            System.out.print("Enter number: ");
            inp = scan.nextLine();

            switch(inp) {
                case "1":
                    registerMenu();
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
        String username = scan.nextLine();
        System.out.print("(Warning, program does not use encrypted communication between client and server yet, do not use a sensitive password)\nEnter password: ");
        String password = scan.nextLine();
        hashVal.put("username", username);
        hashVal.put("password", password);

        JSONObject json = new JSONObject(hashVal);

        JSONObject response = ServerComm.sendJSONToServer(json);
        System.out.println((String) response.get("message"));
    }
}
