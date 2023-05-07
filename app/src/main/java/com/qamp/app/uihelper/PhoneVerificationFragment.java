/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:40 AM
 *
 */

//
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.uihelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.qamp.app.R;
import com.qamp.app.uihelper.Utils.Alert;
import com.qamp.app.uihelper.Utils.BaseFragment;
import com.qamp.app.uihelper.Utils.TMLog;

public class PhoneVerificationFragment extends BaseFragment implements Alert.DialogListener, ILoginResultsInterface, OtpView.OtpViewListener, PhoneAuthenticationHelper.Listener
{
    private boolean mForced;
    private static Boolean mMode;
    private final int PHONEVIEW = 1;
    private final int CODEVIEW = 2;
    private final int PROGRESSVIEW = 3;
    private int mCurrentView;
    private ProgressDialog mProgressDialog;
    TextView mTitle;
    TextView mDescription;
    TextView mBottomNote1;
    TextView mChangeMode;
    TextView mPhoneNumberText;
    TextView mCountryText;
    TextView mEnterCodeText;
    EditText mCountryCode;
    EditText mPhoneNumber;
    EditText mVerificationCode;
    Button mOk;
    TextView mError;
    View mPhoneFields;
    View mCodeFields;
    View mBottomFields;
    LinearLayout mProgressBar;
    MesiboUiHelperConfig mConfig;
    Activity mActivity;
    View mView;
    PhoneAuthenticationHelper mAuth;
    PhoneAuthenticationHelper.PhoneNumber mPhone;
    
    public PhoneVerificationFragment() {
        this.mForced = true;
        this.mCurrentView = 1;
        this.mProgressDialog = null;
        this.mConfig = MesiboUiHelper.getConfig();
        this.mActivity = null;
        this.mView = null;
        this.mAuth = null;
        this.mPhone = new PhoneAuthenticationHelper.PhoneNumber();
    }
    
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final Bundle b = this.getArguments();
        if (null != b) {
            this.mForced = b.getBoolean("forced", true);
        }
        final View v = inflater.inflate(R.layout.fragment_phone_verification_simple, container, false);
        this.mView = v;
        this.mTitle = (TextView)v.findViewById(R.id.title);
        this.mDescription = (TextView)v.findViewById(R.id.description);
        this.mBottomNote1 = (TextView)v.findViewById(R.id.bottomNote1);
        this.mChangeMode = (TextView)v.findViewById(R.id.changemode);
        this.mCountryCode = (EditText)v.findViewById(R.id.country_code);
        this.mPhoneNumber = (EditText)v.findViewById(R.id.phone);
        this.mVerificationCode = (EditText)v.findViewById(R.id.code);
        final Drawable background = this.mPhoneNumber.getBackground();
        final MesiboUiHelperConfig mConfig = this.mConfig;
        background.setColorFilter(MesiboUiHelperConfig.mSecondaryTextColor, PorterDuff.Mode.SRC_IN);
        final Drawable background2 = this.mCountryCode.getBackground();
        final MesiboUiHelperConfig mConfig2 = this.mConfig;
        background2.setColorFilter(MesiboUiHelperConfig.mSecondaryTextColor, PorterDuff.Mode.SRC_IN);
        final Drawable background3 = this.mVerificationCode.getBackground();
        final MesiboUiHelperConfig mConfig3 = this.mConfig;
        background3.setColorFilter(MesiboUiHelperConfig.mSecondaryTextColor, PorterDuff.Mode.SRC_IN);
        final EditText mPhoneNumber = this.mPhoneNumber;
        final MesiboUiHelperConfig mConfig4 = this.mConfig;
        mPhoneNumber.setTextColor(MesiboUiHelperConfig.mSecondaryTextColor);
        final EditText mCountryCode = this.mCountryCode;
        final MesiboUiHelperConfig mConfig5 = this.mConfig;
        mCountryCode.setTextColor(MesiboUiHelperConfig.mSecondaryTextColor);
        final EditText mVerificationCode = this.mVerificationCode;
        final MesiboUiHelperConfig mConfig6 = this.mConfig;
        mVerificationCode.setTextColor(MesiboUiHelperConfig.mSecondaryTextColor);
        final View view = v;
        final MesiboUiHelperConfig mConfig7 = this.mConfig;
        view.setBackgroundColor(MesiboUiHelperConfig.mBackgroundColor);
        this.mOk = (Button)v.findViewById(R.id.button_next);
        final Button mOk = this.mOk;
        final MesiboUiHelperConfig mConfig8 = this.mConfig;
        mOk.setBackgroundColor(MesiboUiHelperConfig.mButttonColor);
        final Button mOk2 = this.mOk;
        final MesiboUiHelperConfig mConfig9 = this.mConfig;
        mOk2.setTextColor(MesiboUiHelperConfig.mButttonTextColor);
        this.mPhoneNumberText = (TextView)v.findViewById(R.id.phone_number_text);
        final TextView mPhoneNumberText = this.mPhoneNumberText;
        final MesiboUiHelperConfig mConfig10 = this.mConfig;
        mPhoneNumberText.setText((CharSequence)MesiboUiHelperConfig.mPhoneNumberSubString);
        final TextView mPhoneNumberText2 = this.mPhoneNumberText;
        final MesiboUiHelperConfig mConfig11 = this.mConfig;
        mPhoneNumberText2.setTextColor(MesiboUiHelperConfig.mSecondaryTextColor);
        this.mCountryText = (TextView)v.findViewById(R.id.country_text);
        final TextView mCountryText = this.mCountryText;
        final MesiboUiHelperConfig mConfig12 = this.mConfig;
        mCountryText.setText((CharSequence)MesiboUiHelperConfig.mCountrySubString);
        final TextView mCountryText2 = this.mCountryText;
        final MesiboUiHelperConfig mConfig13 = this.mConfig;
        mCountryText2.setTextColor(MesiboUiHelperConfig.mSecondaryTextColor);
        this.mEnterCodeText = (TextView)v.findViewById(R.id.enter_code_text);
        final TextView mEnterCodeText = this.mEnterCodeText;
        final MesiboUiHelperConfig mConfig14 = this.mConfig;
        mEnterCodeText.setText((CharSequence)MesiboUiHelperConfig.mEnterCodeSubString);
        final TextView mEnterCodeText2 = this.mEnterCodeText;
        final MesiboUiHelperConfig mConfig15 = this.mConfig;
        mEnterCodeText2.setTextColor(MesiboUiHelperConfig.mSecondaryTextColor);
        final TextView mTitle = this.mTitle;
        final MesiboUiHelperConfig mConfig16 = this.mConfig;
        mTitle.setTextColor(MesiboUiHelperConfig.mLoginTitleColor);
        final TextView mDescription = this.mDescription;
        final MesiboUiHelperConfig mConfig17 = this.mConfig;
        mDescription.setTextColor(MesiboUiHelperConfig.mLoginDescColor);
        final TextView mBottomNote1 = this.mBottomNote1;
        final MesiboUiHelperConfig mConfig18 = this.mConfig;
        mBottomNote1.setTextColor(MesiboUiHelperConfig.mLoginBottomDescColor);
        final TextView mChangeMode = this.mChangeMode;
        final MesiboUiHelperConfig mConfig19 = this.mConfig;
        mChangeMode.setTextColor(MesiboUiHelperConfig.mPrimaryTextColor);
        this.mError = (TextView)v.findViewById(R.id.error);
        final TextView mError = this.mError;
        final MesiboUiHelperConfig mConfig20 = this.mConfig;
        mError.setTextColor(MesiboUiHelperConfig.mErrorTextColor);
        this.mPhoneFields = v.findViewById(R.id.verify_phone_fields);
        this.mCodeFields = v.findViewById(R.id.verify_code_fields);
        this.mBottomFields = v.findViewById(R.id.bottomInfoFields);
        this.mChangeMode.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
            public void onClick(final View v) {
                PhoneVerificationFragment.this.toggleView();
            }
        });
        this.mOk.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
            public void onClick(final View v) {
                PhoneVerificationFragment.this.startPhoneVerification();
            }
        });
        final EditText mCountryCode2 = this.mCountryCode;
        final StringBuilder append = new StringBuilder().append("+");
        final MesiboUiHelperConfig mConfig21 = this.mConfig;
        mCountryCode2.setText((CharSequence)append.append(MesiboUiHelperConfig.mDefaultCountry).toString());
        final PhoneAuthenticationHelper.PhoneNumber mPhone = this.mPhone;
        final MesiboUiHelperConfig mConfig22 = this.mConfig;
        mPhone.mCountryCode = MesiboUiHelperConfig.mDefaultCountry;
        this.mCountryCode.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
            public void onClick(final View v) {
                PhoneVerificationFragment.this.mAuth.selectCountry();
            }
        });
        this.mProgressDialog = Alert.getProgressDialog((Context)this.getActivity(), "Please wait...");
        final PhoneAuthenticationHelper.PhoneNumber mPhone2 = this.mPhone;
        final MesiboUiHelperConfig mConfig23 = this.mConfig;
        mPhone2.mSmartLockUrl = MesiboUiHelperConfig.mSmartLockUrl;
        this.mAuth = new PhoneAuthenticationHelper((AppCompatActivity)this.getActivity(), this.mPhone, this, 5001);
        return v;
    }
    
    public void onResume() {
        super.onResume();
        if (null != this.mFragmentListener) {
            this.mFragmentListener.onFragmentLoaded(this, this.getClass(), true);
        }
        this.showView(1);
    }
    
    public void onPause() {
        super.onPause();
    }
    
    @Override
    public void onDialog(final int id, final int state) {
        if (0 == id) {
            if (state == 1) {
                PhoneVerificationFragment.mMode = false;
                this.mProgressDialog.show();
                final ILoginInterface i = MesiboUiHelper.getLoginInterface();
                i.onLogin((Context)this.getActivity(), this.mPhone.getNumber(), null, this);
            }
            return;
        }
        if (1 == id && state == 1) {
            this.showOTPDialog();
        }
    }
    
    @Override
    public boolean Mesibo_onPhoneAuthenticationNumber(final PhoneAuthenticationHelper.PhoneNumber phoneNumber) {
        if (null == phoneNumber) {
            return false;
        }
        if (!TextUtils.isEmpty((CharSequence)phoneNumber.mCountryCode)) {
            this.setCountryCode(phoneNumber.mCountryCode);
        }
        if (!TextUtils.isEmpty((CharSequence)phoneNumber.mNationalNumber)) {
            this.mPhoneNumber.setText((CharSequence)phoneNumber.mNationalNumber);
        }
        this.mPhoneNumber.setFocusableInTouchMode(true);
        this.mPhoneNumber.setFocusable(true);
        if (this.mPhoneNumber.requestFocus()) {}
        return false;
    }
    
    @Override
    public void Mesibo_onPhoneAuthenticationComplete() {
        final Activity a = (Activity)this.getActivity();
        if (null != a) {
            a.finish();
        }
    }
    
    public void startPhoneVerification() {
        this.mPhone.mNationalNumber = this.mPhoneNumber.getText().toString().trim();
        this.mPhone = this.mAuth.update(this.mPhone);
        if (!this.mPhone.mValid) {
            this.showError("Invalid Phone Number");
            Alert.showInvalidPhoneNumber((Context)this.getActivity());
            return;
        }
        this.showError(null);
        final MesiboUiHelperConfig mConfig = this.mConfig;
        String conformationPrompt = MesiboUiHelperConfig.mMobileConfirmationPrompt;
        conformationPrompt = conformationPrompt.replace("%PHONENUMBER%", this.mPhone.mNationalNumber);
        conformationPrompt = conformationPrompt.replace("%CCODE%", this.mPhone.mCountryCode);
        final ILoginInterface i = MesiboUiHelper.getLoginInterface();
        i.onLogin((Context)this.getActivity(), this.mPhone.getNumber(), null, this);
    }
    
    public void onCancel() {
        this.getActivity().finish();
    }
    
    public void startCodeVerification(final String code) {
        if (null == code) {
            return;
        }
        int codeint = 0;
        try {
            codeint = Integer.parseInt(code);
        }
        catch (Exception e) {
            final MesiboUiHelperConfig mConfig = this.mConfig;
            this.showError(MesiboUiHelperConfig.mInvalidPhoneTitle);
            return;
        }
        this.showError(null);
        PhoneVerificationFragment.mMode = true;
        this.mProgressDialog.show();
        final ILoginInterface i = MesiboUiHelper.getLoginInterface();
        i.onLogin((Context)this.getActivity(), this.mPhone.getNumber(), code, this);
    }
    
    private void setCountryCode(final String code) {
        this.mCountryCode.setText((CharSequence)("+" + code));
    }
    
    private void showError(final String error) {
        if (TextUtils.isEmpty((CharSequence)error)) {
            this.mError.setVisibility(8);
            return;
        }
        this.mError.setText((CharSequence)error);
        this.mError.setVisibility(0);
    }
    
    public void toggleView() {
        this.showError(null);
        if (1 == this.mCurrentView) {
            this.showView(2);
        }
        else {
            this.showView(1);
        }
    }
    
    public void onBackKeyPressed() {
        this.getActivity().finish();
    }
    
    private void showView(final int vid) {
        if (3 == vid) {
            return;
        }
        if (1 == (this.mCurrentView = vid)) {
            final TextView mChangeMode = this.mChangeMode;
            final MesiboUiHelperConfig mConfig = this.mConfig;
            mChangeMode.setText((CharSequence)MesiboUiHelperConfig.mPhoneVerificationSkipText);
            final TextView mTitle = this.mTitle;
            final MesiboUiHelperConfig mConfig2 = this.mConfig;
            mTitle.setText((CharSequence)MesiboUiHelperConfig.mPhoneVerificationTitle);
            final TextView mDescription = this.mDescription;
            final MesiboUiHelperConfig mConfig3 = this.mConfig;
            mDescription.setText((CharSequence)MesiboUiHelperConfig.mPhoneVerificationText);
            final TextView mBottomNote1 = this.mBottomNote1;
            final MesiboUiHelperConfig mConfig4 = this.mConfig;
            mBottomNote1.setText((CharSequence)MesiboUiHelperConfig.mPhoneVerificationBottomText);
            this.mPhoneFields.setVisibility(0);
            this.mCodeFields.setVisibility(8);
        }
        else {
            this.showOTPDialog();
        }
    }
    
    @Override
    public void OtpView_onOtp(final String enteredOtp) {
        if (null != enteredOtp) {
            this.startCodeVerification(enteredOtp);
        }
    }
    
    @Override
    public void OtpView_onResend() {
    }
    
    public void showOTPDialog() {
        String phone = "your phone";
        if (this.mPhone != null) {
            phone = "+" + this.mPhone.getNumber();
        }
        final MesiboUiHelperConfig mConfig = this.mConfig;
        final OtpView.OtpViewConfig config = MesiboUiHelperConfig.otpConfig;
        config.mPhone = phone;
        final OtpView otpView = new OtpView((Context)this.getActivity(), config, this);
        otpView.showPopup(this.mView);
    }
    
    @Override
    public void onLoginResult(final boolean result, final int delay) {
        if (this.mProgressDialog.isShowing()) {
            this.mProgressDialog.dismiss();
        }
        if (result) {
            if (PhoneVerificationFragment.mMode) {
                this.mAuth.stop(true);
            }
            else {
                this.showView(2);
            }
        }
        else if (PhoneVerificationFragment.mMode) {
            final MesiboUiHelperConfig mConfig = this.mConfig;
            final String prompt = MesiboUiHelperConfig.mInvalidOTPMessage;
            final FragmentActivity activity = this.getActivity();
            final MesiboUiHelperConfig mConfig2 = this.mConfig;
            Alert.showAlertDialog((Context)activity, MesiboUiHelperConfig.mInvalidOTPTitle, prompt, "OK", "Cancel", 1, this, true);
        }
        else {
            final MesiboUiHelperConfig mConfig3 = this.mConfig;
            String prompt = MesiboUiHelperConfig.mInvalidPhoneMessage;
            prompt = prompt.replace("%MOBILENUMBER%", this.mPhone.getNumber());
            final FragmentActivity activity2 = this.getActivity();
            final MesiboUiHelperConfig mConfig4 = this.mConfig;
            Alert.showAlertDialog((Context)activity2, MesiboUiHelperConfig.mInvalidPhoneTitle, prompt, "OK", null, 2, this, true);
        }
        TMLog.d("REG-Ver", "on MesiboUiHelper results");
    }
    
    @Override
    public void onActivityResultPrivate(final int requestCode, final int resultCode, final Intent data) {
        this.mAuth.onActivityResult(requestCode, resultCode, data);
    }
    
    static {
        PhoneVerificationFragment.mMode = false;
    }
}
