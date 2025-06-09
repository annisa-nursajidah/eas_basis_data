package com.momoyo.menu;

import com.momoyo.model.*;
import com.momoyo.service.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ProduksiMenu {
    private static final Scanner scanner = new Scanner(System.in);
    private final ProduksiService produksiService;
    private final BarangJadiService barangJadiService;
    private final BahanBakuService bahanBakuService;

    public ProduksiMenu(ProduksiService produksiService, BarangJadiService barangJadiService,
            BahanBakuService bahanBakuService) {
        this.produksiService = produksiService;
        this.barangJadiService = barangJadiService;
        this.bahanBakuService = bahanBakuService;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== MENU PRODUKSI ===\n");
            System.out.println("1. Produksi Barang");
            System.out.println("2. Lihat Stok Bahan Baku Kritis");
            System.out.println("3. Lihat Kebutuhan Bahan Baku");
            System.out.println("0. Kembali");
            System.out.print("\nPilihan: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        produksiBarang();
                        break;
                    case 2:
                        lihatStokKritis();
                        break;
                    case 3:
                        lihatKebutuhanBahan();
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("\nPilihan tidak valid!");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInput harus berupa angka!");
            }
        }
    }

    private void produksiBarang() {
        System.out.println("\n=== PRODUKSI BARANG ===\n");

        try {
            List<BarangJadi> daftarBarangJadi = barangJadiService.getBarangJadiAktif();
            if (daftarBarangJadi.isEmpty()) {
                System.out.println("Tidak ada barang jadi yang aktif.");
                return;
            }

            System.out.println("Pilih barang yang akan diproduksi:");
            System.out.printf("%-5s %-25s %-10s%n", "No.", "Nama Barang", "Stok");
            System.out.println("-".repeat(40));

            int no = 1;
            for (BarangJadi barangJadi : daftarBarangJadi) {
                System.out.printf("%-5d %-25s %-10d%n",
                        no++,
                        barangJadi.getNamaBarang(),
                        barangJadi.getStokJadi());
            }

            System.out.print("\nPilihan (0 untuk batal): ");
            int pilihan = Integer.parseInt(scanner.nextLine());

            if (pilihan == 0)
                return;
            if (pilihan < 1 || pilihan > daftarBarangJadi.size()) {
                System.out.println("\nPilihan tidak valid!");
                return;
            }

            BarangJadi barangJadi = daftarBarangJadi.get(pilihan - 1);

            Resep resep = produksiService.getResepByBarangJadi(barangJadi.getIdBarang());
            if (resep == null) {
                System.out.println("\nBarang ini belum memiliki resep!");
                return;
            }

            System.out.print("Jumlah yang akan diproduksi: ");
            int jumlah = Integer.parseInt(scanner.nextLine());

            if (jumlah <= 0) {
                System.out.println("\nJumlah produksi harus positif!");
                return;
            }

            System.out.println("\nKebutuhan Bahan:");
            System.out.printf("%-25s %-15s %-15s %-15s%n",
                    "Nama Bahan", "Dibutuhkan", "Tersedia", "Satuan");
            System.out.println("-".repeat(70));

            boolean stokCukup = true;
            for (DetailResep detail : resep.getDetailResepList()) {
                BahanBaku bahan = detail.getBahanBaku();
                BigDecimal kebutuhan = detail.hitungKebutuhanBahan(jumlah);
                System.out.printf("%-25s %-15.2f %-15.2f %-15s%n",
                        bahan.getNamaBahan(),
                        kebutuhan,
                        bahan.getStokTersedia(),
                        bahan.getSatuan().getNamaSatuan());

                if (bahan.getStokTersedia().compareTo(kebutuhan) < 0) {
                    stokCukup = false;
                }
            }

            if (!stokCukup) {
                System.out.println("\nStok bahan baku tidak mencukupi!");
                return;
            }

            System.out.print("\nLanjutkan produksi? (y/n): ");
            String konfirmasi = scanner.nextLine();

            if (konfirmasi.equalsIgnoreCase("y")) {
                if (produksiService.produksiBarang(resep, jumlah)) {
                    System.out.println("\nProduksi berhasil!");
                } else {
                    System.out.println("\nGagal melakukan produksi!");
                }
            } else {
                System.out.println("\nProduksi dibatalkan.");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInput numerik tidak valid!");
        } catch (IllegalArgumentException e) {
            System.out.println("\n" + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("\n" + e.getMessage());
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void lihatStokKritis() {
        System.out.println("\n=== STOK BAHAN BAKU KRITIS ===\n");

        try {
            List<BahanBaku> daftarBahanKritis = bahanBakuService.getBahanBakuStokKritis();
            if (daftarBahanKritis.isEmpty()) {
                System.out.println("Tidak ada bahan baku dengan stok kritis.");
                return;
            }

            System.out.printf("%-5s %-25s %-15s %-15s %-15s%n",
                    "No.", "Nama Bahan", "Stok", "Batas Min.", "Satuan");
            System.out.println("-".repeat(75));

            int no = 1;
            for (BahanBaku bahan : daftarBahanKritis) {
                System.out.printf("%-5d %-25s %-15.2f %-15.2f %-15s%n",
                        no++,
                        bahan.getNamaBahan(),
                        bahan.getStokTersedia(),
                        bahan.getAmbangBatas(),
                        bahan.getSatuan().getNamaSatuan());
            }
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void lihatKebutuhanBahan() {
        System.out.println("\n=== KEBUTUHAN BAHAN BAKU ===\n");

        try {
            List<BarangJadi> daftarBarangJadi = barangJadiService.getBarangJadiAktif();
            if (daftarBarangJadi.isEmpty()) {
                System.out.println("Tidak ada barang jadi yang aktif.");
                return;
            }

            System.out.println("Pilih barang:");
            System.out.printf("%-5s %-25s%n", "No.", "Nama Barang");
            System.out.println("-".repeat(30));

            int no = 1;
            for (BarangJadi barangJadi : daftarBarangJadi) {
                System.out.printf("%-5d %-25s%n",
                        no++,
                        barangJadi.getNamaBarang());
            }

            System.out.print("\nPilihan (0 untuk batal): ");
            int pilihan = Integer.parseInt(scanner.nextLine());

            if (pilihan == 0)
                return;
            if (pilihan < 1 || pilihan > daftarBarangJadi.size()) {
                System.out.println("\nPilihan tidak valid!");
                return;
            }

            BarangJadi barangJadi = daftarBarangJadi.get(pilihan - 1);

            Resep resep = produksiService.getResepByBarangJadi(barangJadi.getIdBarang());
            if (resep == null) {
                System.out.println("\nBarang ini belum memiliki resep!");
                return;
            }

            System.out.print("Jumlah yang akan diproduksi: ");
            int jumlah = Integer.parseInt(scanner.nextLine());

            if (jumlah <= 0) {
                System.out.println("\nJumlah produksi harus positif!");
                return;
            }

            System.out.println("\nKebutuhan Bahan:");
            System.out.printf("%-25s %-15s %-15s %-15s%n",
                    "Nama Bahan", "Dibutuhkan", "Tersedia", "Satuan");
            System.out.println("-".repeat(70));

            for (DetailResep detail : resep.getDetailResepList()) {
                BahanBaku bahan = detail.getBahanBaku();
                BigDecimal kebutuhan = detail.hitungKebutuhanBahan(jumlah);
                System.out.printf("%-25s %-15.2f %-15.2f %-15s%n",
                        bahan.getNamaBahan(),
                        kebutuhan,
                        bahan.getStokTersedia(),
                        bahan.getSatuan().getNamaSatuan());
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInput numerik tidak valid!");
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }
}
