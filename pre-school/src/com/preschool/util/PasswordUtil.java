package com.preschool.util;

/**
 * Password utility class.
 */
public class PasswordUtil {

    /**
     * Returns the password as-is (plain text).
     * Replace the body with a hashing algorithm (e.g. BCrypt) for production use.
     */
    public static String password(String password) {
        return password;
    }

    /**
     * Verifies a password by direct equality comparison.
     */
    public static boolean verifyPassword(String inputPassword, String dbPassword) {
        return inputPassword != null && inputPassword.equals(dbPassword);
    }
}
