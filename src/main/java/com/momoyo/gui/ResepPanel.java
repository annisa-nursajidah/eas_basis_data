package com.momoyo.gui;

import com.momoyo.model.BarangJadi;
import com.momoyo.model.BahanBaku;
import com.momoyo.model.DetailResep;
import com.momoyo.model.Resep;
import com.momoyo.service.BarangJadiService;
import com.momoyo.service.BahanBakuService;
import com.momoyo.service.ProduksiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResepPanel extends JPanel {

    private final BarangJadiService barangJadiService;
    private final BahanBakuService bahanBakuService;
    private final ProduksiService produksiService;
    private JTable resepTable;
    private DefaultTableModel tableModel;
    private JComboBox<BarangJadi> barangJadiComboBox;
    private JTextField deskripsiField;
    private JButton tambahButton;
    private JButton ubahButton;
    private JButton hapusButton;
    private JButton tambahBahanButton;
    private JButton hapusBahanButton;
    private JButton simpanBahanButton;
    private JTable bahanTable;
    private DefaultTableModel bahanTableModel;

    private Resep selectedResep;

    public ResepPanel(BarangJadiService barangJadiService, BahanBakuService bahanBakuService,
            ProduksiService produksiService) {
        this.barangJadiService = barangJadiService;
        this.bahanBakuService = bahanBakuService;
        this.produksiService = produksiService;
        setLayout(new BorderLayout());

        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        loadResepData();
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JLabel barangJadiLabel = new JLabel("Barang Jadi:");
        barangJadiComboBox = new JComboBox<>();
        loadBarangJadiComboBox();
        panel.add(barangJadiLabel);
        panel.add(barangJadiComboBox);

        JLabel deskripsiLabel = new JLabel("Deskripsi:");
        deskripsiField = new JTextField(15);
        panel.add(deskripsiLabel);
        panel.add(deskripsiField);

        tambahButton = new JButton("Tambah");
        tambahButton.addActionListener(e -> tambahResep());
        panel.add(tambahButton);

        ubahButton = new JButton("Ubah");
        ubahButton.addActionListener(e -> ubahResep());
        panel.add(ubahButton);

        hapusButton = new JButton("Hapus");
        hapusButton.addActionListener(e -> hapusResep());
        panel.add(hapusButton);

        JPanel bahanPanel = new JPanel(new BorderLayout());
        JLabel bahanLabel = new JLabel("Bahan-bahan:");
        bahanPanel.add(bahanLabel, BorderLayout.NORTH);
        bahanTableModel = new DefaultTableModel(new Object[] { "ID", "Nama Bahan", "Kuantitas", "Satuan" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bahanTable = new JTable(bahanTableModel);
        JScrollPane bahanScrollPane = new JScrollPane(bahanTable);
        bahanPanel.add(bahanScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        tambahBahanButton = new JButton("Tambah Bahan");
        tambahBahanButton.addActionListener(e -> tambahDetailResep());
        buttonPanel.add(tambahBahanButton);

        hapusBahanButton = new JButton("Hapus Bahan");
        hapusBahanButton.addActionListener(e -> hapusDetailResep());
        buttonPanel.add(hapusBahanButton);

        simpanBahanButton = new JButton("Simpan Bahan");
        simpanBahanButton.addActionListener(e -> simpanDetailResep());
        buttonPanel.add(simpanBahanButton);
        bahanPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(bahanPanel);

        return panel;
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

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[] { "ID", "Barang Jadi", "Deskripsi" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resepTable = new JTable(tableModel);
        resepTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = resepTable.getSelectedRow();
            if (selectedRow != -1) {
                int resepId = (Integer) tableModel.getValueAt(selectedRow, 0);
                try {
                    selectedResep = produksiService.getResepById(resepId);
                    if (selectedResep != null) {
                        deskripsiField.setText(selectedResep.getDeskripsiResep());
                        barangJadiComboBox.setSelectedItem(selectedResep.getBarangJadi());
                        loadDetailResep(selectedResep);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error loading resep detail: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                selectedResep = null;
                deskripsiField.setText("");
                bahanTableModel.setRowCount(0);
            }
        });
        JScrollPane scrollPane = new JScrollPane(resepTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadResepData() {
        tableModel.setRowCount(0);
        try {
            List<Resep> resepList = produksiService.getAllResep();
            for (Resep resep : resepList) {
                tableModel.addRow(new Object[] {
                        resep.getIdResep(),
                        resep.getBarangJadi().getNamaBarang(),
                        resep.getDeskripsiResep()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading resep data: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDetailResep(Resep resep) {
        bahanTableModel.setRowCount(0);
        if (resep != null && resep.getDetailResepList() != null) {
            for (DetailResep detail : resep.getDetailResepList()) {
                bahanTableModel.addRow(new Object[] {
                        detail.getBahanBaku().getIdBahan(),
                        detail.getBahanBaku().getNamaBahan(),
                        detail.getKuantitasBahan(),
                        detail.getBahanBaku().getSatuan().getNamaSatuan()
                });
            }
        }
    }

    private void tambahResep() {
        BarangJadi barangJadi = (BarangJadi) barangJadiComboBox.getSelectedItem();
        String deskripsi = deskripsiField.getText();

        if (barangJadi == null || deskripsi.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Barang jadi dan deskripsi harus diisi.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Resep resep = new Resep();
        resep.setBarangJadi(barangJadi);
        resep.setDeskripsiResep(deskripsi);
        resep.setDetailResepList(new ArrayList<>());

        try {
            if (produksiService.tambahResep(resep)) {
                JOptionPane.showMessageDialog(this, "Resep berhasil ditambahkan.");
                loadResepData();
                clearInputFields();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan resep.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error menambahkan resep: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ubahResep() {
        int selectedRow = resepTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih resep yang akan diubah.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int resepId = (Integer) tableModel.getValueAt(selectedRow, 0);
        BarangJadi barangJadi = (BarangJadi) barangJadiComboBox.getSelectedItem();
        String deskripsi = deskripsiField.getText();

        if (barangJadi == null || deskripsi.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Barang jadi dan deskripsi harus diisi.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Resep resep = new Resep();
        resep.setIdResep(resepId);
        resep.setBarangJadi(barangJadi);
        resep.setDeskripsiResep(deskripsi);
        resep.setDetailResepList(selectedResep.getDetailResepList());

        try {
            if (produksiService.updateResep(resep)) {
                JOptionPane.showMessageDialog(this, "Resep berhasil diubah.");
                loadResepData();
                clearInputFields();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengubah resep.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error mengubah resep: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusResep() {
        int selectedRow = resepTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih resep yang akan dihapus.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int resepId = (Integer) tableModel.getValueAt(selectedRow, 0);

        int option = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus resep ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                if (produksiService.hapusResep(resepId)) {
                    JOptionPane.showMessageDialog(this, "Resep berhasil dihapus.");
                    loadResepData();
                    clearInputFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus resep.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error menghapus resep: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void tambahDetailResep() {
        if (selectedResep == null) {
            JOptionPane.showMessageDialog(this, "Pilih resep terlebih dahulu.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<BahanBaku> daftarBahanBaku = null;
        try {
            daftarBahanBaku = bahanBakuService.getAllBahanBaku();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error mengambil daftar bahan baku: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (daftarBahanBaku.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada bahan baku yang tersedia.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JComboBox<BahanBaku> bahanBakuComboBox = new JComboBox<>(daftarBahanBaku.toArray(new BahanBaku[0]));
        JTextField kuantitasField = new JTextField(10);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Bahan Baku:"));
        inputPanel.add(bahanBakuComboBox);
        inputPanel.add(new JLabel("Kuantitas:"));
        inputPanel.add(kuantitasField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Tambah Bahan Baku", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            BahanBaku selectedBahanBaku = (BahanBaku) bahanBakuComboBox.getSelectedItem();
            String kuantitasStr = kuantitasField.getText();

            if (selectedBahanBaku == null || kuantitasStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih bahan baku dan masukkan kuantitas.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                BigDecimal kuantitas = new BigDecimal(kuantitasStr);
                if (kuantitas.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this, "Kuantitas harus lebih dari 0.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                DetailResep detailResep = new DetailResep();
                detailResep.setResep(selectedResep);
                detailResep.setBahanBaku(selectedBahanBaku);
                detailResep.setKuantitasBahan(kuantitas);

                selectedResep.tambahDetailResep(detailResep);
                loadDetailResep(selectedResep);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Kuantitas harus berupa angka.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapusDetailResep() {
        int selectedRow = bahanTable.getSelectedRow();
        if (selectedRow == -1 || selectedResep == null) {
            JOptionPane.showMessageDialog(this, "Pilih bahan yang akan dihapus.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idBahan = (Integer) bahanTableModel.getValueAt(selectedRow, 0);

        DetailResep detailToRemove = null;
        for (DetailResep detail : selectedResep.getDetailResepList()) {
            if (detail.getBahanBaku().getIdBahan() == idBahan) {
                detailToRemove = detail;
                break;
            }
        }

        if (detailToRemove != null) {
            selectedResep.hapusDetailResep(detailToRemove);
            loadDetailResep(selectedResep);
        }
    }

    private void clearInputFields() {
        deskripsiField.setText("");
        barangJadiComboBox.setSelectedIndex(0);
        bahanTableModel.setRowCount(0);
    }

    private void simpanDetailResep() {

        if (selectedResep == null) {
            JOptionPane.showMessageDialog(this, "Pilih resep terlebih dahulu.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int resepId = selectedResep.getIdResep();

        if (selectedResep.getDetailResepList().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada bahan yang ditambahkan.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {

            if (produksiService.updateResep(selectedResep)) {
                JOptionPane.showMessageDialog(this, "Bahan resep berhasil disimpan.");

                loadResepData();

                try {
                    selectedResep = produksiService.getResepById(resepId);
                    if (selectedResep != null) {
                        loadDetailResep(selectedResep);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error memuat ulang resep: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan bahan resep.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error menyimpan bahan resep: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error tidak terduga: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
