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
    
    private PesananController parent;
    private PesananDetailController parent1;
    
    private ArrayList<Barang> selectedBarangArr = new ArrayList<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setPaging();
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
    private void tableSelectedBarangClicked(){
        
        if(tableSelectedBarang.getSelectionModel().getSelectedItem() != null){
            int ok = MsgBox.confirm("Keluarkan data ini ?");
        
            if(ok == 1){
                selectedBarangArr.remove(tableSelectedBarang.getSelectionModel().getSelectedIndex());
                System.out.print(selectedBarangArr.toString());
                setTableSelectedBarang();
            }
        }
        
    }
    
    public void setParent(PesananController parent, PesananDetailController parent1){
        this.parent = parent;
        this.parent1 = parent1;
    }
    
    public void addSelectedBarang(ArrayList<Barang> selectedBarangArr){
                
        this.selectedBarangArr.addAll(selectedBarangArr);
        setTableSelectedBarang();
        
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
                    
                    setTableBarang("1");
                    
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
                setTableBarang(String.valueOf(Integer.parseInt(newIndex.toString())+1))
        );
    }
    
    private void setTableBarang(String page){
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
    
    private void setTableSelectedBarang(){
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
                    nama.setSortType(TableColumn.SortType.ASCENDING);

                    TableColumn harga = new TableColumn("Harga");
                    harga.setCellValueFactory(new PropertyValueFactory<>("harga"));
                    harga.setMinWidth(100);

                    TableColumn kategori = new TableColumn("Kategori");
                    kategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
                    kategori.setMinWidth(50);

                    tableSelectedBarang.getColumns().clear();
                    tableSelectedBarang.getItems().clear();
                    tableSelectedBarang.getColumns().addAll(id,nama,harga,kategori);
                                        
                    ObservableList<Barang> data = FXCollections.observableArrayList();
                
                    for(int i=0; i<selectedBarangArr.size(); i++){
                        data.add(new Barang(
                                selectedBarangArr.get(i).getId(),
                                selectedBarangArr.get(i).getNama(),
                                selectedBarangArr.get(i).getHarga(),
                                selectedBarangArr.get(i).getKategori()
                        ));
                    }
                    
                    tableSelectedBarang.getItems().addAll(data);
                    tableSelectedBarang.getSortOrder().add(nama);
                
                });           
                               
                return null;
            }
            
        }
        
        Work work = new Work();
        Thread t = new Thread(work);
        t.start();
    }
    
}
