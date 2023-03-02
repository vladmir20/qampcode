package com.qamp.app.qampCalls;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RtcBluetoothManager {
    private static final String TAG = "RtcBluetoothManager";
    private static final int BLUETOOTH_SCO_TIMEOUT_MS = 4000;
    private static final int MAX_SCO_CONNECTION_ATTEMPTS = 2;
    private final Context apprtcContext;
    private final RtcAudioManager apprtcAudioManager;
    private final AudioManager audioManager;
    private final Handler handler;
    int scoConnectionAttempts;
    private State bluetoothState;
    private final ServiceListener bluetoothServiceListener;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothHeadset bluetoothHeadset;
    private BluetoothDevice bluetoothDevice;
    private final BroadcastReceiver bluetoothHeadsetReceiver;
    private final Runnable bluetoothTimeoutRunnable = new Runnable() {
        public void run() {
            RtcBluetoothManager.this.bluetoothTimeout();
        }
    };

    static RtcBluetoothManager create(Context var0, RtcAudioManager var1) {
        return new RtcBluetoothManager(var0, var1);
    }

    protected RtcBluetoothManager(Context var1, RtcAudioManager var2) {
        this.apprtcContext = var1;
        this.apprtcAudioManager = var2;
        this.audioManager = this.getAudioManager(var1);
        this.bluetoothState = State.UNINITIALIZED;
        this.bluetoothServiceListener = new BluetoothServiceListener();
        this.bluetoothHeadsetReceiver = new BluetoothHeadsetBroadcastReceiver();
        this.handler = new Handler(Looper.getMainLooper());
    }

    public State getState() {
        return this.bluetoothState;
    }

    public void start() {
        if (!this.hasPermission(this.apprtcContext, "android.permission.BLUETOOTH")) {
            (new StringBuilder("Process (pid=")).append(Process.myPid()).append(") lacks BLUETOOTH permission");
        } else if (this.bluetoothState == State.UNINITIALIZED) {
            this.bluetoothHeadset = null;
            this.bluetoothDevice = null;
            this.scoConnectionAttempts = 0;
            this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (this.bluetoothAdapter != null) {
                if (this.audioManager.isBluetoothScoAvailableOffCall()) {
                    this.logBluetoothAdapterInfo(this.bluetoothAdapter);
                    if (this.getBluetoothProfileProxy(this.apprtcContext, this.bluetoothServiceListener, 1)) {
                        IntentFilter var1;
                        (var1 = new IntentFilter()).addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
                        var1.addAction("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED");
                        this.registerReceiver(this.bluetoothHeadsetReceiver, var1);
                        (new StringBuilder("HEADSET profile state: ")).append(this.stateToString(this.bluetoothAdapter.getProfileConnectionState(1)));
                        this.bluetoothState = State.HEADSET_UNAVAILABLE;
                        (new StringBuilder("start done: BT state=")).append(this.bluetoothState);
                    }
                }
            }
        }
    }

    public void stop() {
        (new StringBuilder("stop: BT state=")).append(this.bluetoothState);
        if (this.bluetoothAdapter != null) {
            try {
                this.stopScoAudio();
                if (this.bluetoothState == State.UNINITIALIZED) {
                    return;
                }

                this.unregisterReceiver(this.bluetoothHeadsetReceiver);
                this.cancelTimer();
                if (this.bluetoothHeadset != null) {
                    this.bluetoothAdapter.closeProfileProxy(1, this.bluetoothHeadset);
                    this.bluetoothHeadset = null;
                }

                this.bluetoothAdapter = null;
                this.bluetoothDevice = null;
                this.bluetoothState = State.UNINITIALIZED;
            } catch (Exception var1) {
            }

            (new StringBuilder("stop done: BT state=")).append(this.bluetoothState);
        }
    }

    public boolean startScoAudio() {
        (new StringBuilder("startSco: BT state=")).append(this.bluetoothState).append(", attempts: ").append(this.scoConnectionAttempts).append(", SCO is on: ").append(this.isScoOn());
        if (this.scoConnectionAttempts >= 2) {
            return false;
        } else if (this.bluetoothState != State.HEADSET_AVAILABLE) {
            return false;
        } else {
            this.bluetoothState = State.SCO_CONNECTING;
            this.audioManager.startBluetoothSco();
            this.audioManager.setBluetoothScoOn(true);
            ++this.scoConnectionAttempts;
            this.startTimer();
            (new StringBuilder("startScoAudio done: BT state=")).append(this.bluetoothState).append(", SCO is on: ").append(this.isScoOn());
            return true;
        }
    }

    public void stopScoAudio() {
        (new StringBuilder("stopScoAudio: BT state=")).append(this.bluetoothState).append(", SCO is on: ").append(this.isScoOn());
        if (this.bluetoothState == State.SCO_CONNECTING || this.bluetoothState == State.SCO_CONNECTED) {
            this.cancelTimer();
            this.audioManager.stopBluetoothSco();
            this.audioManager.setBluetoothScoOn(false);
            this.bluetoothState = State.SCO_DISCONNECTING;
            (new StringBuilder("stopScoAudio done: BT state=")).append(this.bluetoothState).append(", SCO is on: ").append(this.isScoOn());
        }
    }

    public void updateDevice() {
        if (this.bluetoothState != State.UNINITIALIZED && this.bluetoothHeadset != null) {
            List var1;
            if ((var1 = this.bluetoothHeadset.getConnectedDevices()).isEmpty()) {
                this.bluetoothDevice = null;
                this.bluetoothState = State.HEADSET_UNAVAILABLE;
            } else {
                this.bluetoothDevice = (BluetoothDevice)var1.get(0);
                this.bluetoothState = State.HEADSET_AVAILABLE;
                (new StringBuilder("Connected bluetooth headset: name=")).append(this.bluetoothDevice.getName()).append(", state=").append(this.stateToString(this.bluetoothHeadset.getConnectionState(this.bluetoothDevice))).append(", SCO audio=").append(this.bluetoothHeadset.isAudioConnected(this.bluetoothDevice));
            }

            (new StringBuilder("updateDevice done: BT state=")).append(this.bluetoothState);
        }
    }

    protected AudioManager getAudioManager(Context var1) {
        return (AudioManager)var1.getSystemService(Context.AUDIO_SERVICE);
    }

    protected void registerReceiver(BroadcastReceiver var1, IntentFilter var2) {
        this.apprtcContext.registerReceiver(var1, var2);
    }

    protected void unregisterReceiver(BroadcastReceiver var1) {
        try {
            this.apprtcContext.unregisterReceiver(var1);
        } catch (Exception var2) {
        }
    }

    protected boolean getBluetoothProfileProxy(Context var1, ServiceListener var2, int var3) {
        return this.bluetoothAdapter.getProfileProxy(var1, var2, var3);
    }

    protected boolean hasPermission(Context var1, String var2) {
        return this.apprtcContext.checkPermission(var2, Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint({"HardwareIds"})
    protected void logBluetoothAdapterInfo(BluetoothAdapter var1) {
        (new StringBuilder("BluetoothAdapter: enabled=")).append(var1.isEnabled()).append(", state=").append(this.stateToString(var1.getState())).append(", name=").append(var1.getName()).append(", address=").append(var1.getAddress());
        Set var3;
        if (!(var3 = var1.getBondedDevices()).isEmpty()) {
            Iterator var4 = var3.iterator();

            while(var4.hasNext()) {
                BluetoothDevice var2 = (BluetoothDevice)var4.next();
                (new StringBuilder(" name=")).append(var2.getName()).append(", address=").append(var2.getAddress());
            }
        }

    }

    private void updateAudioDeviceState() {
        this.apprtcAudioManager.updateAudioDeviceState();
    }

    private void startTimer() {
        this.handler.postDelayed(this.bluetoothTimeoutRunnable, 4000L);
    }

    private void cancelTimer() {
        this.handler.removeCallbacks(this.bluetoothTimeoutRunnable);
    }

    private void bluetoothTimeout() {
        if (this.bluetoothState != State.UNINITIALIZED && this.bluetoothHeadset != null) {
            (new StringBuilder("bluetoothTimeout: BT state=")).append(this.bluetoothState).append(", attempts: ").append(this.scoConnectionAttempts).append(", SCO is on: ").append(this.isScoOn());
            if (this.bluetoothState == State.SCO_CONNECTING) {
                boolean var1 = false;
                List var2;
                if ((var2 = this.bluetoothHeadset.getConnectedDevices()).size() > 0) {
                    this.bluetoothDevice = (BluetoothDevice)var2.get(0);
                    if (this.bluetoothHeadset.isAudioConnected(this.bluetoothDevice)) {
                        (new StringBuilder("SCO connected with ")).append(this.bluetoothDevice.getName());
                        var1 = true;
                    } else {
                        (new StringBuilder("SCO is not connected with ")).append(this.bluetoothDevice.getName());
                    }
                }

                if (var1) {
                    this.bluetoothState = State.SCO_CONNECTED;
                    this.scoConnectionAttempts = 0;
                } else {
                    this.stopScoAudio();
                }

                this.updateAudioDeviceState();
                (new StringBuilder("bluetoothTimeout done: BT state=")).append(this.bluetoothState);
            }
        }
    }

    private boolean isScoOn() {
        return this.audioManager.isBluetoothScoOn();
    }

    private String stateToString(int var1) {
        switch(var1) {
            case 0:
                return "DISCONNECTED";
            case 1:
                return "CONNECTING";
            case 2:
                return "CONNECTED";
            case 3:
                return "DISCONNECTING";
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            default:
                return "INVALID";
            case 10:
                return "OFF";
            case 11:
                return "TURNING_ON";
            case 12:
                return "ON";
            case 13:
                return "TURNING_OFF";
        }
    }

    private class BluetoothHeadsetBroadcastReceiver extends BroadcastReceiver {
        private BluetoothHeadsetBroadcastReceiver() {
        }

        public void onReceive(Context var1, Intent var2) {
            if (RtcBluetoothManager.this.bluetoothState != State.UNINITIALIZED) {
                label43: {
                    String var3;
                    int var4;
                    if ((var3 = var2.getAction()).equals("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED")) {
                        var4 = var2.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
                        (new StringBuilder("BluetoothHeadsetBroadcastReceiver.onReceive: a=ACTION_CONNECTION_STATE_CHANGED, s=")).append(RtcBluetoothManager.this.stateToString(var4)).append(", sb=").append(this.isInitialStickyBroadcast()).append(", BT state: ").append(RtcBluetoothManager.this.bluetoothState);
                        if (var4 != 2) {
                            if (var4 != 1 && var4 != 3 && var4 == 0) {
                                RtcBluetoothManager.this.stopScoAudio();
                                RtcBluetoothManager.this.updateAudioDeviceState();
                            }
                            break label43;
                        }

                        RtcBluetoothManager.this.scoConnectionAttempts = 0;
                    } else {
                        if (!var3.equals("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED")) {
                            break label43;
                        }

                        var4 = var2.getIntExtra("android.bluetooth.profile.extra.STATE", 10);
                        (new StringBuilder("BluetoothHeadsetBroadcastReceiver.onReceive: a=ACTION_AUDIO_STATE_CHANGED, s=")).append(RtcBluetoothManager.this.stateToString(var4)).append(", sb=").append(this.isInitialStickyBroadcast()).append(", BT state: ").append(RtcBluetoothManager.this.bluetoothState);
                        if (var4 == 12) {
                            RtcBluetoothManager.this.cancelTimer();
                            if (RtcBluetoothManager.this.bluetoothState != State.SCO_CONNECTING) {
                                break label43;
                            }

                            RtcBluetoothManager.this.bluetoothState = State.SCO_CONNECTED;
                            RtcBluetoothManager.this.scoConnectionAttempts = 0;
                        } else {
                            if (var4 == 11 || var4 != 10) {
                                break label43;
                            }

                            if (this.isInitialStickyBroadcast()) {
                                return;
                            }
                        }
                    }

                    RtcBluetoothManager.this.updateAudioDeviceState();
                }

                (new StringBuilder("onReceive done: BT state=")).append(RtcBluetoothManager.this.bluetoothState);
            }
        }
    }

    private class BluetoothServiceListener implements ServiceListener {
        private BluetoothServiceListener() {
        }

        public void onServiceConnected(int var1, BluetoothProfile var2) {
            if (var1 == 1 && RtcBluetoothManager.this.bluetoothState != State.UNINITIALIZED) {
                (new StringBuilder("BluetoothServiceListener.onServiceConnected: BT state=")).append(RtcBluetoothManager.this.bluetoothState);
                RtcBluetoothManager.this.bluetoothHeadset = (BluetoothHeadset)var2;
                RtcBluetoothManager.this.updateAudioDeviceState();
                (new StringBuilder("onServiceConnected done: BT state=")).append(RtcBluetoothManager.this.bluetoothState);
            }
        }

        public void onServiceDisconnected(int var1) {
            if (var1 == 1 && RtcBluetoothManager.this.bluetoothState != State.UNINITIALIZED) {
                (new StringBuilder("BluetoothServiceListener.onServiceDisconnected: BT state=")).append(RtcBluetoothManager.this.bluetoothState);
                RtcBluetoothManager.this.stopScoAudio();
                RtcBluetoothManager.this.bluetoothHeadset = null;
                RtcBluetoothManager.this.bluetoothDevice = null;
                RtcBluetoothManager.this.bluetoothState = State.HEADSET_UNAVAILABLE;
                RtcBluetoothManager.this.updateAudioDeviceState();
                (new StringBuilder("onServiceDisconnected done: BT state=")).append(RtcBluetoothManager.this.bluetoothState);
            }
        }
    }

    public static enum State {
        UNINITIALIZED,
        ERROR,
        HEADSET_UNAVAILABLE,
        HEADSET_AVAILABLE,
        SCO_DISCONNECTING,
        SCO_CONNECTING,
        SCO_CONNECTED;

        private State() {
        }
    }
}

