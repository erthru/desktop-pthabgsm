/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.riwayatpesanan;

import com.erthru.pthajratabadiservismobil.ui.pesanan.*;

/**
 *
 * @author supriantodjamalu
 */
public class Pesanan {
  
    private String id;
    private String namaLengkap;
    private String jenisServis;
    private String status;
    private String createdAt;

    public Pesanan(String id, String namaLengkap, String jenisServis, String status, String createdAt) {
        this.id = id;
        this.namaLengkap = namaLengkap;
        this.jenisServis = jenisServis;
        this.status = status;
        this.createdAt = createdAt;
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

    public String getJenisServis() {
        return jenisServis;
    }

    public void setJenisServis(String jenisServis) {
        this.jenisServis = jenisServis;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    
    
}
