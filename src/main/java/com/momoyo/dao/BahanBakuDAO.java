package com.momoyo.dao;

import com.momoyo.model.BahanBaku;
import com.momoyo.model.Satuan;
import com.momoyo.util.DatabaseUtil;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BahanBakuDAO {
    private final SatuanDAO satuanDAO;

    public BahanBakuDAO() {
        this.satuanDAO = new SatuanDAO();
    }

    public void tambahBahanBaku(BahanBaku bahanBaku) throws SQLException {
        String sql = "INSERT INTO BahanBaku (nama_bahan, id_satuan, stok_tersedia, ambang_batas) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, bahanBaku.getNamaBahan());
            stmt.setInt(2, bahanBaku.getSatuan().getIdSatuan());
            stmt.setBigDecimal(3, bahanBaku.getStokTersedia());
            stmt.setBigDecimal(4, bahanBaku.getAmbangBatas());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Gagal menambah bahan baku baru");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    bahanBaku.setIdBahan(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Gagal mendapatkan ID bahan baku");
                }
            }
        }
    }

    public BahanBaku getBahanBakuById(int idBahan) throws SQLException {
        String sql = "SELECT * FROM BahanBaku WHERE id_bahan = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idBahan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBahanBaku(rs);
                }
            }
        }
        return null;
    }

    public List<BahanBaku> getAllBahanBaku() throws SQLException {
        List<BahanBaku> bahanBakuList = new ArrayList<>();
        String sql = "SELECT * FROM BahanBaku ORDER BY nama_bahan";

        try (Connection conn = DatabaseUtil.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                bahanBakuList.add(mapResultSetToBahanBaku(rs));
            }
        }
        return bahanBakuList;
    }

    public List<BahanBaku> getBahanBakuKritis() throws SQLException {
        List<BahanBaku> bahanBakuKritis = new ArrayList<>();
        String sql = "SELECT * FROM BahanBaku WHERE stok_tersedia <= ambang_batas ORDER BY nama_bahan";

        try (Connection conn = DatabaseUtil.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                bahanBakuKritis.add(mapResultSetToBahanBaku(rs));
            }
        }
        return bahanBakuKritis;
    }

    public void updateBahanBaku(BahanBaku bahanBaku) throws SQLException {
        String sql = "UPDATE BahanBaku SET nama_bahan = ?, id_satuan = ?, stok_tersedia = ?, ambang_batas = ? " +
                "WHERE id_bahan = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, bahanBaku.getNamaBahan());
            stmt.setInt(2, bahanBaku.getSatuan().getIdSatuan());
            stmt.setBigDecimal(3, bahanBaku.getStokTersedia());
            stmt.setBigDecimal(4, bahanBaku.getAmbangBatas());
            stmt.setInt(5, bahanBaku.getIdBahan());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Gagal mengupdate bahan baku dengan ID " + bahanBaku.getIdBahan());
            }
        }
    }

    public void updateStokBahanBaku(int idBahan, BigDecimal stokBaru) throws SQLException {
        String sql = "UPDATE BahanBaku SET stok_tersedia = ? WHERE id_bahan = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, stokBaru);
            stmt.setInt(2, idBahan);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Gagal mengupdate stok bahan baku dengan ID " + idBahan);
            }
        }
    }

    public void hapusBahanBaku(int idBahan) throws SQLException {
        String sql = "DELETE FROM BahanBaku WHERE id_bahan = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idBahan);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Bahan baku dengan ID " + idBahan + " tidak ditemukan");
            }
        }
    }

    private BahanBaku mapResultSetToBahanBaku(ResultSet rs) throws SQLException {
        BahanBaku bahanBaku = new BahanBaku();
        bahanBaku.setIdBahan(rs.getInt("id_bahan"));
        bahanBaku.setNamaBahan(rs.getString("nama_bahan"));
        bahanBaku.setStokTersedia(rs.getBigDecimal("stok_tersedia"));
        bahanBaku.setAmbangBatas(rs.getBigDecimal("ambang_batas"));

        Satuan satuan = satuanDAO.getSatuanById(rs.getInt("id_satuan"));
        if (satuan == null) {
            throw new SQLException("Satuan tidak ditemukan untuk ID: " + rs.getInt("id_satuan"));
        }
        bahanBaku.setSatuan(satuan);

        return bahanBaku;
    }
}
