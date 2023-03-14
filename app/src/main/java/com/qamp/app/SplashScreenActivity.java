package com.qamp.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.qamp.app.Utils.AppUtils;
import com.qamp.app.messaging.MesiboUserListActivityNew;
import com.qamp.app.messaging.Utils;

import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;
    String s1;
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
    }

    private void navigateToNextActivity() {
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
                                    OnBoardingUserProfile.class);
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
