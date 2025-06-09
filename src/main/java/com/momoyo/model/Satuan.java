package com.momoyo.model;

public class Satuan {
    private int idSatuan;
    private String namaSatuan;

    public Satuan() {
    }

    public Satuan(String namaSatuan) {
        this.namaSatuan = namaSatuan;
    }

    public Satuan(int idSatuan, String namaSatuan) {
        this.idSatuan = idSatuan;
        this.namaSatuan = namaSatuan;
    }

    public int getIdSatuan() {
        return idSatuan;
    }

    public void setIdSatuan(int idSatuan) {
        this.idSatuan = idSatuan;
    }

    public String getNamaSatuan() {
        return namaSatuan;
    }

    public void setNamaSatuan(String namaSatuan) {
        this.namaSatuan = namaSatuan;
    }

    @Override
    public String toString() {
        return String.format("Satuan{id=%d, nama='%s'}",
                idSatuan, namaSatuan);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Satuan satuan = (Satuan) o;
        return idSatuan == satuan.idSatuan;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idSatuan);
    }
}
