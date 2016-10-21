package com.merlinbusinesssoftware.merlinsignin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.merlinbusinesssoftware.merlinsignin.structures.StructPending;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DetailsActivity extends MyBaseActivity implements View.OnClickListener {
    private static final int ANIM_DURATION = 600;
    public PicassoTrustAll  picassoTrustAll;
    private TextView        titleTextView;
    private ImageView       imageView;
    private String          staffId;
    private String          title;
    private String          staffImage;
    private String          status;
    private String          signinTime;
    private String          signoutTime;
    private String          ToastMessage;
    private String          lastActivity;
    private int             primaryId;
    private int             department_code;
    private Context         mContext;
    Button                  btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting details screen layout
        setContentView(R.layout.staff_sign_in);

        //retrieves the thumbnail data
        Bundle bundle = getIntent().getExtras();

        title           = bundle.getString("title");
        staffImage      = bundle.getString("image");
        staffId         = bundle.getString("staffId");
        department_code = bundle.getInt("department_code");
        status          = bundle.getString("status");
        signinTime      = bundle.getString("signinTime");
        signoutTime     = bundle.getString("signoutTime");
        primaryId       = bundle.getInt("primaryId");
        lastActivity    = bundle.getString("lastActivity");



        StructPending pending = new StructPending();
        pending.setTitle(title);
        pending.setStaffImagePath(staffImage);
        pending.setStaffId(staffId);
        pending.setDepartmentCode(department_code);
        pending.setStatus(status);
        pending.setSigninTime(signinTime);
        pending.setSignoutTime(signoutTime);
        pending.setPrimaryId(primaryId);
        pending.setLastActivity(lastActivity);


        //initialize and set the image description
        titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(Html.fromHtml(title));

        if(lastActivity.trim().equals("Signed In")){
            TextView lastSignedVal = (TextView) findViewById(R.id.lastSignedVal);
            lastSignedVal.setText( lastActivity + " " + changeDateFormat(signinTime));

            TextView statusTextView = (TextView) findViewById(R.id.status);
            statusTextView.setTextColor(Color.GREEN);
            statusTextView.setText(status);

        }else if(lastActivity.trim().equals("Signed Out")){
            TextView lastSignedVal = (TextView) findViewById(R.id.lastSignedVal);
            lastSignedVal.setText(lastActivity + " " +changeDateFormat(signoutTime));

            TextView statusTextView = (TextView) findViewById(R.id.status);
            statusTextView.setText(status);
        }else  {

            TextView lastSignedVal = (TextView) findViewById(R.id.lastSignedVal);
            lastSignedVal.setText(lastActivity);

            TextView statusTextView = (TextView) findViewById(R.id.status);
            statusTextView.setText(status);

        }


        if( !signinTime.equals("")){
            if (signoutTime.trim().equals("")){

                TextView signoutText = (TextView) findViewById(R.id.reversetext);
                signoutText.setText("If you are outside the building please click ");

                //setting up button here
                Button signInBtn = (Button) findViewById(R.id.staffSign);

                signInBtn.setText("Sign Out");
                signInBtn.setBackgroundResource(R.drawable.staff_sign_out);
                signInBtn.setTextColor(Color.parseColor("#FFFFFF"));
                signInBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                    System.out.println("After Sign out button");

                    new AsyncHttpTask().execute(Constants.BACKEND_SERVER_URL_STAFF_SIGN_OUT);
                    }
                });

                Button signOutBtn = (Button) findViewById(R.id.staffSignOut);

                signOutBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        System.out.println("After Sign In button");

                        new AsyncHttpTask().execute(Constants.BACKEND_SERVER_URL_STAFF_SIGN_IN);
                    }
                });
            }

        }

        //Set image url
        imageView = (ImageView) findViewById(R.id.grid_item_image);
        picassoTrustAll.getInstance(getApplicationContext())
                .load(staffImage)
                .transform(new RoundTransformation(50, 4))
                .error(getApplicationContext().getResources().getDrawable(R.drawable.new_image))
                .memoryPolicy(MemoryPolicy.NO_CACHE )
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(imageView);

        btnSubmit = (Button) findViewById(R.id.takePicture);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                System.out.println("After pressing Picture button");

                StaffImage();
            }
        });

        //close keyboard while click somewhere else on screen

        ImageView img = (ImageView) findViewById(R.id.back_StaffList);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onDestroy();
            }
        });

    }

    public String changeDateFormat(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MM-yyyy h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    private void StaffImage() {
        Intent i = new Intent(this, StaffCameraActivity.class);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){

            System.out.println("Activity goin got refresh here");

            //Start pending structure to get the data from
            StructPending pending = new StructPending();

            Intent refresh = new Intent(this, DetailsActivity.class);

            ImageView imageView = (ImageView) findViewById(R.id.grid_item_image);

            int[] screenLocation = new int[2];
            imageView.getLocationOnScreen(screenLocation);

            //Pass the image title and url to DetailsActivity
            refresh.putExtra("title", pending.getTitle()).
                    putExtra("staffId", pending.getStaffId()).
                    putExtra("department_code", pending.getDepartmentCode()).
                    putExtra("status", pending.getStatus()).
                    putExtra("signinTime", pending.getSigninTime()).
                    putExtra("signoutTime", pending.getSignoutTime()).
                    putExtra("primaryId", pending.getPrimaryId()).
                    putExtra("image", pending.getStaffImagePath()).
                    putExtra("lastActivity", pending.getLastActivity());


            startActivity(refresh);
            this.finish();
        }
    }


    @Override
    public void onBackPressed() {
        finish();
    }


    public void staffSignIn (View v){
        new AsyncHttpTask().execute(Constants.BACKEND_SERVER_URL_STAFF_SIGN_IN);
    }

    public void staffSignOut (View v){
        new AsyncHttpTask().execute(Constants.BACKEND_SERVER_URL_STAFF_SIGN_OUT);
    }

    //Downloading data asynchronously
    public class AsyncHttpTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Integer result = 0;
            try {

                // Create Apache HttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                //HttpClient httpClient = ExSSLSocketFactory.getHttpsClient(new DefaultHttpClient());
                HttpResponse httpResponse = httpClient.execute(new HttpGet(params[0] + staffId ));
                int statusCode = httpResponse.getStatusLine().getStatusCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    //setting toast message here

                    if(params[0] == Constants.BACKEND_SERVER_URL_STAFF_SIGN_IN ){
                        ToastMessage = title + " is successfully signed In.";
                    }else if(params[0] == Constants.BACKEND_SERVER_URL_STAFF_SIGN_OUT) {
                        ToastMessage = title + " is successfully signed Out.";
                    }

                    System.out.println("successful response");
                    String response = streamToString(httpResponse.getEntity().getContent());

                    return response.toString();
                }

            } catch (Exception e) {
                System.out.println("its an exception");
                System.out.println(e.getLocalizedMessage());
            }

            return null;
        }



        @Override
        protected void onPostExecute(String result) {
            // Download complete. Lets update UI

            if (result != null ) {
                String json= result ;
                Map jsonJavaRootObject = new Gson().fromJson(json, Map.class);

                if (jsonJavaRootObject.get("message").equals("completed")) {

                    String set_image = "success_toast";
                    for (int i=0; i < 1; i++) {
                        toast(ToastMessage, set_image);
                    }
                }else
                {
                    ToastMessage = (String) jsonJavaRootObject.get("data");

                    String set_image = "error_toast";
                    for (int i=0; i < 2; i++) {
                        toast(ToastMessage, set_image);
                    }
                }
            } else {

                System.out.println("failed to fetch data");

                String set_image = "error_toast";
                for (int i=0; i < 2; i++) {
                    toast(ToastMessage, set_image);
                }
                //Toast.makeText(GridViewActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }

            onDestroy();

            Main();
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
    }


    public void  toast( String message, String set_image){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        ImageView image = (ImageView) layout.findViewById(R.id.image);
        if(set_image == "error_toast"){
            image.setImageResource(R.drawable.error_toast);
        }else{
            image.setImageResource(R.drawable.success_toast);
        }

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, -60);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onClick(View view) {

    }

}
