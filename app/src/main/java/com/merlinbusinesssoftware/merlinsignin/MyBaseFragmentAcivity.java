package com.merlinbusinesssoftware.merlinsignin;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;

/**
 * Created by aroras on 28/07/16.
 */
public class MyBaseFragmentAcivity extends FragmentActivity {

    public static final long DISCONNECT_TIMEOUT = 70000; // 5 min = 5 * 60 * 1000 ms

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

    private void Main() {
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
    }

}

