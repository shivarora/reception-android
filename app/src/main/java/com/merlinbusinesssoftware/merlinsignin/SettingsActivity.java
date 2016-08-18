package com.merlinbusinesssoftware.merlinsignin;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SettingsActivity extends Activity{

	private String mURL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LoadWebServiceSettings();

		getFragmentManager().beginTransaction()
		.replace(android.R.id.content, new SettingsFragment(mURL))
		.commit();
	}

	private void LoadWebServiceSettings() {
		SharedPreferences Prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mURL = Prefs.getString("url", "");
	}
}
