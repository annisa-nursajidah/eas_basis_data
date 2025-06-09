package com.momoyo.model;

import java.util.ArrayList;
import java.util.List;

public class Resep {
    private int idResep;
    private BarangJadi barangJadi;
    private String deskripsiResep;
    private List<DetailResep> detailResepList;

    public Resep() {
        this.detailResepList = new ArrayList<>();
    }

    public Resep(BarangJadi barangJadi, String deskripsiResep) {
        this.barangJadi = barangJadi;
        this.deskripsiResep = deskripsiResep;
        this.detailResepList = new ArrayList<>();
    }

    public int getIdResep() {
        return idResep;
    }

    public void setIdResep(int idResep) {
        this.idResep = idResep;
    }

    public BarangJadi getBarangJadi() {
        return barangJadi;
    }

    public void setBarangJadi(BarangJadi barangJadi) {
        this.barangJadi = barangJadi;
    }

    public String getDeskripsiResep() {
        return deskripsiResep;
    }

    public void setDeskripsiResep(String deskripsiResep) {
        this.deskripsiResep = deskripsiResep;
    }

    public String getDeskripsi() {
        return getDeskripsiResep();
    }

    public void setDeskripsi(String deskripsi) {
        setDeskripsiResep(deskripsi);
    }

    public List<DetailResep> getDetailResepList() {
        return new ArrayList<>(detailResepList);
    }

    public void tambahDetailResep(DetailResep detailResep) {
        boolean exists = detailResepList.stream()
                .anyMatch(d -> d.getBahanBaku().getIdBahan() == detailResep.getBahanBaku().getIdBahan());
        if (!exists) {
            detailResepList.add(detailResep);
        }
    }

    public void hapusDetailResep(DetailResep detailResep) {
        detailResepList.remove(detailResep);
    }

    public void setDetailResepList(List<DetailResep> detailResepList) {
        this.detailResepList = new ArrayList<>(detailResepList);
    }

    public boolean cekKetersediaanBahan(int jumlahProduksi) {
        for (DetailResep detail : detailResepList) {
            BahanBaku bahan = detail.getBahanBaku();
            if (bahan.getStokTersedia().compareTo(detail.getKuantitasBahan()
                    .multiply(java.math.BigDecimal.valueOf(jumlahProduksi))) < 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Resep untuk %s (ID: %d)\n",
                barangJadi.getNamaBarang(), idResep));
        if (deskripsiResep != null && !deskripsiResep.isEmpty()) {
            sb.append("Deskripsi: ").append(deskripsiResep).append("\n");
        }
        sb.append("Bahan-bahan:\n");
        for (DetailResep detail : detailResepList) {
            sb.append(String.format("- %.2f %s %s\n",
                    detail.getKuantitasBahan(),
                    detail.getBahanBaku().getSatuan().getNamaSatuan(),
                    detail.getBahanBaku().getNamaBahan()));
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Resep resep = (Resep) o;
        return idResep == resep.idResep;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idResep);
    }
}
