/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:31 PM
 *
 */

//
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.uihelper;

import android.content.Intent;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.mesibo.contactutils.ContactUtils;

import java.lang.ref.WeakReference;

public class PhoneAuthentication implements LoginCredentials.Listener
{
    private WeakReference<PhoneAuthenticationHelper.Listener> mListener;
    private PhoneAuthenticationHelper.PhoneNumber mPhone;
    private AppCompatActivity mActivity;
    private LoginCredentials mLoginCredentials;
    private ContactUtils.PhoneNumber mParsedNumber;
    private int mReqCode;
    
    PhoneAuthentication(final AppCompatActivity activity, final PhoneAuthenticationHelper.PhoneNumber phone, final PhoneAuthenticationHelper.Listener listner, final int reqCode) {
        this.mListener = null;
        this.mPhone = null;
        this.mActivity = null;
        this.mLoginCredentials = null;
        this.mParsedNumber = null;
        this.mReqCode = -1;
        this.mActivity = activity;
        this.mReqCode = reqCode;
        if (null != listner) {
            this.mListener = new WeakReference<PhoneAuthenticationHelper.Listener>(listner);
        }
        this.update(phone);
        this.invokePhoneListener();
    }
    
    private AppCompatActivity getActivity() {
        return this.mActivity;
    }
    
    private void invokePhoneListener() {
        if (null == this.mListener) {
            return;
        }
        final PhoneAuthenticationHelper.Listener l = this.mListener.get();
        if (null == l) {
            return;
        }
        l.Mesibo_onPhoneAuthenticationNumber(this.mPhone);
    }
    
    @Override
    public void onSavedCredentials(final ContactUtils.PhoneNumber phone) {
        if (null != phone) {
            this.mPhone.mCountryCode = phone.mCountryCode;
            this.mPhone.mCountryName = phone.mCountry;
            this.mPhone.mNationalNumber = phone.mNationalNumber;
            this.invokePhoneListener();
            return;
        }
        if (TextUtils.isEmpty((CharSequence)this.mPhone.mCountryCode)) {
            this.selectCountry();
        }
    }
    
    @Override
    public void onSaveCompleted() {
        if (null == this.mListener) {
            return;
        }
        final PhoneAuthenticationHelper.Listener l = this.mListener.get();
        if (null == l) {
            return;
        }
        l.Mesibo_onPhoneAuthenticationComplete();
    }
    
    void stop(final boolean success) {
        if (success) {
            final ContactUtils.PhoneNumber p = new ContactUtils.PhoneNumber();
            p.mCountryCode = this.mPhone.mCountryCode;
            p.mNationalNumber = this.mPhone.mNationalNumber;
        }
    }
    
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    }
    
    public PhoneAuthenticationHelper.PhoneNumber update(final PhoneAuthenticationHelper.PhoneNumber phone) {
        this.mPhone = phone;
        if (null == this.mPhone) {
            this.mPhone = new PhoneAuthenticationHelper.PhoneNumber();
        }
        if (null == this.mPhone.mCountryCode) {
            ContactUtils.init(this.mActivity.getApplicationContext());
            this.mPhone.mCountryCode = ContactUtils.getCountryCode();
            this.mPhone.mCountryName = ContactUtils.getCountryName();
        }
        final String phoneNumber = this.mPhone.getNumber();
        if (!TextUtils.isEmpty((CharSequence)phoneNumber)) {
            this.mParsedNumber = ContactUtils.getPhoneNumberInfo(phoneNumber);
        }
        if (null != this.mParsedNumber) {
            this.mPhone.mValid = this.mParsedNumber.mValid;
            if (!TextUtils.isEmpty((CharSequence)this.mParsedNumber.mCountry)) {
                this.mPhone.mCountryName = this.mParsedNumber.mCountry;
            }
            if (!TextUtils.isEmpty((CharSequence)this.mParsedNumber.mCountryCode)) {
                this.mPhone.mCountryCode = this.mParsedNumber.mCountryCode;
            }
            if (!TextUtils.isEmpty((CharSequence)this.mParsedNumber.mNationalNumber)) {
                this.mPhone.mNationalNumber = this.mParsedNumber.mNationalNumber;
            }
        }
        return this.mPhone;
    }
    
    public void selectCountry() {
        final CountryListFragment countryListFragment = new CountryListFragment();
        countryListFragment.setOnCountrySelected(new CountryListFragment.CountryListerer() {
            @Override
            public void onCountrySelected(final String name, final String code) {
                PhoneAuthentication.this.mPhone.mCountryCode = code;
                PhoneAuthentication.this.update(PhoneAuthentication.this.mPhone);
                PhoneAuthentication.this.invokePhoneListener();
            }
            
            @Override
            public void onCountryCanceled() {
            }
        });
        countryListFragment.show(this.mActivity.getSupportFragmentManager(), (String)null);
    }
}
