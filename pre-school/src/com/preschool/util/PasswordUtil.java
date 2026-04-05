package com.preschool.util;

/**
 * Password utility class.
 */
public class PasswordUtil {


    public static String password(String password) {
        return password;
    }

    /*  Verifies a password by direct equality comparison. */
    public static boolean verifyPassword(String inputPassword, String dbPassword) {
        return inputPassword != null && inputPassword.equals(dbPassword);
    }
}
