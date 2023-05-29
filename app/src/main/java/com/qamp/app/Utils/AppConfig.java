/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:39 AM
 *
 */

package com.qamp.app.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.qamp.app.MesiboApiClasses.SampleAPI;

public class AppConfig {
    public static final String sharedPrefKey = "com.qampmessenger.app";
    //public static final StXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxring sharedPrefKey = "com.qamp.app";
    private static final String TAG = "AppSettings";
    private static final String systemPreferenceKey = "mesibo-app-settings";
    //System Specific Preferences - does not change across logins
    public static Configuration mConfig = new Configuration();
    private static AppConfig _instance = null;
    SharedPreferences mSharedPref = null;
    private boolean firstTime = false;
    private Context mContext;


    public AppConfig(Context c) {
        _instance = this;
        mContext = c;

        mSharedPref = c.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE);
        firstTime = false;
        if (!mSharedPref.contains(systemPreferenceKey)) {
            firstTime = true;
        }

        getAppSetting();

        if (firstTime)
            saveSettings();
    }

    public static AppConfig getInstance() {
        return _instance;
    }

    public static Configuration getConfig() {
        return mConfig;
    }

    public static void reset() {
        mConfig.reset();
        save();
        getInstance().backup();
    }

    public static void save() {
        getInstance().saveSettings();
    }

    private void backup() {
    }

    public Boolean isFirstTime() {
        return firstTime;
    }

    // We could use TAG also - to save/retrieve settings
    public void getAppSetting() {
        Gson gson = new Gson();
        String json = mSharedPref.getString(systemPreferenceKey, "");
        mConfig = gson.fromJson(json, Configuration.class);

        if (null == mConfig)
            mConfig = new Configuration();
    }

    private void putAppSetting(SharedPreferences.Editor spe) {
        Gson gson = new Gson();
        String json = gson.toJson(mConfig);
        spe.putString(systemPreferenceKey, json);
        spe.commit();
    }

    public boolean saveSettings() {
        Log.d(TAG, "Updating RMS .. ");
        try {
            synchronized (mSharedPref) {
                SharedPreferences.Editor spe = mSharedPref.edit();
                putAppSetting(spe);
                backup();
                return true;
            }
        } catch (Exception e) {
            Log.d(TAG, "Unable to updateRMS(): " + e.getMessage());
            return false;
        }

    }

    public boolean setStringValue(String key, String value) {
        try {
            synchronized (mSharedPref) {
                SharedPreferences.Editor poEditor = mSharedPref.edit();
                poEditor.putString(key, value);
                poEditor.commit();
                return true;
            }
        } catch (Exception e) {
            Log.d(TAG, "Unable to set long value in RMS:" + e.getMessage());
            return false;
        }
    }

    public String getStringValue(String key, String defaultVal) {
        try {
            synchronized (mSharedPref) {
                if (mSharedPref.contains(key))
                    return mSharedPref.getString(key, defaultVal);
                return defaultVal;
            }
        } catch (Exception e) {
            Log.d(TAG, "Unable to fet long value in RMS:" + e.getMessage());
            return defaultVal;
        }
    }

    public boolean setLongValue(String key, long value) {
        try {
            synchronized (mSharedPref) {
                SharedPreferences.Editor poEditor = mSharedPref.edit();
                poEditor.putLong(key, value);
                poEditor.commit();
                return true;
            }
        } catch (Exception e) {
            Log.d(TAG, "Unable to set long value in RMS:" + e.getMessage());
            return false;
        }
    }

    public long getLongValue(String key, long defaultVal) {
        try {
            synchronized (mSharedPref) {
                if (mSharedPref.contains(key))
                    return mSharedPref.getLong(key, defaultVal);
                return defaultVal;
            }
        } catch (Exception e) {
            Log.d(TAG, "Unable to fet long value in RMS:" + e.getMessage());
            return defaultVal;
        }
    }

    public boolean setIntValue(String key, int value) {
        try {
            synchronized (mSharedPref) {
                SharedPreferences.Editor poEditor = mSharedPref.edit();
                poEditor.putInt(key, value);
                poEditor.commit();
                return true;
            }
        } catch (Exception e) {
            Log.d(TAG, "Unable to set int value in RMS:" + e.getMessage());
            return false;
        }
    }

    public int getIntValue(String key, int defaultVal) {

        try {
            synchronized (mSharedPref) {
                if (mSharedPref.contains(key))
                    return mSharedPref.getInt(key, defaultVal);
                return defaultVal;
            }
        } catch (Exception e) {
            Log.d(TAG, "Unable to get int value in RMS:" + e.getMessage());
            return defaultVal;
        }
    }

    public boolean setBooleanValue(String key, Boolean value) {
        try {
            synchronized (mSharedPref) {
                SharedPreferences.Editor poEditor = mSharedPref.edit();
                poEditor.putBoolean(key, value);
                poEditor.commit();
                return true;
            }
        } catch (Exception e) {
            Log.d(TAG, "Unable to set long value in RMS:" + e.getMessage());
            return false;
        }
    }

    public static class Configuration {
        public String token = "";
        public String phone = "";
        public String countryCode = "";
        public String name = "";
        public String status = "";
        public String cc = "";
        public String photo = "";
        public String tnm = "";
        public String uid = "";
        public long ts = 0;
        public int version = 0;
        public String profileId = "";
        public SampleAPI.Invite invite = null;
        public String uploadurl = null;
        public String downloadurl = null;

        public String deviceToken = "";

        public void reset() {
            token = "";
            phone = "";
            invite = null;
            uploadurl = "";
            downloadurl = "";
        }
    }


};

