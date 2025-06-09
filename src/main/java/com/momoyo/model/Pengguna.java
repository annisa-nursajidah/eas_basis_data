package com.momoyo.model;

public class Pengguna {
    private int idPengguna;
    private String username;
    private String password;
    private Role peran;

    public enum Role {
        ADMIN,
        KASIR
    }

    public Pengguna() {}

    public Pengguna(String username, String password, Role peran) {
        this.username = username;
        this.setPassword(password);
        this.peran = peran;
    }

    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null && !password.isEmpty()) {
            this.password = password;
        }
    }

    public Role getPeran() {
        return peran;
    }

    public void setPeran(Role peran) {
        this.peran = peran;
    }

    public void setRole(String roleString) {
        try {
            this.peran = Role.valueOf(roleString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + roleString);
        }
    }

    public boolean verifikasiPassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    public boolean isAdmin() {
        return this.peran == Role.ADMIN;
    }

    @Override
    public String toString() {
        return String.format("Pengguna{id=%d, username='%s', peran=%s}",
                idPengguna, username, peran);
    }
}
