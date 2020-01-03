package com.SoNe.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

public class ClientHandler extends Thread {
    
    Socket sock;
    DataInputStream dataIn;
    DataOutputStream dataOut;

    public ClientHandler(Socket sock, DataInputStream dataIn, DataOutputStream dataOut) { 
        this.sock = sock;
        this.dataIn = dataIn;
        this.dataOut = dataOut;
    }

    @Override
    public void run() {
        // Handle clients

        String received = null;
        JSONParser parser = new JSONParser();
        try {                
            received = dataIn.readUTF(); 
            System.out.println("From client: " + received);
            JSONObject json = (JSONObject) parser.parse(received);


            JSONObject response = ServerToDatabase.identifyFunction(json);
            String responseString = response.toJSONString();

            System.out.println("To client: " + responseString);

            dataOut.writeUTF(responseString);

            sock.close();
            dataIn.close();
            dataOut.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}
