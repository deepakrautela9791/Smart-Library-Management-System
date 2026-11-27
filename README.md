# 📚 Smart Library Management System

A comprehensive, multi-role **Java Swing GUI Desktop Application** for Library Management built in **Java** with **MySQL** database persistence, **SHA-256 password hashing**, and complete **transaction tracking** (Issue, Return, Renew, and Fine calculation).

---

## 🚀 Features & Role-Based Workflows

### 1. 🔑 Multi-Role Authentication
- **Administrator:** Full administrative control (Manage Books, Manage Students, Issue/Return/Renew Books, View Reports).
- **User (Student):** Dedicated Read-Only Search & Inquiry portal.

### 2. 🔒 SHA-256 Security
- Passwords are never stored in plain text. Every user credential is hashed using Java's `MessageDigest` (`SHA-256`) before database verification.

### 3. 📖 Book & Student Management (CRUD)
- **Book Management:** Add, update, delete, and search books by ID, Title, Author, Category, or ISBN.
- **Student Management:** Add, update, delete, and view student records (Name, Course, Semester, Contact).

### 4. 🔄 Issue, Return & Renew Engine
- **14-Day Issue Period:** Automatic due date calculation (`IssueDate + 14 days`).
- **Borrowing Limit:** Maximum **3** books allowed per student at a time.
- **Fine Calculation:** Late returns incur **Rs. 5.00 per day** beyond the due date.
- **Renewals:** Extend the due date for active issues.

### 5. 📊 Reporting & Analytics
1. **Complete Book Inventory**
2. **Available Books**
3. **Issued Books**
4. **Defaulters / Overdue**
5. **Daily Transaction Log**
6. **Lost Books**

---

## 🛠️ Tech Stack

| Technology | Version / Details |
|---|---|
| **Language** | Java (JDK 17 or higher) |
| **GUI Framework** | Java Swing (`javax.swing`) |
| **Database** | MySQL (8.0 or higher) |
| **Database Driver** | MySQL Connector/J (`mysql-connector-j-8.4.0.jar`) |
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
   This creates the `smart_library_db` database, 4 tables (`ADMIN`, `BOOK`, `STUDENT`, `TRANSACTION`), and default seed data. *(Note: On a hosted DB where `CREATE DATABASE` is not allowed, skip the first two `CREATE DATABASE` / `USE` lines.)*

### 2. Configure Database Connection (`db.properties`)
1. Copy `db.properties.example` to `db.properties` in the project root (or next to `SmartLibrary.exe`):
   ```bash
   cp db.properties.example db.properties
   ```
2. Open `db.properties` and update `db.url`, `db.user`, and `db.password` with your MySQL credentials.

### 3. Build & Run

#### Option A: Compile & Run from Source (CLI)
1. Download **MySQL Connector/J** (`mysql-connector-j-8.4.0.jar`) and place it in a `lib/` folder.
2. Compile all Java source files into `build/`:
   - **Windows (Command Prompt / PowerShell):**
     ```cmd
     mkdir build
     dir /s /B src\*.java > sources.txt
     javac -d build -cp "lib\*" @sources.txt
     java -cp "build;lib\mysql-connector-j-8.4.0.jar" Main
     ```
   - **Linux / macOS:**
     ```bash
     mkdir -p build
     javac -d build -cp "lib/*" $(find src -name "*.java")
     java -cp "build:lib/mysql-connector-j-8.4.0.jar" Main
     ```

#### Option B: Run Windows Desktop App (`SmartLibrary.exe`)
1. Download `SmartLibrary-windows.zip` from the **GitHub Actions** workflow artifact and extract it.
2. Inside the extracted `SmartLibrary` folder, copy `db.properties.example` to `db.properties` and fill in your MySQL `db.url`, `db.user`, and `db.password`.
3. Double-click `SmartLibrary.exe` to launch the application.

---

## 📄 Project Synopsis
- [View Project Synopsis](Library_Management_System_Synopsis.pdf)
