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

    // Buttons and stat boxes that may be hidden
    @FXML private Button teachersButton;
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

    /**
     * ④ POLYMORPHISM in action.
     *
     * We hold a reference to the abstract User type and call
     * canAccessModule() and getDashboardTitle() on it.
     *
     * At runtime, Java dispatches to Admin or TeacherUser's
     * implementation — NO instanceof, NO role string checks here.
     *
     * Admin.canAccessModule("Fees")        → true
     * TeacherUser.canAccessModule("Fees")  → false
     * Same call, different result, zero if/else on role.
     */
    private void applyRoleBasedVisibility() {
        User user = SessionManager.getInstance().getCurrentUser();

        // ── Top bar labels (polymorphic calls) ──────────────────
        if (loggedInUserLabel != null)
            loggedInUserLabel.setText(user.getFullName());
        if (userRoleLabel != null)
            userRoleLabel.setText(user.getRoleLabel());   // polymorphic

        // ── Nav buttons — shown only when user has permission ───
        // canAccessModule() dispatches to Admin or TeacherUser at runtime
        setVisible(teachersButton,  user.canAccessModule("Teachers"));
        setVisible(feesButton,      user.canAccessModule("Fees"));
        setVisible(reportsButton,   user.canAccessModule("Reports"));

        // ── Stat cards — same permission gate ───────────────────
        setVisible(teachersStatBox, user.canAccessModule("Teachers"));
        setVisible(feesStatBox,     user.canAccessModule("Fees"));
    }

    private void loadDashboardStats() {
        User user = SessionManager.getInstance().getCurrentUser();

        totalStudentsLabel.setText(String.valueOf(studentDAO.getTotalStudentCount()));
        presentTodayLabel.setText(String.valueOf(attendanceDAO.getPresentCountToday()));

        // Polymorphic gate — no instanceof, no isAdmin() check
        if (user.canAccessModule("Teachers"))
            totalTeachersLabel.setText(String.valueOf(teacherDAO.getTotalTeacherCount()));
        if (user.canAccessModule("Fees"))
            pendingFeesLabel.setText(String.valueOf(feeDAO.getPendingFeeCount()));
    }

    /** Utility to set both visible and managed in one call */
    private void setVisible(javafx.scene.Node node, boolean visible) {
        if (node != null) {
            node.setVisible(visible);
            node.setManaged(visible);
        }
    }

    @FXML private void handleStudentsButton()   { loadView("/fxml/StudentManagement.fxml"); }
    @FXML private void handleTeachersButton()   { loadView("/fxml/TeacherManagement.fxml"); }
    @FXML private void handleAttendanceButton() { loadView("/fxml/AttendanceManagement.fxml"); }
    @FXML private void handleFeesButton()       { loadView("/fxml/FeeManagement.fxml"); }
    @FXML private void handleReportsButton()    { loadView("/fxml/Reports.fxml"); }

    @FXML
    private void handleLogout() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to logout?",
                ButtonType.OK, ButtonType.CANCEL);
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
            new Alert(Alert.AlertType.ERROR,
                    "Failed to load: " + path, ButtonType.OK).showAndWait();
        }
    }
}
