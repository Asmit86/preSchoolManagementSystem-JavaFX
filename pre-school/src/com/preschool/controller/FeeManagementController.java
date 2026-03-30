package com.preschool.controller;

import com.preschool.dao.FeeDAO;
import com.preschool.dao.StudentDAO;
import com.preschool.model.Fee;
import com.preschool.model.Student;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class FeeManagementController {

    @FXML private TableView<Fee> feeTable;
    @FXML private TableColumn<Fee, Integer>  idColumn;
    @FXML private TableColumn<Fee, String>   studentColumn;
    @FXML private TableColumn<Fee, String>   monthColumn;
    @FXML private TableColumn<Fee, Integer>  yearColumn;
    @FXML private TableColumn<Fee, Double>   amountColumn;
    @FXML private TableColumn<Fee, Double>   paidColumn;
    @FXML private TableColumn<Fee, String>   statusColumn;
    @FXML private TableColumn<Fee, String>   dateColumn;

    @FXML private ComboBox<Student>    studentCombo;
    @FXML private ComboBox<String>     monthCombo;
    @FXML private TextField            yearField;
    @FXML private TextField            amountField;
    @FXML private TextField            paidAmountField;
    @FXML private ComboBox<String>     statusCombo;
    @FXML private DatePicker           paymentDatePicker;
    @FXML private TextField            remarksArea;
    @FXML private TextField            searchField;
    @FXML private ComboBox<String>     filterStatusCombo;

    @FXML private Label totalCollectedLabel;
    @FXML private Label totalPendingLabel;
    @FXML private Label totalRecordsLabel;

    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private final FeeDAO    feeDAO    = new FeeDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final ObservableList<Fee> feeList = FXCollections.observableArrayList();
    private Fee selectedFee = null;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("feeId"));
        studentColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        monthColumn.setCellValueFactory(new PropertyValueFactory<>("feeMonth"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("feeYear"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paidColumn.setCellValueFactory(new PropertyValueFactory<>("paidAmount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getPaymentDate() != null ? cd.getValue().getPaymentDate().toString() : "—"));

        feeTable.setItems(feeList);

        // Populate combos
        List<Student> students = studentDAO.getAllStudents();
        studentCombo.setItems(FXCollections.observableArrayList(students));
        studentCombo.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Student s) { return s == null ? "" : s.getStudentId() + " - " + s.getFullName(); }
            public Student fromString(String s) { return null; }
        });

        monthCombo.setItems(FXCollections.observableArrayList(
                "January","February","March","April","May","June",
                "July","August","September","October","November","December"));

        statusCombo.setItems(FXCollections.observableArrayList("Paid", "Pending", "Partial"));
        statusCombo.setValue("Pending");

        filterStatusCombo.setItems(FXCollections.observableArrayList("All", "Paid", "Pending", "Partial"));
        filterStatusCombo.setValue("All");

        yearField.setText(String.valueOf(LocalDate.now().getYear()));
        paymentDatePicker.setValue(LocalDate.now());

        loadFees();
        updateStats();

        feeTable.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            if (nw != null) {
                selectedFee = nw;
                populateFields(nw);
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });
    }

    private void loadFees() {
        feeList.clear();
        String filter = filterStatusCombo.getValue();
        if (filter == null || filter.equals("All")) {
            feeList.addAll(feeDAO.getAllFees());
        } else {
            feeList.addAll(feeDAO.getFeesByStatus(filter));
        }
        updateStats();
    }

    private void updateStats() {
        totalCollectedLabel.setText(String.format("Rs. %.2f", feeDAO.getTotalCollected()));
        totalPendingLabel.setText(String.format("Rs. %.2f", feeDAO.getTotalPending()));
        totalRecordsLabel.setText(String.valueOf(feeList.size()));
    }

    @FXML
    private void handleAddFee() {
        if (!validate()) return;
        Fee fee = buildFee();
        if (feeDAO.addFee(fee)) {
            showSuccess("Fee record added successfully!");
            loadFees();
            clearFields();
        } else {
            showError("Failed to add fee record. It may already exist for this student/month/year.");
        }
    }

    @FXML
    private void handleUpdateFee() {
        if (selectedFee == null) { showError("Please select a fee record to update."); return; }
        if (!validate()) return;
        Fee fee = buildFee();
        fee.setFeeId(selectedFee.getFeeId());
        if (feeDAO.updateFee(fee)) {
            showSuccess("Fee record updated successfully!");
            loadFees();
            clearFields();
        } else {
            showError("Failed to update fee record.");
        }
    }

    @FXML
    private void handleDeleteFee() {
        if (selectedFee == null) { showError("Please select a fee record to delete."); return; }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setContentText("Delete fee record for " + selectedFee.getStudentName() + "?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (feeDAO.deleteFee(selectedFee.getFeeId())) {
                showSuccess("Fee record deleted!");
                loadFees();
                clearFields();
            } else {
                showError("Failed to delete fee record.");
            }
        }
    }

    @FXML
    private void handleSearch() {
        String kw = searchField.getText().trim();
        feeList.clear();
        feeList.addAll(kw.isEmpty() ? feeDAO.getAllFees() : feeDAO.searchFees(kw));
        updateStats();
    }

    @FXML
    private void handleFilterChange() {
        loadFees();
    }

    @FXML
    private void handleClearFields() { clearFields(); }

    // Auto-set status when paid amount changes
    @FXML
    private void handlePaidAmountChanged() {
        try {
            double total = Double.parseDouble(amountField.getText().trim());
            double paid  = Double.parseDouble(paidAmountField.getText().trim());
            if (paid <= 0)       statusCombo.setValue("Pending");
            else if (paid >= total) statusCombo.setValue("Paid");
            else                 statusCombo.setValue("Partial");
        } catch (NumberFormatException ignored) {}
    }

    private boolean validate() {
        if (studentCombo.getValue() == null || monthCombo.getValue() == null
                || yearField.getText().trim().isEmpty() || amountField.getText().trim().isEmpty()) {
            showError("Please fill all required fields.");
            return false;
        }
        try {
            Double.parseDouble(amountField.getText().trim());
            Double.parseDouble(paidAmountField.getText().trim().isEmpty() ? "0" : paidAmountField.getText().trim());
            Integer.parseInt(yearField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Amount and Year must be valid numbers.");
            return false;
        }
        return true;
    }

    private Fee buildFee() {
        Fee f = new Fee();
        Student s = studentCombo.getValue();
        if (s != null) { f.setStudentId(s.getStudentId()); f.setStudentName(s.getFullName()); }
        f.setFeeMonth(monthCombo.getValue());
        f.setFeeYear(Integer.parseInt(yearField.getText().trim()));
        f.setAmount(Double.parseDouble(amountField.getText().trim()));
        String paid = paidAmountField.getText().trim();
        f.setPaidAmount(paid.isEmpty() ? 0 : Double.parseDouble(paid));
        f.setStatus(statusCombo.getValue());
        f.setPaymentDate(paymentDatePicker.getValue());
        f.setRemarks(remarksArea.getText().trim());
        return f;
    }

    private void populateFields(Fee f) {
        // Find and select the student in combo
        studentCombo.getItems().stream()
                .filter(s -> s.getStudentId() == f.getStudentId())
                .findFirst().ifPresent(studentCombo::setValue);
        monthCombo.setValue(f.getFeeMonth());
        yearField.setText(String.valueOf(f.getFeeYear()));
        amountField.setText(String.valueOf(f.getAmount()));
        paidAmountField.setText(String.valueOf(f.getPaidAmount()));
        statusCombo.setValue(f.getStatus());
        paymentDatePicker.setValue(f.getPaymentDate());
        remarksArea.setText(f.getRemarks() != null ? f.getRemarks() : "");
    }

    private void clearFields() {
        studentCombo.setValue(null);
        monthCombo.setValue(null);
        yearField.setText(String.valueOf(LocalDate.now().getYear()));
        amountField.clear();
        paidAmountField.clear();
        statusCombo.setValue("Pending");
        paymentDatePicker.setValue(LocalDate.now());
        remarksArea.clear();
        selectedFee = null;
        feeTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void showSuccess(String msg) { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }
    private void showError(String msg)   { new Alert(Alert.AlertType.ERROR,       msg, ButtonType.OK).showAndWait(); }
}
