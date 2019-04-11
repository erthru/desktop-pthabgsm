/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author supriantodjamalu
 */
public class MainController implements Initializable {

    /**
     * Initializes the controller class.
     */    
    
    @FXML
    private AnchorPane sidePane;
    
    @FXML
    private Button btnBeranda;
    
    @FXML
    private Button btnBarang;
    
    @FXML
    private Button btnPesanan;
    
    @FXML
    private Button btnPengguna;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            Node node = (Node)FXMLLoader.load(getClass().getResource("/com/erthru/pthajratabadiservismobil/ui/beranda/BerandaFXML.fxml"));
            
            AnchorPane.setBottomAnchor(node, Double.parseDouble("0"));
            AnchorPane.setTopAnchor(node, Double.parseDouble("0"));
            AnchorPane.setLeftAnchor(node, Double.parseDouble("0"));
            AnchorPane.setRightAnchor(node, Double.parseDouble("0"));
            
            sidePane.getChildren().clear();
            sidePane.getChildren().setAll(node);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @FXML
    private void btnBerandaClicked(){
        
        try {
            Node node = (Node)FXMLLoader.load(getClass().getResource("/com/erthru/pthajratabadiservismobil/ui/beranda/BerandaFXML.fxml"));
            
            AnchorPane.setBottomAnchor(node, Double.parseDouble("0"));
            AnchorPane.setTopAnchor(node, Double.parseDouble("0"));
            AnchorPane.setLeftAnchor(node, Double.parseDouble("0"));
            AnchorPane.setRightAnchor(node, Double.parseDouble("0"));
            
            sidePane.getChildren().clear();
            sidePane.getChildren().setAll(node);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @FXML
    private void btnBarangClicked(){
        
        
        
    }
    
    @FXML
    private void btnPesananClicked(){
        
        
        
    }
    
    @FXML
    private void btnPenggunaClicked(){
        
        
        
    }
    
}
