package com.dt2d.heathtracker.classes;

public class Water {
    private int id;
    private int luongnuoc;
    private String ngaytao;

    public Water() {
    }

    public Water(int id, int luongnuoc, String ngaytao) {
        this.id = id;
        this.luongnuoc = luongnuoc;
        this.ngaytao = ngaytao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLuongnuoc() {
        return luongnuoc;
    }

    public void setLuongnuoc(int luongnuoc) {
        this.luongnuoc = luongnuoc;
    }

    public String getNgaytao() {
        return ngaytao;
    }

    public void setNgaytao(String ngaytao) {
        this.ngaytao = ngaytao;
    }
}
