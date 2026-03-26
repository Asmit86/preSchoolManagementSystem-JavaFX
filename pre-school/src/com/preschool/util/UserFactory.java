package com.preschool.util;

import com.preschool.model.Admin;
import com.preschool.model.TeacherUser;
import com.preschool.model.User;


/**
 * Factory class used to create User objects based on role
 * This follows the Factory Design Pattern to centralize object creation
 */
public class UserFactory {


    /**
     * Private constructor to prevent object creation
     * (utility class - only static methods are used)
     */
    private UserFactory() {}


    /**
     * Creates and returns a User object depending on role
     * @return User object (Admin or TeacherUser)
     */
    public static User create(int userId, String username,
                              String fullName, String role,
                              int teacherId) {

        return switch (role.toUpperCase()) {
            case "ADMIN" ->
                    new Admin(userId, username, fullName);                   // create Admin object if role is ADMIN

            case "TEACHER" ->
                    new TeacherUser(userId, username, fullName, teacherId);  // create TeacherUser object if role is TEACHER

            default -> throw new IllegalArgumentException("Unknown role: " + role); // throw error if role is not recognized
        };
    }
}