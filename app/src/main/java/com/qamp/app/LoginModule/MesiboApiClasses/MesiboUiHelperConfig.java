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

package com.qamp.app.LoginModule.MesiboApiClasses;


import java.util.List;

public class MesiboUiHelperConfig
{
     public static boolean mScreenAnimation;
    public static String mWelcomeTermsText;
    public static String mWelcomeBottomText;
    public static String mWelcomeBottomTextLong;
    public static String mTermsUrl;
    public static String mWebsite;
    public static String mSmartLockUrl;
    public static String mWelcomeButtonName;
    public static String mName;
    public static String mDefaultCountry;
    public static String mPhoneVerificationTitle;
    public static String mPhoneVerificationText;
    public static String mPhoneVerificationBottomText;
    public static String mInvalidPhoneTitle;
    public static String mPhoneVerificationSkipText;
    public static String mPhoneSMSVerificatinDiscriptionText;
    public static String mPhoneCALLVerificatinDiscriptionTextRecent;
    public static String mPhoneCALLVerificatinDiscriptionTextOld;
    public static String mCodeVerificationBottomTextRecent;
    public static String mCodeVerificationBottomTextold;
    public static String mCodeVerificationTitle;
    public static String mPhoneVerificationRestartText;
    public static String mCountrySubString;
    public static String mPhoneNumberSubString;
    public static String mEnterCodeSubString;
    public static String mInvalidPhoneMessage;
    public static String mInvalidOTPMessage;
    public static String mInvalidOTPTitle;
    public static String mMobileConfirmationPrompt;
    public static String mMobileConfirmationTitle;
    public static List<String> mPermissions;
    public static String mPermissionsRequestMessage;
    public static String mPermissionsDeniedMessage;
    public static int mAppIconResourceId;
    public static int mButttonColor;
    public static int mButttonTextColor;
    public static int mWelcomeBackgroundColor;
    public static int mWelcomeTextColor;
    public static int mBackgroundColor;
    public static int mLoginTitleColor;
    public static int mLoginDescColor;
    public static int mLoginBottomDescColor;
    public static int mPrimaryTextColor;
    public static int mErrorTextColor;
    public static int mSecondaryTextColor;

    static {
         MesiboUiHelperConfig.mScreenAnimation = false;
        MesiboUiHelperConfig.mWelcomeTermsText = "By registering, you agree to our <b>Terms of services</b> and <b>privacy policy</b>";
        MesiboUiHelperConfig.mWelcomeBottomText = "We will never share your information";
        MesiboUiHelperConfig.mWelcomeBottomTextLong = "Mesibo never publishes anything on your facebook wall\n\n";
        MesiboUiHelperConfig.mTermsUrl = "https://mesibo.com";
        MesiboUiHelperConfig.mWebsite = "https://mesibo.com";
        MesiboUiHelperConfig.mSmartLockUrl = "https://mesibo.com/uihelper";
        MesiboUiHelperConfig.mWelcomeButtonName = "Sign Up";
        MesiboUiHelperConfig.mName = "Mesibo";
        MesiboUiHelperConfig.mDefaultCountry = "1";
        MesiboUiHelperConfig.mPhoneVerificationTitle = "welcome to mesibo";
        MesiboUiHelperConfig.mPhoneVerificationText = "Please enter a valid phone number";
        MesiboUiHelperConfig.mPhoneVerificationBottomText = "Note, Mesibo may call instead of sending an SMS if you enter a landline number.";
        MesiboUiHelperConfig.mInvalidPhoneTitle = "Invalid Phone Number";
        MesiboUiHelperConfig.mPhoneVerificationSkipText = "Already have the OTP?";
        MesiboUiHelperConfig.mPhoneSMSVerificatinDiscriptionText = "We have sent a SMS with one-time password (OTP) to %PHONENUMBER%. It may take a few minutes to receive it.";
        MesiboUiHelperConfig.mPhoneCALLVerificatinDiscriptionTextRecent = "You will soon receive a call from us on %PHONENUMBER% with one-time password (OTP). Note it down and then enter it here";
        MesiboUiHelperConfig.mPhoneCALLVerificatinDiscriptionTextOld = "You might have received a call from us with a verification code on %PHONENUMBER%. Enter that code here";
        MesiboUiHelperConfig.mCodeVerificationBottomTextRecent = "You may restart the verification if you don't receive your one-time password (OTP) within 15 minutes";
        MesiboUiHelperConfig.mCodeVerificationBottomTextold = "You may restart the verification if you haven't received your one-time password (OTP) so far";
        MesiboUiHelperConfig.mCodeVerificationTitle = "Enter one-time password (OTP)";
        MesiboUiHelperConfig.mPhoneVerificationRestartText = "Start Again";
        MesiboUiHelperConfig.mCountrySubString = "Country";
        MesiboUiHelperConfig.mPhoneNumberSubString = "Phone Number";
        MesiboUiHelperConfig.mEnterCodeSubString = "Enter one-time password (OTP)";
        MesiboUiHelperConfig.mInvalidPhoneMessage = "Invalid phone number: %PHONENUMBER% \n\nPlease check number and try again.";
        MesiboUiHelperConfig.mInvalidOTPMessage = "Invalid OTP. Please enter the exact code.";
        MesiboUiHelperConfig.mInvalidOTPTitle = "Invalid One-time password (OTP)";
        MesiboUiHelperConfig.mMobileConfirmationPrompt = "We are about to verify your phone number:\n\n+%CCODE%-%PHONENUMBER%\n\nIs this number correct?";
        MesiboUiHelperConfig.mMobileConfirmationTitle = "Confirm Phone Number";
        MesiboUiHelperConfig.mPermissions = null;
        MesiboUiHelperConfig.mPermissionsRequestMessage = "Please grant permissions to continue";
        MesiboUiHelperConfig.mPermissionsDeniedMessage = "App will close now since the required permissions were not granted";
        MesiboUiHelperConfig.mAppIconResourceId = 0;
        MesiboUiHelperConfig.mButttonColor = -15374912;
        MesiboUiHelperConfig.mButttonTextColor = -1;
        MesiboUiHelperConfig.mWelcomeBackgroundColor = -14575885;
        MesiboUiHelperConfig.mWelcomeTextColor = -1;
        MesiboUiHelperConfig.mBackgroundColor = -14575885;
        MesiboUiHelperConfig.mLoginTitleColor = -16742773;
        MesiboUiHelperConfig.mLoginDescColor = -11184811;
        MesiboUiHelperConfig.mLoginBottomDescColor = -11184811;
        MesiboUiHelperConfig.mPrimaryTextColor = -1;
        MesiboUiHelperConfig.mErrorTextColor = -56798;
        MesiboUiHelperConfig.mSecondaryTextColor = -16777216;
     }
}
