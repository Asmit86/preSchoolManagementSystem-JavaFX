package com.preschool.dao;

import com.preschool.model.Student;
import com.preschool.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public boolean addStudent(Student student) {
        String query = "INSERT INTO students (first_name, last_name, date_of_birth, gender, " +
                "guardian_name, guardian_phone, guardian_email, address, enrollment_date, " +
                "class_id, section, status, behaviour) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, student.getFirstName());
            ps.setString(2, student.getLastName());
            ps.setDate(3, Date.valueOf(student.getDateOfBirth()));
            ps.setString(4, student.getGender());
            ps.setString(5, student.getGuardianName());
            ps.setString(6, student.getGuardianPhone());
            ps.setString(7, student.getGuardianEmail());
            ps.setString(8, student.getAddress());
            ps.setDate(9, Date.valueOf(student.getEnrollmentDate()));
            if (student.getClassId() != null) ps.setInt(10, student.getClassId());
            else ps.setNull(10, Types.INTEGER);
            ps.setString(11, student.getSection());
            ps.setString(12, student.getStatus());
            ps.setString(13, student.getBehaviour());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateStudent(Student student) {
        String query = "UPDATE students SET first_name=?, last_name=?, date_of_birth=?, gender=?, " +
                "guardian_name=?, guardian_phone=?, guardian_email=?, address=?, " +
                "enrollment_date=?, class_id=?, section=?, status=?, behaviour=? WHERE student_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, student.getFirstName());
            ps.setString(2, student.getLastName());
            ps.setDate(3, Date.valueOf(student.getDateOfBirth()));
            ps.setString(4, student.getGender());
            ps.setString(5, student.getGuardianName());
            ps.setString(6, student.getGuardianPhone());
            ps.setString(7, student.getGuardianEmail());
            ps.setString(8, student.getAddress());
            ps.setDate(9, Date.valueOf(student.getEnrollmentDate()));
            if (student.getClassId() != null) ps.setInt(10, student.getClassId());
            else ps.setNull(10, Types.INTEGER);
            ps.setString(11, student.getSection());
            ps.setString(12, student.getStatus());
            ps.setString(13, student.getBehaviour());
            ps.setInt(14, student.getStudentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteStudent(int studentId) {
        String query = "DELETE FROM students WHERE student_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Student> getStudentsByClassId(int classId) {
        List<Student> students = new ArrayList<>();
        String query = "SELECT s.*, CONCAT(c.class_name,' - ',c.section) AS class_display " +
                "FROM students s LEFT JOIN classes c ON s.class_id = c.class_id " +
                "WHERE s.class_id = ? ORDER BY s.student_id DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, classId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) students.add(extract(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return students;
    }

    public List<Student> searchStudentsByClassId(String keyword, int classId) {
        List<Student> students = new ArrayList<>();
        String query = "SELECT s.*, CONCAT(c.class_name,' - ',c.section) AS class_display " +
                "FROM students s LEFT JOIN classes c ON s.class_id = c.class_id " +
                "WHERE s.class_id = ? AND (s.first_name LIKE ? OR s.last_name LIKE ? OR s.guardian_name LIKE ? OR s.guardian_phone LIKE ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            String p = "%" + keyword + "%";
            ps.setInt(1, classId);
            ps.setString(2, p); ps.setString(3, p); ps.setString(4, p); ps.setString(5, p);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) students.add(extract(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return students;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT s.*, CONCAT(c.class_name,' - ',c.section) AS class_display " +
                "FROM students s LEFT JOIN classes c ON s.class_id = c.class_id ORDER BY s.student_id DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) students.add(extract(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return students;
    }

    public Student getStudentById(int studentId) {
        String query = "SELECT s.*, CONCAT(c.class_name,' - ',c.section) AS class_display " +
                "FROM students s LEFT JOIN classes c ON s.class_id = c.class_id WHERE s.student_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extract(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Student> searchStudents(String keyword) {
        List<Student> students = new ArrayList<>();
        String query = "SELECT s.*, CONCAT(c.class_name,' - ',c.section) AS class_display " +
                "FROM students s LEFT JOIN classes c ON s.class_id = c.class_id " +
                "WHERE s.first_name LIKE ? OR s.last_name LIKE ? OR s.guardian_name LIKE ? OR s.guardian_phone LIKE ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p); ps.setString(2, p); ps.setString(3, p); ps.setString(4, p);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) students.add(extract(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return students;
    }

    public int getTotalStudentCount() {
        String query = "SELECT COUNT(*) FROM students WHERE status='Active'";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Student extract(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setFirstName(rs.getString("first_name"));
        s.setLastName(rs.getString("last_name"));
        Date dob = rs.getDate("date_of_birth");
        if (dob != null) s.setDateOfBirth(dob.toLocalDate());
        s.setGender(rs.getString("gender"));
        s.setGuardianName(rs.getString("guardian_name"));
        s.setGuardianPhone(rs.getString("guardian_phone"));
        s.setGuardianEmail(rs.getString("guardian_email"));
        s.setAddress(rs.getString("address"));
        Date enroll = rs.getDate("enrollment_date");
        if (enroll != null) s.setEnrollmentDate(enroll.toLocalDate());
        int cid = rs.getInt("class_id");
        if (!rs.wasNull()) s.setClassId(cid);
        s.setSection(rs.getString("section"));
        s.setStatus(rs.getString("status"));
        s.setBehaviour(rs.getString("behaviour"));
        // class_display is used only for the table column in the controller
        try {
            String cd = rs.getString("class_display");
            if (cd != null) s.setSection(cd);
        } catch (SQLException ignored) {}
        return s;
    }
}
