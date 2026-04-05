package com.preschool.dao;

import com.preschool.model.Teacher;
import com.preschool.util.DatabaseUtil;
import com.preschool.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TeacherDAO handles all database operations related to Teacher management.
 * It also manages linked user login creation using transactional processing.
 */
public class TeacherDAO {

    /** Check whether a username already exists in the users table */
    public boolean usernameExists(String username) {

        String query = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Add a new teacher AND create login account in a single transaction.
     * Ensures both inserts succeed or both fail (data consistency).
     */
    public boolean addTeacherWithLogin(Teacher teacher, String username, String password) {

        Connection conn = null;

        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // Step 1: Insert teacher details
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

                if (ps.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
                // Retrieve generated teacher ID
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    teacherId = rs.getInt(1);
                } else {
                    conn.rollback();
                    return false;
                }
            }

            // Step 2: Create corresponding user login account
            String userQuery = "INSERT INTO users (username, password, full_name, role, teacher_id) " +
                    "VALUES (?, ?, ?, 'TEACHER', ?)";

            try (PreparedStatement ps = conn.prepareStatement(userQuery)) {

                ps.setString(1, username);
                ps.setString(2, PasswordUtil.password(password));
                ps.setString(3, teacher.getFullName());
                ps.setInt(4, teacherId);

                if (ps.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();                          // Commit transaction if both inserts succeed
            return true;

        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());

            try {
                if (conn != null) conn.rollback();           // rollback on failure
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            return false;

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //GET ALL TEACHERS
    public List<Teacher> getAllTeachers() {

        List<Teacher> list = new ArrayList<>();
        String query = "SELECT * FROM teachers ORDER BY teacher_id DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(extract(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    //SEARCH TEACHERS
    public List<Teacher> searchTeachers(String keyword) {

        List<Teacher> list = new ArrayList<>();

        String query = "SELECT * FROM teachers WHERE first_name LIKE ? OR last_name LIKE ? OR phone LIKE ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            String search = "%" + keyword + "%";

            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(extract(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    //UPDATE TEACHER
    public boolean updateTeacher(Teacher teacher) {

        String query = "UPDATE teachers SET first_name=?, last_name=?, phone=?, email=?, " +
                "qualification=?, status=? WHERE teacher_id=?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, teacher.getFirstName());
            ps.setString(2, teacher.getLastName());
            ps.setString(3, teacher.getPhone());
            ps.setString(4, teacher.getEmail());
            ps.setString(5, teacher.getQualification());
            ps.setString(6, teacher.getStatus());
            ps.setInt(7, teacher.getTeacherId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    //DELETE TEACHER
    public boolean deleteTeacher(int teacherId) {

        String query = "DELETE FROM teachers WHERE teacher_id=?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, teacherId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    //DASHBOARD COUNT
    public int getTotalTeacherCount() {

        String query = "SELECT COUNT(*) FROM teachers WHERE status='Active'";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    //RESULTSET → OBJECT
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