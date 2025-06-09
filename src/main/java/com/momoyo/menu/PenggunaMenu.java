package com.momoyo.menu;

import com.momoyo.model.Pengguna;
import com.momoyo.service.PenggunaService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class PenggunaMenu {
    private static final Scanner scanner = new Scanner(System.in);
    private final PenggunaService penggunaService;

    public PenggunaMenu(PenggunaService penggunaService) {
        this.penggunaService = penggunaService;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== MANAJEMEN PENGGUNA ===\n");
            System.out.println("1. Tambah Pengguna");
            System.out.println("2. Lihat Daftar Pengguna");
            System.out.println("3. Ubah Data Pengguna");
            System.out.println("4. Hapus Pengguna");
            System.out.println("0. Kembali");
            System.out.print("\nPilihan: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        tambahPengguna();
                        break;
                    case 2:
                        lihatDaftarPengguna();
                        break;
                    case 3:
                        ubahPengguna();
                        break;
                    case 4:
                        hapusPengguna();
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

    private void tambahPengguna() {
        System.out.println("\n=== TAMBAH PENGGUNA ===\n");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.println("\nRole:");
        System.out.println("1. Admin");
        System.out.println("2. Kasir");
        System.out.print("Pilihan: ");

        try {
            int roleChoice = Integer.parseInt(scanner.nextLine());
            Pengguna.Role roleEnum;
            switch (roleChoice) {
                case 1:
                    roleEnum = Pengguna.Role.ADMIN;
                    break;
                case 2:
                    roleEnum = Pengguna.Role.KASIR;
                    break;
                default:
                    System.out.println("\nPilihan role tidak valid!");
                    return;
            }

            Pengguna pengguna = new Pengguna();
            pengguna.setUsername(username);
            pengguna.setPassword(password);
            pengguna.setPeran(roleEnum);

            if (penggunaService.tambahPengguna(pengguna)) {
                System.out.println("\nPengguna berhasil ditambahkan!");
            } else {
                System.out.println("\nGagal menambahkan pengguna!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInput role harus berupa angka!");
        } catch (IllegalArgumentException e) {
            System.out.println("\n" + e.getMessage());
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void lihatDaftarPengguna() {
        System.out.println("\n=== DAFTAR PENGGUNA ===\n");

        try {
            List<Pengguna> daftarPengguna = penggunaService.getAllPengguna();
            if (daftarPengguna.isEmpty()) {
                System.out.println("Belum ada data pengguna.");
                return;
            }

            System.out.printf("%-5s %-20s %-10s%n", "No.", "Username", "Role");
            System.out.println("-".repeat(35));

            int no = 1;
            for (Pengguna pengguna : daftarPengguna) {
                System.out.printf("%-5d %-20s %-10s%n",
                        no++,
                        pengguna.getUsername(),
                        pengguna.getPeran());
            }
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void ubahPengguna() {
        System.out.println("\n=== UBAH DATA PENGGUNA ===\n");

        try {
            List<Pengguna> daftarPengguna = penggunaService.getAllPengguna();
            if (daftarPengguna.isEmpty()) {
                System.out.println("Belum ada data pengguna.");
                return;
            }

            System.out.printf("%-5s %-20s %-10s%n", "No.", "Username", "Role");
            System.out.println("-".repeat(35));

            int no = 1;
            for (Pengguna pengguna : daftarPengguna) {
                System.out.printf("%-5d %-20s %-10s%n",
                        no++,
                        pengguna.getUsername(),
                        pengguna.getPeran());
            }

            System.out.print("\nPilih nomor pengguna yang akan diubah (0 untuk batal): ");
            int pilihan = Integer.parseInt(scanner.nextLine());

            if (pilihan == 0)
                return;
            if (pilihan < 1 || pilihan > daftarPengguna.size()) {
                System.out.println("\nNomor pengguna tidak valid!");
                return;
            }

            Pengguna pengguna = daftarPengguna.get(pilihan - 1);

            System.out.print("\nUsername baru (kosongkan jika tidak diubah): ");
            String username = scanner.nextLine();
            if (!username.isEmpty()) {
                pengguna.setUsername(username);
            }

            System.out.print("Password baru (kosongkan jika tidak diubah): ");
            String password = scanner.nextLine();
            if (!password.isEmpty()) {
                pengguna.setPassword(password);
            }

            System.out.println("\nRole baru:");
            System.out.println("1. Admin");
            System.out.println("2. Kasir");
            System.out.println("0. Tidak diubah");
            System.out.print("Pilihan: ");

            String roleChoiceStr = scanner.nextLine();
            if (!roleChoiceStr.isEmpty()) {
                int roleChoice = Integer.parseInt(roleChoiceStr);
                Pengguna.Role newRole = null;
                switch (roleChoice) {
                    case 1:
                        newRole = Pengguna.Role.ADMIN;
                        break;
                    case 2:
                        newRole = Pengguna.Role.KASIR;
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("\nPilihan role tidak valid!");
                        return;
                }
                if (newRole != null) {
                    pengguna.setPeran(newRole);
                }
            }

            if (penggunaService.updatePengguna(pengguna)) {
                System.out.println("\nData pengguna berhasil diubah!");
            } else {
                System.out.println("\nGagal mengubah data pengguna!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInput harus berupa angka!");
        } catch (IllegalArgumentException e) {
            System.out.println("\n" + e.getMessage());
        } catch (SQLException e) {
            System.out.println("\nTerjadi kesalahan: " + e.getMessage());
        }
    }

    private void hapusPengguna() {
        System.out.println("\n=== HAPUS PENGGUNA ===\n");

        try {
            List<Pengguna> daftarPengguna = penggunaService.getAllPengguna(); // Declare and assign here
            if (daftarPengguna.isEmpty()) {
                System.out.println("Belum ada data pengguna.");
                return;
            }

            System.out.printf("%-5s %-20s %-10s%n", "No.", "Username", "Role");
            System.out.println("-".repeat(35));

            int no = 1;
            for (Pengguna pengguna : daftarPengguna) {
                System.out.printf("%-5d %-20s %-10s%n",
                        no++,
                        pengguna.getUsername(),
                        pengguna.getPeran());
            }

            System.out.print("\nPilih nomor pengguna yang akan dihapus (0 untuk batal): ");
            int pilihan = Integer.parseInt(scanner.nextLine());

            if (pilihan == 0)
                return;
            if (pilihan < 1 || pilihan > daftarPengguna.size()) {
                System.out.println("\nNomor pengguna tidak valid!");
                return;
            }

            Pengguna pengguna = daftarPengguna.get(pilihan - 1);

            System.out.print("\nAnda yakin ingin menghapus pengguna ini? (y/n): ");
            String konfirmasi = scanner.nextLine();

            if (konfirmasi.equalsIgnoreCase("y")) {
                if (penggunaService.hapusPengguna(pengguna.getIdPengguna())) {
                    System.out.println("\nPengguna berhasil dihapus!");
                } else {
                    System.out.println("\nGagal menghapus pengguna!");
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
