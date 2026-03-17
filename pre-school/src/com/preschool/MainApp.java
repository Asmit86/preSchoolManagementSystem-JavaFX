package com.preschool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;
            showLoginScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showLoginScreen() {
        try {
            Parent root = FXMLLoader.load(MainApp.class.getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(root);
            
            primaryStage.setTitle("Pre-School Management System - Login");
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showDashboard() {
        try {
            Parent root = FXMLLoader.load(MainApp.class.getResource("/fxml/Dashboard.fxml"));
            Scene scene = new Scene(root, 1200, 700);
            scene.getStylesheets().add(MainApp.class.getResource("/css/style.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Pre-School Management System - Dashboard");
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
