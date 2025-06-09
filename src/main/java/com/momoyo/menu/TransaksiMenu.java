package com.momoyo.menu;

import com.momoyo.model.*;
import com.momoyo.service.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TransaksiMenu {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final TransaksiService transaksiService;
    private final BarangJadiService barangJadiService;
    private final Pengguna kasir;

    public TransaksiMenu(TransaksiService transaksiService, BarangJadiService barangJadiService, Pengguna kasir) {
        this.transaksiService = transaksiService;
        this.barangJadiService = barangJadiService;
        this.kasir = kasir;
    }

    public Pengguna getKasir() {
        return kasir;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== MENU TRANSAKSI ===\n");
            System.out.println("1. Transaksi Baru");
            System.out.println("2. Cari Struk");
            System.out.println("3. Laporan Penjualan");
            System.out.println("4. Barang Terlaris");
            System.out.println("0. Kembali");
            System.out.print("\nPilihan: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        transaksiBaru();
                        break;
                    case 2:
                        cariStruk();
                        break;
                    case 3:
                        laporanPenjualan();
                        break;
                    case 4:
                        barangTerlaris();
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

    public void transaksiBaru() {
        System.out.println("\n=== TRANSAKSI BARU ===\n");

        try {
            List<DetailTransaksi> detailList = new ArrayList<>();
            BigDecimal totalTransaksi = BigDecimal.ZERO;

            while (true) {
                List<BarangJadi> daftarBarangJadi = barangJadiService.getBarangJadiAktif();
                if (daftarBarangJadi.isEmpty()) {
                    System.out.println("Tidak ada barang jadi yang aktif.");
                    return;
                }

                System.out.println("\nPilih barang:");
                System.out.printf("%-5s %-25s %-10s %-15s%n", "No.", "Nama Barang", "Stok", "Harga");
                System.out.println("-".repeat(55));

                int no = 1;
                for (BarangJadi barangJadi : daftarBarangJadi) {
                    System.out.printf("%-5d %-25s %-10d Rp %-12.0f%n",
                            no++,
                            barangJadi.getNamaBarang(),
                            barangJadi.getStokJadi(),
                            barangJadi.getHargaJual());
                }

                System.out.print("\nPilihan (0 untuk selesai): ");
                int pilihan = Integer.parseInt(scanner.nextLine());

                if (pilihan == 0)
                    break;
                if (pilihan < 1 || pilihan > daftarBarangJadi.size()) {
                    System.out.println("\nPilihan tidak valid!");
                    continue;
                }

                BarangJadi barangJadi = daftarBarangJadi.get(pilihan - 1);

                System.out.print("Jumlah: ");
                int jumlah = Integer.parseInt(scanner.nextLine());

                if (jumlah <= 0) {
                    System.out.println("\nJumlah harus positif!");
                    continue;
                }

                if (jumlah > barangJadi.getStokJadi()) {
                    System.out.println("\nStok tidak mencukupi!");
                    continue;
                }

                DetailTransaksi detail = new DetailTransaksi();
                detail.setBarangJadi(barangJadi);
                detail.setNamaBarangSaatTransaksi(barangJadi.getNamaBarang());
                detail.setJumlahTerjual(jumlah);
                detail.setHargaSatuanSaatTransaksi(barangJadi.getHargaJual());

                detailList.add(detail);
                totalTransaksi = totalTransaksi.add(detail.getSubtotal());

                System.out.printf("\nSubtotal: Rp %.0f%n", detail.getSubtotal());
                System.out.printf("Total: Rp %.0f%n", totalTransaksi);
            }

            if (detailList.isEmpty()) {
                System.out.println("\nTransaksi dibatalkan.");
                return;
            }

            System.out.println("\n=== RINGKASAN TRANSAKSI ===\n");
            System.out.printf("%-25s %-10s %-15s %-15s%n",
                    "Nama Barang", "Jumlah", "Harga", "Subtotal");
            System.out.println("-".repeat(65));

            for (DetailTransaksi detail : detailList) {
                System.out.printf("%-25s %-10d Rp %-12.0f Rp %-12.0f%n",
                        detail.getNamaBarangSaatTransaksi(),
                        detail.getJumlahTerjual(),
                        detail.getHargaSatuanSaatTransaksi(),
                        detail.getSubtotal());
            }

            System.out.println("-".repeat(65));
            System.out.printf("%-51s Rp %-12.0f%n", "Total", totalTransaksi);

            System.out.print("\nLanjutkan transaksi? (y/n): ");
            String konfirmasi = scanner.nextLine();

            if (konfirmasi.equalsIgnoreCase("y")) {
                Struk struk = new Struk();
                struk.setWaktuTransaksi(LocalDateTime.now());
                struk.setKasir(kasir);
                struk.setDetailTransaksiList(detailList);
                struk.setTotalKeseluruhan(totalTransaksi);

                if (transaksiService.simpanTransaksi(struk)) {
                    System.out.println("\nTransaksi berhasil!");
                    System.out.println("Nomor Struk: " + struk.getNomorStruk());
                } else {
                    System.out.println("\nGagal menyimpan transaksi!");
                }
            } else {
                System.out.println("\nTransaksi dibatalkan.");
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

    public void cariStruk() {
        System.out.println("\n=== CARI STRUK ===\n");

        try {
            System.out.print("Masukkan nomor struk: ");
            String nomorStruk = scanner.nextLine();

            Struk struk = transaksiService.getStrukByNomor(nomorStruk);
            if (struk == null) {
                System.out.println("\nStruk tidak ditemukan!");
                return;
            }

            System.out.println("\nDetail Struk:");
            System.out.println("Nomor Struk   : " + struk.getNomorStruk());
            System.out.println("Waktu         : " + struk.getWaktuTransaksi().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            System.out.println("Kasir         : " + struk.getKasir().getUsername());
            System.out.println();

            System.out.printf("%-25s %-10s %-15s %-15s%n",
                    "Nama Barang", "Jumlah", "Harga", "Subtotal");
            System.out.println("-".repeat(65));

            for (DetailTransaksi detail : struk.getDetailTransaksiList()) {
                System.out.printf("%-25s %-10d Rp %-12.0f Rp %-12.0f%n",
                        detail.getNamaBarangSaatTransaksi(),
                        detail.getJumlahTerjual(),
                        detail.getHargaSatuanSaatTransaksi(),
                        detail.getSubtotal());
            }

            System.out.println("-".repeat(65));
            System.out.printf("%-51s Rp %-12.0f%n", "Total", struk.getTotalKeseluruhan());
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    public void laporanPenjualan() {
        System.out.println("\n=== LAPORAN PENJUALAN ===\n");

        try {
            System.out.println("Format tanggal: dd/MM/yyyy");
            System.out.print("Tanggal awal : ");
            LocalDate tanggalAwal = LocalDate.parse(scanner.nextLine(), DATE_FORMATTER);

            System.out.print("Tanggal akhir: ");
            LocalDate tanggalAkhir = LocalDate.parse(scanner.nextLine(), DATE_FORMATTER);

            if (tanggalAkhir.isBefore(tanggalAwal)) {
                System.out.println("\nTanggal akhir tidak boleh sebelum tanggal awal!");
                return;
            }

            List<Struk> daftarStruk = transaksiService.getLaporanPenjualan(tanggalAwal, tanggalAkhir);
            if (daftarStruk.isEmpty()) {
                System.out.println("\nTidak ada transaksi dalam rentang tanggal tersebut.");
                return;
            }

            BigDecimal totalPenjualan = BigDecimal.ZERO;
            System.out.printf("\n%-20s %-20s %-15s %-15s%n",
                    "Nomor Struk", "Waktu", "Kasir", "Total");
            System.out.println("-".repeat(70));

            for (Struk struk : daftarStruk) {
                System.out.printf("%-20s %-20s %-15s Rp %-12.0f%n",
                        struk.getNomorStruk(),
                        struk.getWaktuTransaksi().format(
                                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        struk.getKasir().getUsername(),
                        struk.getTotalKeseluruhan());
                totalPenjualan = totalPenjualan.add(struk.getTotalKeseluruhan());
            }

            System.out.println("-".repeat(70));
            System.out.printf("%-56s Rp %-12.0f%n", "Total Penjualan", totalPenjualan);
        } catch (DateTimeParseException e) {
            System.out.println("\nFormat tanggal tidak valid!");
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    public void barangTerlaris() {
        System.out.println("\n=== BARANG TERLARIS ===\n");

        try {
            System.out.println("Format tanggal: dd/MM/yyyy");
            System.out.print("Tanggal awal : ");
            LocalDate tanggalAwal = LocalDate.parse(scanner.nextLine(), DATE_FORMATTER);

            System.out.print("Tanggal akhir: ");
            LocalDate tanggalAkhir = LocalDate.parse(scanner.nextLine(), DATE_FORMATTER);

            if (tanggalAkhir.isBefore(tanggalAwal)) {
                System.out.println("\nTanggal akhir tidak boleh sebelum tanggal awal!");
                return;
            }

            List<BarangJadi> daftarTerlaris = transaksiService.getBarangTerlaris(tanggalAwal, tanggalAkhir, 5);
            if (daftarTerlaris.isEmpty()) {
                System.out.println("\nTidak ada barang terjual dalam rentang tanggal tersebut.");
                return;
            }

            System.out.printf("\n%-5s %-25s %-15s%n", "No.", "Nama Barang", "Jumlah Terjual");
            System.out.println("-".repeat(45));

            int no = 1;
            for (BarangJadi barang : daftarTerlaris) {
                System.out.printf("%-5d %-25s %-15d%n",
                        no++,
                        barang.getNamaBarang(),
                        barang.getJumlahTerjual());
            }
        } catch (DateTimeParseException e) {
            System.out.println("\nFormat tanggal tidak valid!");
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }
}
