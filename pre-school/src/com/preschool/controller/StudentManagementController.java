package com.preschool.controller;

import com.preschool.dao.ClassDAO;
import com.preschool.dao.StudentDAO;
import com.preschool.model.SchoolClass;
import com.preschool.model.Student;
import com.preschool.model.TeacherUser;
import com.preschool.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class StudentManagementController {

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, Integer> idColumn;
    @FXML private TableColumn<Student, String>  nameColumn;
    @FXML private TableColumn<Student, String>  genderColumn;
    @FXML private TableColumn<Student, String>  guardianColumn;
    @FXML private TableColumn<Student, String>  phoneColumn;
    @FXML private TableColumn<Student, String>  classColumn;
    @FXML private TableColumn<Student, String>  statusColumn;

    @FXML private TextField  firstNameField;
    @FXML private TextField  lastNameField;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<String>      genderCombo;
    @FXML private TextField  guardianNameField;
    @FXML private TextField  guardianPhoneField;
    @FXML private TextField  guardianEmailField;
    @FXML private TextField  addressArea;
    @FXML private DatePicker enrollmentDatePicker;
    @FXML private ComboBox<SchoolClass> classCombo;
    @FXML private ComboBox<String>      behaviourCombo;
    @FXML private ComboBox<String>      statusCombo;
    @FXML private TextField  searchField;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    // DAO layer objects (handle all database operations, keeping controller clean)
    private final StudentDAO studentDAO = new StudentDAO();
    private final ClassDAO   classDAO   = new ClassDAO();

    // Observable list binds data to TableView dynamically
    private final ObservableList<Student> studentList = FXCollections.observableArrayList();
    private Student selectedStudent = null;

    // If the logged-in user is a Teacher, this holds their assigned class_id (-1 = no class assigned)
    private int teacherClassId = -1;

    @FXML
    public void initialize() {
        // Resolve teacher's assigned class if the logged-in user is a Teacher
        if (SessionManager.getInstance().isTeacher()) {
            TeacherUser tu = (TeacherUser) SessionManager.getInstance().getCurrentUser();
            classDAO.getAllClasses().stream()
                    .filter(c -> c.getTeacherId() != null && c.getTeacherId() == tu.getTeacherId())
                    .findFirst()
                    .ifPresent(c -> teacherClassId = c.getClassId());

            // Teachers may view and update students in their class, but not add or delete
            addButton.setDisable(true);
            addButton.setVisible(false);
            deleteButton.setDisable(true);
            deleteButton.setVisible(false);
        }

        setupTableColumns();
        setupComboBoxes();
        loadStudents();

        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            if (nw != null) {
                selectedStudent = nw;
                populateFields(nw);
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });
    }

    private void setupTableColumns() {
        // Maps table columns to Student object properties
        idColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        nameColumn.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleStringProperty(cd.getValue().getFullName()));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        guardianColumn.setCellValueFactory(new PropertyValueFactory<>("guardianName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("guardianPhone"));
        // Converts classId into readable class name
        classColumn.setCellValueFactory(cd -> {
            Student s = cd.getValue();
            String display = "—";
            if (s.getClassId() != null) {
                for (SchoolClass sc : classCombo.getItems()) {
                    if (sc.getClassId() == s.getClassId()) {
                        display = sc.getDisplayName();
                        break;
                    }
                }
            }
            return new javafx.beans.property.SimpleStringProperty(display);
        });
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        studentTable.setItems(studentList);
    }

    private void setupComboBoxes() {
        // Predefined dropdown values for consistency
        genderCombo.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        statusCombo.setItems(FXCollections.observableArrayList("Active", "Inactive"));
        statusCombo.setValue("Active");
        behaviourCombo.setItems(FXCollections.observableArrayList("Excellent", "Good", "Satisfactory", "Needs Improvement"));
        // Load classes from DB and display meaningful names in ComboBox
        List<SchoolClass> classes = classDAO.getAllClasses();
        classCombo.setItems(FXCollections.observableArrayList(classes));
        classCombo.setConverter(new javafx.util.StringConverter<>() {
            public String toString(SchoolClass sc) { return sc == null ? "" : sc.getDisplayName(); }
            public SchoolClass fromString(String s) { return null; }
        });

        enrollmentDatePicker.setValue(LocalDate.now());
    }

    private void loadStudents() {
        // Reloads student list based on role (teacher/admin)
        studentList.clear();
        if (teacherClassId > 0) {
            studentList.addAll(studentDAO.getStudentsByClassId(teacherClassId));
        } else {
            studentList.addAll(studentDAO.getAllStudents());
        }
    }

    @FXML
    private void handleAddStudent() {
        // Validate before creating object
        if (!validateFields()) return;
        Student student = createStudentFromFields();
        if (studentDAO.addStudent(student)) {
            showSuccess("Student added successfully!");
            loadStudents();
            clearFields();
        } else {
            showError("Failed to add student.");
        }
    }

    @FXML
    private void handleUpdateStudent() {
        // Must select a student before updating
        if (selectedStudent == null) { showError("Please select a student to update."); return; }
        if (!validateFields()) return;
        Student student = createStudentFromFields();
        // Preserve original ID for update operation
        student.setStudentId(selectedStudent.getStudentId());
        if (studentDAO.updateStudent(student)) {
            showSuccess("Student updated successfully!");
            loadStudents();
            clearFields();
        } else {
            showError("Failed to update student.");
        }
    }

    @FXML
    private void handleDeleteStudent() {
        // Prevent delete without selection
        if (selectedStudent == null) { showError("Please select a student to delete."); return; }
        // Confirmation dialog to prevent accidental deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete " + selectedStudent.getFullName() + "?", ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Confirm Delete");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (studentDAO.deleteStudent(selectedStudent.getStudentId())) {
                showSuccess("Student deleted successfully!");
                loadStudents();
                clearFields();
            } else {
                showError("Failed to delete student.");
            }
        }
    }

    @FXML
    private void handleClearFields() { clearFields(); }

    @FXML
    private void handleSearch() {
        // Dynamic search based on role and keyword
        String keyword = searchField.getText().trim();
        studentList.clear();
        if (teacherClassId > 0) {
            studentList.addAll(keyword.isEmpty()
                    ? studentDAO.getStudentsByClassId(teacherClassId)
                    : studentDAO.searchStudentsByClassId(keyword, teacherClassId));
        } else {
            studentList.addAll(keyword.isEmpty()
                    ? studentDAO.getAllStudents()
                    : studentDAO.searchStudents(keyword));
        }
    }

    private boolean validateFields() {
        // Ensures required fields are filled before submission
        if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty()
                || dobPicker.getValue() == null || genderCombo.getValue() == null
                || guardianNameField.getText().trim().isEmpty() || guardianPhoneField.getText().trim().isEmpty()
                || addressArea.getText().trim().isEmpty() || enrollmentDatePicker.getValue() == null) {
            showError("Please fill all required fields.");
            return false;
        }
        // Phone validation (exact 10 digits)
        if (!guardianPhoneField.getText().trim().matches("\\d{10}")) {
            showError("Guardian phone number must be exactly 10 digits.");
            return false;
        }
        // Basic email validation
        String email = guardianEmailField.getText().trim();
        if (!email.isEmpty() && (!email.contains("@") || !email.contains(".com"))) {
            showError("Guardian email must contain '@' and '.com'.");
            return false;
        }
        return true;
    }

    private Student createStudentFromFields() {
        // Converts UI input into Student object (DTO creation)
        Student s = new Student();
        s.setFirstName(firstNameField.getText().trim());
        s.setLastName(lastNameField.getText().trim());
        s.setDateOfBirth(dobPicker.getValue());
        s.setGender(genderCombo.getValue());
        s.setGuardianName(guardianNameField.getText().trim());
        s.setGuardianPhone(guardianPhoneField.getText().trim());
        s.setGuardianEmail(guardianEmailField.getText().trim());
        s.setAddress(addressArea.getText().trim());
        s.setEnrollmentDate(enrollmentDatePicker.getValue());
        s.setStatus(statusCombo.getValue());
        s.setBehaviour(behaviourCombo.getValue());
        SchoolClass sc = classCombo.getValue();
        // Assign class if selected
        if (sc != null) { s.setClassId(sc.getClassId()); s.setSection(sc.getSection()); }
        return s;
    }

    private void populateFields(Student s) {
        // Loads selected student data into form for editing
        firstNameField.setText(s.getFirstName());
        lastNameField.setText(s.getLastName());
        dobPicker.setValue(s.getDateOfBirth());
        genderCombo.setValue(s.getGender());
        guardianNameField.setText(s.getGuardianName());
        guardianPhoneField.setText(s.getGuardianPhone() != null ? s.getGuardianPhone() : "");
        guardianEmailField.setText(s.getGuardianEmail() != null ? s.getGuardianEmail() : "");
        addressArea.setText(s.getAddress());
        enrollmentDatePicker.setValue(s.getEnrollmentDate());
        statusCombo.setValue(s.getStatus());
        behaviourCombo.setValue(s.getBehaviour());
        // Set corresponding class in dropdown
        if (s.getClassId() != null) {
            classCombo.getItems().stream()
                    .filter(sc -> sc.getClassId() == s.getClassId())
                    .findFirst().ifPresent(classCombo::setValue);
        }
    }

    private void clearFields() {
        // Resets form to default state
        firstNameField.clear(); lastNameField.clear();
        dobPicker.setValue(null); genderCombo.setValue(null);
        guardianNameField.clear(); guardianPhoneField.clear();
        guardianEmailField.clear(); addressArea.clear();
        enrollmentDatePicker.setValue(LocalDate.now());
        classCombo.setValue(null); behaviourCombo.setValue(null);
        statusCombo.setValue("Active");

        selectedStudent = null;

        // Clear table selection and disable actions
        studentTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    // Utility methods for consistent user feedback
    private void showSuccess(String msg) { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }
    private void showError(String msg)   { new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait(); }
}
