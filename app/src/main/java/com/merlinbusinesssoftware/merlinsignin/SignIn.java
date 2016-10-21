package com.merlinbusinesssoftware.merlinsignin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.merlinbusinesssoftware.merlinsignin.adapters.AccountsArrayAdapter;
import com.merlinbusinesssoftware.merlinsignin.adapters.ContactsArrayAdapter;
import com.merlinbusinesssoftware.merlinsignin.adapters.EmployeesArrayAdapter;
import com.merlinbusinesssoftware.merlinsignin.controls.CustomAutoCompleteView;
import com.merlinbusinesssoftware.merlinsignin.listeners.AccountsAutoCompleteTextChangedListener;
import com.merlinbusinesssoftware.merlinsignin.listeners.ContactsAutoCompleteTextChangedListener;
import com.merlinbusinesssoftware.merlinsignin.listeners.EmployeesAutoCompleteTextChangedListener;
import com.merlinbusinesssoftware.merlinsignin.structures.StructAccount;
import com.merlinbusinesssoftware.merlinsignin.structures.StructContact;
import com.merlinbusinesssoftware.merlinsignin.structures.StructEmployee;
import com.merlinbusinesssoftware.merlinsignin.structures.StructLog;
import com.merlinbusinesssoftware.merlinsignin.structures.StructPending;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class SignIn extends MyBaseActivity {
    public DatabaseHandler        db;
    public ContactsArrayAdapter   contactsAdapter;
    public AccountsArrayAdapter   accountsAdapter;
    public EmployeesArrayAdapter  employeesAdapter;
    public CustomAutoCompleteView autoCompleteContacts;
    public CustomAutoCompleteView autoCompleteAccounts;
    public CustomAutoCompleteView autoCompleteEmployees;
    private EditText              editVehicleReg;
    private ProgressBar           progressBar1;
    private Button                btnSignIn;
    public ArrayList<StructContact> StructContact;
    public ArrayList<StructAccount> StructAccount;
    public ArrayList<StructEmployee> StructEmployee;
    private StructContact Contact   = new StructContact();
    private StructAccount Account   = new StructAccount();
    private StructEmployee Employee = new StructEmployee();
    private StructLog Log           = new StructLog();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final StructPending pending = new StructPending();
        setContentView(R.layout.activity_sign_in);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        db = new DatabaseHandler(this);

        setAdapters();

        editVehicleReg = (EditText) findViewById(R.id.edit_vehicle_reg);
        if(pending.getVehicleReg() != null) {
            editVehicleReg.setText(pending.getVehicleReg());
        }
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        btnSignIn = (Button) findViewById(R.id.btn_sign_in);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        ImageView img = (ImageView) findViewById(R.id.back_Image);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here
                onDestroy();
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
                                //startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS), 0);
                                // Main();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Main();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("Inside back pressed");
        //    finish();

    }
    private void setAdapters() {

        String[] language ={"C","C++","Java",".NET","iPhone","Android","ASP.NET","PHP"};

        StructPending pending = new StructPending();

        autoCompleteContacts = (CustomAutoCompleteView) findViewById(R.id.autoCompleteTextViewContacts);

        if(pending.getContactName() != null){
            autoCompleteContacts.setText(pending.getContactName());
        }

        autoCompleteContacts.addTextChangedListener(new ContactsAutoCompleteTextChangedListener(this));
        autoCompleteContacts.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Contact = StructContact.get(position);
                setContact();
            }
        });
        contactsAdapter = new ContactsArrayAdapter(this, R.layout.listview_auto_complete, StructContact);
        autoCompleteContacts.setAdapter(contactsAdapter);

        autoCompleteAccounts = (CustomAutoCompleteView) findViewById(R.id.autoCompleteTextViewAccounts);

        if(pending.getAccountName() != null){
            autoCompleteAccounts.setText(pending.getAccountName());
        }

        autoCompleteAccounts.addTextChangedListener(new AccountsAutoCompleteTextChangedListener(this));
        autoCompleteAccounts.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Account = StructAccount.get(position);
                setAccount();
            }
        });
        accountsAdapter = new AccountsArrayAdapter(this, R.layout.listview_auto_complete, StructAccount);
        autoCompleteAccounts.setAdapter(accountsAdapter);


        autoCompleteEmployees = (CustomAutoCompleteView) findViewById(R.id.autoCompleteTextViewEmployees);

        if(pending.getEmployeeName() != null){
            autoCompleteEmployees.setText(pending.getEmployeeName());
        }

        autoCompleteEmployees.addTextChangedListener(new EmployeesAutoCompleteTextChangedListener(this));
        autoCompleteEmployees.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Employee = StructEmployee.get(position);
                setEmployee();
            }
        });
        employeesAdapter = new EmployeesArrayAdapter(this, R.layout.listview_auto_complete, StructEmployee);
        autoCompleteEmployees.setAdapter(employeesAdapter);
    }



    private void setContact() {

        System.out.println("This is auto complete result for Contacts" + Contact.getName());

        autoCompleteContacts.setText(Contact.getName());
        autoCompleteAccounts.requestFocus();
    }

    private void setAccount() {
        autoCompleteAccounts.setText(Account.getName());
        autoCompleteEmployees.requestFocus();
    }

    private void setEmployee() {
        autoCompleteEmployees.setText(Employee.getName());
        editVehicleReg.requestFocus();
    }

    private void setFieldsEnabled(boolean enabled) {
        autoCompleteContacts.setEnabled(enabled);
        autoCompleteAccounts.setEnabled(enabled);
        autoCompleteEmployees.setEnabled(enabled);
        editVehicleReg.setEnabled(enabled);
        btnSignIn.setEnabled(enabled);

        if (!enabled) {
            progressBar1.setVisibility(View.VISIBLE);
        } else {
            progressBar1.setVisibility(View.INVISIBLE);
        }
    }

    public void signIn(View v) {

        //Delete all the previous day log data.
        db.deletePreviousLog(getDate());

        if (autoCompleteContacts.getText().toString().equals("")) {
            autoCompleteContacts.setError("Please enter your name");
            autoCompleteContacts.requestFocus();
            return;
        }

        if (autoCompleteAccounts.getText().toString().equals("")) {
            autoCompleteAccounts.setError("Please enter your company");
            autoCompleteAccounts.requestFocus();
            return;
        }

        if (autoCompleteEmployees.getText().toString().equals("")) {
            autoCompleteEmployees.setError("Please enter the name of the person you are visiting");
            autoCompleteEmployees.requestFocus();
            return;
        }

        setFieldsEnabled(false);

        StructLog log = new StructLog();
        log.setName(autoCompleteContacts.getText().toString() + " (" + autoCompleteAccounts.getText().toString() + ")");
        log.setReceptionLogId(0);
        log.setPendingId(0);
        log.setSetTime(getDate());

        int logId = db.insertLog(log);

        //set logid here ---

        log.setId(logId);

        System.out.println("Log Id for User" + logId);


        StructPending pending = new StructPending();
        pending.setType(Account.getType());
        pending.setAccountId(Account.getAccountId());
        pending.setAccountName(autoCompleteAccounts.getText().toString());
        pending.setContactId(Contact.getContactId());
        pending.setContactName(autoCompleteContacts.getText().toString());
        pending.setEmployeeId(Employee.getEmployeeId());
        pending.setEmployeeName(autoCompleteEmployees.getText().toString());
        pending.setVehicleReg(editVehicleReg.getText().toString());
        pending.setTime(getDateTime());
        pending.setReceptionLogId(0);
        pending.setLogId(logId);

        // int pendingId = db.insertPending(pending);
        // db.updateLogPendingId(logId, pendingId);

        onDestroy();
        Terms();
    }

    public void deleteAllImages(){
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

    private void CameraActivity() {
        Intent i = new Intent(this, CameraActivity.class);
        startActivity(i);
    }

    private void Terms() {
        Toast toast =  Toast.makeText(getApplicationContext(), "Please wait for T&C's to load", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0,0);
        toast.show();

        Intent i = new Intent(this, Terms.class);
        startActivity(i);
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        return dateFormat.format(date);
    }

    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "SignIn Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.merlinbusinesssoftware.merlinsignin/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "SignIn Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.merlinbusinesssoftware.merlinsignin/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString("AccountName", "sfsdf");
        savedInstanceState.putString("ContactName", "dgdfg");
        savedInstanceState.putString("EmployeeName", "gdf");
        savedInstanceState.putString("VehicleName", "gdf");

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}