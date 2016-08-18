package com.merlinbusinesssoftware.merlinsignin.listeners;

import android.content.Context;

import com.merlinbusinesssoftware.merlinsignin.R;
import com.merlinbusinesssoftware.merlinsignin.SignIn;
import com.merlinbusinesssoftware.merlinsignin.adapters.AccountsArrayAdapter;
import com.merlinbusinesssoftware.merlinsignin.structures.StructAccount;

import java.util.ArrayList;

public class AccountsAutoCompleteTextChangedListener extends CustomAutoCompleteTextChangedListener{
	private Context context;

	public AccountsAutoCompleteTextChangedListener(Context context) {
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
			signInActivity.accountsAdapter.notifyDataSetChanged();
			signInActivity.StructAccount = (ArrayList<StructAccount>)signInActivity.db.searchAccounts(s.toString());
			signInActivity.accountsAdapter = new AccountsArrayAdapter(signInActivity, R.layout.listview_auto_complete, signInActivity.StructAccount);
			signInActivity.autoCompleteAccounts.setAdapter(signInActivity.accountsAdapter);

		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
