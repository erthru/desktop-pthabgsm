/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.barang;

import com.erthru.pthajratabadiservismobil.ui.beranda.BerandaController;
import com.erthru.pthajratabadiservismobil.utils.ApiEndPoint;
import com.erthru.pthajratabadiservismobil.utils.Loading;
import com.erthru.pthajratabadiservismobil.utils.MsgBox;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author supriantodjamalu
 */
public class BarangController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private TableView tableBarang;
    
    @FXML
    private Pagination paging;
   
    @FXML
    private TextField txNama;
    
    @FXML
    private TextField txHarga;
    
    @FXML
    private TextField txCari;
    
    @FXML
    private Button btnTambah;
    
    @FXML
    private Button btnEdit;
    
    @FXML
    private Button btnHapus;
    
    @FXML
    private Button btnReset;
    
    @FXML
    private ComboBox comboKategori;
    
    String barangId;
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        reset();
        setComboKategori();
        
    }   
    
    @FXML
    private void btnTambahClicked(){
        
        if(txNama.getText().isEmpty() || txHarga.getText().isEmpty() || comboKategori.getSelectionModel().getSelectedItem() == null){
            MsgBox.error("Data belum lengkap.");
        }else{
            tambahData();
        }
        
    }
    
    @FXML
    private void tableBarangClicked(){
        
        Barang barang = (Barang) tableBarang.getSelectionModel().getSelectedItem();
        barangId = barang.getId();
        txNama.setText(barang.getNama());
        txHarga.setText(barang.getHarga());
        comboKategori.getSelectionModel().select(barang.getKategori());
        
        onSelectedMode();
        
    }
    
    @FXML
    private void btnResetClicked(){
        reset();
    }
    
    @FXML
    private void btnEditClicked(){
        
        if(txNama.getText().isEmpty() || txHarga.getText().isEmpty() || comboKategori.getSelectionModel().getSelectedItem() == null){
            MsgBox.error("Data belum lengkap.");
        }else{
            editData();
        }
        
    }
    
    @FXML
    private void btnHapusClicked(){
        
        int confirm = MsgBox.confirm("Hapus data ini ?");
        if(confirm == 1){
            deleteData();
        }
        
    }
    
    @FXML
    private void txCariOnTextChanged(){
        
        if(txCari.getText().isEmpty()){
            reset();
        }else{
            
            paging.setVisible(false);
            searchTableBarang(txCari.getText());
            
        }
        
    }
    
    private void reset(){
        btnTambah.setDisable(false);
        btnEdit.setDisable(true);
        btnHapus.setDisable(true);
        txNama.setText("");
        txHarga.setText("");
        barangId = null;
        setPaging();
        paging.setVisible(true);
    }
    
    private void onSelectedMode(){
        btnTambah.setDisable(true);
        btnEdit.setDisable(false);
        btnHapus.setDisable(false);
    }
    
    private void setComboKategori(){
        
        ObservableList data = FXCollections.observableArrayList("GR","BODY");
        comboKategori.setItems(data);
        
    }
    
    private void tambahData(){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                    loading.show();
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpPost post = new HttpPost(ApiEndPoint.ADD_BARANG_SERVIS);
                
                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("servis_nama",txNama.getText()));
                params.add(new BasicNameValuePair("servis_harga",txHarga.getText()));
                params.add(new BasicNameValuePair("servis_kategori",comboKategori.getSelectionModel().getSelectedItem().toString()));
                
                post.setEntity(new UrlEncodedFormEntity(params));
                
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
                                Logger.getLogger(BerandaController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            return json;
                        } else {
                            System.out.println("Unexpected response status: " + status);
                            return null;
                        }
                    }

                 };
                
                JSONObject response = httpclient.execute(post, responseHandler);
                
                Platform.runLater(()->{
                    loading.dismiss();
                });
                
                if(response != null){
                    
                    if(!response.getBoolean("error")){
                        Platform.runLater(()->{
                            try {
                                MsgBox.success(response.getString("pesan"));
                            } catch (JSONException ex) {
                                Logger.getLogger(BarangController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                    }else{
                        Platform.runLater(()->{
                            try {
                                MsgBox.error(response.getString("pesan"));
                            } catch (JSONException ex) {
                                Logger.getLogger(BarangController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                    }                                        
                                        
                    reset();
                    
                }else{
                    Platform.runLater(()->{
                        MsgBox.error("Koneksi internet gagal.");
                    });
                }
                
                return null;
            }
            
        }
        
        Work work = new Work();
        Thread t = new Thread(work);
        t.start();
        
    }
    
    private void editData(){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                    loading.show();
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpPost post = new HttpPost(ApiEndPoint.UPDATE_BARANG_SERVIS);
                
                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("servis_id",barangId));
                params.add(new BasicNameValuePair("servis_nama",txNama.getText()));
                params.add(new BasicNameValuePair("servis_harga",txHarga.getText()));
                params.add(new BasicNameValuePair("servis_kategori",comboKategori.getSelectionModel().getSelectedItem().toString()));
                
                post.setEntity(new UrlEncodedFormEntity(params));
                
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
                                Logger.getLogger(BerandaController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            return json;
                        } else {
                            System.out.println("Unexpected response status: " + status);
                            return null;
                        }
                    }

                 };
                
                JSONObject response = httpclient.execute(post, responseHandler);
                
                Platform.runLater(()->{
                    loading.dismiss();
                });
                
                if(response != null){
                    
                    if(!response.getBoolean("error")){
                        Platform.runLater(()->{
                            try {
                                MsgBox.success(response.getString("pesan"));
                            } catch (JSONException ex) {
                                Logger.getLogger(BarangController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                    }else{
                        Platform.runLater(()->{
                            try {
                                MsgBox.error(response.getString("pesan"));
                            } catch (JSONException ex) {
                                Logger.getLogger(BarangController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                    }                                        
                                        
                    reset();
                    
                }else{
                    Platform.runLater(()->{
                        MsgBox.error("Koneksi internet gagal.");
                    });
                }
                
                return null;
            }
            
        }
        
        Work work = new Work();
        Thread t = new Thread(work);
        t.start();
        
    }
    
    private void deleteData(){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                    loading.show();
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpPost post = new HttpPost(ApiEndPoint.DELETE_BARANG_SERVIS);
                
                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("servis_id",barangId));
                
                post.setEntity(new UrlEncodedFormEntity(params));
                
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
                                Logger.getLogger(BerandaController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            return json;
                        } else {
                            System.out.println("Unexpected response status: " + status);
                            return null;
                        }
                    }

                 };
                
                JSONObject response = httpclient.execute(post, responseHandler);
                
                Platform.runLater(()->{
                    loading.dismiss();
                });
                
                if(response != null){
                    
                    if(!response.getBoolean("error")){
                        Platform.runLater(()->{
                            try {
                                MsgBox.success(response.getString("pesan"));
                            } catch (JSONException ex) {
                                Logger.getLogger(BarangController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                    }else{
                        Platform.runLater(()->{
                            try {
                                MsgBox.error(response.getString("pesan"));
                            } catch (JSONException ex) {
                                Logger.getLogger(BarangController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                    }                                        
                                        
                    reset();
                    
                }else{
                    Platform.runLater(()->{
                        MsgBox.error("Koneksi internet gagal.");
                    });
                }
                
                return null;
            }
            
        }
        
        Work work = new Work();
        Thread t = new Thread(work);
        t.start();
        
    }
    
    private void setPaging(){
        
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
                
                Platform.runLater(()->{
                    loading.dismiss();
                });
                
                if(response != null){
                    
                    String total = response.getString("total");
                    
                    Platform.runLater(()->{
                        paging.setPageCount(Integer.parseInt(total) / 10 + 1);
                        paging.setCurrentPageIndex(0);
                    });
                    
                    setTableBarang(1);
                    
                }else{
                    MsgBox.error("Koneksi internet gagal.");
                }
                
                return null;
            }
            
        }
        
        Work work = new Work();
        Thread t = new Thread(work);
        t.start();
        
        paging.currentPageIndexProperty().addListener((obs,oldIndex,newIndex)->
                setTableBarang(Integer.parseInt(newIndex.toString())+1)
        );
        
    }
    
    private void setTableBarang(int page){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                
                    TableColumn id = new TableColumn("ID");
                    id.setCellValueFactory(new PropertyValueFactory<>("id"));
                    id.setMaxWidth(0);
                    id.setMinWidth(0);

                    TableColumn nama = new TableColumn("Nama");
                    nama.setCellValueFactory(new PropertyValueFactory<>("nama"));
                    nama.setMinWidth(300);

                    TableColumn harga = new TableColumn("Harga");
                    harga.setCellValueFactory(new PropertyValueFactory<>("harga"));
                    harga.setMinWidth(100);

                    TableColumn kategori = new TableColumn("Kategori");
                    kategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
                    kategori.setMinWidth(50);

                    tableBarang.getColumns().clear();
                    tableBarang.getItems().clear();
                    tableBarang.getColumns().addAll(id,nama,harga,kategori);
                
                });
                
                Platform.runLater(()->{
                    loading.show();
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.DAFTAR_BARANG_SERVIS+"&page="+page);
                
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
                
                Platform.runLater(()->{
                    loading.dismiss();
                });
                
                if(response != null){
                    JSONArray daftarBarangServis = response.getJSONArray("daftar_barang_servis");
                
                    ObservableList<Barang> data = FXCollections.observableArrayList();

                    for(int i=0; i<daftarBarangServis.length(); i++){

                        String idj = daftarBarangServis.getJSONObject(i).getString("barang_servis_id");
                        String namaj = daftarBarangServis.getJSONObject(i).getString("barang_servis_nama");
                        String hargaj = daftarBarangServis.getJSONObject(i).getString("barang_servis_harga");
                        String kategorij = daftarBarangServis.getJSONObject(i).getString("barang_servis_kategori");

                        data.add(
                                new Barang(idj,namaj,hargaj,kategorij)
                        );

                    }

                    Platform.runLater(()->{
                        tableBarang.setItems(data);
                    });
                    
                }else{
                    Platform.runLater(()->{
                        MsgBox.error("Koneksi internet gagal.");
                    });
                }                                             
                
                
                return null;
            }
            
        }
        
        Work work = new Work();
        Thread t = new Thread(work);
        t.start();
        
    }
    
    private void searchTableBarang(String q){
        
        class Work extends Task<Void>{
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                
                    TableColumn id = new TableColumn("ID");
                    id.setCellValueFactory(new PropertyValueFactory<>("id"));
                    id.setMaxWidth(0);
                    id.setMinWidth(0);

                    TableColumn nama = new TableColumn("Nama");
                    nama.setCellValueFactory(new PropertyValueFactory<>("nama"));
                    nama.setMinWidth(300);

                    TableColumn harga = new TableColumn("Harga");
                    harga.setCellValueFactory(new PropertyValueFactory<>("harga"));
                    harga.setMinWidth(100);

                    TableColumn kategori = new TableColumn("Kategori");
                    kategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
                    kategori.setMinWidth(50);

                    tableBarang.getColumns().clear();
                    tableBarang.getItems().clear();
                    tableBarang.getColumns().addAll(id,nama,harga,kategori);
                
                });
                
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.SEARCH_BARANG_SERVIS+"&q="+q);
                
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
                
                if(response != null){
                    JSONArray daftarBarangServis = response.getJSONArray("daftar_barang_servis");
                
                    ObservableList<Barang> data = FXCollections.observableArrayList();

                    for(int i=0; i<daftarBarangServis.length(); i++){

                        String idj = daftarBarangServis.getJSONObject(i).getString("barang_servis_id");
                        String namaj = daftarBarangServis.getJSONObject(i).getString("barang_servis_nama");
                        String hargaj = daftarBarangServis.getJSONObject(i).getString("barang_servis_harga");
                        String kategorij = daftarBarangServis.getJSONObject(i).getString("barang_servis_kategori");

                        data.add(
                                new Barang(idj,namaj,hargaj,kategorij)
                        );

                    }

                    Platform.runLater(()->{
                        tableBarang.setItems(data);
                    });
                    
                }else{
                    Platform.runLater(()->{
                        MsgBox.error("Koneksi internet gagal.");
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
