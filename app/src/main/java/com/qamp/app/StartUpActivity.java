/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:32 PM
 *
 */

package com.qamp.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.mesibo.contactutils.ContactUtils;
import com.qamp.app.uihelper.MesiboUiHelperConfig;

public class StartUpActivity extends AppCompatActivity {

    private static final String TAG = "MesiboStartupActivity";
    public final static String INTENTEXIT="exit";
    public final static String SKIPTOUR="skipTour";
    public final static String STARTINBACKGROUND ="startinbackground";
    private boolean mRunInBackground = false;
    private boolean mPermissionAlert = false;

    public static void newInstance(Context context, boolean startInBackground) {
        Intent i = new Intent(context, StartUpActivity.class);  //MyActivity can be anything which you want to start on bootup...
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra(StartUpActivity.STARTINBACKGROUND, startInBackground);

        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().getBooleanExtra(INTENTEXIT, false)) {
            Log.d(TAG, "onCreate closing");
            finish();
            return;
        }



        mRunInBackground = getIntent().getBooleanExtra(STARTINBACKGROUND, false);
        if(mRunInBackground) {
            Log.e(TAG, "Moving app to background");
            moveTaskToBack(true);
        } else {
            Log.e(TAG, "Not Moving app to background");
        }


        setContentView(R.layout.activity_blank_launcher);
        startNextActivity();
    }

    void startNextActivity() {

        if(TextUtils.isEmpty(SampleAPI.getToken())) {
            MesiboUiHelperConfig.mDefaultCountry = ContactUtils.getCountryCode();
            MesiboUiHelperConfig.mPhoneVerificationBottomText = "Note, Mesibo may call instead of sending an SMS if SMS delivery to your phone fails.";
            if(null == MesiboUiHelperConfig.mDefaultCountry) {
                MesiboUiHelperConfig.mDefaultCountry = "91";
            }

            if(getIntent().getBooleanExtra(SKIPTOUR, false)) {
                UIManager.launchLogin(this, MesiboListeners.getInstance());
            } else {
              //  UIManager.launchLogin(this, MesiboListeners.getInstance());

                UIManager.launchWelcomeactivity(this, true, MesiboListeners.getInstance(), MesiboListeners.getInstance());
        }

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } else {
            UIManager.launchMesibo(this, 0, mRunInBackground, true);
        }

        finish();
    }

    // since this activity is singleTask, intent will be delivered here if it's running
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");
        if(intent.getBooleanExtra(INTENTEXIT, false)) {
            finish();
        }

        super.onNewIntent(intent);
    }
}
