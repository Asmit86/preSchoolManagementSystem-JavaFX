
CREATE DATABASE IF NOT EXISTS preschoolSystemDB;
USE preschoolSystemDB;


-- TEACHERS TABLE
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

-- USERS TABLE (LOGIN SYSTEM)
-- teacher_id is NULL for admin accounts, set for teacher accounts
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


-- STUDENTS TABLE
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



-- ATTENDANCE TABLE
-- unique(student_id, attendance_date) prevents duplicate records
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

-- FEES TABLE
-- unique(student_id, fee_month, fee_year) prevents duplicate billing
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


-- INDEXES (PERFORMANCE)
CREATE INDEX idx_users_username    ON users(username);
CREATE INDEX idx_students_name     ON students(first_name, last_name);
CREATE INDEX idx_attendance_date   ON attendance(attendance_date);
CREATE INDEX idx_fees_student      ON fees(student_id);
CREATE INDEX idx_fees_status       ON fees(status);


-- DEFAULT ADMIN USER
INSERT INTO users (username, password, full_name, role)
VALUES ('admin', 'admin123', 'System Administrator', 'ADMIN')
ON DUPLICATE KEY UPDATE username = username;



-- --------------------------------------------------- SAMPLE DATA -----------------------------------------------------------
-- STUDENTS
-- class_id: 1 = Nursery | 2 = LKG | 3 = UKG
-- section:  A or B
INSERT INTO students (first_name, last_name, date_of_birth, gender, guardian_name, guardian_phone, guardian_email, address, enrollment_date, class_id, section, status) VALUES
 
-- NURSERY (class_id = 1)
('Aarav',    'Sharma',    '2021-03-10', 'Male',   'Rajesh Sharma',    '9841001001', 'rajesh.sharma@gmail.com',   'Baneshwor, Kathmandu',   '2025-04-01', 1, 'A', 'Active'),
('Priya',    'Thapa',     '2021-05-22', 'Female', 'Krishna Thapa',    '9851002002', 'krishna.thapa@gmail.com',   'Lalitpur, Patan',        '2025-04-01', 1, 'A', 'Active'),
('Rohan',    'Poudel',    '2021-01-15', 'Male',   'Dipak Poudel',     '9841003003', 'dipak.poudel@gmail.com',    'Kalanki, Kathmandu',     '2025-04-01', 1, 'A', 'Active'),
('Sunisha',  'Gurung',    '2021-07-08', 'Female', 'Nabin Gurung',     '9861004004', 'nabin.gurung@gmail.com',    'Chabahil, Kathmandu',    '2025-04-01', 1, 'A', 'Active'),
('Aayush',   'Adhikari',  '2021-02-28', 'Male',   'Suresh Adhikari',  '9841005005', 'suresh.adhikari@gmail.com', 'Bhaktapur',              '2025-04-01', 1, 'B', 'Active'),
('Nisha',    'Rai',       '2021-09-14', 'Female', 'Bikram Rai',       '9851006006', 'bikram.rai@gmail.com',      'Kirtipur, Kathmandu',    '2025-04-01', 1, 'B', 'Active'),
('Sagar',    'Karki',     '2021-06-03', 'Male',   'Ramesh Karki',     '9841007007', 'ramesh.karki@gmail.com',    'Koteshwor, Kathmandu',   '2025-04-01', 1, 'B', 'Active'),
('Mina',     'Tamang',    '2021-11-19', 'Female', 'Babu Tamang',      '9861008008', 'babu.tamang@gmail.com',     'Jorpati, Kathmandu',     '2025-04-01', 1, 'B', 'Active'),
 
-- LKG (class_id = 2)
('Arjun',    'Basnet',    '2020-04-05', 'Male',   'Gopal Basnet',     '9841009009', 'gopal.basnet@gmail.com',    'Thamel, Kathmandu',      '2024-04-01', 2, 'A', 'Active'),
('Anisha',   'Shrestha',  '2020-08-17', 'Female', 'Sanjay Shrestha',  '9851010010', 'sanjay.shrestha@gmail.com', 'Patan Dhoka, Lalitpur',  '2024-04-01', 2, 'A', 'Active'),
('Bibek',    'Joshi',     '2020-02-11', 'Male',   'Prakash Joshi',    '9841011011', 'prakash.joshi@gmail.com',   'Balaju, Kathmandu',      '2024-04-01', 2, 'A', 'Active'),
('Sapana',   'Magar',     '2020-10-29', 'Female', 'Dhan Magar',       '9861012012', 'dhan.magar@gmail.com',      'Bagbazar, Kathmandu',    '2024-04-01', 2, 'A', 'Active'),
('Kiran',    'Lama',      '2020-05-20', 'Male',   'Tenzin Lama',      '9841013013', 'tenzin.lama@gmail.com',     'Boudha, Kathmandu',      '2024-04-01', 2, 'B', 'Active'),
('Kabita',   'Pandey',    '2020-12-07', 'Female', 'Umesh Pandey',     '9851014014', 'umesh.pandey@gmail.com',    'Thankot, Kathmandu',     '2024-04-01', 2, 'B', 'Active'),
('Dipesh',   'Bhandari',  '2020-03-23', 'Male',   'Niroj Bhandari',   '9841015015', 'niroj.bhandari@gmail.com',  'Maharajgunj, Kathmandu', '2024-04-01', 2, 'B', 'Active'),
('Smriti',   'Khatri',    '2020-07-31', 'Female', 'Binod Khatri',     '9861016016', 'binod.khatri@gmail.com',    'Naxal, Kathmandu',       '2024-04-01', 2, 'B', 'Active'),
 
-- UKG (class_id = 3)
('Pratik',   'Koirala',   '2019-05-13', 'Male',   'Shyam Koirala',    '9841017017', 'shyam.koirala@gmail.com',   'Dillibazar, Kathmandu',  '2023-04-01', 3, 'A', 'Active'),
('Kriti',    'Neupane',   '2019-09-02', 'Female', 'Mohan Neupane',    '9851018018', 'mohan.neupane@gmail.com',   'Putalisadak, Kathmandu', '2023-04-01', 3, 'A', 'Active'),
('Suraj',    'Dahal',     '2019-01-27', 'Male',   'Lok Dahal',        '9841019019', 'lok.dahal@gmail.com',       'Swayambhu, Kathmandu',   '2023-04-01', 3, 'A', 'Active'),
('Puja',     'Acharya',   '2019-11-14', 'Female', 'Hari Acharya',     '9861020020', 'hari.acharya@gmail.com',    'Tinkune, Kathmandu',     '2023-04-01', 3, 'A', 'Active'),
('Niraj',    'Ghimire',   '2019-04-08', 'Male',   'Ishwar Ghimire',   '9841021021', 'ishwar.ghimire@gmail.com',  'Gongabu, Kathmandu',     '2023-04-01', 3, 'B', 'Active'),
('Manisha',  'Subedi',    '2019-08-25', 'Female', 'Tirtha Subedi',    '9851022022', 'tirtha.subedi@gmail.com',   'Lagankhel, Lalitpur',    '2023-04-01', 3, 'B', 'Active'),
('Rakesh',   'Parajuli',  '2019-02-16', 'Male',   'Rajan Parajuli',   '9841023023', 'rajan.parajuli@gmail.com',  'Satdobato, Lalitpur',    '2023-04-01', 3, 'B', 'Active'),
('Sujata',   'Dhakal',    '2019-06-30', 'Female', 'Kedar Dhakal',     '9861024024', 'kedar.dhakal@gmail.com',    'Imadol, Lalitpur',       '2023-04-01', 3, 'B', 'Active');
 

-- FEES — current month (March 2026) for all 24 students
-- Nursery: Rs 2,500 | LKG: Rs 3,000 | UKG: Rs 3,500
INSERT INTO fees (student_id, fee_month, fee_year, amount, paid_amount, status, payment_date, remarks) VALUES
 
-- Nursery students (id 1-8) — Rs 2,500
(1,  'March', 2026, 2500.00, 2500.00, 'Paid',    '2026-03-03', NULL),
(2,  'March', 2026, 2500.00, 2500.00, 'Paid',    '2026-03-05', NULL),
(3,  'March', 2026, 2500.00, 2500.00, 'Paid',    '2026-03-02', NULL),
(4,  'March', 2026, 2500.00,    0.00, 'Pending', NULL,         NULL),
(5,  'March', 2026, 2500.00, 2500.00, 'Paid',    '2026-03-07', NULL),
(6,  'March', 2026, 2500.00, 1500.00, 'Partial', '2026-03-10', 'Remaining Rs 1,000 due'),
(7,  'March', 2026, 2500.00, 2500.00, 'Paid',    '2026-03-04', NULL),
(8,  'March', 2026, 2500.00,    0.00, 'Pending', NULL,         NULL),
 
-- LKG students (id 9-16) — Rs 3,000
(9,  'March', 2026, 3000.00, 3000.00, 'Paid',    '2026-03-01', NULL),
(10, 'March', 2026, 3000.00, 3000.00, 'Paid',    '2026-03-03', NULL),
(11, 'March', 2026, 3000.00,    0.00, 'Pending', NULL,         NULL),
(12, 'March', 2026, 3000.00, 3000.00, 'Paid',    '2026-03-06', NULL),
(13, 'March', 2026, 3000.00, 2000.00, 'Partial', '2026-03-08', 'Remaining Rs 1,000 due'),
(14, 'March', 2026, 3000.00, 3000.00, 'Paid',    '2026-03-02', NULL),
(15, 'March', 2026, 3000.00, 3000.00, 'Paid',    '2026-03-09', NULL),
(16, 'March', 2026, 3000.00,    0.00, 'Pending', NULL,         NULL),
 
-- UKG students (id 17-24) — Rs 3,500
(17, 'March', 2026, 3500.00, 3500.00, 'Paid',    '2026-03-01', NULL),
(18, 'March', 2026, 3500.00, 3500.00, 'Paid',    '2026-03-04', NULL),
(19, 'March', 2026, 3500.00,    0.00, 'Pending', NULL,         NULL),
(20, 'March', 2026, 3500.00, 3500.00, 'Paid',    '2026-03-05', NULL),
(21, 'March', 2026, 3500.00, 3500.00, 'Paid',    '2026-03-03', NULL),
(22, 'March', 2026, 3500.00, 2000.00, 'Partial', '2026-03-11', 'Remaining Rs 1,500 due'),
(23, 'March', 2026, 3500.00, 3500.00, 'Paid',    '2026-03-02', NULL),
(24, 'March', 2026, 3500.00,    0.00, 'Pending', NULL,         NULL);

-- ATTENDANCE — today (2026-03-25) for all 24 students
INSERT INTO attendance (student_id, attendance_date, status, remarks) VALUES
-- Nursery
(1,  '2026-03-25', 'Present', NULL),
(2,  '2026-03-25', 'Present', NULL),
(3,  '2026-03-25', 'Absent',  'Sick leave'),
(4,  '2026-03-25', 'Present', NULL),
(5,  '2026-03-25', 'Present', NULL),
(6,  '2026-03-25', 'Present', NULL),
(7,  '2026-03-25', 'Leave',   'Family function'),
(8,  '2026-03-25', 'Present', NULL),
-- LKG
(9,  '2026-03-25', 'Present', NULL),
(10, '2026-03-25', 'Present', NULL),
(11, '2026-03-25', 'Present', NULL),
(12, '2026-03-25', 'Absent',  'Sick leave'),
(13, '2026-03-25', 'Present', NULL),
(14, '2026-03-25', 'Present', NULL),
(15, '2026-03-25', 'Leave',   'Medical appointment'),
(16, '2026-03-25', 'Present', NULL),
-- UKG
(17, '2026-03-25', 'Present', NULL),
(18, '2026-03-25', 'Present', NULL),
(19, '2026-03-25', 'Present', NULL),
(20, '2026-03-25', 'Absent',  'Sick leave'),
(21, '2026-03-25', 'Present', NULL),
(22, '2026-03-25', 'Present', NULL),
(23, '2026-03-25', 'Present', NULL),
(24, '2026-03-25', 'Leave',   'Family function');



