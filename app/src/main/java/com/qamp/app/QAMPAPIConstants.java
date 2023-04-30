/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:32 PM
 *
 */

package com.qamp.app;


public class QAMPAPIConstants {
    // public static String base_url = "http://65.0.220.147:8000/";
    public static String base_url = "http://dcore.qampservices.in/v2/user-service/";
    public static String channel_base_url = "http://dcore.qampservices.in/v1/channel-service/";
    // public static String base_url = "https://35.154.192.182/v1/user-service/";

    public static String accountId = "AC16fb61863a9a03485366c626cc65963a";
    public static String authToken = "fa4b0becd5aed4885ad60c7b192ad91a";
    public static String serviceId = "VAfe85d72e859f5b879acab88a9c726358";

    public static String statusTwilio = "off";

    // API methods
    public static String verification = "v2/Services/%s/Verifications";
    public static String verificationCheck = "v2/Services/%s/VerificationCheck";
    public static String login = "user/login";
    public static String validateOTP = "user/validateotp";
    public static String resendOTP = "user/resendotp";
    public static String updateProfile = "user/profile";
    public static String updateProfilePhoto = "user/profilefileupload";
    public static String getProfilePhoto = "user/profilefile";
    public static String getProfilePhotoById = "user/profilefilebyid/";
    public static String getThumbnailList = "user/profilefilelistthumbnail";


    public static String addChannel = "channel";
}
