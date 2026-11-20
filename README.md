# 📚 Smart Library Management System

**BCA Synopsis Compliant Academic Project**

A comprehensive desktop-based Library Management System developed using **Core Java (Swing/AWT)**, **JDBC**, and **MySQL Database**. Designed with Role-Based Access Control (RBAC), SHA-256 password security, circulation tracking with automated fine calculations, and audit reporting.

---

## 📁 Repository & Folder Structure

```text
Smart-Library-Management-System/
├── database/
│   └── schema.sql             # MySQL schema with 4 tables, FK constraints, and default data
├── src/
│   ├── db/
│   │   └── DBConnection.java  # JDBC Connection setup (localhost:3306/smart_library_db)
│   ├── util/
│   │   └── SecurityUtil.java  # SHA-256 Password Hashing Utility
│   ├── ui/
│   │   ├── LoginFrame.java       # RBAC Login screen with SHA-256 validation
│   │   ├── StudentDashboard.java # Read-only book search portal (Title/Author/Category LIKE search)
│   │   └── AdminDashboard.java   # Full 4-tab Admin dashboard (Inventory, Members, Circulation, Reports)
│   └── Main.java              # Application entry point with Nimbus Look & Feel
└── README.md                  # Project documentation
```

---

## ✨ Key Features

1. **Role-Based Access Control (RBAC):**
   - **Administrator:** Full administrative privileges (Inventory CRUD, Member Registration, Book Circulation, Audit Reports).
   - **User (Student):** Dedicated Read-Only Search & Inquiry portal.

2. **SHA-256 Security:**
   - PreparedStatement execution prevents SQL Injection.
   - All passwords are securely hashed using SHA-256 algorithm before database insertion/verification.

3. **Circulation & Fine Management:**
   - **Issue Rules:** Max 3 books allowed per student; standard return period of 14 days.
   - **Return & Fine:** Automatic calculation of overdue days and fine assessment at **₹5 / day**.

4. **5 Comprehensive Audit Reports:**
   - Complete Book Inventory Report
   - Available Books Report
   - Issued Books Report
   - Defaulters / Overdue Fine Report
   - Daily Transaction Log

---

## 🔑 Default Login Credentials

| Role | Username | Plain Password | Password SHA-256 Hash |
|---|---|---|---|
| **Administrator** | `admin` | `admin123` | `240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9` |
| **User (Student)** | `student` | `student123` | `703b0a3d6ad75b649a28adde7d83c6251da457549063adcbc4be1273b0502057` |

---

## ⚙️ Setup & Execution Instructions

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
- **Using IDE (Eclipse / IntelliJ / NetBeans):**
  - Import project source into IDE.
  - Run `src/Main.java`.
- **Using Command Line:**
  ```bash
  javac -d bin -cp "lib/mysql-connector-j-8.0.33.jar" src/db/*.java src/util/*.java src/ui/*.java src/Main.java
  java -cp "bin:lib/mysql-connector-j-8.0.33.jar" Main
  ```
