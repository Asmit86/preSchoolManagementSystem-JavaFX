package com.preschool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



/**
 * Main entry point of the Preschool Management System
 * This class launches the JavaFX application
 */
public class MainApp extends Application {

    private static Stage primaryStage;      // main stage used across the app

    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;         // store primary stage
            showLoginScreen();            // load first screen
        } catch (Exception e) {
            e.printStackTrace();          // print error if app fails to start
        }
    }


    /**
     * Loads the login screen
     */
    public static void showLoginScreen() {
        try {
            Parent root = FXMLLoader.load(MainApp.class.getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(MainApp.class.getResource("/css/style.css").toExternalForm());

            primaryStage.setTitle("Pre-School Management System - Login");
            primaryStage.setResizable(true);

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the Dashboard after successful login
     */
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
    }      // launch JavaFX app
}
