package com.momoyo.menu;

import com.momoyo.model.*;
import com.momoyo.service.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ResepMenu {
    private static final Scanner scanner = new Scanner(System.in);
    private final BarangJadiService barangJadiService;
    private final BahanBakuService bahanBakuService;
    private final ProduksiService produksiService;

    public ResepMenu(BarangJadiService barangJadiService, BahanBakuService bahanBakuService,
            ProduksiService produksiService) {
        this.barangJadiService = barangJadiService;
        this.bahanBakuService = bahanBakuService;
        this.produksiService = produksiService;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== MANAJEMEN RESEP ===\n");
            System.out.println("1. Tambah Resep");
            System.out.println("2. Lihat Daftar Resep");
            System.out.println("3. Ubah Resep");
            System.out.println("4. Hapus Resep");
            System.out.println("0. Kembali");
            System.out.print("\nPilihan: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        tambahResep();
                        break;
                    case 2:
                        lihatDaftarResep();
                        break;
                    case 3:
                        ubahResep();
                        break;
                    case 4:
                        hapusResep();
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

    private void tambahResep() {
        System.out.println("\n=== TAMBAH RESEP ===\n");

        try {
            List<BarangJadi> daftarBarangJadi = barangJadiService.getBarangJadiAktif();
            if (daftarBarangJadi.isEmpty()) {
                System.out.println("Tidak ada barang jadi yang aktif.");
                return;
            }

            System.out.println("Pilih barang jadi:");
            System.out.printf("%-5s %-25s%n", "No.", "Nama Barang");
            System.out.println("-".repeat(30));

            int no = 1;
            for (BarangJadi barangJadi : daftarBarangJadi) {
                System.out.printf("%-5d %-25s%n",
                        no++,
                        barangJadi.getNamaBarang());
            }

            System.out.print("\nPilihan (0 untuk batal): ");
            int pilihanBarang = Integer.parseInt(scanner.nextLine());

            if (pilihanBarang == 0)
                return;
            if (pilihanBarang < 1 || pilihanBarang > daftarBarangJadi.size()) {
                System.out.println("\nPilihan tidak valid!");
                return;
            }

            BarangJadi barangJadi = daftarBarangJadi.get(pilihanBarang - 1);

            if (produksiService.getResepByBarangJadi(barangJadi.getIdBarang()) != null) {
                System.out.println("\nBarang ini sudah memiliki resep!");
                return;
            }

            System.out.print("\nDeskripsi resep: ");
            String deskripsi = scanner.nextLine();

            Resep resep = new Resep();
            resep.setBarangJadi(barangJadi);
            resep.setDeskripsiResep(deskripsi);
            resep.setDetailResepList(new ArrayList<>());

            while (true) {
                List<BahanBaku> daftarBahanBaku = bahanBakuService.getAllBahanBaku();
                if (daftarBahanBaku.isEmpty()) {
                    System.out.println("\nTidak ada bahan baku tersedia.");
                    if (resep.getDetailResepList().isEmpty()) {
                        System.out.println("Resep tidak dapat dibuat tanpa bahan baku.");
                        return;
                    } else {
                        break;
                    }
                }

                System.out.println("\nDaftar Bahan Baku:");
                System.out.printf("%-5s %-20s %-10s%n", "No.", "Nama Bahan", "Satuan");
                System.out.println("-".repeat(35));

                no = 1;
                for (BahanBaku bahanBaku : daftarBahanBaku) {
                    System.out.printf("%-5d %-20s %-10s%n",
                            no++,
                            bahanBaku.getNamaBahan(),
                            bahanBaku.getSatuan().getNamaSatuan());
                }

                System.out.print("\nPilih bahan (0 untuk selesai): ");
                int pilihanBahan = Integer.parseInt(scanner.nextLine());

                if (pilihanBahan == 0)
                    break;
                if (pilihanBahan < 1 || pilihanBahan > daftarBahanBaku.size()) {
                    System.out.println("\nPilihan tidak valid!");
                    continue;
                }

                BahanBaku bahanBaku = daftarBahanBaku.get(pilihanBahan - 1);

                boolean bahanSudahAda = false;
                for (DetailResep detail : resep.getDetailResepList()) {
                    if (detail.getBahanBaku().getIdBahan() == bahanBaku.getIdBahan()) {
                        bahanSudahAda = true;
                        break;
                    }
                }

                if (bahanSudahAda) {
                    System.out.println("\nBahan ini sudah ada dalam resep!");
                    continue;
                }

                System.out.printf("Jumlah (dalam %s): ", bahanBaku.getSatuan().getNamaSatuan());
                BigDecimal jumlah = new BigDecimal(scanner.nextLine());

                DetailResep detail = new DetailResep();
                detail.setResep(resep);
                detail.setBahanBaku(bahanBaku);
                detail.setKuantitasBahan(jumlah);

                resep.tambahDetailResep(detail);
            }

            if (resep.getDetailResepList().isEmpty()) {
                System.out.println("\nResep harus memiliki minimal 1 bahan!");
                return;
            }

            if (produksiService.tambahResep(resep)) {
                System.out.println("\nResep berhasil ditambahkan!");
            } else {
                System.out.println("\nGagal menambahkan resep!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInput numerik tidak valid!");
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void lihatDaftarResep() {
        System.out.println("\n=== DAFTAR RESEP ===\n");

        try {
            List<Resep> daftarResep = produksiService.getAllResep();
            if (daftarResep.isEmpty()) {
                System.out.println("Belum ada data resep.");
                return;
            }

            for (Resep resep : daftarResep) {
                System.out.println("Barang: " + resep.getBarangJadi().getNamaBarang());
                System.out.println("Deskripsi: " + resep.getDeskripsiResep());
                System.out.println("\nBahan-bahan:");
                System.out.printf("%-25s %-15s %-10s%n",
                        "Nama Bahan", "Jumlah", "Satuan");
                System.out.println("-".repeat(50));

                for (DetailResep detail : resep.getDetailResepList()) {
                    System.out.printf("%-25s %-15.2f %-10s%n",
                            detail.getBahanBaku().getNamaBahan(),
                            detail.getKuantitasBahan(),
                            detail.getBahanBaku().getSatuan().getNamaSatuan());
                }
                System.out.println("\n" + "=".repeat(50));
            }
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void ubahResep() {
        System.out.println("\n=== UBAH RESEP ===\n");

        try {
            List<Resep> daftarResep = produksiService.getAllResep();
            if (daftarResep.isEmpty()) {
                System.out.println("Belum ada data resep.");
                return;
            }

            System.out.println("Pilih resep yang akan diubah:");
            System.out.printf("%-5s %-25s%n", "No.", "Nama Barang");
            System.out.println("-".repeat(30));

            int no = 1;
            for (Resep resep : daftarResep) {
                System.out.printf("%-5d %-25s%n",
                        no++,
                        resep.getBarangJadi().getNamaBarang());
            }

            System.out.print("\nPilihan (0 untuk batal): ");
            int pilihan = Integer.parseInt(scanner.nextLine());

            if (pilihan == 0)
                return;
            if (pilihan < 1 || pilihan > daftarResep.size()) {
                System.out.println("\nPilihan tidak valid!");
                return;
            }

            Resep resep = daftarResep.get(pilihan - 1);

            System.out.print("Deskripsi baru (kosongkan jika tidak diubah): ");
            String deskripsi = scanner.nextLine();
            if (!deskripsi.isEmpty()) {
                resep.setDeskripsiResep(deskripsi);
            }

            while (true) {
                System.out.println("\nDetail Resep Saat Ini:");
                if (resep.getDetailResepList().isEmpty()) {
                    System.out.println("Tidak ada bahan dalam resep ini.");
                } else {
                    System.out.printf("%-5s %-25s %-15s %-10s%n",
                            "No.", "Nama Bahan", "Jumlah", "Satuan");
                    System.out.println("-".repeat(55));

                    no = 1;
                    for (DetailResep detail : resep.getDetailResepList()) {
                        System.out.printf("%-5d %-25s %-15.2f %-10s%n",
                                no++,
                                detail.getBahanBaku().getNamaBahan(),
                                detail.getKuantitasBahan(),
                                detail.getBahanBaku().getSatuan().getNamaSatuan());
                    }
                }

                System.out.println("\n1. Tambah Bahan");
                System.out.println("2. Ubah Jumlah Bahan");
                System.out.println("3. Hapus Bahan");
                System.out.println("0. Selesai");
                System.out.print("\nPilihan: ");

                int pilihanEdit = Integer.parseInt(scanner.nextLine());

                if (pilihanEdit == 0)
                    break;

                switch (pilihanEdit) {
                    case 1:
                        List<BahanBaku> daftarBahanBaku = bahanBakuService.getAllBahanBaku();
                        if (daftarBahanBaku.isEmpty()) {
                            System.out.println("\nTidak ada bahan baku tersedia untuk ditambahkan.");
                            continue;
                        }
                        System.out.println("\nDaftar Bahan Baku:");
                        System.out.printf("%-5s %-20s %-10s%n", "No.", "Nama Bahan", "Satuan");
                        System.out.println("-".repeat(35));

                        no = 1;
                        for (BahanBaku bahanBaku : daftarBahanBaku) {
                            System.out.printf("%-5d %-20s %-10s%n",
                                    no++,
                                    bahanBaku.getNamaBahan(),
                                    bahanBaku.getSatuan().getNamaSatuan());
                        }

                        System.out.print("\nPilih bahan (0 untuk batal): ");
                        int pilihanBahan = Integer.parseInt(scanner.nextLine());

                        if (pilihanBahan == 0)
                            continue;
                        if (pilihanBahan < 1 || pilihanBahan > daftarBahanBaku.size()) {
                            System.out.println("\nPilihan tidak valid!");
                            continue;
                        }

                        BahanBaku bahanBaku = daftarBahanBaku.get(pilihanBahan - 1);

                        boolean bahanSudahAda = false;
                        for (DetailResep detail : resep.getDetailResepList()) {
                            if (detail.getBahanBaku().getIdBahan() == bahanBaku.getIdBahan()) {
                                bahanSudahAda = true;
                                break;
                            }
                        }

                        if (bahanSudahAda) {
                            System.out.println("\nBahan ini sudah ada dalam resep!");
                            continue;
                        }

                        System.out.printf("Jumlah (dalam %s): ", bahanBaku.getSatuan().getNamaSatuan());
                        BigDecimal jumlah = new BigDecimal(scanner.nextLine());

                        DetailResep detail = new DetailResep();
                        detail.setResep(resep);
                        detail.setBahanBaku(bahanBaku);
                        detail.setKuantitasBahan(jumlah);

                        resep.tambahDetailResep(detail);
                        break;

                    case 2:
                        if (resep.getDetailResepList().isEmpty()) {
                            System.out.println("Tidak ada bahan untuk diubah.");
                            continue;
                        }
                        System.out.print("\nPilih nomor bahan yang akan diubah: ");
                        int noBahan = Integer.parseInt(scanner.nextLine());

                        if (noBahan < 1 || noBahan > resep.getDetailResepList().size()) {
                            System.out.println("\nNomor bahan tidak valid!");
                            continue;
                        }

                        DetailResep detailUbah = resep.getDetailResepList().get(noBahan - 1);
                        System.out.printf("Jumlah baru (dalam %s): ",
                                detailUbah.getBahanBaku().getSatuan().getNamaSatuan());
                        BigDecimal jumlahBaru = new BigDecimal(scanner.nextLine());
                        detailUbah.setKuantitasBahan(jumlahBaru);
                        break;

                    case 3:
                        if (resep.getDetailResepList().isEmpty()) {
                            System.out.println("Tidak ada bahan untuk dihapus.");
                            continue;
                        }
                        if (resep.getDetailResepList().size() <= 1) {
                            System.out.println("\nResep harus memiliki minimal 1 bahan!");
                            continue;
                        }

                        System.out.print("\nPilih nomor bahan yang akan dihapus: ");
                        int noHapus = Integer.parseInt(scanner.nextLine());

                        if (noHapus < 1 || noHapus > resep.getDetailResepList().size()) {
                            System.out.println("\nNomor bahan tidak valid!");
                            continue;
                        }

                        resep.getDetailResepList().remove(noHapus - 1);
                        break;

                    default:
                        System.out.println("\nPilihan tidak valid!");
                }
            }

            if (resep.getDetailResepList().isEmpty()) {
                System.out.println("\nResep tidak dapat disimpan tanpa bahan baku. Pembatalan perubahan.");
                return;
            }

            if (produksiService.updateResep(resep)) {
                System.out.println("\nResep berhasil diubah!");
            } else {
                System.out.println("\nGagal mengubah resep!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInput numerik tidak valid!");
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void hapusResep() {
        System.out.println("\n=== HAPUS RESEP ===\n");

        try {
            List<Resep> daftarResep = produksiService.getAllResep();
            if (daftarResep.isEmpty()) {
                System.out.println("Belum ada data resep.");
                return;
            }

            System.out.println("Pilih resep yang akan dihapus:");
            System.out.printf("%-5s %-25s%n", "No.", "Nama Barang");
            System.out.println("-".repeat(30));

            int no = 1;
            for (Resep resep : daftarResep) {
                System.out.printf("%-5d %-25s%n",
                        no++,
                        resep.getBarangJadi().getNamaBarang());
            }

            System.out.print("\nPilihan (0 untuk batal): ");
            int pilihan = Integer.parseInt(scanner.nextLine());

            if (pilihan == 0)
                return;
            if (pilihan < 1 || pilihan > daftarResep.size()) {
                System.out.println("\nPilihan tidak valid!");
                return;
            }

            Resep resep = daftarResep.get(pilihan - 1);

            System.out.print("\nAnda yakin ingin menghapus resep ini? (y/n): ");
            String konfirmasi = scanner.nextLine();

            if (konfirmasi.equalsIgnoreCase("y")) {
                if (produksiService.hapusResep(resep.getIdResep())) {
                    System.out.println("\nResep berhasil dihapus!");
                } else {
                    System.out.println("\nGagal menghapus resep!");
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
