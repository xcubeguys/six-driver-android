package com.tommy.driver;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.androidadvance.topsnackbar.TSnackbar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.tommy.driver.adapter.AppController;
import com.tommy.driver.adapter.Constants;
import com.tommy.driver.adapter.Contributor;
import com.tommy.driver.adapter.DirectionsJSONParser;
import com.tommy.driver.adapter.EasyTimer;
import com.tommy.driver.adapter.FontChangeCrawler;
import com.tommy.driver.adapter.RetrofitArrayAPI;
import com.tommy.driver.adapter.RoundImageTransform;
import com.tommy.driver.adapter.Services;
import com.tommy.driver.services.GoogleLocationService;
import com.tommy.driver.services.LocationUpdateListener;
import com.tommy.driver.services.WidgetService;
import com.tommy.driver.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.maps.android.PolyUtil.isLocationOnPath;

public class Map_Activity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationSource,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GeoQueryEventListener, ValueEventListener, DirectionCallback, View.OnClickListener, android.location.LocationListener {

    // Keys for storing activity state in the Bundle.
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int CUSTOM_OVERLAY_PERMISSION_REQUEST_CODE = 101;
    public static DatabaseReference canceltripreference;
    public static boolean excecuteonce = false;
    public static boolean excecutecancelonce = false;
    static boolean distanceStatus;
    static double distance = 0;
    static long startTime, endTime;
    static int p = 1;
    static String sumofamount;
    static DatabaseReference tollfeereference;
    static boolean active = false;
    static String strTotalDistance = "0";
    //Smart location
    final Handler handler = new Handler();
    final Handler ttsHandler = new Handler();
    public String riderFirstName, riderLastName, status, message, ridermobile, riderFirstNameService;
    public Map<LatLng, Marker> tollMarkersMap;
    public Map<LatLng, Marker> wayPaintsMarker;
    public Map<Integer, Polyline> wayPaintsPolyline;
    protected LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    LocationService myService;
    String tripState, onlinecheck, ratingcheck = "null", previous_final_instruct = "";
    LocationManager locationManager;
    Location mCurrentLocation, lStart, lEnd;
    double speed;
    Double routeLat, routeLng;
    ImageView mute, volon;
    int previousToll = 0;
    double k, surgeaddamount;
    String strDistanceBegin, currentDateTimeString, endAddress, endLat, endLng;
    FlexibleRatingBar flexibleRatingBar;
    String ratingInt;
    String selectedDrawable;
    TextToSpeech textToSpeech;
    List<Step> step;
    Drawable tickDrawable;
    double airportamt = 0.0;
    Bitmap mapBitmap;
    ArrayList<LatLng> MarkerPoints;
    ObjectAnimator animY;
    DatabaseReference requestReference;
    DatabaseReference tripReference, ridercanceltripreference;
    DatabaseReference proofstatusref;
    DatabaseReference faretoolreference;
    int waypointcount = 0;
    float previousBearing = 0;
    List<LatLng> latlngs;
    int mapPosition = 0;
    boolean haslatlng;
    String tempTripWithName, google_api_key;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    SharedPreferences.Editor editor, getState;
    SharedPreferences state, prefs;
    Dialog d, dialogTripSummary, dialog;
    RelativeLayout onlineLay, toolbarLayout;
    String driverId = null, drivername;
    FirebaseApp app;
    GeoFire geoFire;
    DatabaseReference ratingReference, ratingRef, checkAccepRef, checkTripCancelRef, rideridreference, droplocationRef,
            pickupTerminalRef, tripsdataRef, multidestref;
    ValueEventListener listener, tripListener, ratingListener, canceltripslistener, statusListener, droplocationlistener,
            pickupTerminalListen, tripsdataListen, multidestlis;
    BottomBar bottomBar;
    //EasyTimer audioTimer= new EasyTimer(3000);
    CardView navcard;
    EasyTimer easyTimer = new EasyTimer(2000);
    RelativeLayout startTripLayout, arriveNowLayout, endTripLayout, destinationLayout;
    TextView onlineTxt, txtRiderName, txtRiderDestination, txtRiderName_Begin, txtEndTrip, toll_pay, navTxt;
    // location accuracy settings
    Button btnArriveNow, btnEndTrip;
    TextView pickuploc, droploc, progeta, progDistance, progestfare;
    Marker currentLocMarker, pickUPrDropMarker;
    Polyline routePolyline, mulPoly;
    LatLng destLocation, orginLocation, navigationLatLng;
    ImageButton riderinfo, riderinfoinarrived, riderinfoinarrived1;
    Button btnStartTrip;
    String reqID, tripID, strLat, strLng, tripIDWithName, pickLatLng;
    String strsetValue, tripStatus, strTotalPrice;
    RelativeLayout progressLayout1, progresslayout, FAB, routeNavigate;
    ProgressWheel pwOne;
    ImageView requestMapView;
    View mapView;
    String tollfee, totalprice;
    String riderID, strsetdestination, strRiderProfile, strCategory, strPickupAddress;
    ArrayList<String> tripIDs = new ArrayList<>();
    ArrayList<String> tollfrees = new ArrayList<>();
    HashMap<String, String> newMap = new HashMap<>();
    boolean doubleBackToExitPressedOnce = false;
    ProgressDialog progressDialog;
    String strWebprice_km, strwebpricepermin, strwebmaxsize, strwebpricefare, strCacnelStatus, strbookfee, strairportfee,
            strtaxpercentage, strDropPrice;
    TextView trip_rider_name, txtTotalDistance, txtTripAmount, txtTripdate, companyname, companyfee;
    ImageView imgRiderProfile;
    LinearLayout dynamic, dynamic1, dynamic2;
    TextView current_rider, current_rider1, current_rider2;
    BitmapDescriptor mapCarIcon;
    LatLng latLng, removingMarkerPoint;
    LatLng prevLatLng = new LatLng(0, 0);
    MediaPlayer requstingTone = new MediaPlayer();

    String surgePrice, driverCompanyName, driverCompanyFee;

    Ringtone r;
    boolean volume = false;
    JSONObject jsonTollObject = null;
    Date startDate, endDate;
    long onlineDuration;
    int n = 5;//Total Category
    String[] strCategoryName = new String[n];
    ArrayList<LatLng> points = null;
    int ERP_SKIP_COUNT = 0;
    private GoogleMap mMap;
    private OnLocationChangedListener mMapLocationListener = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     * rank list US Is india regnonized
     */
    private GoogleApiClient client;
    private GoogleLocationService googleLocationService;
    private DatabaseReference keyRef;
    private ValueEventListener tripRefValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
                if (!dataSnapshot.getValue().toString().equals("0")) {
                    //tripID = dataSnapshot.getValue().toString();
                    tripIDWithName = dataSnapshot.getValue().toString();
                    LogUtils.i("THE TRIP ID FROM FBASE" + tripIDWithName);

                    if (tripIDWithName.isEmpty() && tripIDWithName.length() == 0) {

                        if (tripState.matches("btnendClicked")) {
                        } else {
                            showProgressDialog();
                        }

                    } else if (tripIDWithName.matches("empty") || tripIDWithName.equals("0")) {
                        dismissProgressDialog();
                    } else {
                        //tripIDs.add("58b7a6bbda71b4437a8b4567"+":"+riderFirstName);
                        //tripIDs.add("58b7a6e4da71b4817b8b4567"+":"+riderFirstName);

                        //tripIDs.add("58ad92c3da71b47d5f8b4567"+":"+"name2");
                        tripIDWithName = dataSnapshot.getValue().toString();
                        String[] tripidArray = tripIDWithName.split(":");
                        String Tripid = tripidArray[0];
                        riderFirstName = tripidArray[1].replaceAll("%20", " ");

                        tripID = Tripid;


                        LogUtils.i("Trips List Firebase before" + tripIDs + "SIZE" + tripIDs.size());
                        LogUtils.i("TripID Firebase" + tripID);
                        LogUtils.i("TripID riderFirstName" + riderFirstNameService);
                               /* if(riderFirstName==null || riderFirstName.isEmpty() || riderFirstName.equals("null"))
                                {
                                    LogUtils.i("Rider ID"+riderID);
                                    if(riderID!=null){
                                        getRiderName(riderID);
                                    }
                                    riderFirstName=riderFirstNameService;
                                    LogUtils.i("Insiide Service" + riderFirstNameService);
                                }*/

                        if (tripIDs.size() == 0) {
                            tripIDs.add(tripIDWithName);
                                   /* Collections.reverse(tripIDs);*/
                        } else {

                            if (!tripIDs.contains(tripIDWithName)) {
                                //if(!tripIDs.contains(tripID)){
                                tripIDs.add(tripIDWithName);
                                //Collections.reverse(tripIDs);

                                LogUtils.i("trip id inside===>" + tripID);
                                LogUtils.i("trip id inside===>" + riderFirstName);
                                LogUtils.i("trip id inside===>" + tripIDs);
                            } else {
                                LogUtils.i("trip id outside===>" + tripID);
                            }
                        }
                        LogUtils.i("Trips List Firebase after" + tripIDs);

                        Set<String> hs = new LinkedHashSet<>();
                        hs.addAll(tripIDs);
                        tripIDs.clear();
                        tripIDs.addAll(hs);
                        LogUtils.i("before Trips List" + tripIDs);
                                /*Collections.reverse(tripIDs);*/
                        LogUtils.i("after Trips List" + tripIDs);

                        //                        LogUtils.i("Trips List Firebase cleared" + tripIDs.get(0));

                        newMap.put(riderFirstName, tripID);
                        LogUtils.i("Trips List" + tripIDs);
                        LogUtils.i("Trips List" + tripIDs.size());
                        LogUtils.i("Trips hash map List" + newMap);

                        createDistanceFireBase();

                        saveArray(tripIDs, "tripidarray", getApplicationContext());

                        if (!tripID.equals("empty")) {
                            getState.putString("tripID", tripID);
                            getState.apply();
                        }

                        getstatusfromfirebase(tripID);
                        createRidersNameList();
                        dismissProgressDialog();
                    }
                } else {
                    dismissProgressDialog();
                }

            } else {
                showProgressDialog();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private static float getRotate(LatLng curPos, LatLng nextPos) {
        double x1 = curPos.latitude;
        double x2 = nextPos.latitude;
        double y1 = curPos.longitude;
        double y2 = nextPos.longitude;

        return (float) (Math.atan2(y2 - y1, x2 - x1) / Math.PI * 180);
    }

    public static StateListDrawable selectorBackgroundColor(Context context
            , int normal, int pressed) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_selected},
                new ColorDrawable(pressed));
        states.addState(new int[]{}, new ColorDrawable(normal));
        return states;
    }

    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view, String markerCount) {

        TextView markerCountTxt = (TextView) view.findViewById(R.id.num_txt);
        markerCountTxt.setText(markerCount);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    @SuppressLint("UseSparseArrays")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        initializeTTS();

        LogUtils.i("distance in oncreate" + strTotalDistance);

        // setup markers
        this.tollMarkersMap = new HashMap<>();
        this.wayPaintsMarker = new HashMap<>();
        this.wayPaintsPolyline = new HashMap<>();

        tickDrawable = getApplicationContext().getResources().getDrawable(R.drawable.check);
        onlineLay = (RelativeLayout) findViewById(R.id.onlinelay);
        progressLayout1 = (RelativeLayout) findViewById(R.id.progress_layout);
        onlineTxt = (TextView) findViewById(R.id.onlineTxt);
        toll_pay = (TextView) findViewById(R.id.toll_pay);
        navcard = (CardView) findViewById(R.id.card_view_navigation);
        navTxt = (TextView) findViewById(R.id.txtnavigation);
        editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();
        getState = getSharedPreferences(Constants.MY_STATE_KEY, MODE_PRIVATE).edit();
        state = getSharedPreferences(Constants.MY_STATE_KEY, MODE_PRIVATE);
        tripState = state.getString("tripstate", null);

        if (tripState == null) {
            //set end click to get request first time
            tripState = "endClicked";

           /* getState.putString("tripstate", tripState);
            getState.apply();*/
        }
        LogUtils.i("Tripstate" + tripState);
        Constants.MAP_ISSHOWING = true;

        startService(new Intent(getBaseContext(), Services.class).setPackage(this.getPackageName()));
        //Getting Google API Key
        getKeys();
        loadTollJSONFromAsset();
        //txtPickUp=(TextView)findViewById(R.id.txtPickUp_Begin);
        //   rateBar=(RatingBar)findViewById(R.id.ratingBar);
        toolbarLayout = (RelativeLayout) findViewById(R.id.toolbar);
        destinationLayout = (RelativeLayout) findViewById(R.id.destinationLayout);
        txtRiderDestination = (TextView) findViewById(R.id.txtDestination);
        txtRiderDestination.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txtRiderDestination.setSelected(true);
        arriveNowLayout = (RelativeLayout) findViewById(R.id.arrive_layout);
        txtRiderName = (TextView) findViewById(R.id.txtRiderName);
        btnArriveNow = (Button) findViewById(R.id.btnArriveNow);
        startTripLayout = (RelativeLayout) findViewById(R.id.begin_start__layout);
        txtRiderName_Begin = (TextView) findViewById(R.id.txtRiderName_StartTrip);
        btnStartTrip = (Button) findViewById(R.id.btnStartTrip);
        endTripLayout = (RelativeLayout) findViewById(R.id.end_trip__layout);
        txtEndTrip = (TextView) findViewById(R.id.txtRiderName_EndTrip);
        btnEndTrip = (Button) findViewById(R.id.btnEndTrip);
        riderinfo = (ImageButton) findViewById(R.id.riderinfo);
        riderinfoinarrived = (ImageButton) findViewById(R.id.riderinfoinarrived);
        riderinfoinarrived1 = (ImageButton) findViewById(R.id.riderinfoinarrived1);
        mute = (ImageView) findViewById(R.id.voloff);
        volon = (ImageView) findViewById(R.id.volon);

        dynamic = (LinearLayout) findViewById(R.id.dynamic);
        dynamic1 = (LinearLayout) findViewById(R.id.dynamic1);
        dynamic2 = (LinearLayout) findViewById(R.id.dynamic2);

        current_rider = (TextView) findViewById(R.id.current_rider);
        current_rider1 = (TextView) findViewById(R.id.current_rider1);
        current_rider2 = (TextView) findViewById(R.id.current_rider2);


        prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        driverId = prefs.getString("driverid", null);
        drivername = prefs.getString("drivername", null);
        strCategory = prefs.getString("carcategory", null);
        LogUtils.i("driverid In MAPP" + driverId);
        LogUtils.i("driverNAMEand CAR In MAPP" + drivername + strCategory);

        SharedPreferences onlinecheckprefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        onlinecheck = onlinecheckprefs.getString("onlinestatus", null);

        mapCarIcon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_standard);

        if (driverId != null)
            checkphonenumber();

        getOnlineStatusFB();
        getCarCategoryrDetails();
        //getOnlineStatus(onlinecheck);

        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.getCurrentTab().setPadding(0, 5, 0, 0);
        bottomBar.setInActiveTabColor(Color.BLACK);


        // setup GeoFire with category
        if (strCategory != null && !strCategory.isEmpty()) {
            strCategory = strCategory.replaceAll("%20", " ");
            geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("drivers_location").child(strCategory));
        } else {
            geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("drivers_location"));
        }

        LogUtils.i("Category user " + strCategory);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled) {

            GPSTurnOnAlert();
        }

        //Start getting Smart Location
        updateSmartLocation();
        //runSmartLocation();

        btnArriveNow.setOnClickListener(new View.OnClickListener()

        {

            @Override
            public void onClick(View v) {
                try {
                    getState.putString("tripstate", "arriveClicked");
                    getState.apply();

                    tripState = "arriveClicked";
                    arriveNowLayout.setVisibility(View.GONE);
                    startTripLayout.setVisibility(View.VISIBLE);
                    destinationLayout.setVisibility(View.VISIBLE);

                    // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId).child("accept");
                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID);
                    Map<String, Object> taskMap = new HashMap<>();
                    taskMap.put("status", "2");
                    ///databaseReference.updateChildren(taskMap);
                    databaseReference2.updateChildren(taskMap);
                    updateArriveRequest();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_profile) {

                    Intent intent = new Intent(Map_Activity.this, SettingActivity_.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_profile) {

                    Intent intent = new Intent(Map_Activity.this, SettingActivity_.class);
                    startActivity(intent);
                    finish();
                }

                if (tabId == R.id.tab_rating) {

                    Intent intent = new Intent(Map_Activity.this, RatingActivity_.class);
                    startActivity(intent);
                }

                if (tabId == R.id.tab_earning) {

                    // Intent intent = new Intent(Map_Activity.this, EarningActivity_.class);
                    Intent intent = new Intent(Map_Activity.this, EarningTabActivity.class);
                    startActivity(intent);
                }

                if (tabId == R.id.trip_history) {
                    startActivity(new Intent(Map_Activity.this, YourTripsActivity_.class));
                }

            }
        }, false);

        FAB = (RelativeLayout) findViewById(R.id.myLocationButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mCurrentLocation != null) {

                    latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

                    float zoomPosition;
                    if (tripState == null || tripState.matches("endClicked") || tripState.matches("btnendClicked"))
                        zoomPosition = Constants.MAP_ZOOM_SIZE;
                    else
                        zoomPosition = Constants.MAP_ZOOM_SIZE_ONTRIP;

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng)                              // Sets the center of the map to current location
                            .zoom(zoomPosition)
                            .tilt(0)                                     // Sets the tilt of the camera to 0 degrees
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                }
            }
        });

        //set navigation volume on/off
        volon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volon.setVisibility(View.GONE);
                mute.setVisibility(View.VISIBLE);
                initializeTTS();

                editor.putBoolean("getvolume", true);
                editor.apply();
            }
        });

        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mute.setVisibility(View.GONE);
                volon.setVisibility(View.VISIBLE);
                stopTTS();
                editor.putBoolean("getvolume", false);
                editor.apply();
            }
        });

        routeNavigate = (RelativeLayout) findViewById(R.id.myNavigationButton);

        routeNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkOverlayPermission();

            }
        });
/*

        routeNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    routeLat=navigationLatLng.latitude;
                    routeLng= navigationLatLng.longitude;
                } catch (Exception e) {
                    e.printStackTrace();
                    routeLat=0.0;
                    routeLng=0.0;
                }


                dialog = new Dialog(Map_Activity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.navigation_dialog);
                FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
                fontChanger.replaceFonts((ViewGroup) dialog.findViewById(android.R.id.content));
                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.setCancelable(true);

                final TextView googleMap = (TextView) dialog.findViewById(R.id.text_google_map);
                final TextView wazeMap= (TextView) dialog.findViewById(R.id.text_waze);
                final TextView inAppNavigation= (TextView) dialog.findViewById(R.id.text_inapp);

                selectedDrawable=prefs.getString("navigationMode",null);
                if(selectedDrawable!=null)
                {
                    if(selectedDrawable.equals("googleMap"))
                    {
                        googleMap.setCompoundDrawablesWithIntrinsicBounds(null, null, tickDrawable, null);
                        wazeMap.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        inAppNavigation.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    }
                    else if(selectedDrawable.equals("wazeMap"))
                    {
                        wazeMap.setCompoundDrawablesWithIntrinsicBounds(null, null, tickDrawable, null);
                        googleMap.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        inAppNavigation.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    }
                    else {

                        inAppNavigation.setCompoundDrawablesWithIntrinsicBounds(null, null, tickDrawable, null);
                        googleMap.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        wazeMap.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    }
                }

                googleMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogUtils.i("Lat and Lng"+routeLat+","+routeLng);
                        dialog.dismiss();
                        selectedDrawable="googleMap";
                        editor.putString("navigationMode",selectedDrawable);
                        editor.apply();
                        googleMap.setCompoundDrawablesWithIntrinsicBounds(null, null, tickDrawable, null);
                        wazeMap.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        inAppNavigation.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        if(!routeLat.isNaN() && !routeLng.isNaN()) {

                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + routeLat + "," + routeLng);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            startActivity(mapIntent);
                        }
                    }
                });

                wazeMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedDrawable="wazeMap";
                        editor.putString("navigationMode",selectedDrawable);
                        editor.apply();
                        wazeMap.setCompoundDrawablesWithIntrinsicBounds(null, null, tickDrawable, null);
                        googleMap.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        inAppNavigation.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        try {
                            //String uri = "waze://?ll=10.367312, 77.980291=yes";
                            dialog.dismiss();
                            String uri = "waze://?ll="+routeLat+", "+routeLng+"=yes";
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
                        } catch (ActivityNotFoundException e) {
                            android.support.v7.app.AlertDialog.Builder builder =
                                    new android.support.v7.app.AlertDialog.Builder(Map_Activity.this, R.style.AppCompatAlertDialogStyle);
                            builder.setTitle("App Not Found");
                            builder.setMessage("For Navigate you need waze app");
                            // builder.setCancelable(false);
                            builder.setPositiveButton("Go to playstore",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Intent intent =
                                                    new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
                                            startActivity(intent);
                                        }
                                    });
                            builder.show();
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                inAppNavigation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        selectedDrawable="inAppNavigation";
                        editor.putString("navigationMode",selectedDrawable);
                        editor.apply();
                        inAppNavigation.setCompoundDrawablesWithIntrinsicBounds(null, null, tickDrawable, null);
                        wazeMap.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        googleMap.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    }
                });


                dialog.show();


            }
        });
*/

        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LogUtils.i("---- begin from onclick");
                strDistanceBegin = "distancebegin";
                distance = 0;

                Map_Activity.p = 0;

                arriveNowLayout.setVisibility(View.GONE);
                endTripLayout.setVisibility(View.VISIBLE);
                destinationLayout.setVisibility(View.VISIBLE);

                getState.putString("tripstate", "startClicked");
                getState.putString("starttime", getCurrentTime());
                getState.apply();

                tripState = "startClicked";

                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID);
                Map<String, Object> taskMap = new HashMap<>();
                taskMap.put("status", "3");
                //databaseReference.updateChildren(taskMap);
                databaseReference2.updateChildren(taskMap);
                startUpdateTrip();

                try {

                    String[] tripidArray = tempTripWithName.split(":");
                    String Tripid = tripidArray[0];
                    if (Tripid != null) {
                        tollfrees.add(Tripid);
                        saveArray(tollfrees, "tolltripidarray", getApplicationContext());
                    }
                    //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId).child("accept");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //removeTripListener();
            }
        });
        // getRiderName("58bcf556da71b46a1c8b4567");

        toll_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toll_pay.setEnabled(false);
                tollFee();
            }
        });

        btnEndTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tripState = "btnendClicked";

                clearSavedDistanceCalculated();
                strDistanceBegin = "totaldistancend";
                tripStatus = "end";

                Map_Activity.p = 1;

                editor.putBoolean("getvolume", false);
                editor.apply();

                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID);
                Map<String, Object> taskMap = new HashMap<>();
                taskMap.put("status", "4");
                //databaseReference.updateChildren(taskMap);

                databaseReference2.updateChildren(taskMap);

                try {

                    updateLocationFDBrMongoDB(mCurrentLocation);

                    if (tempTripWithName == null || tempTripWithName.equals("null") || tempTripWithName.equals("")) {
                        if (tripIDs != null && !tripIDs.isEmpty()) {
                            tempTripWithName = tripIDs.get(tripIDs.size() - 1);
                            LogUtils.i("enter the null validation condition");
                        }
                    }
                    LogUtils.i("enter the null validation condition" + tempTripWithName);

                    //gettripID();

                    destLocation = null;

                    if (mCurrentLocation != null) {
                        latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

                        LogUtils.i("INSIDE ENDTRIP CHANGE" + mCurrentLocation.getLatitude() + mCurrentLocation.getLongitude());
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(latLng)                              // Sets the center of the map to current location
                                .zoom(Constants.MAP_ZOOM_SIZE)
                                .tilt(0)                                     // Sets the tilt of the camera to 0 degrees
                                .build();
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        if (currentLocMarker == null) {

                            currentLocMarker = mMap.addMarker(new MarkerOptions()
                                    .icon(mapCarIcon)
                                    .position(latLng)
                                    .flat(true)
                                    .anchor(0.5f, 0.5f)
                                    .rotation(mCurrentLocation.getBearing()));
                        } else {

                            currentLocMarker.setPosition(latLng);
                        }
                    }
                    //surgepricing
                    String starttime = state.getString("starttime", null);
                    String endtime = getCurrentTime();
                    Integer i = 0;
                    DateFormat format = new SimpleDateFormat("HH:mm");//24 Hour Format

                    Date d1;
                    Date d2;
                    try {
                        d1 = format.parse(starttime);
                        d2 = format.parse(endtime);

                        long diff = d2.getTime() - d1.getTime();
                        long diffSeconds = diff / 1000;
                        long diffMinutes = diff / (60 * 1000);
                        long diffHours = diff / (60 * 60 * 1000);

                        i = (int) (long) diffMinutes;

                        LogUtils.i("THe time difference" + diffMinutes);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (starttime != null)
                        getSurgePricingPercentage(starttime, i);

                    //Add fee to firebase to notify rider
                /*if(tripIDs.size()==0){
                    if (driverId != null && !driverId.isEmpty()) {
                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId).child("accept");
                        Map<String, Object> taskMap2 = new HashMap<>();
                        taskMap2.put("tollfee", "0");
                        databaseReference2.updateChildren(taskMap2);
                    }

                }*/
                    //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId).child("accept");

                    //showTripSummaryDialog();
    /*endUpdateTrip();*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //riderInfo
        riderinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    animY.cancel();
                    showRiderInfoDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        riderinfoinarrived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    showRiderInfoDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        riderinfoinarrived1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    showRiderInfoDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        LogUtils.i("trip status or state in oncreate====>" + tripState);

        if (tripState.matches("requestAccept")) { //1
            tripID = state.getString("tripID", null);
            //getAcceptState();
            getstatusfromfirebase(tripID);
            easyTimer.start();
            easyTimer.setOnTaskRunListener(new EasyTimer.OnTaskRunListener() {
                @Override
                public void onTaskRun(long past_time, String rendered_time) {
                    // Change UI or do something with past_time and rendered_time.
                    // It will NOT block the UI thread.
                    showCancelDialog();
                }
            });
            tripIDs = loadArray("tripidarray", getApplicationContext());
            tollfrees = loadArray("tolltripidarray", getApplicationContext());
            createRidersNameList();

        } else if (tripState.matches("arriveClicked")) { //2
            tripID = state.getString("tripID", null);
            getstatusfromfirebase(tripID);
            easyTimer.start();
            easyTimer.setOnTaskRunListener(new EasyTimer.OnTaskRunListener() {
                @Override
                public void onTaskRun(long past_time, String rendered_time) {
                    // Change UI or do something with past_time and rendered_time.
                    // It will NOT block the UI thread.
                    showCancelDialog();
                }
            });
            tripIDs = loadArray("tripidarray", getApplicationContext());
            tollfrees = loadArray("tolltripidarray", getApplicationContext());
            createRidersNameList();

            //getStartState();
        } else if (tripState.matches("startClicked")) //3

        {
            tripID = state.getString("tripID", null);
            getstatusfromfirebase(tripID);

            tripIDs = loadArray("tripidarray", getApplicationContext());
            tollfrees = loadArray("tolltripidarray", getApplicationContext());
            createRidersNameList();

            //getendState();
        } else if (tripState.matches("lasttrip")) {

            tripID = state.getString("tripID", null);
            getstatusfromfirebase(tripID);

            tripIDs = loadArray("tripidarray", getApplicationContext());
            tollfrees = loadArray("tolltripidarray", getApplicationContext());
            createRidersNameList();

        }

        googleLocationService = new GoogleLocationService(getApplicationContext(), new LocationUpdateListener() {
            @Override
            public void canReceiveLocationUpdates() {
            }

            @Override
            public void cannotReceiveLocationUpdates() {
            }

            //update location to our servers for tracking purpose
            @Override
            public void updateLocation(Location location) {
                if (location != null) {
                    LogUtils.i("updated location in the listener %1$s %2$s" + location.getLatitude() + location.getLongitude());
                    updateLocationToFirebase(location);
                }
            }

            @Override
            public void updateLocationName(String localityName, Location location) {

                googleLocationService.stopLocationUpdates();
            }
        });
        googleLocationService.startUpdates();

        /*audioTimer.setOnTaskRunListener(new EasyTimer.OnTaskRunListener() {
            @Override
            public void onTaskRun(long past_time, String rendered_time) {
                // Change UI or do something with past_time and rendered_time.
                // It will NOT block the UI thread.
                LogUtils.i("Playing Audio");
                playAudio();
            }
        });*/
    }

    private void setMultipleDestListener(String riderid) {
        try {
            multidestref = FirebaseDatabase.getInstance().getReference().child("riders_location").child(riderid).child("DestinationWaypoints");
            multidestlis = multidestref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {

                        for (Polyline mulPoly : Map_Activity.this.wayPaintsPolyline.values()) {
                            mulPoly.remove();
                        }

                        if (points != null)
                            points.clear();

                        collectWaypoints((Map<String, Object>) dataSnapshot.getValue());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void collectWaypoints(Map<String, Object> value) {
        latlngs = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : value.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();

            //phoneNumbers.add((String) singleUser.get("driverid"));
            LogUtils.i("Coordinates====>" + singleUser.get("Coordinates"));

            //String Coordinates = String.valueOf(singleUser.get("Coordinates")).trim();
            ArrayList Coordinates = (ArrayList) singleUser.get("Coordinates");

            if (Coordinates != null) {

                if (Coordinates.size() >= 2)
                    if (Coordinates.get(0) != null & Coordinates.get(1) != null) {

                        //Get phone field and append to list
                        String lat = String.valueOf(Coordinates.get(0)).trim();
                        String lng = String.valueOf(Coordinates.get(1)).trim();
                        Double latdouble = Double.parseDouble(lat);
                        Double lngdouble = Double.parseDouble(lng);

                        if (latdouble > 0) {
                            LatLng latlng = new LatLng(latdouble, lngdouble);
                            latlngs.add(latlng);
                        }
                    }
            }
        }
        getRoute(orginLocation);

    }

    public void getRoute(LatLng orginLocation) {

        LatLng previousPoints;

        if (latlngs != null)
            if (orginLocation != null) {

                if (destLocation != null) {
                    latlngs.add(destLocation);
                }

                previousPoints = orginLocation;

                LogUtils.i("size of the lat and long==>" + latlngs.size());
                LogUtils.i("value of the lat and long==>" + latlngs);

                for (LatLng position : latlngs) {

                    getRoutePoints(previousPoints, position);

                    previousPoints = position;
                }
            }

        LogUtils.i("collected routs are ====>" + points);
    }

    public void tollFee() {

        dialog = new Dialog(Map_Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.call_rider_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setCancelable(false);

        ImageButton back = (ImageButton) dialog.findViewById(R.id.tollclose);
        Button addToll = (Button) dialog.findViewById(R.id.addtoll);
        final EditText fee_edit_text = (EditText) dialog.findViewById(R.id.fee_edit_text);

        CardView infoCard = (CardView) dialog.findViewById(R.id.card_view2_rider_info);
        infoCard.setVisibility(View.GONE);

        CardView tollFeeCard = (CardView) dialog.findViewById(R.id.card_view2_toll_fee);
        tollFeeCard.setVisibility(View.VISIBLE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toll_pay.setEnabled(true);
                dialog.cancel();
            }
        });

        addToll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {

                    String tollAmount = fee_edit_text.getText().toString();

                    addTollAmount(tollAmount);

                    toll_pay.setEnabled(true);
                    dialog.cancel();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Provide Valid Amount", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        dialog.show();
    }

    public String getToll() {
        if (tollfee != null) {
            return tollfee;
        } else {
            return "0";
        }
    }

    public void setToll(String toll) {
        this.tollfee = toll;
    }

    public String getTotalPrice() {
        if (totalprice != null) {
            return totalprice;
        } else {
            return "0";
        }
    }

    public void setTotalPrice(String price) {
        this.totalprice = price;
    }

    public void addTollAmount(String tollAmount) throws Exception {

        int j = 0;
        double strTotal;

        LogUtils.i("Toll Fees in the add toll==>" + tollfee);

        if (tollfrees != null) {
            LogUtils.i("Toll Fees in the add toll if==>" + tollfee);

            if (isDouble(tollAmount)) {

                strTotal = Double.valueOf(tollAmount);

            } else {

                strTotal = Integer.valueOf(tollAmount);
            }

            strTotal = Double.parseDouble(convertToDecimal(strTotal));
            LogUtils.i("double (UP)++ : " + strTotal); //output 20.30

            for (String temp : tollfrees) {
                LogUtils.i("Toll Fees in the add toll foreach==>" + tollfee);
                updatedfirebase(temp, strTotal);
                System.out.println(temp + "trips ids in looping" + j++);
            }
        }
        getState.putInt("tollfee", previousToll);
        getState.apply();

        Toast.makeText(Map_Activity.this, "Toll Added", Toast.LENGTH_SHORT).show();
    }

    private void getCarCategoryrDetails() {
        String url = Constants.CATEGORY_LIVE_URL + "Settings/getCategory";
        LogUtils.i(" CATEGOR URL is " + url);

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject signIn_jsonobj = response.getJSONObject(i);
                                String strWebCategory = signIn_jsonobj.optString("categoryname");
                                strCategoryName[i] = signIn_jsonobj.optString("categoryname");
                                LogUtils.i("Current category" + strCategory);
                                LogUtils.i("Web category" + strWebCategory);


                                if (strCategory.equals(strWebCategory)) {
                                    strWebprice_km = signIn_jsonobj.optString("price_km");
                                    strwebpricepermin = signIn_jsonobj.optString("price_minute");
                                    strwebmaxsize = signIn_jsonobj.optString("max_size");
                                    strwebpricefare = signIn_jsonobj.optString("price_fare");
                                    strbookfee = signIn_jsonobj.optString("book_fee");
                                    strairportfee = signIn_jsonobj.optString("airport_surge");
                                    strtaxpercentage = signIn_jsonobj.optString("tax_percentage");
                                    strDropPrice = signIn_jsonobj.optString("drop_price");

                                    LogUtils.i("Price per KM " + strWebprice_km);
                                    LogUtils.i("Price Per Min" + strwebpricepermin);
                                    LogUtils.i("Max Size " + strwebmaxsize);
                                    LogUtils.i("Price Fare " + strwebpricefare);
                                    LogUtils.i("Drop Prce " + strDropPrice);
                                }

                            } catch (Exception e) {
                                //stopAnim();
                                e.printStackTrace();
                            }
                        }

                        if (strCategory != null) {
                            getDrawable(strCategory);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //protected static final String TAG = null;
                if (error instanceof NoConnectionError) {
                    LogUtils.i("no internet");
                    // stopAnim();
                    //
                    //    Toast.makeText(Map_Activity.this, "An unknown network error has occured", Toast.LENGTH_SHORT).show();
                }
                LogUtils.d("MapActivity: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    private void getKeys() {

        String url = Constants.CATEGORY_LIVE_URL + "settings/getdetails";
        LogUtils.i(" CATEGOR URL is " + url);

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject signIn_jsonobj = response.getJSONObject(i);

                                google_api_key = signIn_jsonobj.optString("google_api_key");


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
                    LogUtils.i("no internet");
                    // stopAnim();
                    //
                    //    Toast.makeText(Map_Activity.this, "An unknown network error has occured", Toast.LENGTH_SHORT).show();
                }
                LogUtils.d("MapActivity: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    private void showRiderInfoDialog() {

        String[] tripidArray = tempTripWithName.split(":");
        String Tripid = tripidArray[0];

        LogUtils.i("Inside the updatedriverRating===>" + Tripid);

        getRiderID(Tripid);
        dialog = new Dialog(Map_Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.call_rider_dialog);
        dialog.getWindow().setWindowAnimations(R.style.DialogTopAnimation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView ridername = (TextView) dialog.findViewById(R.id.ridername);
        ImageView riderimg = (ImageView) dialog.findViewById(R.id.rider_image);
        TextView cartype = (TextView) dialog.findViewById(R.id.car_type);
        ImageButton back = (ImageButton) dialog.findViewById(R.id.backButton);
        LinearLayout call = (LinearLayout) dialog.findViewById(R.id.calllayout);
        TextView txtCancelTrip = (TextView) dialog.findViewById(R.id.txtCancelTrip);
        LinearLayout msg = (LinearLayout) dialog.findViewById(R.id.msglayout);
        LinearLayout cancellay = (LinearLayout) dialog.findViewById(R.id.cancellay);


        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wlp.gravity = Gravity.TOP;
        window.setAttributes(wlp);


        state = getSharedPreferences(Constants.MY_STATE_KEY, MODE_PRIVATE);
        tripState = state.getString("tripstate", null);

        if (tripState != null)
            if (tripState.matches("startClicked")) {
                cancellay.setVisibility(View.GONE);
            } else
                cancellay.setVisibility(View.VISIBLE);

        if (strCategory != null && !strCategory.isEmpty()) {
            cartype.setText(strCategory);
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        LogUtils.i("Rider First Name dialog==>" + riderFirstName);
        LogUtils.i("Rider Last Name dialog==>" + riderLastName);

        if (riderFirstName != null && !riderFirstName.isEmpty()) {
            ridername.setText(riderFirstName + " " + riderLastName);
        }

        if (strRiderProfile != null && !strRiderProfile.isEmpty()) {
            Glide.with(Map_Activity.this)
                    .load(strRiderProfile)
                    .skipMemoryCache(true)
                    .transform(new RoundImageTransform(Map_Activity.this))
                    .into(new GlideDrawableImageViewTarget(riderimg) {
                        @Override
                        public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                            super.onResourceReady(drawable, anim);
                        }
                    });
        }

        // cancel onclick
        txtCancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.support.v7.app.AlertDialog.Builder builder =
                        new android.support.v7.app.AlertDialog.Builder(Map_Activity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(getString(R.string.cancel_trip));
                builder.setMessage(getString(R.string.cancel_dis));
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        tripID = state.getString("tripID", null);
                        LogUtils.i("THE TRIP ID IS You Click Cancel" + tripID);
                      /*  //DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId).child("accept");
                        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID);
                        Map<String, Object> taskMap2 = new HashMap<>();
                        taskMap2.put("status", "5");
                        //databaseReference2.updateChildren(taskMap2);
                        databaseReference3.updateChildren(taskMap2);*/
                        //changes on(17/3/2017)

                        if (tempTripWithName == null || tempTripWithName.equals("null") || tempTripWithName.equals("")) {
                            if (tripIDs != null && !tripIDs.isEmpty()) {
                                tempTripWithName = tripIDs.get(tripIDs.size() - 1);
                                LogUtils.i("enter the null validation condition");
                            }

                        }
                        if (tripIDs != null) {
                            tripIDs.remove(tempTripWithName);
                        }
                        LogUtils.i("Trip with Name in cancel request showCancelDialog" + tempTripWithName);
                        String[] tripidArray = tempTripWithName.split(":");
                        String Tripid = tripidArray[0];

                        LogUtils.i("Trip Size in cancel request" + tripIDs.size() + Tripid);
                        strCacnelStatus = "drivercliked";
                        checktripcancelstatus();
                        clearFirebaseData();
                        clearTripState(Tripid);
                        cancelTrip();
                        dialog1.cancel();
                        dialog.cancel();



                        /*if (tripIDs.size() < 1) {
                            getCancelState();
                            LogUtils.i("Trip Size" + tripIDs.size());
                            endTripLayout.setVisibility(View.GONE);
                            bottomBar.setVisibility(View.VISIBLE);
                            toolbarLayout.setVisibility(View.VISIBLE);
                            startTripLayout.setVisibility(View.GONE);
                            destinationLayout.setVisibility(View.GONE);

                            destLocation = null;
                            distance = 0;
                            strTotalDistance = "0";
                            tripState = "endClicked";
                            //  removeRatingListener();
                            mMap.clear();
                            if (mCurrentLocation != null) {
                                LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

                                LogUtils.i("INSIDE ENDTRIP CHANGE" + mCurrentLocation.getLatitude() + mCurrentLocation.getLongitude());
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(latLng)                              // Sets the center of the map to current location
                                        .zoom(15)
                                        .tilt(0)                                     // Sets the tilt of the camera to 0 degrees
                                        .build();
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                mMap.addMarker(new MarkerOptions()
                                        .icon(mapCarIcon)
                                        .position(latLng));

                            }

                            Intent i = new Intent(getApplicationContext(), Map_Activity.class);
                            startActivity(i);
                        }*/
                        createRidersNameList();
                        getstatusfromfirebase(tripID);
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
        });

        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ridermobile != null && !ridermobile.isEmpty()) {
                    if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.WRITE_SMS") == PackageManager.PERMISSION_GRANTED) {
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.setData(Uri.parse("sms:" + ridermobile));
                        startActivity(sendIntent);
                    }
                } else {
                    Toast.makeText(Map_Activity.this, "Number not register", Toast.LENGTH_SHORT).show();
                }
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ridermobile != null && !ridermobile.isEmpty()) {

                    TelephonyManager telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    int simState = telMgr.getSimState();
                    switch (simState) {
                        case TelephonyManager.SIM_STATE_ABSENT:
                            // do something
                            Toast.makeText(Map_Activity.this, "No SIM Card Inserted", Toast.LENGTH_SHORT).show();

                            break;
                        case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                            // do something
                            break;
                        case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                            // do something
                            break;
                        case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                            // do something
                            break;
                        case TelephonyManager.SIM_STATE_READY:
                            // do something

                            if (ActivityCompat.checkSelfPermission(Map_Activity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            try {

                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ridermobile));
                                startActivity(intent);

                            } catch (android.content.ActivityNotFoundException ex) {

                                Toast.makeText(getApplicationContext(), "Number is not registered", Toast.LENGTH_SHORT).show();
                            }
                    }

                } else {
                    Toast.makeText(Map_Activity.this, "Number not register", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    //showdialog when rider cancel the request
    private void showCancelDialog() {
        try {
            LogUtils.i("Driver ID in cancel listener" + driverId);
            canceltripreference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId).child("cancel_trip_id");
            canceltripreference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null && !dataSnapshot.getValue().toString().equals("0")) {
                        if (!dataSnapshot.getValue().toString().equals("0")) {
                            callCancel(dataSnapshot.getValue().toString());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    LogUtils.i("IN ONCANCEL ERROR" + databaseError);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callCancel(final String status) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId);
        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("cancel_trip_id", "0");
        databaseReference.updateChildren(taskMap);
        LogUtils.i("Status In CANCEL" + status);
        // Toast.makeText(Map_Activity.this, "Rider Canceled the trip", Toast.LENGTH_SHORT).show();
        final android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(Map_Activity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.cancel_trip));
        builder.setMessage(getString(R.string.rider_cancel));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (tripIDs.size() <= 1) {
                    getState.putString("tripstate", "endClicked");
                    getState.apply();
                    LogUtils.d("Value is: " + status);
                    tripIDs.remove(status);
                    System.out.println(tripIDs + "Trip with Name  showCancelDialog ridercancel" + status);
                    easyTimer.stop();
                    Intent intent = new Intent(Map_Activity.this, Map_Activity.class);
                    startActivity(intent);
                } else {
                    LogUtils.d("Value is: " + status);
                    tripIDs.remove(status);
                    System.out.println(tripIDs + "Trip with Name  showCancelDialog ridercancel" + status);
                    createRidersNameList();
                }

            }
        });
        builder.show();
    }

    //save tripid array list:
    public void saveArray(ArrayList<String> array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferencename", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName + "_size", array.size());
        LogUtils.i("INSIDE THE SAVE ARRAY SIZE " + array.size());
        for (int i = 0; i < array.size(); i++)
            editor.putString(arrayName + "_" + i, array.get(i));
        editor.apply();
    }

    //load trip id
    public ArrayList<String> loadArray(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferencename", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        ArrayList<String> data = new ArrayList<String>(size);
        for (int i = 0; i < size; i++)
            data.add(prefs.getString(arrayName + "_" + i, null));

        LogUtils.i("INSIDE THE LOAD ARRAY" + data);

        return data;
    }

    private void cancelTrip() {
        String url = Constants.LIVEURL_REQUEST + "updateTrips/trip_id/" + tripID + "/trip_status/cancel/accept_status/5/distance/0/total_amount/0/user_id/" + driverId;
        LogUtils.i(" ONLINE URL is " + url);
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            LogUtils.i("Cancelled the trip!!");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //protected static final String TAG = null;
                if (error instanceof NoConnectionError) {
                    LogUtils.i("no internet");
                    // stopAnim();
                    // Toast.makeText(Map_Activity.this, "An unknown network error has occured", Toast.LENGTH_SHORT).show();
                }
                LogUtils.d("Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    private void checkphonenumber() {
        String url = Constants.LIVEURL + "editProfile/user_id/" + driverId;
        LogUtils.i(" Checkphone URL is " + url);

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject signIn_jsonobj = response.getJSONObject(i);

                                String mobile = signIn_jsonobj.getString("mobile");
                                //String online_status = signIn_jsonobj.optString("online_status");

                                if (mobile.isEmpty() && mobile.matches("")) {
                                    showAlert();
                                }

/*                                if (online_status.isEmpty() && online_status.matches("")) {

                                    if(online_status.matches("1"))
                                        getOnlineStatus("online");
                                    else
                                        getOnlineStatus("offline");

                                }*/

                            } catch (JSONException e) {
                                //stopAnim();
                                onlineLay.setEnabled(true);
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onlineLay.setEnabled(true);
                //protected static final String TAG = null;
                if (error instanceof NoConnectionError) {
                    LogUtils.i("no internet");
                    // stopAnim();
                    // Toast.makeText(Map_Activity.this, "An unknown network error has occured", Toast.LENGTH_SHORT).show();
                }
                LogUtils.d("Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    public void getDrawable(String category) {

        int categoryPosition = 0, count = 0;

        if (strCategoryName != null)
            for (String forCategory : strCategoryName) {
                if (forCategory != null)
                    if (forCategory.matches(category)) {
                        categoryPosition = count;
                        break;
                    } else
                        count++;
            }

        switch (categoryPosition) {

            case 0:

                //mapCarIcon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_standard);
                mapCarIcon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_standard);
                break;

            case 1:

                //mapCarIcon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_luxury);
                mapCarIcon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_suv_white);
                break;

            case 2:

                //mapCarIcon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_suv);
                mapCarIcon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_suv);
                break;

            case 3:

                //mapCarIcon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_taxi);
                mapCarIcon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_taxi);
                break;

            default:

                //mapCarIcon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_standard);
                mapCarIcon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_standard);
                break;
        }

        if (currentLocMarker != null)
            currentLocMarker.setIcon(mapCarIcon);
    }

    private void showAlert() {
        dialog = new Dialog(Map_Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.set_phone_number_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setCancelable(false);
        // set the custom dialog components - text, image and button
        final Typeface face = Typeface.createFromAsset(getApplicationContext().getAssets(), getString(R.string.app_font));

        TextView setphone = (TextView) dialog.findViewById(R.id.setphone);
        setphone.setTypeface(face);
        ImageButton setlater = (ImageButton) dialog.findViewById(R.id.setcancel);

        TextView needphntxt = (TextView) dialog.findViewById(R.id.needphnnotxt);
        needphntxt.setTypeface(face);
        TextView needphntxttitle = (TextView) dialog.findViewById(R.id.needphnnotxttitle);
        needphntxttitle.setTypeface(face);

        setphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Map_Activity.this, SettingActivity_.class);
                startActivity(i);
            }
        });
        setlater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void createDistanceFireBase() {

        if (driverId != null && tripID != null) {
            //tripID=state.getString("tripID",null);
            LogUtils.i("Inside the save  distance===>" + tripID);
            LogUtils.i("Inside the save rider id in firebase===>" + riderID);
            if (!tripID.equals("0") && !tripID.equals("empty")) {
                ratingRef = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID);
                Map<String, Object> updates = new HashMap<>();
                updates.put("riderid", riderID);
                updates.put("test_riderid", riderID);
                updates.put("driverid", driverId);
                updates.put("req_id", reqID);
                updates.put("Distance", "0");
                updates.put("status", "1");
                updates.put("Price", "0");
                updates.put("rider_rating", "0");
                updates.put("driver_rating", "0");
                updates.put("tollfee", "0");
                LogUtils.i("Total price" + strTotalPrice);

                ratingRef.setValue(updates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        LogUtils.i("DATA SAVED SUCCESSFULLY");
                        if (databaseError != null) {
                            LogUtils.i("DATA SAVED SUCCESSFULLY");
                        }
                    }
                });
            }

        } else {
            LogUtils.i("Inside Else driverid or tripid is empty");
        }
    }

    public void getRiderRequestCancel() {
        if (tripIDs.size() <= 1) {
            getState.putString("tripstate", "endClicked");
            getState.apply();
            LogUtils.d("Value is: " + status);
            tripIDs.remove(status);
            System.out.println(tripIDs + "Trip with Name  showCancelDialog ridercancel" + status);
            easyTimer.stop();
            Intent intent = new Intent(Map_Activity.this, Map_Activity.class);
            startActivity(intent);
        } else {
            LogUtils.d("Value is: " + status);
            tripIDs.remove(status);
            System.out.println(tripIDs + "Trip with Name  showCancelDialog ridercancel" + status);
            createRidersNameList();
        }
    }

    public void getCancelState() {
        LogUtils.i("Trip size in get cancel state" + tripIDs + "SIZE" + tripIDs.size());
        if (tripIDs.isEmpty() || tripIDs.size() == 0) {

            mMap.clear();
            getState.putString("tripstate", "endClicked");
            getState.apply();

            tripState = "btnendClicked";
            clearSavedDistanceCalculated();
            strDistanceBegin = "totaldistancend";
            tripStatus = "end";
            //gettripID();

            destLocation = null;

            if (mCurrentLocation != null) {
                LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

                LogUtils.i("INSIDE ENDTRIP CHANGE" + mCurrentLocation.getLatitude() + mCurrentLocation.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)                              // Sets the center of the map to current location
                        .zoom(Constants.MAP_ZOOM_SIZE)
                        .tilt(0)                                     // Sets the tilt of the camera to 0 degrees
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                if (currentLocMarker == null) {

                    currentLocMarker = mMap.addMarker(new MarkerOptions()
                            .icon(mapCarIcon)
                            .position(latLng)
                            .flat(true)
                            .anchor(0.5f, 0.5f)
                            .rotation(mCurrentLocation.getBearing()));
                } else {

                    currentLocMarker.setPosition(latLng);
                }
            }

            endTripLayout.setVisibility(View.GONE);
            arriveNowLayout.setVisibility(View.GONE);
            bottomBar.setVisibility(View.VISIBLE);
            toolbarLayout.setVisibility(View.VISIBLE);
            routeNavigate.setVisibility(View.GONE);
            startTripLayout.setVisibility(View.GONE);
            destinationLayout.setVisibility(View.GONE);

            easyTimer.stop();
            Intent i = new Intent(getApplicationContext(), Map_Activity.class);
            startActivity(i);

        }
    }

    private void updateDistanceFireBase() {

        if (tripID != null) {
            if (!tripID.equals("")) {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID);
                Map<String, Object> updates = new HashMap<>();
                updates.put("Distance", strTotalDistance);
                //updates.put("Price", strTotalPrice);
                updates.put("Price", getTotalPrice());
                LogUtils.i("Total price" + strTotalPrice);

                ref.updateChildren(updates); //change

            }
            /*tripID = state.getString("tripID", null);
            LogUtils.i("Inside the save  distance===>" + tripID);
*/

        }
    }

    private void updateDriverRating(String driverRating) {
        if (tempTripWithName != null) {
  /*          tripID = state.getString("tripID", null);
            LogUtils.i("Inside the save  distance===>" + tripID);*/
            String[] tripidArray = tempTripWithName.split(":");
            String Tripid = tripidArray[0];

            LogUtils.i("Inside the updatedriverRating===>" + Tripid);
            if (Tripid != null && !Tripid.isEmpty()) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("trips_data").child(Tripid);
                Map<String, Object> updates = new HashMap<>();
                updates.put("driver_rating", driverRating);

                LogUtils.i("Total rating" + driverRating);
                LogUtils.i("Update through ref" + driverRating);
                ref.updateChildren(updates);
            }


           /* if (ratingRef != null) {
                LogUtils.i("Update through ratingref" + driverRating);
                ratingRef.updateChildren(updates);
            } else {
                LogUtils.i("Update through ref" + driverRating);
                ref.updateChildren(updates);
            }*/
        }
    }

    public void getRating() {
        //tripID = state.getString("tripID", null);
        LogUtils.i("Inside the getdriverRating===>" + tripID);

        flexibleRatingBar = (FlexibleRatingBar) dialogTripSummary.findViewById(R.id.flexibleRatingBar);
        ratingReference = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID + "/rider_rating");
        ratingListener = ratingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ratingInt = dataSnapshot.getValue().toString();
                    if (ratingInt != null && !ratingInt.isEmpty()) {
                        try {
                            LogUtils.i("DATASNAPSHOTTT Rattinggggg" + ratingInt);
                            Float rating = Float.parseFloat(ratingInt);
                            flexibleRatingBar.setRating(rating);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void endUpdateTrip() {
        if (mCurrentLocation != null) {
            endAddress = getCompleteAddressString(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            endLat = String.valueOf(mCurrentLocation.getLatitude());
            endLng = String.valueOf(mCurrentLocation.getLongitude());
            try {
                endAddress = endAddress.replaceAll("/", "");
                endAddress = URLEncoder.encode(endAddress, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                endAddress = "";
            }
        } else {
            endAddress = "";
        }
        String[] tripidArray = tempTripWithName.split(":");
        String Tripid = tripidArray[0];
        System.out.println(tripID + "trips ids" + Tripid);
        LogUtils.i("trips idsamount" + getTotalPrice());
        String url = Constants.LIVEURL_REQUEST + "updateTrips/trip_id/" + Tripid + "/trip_status/end/accept_status/4/distance/" + strTotalDistance + "/user_id/" + driverId + "/drop_address/" + endAddress + "/end_lat/" + endLat + "/end_long/" + endLng;
        LogUtils.i(" ONLINE URL is " + url);
        LogUtils.i("end trip url formed = " + url);
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        LogUtils.i("end trip response = " + response.toString());
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject signIn_jsonobj = response.getJSONObject(i);
                                riderID = signIn_jsonobj.getString("rider_id");
                                setTotalPrice(signIn_jsonobj.getString("total_price"));
                                txtTripAmount.setText("$" + convertToDecimal(Double.parseDouble(getTotalPrice())));
                                //save to firebase
                                updateDistanceFireBase();
                                String strDestination = signIn_jsonobj.getString("destination");
                                JSONObject jsonArray = new JSONObject(strDestination);
                                strLat = jsonArray.getString("lat");
                                strLng = jsonArray.getString("long");

                                LogUtils.i("fffffff" + riderID);
                                LogUtils.i("latitiudee OF DESTINATION" + strLat);
                                LogUtils.i("longitude OF DESTINATION" + strLng);

                                //getCompleteAddressString(double_lat, double_lng);
                                getRiderDetails();

                            } catch (JSONException e) {
                                //stopAnim();
                                onlineLay.setEnabled(true);
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.i("end trip error response = " + error.networkResponse);
                onlineLay.setEnabled(true);
                //protected static final String TAG = null;
                if (error instanceof NoConnectionError) {
                    LogUtils.i("no internet");
                    // stopAnim();
                    // Toast.makeText(Map_Activity.this, "An unknown network error has occured", Toast.LENGTH_SHORT).show();
                }
                LogUtils.d("Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    private void startUpdateTrip() {
        String url = Constants.LIVEURL_REQUEST + "updateTrips/trip_id/" + tripID + "/trip_status/on/accept_status/3/total_amount/0/user_id/" + driverId;
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
                                riderID = signIn_jsonobj.getString("rider_id");
                                String strDestination = signIn_jsonobj.getString("destination");
                                JSONObject jsonArray = new JSONObject(strDestination);
                                strLat = jsonArray.getString("lat");
                                strLng = jsonArray.getString("long");
                                strsetValue = "coming_start";
                                setDropListener(riderID);
                                setMultipleDestListener(riderID);
                                pickLatLng = strLat + "," + strLng;
                                PlaceType(pickLatLng);
                                //check the drop listener has lat lng
                                try {
                                    if (!haslatlng) {
                                        double double_lat = Double.parseDouble(strLat);
                                        double double_lng = Double.parseDouble(strLng);

                                        LogUtils.i("fffffff" + riderID);
                                        LogUtils.i("latitiudee OF DESTINATION" + strLat);
                                        LogUtils.i("longitude OF DESTINATION" + strLng);

                                        destLocation = new LatLng(double_lat, double_lng);
                                        if (mCurrentLocation != null)
                                            orginLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

                                        if (orginLocation != null && destLocation != null) {
                                            if (latlngs != null) {

                                                LogUtils.i("The latlngs are" + latlngs);
                                                GoogleDirection.withServerKey(google_api_key)
                                                        .from(orginLocation)
                                                        .to(destLocation)
                                                        .unit(Unit.METRIC)
                                                        .waypoints(latlngs)
                                                        .transportMode(TransportMode.DRIVING)
                                                        .execute(Map_Activity.this);
                                            } else {
                                                GoogleDirection.withServerKey(google_api_key)
                                                        .from(orginLocation)
                                                        .to(destLocation)
                                                        .unit(Unit.METRIC)
                                                        .transportMode(TransportMode.DRIVING)
                                                        .execute(Map_Activity.this);
                                            }
                                        }
                                        getCompleteAddressString(double_lat, double_lng);
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }

                                getRiderDetails();
                            } catch (JSONException e) {
                                //stopAnim();
                                onlineLay.setEnabled(true);
                                e.printStackTrace();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onlineLay.setEnabled(true);
                //protected static final String TAG = null;
                if (error instanceof NoConnectionError) {
                    LogUtils.i("else");
                    // stopAnim();
                    //   Toast.makeText(Map_Activity.this, "An unknown network error has occured", Toast.LENGTH_SHORT).show();
                }
                LogUtils.d("Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    private void updateArriveRequest() {

        //http://demo.cogzidel.com/arcane_lite/requests/updateTrips/trip_id/5858fd6b49ad6a3708b7acd9/trip_status/on/accept_status/2
        String url = Constants.LIVEURL_REQUEST + "updateTrips/trip_id/" + tripID + "/trip_status/off/accept_status/2/total_amount/0/user_id/" + driverId;
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
                                riderID = signIn_jsonobj.getString("rider_id");
                                String strDestination = signIn_jsonobj.getString("pickup");
                                JSONObject jsonArray = new JSONObject(strDestination);
                                strLat = jsonArray.getString("lat");
                                strLng = jsonArray.getString("long");

                                double double_lat = Double.parseDouble(strLat);
                                double double_lng = Double.parseDouble(strLng);

                                LogUtils.i("fffffff" + riderID);
                                LogUtils.i("latitiudee" + strLat);
                                LogUtils.i("longitude" + strLng);

                                strsetValue = "coming_arrive";

                                //check the drop listener has lat lng
                                try {

                                    LogUtils.i("latitiudee" + strLat);
                                    LogUtils.i("longitude" + strLng);

                                    destLocation = new LatLng(double_lat, double_lng);

                                    if (mCurrentLocation != null)
                                        orginLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

                                    GoogleDirection.withServerKey(google_api_key)
                                            .from(orginLocation)
                                            .to(destLocation)
                                            .unit(Unit.METRIC)
                                            .transportMode(TransportMode.DRIVING)
                                            .execute(Map_Activity.this);

                                    getCompleteAddressString(double_lat, double_lng);

                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }

                                getRiderDetails();

                            } catch (JSONException e) {
                                //stopAnim();
                                onlineLay.setEnabled(true);
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onlineLay.setEnabled(true);
                //protected static final String TAG = null;
                if (error instanceof NoConnectionError) {
                    LogUtils.i("no internet");
                    // stopAnim();
                }
                LogUtils.d("Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    private void upDateRequest(Double lat, Double lng) {
        String url = Constants.LIVEURL_REQUEST + "updateRequest/request_id/" + reqID + "/driver_id/" + driverId + "/request_status/accept/" + "lat/" + lat + "/long/" + lng;
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
                                riderID = signIn_jsonobj.getString("rider_id");
                                String strPickup = signIn_jsonobj.getString("pickup");
                                JSONObject jsonArray = new JSONObject(strPickup);
                                strLat = jsonArray.getString("lat");
                                strLng = jsonArray.getString("long");
                                strsetValue = "updatereq";
                                strsetdestination = "updatereq";

                                //setDropListener(riderID);

                                //check the drop listener has lat lng
                                try {
                                    if (!haslatlng) {
                                        double double_lat = Double.parseDouble(strLat);
                                        double double_lng = Double.parseDouble(strLng);

                                        LogUtils.i("latitiudee" + strLat);
                                        LogUtils.i("longitude" + strLng);

                                        destLocation = new LatLng(double_lat, double_lng);

                                        if (mCurrentLocation != null)
                                            orginLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

                                        GoogleDirection.withServerKey(google_api_key)
                                                .from(orginLocation)
                                                .to(destLocation)
                                                .unit(Unit.METRIC)
                                                .transportMode(TransportMode.DRIVING)
                                                .execute(Map_Activity.this);

                                        getCompleteAddressString(double_lat, double_lng);
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }

                                getRiderDetails();

                            } catch (JSONException e) {
                                //stopAnim();
                                onlineLay.setEnabled(true);
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onlineLay.setEnabled(true);
                //protected static final String TAG = null;
                if (error instanceof NoConnectionError) {
                    LogUtils.i("no internet");
                    // stopAnim();
                    //    Toast.makeText(Map_Activity.this, "An unknown network error has occured", Toast.LENGTH_SHORT).show();
                }
                LogUtils.d("Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    private void getRiderDetails() {

        final String url = Constants.LIVEURL_RIDER + "editProfile/user_id/" + riderID;
        LogUtils.i("RiderProfileURL==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        status = jsonObject.optString("status");
                        message = jsonObject.optString("message");


                        if (status.equals("Success")) {
                            riderFirstName = jsonObject.optString("firstname");
                            riderLastName = jsonObject.optString("lastname");
                            strRiderProfile = jsonObject.optString("profile_pic");
                            ridermobile = jsonObject.optString("mobile");
                            riderFirstName = riderFirstName.replaceAll("%20", " ");
                            riderLastName = riderLastName.replaceAll("%20", " ");

                            LogUtils.i("Rider First Name old==>" + riderFirstName);
                            LogUtils.i("Rider Last Name old==>" + riderLastName);

                            if (riderLastName.matches("null"))
                                riderLastName = " ";

                            //Bitmap car = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_standard);
                            try {
                                switch (strsetValue) {
                                    case "updatereq":

                                        txtRiderName.setText(riderFirstName + " " + riderLastName);
                                        break;
                                    case "coming_arrive":

                                        txtRiderName_Begin.setText(riderFirstName + " " + riderLastName);
                                        break;
                                    case "coming_end":
                                        txtEndTrip.setText(riderFirstName + " " + riderLastName);
                                        break;
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                            //     dismissDialog();
                        } else {
                            LogUtils.i("inside else");
//                            Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
                            // dismissDialog();
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                    //  dismissDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NoConnectionError) {
                    //   dismissDialog();
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //mMap.setMyLocationEnabled(true);
        mMap.getMinZoomLevel();

    /*    //Map Camera Move listener
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                FAB.setVisibility(View.GONE);
                toolbarLayout.setVisibility(View.GONE);
                bottomBar.setVisibility(View.GONE);
            }
        });
       *//* mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                FAB.setVisibility(View.GONE);
                toolbarLayout.setVisibility(View.GONE);
                bottomBar.setVisibility(View.GONE);
            }
        });*//*
        //Map Camera Idle listener
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (tripState != null && !tripState.isEmpty()) {
                    if (tripState.matches("newRequest") || tripState.matches("requestAccept") || tripState.matches("arriveClicked") || tripState.matches("startClicked") || tripState.matches("tripnotend")) {
                        toolbarLayout.setVisibility(View.GONE);
                        bottomBar.setVisibility(View.GONE);
                    } else {
                        toolbarLayout.setVisibility(View.VISIBLE);
                        bottomBar.setVisibility(View.VISIBLE);
                    }

                }
                FAB.setVisibility(View.VISIBLE);
            }
        });*/

        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            layoutParams.setMargins(30, 180, 0, 0);
        }

        mGoogleApiClient.connect();

        try {
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    // Make a snapshot when map's done loading
                    mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                        @Override
                        public void onSnapshotReady(Bitmap bitmap) {
                            //Getting Map as Bitmap
                            mapBitmap = bitmap;

                            editor.putString("mapImage", BitMapToString(mapBitmap));
                            editor.apply();

                            // If map won't be used afterwards, remove it's views
                            // ((FrameLayout)findViewById(R.id.map)).removeAllViews();
                        }
                    });
                }
            });
        } catch (Exception e) {
            LogUtils.i("Exception" + e);
        }

        d = new Dialog(Map_Activity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        d.setContentView(R.layout.activity_progress_bar);

        pwOne = (ProgressWheel) d.findViewById(R.id.progressBarTwo);
        progresslayout = (RelativeLayout) d.findViewById(R.id.progresslayout);
        requestMapView = (ImageView) d.findViewById(R.id.map_view);
        pickuploc = (TextView) d.findViewById(R.id.pick_location);
        droploc = (TextView) d.findViewById(R.id.drop_location);
        progeta = (TextView) d.findViewById(R.id.eta);
        progDistance = (TextView) d.findViewById(R.id.distance);
        progestfare = (TextView) d.findViewById(R.id.fare);
        pwOne.setBarWidth(10);
        pwOne.setRimWidth(10);
        pwOne.setRimColor(Color.WHITE);


        MarkerPoints = new ArrayList<>();

        dialogTripSummary = new Dialog(Map_Activity.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        dialogTripSummary.setContentView(R.layout.activity_trip_summary);

       /* try {

            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style));

            if (!success) {
                LogUtils.e("Map_Activity "+"Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            LogUtils.e("Map_Activity "+"Can't find style. Error: ", e);
        }*/


        checkPermission();
        LogUtils.i("INSIDE MAPREADY");

        mCurrentLocation = getFusedLocation();

        if (mCurrentLocation != null) {
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            LogUtils.i("INSIDE LOCAION CHANGE" + mCurrentLocation.getLatitude() + mCurrentLocation.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)                              // Sets the center of the map to current location
                    .zoom(Constants.MAP_ZOOM_SIZE)
                    .tilt(0)                                     // Sets the tilt of the camera to 0 degrees
                    .build();

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            if (currentLocMarker == null) {

                currentLocMarker = mMap.addMarker(new MarkerOptions()
                        .icon(mapCarIcon)
                        .position(latLng)
                        .flat(true)
                        .anchor(0.5f, 0.5f)
                        .rotation(mCurrentLocation.getBearing()));
            } else {

                currentLocMarker.setPosition(latLng);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the task you need to do.
                    startLocationUpdates();

                } else {
                    LogUtils.i("INSIDE request permission");
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
            }
        }
    }

    public void clickOnline(View v) {
        if (!isOnlineButtunClicked()) {
            String strStartDate = getDateTime();
            editor.putString("onlineStartDate", strStartDate);

            getProofStatus();

        } else {
            //goOffline();
            editor.putString("onlineStartDate", null);
            setonline("0", String.valueOf(getDuration()));
            //goOfflineFB();
        }
        editor.apply();
    }

    public void getOnlineStatusFB() {

        if (driverId != null) {

            //Get datasnapshot at your "users" root node
            proofstatusref = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId);
            proofstatusref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Get map of users in datasnapshot
                    if (dataSnapshot.getValue() != null) {

                        LogUtils.i("response ===>" + dataSnapshot.toString());

                        Object onlineStatusObj = dataSnapshot.child("online_status").getValue();
                        Object proofStatusObj = dataSnapshot.child("proof_status").getValue();
                        String onlineStatus, proofStatus;

                        LogUtils.i("response onlineStatus===>" + onlineStatusObj);
                        LogUtils.i("response proofStatus===>" + proofStatusObj);


                        if (proofStatusObj != null) {

                            proofStatus = String.valueOf(proofStatusObj);

                            if (proofStatus.matches("Accepted")) {

                                if (onlineStatusObj != null) {

                                    onlineStatus = String.valueOf(onlineStatusObj);

                                    if (onlineStatus.matches("1")) {

                                        setOnlineButtunStatus(true);
                                        onlineTxt.setText(R.string.go_offline);
                                        if (mCurrentLocation != null)
                                            onLocationReceived(mCurrentLocation);
                                        getRequestStatus();

                                    } else {

                                        setOnlineButtunStatus(false);
                                        onlineTxt.setText(R.string.go_online);
                                        if (mCurrentLocation != null)
                                            onLocationReceived(mCurrentLocation);
                                    }
                                } else {
                                    insertFBdriverStatus("online_status");
                                }

                            } else {
                                setOnlineButtunStatus(false);

                                onlineTxt.setText(R.string.go_online);

                                if (mCurrentLocation != null)
                                    onLocationReceived(mCurrentLocation);

                                setonline("0", String.valueOf(getDuration()));


                                if (proofStatus.matches("Rejected")) {

                                    showdialog((getResources().getString(R.string.proof_rejected)));
                                    //gotooffline();
                                } else {

                                    showdialog(getResources().getString(R.string.proof_not_accept));
                                    //gotooffline();
                                }
                            }

                        } else {

                            insertFBdriverStatus("proof_status");
                        }

                    } else {
                        saveInFirebase();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //handle databaseError
                }
            });
        }
        listenOnlineStatusFB();
    }

    public void listenOnlineStatusFB() {

        if (driverId != null) {
            //Get datasnapshot at your "users" root node
            proofstatusref = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId);
            statusListener = proofstatusref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Get map of users in datasnapshot
                    if (dataSnapshot.getValue() != null) {

                        LogUtils.i("response ===>" + dataSnapshot.toString());

                        Object onlineStatusObj = dataSnapshot.child("online_status").getValue();
                        Object proofStatusObj = dataSnapshot.child("proof_status").getValue();
                        String onlineStatus, proofStatus;

                        LogUtils.i("response onlineStatus===>" + onlineStatusObj);
                        LogUtils.i("response proofStatus===>" + proofStatusObj);


                        if (proofStatusObj != null) {

                            proofStatus = String.valueOf(proofStatusObj);

                            if (proofStatus.matches("Accepted")) {

                                if (onlineStatusObj != null) {

                                    onlineStatus = String.valueOf(onlineStatusObj);

                                    if (onlineStatus.matches("1")) {

                                        if (!onlineTxt.getText().toString().matches("GO OFFLINE")) {
                                            goOnlineFB();
                                        }
                                    } else {

                                        if (onlineTxt.getText().toString().matches("GO OFFLINE")) {

                                            goOfflineFB();
                                        }
                                    }
                                }

                            } else {

                                setOnlineButtunStatus(false);

                                if (proofStatus.matches("Rejected")) {

                                    showdialog((getResources().getString(R.string.proof_rejected)));
                                    //gotooffline();
                                } else {

                                    showdialog(getResources().getString(R.string.proof_not_accept));
                                    //gotooffline();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //handle databaseError
                }
            });
        }
    }

    public void goOfflineFB() {
        setOnlineButtunStatus(false);
        onlineTxt.setText(R.string.go_online);
        stopService(new Intent(getBaseContext(), Services.class).setPackage(this.getPackageName()));
        //setonline("0");
        TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.you_are_now_offline), TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.RED);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.RED);
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        params.setMargins(0, 30, 0, 0);
        textView.setLayoutParams(params);
        snackbar.show();
        if (mCurrentLocation != null)
            onLocationReceived(mCurrentLocation);
    }

    public void goOnlineFB() {
        setOnlineButtunStatus(true);
        onlineTxt.setText(R.string.go_offline);
        startService(new Intent(getBaseContext(), Services.class).setPackage(this.getPackageName()));
        //setonline("1");
        TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.you_are_now_online), TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.GREEN);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        params.setMargins(0, 30, 0, 0);
        textView.setLayoutParams(params);
        snackbar.show();
        if (mCurrentLocation != null)
            onLocationReceived(mCurrentLocation);
        getRequestStatus();
    }

    public void insertFBdriverStatus(String key) {
        setOnlineButtunStatus(false);
        onlineTxt.setText(R.string.go_online);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        if (key.matches("online_status"))
            ref.child("drivers_data").child(driverId).child("online_status").setValue("0");
        else {
            ref.child("drivers_data").child(driverId).child("proof_status").setValue("0");
            ref.child("drivers_data").child(driverId).child("online_status").setValue("0");
        }
        setonline("0", String.valueOf(getDuration()));
    }

    public void getProofStatus() {
        try {
            LogUtils.i("Driver Id In Proof" + driverId);
            proofstatusref = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId).child("proof_status");
            proofstatusref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String proofstatus = dataSnapshot.getValue().toString();
                        LogUtils.i("Driver ProofStatus " + proofstatus);
                        if (!proofstatus.isEmpty() && proofstatus.length() != 0) {
                            if (proofstatus.matches("Accepted")) {

                                //online_method();
                                //goOnlineFB();
                                //setonline("1", String.valueOf(0));
                                getBlockedStatus();
                            } else if (proofstatus.matches("Rejected")) {

                                showdialog((getResources().getString(R.string.proof_rejected)));
                                //gotooffline();
                            } else {

                                showdialog(getResources().getString(R.string.proof_not_accept));
                                //gotooffline();
                            }
                        }
                    } else {

                        saveInFirebase();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    LogUtils.i("error==>" + String.valueOf(databaseError));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBlockedStatus() {
        try {
            LogUtils.i("Driver Id In Blocked" + driverId);
            proofstatusref = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId).child("block_status");
            proofstatusref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String blockStatus = dataSnapshot.getValue().toString();
                        LogUtils.i("Driver blockStatus " + blockStatus);
                        if (!blockStatus.isEmpty() && blockStatus.length() != 0) {
                            if (blockStatus.matches("0")) {

                                //online_method();
                                //goOnlineFB();
                                setonline("1", String.valueOf(0));
                            } else if (blockStatus.matches("1")) {

                                showdialog((getResources().getString(R.string.blocked_by_admin)));
                                //gotooffline();
                            } else {

                                showdialog(getResources().getString(R.string.blocked_by_admin));
                                //gotooffline();
                            }
                        } else {
                            setonline("1", String.valueOf(0));
                        }
                    } else {
                        setonline("1", String.valueOf(0));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    LogUtils.i("error==>" + String.valueOf(databaseError));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveInFirebase() {
        if (driverId != null && !driverId.isEmpty()) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId);
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", drivername);
            updates.put("proof_status", "Pending");    //proofstatus
            updates.put("online_status", "0");    //onlinestatus

            Map<String, Object> updateaccept = new HashMap<>();
            updateaccept.put("status", "0");
            updateaccept.put("trip_id", "0");
            updateaccept.put("trip_id_rider_name", "0");
            updates.put("accept", updateaccept);

            Map<String, Object> updaterequest = new HashMap<>();
            updaterequest.put("req_id", "");
            updaterequest.put("status", "0");
            updates.put("request", updaterequest);

            ref.setValue(updates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    LogUtils.i("DATA SAVED SUCCESSFULLY");
                    if (databaseError != null) {
                        LogUtils.i("DATA SAVED SUCCESSFULLY");
                    }
                    getProofStatus();
                }
            });
        }
    }

    public void showdialog(String message) {
        final TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), message, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.RED);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.RED);
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        params.setMargins(0, 30, 0, 0);
        textView.setLayoutParams(params);
        snackbar.show();
    }

    public void setonline(final String online, String duration) {

        onlineLay.setEnabled(false);

        String url = Constants.LIVEURL + "updateOnlineStatus/userid/" + driverId + "/online_status/" + online + "/online_duration/" + duration;
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
                                String signIn_status = signIn_jsonobj.getString("status");

                                if (signIn_status.equals("Success")) {
                                    onlineLay.setEnabled(true);
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                    ref.child("drivers_data").child(driverId).child("online_status").setValue(online);

                                    if (online.matches("1")) {

                                        setOnlineButtunStatus(true);
                                    } else {

                                        setOnlineButtunStatus(false);
                                    }

                                } else if (signIn_status.equals("Fail")) {
                                    onlineLay.setEnabled(true);

                                    //stopAnim();
                                }

                            } catch (JSONException e) {
                                //stopAnim();
                                onlineLay.setEnabled(true);
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onlineLay.setEnabled(true);
                //protected static final String TAG = null;
                if (error instanceof NoConnectionError) {
                    LogUtils.i("no internet");
                    // stopAnim();
                    //
                    //    Toast.makeText(Map_Activity.this, "An unknown network error has occured", Toast.LENGTH_SHORT).show();
                }
                LogUtils.d("Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    public void getRequestStatus() {
        if (keyRef != null) {
            keyRef.removeEventListener(this);
            keyRef = null;
        }
        keyRef = new GeoFire(FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId + "/request")).getDatabaseReference();
        keyRef.addValueEventListener(this);
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(Map_Activity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Map_Activity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    protected void startLocationUpdates() {
        checkPermission();
        mGoogleApiClient.connect();
        //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this, Looper.getMainLooper());
    }

    protected void stopLocationUpdates() {
        checkPermission();
        mGoogleApiClient.connect();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null && mapPosition == 0) {
            mCurrentLocation = mLastLocation;
            mapPosition = mapPosition + 1;
            //place marker at current position for the first time
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastLocation.getLatitude(),
                            mLastLocation.getLongitude()),
                    Constants.MAP_ZOOM_SIZE));
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.SET_INTERVAL); //5 seconds
        mLocationRequest.setFastestInterval(Constants.SET_FASTESTINTERVAL); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        LogUtils.i("Radius Value " + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    @Override
    public void onLocationChanged(Location location) {

        //checkTolls(location);

        LogUtils.i("location Changed===>" + location.getLatitude() + "  " + location.getLongitude());

        updateLocationFDBrMongoDB(location);

        LatLng curPos;
        float curPosBearing;

        if (mCurrentLocation != null) {
            LogUtils.i("ONLOCATIOn CHANGE bearing" + mCurrentLocation.getBearing());
            curPos = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            curPosBearing = mCurrentLocation.getBearing();

        } else {
            curPos = new LatLng(location.getLatitude(), location.getLongitude());
            curPosBearing = location.getBearing();
            LogUtils.i("location null");
        }

        mCurrentLocation = location;

        LogUtils.i("---- begin from strDistanceBegin = " + strDistanceBegin);
        if (strDistanceBegin != null) {

            if (strDistanceBegin.matches("distancebegin")) {
                if (lStart == null) {
                    lStart = mCurrentLocation;
                    lEnd = mCurrentLocation;
                } else
                    lEnd = mCurrentLocation;

                //Calling the method below updates the  live values of distance and speed to the TextViews.
                updateUI();

                //Saving required data for distance restoring after application crash or unknowingly close
                saveDistanceCalculated();

                //calculating the speed with getSpeed method it returns speed in m/s so we are converting it into kmph
                speed = location.getSpeed() * 18 / 5;
                //Toast.makeText(Map_Activity.this, "trip started!!", Toast.LENGTH_SHORT).show();
            }
        }
        if (mMapLocationListener != null) {
            mMapLocationListener.onLocationChanged(location);
        }
        // mMap.clear();
        if (mCurrentLocation != null) {

            LogUtils.i("INSIDE LOCAION CHANGE" + mCurrentLocation.getLatitude() + mCurrentLocation.getLongitude());
            //polyline:
            if (destLocation != null) {
                if (tripStatus != null && tripStatus.matches("end")) {
                    tripStatus = "null";
                    if (mMap != null)
                        mMap.clear();
                } else {
                    //GoogleDirection.withServerKey(getString(R.string.direction_api_key))
                    if (latlngs != null) {

                        LogUtils.i("The latlngs cordinates are" + latlngs);
                        GoogleDirection.withServerKey(google_api_key)
                                .from(new LatLng(location.getLatitude(), location.getLongitude()))
                                .to(destLocation)
                                .waypoints(latlngs)
                                .unit(Unit.METRIC)
                                .transportMode(TransportMode.DRIVING)
                                .execute(this);
                    } else {
                        GoogleDirection.withServerKey(google_api_key)
                                .from(new LatLng(location.getLatitude(), location.getLongitude()))
                                .to(destLocation)
                                .unit(Unit.METRIC)
                                .transportMode(TransportMode.DRIVING)
                                .execute(this);
                    }
                }
            } else {
                if (mMap != null) {
                    try {

                        LogUtils.i("Key moved ===>" + mCurrentLocation.getSpeed());
                        //mMap.clear();

                        zoomCameraToPosition(curPos);

                        if (currentLocMarker == null) {


                            currentLocMarker = mMap.addMarker(new MarkerOptions()
                                    .icon(mapCarIcon)
                                    .position(curPos)
                                    .flat(true));
                            currentLocMarker.setAnchor(0.5f, 0.5f);
                            currentLocMarker.setRotation(curPosBearing);

                            //currentLocMarker.remove();
                        } else {
                            if (mCurrentLocation.getBearing() != 0.0)
                                previousBearing = mCurrentLocation.getBearing();

                            if (prevLatLng != new LatLng(0, 0)) {

                                if (!(curPos.equals(prevLatLng))) {

                                    double[] startValues = new double[]{prevLatLng.latitude, prevLatLng.longitude};

                                    double[] endValues = new double[]{curPos.latitude, curPos.longitude};

                                    LogUtils.i("Start location===>" + startValues[0] + "  " + startValues[1]);
                                    LogUtils.i("end location===>" + endValues[0] + "  " + endValues[1]);

                                    LogUtils.i("inside locationchange bearing" + mCurrentLocation.getBearing());

                                    animateMarkerTo(currentLocMarker, startValues, endValues, mCurrentLocation.getBearing());

                                } else {
                                    LogUtils.i("outside locationchange bearing" + mCurrentLocation.getBearing());
                                    if (mCurrentLocation.getBearing() == 0.0)
                                        currentLocMarker.setRotation(previousBearing);
                                    else
                                        currentLocMarker.setRotation(mCurrentLocation.getBearing());
                                    // currentLocMarker.setRotation(mCurrentLocation.getBearing());
                                }
                            } else {
                                currentLocMarker.setPosition(curPos);
                                currentLocMarker.setRotation(mCurrentLocation.getBearing());
                            }

                            prevLatLng = new LatLng(curPos.latitude, curPos.longitude);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void zoomCameraToPosition(LatLng curPos) {

        boolean contains = mMap.getProjection().getVisibleRegion().latLngBounds.contains(curPos);

        if (!contains) {
            // MOVE CAMERA
            // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(animatedValue[0],animatedValue[1]),17.0f));

            float zoomPosition;
            if (tripState == null || tripState.matches("endClicked") || tripState.matches("btnendClicked"))
                zoomPosition = Constants.MAP_ZOOM_SIZE;
            else
                zoomPosition = Constants.MAP_ZOOM_SIZE_ONTRIP;

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(curPos)                              // Sets the center of the map to current location
                    .zoom(zoomPosition)
                    .tilt(0)                                     // Sets the tilt of the camera to 0 degrees
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    // Animation handler for old APIs without animation support
    private void animateMarkerTo(final Marker marker, double[] startValues, double[] endValues, final float bearing) {


        ValueAnimator latLngAnimator = ValueAnimator.ofObject(new DoubleArrayEvaluator(), startValues, endValues);
        latLngAnimator.setDuration(1300);
        latLngAnimator.setInterpolator(new DecelerateInterpolator());
        latLngAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                double[] animatedValue = (double[]) animation.getAnimatedValue();
                marker.setPosition(new LatLng(animatedValue[0], animatedValue[1]));
            }
        });
        latLngAnimator.start();
        // marker.setRotation(bearing);
        //rotateMarker(marker,bearing,mMap);

        float rotate = getRotate(new LatLng(startValues[0], startValues[1]), new LatLng(endValues[0], endValues[1]));
        LogUtils.i("Rotate===>" + rotate);
        //marker.setRotation(360 - rotate + myMap.getCameraPosition().bearing);
        if (mCurrentLocation.getBearing() == 0.0)
            marker.setRotation(previousBearing);
        else
            marker.setRotation(mCurrentLocation.getBearing());
    }

    private void updateUI() {
        if (Map_Activity.p == 0) {
            distance = distance + (lStart.distanceTo(lEnd) / 1000.00);
            Map_Activity.endTime = System.currentTimeMillis();
            long diff = Map_Activity.endTime - Map_Activity.startTime;
            diff = TimeUnit.MILLISECONDS.toMinutes(diff);

            // Map_Activity.time.setText("Total Time: " + diff + " minutes");
            if (speed > 0.0)
                LogUtils.i("Speedd===>" + new DecimalFormat("0.00").format(speed) + " km/hr");
                //speed.setText("Current speed: " + new DecimalFormat("#.##").format(speed) + " km/hr");
            else {
                LogUtils.i("else Speedd===>");
                // Toast.makeText(Map_Activity.this, "DISTANCE in Map==>"+new DecimalFormat("#.###").format(distance) + " Km's.", Toast.LENGTH_SHORT).show();
            }

            lStart = lEnd;
            if (distance > 0) {

                strTotalDistance = new DecimalFormat("0.0##").format(distance);
                LogUtils.i("TOTAL DISTANCE+++>" + strTotalDistance);
                //SavePref.saveInt(context,"TotalDistance", strDistance);
                LogUtils.i("Distance in shared preference==>in mappppp" + strTotalDistance);
            } else {
                strTotalDistance = String.valueOf(distance);
            }
        }
    }

    private void saveDistanceCalculated() {
        editor.putString("calculated_distance_doubleStr", "" + distance);
        editor.putString("calculated_distance_lStart_lat", "" + lStart.getLatitude());
        editor.putString("calculated_distance_lStart_lon", "" + lStart.getLongitude());
        editor.apply();
    }

    private void restoreSavedDistanceCalculated() {
        distance = Double.parseDouble(prefs.getString("calculated_distance_doubleStr", "0"));
        double lat = Double.parseDouble(prefs.getString("calculated_distance_lStart_lat", "0"));
        double lon = Double.parseDouble(prefs.getString("calculated_distance_lStart_lon", "0"));
        lStart = new Location("");
        lStart.setLatitude(lat);
        lStart.setLongitude(lon);
    }

    private void clearSavedDistanceCalculated() {
        editor.remove("calculated_distance_doubleStr");
        editor.remove("calculated_distance_lStart_lat");
        editor.remove("calculated_distance_lStart_lon");
        editor.apply();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {

    }

    @Override
    public void onKeyExited(String key) {

    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {

    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {

    }

    public void getRequestID() {

        try {
            requestReference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId).child("request/req_id");
            listener = requestReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    reqID = dataSnapshot.getValue().toString();
                    LogUtils.i("DATASNAPSHOTTT" + reqID);
                    regID();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getReqIDFromFB() {

        if (tripID != null) {
            //Listener to get Total Price from Firebase
            requestReference = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID);
            listener = requestReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {

                        Object requestID = dataSnapshot.child("req_id").getValue();

                        reqID = String.valueOf(requestID);

                        LogUtils.i("Request ID: request ID from firebase==>" + reqID);
                        regID();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void regID() {
        if (mCurrentLocation != null) {
            upDateRequest(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            requestReference.removeEventListener(listener);
        }
    }

    public void gettripID() {

        try {
            // tripReference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId).child("accept").child("trip_id");

            // adding multiple value listener was creating problem and freezes the application.
            if (tripReference != null && tripRefValueEventListener != null) {
                tripReference.removeEventListener(tripRefValueEventListener);
            }
            tripReference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId).child("accept").child("trip_id_rider_name");
            tripListener = tripReference.addValueEventListener(tripRefValueEventListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        Object reqStatusObj = dataSnapshot.child("status").getValue();

        LogUtils.i("Request status response ===>" + reqStatusObj);
        String reqStatus = "0";

        if (reqStatusObj != null)
            reqStatus = String.valueOf(reqStatusObj);


        if (driverId != null) {

            if (reqStatus.equals("1")) {

                showDialog(dataSnapshot);

                if (excecuteonce) {
                    return;
                } else {
                    excecuteonce = true;
                    LogUtils.i("Driver id in show dialog==>" + driverId);
                    generateNotification(getApplicationContext(), "New Request Arrived");
                    playRequestTone();
                }
            } else {

                if (reqStatus.equals("0")) {
                    excecuteonce = false;
                    try {
                        if (d != null) {

                            d.dismiss();
                            stopPlaying();
                        }

                        //audioTimer.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            LogUtils.d("location updated - location: " + dataSnapshot.getValue());

        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    //get Place from Lat Lng
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {

        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        //getlocation();
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                LogUtils.w("" + strReturnedAddress.toString());


                if (strsetdestination == "updatereq") {
                    //  Toast.makeText(Map_Activity.this, "ARRIVE", Toast.LENGTH_SHORT).show();

                    txtRiderDestination.setText(strReturnedAddress.toString());
                    navigationLatLng = new LatLng(LATITUDE, LONGITUDE);
                    strPickupAddress = strReturnedAddress.toString();
                    getTerminal(riderID);

                } else if (strsetValue.matches("coming_start")) {
                    txtRiderDestination.setText(strReturnedAddress.toString());
                    navigationLatLng = new LatLng(LATITUDE, LONGITUDE);
                }


            } else {
                LogUtils.w("No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.w("Canont get Address!");
        }
        return strAdd;
    }

    public void showTripSummaryDialog() {

        if (!isFinishing())
            dialogTripSummary.show();


        //createDistanceFireBase(); //Change
        ratingcheck = "update";
        Button btnmap = (Button) dialogTripSummary.findViewById(R.id.btnGoMap);
        trip_rider_name = (TextView) dialogTripSummary.findViewById(R.id.trip_rider_name);
        txtTotalDistance = (TextView) dialogTripSummary.findViewById(R.id.text_trip_completed);
        txtTripAmount = (TextView) dialogTripSummary.findViewById(R.id.trip_amount);
        imgRiderProfile = (ImageView) dialogTripSummary.findViewById(R.id.trip_end_profile);
        companyname = (TextView) dialogTripSummary.findViewById(R.id.compname);
        companyfee = (TextView) dialogTripSummary.findViewById(R.id.compfee);
        txtTripdate = (TextView) dialogTripSummary.findViewById(R.id.trip_date);
        final ImageView driverEmoji = (ImageView) dialogTripSummary.findViewById(R.id.driver_emoji);

        //flexibleRatingBar=(FlexibleRatingBar)dialogTripSummary.findViewById(R.id.flexibleRatingBar);
        final FlexibleRatingBar driverRatingBar = (FlexibleRatingBar) dialogTripSummary.findViewById(R.id.driver_ratingBar);

        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        LogUtils.i("CRRENT DATE TIME" + currentDateTimeString);
        // textView is the TextView view that should display it
        editor.putString("lastTripTime", currentDateTimeString);
        editor.apply();
        txtTripdate.setText(currentDateTimeString);

        String Tripid = null;
        if (tempTripWithName != null) {
            String[] tripidArray = tempTripWithName.split(":");
            Tripid = tripidArray[0];
            LogUtils.i("strtoolfee" + Tripid + "tripsids" + tripID);
            tollfrees.remove(Tripid);
            saveArray(tollfrees, "tolltripidarray", getApplicationContext());
        }


        if (Tripid != null) {
            faretoolreference = FirebaseDatabase.getInstance().getReference().child("trips_data").child(Tripid).child("tollfee");
            faretoolreference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String strtoolfee = dataSnapshot.getValue().toString();
                    LogUtils.i("fire base tool fees" + strtoolfee);
                    // toolsamount=strtoolfee;
                    setToll(strtoolfee);

                    //getFareCalculation();

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    LogUtils.w("Failed to read value " + error.toException());
                }
            });
        }


        LogUtils.i("list size==>" + String.valueOf(tripIDs.size()));

        if (tripIDs.isEmpty()) {
            checkAcceptStatus();
        }

        try {

            driverRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

                @Override
                public void onRatingChanged(RatingBar arg0, float rateValue, boolean arg2) {
                    // TODO Auto-generated method stub
                    LogUtils.d("Rating " + "your selected value is:" + rateValue);
                    if (ratingcheck.equals("notupdate")) {
                        ratingcheck = "update";
                    } else if (ratingcheck.equals("update")) {
                        String rating = String.valueOf(rateValue);
                        LogUtils.i("rating of driver app" + rating);
                        updateDriverRating(rating);
                        int ratingInt = Math.round(rateValue);
                        switch (ratingInt) {
                            case 1:
                                driverEmoji.setBackgroundResource(R.drawable.one);
                                break;
                            case 2:
                                driverEmoji.setBackgroundResource(R.drawable.two);
                                break;
                            case 3:
                                driverEmoji.setBackgroundResource(R.drawable.three);
                                break;
                            case 4:
                                driverEmoji.setBackgroundResource(R.drawable.four);
                                break;
                            case 5:
                                driverEmoji.setBackgroundResource(R.drawable.five);
                                break;
                            case 0:
                                driverEmoji.setBackgroundResource(R.drawable.none);
                                break;
                        }
                    }
                }
            });

            trip_rider_name.setText(riderFirstName + " " + riderLastName);

            Glide.with(Map_Activity.this)
                    .load(strRiderProfile)
                    .error(R.drawable.account_circle)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .transform(new RoundImageTransform(Map_Activity.this))
                    .into(imgRiderProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ratingcheck = "notupdate";
                driverRatingBar.setRating(0);

                surgePrice = null;

                tripIDs.remove(tempTripWithName);
                LogUtils.i("Trip with Name" + tempTripWithName);
                LogUtils.i("Trip Size" + tripIDs.size());
                saveArray(tripIDs, "tripidarray", getApplicationContext());


                if (tripIDs.size() == 0) {
                    if (driverId != null && !driverId.isEmpty()) {
                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId).child("accept");
                        Map<String, Object> taskMap2 = new HashMap<>();
                        taskMap2.put("tollfee", "0");
                        databaseReference2.updateChildren(taskMap2);
                    }

                }

                if (tripIDs.size() < 1) {
                    tripIDs.clear();
                    getState.putString("tripstate", "endClicked");
                    //mMap.clear();
                    LogUtils.i("Trip Size" + tripIDs.size());
                    endTripLayout.setVisibility(View.GONE);
                    bottomBar.setVisibility(View.VISIBLE);
                    toolbarLayout.setVisibility(View.VISIBLE);
                    routeNavigate.setVisibility(View.GONE);
                    startTripLayout.setVisibility(View.GONE);
                    destinationLayout.setVisibility(View.GONE);

                    destLocation = null;
                    distance = 0;

                    //clear toll value to 0
                    previousToll = 0;
                    getState.putInt("tollfee", previousToll);
                    getState.apply();

                    strTotalDistance = "0";
                    tripState = "tripnotend";
                    lStart = null;
                    //  removeRatingListener();
                    mMap.clear();

                    if (latlngs != null)
                        latlngs.clear();

                    if (points != null)
                        points.clear();

                    if (wayPaintsMarker != null)
                        wayPaintsMarker.clear();

                    for (Polyline mulPoly : Map_Activity.this.wayPaintsPolyline.values()) {
                        mulPoly.remove();
                    }


                    if (mCurrentLocation != null) {
                        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

                        LogUtils.i("INSIDE ENDTRIP CHANGE" + mCurrentLocation.getLatitude() + mCurrentLocation.getLongitude());
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(latLng)                              // Sets the center of the map to current location
                                .zoom(Constants.MAP_ZOOM_SIZE)
                                .tilt(0)                                     // Sets the tilt of the camera to 0 degrees
                                .build();
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        if (currentLocMarker == null) {

                            currentLocMarker = mMap.addMarker(new MarkerOptions()
                                    .icon(mapCarIcon)
                                    .position(latLng)
                                    .flat(true)
                                    .anchor(0.5f, 0.5f)
                                    .rotation(mCurrentLocation.getBearing()));
                        } else {

                            currentLocMarker.setPosition(latLng);
                        }

                    }
                    checkAcceptStatus();
                    easyTimer.stop();
                    Intent i = new Intent(Map_Activity.this, Map_Activity.class);
                    //int id = Map_Activity.this.getTaskId();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        finishAndRemoveTask();
                    } else
                        finish();
                    //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
                if (tripIDs.size() == 1) {
                    String[] tripidArray = tripIDs.get(0).split(":");
                    String Tripid = tripidArray[0];
                    String userName = tripidArray[1].replaceAll("%20", " ");
                    LogUtils.i("INSIDE TRIP 1" + Tripid + userName);

                    tripID = Tripid;

                    getState.putString("tripID", Tripid);
                    getState.apply();

                    tripState = "lasttrip";

                    //getendState();
                    Intent i = new Intent(getApplicationContext(), Map_Activity.class);
                    startActivity(i);
                }

                if (tripIDs.size() >= 1) {
                    //get Last Index
                    String[] tripidArray1 = tripIDs.get(tripIDs.size() - 1).split(":");
                    String Tripid1 = tripidArray1[0];
                    String userName1 = tripidArray1[1];
                    LogUtils.i("Last Trip ID " + Tripid1 + userName1);

                    getstatusfromfirebase(Tripid1);
                }

                createRidersNameList();

                dialogTripSummary.dismiss();
            }
        });
    }

    private void getFareCalculation(String surgeprice, int totalminutes) {
        double strWebprice;

        if (strCategory != null) {
            LogUtils.i("Price per KM in fare" + strWebprice_km);
            LogUtils.i("Price Per Min in fare" + strwebpricepermin);
            LogUtils.i("Max Size in fare" + strwebmaxsize);
            LogUtils.i("Price Fare in fare" + strwebpricefare);
            LogUtils.i("Total Distance in fare" + strTotalDistance);
            LogUtils.i("Total minutes in fare" + totalminutes);
            LogUtils.i("TollFee through out the trip" + getToll());
            float tolDistance;

            if (strTotalDistance == null) {
                strTotalDistance = String.valueOf(0);
                if (strwebpricefare != null && strwebpricepermin != null) {

                    double intWebPriceFare, intWebPricePerMin, web_price_fare;

                    LogUtils.i("after par web price fare ===>" + strwebpricefare);
                    LogUtils.i("after par web price min===>" + strwebpricepermin);

                    if (isDouble(strwebpricefare)) {

                        intWebPriceFare = Double.parseDouble(strwebpricefare);
                    } else {

                        intWebPriceFare = Integer.parseInt(strwebpricefare);
                    }

                    if (isDouble(strwebpricepermin)) {

                        intWebPricePerMin = Double.parseDouble(strwebpricepermin);

                    } else {

                        intWebPricePerMin = Integer.parseInt(strwebpricepermin);
                    }

                    web_price_fare = intWebPriceFare + intWebPricePerMin;

                    LogUtils.i("after par web price fare ===>" + intWebPriceFare);
                    LogUtils.i("after par web price min===>" + intWebPricePerMin);
                    LogUtils.i("without round ===>" + web_price_fare);

                    DecimalFormat df2 = new DecimalFormat("0.00");
                    //df2.setRoundingMode(RoundingMode.UP);
                    LogUtils.i("after round ===>" + df2.format(web_price_fare));


                    web_price_fare = Double.parseDouble(df2.format(web_price_fare));

                    strTotalPrice = String.valueOf(web_price_fare);

                } else {
                    strTotalPrice = String.valueOf(0);
                }

                strTotalPrice = convertToDecimal(Double.parseDouble(strTotalPrice));

                txtTotalDistance.setText("Total Distance : " + strTotalDistance + " KM");
                //txtTripAmount.setText("$" + strTotalPrice);
                setTotalPrice(strTotalPrice);
                //updateDistanceFireBase();
                //getRating();
                endUpdateTrip();

            } else {
                if (strTotalDistance == null) {
                    strTotalDistance = String.valueOf(0);
                }
                if (strWebprice_km == null) {
                    strWebprice_km = String.valueOf(0);
                }

                tolDistance = Float.parseFloat(strTotalDistance);
                strWebprice = Double.parseDouble(strWebprice_km);
                LogUtils.i("Total minutes in fare" + totalminutes);
                try {
                    double totalAmount;
                    LogUtils.i("tool fee added" + getToll());

                    if (getToll() != null && !getToll().isEmpty()) {
                        totalAmount = (tolDistance * strWebprice) + Float.parseFloat(String.valueOf(strwebpricefare)) + Double.parseDouble(getToll()) + Double.parseDouble(strbookfee);
                    } else {
                        totalAmount = (tolDistance * strWebprice) + Float.parseFloat(String.valueOf(strwebpricefare)) + Double.parseDouble(strbookfee);
                    }

                    //totalAmount=totalAmount+Integer.parseInt(getToll());
                    LogUtils.i("Before adding totalminute TTTTTTTTTTTTTTTTTTT" + totalAmount);
                    totalAmount = totalAmount + totalminutes * Float.parseFloat(String.valueOf(strwebpricepermin));
                    LogUtils.i("TTTTTTTTTTTTTTTTTTT" + totalAmount);
                    LogUtils.i("TTTTTTTTTTTTTTTTTTT" + Float.parseFloat(String.valueOf(strwebpricepermin)));

                    LogUtils.i("Calcu distanve" + tolDistance);
                    LogUtils.i("calculated total price" + strWebprice);

                    if (airportamt != 0.0) {
                        totalAmount = totalAmount + airportamt;
                    }
                    double taxpercent, taxaddamount;

                    try {
                        taxpercent = (totalAmount * (Double.parseDouble(strtaxpercentage) / 100.0f));
                        taxaddamount = totalAmount + taxpercent;
                        LogUtils.i("After adding tax percentage of" + taxpercent + "to" + taxaddamount);
                        totalAmount = taxaddamount;
                    } catch (Exception e) {
                        taxpercent = 0.0;
                        taxaddamount = totalAmount + taxpercent;
                        totalAmount = taxaddamount;
                        e.printStackTrace();
                    }

                    //surge price
                    if (!surgeprice.equals("0")) {
                        try {
                            k = (totalAmount * (Double.parseDouble(surgeprice) / 100.0f));
                            surgeaddamount = totalAmount + k;
                            // Toast.makeText(Map_Activity.this, "Surge price percentage of "+surgeprice+" was added", Toast.LENGTH_SHORT).show();
                            LogUtils.i("After adding surge price of" + k + "to" + surgeaddamount);
                            totalAmount = surgeaddamount;
                        } catch (Exception e) {
                            // Toast.makeText(Map_Activity.this, "Surge added failed"+e, Toast.LENGTH_SHORT).show();
                            k = 0.0;
                            surgeaddamount = totalAmount + k;
                            totalAmount = surgeaddamount;
                            e.printStackTrace();
                        }
                    }

                    if (strDropPrice == null)
                        strDropPrice = "0";

                    double dropPrice;

                    if (isDouble(strDropPrice))
                        dropPrice = Double.parseDouble(convertToDecimal(Double.parseDouble(strDropPrice)));
                    else
                        dropPrice = Double.parseDouble(convertToDecimal((double) Integer.parseInt(strDropPrice)));

                    totalAmount = totalAmount + getWaypointTotal() * dropPrice;

                    LogUtils.i("The total amount inside waypoint" + getWaypointTotal() * dropPrice);

                    LogUtils.i("Total Amount too be displayed" + totalAmount);
                    LogUtils.i("Price Fare in fare" + strwebpricefare);
                    String strCalculatedDistance = String.valueOf(totalAmount);
                    LogUtils.i("Total calu===" + strCalculatedDistance);
                    double d = Double.parseDouble(String.valueOf(strCalculatedDistance));
                    strTotalPrice = convertToDecimal(d);
                    txtTotalDistance.setText("Total Distance : " + strTotalDistance + " KM");
                    //txtTripAmount.setText("$" + convertToDecimal(d));
                    setTotalPrice(strTotalPrice);
                    //save to firebase
                    //updateDistanceFireBase();
                    //getRating();
                    endUpdateTrip();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String convertToDecimal(Double amount) {

        if (amount > 0) {
            LogUtils.i("THE AMOUNT IS" + new DecimalFormat("0.00").format(amount));
            //df2.setRoundingMode(RoundingMode.UP);
            return new DecimalFormat("0.00").format(amount);
        } else {
            return String.valueOf(0);
        }
    }

    boolean isDouble(String str) {
        try {

            Double.parseDouble(str);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void checkWaypointCount(final String surge, final int minutes) {

        //checkAccepRef = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId + "/accept/status");
        //  Toast.makeText(Map_Activity.this, "the waypoint is"+waypointcount, Toast.LENGTH_SHORT).show();
        try {
            DatabaseReference checkwaypointcount = FirebaseDatabase.getInstance().getReference().child("riders_location").child(riderID).child("WayPointCount");

            checkwaypointcount.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {

                        setWaypointcount(Integer.parseInt(dataSnapshot.getValue().toString()));
                        // Toast.makeText(Map_Activity.this, "the waypoint is"+waypointcount, Toast.LENGTH_SHORT).show();
                        getFareCalculation(surge, minutes);
                        updatewaycount();
                    } else {

                        getFareCalculation(surge, minutes);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Toast.makeText(Map_Activity.this, "the waypoint is"+waypointcount+databaseError, Toast.LENGTH_SHORT).show();

                    getFareCalculation(surge, minutes);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatewaycount() {
        Map<String, Object> wayPointsCount = new HashMap<>();
        DatabaseReference wayPointcountRef = FirebaseDatabase.getInstance().getReference().child("riders_location").child(riderID);
        wayPointsCount.put("WayPointCount", 0);
        wayPointcountRef.updateChildren(wayPointsCount);
    }

    public void checkAcceptStatus() {

        //checkAccepRef = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId + "/accept/status");

        try {
            checkAccepRef = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID + "/status");

            checkAccepRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String acceptStatus = dataSnapshot.getValue().toString();
                        if (acceptStatus != null && !acceptStatus.isEmpty()) {
                            if (acceptStatus.matches("4")) {
                                clearFirebaseData();
                            }

                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checktripcancelstatus() {
        try {
            //checkTripCancelRef = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId + "/accept/status");
            LogUtils.i("INSIDE THE CANCEL" + tripID);
            checkTripCancelRef = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID + "/status");

            checkTripCancelRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String tripCancelStatus = dataSnapshot.getValue().toString();
                        if (tripCancelStatus != null && !tripCancelStatus.isEmpty()) {
                            if (tripCancelStatus.matches("5")) {
                                try {
                                    clearFirebaseData();
                                    getCancelState();

                                    if (strCacnelStatus != null) {
                                        if (strCacnelStatus.equals("drivercliked")) {
                                            //strCacnelStatus = "mt";
                                            LogUtils.i("Trip ID Size inside Empty " + tripIDs.size());

                                      /*  if(tripIDs.size()==0) {

                                            mMap.clear();
                                            getState.putString("tripstate", "endClicked");
                                            getState.apply();
                                             Intent intent = new Intent(Map_Activity.this, Map_Activity.class);
                                             startActivity(intent);
                                        }
    */

                                        } else {
                                            LogUtils.i("Trip ID Size" + tripIDs.size());
                                            generateNotification(getApplicationContext(), "Rider Cancelled the Trip");
                                            excecutecancelonce = false;
                                            getState.putString("tripstate", "endClicked");
                                            getState.apply();
                                            //showCancelDialog();
                                        }

                                    } else {
                                        LogUtils.i("Trip ID Size" + tripIDs.size());
                                        generateNotification(getApplicationContext(), "Rider Cancelled the Trip");
                                        excecutecancelonce = false;
                                        getState.putString("tripstate", "endClicked");
                                        getState.apply();
                                        //showCancelDialog();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearTripState(String tripid) {
        //changes on(17/3/2017)
        LogUtils.i("THE TRIP ID IS You Click Cancel" + tripid);
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripid);

        Map<String, Object> taskMap2 = new HashMap<>();
        taskMap2.put("status", "5");
        databaseReference2.updateChildren(taskMap2);
    }

    public void clearFirebaseData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId).child("accept");
        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("status", "0");
        taskMap.put("trip_id", "empty");
        taskMap.put("trip_id_rider_name", "empty");
        databaseReference.updateChildren(taskMap);

       /* DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID);

        Map<String, Object> taskMap2 = new HashMap<String, Object>();
        taskMap2.put("status", "0");

        databaseReference2.updateChildren(taskMap);*/

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId).child("request");
        Map<String, Object> taskMap1 = new HashMap<>();
        taskMap1.put("eta", "0");
        taskMap1.put("distance", "0");
        taskMap1.put("pickupAddress", "0");
        taskMap1.put("estFare", "0");
        taskMap1.put("dropAddress", "0");
        taskMap1.put("req_id", "");
        taskMap1.put("status", "0");
        taskMap1.put("estFare", "0");
        // taskMap1.put("rider_id", "0");
        databaseReference1.updateChildren(taskMap1);

        if (tripIDs.size() < 1) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("drivers_data").child(driverId).child("accept").child("trip_id").setValue("0");
            ref.child("drivers_data").child(driverId).child("accept").child("trip_id_rider_name").setValue("0");
        }
    }

    public void showDialog(final DataSnapshot dataSnapshot) {
        LogUtils.w("INSIDE DIALOG!!!");

        LogUtils.w("driverId - " + driverId);

        ridercanceltripreference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId).child("request").child("rider_id");
        ridercanceltripreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {

                    String riderid = dataSnapshot.getValue().toString();

                    if (!riderid.equals("0") & !riderid.equals("")) {

                        ridercancel(riderid);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                LogUtils.w("Failed to read value - " + error.toException());
            }
        });
        //to avoid bad token exception
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {

                    Window window = Map_Activity.this.getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                    window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                    window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

                    try {
                        d.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //audioTimer.start();

                    Object distance = dataSnapshot.child("distance").getValue();
                    Object dropAddress = dataSnapshot.child("dropAddress").getValue();
                    Object ETA = dataSnapshot.child("eta").getValue();
                    Object pickupAddress = dataSnapshot.child("pickupAddress").getValue();
                    Object estFare = dataSnapshot.child("estFare").getValue();

                    LogUtils.i("distance response ===>" + distance);
                    LogUtils.i("dropAddress response ===>" + dropAddress);
                    LogUtils.i("ETA response ===>" + ETA);
                    LogUtils.i("pickupAddress response ===>" + pickupAddress);
                    LogUtils.i("estFare response ===>" + estFare);

                    //set views
                    try {
                        pickuploc.setText(String.valueOf(pickupAddress));
                        droploc.setText(String.valueOf(dropAddress));
                        progeta.setText("ETA: " + String.valueOf(ETA));
                        progDistance.setText("Distance: " + String.valueOf(distance));
                        progestfare.setText("Estimated Fare: $" + String.valueOf(estFare));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    progresslayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            volon.setVisibility(View.VISIBLE);

                            //audioTimer.stop();
                            getRequestID();
                            excecuteonce = false;
                            getState.putString("tripstate", "requestAccept");
                            getState.apply();
                            tripState = "newRequest";
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId).child("accept");
                            //DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID);
                            Map<String, Object> taskMap = new HashMap<>();
                            taskMap.put("status", "1");
                            //taskMap.put("trip_id", "");
                            if (tripIDs.size() > 1) {
                                taskMap.put("trip_id", "0");
                                taskMap.put("trip_id_rider_name", "0");
                            }
                            databaseReference.updateChildren(taskMap);

                            if (driverId != null) {
                                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId);
                                Map<String, Object> taskMap1 = new HashMap<>();
                                taskMap1.put("cancel_trip_id", "0");
                                databaseReference1.updateChildren(taskMap1);

                            }

                            //databaseReference2.updateChildren(taskMap);
                            //progressLayout.setVisibility(View.GONE);
                            if (d != null) {
                                d.dismiss();

                            }
                            stopPlaying();

                            //checktripcancelstatus();
                            easyTimer.start();
                            easyTimer.setOnTaskRunListener(new EasyTimer.OnTaskRunListener() {
                                @Override
                                public void onTaskRun(long past_time, String rendered_time) {
                                    // Change UI or do something with past_time and rendered_time.
                                    // It will NOT block the UI thread.
                                    showCancelDialog();
                                }
                            });

                            animY = ObjectAnimator.ofFloat(riderinfo, "translationY", -100f, 0f);
                            animY.setDuration(2000);//1sec
                            animY.setInterpolator(new BounceInterpolator());
                            animY.setRepeatCount(1);
                            animY.start();

                            btnArriveNow.setEnabled(false);
                            dynamic.setEnabled(false);
                            dynamic1.setEnabled(false);
                            dynamic2.setEnabled(false);
                            gettripID();
                            getAcceptState();

                        }
                    });

                }
            }
        });

        if (driverId != null) {
            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId);
            Map<String, Object> taskMap1 = new HashMap<>();
            taskMap1.put("cancel_trip_id", "0");
            databaseReference1.updateChildren(taskMap1);
        }

        if (mapBitmap != null) {

            setMapImage(mapBitmap);

        } else {

            SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
            String mapBitmapString = prefs.getString("mapImage", null);

            LogUtils.i("am in else of bitmap");
            mapBitmap = StringToBitMap(mapBitmapString);

            if (mapBitmap != null) {

                LogUtils.i("am in else of bitmap1");

                setMapImage(mapBitmap);
            }
        }

        pwOne.resetCount();
        pwOne.startSpinning();
    }

    public void setMapImage(Bitmap mapImageBitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mapImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        Glide.with(getApplicationContext()).load(stream.toByteArray()).asBitmap().centerCrop().skipMemoryCache(true).into(new BitmapImageViewTarget(requestMapView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                requestMapView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    public void getAcceptState() {
        //getRequestID();
        getReqIDFromFB();
        if (d != null) {
            d.dismiss();
            stopPlaying();
        }

        //audioTimer.stop();
        // btnArriveNow.setEnabled(false);
        arriveNowFunction();
    }

    private void arriveNowFunction() {
        //gettripID(); //change

        arriveNowLayout.setVisibility(View.VISIBLE);
        startTripLayout.setVisibility(View.GONE);
        endTripLayout.setVisibility(View.GONE);
        //showDialog("0");

        toolbarLayout.setVisibility(View.GONE);
        routeNavigate.setVisibility(View.VISIBLE);
        bottomBar.setVisibility(View.GONE);
        destinationLayout.setVisibility(View.VISIBLE);

        enableDisableVoiceNavButt();
    }

    public void getStartState() {
        //getRequestID();
        getReqIDFromFB();
        arriveNowLayout.setVisibility(View.GONE);
        startTripLayout.setVisibility(View.VISIBLE);
        endTripLayout.setVisibility(View.GONE);
        destinationLayout.setVisibility(View.VISIBLE);
        toolbarLayout.setVisibility(View.GONE);
        routeNavigate.setVisibility(View.VISIBLE);
        bottomBar.setVisibility(View.GONE);

        enableDisableVoiceNavButt();

        strsetValue = "coming_arrive";
        updateArriveRequest();
    }

    public void getOnTripState() {
        //  removeTripListener();

        previousToll = state.getInt("tollfee", 0);

        arriveNowLayout.setVisibility(View.GONE);
        endTripLayout.setVisibility(View.VISIBLE);
        destinationLayout.setVisibility(View.VISIBLE);
        toolbarLayout.setVisibility(View.GONE);
        routeNavigate.setVisibility(View.VISIBLE);
        bottomBar.setVisibility(View.GONE);
        strsetValue = "coming_start";
        startUpdateTrip();
        enableDisableVoiceNavButt();
        LogUtils.i("---- begin from getOnTripState");
        strDistanceBegin = "distancebegin";
        distance = 0;
        restoreSavedDistanceCalculated();
    }

    public void removeRatingListener() {
        ratingReference.removeEventListener(ratingListener);
    }

    public void showProgressDialog() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Accepting...");
        if (!Map_Activity.this.isFinishing()) {
            progressDialog.show();
        }
    }

    public void dismissProgressDialog() {
       /* btnArriveNow.setEnabled(true);
        dynamic.setEnabled(true);
        dynamic1.setEnabled(true);
        dynamic2.setEnabled(true);*/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                } catch (Exception e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        });
    }

    //generateNotifications
    private void generateNotification(Context context, final String message) {
        LogUtils.i("INSIDE COUNT ZERO");
        //Some Vars
        final int NOTIFICATION_ID = 1; //this can be any int
        String title = "SIX Driver";
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Building the Notification
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(largeIcon);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setLights(Color.RED, 3000, 3000);
        if (!message.equals("New Request Arrived")) {
            builder.setSound(uri);
        }
        builder.setAutoCancel(true);

        builder.getNotification().flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;


        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (message.contains("Toll Detected:")) {

                    if (removingMarkerPoint != null) {

                        tollMarkersMap.remove(removingMarkerPoint);
                        LogUtils.i("Lis of postion!!!!!" + tollMarkersMap);
                        removingMarkerPoint = null;
                    }
                }

                notificationManager.cancel(NOTIFICATION_ID);
                timer.cancel();
            }
        }, 10000, 1000);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            if (isOnlineButtunClicked()) {
                editor.putString("onlineStartDate", null);
                setonline("0", String.valueOf(getDuration()));
                editor.apply();
            }
            stopService(new Intent(getBaseContext(), Services.class).setPackage(this.getPackageName()));
            if (keyRef != null) {
                keyRef.removeEventListener(this);
                keyRef = null;
            }
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

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mMapLocationListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mMapLocationListener = null;
    }

    public void onLocationReceived(Location location) {

        System.out.println(location.getProvider() + "," + location.getLatitude() + "," + location.getLongitude());
        mCurrentLocation = location;
        updateLocationFDBrMongoDB(mCurrentLocation);
    }

    public void updateLocationFDBrMongoDB(Location location) {

        if (driverId != null && !driverId.isEmpty() && !driverId.trim().matches("")) {

            //String strOnlineStatus = onlineTxt.getText().toString().trim();

            if (isOnlineButtunClicked()) {
                //Update current location in Firebase
                updateLocationToFirebase(location);
                if (tripState.matches("endClicked")) {
                    LogUtils.i("trip state in update location==>" + tripState);
                    getUpdateLocation(location.getLatitude(), location.getLongitude());
                }
                //Updating the current location to Database
                //getUpdateLocation(location.getLatitude(), location.getLongitude());
                //3

            } else {

                if (tripState.matches("endClicked")) {
                    getUpdateLocation(0.0, 0.0);
                }
                //getUpdateLocation(0.0, 0.0);
            }
        }
    }

    public void updateLocationToFirebase(Location location) {

        mCurrentLocation = location;

        if (mCurrentLocation != null) {

            if (driverId != null && !driverId.isEmpty() && !driverId.trim().matches("")) {

                if (isOnlineButtunClicked()) {
                    //Updating the current location to Database
                    //getUpdateLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    //new getUpdateLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()).execute();
                    LogUtils.i("location which is updated in==>" + mCurrentLocation.getLatitude() + "<====>" + mCurrentLocation.getLongitude());
                    this.geoFire.setLocation(driverId, new GeoLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), previousBearing, new GeoFire.CompletionListener() {
                        //this.geoFire.offlineLocation(driverId, new GeoLocation(0.0, 0.0), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (error != null) {
                                LogUtils.i("There was an error saving the location to GeoFire: " + mCurrentLocation.getLatitude() + mCurrentLocation.getLongitude());
                            } else {
                                LogUtils.i("Online Location saved on server successfully!");
                            }
                        }
                    });
                } else {
                    //getUpdateLocation(0.0, 0.0);
                    //new getUpdateLocation(0.0,0.0).execute();

                    this.geoFire.offlineLocation(driverId, new GeoLocation(0.0, 0.0), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (error != null) {
                                LogUtils.i("There was an error saving the location to GeoFire: " + mCurrentLocation.getLatitude() + mCurrentLocation.getLongitude());
                            } else {
                                LogUtils.i("Offline Location saved on server successfully!");
                            }
                        }
                    });
                }
            } else {
                this.geoFire.setLocation("geolocation", new GeoLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), previousBearing, new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (error != null) {
                            LogUtils.i("There was an error saving the location to GeoFire: " + mCurrentLocation.getLatitude() + mCurrentLocation.getLongitude());
                        } else {
                            LogUtils.i("Offline Location saved on server successfully!");
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction != null) {
            if (direction.isOK()) {

                if (orginLocation != null && destLocation != null) {

                    onDirectionSuccessMarkerPlacing(direction);
                }
            }
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        LogUtils.i("DIRECTION " + t);
    }

   /* private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            myService = binder.getService();
            distanceStatus = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            distanceStatus = false;
        }
    };*/

   /* void unbindService() {
        if (!distanceStatus)
            return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        unbindService(sc);
        distanceStatus = false;
    }*/

    public void onDirectionSuccessMarkerPlacing(Direction direction) {

        /// LatLng prevLatLng = new LatLng(0, 0);
        LatLng curPos = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        if (curPos.latitude != 0 && curPos.longitude != 0) {

            zoomCameraToPosition(curPos);
        }

        if (currentLocMarker == null) {

            //mMap.clear();
            currentLocMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())).icon(mapCarIcon));
            currentLocMarker.setAnchor(0.5f, 0.5f);
            currentLocMarker.setFlat(true);
            if (mCurrentLocation.getBearing() == 0.0)
                currentLocMarker.setRotation(previousBearing);
            else
                currentLocMarker.setRotation(mCurrentLocation.getBearing());
            //currentLocMarker.setRotation(mCurrentLocation.getBearing());

            //currentLocMarker.remove();
        } else {

            if (mCurrentLocation.getBearing() != 0.0)
                previousBearing = mCurrentLocation.getBearing();

            currentLocMarker.setPosition(curPos);
            if (mCurrentLocation.getBearing() == 0.0)
                currentLocMarker.setRotation(previousBearing);
            else
                currentLocMarker.setRotation(mCurrentLocation.getBearing());
            // currentLocMarker.setRotation(mCurrentLocation.getBearing());

            if (prevLatLng != new LatLng(0, 0)) {

                if (!(curPos.equals(prevLatLng))) {

                    double[] startValues = new double[]{prevLatLng.latitude, prevLatLng.longitude};

                    double[] endValues = new double[]{curPos.latitude, curPos.longitude};

                    LogUtils.i("Start location===>" + startValues[0] + "  " + startValues[1]);
                    LogUtils.i("end location===>" + endValues[0] + "  " + endValues[1]);


                    animateMarkerTo(currentLocMarker, startValues, endValues, mCurrentLocation.getBearing());

                } else {
                    if (mCurrentLocation.getBearing() == 0.0)
                        currentLocMarker.setRotation(previousBearing);
                    else
                        currentLocMarker.setRotation(mCurrentLocation.getBearing());
                    //currentLocMarker.setRotation(mCurrentLocation.getBearing());
                }
            } else {
                currentLocMarker.setPosition(curPos);
                if (mCurrentLocation.getBearing() == 0.0)
                    currentLocMarker.setRotation(previousBearing);
                else
                    currentLocMarker.setRotation(mCurrentLocation.getBearing());
                //currentLocMarker.setRotation(mCurrentLocation.getBearing());
            }

            prevLatLng = new LatLng(curPos.latitude, curPos.longitude);

            LogUtils.i("direction list===>" + direction);
            List<Leg> leg = direction.getRouteList().get(0).getLegList();
            step = leg.get(0).getStepList();

            String final_instruct = getStringfromhtml(step.get(0).getHtmlInstruction());

            // Check whether the route have more than one leg
            try {
                if (leg.size() > 1) {
                    // include next leg only if it within 130 m
                    if (Integer.parseInt(leg.get(0).getDistance().getValue()) <= 130) {
                        if (leg.get(1).getStepList().get(0).getManeuver() != null)
                            final_instruct = getStringfromhtml(leg.get(1).getStepList().get(0).getHtmlInstruction()) + " at " + leg.get(0).getDistance().getText();
                        else
                            final_instruct = getStringfromhtml(leg.get(1).getStepList().get(0).getHtmlInstruction());
                    }

                } else {
                    //if a leg (Waypoint) has more than one step then include next step also
                    if (step.size() > 1) {
                        // Add the next step only if it's within 130 m
                        if (Integer.parseInt(step.get(0).getDistance().getValue()) <= 130) {
                            // Check whether this step have turn instruction.
                            if (step.get(1).getManeuver() != null)
                                final_instruct = getStringfromhtml(step.get(1).getHtmlInstruction()) + " at " + step.get(0).getDistance().getText();
                            else
                                final_instruct = getStringfromhtml(step.get(1).getHtmlInstruction());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // To display a instruction only once
            if (!previous_final_instruct.equals(final_instruct)) {
                startAction(final_instruct);
            }

            if (previous_final_instruct.isEmpty())
                startAction(final_instruct);

            previous_final_instruct = final_instruct;
        }

        if (currentLocMarker.getPosition() != null) {
            if (currentLocMarker.getPosition().latitude != 0 && currentLocMarker.getPosition().longitude != 0) {
                if (mCurrentLocation.getBearing() != 0.0) {
                    final CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(currentLocMarker.getPosition())      // Sets the center of the map to Mountain View
                            //.zoom(Constants.MAP_ZOOM_SIZE_ONTRIP)      // Sets the zoom
                            .bearing(mCurrentLocation.getBearing())                // Sets the orientation of the camera to east
                            .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    CameraUpdate center = CameraUpdateFactory.newLatLng(curPos);
                    mMap.animateCamera(center, 400, null);
                } else {
                    final CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(currentLocMarker.getPosition())      // Sets the center of the map to Mountain View
                            //.zoom(Constants.MAP_ZOOM_SIZE_ONTRIP)      // Sets the zoom
                            .bearing(previousBearing)                // Sets the orientation of the camera to east
                            .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    CameraUpdate center = CameraUpdateFactory.newLatLng(curPos);
                    mMap.animateCamera(center, 400, null);
                }
            }
        }

        try {
            if (strsetValue.matches("coming_start")) {


                if (pickUPrDropMarker != null)
                    pickUPrDropMarker.remove();

                // before loop:
                LogUtils.i("destLocation location ===>" + destLocation);
                pickUPrDropMarker = mMap.addMarker(new MarkerOptions().position(destLocation).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ub__ic_pin_dropoff)));


            } else {

                if (pickUPrDropMarker != null)
                    pickUPrDropMarker.remove();

                LogUtils.i("destLocation location ===>" + destLocation);
                pickUPrDropMarker = mMap.addMarker(new MarkerOptions().position(destLocation).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ub__ic_pin_pickup)));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();


        if (routePolyline != null) {
            routePolyline.setPoints(directionPositionList);

        } else {
            routePolyline = mMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.BLUE));
        }

        if (jsonTollObject != null)
            new getTollDeatilsAsycn(directionPositionList, tollMarkersMap, Map_Activity.this, jsonTollObject).execute();
    }

    private String getStringfromhtml(String htmlstring) {
        return Html.fromHtml(htmlstring).toString().trim();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.i("inside the on start");
        active = true;
        Constants.MAP_ISSHOWING = true;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        if (!client.isConnected()) {
            client.connect();
        }
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        updateSmartLocation();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Map_ Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.tommy.driver/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
        Constants.MAP_ISSHOWING = false;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Map_ Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.tommy.driver/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        if (navcard.isShown())
            navcard.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Constants.MAP_ISSHOWING = false;
        handler.removeCallbacksAndMessages(null);

        stopTTS();

        if (tripReference != null && tripRefValueEventListener != null) {
            tripReference.removeEventListener(tripRefValueEventListener);
        }

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        if (client != null) {
            client.disconnect();
        }

        if (googleLocationService != null) {
            googleLocationService.stopLocationUpdates();
        }

        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }

        if (navcard.isShown())
            navcard.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Constants.MAP_ISSHOWING = false;
        //handler.removeCallbacksAndMessages(null);
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.

        if (navcard.isShown())
            navcard.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.MAP_ISSHOWING = true;
        updateSmartLocation();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        if (textToSpeech == null) {
            initializeTTS();
        }
    }

    @Override
    public void onClick(View v) {

        tempTripWithName = String.valueOf(v.getTag());

        //clear polyline marker
        for (Polyline mulPoly : Map_Activity.this.wayPaintsPolyline.values()) {
            mulPoly.remove();
        }

        for (Marker marker : this.wayPaintsMarker.values()) {
            marker.remove();
        }
        if (wayPaintsMarker != null)
            wayPaintsMarker.clear();


        String[] tripidArray = tempTripWithName.split(":");
        String Tripid = tripidArray[0];
        String userName = tripidArray[1].replaceAll("%20", " ");

        current_rider.setText(userName);
        current_rider1.setText(userName);
        current_rider2.setText(userName);

        tripID = Tripid;

        getState.putString("tripID", tripID);
        getState.apply();
/*        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId).child("accept");
       Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("trip_id", tripID);
        databaseReference.updateChildren(taskMap);*/

        //Toast.makeText(Map_Activity.this, tripID, Toast.LENGTH_SHORT).show();
        getstatusfromfirebase(tripID);

    }

    public void updateTripDetails() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId).child("accept");
        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("status", "2");
        databaseReference.updateChildren(taskMap);
    }

    //Get Trip status from Firebase
    private void getstatusfromfirebase(final String temptripID) {

        tripID = temptripID;
        getRiderID(tripID);
        LogUtils.i("driver id====>" + driverId);
        LogUtils.i("trip id====>" + tripID);

        if (tripID != null) {
            LogUtils.i("trip id statussss====>" + tripID);
            //final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverID).child("accept").child("status");
            tripsdataRef = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID).child("status");
            tripsdataListen = tripsdataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String status = dataSnapshot.getValue().toString();
                        LogUtils.i("Status from Firebase====>status" + status);
                        if (status != null) {


                            switch (status) {

                                case "1":

                                    LogUtils.i("accept state trip id in status 1 ===>" + tripID);

                                    getAcceptState();
                                    getTerminal(riderID);
                                    break;


                                case "2":

                                    tripID = temptripID;
                                    LogUtils.i("arrive now clicked trip id in status 2 ===>" + tripID);
                                    getStartState();
                                    getTerminal(riderID);
                                    break;


                                case "3":

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                    if (riderID != null)
                                        ref.child("riders_location").child(riderID).child("pickup_terminal").setValue("None");
                                    tripID = temptripID;
                                    LogUtils.i("start clicked trip id in status 3 ===>" + tripID);
                                    getOnTripState();

                                    break;


                                case "4":

                                    LogUtils.i("end trip");
                                    if (tripsdataListen != null)
                                        tripsdataRef.removeEventListener(tripsdataListen);

                                    showTripSummaryDialog();

                                    break;

                                case "5":

                                    LogUtils.i("trip cancel state");
                                    if (excecutecancelonce) {
                                        LogUtils.i("Inside excecute once" + excecutecancelonce);
                                        return;
                                    } else {
                                        LogUtils.i("Inside excecute once" + excecutecancelonce);
                                        excecutecancelonce = true;
                                        checktripcancelstatus();
                                    }

                                    break;

                                default:

                                    getState.putString("tripstate", "endClicked");
                                    getState.apply();

                                    break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } /*else {
            LogUtils.i("trip id is null====>");
        }*/
    }

    public void createRidersNameList() {


        int iNumberOfButtons = tripIDs.size();

        TextView[] dynamicButtons = new TextView[iNumberOfButtons];
        TextView[] dynamicButtons1 = new TextView[iNumberOfButtons];
        TextView[] dynamicButtons2 = new TextView[iNumberOfButtons];


        dynamic.removeAllViews();
        dynamic1.removeAllViews();
        dynamic2.removeAllViews();

        LinearLayout.LayoutParams paramsButton = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams paramsButton1 = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams paramsButton2 = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < iNumberOfButtons; i++) {
            //ProductType productType = productTypeList.get(i);

            String tempTirpid = tripIDs.get(i);

            tempTripWithName = tempTirpid;

            String[] tripidArray = tempTirpid.split(":");
            String Tripid = tripidArray[0];
            String userName = tripidArray[1].replaceAll("%20", " ");

            LogUtils.i("TripID Split" + Tripid);
            LogUtils.i("Username Split" + userName);
            dynamicButtons[i] = new TextView(Map_Activity.this);
            dynamicButtons[i].setText("Trip " + (i + 1));
            dynamicButtons[i].setPadding(5, 5, 5, 5);
            paramsButton.setMargins(5, 0, 5, 0);
            //dynamicButtons[i].setTextColor(getResources().getColor(R.color.colorGold));
            dynamicButtons[i].setTextColor(ContextCompat.getColor(Map_Activity.this, R.color.colorWhite));
            dynamicButtons[i].setTextSize(18);
            dynamicButtons[i].setBackgroundColor(Color.BLACK);
            dynamicButtons[i].setId(i);
            dynamicButtons[i].setTag(tempTirpid);
            dynamicButtons[i].setOnClickListener(Map_Activity.this);
            dynamicButtons[i].setLayoutParams(paramsButton);

            dynamic.addView(dynamicButtons[i]); // dynamic is the container of the buttons
            if (userName == null || userName.equals("null")) {
                LogUtils.i("Username RIDERFIRST" + riderFirstName);
                LogUtils.i("Username NOT NULL" + userName);
                userName = riderFirstName.replaceAll("%20", " ");
                LogUtils.i("Username NOT NULL" + userName);
            }
            current_rider.setText(userName);
            btnArriveNow.setEnabled(true);
            dynamic.setEnabled(true);
            dynamic1.setEnabled(true);
            dynamic2.setEnabled(true);
        }

        for (int j = 0; j < iNumberOfButtons; j++) {
            //ProductType productType = productTypeList.get(i);

            String tempTirpid = tripIDs.get(j);

            tempTripWithName = tempTirpid;

            String[] tripidArray = tempTirpid.split(":");
            String Tripid = tripidArray[0];
            String userName = tripidArray[1].replaceAll("%20", " ");

            LogUtils.i("TripID Split" + Tripid);
            LogUtils.i("Username Split" + userName);
            dynamicButtons1[j] = new TextView(Map_Activity.this);
            dynamicButtons1[j].setText("Trip " + (j + 1));
            dynamicButtons1[j].setPadding(5, 5, 5, 5);
            paramsButton1.setMargins(5, 0, 5, 0);
            dynamicButtons1[j].setTextColor(ContextCompat.getColor(Map_Activity.this, R.color.colorWhite));
            dynamicButtons1[j].setTextSize(18);
            dynamicButtons1[j].setBackgroundColor(Color.BLACK);
            dynamicButtons1[j].setId(j);
            dynamicButtons1[j].setTag(tempTirpid);
            dynamicButtons1[j].setOnClickListener(Map_Activity.this);
            dynamicButtons1[j].setLayoutParams(paramsButton1);
            dynamic1.addView(dynamicButtons1[j]); // dynamic is the container of the buttons
            //dynamic1.addView(dynamicButtons[i]); // dynamic is the container of the buttons
            if (userName == null || userName.equals("null")) {
                LogUtils.i("Username RIDERFIRST" + riderFirstName);
                LogUtils.i("Username NOT NULL" + userName);
                userName = riderFirstName;
                LogUtils.i("Username NOT NULL" + userName);
            }
            current_rider1.setText(userName);
        }

        for (int k = 0; k < iNumberOfButtons; k++) {
            //ProductType productType = productTypeList.get(i);

            String tempTirpid = tripIDs.get(k);

            tempTripWithName = tempTirpid;

            String[] tripidArray = tempTirpid.split(":");
            String Tripid = tripidArray[0];
            String userName = tripidArray[1].replaceAll("%20", " ");

            LogUtils.i("TripID Split" + Tripid);
            LogUtils.i("Username Split" + userName);
            dynamicButtons2[k] = new TextView(Map_Activity.this);
            dynamicButtons2[k].setText("Trip " + (k + 1));
            dynamicButtons2[k].setPadding(5, 5, 5, 5);
            paramsButton2.setMargins(5, 0, 5, 0);
            dynamicButtons2[k].setTextColor(ContextCompat.getColor(Map_Activity.this, R.color.colorWhite));
            dynamicButtons2[k].setTextSize(18);
            dynamicButtons2[k].setBackgroundColor(Color.BLACK);
            dynamicButtons2[k].setId(k);
            dynamicButtons2[k].setTag(tempTirpid);
            dynamicButtons2[k].setOnClickListener(Map_Activity.this);
            dynamicButtons2[k].setLayoutParams(paramsButton2);
            dynamic2.addView(dynamicButtons2[k]); // dynamic is the container of the buttons
            //dynamic1.addView(dynamicButtons[i]); // dynamic is the container of the buttons
            if (userName == null || userName.equals("null")) {
                LogUtils.i("Username RIDERFIRST" + riderFirstName);
                LogUtils.i("Username NOT NULL" + userName);
                userName = riderFirstName;
                LogUtils.i("Username NOT NULL" + userName);
            }
            current_rider2.setText(userName);

        }


    }

    //Get Trip status from Firebase
    private void getRiderID(final String tripID) {


        LogUtils.i("trip id====>" + tripID);

        if (tripID != null) {
            LogUtils.i("trip id====>" + tripID);
            //final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverID).child("accept").child("status");
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID).child("riderid");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String riderid = dataSnapshot.getValue().toString();
                        LogUtils.i("Status from Firebase====>rider id" + riderid);
                        if (riderid != null) {
                            riderID = riderid;
                            //  getRetrofitArray(riderid);
                            //setDropListener(riderID);
                            getRiderDetailsOnMultipleTrips(riderID);
                            if (multidestref != null) {
                                multidestref.removeEventListener(multidestlis);
                            }
                            setMultipleDestListener(riderID);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } /*else {
            LogUtils.i("trip id is null====>");
        }*/
    }

    private void getRiderDetailsOnMultipleTrips(String riderID) {

        final String url = Constants.LIVEURL_RIDER + "editProfile/user_id/" + riderID;
        LogUtils.i("RiderProfileURL==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        status = jsonObject.optString("status");
                        message = jsonObject.optString("message");

                        if (status.equals("Success")) {
                            riderFirstName = jsonObject.optString("firstname");
                            riderLastName = jsonObject.optString("lastname");
                            strRiderProfile = jsonObject.optString("profile_pic");
                            ridermobile = jsonObject.optString("mobile");
                            riderFirstName = riderFirstName.replaceAll("%20", " ");
                            riderLastName = riderLastName.replaceAll("%20", " ");

                            LogUtils.i("Rider First Name new==>" + riderFirstName);
                            LogUtils.i("Rider Last Name new==>" + riderLastName);
                            if (riderLastName.matches("null"))
                                riderLastName = " ";


                            try {
                                switch (strsetValue) {
                                    case "updatereq":

                                        txtRiderName.setText(riderFirstName + " " + riderLastName);
                                        break;
                                    case "coming_arrive":

                                        txtRiderName_Begin.setText(riderFirstName + " " + riderLastName);
                                        break;
                                    case "coming_end":
                                        txtEndTrip.setText(riderFirstName + " " + riderLastName);
                                        break;
                                }

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                            //     dismissDialog();
                        } else {
                            LogUtils.i("inside else");
//                            Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
                            // dismissDialog();
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                    //  dismissDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NoConnectionError) {
                    //   dismissDialog();
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    /**
     * @param bitmap The bitmap which need to be converted
     * @return converting bitmap and return a string
     */
    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void updatedfirebase(final String currenttrips, final double values) {

        LogUtils.i("currenttrips" + currenttrips + "values");

        try {
            tollfeereference = FirebaseDatabase.getInstance().getReference().child("trips_data").child(currenttrips).child("tollfee");
            tollfeereference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String tollspre = dataSnapshot.getValue().toString();

                    if (Double.parseDouble(tollspre) > 0) {

                        sumofamount = String.valueOf(values + Double.parseDouble(tollspre));

                        sumofamount = convertToDecimal(Double.parseDouble(sumofamount));

                        LogUtils.i("two values==>" + String.format("Value of a: %.2f", Double.parseDouble(sumofamount)));
                    } else {

                        sumofamount = convertToDecimal(values + Double.parseDouble(tollspre));
                    }

                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("trips_data").child(currenttrips);
                    Map<String, Object> taskMap2 = new HashMap<>();
                    taskMap2.put("tollfee", sumofamount);
                    databaseReference2.updateChildren(taskMap2);
                    sumofamount = "0";
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    LogUtils.w("Failed to read value - " + error.toException());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ridercancel(final String Riderid) {
        rideridreference = FirebaseDatabase.getInstance().getReference().child("riders_location").child(Riderid).child("status");
        canceltripslistener = rideridreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        String status = dataSnapshot.getValue().toString();
                        if (status.equals("6")) {

                            if (d != null) {
                                d.dismiss();
                                stopPlaying();
                            }

                            final android.support.v7.app.AlertDialog.Builder builder =
                                    new android.support.v7.app.AlertDialog.Builder(Map_Activity.this, R.style.AppCompatAlertDialogStyle);
                            builder.setTitle(getString(R.string.cancel_trip_request));
                            builder.setMessage(getString(R.string.rider_request_cancel));
                            builder.setCancelable(false);
                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    clearFirebaseData();
                                    getRiderRequestCancel();
                                    updateRiderStatus(Riderid);
                                    dialogInterface.dismiss();
                                }
                            });
                            if (!Map_Activity.this.isFinishing()) {
                                Map_Activity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        builder.show();
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void updateRiderStatus(String Riderid) {
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("riders_location").child(Riderid);
        Map<String, Object> taskMap2 = new HashMap<>();
        taskMap2.put("status", "0");
        databaseReference2.updateChildren(taskMap2);
    }

    public void updateSmartLocation() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, Constants.updateLocationToFBHandlerTime);
                LogUtils.d("Location Update Handler: Updating Now...");
                updateLocationToFirebase(getFusedLocation());
            }
        }, Constants.updateLocationToFBHandlerTime);
    }

    protected void getUpdateLocation(Double lat, Double lng) {

        String url = Constants.LIVEURL + "updateLocation/userid/" + driverId + "/lat/" + lat + "/long/" + lng;
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
                                String signIn_status = signIn_jsonobj.getString("status");

                                if (signIn_status.equals("Success")) {

                                    LogUtils.i("Location Updated!!");

                                } else if (signIn_status.equals("Fail")) {
                                    LogUtils.i("fail");
                                }

                            } catch (JSONException e) {
                                //stopAnim();
                                onlineLay.setEnabled(true);
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onlineLay.setEnabled(true);
                //protected static final String TAG = null;
                if (error instanceof NoConnectionError) {
                    LogUtils.i("fail");
                    // stopAnim();
                    //      Toast.makeText(Map_Activity.this, "An unknown network error has occured", Toast.LENGTH_SHORT).show();
                }
                LogUtils.d("Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    public void loadTollJSONFromAsset() {

        String json;
        try {
            //InputStream is = getAssets().open("tolls.json");
            //InputStream is = getAssets().open("tolls_name.json");
            InputStream is = getAssets().open("tolls_name_zone.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

            jsonTollObject = new JSONObject(json);

        } catch (IOException | JSONException ex) {

            jsonTollObject = null;
            ex.printStackTrace();
        }
    }

    public void checkTolls(Location location) {


        if (tollMarkersMap != null) {
            try {
                LogUtils.i("Lis of postion!!" + tollMarkersMap);
                //for (LatLng point : listOfTollPos) {
                for (LatLng point : tollMarkersMap.keySet()) {
                    LogUtils.i("keys on the map===>" + point);
                    if (CalculationByDistance(new LatLng(location.getLatitude(), location.getLongitude()), point) < .02) {

                        LogUtils.i("Bingoooo!!" + point.latitude + " " + point.longitude);
                        Marker m = tollMarkersMap.get(point);
                        String name = "None", ZoneID = "None", previousZoneID = "None";
                        if (m != null) {

                            name = m.getTitle();
                            ZoneID = String.valueOf(m.getTag());
                            m.remove();
                            generateNotification(getApplicationContext(), "Toll Detected: " + name);
                            //new getTollAmount(0,ZoneID,"Taxis").execute();

                            removingMarkerPoint = point;

                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void drawMarkerWithCircle(LatLng position, String Name, String ZoneID) {
/*        double radiusInMeters = 5;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mCircle = mMap.addCircle(circleOptions);
        LogUtils.i("am marking...");*/

        //tollMarker = this.tollMarkersMap.get(Name);

        LogUtils.i("toll marker length====>" + tollMarkersMap.size());
        LogUtils.i("toll marker length====>" + tollMarkersMap);

        Marker marker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_erp))
                .position(position));
        marker.setTitle(Name);
        marker.setTag(ZoneID);
        marker.setVisible(true);
        tollMarkersMap.put(position, marker);
    }

    public String getCurrentTime() {

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm");//24 Hour Format
        date.setTimeZone(TimeZone.getDefault());

        String localTime = date.format(currentLocalTime);

        return localTime.replaceAll(" ", "%20");
    }

    private void getSurgePricingPercentage(String starttime, final int minutes) {
        //Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.CATEGORY_LIVE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitArrayAPI service = retrofit.create(RetrofitArrayAPI.class);
        Call<List<Contributor>> call = service.repoContributors(starttime, getCurrentTime(), driverId);
        call.enqueue(new Callback<List<Contributor>>() {
            @Override
            public void onResponse(@NonNull Call<List<Contributor>> call, @NonNull retrofit2.Response<List<Contributor>> response) {
                try {
                    List<Contributor> RequestData = response.body();
                    LogUtils.i("Response " + RequestData);
                    if (RequestData != null) {
                        LogUtils.i("Response Size" + RequestData.size());
                    }
                    if (RequestData != null) {
                        for (int i = 0; i < RequestData.size(); i++) {
                            status = RequestData.get(i).getStatus();
                            if (status.equals("Success")) {
                                surgePrice = RequestData.get(i).getPercentage();
                                driverCompanyName = RequestData.get(i).getCompanyName();
                                LogUtils.i("The Price was" + surgePrice);
                                LogUtils.i("Company name was" + driverCompanyName);
                                if (!driverCompanyName.matches("None")) {
                                    driverCompanyFee = RequestData.get(i).getCompanyFee();
                                } else {
                                    driverCompanyFee = "0";
                                }
                                LogUtils.i("Company Fee is" + driverCompanyFee);

                                checkWaypointCount(surgePrice, minutes);

                            } else {
                                k = 0.0;

                                driverCompanyName = RequestData.get(i).getCompanyName();
                                LogUtils.i("Company name was" + driverCompanyName);
                                if (!driverCompanyName.matches("None")) {
                                    driverCompanyFee = RequestData.get(i).getCompanyFee();
                                } else {
                                    driverCompanyFee = "0";
                                }
                                LogUtils.i("Company Fee is" + driverCompanyFee);

                                checkWaypointCount("0", minutes);
                            }


                            LogUtils.i("the driver company" + driverCompanyName + driverCompanyFee);
                            /*if(driverCompanyFee!=null && driverCompanyName!=null){

                                if(!driverCompanyFee.equals("0")){
                                    companyname.setVisibility(View.VISIBLE);
                                    companyfee.setVisibility(View.VISIBLE);
                                    companyname.setText("Company Name: "+driverCompanyName);
                                    companyfee.setText("Company Fee: "+driverCompanyFee+" %");
                                }
                                else{
                                    companyname.setVisibility(View.GONE);
                                    companyfee.setVisibility(View.GONE);
                                }
                            }*/
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Contributor>> call, @NonNull Throwable t) {
                k = 0.0;
                checkWaypointCount("0", minutes);
            }
        });
    }

    public Location getFusedLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }

        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LogUtils.i("Location Provoider:" + " Fused Location");

        if (mCurrentLocation == null) {

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            LogUtils.i("Location Provoider:" + " Fused Location Fail: GPS Location");

            if (locationManager != null) {

                //To avoid duplicate listener
                try {
                    locationManager.removeUpdates(this);
                    LogUtils.i("remove location listener success");
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.i("remove location listener failed");
                }

                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        Constants.MIN_TIME_BW_UPDATES,
                        Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                mCurrentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (mCurrentLocation == null) {

                    LogUtils.i("Location Provoider:" + " GPS Location Fail: Network Location");

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            Constants.MIN_TIME_BW_UPDATES,
                            Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    mCurrentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
        }

        return mCurrentLocation;
    }

    private void startAction(String description) {

        LogUtils.i("in the distance setting==>");
        try {

            navcard.setVisibility(View.VISIBLE);
            navTxt.setText(description.trim());

            //textToSpeech.addSpeech(description,dsfa,dfa);
            volume = prefs.getBoolean("getvolume", false);
            if (volume)
                readText(description.trim());

            //hide the Navigation card view in 10 secconds
            ttsHandler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 10 seconds
                    if (navcard.isShown())
                        navcard.setVisibility(View.GONE);
                }
            }, 10000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GPSTurnOnAlert() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(Constants.SET_INTERVAL); //5 seconds
        locationRequest.setFastestInterval(Constants.SET_FASTESTINTERVAL); //3 seconds
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(Map_Activity.this, Constants.REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.

                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LogUtils.d("onActivityResult()res: " + Integer.toString(resultCode));
        LogUtils.d("onActivityResult()req: " + Integer.toString(requestCode));

        switch (requestCode) {
            case Constants.REQUEST_CHECK_SETTINGS:


                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        Toast.makeText(Map_Activity.this, "Enabling Location please wait...", Toast.LENGTH_SHORT).show();
                        mCurrentLocation = getFusedLocation();

                        new Handler().postDelayed(new Runnable() {

                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */

                            @Override
                            public void run() {
                                // This method will be executed once the timer is over
                                if (mCurrentLocation != null) {
                                    zoomCameraToPosition(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                                }

                            }
                        }, Constants.GET_ZOOM_TIME);

                        break;

                    case Activity.RESULT_CANCELED:

                        // The user was asked to change settings, but chose not to
                        Toast.makeText(Map_Activity.this, "Location not enabled, user cancelled.", Toast.LENGTH_SHORT).show();
                        finish();
                        break;

                    default:

                        break;
                }

                break;

            case CUSTOM_OVERLAY_PERMISSION_REQUEST_CODE:

                checkOverlayPermission();

                break;
        }
    }

    public void playRequestTone() {

        String requestTone = "android.resource://" + getPackageName() + "/" + R.raw.requesting_tone;

        try {
            requstingTone = new MediaPlayer();
            requstingTone.setDataSource(this, Uri.parse(requestTone));
            requstingTone.prepare();
            requstingTone.setLooping(true);
            requstingTone.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPlaying() {

        LogUtils.i("Am stoping function");

        if (requstingTone.isPlaying()) {
            LogUtils.i("Am stoped");
            requstingTone.stop();
            requstingTone.reset();
        }
    }

    //set listener for drop
    public void setDropListener(String riderId) {
        //Listener to get driver rating from Firebase
        try {
            droplocationRef = FirebaseDatabase.getInstance().getReference().child("riders_location").child(riderId).child("Updatelocation");
            droplocationlistener = droplocationRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (tripState.equals("startClicked")) //3
                    {

                        if (dataSnapshot.getValue() != null) {
                            String status = dataSnapshot.getValue().toString();
                            if (status != null) {
                                LogUtils.i("The drop lat lng from listener" + status);
                                String[] lat1ong = status.split(",");

                                String latitude = lat1ong[0];
                                String longitude = lat1ong[1];

                                String latreplace = latitude.replaceAll("\\[", "");
                                String longreplace = longitude.replaceAll("\\]", "");

                                Double laat = Double.parseDouble(latreplace);
                                Double lngg = Double.parseDouble(longreplace);

                                if (!latreplace.equals("0") && !longreplace.equals("0")) {
                                    destLocation = new LatLng(laat, lngg);
                                    navigationLatLng = new LatLng(laat, lngg);
                                    haslatlng = true;
                                    strLat = latreplace;
                                    strLng = longreplace;
                                    updateEndLocation(destLocation);
                                    new getUpdateAddress(destLocation.latitude, destLocation.longitude).execute();
                                } else {
                                    haslatlng = false;
                                }
                            }
                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateEndLocation(LatLng destLocation) {
        if (mCurrentLocation != null)
            orginLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        GoogleDirection.withServerKey(google_api_key)
                .from(orginLocation)
                .to(destLocation)
                .unit(Unit.METRIC)
                .transportMode(TransportMode.DRIVING)
                .execute(Map_Activity.this);
    }

    public void checkOverlayPermission() {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {

            startWidgetService();
        }
        //Check if it can be displayed on other applications
        else if (Settings.canDrawOverlays(this)) {

            startWidgetService();
        }
        //Display overlay permissions
        else {

            android.support.v7.app.AlertDialog.Builder builder =
                    new android.support.v7.app.AlertDialog.Builder(Map_Activity.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Alert!");
            builder.setMessage("You Must Turn on the Overlay Permission for Switching Application Button.");
            // builder.setCancelable(false);
            builder.setPositiveButton("Turn on",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + Map_Activity.this.getPackageName()));
                            startActivityForResult(intent, CUSTOM_OVERLAY_PERMISSION_REQUEST_CODE);
                        }
                    });

            builder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openRouteNavigator();
                        }
                    });

            builder.setNeutralButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
            builder.show();

        }
    }

    public void startWidgetService() {

        startService(new Intent(getApplication(), WidgetService.class));
        openRouteNavigator();
    }

    public void openRouteNavigator() {

        try {
            routeLat = navigationLatLng.latitude;
            routeLng = navigationLatLng.longitude;
        } catch (Exception e) {
            e.printStackTrace();
            routeLat = 0.0;
            routeLng = 0.0;
        }

        selectedDrawable = prefs.getString("navigationMode", null);

        LogUtils.i("Lat and Lng" + routeLat + "," + routeLng);

        if (selectedDrawable != null) {

            switch (selectedDrawable) {
                case "googleMap":

                    if (!routeLat.isNaN() && !routeLng.isNaN()) {

                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + routeLat + "," + routeLng);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);

                    }
                    break;
                case "inAppNavigation":
/*
                if(mapInAppNavStyle==null)
                    mapInAppNavStyle = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night);
                else
                    mapInAppNavStyle=null;

                mMap.setMapStyle(mapInAppNavStyle);*/

                    break;
                default:

                    try {

                        String uri = "waze://?ll=" + routeLat + ", " + routeLng + "=yes";
                        Intent waze = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        waze.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        waze.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(waze);
                    } catch (ActivityNotFoundException e) {
                        android.support.v7.app.AlertDialog.Builder builder =
                                new android.support.v7.app.AlertDialog.Builder(Map_Activity.this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("App Not Found");
                        builder.setMessage("For Navigate you need waze app");
                        // builder.setCancelable(false);
                        builder.setPositiveButton("Go to playstore",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent intent =
                                                new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                                        startActivity(intent);
                                    }
                                });
                        builder.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        } else {

            try {

                String uri = "waze://?ll=" + routeLat + ", " + routeLng + "=yes";
                Intent waze = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                waze.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(waze);
            } catch (ActivityNotFoundException e) {
                android.support.v7.app.AlertDialog.Builder builder =
                        new android.support.v7.app.AlertDialog.Builder(Map_Activity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("App Not Found");
                builder.setMessage("For Navigate you need waze app");
                // builder.setCancelable(false);
                builder.setPositiveButton("Go to playstore",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent =
                                        new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                                startActivity(intent);
                            }
                        });
                builder.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //set listener for Terminal
    public void getTerminal(String riderId) {
        //Listener to get driver rating from Firebase

        if (riderId != null) {

            pickupTerminalRef = FirebaseDatabase.getInstance().getReference().child("riders_location").child(riderId).child("pickup_terminal");
            pickupTerminalListen = pickupTerminalRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        String status = dataSnapshot.getValue().toString();
                        if (status != null) {
                            LogUtils.i("The drop lat lng from listener" + status);

                            if (!status.matches("None")) {

                                String appndAdd = status + " | " + strPickupAddress;

                                txtRiderDestination.setText(appndAdd);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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

    public long getDuration() {

        String tmpStartDate = prefs.getString("onlineStartDate", null);

        if (tmpStartDate != null) {

            String strEndDate = getDateTime();

            try {
                TimeZone GMT = TimeZone.getTimeZone("GMT");
                DateFormat dateStartTimefarmat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                dateStartTimefarmat.setTimeZone(GMT);
                DateFormat dateEndTimefarmat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                dateEndTimefarmat.setTimeZone(GMT);

                endDate = dateEndTimefarmat.parse(strEndDate);//end_date
                startDate = dateStartTimefarmat.parse(tmpStartDate);//start_date

                if (startDate != null & endDate != null) {

                    onlineDuration = endDate.getTime() - startDate.getTime();

                    LogUtils.i("startDate : " + startDate);
                    LogUtils.i("endDate : " + endDate);
                    LogUtils.i("different : " + onlineDuration);

                    return onlineDuration;

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

    public String getTimeFromDiffernce(long different) {

        //1 minute = 60 seconds
        //1 hour = 60 x 60 = 3600
        //1 day = 3600 x 24 = 86400

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        //System.out.printf("%d days, %d hours, %d minutes, %d seconds%n", elapsedDays,elapsedHours, elapsedMinutes, elapsedSeconds);

        if (elapsedHours == 0 & elapsedMinutes == 0 & elapsedSeconds == 0)
            return elapsedSeconds + " seconds ";
        else if (elapsedHours == 0 & elapsedMinutes == 0)
            return elapsedSeconds + " seconds ";
        else if (elapsedHours == 0)
            return elapsedMinutes + " minutes " + elapsedSeconds + " seconds ";
        else
            return elapsedHours + " hours " + elapsedMinutes + " minutes " + elapsedSeconds + " seconds ";
    }

    public int getWaypointTotal() {
        return waypointcount;
    }

    public void setWaypointcount(int count) {
        waypointcount = count;
    }

    public void getRoutePoints(LatLng origin, LatLng dest) {

        LogUtils.i("origin route==>" + origin + " dest route==>" + dest);

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        if (!url.equals("")) {
            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        if (origin != null && dest != null) {
            String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

            // Destination of route
            String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = str_origin + "&" + str_dest + "&" + sensor;

            // Output format
            String output = "json";

            // Building the url to the web service

            return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        }
        return "";
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        HttpURLConnection urlConnection;
        URL url = new URL(strUrl);

        // Creating an http connection to communicate with url
        urlConnection = (HttpURLConnection) url.openConnection();

        // Connecting to url
        urlConnection.connect();

        // Reading data from url
        try (InputStream iStream = urlConnection.getInputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            LogUtils.d("Exception on dwnld url" + e.toString());
        } finally {
            urlConnection.disconnect();
        }
        return data;
    }

    public void placeMultiDestMarkerPolyline() {

        if (points != null) {

            for (Marker marker : this.wayPaintsMarker.values()) {
                marker.remove();
            }
            if (wayPaintsMarker != null)
                wayPaintsMarker.clear();

            if (routePolyline != null)
                routePolyline.remove();
            View countMarker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
            int count = 0;
            if (latlngs != null) {

                for (LatLng position : latlngs) {

                    if (position != null) {

                        count++;
                        Marker waypointmarker = mMap.addMarker(new MarkerOptions().position(position));

                        if (position.equals(destLocation)) {
                            LogUtils.i("am here..." + position);
                            waypointmarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ub__ic_pin_dropoff));
                        } else {
                            waypointmarker.setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, countMarker, String.valueOf(count))));
                        }

                        this.wayPaintsMarker.put(position, waypointmarker);
                    }
                }
            }

            mulPoly = mMap.addPolyline(DirectionConverter.createPolyline(this, points, 5, Color.BLUE));

            int i = 0;

            this.wayPaintsPolyline.put(i++, mulPoly);

            LogUtils.i("final points are===>" + points);
        }
    }

    public void PlaceType(String latlng) {
        final String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latlng + "&radius=1000&types=airport&key=" + google_api_key;

        LogUtils.i(" ONLINE URL is " + url);

        // Creating volley request obj
        JsonObjectRequest movieReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        // Parsing json
                        try {

                            String status = response.getString("status");
                            LogUtils.i("status responce====>" + status);


                            if (status.matches("OK")) {

                                JSONArray result = response.optJSONArray("results");

                                JSONObject zerothPostion = result.optJSONObject(0);

                                JSONArray types = zerothPostion.optJSONArray("types");

                                String placeType = types.optString(0);

                                LogUtils.i("Airport PlaceType==>" + placeType);

                                if (placeType.matches("airport")) {

                                    airportamt = Double.parseDouble(strairportfee);

                                }


                            } else if (status.matches("INVALID_REQUEST")) {

                                String error_msg = response.getString("error_message");

                                LogUtils.i("The Error message in invalidreq" + error_msg);

                            } else if (status.matches("REQUEST_DENIED")) {

                                String error_msg = response.getString("error_message");

                                LogUtils.i("The Error message in reqdenied" + error_msg);

                            } else {

                                LogUtils.i("Execption on Getting Airport Type");
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError) {

                    Toast.makeText(Map_Activity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                LogUtils.d("Error: " + error.getMessage());

            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    public void fetchERPDeatils(String ERPresponse, String curZoneID, String curVehiType) {

        LogUtils.i("Response for ERP in Post Exe ===>" + ERPresponse);
        LogUtils.i("Current Zone ID===>" + curZoneID);
        LogUtils.i("Current vehicle Type===>" + curVehiType);

        if (ERPresponse != null) {
            // Parsing json
            try {

                JSONObject response = new JSONObject(ERPresponse);

                JSONArray valueArray = response.getJSONArray("value");

                LogUtils.i("length of the value array==>" + valueArray.length());

                int lenthOfArray = valueArray.length();

                String dayOfTheWeek = null, currentTime = null;

                try {
                    TimeZone GMT = TimeZone.getTimeZone("GMT");
                    DateFormat dateTimefarmat = new SimpleDateFormat("yyyy-MM-dd HH:mm"); //kk
                    dateTimefarmat.setTimeZone(GMT);
                    Date now = new Date();
                    String strCurrentDateTime = dateTimefarmat.format(now);
                    //Getting time
                    Date CurrentDateTime = dateTimefarmat.parse(strCurrentDateTime);
                    DateFormat timeFormat = new SimpleDateFormat("HH:mm"); //kk
                    SimpleDateFormat dayOfTheWeekFormat = new SimpleDateFormat("EEEE");
                    dayOfTheWeek = dayOfTheWeekFormat.format(CurrentDateTime);

                    if (!dayOfTheWeek.matches("Saturday"))
                        dayOfTheWeek = "Weekdays";

                    currentTime = timeFormat.format(CurrentDateTime);

                    LogUtils.i("Date: " + dayOfTheWeek);
                    LogUtils.i("Time: " + timeFormat.format(CurrentDateTime));

                } catch (ParseException e) {
                    e.printStackTrace();

                }

                for (int i = 0; i < valueArray.length(); i++) {

                    JSONObject itemsObjects = valueArray.getJSONObject(i);

                    String DayType = itemsObjects.getString("DayType");
                    String ZoneID = itemsObjects.getString("ZoneID");
                    String StartTime = itemsObjects.getString("StartTime");
                    String EndTime = itemsObjects.getString("EndTime");
                    String VehicleType = itemsObjects.getString("VehicleType");

                    if (dayOfTheWeek != null & currentTime != null)
                        if (DayType.matches(dayOfTheWeek) & curZoneID.matches(ZoneID) & VehicleType.matches(curVehiType)) {
                            LogUtils.i("inside zone id");

                            if (isTimeLiesBetweenTwotimes(StartTime, EndTime, currentTime)) {

                                LogUtils.i("Response for items Objects===>" + itemsObjects);
                                LogUtils.i("Current Time===>" + currentTime);

                                String tollChargeAmount = itemsObjects.getString("ChargeAmount");

                                LogUtils.i("Charge Amount===>" + tollChargeAmount);

                                Toast.makeText(Map_Activity.this, tollChargeAmount, Toast.LENGTH_SHORT).show();

                                if (tollChargeAmount != null)
                                    if (!tollChargeAmount.matches("0"))
                                        try {
                                            addTollAmount(tollChargeAmount);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                lenthOfArray = -77;

                                break;
                            }
                        }
                }

                if (lenthOfArray > 0) {

                    ERP_SKIP_COUNT = ERP_SKIP_COUNT + 50;

                    new getTollAmount(ERP_SKIP_COUNT, curZoneID, curVehiType).execute();

                } else if (lenthOfArray == -77) {

                    ERP_SKIP_COUNT = 0;
                } else {

                    LogUtils.i("lenthOfArray" + lenthOfArray);
                    Toast.makeText(Map_Activity.this, "Nothing Found", Toast.LENGTH_SHORT).show();
                    ERP_SKIP_COUNT = 0;
                }

            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }

    public boolean isTimeLiesBetweenTwotimes(String startTime, String endTime, String curTime) {

        try {
            String string1 = startTime; //"08:35"
            Date time1 = new SimpleDateFormat("HH:mm").parse(string1);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);

            String string2 = endTime; //"08:55";
            Date time2 = new SimpleDateFormat("HH:mm").parse(string2);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            calendar2.add(Calendar.DATE, 0);

            String someRandomTime = curTime; //"08:54";
            Date d = new SimpleDateFormat("HH:mm").parse(someRandomTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d);
            calendar3.add(Calendar.DATE, 0);

            Date x = calendar3.getTime();

            if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                //checkes whether the current time is between 14:49:00 and 20:11:13.
                return true;
            } else if (x.equals(calendar1.getTime()) | x.equals(calendar2.getTime())) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void initializeTTS() {

        //Initializing TextToSpeech
        textToSpeech = new TextToSpeech(Map_Activity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                LogUtils.i("text to speach status====>" + status);
                if (status != TextToSpeech.ERROR) {
                    try {
                        textToSpeech.setLanguage(Locale.US);
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Locale lTest = Locale.getDefault();
                            int res = textToSpeech.isLanguageAvailable(lTest);
                            if (res == TextToSpeech.LANG_AVAILABLE)
                                textToSpeech.setLanguage(lTest);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    if (navcard.isShown()) {
                        volume = prefs.getBoolean("getvolume", false);
                        if (navTxt.getText() != null)
                            if (volume)
                                readText(navTxt.getText().toString().trim());
                    }
                }
            }
        });
    }

    public void stopTTS() {

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    public void enableDisableVoiceNavButt() {

        volume = prefs.getBoolean("getvolume", false);

        if (volume) {
            mute.setVisibility(View.VISIBLE);
            volon.setVisibility(View.GONE);
        } else {
            volon.setVisibility(View.VISIBLE);
            mute.setVisibility(View.GONE);
        }
    }

    public void readText(String textToRead) {

        if (!textToSpeech.isSpeaking())
            textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void setOnlineButtunStatus(boolean value) {

        LogUtils.i("Last Status in boolean===>" + value);
        editor.putBoolean("goOnline", value);
        editor.apply();

    }

    public boolean isOnlineButtunClicked() {
        return prefs.getBoolean("goOnline", false);
    }

    private class DoubleArrayEvaluator implements TypeEvaluator<double[]> {

        private double[] mArray;

        /**
         * Create a DoubleArrayEvaluator that does not reuse the animated value. Care must be taken
         * when using this option because on every evaluation a new <code>double[]</code> will be
         * allocated.
         *
         * @see #DoubleArrayEvaluator(double[])
         */
        DoubleArrayEvaluator() {
        }

        /**
         * Create a DoubleArrayEvaluator that reuses <code>reuseArray</code> for every evaluate() call.
         * Caution must be taken to ensure that the value returned from
         * {@link android.animation.ValueAnimator#getAnimatedValue()} is not cached, modified, or
         * used across threads. The value will be modified on each <code>evaluate()</code> call.
         *
         * @param reuseArray The array to modify and return from <code>evaluate</code>.
         */
        DoubleArrayEvaluator(double[] reuseArray) {
            mArray = reuseArray;
        }

        /**
         * Interpolates the value at each index by the fraction. If
         * {@link #DoubleArrayEvaluator(double[])} was used to construct this object,
         * <code>reuseArray</code> will be returned, otherwise a new <code>double[]</code>
         * will be returned.
         *
         * @param fraction   The fraction from the starting to the ending values
         * @param startValue The start value.
         * @param endValue   The end value.
         * @return A <code>double[]</code> where each element is an interpolation between
         * the same index in startValue and endValue.
         */
        @Override
        public double[] evaluate(float fraction, double[] startValue, double[] endValue) {
            double[] array = mArray;
            if (array == null) {
                array = new double[startValue.length];
            }

            for (int i = 0; i < array.length; i++) {
                double start = startValue[i];
                double end = endValue[i];
                array[i] = start + (fraction * (end - start));
            }
            return array;
        }
    }

    // Async Task Class
    private class getTollDeatilsAsycn extends AsyncTask<String, String, String> {

        ArrayList<LatLng> directionPositionList;
        Context context;
        Map<LatLng, Marker> tollMarkersMap;
        JSONObject jsonTollObject;

        private getTollDeatilsAsycn(ArrayList<LatLng> directionPositionList, Map<LatLng, Marker> tollMarkersMap, Context context, JSONObject jsonTollObject) {
            this.directionPositionList = directionPositionList;
            this.tollMarkersMap = tollMarkersMap;
            this.context = context;
            this.jsonTollObject = jsonTollObject;
        }

        // Download Music File from Internet
        @Override
        protected String doInBackground(String... f_url) {

            // Parsing json
            try {

                JSONArray features = jsonTollObject.getJSONArray("features");

                for (int i = 0; i < features.length(); i++) {

                    JSONObject tolllist = features.getJSONObject(i);

                    JSONObject geometry = tolllist.getJSONObject("geometry");
                    JSONArray latlng = geometry.getJSONArray("coordinates");

                    //LogUtils.i("feature===>"+latlng.getDouble(1)+"   "+latlng.getDouble(0));
                    final LatLng tollposOG = new LatLng(latlng.getDouble(1), latlng.getDouble(0));

                    if (isLocationOnPath(tollposOG, directionPositionList, true, 5)) {

                        LogUtils.i("Testing Location: Yeah Did it...");
                        JSONObject properties = tolllist.getJSONObject("properties");
                        final String Name = properties.optString("Name");
                        final String ZoneID = properties.optString("ZoneID");

                        Marker tempmarkertoll = this.tollMarkersMap.get(tollposOG);
                        LogUtils.i("Testing Location: " + tempmarkertoll);

                        if (tempmarkertoll == null) {

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    LogUtils.d("UI thread " + "I am the UI thread");

                                    drawMarkerWithCircle(tollposOG, Name, ZoneID);
                                }
                            });
                        }

                    }
                }
            } catch (Exception e) {
                //stopAnim();
                e.printStackTrace();
            }

            return null;
        }

        // Once Music File is downloaded
        @Override
        protected void onPostExecute(String file_url) {
            // Dismiss the dialog after the Music file was downloaded
        }
    }

    private class getTollAmount extends AsyncTask<String, Void, String> {


        int ASYNC_ERP_SKIP_COUNT;
        String ASYNC_ZONEID, ASYNC_VEHICLETYPE;

        getTollAmount(int ASYNC_ERP_SKIP_COUNT, String ASYNC_ZONEID, String ASYNC_VEHICLETYPE) {

            this.ASYNC_ERP_SKIP_COUNT = ASYNC_ERP_SKIP_COUNT;
            this.ASYNC_ZONEID = ASYNC_ZONEID;
            this.ASYNC_VEHICLETYPE = ASYNC_VEHICLETYPE;

        }

        protected void onPreExecute() {


        }

        protected String doInBackground(String... arg0) {

            URL url;
            HttpURLConnection urlConnection = null;
            try {
                //url = new URL("http://54.172.2.238/driver/checkEmailPhone/email/praveen@gmail.com/mobile/9629888596");
                url = new URL("http://datamall2.mytransport.sg/ltaodataservice/ERPRates?$skip=" + String.valueOf(ASYNC_ERP_SKIP_COUNT));

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(20000 /* milliseconds */);
                urlConnection.setConnectTimeout(20000 /* milliseconds */);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("AccountKey", "zVmrQmzQTZOA0f/1+89rPQ==");
                urlConnection.setRequestProperty("accept", "application/json");

                InputStream in = urlConnection.getInputStream();

                BufferedReader ins = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = ins.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {

            fetchERPDeatils(result, ASYNC_ZONEID, ASYNC_VEHICLETYPE);

        }
    }

    private class getUpdateAddress extends AsyncTask<String, Void, String> {

        Double updatelat, updatlng;

        private getUpdateAddress(Double lat, Double lng) {
            this.updatelat = lat;
            this.updatlng = lng;
        }


        protected void onPreExecute() {
        }

        protected String doInBackground(String... arg0) {
            try {

                getCompleteAddressString(updatelat, updatlng);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            String strDesLoc = txtRiderDestination.getText().toString();
            if (strDesLoc.matches("null") & strDesLoc.matches(""))
                strDesLoc = "Destination Location Updated by Rider.";

            generateNotification(getApplicationContext(), "Destination Location Updated: " + strDesLoc);
        }
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                LogUtils.d("Background Task " + e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            PolylineOptions lineOptions;
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);

                }
                LogUtils.i("collected routs are in back ====>" + points);
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.RED);
            }


            // Drawing polyline in the Google Map for the i-th route
            // multipolyline = mMap.addPolyline(lineOptions);
            placeMultiDestMarkerPolyline();
        }
    }
}