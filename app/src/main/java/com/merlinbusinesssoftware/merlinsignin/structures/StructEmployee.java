package com.merlinbusinesssoftware.merlinsignin.structures;

public class StructEmployee {
	private int EmployeeId = 0;
	private String Name = "";

	public StructEmployee(){

	}

	public int getEmployeeId(){
		return this.EmployeeId;
	}
	public void setEmployeeId(int value){
		this.EmployeeId = value;
	}

	public String getName(){
		return this.Name;
	}
	public void setName(String value){
		this.Name = value;
	}
}