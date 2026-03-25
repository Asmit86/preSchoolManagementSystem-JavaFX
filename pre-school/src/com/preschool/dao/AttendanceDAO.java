package com.preschool.dao;

import com.preschool.model.Attendance;
import com.preschool.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public boolean saveAttendance(Attendance attendance) {
        // Use row alias instead of deprecated VALUES() function for MySQL 8.x compatibility
        String query = "INSERT INTO attendance (student_id, attendance_date, status, remarks) " +
                "VALUES (?, ?, ?, ?) AS new_row " +
                "ON DUPLICATE KEY UPDATE status = new_row.status, remarks = new_row.remarks";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, attendance.getStudentId());
            ps.setDate(2, Date.valueOf(attendance.getAttendanceDate()));
            ps.setString(3, attendance.getStatus());
            ps.setString(4, attendance.getRemarks());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Attendance> getAttendanceByDate(LocalDate date) {
        List<Attendance> list = new ArrayList<>();
        String query = "SELECT a.*, CONCAT(s.first_name, ' ', s.last_name) AS student_name " +
                "FROM attendance a JOIN students s ON a.student_id = s.student_id " +
                "WHERE a.attendance_date = ? ORDER BY s.first_name";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(extract(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Attendance> getAttendanceByStudent(int studentId) {
        List<Attendance> list = new ArrayList<>();
        String query = "SELECT a.*, CONCAT(s.first_name, ' ', s.last_name) AS student_name " +
                "FROM attendance a JOIN students s ON a.student_id = s.student_id " +
                "WHERE a.student_id = ? ORDER BY a.attendance_date DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(extract(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Loads all active students for a given date, pre-filling existing attendance records.
     * Students with no record default to "Present".
     */
    public List<Attendance> getAttendanceSheetForDate(LocalDate date) {
        List<Attendance> list = new ArrayList<>();
        String query = "SELECT s.student_id, CONCAT(s.first_name, ' ', s.last_name) AS student_name, " +
                "COALESCE(a.attendance_id, 0) AS attendance_id, " +
                "COALESCE(a.status, 'Present') AS status, " +
                "COALESCE(a.remarks, '') AS remarks " +
                "FROM students s LEFT JOIN attendance a " +
                "ON s.student_id = a.student_id AND a.attendance_date = ? " +
                "WHERE s.status = 'Active' ORDER BY s.first_name";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Attendance a = new Attendance();
                a.setAttendanceId(rs.getInt("attendance_id"));
                a.setStudentId(rs.getInt("student_id"));
                a.setStudentName(rs.getString("student_name"));
                a.setAttendanceDate(date);
                a.setStatus(rs.getString("status"));
                a.setRemarks(rs.getString("remarks"));
                list.add(a);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int getPresentCountToday() {
        String query = "SELECT COUNT(*) FROM attendance WHERE attendance_date = ? AND status = 'Present'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Attendance extract(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setAttendanceId(rs.getInt("attendance_id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setStudentName(rs.getString("student_name"));
        Date d = rs.getDate("attendance_date");
        if (d != null) a.setAttendanceDate(d.toLocalDate());
        a.setStatus(rs.getString("status"));
        a.setRemarks(rs.getString("remarks"));
        return a;
    }
}
