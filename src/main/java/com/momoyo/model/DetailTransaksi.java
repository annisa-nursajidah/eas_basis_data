package com.momoyo.model;

import java.math.BigDecimal;

public class DetailTransaksi {
    private int idDetailTransaksi;
    private Struk struk;
    private BarangJadi barangJadi;
    private String namaBarangSaatTransaksi;
    private int jumlahTerjual;
    private BigDecimal hargaSatuanSaatTransaksi;
    private BigDecimal subtotal;

    public DetailTransaksi() {
        this.hargaSatuanSaatTransaksi = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }

    public DetailTransaksi(Struk struk, BarangJadi barangJadi, int jumlahTerjual) {
        this.struk = struk;
        this.barangJadi = barangJadi;
        this.namaBarangSaatTransaksi = barangJadi.getNamaBarang();
        this.hargaSatuanSaatTransaksi = barangJadi.getHargaJual();
        setJumlahTerjual(jumlahTerjual);
    }

    public int getIdDetailTransaksi() {
        return idDetailTransaksi;
    }

    public void setIdDetailTransaksi(int idDetailTransaksi) {
        this.idDetailTransaksi = idDetailTransaksi;
    }

    public Struk getStruk() {
        return struk;
    }

    public void setStruk(Struk struk) {
        this.struk = struk;
    }

    public BarangJadi getBarangJadi() {
        return barangJadi;
    }

    public void setBarangJadi(BarangJadi barangJadi) {
        this.barangJadi = barangJadi;
        this.namaBarangSaatTransaksi = barangJadi.getNamaBarang();
        this.hargaSatuanSaatTransaksi = barangJadi.getHargaJual();
        hitungSubtotal();
    }

    public String getNamaBarangSaatTransaksi() {
        return namaBarangSaatTransaksi;
    }

    public void setNamaBarangSaatTransaksi(String namaBarangSaatTransaksi) {
        this.namaBarangSaatTransaksi = namaBarangSaatTransaksi;
    }

    public int getJumlahTerjual() {
        return jumlahTerjual;
    }

    public void setJumlahTerjual(int jumlahTerjual) {
        if (jumlahTerjual <= 0) {
            throw new IllegalArgumentException("Jumlah terjual harus lebih dari 0");
        }
        this.jumlahTerjual = jumlahTerjual;
        hitungSubtotal();
    }

    public BigDecimal getHargaSatuanSaatTransaksi() {
        return hargaSatuanSaatTransaksi;
    }

    public void setHargaSatuanSaatTransaksi(BigDecimal hargaSatuanSaatTransaksi) {
        if (hargaSatuanSaatTransaksi.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Harga satuan harus lebih dari 0");
        }
        this.hargaSatuanSaatTransaksi = hargaSatuanSaatTransaksi;
        hitungSubtotal();
    }

    public void setHargaSatuan(double hargaSatuan) {
        setHargaSatuanSaatTransaksi(BigDecimal.valueOf(hargaSatuan));
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    // Changed visibility from private to public
    public void hitungSubtotal() {
        this.subtotal = this.hargaSatuanSaatTransaksi
                .multiply(BigDecimal.valueOf(this.jumlahTerjual));
    }

    @Override
    public String toString() {
        return String.format("DetailTransaksi{id=%d, barang='%s', jumlah=%d, harga=Rp%,.2f, subtotal=Rp%,.2f}",
                idDetailTransaksi, namaBarangSaatTransaksi, jumlahTerjual,
                hargaSatuanSaatTransaksi, subtotal);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DetailTransaksi that = (DetailTransaksi) o;
        return idDetailTransaksi == that.idDetailTransaksi;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idDetailTransaksi);
    }
}
