/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.Activity;

import static com.qamp.app.MessagingModule.MesiboConfiguration.MESIBO_INTITIAL_READ_USERLIST;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.qamp.app.Utils.AppConfig;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.MessagingModule.MesiboUserListActivityNew;
import com.qamp.app.Utils.ContantContantUtil;

import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;
    String s1 = "";
    String defaultSystemLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //       getSupportActionBar().hide();
        AppUtils.setStatusBarColor(SplashScreenActivity.this, R.color.colorAccent);
        setContentView(R.layout.activity_splash_screen);
        SharedPreferences sh = getSharedPreferences("checkactivity", MODE_PRIVATE);
        s1 = sh.getString("checker", "");
        defaultSystemLanguage = Locale.getDefault().toString();
        SharedPreferences lang = getSharedPreferences("Settings", MODE_PRIVATE);
        String s2 = lang.getString("app_lang", "");
        if (s2.isEmpty()) {
            if (defaultSystemLanguage.contains("en") || defaultSystemLanguage.contains("gu") || defaultSystemLanguage.contains("hi") ||
                    defaultSystemLanguage.contains("pa") || defaultSystemLanguage.contains("ur")) {
                AppUtils.setLocalLanguage(defaultSystemLanguage, SplashScreenActivity.this);
            }
        } else {
            AppUtils.loadLocale(SplashScreenActivity.this);
        }
        navigateToNextActivity();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ContantContantUtil.showUserList(MESIBO_INTITIAL_READ_USERLIST,SplashScreenActivity.this, SplashScreenActivity.this);
            }

        });


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ContantContantUtil.showUserList(MESIBO_INTITIAL_READ_USERLIST,SplashScreenActivity.this, SplashScreenActivity.this);
            }
        };
        new Thread(runnable).start();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            switch (requestCode) {
            case 225: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    // startContactsSync();
                    ContantContantUtil.showUserList(MESIBO_INTITIAL_READ_USERLIST,SplashScreenActivity.this, SplashScreenActivity.this);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                return;
            }
        }

    }

    private void navigateToNextActivity() {
        NotificationsTest.getFirebaseDeviceToken();
        boolean isLoggedIn = !AppConfig.getConfig().token.isEmpty();

        if (isLoggedIn) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(AppConfig.getConfig().token)) {
                        if (s1.equals("1")) {
                            Intent mainActivity = new Intent(SplashScreenActivity.this,
                                    MesiboUserListActivityNew.class);
                            startActivity(mainActivity);
                        } else {
                            Intent mainActivity = new Intent(SplashScreenActivity.this,
                                    OnBoardingScreens.class);
                            startActivity(mainActivity);
                        }
                        SplashScreenActivity.this.finish();
                    }
                }
            }, SPLASH_DISPLAY_LENGTH);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    /* Create an Intent that will start the Login-Activity. */
                    Intent mainIntent = new Intent(SplashScreenActivity.this, LoginQampActivity.class);
                    SplashScreenActivity.this.startActivity(mainIntent);
                    SplashScreenActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
