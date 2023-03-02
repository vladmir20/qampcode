package com.qamp.app.qampCalls;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;


public class HardwareAwareActivity extends AppCompatActivity implements ProximityReceiver.ProximityListener {
    public static String TAG = "NOLOGSHardwareAwareActivity";
    private View mBaseView = null;
    private boolean mUseProximitySensor = true;
    private View touchProtectionWindow = null;
    private WindowManager mWindowManager = null;

    public HardwareAwareActivity() {
    }

    @TargetApi(19)
    private static int getSystemUiVisibility() {
        short var0 = 6;
        if (VERSION.SDK_INT >= 19) {
            var0 = 4102;
        }

        return var0;
    }

    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        this.requestWindowFeature(1);
        this.getWindow().addFlags(6849664);
        this.mBaseView = this.getWindow().getDecorView().getRootView();
        if (this.mBaseView == null) {
            this.mBaseView = this.getWindow().getDecorView();
        }

        if (this.mBaseView != null) {
            this.mBaseView.setSystemUiVisibility(getSystemUiVisibility());
        }

    }

    public void setHideOnProximity(boolean var1) {
        this.mUseProximitySensor = var1;
        if (this.mUseProximitySensor) {
            ProximityReceiver.addListener(this);
        } else {
            ProximityReceiver.removeListener(this);
        }
    }

    public void onProximity(boolean var1) {
        if (var1) {
            this.showTouchPreventionWindow();
        } else {
            this.removeTouchPreventionWindow();
        }
    }

    private void showTouchPreventionWindow() {
        if (this.mWindowManager == null) {
            this.mWindowManager = (WindowManager)this.getSystemService("window");
        }

        if (this.touchProtectionWindow == null) {
            this.buildTouchPreventionWindow();
        }

        short var1 = 2007;
        if (VERSION.SDK_INT >= 26) {
            var1 = 2038;
        }

        LayoutParams var3;
        (var3 = new LayoutParams(-1, -1, var1, 558210, -1)).alpha = 1.0F;
        var3.gravity = 17;
        var3.dimAmount = 1.0F;
        var3.screenBrightness = 0.01F;

        try {
            this.mWindowManager.addView(this.touchProtectionWindow, var3);
        } catch (Exception var2) {
            (new StringBuilder("exception adding protection view: ")).append(var2);
        }
    }

    private void removeTouchPreventionWindow() {
        if (this.touchProtectionWindow != null) {
            try {
                this.mWindowManager.removeView(this.touchProtectionWindow);
            } catch (Exception var1) {
            }
        }
    }

    private void buildTouchPreventionWindow() {
        this.touchProtectionWindow = new LinearLayout(this);
        LayoutParams var1 = new LayoutParams(-1, -1);
        this.touchProtectionWindow.setLayoutParams(var1);
    }

    protected void onPause() {
        super.onPause();
        this.removeTouchPreventionWindow();
        ProximityReceiver.removeListener(this);
    }

    protected void onResume() {
        super.onResume();
        if (this.mUseProximitySensor) {
            ProximityReceiver.addListener(this);
        }

    }

    protected void onDestroy() {
        super.onDestroy();
    }
}

