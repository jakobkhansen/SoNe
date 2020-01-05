package com.SoNe.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

import javax.net.SocketFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ServerComm {

    private static JSONObject send(DataInputStream i, DataOutputStream o, JSONObject v) {
        JSONParser parser = new JSONParser();
        String json = v.toJSONString();
        String answer = null;
        try {
            o.writeUTF(json);
            answer = i.readUTF();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return (JSONObject) parser.parse(answer);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject sendJSONToServer(JSONObject json) {
        Socket sock = connectToServer();
        DataInputStream in = getInputStream(sock);
        DataOutputStream out = getOutputStream(sock);
        
        return send(in, out, json);
    }

    public static Socket connectToServer() {
        Properties settings = getSettings();
        String ip = settings.getProperty("server_ip");
        int port = Integer.parseInt(settings.getProperty("server_port"));
        InetAddress address = null;

        SocketFactory sockFac = SocketFactory.getDefault();
        Socket socket = null;

        try {
            address = InetAddress.getByName(ip);
            socket = sockFac.createSocket(address, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return socket;
    }

    private static DataInputStream getInputStream(Socket sock) {
        DataInputStream in = null;
        try {
            in = new DataInputStream(sock.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }

    private static DataOutputStream getOutputStream(Socket sock) {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(sock.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }
    

    private static Properties getSettings() {
        InputStream settings_file = null;
        Properties props = new Properties();
        try {
            ClassLoader classLoader = ServerComm.class.getClassLoader();
            settings_file = classLoader.getResource("settings.txt").openStream();
            props.load(settings_file);
        } catch (IOException e) {
            System.out.println("Cannot read settings file");
            System.exit(1);
        }
        return props;
    }
}
