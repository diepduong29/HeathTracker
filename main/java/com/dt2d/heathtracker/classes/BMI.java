package com.dt2d.heathtracker.classes;

public class BMI {
    private int id;
    private float cannang;
    private float chieucao;
    private float bmi;
    private String ngaytao;

    public BMI() {
    }

    public BMI(int id, float cannang, float chieucao, float bmi, String ngaytao) {
        this.id = id;
        this.cannang = cannang;
        this.chieucao = chieucao;
        this.bmi = bmi;
        this.ngaytao = ngaytao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getCannang() {
        return cannang;
    }

    public void setCannang(float cannang) {
        this.cannang = cannang;
    }

    public float getChieucao() {
        return chieucao;
    }

    public void setChieucao(float chieucao) {
        this.chieucao = chieucao;
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public String getNgaytao() {
        return ngaytao;
    }

    public void setNgaytao(String ngaytao) {
        this.ngaytao = ngaytao;
    }
}
