package com.merlinbusinesssoftware.merlinsignin;

import android.support.v4.app.FragmentActivity;


/**
 * Created by aroras on 28/07/16.
 */
public class MyBaseFragmentAcivity extends FragmentActivity {
    public DatabaseHandler db;
    public com.merlinbusinesssoftware.merlinsignin.structures.StructSettings StructSettings;

    public int findTabId(){
        db = new DatabaseHandler(this);
        StructSettings = db.getAllSettings();
        return  StructSettings.getTabletId();
    }

}

