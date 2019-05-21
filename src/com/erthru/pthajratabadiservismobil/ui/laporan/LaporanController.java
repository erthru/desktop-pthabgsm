/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.laporan;

import com.erthru.pthajratabadiservismobil.ui.beranda.BerandaController;
import com.erthru.pthajratabadiservismobil.ui.laporanprepare.LaporanPrepareController;
import com.erthru.pthajratabadiservismobil.ui.pengguna.PenggunaController;
import com.erthru.pthajratabadiservismobil.utils.ApiEndPoint;
import com.erthru.pthajratabadiservismobil.utils.Loading;
import com.erthru.pthajratabadiservismobil.utils.MsgBox;
import com.erthru.pthajratabadiservismobil.utils.StringFex;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
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
public class LaporanController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private ComboBox<Bulan> comboBulan;
    
    @FXML
    private ComboBox comboTahun;
    
    @FXML
    private Button btnCari;
    
    @FXML
    private Button btnCetak;
    
    @FXML
    private Label lbTotalPesanan;
    
    @FXML
    private Label lbPesananBerjalan;
    
    @FXML
    private Label lbPesananSelesai;
    
    @FXML
    private Label lbPesananDitolak;
    
    @FXML
    private TableView tablePesanan;
    
    @FXML
    private AnchorPane pane;
    
    boolean onCari = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       setComboBulan();
       setComboTahun();
    }  
    
    @FXML
    private void btnCariClicked(){
        
        if(comboBulan.getSelectionModel().getSelectedItem() == null || comboTahun.getSelectionModel().getSelectedItem() == null){
            MsgBox.error("Tentukan tahun dan bulan.");
        }else{
            cariLaporan(comboBulan.getSelectionModel().getSelectedItem().getNo(),comboTahun.getSelectionModel().getSelectedItem().toString());
        }
        
    }
    
    @FXML
    private void btnCetakClicked(){
        
        if(!onCari){
            MsgBox.error("Data belum ditentukan");
        }else{
            try{
            Stage stage = new Stage();
        
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/erthru/pthajratabadiservismobil/ui/laporanprepare/LaporanPrepareFXML.fxml"));
            Parent root = loader.load();

            stage.setScene(new Scene(root));
            stage.setTitle("Preview Cetak");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            
            stage.show();
            
            
        }catch(Exception e){
            e.printStackTrace();
        }         
        }
    }
    
    private void setComboBulan(){
        
        ObservableList<Bulan> data = FXCollections.observableArrayList();
        
        data.add(new Bulan("01","Januari"));
        data.add(new Bulan("02","Februari"));
        data.add(new Bulan("03","Maret"));
        data.add(new Bulan("04","April"));
        data.add(new Bulan("05","Mei"));
        data.add(new Bulan("06","Juni"));
        data.add(new Bulan("07","Juli"));
        data.add(new Bulan("08","Agustus"));
        data.add(new Bulan("09","September"));
        data.add(new Bulan("10","Oktober"));
        data.add(new Bulan("11","November"));
        data.add(new Bulan("12","Desember"));
        
        comboBulan.setItems(data);
        
        StringConverter<Bulan> converter = new StringConverter<Bulan>(){
            
            @Override
            public String toString(Bulan object) {
                return object.getBulan();
            }

            @Override
            public Bulan fromString(String string) {
                return null;
            }
            
        };
        
        comboBulan.setConverter(converter);
        
    }
    
    private void setComboTahun(){
        
        class Work extends Task<Void>{
            
            Loading loading = new Loading();

            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{loading.show();});
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet get = new HttpGet(ApiEndPoint.DAFTAR_TAHUN);

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
                                Logger.getLogger(LaporanController.class.getName()).log(Level.SEVERE, null, ex);
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
                    
                    JSONArray tahun = response.getJSONArray("tahun");
                    ObservableList<String> data = FXCollections.observableArrayList();
                    
                    for(int i=0; i<tahun.length(); i++){
                        data.add(tahun.getJSONObject(i).getString("year"));
                    }
                    
                    Platform.runLater(()->{comboTahun.setItems(data);});
                    
                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi bermasalah");});
                }
                
                return null;
            }
            
        }
        
        new Thread(new Work()).start();
        
    }
    
    private void cariLaporan(String bulan, String tahun){

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
                HttpGet get = new HttpGet(ApiEndPoint.LAPORAN+"&tahun="+tahun+"&bulan="+bulan);

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
                                Logger.getLogger(LaporanController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            return json;
                        } else {
                            System.out.println("Unexpected response status: " + status);
                            return null;
                        }
                    }

                 };

                JSONObject response = httpclient.execute(get, responseHandler);
                System.out.println(response.toString());

                Platform.runLater(()->{loading.dismiss();});

                if(response != null){

                    ObservableList<com.erthru.pthajratabadiservismobil.ui.pesanan.Pesanan> data = FXCollections.observableArrayList();

                    String totalPesanan = response.getString("total_pesanan");
                    String pesananBerjalan = response.getString("pesanan_berjalan");
                    String pesananSelesai = response.getString("pesanan_selesai");
                    String pesananDitolak = response.getString("pesanan_ditolak");
                    JSONArray dataBooking = response.getJSONArray("daftar_pesanan");

                    for(int i=0; i<dataBooking.length(); i++){

                        String bookingId = dataBooking.getJSONObject(i).getString("booking_id");
                        String namaLengkap1 = dataBooking.getJSONObject(i).getString("user_nama_lengkap");
                        String jenisServis1 = dataBooking.getJSONObject(i).getString("booking_jenis_servis");
                        String lastStatus1 = dataBooking.getJSONObject(i).getString("last_status");
                        String createdAt1 = dataBooking.getJSONObject(i).getString("booking_created_at");

                        data.add(new com.erthru.pthajratabadiservismobil.ui.pesanan.Pesanan(
                                bookingId,
                                namaLengkap1,
                                jenisServis1,
                                lastStatus1,
                                StringFex.dateMax(createdAt1)
                        ));
                        
                    }
                    
                    Platform.runLater(()->{
                        lbTotalPesanan.setText("Total Pesanan: "+totalPesanan);
                        lbPesananBerjalan.setText("Pesanan Berjalan: "+pesananBerjalan);
                        lbPesananSelesai.setText("Pesanan Selesai: "+pesananSelesai);
                        lbPesananDitolak.setText("Pesanan Ditolak: "+pesananDitolak);
                        tablePesanan.getItems().addAll(data);
                    });
                    
                    LaporanPrepareController.TABLE_DATA = data;
                    LaporanPrepareController.PESANAN_TOTAL = totalPesanan;
                    LaporanPrepareController.PESANAN_BERJALAN = pesananBerjalan;
                    LaporanPrepareController.PESANAN_SELESAI = pesananSelesai;
                    LaporanPrepareController.PESANAN_DIOLAK = pesananDitolak;
                    LaporanPrepareController.BULAN = comboBulan.getSelectionModel().getSelectedItem().getBulan();
                    LaporanPrepareController.TAHUN = comboTahun.getSelectionModel().getSelectedItem().toString();
                    onCari = true;

                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi internet gagal.");});
                }
                
                return null;
                
            }
            
        }
        
        new Thread(new Work()).start();
        
    }
    
    
}
