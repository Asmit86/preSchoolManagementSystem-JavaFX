package com.preschool.util;

import com.preschool.model.Admin;
import com.preschool.model.TeacherUser;
import com.preschool.model.User;


public class UserFactory {

    private UserFactory() {}   // utility class — no instantiation

    public static User create(int userId, String username,
                              String fullName, String role) {
        return switch (role.toUpperCase()) {
            case "ADMIN"   -> new Admin(userId, username, fullName);
            case "TEACHER" -> new TeacherUser(userId, username, fullName);
            default -> throw new IllegalArgumentException(
                    "Unknown role: '" + role + "'. Expected ADMIN or TEACHER.");
        };
    }
}
