package com.SoNe.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientCLI {
    public static void main( String[] args ) {
        System.out.println( "Hello World!" );
        Socket socket = ServerComm.connectToServer();
        DataInputStream in = null;
        DataOutputStream out = null;

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("Hello");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
