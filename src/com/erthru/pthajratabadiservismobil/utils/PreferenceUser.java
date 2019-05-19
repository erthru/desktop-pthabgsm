/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erthru.pthajratabadiservismobil.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author supriantodjamalu
 */
public class PreferenceUser {
    
    private Preferences pref = Preferences.userRoot();
    
    public void create(){
        pref.putBoolean("isLogin", true);
    }
    
    public boolean isLogin(){
        return pref.getBoolean("isLogin", false);
    }
    
    public void destroy(){
        try {
            pref.clear();
        } catch (BackingStoreException ex) {
            Logger.getLogger(PreferenceUser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
