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
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    @FXML private TableView<Student>         studentReportTable;
    @FXML private TableColumn<Student,Integer> srIdCol;
    @FXML private TableColumn<Student,String>  srNameCol;
    @FXML private TableColumn<Student,String>  srGenderCol;
    @FXML private TableColumn<Student,String>  srClassCol;
    @FXML private TableColumn<Student,String>  srGuardianCol;
    @FXML private TableColumn<Student,String>  srBehaviourCol;
    @FXML private TableColumn<Student,String>  srStatusCol;
    @FXML private ComboBox<String>             studentStatusFilter;

    // Attendance report
    @FXML private DatePicker attFromDate;
    @FXML private DatePicker attToDate;
    @FXML private TableView<Attendance>        attReportTable;
    @FXML private TableColumn<Attendance,String> arDateCol;
    @FXML private TableColumn<Attendance,String> arNameCol;
    @FXML private TableColumn<Attendance,String> arStatusCol;
    @FXML private PieChart attendancePieChart;

    // Fee report
    @FXML private ComboBox<String>          feeStatusFilter;
    @FXML private TableView<Fee>            feeReportTable;
    @FXML private TableColumn<Fee,String>   frStudentCol;
    @FXML private TableColumn<Fee,String>   frMonthCol;
    @FXML private TableColumn<Fee,Integer>  frYearCol;
    @FXML private TableColumn<Fee,Double>   frAmountCol;
    @FXML private TableColumn<Fee,Double>   frPaidCol;
    @FXML private TableColumn<Fee,String>   frStatusCol;
    @FXML private BarChart<String,Number>   feeBarChart;

    // Teacher report
    @FXML private TableView<Teacher>             teacherReportTable;
    @FXML private TableColumn<Teacher,Integer>   trIdCol;
    @FXML private TableColumn<Teacher,String>    trNameCol;
    @FXML private TableColumn<Teacher,String>    trQualCol;
    @FXML private TableColumn<Teacher,String>    trPhoneCol;
    @FXML private TableColumn<Teacher,String>    trClassCol;
    @FXML private TableColumn<Teacher,String>    trStatusCol;

    private final StudentDAO    studentDAO   = new StudentDAO();
    private final TeacherDAO    teacherDAO   = new TeacherDAO();
    private final AttendanceDAO attDAO       = new AttendanceDAO();
    private final FeeDAO        feeDAO       = new FeeDAO();

    @FXML
    public void initialize() {
        setupStudentReport();
        setupAttendanceReport();
        setupFeeReport();
        setupTeacherReport();
        loadSummary();
    }

    // ── SUMMARY ─────────────────────────────────────────────

    private void loadSummary() {
        List<Student> all = studentDAO.getAllStudents();
        long active = all.stream().filter(s -> "Active".equals(s.getStatus())).count();
        totalStudentsLabel.setText(String.valueOf(all.size()));
        activeStudentsLabel.setText(String.valueOf(active));
        totalTeachersLabel.setText(String.valueOf(teacherDAO.getTotalTeacherCount()));
        presentTodayLabel.setText(String.valueOf(attDAO.getPresentCountToday()));
        totalCollectedLabel.setText(String.format("Rs. %.2f", feeDAO.getTotalCollected()));
        totalPendingLabel.setText(String.format("Rs. %.2f", feeDAO.getTotalPending()));
    }

    // ── STUDENT REPORT ──────────────────────────────────────

    private void setupStudentReport() {
        srIdCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getStudentId()).asObject());
        srNameCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getFullName()));
        srGenderCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getGender()));
        srClassCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getSection() != null ? cd.getValue().getSection() : "—"));
        srGuardianCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getGuardianName()));
        srBehaviourCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getBehaviour() != null ? cd.getValue().getBehaviour() : "—"));
        srStatusCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatus()));

        studentStatusFilter.setItems(FXCollections.observableArrayList("All", "Active", "Inactive"));
        studentStatusFilter.setValue("All");
        loadStudentReport();
    }

    @FXML private void handleStudentReportFilter() { loadStudentReport(); }

    private void loadStudentReport() {
        List<Student> students = studentDAO.getAllStudents();
        String filter = studentStatusFilter.getValue();
        if (filter != null && !filter.equals("All"))
            students = students.stream().filter(s -> filter.equals(s.getStatus())).collect(Collectors.toList());
        studentReportTable.setItems(FXCollections.observableArrayList(students));
    }

    @FXML
    private void handleExportStudentCSV() {
        exportTableToCSV("student_report", new String[]{"ID","Name","Gender","Class","Guardian","Behaviour","Status"},
                studentReportTable.getItems().stream().map(s -> new String[]{
                        String.valueOf(s.getStudentId()), s.getFullName(), s.getGender(),
                        s.getSection() != null ? s.getSection() : "",
                        s.getGuardianName(),
                        s.getBehaviour() != null ? s.getBehaviour() : "",
                        s.getStatus()
                }).collect(Collectors.toList()));
    }

    @FXML
    private void handleExportStudentDocs() {
        exportTableToDocs("Student Report", new String[]{"ID","Name","Gender","Class","Guardian","Behaviour","Status"},
                studentReportTable.getItems().stream().map(s -> new String[]{
                        String.valueOf(s.getStudentId()), s.getFullName(), s.getGender(),
                        s.getSection() != null ? s.getSection() : "",
                        s.getGuardianName(),
                        s.getBehaviour() != null ? s.getBehaviour() : "",
                        s.getStatus()
                }).collect(Collectors.toList()));
    }

    // ── ATTENDANCE REPORT ────────────────────────────────────

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
            showError("Select a valid date range.");
            return;
        }
        ObservableList<Attendance> records = FXCollections.observableArrayList();
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            records.addAll(attDAO.getAttendanceByDate(cursor));
            cursor = cursor.plusDays(1);
        }
        attReportTable.setItems(records);

        long present = records.stream().filter(a -> "Present".equals(a.getStatus())).count();
        long absent  = records.stream().filter(a -> "Absent".equals(a.getStatus())).count();
        long leave   = records.stream().filter(a -> "Leave".equals(a.getStatus())).count();
        attendancePieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Present (" + present + ")", present),
                new PieChart.Data("Absent (" + absent + ")", absent),
                new PieChart.Data("Leave (" + leave + ")", leave)));
        attendancePieChart.setTitle("Attendance Summary");
    }

    @FXML
    private void handleExportAttendanceCSV() {
        exportTableToCSV("attendance_report", new String[]{"Date","Student","Status"},
                attReportTable.getItems().stream().map(a -> new String[]{
                        a.getAttendanceDate() != null ? a.getAttendanceDate().toString() : "",
                        a.getStudentName(), a.getStatus()
                }).collect(Collectors.toList()));
    }

    @FXML
    private void handleExportAttendanceDocs() {
        exportTableToDocs("Attendance Report", new String[]{"Date","Student","Status"},
                attReportTable.getItems().stream().map(a -> new String[]{
                        a.getAttendanceDate() != null ? a.getAttendanceDate().toString() : "",
                        a.getStudentName(), a.getStatus()
                }).collect(Collectors.toList()));
    }

    // ── FEE REPORT ───────────────────────────────────────────

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

    @FXML private void handleFeeReportFilter() { loadFeeReport(); }

    private void loadFeeReport() {
        String filter = feeStatusFilter.getValue();
        List<Fee> fees = (filter == null || filter.equals("All")) ? feeDAO.getAllFees() : feeDAO.getFeesByStatus(filter);
        feeReportTable.setItems(FXCollections.observableArrayList(fees));

        Map<String,Double> collected = fees.stream().collect(
                Collectors.groupingBy(Fee::getFeeMonth, Collectors.summingDouble(Fee::getPaidAmount)));
        Map<String,Double> pending = fees.stream().collect(
                Collectors.groupingBy(Fee::getFeeMonth, Collectors.summingDouble(Fee::getBalance)));

        XYChart.Series<String,Number> cs = new XYChart.Series<>(); cs.setName("Collected");
        XYChart.Series<String,Number> ps = new XYChart.Series<>(); ps.setName("Pending");
        List<String> months = List.of("January","February","March","April","May","June",
                "July","August","September","October","November","December");
        for (String m : months) {
            double c = collected.getOrDefault(m, 0.0);
            double p = pending.getOrDefault(m, 0.0);
            if (c > 0 || p > 0) {
                cs.getData().add(new XYChart.Data<>(m.substring(0,3), c));
                ps.getData().add(new XYChart.Data<>(m.substring(0,3), p));
            }
        }
        feeBarChart.getData().clear();
        feeBarChart.getData().addAll(cs, ps);
        feeBarChart.setTitle("Fee Collection by Month");
    }

    @FXML
    private void handleExportFeeCSV() {
        exportTableToCSV("fee_report", new String[]{"Student","Month","Year","Amount","Paid","Status"},
                feeReportTable.getItems().stream().map(f -> new String[]{
                        f.getStudentName(), f.getFeeMonth(), String.valueOf(f.getFeeYear()),
                        String.valueOf(f.getAmount()), String.valueOf(f.getPaidAmount()), f.getStatus()
                }).collect(Collectors.toList()));
    }

    @FXML
    private void handleExportFeeDocs() {
        exportTableToDocs("Fee Report", new String[]{"Student","Month","Year","Amount","Paid","Status"},
                feeReportTable.getItems().stream().map(f -> new String[]{
                        f.getStudentName(), f.getFeeMonth(), String.valueOf(f.getFeeYear()),
                        String.valueOf(f.getAmount()), String.valueOf(f.getPaidAmount()), f.getStatus()
                }).collect(Collectors.toList()));
    }

    // ── TEACHER REPORT ───────────────────────────────────────

    private void setupTeacherReport() {
        trIdCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getTeacherId()).asObject());
        trNameCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getFullName()));
        trQualCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getQualification()));
        trPhoneCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getPhone()));
        trStatusCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatus()));
        trClassCol.setCellValueFactory(cd -> new SimpleStringProperty(getTeacherClassDisplay(cd.getValue().getTeacherId())));


        loadTeacherReport();
    }

    private String getTeacherClassDisplay(int teacherId) {
        com.preschool.dao.ClassDAO classDAO = new com.preschool.dao.ClassDAO();
        return classDAO.getAllClasses().stream()
                .filter(c -> c.getTeacherId() != null && c.getTeacherId() == teacherId)
                .map(com.preschool.model.SchoolClass::getDisplayName)
                .collect(Collectors.joining(", "));
    }

    @FXML
    private void handleLoadTeacherReport() { loadTeacherReport(); }

    private void loadTeacherReport() {
        teacherReportTable.setItems(FXCollections.observableArrayList(teacherDAO.getAllTeachers()));
    }

    @FXML
    private void handleExportTeacherCSV() {
        exportTableToCSV("teacher_report", new String[]{"ID","Name","Qualification","Phone","Classes","Status"},
                teacherReportTable.getItems().stream().map(t -> new String[]{
                        String.valueOf(t.getTeacherId()), t.getFullName(), t.getQualification(),
                        t.getPhone(), getTeacherClassDisplay(t.getTeacherId()), t.getStatus()
                }).collect(Collectors.toList()));
    }

    @FXML
    private void handleExportTeacherDocs() {
        exportTableToDocs("Teacher Report", new String[]{"ID","Name","Qualification","Phone","Classes","Status"},
                teacherReportTable.getItems().stream().map(t -> new String[]{
                        String.valueOf(t.getTeacherId()), t.getFullName(), t.getQualification(),
                        t.getPhone(), getTeacherClassDisplay(t.getTeacherId()), t.getStatus()
                }).collect(Collectors.toList()));
    }

    // ── EXPORT UTILITIES ─────────────────────────────────────

    private void exportTableToCSV(String defaultName, String[] headers, List<String[]> rows) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export as CSV");
        fc.setInitialFileName(defaultName + ".csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fc.showSaveDialog(null);
        if (file == null) return;
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(String.join(",", headers) + "\n");
            for (String[] row : rows) {
                fw.write(String.join(",", escapeCSV(row)) + "\n");
            }
            showSuccess("Exported to " + file.getName());
        } catch (IOException e) {
            showError("Export failed: " + e.getMessage());
        }
    }

    private void exportTableToDocs(String title, String[] headers, List<String[]> rows) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export as Docs (.txt)");
        fc.setInitialFileName(title.replace(" ", "_").toLowerCase() + ".txt");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fc.showSaveDialog(null);
        if (file == null) return;
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("Pre-School Management System\n");
            fw.write(title + "\n");
            fw.write("Generated: " + LocalDate.now() + "\n");
            fw.write("=".repeat(60) + "\n\n");
            // Column widths
            int[] widths = new int[headers.length];
            for (int i = 0; i < headers.length; i++) widths[i] = headers[i].length();
            for (String[] row : rows)
                for (int i = 0; i < row.length; i++) widths[i] = Math.max(widths[i], row[i].length());
            // Header row
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < headers.length; i++) sb.append(padRight(headers[i], widths[i] + 2));
            fw.write(sb + "\n");
            fw.write("-".repeat(sb.length()) + "\n");
            for (String[] row : rows) {
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < row.length; i++) line.append(padRight(row[i], widths[i] + 2));
                fw.write(line + "\n");
            }
            fw.write("\nTotal records: " + rows.size() + "\n");
            showSuccess("Exported to " + file.getName());
        } catch (IOException e) {
            showError("Export failed: " + e.getMessage());
        }
    }

    private String[] escapeCSV(String[] row) {
        String[] out = new String[row.length];
        for (int i = 0; i < row.length; i++) {
            String v = row[i] == null ? "" : row[i];
            if (v.contains(",") || v.contains("\"") || v.contains("\n"))
                v = "\"" + v.replace("\"", "\"\"") + "\"";
            out[i] = v;
        }
        return out;
    }

    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s == null ? "" : s);
    }

    private void showSuccess(String msg) { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }
    private void showError(String msg)   { new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait(); }
}