/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.pesanan;

import com.erthru.pthajratabadiservismobil.ui.pengguna.PenggunaController;
import com.erthru.pthajratabadiservismobil.ui.pesanandetail.PesananDetailController;
import com.erthru.pthajratabadiservismobil.ui.pesananfilter.PesananFilterController;
import com.erthru.pthajratabadiservismobil.utils.ApiEndPoint;
import com.erthru.pthajratabadiservismobil.utils.Loading;
import com.erthru.pthajratabadiservismobil.utils.MsgBox;
import com.erthru.pthajratabadiservismobil.utils.StringFex;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    
    @FXML
    private Pagination pagingFilter;
    
    private String DATE_A,DATE_B;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setAllPagingListener();
        setPaging();
    }   
    
    private void setAllPagingListener(){
        
        paging.currentPageIndexProperty().addListener((obs,oldIndex,newIndex)->
                setTablePesanan(String.valueOf(Integer.parseInt(newIndex.toString()) + 1))
        );
        
        pagingFilter.currentPageIndexProperty().addListener((obs,oldIndex,newIndex)->
                dateFilterTablePesanan(String.valueOf(Integer.parseInt(newIndex.toString()) + 1),DATE_A,DATE_B)
        );
    }
    
    public void setPaging(){
        
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

                        pagingFilter.setVisible(false);
                        paging.setVisible(true);

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
        
        
    }
    
    public void setPagingDateFilter(String dateA, String dateB){
                
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                    
                    loading.show();
                    
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.DAFTAR_BOOKING_ALL_DATE_FILTER+"&page=1"+"&date_a="+dateA+"&date_b="+dateB);

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
                        pagingFilter.setVisible(true);
                        paging.setVisible(false);

                        pagingFilter.setPageCount((total / 10) + 1);
                        pagingFilter.setCurrentPageIndex(0);

                        DATE_A = dateA;
                        DATE_B = dateB;
                        dateFilterTablePesanan("1",DATE_A,DATE_B);
                    });

                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi internet gagal.");});
                }
                
                return null;
            }
            
        }
        
        new Thread(new Work()).start();
        
        
    }
    
    private void setTablePesanan(String page){
        
        System.out.println(page);
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                    
                    loading.show();
                        
                    TableColumn id = new TableColumn("NO. Invoice");
                    id.setCellValueFactory(new PropertyValueFactory<>("id"));
                    id.setMinWidth(80);

                    TableColumn namaLengkap = new TableColumn("Nama Lengkap");
                    namaLengkap.setCellValueFactory(new PropertyValueFactory<>("namaLengkap"));
                    namaLengkap.setMinWidth(100);

                    TableColumn jenisServis = new TableColumn("Jenis Servis");
                    jenisServis.setCellValueFactory(new PropertyValueFactory<>("jenisServis"));
                    jenisServis.setMinWidth(100);

                    TableColumn status = new TableColumn("Status");
                    status.setCellValueFactory(new PropertyValueFactory<>("status"));
                    status.setMinWidth(100);

                    TableColumn createdAt = new TableColumn("Tgl. Pesanan");
                    createdAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
                    createdAt.setMinWidth(100);

                    tablePesanan.getColumns().clear();
                    tablePesanan.getItems().clear();
                    tablePesanan.getColumns().addAll(id,namaLengkap,jenisServis,status,createdAt);
                    
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
                        String namaLengkap1 = dataBooking.getJSONObject(i).getString("user_nama_lengkap");
                        String jenisServis1 = dataBooking.getJSONObject(i).getString("booking_jenis_servis");
                        String lastStatus1 = dataBooking.getJSONObject(i).getString("last_status");
                        String createdAt1 = dataBooking.getJSONObject(i).getString("booking_created_at");

                        data.add(new Pesanan(
                                bookingId,
                                namaLengkap1,
                                jenisServis1,
                                lastStatus1,
                                StringFex.dateMax(createdAt1)
                        ));

                    }

                    Platform.runLater(()->{tablePesanan.getItems().addAll(data);});

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
                    
                    TableColumn id = new TableColumn("NO. Invoice");
                    id.setCellValueFactory(new PropertyValueFactory<>("id"));
                    id.setMinWidth(80);

                    TableColumn namaLengkap = new TableColumn("Nama Lengkap");
                    namaLengkap.setCellValueFactory(new PropertyValueFactory<>("namaLengkap"));
                    namaLengkap.setMinWidth(100);

                    TableColumn jenisServis = new TableColumn("Jenis Servis");
                    jenisServis.setCellValueFactory(new PropertyValueFactory<>("jenisServis"));
                    jenisServis.setMinWidth(100);

                    TableColumn status = new TableColumn("Status");
                    status.setCellValueFactory(new PropertyValueFactory<>("status"));
                    status.setMinWidth(100);

                    TableColumn createdAt = new TableColumn("Tgl. Pesanan");
                    createdAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
                    createdAt.setMinWidth(100);

                    tablePesanan.getColumns().clear();
                    tablePesanan.getItems().clear();
                    tablePesanan.getColumns().addAll(id,namaLengkap,jenisServis,status,createdAt);
                
                    
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
                        String namaLengkap1 = dataBooking.getJSONObject(i).getString("user_nama_lengkap");
                        String jenisServis1 = dataBooking.getJSONObject(i).getString("booking_jenis_servis");
                        String lastStatus1 = dataBooking.getJSONObject(i).getString("last_status");
                        String createdAt1 = dataBooking.getJSONObject(i).getString("booking_created_at");

                        data.add(new Pesanan(
                                bookingId,
                                namaLengkap1,
                                jenisServis1,
                                lastStatus1,
                                StringFex.dateMax(createdAt1)
                        ));

                    }

                    Platform.runLater(()->{tablePesanan.getItems().addAll(data);});

                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi internet gagal.");});
                }
                
                return null;
                
            }
            
        }
        
        new Thread(new Work()).start();
        
    }
    
    private void dateFilterTablePesanan(String page, String dateA, String dateB){
        
        System.out.println(page);
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                    
                    loading.show();
                        
                    TableColumn id = new TableColumn("NO. Invoice");
                    id.setCellValueFactory(new PropertyValueFactory<>("id"));
                    id.setMinWidth(80);

                    TableColumn namaLengkap = new TableColumn("Nama Lengkap");
                    namaLengkap.setCellValueFactory(new PropertyValueFactory<>("namaLengkap"));
                    namaLengkap.setMinWidth(100);

                    TableColumn jenisServis = new TableColumn("Jenis Servis");
                    jenisServis.setCellValueFactory(new PropertyValueFactory<>("jenisServis"));
                    jenisServis.setMinWidth(100);

                    TableColumn status = new TableColumn("Status");
                    status.setCellValueFactory(new PropertyValueFactory<>("status"));
                    status.setMinWidth(100);

                    TableColumn createdAt = new TableColumn("Tgl. Pesanan");
                    createdAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
                    createdAt.setMinWidth(100);

                    tablePesanan.getColumns().clear();
                    tablePesanan.getItems().clear();
                    tablePesanan.getColumns().addAll(id,namaLengkap,jenisServis,status,createdAt);
                    
                    
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.DAFTAR_BOOKING_ALL_DATE_FILTER+"&page="+page+"&date_a="+dateA+"&date_b="+dateB);

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
                        String namaLengkap1 = dataBooking.getJSONObject(i).getString("user_nama_lengkap");
                        String jenisServis1 = dataBooking.getJSONObject(i).getString("booking_jenis_servis");
                        String lastStatus1 = dataBooking.getJSONObject(i).getString("last_status");
                        String createdAt1 = dataBooking.getJSONObject(i).getString("booking_created_at");

                        data.add(new Pesanan(
                                bookingId,
                                namaLengkap1,
                                jenisServis1,
                                lastStatus1,
                                StringFex.dateMax(createdAt1)
                        ));

                    }

                    Platform.runLater(()->{tablePesanan.getItems().addAll(data);});
                }else{
                    Platform.runLater(()->{ MsgBox.error("Koneksi internet gagal.");});
                }
                return null;
                
            }
            
        }
        
        new Thread(new Work()).start();
        
    }
    
    @FXML
    private void btnFilterClicked() throws Exception{

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(PesananController.class.getResource("/com/erthru/pthajratabadiservismobil/ui/pesananfilter/PesananFilterFXML.fxml"));
        Parent root = loader.load();
        
        stage.setScene(new Scene(root));
        stage.setTitle("Filter");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        
        root.requestFocus();
        PesananFilterController child = loader.getController();
        child.setParent(this);
        stage.show();
        
    }
    
    @FXML
    private void tablePesananClicked() throws Exception{
        
        Pesanan pesanan = (Pesanan) tablePesanan.getSelectionModel().getSelectedItem();
        PesananDetailController.BOOKING_ID = pesanan.getId();
        
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(PesananController.class.getResource("/com/erthru/pthajratabadiservismobil/ui/pesanandetail/PesananDetailFXML.fxml"));
        Parent root = loader.load();
        
        stage.setScene(new Scene(root));
        stage.setTitle("Pesanan Detail");
        stage.setResizable(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        
        root.requestFocus();
        PesananDetailController child = loader.getController();
        child.setParent(this);
        stage.show();
        
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
