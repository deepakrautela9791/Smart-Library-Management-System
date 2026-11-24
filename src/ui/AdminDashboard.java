package ui;

import db.DBConnection;
import util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class AdminDashboard extends JFrame {
    private int adminId;
    private JTabbedPane tabbedPane;

    // Book components
    private JTable bookTable;
    private DefaultTableModel bookModel;
    private JTextField txtTitle, txtAuthor, txtCategory, txtPrice, txtQuantity, txtSearchBook;
    private JComboBox<String> cmbSearchBy;

    // Member components
    private JTable memberTable;
    private DefaultTableModel memberModel;
    private JTextField txtMemName, txtMemEmail, txtMemPhone;

    // Transaction components
    private JTable transTable;
    private DefaultTableModel transModel;
    private JTextField txtIssueBookId, txtIssueStudentId, txtReturnTransId;

    public AdminDashboard(int adminId) {
        this.adminId = adminId;
        setTitle("Smart Library Management System - Admin Dashboard");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));

        JLabel headerLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(Color.RED);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                SessionManager.clearSession();
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        headerPanel.add(btnLogout, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        tabbedPane.addTab("Book Inventory", createBookPanel());
        tabbedPane.addTab("Member Management", createMemberPanel());
        tabbedPane.addTab("Issue / Return Books", createTransactionPanel());

        add(tabbedPane, BorderLayout.CENTER);

        loadBookData("", "Title");
        loadMemberData();
        loadTransactionData();
    }

    private JPanel createBookPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel: Search Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(new JLabel("Search By:"));
        cmbSearchBy = new JComboBox<>(new String[]{"Title", "Author", "Category"});
        searchPanel.add(cmbSearchBy);
        txtSearchBook = new JTextField(15);
        searchPanel.add(txtSearchBook);

        JButton btnSearch = new JButton("Search");
        JButton btnReset = new JButton("Reset");
        searchPanel.add(btnSearch);
        searchPanel.add(btnReset);

        btnSearch.addActionListener(e -> searchBooks());
        btnReset.addActionListener(e -> {
            txtSearchBook.setText("");
            loadBookData("", "Title");
        });

        panel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"BookID", "Title", "Author", "Category", "Price", "Quantity", "Status"};
        bookModel = new DefaultTableModel(columns, 0);
        bookTable = new JTable(bookModel);
        panel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Manage Book"));

        formPanel.add(new JLabel("Title:"));
        txtTitle = new JTextField();
        formPanel.add(txtTitle);

        formPanel.add(new JLabel("Author:"));
        txtAuthor = new JTextField();
        formPanel.add(txtAuthor);

        formPanel.add(new JLabel("Category:"));
        txtCategory = new JTextField();
        formPanel.add(txtCategory);

        formPanel.add(new JLabel("Price:"));
        txtPrice = new JTextField();
        formPanel.add(txtPrice);

        formPanel.add(new JLabel("Quantity:"));
        txtQuantity = new JTextField();
        formPanel.add(txtQuantity);

        JButton btnAdd = new JButton("Add Book");
        JButton btnDelete = new JButton("Delete Selected");
        formPanel.add(btnAdd);
        formPanel.add(btnDelete);

        panel.add(formPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addBook());
        btnDelete.addActionListener(e -> deleteBook());

        return panel;
    }

    private void searchBooks() {
        String keyword = txtSearchBook.getText().trim();
        String searchBy = (String) cmbSearchBy.getSelectedItem();
        loadBookData(keyword, searchBy);
    }

    private void loadBookData(String keyword, String searchBy) {
        bookModel.setRowCount(0);
        String column = "Title";
        if ("Author".equals(searchBy)) column = "Author";
        else if ("Category".equals(searchBy)) column = "Category";

        String sql = "SELECT * FROM Book";
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " WHERE " + column + " LIKE ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (keyword != null && !keyword.trim().isEmpty()) {
                stmt.setString(1, "%" + keyword.trim() + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookModel.addRow(new Object[]{
                        rs.getInt("BookID"),
                        rs.getString("Title"),
                        rs.getString("Author"),
                        rs.getString("Category"),
                        rs.getBigDecimal("Price"),
                        rs.getInt("Quantity"),
                        rs.getString("Status")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addBook() {
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String category = txtCategory.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String qtyStr = txtQuantity.getText().trim();

        if (title.isEmpty() || author.isEmpty() || category.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all book fields.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO Book (Title, Author, Category, Price, Quantity, Status) VALUES (?, ?, ?, ?, ?, 'Available')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, category);
            stmt.setBigDecimal(4, new BigDecimal(priceStr));
            stmt.setInt(5, Integer.parseInt(qtyStr));
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Book Added Successfully!");
            txtTitle.setText(""); txtAuthor.setText(""); txtCategory.setText(""); txtPrice.setText(""); txtQuantity.setText("");
            loadBookData("", "Title");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding book: " + ex.getMessage());
        }
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a book to delete.");
            return;
        }

        int bookId = (int) bookModel.getValueAt(selectedRow, 0);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM Book WHERE BookID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bookId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Book Deleted!");
            loadBookData("", "Title");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting book: " + ex.getMessage());
        }
    }

    private JPanel createMemberPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"StudentID", "Name", "Email", "Phone"};
        memberModel = new DefaultTableModel(columns, 0);
        memberTable = new JTable(memberModel);
        panel.add(new JScrollPane(memberTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Register Student"));

        formPanel.add(new JLabel("Name:"));
        txtMemName = new JTextField();
        formPanel.add(txtMemName);

        formPanel.add(new JLabel("Email:"));
        txtMemEmail = new JTextField();
        formPanel.add(txtMemEmail);

        formPanel.add(new JLabel("Phone:"));
        txtMemPhone = new JTextField();
        formPanel.add(txtMemPhone);

        JButton btnAdd = new JButton("Register Student");
        formPanel.add(btnAdd);

        panel.add(formPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addStudent());

        return panel;
    }

    private void loadMemberData() {
        memberModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Student";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                memberModel.addRow(new Object[]{
                        rs.getInt("StudentID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Phone")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addStudent() {
        String name = txtMemName.getText().trim();
        String email = txtMemEmail.getText().trim();
        String phone = txtMemPhone.getText().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all student fields.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO Student (Name, Email, Phone) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Student Registered!");
            txtMemName.setText(""); txtMemEmail.setText(""); txtMemPhone.setText("");
            loadMemberData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error registering student: " + ex.getMessage());
        }
    }

    private JPanel createTransactionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"TransactionID", "BookID", "StudentID", "IssueDate", "DueDate", "ReturnDate", "FineAmount", "Status"};
        transModel = new DefaultTableModel(columns, 0);
        transTable = new JTable(transModel);
        panel.add(new JScrollPane(transTable), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        // Issue Panel
        JPanel issuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        issuePanel.setBorder(BorderFactory.createTitledBorder("Issue Book"));
        issuePanel.add(new JLabel("Book ID:"));
        txtIssueBookId = new JTextField(5);
        issuePanel.add(txtIssueBookId);

        issuePanel.add(new JLabel("Student ID:"));
        txtIssueStudentId = new JTextField(5);
        issuePanel.add(txtIssueStudentId);

        JButton btnIssue = new JButton("Issue Book");
        issuePanel.add(btnIssue);
        actionPanel.add(issuePanel);

        // Return Panel
        JPanel returnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        returnPanel.setBorder(BorderFactory.createTitledBorder("Return Book"));
        returnPanel.add(new JLabel("Transaction ID:"));
        txtReturnTransId = new JTextField(5);
        returnPanel.add(txtReturnTransId);

        JButton btnReturn = new JButton("Return Book");
        returnPanel.add(btnReturn);
        actionPanel.add(returnPanel);

        panel.add(actionPanel, BorderLayout.SOUTH);

        btnIssue.addActionListener(e -> issueBook());
        btnReturn.addActionListener(e -> returnBook());

        return panel;
    }

    private void loadTransactionData() {
        transModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Transactions";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transModel.addRow(new Object[]{
                        rs.getInt("TransactionID"),
                        rs.getInt("BookID"),
                        rs.getInt("StudentID"),
                        rs.getDate("IssueDate"),
                        rs.getDate("DueDate"),
                        rs.getDate("ReturnDate"),
                        rs.getBigDecimal("FineAmount"),
                        rs.getString("Status")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void issueBook() {
        String bookIdStr = txtIssueBookId.getText().trim();
        String studentIdStr = txtIssueStudentId.getText().trim();

        if (bookIdStr.isEmpty() || studentIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Book ID and Student ID.");
            return;
        }

        int maxBorrowLimit = ((Number) SessionManager.getConfig("maxBorrowLimit")).intValue();

        try (Connection conn = DBConnection.getConnection()) {
            // Check active borrows for limit
            String checkSql = "SELECT COUNT(*) FROM Transactions WHERE StudentID = ? AND Status = 'Issued'";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, Integer.parseInt(studentIdStr));
            ResultSet checkRs = checkStmt.executeQuery();
            if (checkRs.next() && checkRs.getInt(1) >= maxBorrowLimit) {
                JOptionPane.showMessageDialog(this, "Student has reached maximum borrow limit of " + maxBorrowLimit + " books!");
                return;
            }

            LocalDate today = LocalDate.now();
            LocalDate dueDate = today.plusDays(14);

            String sql = "INSERT INTO Transactions (BookID, StudentID, AdminID, IssueDate, DueDate, Status) VALUES (?, ?, ?, ?, ?, 'Issued')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(bookIdStr));
            stmt.setInt(2, Integer.parseInt(studentIdStr));
            stmt.setInt(3, adminId);
            stmt.setDate(4, Date.valueOf(today));
            stmt.setDate(5, Date.valueOf(dueDate));
            stmt.executeUpdate();

            // Update Book Status/Quantity
            String updateBook = "UPDATE Book SET Quantity = Quantity - 1 WHERE BookID = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateBook);
            updateStmt.setInt(1, Integer.parseInt(bookIdStr));
            updateStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Book Issued Successfully!");
            txtIssueBookId.setText(""); txtIssueStudentId.setText("");
            loadTransactionData();
            loadBookData("", "Title");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error issuing book: " + ex.getMessage());
        }
    }

    private void returnBook() {
        String transIdStr = txtReturnTransId.getText().trim();
        if (transIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Transaction ID.");
            return;
        }

        double fineRate = ((Number) SessionManager.getConfig("fineRate")).doubleValue();

        try (Connection conn = DBConnection.getConnection()) {
            int transId = Integer.parseInt(transIdStr);
            String selectSql = "SELECT BookID, DueDate FROM Transactions WHERE TransactionID = ? AND Status = 'Issued'";
            PreparedStatement selectStmt = conn.prepareStatement(selectSql);
            selectStmt.setInt(1, transId);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                int bookId = rs.getInt("BookID");
                Date dueDate = rs.getDate("DueDate");
                LocalDate dueLocalDate = dueDate.toLocalDate();
                LocalDate today = LocalDate.now();

                long overdueDays = ChronoUnit.DAYS.between(dueLocalDate, today);
                double fine = 0.0;
                if (overdueDays > 0) {
                    fine = overdueDays * fineRate;
                }

                String updateTrans = "UPDATE Transactions SET ReturnDate = ?, FineAmount = ?, Status = 'Returned' WHERE TransactionID = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateTrans);
                updateStmt.setDate(1, Date.valueOf(today));
                updateStmt.setBigDecimal(2, BigDecimal.valueOf(fine));
                updateStmt.setInt(3, transId);
                updateStmt.executeUpdate();

                String updateBook = "UPDATE Book SET Quantity = Quantity + 1 WHERE BookID = ?";
                PreparedStatement updateBookStmt = conn.prepareStatement(updateBook);
                updateBookStmt.setInt(1, bookId);
                updateBookStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Book Returned! Fine calculated: $" + fine);
                txtReturnTransId.setText("");
                loadTransactionData();
                loadBookData("", "Title");
            } else {
                JOptionPane.showMessageDialog(this, "Active transaction not found!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error returning book: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard(1).setVisible(true));
    }
}
