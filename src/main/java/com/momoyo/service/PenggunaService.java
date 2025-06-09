package com.momoyo.service;

import com.momoyo.dao.PenggunaDAO;
import com.momoyo.model.Pengguna;

import java.sql.SQLException;
import java.util.List;

public class PenggunaService {
    private final PenggunaDAO penggunaDAO;

    public PenggunaService() {
        this.penggunaDAO = new PenggunaDAO();
    }

    public boolean tambahPengguna(Pengguna pengguna) throws SQLException {
        if (penggunaDAO.getPenggunaByUsername(pengguna.getUsername()) != null) {
            throw new IllegalArgumentException("Username sudah digunakan");
        }

        penggunaDAO.tambahPengguna(pengguna);
        return true;
    }

    public Pengguna login(String username, String password) throws SQLException {
        Pengguna pengguna = penggunaDAO.getPenggunaByUsername(username);
        if (pengguna != null && pengguna.verifikasiPassword(password)) {
            return pengguna;
        }
        return null;
    }

    public List<Pengguna> getAllPengguna() throws SQLException {
        return penggunaDAO.getAllPengguna();
    }

    public Pengguna getPenggunaById(int idPengguna) throws SQLException {
        return penggunaDAO.getPenggunaById(idPengguna);
    }

    public boolean updatePengguna(Pengguna pengguna) throws SQLException {
        Pengguna existing = penggunaDAO.getPenggunaByUsername(pengguna.getUsername());
        if (existing != null && existing.getIdPengguna() != pengguna.getIdPengguna()) {
            throw new IllegalArgumentException("Username sudah digunakan");
        }

        penggunaDAO.updatePengguna(pengguna);
        return true;
    }

    public boolean hapusPengguna(int idPengguna) throws SQLException {
        penggunaDAO.hapusPengguna(idPengguna);
        return true;
    }
}
