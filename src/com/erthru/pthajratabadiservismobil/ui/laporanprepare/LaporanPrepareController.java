/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.ui.laporanprepare;

import com.erthru.pthajratabadiservismobil.utils.Loading;
import com.erthru.pthajratabadiservismobil.utils.MsgBox;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrintResolution;
import javafx.print.Printer;
import javafx.print.PrinterAttributes;
import javafx.print.PrinterJob;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;

/**
 * FXML Controller class
 *
 * @author supriantodjamalu
 */
public class LaporanPrepareController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    public static ObservableList<com.erthru.pthajratabadiservismobil.ui.pesanan.Pesanan> TABLE_DATA;
    public static String PESANAN_TOTAL,PESANAN_BERJALAN,PESANAN_SELESAI,PESANAN_DIOLAK,BULAN,TAHUN;
    
    @FXML
    private AnchorPane pane;
    
    @FXML
    private Label lbPesananTotal;
    
    @FXML
    private Label lbPesananBerjalan;
    
    @FXML
    private Label lbPesananSelesai;
    
    @FXML
    private Label lbPesananDitolak;
    
    @FXML
    private Label lbLaporanPada;
    
    @FXML
    private TableView tablePesanan;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
        Platform.runLater(()->{
        
            lbPesananTotal.setText("Pesanan Total: "+PESANAN_TOTAL);
            lbPesananBerjalan.setText("Pesanan Berjalan: "+PESANAN_BERJALAN);
            lbPesananSelesai.setText("Pesanan Selesai: "+PESANAN_SELESAI);
            lbPesananDitolak.setText("Pesanan Ditolak: "+PESANAN_DIOLAK);
            lbLaporanPada.setText("Laporan Bulan "+BULAN+" "+TAHUN);
            
            TableColumn id = new TableColumn("NO. Invoice");
            id.setCellValueFactory(new PropertyValueFactory<>("id"));
            id.setMinWidth(30);

            TableColumn namaLengkap = new TableColumn("Nama Lengkap");
            namaLengkap.setCellValueFactory(new PropertyValueFactory<>("namaLengkap"));
            namaLengkap.setMinWidth(150);

            TableColumn jenisServis = new TableColumn("Jenis Servis");
            jenisServis.setCellValueFactory(new PropertyValueFactory<>("jenisServis"));
            jenisServis.setMinWidth(50);

            TableColumn status = new TableColumn("Status");
            status.setCellValueFactory(new PropertyValueFactory<>("status"));
            status.setMinWidth(100);

            TableColumn createdAt = new TableColumn("Tgl. Pesanan");
            createdAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            createdAt.setMinWidth(150);

            tablePesanan.getColumns().clear();
            tablePesanan.getItems().clear();
            tablePesanan.getColumns().addAll(id,namaLengkap,jenisServis,status,createdAt);
            tablePesanan.setItems(TABLE_DATA);
        
        });
        
    }  
    
    @FXML
    private void btnCetakClicked(){
        Loading loading = new Loading();
        Platform.runLater(()->{loading.show();});
        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.EQUAL);
        
        if(printer != null){
            
            System.out.println(printer.toString());
            PrinterJob job = PrinterJob.createPrinterJob();
            
            if(job != null){
                Platform.runLater(()->{loading.dismiss();});
                pane.setPrefSize(pageLayout.getPrintableWidth(), pageLayout.getPrintableHeight());
                
                if(job.showPrintDialog(pane.getScene().getWindow())){
                                        
                    double scaleX= pageLayout.getPrintableWidth() / pane.getBoundsInParent().getWidth();
                    double scaleY= pageLayout.getPrintableHeight() / pane.getBoundsInParent().getHeight();
                    
                    Scale scale = new Scale(scaleX, scaleY);
                    
                    pane.getTransforms().add(scale);
                    
                    boolean success = job.printPage(pageLayout,pane);
                    if(success){
                        job.endJob();
                    }
                    
                    pane.getTransforms().remove(scale);
                }
                
            }
        }else{
            Platform.runLater(()->{loading.dismiss();});
            MsgBox.error("Tidak ada printer yg ready.");
        }   
    }
    
}
