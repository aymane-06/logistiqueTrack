package com.logitrack.logitrack.Util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PaswordUtil {
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public static String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}
