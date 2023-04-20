/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:31 PM
 *
 */

package com.qamp.app.qampcallss.api;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

public class DeviceUtils {
    private static final String BASEBAND_VERSION = "gsm.version.baseband";
    public static final String TAG = "DeviceUtils";

    public static boolean BluetoothStatus(Context context) {
        Set<BluetoothDevice> bondedDevices;
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter(context);
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return false;
        }
        new StringBuilder("Device has Bluetooth Support/Feature: ").append(bluetoothAdapter != null);
        int state = bluetoothAdapter.getState();
        new StringBuilder("Bluetooth Active: ").append(12 == state);
        new StringBuilder("Bluetooth in process on Activating: ").append(11 == state);
        if (12 != state || (bondedDevices = bluetoothAdapter.getBondedDevices()) == null) {
            return false;
        }
        new StringBuilder("Number of Devices: ").append(bondedDevices.size());
        int i = 0;
        for (BluetoothDevice next : bondedDevices) {
            if (12 == next.getBondState()) {
                BluetoothClass bluetoothClass = next.getBluetoothClass();
                new StringBuilder("BluetoothDevice[").append(i).append("]: ").append(next.getName()).append(", ").append(next.getBondState()).append(", ").append(next.getAddress()).append(", ").append(next.describeContents());
                if (bluetoothClass != null) {
                    int deviceClass = bluetoothClass.getDeviceClass();
                    int majorDeviceClass = bluetoothClass.getMajorDeviceClass();
                    new StringBuilder("BluetoothDevice[").append(i).append("]: BTMajorClass->").append(majorDeviceClass).append(", BTClass->").append(deviceClass);
                    if (1024 == majorDeviceClass) {
                        new StringBuilder("BluetoothDevice[").append(i).append("]: is an AudioVideo Device, is Usable:").append(1032 == deviceClass);
                        if (1032 == deviceClass || 1028 == deviceClass || 1056 == deviceClass || 1048 == deviceClass) {
                            return true;
                        }
                    }
                } else {
                    new StringBuilder("BluetoothDevice[").append(i).append("]: is not connected.");
                }
                i++;
            }
        }
        return false;
    }

    public static int GetAPIVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static String GetApplicationVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getApplicationContext().getPackageName(), 0);
            if (packageInfo == null) {
                return "";
            }
            String str = packageInfo.versionName;
            if (packageInfo.applicationInfo == null) {
                return str;
            }
            if (2 == (packageInfo.applicationInfo.flags & 2)) {
                str = str + " (d)";
            }
            return 16 == (packageInfo.applicationInfo.flags & 16) ? str + " (t)" : str;
        } catch (Exception e) {
            return "";
        }
    }

    public static String GetBasebandVersion() {
        String systemProperty = getSystemProperty(BASEBAND_VERSION);
        return systemProperty == null ? "" : replaceCommaAndLimitTo32(systemProperty);
    }

    public static String GetDeviceName() {
        String replaceCommaAndLimitTo32 = replaceCommaAndLimitTo32(Build.MANUFACTURER + " " + Build.MODEL);
        if (replaceCommaAndLimitTo32.length() == 0) {
            replaceCommaAndLimitTo32 = replaceCommaAndLimitTo32(Build.PRODUCT);
        }
        return replaceCommaAndLimitTo32.length() == 0 ? "TUnknown OEM" : replaceCommaAndLimitTo32;
    }

    public static String GetKernelVersion() {
        String property = System.getProperty("os.version");
        return property == null ? "" : replaceCommaAndLimitTo32(property);
    }

    public static String GetOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public static boolean IsTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) >= 3;
    }

    public static PowerManager.WakeLock ProximityLock(Context context) {
        return ((PowerManager) context.getSystemService("power")).newWakeLock(32, "mesibo:proximitylock");
    }

    public static boolean doSleep(long j) {
        return doSleep(j, false);
    }

    public static boolean doSleep(long j, boolean z) {
        if (z) {
            long currentTimeMillis = System.currentTimeMillis();
            Thread.yield();
            long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
            if (currentTimeMillis2 >= j) {
                return true;
            }
            j -= currentTimeMillis2;
        }
        try {
            Thread.sleep(j);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    @SuppressLint({"NewApi"})
    public static BluetoothAdapter getBluetoothAdapter(Context context) {
        return hasJellyBeanMR2() ? ((BluetoothManager) context.getSystemService("bluetooth")).getAdapter() : BluetoothAdapter.getDefaultAdapter();
    }

    public static String getBogoMipsFromCpuInfo() {
        String str;
        String[] split = readCPUinfo().split(":");
        int i = 0;
        while (true) {
            if (i >= split.length) {
                str = null;
                break;
            } else if (split[i].contains("BogoMIPS")) {
                str = split[i + 1];
                break;
            } else {
                i++;
            }
        }
        return str != null ? str.trim() : str;
    }

    @TargetApi(17)
    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) activity.getApplication().getSystemService("window")).getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics;
    }

    public static String getSystemProperty(String var0) {
        BufferedReader var1 = null;
        boolean var7 = false;

        label81: {
            try {
                var7 = true;
                Process var13 = Runtime.getRuntime().exec("getprop ".concat(String.valueOf(var0)));
                var0 = (var1 = new BufferedReader(new InputStreamReader(var13.getInputStream()), 1024)).readLine();
                var1.close();
                var7 = false;
                break label81;
            } catch (IOException var11) {
                var7 = false;
            } finally {
                if (var7) {
                    if (var1 != null) {
                        try {
                            var1.close();
                        } catch (IOException var8) {
                        }
                    }

                }
            }

            var0 = "";
            if (var1 != null) {
                try {
                    var1.close();
                } catch (IOException var9) {
                }
            }

            return var0;
        }

        try {
            var1.close();
        } catch (IOException var10) {
        }

        return var0;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= 9;
    }

    public static boolean hasGingerbreadMR1() {
        return Build.VERSION.SDK_INT >= 10;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= 11;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= 12;
    }

    public static boolean hasICS() {
        return Build.VERSION.SDK_INT >= 14;
    }

    public static boolean hasJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= 18;
    }

    public static boolean isKeyguardLocked(Context context) {
        return ((KeyguardManager) context.getSystemService("keyguard")).inKeyguardRestrictedInputMode();
    }

    public static boolean isSIMCardPresent(Context context) {
        boolean z;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
            if (telephonyManager.getPhoneType() == 0) {
                return false;
            }
            switch (telephonyManager.getSimState()) {
                case 1:
                case 2:
                case 3:
                case 4:
                    return false;
                case MesiboCall.MESIBOCALL_HANGUP_REASON_DURATION /*5*/:
                    z = true;
                    break;
                default:
                    z = false;
                    break;
            }
            return z;
        } catch (Exception e) {
            return false;
        }
    }

    public static void notifyIncomingCall(AudioManager audioManager) {
    }

    public static String readCPUinfo() {
        String str = "";
        InputStream inputStream = null;
        try {
            InputStream inputStream2 = new ProcessBuilder(new String[]{"/system/bin/cat", "/proc/cpuinfo"}).start().getInputStream();
            byte[] bArr = new byte[1024];
            while (inputStream2.read(bArr) != -1) {
                System.out.println(new String(bArr));
                str = str + new String(bArr);
            }
            if (inputStream2 != null) {
                try {
                    inputStream2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
        return str;
    }

    private static String replaceCommaAndLimitTo32(String str) {
        String replace = str.replace(",", " ");
        if (replace.length() <= 31) {
            return replace;
        }
        return replace.substring(0, 31) + 0;
    }
}
