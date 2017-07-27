package com.tommy.driver;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.tommy.driver.adapter.AppController;
import com.tommy.driver.adapter.Constants;
import com.tommy.driver.adapter.FontChangeCrawler;
import com.tommy.driver.adapter.RetrofitArrayAPI;
import com.tommy.driver.adapter.RiderInfo;
import com.tommy.driver.adapter.RoundImageTransform;
import com.tommy.driver.adapter.Services;
import com.tommy.driver.socialshare.SocialShare;
import com.tommy.driver.utils.LogUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@EActivity(R.layout.activity_setting)
public class SettingActivity extends AppCompatActivity {

    public String userID, firstName, lastName, nickName, email, mobileNumber, countryCode, referralcode, profileImage, status, message, strCategory, drivercarcategory, vehicleMake, vehicleModel, vehicleYear, vehicleMileage, vehiclenumberplate;
    String strSubjectCategory, strCarCategory;
    ProgressDialog progressDialog;
    Dialog socialShareDialog;
    MaterialSpinner spinSubjectCategory;
    GeoFire geoFire;
    SharedPreferences prefs;

    String[] subjectcategory;

    @ViewById(R.id.profileImage)
    ImageView edtProfileImage;

    @ViewById(R.id.backButton)
    ImageButton backButton;

    @ViewById(R.id.editButton)
    ImageButton editButton;

    @ViewById(R.id.editCancelButton)
    ImageButton editCancel;


    @ViewById(R.id.save_button)
    Button saveButton;

    @NotEmpty(message = "Enter First Name")
    @Length(max = 15)
    @ViewById(R.id.edtFirstName)
    EditText inputFirstName;

    @NotEmpty(message = "Enter Last Name")
    @Length(max = 15)
    @ViewById(R.id.edtLastName)
    EditText inputLastName;

    @NotEmpty(message = "Enter Last Name")
    @Length(max = 15)
    @ViewById(R.id.edtnickname)
    EditText inputNickName;

    @NotEmpty
    @ViewById(R.id.edtCountryCode)
    EditText inputCountryCode;

    @NotEmpty
    @ViewById(R.id.edtMobile)
    EditText inputMobileNumber;

    @ViewById(R.id.edtEmail)
    EditText inputEmail;

    @ViewById(R.id.edit_vehiclemake)
    EditText inputVehiclemake;

    @ViewById(R.id.edit_vehiclemodel)
    EditText inputVehiclemodel;

    @ViewById(R.id.edit_vehicleyear)
    EditText inputVehicleyear;

    @ViewById(R.id.edit_vehiclemileage)
    EditText inputVehiclemileage;

    @ViewById(R.id.carcategory)
    EditText carcategory;

    @ViewById(R.id.Referral_code)
    TextView Referral_code;

    @ViewById(R.id.edit_vehiclenumberplate)
    TextView editvehicleNumberplate;

    @ViewById(R.id.ccp)
    CountryCodePicker ccp;
    SocialShare socialShare;

    @Click(R.id.share)
    void share() {
        // Build view
        if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.WRITE_SMS") == PackageManager.PERMISSION_GRANTED) {

            final View view = socialShare.getDefaultShareUI();
            // Do something with the view, for example show in Dialog
            socialShareDialog = new Dialog(SettingActivity.this);
            socialShareDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            socialShareDialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            socialShareDialog.show();
      /*  Intent i=new Intent(SettingActivity.this,ShareReferralActivity.class);
       i.putExtra("referralcode",referralcode);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);*/
            //finish();
        }
    }

    @Click(R.id.feedback_button)
    public void feedback() {

        getSubjectCategory();

        final Dialog dialog = new Dialog(SettingActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_feedback);
        dialog.setCancelable(false);

        //layouts
        ImageButton back = (ImageButton) dialog.findViewById(R.id.back);
        final EditText feedback = (EditText) dialog.findViewById(R.id.feedback);
        feedback.setVisibility(View.VISIBLE);
        feedback.setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.app_font)));
        TextView titletxt = (TextView) dialog.findViewById(R.id.titletxt);
        titletxt.setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.app_font)));
        TextView titletxt1 = (TextView) dialog.findViewById(R.id.titletxt1);
        titletxt1.setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.app_font)));
        TextView continuetxt = (TextView) dialog.findViewById(R.id.Submit);
        spinSubjectCategory = (MaterialSpinner) dialog.findViewById(R.id.subject_category);
        continuetxt.setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.app_font)));

        //setOnclicks
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        continuetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedback.getText().toString().trim().length() == 0) {
                    Toast.makeText(SettingActivity.this, "Enter your feedback!", Toast.LENGTH_SHORT).show();
                } else if (strSubjectCategory == null || strSubjectCategory.equals("Select a subject")) {
                    Toast.makeText(SettingActivity.this, "Select a subject!", Toast.LENGTH_SHORT).show();
                } else {
                    sendfeedback(strSubjectCategory, feedback.getText().toString());
                    spinSubjectCategory.setSelectedIndex(0);
                    feedback.setText("");
                }
            }
        });

        spinSubjectCategory.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                strSubjectCategory = spinSubjectCategory.getItems().get(spinSubjectCategory.getSelectedIndex()).toString();
            }
        });

        dialog.show();
    }

    @AfterViews
    void settingsActivity() {

        //Change Font to Whole View
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getString("driverid", null);
        strCategory = prefs.getString("carcategory", null);
        LogUtils.i("UserID in settings" + userID + strCategory);
        // setup GeoFire with category
        if (strCategory != null && !strCategory.isEmpty()) {
            strCategory = strCategory.replaceAll("%20", " ");
            geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("drivers_location").child(strCategory));
        } else {
            geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("drivers_location"));
        }

        socialShare = new SocialShare(SettingActivity.this);
        displayDetails();
    }

    @Click(R.id.logout_button)
    void logout() {

        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(SettingActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.logout_header));
        builder.setMessage(getString(R.string.logout_msg));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                stopService(new Intent(getBaseContext(), Services.class).setPackage(SettingActivity.this.getPackageName()));

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("drivers_data").child(userID).child("online_status").setValue("0");

                geoFire.offlineLocation(userID, new GeoLocation(0.0, 0.0), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (error != null) {
                            LogUtils.i("Location not saved on server successfully!");
                        } else {
                            LogUtils.i("Location saved on server successfully!");
                        }
                    }
                });

                gooffline();
            }


        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.dismiss();
            }
            //  alertdialog2.cancel();

        });

        builder.show();
    }

    private void sendfeedback(String subject, String feedback) {

        try {
            feedback = feedback.replaceAll("#", "%23");
            feedback = URLEncoder.encode(feedback, "UTF-8");
            subject = subject.replaceAll("#", "%23");
            subject = URLEncoder.encode(subject, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final String url = Constants.LIVEURL + "feedback?user_id=" + userID + "&feedback=" + feedback + "&subject=" + subject;
        LogUtils.i("Driver Profile==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String status = jsonObject.optString("status");

                        if (status.equals("Success")) {
                            new SweetAlertDialog(SettingActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                                    .setTitleText("Thank You!")
                                    .setContentText("Your feedback was submitted successfully.")
                                    .setCustomImage(R.mipmap.ic_launcher)
                                    .show();
                        } else {
                            Toast.makeText(SettingActivity.this, "Failed to submit feedback", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    private void clearPreference() {

        SharedPreferences settings = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
        LogUtils.i("Driver id in clearing preference===>" + userID);
        Intent intent = new Intent(SettingActivity.this, LaunchActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Click(R.id.editButton)
    void editProfile() {

        Intent i = new Intent(SettingActivity.this, EditProfileActivity_.class);
        i.putExtra("firstName", firstName);
        i.putExtra("lastName", lastName);
        i.putExtra("nickName", nickName);
        i.putExtra("email", email);
        i.putExtra("mobileNumber", mobileNumber);
        i.putExtra("profileimage", profileImage);
        i.putExtra("carcategory", drivercarcategory);
        i.putExtra("coutrycode", countryCode);
        i.putExtra("refrel_code", referralcode);
        startActivity(i);
        finish();
    }

    @Click(R.id.backButton)
    void goBack() {
        Intent intent = new Intent(SettingActivity.this, LaunchActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void displayDetails() {
        //using retrofit----
        showDialog();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.LIVEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitArrayAPI service = retrofit.create(RetrofitArrayAPI.class);
        Call<List<RiderInfo>> call = service.repoContributors(userID);
        call.enqueue(new Callback<List<RiderInfo>>() {
            @Override
            public void onResponse(@NonNull Call<List<RiderInfo>> call, @NonNull retrofit2.Response<List<RiderInfo>> response) {

                try {
                    dismissDialog();
                    List<RiderInfo> RequestData = response.body();
                    if (RequestData != null) {
                        for (int i = 0; i < RequestData.size(); i++) {
                            LogUtils.i("Response Size" + RequestData.size());
                            status = RequestData.get(i).getStatus();
                            message = RequestData.get(i).getMessage();

                            if (status != null && status.equals("Success")) {

                                firstName = RequestData.get(i).getFirstname();
                                lastName = RequestData.get(i).getLastname();
                                email = RequestData.get(i).getEmail();
                                mobileNumber = RequestData.get(i).getMobile();
                                profileImage = RequestData.get(i).getProfile_pic();
                                countryCode = RequestData.get(i).getCountry_code();
                                drivercarcategory = RequestData.get(i).getCategory();
                                referralcode = RequestData.get(i).getRefrel_code();
                                nickName = RequestData.get(i).getNick_name();

                                vehicleMake = RequestData.get(i).getVehicle_make();
                                vehicleModel = RequestData.get(i).getVehicle_model();
                                vehicleYear = RequestData.get(i).getVehicle_year();
                                vehicleMileage = RequestData.get(i).getVehicle_mileage();
                                vehiclenumberplate = RequestData.get(i).getNumber_plate();

                                //savepreferences();
                                //Share code via Social
                                socialShare.setSubject("SIX Referral Code");
                                socialShare.setReferralCode(referralcode);
                                String message;
                                try {
                                    message = "Sign Up Now  " + new URL(Constants.CATEGORY_LIVE_URL) + "\n\n"
                                            + "Download App Now  "
                                            + new URL(Constants.Play_Store_URL) + "\n\n" +
                                            getString(R.string.sms_text)
                                            + "\n\n" + "Your Referral Code is " + referralcode + "." + "\n\n" +
                                            getString(R.string.sms_text_last);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    message = getString(R.string.sms_text) + "\n\n" + "Your Referral Code is " + referralcode + "." + "\n\n" + getString(R.string.sms_text_last);
                                }
                                socialShare.setMessage(message);

                                try {

                                    Glide.with(SettingActivity.this)
                                            .load(profileImage)
                                            .transform(new RoundImageTransform(SettingActivity.this))
                                            .into(edtProfileImage);

                                    inputFirstName.setText(firstName.replaceAll("%20", " "));
                                    inputLastName.setText(lastName.replaceAll("%20", " "));
                                    inputNickName.setText(nickName.replaceAll("%20", " "));
                                    inputEmail.setText(email);
                                    inputMobileNumber.setText(mobileNumber);
                                    inputCountryCode.setText(countryCode);
                                    carcategory.setText(drivercarcategory.replaceAll("%20", " "));
                                    Referral_code.setText(referralcode);
                                    editvehicleNumberplate.setText(vehiclenumberplate.replaceAll("%20", " "));

                                    inputVehiclemake.setText(vehicleMake.replaceAll("%20", " "));
                                    inputVehiclemodel.setText(vehicleModel.replaceAll("%20", " "));
                                    inputVehicleyear.setText(vehicleYear);
                                    inputVehiclemileage.setText(vehicleMileage);

                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                LogUtils.i("inside else");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RiderInfo>> call, @NonNull Throwable t) {
                dismissDialog();
                LogUtils.i("Exception" + t);
                Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void gooffline() {

        long currentDuration = getDuration();

        String url = Constants.LIVEURL + "updateOnlineStatus/userid/" + userID + "/online_status/0/online_duration/" + currentDuration;
        LogUtils.i(" ONLINE URL is " + url);

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {

                            try {

                                JSONObject signIn_jsonobj = response.getJSONObject(i);
                                String signIn_status = signIn_jsonobj.optString("status");

                                if (signIn_status.equals("Success")) {
                                    LogUtils.i("SettingActivity Success");
                                } else if (signIn_status.equals("Fail")) {
                                    //stopAnim();
                                    LogUtils.i("SettingActivity fail");
                                }
                            } catch (JSONException e) {
                                //stopAnim();
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //protected static final String TAG = null;
                if (error instanceof NoConnectionError) {
                    LogUtils.i("SettingActivity NoConnectionError");
                    // stopAnim();
                    //
                    //    Toast.makeText(Map_Activity.this, "An unknown network error has occured", Toast.LENGTH_SHORT).show();
                }
                LogUtils.d("Setting Activity " + "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);

        clearPreference();
    }

    public void showDialog() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        //to avoid bad token exception
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    if (!SettingActivity.this.isFinishing()) {
                        progressDialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void dismissDialog() {

        if (!SettingActivity.this.isFinishing()) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (socialShareDialog != null) {

            if (socialShareDialog.isShowing()) {

                Toast.makeText(this, "Please Wait...", Toast.LENGTH_SHORT).show();
                socialShareDialog.dismiss();
            }
        }
    }

    @Override
    protected void onDestroy() {
        // Clear
        Glide.clear(edtProfileImage);

        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(SettingActivity.this, Map_Activity.class);
        startActivity(intent);
    }

    public long getDuration() {

        String tmpStartDate = prefs.getString("onlineStartDate", null);
        long currentDuration;

        if (tmpStartDate != null) {

            String strEndDate = getDateTime();

            try {

                TimeZone GMT = TimeZone.getTimeZone("GMT");
                DateFormat dateStartTimefarmat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                dateStartTimefarmat.setTimeZone(GMT);
                DateFormat dateEndTimefarmat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                dateEndTimefarmat.setTimeZone(GMT);

                Date endDate = dateEndTimefarmat.parse(strEndDate);//end_date
                Date startDate = dateStartTimefarmat.parse(tmpStartDate);//start_date

                if (startDate != null & endDate != null) {

                    currentDuration = endDate.getTime() - startDate.getTime();

                    LogUtils.i("startDate : " + startDate);
                    LogUtils.i("endDate : " + endDate);
                    LogUtils.i("different : " + currentDuration);

                    return currentDuration;

                } else {
                    return 0;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        } else {

            return 0;
        }
    }

    private String getDateTime() {

        try {
            TimeZone GMT = TimeZone.getTimeZone("GMT");
            DateFormat dateTimefarmat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            dateTimefarmat.setTimeZone(GMT);
            Date now = new Date();
            String strCurrentDateTime = dateTimefarmat.format(now);

            //Getting time and Date
            Date CurrentDateTime = dateTimefarmat.parse(strCurrentDateTime);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
            LogUtils.i("Date: " + dateFormat.format(CurrentDateTime));
            LogUtils.i("Time: " + timeFormat.format(CurrentDateTime));
            LogUtils.i("Date and Time: " + dateFormat.format(CurrentDateTime) + " " + timeFormat.format(CurrentDateTime));

            return strCurrentDateTime;

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getSubjectCategory() {

        final String url = Constants.CATEGORY_LIVE_URL + "home/getsubject";
        LogUtils.i("URL is" + url);
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parsing json
                        subjectcategory = new String[response.length() + 1];
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject strJsonCategory = response.getJSONObject(i);
                                strCarCategory = strJsonCategory.optString("subject");
                                LogUtils.d("OUTPUT IS " + strCarCategory);
                                subjectcategory[0] = "Select a subject";
                                subjectcategory[i + 1] = strCarCategory;
                                LogUtils.i("CATEGORY" + subjectcategory[i]);
                                //  adapteradapter  = new ArrayAdapter<String>(SettingActivity.this, R.layout.spinner_item, subjectcategory);

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }
                        spinSubjectCategory.setItems(subjectcategory);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    Toast.makeText(getApplicationContext(), "No Intenet Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    LogUtils.i("AuthFailureError");
                } else if (error instanceof ServerError) {
                    LogUtils.i("ServerError");
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(), "No Intenet Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    LogUtils.i("ParseError");
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
    }
}