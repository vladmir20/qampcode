/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:32 PM
 *
 */

package com.qamp.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleObserver;

import com.mesibo.api.Mesibo;
import com.qamp.app.qampcallss.api.MesiboCall;
//import com.mesibo.calls.ui.MesiboCallUi;
import com.mesibo.mediapicker.ImagePicker;
import com.mesibo.mediapicker.MediaPicker;
import com.qamp.app.messaging.MesiboUI;

public class MainApplication extends Application implements Mesibo.RestartListener, LifecycleObserver {
    public static final String TAG = "MesiboDemoApplication";
    private static Context mContext = null;
    //private static MesiboCallUi mCallUi = null;
    private static AppConfig mConfig = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Mesibo.setRestartListener(this);
        mConfig = new AppConfig(this);
        SampleAPI.init(getApplicationContext());

        //mCallUi = MesiboCallUi.getInstance();
        MesiboCall.getInstance().init(mContext);

        MesiboUI.Config opt = MesiboUI.getConfig();
        opt.mToolbarColor = 0xff00868b;
        opt.emptyUserListMessage = "No messages! Click on the message icon above to start messaging!";
        MediaPicker.setToolbarColor(opt.mToolbarColor);
        ImagePicker.getInstance().setApp(this);

    }

    public static String getRestartIntent() {
        return "com.mesibo.sampleapp.restart";
    }

    public static Context getAppContext() {
        return mContext;
    }

    @Override
    public void Mesibo_onRestart() {
        Log.d(TAG, "OnRestart");
        StartUpActivity.newInstance(this, true);
    }

}

