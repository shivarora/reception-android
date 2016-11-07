package com.merlinbusinesssoftware.merlinsignin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import java.util.ArrayList;

/**
 * Created by aroras on 03/11/2016.
 */

public class WebPageActivity extends Fragment {

    private static final String TAG = "WebPageActivity" ;
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private GridViewAdapter         mGridAdapter;
    private ArrayList<GridItem> mGridData;
    private InputMethodManager imm;
    private View rootView;
    public Integer buttonId         = 0;
    public DatabaseHandler  db;
    private Integer tabId;

    private String FEED_URL = "";

    // editText search
    SearchView editsearch;

    @Override
    public  void onActivityCreated(Bundle savedInstanceState){
        //Initialize with empty data
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_marshall, container, false);

        return rootView;
    }

}


