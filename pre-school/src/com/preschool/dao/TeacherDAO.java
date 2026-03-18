package com.preschool.dao;

import com.preschool.model.Teacher;
import com.preschool.util.DatabaseUtil;
import com.preschool.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {

    public boolean addTeacher(Teacher teacher) {
        String query = "INSERT INTO teachers (first_name, last_name, date_of_birth, gender, " +
                "phone, email, address, qualification, joining_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, teacher.getFirstName());
            ps.setString(2, teacher.getLastName());
            ps.setDate(3, Date.valueOf(teacher.getDateOfBirth()));
            ps.setString(4, teacher.getGender());
            ps.setString(5, teacher.getPhone());
            ps.setString(6, teacher.getEmail());
            ps.setString(7, teacher.getAddress());
            ps.setString(8, teacher.getQualification());
            ps.setDate(9, Date.valueOf(teacher.getJoiningDate()));
            ps.setString(10, teacher.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateTeacher(Teacher teacher) {
        String query = "UPDATE teachers SET first_name=?, last_name=?, date_of_birth=?, gender=?, " +
                "phone=?, email=?, address=?, qualification=?, joining_date=?, status=? " +
                "WHERE teacher_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, teacher.getFirstName());
            ps.setString(2, teacher.getLastName());
            ps.setDate(3, Date.valueOf(teacher.getDateOfBirth()));
            ps.setString(4, teacher.getGender());
            ps.setString(5, teacher.getPhone());
            ps.setString(6, teacher.getEmail());
            ps.setString(7, teacher.getAddress());
            ps.setString(8, teacher.getQualification());
            ps.setDate(9, Date.valueOf(teacher.getJoiningDate()));
            ps.setString(10, teacher.getStatus());
            ps.setInt(11, teacher.getTeacherId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteTeacher(int teacherId) {
        String query = "DELETE FROM teachers WHERE teacher_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, teacherId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Teacher> getAllTeachers() {
        List<Teacher> list = new ArrayList<>();
        String query = "SELECT * FROM teachers ORDER BY teacher_id DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) list.add(extract(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Teacher> searchTeachers(String keyword) {
        List<Teacher> list = new ArrayList<>();
        String query = "SELECT * FROM teachers WHERE first_name LIKE ? OR last_name LIKE ? OR phone LIKE ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p); ps.setString(2, p); ps.setString(3, p);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(extract(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int getTotalTeacherCount() {
        String query = "SELECT COUNT(*) FROM teachers WHERE status='Active'";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * Checks if a username already exists in the users table.
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Adds a teacher AND creates their login account in one transaction.
     * If either operation fails, both are rolled back.
     * @param teacher the teacher object to insert
     * @param username login username for the teacher
     * @param password login password for the teacher
     * @return true if both operations succeed, false otherwise
     */
    public boolean addTeacherWithLogin(Teacher teacher, String username, String password) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Step 1: Insert teacher
            String teacherQuery = "INSERT INTO teachers (first_name, last_name, date_of_birth, gender, " +
                    "phone, email, address, qualification, joining_date, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            int teacherId;
            try (PreparedStatement ps = conn.prepareStatement(teacherQuery, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, teacher.getFirstName());
                ps.setString(2, teacher.getLastName());
                ps.setDate(3, Date.valueOf(teacher.getDateOfBirth()));
                ps.setString(4, teacher.getGender());
                ps.setString(5, teacher.getPhone());
                ps.setString(6, teacher.getEmail());
                ps.setString(7, teacher.getAddress());
                ps.setString(8, teacher.getQualification());
                ps.setDate(9, Date.valueOf(teacher.getJoiningDate()));
                ps.setString(10, teacher.getStatus());

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    return false;
                }

                // Get generated teacher_id
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    teacherId = rs.getInt(1);
                } else {
                    conn.rollback();
                    return false;
                }
            }

            // Step 2: Insert login account with hashed password
            String userQuery = "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, 'TEACHER')";
            try (PreparedStatement ps = conn.prepareStatement(userQuery)) {
                ps.setString(1, username);
                ps.setString(2, PasswordUtil.hashPassword(password)); // Hash password with BCrypt
                ps.setString(3, teacher.getFirstName() + " " + teacher.getLastName());

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Teacher extract(ResultSet rs) throws SQLException {
        Teacher t = new Teacher();
        t.setTeacherId(rs.getInt("teacher_id"));
        t.setFirstName(rs.getString("first_name"));
        t.setLastName(rs.getString("last_name"));
        Date dob = rs.getDate("date_of_birth");
        if (dob != null) t.setDateOfBirth(dob.toLocalDate());
        t.setGender(rs.getString("gender"));
        t.setPhone(rs.getString("phone"));
        t.setEmail(rs.getString("email"));
        t.setAddress(rs.getString("address"));
        t.setQualification(rs.getString("qualification"));
        Date jd = rs.getDate("joining_date");
        if (jd != null) t.setJoiningDate(jd.toLocalDate());
        t.setStatus(rs.getString("status"));
        return t;
    }
}
