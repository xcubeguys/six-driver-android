package com.tommy.driver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.tommy.driver.adapter.AppController;
import com.tommy.driver.adapter.BulletTextUtil;
import com.tommy.driver.adapter.Constants;
import com.tommy.driver.adapter.FontChangeCrawler;
import com.tommy.driver.utils.LogUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by test on 12/16/16.
 **/
@EActivity(R.layout.activity_upload_doc)
public class DocUpload_Activity extends MyBaseActivity implements Validator.ValidationListener {

    private static final int CAMERA_REQUEST = 95;
    private static final int GALLERY_REQUEST = 96;
    Uri mCapturedImageURI;
    /**
     * 0 - driver license front
     * 1 - driver license back
     * 2 - nric front
     * 3 - nric back
     * 4 - acra
     * 5 - vehicle registration
     * 6 - commercial insurance
     * 7 - experience
     * 8 - private hire
     */
    int photoPickIdentificationId;
    String picturePath, strreferral, strVehiclemake, strVehiclemodel, strVehicleyear, strVehiclemileage, strreferralcode, strnumberplate, content;
    ProgressDialog progressDialog;
    SharedPreferences.Editor editor;
    String[] carcategory;
    Dialog dialog;

    String driverID, driverFirstName, driverLastName, driverNickName, driverEmail, driverMobile, strCarCategory, strNumOfPassenger, referralStatus;
    JSONObject strJsonCategory;
    @ViewById(R.id.termsandconditions)
    CheckBox tandc;
    @NotEmpty(message = "")
    @Length(min = 1, message = "Enter number of passengers")
    @ViewById(R.id.numberofpass)
    EditText numberofpass;
    @NotEmpty(message = "Enter Vehicle License Plate Number")
    @ViewById(R.id.vehicle_numberplate)
    EditText editVehiclenumberplate;
    @NotEmpty(message = "Enter Vehicle Make")
    @ViewById(R.id.vehicle_make)
    EditText edtVehicleMake;
    @NotEmpty(message = "Enter Vehicle Model")
    @ViewById(R.id.vehicle_model)
    EditText edtVehicleModel;
    @NotEmpty(message = "Enter Vehicle Year")
    @ViewById(R.id.vehicle_year)
    EditText edtVehicleYear;
    @NotEmpty(message = "Enter Vehicle Mileage")
    @ViewById(R.id.vehicle_mileage)
    EditText edtVehicleMileage;
    @ViewById(R.id.input_Referral_code)
    EditText edtReferralcode;
    String strEmail, strFirstName, strLastName, strNickName, strPassword, strCity, strMobile, strCountyCode, strProfileImage, strComingfrom, strFBID, strGoogleID, strSelectedCategory;
    Validator validator;
    Spinner spinnerCategory;
    /**
     * 0 - driver license front
     * 1 - driver license back
     * 2 - nric front
     * 3 - nric back
     * 4 - acra
     * 5 - vehicle registration
     * 6 - commercial insurance
     * 7 - experience
     * 8 - private hire
     */
    private String uploadedImageUrls[];
    /**
     * 0 - driver license front
     * 1 - driver license back
     * 2 - nric front
     * 3 - nric back
     * 4 - acra
     * 5 - vehicle registration
     * 6 - commercial insurance
     * 7 - experience
     * 8 - private hire
     */
    private ImageView documentsImageViews[];

    @Click(R.id.back)
    void getback() {
        finish();
    }

    @CheckedChange(R.id.termsandconditions)
    public void terms(boolean isChecked) {
        if (isChecked) {
            getTermsCondition();
            showTermsDialog();
        } else {
            if (dialog != null)
                dialog.dismiss();
        }
    }

    private void initializeViewsOfUploadImages() {
        RelativeLayout driverLicenseRl = (RelativeLayout) findViewById(R.id.driver_license_ll);
        ((TextView) driverLicenseRl.findViewById(R.id.title_tv)).setText("Add Driver License");
        driverLicenseRl.findViewById(R.id.first_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoPickIdentificationId = 0;
                popupImagePickingAlert();
            }
        });
        driverLicenseRl.findViewById(R.id.second_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoPickIdentificationId = 1;
                popupImagePickingAlert();
            }
        });
        ImageView driverFirstIv = (ImageView) driverLicenseRl.findViewById(R.id.first_iv);
        ImageView driverSecondIv = (ImageView) driverLicenseRl.findViewById(R.id.second_iv);

        RelativeLayout nricRl = (RelativeLayout) findViewById(R.id.nric_ll);
        ((TextView) nricRl.findViewById(R.id.title_tv)).setText("Add NRIC Document");
        nricRl.findViewById(R.id.first_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoPickIdentificationId = 2;
                popupImagePickingAlert();
            }
        });
        nricRl.findViewById(R.id.second_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoPickIdentificationId = 3;
                popupImagePickingAlert();
            }
        });
        ImageView nricFirstIv = (ImageView) nricRl.findViewById(R.id.first_iv);
        ImageView nricSecondIv = (ImageView) nricRl.findViewById(R.id.second_iv);

        RelativeLayout acraRentalRl = (RelativeLayout) findViewById(R.id.acra_rental_ll);
        ((TextView) acraRentalRl.findViewById(R.id.title_tv)).setText("Add ACRA Business Profile / Rental Document");
        acraRentalRl.findViewById(R.id.first_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoPickIdentificationId = 4;
                popupImagePickingAlert();
            }
        });
        ImageView acraFirstIv = (ImageView) acraRentalRl.findViewById(R.id.first_iv);
        setUIToUploadOneImage(acraRentalRl);

        RelativeLayout vehicleRegRl = (RelativeLayout) findViewById(R.id.vehicle_registration_ll);
        ((TextView) vehicleRegRl.findViewById(R.id.title_tv)).setText("Add Vehicle Registration (Log Card) / LTA letter for conversion of private vehicle (if it is a private car)");
        vehicleRegRl.findViewById(R.id.first_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoPickIdentificationId = 5;
                popupImagePickingAlert();
            }
        });
        ImageView vehicleRegFirstIv = (ImageView) vehicleRegRl.findViewById(R.id.first_iv);
        setUIToUploadOneImage(vehicleRegRl);

        RelativeLayout commercialInsuranceRl = (RelativeLayout) findViewById(R.id.commercial_insurance_ll);
        ((TextView) commercialInsuranceRl.findViewById(R.id.title_tv)).setText("Add Commercial Insurance");
        commercialInsuranceRl.findViewById(R.id.first_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoPickIdentificationId = 6;
                popupImagePickingAlert();
            }
        });
        ImageView commercialFirstIv = (ImageView) commercialInsuranceRl.findViewById(R.id.first_iv);
        setUIToUploadOneImage(commercialInsuranceRl);

        RelativeLayout experienceRl = (RelativeLayout) findViewById(R.id.experience_ll);
        ((TextView) experienceRl.findViewById(R.id.title_tv)).setText("Add Min 1 month experience as a Private Hired (PH) car - Screenshot of driver’s profile");
        experienceRl.findViewById(R.id.first_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoPickIdentificationId = 7;
                popupImagePickingAlert();
            }
        });
        ImageView experienceFirstIv = (ImageView) experienceRl.findViewById(R.id.first_iv);
        setUIToUploadOneImage(experienceRl);

        RelativeLayout privateVocationalLicenseRl = (RelativeLayout) findViewById(R.id.private_vocational_license_ll);
        ((TextView) privateVocationalLicenseRl.findViewById(R.id.title_tv)).setText("Add Private Hire Car Driver’s Vocational License (PDVL) WEF 1 July 2017 (Please bring alone if you already acquired it)");
        privateVocationalLicenseRl.findViewById(R.id.first_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoPickIdentificationId = 8;
                popupImagePickingAlert();
            }
        });
        ImageView privateFirstIv = (ImageView) privateVocationalLicenseRl.findViewById(R.id.first_iv);
        setUIToUploadOneImage(privateVocationalLicenseRl);

        uploadedImageUrls = new String[9];

        documentsImageViews = new ImageView[9];
        documentsImageViews[0] = driverFirstIv;
        documentsImageViews[1] = driverSecondIv;
        documentsImageViews[2] = nricFirstIv;
        documentsImageViews[3] = nricSecondIv;
        documentsImageViews[4] = acraFirstIv;
        documentsImageViews[5] = vehicleRegFirstIv;
        documentsImageViews[6] = commercialFirstIv;
        documentsImageViews[7] = experienceFirstIv;
        documentsImageViews[8] = privateFirstIv;
    }

    private void setUIToUploadOneImage(RelativeLayout parentRl) {
        parentRl.findViewById(R.id.first_tv).setVisibility(View.INVISIBLE);
        parentRl.findViewById(R.id.second_ll).setVisibility(View.GONE);
    }

    @AfterViews
    void signUpImage() {

        initializeViewsOfUploadImages();

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        Intent i = getIntent();
        strFirstName = i.getStringExtra("FirstName");
        strLastName = i.getStringExtra("LastName");
        strPassword = i.getStringExtra("Password");
        strCity = i.getStringExtra("City");
        strMobile = i.getStringExtra("Mobile");
        strEmail = i.getStringExtra("Email");
        strCountyCode = i.getStringExtra("CountryCode");
        strProfileImage = i.getStringExtra("ProfilePicture");
        strComingfrom = i.getStringExtra("Comingfrom");
        strFBID = i.getStringExtra("FbID");
        strGoogleID = i.getStringExtra("GoogleID");
        strNickName = i.getStringExtra("nick_name");
        strreferral = i.getStringExtra("referral");

        LogUtils.i("strreferral====" + strreferral);
        LogUtils.i("strVehiclemake====" + strVehiclemake);
        LogUtils.i("strVehiclemodel====" + strVehiclemodel);
        LogUtils.i("strVehicleyear====" + strVehicleyear);
        LogUtils.i("strVehiclemileage====" + strVehiclemileage);

        LogUtils.i("DOCUMENT PAGEE " + "FNAME==" + strFirstName + " " + "LNAME==>" + strLastName + " " + "MOBILE==" + strMobile + " " + "PASSWORD==" + strPassword + " " + "CITY==" + strCity + " " + "PROFILE PICTUREE" + strProfileImage);
        getCategoryDetails();

        editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();

        getTermsCondition();

        validator = new Validator(this);
        validator.setValidationListener(this);

        spinnerCategory = (Spinner) findViewById(R.id.car_category);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                strSelectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showTermsDialog() {
        dialog = new Dialog(DocUpload_Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_terms_and_condition);
        dialog.setCancelable(false);

        //layouts
        Button ok = (Button) dialog.findViewById(R.id.iagree);
        TextView titletxt = (TextView) dialog.findViewById(R.id.termscontent);
        titletxt.setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.app_font)));

        CharSequence bulletedList = BulletTextUtil.makeBulletListFromStringArrayResource(10, getApplicationContext(), getResources().getIdentifier("termscontent", "array", getPackageName()));
        if (content != null) {
            content = content.replaceAll("%20", " ").replace("&rsquo;", "'").replaceAll("&amp;", "&");
            content = content.replaceAll("%40", "\n");
            titletxt.setText(content);
        }

        //setOnclicks

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void getCategoryDetails() {

        final String url = Constants.CATEGORY_LIVE_URL + "Settings/getCategory";
        LogUtils.i("URL is" + url);
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parsing json
                        carcategory = new String[response.length() + 1];
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                strJsonCategory = response.getJSONObject(i);
                                strCarCategory = strJsonCategory.getString("categoryname");
                                LogUtils.d("OUTPUT IS " + strCarCategory);
                                carcategory[0] = "Select car category";
                                carcategory[i + 1] = strCarCategory;
                                LogUtils.i("CATEGORY" + carcategory[i]);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(DocUpload_Activity.this, R.layout.spinner_item, carcategory);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerCategory.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "No Internet. Please Connect.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                } else if (error instanceof ServerError) {
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(), "No Internet. Please Connect.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    @Click(R.id.continuebut)
    void con() {
        validator.validate();
    }

    private void googleSignUP() {
        showDialog();
        strFirstName = strFirstName.replaceAll(" ", "%20");
        strLastName = strLastName.replaceAll(" ", "%20");
        strSelectedCategory = strSelectedCategory.trim().replaceAll(" ", "%20");
        //  http://demo.cogzidel.com/arcane_lite/driver/googleSignup/regid/5765/first_name/cogzidel/last_name/c/mobile/73376543212/email/cogzidel_new33@gmrail.com/license/yy.png/insurance/zz.png/google_id/23244

        // TODO: 14/7/17 all new ones needs to be added in server
        String url = Constants.LIVEURL + "googleSignup/" + "first_name/" + strFirstName + "/last_name/" + strLastName + "/nick_name/" + strFirstName +
                "/email/" + strEmail + "/regid/344444444444444" + "/google_id/" + strGoogleID +
                "/license/" + uploadedImageUrls[0] + /*old - driver license front*/
                "/license_back/" + uploadedImageUrls[1] + /*new - driver license back*/
                "/nric_front/" + uploadedImageUrls[2] + /*new - ncri front*/
                "/nric_back/" + uploadedImageUrls[3] + /*new - ncri back*/
                "/ARCA/" + uploadedImageUrls[4] + /*old - arca*/
                "/vehicle_reg/" + uploadedImageUrls[5] + /*old - vehicle registration*/
                "/commercial_pic/" + uploadedImageUrls[6] + /*old - vehicle insurance - but not sure whether it should be "insurance" or "commercial_pic"*/ // TODO: 14/7/17 check this
                "/ph_experience/" + uploadedImageUrls[7] + /*new - 1 month experience*/
                "/ph_hire/" + uploadedImageUrls[8] + /*new - private hire car driver*/
                "/category/" + strSelectedCategory + "/profile_pic/" + strProfileImage + "/vehicle_make/" + strVehiclemake + "/vehicle_model/" + strVehiclemodel + "/vehicle_year/" + strVehicleyear + "/vehicle_mileage/" + strVehiclemileage + "/number_plate/" + strnumberplate;

        if (strreferralcode != null) {
            if (!strreferralcode.equals("")) {
                url = url + "/referral_code/" + strreferralcode;
            }
        }
        LogUtils.i("Driver SignUp URL==>" + url);
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        dismissDialog();
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject register_status = response.getJSONObject(i);

                                if (register_status.optString("status").matches("Success")) {

                                    if (register_status.optString("_id").isEmpty()) {
                                        driverID = register_status.optString("userid");
                                    } else {
                                        driverID = register_status.optString("_id");
                                        saveInFirebase();
                                    }

                                    LogUtils.i("THR DRIER IS" + driverID);
                                    driverFirstName = register_status.optString("first_name");
                                    driverLastName = register_status.optString("last_name");
                                    driverEmail = register_status.optString("email");
                                    driverMobile = register_status.optString("mobile");
                                    savepreferences();

                                    Intent map = new Intent(getApplicationContext(), Map_Activity.class);
                                    map.putExtra("loginfrom", "socialnetwork");
                                    map.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(map);
                                    Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                                } else if (register_status.optString("status_extra").matches("Exist")) {
                                    driverID = register_status.optString("userid");
                                    driverFirstName = register_status.optString("first_name");
                                    driverLastName = register_status.optString("last_name");
                                    driverEmail = register_status.optString("email");
                                    driverMobile = register_status.optString("mobile");
                                    savepreferences();
                                    Intent map = new Intent(getApplicationContext(), Map_Activity.class);
                                    map.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(map);
                                    finish();
                                    Toast.makeText(DocUpload_Activity.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();
                                } else if (register_status.optString("status").equals("Email Already Exists.")) {
                                    //stopAnim();
                                    Toast.makeText(DocUpload_Activity.this, "Sorry! Email already exist.", Toast.LENGTH_SHORT).show();
                                } else if (register_status.optString("status").equals("Mobile Number Already Exists.")) {
                                    //stopAnim();
                                    Toast.makeText(DocUpload_Activity.this, "Sorry! Mobile Number already exist.", Toast.LENGTH_SHORT).show();
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
                dismissDialog();
                //protected static final String TAG = null;
                if (error instanceof NoConnectionError) {
                    // stopAnim();
                    Toast.makeText(DocUpload_Activity.this, "No Internet. Please Connect.", Toast.LENGTH_SHORT).show();
                }
                LogUtils.d("Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    private void faceBookLogin() {
        showDialog();
        if (strFirstName != null) {
            strFirstName = strFirstName.replaceAll(" ", "%20");
        } else
            strFirstName = "";
        if (strLastName != null) {
            strLastName = strLastName.replaceAll(" ", "%20");
        } else
            strLastName = "";
        strSelectedCategory = strSelectedCategory.trim().replaceAll(" ", "%20");
        //final String url= Constants.LIVEURL+"fbSignup/"+"first_name/"+strFirstName+"/last_name/"+strLastName+"/email/"+strEmail+"/regid/344444444444444"+"/profile_pic/"+strProfileImage+"/fb_id/"+strFBID+"/license/"+strLicense+"/insurance/"+strInsurnce;
        String url = Constants.LIVEURL + "fbSignup/" + "first_name/" + strFirstName + "/last_name/" + strLastName +
                "/nick_name/" + strFirstName + "/email/" + strEmail + "/regid/344444444444444" + "/fb_id/" + strFBID +
                "/license/" + uploadedImageUrls[0] + /*old - driver license front*/
                "/license_back/" + uploadedImageUrls[1] + /*new - driver license back*/
                "/nric_front/" + uploadedImageUrls[2] + /*new - ncri front*/
                "/nric_back/" + uploadedImageUrls[3] + /*new - ncri back*/
                "/ARCA/" + uploadedImageUrls[4] + /*old - arca*/
                "/vehicle_reg/" + uploadedImageUrls[5] + /*old - vehicle registration*/
                "/commercial_pic/" + uploadedImageUrls[6] + /*old - vehicle insurance - but not sure whether it should be "insurance" or "commercial_pic"*/ // TODO: 14/7/17 check this
                "/ph_experience/" + uploadedImageUrls[7] + /*new - 1 month experience*/
                "/ph_hire/" + uploadedImageUrls[8] + /*new - private hire car driver*/
                "/category/" + strSelectedCategory +
                "/vehicle_make/" + strVehiclemake + "/vehicle_model/" + strVehiclemodel + "/vehicle_year/" + strVehicleyear + "/vehicle_mileage/" + strVehiclemileage + "/number_plate/" + strnumberplate;
        if (strreferralcode != null) {
            if (!strreferralcode.equals("")) {
                url = url + "/referral_code/" + strreferralcode;
            }
        }
        LogUtils.i("Driver SignUp URL==>" + url);
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        dismissDialog();
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject register_status = response.getJSONObject(i);

                                if (register_status.optString("status").matches("Success")) {

                                    if (register_status.optString("_id").isEmpty()) {

                                        driverID = register_status.optString("userid");
                                    } else {
                                        driverID = register_status.optString("_id");
                                        saveInFirebase();
                                    }

                                    LogUtils.i("Uswer ID" + driverID);
                                    driverFirstName = register_status.optString("first_name");
                                    LogUtils.i("driverFirstName ID" + driverFirstName);

                                    driverLastName = register_status.optString("last_name");
                                    driverEmail = register_status.optString("email");
                                    driverMobile = register_status.optString("mobile");
                                    savepreferences();

                                    Intent map = new Intent(getApplicationContext(), Map_Activity.class);
                                    map.putExtra("loginfrom", "socialnetwork");
                                    map.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(map);
                                    finish();
                                    Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                                } else if (register_status.optString("status").equals("Email Already Exists.")) {
                                    //stopAnim();
                                    Toast.makeText(DocUpload_Activity.this, "Sorry! Email already exist.", Toast.LENGTH_SHORT).show();
                                } else if (register_status.optString("status").equals("Mobile Number Already Exists.")) {
                                    //stopAnim();
                                    Toast.makeText(DocUpload_Activity.this, "Sorry! Mobile Number already exist.", Toast.LENGTH_SHORT).show();
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
                dismissDialog();
                if (error instanceof NoConnectionError) {
                    // stopAnim();
                    Toast.makeText(DocUpload_Activity.this, "No Internet. Please Connect.", Toast.LENGTH_SHORT).show();
                }
                LogUtils.d("Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    private void loginSuccess() {
        showDialog();
        LogUtils.i("Number of passenger in DocUpload" + strNumOfPassenger);
        strFirstName = strFirstName.replaceAll(" ", "%20");
        strLastName = strLastName.replaceAll(" ", "%20");
        strSelectedCategory = strSelectedCategory.trim().replaceAll(" ", "%20");

        String url = Constants.LIVEURL + "signUp/" + "first_name/" + strFirstName + "/last_name/" + strLastName + "/nick_name/" + strNickName +
                "/mobile/" + strMobile + "/country_code/" + strCountyCode + "/password/" + strPassword + "/city/" + strCity +
                "/email/" + strEmail + "/regid/344444444444444" + "/profile_pic/" + strProfileImage +
                "/license/" + uploadedImageUrls[0] + /*old - driver license front*/
                "/license_back/" + uploadedImageUrls[1] + /*new - driver license back*/
                "/nric_front/" + uploadedImageUrls[2] + /*new - ncri front*/
                "/nric_back/" + uploadedImageUrls[3] + /*new - ncri back*/
                "/ARCA/" + uploadedImageUrls[4] + /*old - arca*/
                "/vehicle_reg/" + uploadedImageUrls[5] + /*old - vehicle registration*/
                "/commercial_pic/" + uploadedImageUrls[6] + /*old - vehicle insurance - but not sure whether it should be "insurance" or "commercial_pic"*/ // TODO: 14/7/17 check this
                "/ph_experience/" + uploadedImageUrls[7] + /*new - 1 month experience*/
                "/ph_hire/" + uploadedImageUrls[8] + /*new - private hire car driver*/
                "/category/" + strSelectedCategory + "/passenger_count/" + strNumOfPassenger + "/vehicle_make/" + strVehiclemake +
                "/vehicle_model/" + strVehiclemodel + "/vehicle_year/" + strVehicleyear + "/vehicle_mileage/" + strVehiclemileage + "/number_plate/" + strnumberplate;

        if (strreferralcode != null) {
            if (!strreferralcode.equals("")) {
                url = url + "/referral_code/" + strreferralcode;
            }
        }
        LogUtils.i("Driver SignUp URL==>" + url);
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        dismissDialog();
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject register_status = response.getJSONObject(i);

                                String status = register_status.optString("status");

                                if (status.matches("Success")) {
                                    driverID = register_status.optString("userid");
                                    driverFirstName = register_status.optString("first_name");
                                    driverLastName = register_status.optString("last_name");
                                    driverNickName = register_status.optString("nick_name");
                                    driverEmail = register_status.optString("email");
                                    driverMobile = register_status.optString("mobile");
                                    savepreferences();
                                    saveInFirebase();
                                    Intent map = new Intent(getApplicationContext(), Map_Activity.class);
                                    map.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(map);
                                    finish();

                                    Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                                } else if (status.matches("Fail")) {

                                    String statusMessage = register_status.optString("message");

                                    if (statusMessage.matches("Email already exists")) {
                                        //stopAnim();
                                        Toast.makeText(DocUpload_Activity.this, "Sorry! Email already exist.", Toast.LENGTH_SHORT).show();
                                    } else if (statusMessage.matches("Mobile number already exists")) {
                                        //stopAnim();
                                        Toast.makeText(DocUpload_Activity.this, "Sorry! Mobile Number already exist.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //stopAnim();
                                        Toast.makeText(DocUpload_Activity.this, "Sorry! Email and Mobile Number already exist.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //protected static final String TAG = null;
                dismissDialog();
                if (error instanceof NoConnectionError) {
                    // stopAnim();
                    Toast.makeText(DocUpload_Activity.this, "No Internet. Please Connect.", Toast.LENGTH_SHORT).show();
                }
                LogUtils.d("Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    private void popupImagePickingAlert() {
        String[] messagesArray = getResources().getStringArray(R.array.image_upload_messages);

        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
        builder.setMessage(messagesArray[photoPickIdentificationId]);

        builder.setNegativeButton(getString(R.string.camera), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (DocUpload_Activity.this.checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        start_camera();
                    } else {
                        dialog.cancel();
                        DocUpload_Activity.this.requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 5);
                    }
                } else {
                    start_camera();
                }
            }
        });
        builder.setNeutralButton(getString(R.string.gallery), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_REQUEST);
            }
        });
        builder.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void start_camera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image File name");
        mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
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
                Toast.makeText(DocUpload_Activity.this, "Please provide permission to use camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            // result for camera picking of image
            if (requestCode == CAMERA_REQUEST) {
                picturePath = getRealPathFromURI(mCapturedImageURI);
                setPicturePathAndUpload();
            }

            // result for gallery picking of image
            if (requestCode == GALLERY_REQUEST && null != data) {
                Uri selectedImage = data.getData();
                /*if (Build.VERSION.SDK_INT >= 19) {*/
                if (selectedImage != null && !selectedImage.toString().equals("null")) {
                    LogUtils.i("greater 19:" + "kitkat");
                    picturePath = getImagePath(selectedImage);
                    LogUtils.i("mSelectedFissslssePath res" + picturePath);

                } else {
                    LogUtils.i("greater 19:" + "not kitkat");
                    picturePath = getPathOfImage(selectedImage);
                    LogUtils.i("mSelectedFissslePath res" + picturePath);
                }
                /*}*/
                LogUtils.i("Request Code+requestCode" + "Result Code" + resultCode + "data" + data);
                setPicturePathAndUpload();
            }
        }
    }

    private void setPicturePathAndUpload() {
        documentsImageViews[photoPickIdentificationId].setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(getApplicationContext()).load(picturePath).skipMemoryCache(true).into(documentsImageViews[photoPickIdentificationId]);
        new ImageUploadTask(this).execute();
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

        outState.putParcelable("picUri", mCapturedImageURI);
        super.onSaveInstanceState(outState);
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

    @Override
    public void onValidationSucceeded() {

        String[] stringArray = getResources().getStringArray(R.array.image_upload_errors);
        boolean anyImageNull = false;
        int index = 0;
        for (; index < uploadedImageUrls.length; index++) {
            if (uploadedImageUrls[index] == null) {
                anyImageNull = true;
                break;
            }
        }
        if (anyImageNull) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(stringArray[index]);
            builder.setPositiveButton(getString(R.string.ok), null);
            builder.show();
        } else if (strSelectedCategory == null) {

            android.support.v7.app.AlertDialog.Builder builder =
                    new android.support.v7.app.AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(getString(R.string.select_category));

            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            });
            builder.show();

        } else if (numberofpass.getText().toString().trim().length() == 0) {
            numberofpass.setError("Enter number of passengers");
        } else if (!tandc.isChecked()) {
            Toast.makeText(DocUpload_Activity.this, "Agree terms and conditions", Toast.LENGTH_SHORT).show();
        } else {
            numberofpass.setError(null);
            strNumOfPassenger = numberofpass.getText().toString().trim();

            strVehiclemake = edtVehicleMake.getText().toString();
            strVehiclemodel = edtVehicleModel.getText().toString();
            strVehicleyear = edtVehicleYear.getText().toString();
            strVehiclemileage = edtVehicleMileage.getText().toString();

            strreferralcode = edtReferralcode.getText().toString().trim();
            strnumberplate = editVehiclenumberplate.getText().toString().trim();
            strnumberplate = strnumberplate.replaceAll(" ", "%20");

            strVehiclemake = strVehiclemake.replaceAll(" ", "%20");
            strVehiclemodel = strVehiclemodel.replaceAll(" ", "%20");

            if (!strreferralcode.equals("")) {

                try {
                    byte[] encoded = Base64.encode(strreferralcode.getBytes("UTF-8"), Base64.DEFAULT);
                    strreferralcode = new String(encoded, "UTF-8");
                    strreferralcode = strreferralcode.replaceAll("=", "").trim();
                    LogUtils.i("Encoding UTF" + strreferralcode);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                validatereferral(strreferralcode);
                LogUtils.i("enter the condition");

            } else if (strComingfrom != null) {

                LogUtils.i("enter the condition two");

                if (strComingfrom.matches("facebook")) {
                    faceBookLogin();
                } else if (strComingfrom.matches("google")) {
                    googleSignUP();
                }
            } else {
                loginSuccess();
            }
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected boolean Upload_Server() {

        LogUtils.i("After call progress");
        try {

            LogUtils.e("Image Upload " + "Inside Upload");

            HttpURLConnection connection;
            DataOutputStream outputStream;

            String pathToOurFile = picturePath;
            //	  String pathToOurFile1 = imagepathcam;

            LogUtils.i("Before Image Upload" + picturePath);

            String urlServer = Constants.LIVEURL + "imageUpload";
            LogUtils.i("URL SETVER" + urlServer);
            LogUtils.i("After Image Upload" + picturePath);
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024 * 1024;

            FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile));
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
            //int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            LogUtils.i("image" + serverResponseMessage);

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            DataInputStream inputStream1;
            inputStream1 = new DataInputStream(connection.getInputStream());
            String str;
            String responseStr = "";

            while ((str = inputStream1.readLine()) != null) {
                responseStr = str;
            }
            inputStream1.close();

            String uploadedImageName;
            if (responseStr.length() > 0) {
                uploadedImageName = new JSONArray(responseStr).getJSONObject(0).optString("image_name");
            } else {
                return false;
            }

            //get the image url and store
            if (uploadedImageName == null || uploadedImageName.length() == 0) {
                return false;
            } else {
                uploadedImageUrls[photoPickIdentificationId] = uploadedImageName;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void savepreferences() {

        editor.putString("driverid", driverID);
        editor.putString("drivername", driverFirstName);
        editor.putString("driverphonenum", driverMobile);
        editor.putString("carcategory", strSelectedCategory);
        editor.apply();
    }

    public void saveInFirebase() {

        if (driverID != null && !driverID.isEmpty()) {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverID);
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", driverFirstName);
            updates.put("proof_status", "Pending");    //proofstatus
            updates.put("online_status", "0");    //onlinestatus

            Map<String, Object> updateaccept = new HashMap<>();
            updateaccept.put("status", "");
            updateaccept.put("trip_id", "");
            updates.put("accept", updateaccept);

            Map<String, Object> updaterequest = new HashMap<>();
            updaterequest.put("req_id", "");
            updaterequest.put("status", "");
            updates.put("request", updaterequest);

            ref.setValue(updates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    LogUtils.i("DATA SAVED SUCCESSFULLY");
                    if (databaseError != null) {
                        LogUtils.i("DATA SAVED SUCCESSFULLY");
                    }
                }
            });
        }
    }

    public void showDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    public void dismissDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void validatereferral(String referralcode) {

        showDialog();

        final String url = Constants.LIVEURL + "refrel_code/code/" + referralcode;
        LogUtils.i("EmailExistURL==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                dismissDialog();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        referralStatus = jsonObject.optString("status");

                        if (referralStatus.equals("Success")) {
                            if (strComingfrom != null) {

                                if (strComingfrom.matches("facebook")) {
                                    faceBookLogin();
                                } else if (strComingfrom.matches("google")) {
                                    googleSignUP();
                                }
                            } else {
                                loginSuccess();
                            }
                        } else {
                            edtReferralcode.setError(getResources().getString(R.string.referral_invalid));
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismissDialog();
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "No Internet, Please Connect.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    private void getTermsCondition() {
        final String url = Constants.CATEGORY_LIVE_URL + "Settings/termsconditions";
        LogUtils.i("GetTermsURL==>" + url);
        final JsonArrayRequest infoReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        LogUtils.i("Response from GetTerms==>" + jsonObject);
                        content = jsonObject.optString("value");

                    } catch (JSONException | NullPointerException | ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                LogUtils.i("The ERror in url" + volleyError);
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
                if (volleyError instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });

        infoReq.setRetryPolicy(new DefaultRetryPolicy(5000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(infoReq);
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

    private class ImageUploadTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog;
        private Context context;

        ImageUploadTask(DocUpload_Activity activity) {
            context = activity;
            dialog = new ProgressDialog(context);
        }

        protected void onPreExecute() {

            dialog = new ProgressDialog(context);
            dialog.setMessage("Uploading...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (!success) {
                documentsImageViews[photoPickIdentificationId].setImageBitmap(null);
                AlertDialog.Builder builder = new AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
                builder.setMessage("Failed to upload image. Please try again.");
                builder.setPositiveButton(getString(R.string.ok), null);
                builder.show();
            }

            if (dialog != null && dialog.isShowing()) {
                if (!isFinishing() && !isDestroyed()) {
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
                return Upload_Server();
            } catch (Exception e) {
                LogUtils.e("Schedule " + "UpdateSchedule failed " + e);
                return false;
            }
        }
    }

}