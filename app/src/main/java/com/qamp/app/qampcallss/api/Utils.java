/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 3:15 PM
 *
 */

package com.qamp.app.qampcallss.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mesibo.api.Mesibo;

import java.util.ArrayList;

public class Utils {
    private static Toast logToast = null;

    public static class PowerAndWifiLock {
        PowerManager.WakeLock wakeLock = null;
        WifiManager.WifiLock wifiLock = null;
    }

    public static void alert(Context context, CharSequence charSequence, String str, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context).setTitle(charSequence).setMessage(str).setCancelable(false).setNeutralButton("OK", onClickListener).create().show();
    }

    public static void assertIsTrue(boolean z) {
        if (!z) {
            throw new AssertionError("Expected condition to be true");
        }
    }

    public static int checkPermissions(int i, Activity activity, String[] strArr, boolean z) {
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < strArr.length; i2++) {
            if (ContextCompat.checkSelfPermission(activity, strArr[i2]) != 0) {
                if (z && !ActivityCompat.shouldShowRequestPermissionRationale(activity, strArr[i2])) {
                    return -1;
                }
                arrayList.add(strArr[i2]);
            }
        }
        if (arrayList.size() > 0) {
            ActivityCompat.requestPermissions(activity, (String[]) arrayList.toArray(new String[arrayList.size()]), i);
        }
        return arrayList.size();
    }

    @SuppressLint("InvalidWakeLockTag")
    public static PowerAndWifiLock createPowerAndWifiLock(Context context) {
        PowerAndWifiLock powerAndWifiLock = new PowerAndWifiLock();
        powerAndWifiLock.wifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).createWifiLock(3, "MyWifiLock");
        powerAndWifiLock.wifiLock.acquire();
        powerAndWifiLock.wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(1, "MesiboWakeLock");
        powerAndWifiLock.wakeLock.acquire();
        return powerAndWifiLock;
    }

    protected static boolean enableSecureScreen(AppCompatActivity appCompatActivity) {
//        if (!Mesibo.isSetSecureScreen()) {
//            return false;
//        }
//        appCompatActivity.getWindow().setFlags(8192, 8192);
        return true;
    }

    public static String getThreadInfo() {
        return "@[name=" + Thread.currentThread().getName() + ", id=" + Thread.currentThread().getId() + "]";
    }

    public static boolean hasStringChanged(String str, String str2) {
        if (str == null && str2 != null) {
            return true;
        }
        if (str2 == null && str != null) {
            return true;
        }
        if (str == null && str2 == null) {
            return false;
        }
        return str.compareToIgnoreCase(str2) == 0;
    }

    public static void logAndToast(Context context, String str, String str2) {
        if (logToast != null) {
            logToast.cancel();
        }
        Toast makeText = Toast.makeText(context, str2, Toast.LENGTH_SHORT);
        logToast = makeText;
        makeText.show();
    }

    public static void logDeviceInfo(String str) {
        new StringBuilder("Android SDK: ").append(Build.VERSION.SDK_INT).append(", Release: ").append(Build.VERSION.RELEASE).append(", Brand: ").append(Build.BRAND).append(", Device: ").append(Build.DEVICE).append(", Id: ").append(Build.ID).append(", Hardware: ").append(Build.HARDWARE).append(", Manufacturer: ").append(Build.MANUFACTURER).append(", Model: ").append(Build.MODEL).append(", Product: ").append(Build.PRODUCT);
    }

    public static void releasePowerAndWifiLock(PowerAndWifiLock powerAndWifiLock) {
        if (powerAndWifiLock != null) {
            if (powerAndWifiLock.wakeLock != null && powerAndWifiLock.wakeLock.isHeld()) {
                powerAndWifiLock.wakeLock.release();
            }
            if (powerAndWifiLock.wifiLock != null && powerAndWifiLock.wifiLock.isHeld()) {
                powerAndWifiLock.wifiLock.release();
            }
        }
    }

    public static void setActivityStyle(AppCompatActivity appCompatActivity, Toolbar toolbar, int i, int i2) {
        if (!(toolbar == null || i == 0)) {
            toolbar.setBackgroundColor(i);
        }
        if (Build.VERSION.SDK_INT >= 21 && i2 != 0) {
            Window window = appCompatActivity.getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(i2);
        }
    }

    @SuppressLint("ResourceType")
    public static void showAlert(Context context, String str, String str2) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(str);
            builder.setMessage(str2);
            builder.setCancelable(true);
            builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
            builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
            try {
                builder.show();
            } catch (Exception e) {
            }
        }
    }
}
