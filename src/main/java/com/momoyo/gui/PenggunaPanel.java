package com.momoyo.gui;

import com.momoyo.model.Pengguna;
import com.momoyo.service.PenggunaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PenggunaPanel extends JPanel {

    private final PenggunaService penggunaService;
    private JTable penggunaTable;
    private DefaultTableModel tableModel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<Pengguna.Role> roleComboBox;
    private JButton tambahButton;
    private JButton ubahButton;
    private JButton hapusButton;

    public PenggunaPanel(PenggunaService penggunaService) {
        this.penggunaService = penggunaService;
        setLayout(new BorderLayout());

        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        loadPenggunaData();
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(10);
        panel.add(usernameLabel);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(10);
        panel.add(passwordLabel);
        panel.add(passwordField);

        JLabel roleLabel = new JLabel("Role:");
        roleComboBox = new JComboBox<>(Pengguna.Role.values());
        panel.add(roleLabel);
        panel.add(roleComboBox);

        tambahButton = new JButton("Tambah");
        tambahButton.addActionListener(e -> tambahPengguna());
        panel.add(tambahButton);

        ubahButton = new JButton("Ubah");
        ubahButton.addActionListener(e -> ubahPengguna());
        panel.add(ubahButton);

        hapusButton = new JButton("Hapus");
        hapusButton.addActionListener(e -> hapusPengguna());
        panel.add(hapusButton);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID", "Username", "Role"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        penggunaTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(penggunaTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadPenggunaData() {
        tableModel.setRowCount(0);
        try {
            List<Pengguna> penggunaList = penggunaService.getAllPengguna();
            for (Pengguna pengguna : penggunaList) {
                tableModel.addRow(new Object[]{
                        pengguna.getIdPengguna(),
                        pengguna.getUsername(),
                        pengguna.getPeran()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading pengguna data: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tambahPengguna() {
        String username = usernameField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);
        Pengguna.Role role = (Pengguna.Role) roleComboBox.getSelectedItem();

        if (username.trim().isEmpty() || password.trim().isEmpty() || role == null) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Pengguna pengguna = new Pengguna(username, password, role);
        try {
            if (penggunaService.tambahPengguna(pengguna)) {
                JOptionPane.showMessageDialog(this, "Pengguna berhasil ditambahkan.");
                loadPenggunaData();
                clearInputFields();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan pengguna.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error menambahkan pengguna: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            password = null;
        }
    }

    private void ubahPengguna() {
        int selectedRow = penggunaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pengguna yang akan diubah.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String username = usernameField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);
        Pengguna.Role role = (Pengguna.Role) roleComboBox.getSelectedItem();

        if (username.trim().isEmpty() || role == null) {
            JOptionPane.showMessageDialog(this, "Username dan Role harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Pengguna pengguna = new Pengguna();
        pengguna.setIdPengguna(id);
        pengguna.setUsername(username);
        if (password != null && !password.isEmpty()) {
            pengguna.setPassword(password);
        }
        pengguna.setPeran(role);

        try {
            if (penggunaService.updatePengguna(pengguna)) {
                JOptionPane.showMessageDialog(this, "Pengguna berhasil diubah.");
                loadPenggunaData();
                clearInputFields();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengubah pengguna.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error mengubah pengguna: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            password = null;
        }
    }

    private void hapusPengguna() {
        int selectedRow = penggunaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pengguna yang akan dihapus.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (Integer) tableModel.getValueAt(selectedRow, 0);

        int option = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus pengguna ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                if (penggunaService.hapusPengguna(id)) {
                    JOptionPane.showMessageDialog(this, "Pengguna berhasil dihapus.");
                    loadPenggunaData();
                    clearInputFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus pengguna.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error menghapus pengguna: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearInputFields() {
        usernameField.setText("");
        passwordField.setText("");
        roleComboBox.setSelectedIndex(0);
    }
}
