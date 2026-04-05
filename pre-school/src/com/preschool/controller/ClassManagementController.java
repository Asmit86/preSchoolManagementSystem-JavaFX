package com.preschool.controller;

import com.preschool.dao.ClassDAO;
import com.preschool.dao.TeacherDAO;
import com.preschool.model.SchoolClass;
import com.preschool.model.Teacher;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ClassManagementController {

    // -------------------------------- Class table -------------------------------------------------------------------
    @FXML private TableView<SchoolClass> classTable;
    @FXML private TableColumn<SchoolClass, Integer> idColumn;
    @FXML private TableColumn<SchoolClass, String>  classNameColumn;
    @FXML private TableColumn<SchoolClass, String>  sectionColumn;
    @FXML private TableColumn<SchoolClass, String>  teacherColumn;
    @FXML private TableColumn<SchoolClass, Integer> studentCountColumn;

    // ------------------------------- Class form fields --------------------------------------------------------------
    @FXML private TextField         classNameField;
    @FXML private TextField         sectionField;
    @FXML private ComboBox<Teacher> teacherCombo;
    @FXML private Button            updateButton;
    @FXML private Button            deleteButton;

    private final ClassDAO   classDAO   = new ClassDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final ObservableList<SchoolClass> classList = FXCollections.observableArrayList();
    private SchoolClass selectedClass = null;

    @FXML
    public void initialize() {
        // Class table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("classId"));
        classNameColumn.setCellValueFactory(new PropertyValueFactory<>("className"));
        sectionColumn.setCellValueFactory(new PropertyValueFactory<>("section"));
        teacherColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getTeacherName() != null
                        ? cd.getValue().getTeacherName() : "— Unassigned —"));
        studentCountColumn.setCellValueFactory(cd ->
                new SimpleIntegerProperty(classDAO.getStudentCountForClass(cd.getValue().getClassId())).asObject());
        classTable.setItems(classList);

        loadClasses();
        loadTeachers();

        // When a class is selected, populate the form fields
        classTable.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            if (nw != null) {
                selectedClass = nw;
                populateFields(nw);
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });
    }

    // --------------------------------------- Data loaders -----------------------------------------------------

    private void loadClasses() {
        classList.clear();
        classList.addAll(classDAO.getAllClasses());
    }

    private void loadTeachers() {
        teacherCombo.setItems(FXCollections.observableArrayList(teacherDAO.getAllTeachers()));
        teacherCombo.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Teacher t) { return t == null ? "— None —" : t.getFullName(); }
            public Teacher fromString(String s) { return null; }
        });
    }

    // ------------------------------------------ Class CRUD handlers --------------------------------------------------

    @FXML
    private void handleAddClass() {
        if (!validate()) return;
        if (classDAO.addClass(buildClass())) {
            showSuccess("Class added successfully!");
            loadClasses();
            clearFields();
        } else {
            showError("Failed to add class. It may already exist (same name + section).");
        }
    }

    @FXML
    private void handleUpdateClass() {
        if (selectedClass == null) { showError("Please select a class to update."); return; }
        if (!validate()) return;
        SchoolClass sc = buildClass();
        sc.setClassId(selectedClass.getClassId());
        if (classDAO.updateClass(sc)) {
            showSuccess("Class updated successfully!");
            loadClasses();
            clearFields();
        } else {
            showError("Failed to update class.");
        }
    }

    @FXML
    private void handleDeleteClass() {
        if (selectedClass == null) { showError("Please select a class to delete."); return; }
        int count = classDAO.getStudentCountForClass(selectedClass.getClassId());
        String msg = "Delete class '" + selectedClass.getDisplayName() + "'?";
        if (count > 0)
            msg += "\n\nWarning: " + count + " student(s) assigned to this class will have their class cleared.";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Confirm Delete");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (classDAO.deleteClass(selectedClass.getClassId())) {
                showSuccess("Class deleted successfully!");
                loadClasses();
                clearFields();
            } else {
                showError("Failed to delete class.");
            }
        }
    }

    @FXML
    private void handleAssignTeacher() {
        if (selectedClass == null) { showError("Please select a class first."); return; }
        Teacher t = teacherCombo.getValue();
        if (classDAO.assignTeacher(selectedClass.getClassId(), t != null ? t.getTeacherId() : null)) {
            showSuccess("Teacher assignment updated!");
            loadClasses();
        } else {
            showError("Failed to assign teacher.");
        }
    }

    @FXML private void handleClearFields() { clearFields(); }

    // ---------------------------------------------------- Helpers -------------------------------------------------

    private boolean validate() {
        if (classNameField.getText().trim().isEmpty() || sectionField.getText().trim().isEmpty()) {
            showError("Class name and section are required.");
            return false;
        }
        return true;
    }

    private SchoolClass buildClass() {
        SchoolClass sc = new SchoolClass();
        sc.setClassName(classNameField.getText().trim());
        sc.setSection(sectionField.getText().trim().toUpperCase());
        Teacher t = teacherCombo.getValue();
        sc.setTeacherId(t != null ? t.getTeacherId() : null);
        sc.setTeacherName(t != null ? t.getFullName() : null);
        return sc;
    }

    private void populateFields(SchoolClass sc) {
        classNameField.setText(sc.getClassName());
        sectionField.setText(sc.getSection());
        teacherCombo.getItems().stream()
                .filter(t -> sc.getTeacherId() != null && t.getTeacherId() == sc.getTeacherId())
                .findFirst().ifPresent(teacherCombo::setValue);
        if (sc.getTeacherId() == null) teacherCombo.setValue(null);
    }

    private void clearFields() {
        classNameField.clear();
        sectionField.clear();
        teacherCombo.setValue(null);
        selectedClass = null;
        classTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void showSuccess(String msg) { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }
    private void showError(String msg)   { new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait(); }
}