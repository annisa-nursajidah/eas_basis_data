package com.momoyo.gui;

import com.momoyo.model.Struk;
import com.momoyo.service.TransaksiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.sql.SQLException;
import java.util.List;

public class LaporanPenjualanPanel extends JPanel {

    private final TransaksiService transaksiService;
    private JTable laporanTable;
    private DefaultTableModel tableModel;
    private JTextField tanggalAwalField;
    private JTextField tanggalAkhirField;
    private JButton tampilkanButton;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public LaporanPenjualanPanel(TransaksiService transaksiService) {
        this.transaksiService = transaksiService;
        setLayout(new BorderLayout());

        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JLabel tanggalAwalLabel = new JLabel("Tanggal Awal (dd/MM/yyyy):");
        tanggalAwalField = new JTextField(10);
        panel.add(tanggalAwalLabel);
        panel.add(tanggalAwalField);

        JLabel tanggalAkhirLabel = new JLabel("Tanggal Akhir (dd/MM/yyyy):");
        tanggalAkhirField = new JTextField(10);
        panel.add(tanggalAkhirLabel);
        panel.add(tanggalAkhirField);

        tampilkanButton = new JButton("Tampilkan");
        tampilkanButton.addActionListener(e -> tampilkanLaporan());
        panel.add(tampilkanButton);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[] { "Nomor Struk", "Waktu", "Kasir", "Total" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        laporanTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(laporanTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void tampilkanLaporan() {
        String tanggalAwalStr = tanggalAwalField.getText();
        String tanggalAkhirStr = tanggalAkhirField.getText();

        if (tanggalAwalStr.trim().isEmpty() || tanggalAkhirStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tanggal awal dan akhir harus diisi.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate tanggalAwal = LocalDate.parse(tanggalAwalStr, dateFormatter);
            LocalDate tanggalAkhir = LocalDate.parse(tanggalAkhirStr, dateFormatter);

            if (tanggalAkhir.isBefore(tanggalAwal)) {
                JOptionPane.showMessageDialog(this, "Tanggal akhir tidak boleh sebelum tanggal awal.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Struk> laporan = transaksiService.getLaporanPenjualan(tanggalAwal, tanggalAkhir);
            tableModel.setRowCount(0);

            if (laporan.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tidak ada transaksi dalam rentang tanggal tersebut.", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            for (Struk struk : laporan) {
                tableModel.addRow(new Object[] {
                        struk.getNomorStruk(),
                        struk.getWaktuTransaksi().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        struk.getKasir().getUsername(),
                        struk.getTotalKeseluruhan()
                });
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Format tanggal tidak valid. Gunakan dd/MM/yyyy.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error menampilkan laporan: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
