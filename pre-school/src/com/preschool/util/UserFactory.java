package com.preschool.util;

import com.preschool.model.Admin;
import com.preschool.model.TeacherUser;
import com.preschool.model.User;


public class UserFactory {          // This class is a Factory class (used to create objects)

    private UserFactory() {}       // Private constructor prevents creating objects of this class

    public static User create(int userId, String username,
                              String fullName, String role,
                              int teacherId) {

        return switch (role.toUpperCase()) {
            case "ADMIN" ->
                    new Admin(userId, username, fullName);      // If role is ADMIN, create an Admin object

            case "TEACHER" ->                                   // If role is TEACHER, create a TeacherUser object
                    new TeacherUser(userId, username, fullName, teacherId);

            // If role is something else, throw an error
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        };
    }
}