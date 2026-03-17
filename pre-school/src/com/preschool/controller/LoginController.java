package com.preschool.controller;

import com.preschool.MainApp;
import com.preschool.model.User;
import com.preschool.util.DatabaseUtil;
import com.preschool.util.SessionManager;
import com.preschool.util.UserFactory;
import com.preschool.util.PasswordUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

import java.sql.*;

public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password.");
            return;
        }

        User user = authenticate(username, password);
        if (user != null) {
            SessionManager.getInstance().login(user);
            MainApp.showDashboard();
        } else {
            showError("Invalid username or password.");
        }
    }


    private User authenticate(String username, String password) {
        String sql = "SELECT user_id, username, full_name, role, password " +
                "FROM users WHERE username=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");

                // Verify password using BCrypt
                if (PasswordUtil.verifyPassword(password, hashedPassword)) {
                    // UserFactory decides whether to return Admin or TeacherUser
                    return UserFactory.create(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("full_name"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error: " + e.getMessage());
        }
        return null;
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}
