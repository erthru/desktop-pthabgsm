/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.pesananfilter;

import com.erthru.pthajratabadiservismobil.ui.pesanan.PesananController;
import com.erthru.pthajratabadiservismobil.utils.MsgBox;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author supriantodjamalu
 */
public class PesananFilterController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private DatePicker dateA;
    
    @FXML
    private DatePicker dateB;
    
    @FXML
    private Button btnFilter;
    
    @FXML
    private Button btnReset;
    
    private PesananController parent;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void btnFilterClicked(){
        
        if(dateA.getValue() == null || dateB.getValue() == null){
            MsgBox.error("Tanggal belum dipilih");
        }else{
            
            String a = dateA.getValue().toString();
            String b = dateB.getValue().toString();
            
            parent.setPagingDateFilter(a, b);
            
            Stage stg = (Stage) btnReset.getScene().getWindow();
            stg.close();
            
        }
        
    }
    
    @FXML
    private void btnResetClicked(){
        parent.setPaging();
        Stage stg = (Stage) btnReset.getScene().getWindow();
        stg.close();
    }
    
    public void setParent(PesananController parent){
        this.parent = parent;
    }
    
}
