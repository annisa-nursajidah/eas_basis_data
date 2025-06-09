package com.momoyo.menu;

import com.momoyo.model.BarangJadi;
import com.momoyo.service.BarangJadiService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class BarangJadiMenu {
    private static final Scanner scanner = new Scanner(System.in);
    private final BarangJadiService barangJadiService;

    public BarangJadiMenu(BarangJadiService barangJadiService) {
        this.barangJadiService = barangJadiService;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== MANAJEMEN BARANG JADI ===\n");
            System.out.println("1. Tambah Barang Jadi");
            System.out.println("2. Lihat Daftar Barang Jadi");
            System.out.println("3. Ubah Barang Jadi");
            System.out.println("4. Nonaktifkan Barang");
            System.out.println("5. Tambah Stok Manual");
            System.out.println("6. Kurangi Stok Manual");
            System.out.println("0. Kembali");
            System.out.print("\nPilihan: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        tambahBarangJadi();
                        break;
                    case 2:
                        lihatDaftarBarangJadi();
                        break;
                    case 3:
                        ubahBarangJadi();
                        break;
                    case 4:
                        nonaktifkanBarang();
                        break;
                    case 5:
                        tambahStok();
                        break;
                    case 6:
                        kurangiStok();
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

    private void tambahBarangJadi() {
        System.out.println("\n=== TAMBAH BARANG JADI ===\n");

        try {
            System.out.print("Nama Barang: ");
            String namaBarang = scanner.nextLine();

            System.out.print("Harga Jual: ");
            BigDecimal hargaJual = new BigDecimal(scanner.nextLine());

            System.out.print("Stok Awal: ");
            int stokAwal = Integer.parseInt(scanner.nextLine());

            BarangJadi barangJadi = new BarangJadi();
            barangJadi.setNamaBarang(namaBarang);
            barangJadi.setHargaJual(hargaJual);
            barangJadi.setStokJadi(stokAwal);

            if (barangJadiService.tambahBarangJadi(barangJadi)) {
                System.out.println("\nBarang jadi berhasil ditambahkan!");
            } else {
                System.out.println("\nGagal menambahkan barang jadi!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInput numerik tidak valid!");
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void lihatDaftarBarangJadi() {
        System.out.println("\n=== DAFTAR BARANG JADI ===\n");

        try {
            List<BarangJadi> daftarBarangJadi = barangJadiService.getAllBarangJadi();
            if (daftarBarangJadi.isEmpty()) {
                System.out.println("Belum ada data barang jadi.");
                return;
            }

            System.out.printf("%-5s %-25s %-15s %-10s %-10s%n",
                    "No.", "Nama Barang", "Harga Jual", "Stok", "Status");
            System.out.println("-".repeat(70));

            int no = 1;
            for (BarangJadi barangJadi : daftarBarangJadi) {
                System.out.printf("%-5d %-25s %-15.2f %-10d %-10s%n",
                        no++,
                        barangJadi.getNamaBarang(),
                        barangJadi.getHargaJual(),
                        barangJadi.getStokJadi(),
                        barangJadi.isActive() ? "Aktif" : "Nonaktif");
            }
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void ubahBarangJadi() {
        System.out.println("\n=== UBAH BARANG JADI ===\n");

        try {
            List<BarangJadi> daftarBarangJadi = barangJadiService.getAllBarangJadi();
            if (daftarBarangJadi.isEmpty()) {
                System.out.println("Belum ada data barang jadi.");
                return;
            }

            System.out.printf("%-5s %-25s %-15s %-10s %-10s%n",
                    "No.", "Nama Barang", "Harga Jual", "Stok", "Status");
            System.out.println("-".repeat(70));

            int no = 1;
            for (BarangJadi barangJadi : daftarBarangJadi) {
                System.out.printf("%-5d %-25s %-15.2f %-10d %-10s%n",
                        no++,
                        barangJadi.getNamaBarang(),
                        barangJadi.getHargaJual(),
                        barangJadi.getStokJadi(),
                        barangJadi.isActive() ? "Aktif" : "Nonaktif");
            }

            System.out.print("\nPilih nomor barang yang akan diubah (0 untuk batal): ");
            int pilihan = Integer.parseInt(scanner.nextLine());

            if (pilihan == 0)
                return;
            if (pilihan < 1 || pilihan > daftarBarangJadi.size()) {
                System.out.println("\nNomor barang tidak valid!");
                return;
            }

            BarangJadi barangJadi = daftarBarangJadi.get(pilihan - 1);

            System.out.print("Nama barang baru (kosongkan jika tidak diubah): ");
            String namaBarang = scanner.nextLine();
            if (!namaBarang.isEmpty()) {
                barangJadi.setNamaBarang(namaBarang);
            }

            System.out.print("Harga jual baru (kosongkan jika tidak diubah): ");
            String hargaJualStr = scanner.nextLine();
            if (!hargaJualStr.isEmpty()) {
                barangJadi.setHargaJual(new BigDecimal(hargaJualStr));
            }

            if (barangJadiService.updateBarangJadi(barangJadi)) {
                System.out.println("\nBarang jadi berhasil diubah!");
            } else {
                System.out.println("\nGagal mengubah barang jadi!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInput numerik tidak valid!");
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void nonaktifkanBarang() {
        System.out.println("\n=== NONAKTIFKAN BARANG JADI ===\n");

        try {
            List<BarangJadi> daftarBarangJadi = barangJadiService.getBarangJadiAktif();
            if (daftarBarangJadi.isEmpty()) {
                System.out.println("Tidak ada barang jadi yang aktif.");
                return;
            }

            System.out.printf("%-5s %-25s %-15s %-10s%n",
                    "No.", "Nama Barang", "Harga Jual", "Stok");
            System.out.println("-".repeat(60));

            int no = 1;
            for (BarangJadi barangJadi : daftarBarangJadi) {
                System.out.printf("%-5d %-25s %-15.2f %-10d%n",
                        no++,
                        barangJadi.getNamaBarang(),
                        barangJadi.getHargaJual(),
                        barangJadi.getStokJadi());
            }

            System.out.print("\nPilih nomor barang yang akan dinonaktifkan (0 untuk batal): ");
            int pilihan = Integer.parseInt(scanner.nextLine());

            if (pilihan == 0)
                return;
            if (pilihan < 1 || pilihan > daftarBarangJadi.size()) {
                System.out.println("\nNomor barang tidak valid!");
                return;
            }

            BarangJadi barangJadi = daftarBarangJadi.get(pilihan - 1);

            System.out.print("\nAnda yakin ingin menonaktifkan barang ini? (y/n): ");
            String konfirmasi = scanner.nextLine();

            if (konfirmasi.equalsIgnoreCase("y")) {
                if (barangJadiService.nonaktifkanBarang(barangJadi.getIdBarang())) {
                    System.out.println("\nBarang berhasil dinonaktifkan!");
                } else {
                    System.out.println("\nGagal menonaktifkan barang!");
                }
            } else {
                System.out.println("\nPenonaktifan dibatalkan.");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInput harus berupa angka!");
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void tambahStok() {
        System.out.println("\n=== TAMBAH STOK BARANG JADI ===\n");

        try {
            List<BarangJadi> daftarBarangJadi = barangJadiService.getBarangJadiAktif();
            if (daftarBarangJadi.isEmpty()) {
                System.out.println("Tidak ada barang jadi yang aktif.");
                return;
            }

            System.out.printf("%-5s %-25s %-15s %-10s%n",
                    "No.", "Nama Barang", "Harga Jual", "Stok");
            System.out.println("-".repeat(60));

            int no = 1;
            for (BarangJadi barangJadi : daftarBarangJadi) {
                System.out.printf("%-5d %-25s %-15.2f %-10d%n",
                        no++,
                        barangJadi.getNamaBarang(),
                        barangJadi.getHargaJual(),
                        barangJadi.getStokJadi());
            }

            System.out.print("\nPilih nomor barang (0 untuk batal): ");
            int pilihan = Integer.parseInt(scanner.nextLine());

            if (pilihan == 0)
                return;
            if (pilihan < 1 || pilihan > daftarBarangJadi.size()) {
                System.out.println("\nNomor barang tidak valid!");
                return;
            }

            BarangJadi barangJadi = daftarBarangJadi.get(pilihan - 1);

            System.out.print("Jumlah penambahan: ");
            int jumlah = Integer.parseInt(scanner.nextLine());

            if (barangJadiService.tambahStok(barangJadi.getIdBarang(), jumlah)) {
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
        System.out.println("\n=== KURANGI STOK BARANG JADI ===\n");

        try {
            List<BarangJadi> daftarBarangJadi = barangJadiService.getBarangJadiAktif();
            if (daftarBarangJadi.isEmpty()) {
                System.out.println("Tidak ada barang jadi yang aktif.");
                return;
            }

            System.out.printf("%-5s %-25s %-15s %-10s%n",
                    "No.", "Nama Barang", "Harga Jual", "Stok");
            System.out.println("-".repeat(60));

            int no = 1;
            for (BarangJadi barangJadi : daftarBarangJadi) {
                System.out.printf("%-5d %-25s %-15.2f %-10d%n",
                        no++,
                        barangJadi.getNamaBarang(),
                        barangJadi.getHargaJual(),
                        barangJadi.getStokJadi());
            }

            System.out.print("\nPilih nomor barang (0 untuk batal): ");
            int pilihan = Integer.parseInt(scanner.nextLine());

            if (pilihan == 0)
                return;
            if (pilihan < 1 || pilihan > daftarBarangJadi.size()) {
                System.out.println("\nNomor barang tidak valid!");
                return;
            }

            BarangJadi barangJadi = daftarBarangJadi.get(pilihan - 1);

            System.out.print("Jumlah pengurangan: ");
            int jumlah = Integer.parseInt(scanner.nextLine());

            if (barangJadiService.kurangiStok(barangJadi.getIdBarang(), jumlah)) {
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
}
