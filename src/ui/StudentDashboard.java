package ui;

import db.DBConnection;
import util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentDashboard extends JFrame {
    private JTextField searchField;
    private JComboBox<String> searchByComboBox;
    private JTable bookTable;
    private DefaultTableModel tableModel;

    public StudentDashboard() {
        setTitle("Smart Library Management System - Student Dashboard");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));

        JLabel headerLabel = new JLabel("Student Portal - Book Search & Inquiry");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel, BorderLayout.WEST);

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

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        
        searchPanel.add(new JLabel("Search By:"));
        searchByComboBox = new JComboBox<>(new String[]{"Title", "Author", "Category"});
        searchPanel.add(searchByComboBox);

        searchField = new JTextField(20);
        searchPanel.add(searchField);

        JButton searchButton = new JButton("Search");
        searchPanel.add(searchButton);

        JButton refreshButton = new JButton("Reset");
        searchPanel.add(refreshButton);

        // Table Panel
        String[] columnNames = {"Book ID", "ISBN", "Title", "Author", "Category", "Price", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Event Listeners
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBooks();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                loadBooks("");
            }
        });

        loadBooks("");
    }

    private void searchBooks() {
        String keyword = searchField.getText().trim();
        loadBooks(keyword);
    }

    private void loadBooks(String keyword) {
        tableModel.setRowCount(0);
        String searchBy = (String) searchByComboBox.getSelectedItem();
        String column = "Title";
        if ("Author".equals(searchBy)) column = "Author";
        else if ("Category".equals(searchBy)) column = "Category";

        String sql = "SELECT BookID, ISBN, Title, Author, Category, Price, Status FROM Book";
        if (!keyword.isEmpty()) {
            sql += " WHERE " + column + " LIKE ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (!keyword.isEmpty()) {
                stmt.setString(1, "%" + keyword + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("BookID"),
                        rs.getString("ISBN"),
                        rs.getString("Title"),
                        rs.getString("Author"),
                        rs.getString("Category"),
                        rs.getBigDecimal("Price"),
                        rs.getString("Status")
                };
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentDashboard().setVisible(true));
    }
}
