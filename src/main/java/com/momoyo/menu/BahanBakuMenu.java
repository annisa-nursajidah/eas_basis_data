package com.momoyo.menu;

import com.momoyo.model.BahanBaku;
import com.momoyo.model.Satuan;
import com.momoyo.service.BahanBakuService;
import com.momoyo.service.SatuanService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class BahanBakuMenu {
    private static final Scanner scanner = new Scanner(System.in);
    private final BahanBakuService bahanBakuService;
    private final SatuanService satuanService;

    public BahanBakuMenu(BahanBakuService bahanBakuService, SatuanService satuanService) {
        this.bahanBakuService = bahanBakuService;
        this.satuanService = satuanService;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== MANAJEMEN BAHAN BAKU ===\n");
            System.out.println("1. Tambah Bahan Baku");
            System.out.println("2. Lihat Daftar Bahan Baku");
            System.out.println("3. Ubah Bahan Baku");
            System.out.println("4. Hapus Bahan Baku");
            System.out.println("5. Tambah Stok");
            System.out.println("6. Kurangi Stok");
            System.out.println("7. Lihat Stok Kritis");
            System.out.println("0. Kembali");
            System.out.print("\nPilihan: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        tambahBahanBaku();
                        break;
                    case 2:
                        lihatDaftarBahanBaku();
                        break;
                    case 3:
                        ubahBahanBaku();
                        break;
                    case 4:
                        hapusBahanBaku();
                        break;
                    case 5:
                        tambahStok();
                        break;
                    case 6:
                        kurangiStok();
                        break;
                    case 7:
                        lihatStokKritis();
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

    private void tambahBahanBaku() {
        System.out.println("\n=== TAMBAH BAHAN BAKU ===\n");

        try {
            List<Satuan> daftarSatuan = satuanService.getAllSatuan();
            if (daftarSatuan.isEmpty()) {
                System.out.println("Belum ada data satuan. Silakan tambah satuan terlebih dahulu.");
                return;
            }

            System.out.println("Daftar Satuan:");
            System.out.printf("%-5s %-20s%n", "No.", "Nama Satuan");
            System.out.println("-".repeat(25));

            int no = 1;
            for (Satuan satuan : daftarSatuan) {
                System.out.printf("%-5d %-20s%n",
                        no++,
                        satuan.getNamaSatuan());
            }

            System.out.print("\nPilih nomor satuan: ");
            int pilihanSatuan = Integer.parseInt(scanner.nextLine());

            if (pilihanSatuan < 1 || pilihanSatuan > daftarSatuan.size()) {
                System.out.println("\nNomor satuan tidak valid!");
                return;
            }

            Satuan satuan = daftarSatuan.get(pilihanSatuan - 1);

            System.out.print("Nama Bahan: ");
            String namaBahan = scanner.nextLine();

            System.out.print("Stok Awal: ");
            BigDecimal stokAwal = new BigDecimal(scanner.nextLine());

            System.out.print("Batas Minimum Stok: ");
            BigDecimal batasMinimum = new BigDecimal(scanner.nextLine());

            BahanBaku bahanBaku = new BahanBaku();
            bahanBaku.setNamaBahan(namaBahan);
            bahanBaku.setSatuan(satuan);
            bahanBaku.setStokTersedia(stokAwal);
            bahanBaku.setAmbangBatas(batasMinimum);

            if (bahanBakuService.tambahBahanBaku(bahanBaku)) {
                System.out.println("\nBahan baku berhasil ditambahkan!");
            } else {
                System.out.println("\nGagal menambahkan bahan baku!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInput numerik tidak valid!");
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void lihatDaftarBahanBaku() {
        System.out.println("\n=== DAFTAR BAHAN BAKU ===\n");

        try {
            List<BahanBaku> daftarBahanBaku = bahanBakuService.getAllBahanBaku();
            if (daftarBahanBaku.isEmpty()) {
                System.out.println("Belum ada data bahan baku.");
                return;
            }

            System.out.printf("%-5s %-20s %-15s %-15s %-15s%n",
                    "No.", "Nama Bahan", "Stok", "Batas Min.", "Satuan");
            System.out.println("-".repeat(70));

            int no = 1;
            for (BahanBaku bahanBaku : daftarBahanBaku) {
                System.out.printf("%-5d %-20s %-15.2f %-15.2f %-15s%n",
                        no++,
                        bahanBaku.getNamaBahan(),
                        bahanBaku.getStokTersedia(),
                        bahanBaku.getAmbangBatas(),
                        bahanBaku.getSatuan().getNamaSatuan());
            }
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void ubahBahanBaku() {
        System.out.println("\n=== UBAH BAHAN BAKU ===\n");

        try {
            List<BahanBaku> daftarBahanBaku = bahanBakuService.getAllBahanBaku();
            if (daftarBahanBaku.isEmpty()) {
                System.out.println("Belum ada data bahan baku.");
                return;
            }

            System.out.printf("%-5s %-20s %-15s %-15s %-15s%n",
                    "No.", "Nama Bahan", "Stok", "Batas Min.", "Satuan");
            System.out.println("-".repeat(70));

            int no = 1;
            for (BahanBaku bahanBaku : daftarBahanBaku) {
                System.out.printf("%-5d %-20s %-15.2f %-15.2f %-15s%n",
                        no++,
                        bahanBaku.getNamaBahan(),
                        bahanBaku.getStokTersedia(),
                        bahanBaku.getAmbangBatas(),
                        bahanBaku.getSatuan().getNamaSatuan());
            }

            System.out.print("\nPilih nomor bahan baku yang akan diubah (0 untuk batal): ");
            int pilihan = Integer.parseInt(scanner.nextLine());

            if (pilihan == 0)
                return;
            if (pilihan < 1 || pilihan > daftarBahanBaku.size()) {
                System.out.println("\nNomor bahan baku tidak valid!");
                return;
            }

            BahanBaku bahanBaku = daftarBahanBaku.get(pilihan - 1);

            List<Satuan> daftarSatuan = satuanService.getAllSatuan();
            System.out.println("\nDaftar Satuan:");
            System.out.printf("%-5s %-20s%n", "No.", "Nama Satuan");
            System.out.println("-".repeat(25));

            no = 1;
            for (Satuan satuan : daftarSatuan) {
                System.out.printf("%-5d %-20s%n",
                        no++,
                        satuan.getNamaSatuan());
            }

            System.out.print("\nPilih nomor satuan (0 untuk tidak mengubah): ");
            String pilihanSatuanStr = scanner.nextLine();
            if (!pilihanSatuanStr.isEmpty()) {
                int pilihanSatuan = Integer.parseInt(pilihanSatuanStr);
                if (pilihanSatuan != 0) {
                    if (pilihanSatuan < 1 || pilihanSatuan > daftarSatuan.size()) {
                        System.out.println("\nNomor satuan tidak valid!");
                        return;
                    }
                    bahanBaku.setSatuan(daftarSatuan.get(pilihanSatuan - 1));
                }
            }

            System.out.print("Nama bahan baru (kosongkan jika tidak diubah): ");
            String namaBahan = scanner.nextLine();
            if (!namaBahan.isEmpty()) {
                bahanBaku.setNamaBahan(namaBahan);
            }

            System.out.print("Batas minimum stok baru (kosongkan jika tidak diubah): ");
            String batasMinimumStr = scanner.nextLine();
            if (!batasMinimumStr.isEmpty()) {
                bahanBaku.setAmbangBatas(new BigDecimal(batasMinimumStr));
            }

            if (bahanBakuService.updateBahanBaku(bahanBaku)) {
                System.out.println("\nBahan baku berhasil diubah!");
            } else {
                System.out.println("\nGagal mengubah bahan baku!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInput numerik tidak valid!");
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void hapusBahanBaku() {
        System.out.println("\n=== HAPUS BAHAN BAKU ===\n");

        try {
            List<BahanBaku> daftarBahanBaku = bahanBakuService.getAllBahanBaku();
            if (daftarBahanBaku.isEmpty()) {
                System.out.println("Belum ada data bahan baku.");
                return;
            }

            System.out.printf("%-5s %-20s %-15s %-15s %-15s%n",
                    "No.", "Nama Bahan", "Stok", "Batas Min.", "Satuan");
            System.out.println("-".repeat(70));

            int no = 1;
            for (BahanBaku bahanBaku : daftarBahanBaku) {
                System.out.printf("%-5d %-20s %-15.2f %-15.2f %-15s%n",
                        no++,
                        bahanBaku.getNamaBahan(),
                        bahanBaku.getStokTersedia(),
                        bahanBaku.getAmbangBatas(),
                        bahanBaku.getSatuan().getNamaSatuan());
            }

            System.out.print("\nPilih nomor bahan baku yang akan dihapus (0 untuk batal): ");
            int pilihan = Integer.parseInt(scanner.nextLine());

            if (pilihan == 0)
                return;
            if (pilihan < 1 || pilihan > daftarBahanBaku.size()) {
                System.out.println("\nNomor bahan baku tidak valid!");
                return;
            }

            BahanBaku bahanBaku = daftarBahanBaku.get(pilihan - 1);

            System.out.print("\nAnda yakin ingin menghapus bahan baku ini? (y/n): ");
            String konfirmasi = scanner.nextLine();

            if (konfirmasi.equalsIgnoreCase("y")) {
                if (bahanBakuService.hapusBahanBaku(bahanBaku.getIdBahan())) {
                    System.out.println("\nBahan baku berhasil dihapus!");
                } else {
                    System.out.println("\nGagal menghapus bahan baku!");
                }
            } else {
                System.out.println("\nPenghapusan dibatalkan.");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInput harus berupa angka!");
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void tambahStok() {
        System.out.println("\n=== TAMBAH STOK BAHAN BAKU ===\n");

        try {
            List<BahanBaku> daftarBahanBaku = bahanBakuService.getAllBahanBaku();
            if (daftarBahanBaku.isEmpty()) {
                System.out.println("Belum ada data bahan baku.");
                return;
            }

            System.out.printf("%-5s %-20s %-15s %-15s %-15s%n",
                    "No.", "Nama Bahan", "Stok", "Batas Min.", "Satuan");
            System.out.println("-".repeat(70));

            int no = 1;
            for (BahanBaku bahanBaku : daftarBahanBaku) {
                System.out.printf("%-5d %-20s %-15.2f %-15.2f %-15s%n",
                        no++,
                        bahanBaku.getNamaBahan(),
                        bahanBaku.getStokTersedia(),
                        bahanBaku.getAmbangBatas(),
                        bahanBaku.getSatuan().getNamaSatuan());
            }

            System.out.print("\nPilih nomor bahan baku (0 untuk batal): ");
            int pilihan = Integer.parseInt(scanner.nextLine());

            if (pilihan == 0)
                return;
            if (pilihan < 1 || pilihan > daftarBahanBaku.size()) {
                System.out.println("\nNomor bahan baku tidak valid!");
                return;
            }

            BahanBaku bahanBaku = daftarBahanBaku.get(pilihan - 1);

            System.out.printf("Jumlah penambahan (dalam %s): ", bahanBaku.getSatuan().getNamaSatuan());
            BigDecimal jumlah = new BigDecimal(scanner.nextLine());

            if (bahanBakuService.tambahStok(bahanBaku.getIdBahan(), jumlah)) {
                System.out.println("\nStok berhasil ditambahkan!");
            } else {
                System.out.println("\nGagal menambahkan stok!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInput numerik tidak valid!");
        } catch (IllegalArgumentException e) {
            System.out.println("\n" + e.getMessage());
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void kurangiStok() {
        System.out.println("\n=== KURANGI STOK BAHAN BAKU ===\n");

        try {
            List<BahanBaku> daftarBahanBaku = bahanBakuService.getAllBahanBaku();
            if (daftarBahanBaku.isEmpty()) {
                System.out.println("Belum ada data bahan baku.");
                return;
            }

            System.out.printf("%-5s %-20s %-15s %-15s %-15s%n",
                    "No.", "Nama Bahan", "Stok", "Batas Min.", "Satuan");
            System.out.println("-".repeat(70));

            int no = 1;
            for (BahanBaku bahanBaku : daftarBahanBaku) {
                System.out.printf("%-5d %-20s %-15.2f %-15.2f %-15s%n",
                        no++,
                        bahanBaku.getNamaBahan(),
                        bahanBaku.getStokTersedia(),
                        bahanBaku.getAmbangBatas(),
                        bahanBaku.getSatuan().getNamaSatuan());
            }

            System.out.print("\nPilih nomor bahan baku (0 untuk batal): ");
            int pilihan = Integer.parseInt(scanner.nextLine());

            if (pilihan == 0)
                return;
            if (pilihan < 1 || pilihan > daftarBahanBaku.size()) {
                System.out.println("\nNomor bahan baku tidak valid!");
                return;
            }

            BahanBaku bahanBaku = daftarBahanBaku.get(pilihan - 1);

            System.out.printf("Jumlah pengurangan (dalam %s): ", bahanBaku.getSatuan().getNamaSatuan());
            BigDecimal jumlah = new BigDecimal(scanner.nextLine());

            if (bahanBakuService.kurangiStok(bahanBaku.getIdBahan(), jumlah)) {
                System.out.println("\nStok berhasil dikurangi!");
            } else {
                System.out.println("\nGagal mengurangi stok!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInput numerik tidak valid!");
        } catch (IllegalArgumentException e) {
            System.out.println("\n" + e.getMessage());
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void lihatStokKritis() {
        System.out.println("\n=== DAFTAR BAHAN BAKU STOK KRITIS ===\n");

        try {
            List<BahanBaku> daftarBahanBaku = bahanBakuService.getBahanBakuStokKritis();
            if (daftarBahanBaku.isEmpty()) {
                System.out.println("Tidak ada bahan baku dengan stok kritis.");
                return;
            }

            System.out.printf("%-5s %-20s %-15s %-15s %-15s%n",
                    "No.", "Nama Bahan", "Stok", "Batas Min.", "Satuan");
            System.out.println("-".repeat(70));

            int no = 1;
            for (BahanBaku bahanBaku : daftarBahanBaku) {
                System.out.printf("%-5d %-20s %-15.2f %-15.2f %-15s%n",
                        no++,
                        bahanBaku.getNamaBahan(),
                        bahanBaku.getStokTersedia(),
                        bahanBaku.getAmbangBatas(),
                        bahanBaku.getSatuan().getNamaSatuan());
            }
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }
}
