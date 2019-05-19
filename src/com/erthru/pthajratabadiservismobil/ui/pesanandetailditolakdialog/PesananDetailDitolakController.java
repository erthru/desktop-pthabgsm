/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.pesanandetailditolakdialog;

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
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author supriantodjamalu
 */
public class PesananDetailDitolakController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private TextArea txAlasan;
    
    @FXML
    private Button btnTolak;
    
    @FXML
    private Button btnBatal;
    
    PesananDetailController parent;
    PesananController parent1;
    
    public static String BOOKING_ID;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void setParent(PesananDetailController parent, PesananController parent1){
        this.parent = parent;
        this.parent1 = parent1;
    }
    
    @FXML
    private void btnTolakClicked(){
        
        int ok = MsgBox.confirm("Tolak pesanan ini ?");
        
        if(ok==1){
            if(txAlasan.getText().equals("")){
                MsgBox.error("Alasan harus diisi");
            }else{
                tolakPesanan();
            }
        }
        
    }
    
    @FXML
    private void btnBatalClicked(){
        Stage stg = (Stage) btnTolak.getScene().getWindow();
        stg.close();
    }
    
    private void tolakPesanan(){
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                    
                    loading.show();
                    
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpPost post = new HttpPost(ApiEndPoint.DITOLAK_BOOKING);

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
                                Logger.getLogger(PesananDetailDitolakController.class.getName()).log(Level.SEVERE, null, ex);
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
                    Boolean error = response.getBoolean("error");

                    if(!error){

                        setDitolakAlasan();

                    }else{
                       Platform.runLater(()->{ MsgBox.error(pesan); });
                    }

                }
                
                return null;
            }
            
        }
        
        new Thread(new Work()).start();
    }
    
    private void setDitolakAlasan(){
        
        class Work extends Task<Void>{
            
            Loading loading = new Loading();

            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{
                    
                    loading.show();
                    
                });
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpPost post = new HttpPost(ApiEndPoint.SET_ALASAN_BOOKING_DITOLAK);

                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("booking_id",BOOKING_ID));
                params.add(new BasicNameValuePair("alasan",txAlasan.getText()));

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
                                Logger.getLogger(PesananDetailDitolakController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            return json;
                        } else {
                            System.out.println("Unexpected response status: " + status);
                            return null;
                        }
                    }

                 };

                JSONObject response = httpclient.execute(post, responseHandler);
                
                Platform.runLater(()->{
                    
                    loading.dismiss();
                    
                });
                
                if(response != null){
                    
                    String pesan = response.getString("pesan");
                    
                    Platform.runLater(()->{MsgBox.success(pesan);

                        parent1.setPaging();
                        parent.dispose();

                        Stage stg = (Stage) btnTolak.getScene().getWindow();
                        stg.close();});

                    }
                
                return null;
            }
            
        }
        
        new Thread(new Work()).start();
        
    }
    
}
