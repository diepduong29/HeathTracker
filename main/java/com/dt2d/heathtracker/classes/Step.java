package com.dt2d.heathtracker.classes;

public class Step {
    private int id;
    private int sobuoc;
    private String ngaytao;

    public Step() {
    }

    public Step(int id, int sobuoc, String ngaytao) {
        this.id = id;
        this.sobuoc = sobuoc;
        this.ngaytao = ngaytao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSobuoc() {
        return sobuoc;
    }

    public void setSobuoc(int sobuoc) {
        this.sobuoc = sobuoc;
    }

    public String getNgaytao() {
        return ngaytao;
    }

    public void setNgaytao(String ngaytao) {
        this.ngaytao = ngaytao;
    }
}
