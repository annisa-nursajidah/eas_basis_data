package com.momoyo.dao;

import com.momoyo.model.Satuan;
import com.momoyo.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SatuanDAO {

    public void tambahSatuan(Satuan satuan) throws SQLException {
        String sql = "INSERT INTO Satuan (nama_satuan) VALUES (?)";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, satuan.getNamaSatuan());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Gagal menambah satuan baru");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    satuan.setIdSatuan(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Gagal mendapatkan ID satuan");
                }
            }
        }
    }

    public Satuan getSatuanById(int idSatuan) throws SQLException {
        String sql = "SELECT * FROM Satuan WHERE id_satuan = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idSatuan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSatuan(rs);
                }
            }
        }
        return null;
    }

    public List<Satuan> getAllSatuan() throws SQLException {
        List<Satuan> satuanList = new ArrayList<>();
        String sql = "SELECT * FROM Satuan ORDER BY nama_satuan";

        try (Connection conn = DatabaseUtil.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                satuanList.add(mapResultSetToSatuan(rs));
            }
        }
        return satuanList;
    }

    public void updateSatuan(Satuan satuan) throws SQLException {
        String sql = "UPDATE Satuan SET nama_satuan = ? WHERE id_satuan = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, satuan.getNamaSatuan());
            stmt.setInt(2, satuan.getIdSatuan());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Gagal mengupdate satuan dengan ID " + satuan.getIdSatuan());
            }
        }
    }

    public void hapusSatuan(int idSatuan) throws SQLException {
        String sql = "DELETE FROM Satuan WHERE id_satuan = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idSatuan);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Satuan dengan ID " + idSatuan + " tidak ditemukan");
            }
        }
    }

    public boolean isSatuanUsedInBahanBaku(int idSatuan) throws SQLException {
        String sql = "SELECT COUNT(*) FROM BahanBaku WHERE id_satuan = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idSatuan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private Satuan mapResultSetToSatuan(ResultSet rs) throws SQLException {
        return new Satuan(
                rs.getInt("id_satuan"),
                rs.getString("nama_satuan"));
    }
}
