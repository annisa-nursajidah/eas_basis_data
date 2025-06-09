package com.momoyo.dao;

import com.momoyo.model.Pengguna;
import com.momoyo.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PenggunaDAO {

    public void tambahPengguna(Pengguna pengguna) throws SQLException {
        String sql = "INSERT INTO Pengguna (username, password, peran) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, pengguna.getUsername());
            stmt.setString(2, pengguna.getPassword());
            stmt.setString(3, pengguna.getPeran().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Gagal membuat pengguna baru");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pengguna.setIdPengguna(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Gagal mendapatkan ID pengguna");
                }
            }
        }
    }

    public Pengguna getPenggunaByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM Pengguna WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPengguna(rs);
                }
            }
        }
        return null;
    }

    public Pengguna getPenggunaById(int idPengguna) throws SQLException {
        String sql = "SELECT * FROM Pengguna WHERE id_pengguna = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPengguna);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPengguna(rs);
                }
            }
        }
        return null;
    }

    public List<Pengguna> getAllPengguna() throws SQLException {
        List<Pengguna> penggunaList = new ArrayList<>();
        String sql = "SELECT * FROM Pengguna";

        try (Connection conn = DatabaseUtil.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                penggunaList.add(mapResultSetToPengguna(rs));
            }
        }
        return penggunaList;
    }

    public void hapusPengguna(int idPengguna) throws SQLException {
        String sql = "DELETE FROM Pengguna WHERE id_pengguna = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPengguna);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Pengguna dengan ID " + idPengguna + " tidak ditemukan");
            }
        }
    }

    public void updatePengguna(Pengguna pengguna) throws SQLException {
        String sql = "UPDATE Pengguna SET username = ?, password = ?, peran = ? WHERE id_pengguna = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pengguna.getUsername());
            stmt.setString(2, pengguna.getPassword());
            stmt.setString(3, pengguna.getPeran().name());
            stmt.setInt(4, pengguna.getIdPengguna());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Gagal mengupdate pengguna dengan ID " + pengguna.getIdPengguna());
            }
        }
    }

    private Pengguna mapResultSetToPengguna(ResultSet rs) throws SQLException {
        Pengguna pengguna = new Pengguna();
        pengguna.setIdPengguna(rs.getInt("id_pengguna"));
        pengguna.setUsername(rs.getString("username"));
        pengguna.setPassword(rs.getString("password"));
        pengguna.setPeran(Pengguna.Role.valueOf(rs.getString("peran")));
        return pengguna;
    }
}
