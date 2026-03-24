package com.preschool.util;

public class PasswordUtil {

    // Store password as plain text
    public static String hashPassword(String password) {
        return password;
    }

    // Verify password
    public static boolean verifyPassword(String inputPassword, String dbPassword) {
        return inputPassword != null && inputPassword.equals(dbPassword);
    }
}
