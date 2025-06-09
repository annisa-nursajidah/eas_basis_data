package com.momoyo.dao;

import com.momoyo.model.*;
import com.momoyo.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransaksiDAO {
    private final PenggunaDAO penggunaDAO;
    private final BarangJadiDAO barangJadiDAO;

    public TransaksiDAO() {
        this.penggunaDAO = new PenggunaDAO();
        this.barangJadiDAO = new BarangJadiDAO();
    }

    public void simpanTransaksi(Struk struk) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            String sqlStruk = "INSERT INTO Struk (nomor_struk, waktu_transaksi, id_pengguna_kasir, total_keseluruhan) "
                    +
                    "VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlStruk)) {
                stmt.setString(1, struk.getNomorStruk());
                stmt.setTimestamp(2, Timestamp.valueOf(struk.getWaktuTransaksi()));
                stmt.setInt(3, struk.getKasir().getIdPengguna());
                stmt.setBigDecimal(4, struk.getTotalKeseluruhan());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Gagal menyimpan struk");
                }
            }

            String sqlDetail = "INSERT INTO DetailTransaksi (nomor_struk, id_barang, nama_barang_saat_transaksi, " +
                    "jumlah_terjual, harga_satuan_saat_transaksi, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlDetail)) {
                for (DetailTransaksi detail : struk.getDetailTransaksiList()) {
                    stmt.setString(1, struk.getNomorStruk());
                    stmt.setInt(2, detail.getBarangJadi().getIdBarang());
                    stmt.setString(3, detail.getNamaBarangSaatTransaksi());
                    stmt.setInt(4, detail.getJumlahTerjual());
                    stmt.setBigDecimal(5, detail.getHargaSatuanSaatTransaksi());
                    stmt.setBigDecimal(6, detail.getSubtotal());
                    stmt.addBatch();

                    BarangJadi currentBarang = barangJadiDAO.getBarangJadiById(detail.getBarangJadi().getIdBarang());
                    if (currentBarang == null) {
                        throw new SQLException("Barang jadi dengan ID " + detail.getBarangJadi().getIdBarang() + " tidak ditemukan saat update stok.");
                    }
                    int newStok = currentBarang.getStokJadi() - detail.getJumlahTerjual();
                    if (newStok < 0) {
                        throw new SQLException("Stok barang " + currentBarang.getNamaBarang() + " tidak mencukupi.");
                    }
                    barangJadiDAO.updateStokBarangJadi(currentBarang.getIdBarang(), newStok);
                }
                stmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new SQLException("Error saat rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public Struk getStrukByNomor(String nomorStruk) throws SQLException {
        String sql = "SELECT * FROM Struk WHERE nomor_struk = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomorStruk);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Struk struk = mapResultSetToStruk(rs);
                    struk.setDetailTransaksiList(getDetailTransaksiByNomorStruk(nomorStruk));
                    return struk;
                }
            }
        }
        return null;
    }

    public List<Struk> getLaporanPenjualan(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<Struk> strukList = new ArrayList<>();
        String sql = "SELECT * FROM Struk WHERE waktu_transaksi BETWEEN ? AND ? ORDER BY waktu_transaksi DESC";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Struk struk = mapResultSetToStruk(rs);
                    struk.setDetailTransaksiList(getDetailTransaksiByNomorStruk(struk.getNomorStruk()));
                    strukList.add(struk);
                }
            }
        }
        return strukList;
    }

    private List<DetailTransaksi> getDetailTransaksiByNomorStruk(String nomorStruk) throws SQLException {
        List<DetailTransaksi> detailList = new ArrayList<>();
        String sql = "SELECT * FROM DetailTransaksi WHERE nomor_struk = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomorStruk);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    detailList.add(mapResultSetToDetailTransaksi(rs));
                }
            }
        }
        return detailList;
    }

    private Struk mapResultSetToStruk(ResultSet rs) throws SQLException {
        Struk struk = new Struk();
        struk.setNomorStruk(rs.getString("nomor_struk"));
        struk.setWaktuTransaksi(rs.getTimestamp("waktu_transaksi").toLocalDateTime());
        struk.setTotalKeseluruhan(rs.getBigDecimal("total_keseluruhan"));

        Pengguna kasir = penggunaDAO.getPenggunaById(rs.getInt("id_pengguna_kasir"));
        if (kasir == null) {
            throw new SQLException("Kasir tidak ditemukan untuk ID: " + rs.getInt("id_pengguna_kasir"));
        }
        struk.setKasir(kasir);

        return struk;
    }

    private DetailTransaksi mapResultSetToDetailTransaksi(ResultSet rs) throws SQLException {
        DetailTransaksi detail = new DetailTransaksi();
        detail.setIdDetailTransaksi(rs.getInt("id_detail_transaksi"));
        detail.setNamaBarangSaatTransaksi(rs.getString("nama_barang_saat_transaksi"));
        detail.setJumlahTerjual(rs.getInt("jumlah_terjual"));
        detail.setHargaSatuanSaatTransaksi(rs.getBigDecimal("harga_satuan_saat_transaksi"));

        BarangJadi barangJadi = barangJadiDAO.getBarangJadiById(rs.getInt("id_barang"));
        if (barangJadi == null) {
            throw new SQLException("Barang jadi tidak ditemukan untuk ID: " + rs.getInt("id_barang") + " dalam detail transaksi.");
        }
        detail.setBarangJadi(barangJadi);

        return detail;
    }
}
