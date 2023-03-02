package com.qamp.app.qampCalls;

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
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Set;

public class DeviceUtils {
    public static final String TAG = "DeviceUtils";
    private static final String BASEBAND_VERSION = "gsm.version.baseband";

    public DeviceUtils() {
    }

    private static String replaceCommaAndLimitTo32(String var0) {
        if ((var0 = var0.replace(",", " ")).length() > 31) {
            var0 = var0.substring(0, 31);
            var0 = var0 + '\u0000';
        }

        return var0;
    }

    public static String GetApplicationVersion(Context var0) {
        String var1 = "";
        PackageManager var2 = var0.getPackageManager();

        try {
            PackageInfo var4;
            if ((var4 = var2.getPackageInfo(var0.getApplicationContext().getPackageName(), 0)) != null) {
                var1 = var4.versionName;
                if (var4.applicationInfo != null) {
                    if (2 == (var4.applicationInfo.flags & 2)) {
                        var1 = var1 + " (d)";
                    }

                    if (16 == (var4.applicationInfo.flags & 16)) {
                        var1 = var1 + " (t)";
                    }
                }
            }
        } catch (Exception var3) {
        }

        return var1;
    }

    public static String GetOSVersion() {
        return VERSION.RELEASE;
    }

    public static int GetAPIVersion() {
        return VERSION.SDK_INT;
    }

    public static String GetDeviceName() {
        String var0;
        if ((var0 = replaceCommaAndLimitTo32(Build.MANUFACTURER + " " + Build.MODEL)).length() == 0) {
            var0 = replaceCommaAndLimitTo32(Build.PRODUCT);
        }

        if (var0.length() == 0) {
            var0 = "TUnknown OEM";
        }

        return var0;
    }

    public static String GetKernelVersion() {
        String var0;
        return (var0 = System.getProperty("os.version")) == null ? "" : replaceCommaAndLimitTo32(var0);
    }

    public static String GetBasebandVersion() {
        String var0;
        return (var0 = getSystemProperty("gsm.version.baseband")) == null ? "" : replaceCommaAndLimitTo32(var0);
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
                        } catch (IOException var9) {
                        }
                    }

                }
            }

            var0 = "";
            if (var1 != null) {
                try {
                    var1.close();
                } catch (IOException var8) {
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

    public static boolean IsTablet(Context var0) {
        return (var0.getResources().getConfiguration().screenLayout & 15) >= 3;
    }

    public static boolean isSIMCardPresent(Context var0) {
        boolean var1 = false;

        try {
            TelephonyManager var3;
            if ((var3 = (TelephonyManager)var0.getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType() == 0) {
                var1 = false;
            } else {
                switch(var3.getSimState()) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    default:
                        break;
                    case 5:
                        var1 = true;
                }
            }
        } catch (Exception var2) {
            var1 = false;
        }

        return var1;
    }

    public static boolean isKeyguardLocked(Context var0) {
        return ((KeyguardManager)var0.getSystemService(Context.KEYGUARD_SERVICE)).inKeyguardRestrictedInputMode();
    }

    @SuppressLint({"NewApi"})
    public static BluetoothAdapter getBluetoothAdapter(Context var0) {
        BluetoothAdapter var1;
        if (hasJellyBeanMR2()) {
            var1 = ((BluetoothManager)var0.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        } else {
            var1 = BluetoothAdapter.getDefaultAdapter();
        }

        return var1;
    }

    public static boolean BluetoothStatus(Context var0) {
        BluetoothAdapter var4;
        if ((var4 = getBluetoothAdapter(var0)) == null) {
            return false;
        } else if (!var4.isEnabled()) {
            return false;
        } else {
            (new StringBuilder("Device has Bluetooth Support/Feature: ")).append(var4 != null);
            int var1 = var4.getState();
            (new StringBuilder("Bluetooth Active: ")).append(12 == var1);
            (new StringBuilder("Bluetooth in process on Activating: ")).append(11 == var1);
            if (12 != var1) {
                return false;
            } else {
                Set var5;
                if ((var5 = var4.getBondedDevices()) == null) {
                    return false;
                } else {
                    Iterator var7 = var5.iterator();
                    (new StringBuilder("Number of Devices: ")).append(var5.size());
                    int var2 = 0;

                    while(true) {
                        BluetoothDevice var6;
                        do {
                            if (!var7.hasNext()) {
                                return false;
                            }

                            var6 = (BluetoothDevice)var7.next();
                        } while(12 != var6.getBondState());

                        BluetoothClass var3 = var6.getBluetoothClass();
                        (new StringBuilder("BluetoothDevice[")).append(var2).append("]: ").append(var6.getName()).append(", ").append(var6.getBondState()).append(", ").append(var6.getAddress()).append(", ").append(var6.describeContents());
                        if (var3 != null) {
                            int var8 = var3.getDeviceClass();
                            int var9 = var3.getMajorDeviceClass();
                            (new StringBuilder("BluetoothDevice[")).append(var2).append("]: BTMajorClass->").append(var9).append(", BTClass->").append(var8);
                            if (1024 == var9) {
                                (new StringBuilder("BluetoothDevice[")).append(var2).append("]: is an AudioVideo Device, is Usable:").append(1032 == var8);
                                if (1032 == var8 || 1028 == var8 || 1056 == var8 || 1048 == var8) {
                                    return true;
                                }
                            }
                        } else {
                            (new StringBuilder("BluetoothDevice[")).append(var2).append("]: is not connected.");
                        }

                        ++var2;
                    }
                }
            }
        }
    }

    public static String getBogoMipsFromCpuInfo() {
        String var0 = null;
        String[] var1 = readCPUinfo().split(":");

        for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2].contains("BogoMIPS")) {
                var0 = var1[var2 + 1];
                break;
            }
        }

        if (var0 != null) {
            var0 = var0.trim();
        }

        return var0;
    }

    public static String readCPUinfo() {
        String var1 = "";
        InputStream var2 = null;

        try {
            String[] var0 = new String[]{"/system/bin/cat", "/proc/cpuinfo"};
            var2 = (new ProcessBuilder(var0)).start().getInputStream();

            for(byte[] var11 = new byte[1024]; var2.read(var11) != -1; var1 = var1 + new String(var11)) {
                System.out.println(new String(var11));
            }
        } catch (IOException var9) {
            var9.printStackTrace();
        } finally {
            try {
                if (var2 != null) {
                    var2.close();
                }
            } catch (IOException var8) {
                var8.printStackTrace();
            }

        }

        return var1;
    }

    @SuppressLint("InvalidWakeLockTag")
    public static WakeLock ProximityLock(Context var0) {
        return ((PowerManager)var0.getSystemService(Context.POWER_SERVICE)).newWakeLock(32, "DeviceUtils");
    }

    public static boolean hasGingerbread() {
        return VERSION.SDK_INT >= 9;
    }

    public static boolean hasGingerbreadMR1() {
        return VERSION.SDK_INT >= 10;
    }

    public static boolean hasHoneycomb() {
        return VERSION.SDK_INT >= 11;
    }

    public static boolean hasHoneycombMR1() {
        return VERSION.SDK_INT >= 12;
    }

    public static boolean hasJellyBeanMR2() {
        return VERSION.SDK_INT >= 18;
    }

    public static boolean hasICS() {
        return VERSION.SDK_INT >= 14;
    }

    public static boolean doSleep(long var0) {
        return doSleep(var0, false);
    }

    public static boolean doSleep(long var0, boolean var2) {
        if (var2) {
            long var3 = System.currentTimeMillis();
            Thread.yield();
            if ((var3 = System.currentTimeMillis() - var3) >= var0) {
                return true;
            }

            var0 -= var3;
        }

        try {
            Thread.sleep(var0);
            return true;
        } catch (InterruptedException var5) {
            return false;
        }
    }

    public static void notifyIncomingCall(AudioManager var0) {
    }

    @TargetApi(17)
    public static DisplayMetrics getDisplayMetrics(Activity var0) {
        DisplayMetrics var1 = new DisplayMetrics();
        ((WindowManager)var0.getApplication().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(var1);
        return var1;
    }
}

