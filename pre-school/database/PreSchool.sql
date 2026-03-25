-- ============================================================
-- PRESCHOOL MANAGEMENT DATABASE
-- Updated schema matching the Java model classes
-- ============================================================


CREATE DATABASE IF NOT EXISTS preschoolFinalDB;
USE preschoolFinalDB;

-- ============================================================
-- TEACHERS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS teachers (
    teacher_id    INT PRIMARY KEY AUTO_INCREMENT,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    date_of_birth DATE         NOT NULL,
    gender        VARCHAR(10)  NOT NULL,
    phone         VARCHAR(20)  NOT NULL,
    email         VARCHAR(100),
    address       TEXT         NOT NULL,
    qualification VARCHAR(100) NOT NULL,
    joining_date  DATE         NOT NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'Active',
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================
-- USERS TABLE (LOGIN SYSTEM)
-- teacher_id is NULL for admin accounts, set for teacher accounts
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    user_id    INT PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(50)        UNIQUE NOT NULL,
    password   VARCHAR(255)       NOT NULL,
    full_name  VARCHAR(100)       NOT NULL,
    role       ENUM('ADMIN','TEACHER') NOT NULL,
    teacher_id INT                NULL,
    created_at TIMESTAMP          DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id) ON DELETE CASCADE
);

-- ============================================================
-- STUDENTS TABLE
-- Columns match StudentDAO and Student model exactly
-- ============================================================
CREATE TABLE IF NOT EXISTS students (
    student_id      INT PRIMARY KEY AUTO_INCREMENT,
    first_name      VARCHAR(50)  NOT NULL,
    last_name       VARCHAR(50)  NOT NULL,
    date_of_birth   DATE         NOT NULL,
    gender          VARCHAR(10)  NOT NULL,
    guardian_name   VARCHAR(100) NOT NULL,
    guardian_phone  VARCHAR(20)  NOT NULL,
    guardian_email  VARCHAR(100),
    address         TEXT         NOT NULL,
    enrollment_date DATE         NOT NULL,
    class_id        INT          NULL,
    section         VARCHAR(10),
    status          VARCHAR(20)  NOT NULL DEFAULT 'Active',
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================
-- ATTENDANCE TABLE
-- unique(student_id, attendance_date) prevents duplicate records
-- ============================================================
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id   INT PRIMARY KEY AUTO_INCREMENT,
    student_id      INT          NOT NULL,
    attendance_date DATE         NOT NULL,
    status          ENUM('Present','Absent','Leave') NOT NULL,
    remarks         VARCHAR(255),
    recorded_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_attendance (student_id, attendance_date),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- ============================================================
-- FEES TABLE
-- Columns match FeeDAO and Fee model exactly
-- unique(student_id, fee_month, fee_year) prevents duplicate billing
-- ============================================================
CREATE TABLE IF NOT EXISTS fees (
    fee_id        INT PRIMARY KEY AUTO_INCREMENT,
    student_id    INT            NOT NULL,
    fee_month     VARCHAR(20)    NOT NULL,
    fee_year      INT            NOT NULL,
    amount        DECIMAL(10,2)  NOT NULL,
    paid_amount   DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    status        ENUM('Paid','Pending','Partial') NOT NULL DEFAULT 'Pending',
    payment_date  DATE,
    remarks       TEXT,
    created_at    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_fee (student_id, fee_month, fee_year),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- ============================================================
-- INDEXES (PERFORMANCE)
-- ============================================================
CREATE INDEX idx_users_username    ON users(username);
CREATE INDEX idx_students_name     ON students(first_name, last_name);
CREATE INDEX idx_attendance_date   ON attendance(attendance_date);
CREATE INDEX idx_fees_student      ON fees(student_id);
CREATE INDEX idx_fees_status       ON fees(status);

-- ============================================================
-- DEFAULT ADMIN USER
-- Password stored as plain text (matches PasswordUtil behaviour)
-- Change this password immediately after first login
-- ============================================================
INSERT INTO users (username, password, full_name, role)
VALUES ('admin', 'admin123', 'System Administrator', 'ADMIN')
ON DUPLICATE KEY UPDATE username = username;
