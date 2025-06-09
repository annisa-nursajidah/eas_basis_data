package com.momoyo.gui;

import com.momoyo.model.DetailTransaksi;
import com.momoyo.model.Struk;
import com.momoyo.service.TransaksiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.sql.SQLException;

public class CariStrukPanel extends JPanel {

    private final TransaksiService transaksiService;
    private JTextField nomorStrukField;
    private JButton cariButton;
    private JTable detailTable;
    private DefaultTableModel tableModel;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public CariStrukPanel(TransaksiService transaksiService) {
        this.transaksiService = transaksiService;
        setLayout(new BorderLayout());

        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JLabel nomorStrukLabel = new JLabel("Nomor Struk:");
        panel.add(nomorStrukLabel);

        nomorStrukField = new JTextField(15);
        panel.add(nomorStrukField);

        cariButton = new JButton("Cari");
        cariButton.addActionListener(e -> cariStruk());
        panel.add(cariButton);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[] { "Nama Barang", "Harga", "Jumlah", "Subtotal" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        detailTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(detailTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void cariStruk() {
        String nomorStruk = nomorStrukField.getText();
        if (nomorStruk.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan nomor struk.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Struk struk = transaksiService.getStrukByNomor(nomorStruk);
            if (struk == null) {
                JOptionPane.showMessageDialog(this, "Struk tidak ditemukan.", "Info", JOptionPane.INFORMATION_MESSAGE);
                tableModel.setRowCount(0);
                return;
            }

            tableModel.setRowCount(0);
            for (DetailTransaksi detail : struk.getDetailTransaksiList()) {
                tableModel.addRow(new Object[] {
                        detail.getNamaBarangSaatTransaksi(),
                        detail.getHargaSatuanSaatTransaksi(),
                        detail.getJumlahTerjual(),
                        detail.getSubtotal()
                });
            }

            JOptionPane.showMessageDialog(this, "Struk ditemukan.", "Info", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error mencari struk: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
