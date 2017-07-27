package com.tommy.driver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
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

import java.util.List;

@EActivity(R.layout.activity_forgot_password)
public class ForgotPasswordActivity extends AppCompatActivity implements Validator.ValidationListener {

    @NotEmpty(message = "")
    @Email(message = "Enter a valid email")
    @ViewById(R.id.edtForgotEmail)
    EditText edtEmail;
    Validator validator;
    String strEmail;

    @Click(R.id.back)
    void getBack() {
        Intent intent = new Intent(this, SigninActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.btnSubmit)
    void con() {
        validator.validate();
    }

    @AfterViews
    void create() {
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        validator = new Validator(this);
        validator.setValidationListener(this);

    }

    @Override
    public void onValidationSucceeded() {

        resetPassword();
    }

    private void resetPassword() {
        //http://demo.cogzidel.com/arcane_lite/driver/forgotPassword/email/cogzidel_new@gmail.com
        strEmail = edtEmail.getText().toString().trim();
        String url = Constants.LIVEURL + "forgotPassword/email/" + strEmail;
        LogUtils.i("URL is " + url);

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

                                    Toast.makeText(ForgotPasswordActivity.this, "Link has been sent to your given mail ID", Toast.LENGTH_SHORT).show();
                                    Intent Map = new Intent(getApplicationContext(), SigninActivity_.class);
                                    Map.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(Map);
                                    finish();
                                } else if (signIn_status.equals("Fail")) {
                                    //stopAnim();
                                    Toast.makeText(ForgotPasswordActivity.this, "Email has not registered!!", Toast.LENGTH_SHORT).show();
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
                    // stopAnim();
                    Toast.makeText(ForgotPasswordActivity.this, "An unknown network error has occured", Toast.LENGTH_SHORT).show();
                }
                LogUtils.d("ForgotPassword - " + "Error: " + error.getMessage());
            }
        });


        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
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
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
