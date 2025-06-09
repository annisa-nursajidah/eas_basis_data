package com.momoyo.service;

import com.momoyo.dao.BarangJadiDAO;
import com.momoyo.model.BarangJadi;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class BarangJadiService {
    private final BarangJadiDAO barangJadiDAO;

    public BarangJadiService() {
        this.barangJadiDAO = new BarangJadiDAO();
    }

    public boolean tambahBarangJadi(BarangJadi barangJadi) throws SQLException {
        barangJadi.setActive(true);
        barangJadiDAO.tambahBarangJadi(barangJadi);
        return true;
    }

    public BarangJadi getBarangJadi(int idBarang) throws SQLException {
        return barangJadiDAO.getBarangJadiById(idBarang);
    }

    public List<BarangJadi> getAllBarangJadi() throws SQLException {
        return barangJadiDAO.getAllBarangJadi();
    }

    public List<BarangJadi> getBarangJadiAktif() throws SQLException {
        return barangJadiDAO.getBarangJadiAktif();
    }

    public boolean updateBarangJadi(BarangJadi barangJadi) throws SQLException {
        barangJadiDAO.updateBarangJadi(barangJadi);
        return true;
    }

    public boolean tambahStok(int idBarang, int jumlah) throws SQLException {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah penambahan stok harus positif");
        }

        BarangJadi barangJadi = getBarangJadi(idBarang);
        if (barangJadi == null) {
            throw new IllegalArgumentException("Barang jadi tidak ditemukan");
        }

        barangJadi.tambahStok(jumlah);
        barangJadiDAO.updateStokBarangJadi(barangJadi.getIdBarang(), barangJadi.getStokJadi());
        return true;
    }

    public boolean kurangiStok(int idBarang, int jumlah) throws SQLException {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah pengurangan stok harus positif");
        }

        BarangJadi barangJadi = getBarangJadi(idBarang);
        if (barangJadi == null) {
            throw new IllegalArgumentException("Barang jadi tidak ditemukan");
        }

        if (!barangJadi.isStokTersedia(jumlah)) {
            throw new IllegalArgumentException(
                    String.format("Stok %s tidak mencukupi. Tersedia: %d, Diminta: %d",
                            barangJadi.getNamaBarang(), barangJadi.getStokJadi(), jumlah));
        }

        barangJadi.kurangiStok(jumlah);
        barangJadiDAO.updateStokBarangJadi(barangJadi.getIdBarang(), barangJadi.getStokJadi());
        return true;
    }

    public boolean nonaktifkanBarang(int idBarang) throws SQLException {
        BarangJadi barangJadi = getBarangJadi(idBarang);
        if (barangJadi == null) {
            throw new IllegalArgumentException("Barang jadi tidak ditemukan");
        }

        barangJadi.setActive(false);
        barangJadiDAO.updateBarangJadi(barangJadi);
        return true;
    }

    public List<BarangJadi> getBarangTerlaris(LocalDateTime startDate, LocalDateTime endDate, int limit)
            throws SQLException {
        return barangJadiDAO.getBarangJadiTerlaris(
                java.sql.Timestamp.valueOf(startDate),
                java.sql.Timestamp.valueOf(endDate),
                limit);
    }
}
