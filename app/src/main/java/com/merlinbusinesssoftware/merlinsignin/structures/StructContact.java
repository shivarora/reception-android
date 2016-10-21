package com.merlinbusinesssoftware.merlinsignin.structures;

public class StructContact {
	private int ContactId = 0;
	private int AccountId = 0;
	private String Name = "";
	private String Type = "";
	private String AccountName = "";

	public StructContact(){

	}

	public int getContactId(){
		return this.ContactId;
	}
	public void setContactId(int value){
		this.ContactId = value;
	}

	public int getAccountId(){
		return this.AccountId;
	}
	public void setAccountId(int value){
		this.AccountId = value;
	}

	public String getName(){
		return this.Name;
	}
	public void setName(String value){
		this.Name = value;
	}

	public String getType(){
		return this.Type;
	}
	public void setType(String value){
		this.Type = value;
	}

	public String getAccountName(){
		return this.AccountName;
	}
	public void setAccountName(String value){
		this.AccountName = value;
	}

	public void reset() {
		Type = "";
		ContactId = 0;
		AccountName = "";
		ContactId = 0;
		Name = "";
		Type = "";
	}
}