package com.preschool.model;

import java.time.LocalDate;

public class Fee {
    private int feeId;
    private int studentId;
    private String studentName;
    private String feeMonth;
    private int feeYear;
    private double amount;
    private double paidAmount;
    private String status;
    private LocalDate paymentDate;
    private String remarks;

    public Fee() {}

    public Fee(int feeId, int studentId, String studentName, String feeMonth, int feeYear,
               double amount, double paidAmount, String status, LocalDate paymentDate, String remarks) {
        this.feeId = feeId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.feeMonth = feeMonth;
        this.feeYear = feeYear;
        this.amount = amount;
        this.paidAmount = paidAmount;
        this.status = status;
        this.paymentDate = paymentDate;
        this.remarks = remarks;
    }


    // Getters and Setters
    public int getFeeId() { return feeId; }
    public void setFeeId(int feeId) { this.feeId = feeId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getFeeMonth() { return feeMonth; }
    public void setFeeMonth(String feeMonth) { this.feeMonth = feeMonth; }

    public int getFeeYear() { return feeYear; }
    public void setFeeYear(int feeYear) { this.feeYear = feeYear; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public double getBalance() { return amount - paidAmount; }
}
