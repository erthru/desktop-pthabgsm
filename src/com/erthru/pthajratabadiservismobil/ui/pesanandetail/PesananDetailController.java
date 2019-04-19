/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.pesanandetail;

import com.erthru.pthajratabadiservismobil.ui.pengguna.PenggunaController;
import com.erthru.pthajratabadiservismobil.ui.pesanan.PesananController;
import com.erthru.pthajratabadiservismobil.utils.ApiEndPoint;
import com.erthru.pthajratabadiservismobil.utils.Loading;
import com.erthru.pthajratabadiservismobil.utils.MsgBox;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
    private PesananController parent;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadBookingData();
    } 
    
    @FXML
    private void btnSetClicked(){
        
    }
    
    @FXML
    private void btnTolakClicked(){
        
    }
    
    private void loadBookingData(){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{loading.show();});
                
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
                
                Platform.runLater(()->{loading.dismiss();});
                
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
                    String lastStatus = response.getJSONObject("data_booking").getString("last_status");
                    
                    Platform.runLater(()->{
                        lbInvoice.setText("#"+bookingId);
                        lbJenisServis.setText(bookingJenisServis);
                        lbKeterangan.setText(bookingKeterangan);
                        lbTglPesanan.setText(bookingCreatedAt);
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
                            btnSet.setVisible(false);
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
                        
                    });
                    
                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi internet gagal.");});
                    
                    Stage stg = (Stage) btnSet.getScene().getWindow();
                    stg.close();
                }
                
                return null;
            }
            
        }
        
        new Thread(new Work()).start();
        
    }
    
    public void setParent(PesananController parent){
        this.parent = parent;
    }
    
}
