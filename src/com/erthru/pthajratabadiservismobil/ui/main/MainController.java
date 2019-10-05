/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.main;

import com.erthru.pthajratabadiservismobil.ui.pengguna.PenggunaController;
import com.erthru.pthajratabadiservismobil.utils.ApiEndPoint;
import com.erthru.pthajratabadiservismobil.utils.PreferenceUser;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

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
    
    @FXML
    private Button btnLogout;
    
    @FXML
    private Button btnLaporan;
    
    @FXML
    private Button btnTeknisi;
    
    @FXML
    private Button btnRiwayatPesanan;
    
    @FXML
    private Button btnNotifikasi;
    
    private Boolean isCountNotifikasiDone = false;
    
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
        
        btnNotifikasi.setText("NOTIFIKASI (0)");
        
        reCheckNotifikasi();
        
    }
    
    private void reCheckNotifikasi(){
        
        isCountNotifikasiDone = true;
        
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            
            @Override
            public void run() {
                
                System.out.println("check notif");
                    
                isCountNotifikasiDone = false;                    
                countNotifikasi();
                
            }
            
        }, 0, 1000);
    }
    
    private void countNotifikasi(){
        
        class Work extends Task<Void>{

            @Override
            protected Void call() throws Exception {
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.DAFTAR_NOTIFIKASI_ADMIN);

                ResponseHandler<JSONObject> responseHandler = new ResponseHandler<JSONObject>() {

                    @Override
                    public JSONObject handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                        int status = response.getStatusLine().getStatusCode();
                        if (status >= 200 && status < 300) {
                            HttpEntity entity = response.getEntity();
                            JSONObject json = null;
                            try {
                                json = new JSONObject(entity != null ? EntityUtils.toString(entity) : null);
                            } catch (JSONException ex) {
                                Logger.getLogger(PenggunaController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            return json;
                        } else {
                            System.out.println("Unexpected response status: " + status);
                            return null;
                        }
                    }

                 };

                JSONObject response = httpclient.execute(get, responseHandler);
                
                if(response != null){
                    String unread = response.getString("unread");
                    System.out.println("TOTAL UNREAD NOTIFICATION: "+unread);
                    
                    Platform.runLater(()->{
                        btnNotifikasi.setText("NOTIFIKASI ("+unread+")");
                    });
                    
                    isCountNotifikasiDone = true;
                }
                
                return null;
            }
            
        }
        
        new Thread(new Work()).start();
        
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
        
        try {
            Node node = (Node)FXMLLoader.load(getClass().getResource("/com/erthru/pthajratabadiservismobil/ui/barang/BarangFXML.fxml"));
            
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
    private void btnPesananClicked(){
        
        try {
            Node node = (Node)FXMLLoader.load(getClass().getResource("/com/erthru/pthajratabadiservismobil/ui/pesanan/PesananFXML.fxml"));
            
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
    private void btnRiwayatPesananClicked(){
        
        try {
            Node node = (Node)FXMLLoader.load(getClass().getResource("/com/erthru/pthajratabadiservismobil/ui/riwayatpesanan/RiwayatPesananFXML.fxml"));
            
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
    private void btnPenggunaClicked(){
        
        try {
            Node node = (Node)FXMLLoader.load(getClass().getResource("/com/erthru/pthajratabadiservismobil/ui/pengguna/PenggunaFXML.fxml"));
            
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
    private void btnLogoutClicked(){
        
        new PreferenceUser().destroy();
        
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(MainController.class.getResource("/com/erthru/pthajratabadiservismobil/ui/login/LoginFXML.fxml"));
        Parent root;

        try {
            root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.setMaximized(false);
            stage.setResizable(true);

            root.requestFocus();
            stage.show();

            Stage stg = (Stage) btnLogout.getScene().getWindow();
            stg.close();

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }
    
    @FXML
    private void btnTeknisiClicked(){
        try {
            Node node = (Node)FXMLLoader.load(getClass().getResource("/com/erthru/pthajratabadiservismobil/ui/teknisi/TeknisiFXML.fxml"));

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
    private void btnLaporanClicked(){
        try {
            Node node = (Node)FXMLLoader.load(getClass().getResource("/com/erthru/pthajratabadiservismobil/ui/laporan/LaporanFXML.fxml"));
            
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
    private void btnNotifikasiClicked(){
        try {
            Node node = (Node)FXMLLoader.load(getClass().getResource("/com/erthru/pthajratabadiservismobil/ui/notifikasi/NotifikasiFXML.fxml"));
            
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
    
}
