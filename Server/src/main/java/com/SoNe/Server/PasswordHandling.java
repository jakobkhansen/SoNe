package com.SoNe.Server;

import java.nio.charset.Charset;
import java.util.Random;

import com.lambdaworks.crypto.SCryptUtil;

public class PasswordHandling {

    public static String generateSalt() {
        Random generator = new Random();
        byte[] salt = new byte[16];
        generator.nextBytes(salt);
        return new String(salt, Charset.forName("UTF-8"));
    }

    public static String hashWithSalt(String password, String salt) {

        String salted = password + salt;

        // Scrypt workload parameters
        int N = (int) Math.pow(2, 20);
        int r = 8;
        int p = 1;

        return SCryptUtil.scrypt(salted, N, r, p);
    }

    public static boolean authenticatePass(String password, String salt, String hash) {
        String salted = password + salt;
        return SCryptUtil.check(salted, hash);
    }

}
