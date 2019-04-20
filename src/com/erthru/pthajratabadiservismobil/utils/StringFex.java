/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;

/**
 *
 * @author supriantodjamalu
 */
public class StringFex {
    
    public static String dateMax(String date){
        
        HashMap<String, String> bulan = new HashMap<>();
        bulan.put("01", "Januari");
        bulan.put("02", "Februari");
        bulan.put("03", "Maret");
        bulan.put("04", "April");
        bulan.put("05", "Mei");
        bulan.put("06", "Juni");
        bulan.put("07", "Juli");
        bulan.put("08", "Agustus");
        bulan.put("09", "September");
        bulan.put("10", "Oktober");
        bulan.put("11", "November");
        bulan.put("12", "Desember");
        
        return date.substring(8,10) + " " + bulan.get(date.substring(5,7)) + " " + date.substring(0,4) + " | " + date.substring(11);
        
    }
    
    public static String strToRp(String text){
        
        DecimalFormat kurs = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols dcs = new DecimalFormatSymbols();
        
        dcs.setGroupingSeparator(',');
        kurs.setDecimalFormatSymbols(dcs);
        
        String rp = kurs.format(Double.parseDouble(text)).replace("IDR", "").replace("$", "");
        
        return rp.substring(0,rp.length() - 3);

    }
    
    public static String numberWithComaOnly(String value){
        
        String number = value.replaceAll("\\D+", "");
        
        if(number.equals("")){
            return "";
        }else{
            return strToRp(number);
        }
        
    }

    
}
