package com.merlinbusinesssoftware.merlinsignin.structures;

public class StructSettings {
	private String URL = "";
	private String DSN = "";
	private Integer TABLETID = 0;
	private Integer ID = 0;


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

	public Integer getTabletId(){
		return this.TABLETID;
	}
	public void setTabletId(Integer value){
		this.TABLETID = value;
	}

	public Integer getId(){	return this.ID;	}
	public void setId(Integer value){
		this.ID = value;
	}


}
