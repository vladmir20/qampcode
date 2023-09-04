/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 3:25 AM
 *
 */

//
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.Utils;

import android.content.Intent;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

public class PhoneAuthenticationHelper
{
    PhoneAuthentication mAuth;
    
    public PhoneAuthenticationHelper(final AppCompatActivity activity, final PhoneNumber phone, final Listener listner, final int reqCode) {
        this.mAuth = new PhoneAuthentication(activity, phone, listner, reqCode);
    }
    
    public void stop(final boolean success) {
        this.mAuth.stop(success);
    }
    
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        this.mAuth.onActivityResult(requestCode, resultCode, data);
    }
    
    public PhoneNumber update(final PhoneNumber phoneNumber) {
        return this.mAuth.update(phoneNumber);
    }
    
    public void selectCountry() {
        this.mAuth.selectCountry();
    }
    
    public static class PhoneNumber
    {
        public String mCountryCode;
        public String mNationalNumber;
        public String mCountryName;
        public String mSmartLockUrl;
        boolean mValid;
        
        public PhoneNumber() {
            this.mCountryCode = null;
            this.mNationalNumber = null;
            this.mCountryName = null;
            this.mSmartLockUrl = null;
            this.mValid = false;
        }
        
        public boolean isValid() {
            return this.mValid;
        }
        
        public String getNumber() {
            if (TextUtils.isEmpty((CharSequence)this.mCountryCode)) {
                return "";
            }
            String phoneNumber = this.mCountryCode;
            if (!TextUtils.isEmpty((CharSequence)this.mNationalNumber)) {
                phoneNumber += this.mNationalNumber;
            }
            return phoneNumber;
        }
    }
    
    public interface Listener
    {
        boolean Mesibo_onPhoneAuthenticationNumber(final PhoneNumber p0);
        
        void Mesibo_onPhoneAuthenticationComplete();
    }
}
