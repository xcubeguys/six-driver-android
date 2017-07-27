package com.tommy.driver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.tommy.driver.adapter.Constants;
import com.tommy.driver.adapter.FontChangeCrawler;
import com.tommy.driver.adapter.RoundImageTransform;
import com.tommy.driver.utils.LogUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@EActivity(R.layout.activity_image_upload)
public class ImageUpload extends MyBaseActivity {

    private static final int CAMERA_REQUEST = 1;
    Uri mCapturedImageURI;
    String picturePath, profImage;

    @ViewById(R.id.txtArcane)
    ImageView profImg;

    @ViewById(R.id.btnTakePhoto)
    Button next;

    String strEmail, strFirstName, strLastName, strPassword, strConfirmPassword, strCity, strMobile, strCountyCode, strProfileImage, strnickname, strNumOfPassenger, strVehiclemake, strVehiclemodel, strVehicleyear, strVehiclemileage;
    String strreferral;

    @AfterViews
    void signUpImage() {
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        Intent i = getIntent();
        strFirstName = i.getStringExtra("FirstName");
        strLastName = i.getStringExtra("LastName");
        strPassword = i.getStringExtra("Password");
        strCity = i.getStringExtra("City");
        strMobile = i.getStringExtra("Mobile");
        strCountyCode = i.getStringExtra("CountryCode");
        strEmail = i.getStringExtra("Email");
        strnickname = i.getStringExtra("nick_name");
//        strNumOfPassenger=i.getStringExtra("num_of_passenger");
//        LogUtils.i("Number of passanger in ImageUpload"+strNumOfPassenger);

        profImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder =
                        new android.support.v7.app.AlertDialog.Builder(ImageUpload.this, R.style.AppCompatAlertDialogStyle);
                builder.setMessage(getString(R.string.option));

                builder.setNegativeButton(getString(R.string.camera), new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ImageUpload.this.checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                start_camera();
                            } else {
                                dialog.cancel();
                                ImageUpload.this.requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 5);
                            }
                        } else {
                            start_camera();
                        }
                    }
                });

                builder.setNeutralButton(getString(R.string.gallery), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent i = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, 100);
                    }
                });

                builder.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The Permission is granted to you... Continue your left job...
                start_camera();
            } else {
                Toast.makeText(ImageUpload.this, "Please provide permission to use camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void start_camera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image File name");
        mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Click({R.id.btnTakePhoto})
    void signinPage() {
        if (profImg.getDrawable() != null) {

            LogUtils.i("GOING TO DOCUMENT UPLOAD" + "FNAME==" + strFirstName + " " + "LNAME==>" + strLastName + " " + "MOBILE==" + strMobile + " " + "PASSWORD==" + strPassword + " " + "CITY==" + strCity + " " + "COUNTRY CODE==" + strCountyCode);

            //if(!profImage.isEmpty()&&!profImage.matches("")){
            Intent signin = new Intent(this, DocUpload_Activity_.class);
            signin.putExtra("profImage", profImage);
            signin.putExtra("FirstName", strFirstName);
            signin.putExtra("LastName", strLastName);
            signin.putExtra("Email", strEmail);
            signin.putExtra("Password", strPassword);
            signin.putExtra("Mobile", strMobile);
            signin.putExtra("City", strCity);
            signin.putExtra("CountryCode", strCountyCode);
            signin.putExtra("ProfilePicture", strProfileImage);
            signin.putExtra("nick_name", strnickname);
            signin.putExtra("referral", strreferral);

//            signin.putExtra("num_of_passenger",strNumOfPassenger);
            startActivity(signin);

            LogUtils.i("IMAGE UPLOAD PAGE" + "FNAME==" + strFirstName + " " + "LNAME==>" + strLastName + " " + "MOBILE==" + strMobile + " " + "PASSWORD==" + strPassword + " " + "CITY==" + strCity + " " + "COUNTRY CODE==" + strCountyCode + "IMAGE PROFILEE==" + strProfileImage);
           /* }
          else{
                Toast.makeText(ImageUpload.this, R.string.attachPhoto, Toast.LENGTH_SHORT).show();
            }


        }*/
        } else {
            android.support.v7.app.AlertDialog.Builder builder =
                    new android.support.v7.app.AlertDialog.Builder(ImageUpload.this, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(getString(R.string.option));

            builder.setNegativeButton(getString(R.string.camera), new DialogInterface.OnClickListener() {


                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ImageUpload.this.checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            start_camera();
                        } else {
                            dialog.cancel();
                            ImageUpload.this.requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 5);
                        }
                    } else {
                        start_camera();
                    }
                }
            });

            builder.setNeutralButton(getString(R.string.gallery), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 100);
                }
            });

            builder.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }

    @Click({R.id.back})
    void back() {
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            picturePath = getRealPathFromURI(mCapturedImageURI);
            LogUtils.i("CAMERA IMAGE" + picturePath);
            profImg.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with(ImageUpload.this)
                    .load(picturePath)
                    .skipMemoryCache(true)
                    .transform(new RoundImageTransform(ImageUpload.this))
                    .into(new GlideDrawableImageViewTarget(profImg) {
                        @Override
                        public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                            super.onResourceReady(drawable, anim);
                        }
                    });


//            profImg.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            if (Glide.isSetup()) {
                next.setText("Next");
            } else {
                next.setText(getResources().getString(R.string.take_photo));
            }
            new ImageuploadTask(this).execute();
        }

        LogUtils.i("Request Code+requestCode" + "Result Code" + resultCode + "data" + data);

        if (requestCode == 100 && resultCode == Activity.RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            if (Build.VERSION.SDK_INT >= 19) {
                if (selectedImage != null && !selectedImage.toString().equals("null")) {
                    LogUtils.i("greater 19:" + "kitkat");
                    picturePath = getImagePath(selectedImage);
                    LogUtils.i("mSelectedFissslssePath res" + picturePath);

                } else {
                    LogUtils.i("greater 19:" + "not kitkat");
                    picturePath = getPathOfImage(selectedImage);
                    LogUtils.i("mSelectedFissslePath res" + picturePath);
                }
            }


            /*if (selectedImage != null && !selectedImage.toString().equals("null")) {
                picturePath = getRealPathFromURI(selectedImage);
            } else {
                picturePath = "";
            }*/

            // Set the Image in ImageView after decoding the String
            profImg.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ImageUpload.this)
                    .load(picturePath)
                    .skipMemoryCache(true)
                    .transform(new RoundImageTransform(ImageUpload.this))
                    .into(new GlideDrawableImageViewTarget(profImg) {
                        @Override
                        public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                            super.onResourceReady(drawable, anim);
                        }
                    });

            if (Glide.isSetup()) {
                next.setText("Next");
            } else {
                next.setText(getResources().getString(R.string.take_photo));
            }
            new ImageuploadTask(this).execute();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mCapturedImageURI == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Image File name");
            mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    // Save the activity state when it's going to stop.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("picUri", mCapturedImageURI);
    }

    // Recover the saved state when the activity is recreated.
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mCapturedImageURI = savedInstanceState.getParcelable("picUri");
    }

    public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return contentUri.getPath();
        }
    }

    protected void Upload_Server() {
        LogUtils.i("After call progress");
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        FileInputStream fileInputStream = null;
        DataInputStream inputStream1 = null;
        try {

            String pathToOurFile = picturePath;
            //	  String pathToOurFile1 = imagepathcam;

            LogUtils.i("Before Image Upload" + picturePath);

            String urlServer = Constants.LIVEURL + "imageUpload/";
            LogUtils.i("URL SETVER" + urlServer);
            LogUtils.i("After Image Upload" + picturePath);
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024 * 1024;

            fileInputStream = new FileInputStream(new File(pathToOurFile));
            //  FileInputStream fileInputStream1 = new FileInputStream(new File(pathToOurFile1));

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();
            LogUtils.i("URL is " + url);
            LogUtils.i("connection is " + connection);
            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setChunkedStreamingMode(1024);
            // Enable POST method
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            String serverResponseMessage = connection.getResponseMessage();

            LogUtils.i("image" + serverResponseMessage);

            inputStream1 = new DataInputStream(connection.getInputStream());
            String str;
            String Str1_imageurl = "";

            while ((str = inputStream1.readLine()) != null) {
                LogUtils.e("Debug " + "Server Response " + str);

                Str1_imageurl = str;
                LogUtils.e("Debug " + "Server Response String imageurl" + str);
            }
            LogUtils.i("image url" + Str1_imageurl);

            //get the image url and store
            profImage = Str1_imageurl.trim();
            JSONArray array = new JSONArray(profImage);
            JSONObject jsonObj = array.getJSONObject(0);
            LogUtils.i("image name" + jsonObj.getString("image_name"));

            strProfileImage = jsonObj.optString("image_name");

            LogUtils.i("Profile Picture Path" + profImage);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream1 != null) {
                try {
                    inputStream1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getImagePath(Uri uri) {
        String path = "";
        try {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            LogUtils.i("path111:" + path);
            cursor.close();
        } catch (Exception e) {

        }
        return path;
    }

    private String getPathOfImage(Uri uri) {
        String wholeID = DocumentsContract.getDocumentId(uri);

        LogUtils.i("WholeId:" + wholeID);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        LogUtils.i("File Path1:" + filePath);
        cursor.close();
        return filePath;
    }

    private class ImageuploadTask extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog dialog;
        private ImageUpload activity;
        private Context context;

        ImageuploadTask(ImageUpload activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
        }

        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Uploading...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            if (!isFinishing()) {
                dialog.show();
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (dialog != null && dialog.isShowing()) {
                if (!activity.isFinishing() && !activity.isDestroyed()) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected Boolean doInBackground(final String... args) {
            try {
                // ... processing ...
                Upload_Server();
                return true;
            } catch (Exception e) {
                LogUtils.e("Schedule " + "UpdateSchedule failed - " + e);
                return false;
            }
        }
    }
}