package com.merlinbusinesssoftware.merlinsignin;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.merlinbusinesssoftware.merlinsignin.structures.StructAccount;
import com.merlinbusinesssoftware.merlinsignin.structures.StructContact;
import com.merlinbusinesssoftware.merlinsignin.structures.StructEmployee;
import com.merlinbusinesssoftware.merlinsignin.structures.StructSettings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RefreshData extends Activity implements Runnable {
    private StructSettings mSettings;
    TextView textView1;
    ProgressBar progressBar1;
    private Thread currentThread;
    private boolean mThreadActive = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_data);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();

        textView1 = (TextView) findViewById(R.id.textView1);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar2);

        if (!mThreadActive) {
            currentThread = new Thread(this);
            currentThread.start();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler threadHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            TextView txtView1 = (TextView) findViewById(R.id.textView1);
            txtView1.setText(msg.obj.toString());
        }
    };

    @Override
    public void run() {
        DatabaseHandler db = new DatabaseHandler(this);
        WebService webService = new WebService();

        mSettings = db.getSettings();

        String status = webService.checkStatus(mSettings.getURL(), mSettings.getDSN());
        if (status.equals("0")) {
            db.deleteAllData();
            loadAccounts(webService, db);
            loadContacts(webService, db);
            loadEmployees(webService, db);
        }

        finish();
    }

    @SuppressLint({"UseValueOf", "UseValueOf", "UseValueOf", "UseValueOf",
            "UseValueOf", "UseValueOf"})
    private void loadAccounts(WebService webService, DatabaseHandler db) {

        Message msg = Message.obtain(threadHandler);
        msg.obj = "Loading accounts ... ";
        threadHandler.sendMessage(msg);

        progressBar1.setProgress(0);

        Integer offset = 0;
        Integer rows = 1000;
        Integer progress = 0;
        Integer records = 0;

        NodeList nodeLst1 = webService
                .runSQL(mSettings.getURL(),
                        mSettings.getDSN(),
                        "SELECT sum(x.count) as cnt FROM (" +
                                " SELECT count(customerid) as count FROM customer" +
                                " UNION ALL" +
                                " SELECT count(keyfield) as count FROM prospect" +
                                " UNION ALL" +
                                " SELECT count(keyfield) as count FROM supplier" +
                                " ) x;");
        try {
            for (int s = 0; s < nodeLst1.getLength(); s++) {

                Node fstNode = nodeLst1.item(s);

                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element fstElmnt = (Element) fstNode;
                    records = new Integer(webService.GetNode(fstElmnt, "cnt"));
                }
            }
        } finally {

        }

        if (records < 100) {
            records = 100;
        }

        while (rows == 1000) {

            rows = 0;
            progress = 0;

            NodeList nodeLst = webService
                    .runSQL(mSettings.getURL(),
                            mSettings.getDSN(),
                            "SELECT customerid as accountid, name, 'C' as type" +
                                    " FROM customer" +
                                    " UNION ALL" +
                                    " SELECT keyfield as accountid, name, 'P' as type" +
                                    " FROM prospect" +
                                    " UNION ALL" +
                                    " SELECT keyfield as accountid, name, 'S' as type" +
                                    " FROM supplier" +
                                    " LIMIT 1000" +
                                    " OFFSET " + offset +
                                    ";");
            db.beginTransaction();
            try {
                for (int s = 0; s < nodeLst.getLength(); s++) {

                    Node fstNode = nodeLst.item(s);

                    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element fstElmnt = (Element) fstNode;

                        StructAccount account = new StructAccount();
                        account.setAccountId(new Integer(webService.GetNode(fstElmnt, "accountid")));
                        account.setType(webService.GetNode(fstElmnt, "type"));
                        account.setName(webService.GetNode(fstElmnt, "name"));
                        db.insertAccount(account);

                        if (progress % new Integer(records / 100) == 0) {
                            progressBar1.incrementProgressBy(1);
                        }

                        rows += 1;
                        progress += 1;

                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            offset = offset + rows;
        }
    }

    @SuppressLint({"UseValueOf", "UseValueOf", "UseValueOf", "UseValueOf",
            "UseValueOf", "UseValueOf"})
    private void loadContacts(WebService webService, DatabaseHandler db) {

        Message msg = Message.obtain(threadHandler);
        msg.obj = "Loading contacts ... ";
        threadHandler.sendMessage(msg);

        progressBar1.setProgress(0);

        Integer offset = 0;
        Integer rows = 1000;
        Integer progress = 0;
        Integer records = 0;

        NodeList nodeLst1 = webService
                .runSQL(mSettings.getURL(),
                        mSettings.getDSN(),
                        "SELECT sum(x.count) as cnt FROM (" +
                                " SELECT count(keyfield) as count FROM slcname" +
                                " UNION ALL" +
                                " SELECT count(keyfield) as count FROM prospect_name" +
                                " UNION ALL" +
                                " SELECT count(keyfield) as count FROM plcname" +
                                " ) x;");
        try {
            for (int s = 0; s < nodeLst1.getLength(); s++) {

                Node fstNode = nodeLst1.item(s);

                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element fstElmnt = (Element) fstNode;
                    records = new Integer(webService.GetNode(fstElmnt, "cnt"));
                }
            }
        } finally {

        }

        if (records < 100) {
            records = 100;
        }

        while (rows == 1000) {

            rows = 0;
            progress = 0;

            NodeList nodeLst = webService
                    .runSQL(mSettings.getURL(),
                            mSettings.getDSN(),
                            "SELECT keyfield as contactid, customer_keyfield as accountid, forename, surname, 'C' as type" +
                                    " FROM slcname" +
                                    " UNION ALL" +
                                    " SELECT keyfield as contactid, prospect_keyfield as accountid, forename, surname, 'P' as type" +
                                    " FROM prospect_name" +
                                    " UNION ALL" +
                                    " SELECT keyfield as contactid, supplier_keyfield as accountid, forename, surname, 'S' as type" +
                                    " FROM plcname" +
                                    " LIMIT 1000" +
                                    " OFFSET " + offset +
                                    ";");
            db.beginTransaction();
            try {
                for (int s = 0; s < nodeLst.getLength(); s++) {

                    Node fstNode = nodeLst.item(s);

                    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element fstElmnt = (Element) fstNode;

                        StructContact contact = new StructContact();
                        contact.setContactId(new Integer(webService.GetNode(fstElmnt, "contactid")));
                        contact.setAccountId(new Integer(webService.GetNode(fstElmnt, "accountid")));
                        contact.setType(webService.GetNode(fstElmnt, "type"));
                        contact.setName(webService.GetNode(fstElmnt, "forename") + ' ' + webService.GetNode(fstElmnt, "surname"));
                        db.insertContact(contact);

                        if (progress % new Integer(records / 100) == 0) {
                            progressBar1.incrementProgressBy(1);
                        }

                        rows += 1;
                        progress += 1;

                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            offset = offset + rows;
        }
    }

    @SuppressLint({"UseValueOf", "UseValueOf", "UseValueOf", "UseValueOf",
            "UseValueOf", "UseValueOf"})
    private void loadEmployees(WebService webService, DatabaseHandler db) {

        Message msg = Message.obtain(threadHandler);
        msg.obj = "Loading employees ... ";
        threadHandler.sendMessage(msg);

        progressBar1.setProgress(0);

        Integer offset = 0;
        Integer rows = 1000;
        Integer progress = 0;
        Integer records = 0;

        NodeList nodeLst1 = webService
                .runSQL(mSettings.getURL(),
                        mSettings.getDSN(),
                        "SELECT count(*) as cnt FROM sysuser;");
        try {
            for (int s = 0; s < nodeLst1.getLength(); s++) {

                Node fstNode = nodeLst1.item(s);

                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element fstElmnt = (Element) fstNode;
                    records = new Integer(webService.GetNode(fstElmnt, "cnt"));
                }
            }
        } finally {

        }

        if (records < 100) {
            records = 100;
        }

        while (rows == 1000) {

            rows = 0;
            progress = 0;

            NodeList nodeLst = webService
                    .runSQL(mSettings.getURL(),
                            mSettings.getDSN(),
                            "SELECT sysuserid as employeeid, name" +
                                    " FROM sysuser;");
            db.beginTransaction();
            try {
                for (int s = 0; s < nodeLst.getLength(); s++) {

                    Node fstNode = nodeLst.item(s);

                    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element fstElmnt = (Element) fstNode;

                        StructEmployee employee = new StructEmployee();
                        employee.setEmployeeId(new Integer(webService.GetNode(fstElmnt, "employeeid")));
                        employee.setName(webService.GetNode(fstElmnt, "name"));
                        db.insertEmployee(employee);

                        if (progress % new Integer(records / 100) == 0) {
                            progressBar1.incrementProgressBy(1);
                        }

                        rows += 1;
                        progress += 1;

                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            offset = offset + rows;
        }
    }

    private void loadEmployeesTest(WebService webService, DatabaseHandler db) {

        Message msg = Message.obtain(threadHandler);
        msg.obj = "Loading employees ... ";
        threadHandler.sendMessage(msg);

        progressBar1.setProgress(0);

        Integer offset = 0;
        Integer rows = 1000;
        Integer progress = 0;
        Integer records = 0;

        NodeList nodeLst1 = webService
                .runSQL(mSettings.getURL(),
                        mSettings.getDSN(),
                        "SELECT count(*) as cnt FROM sysuser;");
        try {
            for (int s = 0; s < nodeLst1.getLength(); s++) {

                Node fstNode = nodeLst1.item(s);

                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element fstElmnt = (Element) fstNode;
                    records = new Integer(webService.GetNode(fstElmnt, "cnt"));
                }
            }
        } finally {

        }

        if (records < 100) {
            records = 100;
        }

        while (rows == 1000) {

            rows = 0;
            progress = 0;

            NodeList nodeLst = webService
                    .runSQL(mSettings.getURL(),
                            mSettings.getDSN(),
                            "SELECT sysuserid as employeeid, name" +
                                    " FROM sysuser;");
            db.beginTransaction();
            try {
                for (int s = 0; s < nodeLst.getLength(); s++) {

                    Node fstNode = nodeLst.item(s);

                    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element fstElmnt = (Element) fstNode;

                        StructEmployee employee = new StructEmployee();
                        employee.setEmployeeId(new Integer(webService.GetNode(fstElmnt, "employeeid")));
                        employee.setName(webService.GetNode(fstElmnt, "name"));
                        db.insertEmployee(employee);

                        if (progress % new Integer(records / 100) == 0) {
                            progressBar1.incrementProgressBy(1);
                        }

                        rows += 1;
                        progress += 1;

                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            offset = offset + rows;
        }
    }
}