package com.preschool.util;

import com.preschool.model.Admin;
import com.preschool.model.TeacherUser;
import com.preschool.model.User;

public class UserFactory {

    private UserFactory() {}

    public static User create(int userId, String username,
                              String fullName, String role,
                              int teacherId) {

        return switch (role.toUpperCase()) {
            case "ADMIN" ->
                    new Admin(userId, username, fullName);

            case "TEACHER" ->
                    new TeacherUser(userId, username, fullName, teacherId);

            default -> throw new IllegalArgumentException("Unknown role: " + role);
        };
    }
}