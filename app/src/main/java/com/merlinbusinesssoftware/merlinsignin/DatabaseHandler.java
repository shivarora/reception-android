package com.merlinbusinesssoftware.merlinsignin;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.preference.PreferenceManager;
import android.util.Log;

import com.merlinbusinesssoftware.merlinsignin.structures.StructAccount;
import com.merlinbusinesssoftware.merlinsignin.structures.StructContact;
import com.merlinbusinesssoftware.merlinsignin.structures.StructEmployee;
import com.merlinbusinesssoftware.merlinsignin.structures.StructLog;
import com.merlinbusinesssoftware.merlinsignin.structures.StructPending;
import com.merlinbusinesssoftware.merlinsignin.structures.StructSettings;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "merlin.db";
    private static final int DATABASE_VERSION = 27;
    private SQLiteStatement insertStmt;
    private SQLiteDatabase db;
    private Context mContext;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        db = this.getReadableDatabase();
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE account (id INTEGER PRIMARY KEY, accountid INTEGER NOT NULL UNIQUE," +
                " name TEXT NOT NULL DEFAULT '', type TEXT NOT NULL DEFAULT '');");
        db.execSQL("CREATE TABLE contact (id INTEGER PRIMARY KEY, contactid INTEGER NOT NULL DEFAULT 0," +
                "accountid INTEGER NOT NULL UNIQUE, name TEXT NOT NULL DEFAULT '', type TEXT NOT NULL DEFAULT '');");
        db.execSQL("CREATE TABLE employee (id INTEGER PRIMARY KEY, employeeid INTEGER NOT NULL UNIQUE," +
                "name TEXT NOT NULL DEFAULT '');");
        db.execSQL("CREATE TABLE log(id INTEGER PRIMARY KEY AUTOINCREMENT, reception_log_id INTEGER NOT NULL DEFAULT 0, name TEXT NOT NULL DEFAULT '', pending_id INTEGER NOT NULL DEFAULT 0, set_time TEXT NOT NULL DEFAULT '', visitor_id TEXT NOT NULL DEFAULT '', visitor_image_path TEXT NOT NULL DEFAULT '' );");
        db.execSQL("CREATE UNIQUE INDEX contact_idx1 ON contact (accountid);");
        db.execSQL("CREATE UNIQUE INDEX employee_idx1 ON employee (employeeid);");
        db.execSQL("CREATE TABLE pending (id INTEGER PRIMARY KEY, type TEXT NOT NULL DEFAULT '', accountid INTEGER NOT NULL DEFAULT 0," +
                " account_name TEXT NOT NULL DEFAULT '', contactid INTEGER NOT NULL DEFAULT 0, contact_name TEXT NOT NULL DEFAULT ''," +
                " employeeid INTEGER NOT NULL DEFAULT 0, employee_name TEXT NOT NULL DEFAULT '', vehicle_reg TEXT NOT NULL DEFAULT ''," +
                " time TEXT NOT NULL DEFAULT '', reception_log_id INTEGER NOT NULL DEFAULT 0, log_id INTEGER NOT NULL DEFAULT 0," +
                " pending_id INTEGER NOT NULL DEFAULT 0);");
        db.execSQL("CREATE TABLE settings (id INTEGER PRIMARY KEY, tabletId INTEGER NOT NULL UNIQUE);");
        db.execSQL("CREATE UNIQUE INDEX settings_idx1 ON settings (tabletId);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("CREATE TABLE settings (id INTEGER PRIMARY KEY, tabletId INTEGER NOT NULL UNIQUE);");
        db.execSQL("CREATE UNIQUE INDEX settings_idx1 ON settings (tabletId);");
    }

    public void beginTransaction() {
        try {
            this.db.beginTransaction();
        } catch (Exception e) {
            Log.w("Error", e.getMessage());
        }
    }

    public void endTransaction() {
        try {
            this.db.endTransaction();
        } catch (Exception e) {
            Log.w("Error", e.getMessage());
        }
    }

    public void setTransactionSuccessful() {
        try {
            this.db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.w("Error", e.getMessage());
        }
    }

    public void deleteAllData() {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }

        db.delete("account", "", null);
        db.delete("contact", "", null);
        db.delete("employee", "", null);
        db.delete("log", "", null);
        db.delete("pending", "", null);
    }

    public void deleteLog(int id) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }

        db.delete("log", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deletePreviousLog(String time) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }



        db.delete("log", "set_time<?", new String[]{String.valueOf(time)});
        db.close();
    }

    public void deletePending(int id) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }

        db.delete("pending", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteAllLog() {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }

        db.delete("log", "", null);
        db.close();
    }

    public void insertAccount(StructAccount account) {
        this.insertStmt = db
                .compileStatement("INSERT INTO account (accountid, type, name)"
                        + " values (?,?,?)");
        this.insertStmt.bindLong(1, account.getAccountId());
        this.insertStmt.bindString(2, account.getType());
        this.insertStmt.bindString(3, account.getName());
        this.insertStmt.executeInsert();
    }

    public void replaceAccount(StructAccount account) {

        ContentValues values = new ContentValues();
        values.put("accountid",account.getAccountId() );
        values.put("type",account.getType() );
        values.put("name",account.getName() );
        db.replace("account", null, values);

    }

    public void deleteAccount(int id) {
        db.delete("account", "accountid=?", new String[]{String.valueOf(id)});
    }

    public void deleteAllAccount() {
        db.delete("account", "", null);
    }


    public void insertContact(StructContact contact) {
        this.insertStmt = db
                .compileStatement("INSERT INTO contact (accountid, name, type)"
                        + " values (?,?,?)");
        //this.insertStmt.bindLong(1, contact.getContactId());
        this.insertStmt.bindLong(1, contact.getAccountId());
        this.insertStmt.bindString(2, contact.getName());
        this.insertStmt.bindString(3, contact.getType());
        this.insertStmt.executeInsert();
    }

    public void replaceContact(StructContact account) {

        ContentValues values = new ContentValues();
        values.put("accountid",account.getAccountId() );
        values.put("type",account.getType() );
        values.put("name",account.getName() );
        db.replace("contact", null, values);

    }

    public void deleteContact(int id) {
        db.delete("contact", "accountid=?", new String[]{String.valueOf(id)});
    }

    public void deleteAllContact() {
        db.delete("contact", "", null);
    }



    public void insertEmployee(StructEmployee employee) {
        this.insertStmt = db
                .compileStatement("INSERT INTO employee (employeeid, name)"
                        + " values (?,?)");
        this.insertStmt.bindLong(1, employee.getEmployeeId());
        this.insertStmt.bindString(2, employee.getName());
        this.insertStmt.executeInsert();

    }

    public void replaceEmployee(StructEmployee employee) {

        ContentValues values = new ContentValues();
        values.put("employeeid",employee.getEmployeeId() );
        values.put("name",employee.getName() );
        db.replace("employee", null, values);
    }

    public void deleteEmployee(int id) {
        db.delete("employee", "employeeid=?", new String[]{String.valueOf(id)});
    }

    public void deleteAllEmployee() {
        db.delete("employee", "", null);
    }

    public int insertLog(StructLog log) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }
        ContentValues values = new ContentValues();
        values.put("reception_log_id", log.getReceptionLogId());
        values.put("name", log.getName());
        values.put("pending_id", log.getPendingId());
        values.put("set_time", log.getSetTime());

        int result = (int) db.insert("log", null, values);

        db.close();

        return result;
    }

    public int insertPending(StructPending pending) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }
        ContentValues values = new ContentValues();
        values.put("type", pending.getType());
        values.put("accountid", pending.getAccountId());
        values.put("account_name", pending.getAccountName());
        values.put("contactid", pending.getContactId());
        values.put("contact_name", pending.getContactName());
        values.put("employeeid", pending.getEmployeeId());
        values.put("employee_name", pending.getEmployeeName());
        values.put("vehicle_reg", pending.getVehicleReg());
        values.put("time", pending.getTime());
        values.put("reception_log_id", pending.getReceptionLogId());
        values.put("log_id", pending.getLogId());
        values.put("pending_id", pending.getPendingId());
        int result = 0;
        try {
            result = (int) db.insert("pending", null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();
        return result;
    }

    public StructSettings getSettings() {
        StructSettings settings = new StructSettings();

        SharedPreferences Prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        settings.setURL(Prefs.getString("url", ""));
        settings.setDSN(Prefs.getString("dsn", ""));

        return settings;
    }

    public StructAccount getAccount(int accountId, String type) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }

        StructAccount account = new StructAccount();

        String selectQuery = "SELECT accountid, name, type" +
                " FROM account" +
                " WHERE accountid=" + accountId +
                " AND type='" + type + "'" +
                ";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                account.setAccountId(cursor.getInt(0));
                account.setName(cursor.getString(1));
                account.setType(cursor.getString(2));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return account;
    }

    public List<StructContact> searchContacts_old(String searchTerm) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }

        List<StructContact> contacts = new ArrayList<>();

        String selectQuery = "SELECT contact.contactid, contact.accountid, contact.name, contact.type, account.name" +
                " FROM contact" +
                " INNER JOIN account ON account.accountid=contact.accountid AND account.type=contact.type" +
                " WHERE contact.name LIKE '%" + searchTerm + "%'" +
                " ORDER BY contact.name" +
                " LIMIT 10;";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                StructContact contact = new StructContact();
                contact.setContactId(cursor.getInt(0));
                contact.setAccountId(cursor.getInt(1));
                contact.setName(cursor.getString(2));
                contact.setType(cursor.getString(3));
                contact.setAccountName(cursor.getString(4));
                contacts.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return contacts;
    }

    public List<StructContact> searchContacts(String searchTerm) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }

        List<StructContact> contacts = new ArrayList<>();

        String selectQuery = "SELECT contact.contactid, contact.accountid, contact.name, contact.type" +
                " FROM contact" +
                " WHERE contact.name LIKE '%" + searchTerm + "%'" +
                " ORDER BY contact.name" +
                " LIMIT 5;";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                StructContact contact = new StructContact();
                contact.setContactId(cursor.getInt(0));
                contact.setAccountId(cursor.getInt(1));
                contact.setName(cursor.getString(2));
                contact.setType(cursor.getString(3));
                contact.setAccountName("");
                contacts.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return contacts;
    }

    public List<StructAccount> searchAccounts(String searchTerm) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }

        List<StructAccount> accounts = new ArrayList<>();

        String selectQuery = "SELECT id, accountid, name, type" +
                " FROM account" +
                " WHERE name LIKE '%" + searchTerm + "%'" +
                " ORDER BY name" +
                " LIMIT 5;";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                System.out.println("tis is the search data for Accounts id" + cursor.getInt(0) + "accountID "+ cursor.getInt(1) + " Name "+ cursor.getString(2) + " TYpe" + cursor.getString(3) );

                StructAccount account = new StructAccount();
                account.setAccountId(cursor.getInt(1));
                account.setName(cursor.getString(2));
                account.setType(cursor.getString(3));
                accounts.add(account);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return accounts;
    }

    public List<StructEmployee> searchEmployees(String searchTerm) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }

        List<StructEmployee> employees = new ArrayList<>();

        String selectQuery = "SELECT id,employeeid, name" +
                " FROM employee" +
                " WHERE name LIKE '%" + searchTerm + "%'" +
                " ORDER BY name" +
                " LIMIT 5;";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                System.out.println("tis is the search data for Employee  is id" + cursor.getInt(0) + "EmployeeId "+ cursor.getInt(1) + " Name "+ cursor.getString(2));

                StructEmployee employee = new StructEmployee();
                employee.setEmployeeId(cursor.getInt(1));
                employee.setName(cursor.getString(2));
                employees.add(employee);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return employees;
    }

    public StructLog getLog(int id) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }

        StructLog log = new StructLog();

        String selectQuery = "SELECT id, reception_log_id, name, pending_id, set_time" +
                " FROM log" +
                " WHERE id=" + id +
                ";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            log.setId(cursor.getInt(0));
            log.setReceptionLogId(cursor.getInt(1));
            log.setName(cursor.getString(2));
            log.setPendingId(cursor.getInt(3));

            // print the results
            System.out.println("Log Data======================================");
            System.out.println(cursor.getString(4));
            System.out.println("Log Data End ======================================");
            // print the results

        }

        cursor.close();
        db.close();
        return log;
    }

    // stopping this fucntion as we need to show visitor id now while logging out now.

    public List<StructLog> searchLog(Integer searchTerm) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }

        List<StructLog> logs = new ArrayList<>();

        String selectQuery = "SELECT id, reception_log_id, name, pending_id, visitor_id, visitor_image_path" +
                " FROM log" +
                " WHERE visitor_id = " + searchTerm ;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                StructLog log = new StructLog();

                log.setId(cursor.getInt(0));
                log.setReceptionLogId(cursor.getInt(1));
                log.setName(cursor.getString(2));
                log.setPendingId(cursor.getInt(3));
                log.setVisitorId(cursor.getInt(4));
                log.setVisitorImage(cursor.getString(5));
                logs.add(log);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return logs;
    }

    public List<StructLog> searchLog_new(String searchTerm) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }

        List<StructLog> logs = new ArrayList<>();

        String selectQuery = "SELECT id, reception_log_id, name, pending_id, visitor_id, visitor_image_path" +
                " FROM log" +
                " WHERE visitor_id LIKE '%" + searchTerm + "%'" +
                " ORDER BY visitor_id" +
                " LIMIT 10;";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                StructLog log = new StructLog();
                log.setId(cursor.getInt(0));
                log.setReceptionLogId(cursor.getInt(1));
                log.setName(cursor.getString(2));
                log.setPendingId(cursor.getInt(3));
                log.setVisitorId(cursor.getInt(4));
                log.setVisitorImage(String.valueOf(cursor.getInt(5)));
                logs.add(log);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return logs;
    }

    public List<StructPending> getAllPending() {
        List<StructPending> pendings = new ArrayList<StructPending>();
        String selectQuery = "SELECT id, type, accountid" +
                ", account_name, contactid, contact_name" +
                ", employeeid, employee_name, vehicle_reg" +
                ", time, reception_log_id, log_id, pending_id" +
                " FROM pending" +
                " ORDER BY id" +
                " ";

        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                StructPending pending = new StructPending();
                pending.setId(cursor.getInt(0));
                pending.setType(cursor.getString(1));
                pending.setAccountId(cursor.getInt(2));
                pending.setAccountName(cursor.getString(3));
                pending.setContactId(cursor.getInt(4));
                pending.setContactName(cursor.getString(5));
                pending.setEmployeeId(cursor.getInt(6));
                pending.setEmployeeName(cursor.getString(7));
                pending.setVehicleReg(cursor.getString(8));
                pending.setTime(cursor.getString(9));
                pending.setReceptionLogId(cursor.getInt(10));
                pending.setLogId(cursor.getInt(11));
                pending.setPendingId(cursor.getInt(12));
                pendings.add(pending);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return pendings;
    }

    public StructPending getPendingById(int id){
        StructPending pending = new StructPending();
        String selectQuery = "SELECT id, type, accountid" +
                ", account_name, contactid, contact_name" +
                ", employeeid, employee_name, vehicle_reg" +
                ", time, reception_log_id, log_id, pending_id" +
                " FROM pending" +
                " WHERE id=" + id +
                ";";

        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            pending.setId(cursor.getInt(0));
            pending.setType(cursor.getString(1));
            pending.setAccountId(cursor.getInt(2));
            pending.setAccountName(cursor.getString(3));
            pending.setContactId(cursor.getInt(4));
            pending.setContactName(cursor.getString(5));
            pending.setEmployeeId(cursor.getInt(6));
            pending.setEmployeeName(cursor.getString(7));
            pending.setVehicleReg(cursor.getString(8));
            pending.setTime(cursor.getString(9));
            pending.setReceptionLogId(cursor.getInt(10));
            pending.setLogId(cursor.getInt(11));
            pending.setPendingId(cursor.getInt(12));
        }

        cursor.close();
        db.close();
        return pending;
    }

    public void updatePendingReceptionLogId(int id, int receptionLogId) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }

        ContentValues values = new ContentValues();
        values.put("reception_log_id", receptionLogId);
        db.update("pending", values, "id=" + id, null);
        db.close();
    }

    public void updatePendingPendingId(int id, int pendingId) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }

        ContentValues values = new ContentValues();
        values.put("pending_id", pendingId);
        db.update("pending", values, "id=" + id, null);
        db.close();
    }

    public void updateLogReceptionLogId(int id, int receptionLogId) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }

        ContentValues values = new ContentValues();
        values.put("reception_log_id", receptionLogId);
        db.update("log", values, "id=" + id, null);
        db.close();
    }

    public void updateLogVisitorId(int id, String visitor_image_path,int visitorId) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }

        ContentValues values = new ContentValues();
        values.put("visitor_id", visitorId);
        values.put("visitor_image_path", visitor_image_path);
        db.update("log", values, "id=" + id, null);
        db.close();
    }

    public void insertLogVisitorId(int id, String visitor_image_path,int visitorId) {


        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }
        ContentValues values = new ContentValues();
        values.put("visitor_id", visitorId);
        values.put("visitor_image_path", visitor_image_path);
        int result = (int) db.insert("log", null, values);

        db.close();
    }

    public void updateLogPendingId(int id, int pendingId) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }

        ContentValues values = new ContentValues();
        values.put("pending_id", pendingId);
        System.out.println("updating inside");
        System.out.println("id " + id);
        System.out.println("pend " + pendingId);
        System.out.println("this peint is before updte.take a look");
        db.update("log", values, "id=" + id, null);
        System.out.println("this peint is after updte.take a look");

        db.close();
    }

    public void insertTabletId(int id) {

        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }
        ContentValues values = new ContentValues();
        values.put("tabletId", id);
        int result = (int) db.insert("settings", null, values);
        db.close();
    }


    public StructSettings getAllSettings() {


        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }

        StructSettings settings = new StructSettings();

        String selectQuery = "SELECT id, tabletId" +
                " FROM settings ";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            settings.setId(cursor.getInt(0));
            settings.setTabletId(cursor.getInt(1));
            // print the results
            System.out.println("Log Data======================================");
            System.out.println(cursor.getInt(0));
            System.out.println(cursor.getInt(1));
            System.out.println("Log Data End ======================================");
            // print the results
        }

        cursor.close();
        db.close();
        return settings;
    }

    public void updateTabletId(int tabletId) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }

        ContentValues values = new ContentValues();
        values.put("tabletId", tabletId);
        db.update("settings", values, "tabletId=" + tabletId, null);
        db.close();
    }

}