/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.utils;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author supriantodjamalu
 */
public class PTHajratAbadiServisMobil extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/erthru/pthajratabadiservismobil/ui/login/LoginFXML.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setMaximized(false);
        
        root.requestFocus();        
        stage.show();
        
        startUp();
    }
    
    private void startUp(){
        LocalDB.createTable();
        
        try{
            LocalDB.con().createStatement().executeUpdate("DELETE FROM tb_selected_barang");
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    

    
}
