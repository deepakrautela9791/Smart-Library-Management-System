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
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnLogin, btnCancel;

    public LoginFrame() {
        setTitle("Smart Library Management System - Login");
        setSize(400, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Library System Login", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        formPanel.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        char defaultEchoChar = txtPassword.getEchoChar();

        JToggleButton btnTogglePassword = new JToggleButton(createEyeIcon(false));
        btnTogglePassword.setToolTipText("Show Password");
        btnTogglePassword.setFocusable(false);
        btnTogglePassword.setBackground(Color.WHITE);
        btnTogglePassword.setPreferredSize(new Dimension(34, 24));
        btnTogglePassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTogglePassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnTogglePassword.isSelected()) {
                    txtPassword.setEchoChar((char) 0);
                    btnTogglePassword.setIcon(createEyeIcon(true));
                    btnTogglePassword.setToolTipText("Hide Password");
                } else {
                    txtPassword.setEchoChar(defaultEchoChar);
                    btnTogglePassword.setIcon(createEyeIcon(false));
                    btnTogglePassword.setToolTipText("Show Password");
                }
            }
        });

        JPanel passwordPanel = new JPanel(new BorderLayout(2, 0));
        passwordPanel.add(txtPassword, BorderLayout.CENTER);
        passwordPanel.add(btnTogglePassword, BorderLayout.EAST);
        formPanel.add(passwordPanel);

        formPanel.add(new JLabel("Role:"));
        cmbRole = new JComboBox<>(new String[]{"User", "Administrator"});
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

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private Icon createEyeIcon(boolean slashed) {
        return new Icon() {
            @Override
            public int getIconWidth() {
                return 16;
            }

            @Override
            public int getIconHeight() {
                return 16;
            }

            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(80, 80, 80));
                g2.setStroke(new BasicStroke(1.5f));

                g2.drawOval(x + 1, y + 4, 14, 8);
                g2.fillOval(x + 6, y + 6, 4, 4);

                if (slashed) {
                    g2.drawLine(x + 2, y + 14, x + 14, y + 2);
                }
                g2.dispose();
            }
        };
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String role = (String) cmbRole.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String hashedPassword = SecurityUtil.hashPassword(password);
        String sql = "SELECT AdminID FROM ADMIN WHERE Username = ? AND Password = ? AND Role = ?";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                return;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                stmt.setString(3, role);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int adminId = rs.getInt("AdminID");
                        SessionManager.setSession("username", username);
                        SessionManager.setSession("role", role);
                        SessionManager.setSession("adminId", adminId);
                        JOptionPane.showMessageDialog(this, "Login Successful! Welcome, " + username, "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();

                        if ("Administrator".equals(role)) {
                            new AdminDashboard(adminId).setVisible(true);
                        } else {
                            new StudentDashboard().setVisible(true);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid username, password, or role.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
