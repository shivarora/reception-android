package com.merlinbusinesssoftware.merlinsignin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
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
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
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
public class StaffCameraActivity extends MyBaseActivity implements SurfaceHolder.Callback {
    private static final String TAG                            = "CameraActivity";
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final String IMAGE_DIRECTORY_NAME           = "Hello";
    private int cameraId                                       = 0;
    public DatabaseHandler db;
    private Camera         camera;
    static  Thread          t;
    private String         mURL;
    public  Uri            fileUri; // file url to store image/video

    // Let's keep track of the display rotation and orientation also:
    private int mDisplayRotation;
    private int mDisplayOrientation;
    CountDownTimer inactiveTimer;

    // Holds the Face Detection result:
    private Camera.Face[] mFaces;

    // The surface view for the camera data
    private SurfaceView mView;

    // Draw rectangles and other fancy stuff:
    private FaceOverlayView mFaceView;

    // Log all errors:
    private final CameraErrorCallback mErrorCallback = new CameraErrorCallback();
    public int numberOfTriesLeft                     = 3;
    public int numberOfTriesForCamera                = 99;
    private String userName;

    public ProgressDialog progressDialog;

    private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
    private StructLog StructLog = new StructLog();

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

        inactiveTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Main();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

       // StructPending pending = new StructPending();
       // pending.reset();
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

                                finish();
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
        setDisplayOrientation();

        camera.startPreview();
    }

    private void setDisplayOrientation() {
        // Now set the display orientation:
        mDisplayRotation = Util.getDisplayRotation(StaffCameraActivity.this);
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
                outStream.write(data);
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
        progressDialog = ProgressDialog.show(StaffCameraActivity.this, "", "Processing...Please Wait", true);


        ContentValues values = new ContentValues();

        values.put("staff_id", pending.getStaffId());
        values.put("image_path", pending.getImagePath());
        values.put("local_image_path", pending.getLocalImagePath());

        System.out.println("now calling sendpostrequest ========================================");

        new Handler().postDelayed(new Runnable() {

            //((Activity)context).runOnUiThread(new Runnable() {

            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            //@Override
            public void run() {

                while (numberOfTriesLeft >0) {

                    if (mWifi.isConnected()) {
                        System.out.println("Going to process the request");
                        // Do whatever
                        sendPostRequest(pending.getStaffId(), pending.getImagePath(),pending.getLocalImagePath());
                        break;
                    }else{

                        numberOfTriesLeft = numberOfTriesLeft - 1;
                        System.out.println("Try Number" + numberOfTriesLeft);


                        try {
                            Thread.sleep(1000);
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


    private void sendPostRequest(String staff_id, String image_path, String local_image_path  ) {

        System.out.println("here is  lcoalimage path" + local_image_path);

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {


            private final ProgressDialog dialog = new ProgressDialog(StaffCameraActivity.this);
            @Override
            protected String doInBackground(String... params) {

                String paramStaffId = params[0];
                String paramImagePath = params[1];
                String paramLocalImagePath = params[2];


                System.out.println("*** doInBackground **");

                HttpClient httpClient = ExSSLSocketFactory.getHttpsClient(new DefaultHttpClient());
                //DefaultHttpClient httpClient = new DefaultHttpClient();

                // In a POST request, we don't pass the values in the URL.
                //Therefore we use only the web page URL as the parameter of the HttpPost argument

                HttpPost httpPost = new HttpPost(Constants.BACKEND_STAFF_IMAGE);

                BasicNameValuePair staffidBasicNameValuePair = new BasicNameValuePair("paramStaffId", paramStaffId);
                BasicNameValuePair imagepathBasicNameValuePAir = new BasicNameValuePair("paramImagePath", paramImagePath);
                BasicNameValuePair localimagepathBasicNameValuePAir = new BasicNameValuePair("paramLocalImagePath", paramLocalImagePath);

                // We add the content that we want to pass with the POST request to as name-value pairs
                //Now we put those sending details to an ArrayList with type safe of NameValuePair
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(staffidBasicNameValuePair);
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

                System.out.println("her comes the result" + result);
                //this.dialog.cancel();
                progressDialog.dismiss();

                if(result != null){
                    String json= result ;
                    Map jsonJavaRootObject = new Gson().fromJson(json, Map.class);

                    if (jsonJavaRootObject.get("message").equals("Completed")) {


                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else {
                        // setting up message for toast
                        String message =  "Sorry !There is some problem while submitting your picture...Please contact to the administrator!";
                        String set_image = "error_toast";
                        for (int i=0; i < 1; i++) {
                            toast(message, set_image);
                        }
                    }
                }else{
                    String message =  "Sorry !There is some problem while submitting your picture...Please contact to the administrator!";
                    String set_image = "error_toast";
                    for (int i=0; i < 1; i++) {
                        toast(message, set_image);
                    }

                }


                setResult(RESULT_OK, null);
                finish();

            }


        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(staff_id, image_path, local_image_path);
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
        toast.setDuration(Toast.LENGTH_LONG);
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

    private void Main() {

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}