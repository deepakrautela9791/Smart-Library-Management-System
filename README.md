# 📚 Smart Library Management System

A comprehensive, multi-role **Console-Based Library Management System** built in **Java** with **MySQL** database persistence, **SHA-256 password hashing**, and complete **transaction tracking** (Issue, Return, Fine calculation).

---

## 🚀 Features & Role-Based Workflows

### 1. 🔑 Multi-Role Authentication
- **Administrator:** Full administrative control (Manage Books, Issue/Return, View Audit Reports).
- **User (Student):** Dedicated Read-Only Search & Inquiry portal.

### 2. 🔐 SHA-256 Security
- `PreparedStatement` execution prevents SQL Injection.
- All passwords are securely hashed using SHA-256 algorithm before database insertion/verification.

### 3. 📖 Circulation & Fine Management
- **Issue Rules:** Max 3 books allowed per student; standard return period of 14 days.
- **Return & Fine:** Automatic calculation of overdue days and fine assessment at **₹5 / day**.

### 4. 📊 5 Comprehensive Audit Reports
- Complete Book Inventory Report
- Total Registered Students Report
- Currently Issued Books Report
- Complete Transaction History Log
- Overdue Books & Unpaid Fines Report

---

## 🛠️ Tech Stack & Prerequisites

| Technology | Version / Details |
|---|---|
| **Language** | Java (JDK 8 or higher) |
| **Database** | MySQL (8.0 or higher) |
| **Database Driver** | MySQL Connector/J (`mysql-connector-j-8.x.x.jar`) |
| **IDE (Optional)** | Eclipse, IntelliJ IDEA, or VS Code |

---

## 🔐 Default Login Credentials

| Role | Username | Plain Password | Password SHA-256 Hash |
|---|---|---|---|
| **Administrator** | `admin` | `admin123` | `240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9` |
| **User (Student)** | `student` | `student123` | `703b0a3d6ad75b649a28adde7d83c6251da457549063adcbc4be1273b0502057` |

---

## 📋 Setup & Installation Guide

### 1. Database Setup
1. Open MySQL Workbench or MySQL CLI.
2. Run the SQL script located in `database/schema.sql`:
   ```sql
   SOURCE database/schema.sql;
   ```
   This creates `smart_library_db` database, 4 tables (`ADMIN`, `BOOK`, `STUDENT`, `TRANSACTION`), and default seed data.

### 2. JDBC Driver
- Add **MySQL Connector/J** (`mysql-connector-j-8.x.x.jar`) to your project classpath.

### 3. Build & Run

## Project Synopsis
- [View Project Synopsis](Library_Management_System_Synopsis.pdf)
