package com.momoyo.gui;

import com.momoyo.model.BarangJadi;
import com.momoyo.model.DetailTransaksi;
import com.momoyo.model.Pengguna;
import com.momoyo.model.Struk;
import com.momoyo.service.BarangJadiService;
import com.momoyo.service.TransaksiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.DefaultCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransaksiPanel extends JPanel {

    private final TransaksiService transaksiService;
    private final BarangJadiService barangJadiService;
    private final Pengguna kasir;
    private JComboBox<BarangJadi> barangJadiComboBox;
    private JTextField jumlahField;
    private JButton tambahButton;
    private JButton selesaiButton;
    private JTable detailTable;
    private DefaultTableModel tableModel;
    private List<DetailTransaksi> detailTransaksiList;
    private JLabel totalLabel;

    public TransaksiPanel(TransaksiService transaksiService, BarangJadiService barangJadiService, Pengguna kasir) {
        this.transaksiService = transaksiService;
        this.barangJadiService = barangJadiService;
        this.kasir = kasir;
        this.detailTransaksiList = new ArrayList<>();
        setLayout(new BorderLayout());

        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        totalLabel = new JLabel("Total: Rp 0.00");
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add some padding
        add(totalLabel, BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JLabel barangLabel = new JLabel("Pilih Barang:");
        panel.add(barangLabel);

        barangJadiComboBox = new JComboBox<>();
        loadBarangJadiComboBox();
        panel.add(barangJadiComboBox);

        JLabel jumlahLabel = new JLabel("Jumlah:");
        panel.add(jumlahLabel);

        jumlahField = new JTextField(5);
        panel.add(jumlahField);

        tambahButton = new JButton("Tambah");
        tambahButton.addActionListener(e -> tambahDetailTransaksi());
        panel.add(tambahButton);

        selesaiButton = new JButton("Selesai");
        selesaiButton.addActionListener(e -> simpanTransaksi());
        panel.add(selesaiButton);

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
        // Added "Aksi" column
        tableModel = new DefaultTableModel(new Object[] { "Nama Barang", "Harga", "Jumlah", "Subtotal", "Aksi" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only the "Aksi" column (index 4) is editable
                return column == 4;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) {
                    return JButton.class; // Treat the column as buttons
                }
                return super.getColumnClass(columnIndex);
            }
        };
        detailTable = new JTable(tableModel);

        // Set up the button column renderer and editor
        detailTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        detailTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JTextField())); // Use default JTextField editor

        JScrollPane scrollPane = new JScrollPane(detailTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void tambahDetailTransaksi() {
        BarangJadi barangJadi = (BarangJadi) barangJadiComboBox.getSelectedItem();
        String jumlahStr = jumlahField.getText();

        if (barangJadi == null || jumlahStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih barang dan masukkan jumlah.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int jumlah = Integer.parseInt(jumlahStr);
            if (jumlah <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if item already exists in the list
            boolean found = false;
            for (DetailTransaksi detail : detailTransaksiList) {
                if (detail.getBarangJadi().getIdBarang() == barangJadi.getIdBarang()) {
                    // Update quantity if item exists
                    int newJumlah = detail.getJumlahTerjual() + jumlah;
                    // Need to fetch the latest stock from the service/DAO before checking
                    BarangJadi latestBarangJadi = barangJadiService.getBarangJadi(barangJadi.getIdBarang());
                     if (latestBarangJadi == null || newJumlah > latestBarangJadi.getStokJadi()) {
                        JOptionPane.showMessageDialog(this, "Stok tidak mencukupi!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    detail.setJumlahTerjual(newJumlah);
                    detail.hitungSubtotal(); // Recalculate subtotal
                    found = true;
                    break;
                }
            }

            if (!found) {
                // Add new item if it doesn't exist
                 BarangJadi latestBarangJadi = barangJadiService.getBarangJadi(barangJadi.getIdBarang());
                 if (latestBarangJadi == null || jumlah > latestBarangJadi.getStokJadi()) {
                    JOptionPane.showMessageDialog(this, "Stok tidak mencukupi!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                DetailTransaksi detail = new DetailTransaksi();
                detail.setBarangJadi(barangJadi);
                detail.setNamaBarangSaatTransaksi(barangJadi.getNamaBarang());
                detail.setJumlahTerjual(jumlah);
                detail.setHargaSatuanSaatTransaksi(barangJadi.getHargaJual());
                detailTransaksiList.add(detail);
            }

            updateDetailTable();
            jumlahField.setText("");
            // Keep the selected item in the combo box for faster entry of the same item
            // barangJadiComboBox.setSelectedIndex(0);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(this, "Error saat memeriksa stok: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDetailTable() {
        tableModel.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;
        for (DetailTransaksi detail : detailTransaksiList) {
            tableModel.addRow(new Object[] {
                    detail.getNamaBarangSaatTransaksi(),
                    detail.getHargaSatuanSaatTransaksi(),
                    detail.getJumlahTerjual(),
                    detail.getSubtotal(),
                    "Hapus" // Button text
            });
            total = total.add(detail.getSubtotal());
        }
        totalLabel.setText("Total: Rp " + String.format("%,.2f", total)); // Format total
    }

    private void simpanTransaksi() {
        if (detailTransaksiList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada item dalam transaksi.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Struk struk = new Struk();
        struk.setKasir(kasir);
        struk.setDetailTransaksiList(detailTransaksiList);

        try {
            if (transaksiService.simpanTransaksi(struk)) {
                JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan. Nomor Struk: " + struk.getNomorStruk(),
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                detailTransaksiList.clear();
                updateDetailTable();
                loadBarangJadiComboBox(); // Reload barang jadi list to reflect stock changes
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat menyimpan transaksi: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Custom Cell Renderer for the Button column
    class ButtonRenderer extends DefaultTableCellRenderer {
        private final JButton button;

        public ButtonRenderer() {
            button = new JButton("Hapus");
            button.setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(UIManager.getColor("Button.background"));
            }
            button.setText((value == null) ? "" : value.toString());
            return button;
        }
    }

    // Custom Cell Editor for the Button column
    class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;
        private int selectedRow;

        public ButtonEditor(JTextField textField) {
            super(textField);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Stop editing first
                    stopCellEditing();
                    // Perform the delete action here
                    if (selectedRow >= 0 && selectedRow < detailTransaksiList.size()) {
                        detailTransaksiList.remove(selectedRow);
                        updateDetailTable(); // Update the table display
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(UIManager.getColor("Button.background"));
            }
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            selectedRow = row; // Store the selected row index
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            // Return the label, the action is handled in actionPerformed
            return label;
        }

        // Override to prevent the default stopCellEditing behavior from firing
        // fireEditingStopped() immediately, as we call it manually in actionPerformed
        @Override
        public boolean stopCellEditing() {
             return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
