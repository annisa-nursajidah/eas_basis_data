package com.momoyo.service;

import com.momoyo.dao.BahanBakuDAO;
import com.momoyo.model.BahanBaku;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class BahanBakuService {
    private final BahanBakuDAO bahanBakuDAO;

    public BahanBakuService() {
        this.bahanBakuDAO = new BahanBakuDAO();
    }

    public boolean tambahBahanBaku(BahanBaku bahanBaku) throws SQLException {
        bahanBakuDAO.tambahBahanBaku(bahanBaku);
        return true;
    }

    public BahanBaku getBahanBaku(int idBahanBaku) throws SQLException {
        return bahanBakuDAO.getBahanBakuById(idBahanBaku);
    }

    public List<BahanBaku> getAllBahanBaku() throws SQLException {
        return bahanBakuDAO.getAllBahanBaku();
    }

    public List<BahanBaku> getBahanBakuStokKritis() throws SQLException {
        return bahanBakuDAO.getBahanBakuKritis();
    }

    public boolean updateBahanBaku(BahanBaku bahanBaku) throws SQLException {
        bahanBakuDAO.updateBahanBaku(bahanBaku);
        return true;
    }

    public boolean tambahStok(int idBahanBaku, BigDecimal jumlah) throws SQLException {
        if (jumlah.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Jumlah penambahan stok harus positif");
        }

        BahanBaku bahanBaku = getBahanBaku(idBahanBaku);
        if (bahanBaku == null) {
            throw new IllegalArgumentException("Bahan baku tidak ditemukan");
        }

        bahanBaku.tambahStok(jumlah);
        bahanBakuDAO.updateStokBahanBaku(bahanBaku.getIdBahan(), bahanBaku.getStokTersedia());
        return true;
    }

    public boolean kurangiStok(int idBahanBaku, BigDecimal jumlah) throws SQLException {
        if (jumlah.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Jumlah pengurangan stok harus positif");
        }

        BahanBaku bahanBaku = getBahanBaku(idBahanBaku);
        if (bahanBaku == null) {
            throw new IllegalArgumentException("Bahan baku tidak ditemukan");
        }

        if (bahanBaku.getStokTersedia().compareTo(jumlah) < 0) {
            throw new IllegalArgumentException(
                    String.format("Stok %s tidak mencukupi. Tersedia: %.2f %s, Diminta: %.2f %s",
                            bahanBaku.getNamaBahan(),
                            bahanBaku.getStokTersedia(),
                            bahanBaku.getSatuan().getNamaSatuan(),
                            jumlah,
                            bahanBaku.getSatuan().getNamaSatuan()));
        }

        bahanBaku.kurangiStok(jumlah);
        bahanBakuDAO.updateStokBahanBaku(bahanBaku.getIdBahan(), bahanBaku.getStokTersedia());
        return true;
    }

    public boolean hapusBahanBaku(int idBahanBaku) throws SQLException {
        bahanBakuDAO.hapusBahanBaku(idBahanBaku);
        return true;
    }
}
