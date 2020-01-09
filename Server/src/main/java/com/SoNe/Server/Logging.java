package com.SoNe.Server;

import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logging {

        
    static void consoleLogConnect(Socket s) {
        System.out.println(getTime() + "Connected to client at: " + getAddr(s));
    }

    static void consoleLogJSON(Socket s, String jsonPackage, String direction) {
        System.out.println(getTime() + direction + " " + getAddr(s) + ": " + jsonPackage);
    }

    static String getTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        return "[" + dtf.format(now) + "] ";
    }

    static String getAddr(Socket s) {
        return s.getInetAddress().toString();
    }
}
