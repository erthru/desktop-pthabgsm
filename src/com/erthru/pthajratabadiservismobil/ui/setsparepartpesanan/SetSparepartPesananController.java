/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.setsparepartpesanan;

import com.erthru.pthajratabadiservismobil.ui.pesanan.PesananController;
import com.erthru.pthajratabadiservismobil.ui.pesanandetail.PesananDetailController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author supriantodjamalu
 */
public class SetSparepartPesananController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    private PesananController parent;
    private PesananDetailController parent1;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }  
    
    public void setParent(PesananController parent, PesananDetailController parent1){
        this.parent = parent;
        this.parent1 = parent1;
    }
    
}
