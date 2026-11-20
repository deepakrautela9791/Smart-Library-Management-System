-- Smart Library Management System Database Schema

CREATE DATABASE IF NOT EXISTS smart_library_db;
USE smart_library_db;

-- 1. ADMIN Table
CREATE TABLE IF NOT EXISTS ADMIN (
    AdminID INT PRIMARY KEY AUTO_INCREMENT,
    Username VARCHAR(50) UNIQUE NOT NULL,
    Password VARCHAR(64) NOT NULL,
    Role ENUM('Administrator', 'User') NOT NULL
);

-- 2. BOOK Table
CREATE TABLE IF NOT EXISTS BOOK (
    BookID INT PRIMARY KEY AUTO_INCREMENT,
    ISBN VARCHAR(20) UNIQUE NOT NULL,
    Title VARCHAR(150) NOT NULL,
    Author VARCHAR(100) NOT NULL,
    Category VARCHAR(50) NOT NULL,
    Price DECIMAL(10,2) NOT NULL,
    Status ENUM('Available', 'Issued', 'Lost') DEFAULT 'Available',
    AdminID INT,
    FOREIGN KEY (AdminID) REFERENCES ADMIN(AdminID) ON DELETE SET NULL
);

-- 3. STUDENT Table
CREATE TABLE IF NOT EXISTS STUDENT (
    StudentID INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(100) NOT NULL,
    Course VARCHAR(50) NOT NULL,
    Semester INT NOT NULL,
    Contact VARCHAR(15) NOT NULL
);

-- 4. TRANSACTION Table
CREATE TABLE IF NOT EXISTS TRANSACTION (
    TransID INT PRIMARY KEY AUTO_INCREMENT,
    BookID INT NOT NULL,
    StudentID INT NOT NULL,
    IssueDate DATE NOT NULL,
    DueDate DATE NOT NULL,
    ReturnDate DATE,
    FineAmount DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (BookID) REFERENCES BOOK(BookID),
    FOREIGN KEY (StudentID) REFERENCES STUDENT(StudentID)
);

-- Default Users
INSERT INTO ADMIN (Username, Password, Role) VALUES 
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Administrator'),
('student', '703b0a3d6ad75b649a28adde7d83c6251da457549063adcbc4be1273b0502057', 'User')
ON DUPLICATE KEY UPDATE Username=Username;

-- Sample Data for Students
INSERT INTO STUDENT (Name, Course, Semester, Contact) VALUES
('Rahul Sharma', 'BCA', 4, '9876543210'),
('Priya Singh', 'BCA', 6, '9876543211')
ON DUPLICATE KEY UPDATE StudentID=StudentID;

-- Sample Data for Books
INSERT INTO BOOK (ISBN, Title, Author, Category, Price, Status, AdminID) VALUES
('978-0134685991', 'Effective Java', 'Joshua Bloch', 'Programming', 750.00, 'Available', 1),
('978-0596009205', 'Head First Java', 'Kathy Sierra', 'Programming', 650.00, 'Available', 1),
('978-0132350884', 'Clean Code', 'Robert C. Martin', 'Software Engineering', 890.00, 'Available', 1)
ON DUPLICATE KEY UPDATE BookID=BookID;
