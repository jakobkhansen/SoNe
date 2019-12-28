package com.SoNe.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

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
    }
}
