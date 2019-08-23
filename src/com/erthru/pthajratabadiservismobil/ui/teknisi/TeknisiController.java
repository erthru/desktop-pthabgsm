/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.teknisi;

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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
public class TeknisiController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    private String id;
    
    @FXML
    private TextField txNama;
    
    @FXML
    private TextField txEmail;
    
    @FXML
    private PasswordField txPassword;
    
    @FXML
    private Button btnSimpan;
    
    @FXML
    private Button btnEdit;
    
    @FXML
    private Button btnHapus;
    
    @FXML
    private Button btnRefresh;
    
    @FXML
    private Pagination pageTeknisi;
    
    @FXML
    private TableView tableTeknisi;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reset();
        setCustomListener();
    }    
    
    @FXML
    private void onBtnSimpanClicked(){
        tambahTeknisi();
    }
    
    @FXML
    private void onBtnEditClicked(){
        editTeknisi();
    }
    
    @FXML
    private void onBtnHapusClicked(){
        int ok = MsgBox.confirm("Hapus data ini ?");
        
        if (ok == 1){
            hapusTeknisi();
        }
    }
    
    @FXML
    private void onBtnRefreshClicked(){
        reset();
    }
    
    @FXML
    private void onTableTeknisiClicked(){
        
        Teknisi teknisi = (Teknisi) tableTeknisi.getSelectionModel().getSelectedItem();
        
        if (teknisi != null){
            btnSimpan.setDisable(true);
            btnEdit.setDisable(false);
            btnHapus.setDisable(false);
                        
            id = teknisi.getId();
            txNama.setText(teknisi.getNama());
            txEmail.setText(teknisi.getEmail());
            txPassword.setDisable(true);
        }
        
    }
    
    private void setCustomListener(){
        pageTeknisi.currentPageIndexProperty().addListener((obs,oldIndex,newIndex)->{
                int index = Integer.parseInt(newIndex.toString())+1;
                loadTeknisi(String.valueOf(index));
        });
    }
    
    private void reset(){
        btnSimpan.setDisable(false);
        btnEdit.setDisable(true);
        btnHapus.setDisable(true);
        
        txNama.setText("");
        txEmail.setText("");
        txPassword.setText("");
        txPassword.setDisable(false);
        
        loadTeknisiPage();
        
        id = null;
    }
    
    private void loadTeknisiPage(){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                    loading.show();
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.DAFTAR_TEKNISI+"&page=1");

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
                
                Platform.runLater(()->{loading.dismiss();});
                
                if (response != null){
                    int total = response.getInt("total");
                    
                    Platform.runLater(()->{
                        pageTeknisi.setPageCount(total / 10 + 1);
                        pageTeknisi.setCurrentPageIndex(0);
                        
                        loadTeknisi("1");
                    });
                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi internet gagal.");});
                }
                
                return null;
                
            }
            
        }
        
        Thread thread = new Thread(new Work());
        thread.start();
        
    }
    
    private void loadTeknisi(String page){
        System.out.print(page);
        
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
                    
                    TableColumn email = new TableColumn("Email");
                    email.setCellValueFactory(new PropertyValueFactory<>("email"));
                    
                    tableTeknisi.getColumns().clear();
                    tableTeknisi.getItems().clear();
                    tableTeknisi.getColumns().addAll(id,nama,email);
                    
                });
                
                Platform.runLater(()->{loading.show();});

                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.DAFTAR_TEKNISI+"&page="+page);

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
                
                Platform.runLater(()->{loading.dismiss();});
                
                if (response != null){
                    
                    JSONArray teknisiArray = response.getJSONArray("teknisis");
                    
                    ObservableList<Teknisi> data = FXCollections.observableArrayList();
                    
                    for (int i=0; i<teknisiArray.length(); i++){
                        
                        String idj = teknisiArray.getJSONObject(i).getString("teknisi_id");
                        String emailj = teknisiArray.getJSONObject(i).getString("teknisi_email");
                        String namaj = teknisiArray.getJSONObject(i).getString("teknisi_nama");
                        
                        data.add(new Teknisi(idj,namaj,emailj));
                        
                    }
                                        
                    Platform.runLater(()->{tableTeknisi.setItems(data);});
                    
                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi internet gagal.");});
                }
                
                return null;
            }
            
        }
        
        Thread t = new Thread(new Work());
        t.start();
    }
    
    private void tambahTeknisi(){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{loading.show();});
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpPost post = new HttpPost(ApiEndPoint.ADD_TEKNISI);
                
                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("nama",txNama.getText()));
                params.add(new BasicNameValuePair("email",txEmail.getText()));
                params.add(new BasicNameValuePair("password",txPassword.getText()));

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
                Platform.runLater(()->{loading.dismiss();});
                              
                if (response != null){
               
                    String msg = response.getString("message");
                    Boolean error = response.getBoolean("error");
                    
                    if (!error){
                        Platform.runLater(()->{MsgBox.success(msg);});
                        reset();
                    }else{
                        Platform.runLater(()->{MsgBox.error(msg);});
                    }
                    
                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi bermasalah.");});
                }
                
                return null;
            }
            
        }
        
        Thread t = new Thread(new Work());
        t.start();
        
    }
    
    private void editTeknisi(){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{loading.show();});
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpPost post = new HttpPost(ApiEndPoint.UPDATE_TEKNISI);
                
                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("id",id));
                params.add(new BasicNameValuePair("nama",txNama.getText()));
                params.add(new BasicNameValuePair("email",txEmail.getText()));

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
                Platform.runLater(()->{loading.dismiss();});
                              
                if (response != null){
               
                    String msg = response.getString("message");
                    Boolean error = response.getBoolean("error");
                    
                    if (!error){
                        Platform.runLater(()->{MsgBox.success(msg);});
                        reset();
                    }else{
                        Platform.runLater(()->{MsgBox.error(msg);});
                    }
                    
                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi bermasalah.");});
                }
                
                return null;
            }
            
        }
        
        Thread t = new Thread(new Work());
        t.start();
        
    }
    
    private void hapusTeknisi(){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{loading.show();});
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpPost post = new HttpPost(ApiEndPoint.DELETE_TEKNISI);
                
                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("id",id));

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
                Platform.runLater(()->{loading.dismiss();});
                              
                if (response != null){
               
                    String msg = response.getString("message");
                    Boolean error = response.getBoolean("error");
                    
                    if (!error){
                        Platform.runLater(()->{MsgBox.success(msg);});
                        reset();
                    }else{
                        Platform.runLater(()->{MsgBox.error(msg);});
                    }
                    
                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi bermasalah.");});
                }
                
                return null;
            }
            
        }
        
        Thread t = new Thread(new Work());
        t.start();
        
    }
    
}
