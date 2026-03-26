package com.preschool.controller;

import com.preschool.dao.TeacherDAO;
import com.preschool.model.Teacher;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.Optional;


/**
 * Controller class responsible for handling UI events
 * and connecting the interface with backend logic
 */
public class TeacherManagementController {

    @FXML private TableView<Teacher> teacherTable;
    @FXML private TableColumn<Teacher, Integer> idColumn;
    @FXML private TableColumn<Teacher, String> nameColumn;
    @FXML private TableColumn<Teacher, String> genderColumn;
    @FXML private TableColumn<Teacher, String> phoneColumn;
    @FXML private TableColumn<Teacher, String> qualificationColumn;
    @FXML private TableColumn<Teacher, String> statusColumn;

    /* input field for teacher details */
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<String> genderCombo;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressArea;
    @FXML private TextField qualificationField;
    @FXML private DatePicker joiningDatePicker;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField searchField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final ObservableList<Teacher> teacherList = FXCollections.observableArrayList();
    private Teacher selectedTeacher = null;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("teacherId"));
        nameColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getFullName()));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        qualificationColumn.setCellValueFactory(new PropertyValueFactory<>("qualification"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        teacherTable.setItems(teacherList);

        genderCombo.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        statusCombo.setItems(FXCollections.observableArrayList("Active", "Inactive"));
        statusCombo.setValue("Active");
        joiningDatePicker.setValue(LocalDate.now());

        loadTeachers();

        teacherTable.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            if (nw != null) {
                selectedTeacher = nw;
                populateFields(nw);
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });
    }

    private void loadTeachers() {
        teacherList.clear();
        teacherList.addAll(teacherDAO.getAllTeachers());
    }

    @FXML
    private void handleAddTeacher() {
        // For Add, all fields including username and password are required
        if (!validateForAdd()) return;

        if (teacherDAO.usernameExists(usernameField.getText().trim())) {
            showError("Username already exists. Please choose a different username.");
            return;
        }

        Teacher teacher = buildTeacher();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (teacherDAO.addTeacherWithLogin(teacher, username, password)) {
            showSuccess("Teacher and login account created successfully!");
            loadTeachers();
            clearFields();
        } else {
            showError("Failed to add teacher.");
        }
    }

    @FXML
    private void handleUpdateTeacher() {
        if (selectedTeacher == null) { showError("Please select a teacher to update."); return; }
        // For Update, username and password are NOT required (login credentials are set at creation)
        if (!validateForUpdate()) return;
        Teacher t = buildTeacher();
        t.setTeacherId(selectedTeacher.getTeacherId());
        if (teacherDAO.updateTeacher(t)) {
            showSuccess("Teacher updated successfully!");
            loadTeachers();
            clearFields();
        } else {
            showError("Failed to update teacher.");
        }
    }

    @FXML
    private void handleDeleteTeacher() {
        if (selectedTeacher == null) { showError("Please select a teacher to delete."); return; }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setContentText("Delete " + selectedTeacher.getFullName() + "?\n" +
                "This will also delete their login account.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (teacherDAO.deleteTeacher(selectedTeacher.getTeacherId())) {
                showSuccess("Teacher deleted successfully!");
                loadTeachers();
                clearFields();
            } else {
                showError("Failed to delete teacher.");
            }
        }
    }

    @FXML
    private void handleSearch() {
        String kw = searchField.getText().trim();
        teacherList.clear();
        teacherList.addAll(kw.isEmpty() ? teacherDAO.getAllTeachers() : teacherDAO.searchTeachers(kw));
    }

    @FXML
    private void handleClearFields() { clearFields(); }

    /** Full validation used when adding a new teacher (includes login credentials). */
    private boolean validateForAdd() {
        if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty()
                || dobPicker.getValue() == null || genderCombo.getValue() == null
                || phoneField.getText().trim().isEmpty() || addressArea.getText().trim().isEmpty()
                || qualificationField.getText().trim().isEmpty() || joiningDatePicker.getValue() == null
                || usernameField.getText().trim().isEmpty() || passwordField.getText().trim().isEmpty()) {
            showError("Please fill all required fields (including username and password).");
            return false;
        }
        if (passwordField.getText().trim().length() < 6) {
            showError("Password must be at least 6 characters long.");
            return false;
        }
        return true;
    }

    /** Partial validation used when updating (login credentials not re-entered). */
    private boolean validateForUpdate() {
        if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty()
                || dobPicker.getValue() == null || genderCombo.getValue() == null
                || phoneField.getText().trim().isEmpty() || addressArea.getText().trim().isEmpty()
                || qualificationField.getText().trim().isEmpty() || joiningDatePicker.getValue() == null) {
            showError("Please fill all required fields.");
            return false;
        }
        return true;
    }

    private Teacher buildTeacher() {
        Teacher t = new Teacher();
        t.setFirstName(firstNameField.getText().trim());
        t.setLastName(lastNameField.getText().trim());
        t.setDateOfBirth(dobPicker.getValue());
        t.setGender(genderCombo.getValue());
        t.setPhone(phoneField.getText().trim());
        t.setEmail(emailField.getText().trim());
        t.setAddress(addressArea.getText().trim());
        t.setQualification(qualificationField.getText().trim());
        t.setJoiningDate(joiningDatePicker.getValue());
        t.setStatus(statusCombo.getValue());
        return t;
    }

    private void populateFields(Teacher t) {
        firstNameField.setText(t.getFirstName());
        lastNameField.setText(t.getLastName());
        dobPicker.setValue(t.getDateOfBirth());
        genderCombo.setValue(t.getGender());
        phoneField.setText(t.getPhone());
        emailField.setText(t.getEmail());
        addressArea.setText(t.getAddress());
        qualificationField.setText(t.getQualification());
        joiningDatePicker.setValue(t.getJoiningDate());
        statusCombo.setValue(t.getStatus());
        // Clear login fields when selecting a teacher for update
        usernameField.clear();
        passwordField.clear();
    }

    private void clearFields() {
        firstNameField.clear(); lastNameField.clear();
        dobPicker.setValue(null); genderCombo.setValue(null);
        phoneField.clear(); emailField.clear(); addressArea.clear();
        qualificationField.clear();
        joiningDatePicker.setValue(LocalDate.now());
        statusCombo.setValue("Active");
        usernameField.clear();
        passwordField.clear();
        selectedTeacher = null;
        teacherTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void showSuccess(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
