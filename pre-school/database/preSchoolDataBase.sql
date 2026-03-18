-- Drop existing database if exists
DROP DATABASE IF EXISTS preschoollatest_db;

-- Create database
CREATE DATABASE preschoollatest_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE preschoollatest_db;

--  1. USERS TABLE 
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'TEACHER') NOT NULL DEFAULT 'TEACHER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB;

--  2. STUDENTS TABLE
CREATE TABLE students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
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
    section varchar(20),
    status ENUM('Active', 'Inactive') DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (first_name, last_name),
    INDEX idx_status (status)
) ENGINE=InnoDB;

--  3. TEACHERS TABLE
CREATE TABLE teachers (
    teacher_id INT AUTO_INCREMENT PRIMARY KEY,
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
    INDEX idx_name (first_name, last_name),
    INDEX idx_status (status)
) ENGINE=InnoDB;


SELECT COLUMN_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'preschool_db'
  AND TABLE_NAME = 'users'
  AND COLUMN_NAME = 'role';

SELECT user_id, username, full_name, role
FROM users
ORDER BY role, user_id;


--  4. ATTENDANCE TABLE
CREATE TABLE attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    status ENUM('Present', 'Absent', 'Late', 'Excused') NOT NULL,
    remarks VARCHAR(200),
    recorded_by INT COMMENT 'User ID who recorded this',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    UNIQUE KEY unique_attendance (student_id, attendance_date),
    INDEX idx_date (attendance_date),
    INDEX idx_student_date (student_id, attendance_date)
) ENGINE=InnoDB;

--  5. FEES TABLE
CREATE TABLE fees (
    fee_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    fee_type VARCHAR(50) NOT NULL COMMENT 'Tuition, Transport, Activities, etc.',
    amount DECIMAL(10, 2) NOT NULL,
    paid_amount DECIMAL(10, 2) DEFAULT 0.00,
    due_date DATE NOT NULL,
    payment_date DATE,
    status ENUM('Pending', 'Partial', 'Paid', 'Overdue') DEFAULT 'Pending',
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    INDEX idx_student (student_id),
    INDEX idx_status (status),
    INDEX idx_due_date (due_date)
) ENGINE=InnoDB;



-- Admin Account
-- Username: admin | Password: admin123

INSERT INTO users (username, password, full_name, role) VALUES
('admin', 'admin123', 'System Administrator', 'ADMIN');


-- Check all users
SELECT user_id, username, full_name, role FROM users;

-- Check students count
SELECT COUNT(*) as total_students FROM students WHERE status = 'Active';

-- Check teachers count
SELECT COUNT(*) as total_teachers FROM teachers WHERE status = 'Active';

-- Check recent attendance
SELECT s.first_name, s.last_name, a.attendance_date, a.status 
FROM attendance a 
JOIN students s ON a.student_id = s.student_id 
ORDER BY a.attendance_date DESC 
LIMIT 10;

-- Check fee summary
SELECT 
    s.first_name, 
    s.last_name, 
    SUM(f.amount) as total_fees, 
    SUM(f.paid_amount) as total_paid,
    SUM(f.amount - f.paid_amount) as balance
FROM fees f
JOIN students s ON f.student_id = s.student_id
GROUP BY f.student_id;



