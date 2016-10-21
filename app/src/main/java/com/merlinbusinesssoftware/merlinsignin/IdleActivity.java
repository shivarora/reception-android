package com.merlinbusinesssoftware.merlinsignin;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Created by jamied on 12/08/2015.
 */
public class IdleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idle);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onUserInteraction() {
        finish();
    }
}
