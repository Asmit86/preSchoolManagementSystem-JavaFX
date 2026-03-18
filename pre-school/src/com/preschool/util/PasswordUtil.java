package com.preschool.util;

import org.mindrot.jbcrypt.BCrypt;




public class PasswordUtil {

    // Compare plain text password with DB password
    public static boolean verifyPassword(String inputPassword, String dbPassword) {
        return inputPassword != null && inputPassword.equals(dbPassword);
    }

    // Optional: keep this if you plan to create users later
    public static String hashPassword(String password) {
        return password; // no hashing, store as plain text
    }
}
