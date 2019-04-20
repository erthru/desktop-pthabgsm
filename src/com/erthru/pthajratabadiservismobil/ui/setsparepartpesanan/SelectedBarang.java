/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.setsparepartpesanan;

/**
 *
 * @author supriantodjamalu
 */
public class SelectedBarang {
    
    private String id;
    private String code;
    private String nama;
    private String harga;
    private String kategori;

    public SelectedBarang(String id, String code, String nama, String harga, String kategori) {
        this.id = id;
        this.code = code;
        this.nama = nama;
        this.harga = harga;
        this.kategori = kategori;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }
    
    
    
}
