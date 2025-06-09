package com.momoyo.dao;

import com.momoyo.model.*;
import com.momoyo.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResepDAO {
    private final BarangJadiDAO barangJadiDAO;
    private final BahanBakuDAO bahanBakuDAO;

    public ResepDAO() {
        this.barangJadiDAO = new BarangJadiDAO();
        this.bahanBakuDAO = new BahanBakuDAO();
    }

    public void tambahResep(Resep resep) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            String sqlResep = "INSERT INTO Resep (id_barang, deskripsi_resep) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlResep, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, resep.getBarangJadi().getIdBarang());
                stmt.setString(2, resep.getDeskripsiResep());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Gagal menambah resep baru");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        resep.setIdResep(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Gagal mendapatkan ID resep");
                    }
                }
            }

            String sqlDetail = "INSERT INTO DetailResep (id_resep, id_bahan, kuantitas_bahan) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlDetail)) {
                for (DetailResep detail : resep.getDetailResepList()) {
                    stmt.setInt(1, resep.getIdResep());
                    stmt.setInt(2, detail.getBahanBaku().getIdBahan());
                    stmt.setBigDecimal(3, detail.getKuantitasBahan());
                    stmt.addBatch();
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

    public Resep getResepById(int idResep) throws SQLException {
        String sql = "SELECT * FROM Resep WHERE id_resep = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idResep);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Resep resep = mapResultSetToResep(rs);
                    resep.setDetailResepList(getDetailResepByIdResep(idResep));
                    return resep;
                }
            }
        }
        return null;
    }

    public Resep getResepByBarangJadi(int idBarang) throws SQLException {
        String sql = "SELECT * FROM Resep WHERE id_barang = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idBarang);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Resep resep = mapResultSetToResep(rs);
                    resep.setDetailResepList(getDetailResepByIdResep(resep.getIdResep()));
                    return resep;
                }
            }
        }
        return null;
    }

    public List<Resep> getAllResep() throws SQLException {
        List<Resep> resepList = new ArrayList<>();
        String sql = "SELECT * FROM Resep";

        try (Connection conn = DatabaseUtil.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Resep resep = mapResultSetToResep(rs);
                resep.setDetailResepList(getDetailResepByIdResep(resep.getIdResep()));
                resepList.add(resep);
            }
        }
        return resepList;
    }

    public void updateResep(Resep resep) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            String sqlResep = "UPDATE Resep SET deskripsi_resep = ? WHERE id_resep = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlResep)) {
                stmt.setString(1, resep.getDeskripsiResep());
                stmt.setInt(2, resep.getIdResep());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Gagal mengupdate resep dengan ID " + resep.getIdResep());
                }
            }

            String sqlDeleteDetail = "DELETE FROM DetailResep WHERE id_resep = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteDetail)) {
                stmt.setInt(1, resep.getIdResep());
                stmt.executeUpdate();
            }

            String sqlDetail = "INSERT INTO DetailResep (id_resep, id_bahan, kuantitas_bahan) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlDetail)) {
                for (DetailResep detail : resep.getDetailResepList()) {
                    stmt.setInt(1, resep.getIdResep());
                    stmt.setInt(2, detail.getBahanBaku().getIdBahan());
                    stmt.setBigDecimal(3, detail.getKuantitasBahan());
                    stmt.addBatch();
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

    public void hapusResep(int idResep) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            String sqlDeleteDetail = "DELETE FROM DetailResep WHERE id_resep = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteDetail)) {
                stmt.setInt(1, idResep);
                stmt.executeUpdate();
            }

            String sqlResep = "DELETE FROM Resep WHERE id_resep = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlResep)) {
                stmt.setInt(1, idResep);
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Resep dengan ID " + idResep + " tidak ditemukan");
                }
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

    private List<DetailResep> getDetailResepByIdResep(int idResep) throws SQLException {
        List<DetailResep> detailList = new ArrayList<>();
        String sql = "SELECT * FROM DetailResep WHERE id_resep = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idResep);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    detailList.add(mapResultSetToDetailResep(rs));
                }
            }
        }
        return detailList;
    }

    private Resep mapResultSetToResep(ResultSet rs) throws SQLException {
        Resep resep = new Resep();
        resep.setIdResep(rs.getInt("id_resep"));
        resep.setDeskripsiResep(rs.getString("deskripsi_resep"));

        BarangJadi barangJadi = barangJadiDAO.getBarangJadiById(rs.getInt("id_barang"));
        if (barangJadi == null) {
            throw new SQLException("Barang jadi tidak ditemukan untuk ID: " + rs.getInt("id_barang"));
        }
        resep.setBarangJadi(barangJadi);

        return resep;
    }

    private DetailResep mapResultSetToDetailResep(ResultSet rs) throws SQLException {
        DetailResep detail = new DetailResep();
        detail.setIdDetailResep(rs.getInt("id_detail_resep"));
        detail.setKuantitasBahan(rs.getBigDecimal("kuantitas_bahan"));

        BahanBaku bahanBaku = bahanBakuDAO.getBahanBakuById(rs.getInt("id_bahan"));
        if (bahanBaku == null) {
            throw new SQLException("Bahan baku tidak ditemukan untuk ID: " + rs.getInt("id_bahan"));
        }
        detail.setBahanBaku(bahanBaku);

        return detail;
    }
}
