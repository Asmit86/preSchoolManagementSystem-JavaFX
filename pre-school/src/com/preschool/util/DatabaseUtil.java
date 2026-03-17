package com.preschool.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/preschool_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Divyana@2021";

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to database.");
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Database initialization script
    public static String getDatabaseSetupScript() {
        return """
            CREATE DATABASE IF NOT EXISTS preschool_db;
            USE preschool_db;
            
            CREATE TABLE IF NOT EXISTS users (
                user_id INT PRIMARY KEY AUTO_INCREMENT,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                full_name VARCHAR(100) NOT NULL,
                role ENUM('ADMIN') DEFAULT 'ADMIN',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            
            CREATE TABLE IF NOT EXISTS students (
                student_id INT PRIMARY KEY AUTO_INCREMENT,
                first_name VARCHAR(50) NOT NULL,
                last_name VARCHAR(50) NOT NULL,
                date_of_birth DATE NOT NULL,
                gender ENUM('Male', 'Female', 'Other') NOT NULL,
                guardian_name VARCHAR(100) NOT NULL,
                guardian_phone VARCHAR(20) NOT NULL,
                guardian_email VARCHAR(100),
                address TEXT NOT NULL,
                enrollment_date DATE NOT NULL,
                class_id INT,
                section VARCHAR(10),
                status ENUM('Active', 'Inactive') DEFAULT 'Active',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            );
            
            CREATE TABLE IF NOT EXISTS teachers (
                teacher_id INT PRIMARY KEY AUTO_INCREMENT,
                first_name VARCHAR(50) NOT NULL,
                last_name VARCHAR(50) NOT NULL,
                date_of_birth DATE NOT NULL,
                gender ENUM('Male', 'Female', 'Other') NOT NULL,
                phone VARCHAR(20) NOT NULL,
                email VARCHAR(100),
                address TEXT NOT NULL,
                qualification VARCHAR(100) NOT NULL,
                joining_date DATE NOT NULL,
                status ENUM('Active', 'Inactive') DEFAULT 'Active',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            );
            
            CREATE TABLE IF NOT EXISTS classes (
                class_id INT PRIMARY KEY AUTO_INCREMENT,
                class_name VARCHAR(50) NOT NULL UNIQUE,
                capacity INT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            
            CREATE TABLE IF NOT EXISTS attendance (
                attendance_id INT PRIMARY KEY AUTO_INCREMENT,
                student_id INT NOT NULL,
                attendance_date DATE NOT NULL,
                status ENUM('Present', 'Absent', 'Leave') NOT NULL,
                remarks TEXT,
                recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
                UNIQUE KEY unique_attendance (student_id, attendance_date)
            );
            
            CREATE TABLE IF NOT EXISTS fees (
                fee_id INT PRIMARY KEY AUTO_INCREMENT,
                student_id INT NOT NULL,
                fee_month VARCHAR(20) NOT NULL,
                fee_year INT NOT NULL,
                amount DECIMAL(10, 2) NOT NULL,
                paid_amount DECIMAL(10, 2) DEFAULT 0,
                status ENUM('Paid', 'Pending', 'Partial') DEFAULT 'Pending',
                payment_date DATE,
                remarks TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
                UNIQUE KEY unique_fee (student_id, fee_month, fee_year)
            );
            
            -- Insert default admin user (password: admin123)
            INSERT INTO users (username, password, full_name, role) 
            VALUES ('admin', 'admin123', 'System Administrator', 'ADMIN')
            ON DUPLICATE KEY UPDATE username=username;
            
            -- Insert sample classes
            INSERT INTO classes (class_name, capacity) VALUES 
            ('Nursery', 20),
            ('LKG', 25),
            ('UKG', 25)
            ON DUPLICATE KEY UPDATE class_name=class_name;
            """;
    }
}
