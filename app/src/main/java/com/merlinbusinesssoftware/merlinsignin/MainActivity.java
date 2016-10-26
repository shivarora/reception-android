package com.merlinbusinesssoftware.merlinsignin;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.merlinbusinesssoftware.merlinsignin.structures.StructAccount;
import com.merlinbusinesssoftware.merlinsignin.structures.StructContact;
import com.merlinbusinesssoftware.merlinsignin.structures.StructEmployee;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import javax.net.ssl.SSLContext;


@SuppressLint("InflateParams")
public class  MainActivity extends MyBaseFragmentAcivity {

    private IntentFilter filter       = new IntentFilter(UpdateConnection.ACTION_UPDATE_CONNECTION);
    private UpdateConnection receiver = new UpdateConnection();
    private static final String TAG   = "MainActivity";
    public DatabaseHandler  db;
    BackgroundService       mService;
    boolean mBound          = false;
    private PendingIntent   pendingIntent;
    private AlarmManager    manager;
    ViewPager               mViewPager;
    private LinearLayout    rootView;
    private Integer tabId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootView = (LinearLayout) findViewById(R.id.mainAct);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tabId = findTabId();

        //sockets
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

        //Suggestions Add
        mSocket.on("AddSuggestion-"     + String.valueOf(tabId), onNewMessage);

        //Suggestion Update
        mSocket.on("UpdateSuggestion-"  + String.valueOf(tabId), onUpdateMessage);

        //Suggestion Delete
        mSocket.on("DeleteSuggestion-"  + String.valueOf(tabId), onDeleteMessage);

        //Conenct Message
        mSocket.on("connectMessage", connectMessage);

        //Disconnect Message
        mSocket.on("disconnect", onDisconnect);

        mSocket.connect();

        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        db = new DatabaseHandler(this);

        //sockets finish
        int position = 0;

        mViewPager = (ViewPager) findViewById(R.id.pager);
        /** set the adapter for ViewPager */
        mViewPager.setAdapter(new SamplePagerAdapter(getSupportFragmentManager()));

        mViewPager.setCurrentItem(position);

        // Functionality for image Button at bottom

        final ImageButton home = (ImageButton) findViewById(R.id.imageButtonHome);
        final ImageButton firstaid = (ImageButton) findViewById(R.id.imageButtonAid);
        final ImageButton fire = (ImageButton) findViewById(R.id.imageButtonFire);
        final ImageButton help = (ImageButton) findViewById(R.id.imageButtonHelp);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home.setBackgroundResource(R.drawable.home_icon_active);
                firstaid.setBackgroundResource(R.drawable.first_aiders_icon);
                fire.setBackgroundResource(R.drawable.fire_list_icon);
                help.setBackgroundResource(R.drawable.help_icon);
                mViewPager.setCurrentItem(0);
            }
        });


        firstaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstaid.setBackgroundResource(R.drawable.first_aiders_icon_active);
                home.setBackgroundResource(R.drawable.home_icon_active);
                fire.setBackgroundResource(R.drawable.fire_list_icon);
                help.setBackgroundResource(R.drawable.help_icon);
                mViewPager.setCurrentItem(2);
            }
        });



        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fire.setBackgroundResource(R.drawable.fire_list_icon_active);
                home.setBackgroundResource(R.drawable.home_icon_active);
                firstaid.setBackgroundResource(R.drawable.first_aiders_icon);
                help.setBackgroundResource(R.drawable.help_icon);
                mViewPager.setCurrentItem(3);
            }
        });


        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                help.setBackgroundResource(R.drawable.help_icon_active);
                home.setBackgroundResource(R.drawable.home_icon_active);
                firstaid.setBackgroundResource(R.drawable.first_aiders_icon);
                fire.setBackgroundResource(R.drawable.fire_list_icon);
                mViewPager.setCurrentItem(4);
            }
        });

    }


    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if(currentNetworkInfo.isConnected()){
                System.out.println("connected to network");
            }else{
                System.out.println("Disconnected to network");
                mSocket.disconnect();
            }
        }
    };


    /** Defining a FragmentPagerAdapter class for controlling the fragments to be shown when user swipes on the screen. */
    public class SamplePagerAdapter extends FragmentPagerAdapter {

        public SamplePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            /** Show a Fragment based on the position of the current screen */
            if (position == 0) {
                return new SampleFragment();
            } else if(position == 1) {
                return new GridViewActivity();
            }
            else if(position == 2) {
                return new FirstAidActivity();
            }
            else if(position == 3) {
                return new MarshallActivity();
            }
            else if(position == 4) {
                return new HelpActivity();
            }
            else{
                return new SampleFragment();
            }
        }


        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }


    @Override
    public void onUserInteraction() {

    }

    @Override
    public void onResume(){

        super.onResume();
        Intent intent = new Intent(this, BackgroundService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        registerReceiver(receiver, filter);
    }

    @Override
    public void onPause(){
        super.onPause();

        unregisterReceiver(receiver);
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            BackgroundService.ServiceBinder binder = (BackgroundService.ServiceBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public void SignIn(View v) {
        Intent i = new Intent(this, SignIn.class);
        startActivity(i);
    }

    public void SignOut(View v) {
        Intent i = new Intent(this, SignOut.class);
        startActivity(i);
    }


    private void openSettings() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public void validatePIN(View v) {
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.dialog_request_pin, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(dialogView);

        final EditText editPin = (EditText) dialogView
                .findViewById(R.id.edit_pin);

        alertDialogBuilder
                .setTitle("Settings")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (editPin.getText().toString().equals("2367")) {
                                    openSettings();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void updateConnection(boolean connected){
        ImageView imgError = (ImageView)findViewById(R.id.img_error);
        if (connected){
            imgError.setVisibility(View.GONE);
        } else {
            imgError.setVisibility(View.VISIBLE);
        }
    }


    public void onPrint(final View v) {

        System.out.println("inside print");

        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.dialog_fire, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(dialogView);

        alertDialogBuilder
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                allPrintOut(v);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public void allPrintOut(View v) {

        System.out.println("inside printout");

        new AsyncHttpTask().execute(Constants.BACKEND_ALL_PRINT_OUT);

    }

    //Downloading data asynchronously
    public class AsyncHttpTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Integer result = 0;
            try {

                // Create Apache HttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                //HttpClient httpClient = ExSSLSocketFactory.getHttpsClient(new DefaultHttpClient());
                HttpResponse httpResponse = httpClient.execute(new HttpGet(params[0]));
                int statusCode = httpResponse.getStatusLine().getStatusCode();


                // 200 represents HTTP OK
                if (statusCode == 200) {
                    return "printed";
                }
                else {
                    return "Error";
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            System.out.println("this is result" + result);

            if(result != null){

                if (result == "printed") {

                    // setting up message for toast
                    String message =  "Please wait for the print out of All Visitors and Staff Activity for today";
                    toast(message);

                } else {

                    String message =  "Sorry!! There is some problem while Printing Out All visitors and Staff";
                    toast(message);
                }
            }else{
                String message =  "Sorry!! There is some problem while Printing Out All visitors and Staff";
                toast(message);

            }

            Main();

        }
    }

    public void  toast( String message){

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

    private class UpdateConnection extends BroadcastReceiver{
        private static final String ACTION_UPDATE_CONNECTION = "com.merlinbusinesssoftware.merlinsignin.updateconnection";

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean connected = intent.getBooleanExtra("connected", false);
            updateConnection(connected);
        }
    }

    public void onBackPressed() {
            Main();
    }

    private void Main() {

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }


    // SOCKET IO PROGRAM

    private Socket mSocket;
    {
        try {

            SSLContext sslContext = ExSSLSocketFactory.getSSLContext();
            // set as an option
            IO.Options opts = new IO.Options();
            opts.sslContext = sslContext;
            //opts.hostnameVerifier = myHostnameVerifier;


            mSocket = IO.socket(Constants.BACKEND_SERVER_URL, opts);
        }
        catch (URISyntaxException e) {

        }
    }


    private Emitter.Listener onNewMessage = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {

            JSONObject data = (JSONObject) args[0];

            String id;
            String type;
            String suggestion;

            System.out.println("here is data" + data);

            try
            {
                id = data.getString("id");
                type = data.getString("type");
                suggestion = data.getString("suggestion");

                System.out.println("Suggestion Keyword" + suggestion);
                System.out.println("Suggestion Type" + type);

                if(data.getString("type").equals("Visiting")){

                    StructEmployee employee = new StructEmployee();
                    employee.setEmployeeId(new Integer(id));
                    employee.setName(suggestion);
                    db.insertEmployee(employee);

                }else if (data.getString("type").equals("Company")){

                    StructAccount account = new StructAccount();
                    account.setAccountId(new Integer(id));
                    account.setType(type);
                    account.setName(suggestion);
                    db.insertAccount(account);
                }else if (data.getString("type").equals("visitor_name")){

                    StructContact contact = new StructContact();
                    contact.setAccountId(new Integer(id));
                    contact.setType(type);
                    contact.setName(suggestion);
                    db.insertContact(contact);
                }

            }
            catch (JSONException e)
            {
                return;
            }
        }

    };

    private Emitter.Listener onUpdateMessage = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {

            JSONObject data = (JSONObject) args[0];

            String id;
            String type;
            String suggestion;

            try

            {
                id = data.getString("id");
                type = data.getString("type");
                suggestion = data.getString("suggestion");

                System.out.println("Suggestion Keyword" + suggestion);
                System.out.println("Suggestion Type" + type);

                if(data.getString("type").equals("Visiting")){

                    StructEmployee employee = new StructEmployee();
                    employee.setEmployeeId(new Integer(id));
                    employee.setName(suggestion);
                    // on update first run delete coz ther is no gurantee this id is in this table ,as this could be in company table as well.
                    // So after delete to account table , make a new entry to table.

                    db.deleteAccount(new Integer(id));
                    db.deleteContact(new Integer(id));
                    db.replaceEmployee(employee);

                }else if (data.getString("type").equals("Company")){

                    System.out.println("Update Company");

                    StructAccount account = new StructAccount();
                    account.setAccountId(new Integer(id));
                    account.setType(type);
                    account.setName(suggestion);

                    // on update first run delete coz ther is no gurantee this id is in this table ,as this could be in employee table as well.
                    // So after delete to employee table , make a new entry to table.

                    db.deleteEmployee(new Integer(id));
                    db.deleteContact(new Integer(id));
                    db.replaceAccount(account);

                }else if (data.getString("type").equals("visitor_name")){

                    System.out.println("Update Visitor Name");

                    StructContact contact = new StructContact();
                    contact.setAccountId(new Integer(id));
                    contact.setType(type);
                    contact.setName(suggestion);

                    // on update first run delete coz ther is no gurantee this id is in this table ,as this could be in employee table as well.
                    // So after delete to employee table , make a new entry to table.

                    db.deleteEmployee(new Integer(id));
                    db.deleteAccount(new Integer(id));
                    db.replaceContact(contact);

                }
            }
            catch (JSONException e)
            {
                return;
            }
        }

    };

    private Emitter.Listener onDeleteMessage = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {

            System.out.println("Suggestions data to delete" + args[0]);

            JSONObject data = (JSONObject) args[0];

            String id;

            try
            {

                id   = data.getString("id");

                if(data.getString("type").equals("Visiting")){
                    db.deleteEmployee(new Integer(id));
                }
                else  if(data.getString("type").equals("Company")){
                    db.deleteAccount(new Integer(id));
                }
                else  if(data.getString("type").equals("visitor_name")){
                    db.deleteContact(new Integer(id));
                }


            }
            catch (JSONException e)
            {
                return;
            }
        }

    };

    private Emitter.Listener connectMessage = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {

            JSONObject data = (JSONObject) args[0];

            System.out.println("Device Status" + data);

        }

    };
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("Service connected");

            String message = "1";
            //your code
            mSocket.emit("up", message);

        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            new java.util.Timer().schedule(

                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            // your code here, and if you have to refresh UI put this code:
                            runOnUiThread(new   Runnable() {
                                public void run() {
                                    //your code
                                    mSocket.connect();
                                }
                            });
                        }
                    },
                    5000
            );
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("Unable to connect to Backend");

            new java.util.Timer().schedule(

                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            // your code here, and if you have to refresh UI put this code:
                            runOnUiThread(new   Runnable() {
                                public void run() {
                                    //your code
                                    mSocket.connect();
                                }
                            });
                        }
                    },
                    5000
            );
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Toast toast = new Toast(getApplicationContext());
        toast.cancel();

        //deleting socket connection and clearing all socket listeners
        mSocket.disconnect();
        mSocket.off("brcSuggestionAdd", onNewMessage);
        mSocket.off("brcSuggestionUpdate", onUpdateMessage);
        mSocket.off("brcSuggestionDelete", onDeleteMessage);
        mSocket.off("connectMessage", connectMessage);
    }

    @Override
    public void onStop() {
        super.onStop();
        Toast toast = new Toast(getApplicationContext());
        toast.cancel();
    }
}
