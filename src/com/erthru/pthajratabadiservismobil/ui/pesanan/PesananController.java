/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.pesanan;

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
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
public class PesananController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private Button btnFilter;
    
    @FXML
    private TextField txCari;
    
    @FXML
    private TableView tablePesanan;
    
    @FXML
    private Pagination paging;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setPaging();
    }   
    
    private void setPaging(){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{loading.show();});
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.DAFTAR_BOOKING_ALL+"&page=1");
                
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
                    
                    int total = response.getInt("total");
                    
                    Platform.runLater(()->{
                    
                        paging.setPageCount((total / 10) + 1);
                        paging.setCurrentPageIndex(0);
                        
                        setTablePesanan("1");
                    
                    });
                    
                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi internet gagal.");});
                }
                
                return null;
            }
            
        }
        
        new Thread(new Work()).start();
        
        paging.currentPageIndexProperty().addListener((obs,oldIndex,newIndex)->
                setTablePesanan(String.valueOf(Integer.parseInt(newIndex.toString()) + 1))
        );
        
    }
    
    private void setTablePesanan(String page){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{loading.show();});
                
                Platform.runLater(()->{
                
                    TableColumn id = new TableColumn("ID");
                    id.setCellValueFactory(new PropertyValueFactory<>("id"));
                    id.setMaxWidth(0);
                    id.setMinWidth(0);
                    
                    TableColumn namaLengkap = new TableColumn("Nama Lengkap");
                    namaLengkap.setCellValueFactory(new PropertyValueFactory<>("namaLengkap"));
                    namaLengkap.setMinWidth(100);
                    
                    TableColumn jenisServis = new TableColumn("Jenis Servis");
                    jenisServis.setCellValueFactory(new PropertyValueFactory<>("jenisServis"));
                    jenisServis.setMinWidth(100);
                    
                    TableColumn status = new TableColumn("Status");
                    status.setCellValueFactory(new PropertyValueFactory<>("status"));
                    status.setMinWidth(100);
                    
                    tablePesanan.getColumns().clear();
                    tablePesanan.getItems().clear();
                    tablePesanan.getColumns().addAll(id,namaLengkap,jenisServis,status);
                    
                    
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.DAFTAR_BOOKING_ALL+"&page="+page);
                
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
                    
                    ObservableList<Pesanan> data = FXCollections.observableArrayList();
                    
                    JSONArray dataBooking = response.getJSONArray("data_booking");
                    
                    for(int i=0; i<dataBooking.length(); i++){
                        
                        String bookingId = dataBooking.getJSONObject(i).getString("booking_id");
                        String namaLengkap = dataBooking.getJSONObject(i).getString("user_nama_lengkap");
                        String jenisServis = dataBooking.getJSONObject(i).getString("booking_jenis_servis");
                        String lastStatus = dataBooking.getJSONObject(i).getString("last_status");
                        
                        data.add(new Pesanan(
                                bookingId,
                                namaLengkap,
                                jenisServis,
                                lastStatus
                        ));
                        
                    }
                    
                    Platform.runLater(()->{ tablePesanan.getItems().addAll(data); });
                    
                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi internet gagal.");});
                }
                
                return null;
                
            }
            
        }
        
        new Thread(new Work()).start();
        
    }
    
    private void searchTablePesanan(String q){
        
        class Work extends Task<Void>{
            
            @Override
            protected Void call() throws Exception {
                                
                Platform.runLater(()->{
                
                    TableColumn id = new TableColumn("ID");
                    id.setCellValueFactory(new PropertyValueFactory<>("id"));
                    id.setMaxWidth(0);
                    id.setMinWidth(0);
                    
                    TableColumn namaLengkap = new TableColumn("Nama Lengkap");
                    namaLengkap.setCellValueFactory(new PropertyValueFactory<>("namaLengkap"));
                    namaLengkap.setMinWidth(100);
                    
                    TableColumn jenisServis = new TableColumn("Jenis Servis");
                    jenisServis.setCellValueFactory(new PropertyValueFactory<>("jenisServis"));
                    jenisServis.setMinWidth(100);
                    
                    TableColumn status = new TableColumn("Status");
                    status.setCellValueFactory(new PropertyValueFactory<>("status"));
                    status.setMinWidth(100);
                    
                    tablePesanan.getColumns().clear();
                    tablePesanan.getItems().clear();
                    tablePesanan.getColumns().addAll(id,namaLengkap,jenisServis,status);
                    
                    
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.SEARCH_BOOKING_ALL+"&q="+q);
                
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
                    
                    ObservableList<Pesanan> data = FXCollections.observableArrayList();
                    
                    JSONArray dataBooking = response.getJSONArray("data_booking");
                    
                    for(int i=0; i<dataBooking.length(); i++){
                        
                        String bookingId = dataBooking.getJSONObject(i).getString("booking_id");
                        String namaLengkap = dataBooking.getJSONObject(i).getString("user_nama_lengkap");
                        String jenisServis = dataBooking.getJSONObject(i).getString("booking_jenis_servis");
                        String lastStatus = dataBooking.getJSONObject(i).getString("last_status");
                        
                        data.add(new Pesanan(
                                bookingId,
                                namaLengkap,
                                jenisServis,
                                lastStatus
                        ));
                        
                    }
                    
                    Platform.runLater(()->{ tablePesanan.getItems().addAll(data); });
                    
                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi internet gagal.");});
                }
                
                return null;
                
            }
            
        }
        
        new Thread(new Work()).start();
        
    }
    
    @FXML
    private void btnFilterClicked(){
        
    }
    
    @FXML
    private void txCariOnTextChanged(){
        
        if(txCari.getText().isEmpty()){
            paging.setVisible(true);
            setPaging();
        }else{
            paging.setVisible(false);
            searchTablePesanan(txCari.getText());
        }
        
    }
    
}
