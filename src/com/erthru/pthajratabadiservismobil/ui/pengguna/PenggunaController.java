/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.pengguna;

import com.erthru.pthajratabadiservismobil.ui.beranda.BerandaController;
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
import javafx.fxml.Initializable;
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
public class PenggunaController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private TextField txCari;
    
    @FXML
    private TableView tableUser;
    
    @FXML
    private Pagination paging;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setPaging();
        setCustomListener();
    }    
    
    private void setCustomListener(){
        paging.currentPageIndexProperty().addListener((obs,oldIndex,newIndex)->
                setTableUser(Integer.parseInt(newIndex.toString()) + 1)
        );
    }
    
    @FXML
    private void txCariOnTextChanged(){
        
        if(txCari.getText().isEmpty()){
            paging.setVisible(true);
            setPaging();
        }else{
            paging.setVisible(false);
            searchTableUser(txCari.getText());
        }
        
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
                HttpGet get = new HttpGet(ApiEndPoint.USER_ALL+"&page=1");

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

                    paging.setPageCount((total / 10) + 1);
                    paging.setCurrentPageIndex(0);

                    Platform.runLater(()->{setTableUser(1);});

                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi internet gagal.");});
                }
                
                return null;
            }
            
        }
                
        Thread t = new Thread(new Work());
        t.start();
        
               
    }
    
    private void setTableUser(int page){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                    
                    loading.show();
                    TableColumn id = new TableColumn("ID");
                    id.setCellValueFactory(new PropertyValueFactory<>("id"));
                    id.setMaxWidth(0);
                    id.setMinWidth(0);

                    TableColumn namaLengkap = new TableColumn("Nama Lengkap");
                    namaLengkap.setCellValueFactory(new PropertyValueFactory<>("namaLengkap"));
                    namaLengkap.setMaxWidth(200);
                    namaLengkap.setMinWidth(200);

                    TableColumn alamat = new TableColumn("Alamat");
                    alamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));
                    alamat.setMaxWidth(200);
                    alamat.setMinWidth(200);

                    TableColumn noHp = new TableColumn("No. Hp");
                    noHp.setCellValueFactory(new PropertyValueFactory<>("noHp"));
                    noHp.setMaxWidth(200);
                    noHp.setMinWidth(200);

                    TableColumn createdAt = new TableColumn("Registrasi Pada");
                    createdAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
                    createdAt.setMaxWidth(200);
                    createdAt.setMinWidth(200);

                    TableColumn updatedAt = new TableColumn("Diperbarui Pada");
                    updatedAt.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
                    updatedAt.setMaxWidth(200);
                    updatedAt.setMinWidth(200);

                    TableColumn email = new TableColumn("Email");
                    email.setCellValueFactory(new PropertyValueFactory<>("email"));
                    email.setMaxWidth(200);
                    email.setMinWidth(200);

                    TableColumn pass = new TableColumn("Password");
                    pass.setCellValueFactory(new PropertyValueFactory<>("pass"));
                    pass.setMaxWidth(0);
                    pass.setMinWidth(0);

                    TableColumn level = new TableColumn("Level");
                    level.setCellValueFactory(new PropertyValueFactory<>("level"));
                    level.setMaxWidth(0);
                    level.setMinWidth(0);

                    tableUser.getColumns().clear();
                    tableUser.getItems().clear();
                    tableUser.getColumns().addAll(id,namaLengkap,alamat,noHp,createdAt,updatedAt,email,pass,level);
                    
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.USER_ALL+"&page="+page);

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

                if(response != null){

                    JSONArray jsonArray = response.getJSONArray("users");

                    ObservableList<Pengguna> data = FXCollections.observableArrayList();

                    for(int i=0; i<jsonArray.length(); i++){

                        String userId = jsonArray.getJSONObject(i).getString("user_id");
                        String userNamaLengkap = jsonArray.getJSONObject(i).getString("user_nama_lengkap");
                        String userAlamat = jsonArray.getJSONObject(i).getString("user_alamat");
                        String userNoHp = jsonArray.getJSONObject(i).getString("user_no_hp");
                        String userCreatedAt = jsonArray.getJSONObject(i).getString("user_created_at");
                        String userUpdatedAt = jsonArray.getJSONObject(i).getString("user_updated_at");
                        String userEmail = jsonArray.getJSONObject(i).getString("login_email");
                        String userPass = jsonArray.getJSONObject(i).getString("login_pass");
                        String userLoginLvl = jsonArray.getJSONObject(i).getString("login_lvl");

                        data.add(
                                new Pengguna(
                                        userId,
                                        userNamaLengkap,
                                        userAlamat,
                                        userNoHp,
                                        StringFex.dateMax(userCreatedAt),
                                        StringFex.dateMax(userUpdatedAt),
                                        userEmail,
                                        userPass,
                                        userLoginLvl
                                )
                        );

                    }

                    Platform.runLater(()->{tableUser.getItems().addAll(data);});

                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi internet gagal.");});
                }

                return null;
            }
            
        }
        
        new Thread(new Work()).start();
        
    }
    
    private void searchTableUser(String q){
        
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
                    namaLengkap.setMaxWidth(200);
                    namaLengkap.setMinWidth(200);

                    TableColumn alamat = new TableColumn("Alamat");
                    alamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));
                    alamat.setMaxWidth(200);
                    alamat.setMinWidth(200);

                    TableColumn noHp = new TableColumn("No. Hp");
                    noHp.setCellValueFactory(new PropertyValueFactory<>("noHp"));
                    noHp.setMaxWidth(200);
                    noHp.setMinWidth(200);

                    TableColumn createdAt = new TableColumn("Registrasi Pada");
                    createdAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
                    createdAt.setMaxWidth(200);
                    createdAt.setMinWidth(200);

                    TableColumn updatedAt = new TableColumn("Diperbarui Pada");
                    updatedAt.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
                    updatedAt.setMaxWidth(200);
                    updatedAt.setMinWidth(200);

                    TableColumn email = new TableColumn("Email");
                    email.setCellValueFactory(new PropertyValueFactory<>("email"));
                    email.setMaxWidth(200);
                    email.setMinWidth(200);

                    TableColumn pass = new TableColumn("Password");
                    pass.setCellValueFactory(new PropertyValueFactory<>("pass"));
                    pass.setMaxWidth(0);
                    pass.setMinWidth(0);

                    TableColumn level = new TableColumn("Level");
                    level.setCellValueFactory(new PropertyValueFactory<>("level"));
                    level.setMaxWidth(0);
                    level.setMinWidth(0);

                    tableUser.getColumns().clear();
                    tableUser.getItems().clear();
                    tableUser.getColumns().addAll(id,namaLengkap,alamat,noHp,createdAt,updatedAt,email,pass,level);
                   
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.USER_SEARCH+"&q="+q);

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

                    JSONArray jsonArray = response.getJSONArray("users");

                    ObservableList<Pengguna> data = FXCollections.observableArrayList();

                    for(int i=0; i<jsonArray.length(); i++){

                        String userId = jsonArray.getJSONObject(i).getString("user_id");
                        String userNamaLengkap = jsonArray.getJSONObject(i).getString("user_nama_lengkap");
                        String userAlamat = jsonArray.getJSONObject(i).getString("user_alamat");
                        String userNoHp = jsonArray.getJSONObject(i).getString("user_no_hp");
                        String userCreatedAt = jsonArray.getJSONObject(i).getString("user_created_at");
                        String userUpdatedAt = jsonArray.getJSONObject(i).getString("user_updated_at");
                        String userEmail = jsonArray.getJSONObject(i).getString("login_email");
                        String userPass = jsonArray.getJSONObject(i).getString("login_pass");
                        String userLoginLvl = jsonArray.getJSONObject(i).getString("login_lvl");

                        data.add(
                                new Pengguna(
                                        userId,
                                        userNamaLengkap,
                                        userAlamat,
                                        userNoHp,
                                        StringFex.dateMax(userCreatedAt),
                                        StringFex.dateMax(userUpdatedAt),
                                        userEmail,
                                        userPass,
                                        userLoginLvl
                                )
                        );

                    }

                    Platform.runLater(()->{tableUser.getItems().addAll(data);});

                }else{
                   Platform.runLater(()->{ MsgBox.error("Koneksi internet gagal.");});
                }
                                
                return null;
            }
            
        }
        
        new Thread(new Work()).start();
        
    }
    
    @FXML
    private void tablePenggunaClicked(){
        
        if(tableUser.getSelectionModel().getSelectedItem() != null){
            int ok = MsgBox.confirm("Hapus data pengguna ini ?");
        
            if(ok == 1){
                Pengguna row = (Pengguna) tableUser.getSelectionModel().getSelectedItem();
                deletePengguna(row.getId());
            }
        }
        
        
    }
    
    private void deletePengguna(String userId){
        
        class Work extends Task<Void>{
            
            Loading loading = new Loading();

            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{loading.show();});
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.USER_DELETE+"&user_id="+userId);

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
                    if(!response.getBoolean("error")){
                        String pesan = response.getString("pesan");
                        Platform.runLater(()->{MsgBox.success(pesan);});
                        setPaging();
                    }
                }
                
                Platform.runLater(()->{loading.dismiss();});
                
                return null;
            }
            
        }
        
        new Thread(new Work()).start();
        
    }
    
}
