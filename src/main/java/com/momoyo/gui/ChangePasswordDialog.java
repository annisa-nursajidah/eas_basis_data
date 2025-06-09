package com.momoyo.gui;

import com.momoyo.model.Pengguna;
import com.momoyo.service.PenggunaService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ChangePasswordDialog extends JDialog {

    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton changeButton;
    private final PenggunaService penggunaService;
    private final Pengguna currentUser;

    public ChangePasswordDialog(Frame parent, PenggunaService penggunaService, Pengguna currentUser) {
        super(parent, "Ganti Password", true);
        this.penggunaService = penggunaService;
        this.currentUser = currentUser;

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel oldPasswordLabel = new JLabel("Password Lama:");
        oldPasswordField = new JPasswordField();
        JLabel newPasswordLabel = new JLabel("Password Baru:");
        newPasswordField = new JPasswordField();
        JLabel confirmPasswordLabel = new JLabel("Konfirmasi Password Baru:");
        confirmPasswordField = new JPasswordField();
        changeButton = new JButton("Ganti Password");

        panel.add(oldPasswordLabel);
        panel.add(oldPasswordField);
        panel.add(newPasswordLabel);
        panel.add(newPasswordField);
        panel.add(confirmPasswordLabel);
        panel.add(confirmPasswordField);
        panel.add(new JLabel());
        panel.add(changeButton);

        changeButton.addActionListener(e -> changePassword());

        getContentPane().add(panel, BorderLayout.CENTER);
        pack();
        setResizable(false);
    }

    private void changePassword() {
        char[] oldPasswordChars = oldPasswordField.getPassword();
        char[] newPasswordChars = newPasswordField.getPassword();
        char[] confirmPasswordChars = confirmPasswordField.getPassword();

        String oldPassword = new String(oldPasswordChars);
        String newPassword = new String(newPasswordChars);
        String confirmPassword = new String(confirmPasswordChars);

        if (oldPassword.trim().isEmpty() || newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Password baru dan konfirmasi password tidak cocok.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {

            if (!currentUser.verifikasiPassword(oldPassword)) {
                JOptionPane.showMessageDialog(this, "Password lama salah.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            currentUser.setPassword(newPassword);
            if (penggunaService.updatePengguna(currentUser)) {
                JOptionPane.showMessageDialog(this, "Password berhasil diubah.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengubah password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat mengubah password: " + e.getMessage(), "Error Database",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            if (oldPasswordChars != null)
                java.util.Arrays.fill(oldPasswordChars, '0');
            if (newPasswordChars != null)
                java.util.Arrays.fill(newPasswordChars, '0');
            if (confirmPasswordChars != null)
                java.util.Arrays.fill(confirmPasswordChars, '0');
        }
    }
}
