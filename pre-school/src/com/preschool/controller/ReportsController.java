package com.preschool.controller;

import com.preschool.dao.AttendanceDAO;
import com.preschool.dao.FeeDAO;
import com.preschool.dao.StudentDAO;
import com.preschool.dao.TeacherDAO;
import com.preschool.model.Attendance;
import com.preschool.model.Fee;
import com.preschool.model.Student;
import com.preschool.model.Teacher;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportsController {

    // Summary cards
    @FXML private Label totalStudentsLabel;
    @FXML private Label activeStudentsLabel;
    @FXML private Label totalTeachersLabel;
    @FXML private Label presentTodayLabel;
    @FXML private Label totalCollectedLabel;
    @FXML private Label totalPendingLabel;

    // Student report
    @FXML private TableView<Student> studentReportTable;
    @FXML private TableColumn<Student, Integer> srIdCol;
    @FXML private TableColumn<Student, String>  srNameCol;
    @FXML private TableColumn<Student, String>  srGenderCol;
    @FXML private TableColumn<Student, String>  srClassCol;
    @FXML private TableColumn<Student, String>  srGuardianCol;
    @FXML private TableColumn<Student, String>  srStatusCol;
    @FXML private ComboBox<String> studentStatusFilter;

    // Attendance report
    @FXML private DatePicker attFromDate;
    @FXML private DatePicker attToDate;
    @FXML private TableView<Attendance> attReportTable;
    @FXML private TableColumn<Attendance, String> arDateCol;
    @FXML private TableColumn<Attendance, String> arNameCol;
    @FXML private TableColumn<Attendance, String> arStatusCol;
    @FXML private PieChart attendancePieChart;

    // Fee report
    @FXML private ComboBox<String> feeStatusFilter;
    @FXML private TableView<Fee> feeReportTable;
    @FXML private TableColumn<Fee, String>  frStudentCol;
    @FXML private TableColumn<Fee, String>  frMonthCol;
    @FXML private TableColumn<Fee, Integer> frYearCol;
    @FXML private TableColumn<Fee, Double>  frAmountCol;
    @FXML private TableColumn<Fee, Double>  frPaidCol;
    @FXML private TableColumn<Fee, String>  frStatusCol;
    @FXML private BarChart<String, Number>  feeBarChart;

    private final StudentDAO   studentDAO   = new StudentDAO();
    private final TeacherDAO   teacherDAO   = new TeacherDAO();
    private final AttendanceDAO attDAO      = new AttendanceDAO();
    private final FeeDAO        feeDAO      = new FeeDAO();

    @FXML
    public void initialize() {
        setupStudentReport();
        setupAttendanceReport();
        setupFeeReport();
        loadSummary();
    }

    // SUMMARY

    private void loadSummary() {
        List<Student> allStudents = studentDAO.getAllStudents();
        long active = allStudents.stream().filter(s -> "Active".equals(s.getStatus())).count();
        totalStudentsLabel.setText(String.valueOf(allStudents.size()));
        activeStudentsLabel.setText(String.valueOf(active));
        totalTeachersLabel.setText(String.valueOf(teacherDAO.getTotalTeacherCount()));
        presentTodayLabel.setText(String.valueOf(attDAO.getPresentCountToday()));
        totalCollectedLabel.setText(String.format("Rs. %.2f", feeDAO.getTotalCollected()));
        totalPendingLabel.setText(String.format("Rs. %.2f", feeDAO.getTotalPending()));
    }

    // STUDENT REPORT

    private void setupStudentReport() {
        srIdCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getStudentId()).asObject());
        srNameCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getFullName()));
        srGenderCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getGender()));
        srClassCol.setCellValueFactory(cd -> {
            Integer cid = cd.getValue().getClassId();
            String cn = cid == null ? "—" : cid == 1 ? "Nursery" : cid == 2 ? "LKG" : "UKG";
            return new SimpleStringProperty(cn);
        });
        srGuardianCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getGuardianName()));
        srStatusCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatus()));

        studentStatusFilter.setItems(FXCollections.observableArrayList("All", "Active", "Inactive"));
        studentStatusFilter.setValue("All");

        loadStudentReport();
    }

    @FXML
    private void handleStudentReportFilter() { loadStudentReport(); }

    private void loadStudentReport() {
        List<Student> students = studentDAO.getAllStudents();
        String filter = studentStatusFilter.getValue();
        if (filter != null && !filter.equals("All")) {
            students = students.stream().filter(s -> filter.equals(s.getStatus())).collect(Collectors.toList());
        }
        studentReportTable.setItems(FXCollections.observableArrayList(students));
    }

    // ATTENDANCE REPORT

    private void setupAttendanceReport() {
        arDateCol.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getAttendanceDate() != null ? cd.getValue().getAttendanceDate().toString() : ""));
        arNameCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStudentName()));
        arStatusCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatus()));

        attFromDate.setValue(LocalDate.now().withDayOfMonth(1));
        attToDate.setValue(LocalDate.now());
    }

    @FXML
    private void handleLoadAttendanceReport() {
        LocalDate from = attFromDate.getValue();
        LocalDate to   = attToDate.getValue();
        if (from == null || to == null || from.isAfter(to)) {
            new Alert(Alert.AlertType.ERROR, "Select a valid date range.", ButtonType.OK).showAndWait();
            return;
        }

        ObservableList<Attendance> records = FXCollections.observableArrayList();
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            records.addAll(attDAO.getAttendanceByDate(cursor));
            cursor = cursor.plusDays(1);
        }
        attReportTable.setItems(records);

        // Pie chart
        long present = records.stream().filter(a -> "Present".equals(a.getStatus())).count();
        long absent  = records.stream().filter(a -> "Absent".equals(a.getStatus())).count();
        long leave   = records.stream().filter(a -> "Leave".equals(a.getStatus())).count();
        attendancePieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Present (" + present + ")", present),
                new PieChart.Data("Absent (" + absent + ")", absent),
                new PieChart.Data("Leave (" + leave + ")", leave)
        ));
        attendancePieChart.setTitle("Attendance Summary");
    }

    // FEE REPORT

    private void setupFeeReport() {
        frStudentCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStudentName()));
        frMonthCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getFeeMonth()));
        frYearCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getFeeYear()).asObject());
        frAmountCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleDoubleProperty(cd.getValue().getAmount()).asObject());
        frPaidCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleDoubleProperty(cd.getValue().getPaidAmount()).asObject());
        frStatusCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatus()));

        feeStatusFilter.setItems(FXCollections.observableArrayList("All", "Paid", "Pending", "Partial"));
        feeStatusFilter.setValue("All");

        loadFeeReport();
    }

    @FXML
    private void handleFeeReportFilter() { loadFeeReport(); }

    private void loadFeeReport() {
        List<Fee> fees;
        String filter = feeStatusFilter.getValue();
        if (filter == null || filter.equals("All")) fees = feeDAO.getAllFees();
        else fees = feeDAO.getFeesByStatus(filter);

        feeReportTable.setItems(FXCollections.observableArrayList(fees));

        // Bar chart: collected vs pending per month
        Map<String, Double> collectedByMonth = fees.stream()
                .collect(Collectors.groupingBy(Fee::getFeeMonth, Collectors.summingDouble(Fee::getPaidAmount)));
        Map<String, Double> pendingByMonth = fees.stream()
                .collect(Collectors.groupingBy(Fee::getFeeMonth, Collectors.summingDouble(Fee::getBalance)));

        XYChart.Series<String, Number> collectedSeries = new XYChart.Series<>();
        collectedSeries.setName("Collected");
        XYChart.Series<String, Number> pendingSeries = new XYChart.Series<>();
        pendingSeries.setName("Pending");

        List<String> months = List.of("January","February","March","April","May","June",
                "July","August","September","October","November","December");
        for (String month : months) {
            double col = collectedByMonth.getOrDefault(month, 0.0);
            double pen = pendingByMonth.getOrDefault(month, 0.0);
            if (col > 0 || pen > 0) {
                collectedSeries.getData().add(new XYChart.Data<>(month.substring(0, 3), col));
                pendingSeries.getData().add(new XYChart.Data<>(month.substring(0, 3), pen));
            }
        }

        feeBarChart.getData().clear();
        feeBarChart.getData().addAll(collectedSeries, pendingSeries);
        feeBarChart.setTitle("Fee Collection by Month");
    }
}
