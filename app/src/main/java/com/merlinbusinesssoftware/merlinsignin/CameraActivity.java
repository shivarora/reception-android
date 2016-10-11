package com.merlinbusinesssoftware.merlinsignin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.merlinbusinesssoftware.merlinsignin.structures.StructLog;
import com.merlinbusinesssoftware.merlinsignin.structures.StructPending;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Listing 15-26: Previewing a real-time camera stream
 */
public class CameraActivity extends MyBaseActivity implements SurfaceHolder.Callback {
    public DatabaseHandler db;
    CountDownTimer inactiveTimer;
    private static final String TAG = "CameraActivity";
    private Camera camera;
    private int cameraId = 0;
    static Thread t;
    private String mURL;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final String IMAGE_DIRECTORY_NAME = "Hello";
    public Uri fileUri; // file url to store image/video

    private int mOrientation;
    private int mOrientationCompensation;
    private OrientationEventListener mOrientationEventListener;

    // Let's keep track of the display rotation and orientation also:
    private int mDisplayRotation;
    private int mDisplayOrientation;

    // Holds the Face Detection result:
    private Camera.Face[] mFaces;

    // The surface view for the camera data
    private SurfaceView mView;

    // Draw rectangles and other fancy stuff:
    private FaceOverlayView mFaceView;

    // Log all errors:
    private final CameraErrorCallback mErrorCallback = new CameraErrorCallback();

    public int numberOfTriesLeft = 3;
    public int numberOfTriesForCamera = 99;
    private String userName;

    public ProgressDialog progressDialog;

    private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
    private StructLog StructLog = new StructLog();
    private StructPending pending = new StructPending();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

        db = new DatabaseHandler(this);

        SurfaceView surface = (SurfaceView)findViewById(R.id.surfaceView);
        final SurfaceHolder holder = surface.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.setFixedSize(200, 100);

        Button snap = (Button)findViewById(R.id.buttonTakePicture);

        snap.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                System.out.println("After pressing Picture button");

                takePicture();

            }
        });

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {

        }else{
            showD();
        }

    }

    @Override
    public void onBackPressed() {

        System.out.println("inside backpressed");
        //moveTaskToBack(true);
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.dialog_revert, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(dialogView);

        alertDialogBuilder
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                onDestroy();
                                // if user press back button, entry will be deleted from log
                                db.deleteLog(StructLog.getId());

                                Main();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.setDisplayOrientation(90);
            camera.startPreview();
            // TODO Draw over the preview if required.
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();

    }

    public void surfaceChanged(SurfaceHolder holder, int format,
                               int width, int height) {
        if (holder.getSurface() == null) {
            return;
        }
        // Try to stop the current preview:
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // Ignore...
        }

        // configureCamera(width, height);
        setDisplayOrientation();

        camera.startPreview();
    }

    private void setDisplayOrientation() {
        // Now set the display orientation:
        mDisplayRotation = Util.getDisplayRotation(CameraActivity.this);
        mDisplayOrientation = Util.getDisplayOrientation(mDisplayRotation, 0);

        camera.setDisplayOrientation(mDisplayOrientation);

        if (mFaceView != null) {
            mFaceView.setDisplayOrientation(mDisplayOrientation);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.stopPreview();
    }

    @Override
    public void onResume() {

        super.onResume();

        while(numberOfTriesForCamera >0){

            try{
                cameraId = findFrontFacingCamera();
                camera = Camera.open(cameraId);
                break;
            }catch(Exception e){

                numberOfTriesForCamera = numberOfTriesForCamera -1 ;
                if(numberOfTriesForCamera == 0){
                    for (int i=0; i < 2; i++) {
                        Toast.makeText(getApplicationContext(), "There is Problem with  Camera.Please try again..", Toast.LENGTH_LONG).show();
                    }

                    Main();
                }

            }

        }
    }

    /**
     * Listing 15-27: Taking a picture
     */
    private void takePicture() {
        // Intent intent = new Intent(this, CameraActivity.class);
        System.out.println("Inside take picture");
        camera.takePicture(shutterCallback, rawCallback, postviewCallback, jpegCallback);

    }
    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "inside result");

        System.out.println(requestCode);
        System.out.println(resultCode);


        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                //previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            // TODO Do something when the shutter closes.
        }
    };

    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Do something with the image RAW data.
        }
    };

    PictureCallback postviewCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Do something with the image RAW data.
        }
    };

    PictureCallback jpegCallback = new PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            bitmap =
                    ThumbnailUtils.extractThumbnail(bitmap, 200, 150);
            // NOTE that's an incredibly useful trick for cropping/resizing squares

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
            byte[] yourByteArray;
            yourByteArray = baos.toByteArray();


            System.out.println("Inside jpeg");
            // Save the image JPEG data to the SD card
            FileOutputStream outStream = null;
            try {
                System.out.println("Inside try block");
                File mediaStorageDir = new File(
                        Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        IMAGE_DIRECTORY_NAME);

                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                                + IMAGE_DIRECTORY_NAME + " directory");
                    }
                }
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
                        File.separator + IMAGE_DIRECTORY_NAME + File.separator + "IMG_" + timeStamp +".jpg" ;

                outStream = new FileOutputStream(path);
                outStream.write(yourByteArray);
                outStream.close();

                File file = new File(path);
                FileInputStream imageInFile = new FileInputStream(file);
                byte imageData[] = new byte[(int) file.length()];
                imageInFile.read(imageData);

                String imageDataString =   encodeImage(imageData);

                System.out.println("Now it will process");

                // Now Resume the Camera Here
                camera.startPreview();

                StructPending pending = new StructPending();
                pending.setImagePath(imageDataString);
                pending.setLocalImagePath(path);

                System.out.println("now calling insertpending");

                insertPending(pending);


            } catch (FileNotFoundException e) {
                Log.e(TAG, "File Note Found", e);
            } catch (IOException e) {
                Log.e(TAG, "IO Exception", e);
            }

        }
    };

    public static String encodeImage(byte[] imageByteArray) {
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT);
    }



    public void showD() {
        //moveTaskToBack(true);
        numberOfTriesLeft =3;
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.dialog_wifi, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS), 0);
                                //startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                // Main();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();

                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void insertPending(final StructPending pending) {
        progressDialog = ProgressDialog.show(CameraActivity.this, "", "Processing...Please Wait", true);

        if(pending.getAccountName().equals("")){
            for (int i=0; i < 2; i++) {
                Toast.makeText(getApplicationContext(), "Sorry !!!You are not logged in. Cromwell Reception software unable to use WiFi. Please ring Helpdesk", Toast.LENGTH_LONG).show();
            }

            Main();
        }

        ContentValues values = new ContentValues();

        userName = pending.getContactName();

        values.put("type", pending.getType());
        values.put("accountid", pending.getAccountId());
        values.put("account_name", pending.getAccountName());
        values.put("contactid", pending.getContactId());
        values.put("contact_name", pending.getContactName());
        values.put("employeeid", pending.getEmployeeId());
        values.put("employee_name", pending.getEmployeeName());
        values.put("vehicle_reg", pending.getVehicleReg());
        values.put("time", pending.getTime());
        values.put("reception_log_id", pending.getReceptionLogId());
        values.put("log_id", pending.getLogId());
        values.put("pending_id", pending.getPendingId());
        values.put("image_path", pending.getImagePath());
        values.put("local_image_path", pending.getLocalImagePath());

        new Handler().postDelayed(new Runnable() {

            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            //@Override
            public void run() {

                while (numberOfTriesLeft >0) {

                    if (mWifi.isConnected()) {
                        System.out.println("Going to process the request");
                        // Do whatever
                        sendPostRequest(pending.getType(), pending.getAccountId(), pending.getAccountName(), pending.getContactId(), pending.getContactName(), pending.getEmployeeId(),
                                pending.getEmployeeName(), pending.getVehicleReg(), pending.getTime(), pending.getReceptionLogId(), pending.getLogId(), pending.getPendingId(), pending.getImagePath(),
                                pending.getLocalImagePath());
                        break;
                    }else{

                        numberOfTriesLeft = numberOfTriesLeft - 1;
                        System.out.println("Try Number" + numberOfTriesLeft);


                        try {
                            Thread.sleep(3000);
                            System.out.println("Try Number" + numberOfTriesLeft);

                            if(numberOfTriesLeft == 0){

                                progressDialog.dismiss();

                                showD();
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Main();
                        }

                    }
                }
            }
        }, 3000);

    }


    private void sendPostRequest(String type, int accountid, String account_name, int contactid, String contact_name, int employeeid,
                                 String employee_name, String vehicle_reg, String time, int reception_log_id, int log_id, int pending_id,
                                 String image_path, String local_image_path  ) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {


            private final ProgressDialog dialog = new ProgressDialog(CameraActivity.this);
            @Override
            protected String doInBackground(String... params) {

                String paramType = params[0];
                String paramAccountId = params[1];
                String paramAccountName = params[2];
                String paramContactId = params[3];
                String paramContactName = params[4];
                String paramEmployeeId = params[5];
                String paramEmployeeName = params[6];
                String paramVehicleReg = params[7];
                String paramTime = params[8];
                String paramRecLogId = params[9];
                String paramLogId = params[10];
                String paramPendigId = params[11];
                String paramImagePath = params[12];
                String paramLocalImagePath = params[13];


                System.out.println("*** doInBackground ** paramAccountName " + paramAccountName + " paramContactName :" + paramContactName);

                 DefaultHttpClient httpClient = new DefaultHttpClient();

                //HttpClient httpClient = ExSSLSocketFactory.getHttpsClient(new DefaultHttpClient());

                System.out.println(httpClient.getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY));

                // In a POST request, we don't pass the values in the URL.
                //Therefore we use only the web page URL as the parameter of the HttpPost argument

                HttpPost httpPost = new HttpPost( Constants.BACKEND_SERVER_URL_VISITORS);


                // Because we are not passing values over the URL, we should have a mechanism to pass the values that can be
                //uniquely separate by the other end.
                //To achieve that we use BasicNameValuePair
                //Things we need to pass with the POST request
                BasicNameValuePair typeBasicNameValuePair = new BasicNameValuePair("paramType", paramType);
                BasicNameValuePair accountidBasicNameValuePAir = new BasicNameValuePair("paramAccountId", paramAccountId);
                BasicNameValuePair accountnameBasicNameValuePair = new BasicNameValuePair("paramAccountName", paramAccountName);
                BasicNameValuePair contactidBasicNameValuePAir = new BasicNameValuePair("paramContactId", paramContactId);
                BasicNameValuePair contactnameBasicNameValuePair = new BasicNameValuePair("paramContactName", paramContactName);
                BasicNameValuePair employeeidBasicNameValuePAir = new BasicNameValuePair("paramEmployeeId", paramEmployeeId);
                BasicNameValuePair employeenameBasicNameValuePair = new BasicNameValuePair("paramEmployeeName", paramEmployeeName);
                BasicNameValuePair vehicleregBasicNameValuePAir = new BasicNameValuePair("paramvehicleReg", paramVehicleReg);
                BasicNameValuePair timeBasicNameValuePair = new BasicNameValuePair("paramTime", paramTime);
                BasicNameValuePair reclogidBasicNameValuePAir = new BasicNameValuePair("paramRecLogId", paramRecLogId);
                BasicNameValuePair logidBasicNameValuePair = new BasicNameValuePair("paramLogId", paramLogId);
                BasicNameValuePair pendingidBasicNameValuePair = new BasicNameValuePair("paramPendigId", paramPendigId);
                BasicNameValuePair imagepathBasicNameValuePAir = new BasicNameValuePair("paramImagePath", paramImagePath);
                BasicNameValuePair localimagepathBasicNameValuePAir = new BasicNameValuePair("paramLocalImagePath", paramLocalImagePath);

                // We add the content that we want to pass with the POST request to as name-value pairs
                //Now we put those sending details to an ArrayList with type safe of NameValuePair
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(typeBasicNameValuePair);
                nameValuePairList.add(accountidBasicNameValuePAir);
                nameValuePairList.add(accountnameBasicNameValuePair);
                nameValuePairList.add(contactidBasicNameValuePAir);
                nameValuePairList.add(contactnameBasicNameValuePair);
                nameValuePairList.add(employeeidBasicNameValuePAir);
                nameValuePairList.add(employeenameBasicNameValuePair);
                nameValuePairList.add(vehicleregBasicNameValuePAir);
                nameValuePairList.add(timeBasicNameValuePair);
                nameValuePairList.add(reclogidBasicNameValuePAir);
                nameValuePairList.add(logidBasicNameValuePair);
                nameValuePairList.add(pendingidBasicNameValuePair);
                nameValuePairList.add(imagepathBasicNameValuePAir);
                nameValuePairList.add(localimagepathBasicNameValuePAir);

                try {
                    // UrlEncodedFormEntity is an entity composed of a list of url-encoded pairs.
                    //This is typically useful while sending an HTTP POST request.
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

                    // setEntity() hands the entity (here it is urlEncodedFormEntity) to the request.
                    httpPost.setEntity(urlEncodedFormEntity);

                    try {
                        // HttpResponse is an interface just like HttpPost.
                        //Therefore we can't initialize them
                        HttpResponse httpResponse = httpClient.execute(httpPost);


                        // According to the JAVA API, InputStream constructor do nothing.
                        //So we can't initialize InputStream although it is not an interface
                        InputStream inputStream = httpResponse.getEntity().getContent();

                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        StringBuilder stringBuilder = new StringBuilder();

                        String bufferedStrChunk = null;

                        while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                            stringBuilder.append(bufferedStrChunk);
                        }

                        return stringBuilder.toString();

                    } catch (ClientProtocolException cpe) {
                        System.out.println("First Exception caz of HttpResponese :" + cpe);
                        cpe.printStackTrace();
                    } catch (IOException ioe) {
                        System.out.println("Second Exception caz of HttpResponse :" + ioe);
                        ioe.printStackTrace();

                    }

                } catch (UnsupportedEncodingException uee) {
                    System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
                    uee.printStackTrace();
                }

                return null;
            }

            protected  void onPreExecute(){
                // this.dialog.setMessage("Processing...Please Wait");
                // this.dialog.show();

            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                System.out.println(result);
                //this.dialog.cancel();
                progressDialog.dismiss();

                if(result != null){
                    String json= result ;
                    Map jsonJavaRootObject = new Gson().fromJson(json, Map.class);

                    if (jsonJavaRootObject.get("message").equals("completed")) {

                        StructLog log = new StructLog();

                        StructPending pending = new StructPending();

                        System.out.println("this is pending image after post " + pending.getLocalImagePath());


                        Double d = (Double) jsonJavaRootObject.get("id");
                        Integer vid = d.intValue();

                        System.out.println("int here " + vid);

                        db.updateLogVisitorId(pending.getLogId(), pending.getLocalImagePath(),vid);

                        //reseting pending data here

                        pending.reset();
                        //deleting all cached data after successful sign in.
                        onDestroy();

                        // setting up message for toast
                        String message =  userName + " is  succesfully signed in.  Please wait for your badge to print. ";
                        String set_image = "success_toast";
                        for (int i=0; i < 1; i++) {
                            toast(message, set_image);
                        }

                    } else {

                        // if user is not able to login because of node app is down, then delete user data from tab also, so it didn't show in signout.
                        db.deleteLog(StructLog.getId());

                        // setting up message for toast
                        String message =  "Sorry ! " + userName + " is not logged in. Try Again!!...";
                        String set_image = "error_toast";
                        for (int i=0; i < 2; i++) {
                            toast(message, set_image);
                        }
                    }
                }else{

                    // if user is not able to login because of node app is down, then delete user data from tab also, so it didn't show in signout.
                    db.deleteLog(StructLog.getId());

                    String message =  "Sorry " + userName + " ! There is some problem signing you in...Please contact to the administrator!";
                    String set_image = "error_toast";
                    for (int i=0; i < 2; i++) {
                        toast(message, set_image);
                    }

                }

                Main();

            }


        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(type, Integer.toString(accountid) , account_name,Integer.toString(contactid),contact_name,Integer.toString(employeeid), employee_name, vehicle_reg, time, Integer.toString(reception_log_id), Integer.toString(log_id),Integer.toString(pending_id), image_path, local_image_path);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    /*
     * Here we restore the fileUri again
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        Log.d(TAG, "befeor foor loop camera");
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            Log.d(TAG, "going to find camera");
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }


}