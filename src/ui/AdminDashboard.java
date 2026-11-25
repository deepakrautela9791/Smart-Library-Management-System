package ui;

import db.DBConnection;
import util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class AdminDashboard extends JFrame {
    private int adminId;

    // Book Inventory components
    private JTextField txtIsbn, txtTitle, txtAuthor, txtCategory, txtPrice, txtBookSearch;
    private JComboBox<String> cmbStatus, cmbBookSearchBy;
    private JTable bookTable;
    private DefaultTableModel bookTableModel;
    private int selectedBookId = -1;

    // Member Registration components
    private JTextField txtStudentName, txtCourse, txtSemester, txtContact;
    private JTable studentTable;
    private DefaultTableModel studentTableModel;
    private int selectedStudentId = -1;

    // Circulation components
    private JTextField txtIssueBookId, txtIssueStudentId, txtReturnBookId;
    private JTable activeTransTable;
    private DefaultTableModel activeTransModel;

    // Reports components
    private JComboBox<String> cmbReportType;
    private JTable reportTable;
    private DefaultTableModel reportTableModel;

    public AdminDashboard(int adminId) {
        this.adminId = adminId;

        setTitle("Smart Library Management System - Admin Dashboard");
        setSize(1050, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        String adminUser = SessionManager.getSession("username") != null
                ? SessionManager.getSession("username").toString() : "Admin";
        JLabel titleLabel = new JLabel("Smart Library - Admin Dashboard (" + adminUser + ")");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                SessionManager.clearSession();
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        headerPanel.add(btnLogout, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.addTab("Book Inventory", createBookPanel());
        tabbedPane.addTab("Member Registration", createMemberPanel());
        tabbedPane.addTab("Circulation (Issue / Return)", createCirculationPanel());
        tabbedPane.addTab("Reports & Audits", createReportsPanel());

        add(tabbedPane, BorderLayout.CENTER);

        loadBooks("");
        loadStudents();
        loadActiveTransactions();
    }

    // ==================== 1. BOOK INVENTORY TAB ====================
    private JPanel createBookPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Book Details"));

        txtIsbn = new JTextField();
        txtTitle = new JTextField();
        txtAuthor = new JTextField();
        txtCategory = new JTextField();
        txtPrice = new JTextField();
        cmbStatus = new JComboBox<>(new String[]{"Available", "Issued", "Lost"});

        formPanel.add(new JLabel("ISBN:")); formPanel.add(txtIsbn);
        formPanel.add(new JLabel("Title:")); formPanel.add(txtTitle);
        formPanel.add(new JLabel("Author:")); formPanel.add(txtAuthor);
        formPanel.add(new JLabel("Category:")); formPanel.add(txtCategory);
        formPanel.add(new JLabel("Price (Rs.):")); formPanel.add(txtPrice);
        formPanel.add(new JLabel("Status:")); formPanel.add(cmbStatus);

        // Buttons + Search Bar
        JPanel actionPanel = new JPanel(new BorderLayout(10, 5));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        JButton btnAdd = styleButton("Add Book", new Color(39, 174, 96));
        JButton btnUpdate = styleButton("Update Book", new Color(41, 128, 185));
        JButton btnDelete = styleButton("Delete Book", new Color(192, 57, 43));
        JButton btnClear = styleButton("Clear", new Color(127, 140, 141));

        btnPanel.add(btnAdd); btnPanel.add(btnUpdate); btnPanel.add(btnDelete); btnPanel.add(btnClear);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        searchPanel.add(new JLabel("Search By:"));
        cmbBookSearchBy = new JComboBox<>(new String[]{"Title", "Author", "Category"});
        txtBookSearch = new JTextField(15);
        JButton btnSearch = styleButton("Search", new Color(52, 73, 94));
        JButton btnReset = styleButton("Reset", new Color(149, 165, 166));
        searchPanel.add(cmbBookSearchBy); searchPanel.add(txtBookSearch); searchPanel.add(btnSearch); searchPanel.add(btnReset);

        actionPanel.add(btnPanel, BorderLayout.WEST);
        actionPanel.add(searchPanel, BorderLayout.EAST);

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(actionPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);

        // Table
        String[] cols = {"Book ID", "ISBN", "Title", "Author", "Category", "Price", "Status"};
        bookTableModel = new DefaultTableModel(cols, 0);
        bookTable = new JTable(bookTableModel);
        bookTable.setRowHeight(24);
        panel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        // Row selection
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            int row = bookTable.getSelectedRow();
            if (row != -1) {
                selectedBookId = Integer.parseInt(bookTableModel.getValueAt(row, 0).toString());
                txtIsbn.setText(String.valueOf(bookTableModel.getValueAt(row, 1)));
                txtTitle.setText(String.valueOf(bookTableModel.getValueAt(row, 2)));
                txtAuthor.setText(String.valueOf(bookTableModel.getValueAt(row, 3)));
                txtCategory.setText(String.valueOf(bookTableModel.getValueAt(row, 4)));
                txtPrice.setText(String.valueOf(bookTableModel.getValueAt(row, 5)));
                cmbStatus.setSelectedItem(String.valueOf(bookTableModel.getValueAt(row, 6)));
            }
        });

        btnAdd.addActionListener(e -> addBook());
        btnUpdate.addActionListener(e -> updateBook());
        btnDelete.addActionListener(e -> deleteBook());
        btnClear.addActionListener(e -> clearBookForm());
        btnSearch.addActionListener(e -> loadBooks(txtBookSearch.getText().trim()));
        btnReset.addActionListener(e -> { txtBookSearch.setText(""); loadBooks(""); });

        return panel;
    }

    private void loadBooks(String keyword) {
        bookTableModel.setRowCount(0);
        String searchBy = (String) cmbBookSearchBy.getSelectedItem();
        String col = "Title";
        if ("Author".equals(searchBy)) col = "Author";
        else if ("Category".equals(searchBy)) col = "Category";

        String sql = "SELECT BookID, ISBN, Title, Author, Category, Price, Status FROM BOOK";
        if (!keyword.isEmpty()) sql += " WHERE " + col + " LIKE ?";

        ArrayList<Object[]> rows = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (!keyword.isEmpty()) stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getInt("BookID"), rs.getString("ISBN"), rs.getString("Title"),
                    rs.getString("Author"), rs.getString("Category"),
                    rs.getBigDecimal("Price"), rs.getString("Status")
                });
            }
            for (Object[] r : rows) bookTableModel.addRow(r);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + ex.getMessage());
        }
    }

    private void addBook() {
        String sql = "INSERT INTO BOOK (ISBN, Title, Author, Category, Price, Status, AdminID) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, txtIsbn.getText().trim());
            stmt.setString(2, txtTitle.getText().trim());
            stmt.setString(3, txtAuthor.getText().trim());
            stmt.setString(4, txtCategory.getText().trim());
            stmt.setBigDecimal(5, new BigDecimal(txtPrice.getText().trim()));
            stmt.setString(6, (String) cmbStatus.getSelectedItem());
            stmt.setInt(7, adminId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Book added successfully!");
            clearBookForm();
            loadBooks("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding book: " + ex.getMessage());
        }
    }

    private void updateBook() {
        if (selectedBookId == -1) { JOptionPane.showMessageDialog(this, "Select a book first!"); return; }
        String sql = "UPDATE BOOK SET ISBN=?, Title=?, Author=?, Category=?, Price=?, Status=? WHERE BookID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, txtIsbn.getText().trim());
            stmt.setString(2, txtTitle.getText().trim());
            stmt.setString(3, txtAuthor.getText().trim());
            stmt.setString(4, txtCategory.getText().trim());
            stmt.setBigDecimal(5, new BigDecimal(txtPrice.getText().trim()));
            stmt.setString(6, (String) cmbStatus.getSelectedItem());
            stmt.setInt(7, selectedBookId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Book updated successfully!");
            clearBookForm();
            loadBooks("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating book: " + ex.getMessage());
        }
    }

    private void deleteBook() {
        if (selectedBookId == -1) { JOptionPane.showMessageDialog(this, "Select a book first!"); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected book?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        String sql = "DELETE FROM BOOK WHERE BookID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, selectedBookId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Book deleted!");
            clearBookForm();
            loadBooks("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting book: " + ex.getMessage());
        }
    }

    private void clearBookForm() {
        selectedBookId = -1;
        txtIsbn.setText(""); txtTitle.setText(""); txtAuthor.setText("");
        txtCategory.setText(""); txtPrice.setText(""); cmbStatus.setSelectedIndex(0);
        bookTable.clearSelection();
    }

    // ==================== 2. MEMBER REGISTRATION TAB ====================
    private JPanel createMemberPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));

        txtStudentName = new JTextField();
        txtCourse = new JTextField();
        txtSemester = new JTextField();
        txtContact = new JTextField();

        formPanel.add(new JLabel("Full Name:")); formPanel.add(txtStudentName);
        formPanel.add(new JLabel("Course:")); formPanel.add(txtCourse);
        formPanel.add(new JLabel("Semester:")); formPanel.add(txtSemester);
        formPanel.add(new JLabel("Contact:")); formPanel.add(txtContact);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        JButton btnReg = styleButton("Register Student", new Color(39, 174, 96));
        JButton btnUpd = styleButton("Update Student", new Color(41, 128, 185));
        JButton btnDel = styleButton("Delete Student", new Color(192, 57, 43));
        JButton btnClr = styleButton("Clear", new Color(127, 140, 141));
        btnPanel.add(btnReg); btnPanel.add(btnUpd); btnPanel.add(btnDel); btnPanel.add(btnClr);

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(btnPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] cols = {"S.No.", "Name", "Course", "Semester", "Contact", "Student ID"};
        studentTableModel = new DefaultTableModel(cols, 0);
        studentTable = new JTable(studentTableModel);
        studentTable.setRowHeight(24);
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        studentTable.getSelectionModel().addListSelectionListener(e -> {
            int row = studentTable.getSelectedRow();
            if (row != -1) {
                selectedStudentId = Integer.parseInt(studentTableModel.getValueAt(row, 5).toString());
                txtStudentName.setText(String.valueOf(studentTableModel.getValueAt(row, 1)));
                txtCourse.setText(String.valueOf(studentTableModel.getValueAt(row, 2)));
                txtSemester.setText(String.valueOf(studentTableModel.getValueAt(row, 3)));
                txtContact.setText(String.valueOf(studentTableModel.getValueAt(row, 4)));
            }
        });

        btnReg.addActionListener(e -> registerStudent());
        btnUpd.addActionListener(e -> updateStudent());
        btnDel.addActionListener(e -> deleteStudent());
        btnClr.addActionListener(e -> clearStudentForm());

        return panel;
    }

    private void loadStudents() {
        studentTableModel.setRowCount(0);
        String sql = "SELECT StudentID, Name, Course, Semester, Contact FROM STUDENT";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
                int sno = 1;
                while (rs.next()) {
                    studentTableModel.addRow(new Object[]{
                    sno++, rs.getString("Name"),
                    rs.getString("Course"), rs.getInt("Semester"),
                    rs.getString("Contact"), rs.getInt("StudentID")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + ex.getMessage());
        }
    }

    private void registerStudent() {
        String sql = "INSERT INTO STUDENT (Name, Course, Semester, Contact) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, txtStudentName.getText().trim());
            stmt.setString(2, txtCourse.getText().trim());
            stmt.setInt(3, Integer.parseInt(txtSemester.getText().trim()));
            stmt.setString(4, txtContact.getText().trim());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student registered successfully!");
            clearStudentForm();
            loadStudents();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error registering student: " + ex.getMessage());
        }
    }

    private void updateStudent() {
        if (selectedStudentId == -1) { JOptionPane.showMessageDialog(this, "Select a student first!"); return; }
        String sql = "UPDATE STUDENT SET Name=?, Course=?, Semester=?, Contact=? WHERE StudentID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, txtStudentName.getText().trim());
            stmt.setString(2, txtCourse.getText().trim());
            stmt.setInt(3, Integer.parseInt(txtSemester.getText().trim()));
            stmt.setString(4, txtContact.getText().trim());
            stmt.setInt(5, selectedStudentId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student updated!");
            clearStudentForm();
            loadStudents();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating student: " + ex.getMessage());
        }
    }

    private void deleteStudent() {
        if (selectedStudentId == -1) { JOptionPane.showMessageDialog(this, "Select a student first!"); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected student?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        String sql = "DELETE FROM STUDENT WHERE StudentID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, selectedStudentId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student deleted!");
            clearStudentForm();
            loadStudents();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting student: " + ex.getMessage());
        }
    }

    private void clearStudentForm() {
        selectedStudentId = -1;
        txtStudentName.setText(""); txtCourse.setText(""); txtSemester.setText(""); txtContact.setText("");
        studentTable.clearSelection();
    }

    // ==================== 3. CIRCULATION TAB ====================
    private JPanel createCirculationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topGrid = new JPanel(new GridLayout(1, 2, 15, 10));

        // Issue Panel
        JPanel issuePanel = new JPanel(new GridLayout(3, 2, 8, 8));
        issuePanel.setBorder(BorderFactory.createTitledBorder("Issue Book (14 Days Limit)"));
        txtIssueBookId = new JTextField();
        txtIssueStudentId = new JTextField();
        JButton btnIssue = styleButton("Issue Book", new Color(39, 174, 96));
        issuePanel.add(new JLabel("Book ID:")); issuePanel.add(txtIssueBookId);
        issuePanel.add(new JLabel("Student ID:")); issuePanel.add(txtIssueStudentId);
        issuePanel.add(new JLabel("")); issuePanel.add(btnIssue);

        // Return / Renew Panel
        JPanel returnPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        returnPanel.setBorder(BorderFactory.createTitledBorder("Return / Renew Book"));
        txtReturnBookId = new JTextField();
        JButton btnReturn = styleButton("Return Book", new Color(230, 126, 34));
        JButton btnRenew = styleButton("Renew Book (+14 Days)", new Color(41, 128, 185));
        returnPanel.add(new JLabel("Book ID:")); returnPanel.add(txtReturnBookId);
        returnPanel.add(new JLabel("")); returnPanel.add(btnReturn);
        returnPanel.add(new JLabel("")); returnPanel.add(btnRenew);

        topGrid.add(issuePanel);
        topGrid.add(returnPanel);
        panel.add(topGrid, BorderLayout.NORTH);

        String[] cols = {"Trans ID", "Book ID", "Book Title", "Student ID", "Student Name", "Issue Date", "Due Date"};
        activeTransModel = new DefaultTableModel(cols, 0);
        activeTransTable = new JTable(activeTransModel);
        activeTransTable.setRowHeight(24);
        panel.add(new JScrollPane(activeTransTable), BorderLayout.CENTER);

        btnIssue.addActionListener(e -> issueBook());
        btnReturn.addActionListener(e -> returnBook());
        btnRenew.addActionListener(e -> renewBook());

        return panel;
    }

    private void loadActiveTransactions() {
        activeTransModel.setRowCount(0);
        String sql = "SELECT t.TransID, t.BookID, b.Title, t.StudentID, s.Name, t.IssueDate, t.DueDate " +
                     "FROM TRANSACTION t JOIN BOOK b ON t.BookID = b.BookID " +
                     "JOIN STUDENT s ON t.StudentID = s.StudentID WHERE t.ReturnDate IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                activeTransModel.addRow(new Object[]{
                    rs.getInt("TransID"), rs.getInt("BookID"), rs.getString("Title"),
                    rs.getInt("StudentID"), rs.getString("Name"),
                    rs.getDate("IssueDate"), rs.getDate("DueDate")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + ex.getMessage());
        }
    }

    private void issueBook() {
        try {
            int bookId = Integer.parseInt(txtIssueBookId.getText().trim());
            int studentId = Integer.parseInt(txtIssueStudentId.getText().trim());
            int maxLimit = SessionManager.getConfig("maxBorrowLimit") != null
                    ? ((Number) SessionManager.getConfig("maxBorrowLimit")).intValue() : 3;

            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement psBook = conn.prepareStatement("SELECT Status FROM BOOK WHERE BookID=?");
                psBook.setInt(1, bookId);
                ResultSet rsBook = psBook.executeQuery();
                if (!rsBook.next()) { JOptionPane.showMessageDialog(this, "Book not found!"); return; }
                if (!"Available".equalsIgnoreCase(rsBook.getString("Status"))) {
                    JOptionPane.showMessageDialog(this, "Book is not available!"); return;
                }

                PreparedStatement psLimit = conn.prepareStatement("SELECT COUNT(*) FROM TRANSACTION WHERE StudentID=? AND ReturnDate IS NULL");
                psLimit.setInt(1, studentId);
                ResultSet rsLimit = psLimit.executeQuery();
                if (rsLimit.next() && rsLimit.getInt(1) >= maxLimit) {
                    JOptionPane.showMessageDialog(this, "Student has reached max borrow limit (" + maxLimit + " books)!"); return;
                }

                LocalDate issueDate = LocalDate.now();
                LocalDate dueDate = issueDate.plusDays(14);

                PreparedStatement psIns = conn.prepareStatement("INSERT INTO TRANSACTION (BookID, StudentID, IssueDate, DueDate) VALUES (?, ?, ?, ?)");
                psIns.setInt(1, bookId);
                psIns.setInt(2, studentId);
                psIns.setDate(3, Date.valueOf(issueDate));
                psIns.setDate(4, Date.valueOf(dueDate));
                psIns.executeUpdate();

                PreparedStatement psUpd = conn.prepareStatement("UPDATE BOOK SET Status='Issued' WHERE BookID=?");
                psUpd.setInt(1, bookId);
                psUpd.executeUpdate();

                JOptionPane.showMessageDialog(this, "Book issued! Due Date: " + dueDate);
                txtIssueBookId.setText(""); txtIssueStudentId.setText("");
                loadBooks("");
                loadActiveTransactions();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error issuing book: " + ex.getMessage());
        }
    }

    private void returnBook() {
        try {
            int bookId = Integer.parseInt(txtReturnBookId.getText().trim());
            double fineRate = SessionManager.getConfig("fineRate") != null
                    ? ((Number) SessionManager.getConfig("fineRate")).doubleValue() : 5.0;

            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("SELECT TransID, DueDate FROM TRANSACTION WHERE BookID=? AND ReturnDate IS NULL");
                ps.setInt(1, bookId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) { JOptionPane.showMessageDialog(this, "No active issue found for this Book ID!"); return; }

                int transId = rs.getInt("TransID");
                LocalDate dueDate = rs.getDate("DueDate").toLocalDate();
                LocalDate returnDate = LocalDate.now();

                long overdueDays = ChronoUnit.DAYS.between(dueDate, returnDate);
                double fine = overdueDays > 0 ? overdueDays * fineRate : 0.0;

                PreparedStatement psUpdTrans = conn.prepareStatement("UPDATE TRANSACTION SET ReturnDate=?, FineAmount=? WHERE TransID=?");
                psUpdTrans.setDate(1, Date.valueOf(returnDate));
                psUpdTrans.setDouble(2, fine);
                psUpdTrans.setInt(3, transId);
                psUpdTrans.executeUpdate();

                PreparedStatement psUpdBook = conn.prepareStatement("UPDATE BOOK SET Status='Available' WHERE BookID=?");
                psUpdBook.setInt(1, bookId);
                psUpdBook.executeUpdate();

                JOptionPane.showMessageDialog(this, "Book returned! Overdue Fine: Rs. " + fine);
                txtReturnBookId.setText("");
                loadBooks("");
                loadActiveTransactions();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error returning book: " + ex.getMessage());
        }
    }
    private void renewBook() {
        try {
            int bookId = Integer.parseInt(txtReturnBookId.getText().trim());
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("SELECT TransID, DueDate FROM TRANSACTION WHERE BookID=? AND ReturnDate IS NULL");
                ps.setInt(1, bookId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "No active issue found for this Book ID!");
                    return;
                }

                int transId = rs.getInt("TransID");
                LocalDate currentDueDate = rs.getDate("DueDate").toLocalDate();
                LocalDate newDueDate = currentDueDate.plusDays(14);

                PreparedStatement psUpd = conn.prepareStatement("UPDATE TRANSACTION SET DueDate=? WHERE TransID=?");
                psUpd.setDate(1, Date.valueOf(newDueDate));
                psUpd.setInt(2, transId);
                psUpd.executeUpdate();

                JOptionPane.showMessageDialog(this, "Book renewed! New Due Date: " + newDueDate);
                txtReturnBookId.setText("");
                loadActiveTransactions();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error renewing book: " + ex.getMessage());
        }
    }

    // ==================== 4. REPORTS & AUDITS TAB ====================
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.add(new JLabel("Select Report:"));
        cmbReportType = new JComboBox<>(new String[]{
            "1. Complete Book Inventory Report",
            "2. Available Books Report",
            "3. Issued Books Report",
            "4. Defaulters / Overdue Report",
            "5. Daily Transaction Log"
        });
        JButton btnGenerate = styleButton("Generate Report", new Color(41, 128, 185));
        topPanel.add(cmbReportType);
        topPanel.add(btnGenerate);
        panel.add(topPanel, BorderLayout.NORTH);

        reportTableModel = new DefaultTableModel();
        reportTable = new JTable(reportTableModel);
        reportTable.setRowHeight(24);
        panel.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        btnGenerate.addActionListener(e -> generateReport());
        return panel;
    }

    private void generateReport() {
        int idx = cmbReportType.getSelectedIndex();
        String sql = "";
        switch (idx) {
            case 0: sql = "SELECT BookID, ISBN, Title, Author, Category, Price, Status FROM BOOK"; break;
            case 1: sql = "SELECT BookID, ISBN, Title, Author, Category, Price FROM BOOK WHERE Status='Available'"; break;
            case 2: sql = "SELECT t.TransID, b.Title, s.Name AS Borrower, t.IssueDate, t.DueDate FROM TRANSACTION t JOIN BOOK b ON t.BookID=b.BookID JOIN STUDENT s ON t.StudentID=s.StudentID WHERE t.ReturnDate IS NULL"; break;
            case 3: sql = "SELECT t.TransID, b.Title, s.Name AS Defaulter, s.Contact, t.DueDate FROM TRANSACTION t JOIN BOOK b ON t.BookID=b.BookID JOIN STUDENT s ON t.StudentID=s.StudentID WHERE t.ReturnDate IS NULL AND t.DueDate < CURDATE()"; break;
            case 4: sql = "SELECT t.TransID, b.Title, s.Name, t.IssueDate, t.DueDate, t.ReturnDate, t.FineAmount FROM TRANSACTION t JOIN BOOK b ON t.BookID=b.BookID JOIN STUDENT s ON t.StudentID=s.StudentID WHERE t.IssueDate = CURDATE() OR t.ReturnDate = CURDATE()"; break;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            reportTableModel.setRowCount(0);
            reportTableModel.setColumnCount(0);
            for (int i = 1; i <= colCount; i++) reportTableModel.addColumn(meta.getColumnLabel(i));
            while (rs.next()) {
                Object[] row = new Object[colCount];
                for (int i = 1; i <= colCount; i++) row[i - 1] = rs.getObject(i);
                reportTableModel.addRow(row);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage());
        }
    }

    private JButton styleButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        return btn;
    }
}
