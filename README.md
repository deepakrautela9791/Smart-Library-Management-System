# 📚 Smart Library Management System

**BCA Academic Project**  
A desktop-based Library Management System built using **Core Java (Swing/AWT)**, **JDBC**, and **MySQL**. This application streamlines daily library operations such as managing book inventories, tracking issued and returned books, and maintaining student/member records.

---

## ✨ Key Features

* **📖 Book Management:** Add new books, update existing details, and view the complete book inventory in a tabular format.
* **🔄 Issue & Return System:** Easily issue books to students and process returns with real-time database updates.
* **🔍 Quick Search & Records:** View issued books and check availability status instantly.
* **🗄️ Persistent Storage:** Powered by MySQL database using JDBC for secure and reliable data management.
* **🖥️ User-Friendly GUI:** Clean and responsive desktop interface built with Java Swing and AWT.

---

## 🛠️ Tech Stack

* **Programming Language:** Core Java (JDK 8 or higher)
* **GUI Framework:** Java Swing & AWT
* **Database:** MySQL
* **Database Connectivity:** JDBC (`mysql-connector-j`)

---

## 📂 Project Structure

```text
Smart-Library-Management-System/
├── SmartLibraryApp.java   # Main application source code (GUI + JDBC logic)
├── database.sql           # SQL script for database creation and sample data
└── README.md              # Project documentation
```

---

## 🚀 How to Run the Project Locally

Follow these steps to set up and run the project on your machine:

### 1. Prerequisites
* Install **Java JDK** (8 or above).
* Install **MySQL Server** (or XAMPP/WAMP).
* Download the **MySQL JDBC Driver** (`mysql-connector-j.jar`).

### 2. Clone the Repository
```bash
git clone https://github.com/deepakrautela9791/Smart-Library-Management-System.git
cd Smart-Library-Management-System
```

### 3. Database Setup
1. Open your MySQL client (MySQL Workbench, Command Line, or phpMyAdmin).
2. Import the `database.sql` file to create the required database and tables:
```sql
SOURCE path/to/database.sql;
```
3. Open `SmartLibraryApp.java` in a text editor or IDE and verify that the database URL, username, and password match your local MySQL configuration:
```java
String url = "jdbc:mysql://localhost:3306/library_db";
String user = "root";
String password = "your_mysql_password";
```

### 4. Compile and Run
Make sure the `mysql-connector-j.jar` file is in your classpath.

**For Windows:**
```bash
javac SmartLibraryApp.java
java -cp ".;path\to\mysql-connector-j.jar" SmartLibraryApp
```

**For Linux / macOS:**
```bash
javac SmartLibraryApp.java
java -cp ".:path/to/mysql-connector-j.jar" SmartLibraryApp
```

---

## 👨‍💻 Author

**Deepak Kumar Rautela**  
* GitHub: [@deepakrautela9791](https://github.com/deepakrautela9791)
