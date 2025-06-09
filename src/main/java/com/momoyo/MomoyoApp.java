package com.momoyo;

import com.momoyo.model.*;
import com.momoyo.service.*;
import com.momoyo.menu.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class MomoyoApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final PenggunaService penggunaService = new PenggunaService();
    private static final SatuanService satuanService = new SatuanService();
    private static final BahanBakuService bahanBakuService = new BahanBakuService();
    private static final BarangJadiService barangJadiService = new BarangJadiService();
    private static final ProduksiService produksiService = new ProduksiService();
    private static final TransaksiService transaksiService = new TransaksiService();

    private static final PenggunaMenu penggunaMenu = new PenggunaMenu(penggunaService);
    private static final SatuanMenu satuanMenu = new SatuanMenu(satuanService);
    private static final BahanBakuMenu bahanBakuMenu = new BahanBakuMenu(bahanBakuService, satuanService);
    private static final BarangJadiMenu barangJadiMenu = new BarangJadiMenu(barangJadiService);
    private static final ResepMenu resepMenu = new ResepMenu(barangJadiService, bahanBakuService, produksiService);
    private static final ProduksiMenu produksiMenu = new ProduksiMenu(produksiService, barangJadiService,
            bahanBakuService);
    private static TransaksiMenu transaksiMenu;

    private static Pengguna currentUser = null;

    public static void main(String[] args) {
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else if (currentUser.getPeran() == Pengguna.Role.ADMIN) {
                showAdminMenu();
            } else {
                if (transaksiMenu == null || transaksiMenu.getKasir() != currentUser) {
                    transaksiMenu = new TransaksiMenu(transaksiService, barangJadiService, currentUser);
                }
                showKasirMenu();
            }
        }
    }

    private static void showLoginMenu() {
        System.out.println("\n=== MOMOYO STORE ===\n");
        System.out.println("1. Login");
        System.out.println("0. Keluar");
        System.out.print("\nPilihan: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    doLogin();
                    break;
                case 0:
                    System.out.println("\nTerima kasih telah menggunakan aplikasi Momoyo Store.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("\nPilihan tidak valid!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInput harus berupa angka!");
        }
    }

    private static void showAdminMenu() {
        System.out.println("\n=== MENU ADMIN ===\n");
        System.out.println("1. Manajemen Pengguna");
        System.out.println("2. Manajemen Satuan");
        System.out.println("3. Manajemen Bahan Baku");
        System.out.println("4. Manajemen Barang Jadi");
        System.out.println("5. Manajemen Resep");
        System.out.println("6. Produksi Barang");
        System.out.println("7. Laporan Penjualan");
        System.out.println("8. Ganti Password");
        System.out.println("0. Logout");
        System.out.print("\nPilihan: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    penggunaMenu.showMenu();
                    break;
                case 2:
                    satuanMenu.showMenu();
                    break;
                case 3:
                    bahanBakuMenu.showMenu();
                    break;
                case 4:
                    barangJadiMenu.showMenu();
                    break;
                case 5:
                    resepMenu.showMenu();
                    break;
                case 6:
                    produksiMenu.showMenu();
                    break;
                case 7:
                    transaksiMenu.laporanPenjualan();
                    break;
                case 8:
                    changePassword();
                    break;
                case 0:
                    logout();
                    break;
                default:
                    System.out.println("\nPilihan tidak valid!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInput harus berupa angka!");
        }
    }

    private static void showKasirMenu() {
        System.out.println("\n=== MENU KASIR ===\n");
        System.out.println("1. Transaksi Baru");
        System.out.println("2. Cari Struk");
        System.out.println("3. Ganti Password");
        System.out.println("0. Logout");
        System.out.print("\nPilihan: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    transaksiMenu.transaksiBaru();
                    break;
                case 2:
                    transaksiMenu.cariStruk();
                    break;
                case 3:
                    changePassword();
                    break;
                case 0:
                    logout();
                    break;
                default:
                    System.out.println("\nPilihan tidak valid!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInput harus berupa angka!");
        }
    }

    private static void doLogin() {
        System.out.print("\nUsername: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            currentUser = penggunaService.login(username, password);
            if (currentUser != null) {
                System.out.println("\nLogin berhasil! Selamat datang, " + currentUser.getUsername());
                transaksiMenu = new TransaksiMenu(transaksiService, barangJadiService, currentUser);
            } else {
                System.out.println("\nUsername atau password salah!");
            }
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private static void logout() {
        currentUser = null;
        transaksiMenu = null;
        System.out.println("\nAnda telah logout.");
    }

    private static void changePassword() {
        System.out.print("\nPassword Lama: ");
        String oldPassword = scanner.nextLine();
        System.out.print("Password Baru: ");
        String newPassword = scanner.nextLine();
        System.out.print("Konfirmasi Password Baru: ");
        String confirmPassword = scanner.nextLine();

        try {
            if (!currentUser.verifikasiPassword(oldPassword)) {
                System.out.println("\nPassword lama salah!");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                System.out.println("\nKonfirmasi password baru tidak sesuai!");
                return;
            }

            currentUser.setPassword(newPassword);
            if (penggunaService.updatePengguna(currentUser)) {
                System.out.println("\nPassword berhasil diubah!");
            } else {
                System.out.println("\nGagal mengubah password!");
            }
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }
}
