/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author supriantodjamalu
 */
public class LocalDB{
    
    
    public static Connection con(){
        
        Connection conn = null;
        
        try{
            
            Class.forName("org.sqlite.JDBC");
            String dbUrl = "jdbc:sqlite:src/com/erthru/pthajratabadiservismobil/utils/app.db";
            conn = DriverManager.getConnection(dbUrl);
            
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return conn;
        
    }
    
    public static void createTable(){
        
        try{
            con().createStatement().executeUpdate(""
                + "CREATE TABLE IF NOT EXISTS tb_selected_barang ("
                    + "selected_barang_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "selected_barang_code TEXT,"
                    + "selected_barang_nama TEXT,"
                    + "selected_barang_harga TEXT,"
                    + "selected_barang_kategori TEXT"
                    + ")"
                + "");
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
}
