/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.login;

import com.erthru.pthajratabadiservismobil.ui.beranda.BerandaController;
import static com.erthru.pthajratabadiservismobil.ui.setsparepartpesanan.SetSparepartPesananController.BOOKING_ID;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
public class LoginController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private TextField txEmail;
    
    @FXML
    private PasswordField txPassword;
    
    @FXML
    private Button btnLogin;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void btnLoginClicked(){
        login();
    }
    
    private void login(){
        
        class Work extends Task<Void>{

            Loading loading = new Loading();
            
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(()->{loading.show();});
                
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpPost post = new HttpPost(ApiEndPoint.LOGIN_ADMIN);

                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("login_email",txEmail.getText()));
                params.add(new BasicNameValuePair("login_pass",txPassword.getText()));

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
                    
                    Boolean error = response.getBoolean("error");
                    String pesan = response.getString("pesan");
                    
                    if(!error){
                        
                        Platform.runLater(()->{
                        
                            Stage stage = new Stage();
                            FXMLLoader loader = new FXMLLoader(LoginController.class.getResource("/com/erthru/pthajratabadiservismobil/ui/main/MainFXML.fxml"));
                            Parent root;
                            
                            try {
                                root = loader.load();
                                stage.setScene(new Scene(root));
                                stage.setTitle("Main");
                                stage.setMaximized(true);
                                stage.setResizable(true);

                                root.requestFocus();
                                stage.show();
                                
                                Stage stg = (Stage) btnLogin.getScene().getWindow();
                                stg.close();
                                
                            } catch (IOException ex) {
                                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            
                            
                        });
                        
                    }else{
                        
                        Platform.runLater(()->{MsgBox.error(pesan);});
                        
                    }
                    
                }else{
                    Platform.runLater(()->{MsgBox.error("Koneksi internet gagal.");});
                }
                
                return null;
            }
            
        }
        
        new Thread(new Work()).start();
        
    }
    
}
