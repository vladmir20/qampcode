package com.qamp.app.qampCalls;


import android.os.WorkSource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.Executor;

public final class PowerManager {
    public static final int ACQUIRE_CAUSES_WAKEUP = 268435456;
    public static final String ACTION_DEVICE_IDLE_MODE_CHANGED = "android.os.action.DEVICE_IDLE_MODE_CHANGED";
    public static final String ACTION_POWER_SAVE_MODE_CHANGED = "android.os.action.POWER_SAVE_MODE_CHANGED";
    /** @deprecated */
    @Deprecated
    public static final int FULL_WAKE_LOCK = 26;
    public static final int LOCATION_MODE_ALL_DISABLED_WHEN_SCREEN_OFF = 2;
    public static final int LOCATION_MODE_FOREGROUND_ONLY = 3;
    public static final int LOCATION_MODE_GPS_DISABLED_WHEN_SCREEN_OFF = 1;
    public static final int LOCATION_MODE_NO_CHANGE = 0;
    public static final int LOCATION_MODE_THROTTLE_REQUESTS_WHEN_SCREEN_OFF = 4;
    public static final int ON_AFTER_RELEASE = 536870912;
    public static final int PARTIAL_WAKE_LOCK = 1;
    public static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;
    public static final int RELEASE_FLAG_WAIT_FOR_NO_PROXIMITY = 1;
    /** @deprecated */
    @Deprecated
    public static final int SCREEN_BRIGHT_WAKE_LOCK = 10;
    /** @deprecated */
    @Deprecated
    public static final int SCREEN_DIM_WAKE_LOCK = 6;
    public static final int THERMAL_STATUS_CRITICAL = 4;
    public static final int THERMAL_STATUS_EMERGENCY = 5;
    public static final int THERMAL_STATUS_LIGHT = 1;
    public static final int THERMAL_STATUS_MODERATE = 2;
    public static final int THERMAL_STATUS_NONE = 0;
    public static final int THERMAL_STATUS_SEVERE = 3;
    public static final int THERMAL_STATUS_SHUTDOWN = 6;

    PowerManager() {
        throw new RuntimeException("Stub!");
    }

    public WakeLock newWakeLock(int levelAndFlags, String tag) {
        throw new RuntimeException("Stub!");
    }

    public boolean isWakeLockLevelSupported(int level) {
        throw new RuntimeException("Stub!");
    }

    /** @deprecated */
    @Deprecated
    public boolean isScreenOn() {
        throw new RuntimeException("Stub!");
    }

    public boolean isInteractive() {
        throw new RuntimeException("Stub!");
    }

    public boolean isRebootingUserspaceSupported() {
        throw new RuntimeException("Stub!");
    }

    public void reboot(@Nullable String reason) {
        throw new RuntimeException("Stub!");
    }

    public boolean isPowerSaveMode() {
        throw new RuntimeException("Stub!");
    }

    public int getLocationPowerSaveMode() {
        throw new RuntimeException("Stub!");
    }

    public boolean isDeviceIdleMode() {
        throw new RuntimeException("Stub!");
    }

    public boolean isIgnoringBatteryOptimizations(String packageName) {
        throw new RuntimeException("Stub!");
    }

    public boolean isSustainedPerformanceModeSupported() {
        throw new RuntimeException("Stub!");
    }

    public int getCurrentThermalStatus() {
        throw new RuntimeException("Stub!");
    }

    public void addThermalStatusListener(@NonNull android.os.PowerManager.OnThermalStatusChangedListener listener) {
        throw new RuntimeException("Stub!");
    }

    public void addThermalStatusListener(@NonNull Executor executor, @NonNull android.os.PowerManager.OnThermalStatusChangedListener listener) {
        throw new RuntimeException("Stub!");
    }

    public void removeThermalStatusListener(@NonNull android.os.PowerManager.OnThermalStatusChangedListener listener) {
        throw new RuntimeException("Stub!");
    }

    public float getThermalHeadroom(int forecastSeconds) {
        throw new RuntimeException("Stub!");
    }

    public final class WakeLock {
        WakeLock() {
            throw new RuntimeException("Stub!");
        }

        protected void finalize() throws Throwable {
            throw new RuntimeException("Stub!");
        }

        public void setReferenceCounted(boolean value) {
            throw new RuntimeException("Stub!");
        }

        public void acquire() {
            throw new RuntimeException("Stub!");
        }

        public void acquire(long timeout) {
            throw new RuntimeException("Stub!");
        }

        public void release() {
            throw new RuntimeException("Stub!");
        }

        public void release(int flags) {
            throw new RuntimeException("Stub!");
        }

        public boolean isHeld() {
            throw new RuntimeException("Stub!");
        }

        public void setWorkSource(WorkSource ws) {
            throw new RuntimeException("Stub!");
        }

        public String toString() {
            throw new RuntimeException("Stub!");
        }
    }

    public interface OnThermalStatusChangedListener {
        void onThermalStatusChanged(int var1);
    }
}

