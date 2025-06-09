package com.momoyo.service;

import com.momoyo.dao.*;
import com.momoyo.model.*;

import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.List;

public class ProduksiService {
    private final ResepDAO resepDAO;
    private final BahanBakuDAO bahanBakuDAO;
    private final BarangJadiDAO barangJadiDAO;

    public ProduksiService() {
        this.resepDAO = new ResepDAO();
        this.bahanBakuDAO = new BahanBakuDAO();
        this.barangJadiDAO = new BarangJadiDAO();
    }

    public boolean produksiBarang(Resep resep, int jumlahProduksi) throws SQLException {
        if (jumlahProduksi <= 0) {
            throw new IllegalArgumentException("Jumlah produksi harus lebih dari 0");
        }

        if (!cekKetersediaanBahanBaku(resep, jumlahProduksi)) {
            throw new IllegalStateException("Stok bahan baku tidak mencukupi");
        }

        for (DetailResep detail : resep.getDetailResepList()) {
            BahanBaku bahan = detail.getBahanBaku();
            BigDecimal jumlahDibutuhkan = detail.getKuantitasBahan()
                    .multiply(BigDecimal.valueOf(jumlahProduksi));

            bahan.kurangiStok(jumlahDibutuhkan);
            bahanBakuDAO.updateStokBahanBaku(bahan.getIdBahan(), bahan.getStokTersedia());
        }

        BarangJadi barangJadi = resep.getBarangJadi();
        barangJadi.tambahStok(jumlahProduksi);
        barangJadiDAO.updateStokBarangJadi(barangJadi.getIdBarang(), barangJadi.getStokJadi());

        return true;
    }

    private boolean cekKetersediaanBahanBaku(Resep resep, int jumlahProduksi) {
        for (DetailResep detail : resep.getDetailResepList()) {
            BahanBaku bahan = detail.getBahanBaku();
            BigDecimal jumlahDibutuhkan = detail.getKuantitasBahan()
                    .multiply(BigDecimal.valueOf(jumlahProduksi));

            if (bahan.getStokTersedia().compareTo(jumlahDibutuhkan) < 0) {
                return false;
            }
        }
        return true;
    }

    public List<BahanBaku> getBahanBakuKritis() throws SQLException {
        return bahanBakuDAO.getBahanBakuKritis();
    }

    public boolean cekStokBarangJadi(int idBarang, int jumlah) throws SQLException {
        BarangJadi barangJadi = barangJadiDAO.getBarangJadiById(idBarang);
        if (barangJadi == null) {
            throw new IllegalArgumentException("Barang jadi tidak ditemukan");
        }
        return barangJadi.isStokTersedia(jumlah);
    }

    public Resep getResepByBarangJadi(int idBarang) throws SQLException {
        return resepDAO.getResepByBarangJadi(idBarang);
    }

    public List<Resep> getAllResep() throws SQLException {
        return resepDAO.getAllResep();
    }

    public boolean tambahResep(Resep resep) throws SQLException {
        resepDAO.tambahResep(resep);
        return true;
    }

    public boolean updateResep(Resep resep) throws SQLException {
        resepDAO.updateResep(resep);
        return true;
    }

    public boolean hapusResep(int idResep) throws SQLException {
        resepDAO.hapusResep(idResep);
        return true;
    }

    public Resep getResepById(int idResep) throws SQLException {
        return resepDAO.getResepById(idResep);
    }
}
