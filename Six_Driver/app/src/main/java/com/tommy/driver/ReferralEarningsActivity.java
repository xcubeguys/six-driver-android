package com.tommy.driver;


import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.tommy.driver.adapter.AppController;
import com.tommy.driver.adapter.Constants;
import com.tommy.driver.adapter.FontChangeCrawler;
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
import java.util.Date;

@EActivity(R.layout.activity_referral_earnings)
public class ReferralEarningsActivity extends AppCompatActivity {
    String tripTime, driverId = null, earnings, earnings_daily, earnings_weekly, earnings_monthly, earnings_yearly, userID;
    SharedPreferences state;
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
    TextView lastTripTime;

    @Click(R.id.back)
    void back() {
        Intent intent = new Intent(this, Map_Activity.class);
        startActivity(intent);
    }

    @Click(R.id.referral_user)
    void viewReferralUser() {
        Intent intent = new Intent(this, ReferralUsersActivity_.class);
        startActivity(intent);
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

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getString("driverid", null);
        LogUtils.i("UserID in settings" + userID);

        getEarning();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Map_Activity.class);
        startActivity(intent);
    }

    public void getEarning() {

        String url = Constants.LIVEURL + "yourEarnings/userid/" + userID;
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
                                earnings = signIn_jsonobj.optString("referd_amount");
                                tripTime = signIn_jsonobj.optString("referd_users");

                                earnings_daily = signIn_jsonobj.optString("referd_amount_date");
                                earnings_weekly = signIn_jsonobj.optString("referd_amount_week");
                                earnings_monthly = signIn_jsonobj.optString("referd_amount_month");
                                earnings_yearly = signIn_jsonobj.optString("referd_amount_year");

                                /*earnings_daily = "1";
                                earnings_weekly = "10";
                                earnings_monthly = "100";
                                earnings_yearly = "1000";*/

                                if (earnings != null && !earnings.isEmpty()) {
                                    Double amount = Double.parseDouble(earnings);
                                    earningamount.setText("$" + convertToDecimal(amount));

                                } else {
                                    earningamount.setText("$0");
                                }

                                if (earnings_daily != null && !earnings_daily.isEmpty()) {
                                    Double amount = Double.parseDouble(earnings_daily);
                                    earningamount_daily.setText("$" + convertToDecimal(amount));

                                } else {
                                    earningamount_daily.setText("$0");
                                }
                                if (earnings_weekly != null && !earnings_weekly.isEmpty()) {
                                    Double amount = Double.parseDouble(earnings_weekly);
                                    earningamount_weekly.setText("$" + convertToDecimal(amount));
                                } else {
                                    earningamount_weekly.setText("$0");
                                }
                                if (earnings_monthly != null && !earnings_monthly.isEmpty()) {
                                    Double amount = Double.parseDouble(earnings_monthly);
                                    earningamount_monthly.setText("$" + convertToDecimal(amount));

                                } else {
                                    earningamount_monthly.setText("$0");
                                }
                                if (earnings_yearly != null && !earnings_yearly.isEmpty()) {
                                    Double amount = Double.parseDouble(earnings_yearly);
                                    earningamount_yearly.setText("$" + convertToDecimal(amount));

                                } else {
                                    earningamount_yearly.setText("$0");
                                }

                                if (tripTime != null && !tripTime.isEmpty()) {
                                    lastTripTime.setText(tripTime + " Users used your code");
                                } else {
                                    lastTripTime.setText("0" + " Users used your code");
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
                    LogUtils.i("no internet");
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
}