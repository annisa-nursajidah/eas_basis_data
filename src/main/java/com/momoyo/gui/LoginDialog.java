package com.momoyo.gui;

import com.momoyo.model.Pengguna;
import com.momoyo.service.PenggunaService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginDialog extends JDialog {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private Pengguna authenticatedUser;
    private final PenggunaService penggunaService;

    public LoginDialog(Frame parent, PenggunaService penggunaService) {
        super(parent, "Login", true);
        this.penggunaService = penggunaService;
        authenticatedUser = null;

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(loginButton);

        loginButton.addActionListener(e -> performLogin());

        passwordField.addActionListener(e -> performLogin());

        getContentPane().add(panel, BorderLayout.CENTER);
        pack();
        setResizable(false);
    }

    private void performLogin() {
        String username = usernameField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password harus diisi.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            authenticatedUser = penggunaService.login(username, password);
            if (authenticatedUser != null) {
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username atau password salah.", "Login Gagal",
                        JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat login: " + e.getMessage(), "Error Database",
                    JOptionPane.ERROR_MESSAGE);
        } finally {

            if (passwordChars != null) {
                java.util.Arrays.fill(passwordChars, '0');
            }
        }
    }

    public Pengguna getAuthenticatedUser() {
        return authenticatedUser;
    }
}
