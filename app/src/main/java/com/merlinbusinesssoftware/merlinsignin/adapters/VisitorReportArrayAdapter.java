package com.merlinbusinesssoftware.merlinsignin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.merlinbusinesssoftware.merlinsignin.R;
import com.merlinbusinesssoftware.merlinsignin.structures.StructReceptionLog;

import java.util.List;

public class VisitorReportArrayAdapter extends ArrayAdapter<StructReceptionLog> {
    int resource;

    public VisitorReportArrayAdapter(Context context,
                                     int resource,
                                     List<StructReceptionLog> items) {

        super(context, resource, items);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout listview;

        StructReceptionLog item = getItem(position);

        if (convertView == null) {
            listview = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater) getContext().getSystemService(inflater);
            li.inflate(resource, listview, true);
        } else {
            listview = (LinearLayout) convertView;
        }

        TextView txtName = (TextView) listview.findViewById(R.id.txt_name);
        TextView txtCompany = (TextView) listview.findViewById(R.id.txt_company);
        TextView txtVisiting = (TextView) listview.findViewById(R.id.txt_visiting);
        TextView txtVehicleReg = (TextView) listview.findViewById(R.id.txt_vehicle_reg);
        TextView txtSignIn = (TextView) listview.findViewById(R.id.txt_sign_in);

        txtName.setText(item.getContactName());
        txtCompany.setText(item.getAccountName());
        txtVisiting.setText(item.getEmployeeName());
        txtVehicleReg.setText(item.getVehicleReg());
        txtSignIn.setText(item.getSignIn());

        return listview;
    }
}
