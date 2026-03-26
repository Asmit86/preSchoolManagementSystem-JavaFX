package com.preschool.controller;

import com.preschool.dao.AttendanceDAO;
import com.preschool.model.Attendance;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;


/**
 * Controller class responsible for handling UI events
 * and connecting the interface with backend logic
 */
public class AttendanceManagementController {

    // Mark Attendance tab
    @FXML private DatePicker attendanceDatePicker;
    @FXML private TableView<Attendance> markTable;
    @FXML private TableColumn<Attendance, String> markNameColumn;
    @FXML private TableColumn<Attendance, String> markStatusColumn;
    @FXML private TableColumn<Attendance, String> markRemarksColumn;
    @FXML private Label attendanceSummaryLabel;

    // View History tab
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private TableView<Attendance> historyTable;
    @FXML private TableColumn<Attendance, String> historyDateColumn;
    @FXML private TableColumn<Attendance, String> historyNameColumn;
    @FXML private TableColumn<Attendance, String> historyStatusColumn;
    @FXML private TableColumn<Attendance, String> historyRemarksColumn;

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final ObservableList<Attendance> markList = FXCollections.observableArrayList();
    private final ObservableList<Attendance> historyList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Mark Attendance tab setup
        markNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        markStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        markRemarksColumn.setCellValueFactory(new PropertyValueFactory<>("remarks"));

        // Make status column editable with ComboBox
        markStatusColumn.setCellFactory(col -> new TableCell<>() {
            private final ComboBox<String> combo = new ComboBox<>(
                    FXCollections.observableArrayList("Present", "Absent", "Leave"));
            {
                combo.setOnAction(e -> {
                    Attendance a = getTableRow().getItem();
                    if (a != null) {
                        a.setStatus(combo.getValue());
                        updateSummary();
                    }
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); }
                else {
                    combo.setValue(item);
                    setGraphic(combo);
                }
            }
        });

        markTable.setItems(markList);
        attendanceDatePicker.setValue(LocalDate.now());

        // History tab setup
        historyDateColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getAttendanceDate() != null ?
                        cd.getValue().getAttendanceDate().toString() : ""));
        historyNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        historyStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        historyRemarksColumn.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        historyTable.setItems(historyList);

        fromDatePicker.setValue(LocalDate.now().withDayOfMonth(1));
        toDatePicker.setValue(LocalDate.now());

        loadAttendanceSheet();
    }

    @FXML
    private void handleLoadDate() {
        loadAttendanceSheet();
    }

    private void loadAttendanceSheet() {
        LocalDate date = attendanceDatePicker.getValue();
        if (date == null) return;
        markList.clear();
        markList.addAll(attendanceDAO.getAttendanceSheetForDate(date));
        updateSummary();
    }

    private void updateSummary() {
        long present = markList.stream().filter(a -> "Present".equals(a.getStatus())).count();
        long absent  = markList.stream().filter(a -> "Absent".equals(a.getStatus())).count();
        long leave   = markList.stream().filter(a -> "Leave".equals(a.getStatus())).count();
        attendanceSummaryLabel.setText(
                "Total: " + markList.size() + "  |  Present: " + present +
                        "  |  Absent: " + absent + "  |  Leave: " + leave);
    }

    @FXML
    private void handleSaveAttendance() {
        if (markList.isEmpty()) {
            showError("No students loaded. Select a date and click Load.");
            return;
        }
        LocalDate date = attendanceDatePicker.getValue();
        int saved = 0;
        for (Attendance a : markList) {
            a.setAttendanceDate(date);
            if (attendanceDAO.saveAttendance(a)) saved++;
        }
        showSuccess("Attendance saved for " + saved + " students on " + date);
    }

    @FXML
    private void handleMarkAllPresent() {
        markList.forEach(a -> a.setStatus("Present"));
        markTable.refresh();
        updateSummary();
    }

    @FXML
    private void handleViewHistory() {
        LocalDate from = fromDatePicker.getValue();
        LocalDate to   = toDatePicker.getValue();
        if (from == null || to == null) { showError("Select both From and To dates."); return; }
        if (from.isAfter(to)) { showError("From date cannot be after To date."); return; }

        historyList.clear();
        // Iterate each day in range and collect records
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            List<Attendance> dayRecords = attendanceDAO.getAttendanceByDate(cursor);
            historyList.addAll(dayRecords);
            cursor = cursor.plusDays(1);
        }

        if (historyList.isEmpty()) showError("No attendance records found for the selected range.");
    }

    private void showSuccess(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
