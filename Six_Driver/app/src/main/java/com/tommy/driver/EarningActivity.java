package com.tommy.driver;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.tommy.driver.adapter.AppController;
import com.tommy.driver.adapter.Constants;
import com.tommy.driver.adapter.CustomBaseAdapter;
import com.tommy.driver.adapter.FontChangeCrawler;
import com.tommy.driver.adapter.NonScrollListView;
import com.tommy.driver.adapter.YourTrips;
import com.tommy.driver.utils.LogUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
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
import java.util.Locale;
import java.util.TimeZone;

import cn.pedant.SweetAlert.SweetAlertDialog;

@EActivity(R.layout.activity_earning)
public class EarningActivity extends AppCompatActivity {
    String tripTime, driverId = null, earnings, earnings_daily, earnings_weekly, earnings_monthly, earnings_yearly, referd_amount,
            referd_users, admincommission, totaltrips, tripDate, onlineduration_daily, onlineduration_monthly, onlineduration_weekly,
            onlineduration_yearly, tripId;
    String admincommissionweek, admincommissionmonth, admincommissionyear, totaltirpsweek, totaltirpsmonth, totaltirpsyear;
    SharedPreferences state, prefs;
    Dialog dialog;
    Double total = 0.0, totalweek = 0.0, totalmonth = 0.0, totalyear = 0.0;
    SweetAlertDialog pDialog;
    Date endDate, startDate;
    @ViewById(R.id.bank_info)
    ImageView bank_info;
    @ViewById(R.id.trip_amount)
    TextView earningamount;
    @ViewById(R.id.trip_amount_daily)
    TextView earningamount_daily;
    @ViewById(R.id.trip_amount_weekly)
    TextView earningamount_weekly;
    @ViewById(R.id.trip_amount_monthly)
    TextView earningamount_monthly;
    @ViewById(R.id.trip_amount_yearly)
    TextView earningamount_yearly;
    @ViewById(R.id.count)
    TextView referral_count;
    @ViewById(R.id.timetrip)
    TextView lastTripTime;
    private ArrayList<YourTrips> tripsListItemsDaily = new ArrayList<>();
    private ArrayList<YourTrips> tripsListItemsWeakly = new ArrayList<>();
    private ArrayList<YourTrips> tripsListItemsMonthly = new ArrayList<>();
    private ArrayList<YourTrips> tripsListItemsYearly = new ArrayList<>();
    private CustomBaseAdapter tripsListsAdapter;

    public static String getCurrentTimeStamp() {
        SimpleDateFormat formDate = new SimpleDateFormat("dd-MM-yyyy");

        // String strDate = formDate.format(System.currentTimeMillis()); // option 1
        return formDate.format(new Date());
    }

    @Click(R.id.back)
    void back() {
        Intent intent = new Intent(this, Map_Activity.class);
        startActivity(intent);
    }

    @Click(R.id.viewtxt)
    void viewdailyearning() {
        showdialogEarning("daily");
    }

    @Click(R.id.viewtxt1)
    void viewweekearning() {
        showdialogEarning("week");
    }

    @Click(R.id.viewtxt2)
    void viewmonthearning() {
        showdialogEarning("month");
    }

    @Click(R.id.viewtxt3)
    void viewyearearning() {
        showdialogEarning("year");
    }

    private void showdialogEarning(final String type) {

        EarningActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                dialog = new Dialog(EarningActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dailyearnings_detail);
                dialog.setCancelable(false);

                //layouts
                ImageButton back = (ImageButton) dialog.findViewById(R.id.backButton);
                TextView currentdate = (TextView) dialog.findViewById(R.id.trip_date_history);
                TextView trip_amount = (TextView) dialog.findViewById(R.id.trip_amount);
                TextView fare = (TextView) dialog.findViewById(R.id.fare);
                TextView total_price = (TextView) dialog.findViewById(R.id.total_price);
                TextView commission = (TextView) dialog.findViewById(R.id.commission);
                TextView sixcommision = (TextView) dialog.findViewById(R.id.sixcommision);
                TextView payouttxt = (TextView) dialog.findViewById(R.id.payouttxt);
                TextView totalpayout = (TextView) dialog.findViewById(R.id.totalpayout);
                TextView completedtrips = (TextView) dialog.findViewById(R.id.completedtrips);
                TextView duration = (TextView) dialog.findViewById(R.id.duration);
                TextView timeonlinetxt = (TextView) dialog.findViewById(R.id.timeonlinetxt);
                TextView title = (TextView) dialog.findViewById(R.id.trip_date1);
                NonScrollListView listview = (NonScrollListView) dialog.findViewById(R.id.listview);

                ScrollView scrollView = (ScrollView) dialog.findViewById(R.id.scrollView);
                scrollView.setFocusableInTouchMode(true);
                scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat month_date = new SimpleDateFormat("MMM dd");
                String month_name = month_date.format(cal.getTime());

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                switch (type) {
                    case "daily":
                        currentdate.setText(month_name);
                        title.setText("Daily Earnings");
                        total_price.setText("$ " + earnings_daily);
                        sixcommision.setText("-$ " + admincommission);
                        trip_amount.setText("$ " + convertToDecimal(total));
                        totalpayout.setText("$ " + convertToDecimal(total));
                        completedtrips.setText(totaltrips);
                        showTotalOnlineDuration(onlineduration_daily, duration);

                        tripsListsAdapter = new CustomBaseAdapter(getApplicationContext(), tripsListItemsDaily);
                        tripsListsAdapter.notifyDataSetChanged();
                        completedtrips.setText(String.valueOf(tripsListItemsDaily.size()));
                        break;
                    case "week":
                        currentdate.setText("Week");
                        title.setText("Weekly Earnings");
                        total_price.setText("$ " + earnings_weekly);
                        sixcommision.setText("-$ " + admincommissionweek);
                        trip_amount.setText("$ " + convertToDecimal(totalweek));
                        totalpayout.setText("$ " + convertToDecimal(totalweek));
                        completedtrips.setText(totaltirpsweek);
                        showTotalOnlineDuration(onlineduration_weekly, duration);

                        tripsListsAdapter = new CustomBaseAdapter(getApplicationContext(), tripsListItemsWeakly);
                        tripsListsAdapter.notifyDataSetChanged();
                        completedtrips.setText(String.valueOf(tripsListItemsWeakly.size()));
                        break;
                    case "month":
                        currentdate.setText("Month");
                        title.setText("Monthly Earnings");
                        total_price.setText("$ " + earnings_monthly);
                        sixcommision.setText("-$ " + admincommissionmonth);
                        trip_amount.setText("$ " + convertToDecimal(totalmonth));
                        totalpayout.setText("$ " + convertToDecimal(totalmonth));
                        completedtrips.setText(totaltirpsmonth);
                        showTotalOnlineDuration(onlineduration_monthly, duration);

                        tripsListsAdapter = new CustomBaseAdapter(getApplicationContext(), tripsListItemsMonthly);
                        tripsListsAdapter.notifyDataSetChanged();
                        completedtrips.setText(String.valueOf(tripsListItemsMonthly.size()));
                        break;
                    default:
                        currentdate.setText("Year");
                        title.setText("Yearly Earnings");
                        total_price.setText("$ " + earnings_yearly);
                        sixcommision.setText("-$ " + admincommissionyear);
                        trip_amount.setText("$ " + convertToDecimal(totalyear));
                        totalpayout.setText("$ " + convertToDecimal(totalyear));
                        completedtrips.setText(totaltirpsyear);
                        showTotalOnlineDuration(onlineduration_yearly, duration);

                        tripsListsAdapter = new CustomBaseAdapter(getApplicationContext(), tripsListItemsYearly);
                        tripsListsAdapter.notifyDataSetChanged();
                        completedtrips.setText(String.valueOf(tripsListItemsYearly.size()));
                        break;
                }
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent tripdetail = new Intent(EarningActivity.this, YourTripDetailsActivity_.class);

                        switch (type) {
                            case "daily":
                                tripdetail.putExtra("trip_id", tripsListItemsDaily.get(position).getTripID());
                                break;
                            case "week":
                                tripdetail.putExtra("trip_id", tripsListItemsWeakly.get(position).getTripID());
                                break;
                            case "month":
                                tripdetail.putExtra("trip_id", tripsListItemsMonthly.get(position).getTripID());
                                break;
                            default:
                                tripdetail.putExtra("trip_id", tripsListItemsYearly.get(position).getTripID());
                                break;
                        }

                        startActivity(tripdetail);
                    }
                });

                listview.setAdapter(tripsListsAdapter);

                dialog.show();
            }
        });

    }

    @AfterViews
    void create() {
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
        earningamount.setHorizontallyScrolling(true);
        earningamount_daily.setHorizontallyScrolling(true);
        earningamount_weekly.setHorizontallyScrolling(true);
        earningamount_monthly.setHorizontallyScrolling(true);
        earningamount_yearly.setHorizontallyScrolling(true);

        prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        driverId = prefs.getString("driverid", null);
        tripsListsAdapter = new CustomBaseAdapter(getApplicationContext(), tripsListItemsDaily);
        displayYourTodayTrips();
        getEarning();
    }

    @Click(R.id.bank_info)
    void bank() {
        Intent i = new Intent(EarningActivity.this, Bank_Details_.class);
        startActivity(i);
        finish();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EarningActivity.this, Map_Activity.class);
        startActivity(intent);
    }

    public void showTotalOnlineDuration(String getOnlineDuration, TextView tvduration) {

        TimeZone GMT = TimeZone.getTimeZone("GMT");

        String tmpStartDate = prefs.getString("onlineStartDate", null);

        DateFormat dateStartTimefarmat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        dateStartTimefarmat.setTimeZone(GMT);

        long tmpWebDuration, currentDuration = 0;

        try {

            if (tmpStartDate != null)
                currentDuration = getDuration(dateStartTimefarmat.parse(tmpStartDate));
            else
                currentDuration = getDuration(null);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String tmpOnlineduration;

        if (getOnlineDuration != null && !getOnlineDuration.isEmpty())
            tmpWebDuration = Long.parseLong(getOnlineDuration);
        else {
            tmpWebDuration = 0;
            getOnlineDuration = "0";
        }


        if (currentDuration != 0)
            tmpOnlineduration = String.valueOf(tmpWebDuration + currentDuration);
        else
            tmpOnlineduration = getOnlineDuration;


        long l = Long.parseLong(tmpOnlineduration);
        String strduration;
        Bundle bundleData = getTimeFromDiffernce(l);

        long elapsedMonths = bundleData.getLong("Months");
        long elapsedDays = bundleData.getLong("Days");
        long elapsedHours = bundleData.getLong("Hours");
        long elapsedMinutes = bundleData.getLong("Minutes");
        long elapsedSeconds = bundleData.getLong("Seconds");

        if (elapsedMonths == 0 & elapsedDays == 0 & elapsedHours == 0 & elapsedMinutes == 0 & elapsedSeconds == 0)
            strduration = elapsedSeconds + " sec";
        else if (elapsedMonths == 0 & elapsedDays == 0 & elapsedHours == 0 & elapsedMinutes == 0)
            strduration = elapsedSeconds + " sec";
        else if (elapsedMonths == 0 & elapsedDays == 0 & elapsedHours == 0)
            strduration = elapsedMinutes + "min " + elapsedSeconds + "sec";
        else if (elapsedMonths == 0 & elapsedDays == 0)
            strduration = elapsedHours + "h " + elapsedMinutes + "m " + elapsedSeconds + "s";
        else if (elapsedMonths == 0)
            strduration = elapsedDays + "day " + elapsedHours + "h " + elapsedMinutes + "m " + elapsedSeconds + "s";
        else
            strduration = elapsedMonths + "Mon " + elapsedDays + "day " + elapsedHours + "h " + elapsedMinutes + "m " + elapsedSeconds + "s";

        tvduration.setText(strduration);
    }

    public void getEarning() {

        String url = Constants.LIVEURL + "yourEarnings/userid/" + driverId;
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
                                earnings = signIn_jsonobj.optString("total_price");
                                tripTime = signIn_jsonobj.optString("last_tripDate");
                                referd_amount = signIn_jsonobj.optString("referd_amount");
                                referd_users = signIn_jsonobj.optString("referd_users");

                                earnings_daily = signIn_jsonobj.optString("drive_amount_daily");
                                admincommission = signIn_jsonobj.optString("admin_commission_daily");
                                admincommissionweek = signIn_jsonobj.optString("admin_commission_weekly");
                                admincommissionmonth = signIn_jsonobj.optString("admin_commission_monthly");
                                admincommissionyear = signIn_jsonobj.optString("admin_commission_yearly");

                                totaltrips = signIn_jsonobj.optString("total_trips_daily");
                                totaltirpsweek = signIn_jsonobj.optString("total_trips_weekly");
                                totaltirpsmonth = signIn_jsonobj.optString("total_trips_monthly");
                                totaltirpsyear = signIn_jsonobj.optString("total_trips_yearly");

                                earnings_weekly = signIn_jsonobj.optString("drive_amount_weekly");
                                earnings_monthly = signIn_jsonobj.optString("drive_amount_monthly");
                                earnings_yearly = signIn_jsonobj.optString("drive_amount_yearly");
                                onlineduration_daily = signIn_jsonobj.optString("online_duration_daily");
                                onlineduration_weekly = signIn_jsonobj.optString("online_duration_weekly");
                                onlineduration_monthly = signIn_jsonobj.optString("online_duration_monthly");
                                onlineduration_yearly = signIn_jsonobj.optString("online_duration_yearly");

                                try {
                                    total = Double.parseDouble(earnings_daily) - Double.parseDouble(admincommission);
                                    totalweek = Double.parseDouble(earnings_weekly) - Double.parseDouble(admincommissionweek);
                                    totalmonth = Double.parseDouble(earnings_monthly) - Double.parseDouble(admincommissionmonth);
                                    totalyear = Double.parseDouble(earnings_yearly) - Double.parseDouble(admincommissionyear);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (total != null)
                                    earningamount_daily.setText("$" + convertToDecimal(total));
                                else
                                    earningamount_daily.setText("$0");

                                if (totalweek != null) {
                                    earningamount_weekly.setText("$" + convertToDecimal(totalweek));

                                } else {
                                    earningamount_weekly.setText("$0");
                                }
                                if (totalmonth != null) {
                                    earningamount_monthly.setText("$" + convertToDecimal(totalmonth));
                                } else {
                                    earningamount_monthly.setText("$0");
                                }
                                if (totalyear != null) {
                                    earningamount_yearly.setText("$" + convertToDecimal(totalyear));
                                    earningamount.setText("$" + convertToDecimal(totalyear));
                                } else {
                                    earningamount_yearly.setText("$0");
                                    earningamount.setText("$0");
                                }

                                if (referd_users != null && !referd_users.isEmpty()) {
                                    //Double amount=Double.parseDouble(referd_users);
                                    referral_count.setText(referd_users + " Users used your code");
                                    LogUtils.i("THE AMOUNT IS" + referd_users);
                                } else {
                                    referral_count.setText("0" + " Users used your code");
                                }


                                if (tripTime != null && !tripTime.isEmpty()) {
                                    try {
                                        long time = Long.parseLong(tripTime);
                                        lastTripTime.setText(getCurrentdate(time));
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    lastTripTime.setText("NIL");
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
                if (error instanceof NoConnectionError) {
                    LogUtils.i("No Internet");
                }
                LogUtils.d("EarningActivity: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    public String convertToDecimal(Double amount) {

        if (amount > 0) {
            LogUtils.i("THE AMOUNT IS" + new DecimalFormat("0.00").format(amount));
            return new DecimalFormat("0.00").format(amount);
        } else {
            return String.valueOf(0);
        }
    }

    public String getCurrentdate(long timestamp) {
        try {
            //DateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            Date netDate = (new Date(timestamp * 1000L));
            return DateFormat.getDateTimeInstance().format(netDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void showSweetDialog() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void dismissSweet() {
        if (pDialog != null && pDialog.isShowing() && !isFinishing()) {
            try {
                pDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void displayYourTodayTrips() {
        showSweetDialog();
//        final String url = "http://demo.cogzideltemplates.com/tommy/requests/yourtrips/userid/58b69164da71b494448b4567";
        final String url = Constants.LIVEURL_REQUEST + "yourtripsdriver/userid/" + driverId;
        LogUtils.i("Your Trips URL==>" + url);
        final JsonArrayRequest tripListReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                LogUtils.i("response length=" + response.length());
                dismissSweet();
                //jsonArray=response;
                for (int i = 0; i < response.length(); i++) {
                    try {

                        JSONObject jsonObject = response.getJSONObject(i);
                        LogUtils.i("Status from Your Trips" + jsonObject.optString("status"));

                        if (jsonObject.optString("status").equals("success")) {
                            YourTrips trips = new YourTrips();
                            if (!jsonObject.optString("total_price").isEmpty()) {
                                if (!jsonObject.optString("total_price").equals("0")) {
                                    tripDate = jsonObject.optString("created");
                                    String tripDateTimeStamp = jsonObject.optString("created_timestamp");

                                    String strStarttDate = getDateFromTS(Long.parseLong(tripDateTimeStamp));

                                    DateFormat dateStartTimefarmat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

                                    long currentdateDuraion = 0;

                                    try {

                                        currentdateDuraion = getDuration(dateStartTimefarmat.parse(strStarttDate));

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    Bundle bundleData = getTimeFromDiffernce(currentdateDuraion);
                                    LogUtils.i("Current trip duration is===>" + bundleData);

                                    long elapsedMonths = bundleData.getLong("Months");
                                    long elapsedDays = bundleData.getLong("Days");
                                    long elapsedHours = bundleData.getLong("Hours");

                                    LogUtils.i("Inside today date" + tripDate + tripId);
                                    trips.setEndtime(jsonObject.optString("update_created"));
                                    trips.setDailycash(jsonObject.optString("total_price"));
                                    String admin_commission = jsonObject.optString("admin_commission");
                                    admin_commission = admin_commission.replaceAll(",", "");
                                    trips.setAdmincommission(admin_commission);
                                    trips.setTripID(jsonObject.optString("trip_id"));

                                    if (tripDate.equals(getCurrentTimeStamp())) {
                                        LogUtils.i("Trip===>" + jsonObject.optString("trip_id"));
                                        tripsListItemsDaily.add(0, trips); // Reverse the list items in the Array
                                    }

                                    if (elapsedDays < 7 & elapsedMonths < 1) {

                                        tripsListItemsWeakly.add(0, trips); // Reverse the list items in the Array
                                    }

                                    if (elapsedMonths < 1) {

                                        tripsListItemsMonthly.add(0, trips); // Reverse the list items in the Array
                                    }

                                    if (elapsedMonths < 12) {

                                        tripsListItemsYearly.add(0, trips); // Reverse the list items in the Array
                                    }
                                }
                            }
                        }

                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                tripsListsAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismissSweet();
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

    private String getDateFromTS(long TS) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        TimeZone tz = TimeZone.getTimeZone("GMT");
        cal.setTimeInMillis(TS * 1000);
        cal.add(Calendar.MILLISECOND, tz.getOffset(cal.getTimeInMillis()));
        SimpleDateFormat dateformate, time;
        dateformate = new SimpleDateFormat("yyyy/MM/dd");
        time = new SimpleDateFormat("hh:mm:ss");
        Date currenTimeZone = cal.getTime();
        LogUtils.i("Date: " + dateformate.format(currenTimeZone));
        LogUtils.i("Time: " + time.format(currenTimeZone));
        return dateformate.format(currenTimeZone) + " " + time.format(currenTimeZone);
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

    public long getDuration(Date tmpStartDate) {

        long currentDuration;

        if (tmpStartDate != null) {

            String strEndDate = getDateTime();

            try {
                TimeZone GMT = TimeZone.getTimeZone("GMT");
                DateFormat dateEndTimefarmat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                dateEndTimefarmat.setTimeZone(GMT);

                endDate = dateEndTimefarmat.parse(strEndDate);//end_date
                startDate = tmpStartDate;//start_date

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

    public Bundle getTimeFromDiffernce(long different) {

        //1 minute = 60 seconds
        //1 hour = 60 x 60 = 3600
        //1 day = 3600 x 24 = 86400
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long monthInMilli = daysInMilli * 30;

        long elapsedMonths = different / monthInMilli;
        different = different % monthInMilli;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        //System.out.printf("%d days, %d hours, %d minutes, %d seconds%n", elapsedDays,elapsedHours, elapsedMinutes, elapsedSeconds);

        Bundle b = new Bundle();
        b.putLong("Months", elapsedMonths);
        b.putLong("Days", elapsedDays);
        b.putLong("Hours", elapsedHours);
        b.putLong("Minutes", elapsedMinutes);
        b.putLong("Seconds", elapsedSeconds);

        return b;

    }
}