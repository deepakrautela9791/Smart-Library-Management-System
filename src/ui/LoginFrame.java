package ui;

import db.DBConnection;
import util.SecurityUtil;
import util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnLogin, btnCancel;

    public LoginFrame() {
        setTitle("Smart Library - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        JLabel lblTitle = new JLabel("Smart Library System");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        formPanel.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        formPanel.add(txtPassword);

        formPanel.add(new JLabel("Role:"));
        cmbRole = new JComboBox<>(new String[]{"Student", "Administrator"});
        formPanel.add(cmbRole);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        btnLogin = new JButton("Login");
        btnCancel = new JButton("Cancel");

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        btnCancel.addActionListener(e -> System.exit(0));
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String role = (String) cmbRole.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM Users WHERE Username = ? AND Password = ? AND Role = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, SecurityUtil.hashPassword(password));
            stmt.setString(3, role);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int adminId = rs.getInt("AdminID");
                SessionManager.setSession("username", username);
                SessionManager.setSession("role", role);
                SessionManager.setSession("adminId", rs.getInt("AdminID"));
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
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
