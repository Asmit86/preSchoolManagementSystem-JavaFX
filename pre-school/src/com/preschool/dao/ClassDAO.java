package com.preschool.dao;

import com.preschool.model.SchoolClass;
import com.preschool.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassDAO {

    public List<SchoolClass> getAllClasses() {
        List<SchoolClass> list = new ArrayList<>();
        String query = "SELECT c.*, CONCAT(t.first_name,' ',t.last_name) AS teacher_name " +
                "FROM classes c LEFT JOIN teachers t ON c.teacher_id = t.teacher_id " +
                "ORDER BY c.class_name, c.section";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) list.add(extract(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addClass(SchoolClass sc) {
        String query = "INSERT INTO classes (class_name, section, teacher_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, sc.getClassName());
            ps.setString(2, sc.getSection());
            if (sc.getTeacherId() != null) ps.setInt(3, sc.getTeacherId());
            else ps.setNull(3, Types.INTEGER);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateClass(SchoolClass sc) {
        String query = "UPDATE classes SET class_name=?, section=?, teacher_id=? WHERE class_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, sc.getClassName());
            ps.setString(2, sc.getSection());
            if (sc.getTeacherId() != null) ps.setInt(3, sc.getTeacherId());
            else ps.setNull(3, Types.INTEGER);
            ps.setInt(4, sc.getClassId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteClass(int classId) {
        String query = "DELETE FROM classes WHERE class_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, classId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean assignTeacher(int classId, Integer teacherId) {
        String query = "UPDATE classes SET teacher_id=? WHERE class_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            if (teacherId != null) ps.setInt(1, teacherId);
            else ps.setNull(1, Types.INTEGER);
            ps.setInt(2, classId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public int getStudentCountForClass(int classId) {
        String query = "SELECT COUNT(*) FROM students WHERE class_id=? AND status='Active'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, classId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private SchoolClass extract(ResultSet rs) throws SQLException {
        SchoolClass sc = new SchoolClass();
        sc.setClassId(rs.getInt("class_id"));
        sc.setClassName(rs.getString("class_name"));
        sc.setSection(rs.getString("section"));
        int tid = rs.getInt("teacher_id");
        sc.setTeacherId(rs.wasNull() ? null : tid);
        sc.setTeacherName(rs.getString("teacher_name"));
        return sc;
    }
}
