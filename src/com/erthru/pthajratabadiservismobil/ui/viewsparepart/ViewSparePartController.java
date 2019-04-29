/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.viewsparepart;

import com.erthru.pthajratabadiservismobil.ui.beranda.BerandaController;
import com.erthru.pthajratabadiservismobil.ui.pesanan.PesananController;
import com.erthru.pthajratabadiservismobil.ui.pesanandetail.PesananDetailController;
import static com.erthru.pthajratabadiservismobil.ui.setsparepartpesanan.SetSparepartPesananController.BOOKING_ID;
import com.erthru.pthajratabadiservismobil.utils.ApiEndPoint;
import com.erthru.pthajratabadiservismobil.utils.Loading;
import com.erthru.pthajratabadiservismobil.utils.MsgBox;
import com.erthru.pthajratabadiservismobil.utils.StringFex;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
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
public class ViewSparePartController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private TableView tableItem;
    
    @FXML
    private Label lbTotal;
    
    @FXML
    private Label lbDesc;
    
    @FXML
    private Button btnSet;
    
    private PesananController parent;
    private PesananDetailController parent1;
    public static String LAST_STATUS;
    public static String BOOKING_BIAYA;
    public static String BOOKING_ID;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        setTableItem();
        
        if(LAST_STATUS.equals("BELUM DIPROSES")){

        }else if(LAST_STATUS.equals("DITERIMA")){
             
        }else if(LAST_STATUS.equals("PEMILIHAN PART")){
            btnSet.setVisible(false);
            lbDesc.setText("* Menunggu pengguna untuk konfirmasi atau perubahan item/sparepart servis");
        }else if(LAST_STATUS.equals("MENUNGGU PERSETUJUAN")){
            lbDesc.setText("* Pengguna telah menentukan sparepart yang ingin digunakan.");
            btnSet.setText("TERIMA SPAREPART YANG DIPILIH");
        }else if(LAST_STATUS.equals("DALAM PENGERJAAN")){
            lbDesc.setText("* Dalam pengerjaan");
            btnSet.setText("SELESAIKAN PESANAN");
        }else if(LAST_STATUS.equals("SELESAI")){
            lbDesc.setText("* Pesanan ini telah selesai.");
            btnSet.setVisible(false);
        }else if(LAST_STATUS.equals("DITOLAK")){
            lbDesc.setText("* Pesanan ini telah ditolak.");
            btnSet.setVisible(false);
        }    
              
    }
    
    private void setTableItem(){
        
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

                    TableColumn nama = new TableColumn("Nama Barang");
                    nama.setCellValueFactory(new PropertyValueFactory<>("nama"));
                    nama.setMinWidth(100);

                    TableColumn harga = new TableColumn("Harga Barang");
                    harga.setCellValueFactory(new PropertyValueFactory<>("harga"));
                    harga.setMinWidth(100);

                    TableColumn kategori = new TableColumn("Kategori");
                    kategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
                    kategori.setMinWidth(100);

                    TableColumn status = new TableColumn("Status");
                    status.setCellValueFactory(new PropertyValueFactory<>("status"));
                    status.setMinWidth(100);

                    tableItem.getColumns().clear();
                    tableItem.getItems().clear();
                    tableItem.getColumns().addAll(id,nama,harga,kategori,status);
                    
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.DAFTAR_BOOKING_ITEM+"&booking_id="+BOOKING_ID);

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

                    ObservableList<Item> data = FXCollections.observableArrayList();
                    JSONArray bookingItem = response.getJSONArray("booking_item");

                    for(int i=0; i<bookingItem.length(); i++){

                        String id1 = bookingItem.getJSONObject(i).getString("booking_item_id");
                        String nama1 = bookingItem.getJSONObject(i).getString("barang_servis_nama");
                        String harga1 = bookingItem.getJSONObject(i).getString("barang_servis_harga");
                        String kategori1 = bookingItem.getJSONObject(i).getString("barang_servis_kategori");
                        String status1 = bookingItem.getJSONObject(i).getString("booking_item_status");

                        data.add(new Item(
                                id1,nama1,harga1,kategori1,status1
                        ));

                    }

                    Platform.runLater(()->{tableItem.getItems().addAll(data);});

                    int totalFromTable = 0;

                    for(int x=0; x<data.size(); x++){

                        if(data.get(x).getStatus().equals("DIPILIH")){
                            totalFromTable += Integer.parseInt(data.get(x).getHarga());
                        }

                    }

                    totalFromTable += Integer.parseInt(BOOKING_BIAYA);
                    int totalHasil = totalFromTable;
                    Platform.runLater(()->{lbTotal.setText("Rp. "+StringFex.strToRp(String.valueOf(totalHasil))+" (Termasuk biaya servis: Rp."+StringFex.strToRp(BOOKING_BIAYA)+")");});

                }else{

                    Platform.runLater(()->{MsgBox.error("Koneksi internet gagal.");

                    Stage stg = (Stage) btnSet.getScene().getWindow();
                    stg.close();});

                }
                
                return null;
            }
            
        }
        
        new Thread(new Work()).start();
    }
    
    @FXML
    private void btnSetClicked(){
        
        if(LAST_STATUS.equals("BELUM DIPROSES")){

        }else if(LAST_STATUS.equals("DITERIMA")){
             
        }else if(LAST_STATUS.equals("PEMILIHAN PART")){
            
        }else if(LAST_STATUS.equals("MENUNGGU PERSETUJUAN")){
            
            int ok = MsgBox.confirm("Setujui sparepart yang dipilih?.");
            
            if(ok == 1){
                setPengerjaan();
            }
            
        }else if(LAST_STATUS.equals("DALAM PENGERJAAN")){
            
            int ok = MsgBox.confirm("Pengerjaan pesanan ini telah selesai?.");
            
            if(ok == 1){
                setSelesai();
            }
                        
        }else if(LAST_STATUS.equals("SELESAI")){
            
        }else if(LAST_STATUS.equals("DITOLAK")){
            
        }    
        
    }
    
    private void setPengerjaan(){
        
        class Work extends Task<Void>{
        
            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                
                    loading.show();
                    
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpPost post = new HttpPost(ApiEndPoint.DALAM_PENGERJAAM_BOOKING);

                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("booking_id",BOOKING_ID));

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

                if(response != null){

                   String pesan = response.getString("pesan");
                   Platform.runLater(()->{

                        MsgBox.success(pesan);

                        parent1.setTableStatus();
                        parent1.loadBookingData();
                        parent.setPaging();

                        Stage stg = (Stage) btnSet.getScene().getWindow();
                        stg.close();

                   });

                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi interner gagal.");});
                }
                
                return null;
            }
        }
        
        new Thread(new Work()).start();
                
    }
    
    private void setSelesai(){
        
        class Work extends Task<Void>{
        
            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                
                    loading.show();
                    
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpPost post = new HttpPost(ApiEndPoint.SELESAI_BOOKING);

                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("booking_id",BOOKING_ID));

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

                if(response != null){

                    String pesan = response.getString("pesan");
                    Platform.runLater(()->{
                        MsgBox.success(pesan);

                        parent1.setTableStatus();
                        parent1.loadBookingData();
                        parent.setPaging();

                        Stage stg = (Stage) btnSet.getScene().getWindow();
                        stg.close();
                    });

                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi interner gagal.");});
                }
                
                return null;
            }
        }
        
        new Thread(new Work()).start();
        
    }
    
    public void setParent(PesananController parent, PesananDetailController parent1){
        this.parent = parent;
        this.parent1 = parent1;
    }
    
}
