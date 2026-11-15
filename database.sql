-- Smart Library Management System Database Schema
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'ADMIN'
);

-- Books Table
CREATE TABLE IF NOT EXISTS books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    author VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    quantity INT DEFAULT 1,
    available INT DEFAULT 1
);

-- Students Table
CREATE TABLE IF NOT EXISTS students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    roll_no VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    course VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20)
);

-- Issue/Return Table
CREATE TABLE IF NOT EXISTS issue_records (
    issue_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    student_id INT NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE DEFAULT NULL,
    fine_amount DECIMAL(10, 2) DEFAULT 0.00,
    status VARCHAR(20) DEFAULT 'ISSUED',
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);

-- Sample Data
INSERT INTO users (username, password, full_name, role) VALUES ('admin', 'admin123', 'System Administrator', 'ADMIN');
INSERT INTO books (title, author, category, quantity, available) VALUES ('Core Java Programming', 'Herbert Schildt', 'Computer Science', 5, 5), ('Database System Concepts', 'Silberschatz', 'Computer Science', 3, 3);
INSERT INTO students (roll_no, name, course, email, phone) VALUES ('BCA001', 'Rahul Sharma', 'BCA', 'rahul@example.com', '9876543210');
