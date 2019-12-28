package com.SoNe.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ServerComm {

    public static JSONObject createJSONObj(String[] args, String[] values) {
        HashMap<String, String> hashObj = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            hashObj.put(args[i], values[i]);
        }
        return new JSONObject(hashObj);
    } 

    public static JSONObject send(DataInputStream in, DataOutputStream out, JSONObject v) {
        JSONParser parser = new JSONParser();
        String json = v.toJSONString();
        String answer = null;
        try {
            out.writeUTF(json);
            answer = in.readUTF();

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

    public static Socket connectToServer() {
        Properties settings = getSettings();
        String ip = settings.getProperty("server_ip");
        int port = Integer.parseInt(settings.getProperty("server_port"));
        InetAddress address = null;
        try {
            address = InetAddress.getByName(ip);
            return new Socket(address, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    

    public static Properties getSettings() {
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
