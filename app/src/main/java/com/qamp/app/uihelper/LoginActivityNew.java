package com.qamp.app.uihelper;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboPresence;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.Utils.AppUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.qamp.app.R;

import java.util.HashMap;
import java.util.Map;

public class LoginActivityNew extends AppCompatActivity implements Mesibo.ConnectionListener,
        Mesibo.PresenceListener,
        Mesibo.ProfileListener {

    private static boolean mResetSyncedContacts = false;
    private static boolean mSyncPending = true;
    public String base_url = "https://dcore.qampservices.in/v2/user-service/";
    public String login = "user/login";
    public String validateOTP = "user/validateotp";
    EditText enter_number, enter_otp;
    Button generate_otp, validate_otp;
    boolean save = false;
    DemoUser mUser1 = new DemoUser("f065e206719767734e3c3e9cead41f20b4599e3d13cdef68e6646755fna8f428b4c1f", "User-1", "919452059368");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_login_new);
        enter_number = findViewById(R.id.enter_number);
        generate_otp = findViewById(R.id.generate_otp);
        enter_otp = findViewById(R.id.enter_otp);
        validate_otp = findViewById(R.id.validate_otp);
        generate_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(LoginActivityNew.this);
                    String URL = base_url + login;
                    JSONObject jsonBody = new JSONObject();
                    String phone = enter_number.getText().toString();
                    String countryCode = "91";
                    jsonBody.put("mobileNumber", phone);
                    jsonBody.put("region", "TEST");
                    jsonBody.put("countryCode", countryCode);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.i("VOLLEY======", response);
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                Log.e("VOLLEY Status======", status);
                                if (status.contains("SUCCESS")) {
                                    JSONObject data = jsonObject.getJSONObject("data");
                                    String mobileNumber = data.getString("mobileNumber");
                                    String state = data.getString("state");
                                    enter_number.getText().clear();
                                    enter_number.setHint("Enter Otp");
                                    generate_otp.setText("Validate Otp");
                                } else {
                                    JSONArray errors = jsonObject.getJSONArray("error");
                                    JSONObject error = (JSONObject) errors.get(0);
                                    String errMsg = error.getString("errMsg");
                                    String errorCode = error.getString("errCode");
                                    Toast.makeText(LoginActivityNew.this, errMsg, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VOLLEY", error.toString());
                            Toast.makeText(LoginActivityNew.this, " getString(R.string.general_error)", Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/x-www-form-urlencoded; charset=UTF-8";
                        }

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            try {
                                params.put("mobileNumber", jsonBody.getString("mobileNumber"));
                                params.put("region", jsonBody.getString("region"));
                                params.put("countryCode", jsonBody.getString("countryCode"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (params != null && params.size() > 0) {
                                return AppUtils.encodeParameters(params, getParamsEncoding());
                            }

                            return null;
                        }
                    };

                    requestQueue.add(stringRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivityNew.this, "getString(R.string.general_error", Toast.LENGTH_LONG).show();
                }
            }
        });
        validate_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(LoginActivityNew.this);
                    String URL = base_url + validateOTP;
                    JSONObject jsonBody = new JSONObject();
                    String phone = enter_number.getText().toString();
                    String countryCode = "91";
                    String otp = enter_otp.getText().toString();
                    jsonBody.put("mobileNumber", phone);
                    jsonBody.put("password", otp);
                    jsonBody.put("region", "TEST");
                    jsonBody.put("countryCode", countryCode);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                Log.i("VOLLEY======", response);
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                Log.e("VOLLEY Status======", status);
                                if (status.contains("SUCCESS")) {
                                    JSONObject data = jsonObject.getJSONObject("data");
                                    String mobileNumber = data.getString("mobileNumber");
                                    String token = data.getString("token");
                                    int responseVersion = data.getInt("version");
                                    String profileStatus = "";
                                    String fullName = "";
                                    String profilePicId = "";
                                    if (data.has("status")) {
                                        profileStatus = data.getString("status");
                                    }
                                    if (data.has("fullName")) {
                                        fullName = data.getString("fullName");
                                    }
                                    if (data.has("profilePicId")) {
                                        profilePicId = data.getString("profilePicId");
                                    }
                                    if (!TextUtils.isEmpty(token)) {
                                        Toast.makeText(LoginActivityNew.this, "SUCCESS", Toast.LENGTH_SHORT).show();

                                    }
                                } else {
                                    JSONArray errors = jsonObject.getJSONArray("error");
                                    JSONObject error = (JSONObject) errors.get(0);
                                    String errMsg = error.getString("errMsg");
                                    String errorCode = error.getString("errCode");
//                                    otpTextView1.getText().clear();
//                                    otpTextView2.getText().clear();
//                                    otpTextView3.getText().clear();
//                                    otpTextView4.getText().clear();
//
//                                    otpBackgroundRed(true);
//
//                                    WrongVibrate();
//                                    otpTextView1.requestFocus();
                                    // Toast.makeText(LoginActivity.this, errMsg, Toast.LENGTH_LONG).show();
                                    Toast.makeText(LoginActivityNew.this, errMsg, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("Error", e.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //AppUtils.closeProgresDialog();
                            Log.e("VOLLEY", error.toString());
                            Toast.makeText(LoginActivityNew.this, "qwertyui", Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/x-www-form-urlencoded; charset=UTF-8";
                        }

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            try {
                                params.put("mobileNumber", jsonBody.getString("mobileNumber"));
                                params.put("password", jsonBody.getString("password"));
                                params.put("countryCode", jsonBody.getString("countryCode"));
                                params.put("region", jsonBody.getString("region"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (params != null && params.size() > 0) {
                                return AppUtils.encodeParameters(params, getParamsEncoding());
                            }

                            return null;
                        }
                    };
                    requestQueue.add(stringRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivityNew.this, "sdfrghyujkjhv", Toast.LENGTH_LONG).show();
                }
            }
        });
        //mesiboInit(mUser1);
    }


    @Override
    public void Mesibo_onConnectionStatus(int i) {

    }

    @Override
    public void Mesibo_onPresence(@NotNull MesiboPresence mesiboPresence) {

    }

    @Override
    public void Mesibo_onPresenceRequest(@NotNull MesiboPresence mesiboPresence) {

    }

    @Override
    public void Mesibo_onProfileUpdated(MesiboProfile mesiboProfile) {

    }

    @Override
    public boolean Mesibo_onGetProfile(MesiboProfile mesiboProfile) {
        return false;
    }

    class DemoUser {
        public String token;
        public String name;
        public String address;

        DemoUser(String token, String name, String address) {
            this.token = token;
            this.name = name;
            this.address = address;
        }
    }
}
