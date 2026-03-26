package com.preschool.dao;

import com.preschool.model.Student;
import com.preschool.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * DAO class for handling student database operations
 */
public class StudentDAO {


    /**
     * Adds a new student to the database
     */
    public boolean addStudent(Student student) {
        String query = "INSERT INTO students (first_name, last_name, date_of_birth, gender, " +
                "guardian_name, guardian_phone, guardian_email, address, enrollment_date, " +
                "class_id, section, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setDate(3, Date.valueOf(student.getDateOfBirth()));
            pstmt.setString(4, student.getGender());
            pstmt.setString(5, student.getGuardianName());
            pstmt.setString(6, student.getGuardianPhone());
            pstmt.setString(7, student.getGuardianEmail());
            pstmt.setString(8, student.getAddress());
            pstmt.setDate(9, Date.valueOf(student.getEnrollmentDate()));

            if (student.getClassId() != null) {
                pstmt.setInt(10, student.getClassId());
            } else {
                pstmt.setNull(10, Types.INTEGER);
            }

            pstmt.setString(11, student.getSection());
            pstmt.setString(12, student.getStatus());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStudent(Student student) {
        String query = "UPDATE students SET first_name=?, last_name=?, date_of_birth=?, gender=?, " +
                "guardian_name=?, guardian_phone=?, guardian_email=?, address=?, " +
                "enrollment_date=?, class_id=?, section=?, status=? WHERE student_id=?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setDate(3, Date.valueOf(student.getDateOfBirth()));
            pstmt.setString(4, student.getGender());
            pstmt.setString(5, student.getGuardianName());
            pstmt.setString(6, student.getGuardianPhone());
            pstmt.setString(7, student.getGuardianEmail());
            pstmt.setString(8, student.getAddress());
            pstmt.setDate(9, Date.valueOf(student.getEnrollmentDate()));

            if (student.getClassId() != null) {
                pstmt.setInt(10, student.getClassId());
            } else {
                pstmt.setNull(10, Types.INTEGER);
            }

            pstmt.setString(11, student.getSection());
            pstmt.setString(12, student.getStatus());
            pstmt.setInt(13, student.getStudentId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStudent(int studentId) {
        String query = "DELETE FROM students WHERE student_id=?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students ORDER BY student_id DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public Student getStudentById(int studentId) {
        String query = "SELECT * FROM students WHERE student_id=?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractStudentFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Student> searchStudents(String keyword) {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students WHERE first_name LIKE ? OR last_name LIKE ? " +
                "OR guardian_name LIKE ? OR guardian_phone LIKE ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public int getTotalStudentCount() {
        String query = "SELECT COUNT(*) as total FROM students WHERE status='Active'";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private Student extractStudentFromResultSet(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));

        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            student.setDateOfBirth(dob.toLocalDate());
        }

        student.setGender(rs.getString("gender"));
        student.setGuardianName(rs.getString("guardian_name"));
        student.setGuardianPhone(rs.getString("guardian_phone"));
        student.setGuardianEmail(rs.getString("guardian_email"));
        student.setAddress(rs.getString("address"));

        Date enrollDate = rs.getDate("enrollment_date");
        if (enrollDate != null) {
            student.setEnrollmentDate(enrollDate.toLocalDate());
        }

        int classId = rs.getInt("class_id");
        if (!rs.wasNull()) {
            student.setClassId(classId);
        }

        student.setSection(rs.getString("section"));
        student.setStatus(rs.getString("status"));

        return student;
    }
}
