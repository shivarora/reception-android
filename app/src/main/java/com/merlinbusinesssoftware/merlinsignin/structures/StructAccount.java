package com.merlinbusinesssoftware.merlinsignin.structures;

public class StructAccount {
	private int AccountId = 0;
	private String Type = "";
	private String Name = "";

	public StructAccount(){

	}


	public int getAccountId(){
		return this.AccountId;
	}
	public void setAccountId(int value){
		this.AccountId = value;
	}

	public String getType(){
		return this.Type;
	}
	public void setType(String value){
		this.Type = value;
	}

	public String getName(){
		return this.Name;
	}
	public void setName(String value){
		this.Name = value;
	}
}
