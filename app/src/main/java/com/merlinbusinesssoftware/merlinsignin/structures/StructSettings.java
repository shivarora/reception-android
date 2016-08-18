package com.merlinbusinesssoftware.merlinsignin.structures;

public class StructSettings {
	private String URL = "";
	private String DSN = "";

	public StructSettings(){

	}

	public String getURL(){
		return this.URL;
	}
	public void setURL(String value){
		this.URL = value;
	}

	public String getDSN(){
		return this.DSN;
	}
	public void setDSN(String value){
		this.DSN = value;
	}
}
