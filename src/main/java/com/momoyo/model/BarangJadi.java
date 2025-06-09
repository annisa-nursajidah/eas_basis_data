package com.momoyo.model;

import java.math.BigDecimal;

public class BarangJadi {
    private int idBarang;
    private String namaBarang;
    private BigDecimal hargaJual;
    private int stokJadi;
    private boolean isActive;

    private transient int jumlahTerjual;

    public BarangJadi() {
        this.hargaJual = BigDecimal.ZERO;
        this.stokJadi = 0;
        this.isActive = true;
    }

    public BarangJadi(String namaBarang, BigDecimal hargaJual) {
        this.namaBarang = namaBarang;
        this.hargaJual = hargaJual;
        this.stokJadi = 0;
        this.isActive = true;
    }

    public int getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(int idBarang) {
        this.idBarang = idBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public BigDecimal getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(BigDecimal hargaJual) {
        if (hargaJual.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Harga jual tidak boleh negatif");
        }
        this.hargaJual = hargaJual;
    }

    public void setHargaJual(double hargaJual) {
        setHargaJual(BigDecimal.valueOf(hargaJual));
    }

    public int getStokJadi() {
        return stokJadi;
    }

    public void setStokJadi(int stokJadi) {
        if (stokJadi < 0) {
            throw new IllegalArgumentException("Stok tidak boleh negatif");
        }
        this.stokJadi = stokJadi;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getJumlahTerjual() {
        return jumlahTerjual;
    }

    public void setJumlahTerjual(int jumlahTerjual) {
        this.jumlahTerjual = jumlahTerjual;
    }

    public void tambahStok(int jumlah) {
        if (jumlah < 0) {
            throw new IllegalArgumentException("Jumlah penambahan stok tidak boleh negatif");
        }
        this.stokJadi += jumlah;
    }

    public void kurangiStok(int jumlah) {
        if (jumlah < 0) {
            throw new IllegalArgumentException("Jumlah pengurangan stok tidak boleh negatif");
        }
        if (this.stokJadi < jumlah) {
            throw new IllegalStateException("Stok tidak mencukupi");
        }
        this.stokJadi -= jumlah;
    }

    public boolean isStokTersedia(int jumlah) {
        return this.stokJadi >= jumlah;
    }

    public BigDecimal hitungTotalHarga(int jumlah) {
        return this.hargaJual.multiply(BigDecimal.valueOf(jumlah));
    }

    @Override
    public String toString() {
        return String.format("BarangJadi{id=%d, nama='%s', harga=Rp%,.2f, stok=%d, aktif=%b}",
                idBarang, namaBarang, hargaJual, stokJadi, isActive);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BarangJadi barangJadi = (BarangJadi) o;
        return idBarang == barangJadi.idBarang;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idBarang);
    }
}
