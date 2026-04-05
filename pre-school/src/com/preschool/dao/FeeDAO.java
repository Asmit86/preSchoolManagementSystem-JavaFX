package com.preschool.dao;

import com.preschool.model.Fee;
import com.preschool.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FeeDAO handles all database operations related to Fee management.
 * Includes CRUD operations and financial calculations like totals and pending fees.
 */
public class FeeDAO {

    /** Insert a new fee record into the database */
    public boolean addFee(Fee fee) {
        String query = "INSERT INTO fees (student_id, fee_month, fee_year, amount, paid_amount, status, payment_date, remarks) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, fee.getStudentId());
            ps.setString(2, fee.getFeeMonth());
            ps.setInt(3, fee.getFeeYear());
            ps.setDouble(4, fee.getAmount());
            ps.setDouble(5, fee.getPaidAmount());
            ps.setString(6, fee.getStatus());
            ps.setDate(7, fee.getPaymentDate() != null ? Date.valueOf(fee.getPaymentDate()) : null);
            ps.setString(8, fee.getRemarks());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Update payment details of an existing fee record */
    public boolean updateFee(Fee fee) {
        String query = "UPDATE fees SET paid_amount=?, status=?, payment_date=?, remarks=? WHERE fee_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setDouble(1, fee.getPaidAmount());
            ps.setString(2, fee.getStatus());
            ps.setDate(3, fee.getPaymentDate() != null ? Date.valueOf(fee.getPaymentDate()) : null);
            ps.setString(4, fee.getRemarks());
            ps.setInt(5, fee.getFeeId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Delete a fee record by its ID */
    public boolean deleteFee(int feeId) {
        String query = "DELETE FROM fees WHERE fee_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, feeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Retrieve all fee records with student name included */
    public List<Fee> getAllFees() {
        List<Fee> list = new ArrayList<>();
        String query = "SELECT f.*, CONCAT(s.first_name,' ',s.last_name) AS student_name " +
                "FROM fees f JOIN students s ON f.student_id = s.student_id ORDER BY f.fee_id DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) list.add(extract(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Filter fee records based on payment status (Paid / Pending) */
    public List<Fee> getFeesByStatus(String status) {
        List<Fee> list = new ArrayList<>();
        String query = "SELECT f.*, CONCAT(s.first_name,' ',s.last_name) AS student_name " +
                "FROM fees f JOIN students s ON f.student_id = s.student_id " +
                "WHERE f.status=? ORDER BY f.fee_id DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(extract(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Search fee records using student name or fee month */
    public List<Fee> searchFees(String keyword) {
        List<Fee> list = new ArrayList<>();
        String query = "SELECT f.*, CONCAT(s.first_name,' ',s.last_name) AS student_name " +
                "FROM fees f JOIN students s ON f.student_id = s.student_id " +
                "WHERE s.first_name LIKE ? OR s.last_name LIKE ? OR f.fee_month LIKE ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p); ps.setString(2, p); ps.setString(3, p);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(extract(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Count number of unpaid or partially paid fee records */
    public int getPendingFeeCount() {
        String query = "SELECT COUNT(*) FROM fees WHERE status != 'Paid'";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /** Calculate total amount collected from all fees */
    public double getTotalCollected() {
        String query = "SELECT COALESCE(SUM(paid_amount), 0) FROM fees";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /** Calculate total pending amount (amount - paid_amount) */
    public double getTotalPending() {
        String query = "SELECT COALESCE(SUM(amount - paid_amount), 0) FROM fees WHERE status != 'Paid'";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * Helper method: Maps ResultSet row to Fee object
     * Used to avoid repeated mapping logic in all methods
     */
    private Fee extract(ResultSet rs) throws SQLException {
        Fee f = new Fee();
        f.setFeeId(rs.getInt("fee_id"));
        f.setStudentId(rs.getInt("student_id"));
        f.setStudentName(rs.getString("student_name"));
        f.setFeeMonth(rs.getString("fee_month"));
        f.setFeeYear(rs.getInt("fee_year"));
        f.setAmount(rs.getDouble("amount"));
        f.setPaidAmount(rs.getDouble("paid_amount"));
        f.setStatus(rs.getString("status"));
        Date pd = rs.getDate("payment_date");
        if (pd != null) f.setPaymentDate(pd.toLocalDate());
        f.setRemarks(rs.getString("remarks"));
        return f;
    }
}
