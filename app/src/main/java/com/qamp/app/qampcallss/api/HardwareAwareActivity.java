/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:40 AM
 *
 */

package com.qamp.app.qampcallss.api;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class HardwareAwareActivity extends AppCompatActivity implements ProximityReceiver.ProximityListener {
    public static String TAG = "NOLOGSHardwareAwareActivity";
    private View mBaseView = null;
    private PowerManager.WakeLock mLock = null;
    private boolean mUseProximitySensor = false;
    private WindowManager mWindowManager = null;
    private View touchProtectionWindow = null;

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.mesibo.calls.api.HardwareAwareActivity] */
    private void buildTouchPreventionWindow() {
        this.touchProtectionWindow = new LinearLayout(this);
        this.touchProtectionWindow.setLayoutParams(new WindowManager.LayoutParams(-1, -1));
    }

    @TargetApi(19)
    private static int getSystemUiVisibility() {
        return Build.VERSION.SDK_INT >= 19 ? 4102 : 6;
    }

    private void removeTouchPreventionWindow() {
        if (this.mLock.isHeld()) {
            this.mLock.release();
        }
    }

    private void showTouchPreventionWindow() {
        this.mLock.acquire();
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.mesibo.calls.api.HardwareAwareActivity, androidx.appcompat.app.AppCompatActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        HardwareAwareActivity.super.onCreate(bundle);
        this.mLock = DeviceUtils.ProximityLock(this);
        requestWindowFeature(1);
        getWindow().addFlags(6849664);
        this.mBaseView = getWindow().getDecorView().getRootView();
        if (this.mBaseView == null) {
            this.mBaseView = getWindow().getDecorView();
        }
        if (this.mBaseView != null) {
            this.mBaseView.setSystemUiVisibility(getSystemUiVisibility());
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        HardwareAwareActivity.super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        HardwareAwareActivity.super.onPause();
        removeTouchPreventionWindow();
        ProximityReceiver.removeListener(this);
    }

    public void onProximity(boolean z) {
        if (z) {
            showTouchPreventionWindow();
        } else {
            removeTouchPreventionWindow();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        HardwareAwareActivity.super.onResume();
        if (this.mUseProximitySensor) {
            ProximityReceiver.addListener(this);
        }
    }

    public void setHideOnProximity(boolean z) {
        this.mUseProximitySensor = z;
        if (this.mUseProximitySensor) {
            ProximityReceiver.addListener(this);
        } else {
            ProximityReceiver.removeListener(this);
        }
    }
}
