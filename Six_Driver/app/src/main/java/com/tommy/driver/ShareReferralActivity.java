package com.tommy.driver;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.tommy.driver.adapter.contacts.DatabaseAdapter;
import com.tommy.driver.adapter.contacts.SelectUser;
import com.tommy.driver.adapter.contacts.SelectUserAdapter;
import com.tommy.driver.utils.LogUtils;

import java.util.List;

public class ShareReferralActivity extends AppCompatActivity {

    DatabaseAdapter mydb;
    SelectUserAdapter suAdapter;

    RecyclerView recyclerView;
    SearchView search;
    ImageButton backButton;
    TextView srchHint;
    Button btnSend;
    Handler progresshandler;

    ProgressDialog progressDialog;

    SelectUser users;

    String referralcode;

    boolean notCheckedStatus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_referral);

        mydb = new DatabaseAdapter(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.contacts_list);
        setRecyclerview();

        search = (SearchView) findViewById(R.id.searchView);
        srchHint = (TextView) findViewById(R.id.srchHint);
        backButton = (ImageButton) findViewById(R.id.backButton);
        btnSend = (Button) findViewById(R.id.btnSend);

        Intent i = getIntent();
        referralcode = i.getStringExtra("referralcode");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending Messages.. Please wait!");

        progresshandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                progressDialog.dismiss();

                if (notCheckedStatus) {

                    sharedAlert(getResources().getString(R.string.no_contact_alert));
                } else {

                    sharedAlert(getResources().getString(R.string.share_referral_msg));
                }
            }
        };

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String stext) {
                if (stext.trim().equals("")) {
                    srchHint.setVisibility(View.VISIBLE);
                } else {
                    srchHint.setVisibility(View.GONE);
                }

                if (suAdapter != null)
                    suAdapter.filter(stext);

                return false;
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = ShareReferralActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShareReferralActivity.SendMessagesThread thread = new ShareReferralActivity.SendMessagesThread(progresshandler);
                thread.start();
                progressDialog.show();
            }
        });
    }

    private void setRecyclerview() {
        new ConttactLoader().execute();
    }

    public void sharedAlert(final String alertMsg) {

        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(ShareReferralActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.referral_alert_header);
        builder.setMessage(alertMsg);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

                if (alertMsg.matches(getResources().getString(R.string.share_referral_msg))) {

                    View view = ShareReferralActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    finish();
                }
            }


        });

        builder.show();
    }

    private void sendSMS(final String phoneNumber, final String message) {

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        // ---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:

                        ContentValues values = new ContentValues();
                        values.put("address", phoneNumber);// txtPhoneNo.getText().toString());
                        values.put("body", message);
                        getContentResolver().insert(Uri.parse("content://sms/sent"), values);
                        Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        // ---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    private class ConttactLoader extends AsyncTask<Void, Void, List<SelectUser>> {

        @Override
        protected List<SelectUser> doInBackground(Void... voids) {

            return mydb.getData();
        }

        @Override
        protected void onPostExecute(List<SelectUser> selectUsers) {
            if (!selectUsers.isEmpty()) {

                suAdapter = new SelectUserAdapter(ShareReferralActivity.this, selectUsers);

                recyclerView.setLayoutManager(new LinearLayoutManager(ShareReferralActivity.this));
                recyclerView.setAdapter(suAdapter);
            }
        }
    }

    private class SendMessagesThread extends Thread {
        Handler handler;

        SendMessagesThread(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {

            // Find out which contacts are selected
            for (int i = 0; i < suAdapter.getlist().size() - 1; i++) {

                LogUtils.d("inside the loop " + i);

                users = suAdapter.getlist().get(i);

                if (users.getCheckedBox()) {

                    notCheckedStatus = false;

                    String mobile = users.getPhone().trim();

                    try {

                        mobile = mobile.replaceAll(" ", "");
                        LogUtils.d("message sent" + mobile);

                        sendSMS(mobile, referralcode);

                        /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + mobile));
                        intent.putExtra("sms_body", getString(R.string.sms_text)+" "+"Your Referral Code is "+referralcode+".");
                        startActivity(intent);*/

                        //smsManager.sendTextMessage(mobile, null, getString(R.string.sms_text)+" "+"Your Referral Code is "+referralcode+".", null, null);

                    } catch (Exception ex) {
                        LogUtils.d("Could not send message to " + mobile);
                    }
                } else {

                    LogUtils.d("not checked" + i);
                }
            }

            Message m = handler.obtainMessage();
            handler.sendMessage(m);
        } // run
    } // Thread
}