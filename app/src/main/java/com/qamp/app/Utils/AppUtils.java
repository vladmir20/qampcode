/** Copyright (c) 2021 Mesibo
 * https://mesibo.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the terms and condition mentioned on https://mesibo.com
 * as well as following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions, the following disclaimer and links to documentation and source code
 * repository.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 *
 * Neither the name of Mesibo nor the names of its contributors may be used to endorse
 * or promote products derived from this software without specific prior written
 * permission.
 *
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Documentation
 * https://mesibo.com/documentation/
 *
 * Source Code Repository
 * https://github.com/mesibo/messenger-app-android
 *
 */
package com.qamp.app.Utils;

import static android.content.Context.MODE_PRIVATE;
import static com.qamp.app.messaging.MesiboConfiguration.TOOLBAR_COLOR;
import static com.qamp.app.messaging.MesiboConfiguration.TOOLBAR_STATUSBAR_COLOR;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.qamp.app.QampUiHelper;
import com.qamp.app.R;
import com.qamp.app.SelectLanguage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Map;

public class AppUtils {

    static private ProgressDialog progressDialog;
    public static void setLanguage(String language, Activity activity) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        android.content.res.Configuration configuration = new android.content.res.Configuration();
        configuration.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(configuration, activity.getBaseContext()
                .getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = activity.getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("app_lang", language);
        editor.apply();

        if (activity.getClass().equals(SelectLanguage.class)) {
            QampUiHelper.launchEditProfileNew(activity, 0, 0, false);
            activity.finishAffinity();
        }
    }

    public static boolean aquireUserPermission(Context context, final String permission, int REQUEST_CODE) {
        if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity)context,
                    permission)) {

            } else {
                ActivityCompat.requestPermissions((AppCompatActivity)context,
                        new String[]{permission},
                        REQUEST_CODE);
            }

            return false;
        }

        return true;

    }

    public static void setActivityStyle(AppCompatActivity context, Toolbar toolbar) {
        if (null != toolbar) {
            if (TOOLBAR_COLOR != 0) {
//                toolbar.setBackgroundColor(TOOLBAR_COLOR);
            }
        }

        Window window1 = context.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window1.setStatusBarColor(ContextCompat.getColor(context, R.color.grey_3));
        }
        View decor = window1.getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (TOOLBAR_STATUSBAR_COLOR != 0) {
                Window window = context.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        }
    }

    public static String saveToInternalStorage(Context applicationContext, Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(applicationContext);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
    public static String getFormatedNumber(String fullNumberWithPlus) {
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            String fullNumber = "+" + fullNumberWithPlus;
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(fullNumber, "");
            String selectedCountryCode = String.valueOf(numberProto.getCountryCode());
            String phoneNumber = String.valueOf(numberProto.getNationalNumber());

            return "+" + selectedCountryCode + "-" + phoneNumber;
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e);
        }

        return "";
    }

    @SuppressLint("ResourceType")
    public static void openProgressDialog(Activity activity) {
        closeProgresDialog();
        progressDialog = new ProgressDialog(activity, R.style.DialogTheme1);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.activity_indicator);
//        ImageView logo = progressDialog.findViewById(R.id.iv_logo);
//        logo.startAnimation(AnimationUtils.loadAnimation(activity, R.drawable.rotate));
    }

    public static boolean isNetWorkAvailable(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            if (connMgr == null) {
                return false;
            } else return connMgr.getActiveNetworkInfo() != null
                    && connMgr.getActiveNetworkInfo().isAvailable()
                    && connMgr.getActiveNetworkInfo().isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    public static void closeProgresDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

        } catch (Exception ex) {
            Log.e("error", ex.getMessage());
        }
    }

    public static void showAlert(Context context, String title, String mesage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(mesage);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public static void setLocalLanguage(String language, Activity activity) {
        if (language.contains("hi"))
            language = "hi";
        else if (language.contains("en"))
            language = "en";
        else if (language.contains("gu"))
            language = "gu";
        else if (language.contains("pa"))
            language = "pa";
        else if (language.contains("ur"))
            language = "ur";

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        android.content.res.Configuration configuration = new android.content.res.Configuration();
        configuration.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(configuration, activity.getBaseContext()
                .getResources().getDisplayMetrics());

    }

    public static void loadLocale(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences("Settings", MODE_PRIVATE);
        String language = preferences.getString("app_lang", "");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        android.content.res.Configuration configuration = new android.content.res.Configuration();
        configuration.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(configuration, activity.getBaseContext()
                .getResources().getDisplayMetrics());

    }

    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(color));
        }
    }


    public static byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }
}
