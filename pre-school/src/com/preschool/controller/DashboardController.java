package com.preschool.controller;

import com.preschool.MainApp;
import com.preschool.dao.AttendanceDAO;
import com.preschool.dao.FeeDAO;
import com.preschool.dao.StudentDAO;
import com.preschool.dao.TeacherDAO;
import com.preschool.model.User;
import com.preschool.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class DashboardController {

    @FXML private BorderPane mainBorderPane;
    @FXML private Label      totalStudentsLabel;
    @FXML private Label      totalTeachersLabel;
    @FXML private Label      presentTodayLabel;
    @FXML private Label      pendingFeesLabel;
    @FXML private Label      loggedInUserLabel;
    @FXML private Label      userRoleLabel;

    @FXML private Button teachersButton;
    @FXML private Button classesButton;
    @FXML private Button feesButton;
    @FXML private Button reportsButton;
    @FXML private VBox   teachersStatBox;
    @FXML private VBox   feesStatBox;

    private final StudentDAO    studentDAO    = new StudentDAO();
    private final TeacherDAO    teacherDAO    = new TeacherDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final FeeDAO        feeDAO        = new FeeDAO();

    @FXML
    public void initialize() {
        applyRoleBasedVisibility();
        loadDashboardStats();
        loadView("/fxml/StudentManagement.fxml");
    }

    private void applyRoleBasedVisibility() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (loggedInUserLabel != null) loggedInUserLabel.setText(user.getFullName());
        if (userRoleLabel != null)     userRoleLabel.setText(user.getRoleLabel());

        setVisible(teachersButton,  user.canAccessModule("Teachers"));
        setVisible(classesButton,   user.canAccessModule("Classes"));
        setVisible(feesButton,      user.canAccessModule("Fees"));
        setVisible(reportsButton,   user.canAccessModule("Reports"));
        setVisible(teachersStatBox, user.canAccessModule("Teachers"));
        setVisible(feesStatBox,     user.canAccessModule("Fees"));
    }

    private void loadDashboardStats() {
        User user = SessionManager.getInstance().getCurrentUser();
        totalStudentsLabel.setText(String.valueOf(studentDAO.getTotalStudentCount()));
        presentTodayLabel.setText(String.valueOf(attendanceDAO.getPresentCountToday()));
        if (user.canAccessModule("Teachers"))
            totalTeachersLabel.setText(String.valueOf(teacherDAO.getTotalTeacherCount()));
        if (user.canAccessModule("Fees"))
            pendingFeesLabel.setText(String.valueOf(feeDAO.getPendingFeeCount()));
    }

    private void setVisible(javafx.scene.Node node, boolean visible) {
        if (node != null) { node.setVisible(visible); node.setManaged(visible); }
    }

    @FXML private void handleStudentsButton()   { loadView("/fxml/StudentManagement.fxml"); }
    @FXML private void handleTeachersButton()   { loadView("/fxml/TeacherManagement.fxml"); }
    @FXML private void handleClassesButton()    { loadView("/fxml/ClassManagement.fxml"); }
    @FXML private void handleAttendanceButton() { loadView("/fxml/AttendanceManagement.fxml"); }
    @FXML private void handleFeesButton()       { loadView("/fxml/FeeManagement.fxml"); }
    @FXML private void handleReportsButton()    { loadView("/fxml/Reports.fxml"); }

    @FXML
    private void handleLogout() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to logout?", ButtonType.OK, ButtonType.CANCEL);
        a.setTitle("Logout");
        if (a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            SessionManager.getInstance().logout();
            MainApp.showLoginScreen();
        }
    }

    private void loadView(String path) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(path));
            mainBorderPane.setCenter(view);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load: " + path, ButtonType.OK).showAndWait();
        }
    }
}
