package com.tommy.driver;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.tommy.driver.adapter.AppController;
import com.tommy.driver.adapter.Constants;
import com.tommy.driver.adapter.FontChangeCrawler;
import com.tommy.driver.utils.LogUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@EActivity(R.layout.activity_launch)
public class LaunchActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    GoogleSignInOptions gso;
    GoogleApiClient mGoogleApiClient;
    boolean doubleBackToExitPressedOnce = false;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    Bundle parameters;
    String driverID, driverFirstName, driverLastName, driverEmail, driverMobile, carcategory;
    String fbEmail, fbFullName, fbFirstName, fbLastName, fbUserProfile, fbID, fbToken, driverId;
    String googleEmail, googleFirstName, googleLastName, googleUserProfile, googleID, googleIDToken;
    private int GOOGLE_SIGN_IN = 100;
    //Facebook Declaration
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    @AfterViews
    void launchActivity() {
        //Change Font to Whole View
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        driverId = prefs.getString("driverid", null);
        LogUtils.i("driverid" + driverId);

        Intent intent = getIntent();
        String istimeout = intent.getStringExtra("timeout");

        if (istimeout != null) {
            android.app.AlertDialog.Builder builder =
                    new AlertDialog.Builder(LaunchActivity.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.timeout);
            builder.setMessage(R.string.time_out_msg);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }

            });


            builder.show();
        }

        if (driverId != null) {
            Intent i = new Intent(getApplicationContext(), Map_Activity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }


        editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                //.requestIdToken(Constants.WEB_CLIENT_ID)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //Facebook Initialize
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayMessage(newProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        getFbKeyHash("com.tommy.driver");

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        if (Profile.getCurrentProfile() == null) {
                            profileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                    //LogUtils.i("facebook - profile2 "+profile2.getFirstName());
                                    displayMessage(profile2);
                                    profileTracker.stopTracking();
                                }
                            };
                            profileTracker.startTracking();
                        } else {
                            Profile profile = Profile.getCurrentProfile();
                            displayMessage(profile);
                            //LogUtils.i("facebook - profile - "+profile.getFirstName());
                        }

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {

                                        if (object != null) {
                                            LogUtils.i("jsonobject" + object);

                                            fbEmail = object.optString("email");
                                            fbFullName = object.optString("name");
                                            fbID = object.optString("id");
                                            LogUtils.i("fb email id===>" + fbEmail);

                                            if (fbEmail == null) {
                                                fbEmail = "Nill";
                                            } else if (fbEmail.equals("")) {
                                                fbEmail = "Nill";
                                            }
                                            faceBoookLogin();
                                        }
                                    }
                                });

                        parameters = new Bundle();
                        parameters.putString("fields", "id,name,link,gender,birthday,email");
                        request.setParameters(parameters);
                        request.executeAsync();

                        Bundle bundle = new Bundle();
                        bundle.putString("fields", "token_for_business");
                    }

                    @Override
                    public void onCancel() {
                        LogUtils.i("Facebook Login failed!!");
                        //Toast.makeText(EnrollActivity.this, "Login Cancelled by user!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException e) {
                        LogUtils.d("Facebooksdk " + "Login with Facebook failure " + e);
                        Toast.makeText(LaunchActivity.this, "An unknown network error has occured", Toast.LENGTH_LONG).show();
                        LogUtils.i("Facebook Login failed!!");
                    }
                });
    }

    private void faceBoookLogin() {
        LoginManager.getInstance().logOut();
        checkFB();
    }

    public void getFbKeyHash(String packageName) {

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                LogUtils.i("hash key value" + something);
                LogUtils.e("hash key - " + something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            LogUtils.e("name not found " + e1.toString());
        } catch (NoSuchAlgorithmException e) {
            LogUtils.e("no such an algorithm " + e.toString());
        } catch (Exception e) {
            LogUtils.e("exception " + e.toString());
        }

    }

    private void displayMessage(Profile profile) {
        if (profile != null) {
            //Toast.makeText(getApplicationContext(),"displaymessage",Toast.LENGTH_LONG).show();
            fbFullName = profile.getName();
            fbFirstName = profile.getFirstName();
            fbLastName = profile.getLastName();
            fbID = profile.getId();
            fbUserProfile = profile.getProfilePictureUri(100, 100).toString();


            //String email1= this.getIntent().getExtras().getString("fields");
            //LogUtils.i("bundle email"+email1);
            LogUtils.i("Facebook Fullname" + fbFullName);
            LogUtils.i("Facebook Firstname" + fbFirstName);
            LogUtils.i("Facebook Lastname" + fbLastName);
            LogUtils.i("Facebook Profile" + fbUserProfile);
            LogUtils.i("Facebook ID" + fbID);
            LogUtils.i("Facebook Email" + fbEmail);
            LogUtils.i("Facebook Access Token" + fbToken);
            //call webservice

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (data != null) {
            if (requestCode == GOOGLE_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            } else {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }

    }

    private void handleSignInResult(GoogleSignInResult result) {

        LogUtils.d("handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            // Toast.makeText(this,"Successfully Logged in "+acct.getDisplayName(),Toast.LENGTH_SHORT).show();
            //googleFullName = acct.getDisplayName();
            googleFirstName = acct.getGivenName();
            googleLastName = acct.getFamilyName();
            googleEmail = acct.getEmail();
            googleUserProfile = String.valueOf(acct.getPhotoUrl());
            googleUserProfile = googleUserProfile.replaceAll("/", "-__-");
            googleID = acct.getId();
            googleIDToken = acct.getIdToken();
            googleSignOut();
            callGoogleSignUp();
        } else {
            // Signed out, show unauthenticated UI.

        }
    }

    private void callGoogleSignUp() {

        checkGoogle();
    }


    private void googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
//                        Toast.makeText(getApplicationContext(),"Logged out Successfully",Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();

            this.finishAffinity();
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
            System.exit(0);
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit the app", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Click({R.id.signin_button})
    void signinPage() {
        Intent signin = new Intent(this, SigninActivity_.class);
        startActivity(signin);
        finish();
    }


    @Click({R.id.register_button})
    void registerPage() {
        Intent signin = new Intent(this, SignUp_Activity_.class);
        startActivity(signin);
    }

    @Click(R.id.google_button)
    void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Click(R.id.facebook_button)
    public void fblogin() {
        LoginManager.getInstance().logOut();//Logout Facebook
        LoginManager.getInstance().logInWithReadPermissions(LaunchActivity.this, Arrays.asList("public_profile", "email"));
    }

    private void checkFB() {
        showProgress(getString(R.string.fb_msg));

        try {
            fbUserProfile = URLEncoder.encode(fbUserProfile, "utf-8");
        } catch (UnsupportedEncodingException | NullPointerException e) {
            LogUtils.i("Facebook Exception " + e.toString());
            e.printStackTrace();
        }

        final String url = Constants.LIVEURL + "FBExist/fb_id/" + fbID;
        //final String url= Constants.LIVEURL+"fbSignup/"+"first_name/"+fbFirstName+"/last_name/"+fbLastName+"/email/"+fbEmail+"/regid/344444444444444"+"/fb_id/"+fbID+"/license/"+""+"/insurance/"+"";
        LogUtils.i("Driver SignUp URL==>" + url);
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        if (!isFinishing())
                            progressDialog.dismiss();
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject register_status = response.getJSONObject(i);
                                if (register_status.optString("status_extra").matches("Exist")) {

                                    driverID = register_status.optString("userid");
                                    driverFirstName = register_status.optString("first_name");
                                    driverLastName = register_status.optString("last_name");
                                    driverEmail = register_status.optString("email");
                                    driverMobile = register_status.optString("mobile");
                                    carcategory = register_status.optString("category");
                                    savepreferences();

                                    Intent map = new Intent(getApplicationContext(), Map_Activity.class);
                                    map.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(map);
                                    finish();
                                    Toast.makeText(LaunchActivity.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();
                                } else {

                                    LogUtils.i("fb email id===>" + fbEmail);

                                    Intent intent = new Intent(LaunchActivity.this, DocUpload_Activity_.class);
                                    intent.putExtra("FirstName", fbFirstName);
                                    intent.putExtra("LastName", fbLastName);
                                    intent.putExtra("Email", fbEmail);
                                    intent.putExtra("FbID", fbID);
                                    intent.putExtra("ProfilePicture", fbUserProfile);
                                    intent.putExtra("Comingfrom", "facebook");

                                    startActivity(intent);
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
                if (!isFinishing())
                    progressDialog.dismiss();
                //protected static final String TAG = null;
                if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    // stopAnim();
                    // Toast.makeText(DocUpload_Activity.this, "An unknown network error has occured", Toast.LENGTH_SHORT).show();
                }
                LogUtils.d("Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    private void checkGoogle() {
        showProgress(getString(R.string.google_msg));

        String url = Constants.LIVEURL + "GBExist/google_id/" + googleID;

        LogUtils.i("Driver SignUp URL==>" + url);
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        if (!isFinishing())
                            progressDialog.dismiss();
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject register_status = response.getJSONObject(i);

                                if (register_status.optString("status").matches("Fail")) {

                                    Intent intent = new Intent(LaunchActivity.this, DocUpload_Activity_.class);
                                    intent.putExtra("FirstName", googleFirstName);
                                    intent.putExtra("LastName", googleLastName);
                                    intent.putExtra("Email", googleEmail);
                                    intent.putExtra("GoogleID", googleID);
                                    intent.putExtra("ProfilePicture", googleUserProfile);
                                    intent.putExtra("Comingfrom", "google");

                                    startActivity(intent);
                                } else {
                                    driverID = register_status.optString("userid");
                                    driverFirstName = register_status.optString("first_name");
                                    driverLastName = register_status.optString("last_name");
                                    driverEmail = register_status.optString("email");
                                    driverMobile = register_status.optString("mobile");
                                    carcategory = register_status.optString("category");
                                    savepreferences();

                                    Intent map = new Intent(getApplicationContext(), Map_Activity.class);
                                    map.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(map);
                                    finish();
                                    Toast.makeText(LaunchActivity.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();
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
                if (!isFinishing())
                    progressDialog.dismiss();
                //protected static final String TAG = null;
                if (error instanceof NoConnectionError) {
                    LogUtils.i("NoConnectionError");
                    // stopAnim();
                    //  Toast.makeText(DocUpload_Activity.this, "An unknown network error has occured", Toast.LENGTH_SHORT).show();
                }
                LogUtils.d("Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    public void savepreferences() {
        editor.putString("driverid", driverID);
        editor.putString("drivername", driverFirstName);
        editor.putString("driverphonenum", driverMobile);
        editor.putString("carcategory", carcategory);
        editor.apply();
    }

    //show progress

    public void showProgress(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (!LaunchActivity.this.isFinishing()) {
            progressDialog.show();
        }
    }
}