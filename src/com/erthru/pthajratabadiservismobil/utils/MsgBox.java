/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.utils;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author supriantodjamalu
 */
public class MsgBox {
    
    public static void success(String msg){
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukses");
        alert.setContentText(msg);
        alert.showAndWait();
                
    }
    
    public static void error(String msg){
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.showAndWait();
        
    }
    
    public static void warning(String msg){
        
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setContentText(msg);
        alert.showAndWait();
        
    }
    
    public static int confirm(String msg){
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Sukses");
        alert.setContentText(msg);
        Optional<ButtonType> result = alert.showAndWait();
        
        if(result.isPresent() && result.get() == ButtonType.OK){
            return 1;
        }else{
            return 0;
        }
        
    }
    
}
