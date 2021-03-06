package com.SoNe.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

import javax.net.ServerSocketFactory;

/* 
    This class handles threading and establishing communication with the client
    If you are looking for the threads that handle the client requests, 
    see ClientHandler.java
*/

public class ServerToClient {

    public static void main(String[] args) throws IOException {
        clearScreen();
        int portNum = Integer.parseInt(Database.settings.getProperty("server_port"));
        
        ServerSocket serverSocket = genServerSock(portNum);

        System.out.println("Serving clients...");
        // Server loop
        while (true) {

            try {
                Socket cSocket = serverSocket.accept();

                Logging.consoleLogConnect(cSocket);

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

    public static ServerSocket genServerSock(int portNum) {
        ServerSocketFactory socketFac = ServerSocketFactory.getDefault();
        ServerSocket serverSocket = null;
        try {
            serverSocket = socketFac.createServerSocket(portNum);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return serverSocket;
    } 

    public static void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }

}
