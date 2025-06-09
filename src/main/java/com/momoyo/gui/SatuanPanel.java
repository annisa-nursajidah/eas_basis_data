package com.momoyo.gui;

import com.momoyo.model.Satuan;
import com.momoyo.service.SatuanService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class SatuanPanel extends JPanel {

    private final SatuanService satuanService;
    private JTable satuanTable;
    private DefaultTableModel tableModel;
    private JTextField namaSatuanField;
    private JButton tambahButton;
    private JButton ubahButton;
    private JButton hapusButton;

    public SatuanPanel(SatuanService satuanService) {
        this.satuanService = satuanService;
        setLayout(new BorderLayout());

        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        loadSatuanData();
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JLabel namaSatuanLabel = new JLabel("Nama Satuan:");
        namaSatuanField = new JTextField(10);
        panel.add(namaSatuanLabel);
        panel.add(namaSatuanField);

        tambahButton = new JButton("Tambah");
        tambahButton.addActionListener(e -> tambahSatuan());
        panel.add(tambahButton);

        ubahButton = new JButton("Ubah");
        ubahButton.addActionListener(e -> ubahSatuan());
        panel.add(ubahButton);

        hapusButton = new JButton("Hapus");
        hapusButton.addActionListener(e -> hapusSatuan());
        panel.add(hapusButton);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nama Satuan"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        satuanTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(satuanTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadSatuanData() {
        tableModel.setRowCount(0);
        try {
            List<Satuan> satuanList = satuanService.getAllSatuan();
            for (Satuan satuan : satuanList) {
                tableModel.addRow(new Object[]{
                        satuan.getIdSatuan(),
                        satuan.getNamaSatuan()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading satuan data: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tambahSatuan() {
        String namaSatuan = namaSatuanField.getText();
        if (namaSatuan.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama satuan harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Satuan satuan = new Satuan(namaSatuan);
        try {
            if (satuanService.tambahSatuan(satuan)) {
                JOptionPane.showMessageDialog(this, "Satuan berhasil ditambahkan.");
                loadSatuanData();
                namaSatuanField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan satuan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error menambahkan satuan: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ubahSatuan() {
        int selectedRow = satuanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih satuan yang akan diubah.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String namaSatuan = namaSatuanField.getText();
        if (namaSatuan.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama satuan harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Satuan satuan = new Satuan(id, namaSatuan);
        try {
            if (satuanService.updateSatuan(satuan)) {
                JOptionPane.showMessageDialog(this, "Satuan berhasil diubah.");
                loadSatuanData();
                namaSatuanField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengubah satuan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error mengubah satuan: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusSatuan() {
        int selectedRow = satuanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih satuan yang akan dihapus.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (Integer) tableModel.getValueAt(selectedRow, 0);

        int option = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus satuan ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                if (satuanService.hapusSatuan(id)) {
                    JOptionPane.showMessageDialog(this, "Satuan berhasil dihapus.");
                    loadSatuanData();
                    namaSatuanField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus satuan.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error menghapus satuan: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IllegalStateException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
