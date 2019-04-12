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
public class BarangController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private TableView tableBarang;
    
    private int page = 1;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        setTableBarang();
        
    }    
    
    private void setTableBarang(){
        
        class Work extends Task<Void>{

            @Override
            protected Void call() throws Exception {
                
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
                

                tableBarang.getColumns().clear();
                tableBarang.getItems().clear();
                tableBarang.getColumns().addAll(id,nama,harga);
                
                Loading loading = new Loading();
                
                Platform.runLater(()->{
                    loading.show();
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.DAFTAR_BARANG_SERVIS+"&page="+page);
                
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

                        data.add(
                                new Barang(idj,namaj,hargaj)
                        );

                    }

                    tableBarang.setItems(data);
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
