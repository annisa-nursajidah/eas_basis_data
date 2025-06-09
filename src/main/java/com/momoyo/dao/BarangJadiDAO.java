package com.momoyo.dao;

import com.momoyo.model.BarangJadi;
import com.momoyo.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BarangJadiDAO {

    public void tambahBarangJadi(BarangJadi barangJadi) throws SQLException {
        String sql = "INSERT INTO BarangJadi (nama_barang, harga_jual, stok_jadi, is_active) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, barangJadi.getNamaBarang());
            stmt.setBigDecimal(2, barangJadi.getHargaJual());
            stmt.setInt(3, barangJadi.getStokJadi());
            stmt.setBoolean(4, barangJadi.isActive());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Gagal menambah barang jadi baru");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    barangJadi.setIdBarang(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Gagal mendapatkan ID barang jadi");
                }
            }
        }
    }

    public BarangJadi getBarangJadiById(int idBarang) throws SQLException {
        String sql = "SELECT * FROM BarangJadi WHERE id_barang = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idBarang);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBarangJadi(rs);
                }
            }
        }
        return null;
    }

    public List<BarangJadi> getAllBarangJadi() throws SQLException {
        List<BarangJadi> barangJadiList = new ArrayList<>();
        String sql = "SELECT * FROM BarangJadi ORDER BY nama_barang";

        try (Connection conn = DatabaseUtil.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                barangJadiList.add(mapResultSetToBarangJadi(rs));
            }
        }
        return barangJadiList;
    }

    public List<BarangJadi> getBarangJadiAktif() throws SQLException {
        List<BarangJadi> barangJadiList = new ArrayList<>();
        String sql = "SELECT * FROM BarangJadi WHERE is_active = true ORDER BY nama_barang";

        try (Connection conn = DatabaseUtil.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                barangJadiList.add(mapResultSetToBarangJadi(rs));
            }
        }
        return barangJadiList;
    }

    public void updateBarangJadi(BarangJadi barangJadi) throws SQLException {
        String sql = "UPDATE BarangJadi SET nama_barang = ?, harga_jual = ?, stok_jadi = ?, is_active = ? " +
                "WHERE id_barang = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, barangJadi.getNamaBarang());
            stmt.setBigDecimal(2, barangJadi.getHargaJual());
            stmt.setInt(3, barangJadi.getStokJadi());
            stmt.setBoolean(4, barangJadi.isActive());
            stmt.setInt(5, barangJadi.getIdBarang());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Gagal mengupdate barang jadi dengan ID " + barangJadi.getIdBarang());
            }
        }
    }

    public void updateStokBarangJadi(int idBarang, int stokBaru) throws SQLException {
        String sql = "UPDATE BarangJadi SET stok_jadi = ? WHERE id_barang = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, stokBaru);
            stmt.setInt(2, idBarang);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Gagal mengupdate stok barang jadi dengan ID " + idBarang);
            }
        }
    }

    public void nonaktifkanBarangJadi(int idBarang) throws SQLException {
        String sql = "UPDATE BarangJadi SET is_active = false WHERE id_barang = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idBarang);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Gagal menonaktifkan barang jadi dengan ID " + idBarang);
            }
        }
    }

    public List<BarangJadi> getBarangJadiTerlaris(Timestamp startDate, Timestamp endDate, int limit) throws SQLException {
        String sql = "SELECT bj.*, SUM(dt.jumlah_terjual) as total_terjual " +
                "FROM BarangJadi bj " +
                "JOIN DetailTransaksi dt ON bj.id_barang = dt.id_barang " +
                "JOIN Struk s ON dt.nomor_struk = s.nomor_struk " +
                "WHERE s.waktu_transaksi BETWEEN ? AND ? " +
                "GROUP BY bj.id_barang, bj.nama_barang, bj.harga_jual, bj.stok_jadi, bj.is_active " +
                "ORDER BY total_terjual DESC " +
                "LIMIT ?";

        List<BarangJadi> barangTerlaris = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, startDate);
            stmt.setTimestamp(2, endDate);
            stmt.setInt(3, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BarangJadi barangJadi = mapResultSetToBarangJadi(rs);
                    barangJadi.setJumlahTerjual(rs.getInt("total_terjual"));
                    barangTerlaris.add(barangJadi);
                }
            }
        }
        return barangTerlaris;
    }

    private BarangJadi mapResultSetToBarangJadi(ResultSet rs) throws SQLException {
        BarangJadi barangJadi = new BarangJadi();
        barangJadi.setIdBarang(rs.getInt("id_barang"));
        barangJadi.setNamaBarang(rs.getString("nama_barang"));
        barangJadi.setHargaJual(rs.getBigDecimal("harga_jual"));
        barangJadi.setStokJadi(rs.getInt("stok_jadi"));
        barangJadi.setActive(rs.getBoolean("is_active"));
        return barangJadi;
    }
}
