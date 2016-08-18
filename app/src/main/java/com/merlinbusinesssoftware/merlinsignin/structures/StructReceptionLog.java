package com.merlinbusinesssoftware.merlinsignin.structures;

public class StructReceptionLog {
    private String AccountName = "";
    private String ContactName = "";
    private String EmployeeName = "";
    private String VehicleReg = "";
    private String SignIn = "";

    public StructReceptionLog(){

    }

    public String getAccountName(){
        return this.AccountName;
    }
    public void setAccountName(String value){
        this.AccountName = value;
    }

    public String getContactName(){
        return this.ContactName;
    }
    public void setContactName(String value){
        this.ContactName = value;
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

    public String getSignIn(){
        return this.SignIn;
    }
    public void setSignIn(String value){
        this.SignIn = value;
    }
}
