/*package com.normasiso.normaiso9001.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashTest {
    public static void main(String[] args) {
        String rawPassword = "1234";
        String hash = "$2a$10$..dWc/oCv8ocWIm5fK6kV.zc.EQoXZcy3FgHl/qmYC2iBmWn5Q70a";

        boolean ok = new BCryptPasswordEncoder().matches(rawPassword, hash);
        System.out.println("Concide la contraseña '1234'? "+ok);
        String hash = new BCryptPasswordEncoder().encode(rawPassword);
        System.out.println("hash generado para '" +rawPassword+ "':");
        System.out.println(hash);
    }
}*/
