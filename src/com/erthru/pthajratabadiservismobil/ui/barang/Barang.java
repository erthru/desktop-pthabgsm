/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.barang;

/**
 *
 * @author supriantodjamalu
 */
public class Barang {
    
    private String id;
    private String nama;
    private String harga;

    public Barang(String id, String nama, String harga) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    
    
    
}
