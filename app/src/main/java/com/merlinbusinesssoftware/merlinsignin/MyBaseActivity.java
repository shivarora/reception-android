package com.merlinbusinesssoftware.merlinsignin;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by aroras on 28/07/16.
 */
public class MyBaseActivity extends Activity {
    public DatabaseHandler db;
    public com.merlinbusinesssoftware.merlinsignin.structures.StructSettings StructSettings;


    public static final long DISCONNECT_TIMEOUT = 90000; // 2 min = 2 * 60 * 1000 ms

    private Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {
        }
    };

    private final Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {

            System.out.println("here after disconnection");
            // Perform any required operation on disconnect
            disconnectHandler.removeCallbacks(disconnectCallback);
            Main();
        }
    };

    public void Main() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }

    @Override
    protected void onDestroy() {

        System.out.println("Its in Destroy Now");

        super.onDestroy();
        stopDisconnectTimer();
        finish();
    }

    public int findTabId(){
        db = new DatabaseHandler(this);
        StructSettings = db.getAllSettings();
        return  StructSettings.getTabletId();
    }

    public void toast(String message){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        ImageView image = (ImageView) layout.findViewById(R.id.image);

        image.setImageResource(R.drawable.success_toast);

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, -60);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}
