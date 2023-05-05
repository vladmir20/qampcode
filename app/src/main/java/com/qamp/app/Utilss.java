/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:32 PM
 *
 */

package com.qamp.app;

import static android.content.Context.MODE_PRIVATE;
import static com.qamp.app.messaging.LetterTitleProvider.SAN_SERIF_LIGHT;
import static com.qamp.app.messaging.MesiboConfiguration.GOOGLE_PLAYSERVICE_STRING;
import static com.qamp.app.messaging.MesiboConfiguration.TOOLBAR_COLOR;
import static com.qamp.app.messaging.MesiboConfiguration.TOOLBAR_STATUSBAR_COLOR;
import static com.qamp.app.messaging.MesiboConfiguration.TOOLBAR_TEXT_COLOR;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.qamp.app.Utils.AppUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Bhushan Udawant on 15/03/21.
 */
public final class Utilss {

    private static Toast logToast = null;


    public Utilss() {
    }

    public static void setActivityStyle(AppCompatActivity context, Toolbar toolbar) {
        if (null != toolbar) {
            if (TOOLBAR_COLOR != 0) {
//                toolbar.setBackgroundColor(TOOLBAR_COLOR);
            }
        }

        Window window1 = context.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window1.setStatusBarColor(ContextCompat.getColor(context, R.color.huddle_grey));
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

    public static void createRoundDrawable(Context context, View view, int color, float radiusInDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        float radiusInPx = radiusInDp * 8; //some random approx if context is null

        if (null != context)
            radiusInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radiusInDp, context.getResources().getDisplayMetrics());

        drawable.setCornerRadius(radiusInPx);

        view.setBackground(drawable);
    }

    public static void setTitleAndColor(ActionBar actionBar, String title) {
        SpannableString s = new SpannableString(title);
        if (TOOLBAR_TEXT_COLOR != 0) {
            s.setSpan(new ForegroundColorSpan(TOOLBAR_TEXT_COLOR), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (null != title)
            actionBar.setTitle(s);
    }

    public static void setTextViewColor(TextView textView, int color) {
        if (color != 0) {
            textView.setTextColor(color);
        }
    }

    public static void setLanguage(Activity activity) {
        String defaultSystemLanguage = Locale.getDefault().toString();
        SharedPreferences lang = activity.getSharedPreferences("Settings", MODE_PRIVATE);
        String s2 = lang.getString("app_lang", "");
        if (s2.isEmpty()) {
            if (defaultSystemLanguage.contains("en") || defaultSystemLanguage.contains("gu") || defaultSystemLanguage.contains("hi") ||
                    defaultSystemLanguage.contains("pa") || defaultSystemLanguage.contains("ur")) {
                AppUtils.setLocalLanguage(defaultSystemLanguage, activity);
            }
        } else {
            AppUtils.loadLocale(activity);
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

    public static String getFileSizeString(long fileSize) {
        String unit = "KB";
        if (fileSize > 1024 * 1024) {
            unit = "MB";
            fileSize /= 1024 * 1024;
        } else {
            fileSize /= 1024;
        }

        if (fileSize < 1)
            fileSize = 1;

        return String.valueOf(fileSize) + unit;
    }

    public static boolean saveBitmpToFilePath(Bitmap bmp, String filePath) {
        File file = new File(filePath);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        if (null != bmp) {

            bmp.compress(Bitmap.CompressFormat.PNG, 70, fOut);
            try {
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
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

    public static boolean aquireUserPermissions(Context context, List<String> permissions, int REQUEST_CODE) {
        List<String> permissionsNeeded = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {

                permissionsNeeded.add(permission);
            }
        }

        if (permissionsNeeded.isEmpty())
            return true;


        ActivityCompat.requestPermissions((AppCompatActivity) context,
                permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                REQUEST_CODE);

        return false;
    }

    public static boolean checkPermissionGranted(Context context, String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (false) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = context.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(context, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }

        return result;
    }

    /**
     * Method to verify google play services on the device
     */
    public static boolean checkPlayServices(Activity activity, int requestCode) {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        requestCode).show();
            } else {
                Toast.makeText(activity.getApplicationContext(),
                                GOOGLE_PLAYSERVICE_STRING, Toast.LENGTH_LONG)
                        .show();
                activity.finish();
            }
            return false;
        }
        return true;
    }

    public static void setStatusBarTheme(AppCompatActivity context) {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        int currentTheme = 0;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                currentTheme = Configuration.UI_MODE_NIGHT_NO;
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                currentTheme = Configuration.UI_MODE_NIGHT_YES;
                break;
        }

        if (currentTheme == Configuration.UI_MODE_NIGHT_NO) {
            Utilss.setStatusBarStyle(false, context);
        } else {
            Utilss.setStatusBarStyle(true, context);
        }
    }

    public static void setStatusBarStyle(Boolean darkThemeStatus, AppCompatActivity context) {
        Window window1 = context.getWindow();
        View decorView = window1.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        if (darkThemeStatus) {
            // Draw light icons on a dark background color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window1.setStatusBarColor(ContextCompat.getColor(context, R.color.status_bar_color));
            }
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            // Draw dark icons on a light background color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window1.setStatusBarColor(ContextCompat.getColor(context, R.color.status_bar_color));
            }
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
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
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        return "";
    }

//    private static String getImagePathFromStorage(String path)
//    {
//
//        try {
//            File f=new File(path, "profile.jpg");
//            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//            ImageView img=(ImageView)findViewById(R.id.imgPicker);
//            img.setImageBitmap(b);
//        }
//        catch (FileNotFoundException e)
//        {
//            e.printStackTrace();
//        }
//
//    }

    public static boolean hasImage(@NonNull LinearLayout view) {
        Drawable drawable = view.getBackground();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }

        return hasImage;
    }

    public static String saveToInternalStorage(Context applicationContext, Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(applicationContext);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
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

    public static void setActivityStyle(AppCompatActivity var0, Toolbar var1, int var2, int var3) {
        if (var1 != null && var2 != 0) {
            var1.setBackgroundColor(var2);
        }

        if (Build.VERSION.SDK_INT >= 21 && var3 != 0) {
            Window var4;
            (var4 = var0.getWindow()).addFlags(-2147483648);
            var4.setStatusBarColor(var3);
        }

    }

    public static int checkPermissions(int var0, Activity var1, String[] var2, boolean var3) {
        ArrayList var4 = new ArrayList();

        for (int var5 = 0; var5 < var2.length; ++var5) {
            if (ContextCompat.checkSelfPermission(var1, var2[var5]) != 0) {
                if (var3 && !ActivityCompat.shouldShowRequestPermissionRationale(var1, var2[var5])) {
                    return -1;
                }

                var4.add(var2[var5]);
            }
        }

        if (var4.size() > 0) {
            ActivityCompat.requestPermissions(var1, (String[]) var4.toArray(new String[var4.size()]), var0);
        }

        return var4.size();
    }

    @SuppressLint("InvalidWakeLockTag")
    public static PowerAndWifiLock createPowerAndWifiLock(Context var0) {
        PowerAndWifiLock var1 = new PowerAndWifiLock();
        WifiManager var2 = (WifiManager) var0.getSystemService(Context.WIFI_SERVICE);
        var1.wifiLock = var2.createWifiLock(3, "MyWifiLock");
        var1.wifiLock.acquire();
        PowerManager var3 = (PowerManager) var0.getSystemService(Context.POWER_SERVICE);
        var1.wakeLock = var3.newWakeLock(1, "MesiboWakeLock");
        var1.wakeLock.acquire();
        return var1;
    }

    public static void releasePowerAndWifiLock(PowerAndWifiLock var0) {
        if (var0 != null) {
            if (var0.wakeLock != null && var0.wakeLock.isHeld()) {
                var0.wakeLock.release();
            }

            if (var0.wifiLock != null && var0.wifiLock.isHeld()) {
                var0.wifiLock.release();
            }

        }
    }

//    public static void showAlert(Context var0, String var1, String var2) {========
//        if (var0 != null) {
//            Builder var4;
//            (var4 = new Builder(var0)).setTitle(var1);
//            var4.setMessage(var2);
//            var4.setCancelable(true);
//            var4.setPositiveButton(17039370, (OnClickListener)null);
//            var4.setNegativeButton(17039360, (OnClickListener)null);
//
//            try {
//                var4.show();
//            } catch (Exception var3) {
//            }
//        }
//    }

    public static void alert(Context var0, CharSequence var1, String var2, View.OnClickListener var3) {
//        (new android.app.AlertDialog.Builder(var0)).setTitle(var1).setMessage(var2).setCancelable(false).setNeutralButton("OK", var3).create().show();//=============
    }

    public static void logAndToast(Context var0, String var1, String var2) {
        if (logToast != null) {
            logToast.cancel();
        }

        (logToast = Toast.makeText(var0, var2, Toast.LENGTH_SHORT)).show();
    }

    public static void assertIsTrue(boolean var0) {
        if (!var0) {
            throw new AssertionError("Expected condition to be true");
        }
    }

    public static String getThreadInfo() {
        return "@[name=" + Thread.currentThread().getName() + ", id=" + Thread.currentThread().getId() + "]";
    }

    public static void logDeviceInfo(String var0) {
        (new StringBuilder("Android SDK: ")).append(Build.VERSION.SDK_INT).append(", Release: ").append(Build.VERSION.RELEASE).append(", Brand: ").append(Build.BRAND).append(", Device: ").append(Build.DEVICE).append(", Id: ").append(Build.ID).append(", Hardware: ").append(Build.HARDWARE).append(", Manufacturer: ").append(Build.MANUFACTURER).append(", Model: ").append(Build.MODEL).append(", Product: ").append(Build.PRODUCT);
    }

    public static boolean hasStringChanged(String var0, String var1) {
        if (var0 == null && var1 != null || var1 == null && var0 != null) {
            return true;
        } else if (var0 == null && var1 == null) {
            return false;
        } else {
            return 0 == var0.compareToIgnoreCase(var1);
        }
    }

    public static class PowerAndWifiLock {
        WifiManager.WifiLock wifiLock = null;
        WakeLock wakeLock = null;

        public PowerAndWifiLock() {
        }
    }

    public static Bitmap getLetterTile(String displayName) {
        int width = 30;
        int height = 30;
        final char[] mFirstChar = new char[1];
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final TextPaint mPaint = new TextPaint();
        final Rect mBounds = new Rect();

        mPaint.setTypeface(Typeface.create(SAN_SERIF_LIGHT, Typeface.NORMAL));
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setAntiAlias(true);
        char firstChar = '*';
        final Canvas c = new Canvas();

        c.setBitmap(bmp);
        if (!TextUtils.isEmpty(displayName))
            firstChar = displayName.charAt(0);

        int color = pickColor(displayName);
        c.drawColor(color);

        mFirstChar[0] = firstChar;
        mPaint.setTextSize(16);
        mPaint.getTextBounds(mFirstChar, 0, 1, mBounds);
        c.drawText(mFirstChar, 0, 1, 0 + width / 2, 0 + height / 2
                + (mBounds.bottom - mBounds.top) / 2, mPaint);

        return bmp;
    }

    public static int pickColor(String key) {
        int[] mColors = {
                0xfff16364, 0xfff58559, 0xfff9a43e, 0xffe4c62e,
                0xff67bf74, 0xff59a2be, 0xff2093cd, 0xffad62a7
        };
        if (TextUtils.isEmpty(key))
            return 0;
        // String.hashCode() is not supposed to change across java versions, so
        // this should guarantee the same key always maps to the same color
        int color = Math.abs(key.hashCode()) % mColors.length;
        return mColors[color];
    }
}
