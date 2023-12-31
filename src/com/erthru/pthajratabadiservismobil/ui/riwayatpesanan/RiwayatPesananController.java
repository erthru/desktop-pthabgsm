/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.riwayatpesanan;

import com.erthru.pthajratabadiservismobil.ui.pengguna.PenggunaController;
import com.erthru.pthajratabadiservismobil.ui.pesanan.PesananController;
import com.erthru.pthajratabadiservismobil.ui.pesanandetail.PesananDetailController;
import com.erthru.pthajratabadiservismobil.utils.ApiEndPoint;
import com.erthru.pthajratabadiservismobil.utils.Loading;
import com.erthru.pthajratabadiservismobil.utils.MsgBox;
import com.erthru.pthajratabadiservismobil.utils.StringFex;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
public class RiwayatPesananController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private DatePicker dateB;
    
    @FXML
    private DatePicker dateA;
    
    @FXML
    private Button btnFilter;
    
    @FXML
    private TableView tablePesanan;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
                
        dateB.setValue(LocalDate.now());
        dateA.setValue(LocalDate.now());
        
        String before = dateB.getValue().toString();
        String after = dateA.getValue().toString();
        
        setTablePesanan(before,after);
        
    }  
    
    
    @FXML
    private void btnFilterClicked(){
        String before = dateB.getValue().toString();
        String after = dateA.getValue().toString();
        
        System.out.println("Date before selected is: "+before);
        System.out.println("Date after selected is: "+after);
        
        setTablePesanan(before,after);
    }
    
    @FXML
    private void tablePesananClicked() throws Exception{
        Pesanan pesanan = (Pesanan) tablePesanan.getSelectionModel().getSelectedItem();
        PesananDetailController.BOOKING_ID = pesanan.getId();
        
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(PesananController.class.getResource("/com/erthru/pthajratabadiservismobil/ui/pesanandetail/PesananDetailFXML.fxml"));
        Parent root = loader.load();
        
        stage.setScene(new Scene(root));
        stage.setTitle("Pesanan Detail");
        stage.setResizable(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        
        root.requestFocus();
        PesananDetailController child = loader.getController();
        child.setParent(null);
        stage.show();
    }
    
    private void setTablePesanan(String dateB, String dateA){
        
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
                HttpGet get = new HttpGet(ApiEndPoint.RIWAYAT_BOOKING_ALL+"&date_b="+dateB+"&date_a="+dateA);

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

                    ObservableList<Pesanan> data = FXCollections.observableArrayList();

                    JSONArray dataBooking = response.getJSONArray("data_booking");

                    for(int i=0; i<dataBooking.length(); i++){

                        String bookingId = dataBooking.getJSONObject(i).getString("booking_id");
                        String namaLengkap1 = dataBooking.getJSONObject(i).getString("user_nama_lengkap");
                        String jenisServis1 = dataBooking.getJSONObject(i).getString("booking_jenis_servis");
                        String lastStatus1 = dataBooking.getJSONObject(i).getString("last_status");
                        String createdAt1 = dataBooking.getJSONObject(i).getString("booking_created_at");

                        data.add(new Pesanan(
                                bookingId,
                                namaLengkap1,
                                jenisServis1,
                                lastStatus1,
                                StringFex.dateMax(createdAt1)
                        ));
                        
                        System.out.println(data.size());

                    }
                    
                    System.out.println("printing response");
                    Platform.runLater(()->{tablePesanan.getItems().addAll(data);});

                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi internet gagal.");});
                }
                
                return null;
            }
            
        }
        
        new Thread(new Work()).start();
        
    }
    
}
