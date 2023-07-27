

package com.qamp.app.Activity;

import static com.qamp.app.MessagingModule.MesiboConfiguration.MESIBO_INTITIAL_READ_USERLIST;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mesibo.api.Mesibo;
import com.qamp.app.Utils.AppConfig;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.MessagingModule.MesiboUserListActivityNew;
import com.qamp.app.Utils.ContactSyncClass;
import com.qamp.app.Utils.ContantContantUtil;
import com.qamp.app.Utils.Log;

import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;
    private static final int REQUEST_READ_CONTACTS = 101;

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
        Log.e("androidToken",AppConfig.getConfig().deviceToken);
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
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        } else {
            //Permission has already been granted
            ContactSyncClass.getContactData(SplashScreenActivity.this);
        }
        navigateToNextActivity();
        /**AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ContantContantUtil.showUserList(MESIBO_INTITIAL_READ_USERLIST,SplashScreenActivity.this, SplashScreenActivity.this);
            }
        });*/

       // System.out.println(Mesibo.getSortedUserProfiles());

//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                ContantContantUtil.showUserList(MESIBO_INTITIAL_READ_USERLIST,SplashScreenActivity.this, SplashScreenActivity.this);
//            }
//        };
//        new Thread(runnable).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch contacts
                ContactSyncClass.getContactData(SplashScreenActivity.this);
            } else {
                // Permission denied, show a message or handle it gracefully
                Toast.makeText(this, "Read contacts permission denied", Toast.LENGTH_SHORT).show();
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
