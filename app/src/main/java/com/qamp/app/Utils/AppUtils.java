/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:39 AM
 *
 */
package com.qamp.app.Utils;

import static android.content.Context.MODE_PRIVATE;
import static com.qamp.app.MessagingModule.MesiboConfiguration.TOOLBAR_COLOR;
import static com.qamp.app.MessagingModule.MesiboConfiguration.TOOLBAR_STATUSBAR_COLOR;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.qamp.app.R;
import com.qamp.app.Activity.SelectLanguage;

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

    public static void saveUserApiVersion(Activity activity, String userVersion) {
        //Saving the user version in shared preferences
        SharedPreferences sharedPreferences = activity.getSharedPreferences("MyVersion", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("userVersion", userVersion);
        myEdit.commit();
    }
    public static String getUserApiVersion(Activity activity) {
        //Returning the user version from shared preferences
        SharedPreferences sh = activity.getSharedPreferences("MyVersion", MODE_PRIVATE);
        String s1 = sh.getString("userVersion", "");
        return s1;
    }


    public static boolean aquireUserPermission(Context context, final String permission, int REQUEST_CODE) {
        if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) context,
                    permission)) {

            } else {
                ActivityCompat.requestPermissions((AppCompatActivity) context,
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

    public static void under_development_message(Activity activity){
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.under_development,
                (ViewGroup) activity.findViewById(R.id.toast_layout_root));

//        ImageView image = (ImageView) layout.findViewById(R.id.image);
//        image.setImageResource(R.drawable.loader);
//        TextView text = (TextView) layout.findViewById(R.id.text);
//        text.setText("Hello! This is a custom toast!");

        Toast toast = new Toast(activity.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static byte[] encodeParameter(Map<String, Object> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode((String) entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }
}
