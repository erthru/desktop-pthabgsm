/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.utils;

import com.erthru.pthajratabadiservismobil.ui.loading.LoadingController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author supriantodjamalu
 */
public class Loading {
    
    Stage stage;
        
    public void show(){
        
        try{
            stage = new Stage();
        
            Parent root = FXMLLoader.load(LoadingController.class.getResource("LoadingFXML.fxml"));

            stage.setScene(new Scene(root));
            stage.setTitle("Pegawai Detail");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void dismiss(){
        
        if(stage.isShowing())
            stage.close();
        
    }
    
}
