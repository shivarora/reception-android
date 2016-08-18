package com.merlinbusinesssoftware.merlinsignin.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.merlinbusinesssoftware.merlinsignin.R;
import com.merlinbusinesssoftware.merlinsignin.structures.StructLog;

import java.util.List;

public class LogArrayAdapter extends ArrayAdapter<StructLog>{
    int resource;

    public LogArrayAdapter(Context context,
                                int resource,
                                List<StructLog> items){

        super(context, resource, items);
        this.resource = resource;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LinearLayout listview;

        StructLog item = getItem(position);

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


        ImageView image = (ImageView) listview.findViewById(R.id.visitorImage);


        System.out.println(item.getVisitorImage());

        Bitmap bmp = BitmapFactory.decodeFile(item.getVisitorImage());
        image.setImageBitmap(bmp);

        return listview;
    }
}
