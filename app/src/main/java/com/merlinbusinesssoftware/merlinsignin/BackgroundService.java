package com.merlinbusinesssoftware.merlinsignin;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

/**
 * Created by jamied on 04/09/2015.
 */
public class BackgroundService extends Service {
    static Thread t;
    private String mURL;
    private String mDSN;
    Handler timerHandler = new Handler();
    private boolean processing = false;
    private static final String ACTION_UPDATE_CONNECTION = "com.merlinbusinesssoftware.merlinsignin.updateconnection";
    private final IBinder mBinder = new ServiceBinder();

    @Override
    public IBinder onBind(Intent intent) {
        timerHandler.postDelayed(timerRunnable, 100);
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        super.onUnbind(intent);
        timerHandler.removeCallbacks(timerRunnable);

        return super.onUnbind(intent);
    }

    public class ServiceBinder extends Binder {
        BackgroundService getService() {
            return BackgroundService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void runThread() {
       // t = new Thread(null, sendData, "Background");
       // t.start();
    }


    /**
     *  this senddata fucntion is already there. i am jsut trying to test to http . shiv
     *  i am changing its name tos senddata_old.
     */

    private Runnable sendData = new Runnable() {
        public void run() {

            System.out.println("inside Send Data =========================================================");
            processing = true;

            //LoadWebServiceSettings();
            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
            WebService webService = new WebService();

//            ArrayList<StructPending> signIns = (ArrayList<StructPending>) db.getAllPending();
//            if (signIns.size() == 0) {
//                processing = false;
//                updateConnectionUI(true);
//                return;
//            }

//            if (webService.checkStatus(mURL, mDSN).equals("0")) {
//                for (int i = 0; i < signIns.size(); i++) {
//                    StructPending pending = db.getPendingById(signIns.get(i).getId());
//
//                    if (pending.getReceptionLogId() == 0) {
//                        String query = "SELECT bespoke.fn_reception_log_insert(" +
//                                " '" + pending.getType() + "'" +
//                                ", " + pending.getAccountId() +
//                                ", '" + pending.getAccountName() + "'" +
//                                ", " + pending.getContactId() +
//                                ", '" + pending.getContactName() + "'" +
//                                ", " + pending.getEmployeeId() +
//                                ", '" + pending.getEmployeeName() + "'" +
//                                ", '" + pending.getVehicleReg() + "'" +
//                                ", '" + pending.getTime() + "'" +
//                                ")";
//
//                        NodeList nodeLst = webService
//                                .runSQL(mURL,
//                                        mDSN,
//                                        query);
//
//                        if (nodeLst == null) {
//                            processing = false;
//                            return;
//                        }
//
//                        for (int s = 0; s < nodeLst.getLength(); s++) {
//
//                            Node fstNode = nodeLst.item(s);
//
//                            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
//
//                                Element fstElmnt = (Element) fstNode;
//                                int receptionLogId = Integer.valueOf(webService.GetNode(fstElmnt, "fn_reception_log_insert"));
//
//                                db.deletePending(pending.getId());
//                                db.updatePendingReceptionLogId(pending.getPendingId(), receptionLogId);
//                                db.updateLogReceptionLogId(pending.getLogId(), receptionLogId);
//                            }
//                        }
//                    } else {
//                        String query = "SELECT TRUE; UPDATE bespoke.reception_log SET sign_out='" + pending.getTime() + "'" +
//                                " WHERE" +
//                                " id=" + pending.getReceptionLogId() +
//                                ";";
//
//                        NodeList nodeLst = webService
//                                .runSQL(mURL,
//                                        mDSN,
//                                        query);
//
//                        if (nodeLst == null) {
//                            processing = false;
//                            return;
//                        }
//
//                        db.deletePending(pending.getId());
//                    }
//                }
//
//                updateConnectionUI(true);
//            } else {
//                updateConnectionUI(false);
//            }

            webService = null;
            processing = false;
        }
    };

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            timerHandler.post(new Runnable() {
                public void run() {
                    if (!processing) {
                        runThread();
                    }
                }
            });

            timerHandler.postDelayed(this, 300);
        }
    };

//    private void LoadWebServiceSettings() {
//        SharedPreferences Prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        mURL = Prefs.getString("url", "");
//        mDSN = Prefs.getString("dsn", "");
//    }

    private void updateConnectionUI(boolean connected){
        Intent intent = new Intent(ACTION_UPDATE_CONNECTION);
        intent.putExtra("connected", connected);
        sendBroadcast(intent);
    }
}