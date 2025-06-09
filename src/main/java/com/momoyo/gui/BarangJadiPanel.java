package com.momoyo.gui;

import com.momoyo.model.BarangJadi;
import com.momoyo.service.BarangJadiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class BarangJadiPanel extends JPanel {

    private final BarangJadiService barangJadiService;
    private JTable barangJadiTable;
    private DefaultTableModel tableModel;
    private JTextField namaBarangField;
    private JTextField hargaJualField;
    private JTextField stokJadiField;
    private JCheckBox aktifCheckBox;
    private JButton tambahButton;
    private JButton ubahButton;
    private JButton nonaktifkanButton;

    public BarangJadiPanel(BarangJadiService barangJadiService) {
        this.barangJadiService = barangJadiService;
        setLayout(new BorderLayout());

        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        loadBarangJadiData();
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JLabel namaBarangLabel = new JLabel("Nama Barang:");
        namaBarangField = new JTextField(10);
        panel.add(namaBarangLabel);
        panel.add(namaBarangField);

        JLabel hargaJualLabel = new JLabel("Harga Jual:");
        hargaJualField = new JTextField(10);
        panel.add(hargaJualLabel);
        panel.add(hargaJualField);

        JLabel stokJadiLabel = new JLabel("Stok Awal:");
        stokJadiField = new JTextField(10);
        panel.add(stokJadiLabel);
        panel.add(stokJadiField);

        aktifCheckBox = new JCheckBox("Aktif");
        aktifCheckBox.setSelected(true);
        panel.add(aktifCheckBox);

        tambahButton = new JButton("Tambah");
        tambahButton.addActionListener(e -> tambahBarangJadi());
        panel.add(tambahButton);

        ubahButton = new JButton("Ubah");
        ubahButton.addActionListener(e -> ubahBarangJadi());
        panel.add(ubahButton);

        nonaktifkanButton = new JButton("Nonaktifkan");
        nonaktifkanButton.addActionListener(e -> nonaktifkanBarangJadi());
        panel.add(nonaktifkanButton);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[] { "ID", "Nama Barang", "Harga Jual", "Stok", "Status" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        barangJadiTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(barangJadiTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadBarangJadiData() {
        tableModel.setRowCount(0);
        try {
            List<BarangJadi> barangJadiList = barangJadiService.getAllBarangJadi();
            for (BarangJadi barangJadi : barangJadiList) {
                tableModel.addRow(new Object[] {
                        barangJadi.getIdBarang(),
                        barangJadi.getNamaBarang(),
                        barangJadi.getHargaJual(),
                        barangJadi.getStokJadi(),
                        barangJadi.isActive() ? "Aktif" : "Nonaktif"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading barang jadi data: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tambahBarangJadi() {
        String namaBarang = namaBarangField.getText();
        String hargaJualStr = hargaJualField.getText();
        String stokJadiStr = stokJadiField.getText();
        boolean isActive = aktifCheckBox.isSelected();

        if (namaBarang.trim().isEmpty() || hargaJualStr.trim().isEmpty() || stokJadiStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BigDecimal hargaJual = new BigDecimal(hargaJualStr);
            int stokJadi = Integer.parseInt(stokJadiStr);

            BarangJadi barangJadi = new BarangJadi(namaBarang, hargaJual);
            barangJadi.setStokJadi(stokJadi);
            barangJadi.setActive(isActive);

            if (barangJadiService.tambahBarangJadi(barangJadi)) {
                JOptionPane.showMessageDialog(this, "Barang jadi berhasil ditambahkan.");
                loadBarangJadiData();
                clearInputFields();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan barang jadi.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga jual dan stok harus berupa angka.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error menambahkan barang jadi: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ubahBarangJadi() {
        int selectedRow = barangJadiTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih barang jadi yang akan diubah.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String namaBarang = namaBarangField.getText();
        String hargaJualStr = hargaJualField.getText();
        String stokJadiStr = stokJadiField.getText();
        boolean isActive = aktifCheckBox.isSelected();

        if (namaBarang.trim().isEmpty() || hargaJualStr.trim().isEmpty() || stokJadiStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BigDecimal hargaJual = new BigDecimal(hargaJualStr);
            int stokJadi = Integer.parseInt(stokJadiStr);

            BarangJadi barangJadi = new BarangJadi();
            barangJadi.setIdBarang(id);
            barangJadi.setNamaBarang(namaBarang);
            barangJadi.setHargaJual(hargaJual);
            barangJadi.setStokJadi(stokJadi);
            barangJadi.setActive(isActive);

            if (barangJadiService.updateBarangJadi(barangJadi)) {
                JOptionPane.showMessageDialog(this, "Barang jadi berhasil diubah.");
                loadBarangJadiData();
                clearInputFields();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengubah barang jadi.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga jual dan stok harus berupa angka.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error mengubah barang jadi: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void nonaktifkanBarangJadi() {
        int selectedRow = barangJadiTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih barang jadi yang akan dinonaktifkan.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (Integer) tableModel.getValueAt(selectedRow, 0);

        int option = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menonaktifkan barang jadi ini?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                if (barangJadiService.nonaktifkanBarang(id)) {
                    JOptionPane.showMessageDialog(this, "Barang jadi berhasil dinonaktifkan.");
                    loadBarangJadiData();
                    clearInputFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menonaktifkan barang jadi.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error menonaktifkan barang jadi: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearInputFields() {
        namaBarangField.setText("");
        hargaJualField.setText("");
        stokJadiField.setText("");
        aktifCheckBox.setSelected(true);
    }
}
