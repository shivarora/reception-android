package com.merlinbusinesssoftware.merlinsignin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends MyBaseActivity{

	private Integer tabId;
	private EditText TabText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings_header);

		LayoutInflater li = LayoutInflater.from(this);
		View dialogView = li.inflate(R.layout.activity_user_settings, null);

		TabText = (EditText) dialogView.findViewById(R.id.tabletId);

		tabId = findTabId();

		if(tabId != 0){
			TabText.setText("" + tabId);
		}else{
			TabText.setText("" + 0);
		}

		TabText.setSelection(TabText.getText().length());

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setView(dialogView);

		alertDialogBuilder
				.setTitle("TabletId")
				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								onDestroy();

								tabId = Integer.parseInt(TabText.getText().toString());

								System.out.println("There is tablet ID setup By User.Check It" + tabId);

								if(tabId == 0){
									System.out.println("There is no tablet ID setup.");
									db.insertTabletId(tabId);
								}else{
									System.out.println("This is already setup Tab Id" + tabId);
									db.updateTabletId(tabId);
								}

								toast("Tablet ID has been successfully set up");
								Main();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								Main();
							}
						});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}

}
