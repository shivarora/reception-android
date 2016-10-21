package com.merlinbusinesssoftware.merlinsignin;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.merlinbusinesssoftware.merlinsignin.structures.StructLog;
import com.merlinbusinesssoftware.merlinsignin.structures.StructPending;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

/**
 * Created by aroras on 12/05/16.
 */
public class Terms extends MyBaseActivity implements ScrollViewListener {
    public int numberOfTriesLeft   = 3;
    private StructLog Log          = new StructLog();
    private StructPending pending  = new StructPending();
    public DatabaseHandler          db;
    public TextView                 tv;
    Button                          mEulaAgreed;
    private String                  mURL;
    private InputMethodManager imm;
    private View rootView;

    protected void onCreate(Bundle savedInstancesState) {

        super.onCreate(savedInstancesState);

        setContentView(R.layout.activity_sign_post);

        rootView = (LinearLayout) findViewById(R.id.sign_post);
        setupUI(rootView);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mEulaAgreed = (Button) findViewById(R.id.signup_button);

        ScrollViewExt scroll = (ScrollViewExt) findViewById(R.id.scrollView1);
        scroll.setScrollViewListener(this);

        WebView webView = (WebView)findViewById(R.id.loreum);

        WebSettings webSetting = webView.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl("file:///android_asset/first.html");

        mEulaAgreed.setEnabled(false);

        db = new DatabaseHandler(this);

        ImageView img = (ImageView) findViewById(R.id.back_SignIn);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // if user press back button, entry will be deleted from log

                onDestroy();

                db.deleteLog(Log.getId());
                SignIn();
            }
        });
    };



    public void setupUI(View view) {
        System.out.println("inside the staff selection view");

        if (!(view instanceof Button)) {

            System.out.println("Its not Button");

            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {

                    Toast toast =  Toast.makeText(getApplicationContext(), "Scroll Down to Accept T&C's", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0,0);
                    toast.show();
                    return false;
                }
            });
        }

        if(view instanceof WebView) {
            System.out.println("Its in WebView");
        }
    }


    public void showD() {
        //moveTaskToBack(true);
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.dialog_wifi, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS), 0);
                                //startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                // Main();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();

                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private class WebViewClient extends android.webkit.WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url,
                                  android.graphics.Bitmap favicon) {
            logLine(view, "onPageStarted() called: url = "+url);
        }
        public void onPageFinished(WebView view, String url) {
            logLine(view, "onPageFinished() called: url = "+url);
            // view.saveWebArchive("Google.xml"); // Incorrect use
            view.saveWebArchive(getFilesDir().getAbsolutePath()
                    + File.separator + System.currentTimeMillis()+".xml");
        }
        public void onLoadResource(WebView view, String url) {
            logLine(view, "onLoadResource() called: url = "+url);
        }
        public void logLine(WebView view, String msg) {
            try {
                FileOutputStream fos =
                        view.getContext()
                                .openFileOutput("Activity.log", Context.MODE_APPEND);
                OutputStreamWriter out = new OutputStreamWriter(fos);
                out.write((new Date()).toString()+": "+msg+"\n");
                out.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // Make a note about the failed load.

            System.out.println("Try No to Load Page" + numberOfTriesLeft);

            while(numberOfTriesLeft > 0) {
                try {
                    Thread.sleep(1000);
                    //view.loadUrl( mURL + "terms");
                    view.loadUrl("file:///android_asset/first.html");
                    numberOfTriesLeft = numberOfTriesLeft - 1;

                    if (numberOfTriesLeft == 0) {
                        showD();
                    }
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

    }

    public void terms(View v) {
        onDestroy();
        Intent i = new Intent(this, CameraActivity.class);
        startActivity(i);
    }

    @Override
    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {

        // We take the last son in the scrollview
        View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

        // if diff is zero, then the bottom has been reached
        if (diff == 0) {
            // do stuff
            mEulaAgreed.setText("Accept T&C");
            mEulaAgreed.setEnabled(true);
        }else {

            System.out.println("its inside Scroll+++");

            mEulaAgreed.setText("SCROLL DOWN TO READ ALL T&C’s");
            mEulaAgreed.setEnabled(false);

        }
    }

    private void SignIn() {
        Intent i = new Intent(this, SignIn.class);
        startActivity(i);
    }
}
