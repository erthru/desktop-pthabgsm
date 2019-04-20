/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.setsparepartpesanan;

import com.erthru.pthajratabadiservismobil.ui.addsparepart.AddSparePartController;
import com.erthru.pthajratabadiservismobil.ui.beranda.BerandaController;
import com.erthru.pthajratabadiservismobil.ui.pesanan.PesananController;
import com.erthru.pthajratabadiservismobil.ui.pesanandetail.PesananDetailController;
import com.erthru.pthajratabadiservismobil.utils.ApiEndPoint;
import com.erthru.pthajratabadiservismobil.utils.Loading;
import com.erthru.pthajratabadiservismobil.utils.LocalDB;
import com.erthru.pthajratabadiservismobil.utils.MsgBox;
import com.erthru.pthajratabadiservismobil.utils.StringFex;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
public class SetSparepartPesananController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private TextField txCari;
    
    @FXML
    private TableView tableBarang;
    
    @FXML
    private Pagination paging;
    
    @FXML
    private TableView tableSelectedBarang;
    
    @FXML
    private TextField txBiaya;
    
    @FXML
    private Button btnSet;
    
    @FXML
    private Button btnReset;
    
    private PesananController parent;
    private PesananDetailController parent1;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setPaging();
        setCustomListener();
    }  
    
    @FXML
    private void btnResetClicked() throws Exception{
        
        LocalDB.con().createStatement().executeUpdate("DELETE FROM tb_selected_barang");
        setTableSelectedBarang();
        
    }
    
    @FXML
    private void txBiayaOnTextChanged(){
        
        txBiaya.setText(StringFex.numberWithComaOnly(txBiaya.getText()));
        txBiaya.positionCaret(txBiaya.getText().length());
        
    }
    
    private void setCustomListener(){
                
        Platform.runLater(()->{
            
            try{
                
                paging.currentPageIndexProperty().addListener((obs,oldIndex,newIndex)->
                    setTableBarang(String.valueOf(Integer.parseInt(newIndex.toString())+1))
                );

                Stage thisStage = (Stage) btnSet.getScene().getWindow();
                thisStage.setOnHiding(event -> {
                    try {
                        LocalDB.con().createStatement().executeUpdate("DELETE FROM tb_selected_barang");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                
            }catch(Exception e){
                e.printStackTrace();
            }
            
        });
        
    }
    
    @FXML
    private void btnSetClicked(){
        
        
    }
    
    @FXML
    private void txCariOnTextChanged(){
        if(txCari.getText().isEmpty()){
            setPaging();
            paging.setVisible(true);
        }else{
            
            paging.setVisible(false);
            searchTableBarang(txCari.getText());
            
        }
    }
    
    @FXML
    private void tableBarangOnClicked() throws Exception{
        
        Barang barang = (Barang) tableBarang.getSelectionModel().getSelectedItem();
        AddSparePartController.barang = barang;
        
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/erthru/pthajratabadiservismobil/ui/addsparepart/AddSparePartFXML.fxml"));
        Parent root = loader.load();
        
        stage.setScene(new Scene(root));
        stage.setTitle("Tambah Sparepart");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        
        root.requestFocus();
        AddSparePartController child = loader.getController();
        child.setParent(parent, parent1, this);
        stage.show();
        
    }
    
    @FXML
    private void tableSelectedBarangClicked() throws Exception{
        
        if(tableSelectedBarang.getSelectionModel().getSelectedItem() != null){
            int ok = MsgBox.confirm("Keluarkan data ini ?");
        
            if(ok == 1){
                
                SelectedBarang selectedBarang = (SelectedBarang) tableSelectedBarang.getSelectionModel().getSelectedItem();
                LocalDB.con().createStatement().executeUpdate("DELETE FROM tb_selected_barang WHERE selected_barang_id = '"+selectedBarang.getId()+"'");
                setTableSelectedBarang();
                
            }
        }
        
    }
    
    public void setParent(PesananController parent, PesananDetailController parent1){
        this.parent = parent;
        this.parent1 = parent1;
    }
    
    public void addSelectedBarang(ArrayList<Barang> barangArr){
                
        for(int i=0; i<barangArr.size(); i++){
            
            try{
                LocalDB.con().createStatement().executeUpdate("INSERT INTO tb_selected_barang (selected_barang_code,selected_barang_nama,selected_barang_harga,selected_barang_kategori) VALUES ('"+barangArr.get(i).getId()+"','"+barangArr.get(i).getNama()+"','"+barangArr.get(i).getHarga()+"','"+barangArr.get(i).getKategori()+"')");
            }catch(Exception e){
                e.printStackTrace();
            }
            
        }
        
        setTableSelectedBarang();
        
    }
    
    private void setPaging(){
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                    try{
                        loading.show();
                        
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

                        loading.dismiss();

                        if(response != null){

                            String total = response.getString("total");

                            paging.setPageCount(Integer.parseInt(total) / 10 + 1);
                            paging.setCurrentPageIndex(0);

                            setTableBarang("1");

                        }else{
                            MsgBox.error("Koneksi internet gagal.");
                        }
                        
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                });
                
                
                
                return null;
            }
            
        }
        
        Work work = new Work();
        Thread t = new Thread(work);
        t.start();
        
        
    }
    
    private void setTableBarang(String page){
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                                                        
                    try{
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
                        
                        loading.show();
                        
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

                        loading.dismiss();

                        if(response != null){
                            JSONArray daftarBarangServis = response.getJSONArray("daftar_barang_servis");

                            ObservableList<Barang> data = FXCollections.observableArrayList();

                            for(int i=0; i<daftarBarangServis.length(); i++){

                                String idj = daftarBarangServis.getJSONObject(i).getString("barang_servis_id");
                                String namaj = daftarBarangServis.getJSONObject(i).getString("barang_servis_nama");
                                String hargaj = daftarBarangServis.getJSONObject(i).getString("barang_servis_harga");
                                String kategorij = daftarBarangServis.getJSONObject(i).getString("barang_servis_kategori");

                                data.add(
                                        new Barang(idj,namaj,"Rp. "+StringFex.numberWithComaOnly(hargaj),kategorij)
                                );

                            }

                            tableBarang.setItems(data);

                        }else{
                            MsgBox.error("Koneksi internet gagal.");
                        }           
                        
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                
                });

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
                
                    try{
                        
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
                                        new Barang(idj,namaj,"Rp. "+StringFex.numberWithComaOnly(hargaj),kategorij)
                                );

                            }

                            tableBarang.setItems(data);

                        }else{
                            MsgBox.error("Koneksi internet gagal.");
                        }           
                        
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                
                });
                

                return null;
            }
            
        }
        
        Work work = new Work();
        Thread t = new Thread(work);
        t.start();
    }
    
    private void setTableSelectedBarang(){
        class Work extends Task<Void>{
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                
                   
                    try{
                        
                        TableColumn id = new TableColumn("ID");
                        id.setCellValueFactory(new PropertyValueFactory<>("id"));
                        id.setMaxWidth(0);
                        id.setMinWidth(0);

                        TableColumn code = new TableColumn("CODE");
                        code.setCellValueFactory(new PropertyValueFactory<>("code"));
                        code.setMaxWidth(0);
                        code.setMinWidth(0);

                        TableColumn nama = new TableColumn("Nama");
                        nama.setCellValueFactory(new PropertyValueFactory<>("nama"));
                        nama.setMinWidth(300);

                        TableColumn harga = new TableColumn("Harga");
                        harga.setCellValueFactory(new PropertyValueFactory<>("harga"));
                        harga.setMinWidth(100);

                        TableColumn kategori = new TableColumn("Kategori");
                        kategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
                        kategori.setMinWidth(50);

                        tableSelectedBarang.getColumns().clear();
                        tableSelectedBarang.getItems().clear();
                        tableSelectedBarang.getColumns().addAll(id,code,nama,harga,kategori);
                        
                        ObservableList<SelectedBarang> data = FXCollections.observableArrayList();
                        
                        ResultSet rs = LocalDB.con().createStatement().executeQuery("SELECT * FROM tb_selected_barang ORDER BY selected_barang_nama ASC");
                        
                        while(rs.next()){
                            
                            data.add(new SelectedBarang(
                                    rs.getString("selected_barang_id"),
                                    rs.getString("selected_barang_code"),
                                    rs.getString("selected_barang_nama"),
                                    "Rp. "+StringFex.numberWithComaOnly(rs.getString("selected_barang_harga")),
                                    rs.getString("selected_barang_kategori")
                            ));
                            
                        }
                        
                        tableSelectedBarang.getItems().addAll(data);
                        
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                
                });           
                               
                return null;
            }
            
        }
        
        Work work = new Work();
        Thread t = new Thread(work);
        t.start();
    }
    
}
