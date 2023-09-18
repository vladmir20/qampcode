package com.qamp.app.Activity;

import static com.qamp.app.Utils.AppUtils.formatPhoneNumber;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.APIHandling.ApiGeneratorClass;
import com.qamp.app.CustomClasses.ForegroundLinearLayout;
import com.qamp.app.LoginModule.MesiboApiClasses.MesiboListeners;
import com.qamp.app.R;
import com.qamp.app.Utils.AnimationUtils;
import com.qamp.app.Utils.AppUtils;

public class LoginActivity extends AppCompatActivity implements Mesibo.ProfileListener, Mesibo.ConnectionListener {

    ForegroundLinearLayout nextBtn;
    EditText phoneNumberText;
    ImageView backBtn, qamp_welcome_text,otp_verified;
    TextView nextButtonText, phoneNumberTextGiven,resendOTP2;
    private EditText editText1, editText2, editText3, editText4;
    private Vibrator vibrator;
    // Define the parent LinearLayouts for OTP fields
    private LinearLayout otpLayout1, otpLayout2, otpLayout3, otpLayout4, linearLayout2;
    private ConstraintLayout login_layout, otp_layout;

    private EditText[] editTexts; // Array to hold all EditText fields
    private int currentEditTextIndex = 0; // Index of the currently focused EditText
    // Flag to track whether an incorrect OTP has been entered
    private boolean isIncorrectOTP = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setStatusBarColor(LoginActivity.this, R.color.colorAccent);
        setContentView(R.layout.activity_login);
        initViewsAndBackgroundAnimation();
        AnimationUtils.animateViewVisibility(phoneNumberTextGiven, false);
        AnimationUtils.animateViewVisibility(otp_verified, false);
        AnimationUtils.animateViewVisibility(resendOTP2, false);

        phoneNumberText.requestFocus();
        phoneNumberText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 10) {
                    AnimationUtils.animateViewVisibility(backBtn, true);
                    int backgroundColor = ContextCompat.getColor(LoginActivity.this, R.color.text_color_new);
                    nextBtn.setBackgroundColor(backgroundColor);
                } else {
                    int backgroundColor = ContextCompat.getColor(LoginActivity.this, R.color.colorPrimaryLight);
                    nextBtn.setBackgroundColor(backgroundColor);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneNumberText.getText().toString().length()==10){
                    String countryCode="91";
                    //calling otp api from external class
                    ApiGeneratorClass.callOTPFunction(phoneNumberText.getText().toString(),countryCode, LoginActivity.this);
                    AnimationUtils.changeImageWithAnimation(qamp_welcome_text, R.drawable.verify_otp_image);
                    AnimationUtils.animateViewVisibility(login_layout, false);
                    AnimationUtils.animateViewVisibility(otp_layout, true);
                    editText1.requestFocus();
                    AnimationUtils.animateViewVisibility(backBtn, true);
                    phoneNumberTextGiven.setText(formatPhoneNumber(phoneNumberText.getText().toString()));
                    AnimationUtils.animateViewVisibility(phoneNumberTextGiven, true);
                    AnimationUtils.animateViewVisibility(resendOTP2, false);
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login_layout.getVisibility()==View.VISIBLE){
                    Intent intent = new Intent(LoginActivity.this, SplashScreenActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }else{
                    AnimationUtils.changeImageWithAnimation(qamp_welcome_text, R.drawable.welcom_to_qamp_image);
                    AnimationUtils.animateViewVisibility(login_layout, true);
                    //AnimationUtils.animateViewVisibility(otp_layout, false);
                    otp_layout.setVisibility(View.GONE);
                    AnimationUtils.animateViewVisibility(phoneNumberTextGiven, false);
                    phoneNumberText.requestFocus();
                    // Show the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(phoneNumberText, InputMethodManager.SHOW_IMPLICIT);
                    editText1.setText("");
                    editText2.setText("");
                    editText3.setText("");
                    editText4.setText("");
                    AnimationUtils.animateViewVisibility(linearLayout2, true);
                    AnimationUtils.animateViewVisibility(otp_verified, false);
                    AnimationUtils.animateViewVisibility(backBtn, false);
                }
            }
        });
    }


    private void initViewsAndBackgroundAnimation() {

        phoneNumberText = findViewById(R.id.phoneNumberText);
        nextBtn = findViewById(R.id.nextButton);
        backBtn = findViewById(R.id.backBtn);
        nextButtonText = findViewById(R.id.nextButtonText);
        qamp_welcome_text = findViewById(R.id.qamp_welcome_text);
        phoneNumberTextGiven = findViewById(R.id.phoneNumberTextGiven);
        linearLayout2 = findViewById(R.id.linearLayout2);
        login_layout  = findViewById(R.id.login_layout);
        otp_layout = findViewById(R.id.otp_layout);
        otp_verified = findViewById(R.id.otp_verified);
        editText1 = findViewById(R.id.otpET1);
        editText2 = findViewById(R.id.otpET2);
        editText3 = findViewById(R.id.otpET3);
        editText4 = findViewById(R.id.otpET4);
        resendOTP2 = findViewById(R.id.resendOTP2);
        // Find the parent LinearLayouts by their IDs
        otpLayout1 = findViewById(R.id.otp_layout1);
        otpLayout2 = findViewById(R.id.otp_layout2);
        otpLayout3 = findViewById(R.id.otp_layout3);
        otpLayout4 = findViewById(R.id.otp_layout4);
        // Initialize the array of EditText fields
        editTexts = new EditText[]{editText1, editText2, editText3, editText4};

        // Initialize Vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Set up the TextWatchers for EditText fields
        setUpTextWatchers();
        // Set up the OnFocusChangeListeners for EditText fields
        setUpOnFocusChangeListeners();

        // Retrieve the background resource ID
        int backgroundResource = getIntent().getIntExtra("backgroundResource", R.mipmap.splash);
        // Set up the background ConstraintLayout
        ConstraintLayout backgroundLayout = findViewById(R.id.secondActivityLayout);
        backgroundLayout.setBackgroundResource(backgroundResource);
        // Apply a zoom-in animation to the background ConstraintLayout
        float zoomFactor = 1.2f; // Adjust the zoom factor as needed
        ObjectAnimator zoomIn = ObjectAnimator.ofFloat(backgroundLayout, "scaleX", zoomFactor);
        zoomIn.setDuration(500); // Adjust the duration as needed
        ObjectAnimator zoomInY = ObjectAnimator.ofFloat(backgroundLayout, "scaleY", zoomFactor);
        zoomInY.setDuration(500); // Adjust the duration as needed
        zoomIn.start();
        zoomInY.start();
    }

    private void setUpOnFocusChangeListeners() {
        for (int i = 0; i < editTexts.length; i++) {
            final int index = i;
            editTexts[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        // Restore the background drawable when an OTP field gains focus
                        if (!isIncorrectOTP) {
                            restoreBackgroundForAllFields();
                        }
                    }
                }
            });
        }
    }

    private void restoreBackgroundForAllFields() {
        // Restore the background drawable for all OTP fields
        changeBackgroundDrawable("white");
    }

    private void setUpTextWatchers() {
        for (int i = 0; i < editTexts.length; i++) {
            editTexts[i].addTextChangedListener(new MyTextWatcher(i));
        }
    }

    private boolean isAllOTPDigitsEntered() {
        for (EditText editText : editTexts) {
            if (editText.getText().toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void vibratePhone() {
        // Vibrate the phone
        if (vibrator != null) {
            vibrator.vibrate(200); // Vibrate for 200 milliseconds
        }
    }


    private String getEnteredOTP() {
        StringBuilder otpBuilder = new StringBuilder();
        for (EditText editText : editTexts) {
            otpBuilder.append(editText.getText().toString());
        }
        return otpBuilder.toString();
    }

    private void moveToNextEditText() {
        if (currentEditTextIndex < editTexts.length - 1) {
            currentEditTextIndex++;
            editTexts[currentEditTextIndex].requestFocus();
        }
    }

    private void changeBackgroundDrawable(String isCorrect) {
        // Change the background drawable of all OTP parent LinearLayouts based on correctness
        int backgroundResource;
        //isCorrect ? R.drawable.otp_field_corners : R.drawable.otp_red_corner;
        if (isCorrect.equals("green")) {
            backgroundResource = R.drawable.otp_green_corner;
        } else if (isCorrect.equals("red")) {
            backgroundResource = R.drawable.otp_red_corner;
        } else {
            backgroundResource = R.drawable.otp_field_corners;
        }
        otpLayout1.setBackgroundResource(backgroundResource);
        otpLayout2.setBackgroundResource(backgroundResource);
        otpLayout3.setBackgroundResource(backgroundResource);
        otpLayout4.setBackgroundResource(backgroundResource);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            // Handle Back button press
            if (currentEditTextIndex > 0) {
                // Move focus to the previous EditText
                currentEditTextIndex--;
                editTexts[currentEditTextIndex].requestFocus();
                // Override previous EditText's number
                editTexts[currentEditTextIndex].setText("");
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void Mesibo_onConnectionStatus(int i) {

    }

    @Override
    public void Mesibo_onProfileUpdated(MesiboProfile mesiboProfile) {

    }

    @Override
    public boolean Mesibo_onGetProfile(MesiboProfile mesiboProfile) {
        return false;
    }


    private class MyTextWatcher implements TextWatcher {
        private int index;

        public MyTextWatcher(int index) {
            this.index = index;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1) {
                moveToNextEditText();
            }
            if (isAllOTPDigitsEntered()) {
                String otp = getEnteredOTP();
                MesiboListeners.getInstance().onLogin(LoginActivity.this, "91" + phoneNumberText.getText().toString(), "");
                //verifing otp and navigating if it is giving me success
                    if (otp.equals("1527")) {
                        changeBackgroundDrawable("green");
                        AnimationUtils.animateViewVisibility(linearLayout2, false);
                        AnimationUtils.animateViewVisibility(otp_verified, true);
                        AnimationUtils.changeImageWithAnimation(otp_verified,R.drawable.otp_verified_toast_image);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText4.getWindowToken(), 0);
                        ApiGeneratorClass.verifyOTPFunction(phoneNumberText.getText().toString(),otp,LoginActivity.this);
                    }
                    else {
                        // If OTP is incorrect, change the background drawable and vibrate
                        isIncorrectOTP = true; // Set the flag to true
                        changeBackgroundDrawable("red"); // Change background for all OTP fields
                        AnimationUtils.animateViewVisibility(linearLayout2, false);
                        AnimationUtils.animateViewVisibility(otp_verified, true);
                        AnimationUtils.animateViewVisibility(resendOTP2, true);
                        AnimationUtils.changeImageWithAnimation(otp_verified,R.drawable.wront_otp_toast_image);
                        vibratePhone(); // Vibrate the phone
                }
            } else {
                // Reset the flag and background drawable when new OTP digits are entered
                isIncorrectOTP = false;
                changeBackgroundDrawable("white"); // Restore background for all OTP fields
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LoginActivity.this, SplashScreenActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}