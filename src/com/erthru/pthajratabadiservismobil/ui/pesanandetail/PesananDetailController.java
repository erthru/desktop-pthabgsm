/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.pesanandetail;

import com.erthru.pthajratabadiservismobil.ui.beranda.BerandaController;
import com.erthru.pthajratabadiservismobil.ui.pengguna.PenggunaController;
import com.erthru.pthajratabadiservismobil.ui.pesanan.PesananController;
import com.erthru.pthajratabadiservismobil.ui.setsparepartpesanan.SetSparepartPesananController;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
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
public class PesananDetailController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private Label lbInvoice;
    
    @FXML
    private Label lbJenisServis;
    
    @FXML
    private Label lbKeterangan;
    
    @FXML
    private Label lbTglPesanan;
    
    @FXML
    private Label lbNamaPemesan;
    
    @FXML
    private Label lbAlamat;
    
    @FXML
    private Label lbNohp;
    
    @FXML
    private TableView tableStatus;
    
    @FXML
    private Button btnSet;
    
    @FXML
    private Button btnTolak;
    
    @FXML
    private Label lbBottomKet;
    
    public static String BOOKING_ID;
    private String lastStatus;
    
    private PesananController parent;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadBookingData();
        setTableStatus();
    } 
    
    @FXML
    private void btnSetClicked() throws Exception{
        if(lastStatus.equals("BELUM DIPROSES")){
            
            int confirm = MsgBox.confirm("Terima pesanan ini ?");
            
            if(confirm == 1){
                terimaBooking();
            }
            
        }else if(lastStatus.equals("DITERIMA")){
             
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/erthru/pthajratabadiservismobil/ui/setsparepartpesanan/SetSparepartPesananFXML.fxml"));
            Parent root = loader.load();
            
            stage.setScene(new Scene(root));
            stage.setTitle("Set Sparepart");
            stage.setResizable(true);
            stage.initModality(Modality.APPLICATION_MODAL);
            
            root.requestFocus();
            SetSparepartPesananController child = loader.getController();
            child.setParent(parent,this);
            stage.show();
            
            Stage stg = (Stage) btnSet.getScene().getWindow();
            stg.close();
            
        }else if(lastStatus.equals("PEMILIHAN PART")){
            
        }else if(lastStatus.equals("MENUNGGU PERSETUJUAN")){
            
        }else if(lastStatus.equals("DALAM PENGERJAAN")){
            
        }else if(lastStatus.equals("SELESAI")){
            
        }else if(lastStatus.equals("DITOLAK")){
            
        }
    }
    
    @FXML
    private void btnTolakClicked(){
        
    }
    
    private void setTableStatus(){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                    try{
                        
                        loading.show();
                    
                        TableColumn id = new TableColumn("ID");
                        id.setCellValueFactory(new PropertyValueFactory<>("id"));
                        id.setMaxWidth(0);
                        id.setMinWidth(0);

                        TableColumn createdAt = new TableColumn("Tgl");
                        createdAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
                        createdAt.setMinWidth(100);

                        TableColumn stat = new TableColumn("Status");
                        stat.setCellValueFactory(new PropertyValueFactory<>("stat"));
                        stat.setMinWidth(100);

                        tableStatus.getColumns().clear();
                        tableStatus.getItems().clear();
                        tableStatus.getColumns().addAll(id,createdAt,stat);
                        
                         CloseableHttpClient httpclient = HttpClients.createDefault();
                        HttpGet get = new HttpGet(ApiEndPoint.DAFTAR_BOOKING_STATUS+"&booking_id="+BOOKING_ID);

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

                            ObservableList<Status> data = FXCollections.observableArrayList();

                            JSONArray bookingStatus = response.getJSONArray("booking_status");

                            for(int i=0; i<bookingStatus.length(); i++){

                                String id1 = bookingStatus.getJSONObject(i).getString("booking_status_id");
                                String createdAt1 = bookingStatus.getJSONObject(i).getString("booking_status_created_at");
                                String stat1 = bookingStatus.getJSONObject(i).getString("booking_status_stat");

                                data.add(new Status(
                                        id1,StringFex.dateMax(createdAt1),stat1
                                ));

                            }

                            tableStatus.getItems().addAll(data);

                        }else{

                            MsgBox.error("Koneksi internet gagal.");

                            Stage stg = (Stage) btnSet.getScene().getWindow();
                            stg.close();

                        }
                        
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    
                });
                                               
                return null;
            }
            
        }
        
        new Thread(new Work()).start();
        
    }
    
    private void loadBookingData(){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                    try{
                        
                        loading.show();
                        
                        CloseableHttpClient httpclient = HttpClients.createDefault();
                        HttpGet get = new HttpGet(ApiEndPoint.DAFTAR_BOOKING_DETAIL+"&booking_id="+BOOKING_ID);

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

                        loading.dismiss();

                        if(response != null){

                            String bookingId = response.getJSONObject("data_booking").getString("booking_id");
                            String bookingJenisServis = response.getJSONObject("data_booking").getString("booking_jenis_servis");
                            String bookingKeterangan = response.getJSONObject("data_booking").getString("booking_keterangan");
                            String bookingCreatedAt = response.getJSONObject("data_booking").getString("booking_created_at");
                            String userId = response.getJSONObject("data_booking").getString("user_id");
                            String dealerId = response.getJSONObject("data_booking").getString("dealer_id");
                            String dealerNama = response.getJSONObject("data_booking").getString("dealer_nama");
                            String dealerAlamat = response.getJSONObject("data_booking").getString("dealer_alamat");
                            String userNamaLengkap = response.getJSONObject("data_booking").getString("user_nama_lengkap");
                            String userAlamat = response.getJSONObject("data_booking").getString("user_alamat");
                            String userNohp = response.getJSONObject("data_booking").getString("user_no_hp");
                            String userCreatedAt = response.getJSONObject("data_booking").getString("user_created_at");
                            String userUpdatedAt = response.getJSONObject("data_booking").getString("user_updated_at");
                            lastStatus = response.getJSONObject("data_booking").getString("last_status");

                            lbInvoice.setText("#"+bookingId);
                            lbJenisServis.setText(bookingJenisServis);
                            lbKeterangan.setText(bookingKeterangan);
                            lbTglPesanan.setText(StringFex.dateMax(bookingCreatedAt));
                            lbNamaPemesan.setText(userNamaLengkap);
                            lbAlamat.setText(userAlamat);
                            lbNohp.setText(userNohp);

                            btnSet.setVisible(true);
                            btnTolak.setVisible(true);

                            if(lastStatus.equals("BELUM DIPROSES")){
                                btnSet.setText("TERIMA PESANAN INI");
                                lbBottomKet.setText("Pesanan ini belum diproses");
                            }else if(lastStatus.equals("DITERIMA")){
                                btnSet.setText("TENTUKAN SPAREPART SERVIS");
                                lbBottomKet.setText("Pesanan ini telah diterima. Tekan tombol di atas untuk menentukan sparepart yang akan diganti/servis.");
                            }else if(lastStatus.equals("PEMILIHAN PART")){
                                btnSet.setText("LIHAT DAFTAR SPAREPART YANG DIPILIH");
                                lbBottomKet.setText("Menunggu persetujuan dari pengguna untuk pemilihan part yang telah ditentukan");
                            }else if(lastStatus.equals("MENUNGGU PERSETUJUAN")){
                                btnSet.setText("LIHAT SPAREPART YANG DIPILIH");
                                lbBottomKet.setText("Pengguna telah menentukan sparepart yang ingin digunakan.");
                            }else if(lastStatus.equals("DALAM PENGERJAAN")){
                                btnSet.setText("SET PESANAN INI TELAH SELESAI");
                                lbBottomKet.setText("Tekan tombol di atas jika pesanan ini telah selesai.");
                            }else if(lastStatus.equals("SELESAI")){
                                btnSet.setVisible(false);
                                btnTolak.setVisible(false);
                                lbBottomKet.setText("Pesanan ini telah selesai");
                            }else if(lastStatus.equals("DITOLAK")){
                                btnSet.setVisible(false);
                                btnTolak.setVisible(false);
                                lbBottomKet.setText("Pesanan ini telah ditolak");
                            }

                        }else{
                            MsgBox.error("Koneksi internet gagal.");

                            Stage stg = (Stage) btnSet.getScene().getWindow();
                            stg.close();
                        }
                        
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                });
                

                return null;
            }
            
        }
        
        new Thread(new Work()).start();
        
    }
    
    private void terimaBooking(){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                    try{
                        
                        loading.show();
                        
                        CloseableHttpClient httpclient = HttpClients.createDefault();
                        HttpPost post = new HttpPost(ApiEndPoint.TERIMA_BOOKING);

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

                        loading.dismiss();

                        if(response != null){

                            Boolean error = response.getBoolean("error");
                            String pesan = response.getString("pesan");

                            if(!error){

                                MsgBox.success(pesan);

                                parent.setPaging();

                                Stage stg = (Stage) btnSet.getScene().getWindow();
                                stg.close();

                            }else{
                                MsgBox.error(pesan); 
                            }

                        }
                        
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                });
                
                
                
                return null;
            }
            
        }
        
        new Thread(new Work()).start();
        
    }
    
    public void setParent(PesananController parent){
        this.parent = parent;
    }
    
}
