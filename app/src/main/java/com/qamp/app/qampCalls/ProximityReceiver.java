package com.qamp.app.qampCalls;



import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build.VERSION;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

public class ProximityReceiver implements SensorEventListener {
    private static final String TAG = "ProximityReceiver";
    private static SensorManager mSensorManager = null;
    private static Sensor mProximity = null;
    private static Context context = null;
    private static Set<ProximityListener> mListeners = Collections.newSetFromMap(new WeakHashMap());
    private static ProximityReceiver _instance = null;
    private static boolean mState = false;
    private static float THRESHOLD_DISTANCE = 2.0F;

    private ProximityReceiver(Context var1) {
        _instance = this;
        context = var1;
        mProximity = (mSensorManager = (SensorManager)var1.getSystemService(Context.SENSOR_SERVICE)).getDefaultSensor(8);
    }

    private static synchronized ProximityReceiver init() {
        return _instance != null ? _instance : new ProximityReceiver(CallManager.getAppContext());
    }

    public final void onAccuracyChanged(Sensor var1, int var2) {
        if (var1.getType() == 8) {
            ;
        }
    }

    public void onSensorChanged(SensorEvent var1) {
        if (var1.values != null) {
            float var2 = var1.sensor.getMaximumRange();
            float var3 = var1.values[0];
            (new StringBuilder("ProximitySensor onSensorChanged: distance: ")).append(var3).append(", max range").append(var2);
            if (var2 <= THRESHOLD_DISTANCE) {
                THRESHOLD_DISTANCE = (float)((double)var2 - 0.1D);
            }

            if (var3 < THRESHOLD_DISTANCE) {
                if (!mState) {
                    mState = true;
                    this.invokeListeners(true);
                    return;
                }
            } else if (mState) {
                mState = false;
                this.invokeListeners(false);
            }

        }
    }

    private void invokeListeners(boolean var1) {
        if (0 != mListeners.size()) {
            Iterator var2 = mListeners.iterator();

            while(var2.hasNext()) {
                ((ProximityListener)var2.next()).onProximity(var1);
            }

        }
    }

    public static void addListener(ProximityListener var0) {
        init();
        if (mListeners.size() == 0) {
            mSensorManager.registerListener(_instance, mProximity, 3);
        }

        mListeners.add(var0);
    }

    public static void removeListener(ProximityListener var0) {
        init();
        mListeners.remove(var0);
        if (mListeners.size() == 0) {
            mSensorManager.unregisterListener(_instance);
        }

    }

    private void logProximitySensorInfo() {
        if (mProximity != null) {
            StringBuilder var1;
            (var1 = new StringBuilder("Proximity sensor: ")).append("name=").append(mProximity.getName());
            var1.append(", vendor: ").append(mProximity.getVendor());
            var1.append(", power: ").append(mProximity.getPower());
            var1.append(", resolution: ").append(mProximity.getResolution());
            var1.append(", max range: ").append(mProximity.getMaximumRange());
            var1.append(", min delay: ").append(mProximity.getMinDelay());
            if (VERSION.SDK_INT >= 20) {
                var1.append(", type: ").append(mProximity.getStringType());
            }

            if (VERSION.SDK_INT >= 21) {
                var1.append(", max delay: ").append(mProximity.getMaxDelay());
                var1.append(", reporting mode: ").append(mProximity.getReportingMode());
                var1.append(", isWakeUpSensor: ").append(mProximity.isWakeUpSensor());
            }

        }
    }

    public interface ProximityListener {
        void onProximity(boolean var1);
    }
}

