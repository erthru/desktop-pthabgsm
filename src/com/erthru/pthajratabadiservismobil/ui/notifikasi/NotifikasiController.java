/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.notifikasi;

import com.erthru.pthajratabadiservismobil.ui.pengguna.PenggunaController;
import com.erthru.pthajratabadiservismobil.utils.ApiEndPoint;
import com.erthru.pthajratabadiservismobil.utils.Loading;
import com.erthru.pthajratabadiservismobil.utils.MsgBox;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author supriantodjamalu
 */
public class NotifikasiController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private TableView tableNotifikasi;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        loadNotifikasiTable();
    }    
    
    private void readNotif(){
        
        class Work extends Task<Void>{

            @Override
            protected Void call() throws Exception {
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.BACA_NOTIFIKASI_ADMIN);

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
                    System.out.println("Notifikasi telah dibaca");
                }
                
                return null;
            }
            
        }
        
        new Thread(new Work()).start();
        
    }
    
    private void loadNotifikasiTable(){
        
        class Work extends Task<Void>{
            
            Loading loading = new Loading();

            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(() -> {
                    
                    loading.show();
                
                    TableColumn body = new TableColumn("Keterangan");
                    body.setCellValueFactory(new PropertyValueFactory<>("body"));
                    
                    tableNotifikasi.getColumns().clear();
                    tableNotifikasi.getItems().clear();
                    tableNotifikasi.getColumns().addAll(body);
                
                });
                
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
                Platform.runLater(()->{loading.dismiss();});
                
                if(response != null){
                    
                    ObservableList<Notifikasi> data = FXCollections.observableArrayList();
                    
                    JSONArray notifArr = response.getJSONArray("notifikasi");
                    
                    for(int i=0; i<notifArr.length(); i++){
                        data.add(new Notifikasi(
                                notifArr.getJSONObject(i).getString("notification_body")
                        ));
                    }
                    
                    readNotif();
                    Platform.runLater(()->{                        
                        tableNotifikasi.getItems().addAll(data);
                    });
                    
                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi gagal.");});
                }
                
                return null;
            }
            
        }
        
        new Thread(new Work()).start();
        
    }
    
}
