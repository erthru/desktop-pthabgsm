/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.addsparepart;

import com.erthru.pthajratabadiservismobil.ui.pesanan.PesananController;
import com.erthru.pthajratabadiservismobil.ui.pesanandetail.PesananDetailController;
import com.erthru.pthajratabadiservismobil.ui.setsparepartpesanan.Barang;
import com.erthru.pthajratabadiservismobil.ui.setsparepartpesanan.SetSparepartPesananController;
import com.erthru.pthajratabadiservismobil.utils.MsgBox;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author supriantodjamalu
 */
public class AddSparePartController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private Label lbNamaBarang;
    
    @FXML
    private Label lbHarga;
    
    @FXML
    private TextField txJumlah;
    
    @FXML
    private Button btnTambah;
    
    public static Barang barang;
    
    private PesananController parent;
    private PesananDetailController parent1;
    private SetSparepartPesananController parent2;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lbNamaBarang.setText(barang.getNama()+" ("+barang.getKategori()+")");
        lbHarga.setText(barang.getHarga());
    }    
    
    @FXML
    private void btnTambahClicked(){
        
        if(txJumlah.getText().isEmpty()){
            MsgBox.error("Jumlah harus diisi.");
        }else{
            
            int jumlah = Integer.parseInt(txJumlah.getText());
            
            ArrayList<Barang> selectedBarangArr = new ArrayList<>();
            
            for(int i=0; i<jumlah; i++){
                selectedBarangArr.add(new Barang(
                        barang.getId(),
                        barang.getNama(),
                        barang.getHarga(),
                        barang.getKategori()
                ));
                
            }
            
            
            parent2.addSelectedBarang(selectedBarangArr);
            
            Stage stg = (Stage) btnTambah.getScene().getWindow();
            stg.close();
            
        }
        
    }
    
    public void setParent(PesananController parent, PesananDetailController parent1, SetSparepartPesananController parent2){
        this.parent = parent;
        this.parent1 = parent1;
        this.parent2 = parent2;
    }
    
}
