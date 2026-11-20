package ui;

import db.DBConnection;
import util.SecurityUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Smart Library Management System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        JLabel headerLabel = new JLabel("Smart Library Login");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Role:"));
        roleComboBox = new JComboBox<>(new String[]{"Administrator", "User"});
        formPanel.add(roleComboBox);

        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(46, 204, 113));
        loginButton.setForeground(Color.WHITE);
        formPanel.add(new JLabel()); // Placeholder
        formPanel.add(loginButton);

        add(formPanel, BorderLayout.CENTER);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Username and Password.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hashedPassword = SecurityUtil.hashPassword(password);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM ADMIN WHERE Username = ? AND Password = ? AND Role = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                stmt.setString(3, role);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int adminId = rs.getInt("AdminID");
                    JOptionPane.showMessageDialog(this, "Login Successful! Welcome, " + username, "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();

                    if ("Administrator".equals(role)) {
                        new AdminDashboard(adminId).setVisible(true);
                    } else {
                        new StudentDashboard().setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials or role mismatch.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Connection Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
