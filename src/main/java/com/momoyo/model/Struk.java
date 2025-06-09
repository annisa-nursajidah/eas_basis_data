package com.momoyo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Struk {
    private String nomorStruk;
    private LocalDateTime waktuTransaksi;
    private Pengguna kasir;
    private BigDecimal totalKeseluruhan;
    private List<DetailTransaksi> detailTransaksiList;

    public Struk() {
        this.waktuTransaksi = LocalDateTime.now();
        this.totalKeseluruhan = BigDecimal.ZERO;
        this.detailTransaksiList = new ArrayList<>();
    }

    public Struk(String nomorStruk, Pengguna kasir) {
        this();
        this.nomorStruk = nomorStruk;
        this.kasir = kasir;
    }

    public String getNomorStruk() {
        return nomorStruk;
    }

    public void setNomorStruk(String nomorStruk) {
        this.nomorStruk = nomorStruk;
    }

    public LocalDateTime getWaktuTransaksi() {
        return waktuTransaksi;
    }

    public void setWaktuTransaksi(LocalDateTime waktuTransaksi) {
        this.waktuTransaksi = waktuTransaksi;
    }

    public Pengguna getKasir() {
        return kasir;
    }

    public void setKasir(Pengguna kasir) {
        this.kasir = kasir;
    }

    public BigDecimal getTotalKeseluruhan() {
        return totalKeseluruhan;
    }

    public void setTotalKeseluruhan(BigDecimal totalKeseluruhan) {
        this.totalKeseluruhan = totalKeseluruhan;
    }

    public void setTotalTransaksi(double totalTransaksi) {
        setTotalKeseluruhan(BigDecimal.valueOf(totalTransaksi));
    }

    public BigDecimal getTotalTransaksi() {
        return getTotalKeseluruhan();
    }

    public List<DetailTransaksi> getDetailTransaksiList() {
        return new ArrayList<>(detailTransaksiList);
    }

    public void setDetailTransaksiList(List<DetailTransaksi> detailTransaksiList) {
        this.detailTransaksiList = new ArrayList<>(detailTransaksiList);
        hitungTotalKeseluruhan();
    }

    public void tambahDetailTransaksi(DetailTransaksi detail) {
        detailTransaksiList.add(detail);
        hitungTotalKeseluruhan();
    }

    public void hapusDetailTransaksi(DetailTransaksi detail) {
        detailTransaksiList.remove(detail);
        hitungTotalKeseluruhan();
    }

    public void hitungTotalKeseluruhan() {
        this.totalKeseluruhan = detailTransaksiList.stream()
                .map(DetailTransaksi::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static String generateNomorStruk() {
        return String.format("STR-%tY%<tm%<td-%<tH%<tM%<tS-%03d",
                LocalDateTime.now(),
                (int) (Math.random() * 1000));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Struk Penjualan\nNomor: %s\nWaktu: %s\nKasir: %s\n\n",
                nomorStruk, waktuTransaksi, kasir.getUsername()));

        sb.append("Detail Pembelian:\n");
        for (DetailTransaksi detail : detailTransaksiList) {
            sb.append(String.format("%s x%d @ Rp%,.2f = Rp%,.2f\n",
                    detail.getNamaBarangSaatTransaksi(),
                    detail.getJumlahTerjual(),
                    detail.getHargaSatuanSaatTransaksi(),
                    detail.getSubtotal()));
        }

        sb.append(String.format("\nTotal: Rp%,.2f", totalKeseluruhan));
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Struk struk = (Struk) o;
        return nomorStruk.equals(struk.nomorStruk);
    }

    @Override
    public int hashCode() {
        return nomorStruk.hashCode();
    }
}
