package com.momoyo.model;

import java.math.BigDecimal;

public class DetailResep {
    private int idDetailResep;
    private Resep resep;
    private BahanBaku bahanBaku;
    private BigDecimal kuantitasBahan;

    public DetailResep() {
        this.kuantitasBahan = BigDecimal.ZERO;
    }

    public DetailResep(Resep resep, BahanBaku bahanBaku, BigDecimal kuantitasBahan) {
        this.resep = resep;
        this.bahanBaku = bahanBaku;
        setKuantitasBahan(kuantitasBahan);
    }

    public int getIdDetailResep() {
        return idDetailResep;
    }

    public void setIdDetailResep(int idDetailResep) {
        this.idDetailResep = idDetailResep;
    }

    public Resep getResep() {
        return resep;
    }

    public void setResep(Resep resep) {
        this.resep = resep;
    }

    public BahanBaku getBahanBaku() {
        return bahanBaku;
    }

    public void setBahanBaku(BahanBaku bahanBaku) {
        this.bahanBaku = bahanBaku;
    }

    public BigDecimal getKuantitasBahan() {
        return kuantitasBahan;
    }

    public void setKuantitasBahan(BigDecimal kuantitasBahan) {
        if (kuantitasBahan.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Kuantitas bahan harus lebih dari 0");
        }
        this.kuantitasBahan = kuantitasBahan;
    }

    public void setKuantitasBahan(double kuantitasBahan) {
        setKuantitasBahan(BigDecimal.valueOf(kuantitasBahan));
    }

    public BigDecimal hitungKebutuhanBahan(int jumlahProduksi) {
        return this.kuantitasBahan.multiply(BigDecimal.valueOf(jumlahProduksi));
    }

    public boolean cekKetersediaanBahan(int jumlahProduksi) {
        BigDecimal kebutuhan = hitungKebutuhanBahan(jumlahProduksi);
        return bahanBaku.getStokTersedia().compareTo(kebutuhan) >= 0;
    }

    @Override
    public String toString() {
        return String.format("DetailResep{id=%d, bahan='%s', kuantitas=%.2f %s}",
                idDetailResep, bahanBaku.getNamaBahan(), kuantitasBahan,
                bahanBaku.getSatuan().getNamaSatuan());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DetailResep that = (DetailResep) o;
        return idDetailResep == that.idDetailResep;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idDetailResep);
    }
}
