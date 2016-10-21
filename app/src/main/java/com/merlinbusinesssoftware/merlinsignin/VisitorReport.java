package com.merlinbusinesssoftware.merlinsignin;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.merlinbusinesssoftware.merlinsignin.adapters.VisitorReportArrayAdapter;
import com.merlinbusinesssoftware.merlinsignin.structures.StructPending;
import com.merlinbusinesssoftware.merlinsignin.structures.StructReceptionLog;
import com.merlinbusinesssoftware.merlinsignin.structures.StructSettings;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class VisitorReport extends Activity {

    Thread t;
    private ArrayList<StructReceptionLog> StructReceptionLog = new ArrayList<StructReceptionLog>();
    public DatabaseHandler db;
    private StructSettings mSettings = new StructSettings();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_report);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.VISIBLE);

        if (t != null)
            if (t.isAlive())
                t.interrupt();
        t = new Thread(new Runnable() {
            @Override
            public void run() {


                HttpClient httpClient = new DefaultHttpClient();
                System.out.println(httpClient.getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY));

                HttpGet httpGet = new HttpGet("http://10.100.15.195:4000/allVisitors");

                try {
                    // HttpResponse is an interface just like HttpPost.
                    //Therefore we can't initialize them
                    HttpResponse httpResponse = httpClient.execute(httpGet);


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

                   // StructPending jsonJavaRootObject = new Gson().fromJson(stringBuilder.toString(), StructPending.class);
//                    JSONTokener tokener = new JSONTokener(stringBuilder.toString());
//                    JSONArray finalResult = new JSONArray(tokener);
                    JSONObject object = new JSONObject(stringBuilder.toString());
                    JSONArray Jarray = object.getJSONArray("row");
                    //Map jsonJavaRootObject = new Gson().fromJson(String.valueOf(stringBuilder), Map.class);

                    for (int i = 0; i < Jarray.length(); i++) {
                        JSONObject Jasonobject = Jarray.getJSONObject(i);

                        String id = Jasonobject.getString("id");
                        String name = Jasonobject.getString("accountname");
                        String contactname = Jasonobject.getString("contactname");
                        String employeename = Jasonobject.getString("employeename");
                        String vehiclereg = Jasonobject.getString("vehiclereg");
                        String signin = Jasonobject.getString("settime");


                    StructReceptionLog log = new StructReceptionLog();
                    log.setAccountName(name);
                    log.setContactName(contactname);
                    log.setEmployeeName(employeename);
                    log.setVehicleReg(vehiclereg);
                    log.setSignIn(signin);


                    StructReceptionLog.add(log);
                    }
                   // System.out.println(Jarray);
                   // System.exit(0);

                    //return stringBuilder.toString();

                } catch (ClientProtocolException cpe) {
                    System.out.println("First Exception caz of HttpResponese :" + cpe);
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    System.out.println("Second Exception caz of HttpResponse :" + ioe);
                    ioe.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        populateList();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        t.start();
    }

    protected void onCreate_old(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_report);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        db = new DatabaseHandler(this);

        mSettings = db.getSettings();

        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.VISIBLE);




        if (t != null)
            if (t.isAlive())
                t.interrupt();
        t = new Thread(new Runnable() {
            @Override
            public void run() {

                ArrayList<StructPending> signIns = (ArrayList<StructPending>) db.getAllPending();

                for (int i = 0; i < signIns.size(); i++) {
                    StructPending pending = db.getPendingById(signIns.get(i).getId());


                    StructReceptionLog log = new StructReceptionLog();
                    log.setAccountName(pending.getAccountName());
                    log.setContactName(pending.getContactName());
                    log.setEmployeeName(pending.getEmployeeName());
                    log.setVehicleReg(pending.getVehicleReg());
                    //log.setSignIn(webService.GetNode(fstElmnt, "sign_in"));

                    StructReceptionLog.add(log);
                }


               // System.exit(0);

//                WebService webService = new WebService();
//
//                String query = "SELECT account_name, contact_name, employee_name, vehicle_reg, sign_in" +
//                        " FROM bespoke.reception_log" +
//                        " WHERE sign_out='1899-12-30'::timestamp" +
//                        " ORDER BY sign_in;";

//                NodeList nodeLst = webService
//                        .runSQL(mSettings.getURL(),
//                                mSettings.getDSN(),
//                                query);
//
//                if (nodeLst == null) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            progressBar.setVisibility(View.INVISIBLE);
//                            Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_LONG).show();
//                        }
//                    });
//
//                    return;
//                }
//
//                for (int s = 0; s < nodeLst.getLength(); s++) {
//
//                    Node fstNode = nodeLst.item(s);
//
//                    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
//
//                        Element fstElmnt = (Element) fstNode;
//
//                        StructReceptionLog log = new StructReceptionLog();
//                        log.setAccountName(webService.GetNode(fstElmnt, "account_name"));
//                        log.setContactName(webService.GetNode(fstElmnt, "contact_name"));
//                        log.setEmployeeName(webService.GetNode(fstElmnt, "employee_name"));
//                        log.setVehicleReg(webService.GetNode(fstElmnt, "vehicle_reg"));
//                        log.setSignIn(webService.GetNode(fstElmnt, "sign_in"));
//
//                        StructReceptionLog.add(log);
//                    }
//                }



                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        populateList();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        t.start();
    }

    private void populateList() {
        ListView listViewReport = (ListView) findViewById(R.id.listview_report);
        VisitorReportArrayAdapter visitorReportArrayAdapter = new VisitorReportArrayAdapter(this, R.layout.listview_visitor_report, StructReceptionLog);
        listViewReport.setAdapter(visitorReportArrayAdapter);
    }
}
