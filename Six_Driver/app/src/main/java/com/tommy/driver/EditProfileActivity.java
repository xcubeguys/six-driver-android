package com.tommy.driver;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.core.GeoHash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.tommy.driver.adapter.AppController;
import com.tommy.driver.adapter.Constants;
import com.tommy.driver.adapter.FontChangeCrawler;
import com.tommy.driver.adapter.RoundImageTransform;
import com.tommy.driver.utils.LogUtils;

import org.androidannotations.annotations.AfterViews;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.activity_edit_profile)
public class EditProfileActivity extends AppCompatActivity implements CountryCodePicker.OnCountryChangeListener, Validator.ValidationListener {

    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE = 100;
    public String userID, firstName, lastName, nickName, email, mobileNumber, countryCode, referal_code, profileImage, profileImageNew = "null", status, message, driverUpdateURL, vehicleMake, vehicleModel, vehicleYear, vehicleMileage, vehiclenumberplate;
    Validator validator;
    String picturePath, profImage, strSelectedCategory, strCarCategory, strCategory, selectedMap;
    DatabaseReference requestReference, tripReference, proofstatusref;
    ProgressDialog progressDialog;

    SharedPreferences.Editor editor;

    String[] carcategory;
    JSONObject strJsonCategory;

    Spinner spinCarCategory;
    @ViewById(R.id.profileImage)
    ImageView edtProfileImage;
    @ViewById(R.id.backButton)
    ImageButton backButton;
    @ViewById(R.id.save_button)
    Button saveButton;
    @NotEmpty(message = "Enter first name")
    @ViewById(R.id.edtFirstName)
    EditText inputFirstName;
    @NotEmpty(message = "Enter last name")
    @ViewById(R.id.edtLastName)
    EditText inputLastName;
    @NotEmpty(message = "Enter nick name")
    @ViewById(R.id.edtNick)
    EditText inputNickName;
    @NotEmpty(message = "Enter vehicle make")
    @ViewById(R.id.edit_vehiclemake)
    EditText inputVehiclemake;
    @NotEmpty(message = "Enter vehicle model")
    @ViewById(R.id.edit_vehiclemodel)
    EditText inputVehiclemodel;
    @NotEmpty(message = "Enter vehicle year")
    @ViewById(R.id.edit_vehicleyear)
    EditText inputVehicleyear;
    @NotEmpty(message = "Enter Vehicle License Plate Number")
    @ViewById(R.id.edit_vehiclenumberplate)
    EditText inputVehicleNumberplate;
    @NotEmpty(message = "Enter vehicle mileage")
    @ViewById(R.id.edit_vehiclemileage)
    EditText inputVehiclemileage;
    @NotEmpty
    @ViewById(R.id.edtCountryCode)
    TextView inputCountryCode;
    @NotEmpty
    @ViewById(R.id.edtMobile)
    EditText inputMobileNumber;
    @ViewById(R.id.Referral_code)
    TextView inputReferalcode;
    @ViewById(R.id.edtEmail)
    EditText inputEmail;
    @ViewById(R.id.navradioGroup)
    RadioGroup mapSelectorRadioGroup;
    @ViewById(R.id.googlemap)
    RadioButton googleMapRadioButton;
    @ViewById(R.id.wazemap)
    RadioButton wazeMapRadioButton;
    @ViewById(R.id.inappmap)
    RadioButton inappMapRadioButton;
    @ViewById(R.id.ccp)
    CountryCodePicker ccp;
    ArrayAdapter<String> adapteradapter;
    private Uri fileUri; // file url to store image/video

    @AfterViews
    void settingsActivity() {
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getString("driverid", null);
        selectedMap = prefs.getString("navigationMode", null);

        editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();

        spinCarCategory = (Spinner) findViewById(R.id.car_category);
        //userID="5857f2cdda71b462688b4567";
        LogUtils.i("UserID in settings" + userID);
        validator = new Validator(this);
        validator.setValidationListener(this);
        ccp.setOnCountryChangeListener(this);
        //Change Font to Whole View


        if (selectedMap != null) {
            switch (selectedMap) {
                case "googleMap":
                    googleMapRadioButton.setChecked(true);
                    break;
                case "inAppNavigation":
                    inappMapRadioButton.setChecked(true);
                    break;
                default:
                    wazeMapRadioButton.setChecked(true);
                    break;
            }
        } else {
            wazeMapRadioButton.setChecked(true);
        }

        Intent i = getIntent();
        //strCategory=i.getStringExtra("carcategory");

        //displayDetails();

/*            if(firstName!=null)
        {
            inputFirstName.setText(firstName);
        }

        if(lastName!=null)
        {
            inputLastName.setText(lastName);
        }

        if(email!=null)
        {
            inputEmail.setText(email);
        }

        if(mobileNumber!=null)
        {
            inputMobileNumber.setText(mobileNumber);
        }

        if(countryCode!=null)
        {
            inputCountryCode.setText(countryCode);
        }

        if(profileImage!=null)
        {
            Glide.with(EditProfileActivity.this)
                    .load(profileImage)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .transform(new RoundImageTransform(EditProfileActivity.this))
                    .into(edtProfileImage);
        }*/
        getCarCategory();
        displayDetails();

        getProofStatus();

        spinCarCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                strSelectedCategory = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mapSelectorRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // find which radio button is selected
                if (checkedId == R.id.inappmap) {

                    editor.putString("navigationMode", "inAppNavigation");
                    editor.apply();

                } else if (checkedId == R.id.googlemap) {

                    editor.putString("navigationMode", "googleMap");
                    editor.apply();

                } else {

                    editor.putString("navigationMode", "wazeMap");
                    editor.apply();
                }
            }
        });
    }

    private void getCarCategory() {

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
                                strCarCategory = strJsonCategory.optString("categoryname");
                                LogUtils.d("OUTPUT IS " + strCarCategory);
                                carcategory[0] = "Select car category";
                                carcategory[i + 1] = strCarCategory;
                                LogUtils.i("CATEGORY" + carcategory[i]);
                                adapteradapter = new ArrayAdapter<>(EditProfileActivity.this, R.layout.spinner_item, carcategory);
                                spinCarCategory.setAdapter(adapteradapter);

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    Toast.makeText(getApplicationContext(), "No Intenet Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                } else if (error instanceof ServerError) {
                } else if (error instanceof NetworkError) {

                    Toast.makeText(getApplicationContext(), "No Intenet", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    public void displayDetails() {
        showDialog();
        final String url = Constants.LIVEURL + "editProfile/user_id/" + userID;
        LogUtils.i("RiderProfileURL==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                dismissDialog();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        status = jsonObject.optString("status");
                        message = jsonObject.optString("message");

                        if (status.equals("Success")) {

                            firstName = jsonObject.optString("firstname");
                            lastName = jsonObject.optString("lastname");
                            nickName = jsonObject.optString("nick_name");  ///////nick name check
                            email = jsonObject.optString("email");
                            mobileNumber = jsonObject.optString("mobile");
                            profileImage = jsonObject.optString("profile_pic");
                            countryCode = jsonObject.optString("country_code");
                            referal_code = jsonObject.optString("refrel_code");
                            strCategory = jsonObject.optString("category");
                            vehicleMake = jsonObject.optString("vehicle_make");
                            vehicleModel = jsonObject.optString("vehicle_model");
                            vehicleYear = jsonObject.optString("vehicle_year");
                            vehicleMileage = jsonObject.optString("vehicle_mileage");
                            vehiclenumberplate = jsonObject.optString("number_plate");

//                            savepreferences();

                            try {
                                inputFirstName.setText(firstName.replaceAll("%20", " "));
                                inputLastName.setText(lastName.replaceAll("%20", " "));
                                inputNickName.setText(nickName.replaceAll("%20", " "));
                                inputEmail.setText(email);
                                inputMobileNumber.setText(mobileNumber);
                                inputCountryCode.setText(countryCode);
                                inputReferalcode.setText(referal_code);
                                inputVehiclemileage.setText(vehicleMileage);
                                inputVehicleNumberplate.setText(vehiclenumberplate.replaceAll("%20", " "));

                                inputVehiclemake.setText(vehicleMake.replaceAll("%20", " "));
                                inputVehiclemodel.setText(vehicleModel.replaceAll("%20", " "));
                                inputVehicleyear.setText(vehicleYear);

                                Glide.with(EditProfileActivity.this)
                                        .load(profileImage)
                                        .transform(new RoundImageTransform(EditProfileActivity.this))
                                        .into(edtProfileImage);

                                if (strCategory != null) {

                                    if (!strCategory.equals("null") && !strCategory.equals("")) {
                                        int spinnerPosition = adapteradapter.getPosition(strCategory);
                                        spinCarCategory.setSelection(spinnerPosition);
                                    }
                                }

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        } else {
//                            Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
                            LogUtils.i("inside else");
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        dismissDialog();
                        if (volleyError instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }


    @Click(R.id.edtCountryCode)
    public void countryCode(View view) {
        CountryCodeDialog.openCountryCodeDialog(ccp);//Open country code dialog
    }

    @Click(R.id.profileImage)
    public void updateProfileImage() {
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(EditProfileActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setMessage(getString(R.string.option));

        builder.setNegativeButton(getString(R.string.camera), new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (EditProfileActivity.this.checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        start_camera();
                    } else {
                        dialog.cancel();
                        EditProfileActivity.this.requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 5);
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
                startActivityForResult(i, MEDIA_TYPE_IMAGE);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The Permission is granted to you... Continue your left job...
                start_camera();
            } else {
                Toast.makeText(EditProfileActivity.this, "Please provide permission to use camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void start_camera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image File name");
        fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE);
    }

    @Click(R.id.save_button)
    void saveProfile() {

        validator.validate();
    }

    @Click(R.id.backButton)
    void goBack() {
        Intent i = new Intent(EditProfileActivity.this, SettingActivity_.class);
        startActivity(i);
        finish();
    }

    private void updateProfile() {

        firstName = inputFirstName.getText().toString().trim();
        lastName = inputLastName.getText().toString().trim();
        nickName = inputNickName.getText().toString().trim();
        countryCode = inputCountryCode.getText().toString().trim();
        mobileNumber = inputMobileNumber.getText().toString().trim();
        referal_code = inputReferalcode.getText().toString().trim();
        email = inputEmail.getText().toString().trim();

        vehicleMake = inputVehiclemake.getText().toString().trim();
        vehicleModel = inputVehiclemodel.getText().toString().trim();
        vehicleYear = inputVehicleyear.getText().toString().trim();
        vehiclenumberplate = inputVehicleNumberplate.getText().toString().trim();
        vehiclenumberplate = vehiclenumberplate.replaceAll(" ", "%20");
        vehicleMileage = inputVehiclemileage.getText().toString().trim();
        vehicleMake = vehicleMake.replaceAll(" ", "%20");
        vehicleModel = vehicleModel.replaceAll(" ", "%20");

        firstName = firstName.replaceAll(" ", "%20");
        lastName = lastName.replaceAll(" ", "%20");
        nickName = nickName.replaceAll(" ", "%20");

        strSelectedCategory = strSelectedCategory.replaceAll(" ", "_");
        //Updating the Category in Shared Preferences
        editor.putString("carcategory", strSelectedCategory);
        editor.apply();

        showDialog();
        if (profileImageNew == null || profileImageNew.equals("null")) {
            driverUpdateURL = Constants.LIVEURL + "updateDetails/user_id/" + userID + "/firstname/" + firstName + "/lastname/" + lastName + "/nick_name/" + nickName + "/mobile/" + mobileNumber + "/country_code/" + countryCode + "/city/" + "madurai" + "/email/" + email + "/vehicle_make/" + vehicleMake + "/vehicle_model/" + vehicleModel + "/vehicle_year/" + vehicleYear + "/vehicle_mileage/" + vehicleMileage + "/category/" + strSelectedCategory + "/number_plate/" + vehiclenumberplate;
        } else {
            driverUpdateURL = Constants.LIVEURL + "updateDetails/user_id/" + userID + "/firstname/" + firstName + "/lastname/" + lastName + "/nick_name/" + nickName + "/mobile/" + mobileNumber + "/country_code/" + countryCode + "/profile_pic/" + profileImageNew + "/city/" + "madurai" + "/email/" + email + "/vehicle_make/" + vehicleMake + "/vehicle_model/" + vehicleModel + "/vehicle_year/" + vehicleYear + "/vehicle_mileage/" + vehicleMileage + "/category/" + strSelectedCategory + "/number_plate/" + vehiclenumberplate;
        }
        LogUtils.i("RiderUpdateProfileURL==>" + driverUpdateURL);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(driverUpdateURL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                dismissDialog();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        status = jsonObject.optString("status");
                        message = jsonObject.optString("message");

                        if (status.equals("Success")) {
//                            savepreferences();
                            editor.putBoolean("goOnline", false);
                            editor.apply();
                            removeFireBaseDriverLocationData();
                            Intent intent = new Intent(EditProfileActivity.this, SettingActivity_.class);
                            startActivity(intent);
                            finish();
                        } else {
                            LogUtils.i("else==>");
//                            Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "No internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(5 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    @Override
    public void onCountrySelected() {
        inputCountryCode.setText(ccp.getSelectedCountryCodeWithPlus());
    }

    private boolean validateCountryCode() {

        if (inputCountryCode.getText().toString().trim().length() == 0) {
            inputCountryCode.setError("");
            inputCountryCode.setError(getString(R.string.enter_valid_cc));
            return false;
        } else if (inputCountryCode.getText().toString().equals("CC")) {
            inputCountryCode.setError("");
            inputCountryCode.setError(getString(R.string.enter_valid_cc));
            return false;
        } else {
            inputCountryCode.setError(null);
        }
        //equestFocus(countrycode);

        return true;
    }

    private boolean validatePhone() {
        if (inputMobileNumber.getText().toString().trim().isEmpty()) {
            inputMobileNumber.setError(getString(R.string.enter_mobile_number));
            return false;
        } else if (inputCountryCode.getText().toString().trim().isEmpty()) {
            inputMobileNumber.setError(getString(R.string.enter_valid_cc));
            return false;
        } else if (!inputMobileNumber.getText().toString().trim().isEmpty()) {
            if (inputMobileNumber.getText().toString().substring(0, 1).matches("0")) {
                inputMobileNumber.setError("Enter a valid number");
                return false;
            } else {
                int maxLengthofEditText = 15;
                inputMobileNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLengthofEditText)});
                inputMobileNumber.setError(null);
            }
            return true;
        }

        return true;
    }

    private boolean validateUsing_libphonenumber() {
        countryCode = inputCountryCode.getText().toString();
        mobileNumber = inputMobileNumber.getText().toString();
        if (validatePhone() && validateCountryCode()) {
            LogUtils.i("CountryCode==>" + countryCode);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                countryCode = countryCode.replace("+", "");
            }
            LogUtils.i("SDK_VERSION==>" + Build.VERSION.SDK_INT);
            LogUtils.i("SDK_VERSION_RELEASE" + Build.VERSION.RELEASE);
            LogUtils.i("CountryCode1==>" + countryCode);
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
            Phonenumber.PhoneNumber phoneNumber = null;

            try {
                //phoneNumber = phoneNumberUtil.parse(phNumber, "IN");  //if you want to pass region code
                phoneNumber = phoneNumberUtil.parse(mobileNumber, isoCode);
            } catch (NumberParseException e) {
                e.printStackTrace();
            }

            boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
            if (isValid) {
                if (phoneNumber != null) {
                    String internationalFormat = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                }
                return true;
            } else {
                inputMobileNumber.setError(getString(R.string.enter_a_valid_mobile_number));
                return false;
            }
        }
        return true;
    }

    @Override
    public void onValidationSucceeded() {


        if (!validateCountryCode()) {

        } else if (!validatePhone()) {

        } else if (!validateUsing_libphonenumber()) {
            inputMobileNumber.setError(getString(R.string.invalid_mobile_number));
        } else {
            LogUtils.i("Category" + strSelectedCategory);
            if (strSelectedCategory != null && !strSelectedCategory.matches("Select car category")) {
                updateProfile();
            } else {
                Toast.makeText(this, "Please select your Category", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "All fields are required", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_CAPTURE_IMAGE && resultCode == RESULT_OK) {

            picturePath = getRealPathFromURI(fileUri);

            edtProfileImage.setScaleType(ImageView.ScaleType.FIT_XY);
//                edtProfileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            Glide.with(EditProfileActivity.this)
                    .load(picturePath)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .transform(new RoundImageTransform(EditProfileActivity.this))
                    .into(edtProfileImage);

            new ImageuploadTask(this).execute();

        } /*else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {*/ else if (requestCode == MEDIA_TYPE_IMAGE && resultCode == RESULT_OK && null != data) {

//            String single_path = data.getStringExtra("single_path");
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

          /*  if (selectedImage != null && !selectedImage.toString().equals("null")) {
                picturePath = getRealPathFromURI(selectedImage);
            } else {
                picturePath = "";
            }*/
//            edtProfileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            Glide.with(EditProfileActivity.this)
                    .load(picturePath)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .transform(new RoundImageTransform(EditProfileActivity.this))
                    .into(edtProfileImage);
            new ImageuploadTask(EditProfileActivity.this).execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (fileUri == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Image File name");
            fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    // Save the activity state when it's going to stop.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("picUri", fileUri);
    }

    // Recover the saved state when the activity is recreated.
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        fileUri = savedInstanceState.getParcelable("picUri");
    }


    public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return contentUri.getPath();
        }
    }

    protected void Upload_Server() {
        // TODO Auto-generated method stub
        LogUtils.i("After call progress");
        try {

            HttpURLConnection connection;
            DataOutputStream outputStream;

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
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            LogUtils.i("image" + serverResponseMessage);

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            DataInputStream inputStream1;
            inputStream1 = new DataInputStream(connection.getInputStream());
            String str;
            String Str1_imageurl = "";

            while ((str = inputStream1.readLine()) != null) {
                LogUtils.e("Debug " + "Server Response " + str);

                Str1_imageurl = str;
                LogUtils.e("Debug " + "Server Response String imageurl" + str);
            }
            inputStream1.close();
            LogUtils.i("image url" + Str1_imageurl);

            //get the image url and store
            profImage = Str1_imageurl.trim();
            JSONArray array = new JSONArray(profImage);
            JSONObject jsonObj = array.getJSONObject(0);
            LogUtils.i("image name" + jsonObj.getString("image_name"));

            profileImageNew = jsonObj.optString("image_name");

            LogUtils.i("Profile Picture Path" + profImage);
        } catch (Exception e) {

            e.printStackTrace();
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(EditProfileActivity.this, SettingActivity_.class);
        startActivity(intent);
    }

    public void removeFireBaseDriverLocationData() {

        if (userID != null && !userID.isEmpty()) {
            //Get datasnapshot at your "users" root node
            strCategory = strCategory.replaceAll("%20", " ");
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("drivers_location").child(strCategory).child(userID);

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        //Get map of users in datasnapshot
                        //GetDriverData((Map<String,Object>) dataSnapshot.getValue());
                        ref.removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //handle databaseError
                }
            });

            ref.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    LogUtils.i("DATA DELETE SUCCESSFULLY");
                    insertFireBaseDriverLocationData();
                    if (databaseError != null) {
                        LogUtils.i("DATA DELETE SUCCESSFULLY WITH ERROR");
                    }
                }
            });
        }
    }

    public void insertFireBaseDriverLocationData() {

        if (userID != null && !userID.isEmpty()) {

            strSelectedCategory = strSelectedCategory.replaceAll("%20", " ");
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("drivers_location").child(strSelectedCategory);
            //DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverID);

            final GeoLocation location = new GeoLocation(0.0, 0.0);

            GeoHash geoHash = new GeoHash(location);

            Map<String, Object> updateaccept = new HashMap<String, Object>();
            updateaccept.put("g", geoHash.getGeoHashString());
            updateaccept.put("l", Arrays.asList(location.latitude, location.longitude));

            ref.child(userID).setValue(updateaccept, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    LogUtils.i("DATA SAVED SUCCESSFULLY");

                    if (databaseError != null) {
                        LogUtils.i("DATA SAVED SUCCESSFULLY WITH ERROR");
                    }
                }
            });
        }
    }

    public void getProofStatus() {
        LogUtils.i("Driver Id In Proof" + userID);
        proofstatusref = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(userID).child("proof_status");
        proofstatusref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String proofstatus = dataSnapshot.getValue().toString();
                    LogUtils.i("Driver ProofStatus " + proofstatus);
                    if (!proofstatus.isEmpty() && proofstatus.length() != 0) {
                        if (proofstatus.matches("Accepted")) {

                            inputFirstName.setEnabled(false);
                            inputLastName.setEnabled(false);
                            //inputNickName.setEnabled(false);
                            inputEmail.setEnabled(false);
                            LogUtils.i("enter eteed ");


                        } else {

                            inputFirstName.setEnabled(true);
                            inputLastName.setEnabled(true);
                            inputNickName.setEnabled(true);
                            inputEmail.setEnabled(true);

                            LogUtils.i("enter eteed gfhfhf");
                        }
                    }
                } else {

                    inputFirstName.setEnabled(true);
                    inputLastName.setEnabled(true);
                    inputNickName.setEnabled(true);
                    inputEmail.setEnabled(true);
                    LogUtils.i("enter eteed gfg");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        private EditProfileActivity activity;
        private Context context;

        ImageuploadTask(EditProfileActivity activity) {
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
            dialog.show();
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
                LogUtils.e("Schedule " + "UpdateSchedule failed " + e);
                return false;
            }
        }
    }
}