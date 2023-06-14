/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 3:15 PM
 *
 */

package com.qamp.app.qampcallss.api;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.Set;

public class RtcBluetoothManager {
    private static final int BLUETOOTH_SCO_TIMEOUT_MS = 4000;
    private static final int MAX_SCO_CONNECTION_ATTEMPTS = 2;
    private static final String TAG = "RtcBluetoothManager";
    private final RtcAudioManager apprtcAudioManager;
    private final Context apprtcContext;
    private final AudioManager audioManager;
    private BluetoothAdapter bluetoothAdapter;
    /* access modifiers changed from: private */
    public BluetoothDevice bluetoothDevice;
    /* access modifiers changed from: private */
    public BluetoothHeadset bluetoothHeadset;
    private final BroadcastReceiver bluetoothHeadsetReceiver;
    private final BluetoothProfile.ServiceListener bluetoothServiceListener;
    /* access modifiers changed from: private */
    public State bluetoothState;
    private final Runnable bluetoothTimeoutRunnable = new Runnable() {
        public void run() {
            RtcBluetoothManager.this.bluetoothTimeout();
        }
    };
    private final Handler handler;
    int scoConnectionAttempts;

    private class BluetoothHeadsetBroadcastReceiver extends BroadcastReceiver {
        private BluetoothHeadsetBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (RtcBluetoothManager.this.bluetoothState != State.UNINITIALIZED) {
                String action = intent.getAction();
                if (action.equals("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED")) {
                    int intExtra = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
                    new StringBuilder("BluetoothHeadsetBroadcastReceiver.onReceive: a=ACTION_CONNECTION_STATE_CHANGED, s=").append(RtcBluetoothManager.this.stateToString(intExtra)).append(", sb=").append(isInitialStickyBroadcast()).append(", BT state: ").append(RtcBluetoothManager.this.bluetoothState);
                    if (intExtra == 2) {
                        RtcBluetoothManager.this.scoConnectionAttempts = 0;
                    } else {
                        if (!(intExtra == 1 || intExtra == 3 || intExtra != 0)) {
                            RtcBluetoothManager.this.stopScoAudio();
                            RtcBluetoothManager.this.updateAudioDeviceState();
                        }
                        new StringBuilder("onReceive done: BT state=").append(RtcBluetoothManager.this.bluetoothState);
                    }
                } else {
                    if (action.equals("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED")) {
                        int intExtra2 = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 10);
                        new StringBuilder("BluetoothHeadsetBroadcastReceiver.onReceive: a=ACTION_AUDIO_STATE_CHANGED, s=").append(RtcBluetoothManager.this.stateToString(intExtra2)).append(", sb=").append(isInitialStickyBroadcast()).append(", BT state: ").append(RtcBluetoothManager.this.bluetoothState);
                        if (intExtra2 == 12) {
                            RtcBluetoothManager.this.cancelTimer();
                            if (RtcBluetoothManager.this.bluetoothState == State.SCO_CONNECTING) {
                                State unused = RtcBluetoothManager.this.bluetoothState = State.SCO_CONNECTED;
                                RtcBluetoothManager.this.scoConnectionAttempts = 0;
                            }
                        } else if (intExtra2 != 11 && intExtra2 == 10) {
                            if (isInitialStickyBroadcast()) {
                                return;
                            }
                        }
                    }
                    new StringBuilder("onReceive done: BT state=").append(RtcBluetoothManager.this.bluetoothState);
                }
                RtcBluetoothManager.this.updateAudioDeviceState();
                new StringBuilder("onReceive done: BT state=").append(RtcBluetoothManager.this.bluetoothState);
            }
        }
    }

    private class BluetoothServiceListener implements BluetoothProfile.ServiceListener {
        private BluetoothServiceListener() {
        }

        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            if (i == 1 && RtcBluetoothManager.this.bluetoothState != State.UNINITIALIZED) {
                new StringBuilder("BluetoothServiceListener.onServiceConnected: BT state=").append(RtcBluetoothManager.this.bluetoothState);
                BluetoothHeadset unused = RtcBluetoothManager.this.bluetoothHeadset = (BluetoothHeadset) bluetoothProfile;
                RtcBluetoothManager.this.updateAudioDeviceState();
                new StringBuilder("onServiceConnected done: BT state=").append(RtcBluetoothManager.this.bluetoothState);
            }
        }

        public void onServiceDisconnected(int i) {
            if (i == 1 && RtcBluetoothManager.this.bluetoothState != State.UNINITIALIZED) {
                new StringBuilder("BluetoothServiceListener.onServiceDisconnected: BT state=").append(RtcBluetoothManager.this.bluetoothState);
                RtcBluetoothManager.this.stopScoAudio();
                BluetoothHeadset unused = RtcBluetoothManager.this.bluetoothHeadset = null;
                BluetoothDevice unused2 = RtcBluetoothManager.this.bluetoothDevice = null;
                State unused3 = RtcBluetoothManager.this.bluetoothState = State.HEADSET_UNAVAILABLE;
                RtcBluetoothManager.this.updateAudioDeviceState();
                new StringBuilder("onServiceDisconnected done: BT state=").append(RtcBluetoothManager.this.bluetoothState);
            }
        }
    }

    public enum State {
        UNINITIALIZED,
        ERROR,
        HEADSET_UNAVAILABLE,
        HEADSET_AVAILABLE,
        SCO_DISCONNECTING,
        SCO_CONNECTING,
        SCO_CONNECTED
    }

    protected RtcBluetoothManager(Context context, RtcAudioManager rtcAudioManager) {
        this.apprtcContext = context;
        this.apprtcAudioManager = rtcAudioManager;
        this.audioManager = getAudioManager(context);
        this.bluetoothState = State.UNINITIALIZED;
        this.bluetoothServiceListener = new BluetoothServiceListener();
        this.bluetoothHeadsetReceiver = new BluetoothHeadsetBroadcastReceiver();
        this.handler = new Handler(Looper.getMainLooper());
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0069  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0091  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void bluetoothTimeout() {
        /*
            r3 = this;
            r1 = 0
            com.mesibo.calls.api.RtcBluetoothManager$State r0 = r3.bluetoothState
            com.mesibo.calls.api.RtcBluetoothManager$State r2 = com.mesibo.calls.api.RtcBluetoothManager.State.UNINITIALIZED
            if (r0 == r2) goto L_0x000b
            android.bluetooth.BluetoothHeadset r0 = r3.bluetoothHeadset
            if (r0 != 0) goto L_0x000c
        L_0x000b:
            return
        L_0x000c:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r2 = "bluetoothTimeout: BT state="
            r0.<init>(r2)
            com.mesibo.calls.api.RtcBluetoothManager$State r2 = r3.bluetoothState
            java.lang.StringBuilder r0 = r0.append(r2)
            java.lang.String r2 = ", attempts: "
            java.lang.StringBuilder r0 = r0.append(r2)
            int r2 = r3.scoConnectionAttempts
            java.lang.StringBuilder r0 = r0.append(r2)
            java.lang.String r2 = ", SCO is on: "
            java.lang.StringBuilder r0 = r0.append(r2)
            boolean r2 = r3.isScoOn()
            r0.append(r2)
            com.mesibo.calls.api.RtcBluetoothManager$State r0 = r3.bluetoothState
            com.mesibo.calls.api.RtcBluetoothManager$State r2 = com.mesibo.calls.api.RtcBluetoothManager.State.SCO_CONNECTING
            if (r0 != r2) goto L_0x000b
            android.bluetooth.BluetoothHeadset r0 = r3.bluetoothHeadset
            java.util.List r0 = r0.getConnectedDevices()
            int r2 = r0.size()
            if (r2 <= 0) goto L_0x008f
            java.lang.Object r0 = r0.get(r1)
            android.bluetooth.BluetoothDevice r0 = (android.bluetooth.BluetoothDevice) r0
            r3.bluetoothDevice = r0
            android.bluetooth.BluetoothHeadset r0 = r3.bluetoothHeadset
            android.bluetooth.BluetoothDevice r2 = r3.bluetoothDevice
            boolean r0 = r0.isAudioConnected(r2)
            if (r0 == 0) goto L_0x007f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r2 = "SCO connected with "
            r0.<init>(r2)
            android.bluetooth.BluetoothDevice r2 = r3.bluetoothDevice
            java.lang.String r2 = r2.getName()
            r0.append(r2)
            r0 = 1
        L_0x0067:
            if (r0 == 0) goto L_0x0091
            com.mesibo.calls.api.RtcBluetoothManager$State r0 = com.mesibo.calls.api.RtcBluetoothManager.State.SCO_CONNECTED
            r3.bluetoothState = r0
            r3.scoConnectionAttempts = r1
        L_0x006f:
            r3.updateAudioDeviceState()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r1 = "bluetoothTimeout done: BT state="
            r0.<init>(r1)
            com.mesibo.calls.api.RtcBluetoothManager$State r1 = r3.bluetoothState
            r0.append(r1)
            goto L_0x000b
        L_0x007f:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r2 = "SCO is not connected with "
            r0.<init>(r2)
            android.bluetooth.BluetoothDevice r2 = r3.bluetoothDevice
            java.lang.String r2 = r2.getName()
            r0.append(r2)
        L_0x008f:
            r0 = r1
            goto L_0x0067
        L_0x0091:
            r3.stopScoAudio()
            goto L_0x006f
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mesibo.calls.api.RtcBluetoothManager.bluetoothTimeout():void");
    }

    /* access modifiers changed from: private */
    public void cancelTimer() {
        this.handler.removeCallbacks(this.bluetoothTimeoutRunnable);
    }

    static RtcBluetoothManager create(Context context, RtcAudioManager rtcAudioManager) {
        return new RtcBluetoothManager(context, rtcAudioManager);
    }

    private boolean isScoOn() {
        return this.audioManager.isBluetoothScoOn();
    }

    private void startTimer() {
        this.handler.postDelayed(this.bluetoothTimeoutRunnable, 4000);
    }

    /* access modifiers changed from: private */
    public String stateToString(int i) {
        switch (i) {
            case 0:
                return "DISCONNECTED";
            case 1:
                return "CONNECTING";
            case 2:
                return "CONNECTED";
            case 3:
                return "DISCONNECTING";
            case 10:
                return "OFF";
            case 11:
                return "TURNING_ON";
            case 12:
                return "ON";
            case 13:
                return "TURNING_OFF";
            default:
                return "INVALID";
        }
    }

    /* access modifiers changed from: private */
    public void updateAudioDeviceState() {
        this.apprtcAudioManager.updateAudioDeviceState();
    }

    /* access modifiers changed from: protected */
    public AudioManager getAudioManager(Context context) {
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /* access modifiers changed from: protected */
    public boolean getBluetoothProfileProxy(Context context, BluetoothProfile.ServiceListener serviceListener, int i) {
        return this.bluetoothAdapter.getProfileProxy(context, serviceListener, i);
    }

    public State getState() {
        return this.bluetoothState;
    }

    /* access modifiers changed from: protected */
    public boolean hasPermission(Context context, String str) {
        return this.apprtcContext.checkPermission(str, Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED;
    }

    /* access modifiers changed from: protected */
    public boolean hasPermissionNew(Context context, String str) {
        return ActivityCompat.checkSelfPermission(context, str) == 0;
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"HardwareIds"})
    public void logBluetoothAdapterInfo(BluetoothAdapter bluetoothAdapter2) {
        new StringBuilder("BluetoothAdapter: enabled=").append(bluetoothAdapter2.isEnabled()).append(", state=").append(stateToString(bluetoothAdapter2.getState())).append(", name=").append(bluetoothAdapter2.getName()).append(", address=").append(bluetoothAdapter2.getAddress());
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter2.getBondedDevices();
        if (!bondedDevices.isEmpty()) {
            for (BluetoothDevice next : bondedDevices) {
                new StringBuilder(" name=").append(next.getName()).append(", address=").append(next.getAddress());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        this.apprtcContext.registerReceiver(broadcastReceiver, intentFilter);
    }

    public void start() {
        if (!hasPermission(this.apprtcContext, "android.permission.BLUETOOTH")) {
            new StringBuilder("Process (pid=").append(Process.myPid()).append(") lacks BLUETOOTH permission");
        } else if (Build.VERSION.SDK_INT >= 31 && !hasPermission(this.apprtcContext, "android.permission.BLUETOOTH_CONNECT")) {
            new StringBuilder("Process (pid=").append(Process.myPid()).append(") lacks BLUETOOTH_CONNECT permission");
        } else if (this.bluetoothState == State.UNINITIALIZED) {
            this.bluetoothHeadset = null;
            this.bluetoothDevice = null;
            this.scoConnectionAttempts = 0;
            this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (this.bluetoothAdapter != null && this.audioManager.isBluetoothScoAvailableOffCall()) {
                logBluetoothAdapterInfo(this.bluetoothAdapter);
                if (getBluetoothProfileProxy(this.apprtcContext, this.bluetoothServiceListener, 1)) {
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
                    intentFilter.addAction("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED");
                    registerReceiver(this.bluetoothHeadsetReceiver, intentFilter);
                    new StringBuilder("HEADSET profile state: ").append(stateToString(this.bluetoothAdapter.getProfileConnectionState(1)));
                    this.bluetoothState = State.HEADSET_UNAVAILABLE;
                    new StringBuilder("start done: BT state=").append(this.bluetoothState);
                }
            }
        }
    }

    public boolean startScoAudio() {
        new StringBuilder("startSco: BT state=").append(this.bluetoothState).append(", attempts: ").append(this.scoConnectionAttempts).append(", SCO is on: ").append(isScoOn());
        if (this.scoConnectionAttempts >= 2 || this.bluetoothState != State.HEADSET_AVAILABLE) {
            return false;
        }
        this.bluetoothState = State.SCO_CONNECTING;
        this.audioManager.startBluetoothSco();
        this.audioManager.setBluetoothScoOn(true);
        this.scoConnectionAttempts++;
        startTimer();
        new StringBuilder("startScoAudio done: BT state=").append(this.bluetoothState).append(", SCO is on: ").append(isScoOn());
        return true;
    }

    public void stop() {
        new StringBuilder("stop: BT state=").append(this.bluetoothState);
        if (this.bluetoothAdapter != null) {
            try {
                stopScoAudio();
                if (this.bluetoothState != State.UNINITIALIZED) {
                    unregisterReceiver(this.bluetoothHeadsetReceiver);
                    cancelTimer();
                    if (this.bluetoothHeadset != null) {
                        this.bluetoothAdapter.closeProfileProxy(1, this.bluetoothHeadset);
                        this.bluetoothHeadset = null;
                    }
                    this.bluetoothAdapter = null;
                    this.bluetoothDevice = null;
                    this.bluetoothState = State.UNINITIALIZED;
                    new StringBuilder("stop done: BT state=").append(this.bluetoothState);
                }
            } catch (Exception e) {
            }
        }
    }

    public void stopScoAudio() {
        new StringBuilder("stopScoAudio: BT state=").append(this.bluetoothState).append(", SCO is on: ").append(isScoOn());
        if (this.bluetoothState == State.SCO_CONNECTING || this.bluetoothState == State.SCO_CONNECTED) {
            cancelTimer();
            this.audioManager.stopBluetoothSco();
            this.audioManager.setBluetoothScoOn(false);
            this.bluetoothState = State.SCO_DISCONNECTING;
            new StringBuilder("stopScoAudio done: BT state=").append(this.bluetoothState).append(", SCO is on: ").append(isScoOn());
        }
    }

    /* access modifiers changed from: protected */
    public void unregisterReceiver(BroadcastReceiver broadcastReceiver) {
        try {
            this.apprtcContext.unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
        }
    }

    public void updateDevice() {
        if (this.bluetoothState != State.UNINITIALIZED && this.bluetoothHeadset != null) {
            List<BluetoothDevice> connectedDevices = this.bluetoothHeadset.getConnectedDevices();
            if (connectedDevices.isEmpty()) {
                this.bluetoothDevice = null;
                this.bluetoothState = State.HEADSET_UNAVAILABLE;
            } else {
                this.bluetoothDevice = connectedDevices.get(0);
                this.bluetoothState = State.HEADSET_AVAILABLE;
                new StringBuilder("Connected bluetooth headset: name=").append(this.bluetoothDevice.getName()).append(", state=").append(stateToString(this.bluetoothHeadset.getConnectionState(this.bluetoothDevice))).append(", SCO audio=").append(this.bluetoothHeadset.isAudioConnected(this.bluetoothDevice));
            }
            new StringBuilder("updateDevice done: BT state=").append(this.bluetoothState);
        }
    }
}
