package com.merlinbusinesssoftware.merlinsignin.listeners;

import android.content.Context;

import com.merlinbusinesssoftware.merlinsignin.R;
import com.merlinbusinesssoftware.merlinsignin.SignIn;
import com.merlinbusinesssoftware.merlinsignin.adapters.ContactsArrayAdapter;
import com.merlinbusinesssoftware.merlinsignin.structures.StructContact;

import java.util.ArrayList;

public class ContactsAutoCompleteTextChangedListener extends CustomAutoCompleteTextChangedListener{
	private Context context;

	public ContactsAutoCompleteTextChangedListener(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		try{

			System.out.println(" Going to searchContact function");

			if (s.length() >50 || s.length()< 2){
				return;
			}
			SignIn signInActivity = ((SignIn) context);
			signInActivity.contactsAdapter.notifyDataSetChanged();
			signInActivity.StructContact = (ArrayList<StructContact>)signInActivity.db.searchContacts(s.toString());
			signInActivity.contactsAdapter = new ContactsArrayAdapter(signInActivity, R.layout.listview_auto_complete, signInActivity.StructContact);
			signInActivity.autoCompleteContacts.setAdapter(signInActivity.contactsAdapter);

		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
