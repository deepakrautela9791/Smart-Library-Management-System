package ui;

import db.DBConnection;

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

    public AdminDashboard(int adminId) {
        this.adminId = adminId;
        setTitle("Smart Library Management System - Admin Dashboard");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setOpaque(true);
        headerLabel.setBackground(new Color(44, 62, 80));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        add(headerLabel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        tabbedPane.addTab("Book Inventory", createBookInventoryPanel());
        tabbedPane.addTab("Member Registration", createMemberRegistrationPanel());
        tabbedPane.addTab("Circulation", createCirculationPanel());
        tabbedPane.addTab("Reports & Audits", createReportsPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // --- TAB 1: BOOK INVENTORY ---
    private JPanel createBookInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Book Details"));

        JTextField isbnField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField priceField = new JTextField();
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Available", "Issued", "Lost"});

        formPanel.add(new JLabel("ISBN:")); formPanel.add(isbnField);
        formPanel.add(new JLabel("Title:")); formPanel.add(titleField);
        formPanel.add(new JLabel("Author:")); formPanel.add(authorField);
        formPanel.add(new JLabel("Category:")); formPanel.add(categoryField);
        formPanel.add(new JLabel("Price (₹):")); formPanel.add(priceField);
        formPanel.add(new JLabel("Status:")); formPanel.add(statusCombo);

        String[] cols = {"Book ID", "ISBN", "Title", "Author", "Category", "Price", "Status"};
        DefaultTableModel bookModel = new DefaultTableModel(cols, 0);
        JTable bookTable = new JTable(bookModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);

        Runnable loadBooks = () -> {
            bookModel.setRowCount(0);
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM BOOK")) {
                while (rs.next()) {
                    bookModel.addRow(new Object[]{
                            rs.getInt("BookID"), rs.getString("ISBN"), rs.getString("Title"),
                            rs.getString("Author"), rs.getString("Category"), rs.getBigDecimal("Price"),
                            rs.getString("Status")
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };
        loadBooks.run();

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Book");
        JButton updateBtn = new JButton("Update Book");
        JButton deleteBtn = new JButton("Delete Book");
        JButton refreshBtn = new JButton("Refresh");

        btnPanel.add(addBtn); btnPanel.add(updateBtn); btnPanel.add(deleteBtn); btnPanel.add(refreshBtn);

        addBtn.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO BOOK (ISBN, Title, Author, Category, Price, Status, AdminID) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                stmt.setString(1, isbnField.getText().trim());
                stmt.setString(2, titleField.getText().trim());
                stmt.setString(3, authorField.getText().trim());
                stmt.setString(4, categoryField.getText().trim());
                stmt.setBigDecimal(5, new BigDecimal(priceField.getText().trim()));
                stmt.setString(6, (String) statusCombo.getSelectedItem());
                stmt.setInt(7, adminId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Book added successfully!");
                loadBooks.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        updateBtn.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a book from the table to update.");
                return;
            }
            int bookId = (int) bookModel.getValueAt(selectedRow, 0);
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE BOOK SET ISBN=?, Title=?, Author=?, Category=?, Price=?, Status=? WHERE BookID=?")) {
                stmt.setString(1, isbnField.getText().trim());
                stmt.setString(2, titleField.getText().trim());
                stmt.setString(3, authorField.getText().trim());
                stmt.setString(4, categoryField.getText().trim());
                stmt.setBigDecimal(5, new BigDecimal(priceField.getText().trim()));
                stmt.setString(6, (String) statusCombo.getSelectedItem());
                stmt.setInt(7, bookId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Book updated successfully!");
                loadBooks.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a book to delete.");
                return;
            }
            int bookId = (int) bookModel.getValueAt(selectedRow, 0);
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM BOOK WHERE BookID=?")) {
                stmt.setInt(1, bookId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Book deleted!");
                loadBooks.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        refreshBtn.addActionListener(e -> loadBooks.run());

        bookTable.getSelectionModel().addListSelectionListener(e -> {
            int r = bookTable.getSelectedRow();
            if (r != -1) {
                isbnField.setText(bookModel.getValueAt(r, 1).toString());
                titleField.setText(bookModel.getValueAt(r, 2).toString());
                authorField.setText(bookModel.getValueAt(r, 3).toString());
                categoryField.setText(bookModel.getValueAt(r, 4).toString());
                priceField.setText(bookModel.getValueAt(r, 5).toString());
                statusCombo.setSelectedItem(bookModel.getValueAt(r, 6).toString());
            }
        });

        JPanel top = new JPanel(new BorderLayout());
        top.add(formPanel, BorderLayout.CENTER);
        top.add(btnPanel, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // --- TAB 2: MEMBER REGISTRATION ---
    private JPanel createMemberRegistrationPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));

        JTextField nameField = new JTextField();
        JTextField courseField = new JTextField();
        JTextField semField = new JTextField();
        JTextField contactField = new JTextField();

        formPanel.add(new JLabel("Full Name:")); formPanel.add(nameField);
        formPanel.add(new JLabel("Course:")); formPanel.add(courseField);
        formPanel.add(new JLabel("Semester:")); formPanel.add(semField);
        formPanel.add(new JLabel("Contact:")); formPanel.add(contactField);

        String[] cols = {"Student ID", "Name", "Course", "Semester", "Contact"};
        DefaultTableModel studentModel = new DefaultTableModel(cols, 0);
        JTable studentTable = new JTable(studentModel);

        Runnable loadStudents = () -> {
            studentModel.setRowCount(0);
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM STUDENT")) {
                while (rs.next()) {
                    studentModel.addRow(new Object[]{
                            rs.getInt("StudentID"), rs.getString("Name"), rs.getString("Course"),
                            rs.getInt("Semester"), rs.getString("Contact")
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };
        loadStudents.run();

        JButton registerBtn = new JButton("Register Student");
        registerBtn.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO STUDENT (Name, Course, Semester, Contact) VALUES (?, ?, ?, ?)")) {
                stmt.setString(1, nameField.getText().trim());
                stmt.setString(2, courseField.getText().trim());
                stmt.setInt(3, Integer.parseInt(semField.getText().trim()));
                stmt.setString(4, contactField.getText().trim());
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Student registered successfully!");
                loadStudents.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        JPanel top = new JPanel(new BorderLayout());
        top.add(formPanel, BorderLayout.CENTER);
        top.add(registerBtn, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        return panel;
    }

    // --- TAB 3: CIRCULATION ---
    private JPanel createCirculationPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Issue Section
        JPanel issuePanel = new JPanel(new GridLayout(4, 2, 10, 10));
        issuePanel.setBorder(BorderFactory.createTitledBorder("Issue Book"));
        JTextField issueBookId = new JTextField();
        JTextField issueStudentId = new JTextField();
        JButton issueBtn = new JButton("Issue Book (14 Days)");

        issuePanel.add(new JLabel("Book ID:")); issuePanel.add(issueBookId);
        issuePanel.add(new JLabel("Student ID:")); issuePanel.add(issueStudentId);
        issuePanel.add(new JLabel()); issuePanel.add(issueBtn);

        issueBtn.addActionListener(e -> {
            int bId = Integer.parseInt(issueBookId.getText().trim());
            int sId = Integer.parseInt(issueStudentId.getText().trim());

            try (Connection conn = DBConnection.getConnection()) {
                // Check Availability
                PreparedStatement bStmt = conn.prepareStatement("SELECT Status FROM BOOK WHERE BookID = ?");
                bStmt.setInt(1, bId);
                ResultSet bRs = bStmt.executeQuery();
                if (!bRs.next() || !"Available".equalsIgnoreCase(bRs.getString("Status"))) {
                    JOptionPane.showMessageDialog(this, "Book is not available for issue.");
                    return;
                }

                // Check Active Issued Count (Limit max 3)
                PreparedStatement cStmt = conn.prepareStatement("SELECT COUNT(*) FROM TRANSACTION WHERE StudentID = ? AND ReturnDate IS NULL");
                cStmt.setInt(1, sId);
                ResultSet cRs = cStmt.executeQuery();
                if (cRs.next() && cRs.getInt(1) >= 3) {
                    JOptionPane.showMessageDialog(this, "Student has reached maximum issue limit (3 books).");
                    return;
                }

                // Issue Book
                LocalDate today = LocalDate.now();
                LocalDate dueDate = today.plusDays(14);
                PreparedStatement iStmt = conn.prepareStatement("INSERT INTO TRANSACTION (BookID, StudentID, IssueDate, DueDate) VALUES (?, ?, ?, ?)");
                iStmt.setInt(1, bId);
                iStmt.setInt(2, sId);
                iStmt.setDate(3, Date.valueOf(today));
                iStmt.setDate(4, Date.valueOf(dueDate));
                iStmt.executeUpdate();

                // Update Status
                PreparedStatement uStmt = conn.prepareStatement("UPDATE BOOK SET Status = 'Issued' WHERE BookID = ?");
                uStmt.setInt(1, bId);
                uStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Book Issued! Due Date: " + dueDate);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Return Section
        JPanel returnPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        returnPanel.setBorder(BorderFactory.createTitledBorder("Return Book"));
        JTextField returnTransId = new JTextField();
        JButton returnBtn = new JButton("Return Book & Calculate Fine");

        returnPanel.add(new JLabel("Transaction ID:")); returnPanel.add(returnTransId);
        returnPanel.add(new JLabel()); returnPanel.add(returnBtn);

        returnBtn.addActionListener(e -> {
            int tId = Integer.parseInt(returnTransId.getText().trim());

            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement tStmt = conn.prepareStatement("SELECT * FROM TRANSACTION WHERE TransID = ? AND ReturnDate IS NULL");
                tStmt.setInt(1, tId);
                ResultSet tRs = tStmt.executeQuery();
                if (!tRs.next()) {
                    JOptionPane.showMessageDialog(this, "Transaction not found or already returned.");
                    return;
                }

                int bId = tRs.getInt("BookID");
                Date dueDate = tRs.getDate("DueDate");
                LocalDate dueLocalDate = dueDate.toLocalDate();
                LocalDate returnLocalDate = LocalDate.now();

                long overdueDays = ChronoUnit.DAYS.between(dueLocalDate, returnLocalDate);
                double fine = 0.0;
                if (overdueDays > 0) {
                    fine = overdueDays * 5.0; // ₹5 per day
                }

                PreparedStatement rStmt = conn.prepareStatement("UPDATE TRANSACTION SET ReturnDate = ?, FineAmount = ? WHERE TransID = ?");
                rStmt.setDate(1, Date.valueOf(returnLocalDate));
                rStmt.setDouble(2, fine);
                rStmt.setInt(3, tId);
                rStmt.executeUpdate();

                PreparedStatement uStmt = conn.prepareStatement("UPDATE BOOK SET Status = 'Available' WHERE BookID = ?");
                uStmt.setInt(1, bId);
                uStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Book Returned! Overdue Days: " + Math.max(0, overdueDays) + ", Fine: ₹" + fine);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        panel.add(issuePanel);
        panel.add(returnPanel);
        return panel;
    }

    // --- TAB 4: REPORTS & AUDITS ---
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel controlPanel = new JPanel(new FlowLayout());
        JComboBox<String> reportCombo = new JComboBox<>(new String[]{
                "1. Complete Book Inventory",
                "2. Available Books",
                "3. Issued Books",
                "4. Defaulters / Overdue Report",
                "5. Daily Transaction Log"
        });
        JButton generateBtn = new JButton("Generate Report");
        controlPanel.add(new JLabel("Select Report:"));
        controlPanel.add(reportCombo);
        controlPanel.add(generateBtn);

        DefaultTableModel reportModel = new DefaultTableModel();
        JTable reportTable = new JTable(reportModel);
        JScrollPane scrollPane = new JScrollPane(reportTable);

        generateBtn.addActionListener(e -> {
            int selectedIndex = reportCombo.getSelectedIndex();
            reportModel.setRowCount(0);
            reportModel.setColumnCount(0);

            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement()) {

                switch (selectedIndex) {
                    case 0: // Complete Inventory
                        reportModel.setColumnIdentifiers(new String[]{"Book ID", "ISBN", "Title", "Author", "Category", "Price", "Status"});
                        ResultSet rs1 = stmt.executeQuery("SELECT BookID, ISBN, Title, Author, Category, Price, Status FROM BOOK");
                        while (rs1.next()) {
                            reportModel.addRow(new Object[]{rs1.getInt(1), rs1.getString(2), rs1.getString(3), rs1.getString(4), rs1.getString(5), rs1.getBigDecimal(6), rs1.getString(7)});
                        }
                        break;
                    case 1: // Available Books
                        reportModel.setColumnIdentifiers(new String[]{"Book ID", "ISBN", "Title", "Author", "Category", "Price"});
                        ResultSet rs2 = stmt.executeQuery("SELECT BookID, ISBN, Title, Author, Category, Price FROM BOOK WHERE Status = 'Available'");
                        while (rs2.next()) {
                            reportModel.addRow(new Object[]{rs2.getInt(1), rs2.getString(2), rs2.getString(3), rs2.getString(4), rs2.getString(5), rs2.getBigDecimal(6)});
                        }
                        break;
                    case 2: // Issued Books
                        reportModel.setColumnIdentifiers(new String[]{"Trans ID", "Book ID", "Book Title", "Student ID", "Student Name", "Issue Date", "Due Date"});
                        ResultSet rs3 = stmt.executeQuery("SELECT t.TransID, b.BookID, b.Title, s.StudentID, s.Name, t.IssueDate, t.DueDate FROM TRANSACTION t JOIN BOOK b ON t.BookID = b.BookID JOIN STUDENT s ON t.StudentID = s.StudentID WHERE t.ReturnDate IS NULL");
                        while (rs3.next()) {
                            reportModel.addRow(new Object[]{rs3.getInt(1), rs3.getInt(2), rs3.getString(3), rs3.getInt(4), rs3.getString(5), rs3.getDate(6), rs3.getDate(7)});
                        }
                        break;
                    case 3: // Defaulters / Overdue
                        reportModel.setColumnIdentifiers(new String[]{"Trans ID", "Student Name", "Contact", "Book Title", "Due Date", "Overdue Days", "Est. Fine (₹)"});
                        ResultSet rs4 = stmt.executeQuery("SELECT t.TransID, s.Name, s.Contact, b.Title, t.DueDate, DATEDIFF(CURRENT_DATE, t.DueDate) AS OverdueDays FROM TRANSACTION t JOIN BOOK b ON t.BookID = b.BookID JOIN STUDENT s ON t.StudentID = s.StudentID WHERE t.ReturnDate IS NULL AND t.DueDate < CURRENT_DATE");
                        while (rs4.next()) {
                            int overdueDays = rs4.getInt("OverdueDays");
                            reportModel.addRow(new Object[]{rs4.getInt(1), rs4.getString(2), rs4.getString(3), rs4.getString(4), rs4.getDate(5), overdueDays, overdueDays * 5.0});
                        }
                        break;
                    case 4: // Daily Transaction Log
                        reportModel.setColumnIdentifiers(new String[]{"Trans ID", "Book ID", "Student ID", "Issue Date", "Due Date", "Return Date", "Fine (₹)"});
                        ResultSet rs5 = stmt.executeQuery("SELECT TransID, BookID, StudentID, IssueDate, DueDate, ReturnDate, FineAmount FROM TRANSACTION ORDER BY TransID DESC");
                        while (rs5.next()) {
                            reportModel.addRow(new Object[]{rs5.getInt(1), rs5.getInt(2), rs5.getInt(3), rs5.getDate(4), rs5.getDate(5), rs5.getDate(6), rs5.getBigDecimal(7)});
                        }
                        break;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage());
            }
        });

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
}
