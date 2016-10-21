package com.merlinbusinesssoftware.merlinsignin.listeners;

import android.content.Context;

import com.merlinbusinesssoftware.merlinsignin.R;
import com.merlinbusinesssoftware.merlinsignin.SignIn;
import com.merlinbusinesssoftware.merlinsignin.adapters.EmployeesArrayAdapter;
import com.merlinbusinesssoftware.merlinsignin.structures.StructEmployee;

import java.util.ArrayList;

public class EmployeesAutoCompleteTextChangedListener extends CustomAutoCompleteTextChangedListener{
	private Context context;

	public EmployeesAutoCompleteTextChangedListener(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		try{
			if (s.length()>50 || s.length() < 2){
				return;
			}
			SignIn signInActivity = ((SignIn) context);
			signInActivity.employeesAdapter.notifyDataSetChanged();
			signInActivity.StructEmployee = (ArrayList<StructEmployee>)signInActivity.db.searchEmployees(s.toString());
			signInActivity.employeesAdapter = new EmployeesArrayAdapter(signInActivity, R.layout.listview_auto_complete, signInActivity.StructEmployee);
			signInActivity.autoCompleteEmployees.setAdapter(signInActivity.employeesAdapter);

		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
