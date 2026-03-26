package com.preschool.controller;

import com.preschool.MainApp;
import com.preschool.dao.UserDAO;
import com.preschool.model.User;
import com.preschool.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;


/**
 * Controller class for handling login functionality
 */
public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;

    // UserDAO handles all database access — no SQL in this controller
    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);

        // Allow login by pressing Enter in the password field
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

        User user = userDAO.findByCredentials(username, password);

        if (user != null) {
            SessionManager.getInstance().login(user);
            MainApp.showDashboard();
        } else {
            showError("Invalid username or password.");
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}
