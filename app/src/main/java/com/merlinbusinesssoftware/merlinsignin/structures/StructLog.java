package com.merlinbusinesssoftware.merlinsignin.structures;

public class StructLog {
    private int Id = 0;
    private int ReceptionLogId = 0;
    private String Name = "";
    private String AccountName = "";
    private int PendingId = 0;
    private String SetTime = "";
    private int VisitorId = 0;
    private String VisitorImage = "";


    public StructLog(){

    }

    public int getId(){
        return this.Id;
    }
    public void setId(int value){
        this.Id = value;
    }

    public int getReceptionLogId(){
        return this.ReceptionLogId;
    }
    public void setReceptionLogId(int value){
        this.ReceptionLogId = value;
    }

    public String getName(){
        return this.Name;
    }
    public void setName(String value){
        this.Name = value;
    }

    public String getAccountName(){
        return this.AccountName;
    }
    public void setAccountName(String value){
        this.AccountName = value;
    }

    public int getPendingId(){
        return this.PendingId;
    }
    public void setPendingId(int value){
        this.PendingId = value;
    }

    public String getSetTime(){
        return this.SetTime;
    }
    public void setSetTime(String value){
        this.SetTime = value;
    }

    public int getVisitorId(){
        return this.VisitorId;
    }
    public void setVisitorId(int value){
        this.VisitorId = value;
    }

    public String getVisitorImage(){
        return this.VisitorImage;
    }
    public void setVisitorImage(String value){
        this.VisitorImage = value;
    }



    public void  reset() {

          Id = 0;
          ReceptionLogId = 0;
          Name = "";
          AccountName = "";
          PendingId = 0;
          SetTime = "";
          VisitorId = 0;
          VisitorImage = "";

    }


}
