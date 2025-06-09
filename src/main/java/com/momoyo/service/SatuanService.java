package com.momoyo.service;

import com.momoyo.dao.SatuanDAO;
import com.momoyo.model.Satuan;

import java.sql.SQLException;
import java.util.List;

public class SatuanService {
    private final SatuanDAO satuanDAO;

    public SatuanService() {
        this.satuanDAO = new SatuanDAO();
    }

    public boolean tambahSatuan(Satuan satuan) throws SQLException {
        satuanDAO.tambahSatuan(satuan);
        return true;
    }

    public Satuan getSatuan(int idSatuan) throws SQLException {
        return satuanDAO.getSatuanById(idSatuan);
    }

    public List<Satuan> getAllSatuan() throws SQLException {
        return satuanDAO.getAllSatuan();
    }

    public boolean updateSatuan(Satuan satuan) throws SQLException {
        satuanDAO.updateSatuan(satuan);
        return true;
    }

    public boolean hapusSatuan(int idSatuan) throws SQLException {
        if (satuanDAO.isSatuanUsedInBahanBaku(idSatuan)) {
            throw new IllegalStateException("Satuan masih digunakan dalam bahan baku");
        }
        satuanDAO.hapusSatuan(idSatuan);
        return true;
    }
}
