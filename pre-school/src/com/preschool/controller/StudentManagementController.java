package com.preschool.controller;

import com.preschool.dao.StudentDAO;
import com.preschool.model.Student;
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
public class StudentManagementController {

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, Integer> idColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> genderColumn;
    @FXML private TableColumn<Student, String> guardianColumn;
    @FXML private TableColumn<Student, String> phoneColumn;
    @FXML private TableColumn<Student, String> statusColumn;

    /* input field for student details */
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<String> genderCombo;
    @FXML private TextField guardianNameField;
    @FXML private TextField guardianPhoneField;
    @FXML private TextField guardianEmailField;
    @FXML private TextField addressArea;
    @FXML private DatePicker enrollmentDatePicker;
    @FXML private ComboBox<String> classCombo;
    @FXML private TextField sectionField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField searchField;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;

    private StudentDAO studentDAO = new StudentDAO();
    private ObservableList<Student> studentList = FXCollections.observableArrayList();
    private Student selectedStudent = null;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupComboBoxes();
        loadStudents();

        // Table selection listener
        studentTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedStudent = newSelection;
                        populateFields(newSelection);
                        updateButton.setDisable(false);
                        deleteButton.setDisable(false);
                    }
                }
        );
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        nameColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName()));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        guardianColumn.setCellValueFactory(new PropertyValueFactory<>("guardianName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("guardianPhone"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        studentTable.setItems(studentList);
    }

    private void setupComboBoxes() {
        genderCombo.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        statusCombo.setItems(FXCollections.observableArrayList("Active", "Inactive"));
        classCombo.setItems(FXCollections.observableArrayList("Nursery", "LKG", "UKG"));

        statusCombo.setValue("Active");
    }

    private void loadStudents() {
        studentList.clear();
        studentList.addAll(studentDAO.getAllStudents());
    }

    @FXML
    private void handleAddStudent() {
        if (!validateFields()) return;

        Student student = createStudentFromFields();

        if (studentDAO.addStudent(student)) {
            showSuccess("Student added successfully!");
            loadStudents();
            clearFields();
        } else {
            showError("Failed to add student");
        }
    }

    @FXML
    private void handleUpdateStudent() {
        if (selectedStudent == null) {
            showError("Please select a student to update");
            return;
        }

        if (!validateFields()) return;

        Student student = createStudentFromFields();
        student.setStudentId(selectedStudent.getStudentId());

        if (studentDAO.updateStudent(student)) {
            showSuccess("Student updated successfully!");
            loadStudents();
            clearFields();
        } else {
            showError("Failed to update student");
        }
    }

    @FXML
    private void handleDeleteStudent() {
        if (selectedStudent == null) {
            showError("Please select a student to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Delete Student");
        alert.setContentText("Are you sure you want to delete " + selectedStudent.getFullName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (studentDAO.deleteStudent(selectedStudent.getStudentId())) {
                showSuccess("Student deleted successfully!");
                loadStudents();
                clearFields();
            } else {
                showError("Failed to delete student");
            }
        }
    }

    @FXML
    private void handleClearFields() {
        clearFields();
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadStudents();
        } else {
            studentList.clear();
            studentList.addAll(studentDAO.searchStudents(keyword));
        }
    }

    private boolean validateFields() {
        if (firstNameField.getText().trim().isEmpty() ||
                lastNameField.getText().trim().isEmpty() ||
                dobPicker.getValue() == null ||
                genderCombo.getValue() == null ||
                guardianNameField.getText().trim().isEmpty() ||
                guardianPhoneField.getText().trim().isEmpty() ||
                addressArea.getText().trim().isEmpty() ||
                enrollmentDatePicker.getValue() == null) {

            showError("Please fill all required fields");
            return false;
        }

        // Phone validation
        String phone = guardianPhoneField.getText().trim();
        if (phone.length() < 10) {
            showError("Phone number must be at least 10 digits.");
            return false;
        }

        // Email validation
        String email = guardianEmailField.getText().trim();
        if (!email.isEmpty() && (!email.contains("@") || !email.endsWith(".com"))) {
            showError("Invalid email format.");
            return false;
        }

        return true;
    }

    private Student createStudentFromFields() {
        Student student = new Student();
        student.setFirstName(firstNameField.getText().trim());
        student.setLastName(lastNameField.getText().trim());
        student.setDateOfBirth(dobPicker.getValue());
        student.setGender(genderCombo.getValue());
        student.setGuardianName(guardianNameField.getText().trim());
        student.setGuardianPhone(guardianPhoneField.getText().trim());
        student.setGuardianEmail(guardianEmailField.getText().trim());
        student.setAddress(addressArea.getText().trim());
        student.setEnrollmentDate(enrollmentDatePicker.getValue());
        student.setSection(sectionField.getText().trim());
        student.setStatus(statusCombo.getValue());

        // Map class name to ID (simplified)
        String className = classCombo.getValue();
        if (className != null) {
            switch (className) {
                case "Nursery": student.setClassId(1); break;
                case "LKG": student.setClassId(2); break;
                case "UKG": student.setClassId(3); break;
            }
        }

        return student;
    }

    private void populateFields(Student student) {
        firstNameField.setText(student.getFirstName());
        lastNameField.setText(student.getLastName());
        dobPicker.setValue(student.getDateOfBirth());
        genderCombo.setValue(student.getGender());
        guardianNameField.setText(student.getGuardianName());
        guardianPhoneField.setText(student.getGuardianPhone());
        guardianEmailField.setText(student.getGuardianEmail());
        addressArea.setText(student.getAddress());
        enrollmentDatePicker.setValue(student.getEnrollmentDate());
        sectionField.setText(student.getSection());
        statusCombo.setValue(student.getStatus());

        // Map class ID to name
        if (student.getClassId() != null) {
            switch (student.getClassId()) {
                case 1: classCombo.setValue("Nursery"); break;
                case 2: classCombo.setValue("LKG"); break;
                case 3: classCombo.setValue("UKG"); break;
            }
        }
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        dobPicker.setValue(null);
        genderCombo.setValue(null);
        guardianNameField.clear();
        guardianPhoneField.clear();
        guardianEmailField.clear();
        addressArea.clear();
        enrollmentDatePicker.setValue(LocalDate.now());
        classCombo.setValue(null);
        sectionField.clear();
        statusCombo.setValue("Active");

        selectedStudent = null;
        studentTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
