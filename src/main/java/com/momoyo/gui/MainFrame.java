package com.momoyo.gui;

import com.momoyo.model.Pengguna;
import com.momoyo.service.BarangJadiService;
import com.momoyo.service.BahanBakuService;
import com.momoyo.service.PenggunaService;
import com.momoyo.service.ProduksiService;
import com.momoyo.service.SatuanService;
import com.momoyo.service.TransaksiService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class MainFrame extends JFrame {

    private final PenggunaService penggunaService;
    private final SatuanService satuanService;
    private final BahanBakuService bahanBakuService;
    private final BarangJadiService barangJadiService;
    private final ProduksiService produksiService;
    private final TransaksiService transaksiService;
    private Pengguna currentUser;

    private JPanel contentPane;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem logoutMenuItem;
    private JMenu adminMenu;
    private JMenuItem penggunaMenuItem;
    private JMenuItem satuanMenuItem;
    private JMenuItem bahanBakuMenuItem;
    private JMenuItem barangJadiMenuItem;
    private JMenuItem resepMenuItem;
    private JMenuItem produksiMenuItem;
    private JMenuItem laporanPenjualanMenuItem;
    private JMenuItem gantiPasswordMenuItem;
    private JMenu kasirMenu;
    private JMenuItem transaksiBaruMenuItem;
    private JMenuItem cariStrukMenuItem;
    private DashboardPanel dashboardPanel;

    // New components for the always-visible logout button
    private JPanel headerPanel;
    private JLabel loggedInUserLabel;
    private JButton logoutButton;

    public MainFrame() {
        this.penggunaService = new PenggunaService();
        this.satuanService = new SatuanService();
        this.bahanBakuService = new BahanBakuService();
        this.barangJadiService = new BarangJadiService();
        this.produksiService = new ProduksiService();
        this.transaksiService = new TransaksiService();
        this.currentUser = null;

        setTitle("Momoyo Store");
        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        createMenu();
        createHeaderPanel(); // Create the header panel
        showLoginDialog();
    }

    private void createMenu() {
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        logoutMenuItem = new JMenuItem("Logout");
        logoutMenuItem.addActionListener(this::logout);
        fileMenu.add(logoutMenuItem);
        menuBar.add(fileMenu);

        adminMenu = new JMenu("Admin");
        penggunaMenuItem = new JMenuItem("Manajemen Pengguna");
        penggunaMenuItem.addActionListener(e -> showPanel(new PenggunaPanel(penggunaService)));
        adminMenu.add(penggunaMenuItem);

        satuanMenuItem = new JMenuItem("Manajemen Satuan");
        satuanMenuItem.addActionListener(e -> showPanel(new SatuanPanel(satuanService)));
        adminMenu.add(satuanMenuItem);

        bahanBakuMenuItem = new JMenuItem("Manajemen Bahan Baku");
        bahanBakuMenuItem.addActionListener(e -> showPanel(new BahanBakuPanel(bahanBakuService, satuanService)));
        adminMenu.add(bahanBakuMenuItem);

        barangJadiMenuItem = new JMenuItem("Manajemen Barang Jadi");
        barangJadiMenuItem.addActionListener(e -> showPanel(new BarangJadiPanel(barangJadiService)));
        adminMenu.add(barangJadiMenuItem);

        resepMenuItem = new JMenuItem("Manajemen Resep");
        resepMenuItem.addActionListener(
                e -> showPanel(new ResepPanel(barangJadiService, bahanBakuService, produksiService)));
        adminMenu.add(resepMenuItem);

        produksiMenuItem = new JMenuItem("Produksi Barang");
        produksiMenuItem.addActionListener(
                e -> showPanel(new ProduksiPanel(produksiService, barangJadiService, bahanBakuService)));
        adminMenu.add(produksiMenuItem);

        laporanPenjualanMenuItem = new JMenuItem("Laporan Penjualan");
        laporanPenjualanMenuItem.addActionListener(e -> showPanel(new LaporanPenjualanPanel(transaksiService)));
        adminMenu.add(laporanPenjualanMenuItem);

        gantiPasswordMenuItem = new JMenuItem("Ganti Password");
        gantiPasswordMenuItem.addActionListener(this::showChangePasswordDialog);
        adminMenu.add(gantiPasswordMenuItem);
        menuBar.add(adminMenu);

        kasirMenu = new JMenu("Kasir");
        transaksiBaruMenuItem = new JMenuItem("Transaksi Baru");
        transaksiBaruMenuItem.addActionListener(
                e -> showPanel(new TransaksiPanel(transaksiService, barangJadiService, currentUser)));
        kasirMenu.add(transaksiBaruMenuItem);

        cariStrukMenuItem = new JMenuItem("Cari Struk");
        cariStrukMenuItem.addActionListener(e -> showPanel(new CariStrukPanel(transaksiService)));
        kasirMenu.add(cariStrukMenuItem);
        // Add Ganti Password to Kasir menu as well
        JMenuItem kasirGantiPasswordMenuItem = new JMenuItem("Ganti Password");
        kasirGantiPasswordMenuItem.addActionListener(this::showChangePasswordDialog);
        kasirMenu.add(kasirGantiPasswordMenuItem);

        menuBar.add(kasirMenu);

        setJMenuBar(menuBar);
    }

    private void createHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add padding

        loggedInUserLabel = new JLabel("Logged in as: ");
        loggedInUserLabel.setFont(loggedInUserLabel.getFont().deriveFont(Font.BOLD));
        headerPanel.add(loggedInUserLabel, BorderLayout.WEST);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(this::logout);
        JPanel logoutButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Panel to right-align button
        logoutButtonPanel.add(logoutButton);
        headerPanel.add(logoutButtonPanel, BorderLayout.EAST);

        // Initially hide the header panel
        headerPanel.setVisible(false);
        contentPane.add(headerPanel, BorderLayout.NORTH);
    }

    private void showLoginDialog() {
        LoginDialog loginDialog = new LoginDialog(this, penggunaService);
        loginDialog.setVisible(true);
        loginDialog.setLocationRelativeTo(this);
        this.currentUser = loginDialog.getAuthenticatedUser();
        updateMenuForUser();
    }

    private void updateMenuForUser() {
        if (currentUser == null) {
            adminMenu.setVisible(false);
            kasirMenu.setVisible(false);
            fileMenu.setVisible(true); // Keep File menu visible for Logout (though button is primary)
            logoutMenuItem.setVisible(false); // Hide menu item if button is present
            headerPanel.setVisible(false); // Hide header panel when logged out
            showPanel(new DashboardPanel()); // Show dashboard or initial panel
        } else {
            loggedInUserLabel.setText("Logged in as: " + currentUser.getUsername() + " (" + currentUser.getPeran() + ")");
            headerPanel.setVisible(true); // Show header panel when logged in
            logoutMenuItem.setVisible(true); // Keep menu item visible if desired, or hide if button is primary

            if (currentUser.getPeran() == Pengguna.Role.ADMIN) {
                adminMenu.setVisible(true);
                kasirMenu.setVisible(false);
            } else { // Kasir
                adminMenu.setVisible(false);
                kasirMenu.setVisible(true);
            }
            fileMenu.setVisible(true); // Keep File menu visible
            showPanel(new DashboardPanel()); // Show dashboard after login
        }
    }

    private void logout(ActionEvent e) {
        currentUser = null;
        updateMenuForUser();
        showLoginDialog(); // Show login dialog again after logout
    }

    private void showChangePasswordDialog(ActionEvent e) {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Silakan login terlebih dahulu.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(this, penggunaService, currentUser);
        changePasswordDialog.setVisible(true);
        changePasswordDialog.setLocationRelativeTo(this);
    }

    private void showPanel(JPanel panel) {
        // Remove existing panel from the center
        Component[] components = contentPane.getComponents();
        for (Component comp : components) {
            if (contentPane.getLayout() instanceof BorderLayout) {
                BorderLayout layout = (BorderLayout) contentPane.getLayout();
                if (BorderLayout.CENTER.equals(layout.getConstraints(comp))) {
                    contentPane.remove(comp);
                    break; // Assuming only one component in the center
                }
            }
        }

        // Add the new panel to the center
        contentPane.add(panel, BorderLayout.CENTER);
        contentPane.revalidate();
        contentPane.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                    | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            MainFrame mainFrame = new MainFrame();
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(1000, 600);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
        });
    }
}
