package com.momoyo.gui;

import com.momoyo.model.BahanBaku;
import com.momoyo.model.Satuan;
import com.momoyo.service.BahanBakuService;
import com.momoyo.service.SatuanService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class BahanBakuPanel extends JPanel {

    private final BahanBakuService bahanBakuService;
    private final SatuanService satuanService;
    private JTable bahanBakuTable;
    private DefaultTableModel tableModel;
    private JTextField namaBahanField;
    private JComboBox<Satuan> satuanComboBox;
    private JTextField stokTersediaField;
    private JTextField ambangBatasField;
    private JButton tambahButton;
    private JButton ubahButton;
    private JButton hapusButton;

    public BahanBakuPanel(BahanBakuService bahanBakuService, SatuanService satuanService) {
        this.bahanBakuService = bahanBakuService;
        this.satuanService = satuanService;
        setLayout(new BorderLayout());

        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        loadBahanBakuData();
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JLabel namaBahanLabel = new JLabel("Nama Bahan:");
        namaBahanField = new JTextField(10);
        panel.add(namaBahanLabel);
        panel.add(namaBahanField);

        JLabel satuanLabel = new JLabel("Satuan:");
        satuanComboBox = new JComboBox<>();
        loadSatuanComboBox();
        panel.add(satuanLabel);
        panel.add(satuanComboBox);

        JLabel stokTersediaLabel = new JLabel("Stok Tersedia:");
        stokTersediaField = new JTextField(10);
        panel.add(stokTersediaLabel);
        panel.add(stokTersediaField);

        JLabel ambangBatasLabel = new JLabel("Ambang Batas:");
        ambangBatasField = new JTextField(10);
        panel.add(ambangBatasLabel);
        panel.add(ambangBatasField);

        tambahButton = new JButton("Tambah");
        tambahButton.addActionListener(e -> tambahBahanBaku());
        panel.add(tambahButton);

        ubahButton = new JButton("Ubah");
        ubahButton.addActionListener(e -> ubahBahanBaku());
        panel.add(ubahButton);

        hapusButton = new JButton("Hapus");
        hapusButton.addActionListener(e -> hapusBahanBaku());
        panel.add(hapusButton);

        return panel;
    }

    private void loadSatuanComboBox() {
        satuanComboBox.removeAllItems();
        try {
            List<Satuan> satuanList = satuanService.getAllSatuan();
            for (Satuan satuan : satuanList) {
                satuanComboBox.addItem(satuan);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading satuan data: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nama Bahan", "Satuan", "Stok", "Ambang Batas"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bahanBakuTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bahanBakuTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadBahanBakuData() {
        tableModel.setRowCount(0);
        try {
            List<BahanBaku> bahanBakuList = bahanBakuService.getAllBahanBaku();
            for (BahanBaku bahanBaku : bahanBakuList) {
                tableModel.addRow(new Object[]{
                        bahanBaku.getIdBahan(),
                        bahanBaku.getNamaBahan(),
                        bahanBaku.getSatuan().getNamaSatuan(),
                        bahanBaku.getStokTersedia(),
                        bahanBaku.getAmbangBatas()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading bahan baku data: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tambahBahanBaku() {
        String namaBahan = namaBahanField.getText();
        Satuan satuan = (Satuan) satuanComboBox.getSelectedItem();
        String stokTersediaStr = stokTersediaField.getText();
        String ambangBatasStr = ambangBatasField.getText();

        if (namaBahan.trim().isEmpty() || satuan == null || stokTersediaStr.trim().isEmpty() || ambangBatasStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BigDecimal stokTersedia = new BigDecimal(stokTersediaStr);
            BigDecimal ambangBatas = new BigDecimal(ambangBatasStr);

            BahanBaku bahanBaku = new BahanBaku(namaBahan, satuan, stokTersedia, ambangBatas);
            if (bahanBakuService.tambahBahanBaku(bahanBaku)) {
                JOptionPane.showMessageDialog(this, "Bahan baku berhasil ditambahkan.");
                loadBahanBakuData();
                clearInputFields();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan bahan baku.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Stok dan ambang batas harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error menambahkan bahan baku: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ubahBahanBaku() {
        int selectedRow = bahanBakuTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih bahan baku yang akan diubah.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String namaBahan = namaBahanField.getText();
        Satuan satuan = (Satuan) satuanComboBox.getSelectedItem();
        String stokTersediaStr = stokTersediaField.getText();
        String ambangBatasStr = ambangBatasField.getText();

        if (namaBahan.trim().isEmpty() || satuan == null || stokTersediaStr.trim().isEmpty() || ambangBatasStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BigDecimal stokTersedia = new BigDecimal(stokTersediaStr);
            BigDecimal ambangBatas = new BigDecimal(ambangBatasStr);

            BahanBaku bahanBaku = new BahanBaku();
            bahanBaku.setIdBahan(id);
            bahanBaku.setNamaBahan(namaBahan);
            bahanBaku.setSatuan(satuan);
            bahanBaku.setStokTersedia(stokTersedia);
            bahanBaku.setAmbangBatas(ambangBatas);

            if (bahanBakuService.updateBahanBaku(bahanBaku)) {
                JOptionPane.showMessageDialog(this, "Bahan baku berhasil diubah.");
                loadBahanBakuData();
                clearInputFields();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengubah bahan baku.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Stok dan ambang batas harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error mengubah bahan baku: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusBahanBaku() {
        int selectedRow = bahanBakuTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih bahan baku yang akan dihapus.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (Integer) tableModel.getValueAt(selectedRow, 0);

        int option = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus bahan baku ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                if (bahanBakuService.hapusBahanBaku(id)) {
                    JOptionPane.showMessageDialog(this, "Bahan baku berhasil dihapus.");
                    loadBahanBakuData();
                    clearInputFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus bahan baku.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error menghapus bahan baku: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearInputFields() {
        namaBahanField.setText("");
        satuanComboBox.setSelectedIndex(0);
        stokTersediaField.setText("");
        ambangBatasField.setText("");
    }
}
