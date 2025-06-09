package com.momoyo.model;

import java.math.BigDecimal;

public class BahanBaku {
    private int idBahan;
    private String namaBahan;
    private Satuan satuan;
    private BigDecimal stokTersedia;
    private BigDecimal ambangBatas;

    public BahanBaku() {
        this.stokTersedia = BigDecimal.ZERO;
        this.ambangBatas = BigDecimal.ZERO;
    }

    public BahanBaku(String namaBahan, Satuan satuan, BigDecimal stokTersedia, BigDecimal ambangBatas) {
        this.namaBahan = namaBahan;
        this.satuan = satuan;
        this.stokTersedia = stokTersedia;
        this.ambangBatas = ambangBatas;
    }

    public int getIdBahan() {
        return idBahan;
    }

    public void setIdBahan(int idBahan) {
        this.idBahan = idBahan;
    }

    public String getNamaBahan() {
        return namaBahan;
    }

    public void setNamaBahan(String namaBahan) {
        this.namaBahan = namaBahan;
    }

    public Satuan getSatuan() {
        return satuan;
    }

    public void setSatuan(Satuan satuan) {
        this.satuan = satuan;
    }

    public BigDecimal getStokTersedia() {
        return stokTersedia;
    }

    public void setStokTersedia(BigDecimal stokTersedia) {
        if (stokTersedia.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Stok tidak boleh negatif");
        }
        this.stokTersedia = stokTersedia;
    }

    public BigDecimal getAmbangBatas() {
        return ambangBatas;
    }

    public void setAmbangBatas(BigDecimal ambangBatas) {
        if (ambangBatas.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Ambang batas tidak boleh negatif");
        }
        this.ambangBatas = ambangBatas;
    }

    public void tambahStok(BigDecimal jumlah) {
        if (jumlah.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Jumlah penambahan stok tidak boleh negatif");
        }
        this.stokTersedia = this.stokTersedia.add(jumlah);
    }

    public void kurangiStok(BigDecimal jumlah) {
        if (jumlah.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Jumlah pengurangan stok tidak boleh negatif");
        }
        BigDecimal stokBaru = this.stokTersedia.subtract(jumlah);
        if (stokBaru.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Stok tidak mencukupi");
        }
        this.stokTersedia = stokBaru;
    }

    public boolean isStokKritis() {
        return this.stokTersedia.compareTo(this.ambangBatas) <= 0;
    }

    public boolean isStokTersedia(double jumlah) {
        return this.stokTersedia.compareTo(BigDecimal.valueOf(jumlah)) >= 0;
    }

    @Override
    public String toString() {
        return String.format("BahanBaku{id=%d, nama='%s', stok=%.2f %s, ambang batas=%.2f %s}",
                idBahan, namaBahan, stokTersedia, satuan.getNamaSatuan(),
                ambangBatas, satuan.getNamaSatuan());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BahanBaku bahanBaku = (BahanBaku) o;
        return idBahan == bahanBaku.idBahan;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idBahan);
    }
}
