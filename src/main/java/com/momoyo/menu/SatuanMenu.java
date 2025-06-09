package com.momoyo.menu;

import com.momoyo.model.Satuan;
import com.momoyo.service.SatuanService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class SatuanMenu {
    private static final Scanner scanner = new Scanner(System.in);
    private final SatuanService satuanService;

    public SatuanMenu(SatuanService satuanService) {
        this.satuanService = satuanService;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== MANAJEMEN SATUAN ===\n");
            System.out.println("1. Tambah Satuan");
            System.out.println("2. Lihat Daftar Satuan");
            System.out.println("3. Ubah Satuan");
            System.out.println("4. Hapus Satuan");
            System.out.println("0. Kembali");
            System.out.print("\nPilihan: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        tambahSatuan();
                        break;
                    case 2:
                        lihatDaftarSatuan();
                        break;
                    case 3:
                        ubahSatuan();
                        break;
                    case 4:
                        hapusSatuan();
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

    private void tambahSatuan() {
        System.out.println("\n=== TAMBAH SATUAN ===\n");

        System.out.print("Nama Satuan: ");
        String namaSatuan = scanner.nextLine();

        try {
            Satuan satuan = new Satuan();
            satuan.setNamaSatuan(namaSatuan);

            if (satuanService.tambahSatuan(satuan)) {
                System.out.println("\nSatuan berhasil ditambahkan!");
            } else {
                System.out.println("\nGagal menambahkan satuan!");
            }
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void lihatDaftarSatuan() {
        System.out.println("\n=== DAFTAR SATUAN ===\n");

        try {
            List<Satuan> daftarSatuan = satuanService.getAllSatuan();
            if (daftarSatuan.isEmpty()) {
                System.out.println("Belum ada data satuan.");
                return;
            }

            System.out.printf("%-5s %-20s%n", "No.", "Nama Satuan");
            System.out.println("-".repeat(25));

            int no = 1;
            for (Satuan satuan : daftarSatuan) {
                System.out.printf("%-5d %-20s%n",
                        no++,
                        satuan.getNamaSatuan());
            }
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void ubahSatuan() {
        System.out.println("\n=== UBAH SATUAN ===\n");

        try {
            List<Satuan> daftarSatuan = satuanService.getAllSatuan();
            if (daftarSatuan.isEmpty()) {
                System.out.println("Belum ada data satuan.");
                return;
            }

            System.out.printf("%-5s %-20s%n", "No.", "Nama Satuan");
            System.out.println("-".repeat(25));

            int no = 1;
            for (Satuan satuan : daftarSatuan) {
                System.out.printf("%-5d %-20s%n",
                        no++,
                        satuan.getNamaSatuan());
            }

            System.out.print("\nPilih nomor satuan yang akan diubah (0 untuk batal): ");
            int pilihan = Integer.parseInt(scanner.nextLine());

            if (pilihan == 0)
                return;
            if (pilihan < 1 || pilihan > daftarSatuan.size()) {
                System.out.println("\nNomor satuan tidak valid!");
                return;
            }

            Satuan satuan = daftarSatuan.get(pilihan - 1);

            System.out.print("Nama satuan baru: ");
            String namaSatuan = scanner.nextLine();
            satuan.setNamaSatuan(namaSatuan);

            if (satuanService.updateSatuan(satuan)) {
                System.out.println("\nSatuan berhasil diubah!");
            } else {
                System.out.println("\nGagal mengubah satuan!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInput harus berupa angka!");
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void hapusSatuan() {
        System.out.println("\n=== HAPUS SATUAN ===\n");

        try {
            List<Satuan> daftarSatuan = satuanService.getAllSatuan();
            if (daftarSatuan.isEmpty()) {
                System.out.println("Belum ada data satuan.");
                return;
            }

            System.out.printf("%-5s %-20s%n", "No.", "Nama Satuan");
            System.out.println("-".repeat(25));

            int no = 1;
            for (Satuan satuan : daftarSatuan) {
                System.out.printf("%-5d %-20s%n",
                        no++,
                        satuan.getNamaSatuan());
            }

            System.out.print("\nPilih nomor satuan yang akan dihapus (0 untuk batal): ");
            int pilihan = Integer.parseInt(scanner.nextLine());

            if (pilihan == 0)
                return;
            if (pilihan < 1 || pilihan > daftarSatuan.size()) {
                System.out.println("\nNomor satuan tidak valid!");
                return;
            }

            Satuan satuan = daftarSatuan.get(pilihan - 1);

            System.out.print("\nAnda yakin ingin menghapus satuan ini? (y/n): ");
            String konfirmasi = scanner.nextLine();

            if (konfirmasi.equalsIgnoreCase("y")) {
                try {
                    if (satuanService.hapusSatuan(satuan.getIdSatuan())) {
                        System.out.println("\nSatuan berhasil dihapus!");
                    } else {
                        System.out.println("\nGagal menghapus satuan!");
                    }
                } catch (IllegalStateException e) {
                    System.out.println("\n" + e.getMessage());
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
}
