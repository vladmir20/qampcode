/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:39 AM
 *
 */

package com.qamp.app.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;
import com.mesibo.api.Mesibo;
import com.qamp.app.MesiboApiClasses.SampleAPI;
import com.qamp.app.Utils.AppConfig;
import com.qamp.app.MesiboApiClasses.MesiboListeners;
import com.qamp.app.Utils.QAMPAPIConstants;
import com.qamp.app.Utils.QampConstants;
import com.qamp.app.Utils.QampUiHelper;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.Utils.Utilss;
import com.qamp.app.MessagingModule.MesiboConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginQampActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int CREDENTIAL_PICKER_REQUEST = 1000;
    private static final int RESOLVE_HINT = 1001;
    String phoneNumber = "";
    String selectedCountryCode = "91";
    CountryCodePicker ccp;
    int swipeCount = 0;
    //Declare timer
    CountDownTimer cTimer = null;
    private LinearLayout otp_layout1, otp_layout2, otp_layout3, otp_layout4;
    private ImageView loginLogo = null, onboardingImage = null, pageSelectorImage = null;
    private TextView enterPhoneNumberTextView, otpFieldPhoneNumber, onBoardingHeading, onBoardingSubHeading, wrongOtpText;
    private EditText phoneEditText;
    private Button generateOTPButton, signInButton, resendOTP;
    private LinearLayout enterPhoneView, otpView;
    private EditText otpTextView1, otpTextView2, otpTextView3, otpTextView4;
    private boolean otpBackgroundRed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Utilss.setLanguage(LoginQampActivity.this);
        setContentView(R.layout.activity_login);
        initViews();
        fetchNumber();
        initPermissions();
        AppUtils.setStatusBarColor(LoginQampActivity.this, R.color.colorAccent);
        generateOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validatePhoneNumber() && (ccp.getSelectedCountryCodeWithPlus().equals("+91"))) {
                    if (QAMPAPIConstants.statusTwilio.contains("on")) {
                        generateOTP();
                    } else {
                        otpView.setVisibility(View.VISIBLE);
                        enterPhoneView.setVisibility(View.GONE);

                        phoneNumber = phoneEditText.getText().toString();
                        otpFieldPhoneNumber.setText(ccp.getSelectedCountryCodeWithPlus() + "-" + phoneNumber);
                        MesiboListeners.getInstance().onLogin(LoginQampActivity.this, selectedCountryCode + phoneNumber, "");
                        generateOTP();

                        otpTextView1.clearFocus();
                        otpTextView2.clearFocus();
                        otpTextView3.clearFocus();
                        otpTextView4.clearFocus();

                        otpTextView1.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(otpTextView1, InputMethodManager.SHOW_IMPLICIT);
                    }

                } else {
                    Toast.makeText(LoginQampActivity.this, getResources().getString(R.string.pleaseSelect), Toast.LENGTH_SHORT).show();
                }
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (QAMPAPIConstants.statusTwilio.contains("on")) {
                } else {
                    String otp = otpTextView1.getText().toString() + otpTextView2.getText().toString() + otpTextView3.getText().toString() + otpTextView4.getText().toString();
                    MesiboListeners.getInstance().onLogin(LoginQampActivity.this, selectedCountryCode + phoneNumber, otp);
                    verifyOTP();
                }
            }
        });
        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otpView.setVisibility(View.VISIBLE);
                enterPhoneView.setVisibility(View.GONE);

                phoneNumber = phoneEditText.getText().toString();
                otpFieldPhoneNumber.setText(ccp.getSelectedCountryCodeWithPlus() + "-" + phoneNumber);

                //MesiboListeners.getInstance().onLogin(this,selectedCountryCode + phoneNumber, "");

                otp_layout1.setBackgroundDrawable(ContextCompat.getDrawable(LoginQampActivity.this, R.drawable.otp_field_corners));
                otp_layout2.setBackgroundDrawable(ContextCompat.getDrawable(LoginQampActivity.this, R.drawable.otp_field_corners));
                otp_layout3.setBackgroundDrawable(ContextCompat.getDrawable(LoginQampActivity.this, R.drawable.otp_field_corners));
                otp_layout4.setBackgroundDrawable(ContextCompat.getDrawable(LoginQampActivity.this, R.drawable.otp_field_corners));

                otpTextView1.setText("");
                otpTextView2.setText("");
                otpTextView3.setText("");
                otpTextView4.setText("");

                otpTextView1.clearFocus();
                otpTextView2.clearFocus();
                otpTextView3.clearFocus();
                otpTextView4.clearFocus();
                resendOTP();


                startTimer();
            }
        });

        otpTextView2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (otpTextView2.getText().toString().length() == 0)
                        otpTextView1.requestFocus();
                }
                return false;
            }
        });
        otpTextView3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (otpTextView3.getText().toString().length() == 0)
                        otpTextView2.requestFocus();
                }
                return false;
            }
        });
        otpTextView4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (otpTextView4.getText().toString().length() == 0)
                        otpTextView3.requestFocus();
                }
                return false;
            }
        });


    }

    private void initPermissions() {
        MesiboConfiguration config = new MesiboConfiguration();
        List<String> permissions = new ArrayList<>();

        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_CONTACTS);
        MesiboConfiguration.mPermissions = permissions;
        MesiboConfiguration.mPermissionsRequestMessage = "Qamp requires Storage and Contacts permissions " + "so that you can send messages and make calls to your contacts. Please grant to continue!";
        MesiboConfiguration.mPermissionsDeniedMessage = "Qamp will close now " + "since the required permissions were not granted";
        QampUiHelper.setConfig(config);
    }

    private void initViews() {
        enterPhoneNumberTextView = findViewById(R.id.enterPhoneLabel);
        otpFieldPhoneNumber = findViewById(R.id.otpFieldPhoneNumber);
        // Phone Edit Text
        phoneEditText = findViewById(R.id.phoneEditText);
        ccp = findViewById(R.id.ccp);
        generateOTPButton = findViewById(R.id.generateOTP);
        signInButton = findViewById(R.id.signInButton);
        resendOTP = findViewById(R.id.resendOTP);
        loginLogo = findViewById(R.id.loginLogo);
        loginLogo.setVisibility(View.VISIBLE);
        Animation animation;
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_to_original);
        loginLogo.setAnimation(animation);
        enterPhoneView = findViewById(R.id.enterPhoneView);
        enterPhoneView.setVisibility(View.VISIBLE);
        otpView = findViewById(R.id.otpView);
        otpView.setVisibility(View.GONE);
        wrongOtpText = findViewById(R.id.wrongOtpText);


        otpTextView1 = findViewById(R.id.otpET1);
        otpTextView2 = findViewById(R.id.otpET2);
        otpTextView3 = findViewById(R.id.otpET3);
        otpTextView4 = findViewById(R.id.otpET4);

        otp_layout1 = findViewById(R.id.otp_layout1);
        otp_layout2 = findViewById(R.id.otp_layout2);
        otp_layout3 = findViewById(R.id.otp_layout3);
        otp_layout4 = findViewById(R.id.otp_layout4);

        textFieldListeners();

    }

    private void fetchNumber() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this).addApi(Auth.CREDENTIALS_API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
        HintRequest hintRequest = new HintRequest.Builder().setPhoneNumberIdentifierSupported(true).build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (credential != null) {

                    // TODO: Check  Handle the country code
                    String mobNumber = credential.getId();
                    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                    try {
                        // phone must begin with '+'
                        Phonenumber.PhoneNumber numberProto = phoneUtil.parse(mobNumber, "");
                        selectedCountryCode = String.valueOf(numberProto.getCountryCode());
                        phoneNumber = String.valueOf(numberProto.getNationalNumber());
                        ccp.setCountryForPhoneCode(numberProto.getCountryCode());
                        phoneEditText.setText(phoneNumber);
                    } catch (NumberParseException e) {
                        System.err.println("NumberParseException was thrown: " + e);
                    }
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void textFieldListeners() {

        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (phoneEditText.getText().toString().length() == 10) {
                    if (validatePhoneNumber()) {
                        generateOTPButton.setBackgroundDrawable(ContextCompat.getDrawable(LoginQampActivity.this, R.drawable.corner_radius_button));
                        generateOTPButton.setEnabled(true);
                    }
                } else {
                    generateOTPButton.setBackgroundDrawable(ContextCompat.getDrawable(LoginQampActivity.this, R.drawable.corner_radius_buttoon_grey));
                    generateOTPButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        otpTextView1.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otpTextView1.getText().toString().length() == 1) {
                    if (otpBackgroundRed) {
                        otp_layout1.setBackgroundDrawable(ContextCompat.getDrawable(LoginQampActivity.this, R.drawable.otp_field_corners));
                    }
                    otpTextView2.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });


        otpTextView2.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otpTextView2.getText().toString().length() == 1) {
                    if (otpBackgroundRed) {
                        otp_layout2.setBackgroundDrawable(ContextCompat.getDrawable(LoginQampActivity.this, R.drawable.otp_field_corners));
                    }
                    otpTextView3.requestFocus();
                } else if (otpTextView2.getText().toString().length() == 0) {
                    otpTextView1.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });


        otpTextView3.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otpTextView3.getText().toString().length() == 1) {
                    if (otpBackgroundRed) {
                        otp_layout3.setBackgroundDrawable(ContextCompat.getDrawable(LoginQampActivity.this, R.drawable.otp_field_corners));
                    }
                    otpTextView4.requestFocus();
                } else if (otpTextView3.getText().toString().length() == 0) {
                    otpTextView2.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });

        otpTextView4.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otpTextView4.getText().toString().length() == 1) {
                    if (otpBackgroundRed) {
                        otp_layout4.setBackgroundDrawable(ContextCompat.getDrawable(LoginQampActivity.this, R.drawable.otp_field_corners));
                    }
                    otpTextView4.requestFocus();
                } else if (otpTextView4.getText().toString().length() == 0) {
                    otpTextView3.requestFocus();
                }


            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (otpTextView1.getText().toString().length() == 1 && otpTextView2.getText().toString().length() == 1 && otpTextView3.getText().toString().length() == 1 && otpTextView4.getText().toString().length() == 1) {
                    verifyOTP();
                    otpBackgroundRed = false;
                }
            }

        });
    }

    //start timer function
    void startTimer() {
        cTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                String text = getString(R.string.resend_otp_button) + "-" + (millisUntilFinished / 1000);
                resendOTP.setEnabled(false);
                resendOTP.setText(text);
                resendOTP.setTextColor(getApplication().getResources().getColor(R.color.grey_60));

            }

            public void onFinish() {
                resendOTP.setEnabled(true);
                resendOTP.setText(R.string.resend_otp_button);
                resendOTP.setTextColor(getApplication().getResources().getColor(R.color.colorPrimary));
            }
        };
        cTimer.start();
    }

    //cancel timer
    void cancelTimer() {
        if (cTimer != null) cTimer.cancel();
    }

    public Boolean validatePhoneNumber() {
        String swissNumberStr = phoneEditText.getText().toString();
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(swissNumberStr, ccp.getSelectedCountryNameCode());
            boolean isValid = phoneUtil.isValidNumber(swissNumberProto); // returns true
            if (isValid) {
                return true;
            }
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e);
        }

        return false;
    }

    private void generateOTP() {
        if (AppUtils.isNetWorkAvailable(this)) {
            AppUtils.openProgressDialog(LoginQampActivity.this);
            String url = QAMPAPIConstants.base_url + String.format(QAMPAPIConstants.login);
            Log.e("host", "" + url);
            generateOTPDataTask(url);
        } else {
            Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
        }
    }

    private void generateOTPDataTask(String url) {
        otpTextView1.requestFocus();
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(LoginQampActivity.this);
            String URL = url;
            JSONObject jsonBody = new JSONObject();
            String phone = phoneNumber;
            String countryCode = selectedCountryCode;
            jsonBody.put("mobileNumber", phone);
            jsonBody.put("region", "TEST");
            jsonBody.put("countryCode", countryCode);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        AppUtils.closeProgresDialog();
                        Log.i("VOLLEY======", response);
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        Log.e("VOLLEY Status======", status);
                        if (status.contains(QampConstants.success)) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            String mobileNumber = data.getString("mobileNumber");
                            String state = data.getString("state");
                            otpTextView1.findFocus();
                        } else {
                            JSONArray errors = jsonObject.getJSONArray("error");
                            JSONObject error = (JSONObject) errors.get(0);
                            String errMsg = error.getString("errMsg");
                            String errorCode = error.getString("errCode");
                            Toast.makeText(LoginQampActivity.this, errMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    AppUtils.closeProgresDialog();
                    Log.e("VOLLEY", error.toString());
                    Toast.makeText(LoginQampActivity.this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
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
            AppUtils.closeProgresDialog();
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
        }
    }

    private void verifyOTP() {
        if (AppUtils.isNetWorkAvailable(this)) {
            AppUtils.openProgressDialog(this);
            String url = QAMPAPIConstants.base_url + String.format(QAMPAPIConstants.validateOTP);
            verifyOTPDataTask(url);
        } else {
            Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
        }
    }

    private void verifyOTPDataTask(String url) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(LoginQampActivity.this);
            String URL = url;
            JSONObject jsonBody = new JSONObject();
            String phone = phoneNumber;
            String countryCode = selectedCountryCode;
            String otp = otpTextView1.getText().toString() + otpTextView2.getText().toString() + otpTextView3.getText().toString() + otpTextView4.getText().toString();
            jsonBody.put("mobileNumber", phone);
            jsonBody.put("password", otp);
            jsonBody.put("region", "TEST");
            jsonBody.put("countryCode", countryCode);
//            NotificationsTest.getFirebaseDeviceToken();
//            if (AppConfig.getConfig().deviceToken!="") {
//                jsonBody.put("androidToken", AppConfig.getConfig().deviceToken);
//             }
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        AppUtils.closeProgresDialog();
                        Log.i("VOLLEY======", response);
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        Log.e("VOLLEY Status======", status);
                        if (status.contains(QampConstants.success)) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            String mobileNumber = data.getString("mobileNumber");
                            //String password = data.getString("password");
                            //String firstName = data.getString("firstName");
                            String token = data.getString("token");
                            int responseVersion = data.getInt("version");
                            //String region = data.getString("region");
                            //String creationDate = data.getString("creationDate");
                            //String updatedDate = data.getString("updatedDate");
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

//                            String firstName = data.getString("firstName");
//                            String middleName = data.getString("middleName");
//                            String lastName = data.getString("lastName");

                            if (!TextUtils.isEmpty(token)) {
                                AppConfig.getConfig().token = token; //TBD, save into preference
                                AppConfig.getConfig().phone = selectedCountryCode + mobileNumber;
                                AppConfig.getConfig().countryCode = ccp.getSelectedCountryCode();
//                              AppConfig.getConfig().cc = response.cc;
                                AppConfig.getConfig().name = fullName; //TBD, save into preference
                                AppConfig.getConfig().status = profileStatus;
//                              AppConfig.getConfig().photo = response.photo;
                                AppConfig.getConfig().ts = 1649220290;
                                AppConfig.getConfig().tnm = "1649220290";
                                AppConfig.getConfig().version = responseVersion;
                                AppConfig.getConfig().profileId = profilePicId;
                                AppUtils.saveUserApiVersion(LoginQampActivity.this, String.valueOf(responseVersion));
                               // Toast.makeText(LoginQampActivity.this, "Response Version is-" + String.valueOf(responseVersion), Toast.LENGTH_SHORT).show();
                               /// Toast.makeText(LoginQampActivity.this, "Saved Version is-" + AppUtils.getUserApiVersion(LoginQampActivity.this), Toast.LENGTH_SHORT).show();
                                SampleAPI.setSyncFlags();
                                Mesibo.reset();
                                if (SampleAPI.startMesibo(true)) {
                                    SampleAPI.startSync();
                                }
                                AppConfig.save();
                                closeKeyboard();
                                SharedPreferences sh = getSharedPreferences("OnBoardingDone", MODE_PRIVATE);
                                String s1 = sh.getString("isOnBoardingScreensDone", "");
                                if (s1.equals("")) {
                                    Intent intent = new Intent(LoginQampActivity.this, OnBoardingScreens.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(LoginQampActivity.this, OnBoardingUserProfile.class);
                                    startActivity(intent);
                                    finish();
//                                    }
                                }

                            }
                        } else {
                            JSONArray errors = jsonObject.getJSONArray("error");
                            JSONObject error = (JSONObject) errors.get(0);
                            String errMsg = error.getString("errMsg");
                            String errorCode = error.getString("errCode");
                            otpTextView1.getText().clear();
                            otpTextView2.getText().clear();
                            otpTextView3.getText().clear();
                            otpTextView4.getText().clear();

                            otpBackgroundRed(true);
                            otpTextView1.requestFocus();
                            WrongVibrate();

                            Toast.makeText(LoginQampActivity.this, errMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Error", e.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    AppUtils.closeProgresDialog();
                    Log.e("VOLLEY", error.toString());
                    Toast.makeText(LoginQampActivity.this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
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
            AppUtils.closeProgresDialog();
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
        }
    }


    private void WrongVibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

    private void otpBackgroundRed(boolean b) {
        otpBackgroundRed = b;
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            otp_layout1.setBackgroundDrawable(ContextCompat.getDrawable(LoginQampActivity.this, R.drawable.otp_red_corner));
            otp_layout2.setBackgroundDrawable(ContextCompat.getDrawable(LoginQampActivity.this, R.drawable.otp_red_corner));
            otp_layout3.setBackgroundDrawable(ContextCompat.getDrawable(LoginQampActivity.this, R.drawable.otp_red_corner));
            otp_layout4.setBackgroundDrawable(ContextCompat.getDrawable(LoginQampActivity.this, R.drawable.otp_red_corner));
        } else {
            otp_layout1.setBackgroundDrawable(ContextCompat.getDrawable(LoginQampActivity.this, R.drawable.otp_red_corner));
            otp_layout2.setBackgroundDrawable(ContextCompat.getDrawable(LoginQampActivity.this, R.drawable.otp_red_corner));
            otp_layout3.setBackgroundDrawable(ContextCompat.getDrawable(LoginQampActivity.this, R.drawable.otp_red_corner));
            otp_layout4.setBackgroundDrawable(ContextCompat.getDrawable(LoginQampActivity.this, R.drawable.otp_red_corner));
        }
        wrongOtpText.setVisibility(View.VISIBLE);
    }

    private void resendOTP() {
        if (AppUtils.isNetWorkAvailable(this)) {
            AppUtils.openProgressDialog(this);
            String url = QAMPAPIConstants.base_url + String.format(QAMPAPIConstants.resendOTP);
            generateOTPDataTask(url);
        } else {
            Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
        }
    }


    private void closeKeyboard() {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }


}
