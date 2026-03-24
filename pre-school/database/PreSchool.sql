
-- PRESCHOOL MANAGEMENT DATABASE


-- 1. CREATE DATABASE
CREATE DATABASE IF NOT EXISTS preschoolFinalDB;
USE preschoolFinalDB;



-- 2. TEACHERS TABLE

CREATE TABLE teachers (
    teacher_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(10),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    qualification VARCHAR(100),
    joining_date DATE,
    status VARCHAR(20) DEFAULT 'Active'
);


-- 3. USERS TABLE (LOGIN SYSTEM)

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, 
    full_name VARCHAR(100),
    role ENUM('ADMIN','TEACHER') NOT NULL,
    teacher_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (teacher_id)
        REFERENCES teachers(teacher_id)
        ON DELETE CASCADE
);


-- 4. STUDENTS TABLE

CREATE TABLE students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(10),
    parent_name VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    admission_date DATE,
    status VARCHAR(20) DEFAULT 'Active'
);


-- 5. ATTENDANCE TABLE

CREATE TABLE attendance (
    attendance_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    date DATE NOT NULL,
    status ENUM('Present','Absent') NOT NULL,

    FOREIGN KEY (student_id)
        REFERENCES students(student_id)
        ON DELETE CASCADE
);


-- 6. FEES TABLE

CREATE TABLE fees (
    fee_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    due_date DATE,
    status ENUM('Paid','Pending') DEFAULT 'Pending',

    FOREIGN KEY (student_id)
        REFERENCES students(student_id)
        ON DELETE CASCADE
);


-- 7. INDEXES (PERFORMANCE)

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_students_name ON students(first_name, last_name);
CREATE INDEX idx_attendance_date ON attendance(date);


-- ADMIN LOGIN
INSERT INTO users (username, password, full_name, role)
VALUES ('admin', 'admin123', 'System Admin', 'ADMIN');

