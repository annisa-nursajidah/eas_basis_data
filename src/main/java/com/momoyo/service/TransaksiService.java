package com.momoyo.service;

import com.momoyo.dao.*;
import com.momoyo.model.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TransaksiService {
    private final TransaksiDAO transaksiDAO;
    private final BarangJadiDAO barangJadiDAO;

    public TransaksiService() {
        this.transaksiDAO = new TransaksiDAO();
        this.barangJadiDAO = new BarangJadiDAO();
    }

    public boolean simpanTransaksi(Struk struk) throws SQLException {
        for (DetailTransaksi detail : struk.getDetailTransaksiList()) {
            BarangJadi barang = detail.getBarangJadi();
            BarangJadi currentBarang = barangJadiDAO.getBarangJadiById(barang.getIdBarang());
            if (currentBarang == null || !currentBarang.isStokTersedia(detail.getJumlahTerjual())) {
                throw new IllegalStateException(
                        String.format("Stok %s tidak mencukupi. Tersedia: %d, Diminta: %d",
                                barang.getNamaBarang(), currentBarang != null ? currentBarang.getStokJadi() : 0,
                                detail.getJumlahTerjual()));
            }
        }

        if (struk.getNomorStruk() == null || struk.getNomorStruk().isEmpty()) {
            struk.setNomorStruk(Struk.generateNomorStruk());
        }

        if (struk.getWaktuTransaksi() == null) {
            struk.setWaktuTransaksi(LocalDateTime.now());
        }

        struk.hitungTotalKeseluruhan();

        transaksiDAO.simpanTransaksi(struk);
        return true;
    }

    public Struk getStrukByNomor(String nomorStruk) throws SQLException {
        return transaksiDAO.getStrukByNomor(nomorStruk);
    }

    public List<Struk> getLaporanPenjualan(LocalDate startDate, LocalDate endDate)
            throws SQLException {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        return transaksiDAO.getLaporanPenjualan(startDateTime, endDateTime);
    }

    public List<BarangJadi> getBarangTerlaris(LocalDate startDate, LocalDate endDate, int limit)
            throws SQLException {
        java.sql.Timestamp startTimestamp = java.sql.Timestamp.valueOf(startDate.atStartOfDay());
        java.sql.Timestamp endTimestamp = java.sql.Timestamp.valueOf(endDate.atTime(23, 59, 59));
        return barangJadiDAO.getBarangJadiTerlaris(startTimestamp, endTimestamp, limit);
    }
}
