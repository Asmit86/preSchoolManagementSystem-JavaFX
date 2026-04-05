package com.preschool.controller;

import com.preschool.MainApp;
import com.preschool.dao.UserDAO;
import com.preschool.model.User;
import com.preschool.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;

    // UserDAO handles all database access — no SQL in this controller
    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        // Hide error message initially when UI loads
        errorLabel.setVisible(false);

        // Improves UX: allows user to press Enter instead of clicking login button
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });
    }

    @FXML
    private void handleLogin() {
        // Trim removes unnecessary spaces from user input
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Basic validation to prevent empty login attempts
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password.");
            return;
        }

        // Delegates authentication logic to DAO (separation of concerns)
        User user = userDAO.findByCredentials(username, password);

        // Navigate to main dashboard after successful login
        if (user != null) {
            SessionManager.getInstance().login(user);
            MainApp.showDashboard();
        } else {
            showError("Invalid username or password.");        // Display error if authentication fails
        }
    }

    // Centralized method to display error messages on UI
    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}
