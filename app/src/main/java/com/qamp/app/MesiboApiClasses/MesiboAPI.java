/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 3:25 AM
 *
 */

package com.qamp.app.MesiboApiClasses;

import static com.qamp.app.MessagingModule.MesiboConfiguration.MISSED_CALL;
import static com.qamp.app.MessagingModule.MesiboConfiguration.NO_INTERNET;
import static com.qamp.app.MessagingModule.MesiboConfiguration.PHONE_NOT_CONNECTED;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboHttp;
import com.mesibo.api.MesiboMessageProperties;
import com.mesibo.api.MesiboProfile;
import com.mesibo.contactutils.ContactUtils;
import com.mesibo.mediapicker.MediaPicker;
import com.qamp.app.MainApplication;
import com.qamp.app.Utils.NotifyUser;
import com.qamp.app.Utils.QampConstants;
import com.qamp.app.R;
import com.qamp.app.Utils.AppConfig;
import com.qamp.app.qampcallss.api.MesiboCall;
import com.qamp.app.Utils.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Observable;


public class MesiboAPI extends Observable {
    public final static String KEY_SYNCEDCONTACTS = "AppSyncedContacts";
    public final static String KEY_SYNCEDDEVICECONTACTSTIME = "AppSyncedPhoneContactTs";
    public final static String KEY_SYNCEDCONTACTSTIME = "AppSyncedTsNew";
    public final static String KEY_AUTODOWNLOAD = "autodownload";
    public final static String KEY_GCMTOKEN = "gcmtoken";
    public static final int VISIBILITY_HIDE = 0;
    public static final int VISIBILITY_VISIBLE = 1;
    public static final int VISIBILITY_UNCHANGED = 2;
    private static final String TAG = "HuddleAPI";
    private static final String DEFAULT_FILE_URL = "https://media.mesibo.com/files/";
    private static NotifyUser mNotifyUser = null;
    private static boolean mSyncPending = true;
    private static boolean mContactSyncOver = false;
    private static Context mContext = null;
    private static boolean mResetSyncedContacts = false;
    private static String mAkClientToken = null;
    private static String mAkAppId = null;
    private static boolean mAutoDownload = true;
    private static String FCM_SENDER_ID = QampConstants.FCM_SENDER_ID;

    private static Gson mGson = new Gson();
    private static String mApiUrl = QampConstants.mApiUrl;
    private static long mContactTs = 0;
    //private static ArrayList<Mesibo.UserProfile> mPendingHiddenContacts = null;


    public static void init(Context context) {
        mContext = context;

        Mesibo api = Mesibo.getInstance();
        api.init(context);

        //Mesibo.initCrashHandler(MesiboListeners.getInstance());
        Mesibo.uploadCrashLogs();
        //Mesibo.setSecureConnection(true);

        if (!TextUtils.isEmpty(AppConfig.getConfig().token)) {
            if (startMesibo(false))
                startSync();
        }
    }

    public static void login(String phoneNumber, String verificationCode, ResponseHandler handler) {
        JSONObject b = new JSONObject();
        try {
            b.put("op", "login");
            b.put("appid", QampConstants.huddlePackage);
//            b.put("appid", "org.mesibo.messenger");
            b.put("phone", phoneNumber);
            if (!TextUtils.isEmpty(verificationCode))
                b.put("otp", verificationCode);
        } catch (Exception e) {

        }

        handler.setOnUiThread(true);
        handler.sendRequest(b, null, null);
    }

    private static boolean parseResponse(Response response, Context context, boolean uiThread) {
        if (null == response || null == response.result) {
            if (uiThread && null != context) {
                showConnectionError(context);
            }

            return false;
        }

        if (!response.result.equalsIgnoreCase("OK")) {
            if (null != response.error && response.error.equalsIgnoreCase("AUTHFAIL")) {
                Toast.makeText(mContext, R.string.general_error, Toast.LENGTH_SHORT).show();
                return false;
            }

            if (null != response.error && response.error.equalsIgnoreCase("UPDATE")) {
            }

            if (null != response.errmsg) {
                if (null == response.errtitle) response.errtitle = "Failed";
                Toast.makeText(mContext, R.string.general_error, Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        boolean save = false;
        if (null != response.urls) {
            AppConfig.getConfig().uploadurl = response.urls.upload;
            AppConfig.getConfig().downloadurl = response.urls.download;
            save = true;
        }

        if (null != response.share) {
            AppConfig.getConfig().invite = response.share;
            save = true;
        }

        if (response.op.equals("login") && !TextUtils.isEmpty(response.token)) {
            AppConfig.getConfig().token = response.token; //TBD, save into preference
            AppConfig.getConfig().phone = response.phone;
            AppConfig.getConfig().cc = response.cc;
            AppConfig.getConfig().name = response.name; //TBD, save into preference
            AppConfig.getConfig().status = response.status;
            AppConfig.getConfig().photo = response.photo;
            AppConfig.getConfig().ts = response.ts;
            AppConfig.getConfig().tnm = response.tn;

            mResetSyncedContacts = true;
            mSyncPending = true;

            save = true;

            Mesibo.reset();
            if (startMesibo(true)) {
                startSync();
            }
        } else if (response.op.equals("logout")) {
//            forceLogout();
            AppConfig.reset();
        }

        if (save)
            AppConfig.save();
        return true;
    }

    public static void setSyncFlags() {
        mResetSyncedContacts = true;
        mSyncPending = true;
    }

    private static boolean invokeApi(final Context context, final JSONObject postBunlde, String filePath, String formFieldName, boolean uiThread) {
        ResponseHandler http = new ResponseHandler() {
            @Override
            public void HandleAPIResponse(Response response) {

            }
        };

        http.setContext(context);
        http.setOnUiThread(uiThread);
        return http.sendRequest(postBunlde, filePath, formFieldName);
    }

    public static void showConnectionError(Context context) {
        String title = NO_INTERNET;
        String message = PHONE_NOT_CONNECTED;
    }

    public static boolean startMesibo(boolean resetContacts) {
//        // set path for storing DB and MessagingModule files
//        Mesibo.setPath(Environment.getExternalStorageDirectory().getAbsolutePath());

//        MesiboRegistrationIntentService.startRegistration(mContext, "978866948854", MesiboListeners.getInstance());
        Log.e("line1",String.valueOf(resetContacts));

        String path = Mesibo.getBasePath();
        MediaPicker.setPath(path);
        Log.e("line2",String.valueOf(path));

        // add lister
        Mesibo.addListener(MesiboListeners.getInstance());
        com.qamp.app.qampcallss.api.MesiboCall.getInstance().setListener(MesiboListeners.getInstance());

        // add file transfer handler
        MesiboFileTransferHelper fileTransferHelper = new MesiboFileTransferHelper();
        Mesibo.addListener(fileTransferHelper);
        Log.e("line3",String.valueOf(path));

        //this will also register listener from the constructor
        mNotifyUser = new NotifyUser(MainApplication.getAppContext());
        Log.e("line4",String.valueOf(path));
        // set access token
        int i = Mesibo.setAccessToken(AppConfig.getConfig().token);
        //Toast.makeText(mContext, "true"+String.valueOf(i), Toast.LENGTH_LONG).show();
        Log.e("TestToken",String.valueOf(i));
        if (0 != i) {
//            AppConfig.getConfig().token = "";
            AppConfig.save();
            Mesibo.setMessageRetractionInterval(172800);
            Log.e("TestToken2",String.valueOf(i));
            //  Toast.makeText(mContext, "true"+String.valueOf(i), Toast.LENGTH_LONG).show();
            return false;
        }

        // set database after setting access token so that it's associated with user
        Mesibo.setDatabase("mesibo.db", 0);

        // do this after setting token and db
        if (resetContacts) {
            MesiboAPI.saveLocalSyncedContacts("", 0);
            MesiboAPI.saveSyncedTimestamp(0);
        }

        initAutoDownload();

        // Now start mesibo
        int test = Mesibo.start();
        if (0 != test) {
            return false;
        }
        Mesibo.setMessageRetractionInterval(172800);

        String ts = Mesibo.readKey(KEY_SYNCEDCONTACTSTIME);

        ContactUtils.init(mContext);
        int cc = Mesibo.getCountryCodeFromPhone(AppConfig.getConfig().phone);
        ContactUtils.setCountryCode(String.valueOf(cc));

        if (resetContacts) {
            ContactUtils.syncReset();
        }

        return true;
    }

    public static boolean startLogout() {
        if (TextUtils.isEmpty(AppConfig.getConfig().token))
            return false;
        JSONObject b = new JSONObject();
        try {
            b.put("op", "logout");
            b.put("token", AppConfig.getConfig().token);
            AppConfig.getConfig().token = "";
            AppConfig.save();
        } catch (Exception e) {

        }
        invokeApi(null, b, null, null, false);
        return true;
    }

    public static void notify(int id, String title, String message) {
        mNotifyUser.sendNotification(id, title, message);
    }

    public static void notify(MesiboMessageProperties params, String message) {
        // if call is in progress, we must give notification even if reading because user is in call
        // screen
        if (!MesiboCall.getInstance().isCallInProgress() && Mesibo.isReading(params))
            return;

        if (Mesibo.ORIGIN_REALTIME != 0 || Mesibo.MSGSTATUS_OUTBOX == params.getStatus())///test out
            return;

        //MUST not happen for realtime message
        if (params.groupid > 0 && null == params.groupProfile)
            return;

        MesiboProfile profile = Mesibo.getProfile(params);

        // this will also mute message from user in group
        if (null != profile && profile.isMuted())
            return;

        String name = params.peer;
        if (null != profile) {
            name = profile.getName();
        }

        if (params.groupid > 0) {
            MesiboProfile gp = Mesibo.getProfile(params.groupid);
            if (null == gp)
                return; // must not happen

            if (gp.isMuted())
                return;

            name += " @ " + gp.getName();
        }

        if (params.isMissedCall()) {
            String subject = MISSED_CALL;
            message = "You missed a huddle " + (params.isVideoCall() ? "video " : "") + "call from " + profile.getName();
            MesiboAPI.notify(2, subject, message);
            return;
        }

        // outgoing or incoming call
        if (params.isCall()) return;

        mNotifyUser.sendNotificationInList(name, message);
    }

    public static void notifyClear() {
        mNotifyUser.clearNotification();
    }

    public static String getToken() {
        return AppConfig.getConfig().token;
    }

    public static String getUploadUrl() {
        return AppConfig.getConfig().uploadurl;
    }

    public static String getDownloadUrl() {
        return AppConfig.getConfig().downloadurl;
    }

    public static void startOnlineAction() {
        sendGCMToken();
        startSync();
    }

    public static void saveLocalSyncedContacts(String contacts, long timestamp) {
        Mesibo.setKey(MesiboAPI.KEY_SYNCEDCONTACTS, contacts);
        Mesibo.setKey(MesiboAPI.KEY_SYNCEDDEVICECONTACTSTIME, String.valueOf(timestamp));
    }

    public static void saveSyncedTimestamp(long timestamp) {
        Mesibo.setKey(MesiboAPI.KEY_SYNCEDCONTACTSTIME, String.valueOf(timestamp));
    }

    public static void startSync() {

        synchronized (MesiboAPI.class) {
            if (!mSyncPending)
                return;
            mSyncPending = false;
        }

        startContactsSync();
    }

    private static Bundle createPostBundle(String op) {
        if (TextUtils.isEmpty(AppConfig.getConfig().token))
            return null;

        Bundle b = new Bundle();
        b.putString("op", op);
        b.putString("token", AppConfig.getConfig().token);
        return b;
    }

    public static String array2String(String[] a) {
        String str = "";
        for (int i = 0; i < a.length; i++) {
            if (i > 0)
                str += ",";
            str += a[i];
        }

        return str;
    }

    public static boolean getContacts(ArrayList<String> contacts, boolean contact, boolean syncNow) {
        if (null == contacts || contacts.size() == 0)
            return false;

        String[] c = contacts.toArray(new String[contacts.size()]);
        Mesibo.syncContacts(c, contact, true, 0, syncNow);
        return true;
    }

    public static void addContacts(ArrayList<MesiboProfile> profiles, boolean hidden) {
        ArrayList<String> c = new ArrayList<String>();

        for (int i = 0; i < profiles.size(); i++) {
            MesiboProfile profile = profiles.get(i);
            if (!TextUtils.isEmpty(profile.address))
                c.add(profile.address);
        }

        if (c.size() == 0)
            return;

        getContacts(c, hidden, true);
    }

    public static void setGCMToken(String token) {
//        mGCMToken = token;
//        sendGCMToken();
    }

    private static void sendGCMToken() {
//        if(null == mGCMToken || mGCMTokenSent) {
//            return;
//        }
//
//        synchronized (SampleAPI.class) {
//            if(mGCMTokenSent)
//                return;
//            mGCMTokenSent = true;
//        }
//
//        Mesibo.setPushToken(mGCMToken);

    }

    /* if it is called from service, it's okay to block, we should wait till
       we are online. As soon as we return, service will be destroyed
     */
    public static void onGCMMessage(boolean inService) {
        Mesibo.setForegroundContext(null, -1, true);

        while (inService) {
            if (Mesibo.STATUS_ONLINE == Mesibo.getConnectionStatus())
                break;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }

        // wait for messages to receive etc
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    public static void syncDone() {
        synchronized (MesiboAPI.class) {

        }
    }

    public static String getPhone() {
        if (!TextUtils.isEmpty(AppConfig.getConfig().phone)) {
            return AppConfig.getConfig().phone;
        }


        MesiboProfile u = Mesibo.getSelfProfile();

        //MUST not happen
        if (null == u) {
//            forceLogout();
            return null;
        }
        AppConfig.getConfig().phone = u.address;
        AppConfig.save();
        return AppConfig.getConfig().phone;
    }

    public static boolean deleteContacts(ArrayList<String> contacts) {
        if (null == contacts || 0 == contacts.size())
            return false;

        String[] c = contacts.toArray(new String[contacts.size()]);
        Mesibo.syncContacts(c, false, true, 0, true);

        return true;
    }

    public static void startContactsSync() {
        String synced = Mesibo.readKey(KEY_SYNCEDCONTACTS);
        String syncedts = Mesibo.readKey(KEY_SYNCEDDEVICECONTACTSTIME);
        long ts = 0;
        if (!TextUtils.isEmpty(syncedts)) {
            try {
                ts = Long.parseLong(syncedts);
            } catch (Exception e) {
            }
        }
        //       if (PermissionChecker.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS) != PermissionChecker.PERMISSION_GRANTED) {

        //    } else {
        ContactUtils.sync(synced, ts, false, MesiboListeners.getInstance());
        //  }
    }

    public static String phoneBookLookup(String phone) {
        if (TextUtils.isEmpty(phone))
            return null;

        return ContactUtils.reverseLookup(phone);
    }

//    public static synchronized  void autoAddContact(Mesibo.MessageParams params) {
//        if(null == params) {
//            if(null != mPendingHiddenContacts) {
//                addContacts(mPendingHiddenContacts, true);
//                mPendingHiddenContacts = null;
//            }
//            return;
//        }
//
//        // the logic is if user replies, we will see contact details, else not */
//        if(/*Mesibo.ORIGIN_REALTIME != params.origin || */Mesibo.MSGSTATUS_OUTBOX == params.getStatus())
//            return;
//
//        if((params.profile.flag& Mesibo.UserProfile.FLAG_TEMPORARY) == 0 || (params.profile.flag & Mesibo.UserProfile.FLAG_PROFILEREQUESTED) > 0 )
//            return;
//
//        if(null == mPendingHiddenContacts)
//            mPendingHiddenContacts = new ArrayList<Mesibo.UserProfile>();
//
//        mPendingHiddenContacts.add(params.profile);
//
//        if(!mContactSyncOver) {
//            return;
//        }
//
//        addContacts(mPendingHiddenContacts, true);
//        mPendingHiddenContacts = null;
//    }

    private static void initAutoDownload() {
        String autodownload = Mesibo.readKey(KEY_AUTODOWNLOAD);
        mAutoDownload = (TextUtils.isEmpty(autodownload));
    }

    public static boolean getMediaAutoDownload() {
        return mAutoDownload;
    }

    public static void setMediaAutoDownload(boolean autoDownload) {
        mAutoDownload = autoDownload;
        Mesibo.setKey(KEY_AUTODOWNLOAD, mAutoDownload ? "" : "0");
    }

    public static abstract class ResponseHandler implements MesiboHttp.Listener {
        public static boolean result = true;
        public Context mContext = null;
        private MesiboHttp http = null;
        private Bundle mRequest = null;
        private boolean mBlocking = false;
        private boolean mOnUiThread = false;

        @Override
        public boolean Mesibo_onHttpProgress(MesiboHttp http, int state, int percent) {
            if (percent < 0) {
                HandleAPIResponse(null);
                return true;
            }

            if (100 == percent && MesiboHttp.STATE_DOWNLOAD == state) {
                String strResponse = http.getDataString();
                Response response = null;

                if (null != strResponse) {
                    try {
                        response = mGson.fromJson(strResponse, Response.class);
                    } catch (Exception e) {
                        result = false;
                    }
                }

                if (null == response)
                    result = false;

                final Context context = (null == this.mContext) ? MesiboAPI.mContext : this.mContext;

                if (!mOnUiThread) {
                    parseResponse(response, context, false);
                    HandleAPIResponse(response);
                } else {
                    final Response r = response;

                    if (null == context)
                        return true;

                    Handler uiHandler = new Handler(context.getMainLooper());

                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            parseResponse(r, context, true);
                            HandleAPIResponse(r);
                        }
                    };
                    uiHandler.post(myRunnable);
                }
            }
            return true;
        }

        public void setBlocking(boolean blocking) {
            mBlocking = blocking;
        }

        public void setOnUiThread(boolean onUiThread) {
            mOnUiThread = onUiThread;
        }

        public boolean sendRequest(JSONObject j, String filePath, String formFieldName) {
            try {
                j.put("dt", String.valueOf(Mesibo.getDeviceType()));
            } catch (Exception e) {

            }
            int nwtype = Mesibo.getNetworkConnectivity();
            if (nwtype == 0xFF) {

            }

            http = new MesiboHttp();
            http.url = mApiUrl;
            try {
                http.post = j.toString().getBytes();
            } catch (Exception e) {
            }

            http.contentType = "application/json";
            http.uploadFile = filePath;
            http.uploadFileField = formFieldName;
            http.notifyOnCompleteOnly = true;
            http.concatData = true;
            http.listener = this;
            if (mBlocking)
                return http.executeAndWait();
            return http.execute();
        }

        public Context getContext() {
            return this.mContext;
        }

        public void setContext(Context context) {
            this.mContext = context;
        }

        public abstract void HandleAPIResponse(Response response);
    }

//    public static void createContact(String name, String phone,  long groupid, String status, String members, String photo, String tnBasee64, long ts, long when, boolean selfProfile, boolean refresh, int visibility) {
//        Mesibo.UserProfile u = new Mesibo.UserProfile();
//        u.address = phone;
//        u.groupid = groupid;
//
//        if(!selfProfile && 0 == u.groupid)
//            u.name = phoneBookLookup(phone);
//
//        if(TextUtils.isEmpty(u.name))
//            u.name = name;
//
//        if(TextUtils.isEmpty(u.name)) {
//            u.name = phone;
//            if(TextUtils.isEmpty(u.name))
//                u.name = "Group-" + groupid;
//        }
//
//        if(groupid == 0 && !TextUtils.isEmpty(phone) && phone.equalsIgnoreCase("0")) {
//            u.name = "hello";
//            return;
//        }
//
//        u.status = status; // Base64.decode(c[i].status, Base64.DEFAULT).toString();
//
//        if(groupid > 0) {
//           u.groupMembers = members;
//            String p = getPhone();
//            if(null == p) return;
//            //if members empty or doesn't contain myself, it means I am not a member or group deleted
//            if(!members.contains(getPhone())) {
//                updateDeletedGroup(groupid);
//                return;
//            }
//            u.status = groupStatusFromMembers(members);
//        }
//
//        if(null == u.status) {
//            u.status = "";
//        }
//
//        u.picturePath = photo;
//        u.timestamp = ts;
//        if(ts > 0 && u.timestamp > mContactTs)
//            mContactTs = u.timestamp;
//
//        if(when >= 0) {
//            u.lastActiveTime = Mesibo.getTimestamp() - (when*1000);
//        }
//
//        if(!TextUtils.isEmpty(tnBasee64)) {
//            byte[] tn = null;
//            try {
//                tn = Base64.decode(tnBasee64, Base64.DEFAULT);
//
//                if(Mesibo.createFile(Mesibo.getFilePath(Mesibo.FileInfo.TYPE_PROFILETHUMBNAIL), photo, tn, true)) {
//                    //u.tnPath = photo;
//                }
//            } catch (Exception e) {}
//        }
//
//        if(visibility == VISIBILITY_HIDE)
//            u.flag |= Mesibo.UserProfile.FLAG_HIDDEN;
//        else if(visibility == VISIBILITY_UNCHANGED) {
//            Mesibo.UserProfile tp = Mesibo.getUserProfile(phone, groupid);
//            if(null != tp && (tp.flag&Mesibo.UserProfile.FLAG_HIDDEN) >0)
//                u.flag |= Mesibo.UserProfile.FLAG_HIDDEN;
//        }
//
//        if(selfProfile) {
//            AppConfig.getConfig().phone = u.address;
//            Mesibo.setSelfProfile(u);
//        }
//        else
//            Mesibo.setUserProfile(u, refresh);
//    }

    public static class Urls {
        public String upload = "";
        public String download = "";
    }

    public static class Invite {
        public String text = "";
        public String subject = "";
        public String title = "";
    }

    public static class Contacts {
        public String name = "";
        public String phone = "";
        public long gid = 0;
        public long ts = 0;
        public String status = "";
        public String photo = "";
        public String tn = "";
        public String members = "";
    }

    public static class Response {
        public String result;
        public String op;
        public String error;
        public String errmsg;
        public String errtitle;
        public String token;
        public Contacts[] contacts;
        public String name;
        public String status;
        public String members;
        public String photo;
        public String phone;
        public String cc;

        public Urls urls;
        public Invite share;

        public long gid;
        public int type;
        public int profile;
        public long ts = 0;
        public String tn = null;

        Response() {
            result = null;
            op = null;
            error = null;
            errmsg = null;
            errtitle = null;
            token = null;
            contacts = null;
            gid = 0;
            type = 0;
            profile = 0;
            urls = null;
        }
    }

//    public static boolean setProfilePicture(String filePath, long groupid, ResponseHandler handler) {
//        Bundle b = createPostBundle(BUNDLE_UPLOAD);
//        if(null == b) return false;
//        b.putLong("mid", 0);
//        b.putInt("profile", 1);
//        b.putLong("gid", groupid);
//
//        handler.setOnUiThread(true);
//
//        if(TextUtils.isEmpty(filePath)) {
//            b.putInt("delete", 1);
//            handler.sendRequest(b, null, null);
//            return true;
//        }
//
//        handler.sendRequest(b, filePath, "photo");
//        return true;
//    }

//    // groupid is 0 for new group else pass actual value to add/remove members
//    public static boolean setGroup(long groupid, String name, String status, String photoPath, String[] members, ResponseHandler handler) {
//        Bundle b = createPostBundle(BUNDLE_SETGROUP);
//        if(null == b) return false;
//
//        b.putString("name", name);
//        b.putLong("gid", groupid);
//        b.putString("status", status);
//        if(null != members)
//            b.putString("m", array2String(members));
//
//        handler.setOnUiThread(true);
//        handler.sendRequest(b, photoPath, "photo");
//        return true;
//    }

//    public static boolean deleteGroup(long groupid, ResponseHandler handler) {
//        Bundle b = createPostBundle(BUNDLE_DELGROUP);
//        if(null == b) return false;
//
//        b.putLong("gid", groupid);
//        handler.setOnUiThread(true);
//        handler.sendRequest(b, null, null);
//
//        return true;
//    }

//    public static boolean getGroup(long groupid, ResponseHandler handler) {
//        if(0 == groupid)
//            return false;
//
//        Bundle b = createPostBundle(BUNDLE_GETGROUP);
//        if(null == b) return false;
//
//        b.putLong("gid", groupid);
//        handler.setOnUiThread(true);
//        handler.sendRequest(b, null, null);
//        return true;
//    }

//    public static ArrayList<Mesibo.UserProfile> getGroupMembers(String members) {
//        if (TextUtils.isEmpty(members))
//            return null;
//
//        String[] s = members.split("\\:");
//        if (null == s || s.length < 2)
//            return null;
//
//        String[] users = s[1].split("\\,");
//        if (null == users)
//            return null;
//
//        ArrayList<Mesibo.UserProfile> profiles = new ArrayList<Mesibo.UserProfile>();
//
//        String status = "";
//        for (int i = 0; i < users.length; i++) {
//
//            //TBD, check about self profile
//            Mesibo.UserProfile u = Mesibo.getUserProfile(users[i], 0);
//            if(null == u) {
//                u = Mesibo.createUserProfile(users[i], 0, users[i]);
//            }
//
//            profiles.add(u);
//        }
//        return profiles;
//    }

//    public static String groupStatusFromMembers(String members) {
//        if (TextUtils.isEmpty(members))
//            return null;
//
//        String[] s = members.split("\\:");
//        if (null == s || s.length < 2)
//            return null;
//
//        String[] users = s[1].split("\\,");
//        if (null == users)
//            return "";
//
//        String status = "";
//        for (int i = 0; i < users.length; i++) {
//            if (!TextUtils.isEmpty(status))
//                status += ", ";
//
//            if (getPhone().equalsIgnoreCase(users[i])) {
//                status += "You";
//            } else {
//                Mesibo.UserProfile u = Mesibo.getUserProfile(users[i], 0);
//
//                //TBD, use only the first name
//                if (u != null)
//                    status += u.name;
//                else
//                    status += users[i];
//            }
//
//            if (status.length() > 32)
//                break;
//        }
//        return status;
//    }

//    public static void updateDeletedGroup(long gid) {
//        if(0 == gid) return;
//        Mesibo.UserProfile u = Mesibo.getUserProfile(gid);
//        if(null == u) return;
//        u.flag |= Mesibo.UserProfile.FLAG_DELETED;
//        u.status = "Not a group member"; // can be better handle dynamically
//        Mesibo.setUserProfile(u, false);
//    }

//    public static boolean editMembers(long groupid, String[] members, boolean remove, ResponseHandler handler) {
//        if(0 == groupid || null == members)
//            return false;
//
//        Bundle b = createPostBundle(BUNDLE_EDITMEMBER);
//        if(null == b) return false;
//
//        b.putLong("gid", groupid);
//        b.putString("m", array2String(members));
//        b.putInt("delete", remove?1:0);
//
//        handler.setOnUiThread(true);
//        handler.sendRequest(b, null, null);
//        return true;
//    }

//    public static boolean setAdmin(long groupid, String member, boolean admin, ResponseHandler handler) {
//        if(0 == groupid || TextUtils.isEmpty(member))
//            return false;
//
//        Bundle b = createPostBundle(BUNDLE_SETADMIN);
//        if(null == b) return false;
//
//        b.putLong("gid", groupid);
//        b.putString("m", member);
//        b.putInt("admin", admin?1:0);
//
//        handler.setOnUiThread(true);
//        handler.sendRequest(b, null, null);
//        return true;
//    }

//    public static boolean setProfile(String name, String status, long groupid, ResponseHandler handler) {
//        Bundle b = createPostBundle(BUNDLE_PROFILE);
//        if(null == b) return false;
//
//        b.putString("name", name);
//        b.putString("status", status);
//        b.putLong("gid", groupid);
//
//        handler.setOnUiThread(true);
//        handler.sendRequest(b, null, null);
//
//        return true;
//    }
}

