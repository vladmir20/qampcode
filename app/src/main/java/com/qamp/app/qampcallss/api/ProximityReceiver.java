/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 3:15 PM
 *
 */

package com.qamp.app.qampcallss.api;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class ProximityReceiver implements SensorEventListener {
    private static final String TAG = "ProximityReceiver";
    private static float THRESHOLD_DISTANCE = 2.0f;
    private static ProximityReceiver _instance = null;
    private static Context context = null;
    private static Set<ProximityListener> mListeners = Collections.newSetFromMap(new WeakHashMap());
    private static Sensor mProximity = null;
    private static SensorManager mSensorManager = null;
    private static boolean mState = false;

    public interface ProximityListener {
        void onProximity(boolean z);
    }

    private ProximityReceiver(Context context2) {
        _instance = this;
        context = context2;
        SensorManager sensorManager = (SensorManager) context2.getSystemService(Context.SENSOR_SERVICE);
        mSensorManager = sensorManager;
        mProximity = sensorManager.getDefaultSensor(8);
    }

    public static void addListener(ProximityListener proximityListener) {
        init();
        if (mListeners.size() == 0) {
            mSensorManager.registerListener(_instance, mProximity, 3);
        }
        mListeners.add(proximityListener);
    }

    private static synchronized ProximityReceiver init() {
        ProximityReceiver proximityReceiver;
        synchronized (ProximityReceiver.class) {
            proximityReceiver = _instance != null ? _instance : new ProximityReceiver(CallManager.getAppContext());
        }
        return proximityReceiver;
    }

    private void invokeListeners(boolean z) {
        if (mListeners.size() != 0) {
            for (ProximityListener onProximity : mListeners) {
                onProximity.onProximity(z);
            }
        }
    }

    private void logProximitySensorInfo() {
        if (mProximity != null) {
            StringBuilder sb = new StringBuilder("Proximity sensor: ");
            sb.append("name=").append(mProximity.getName());
            sb.append(", vendor: ").append(mProximity.getVendor());
            sb.append(", power: ").append(mProximity.getPower());
            sb.append(", resolution: ").append(mProximity.getResolution());
            sb.append(", max range: ").append(mProximity.getMaximumRange());
            sb.append(", min delay: ").append(mProximity.getMinDelay());
            if (Build.VERSION.SDK_INT >= 20) {
                sb.append(", type: ").append(mProximity.getStringType());
            }
            if (Build.VERSION.SDK_INT >= 21) {
                sb.append(", max delay: ").append(mProximity.getMaxDelay());
                sb.append(", reporting mode: ").append(mProximity.getReportingMode());
                sb.append(", isWakeUpSensor: ").append(mProximity.isWakeUpSensor());
            }
        }
    }

    public static void removeListener(ProximityListener proximityListener) {
        init();
        mListeners.remove(proximityListener);
        if (mListeners.size() == 0) {
            mSensorManager.unregisterListener(_instance);
        }
    }

    public final void onAccuracyChanged(Sensor sensor, int i) {
        if (sensor.getType() != 8) {
        }
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.values != null) {
            float maximumRange = sensorEvent.sensor.getMaximumRange();
            float f = sensorEvent.values[0];
            new StringBuilder("ProximitySensor onSensorChanged: distance: ").append(f).append(", max range").append(maximumRange);
            if (maximumRange <= THRESHOLD_DISTANCE) {
                THRESHOLD_DISTANCE = (float) (((double) maximumRange) - 0.1d);
            }
            if (f < THRESHOLD_DISTANCE) {
                if (!mState) {
                    mState = true;
                    invokeListeners(true);
                }
            } else if (mState) {
                mState = false;
                invokeListeners(false);
            }
        }
    }
}
