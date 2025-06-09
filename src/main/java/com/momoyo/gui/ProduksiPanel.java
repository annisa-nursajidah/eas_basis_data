package com.momoyo.gui;

import com.momoyo.model.BarangJadi;
import com.momoyo.model.Resep;
import com.momoyo.service.BarangJadiService;
import com.momoyo.service.BahanBakuService;
import com.momoyo.service.ProduksiService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ProduksiPanel extends JPanel {

    private final ProduksiService produksiService;
    private final BarangJadiService barangJadiService;
    private final BahanBakuService bahanBakuService;
    private JComboBox<BarangJadi> barangJadiComboBox;
    private JTextField jumlahField;
    private JButton produksiButton;

    public ProduksiPanel(ProduksiService produksiService, BarangJadiService barangJadiService,
            BahanBakuService bahanBakuService) {
        this.produksiService = produksiService;
        this.barangJadiService = barangJadiService;
        this.bahanBakuService = bahanBakuService;
        setLayout(new FlowLayout());

        JLabel barangLabel = new JLabel("Pilih Barang Jadi:");
        add(barangLabel);

        barangJadiComboBox = new JComboBox<>();
        loadBarangJadiComboBox();
        add(barangJadiComboBox);

        JLabel jumlahLabel = new JLabel("Jumlah Produksi:");
        add(jumlahLabel);

        jumlahField = new JTextField(10);
        add(jumlahField);

        produksiButton = new JButton("Produksi");
        produksiButton.addActionListener(e -> produksiBarang());
        add(produksiButton);
    }

    private void loadBarangJadiComboBox() {
        barangJadiComboBox.removeAllItems();
        try {
            List<BarangJadi> barangJadiList = barangJadiService.getBarangJadiAktif();
            for (BarangJadi barangJadi : barangJadiList) {
                barangJadiComboBox.addItem(barangJadi);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading barang jadi data: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void produksiBarang() {
        BarangJadi barangJadi = (BarangJadi) barangJadiComboBox.getSelectedItem();
        String jumlahStr = jumlahField.getText();

        if (barangJadi == null || jumlahStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih barang jadi dan masukkan jumlah.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int jumlah = Integer.parseInt(jumlahStr);
            if (jumlah <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah produksi harus lebih dari 0.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Resep resep = produksiService.getResepByBarangJadi(barangJadi.getIdBarang());
            if (resep == null) {
                JOptionPane.showMessageDialog(this, "Barang ini belum memiliki resep.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!cekKetersediaanBahan(resep, jumlah)) {
                JOptionPane.showMessageDialog(this, "Stok bahan baku tidak mencukupi.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (produksiService.produksiBarang(resep, jumlah)) {
                JOptionPane.showMessageDialog(this, "Produksi berhasil!");
                jumlahField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal melakukan produksi.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat produksi: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean cekKetersediaanBahan(Resep resep, int jumlahProduksi) {
        try {
            return produksiService.cekStokBarangJadi(resep.getBarangJadi().getIdBarang(), jumlahProduksi);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cek stok barang jadi: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
