package com.tommy.driver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tommy.driver.adapter.AppController;
import com.tommy.driver.adapter.Constants;
import com.tommy.driver.adapter.FontChangeCrawler;
import com.tommy.driver.utils.LogUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@EActivity(R.layout.activity_your_trip_details)
public class YourTripDetailsActivity extends AppCompatActivity implements DirectionCallback, OnMapReadyCallback {

    String userID, tripID, strTripDate, strDriveName, strDriverImage, strCarName, strCarImage, strDistanceTraveled,
            strPickupTime, strDropTime, strPickupLocation, strDropLocation, strPaymentType, strTotalAmount, strRating, strCommission, google_api_key, strCancelfee;
    ProgressDialog progressDialog;
    String strbookfee, strairportfee, strtaxpercent, strcompname, strcompfee, waypointfee = "0";
    LatLng pickuplatlng, droplatlng;
    GoogleMap mMap;
    Double total = 0.0, feepercent, feeaddamount, taxpercent;
    double intcommission, intcompanycom, fareamount = 0.0;
    Marker pickup, drop;
    @ViewById(R.id.trip_date_history)
    TextView tripDate;

    @ViewById(R.id.totalpayout)
    TextView totalpayout;

    @ViewById(R.id.pickup_time)
    TextView pickupTime;

    @ViewById(R.id.trip_amount)
    TextView trip_amount;

    @ViewById(R.id.drop_time)
    TextView dropTime;

    @ViewById(R.id.pick_location)
    TextView pickupLocation;

    @ViewById(R.id.drop_location)
    TextView dropLocation;


    @ViewById(R.id.total_price)
    TextView totalAmount;

    @ViewById(R.id.sixcommision)
    TextView sixcommision;

    @ViewById(R.id.commission)
    TextView commission;

    @ViewById(R.id.duration)
    TextView duration;

    @ViewById(R.id.distance)
    TextView distanceTraveled;

    @ViewById(R.id.bookfee)
    TextView bookfee;

    @ViewById(R.id.airportfee)
    TextView airportfee;

    @ViewById(R.id.taxfee)
    TextView taxfee;

    @ViewById(R.id.taxtxt)
    TextView taxtxt;

    @ViewById(R.id.bookfeelay)
    LinearLayout bookfeelay;

    @ViewById(R.id.airportfeelay)
    LinearLayout airportfeelay;

    @ViewById(R.id.taxlay)
    LinearLayout taxfeelay;

    @ViewById(R.id.cancellay)
    LinearLayout cancellay;

    @ViewById(R.id.cancellayout)
    LinearLayout cancellayout;

    @ViewById(R.id.cancel_price)
    TextView cancelprice;

    @ViewById(R.id.companynamelay)
    LinearLayout companynamelay;

    @ViewById(R.id.companyname)
    TextView companyname;

    @ViewById(R.id.waypointlay)
    LinearLayout wayPointLay;

    @ViewById(R.id.waypointamt)
    TextView wayPointAmt;

    @ViewById(R.id.waypointtxt)
    TextView wayPointTxt;

    @ViewById(R.id.driver_image)
    ImageView driverImage;

    @FragmentById(R.id.map)
    MapFragment mapimg;

    @ViewById(R.id.ratingBar)
    RatingBar ratingBar;

    @ViewById(R.id.backButton)
    ImageButton back;

    static String formatDate(long dateInMillis) {
        Date date = new Date(dateInMillis);
        return DateFormat.getDateInstance(DateFormat.FULL).format(date);
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat formDate = new SimpleDateFormat("dd-MM-yyyy");

        // String strDate = formDate.format(System.currentTimeMillis()); // option 1
        return formDate.format(new Date()); // option 2
    }

    @AfterViews
    void yourTripDetails() {
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getString("userid", null);
        LogUtils.i("User ID in YourTripsDetails" + userID);

        Intent tripDetails = getIntent();
        tripID = tripDetails.getStringExtra("trip_id");
        strTripDate = tripDetails.getStringExtra("created");
        LogUtils.i("Trip ID in YourTripsDetails" + tripID);

        getKeys();

        mapimg = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapimg.getMapAsync(this);
        GoogleMapOptions options = new GoogleMapOptions().liteMode(true);
        MapFragment.newInstance(options);
        /*try {
            Glide.with(YourTripDetailsActivity.this)
                    .load(map.toURL())
                    .into(mapimg);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/

       /* getTripDetails();
        */

        getRating();
    }

    private void getTripDetails() {
        showDialog();
        final String url = Constants.LIVEURL_REQUEST + "getDriverTrips/trip_id/" + tripID;
        LogUtils.i("Get Trips URL==>" + url);
        final JsonArrayRequest tripListReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() <= 0) {
                    dismissDialog();
                } else {
                    dismissDialog();
                    for (int i = 0; i < response.length(); i++) {

                        try {
                            JSONObject jsonObject = response.getJSONObject(i);

                            strTripDate = getDate(Long.parseLong(jsonObject.optString("created")), "date");
                            strDriveName = jsonObject.optString("rider_name");
                            strDriverImage = jsonObject.optString("rider_profile");
//                            strCarImage=jsonObject.optString("");
                            strCarName = jsonObject.optString("car_category");
                            strDistanceTraveled = jsonObject.optString("total_distance");
                            strPickupTime = getDate(Long.parseLong(jsonObject.optString("created")), "time");
                            strDropTime = getDate(Long.parseLong(jsonObject.optString("update_created")), "time");
                            strPickupLocation = jsonObject.optString("pickup_address");
                            strDropLocation = jsonObject.optString("drop_address");
                            strPaymentType = jsonObject.optString("payment_mode");
                            strTotalAmount = jsonObject.optString("total_price");
                            strCommission = jsonObject.optString("admin_commission");
                            strcompname = jsonObject.optString("company_name");
                            strcompfee = jsonObject.optString("company_fee");
                            strbookfee = jsonObject.optString("book_fee");
                            strairportfee = jsonObject.optString("airport_surge");
                            waypointfee = jsonObject.optString("waypoint_fee");
                            strtaxpercent = jsonObject.optString("tax_percentage");
                            strCancelfee = jsonObject.optString("cancelation_fee");
                            strCommission = strCommission.replaceAll(",", "");

                            JSONObject waypointObject = jsonObject.optJSONObject("DestinationWaypoints");

                            if (strTotalAmount != null) {
                                strTotalAmount = strTotalAmount.replaceAll("%20", " ");
                                if (isDouble(strTotalAmount))
                                    fareamount = Double.parseDouble(strTotalAmount);
                                else
                                    fareamount = (double) Integer.parseInt(strTotalAmount);
                            }

                            if (waypointObject != null) {

                                int wayPointItemCount = waypointObject.length();
                                LogUtils.i("latitutesssss==>" + wayPointItemCount);

                                wayPointLay.setVisibility(View.VISIBLE);
                                wayPointAmt.setText("$ " + convertToDecimal(wayPointItemCount * Double.parseDouble(waypointfee)));
                                wayPointTxt.setText(String.valueOf(wayPointItemCount) + " Additional Stops");
                                fareamount = fareamount - wayPointItemCount * Double.parseDouble(waypointfee);

                            } else {

                                wayPointLay.setVisibility(View.GONE);
                                LogUtils.i("latitutesssss==> 0");
                            }

//                            strRating=jsonObject.optString("");

                            //get pickup drop latlng

                            JSONObject pickupObject = jsonObject.optJSONObject("pickup");
                            String strLat = pickupObject.optString("lat");
                            String strLong = pickupObject.optString("long");

                            LogUtils.i("latitute==>" + strLat + " longitute==>" + strLong);

                            JSONObject dropObject = jsonObject.optJSONObject("destination");
                            String strdropLat = dropObject.optString("lat");
                            String strdropLong = dropObject.optString("long");

                            LogUtils.i("latitute==>" + strLat + " longitute==>" + strLong);
                            LogUtils.i("latitutedrop==>" + strdropLat + " longitutedrop==>" + strdropLong);

                            if (strLong != null & strLong != null) {
                                pickuplatlng = new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLong));
                            }

                            if (strLong != null & strLong != null) {
                                droplatlng = new LatLng(Double.parseDouble(strdropLat), Double.parseDouble(strdropLong));
                            }

                            String pickLatLng = null;

                            if (strLong != null & strLong != null)
                                pickLatLng = strLat + "," + strLong;


                            if (strTripDate != null)
                                tripDate.setText(strTripDate);

                            if (strbookfee != null && !strbookfee.equals("0")) {
                                bookfeelay.setVisibility(View.VISIBLE);
                                bookfee.setText("$ " + strbookfee);
                                fareamount = fareamount - Double.parseDouble(strbookfee);
                            } else {
                                bookfeelay.setVisibility(View.GONE);
                            }


                            if (strairportfee != null && !strairportfee.equals("0")) {
                                if (pickLatLng != null)
                                    if (!pickLatLng.contains("null"))
                                        PlaceType(pickLatLng);
                            } else {
                                airportfeelay.setVisibility(View.GONE);
                            }


                            if (strDriveName != null) {
                                strDriveName = strDriveName.replaceAll("%20", " ");
                                //driverName.setText(Constants.capitalizeFirstLetter(strDriveName));
                                //driverName.setText(strDriveName);
                            }


                            if (strDistanceTraveled != null) {
                                if (!strDistanceTraveled.equals("null")) {
                                    strDistanceTraveled = strDistanceTraveled.replaceAll("%20", " ");
                                    distanceTraveled.setText(strDistanceTraveled + " km");
                                } else {
                                    distanceTraveled.setText("0 km");
                                }
                            }

                            if (strPickupTime != null && strDropTime != null) {
                                LogUtils.i("THe start and end time is" + strPickupTime);
                                duration.setText(getTripDuration(strPickupTime.replaceAll("%20", " "), strDropTime.replaceAll("%20", " ")));
                            }


                            if (strPickupLocation != null) {
                                strPickupLocation = strPickupLocation.replaceAll("%20", " ");
                                pickupLocation.setText(strPickupLocation);
                                //pickuplatlng=getLocationFromAddress(getApplicationContext(),strPickupLocation);
                            }

                            if (strDropLocation != null) {
                                strDropLocation = strDropLocation.replaceAll("%20", " ");
                                dropLocation.setText(strDropLocation);
                                //droplatlng=getLocationFromAddress(getApplicationContext(),strDropLocation);
                            }


                            LogUtils.i("In Direction success" + pickuplatlng + droplatlng);

                            if (pickuplatlng != null && droplatlng != null) {
                                GoogleDirection.withServerKey(google_api_key)
                                        .from(pickuplatlng)
                                        .to(droplatlng)
                                        .transportMode(TransportMode.DRIVING)
                                        .execute(YourTripDetailsActivity.this);

                            }

                            if (strtaxpercent != null && !strtaxpercent.equals("0")) {
                                taxfeelay.setVisibility(View.VISIBLE);
                                taxtxt.setText("Tax (" + strtaxpercent + " %)");
                                taxpercent = (Double.parseDouble(strTotalAmount) * (Double.parseDouble(strtaxpercent) / 100.0f));
                                fareamount = fareamount - taxpercent;
                                taxfee.setText("$ " + convertToDecimal(taxpercent));
                            } else {
                                taxfeelay.setVisibility(View.GONE);
                            }

                            if (strCommission != null) {
                                if (strcompname.equals("None")) {
                                    sixcommision.setText("-$ " + strCommission);
                                    companynamelay.setVisibility(View.GONE);
                                    if (isDouble(strCommission)) {

                                        intcommission = Double.parseDouble(strCommission);
                                    } else {

                                        intcommission = (double) Integer.parseInt(strCommission);
                                    }
                                    total = Double.parseDouble(strTotalAmount) - intcommission;
                                    LogUtils.i("The total after commission" + String.valueOf(total));
                                } else {
                                    commission.setText("Company fee");
                                    sixcommision.setText("-" + strcompfee + " %");
                                    companynamelay.setVisibility(View.VISIBLE);
                                    companyname.setText(strcompname);
                                    try {
                                        feepercent = (Double.parseDouble(strTotalAmount) * (Double.parseDouble(strcompfee) / 100.0f));
                                        feeaddamount = Double.parseDouble(strTotalAmount) - feepercent;
                                        LogUtils.i("After adding fee percentage of" + feepercent + "to" + feeaddamount);
                                        total = feeaddamount;
                                    } catch (Exception e) {
                                        feepercent = 0.0;
                                        feeaddamount = Double.parseDouble(strTotalAmount) - feepercent;
                                        total = feeaddamount;
                                        e.printStackTrace();
                                    }
                                    LogUtils.i("The total after company commission" + String.valueOf(total));
                                }
                            }

                            if (total != 0.0) {
                                totalpayout.setText("$ " + convertToDecimal(total));
                                trip_amount.setText("$ " + convertToDecimal(total));
                            } else {
                                totalpayout.setText("$ " + strTotalAmount);
                            }

                            if (fareamount != 0.0) {
                                totalAmount.setText("$ " + convertToDecimal(fareamount));
                            } else {
                                totalAmount.setText("$0");
                            }

                            if (strCancelfee != null) {
                                if (!strCancelfee.equals("0") && fareamount != 0.0) {
                                    cancellay.setVisibility(View.VISIBLE);
                                    cancellayout.setVisibility(View.VISIBLE);
                                    cancelprice.setText("$ " + convertToDecimal(fareamount));
                                } else {
                                    cancellay.setVisibility(View.GONE);
                                    cancellayout.setVisibility(View.GONE);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismissDialog();
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
                if (volleyError instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        tripListReq.setRetryPolicy(new DefaultRetryPolicy(5000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(tripListReq);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Click(R.id.backButton)
    void goBack() {
        finish();
    }

    private String getDate(long time, String type) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        TimeZone tz = TimeZone.getTimeZone("GMT");
        cal.setTimeInMillis(time * 1000);
        cal.add(Calendar.MILLISECOND, tz.getOffset(cal.getTimeInMillis()));
        SimpleDateFormat sdf;
        if (type.equals("date")) {
            sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        } else {
            sdf = new SimpleDateFormat("hh:mm:ss a");
        }

        Date currenTimeZone = (Date) cal.getTime();
        LogUtils.i("Trip Date==>" + sdf.format(currenTimeZone));
        return sdf.format(currenTimeZone);
    }

    public void showDialog() {
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            if (!isFinishing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    public void getRating() {
        if (tripID != null) {

            //Get datasnapshot at your "users" root node
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID);
            ref.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Get map of users in datasnapshot
                            if (dataSnapshot.getValue() != null) {

                                LogUtils.i("response ===>" + dataSnapshot.toString());

                                Object tollAmount = dataSnapshot.child("tollfee").getValue();
                                Object RiderRatings = dataSnapshot.child("rider_rating").getValue();

                                LogUtils.i("response tollAmount===>" + tollAmount);
                                LogUtils.i("response RiderRatings===>" + RiderRatings);

                                if (tollAmount != null) {

                                    //tollPrice.setText("$"+tollAmount);
                                } else {

                                    //tollPrice.setText("$0");
                                }

                                if (RiderRatings != null) {
                                    ratingBar.setRating(Float.parseFloat(String.valueOf(RiderRatings)));
                                } else {

                                    ratingBar.setRating(0);
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

    public String convertToDecimal(Double amount) {

        if (amount > 0) {
            LogUtils.i("THE AMOUNT IS" + new DecimalFormat("0.00").format(amount));
            return new DecimalFormat("0.00").format(amount);
        } else {
            return String.valueOf(0);
        }
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {

            ex.printStackTrace();
        }


        return p1;
    }

    private void getKeys() {

        //http://demo.cogzideltemplates.com/tommy/settings/getdetails
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
                    LogUtils.i("NoConnectionError");
                    // stopAnim();
                    //
                    //    Toast.makeText(Map_Activity.this, "An unknown network error has occured", Toast.LENGTH_SHORT).show();
                }
                LogUtils.d("EarningActivity: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            try {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                pickup = mMap.addMarker(new MarkerOptions().position(pickuplatlng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ub__ic_pin_pickup)));

                drop = mMap.addMarker(new MarkerOptions().position(droplatlng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ub__ic_pin_dropoff)));
                builder.include(pickup.getPosition());
                builder.include(drop.getPosition());
                LatLngBounds bounds = builder.build();

                LogUtils.i("In Direction success" + direction.getStatus() + pickuplatlng + droplatlng);

                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 90));

                ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                mMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.BLUE));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        LogUtils.i("In Direction Failure" + t);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getTripDetails();
    }

    public String getTripDuration(String starttime, String endtime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        int days, hours = 0, min = 0, sec = 0;
        LogUtils.i("THe start and end time is" + starttime + "Endtime" + endtime);
        try {
            Date date1 = simpleDateFormat.parse(starttime);
            Date date2 = simpleDateFormat.parse(endtime);

            long difference = date2.getTime() - date1.getTime();
            int timeInSeconds = (int) difference / 1000;
            days = (int) (difference / (1000 * 60 * 60 * 24));
            hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
            timeInSeconds = timeInSeconds - (min * 60);
            sec = timeInSeconds;
            hours = (hours < 0 ? -hours : hours);
            LogUtils.i("====== Hours :: " + hours + min + sec);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (min == 0 && hours == 0)
            return String.valueOf(sec) + " sec";
        else
            return String.valueOf(hours) + " hr" + " " + String.valueOf(min) + " min";
    }

    public void PlaceType(String latlng) {

        //final String url="https://maps.googleapis.com/maps/api/place/details/json?placeid="+placeID+"&key="+google_api_key;
        //final String url="https://maps.googleapis.com/maps/api/geocode/json?latlng="+latlng+"&sensor=true&key="+google_api_key;
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

                                    double airportamt = Double.parseDouble(strairportfee);
                                    fareamount = fareamount - airportamt;
                                    airportfee.setText("$" + convertToDecimal(airportamt));
                                    airportfeelay.setVisibility(View.VISIBLE);
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

                    Toast.makeText(YourTripDetailsActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                LogUtils.d("Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    boolean isDouble(String str) {
        try {

            Double.parseDouble(str);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}