package com.merlinbusinesssoftware.merlinsignin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.merlinbusinesssoftware.merlinsignin.R;
import com.merlinbusinesssoftware.merlinsignin.structures.StructContact;

import java.util.List;

public class ContactsArrayAdapter extends ArrayAdapter<StructContact>{
	int resource;

	public ContactsArrayAdapter(Context context, 
			int resource, 
			List<StructContact> items){

		super(context, resource, items);
		this.resource = resource;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		LinearLayout listview;

		StructContact item = getItem(position);

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
