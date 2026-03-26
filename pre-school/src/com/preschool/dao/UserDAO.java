package com.preschool.dao;

import com.preschool.model.User;
import com.preschool.util.DatabaseUtil;
import com.preschool.util.UserFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * Looks up a user by username and verifies the plain text password
     * and returns the appropriate User type (Admin or TeacherUser).
     * Returns null if the credentials are invalid or a DB error occurs.
     */
    public User findByCredentials(String username, String password) {

        String sql = "SELECT user_id, username, full_name, role, password, teacher_id " +
                "FROM users WHERE username = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                // Plain-text comparison (replace with BCrypt for production)
                if (password.equals(storedPassword)) {
                    int teacherId = rs.getInt("teacher_id");
                    if (rs.wasNull()) teacherId = -1;

                    return UserFactory.create(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("full_name"),
                            rs.getString("role"),
                            teacherId
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
