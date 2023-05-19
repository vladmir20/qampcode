/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 3:25 AM
 *
 */

//
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.Utils;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mesibo.contactutils.ContactUtils;

import java.lang.ref.WeakReference;

public class LoginCredentials implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private String mCredentialUrl;
    private AppCompatActivity mContext;
    public static final String TAG = "LoginCredentials";
    private WeakReference<Listener> mListener;
    private int mSaveReqCode;
    private int mGetReqCode;
    private int mGetHintCode;
    private ContactUtils.PhoneNumber mSavedPhone;
    private boolean mUseSmartLock;
    private GoogleApiClient mGoogleApiClient;

    LoginCredentials(final AppCompatActivity context, final String url, final Listener listener) {
        this.mListener = null;
        this.mSaveReqCode = -1;
        this.mGetReqCode = -1;
        this.mGetHintCode = -1;
        this.mSavedPhone = null;
        this.mUseSmartLock = false;
        this.mGoogleApiClient = null;
         this.mContext = context;
        this.mCredentialUrl = url;
        this.mListener = new WeakReference<Listener>(listener);
        //this.googleClientInit();
    }
    
//    private void googleClientInit() {
//        (this.mGoogleApiClient = new GoogleApiClient.Builder((Context)this.mContext).addApi(Auth.CREDENTIALS_API).addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks)this).addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener)this).enableAutoManage((FragmentActivity)this.mContext, (GoogleApiClient.OnConnectionFailedListener)this).build()).connect();
//    }
    
    private void sendNumberToListener(final String phone) {
        if (TextUtils.isEmpty((CharSequence)phone)) {
            return;
        }
        this.mSavedPhone = ContactUtils.getPhoneNumberInfo(phone);
        final Listener l = this.mListener.get();
        if (null != l) {
            l.onSavedCredentials(this.mSavedPhone);
        }
    }
    
    void save(final ContactUtils.PhoneNumber phoneNumber, final int reqCode) {
        if (null == phoneNumber || null == this.mCredentialUrl || TextUtils.isEmpty((CharSequence) this.mCredentialUrl)) {
            return;
        }
        if (null != this.mSavedPhone && null != this.mSavedPhone.mCountryCode && null != this.mSavedPhone.mNationalNumber && phoneNumber.mNationalNumber.contentEquals(this.mSavedPhone.mNationalNumber) && phoneNumber.mCountryCode.contentEquals(this.mSavedPhone.mCountryCode)) {
            final Listener l = this.mListener.get();
            if (null != l) {
                l.onSaveCompleted();
            }
            return;
        }
        final String phone = "+" + phoneNumber.mCountryCode + phoneNumber.mNationalNumber;
        this.mSaveReqCode = reqCode;
//        final Credential credential = new Credential.Builder(phone).setAccountType(this.mCredentialUrl).build();
//        Auth.CredentialsApi.save(this.mGoogleApiClient, credential).setResultCallback((ResultCallback)new ResultCallback() {
//            public void onResult(final Result result) {
//                final Status status = result.getStatus();
//                if (status.isSuccess()) {
//                    final Listener l = (Listener)LoginCredentials.this.mListener.get();
//                    if (null != l) {
//                        l.onSaveCompleted();
//                    }
//                }
//                else if (status.hasResolution()) {
//                    try {
//                        status.startResolutionForResult((Activity)LoginCredentials.this.mContext, LoginCredentials.this.mSaveReqCode);
//                    }
//                    catch (Exception ex) {}
//                }
//            }
//        });
//    }
    }
    void get(final int reqCode) {
        this.mUseSmartLock = false;
        if (null == this.mCredentialUrl || TextUtils.isEmpty((CharSequence) this.mCredentialUrl)) {
            return;
        }
        this.mGetReqCode = reqCode;
//        this.mCredentialRequest = new CredentialRequest.Builder().setAccountTypes(new String[] { this.mCredentialUrl }).build();
//        Auth.CredentialsApi.request(this.mGoogleApiClient, this.mCredentialRequest).setResultCallback((ResultCallback)new ResultCallback<CredentialRequestResult>() {
//            public void onResult(final CredentialRequestResult credentialRequestResult) {
//                if (credentialRequestResult.getStatus().isSuccess()) {
//                    final String phone = credentialRequestResult.getCredential().getId();
//                    LoginCredentials.this.sendNumberToListener(phone);
//                }
//                else if (credentialRequestResult.getStatus().hasResolution()) {
//                    try {
//                        credentialRequestResult.getStatus().startResolutionForResult((Activity)LoginCredentials.this.mContext, LoginCredentials.this.mGetReqCode);
//                    }
//                    catch (Exception ex) {}
//                }
//            }
//        });
//    }
    }
//    public void requestHint(final int reqCode, final boolean getSavedNumberIfFailed) {
//        this.mUseSmartLock = getSavedNumberIfFailed;
//        final HintRequest hintRequest = new HintRequest.Builder().setHintPickerConfig(new CredentialPickerConfig.Builder().setShowCancelButton(true).build()).setPhoneNumberIdentifierSupported(true).setEmailAddressIdentifierSupported(false).build();
//        this.mCredentialRequest = new CredentialRequest.Builder().setAccountTypes(new String[] { this.mCredentialUrl }).build();
//        final PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(this.mGoogleApiClient, hintRequest);
//        int mutableFlag = 0;
//        if (Build.VERSION.SDK_INT >= 31) {
//            mutableFlag = 67108864;
//        }
//        try {
//            this.mContext.startIntentSenderForResult(intent.getIntentSender(), reqCode, (Intent)null, 0, 0, 0);
//        }
//        catch (Exception e) {
//            TMLog.d("LoginCredentials", "Exception " + e.getMessage());
//        }
//    }
//
//    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
//        if (this.mSaveReqCode == requestCode) {
//            final Listener l = this.mListener.get();
//            if (null != l) {
//                l.onSaveCompleted();
//            }
//            return;
//        }
//        if (resultCode == -1) {
//            final Credential credential = (Credential)data.getParcelableExtra("com.google.android.gms.credentials.Credential");
//            if (credential != null) {
//                final String phone = credential.getId();
//                this.sendNumberToListener(phone);
//            }
//        }
//        else {
//            if (this.mUseSmartLock) {
//                this.mUseSmartLock = false;
//                this.get(requestCode);
//                return;
//            }
//            final Listener l = this.mListener.get();
//            if (null != l) {
//                l.onSavedCredentials(null);
//            }
//            if (1002 == resultCode) {}
//        }
//    }
//
    public void onConnected(@Nullable final Bundle bundle) {
    }
    
    public void onConnectionSuspended(final int i) {
    }
    
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
    }
    
    public interface Listener
    {
        void onSavedCredentials(final ContactUtils.PhoneNumber p0);
        
        void onSaveCompleted();
    }
}
