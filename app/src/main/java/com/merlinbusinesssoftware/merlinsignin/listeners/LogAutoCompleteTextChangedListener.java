package com.merlinbusinesssoftware.merlinsignin.listeners;

import android.content.Context;

import com.merlinbusinesssoftware.merlinsignin.R;
import com.merlinbusinesssoftware.merlinsignin.SignOut;
import com.merlinbusinesssoftware.merlinsignin.adapters.LogArrayAdapter;
import com.merlinbusinesssoftware.merlinsignin.structures.StructLog;

import java.util.ArrayList;

public class LogAutoCompleteTextChangedListener extends CustomAutoCompleteTextChangedListener{
    private Context context;

    public LogAutoCompleteTextChangedListener(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try{
            if (s.length()>50 || s.length() < 2){
                return;
            }
            SignOut signOutActivity = ((SignOut) context);
            signOutActivity.logAdapter.notifyDataSetChanged();
            //signOutActivity.StructLog = (ArrayList<StructLog>)signOutActivity.db.searchLog(s.toString());
            signOutActivity.StructLog = (ArrayList<StructLog>)signOutActivity.db.searchLog(Integer.parseInt(String.valueOf(s)));
            signOutActivity.logAdapter = new LogArrayAdapter(signOutActivity, R.layout.listview_auto_complete_signout, signOutActivity.StructLog);
            signOutActivity.autoCompleteLog.setAdapter(signOutActivity.logAdapter);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
