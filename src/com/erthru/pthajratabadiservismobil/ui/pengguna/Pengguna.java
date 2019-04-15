/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.pengguna;

/**
 *
 * @author supriantodjamalu
 */
public class Pengguna {
    
    private String id;
    private String namaLengkap;
    private String alamat;
    private String noHp;
    private String createdAt;
    private String updatedAt;
    private String email;
    private String pass;
    private String level;

    public Pengguna(String id, String namaLengkap, String alamat, String noHp, String createdAt, String updatedAt, String email, String pass, String level) {
        this.id = id;
        this.namaLengkap = namaLengkap;
        this.alamat = alamat;
        this.noHp = noHp;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.email = email;
        this.pass = pass;
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
    
    
    
}
