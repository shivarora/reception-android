package com.merlinbusinesssoftware.merlinsignin.structures;

/**
 * Created by jamied on 04/09/2015.
 */
public class StructPending {
    private  static int Id                     = 0;
    private static int AccountId               = 0;
    private static int ContactId               = 0;
    private static int EmployeeId              = 0;
    private static int ReceptionLogId          = 0;
    private static int LogId                   = 0;
    private static int PendingId               = 0;
    private static int department_code         = 0;
    private static int    primaryId            = 0;
    private  static String Type                = "";
    private static String AccountName          = "";
    private static String ContactName          = "";
    private static String EmployeeName         = "";
    private static String VehicleReg           = "";
    private static String Time                 = "";
    private static String ImagePath            = "";
    private static String LocalImagePath       = "";
    private static String StaffId              = "";
    private static String Title                = "";
    private static String staff_image          = "";
    private static String status               = "";
    private static String signinTime           = "";
    private static String signoutTime          = "";
    private static String lastActivity         = "";



    public StructPending() {}

    public  int getId(){
        return this.Id;
    }
    public  void setId(int value){
        this.Id = value;
    }

    public  String getType(){
        return this.Type;
    }
    public  void setType(String value){
        this.Type = value;
    }

    public int getAccountId(){
        return this.AccountId;
    }
    public void setAccountId(int value){ this.AccountId = value; }

    public String getAccountName(){
        return this.AccountName;
    }
    public void setAccountName(String value){
        this.AccountName = value;
    }

    public int getContactId(){
        return this.ContactId;
    }
    public void setContactId(int value){
        this.ContactId = value;
    }

    public String getContactName(){
        return this.ContactName;
    }
    public void setContactName(String value){
        this.ContactName = value;
    }

    public int getEmployeeId(){
        return this.EmployeeId;
    }
    public void setEmployeeId(int value){
        this.EmployeeId = value;
    }

    public String getEmployeeName(){
        return this.EmployeeName;
    }
    public void setEmployeeName(String value){
        this.EmployeeName = value;
    }

    public String getVehicleReg(){
        return this.VehicleReg;
    }
    public void setVehicleReg(String value){
        this.VehicleReg = value;
    }

    public String getTime(){
        return this.Time;
    }
    public void setTime(String value){
        this.Time = value;
    }

    public int getReceptionLogId(){
        return this.ReceptionLogId;
    }
    public void setReceptionLogId(int value){
        this.ReceptionLogId = value;
    }

    public int getLogId(){
        return this.LogId;
    }
    public void setLogId(int value){
        this.LogId = value;
    }

    public int getPendingId(){
        return this.PendingId;
    }
    public void setPendingId(int value){
        this.PendingId = value;
    }

    public String getImagePath(){
        return this.ImagePath;
    }
    public void setImagePath(String value){
        this.ImagePath = value;
    }

    public String getLocalImagePath(){
        return this.LocalImagePath;
    }
    public void setLocalImagePath(String value){
        this.LocalImagePath = value;
    }


    public String getStaffId(){
        return  this.StaffId;
    }
    public void setStaffId(String value){
        this.StaffId = value;
    }

    public String getTitle(){
        return  this.Title;
    }
    public void setTitle(String value){
        this.Title = value;
    }

    public Integer getDepartmentCode(){
        return  this.department_code;
    }
    public void setDepartmentCode(Integer value){this.department_code = value;}

    public String getStaffImagePath(){
        return  this.staff_image;
    }
    public void setStaffImagePath(String value){
        this.staff_image = value;
    }


    public String getSigninTime() {
        return signinTime;
    }

    public void setSigninTime(String signintime) {
        this.signinTime = signintime;
    }

    public String getSignoutTime() {return signoutTime;}

    public void setSignoutTime(String signouttime) { this.signoutTime = signouttime;}

    public String getStatus() { return status;}

    public void setStatus(String status) { this.status = status;}


    public Integer getPrimaryId() { return primaryId;}

    public void setPrimaryId(int primaryId) { this.primaryId = primaryId;}

    public String getLastActivity() { return lastActivity;}

    public void setLastActivity(String lastActivity) { this.lastActivity = lastActivity;}


    public static void reset() {
        Id                  = 0;
        AccountId           = 0;
        EmployeeId          = 0;
        ContactId           = 0;
        ReceptionLogId      = 0;
        LogId               = 0;
        PendingId           = 0;
        department_code     = 0;
        primaryId           = 0;
        Type                = "";
        AccountName         = "";
        ContactName         = "";
        EmployeeName        = "";
        VehicleReg          = "";
        Time                = "";
        ImagePath           = "";
        LocalImagePath      = "";
        StaffId             = "";
        Title               = "";
        status              = "";
        signinTime          = "";
        signoutTime         = "";
        lastActivity        = "";
    }
}
