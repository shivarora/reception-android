package com.merlinbusinesssoftware.merlinsignin.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.merlinbusinesssoftware.merlinsignin.R;
import com.merlinbusinesssoftware.merlinsignin.structures.StructEmployee;

public class EmployeesArrayAdapter extends ArrayAdapter<StructEmployee>{
	int resource;

	public EmployeesArrayAdapter(Context context, 
			int resource, 
			List<StructEmployee> items){

		super(context, resource, items);
		this.resource = resource;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		LinearLayout listview;

		StructEmployee item = getItem(position);

		if (convertView == null){
			listview = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li;
			li = (LayoutInflater)getContext().getSystemService(inflater);
			li.inflate(resource, listview, true);
		} else {
			listview = (LinearLayout) convertView;
		}

		TextView txtItem = (TextView)listview.findViewById(R.id.textViewItem);
		txtItem.setText(item.getName());

		return listview;
	}
}
