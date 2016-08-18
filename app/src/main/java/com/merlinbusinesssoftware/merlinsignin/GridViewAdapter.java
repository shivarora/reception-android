package com.merlinbusinesssoftware.merlinsignin;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by aroras on 03/07/16.
 */
public class GridViewAdapter extends ArrayAdapter<GridItem> implements Filterable, SectionIndexer {
    private ArrayList<GridItem> mGridData = new ArrayList<GridItem>();
    private ArrayList<GridItem>             filterlist;
    public PicassoTrustAll                  picassoTrustAll;
    private Context                         mContext;
    private int                             layoutResourceId;
    LayoutInflater                          inflater;
    AlphaFilter                             alphaFilter;
    CustomFilter                            filter;


    public GridViewAdapter(Context mContext, int layoutResourceId, ArrayList<GridItem> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        this.mGridData = mGridData;
        this.filterlist = mGridData;
    }

    @Override
    public  int getCount() {
        return mGridData.size();
    }

    @Override
    public GridItem getItem(int pos){
        return mGridData.get(pos);
    }

    @Override
    public long getItemId(int pos){
        return mGridData.indexOf(getItem(pos));
    }
    /**
     * Updates grid data and refresh grid items.
     * @param mGridData
     */
    public void setGridData(ArrayList<GridItem> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) row.findViewById(R.id.grid_item_title);
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        GridItem item = mGridData.get(position);
        holder.titleTextView.setText(Html.fromHtml(item.getTitle()));

        if(item.getStatus().trim().equals("Outside of Building")){
            picassoTrustAll.getInstance(mContext)
                    .load(item.getImage())
                    .placeholder(mContext.getResources().getDrawable(R.drawable.new_image))
                    .error(mContext.getResources().getDrawable(R.drawable.new_image))
                    .into(holder.imageView);
        }else{

            System.out.println("Inside building");

            picassoTrustAll.getInstance(mContext)
                    .load(item.getImage())
                    .placeholder(mContext.getResources().getDrawable(R.drawable.new_image))
                    .transform(new ColorTransformation(5, 1))
                    .error(mContext.getResources().getDrawable(R.drawable.new_image))
                    .into(holder.imageView);
        }

        return row;
    }

    @Override
    public Object[] getSections() {
        String[] chars = new String[SideSelector.ALPHABET.length];
        for (int i = 0; i < SideSelector.ALPHABET.length; i++) {
            chars[i] = String.valueOf(SideSelector.ALPHABET[i]);
        }

        return chars;

      //  return new Object[0];
    }

    @Override
    public int getPositionForSection(int i) {
        return (int) (getCount() * ((float)i/(float)getSections().length));
        //return 0;
    }

    @Override
    public int getSectionForPosition(int i) {
        return 0;
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }

    @Override
    public Filter getFilter (){

        if(filter == null) {
            filter = new CustomFilter();
        }

        return  filter;
    }


    public Filter alphaFilter (){

        System.out.println("inside alphafilter");

        if(alphaFilter == null) {
            alphaFilter = new AlphaFilter();
        }

        return  alphaFilter;
    }

    class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if(constraint !=null && constraint.length() >0 ) {
                constraint = constraint.toString().trim().toUpperCase();

                ArrayList<GridItem> filters = new ArrayList<GridItem>();


                System.out.println("this is the filtered value" + constraint );
                //Filtering

                for (int i = 0; i < filterlist.size(); i++) {

                    if(filterlist.get(i).getTitle().toUpperCase().contains(constraint)){

                        System.out.println("this is the filtered match in contains" + filterlist.get(i).getTitle());

                        GridItem item = new GridItem();

                        item.setImage(filterlist.get(i).getImage());
                        item.setTitle(filterlist.get(i).getTitle());
                        item.setStaffId(filterlist.get(i).getStaffId());
                        item.setDepartment_code(filterlist.get(i).getDepartment_code());
                        item.setStatus(filterlist.get(i).getStatus());
                        item.setSignin_time(filterlist.get(i).getSignin_time());
                        item.setSignout_time(filterlist.get(i).getSignout_time());
                        item.setPrimaryId(filterlist.get(i).getPrimaryId());
                        item.setLastActivity(filterlist.get(i).getLastActivity());

                        filters.add(item);
                    }else if(filterlist.get(i).getStaffId().contains(constraint)){
                        GridItem item = new GridItem();

                        item.setImage(filterlist.get(i).getImage());
                        item.setTitle(filterlist.get(i).getTitle());
                        item.setStaffId(filterlist.get(i).getStaffId());
                        item.setDepartment_code(filterlist.get(i).getDepartment_code());
                        item.setStatus(filterlist.get(i).getStatus());
                        item.setSignin_time(filterlist.get(i).getSignin_time());
                        item.setSignout_time(filterlist.get(i).getSignout_time());
                        item.setPrimaryId(filterlist.get(i).getPrimaryId());
                        item.setLastActivity(filterlist.get(i).getLastActivity());

                        filters.add(item);
                    }

                }

                results.count = filters.size();
                results.values = filters;

            }else{
                results.count = filterlist.size();
                results.values = filterlist;
            }


            return results;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            mGridData = (ArrayList<GridItem>) results.values;
            notifyDataSetChanged();
        }
    }



    class AlphaFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if(constraint !=null && constraint.length() >0 && constraint != "All") {
                constraint = constraint.toString().trim().toUpperCase();

                ArrayList<GridItem> filters = new ArrayList<GridItem>();


                System.out.println("this is the filtered value" + constraint );
                //Filtering

                for (int i = 0; i < filterlist.size(); i++) {

                    if(filterlist.get(i).getTitle().toUpperCase().startsWith((String) constraint)){

                        System.out.println("this is the filtered match in contains" + filterlist.get(i).getTitle());

                        GridItem item = new GridItem();

                        item.setImage(filterlist.get(i).getImage());
                        item.setTitle(filterlist.get(i).getTitle());
                        item.setStaffId(filterlist.get(i).getStaffId());
                        item.setDepartment_code(filterlist.get(i).getDepartment_code());
                        item.setStatus(filterlist.get(i).getStatus());
                        item.setSignin_time(filterlist.get(i).getSignin_time());
                        item.setSignout_time(filterlist.get(i).getSignout_time());
                        item.setPrimaryId(filterlist.get(i).getPrimaryId());
                        item.setLastActivity(filterlist.get(i).getLastActivity());

                        filters.add(item);
                    }

                }

                results.count = filters.size();
                results.values = filters;

            }else{
                results.count = filterlist.size();
                results.values = filterlist;
            }


            return results;
        }



        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            mGridData = (ArrayList<GridItem>) results.values;
            notifyDataSetChanged();
        }
    }
}