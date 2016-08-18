package com.merlinbusinesssoftware.merlinsignin;

/**
 * Created by aroras on 13/06/16.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import java.io.File;

public class AlarmReceiver extends BroadcastReceiver {

  //  private String mURL = "http://10.100.15.119:5000/";
    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // For our recurring task, we'll just display a message

        File dir = new File(Environment.getExternalStorageDirectory()+"images");
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
            }
        }

    }

}