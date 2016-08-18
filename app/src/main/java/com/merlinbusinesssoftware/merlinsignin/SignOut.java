package com.merlinbusinesssoftware.merlinsignin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.merlinbusinesssoftware.merlinsignin.adapters.LogArrayAdapter;
import com.merlinbusinesssoftware.merlinsignin.controls.CustomAutoCompleteView;
import com.merlinbusinesssoftware.merlinsignin.listeners.LogAutoCompleteTextChangedListener;
import com.merlinbusinesssoftware.merlinsignin.structures.StructLog;
import com.merlinbusinesssoftware.merlinsignin.structures.StructPending;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SignOut extends Activity {
    public ArrayList<StructLog>   StructLog;
    public DatabaseHandler        db;
    CountDownTimer                inactiveTimer;
    public LogArrayAdapter        logAdapter;
    public CustomAutoCompleteView autoCompleteLog;
    private ProgressBar           progressBar1;
    private Button                btnSignOut;
    private String                 mURL;
    public ProgressDialog          progressDialog;
    private StructLog Log          = new StructLog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_out);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        db = new DatabaseHandler(this);

        setAdapters();

        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);

        ImageView img = (ImageView) findViewById(R.id.back_signout);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here
                Main();
            }
        });

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {

        }else{
            showD();
        }
    }

    public void showD() {
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
                                 Main();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                Main();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



    private void Main() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private void setAdapters() {

        StructLog structlog = new StructLog();
        structlog.reset();
        autoCompleteLog = (CustomAutoCompleteView) findViewById(R.id.autoCompleteTextViewLog);
        autoCompleteLog.addTextChangedListener(new LogAutoCompleteTextChangedListener(this));
        autoCompleteLog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log = StructLog.get(position);
                setLog();
            }
        });
        logAdapter = new LogArrayAdapter(this, R.layout.listview_auto_complete_signout, StructLog);
        autoCompleteLog.setAdapter(logAdapter);
    }

    private void setLog() {
        autoCompleteLog.setText(Log.getName());
    }

    private void setFieldsEnabled(boolean enabled) {
        autoCompleteLog.setEnabled(enabled);
        btnSignOut.setEnabled(enabled);

        if (!enabled) {
            progressBar1.setVisibility(View.VISIBLE);
        } else {
            progressBar1.setVisibility(View.INVISIBLE);
        }
    }

    public void signOut_old(View v) {
        setFieldsEnabled(false);

        StructPending pending = new StructPending();

        pending.setReceptionLogId(Log.getReceptionLogId());
        pending.setTime(getDateTime());
        pending.setLogId(Log.getId());

        int pendingId = db.insertPending(pending);

        db.updatePendingPendingId(Log.getPendingId(), pendingId);
        db.deleteLog(Log.getId());
        finish();
    }

    public void signOut(View v) {
        setFieldsEnabled(false);
        // System.out.println("this is the id user want to log out" + autoCompleteLog.getText());

        //progressDialog = ProgressDialog.show(SignOut.this, "", "Processing...Please Wait", true);

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            private final ProgressDialog dialog = new ProgressDialog(SignOut.this);
            @Override
            protected String doInBackground(String... params) {


                //HttpClient httpClient = new DefaultHttpClient();
                HttpClient httpClient = ExSSLSocketFactory.getHttpsClient(new DefaultHttpClient());
                // In a POST request, we don't pass the values in the URL.
                //Therefore we use only the web page URL as the parameter of the HttpPost argument
                HttpPut httpPut = new HttpPut( Constants.BACKEND_SERVER_URL_VISITORS+ Log.getId());

                //BasicNameValuePair logidBasicNameValuePair = new BasicNameValuePair("logid", Integer.toString(Log.getId()));
                BasicNameValuePair signoutBasicNameValuePAir = new BasicNameValuePair("signout", getDateTime());

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                //nameValuePairList.add(logidBasicNameValuePair);
                nameValuePairList.add(signoutBasicNameValuePAir);

                try {
                    // UrlEncodedFormEntity is an entity composed of a list of url-encoded pairs.
                    //This is typically useful while sending an HTTP POST request.
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

                    // setEntity() hands the entity (here it is urlEncodedFormEntity) to the request.
                    httpPut.setEntity(urlEncodedFormEntity);

                    try {
                        // HttpResponse is an interface just like HttpPost.
                        //Therefore we can't initialize them
                        HttpResponse httpResponse = httpClient.execute(httpPut);


                        // According to the JAVA API, InputStream constructor do nothing.
                        //So we can't initialize InputStream although it is not an interface
                        InputStream inputStream = httpResponse.getEntity().getContent();

                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        StringBuilder stringBuilder = new StringBuilder();

                        String bufferedStrChunk = null;

                        while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                            stringBuilder.append(bufferedStrChunk);
                        }

                        return stringBuilder.toString();

                    } catch (ClientProtocolException cpe) {
                        System.out.println("First Exception caz of HttpResponese :" + cpe);
                        cpe.printStackTrace();
                    } catch (IOException ioe) {
                        System.out.println("Second Exception caz of HttpResponse :" + ioe);
                        ioe.printStackTrace();
                    }

                } catch (UnsupportedEncodingException uee) {
                    System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
                    uee.printStackTrace();
                }

                return null;
            }

            protected  void onPreExecute(){
                this.dialog.setMessage("Processing...Please Wait");
                this.dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                this.dialog.cancel();

                if (result != null) {

                    System.out.println("this is the log id" + Log.getId());
                    System.out.println("this is the result" + result);

                    String json = result;
                    Map jsonJavaRootObject = new Gson().fromJson(json, Map.class);
                    //System.out.println(jsonJavaRootObject.get("message"));

                    if (jsonJavaRootObject.get("message").equals("completed")) {

                        db.deleteLog(Log.getId());

                        String message =  "You are  succesfully signed out.";
                        String set_image = "success_toast";
                        for (int i=0; i < 2; i++) {
                            toast(message, set_image);
                        }
                    } else {
                        String message =  "Sorry ! You are not logged out. Try Again!!...";

                        if(jsonJavaRootObject.get("message").equals("Error")){

                            message =  "Sorry ! There is no user logged in with this Visitor Id. Try Again!!...";
                        }

                        String set_image = "error_toast";
                        for (int i=0; i < 2; i++) {
                            toast(message, set_image);
                        }
                    }

                }else {
                    String message =  "Sorry! There is some problem signing you out... Please contact to the administrator!";
                    String set_image = "error_toast";
                    for (int i=0; i < 2; i++) {
                        toast(message, set_image);
                    }

                }

                Main();
            }


        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();

        // db.deleteLog(Log.getId());
        // finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void  toast( String message, String set_image){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        ImageView image = (ImageView) layout.findViewById(R.id.image);
        if(set_image == "error_toast"){
            image.setImageResource(R.drawable.error_toast);
        }else{
            image.setImageResource(R.drawable.success_toast);
        }

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, -60);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }


    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}