package com.SoNe.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/* 
    This class handles threading and establishing communication with the client
    If you are looking for the threads that handle the client requests, 
    see ClientHandler.java
*/

public class ClientServer {

    public static void main(String[] args) throws IOException {
        int portNum = Integer.parseInt(Database.settings.getProperty("server_port"));
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNum);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Server loop
        while (true) {

            try {
                Socket cSocket = serverSocket.accept();
                DataInputStream dataIn = new DataInputStream(cSocket.getInputStream());
                DataOutputStream dataOut = new DataOutputStream(cSocket.getOutputStream());

                Thread handler = new ClientHandler(cSocket, dataIn, dataOut);

                handler.start();

            } catch (IOException e) {
                e.printStackTrace();
                serverSocket.close();
                System.exit(1);
            }
        }
    }
}
