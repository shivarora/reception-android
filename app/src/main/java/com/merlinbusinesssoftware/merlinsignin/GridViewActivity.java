package com.merlinbusinesssoftware.merlinsignin;

/**
 * Created by aroras on 03/07/16.
 */

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GridViewActivity extends Fragment  {

    private static final String TAG = "GridViewActivity" ;
    private GridView                mGridView;
    private ProgressBar             mProgressBar;
    private GridViewAdapter         mGridAdapter;
    private ArrayList<GridItem>     mGridData;
    private InputMethodManager      imm;
    private View                    rootView;
    public Integer buttonId         = 0;

    private String FEED_URL =  Constants.BACKEND_SERVER_URL_ALL_STAFF;

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
        rootView = inflater.inflate(R.layout.fragment_two, container, false);
        //set up fucntion to check if anyone clicking rather than searchview
        setupUI(rootView);

        //get the input method manager service
        imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, mGridData);
        mGridView.setAdapter(mGridAdapter);

        // Locate the EditText in listview_main.xml
        editsearch = (SearchView) rootView.findViewById(R.id.searchView);
        editsearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if(buttonId > 0) {
                    Button previousButton = (Button) rootView.findViewById(buttonId);
                    if(previousButton !=null) {
                        previousButton.setBackgroundColor(Color.GRAY);
                    }
                }

                mGridAdapter.getFilter().filter(query);

                return false;
            }
        });

        ImageView img = (ImageView) rootView.findViewById(R.id.back_StaffSignIn);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onDestroy();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });


        //Grid view click event
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                GridItem item = (GridItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                ImageView imageView = (ImageView) v.findViewById(R.id.grid_item_image);

                // Interesting data to pass across are the thumbnail size/location, the
                // resourceId of the source bitmap, the picture description, and the
                // orientation (to avoid returning back to an obsolete configuration if
                // the device rotates again in the meantime)

                int[] screenLocation = new int[2];
                imageView.getLocationOnScreen(screenLocation);

                //Pass the image title and url to DetailsActivity
                intent. putExtra("title", item.getTitle()).
                        putExtra("staffId", item.getStaffId()).
                        putExtra("department_code", item.getDepartment_code()).
                        putExtra("status", item.getStatus()).
                        putExtra("signinTime", item.getSignin_time()).
                        putExtra("signoutTime", item.getSignout_time()).
                        putExtra("primaryId", item.getPrimaryId()).
                        putExtra("image", item.getImage()).
                        putExtra("lastActivity", item.getLastActivity());

                //Start details activity
                startActivity(intent);
            }
        });

        //Start download
        new AsyncHttpTask().execute(FEED_URL);

        mProgressBar.setVisibility(rootView.VISIBLE);

        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.buttonIdForAlpha);

        Button btnAllTag    = new Button(getActivity());
        btnAllTag.setLayoutParams(new LinearLayout.LayoutParams(50, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnAllTag.setText("All");
        btnAllTag.setId(Integer.parseInt(""+1));
        btnAllTag.setOnClickListener(getOnClickDoSomething(btnAllTag));
        layout.addView(btnAllTag);

        for (int i = 0; i < 1; i++) {
            LinearLayout row = new LinearLayout(getActivity());
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            char ch;

            for(ch = 'A'; ch <= 'Z'; ch++){
                Button btnTag = new Button(getActivity());
                btnTag.setLayoutParams(new LinearLayout.LayoutParams(37, LinearLayout.LayoutParams.WRAP_CONTENT));
                btnTag.setText(""+ch);
                btnTag.setId(ch);
                btnTag.setOnClickListener(getOnClickDoSomething(btnTag));
                row.addView(btnTag);
            }

            layout.addView(row);
        }


        return rootView;
    }




    View.OnClickListener getOnClickDoSomething(final Button button)  {
        return new View.OnClickListener() {
            public void onClick(View v) {

                if(buttonId > 0) {
                    Button previousButton = (Button) rootView.findViewById(buttonId);
                    if(previousButton !=null) {
                        previousButton.setBackgroundColor(Color.GRAY);
                    }

                }

                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setStroke(5, Color.GREEN);
                drawable.setColor(Color.GREEN);

                button.setBackgroundDrawable(drawable);
                int buttonID = button.getId();

                buttonId = buttonID;

                System.out.println(buttonID);

                String buttonText = button.getText().toString();

                System.out.println("this is button text" + buttonText);

                mGridAdapter.alphaFilter().filter(buttonText);

            }
        };
    }

    public void setupUI(View view) {
        System.out.println("inside the staff selection view");

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof SearchView)) {

            System.out.println("Its not seacrchview");

            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                    editsearch = (SearchView) rootView.findViewById(R.id.searchView);
                    if(editsearch == null) return false;
                    editsearch.clearFocus();
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            System.out.println("Dont KNow");

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }

    }

    //Downloading data asynchronously
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            System.out.println("insideasyntask");

            Integer result = 0;
            try {

                System.out.println("Going to fetch data from backend app");
                // Create Apache HttpClient
                HttpClient httpClient = ExSSLSocketFactory.getHttpsClient(new DefaultHttpClient());
                HttpResponse httpResponse = httpClient.execute(new HttpGet(params[0]));
                int statusCode = httpResponse.getStatusLine().getStatusCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    System.out.println("successful response");
                    String response = streamToString(httpResponse.getEntity().getContent());

                    System.out.println("Response string" + response);

                    parseResult(response);
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Lets update UI
            if (result == 1) {
                mGridAdapter.setGridData(mGridData);

            } else {
                System.out.println("failed to fetch data");
            }
            //Hide progressbar
            mProgressBar.setVisibility(View.GONE);
        }
    }


     String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        // Close stream
        if (null != stream) {
            stream.close();
        }
        return result;
    }

    /**
     * Parsing the feed results and get the list
     *
     * @param result
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void parseResult(String result) {

        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("data");
            GridItem item;

            JSONArray sortedJsonArray = sortJsonArray(posts);

            for (int i = 0; i < sortedJsonArray.length(); i++) {

                JSONObject post = sortedJsonArray.optJSONObject(i);

                String title = post.optString("first_name") + " " + post.optString("surname");
                Integer department_code = post.optInt("department_id");
                String staffId = post.optString("employee_number");
                String status = post.optString("status");
                String signin_time = post.optString("signinTime");
                String signout_time = post.optString("signoutTime");
                Integer primaryId = post.optInt("primaryId");
                String lastActivity = post.optString("lastActivity");

                item = new GridItem();

                item.setTitle(title);
                item.setStaffId(staffId);
                item.setDepartment_code(department_code);
                item.setStatus(status);
                item.setSignin_time(signin_time);
                item.setSignout_time(signout_time);
                item.setPrimaryId(primaryId);
                item.setLastActivity(lastActivity);

                item.setImage(Constants.BACKEND_SERVER_URL_IMAGES + post.optString("employee_number") + ".jpg?thumb=200x150");

                // adding data to GridCollector

                mGridData.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public static JSONArray sortJsonArray(JSONArray array) throws JSONException {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            jsons.add(array.getJSONObject(i));
        }
        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                String lid = null;
                try {
                    lid = lhs.getString("first_name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String rid = null;
                try {
                    rid = rhs.getString("first_name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Here you could parse string id to integer and then compare.
                return lid.compareTo(rid);
            }
        });
        return new JSONArray(jsons);
    }


    public void validatePIN(View v) {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View dialogView = li.inflate(R.layout.dialog_request_pin, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setView(dialogView);

        final EditText editPin = (EditText) dialogView
                .findViewById(R.id.edit_pin);

        alertDialogBuilder
                .setTitle("Settings")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (editPin.getText().toString().equals("2367")) {
                                    openSettings();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void openSettings() {
        Intent i = new Intent(getActivity(), SettingsActivity.class);
        startActivity(i);
    }
}