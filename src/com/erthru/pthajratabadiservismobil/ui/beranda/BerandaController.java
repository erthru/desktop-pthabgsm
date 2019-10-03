/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.beranda;

import com.erthru.pthajratabadiservismobil.utils.ApiEndPoint;
import com.erthru.pthajratabadiservismobil.utils.Loading;
import com.erthru.pthajratabadiservismobil.utils.MsgBox;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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
public class BerandaController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private Label lbTotalBarang;
    
    @FXML
    private Label lbTotalPesanan;
    
    @FXML
    private Label lbPenggunaAktif;
    
    @FXML
    private Label lbPesananHari;
    
    @FXML
    private Label lbPesananMinggu;
    
    @FXML
    private Label lbPesananBulan;
    
    @FXML
    private Label lbPesananTahun;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadTotalBarang();
        loadTotalPesanan();
        loadTotalPengguna();
        loadPesananReport();
    }    
    
    private void loadTotalBarang(){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                    
                    loading.show();
  
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.DAFTAR_BARANG_SERVIS+"&page=1");

                ResponseHandler<JSONObject> responseHandler = new ResponseHandler<JSONObject>() {

                    @Override
                    public JSONObject handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                        int status = response.getStatusLine().getStatusCode();
                        System.out.print(status);
                        if (status >= 200 && status < 300) {
                            HttpEntity entity = response.getEntity();
                            JSONObject json = null;
                            try {
                                json = new JSONObject(entity != null ? EntityUtils.toString(entity) : null);
                            } catch (JSONException ex) {
                                Logger.getLogger(BerandaController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            return json;
                        } else {
                            System.out.println("Unexpected response status: " + status);
                            return null;
                        }
                    }

                 };

                JSONObject response = httpclient.execute(get, responseHandler);

                Platform.runLater(()->{loading.dismiss();});

                if(response == null){
                    Platform.runLater(()->{MsgBox.error("Koneksi internet gagal.");});
                }else{
                    String totalBarang = response.getString("total");
                    Platform.runLater(()->{lbTotalBarang.setText(totalBarang);});

                }
                
                return null;
            }
            
        }
        
        Work work = new Work();
        Thread t = new Thread(work);
        t.start();
        
    }
    
    private void loadTotalPengguna(){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                    loading.show();
                    
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.USER_ALL+"&page=1");

                ResponseHandler<JSONObject> responseHandler = new ResponseHandler<JSONObject>() {

                    @Override
                    public JSONObject handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                        int status = response.getStatusLine().getStatusCode();
                        System.out.print(status);
                        if (status >= 200 && status < 300) {
                            HttpEntity entity = response.getEntity();
                            JSONObject json = null;
                            try {
                                json = new JSONObject(entity != null ? EntityUtils.toString(entity) : null);
                            } catch (JSONException ex) {
                                Logger.getLogger(BerandaController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            return json;
                        } else {
                            System.out.println("Unexpected response status: " + status);
                            return null;
                        }
                    }

                 };

                JSONObject response = httpclient.execute(get, responseHandler);

                Platform.runLater(()->{loading.dismiss();});

                if(response == null){
                    Platform.runLater(()->{MsgBox.error("Koneksi internet gagal.");});
                }else{
                    String pengguna = response.getString("total");
                    Platform.runLater(()->{lbPenggunaAktif.setText(pengguna);});
                }
                
                return null;
            }
            
        }
        
        Work work = new Work();
        Thread t = new Thread(work);
        t.start();
        
    }
    
     private void loadTotalPesanan(){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                    loading.show();
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.DAFTAR_BOOKING_ALL+"&page=1");

                ResponseHandler<JSONObject> responseHandler = new ResponseHandler<JSONObject>() {

                    @Override
                    public JSONObject handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                        int status = response.getStatusLine().getStatusCode();
                        System.out.print(status);
                        if (status >= 200 && status < 300) {
                            HttpEntity entity = response.getEntity();
                            JSONObject json = null;
                            try {
                                json = new JSONObject(entity != null ? EntityUtils.toString(entity) : null);
                            } catch (JSONException ex) {
                                Logger.getLogger(BerandaController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            return json;
                        } else {
                            System.out.println("Unexpected response status: " + status);
                            return null;
                        }
                    }

                 };

                JSONObject response = httpclient.execute(get, responseHandler);

                Platform.runLater(()->{loading.dismiss();});

                if(response == null){
                   Platform.runLater(()->{ MsgBox.error("Koneksi internet gagal.");});
                }else{
                    int pesanan = response.getInt("total_whaving");
                    Platform.runLater(()->{lbTotalPesanan.setText(""+pesanan);});
                }
                
                return null;
            }
            
        }
        
        Work work = new Work();
        Thread t = new Thread(work);
        t.start();
        
    }
     
     private void loadPesananReport(){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                    loading.show();
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.ADMIN_HOME_REPORT);

                ResponseHandler<JSONObject> responseHandler = new ResponseHandler<JSONObject>() {

                    @Override
                    public JSONObject handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                        int status = response.getStatusLine().getStatusCode();
                        System.out.print(status);
                        if (status >= 200 && status < 300) {
                            HttpEntity entity = response.getEntity();
                            JSONObject json = null;
                            try {
                                json = new JSONObject(entity != null ? EntityUtils.toString(entity) : null);
                            } catch (JSONException ex) {
                                Logger.getLogger(BerandaController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            return json;
                        } else {
                            System.out.println("Unexpected response status: " + status);
                            return null;
                        }
                    }

                 };

                JSONObject response = httpclient.execute(get, responseHandler);

                Platform.runLater(()->{loading.dismiss();});

                if(response == null){
                   Platform.runLater(()->{ MsgBox.error("Koneksi internet gagal.");});
                }else{
                    String today = response.getString("booking_hari_ini");
                    String month = response.getString("booking_bulan_ini");
                    String year = response.getString("booking_tahun_ini");
                    String week = response.getString("booking_minggu_ini");
                    Platform.runLater(()->{
                        lbPesananHari.setText(""+today);
                        lbPesananMinggu.setText(""+week);
                        lbPesananBulan.setText(""+month);
                        lbPesananTahun.setText(""+year);
                    });
                }
                
                return null;
            }
            
        }
        
        Work work = new Work();
        Thread t = new Thread(work);
        t.start();
        
    }
    
}
