package com.qamp.app.qampCalls;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Build.VERSION;

import com.qamp.app.Utilss;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RtcAudioManager implements ProximityReceiver.ProximityListener {
    private static final String TAG = "RtcAudioManager";
    private final Context apprtcContext;
    private AudioManager audioManager;
    private AudioManagerListener mListener;
    private AudioManagerState amState;
    private int savedAudioMode = -2;
    private boolean savedIsSpeakerPhoneOn = false;
    private boolean savedIsMicrophoneMute = false;
    private boolean hasWiredHeadset = false;
    private boolean proximityDeviceChanged = false;
    private MesiboCall.AudioDevice defaultAudioDevice;
    private MesiboCall.AudioDevice selectedAudioDevice;
    private MesiboCall.AudioDevice userSelectedAudioDevice;
    private final RtcBluetoothManager bluetoothManager;
    private Set<MesiboCall.AudioDevice> audioDevices;
    private BroadcastReceiver wiredHeadsetReceiver;
    private OnAudioFocusChangeListener audioFocusChangeListener;
    private boolean mDisableSpeakerOnProximity;

    public void onProximity(boolean var1) {
        if (CallManager.getInstance().isAudioCallInProgress() && this.mDisableSpeakerOnProximity) {
            if (this.selectedAudioDevice == MesiboCall.AudioDevice.SPEAKER) {
                if (this.audioDevices.size() == 2 && this.audioDevices.contains(MesiboCall.AudioDevice.EARPIECE) && this.audioDevices.contains(MesiboCall.AudioDevice.SPEAKER)) {
                    if (var1) {
                        this.selectAudioDevice(MesiboCall.AudioDevice.EARPIECE);
                        this.proximityDeviceChanged = true;
                        return;
                    }

                    this.selectAudioDevice(MesiboCall.AudioDevice.SPEAKER);
                }

            }
        }
    }

    static RtcAudioManager create(Context var0, boolean var1, boolean var2) {
        return new RtcAudioManager(var0, var1, var2);
    }

    private RtcAudioManager(Context var1, boolean var2, boolean var3) {
        this.defaultAudioDevice = MesiboCall.AudioDevice.EARPIECE;
        this.selectedAudioDevice = MesiboCall.AudioDevice.DEFAULT;
        this.userSelectedAudioDevice = MesiboCall.AudioDevice.DEFAULT;
        this.audioDevices = new HashSet();
        this.mDisableSpeakerOnProximity = true;
        this.mDisableSpeakerOnProximity = var3;
        this.apprtcContext = var1;
        this.audioManager = (AudioManager)var1.getSystemService(Context.AUDIO_SERVICE);
        this.bluetoothManager = RtcBluetoothManager.create(var1, this);
        this.wiredHeadsetReceiver = new WiredHeadsetReceiver();
        this.amState = AudioManagerState.UNINITIALIZED;
        this.defaultAudioDevice = var2 ? MesiboCall.AudioDevice.SPEAKER : MesiboCall.AudioDevice.EARPIECE;
        ProximityReceiver.addListener(this);
    }

    @SuppressLint("WrongConstant")
    public void start(AudioManagerListener var1) {
        if (this.amState != AudioManagerState.RUNNING) {
            this.mListener = var1;
            this.amState = AudioManagerState.RUNNING;
            this.savedAudioMode = this.audioManager.getMode();
            this.savedIsSpeakerPhoneOn = this.audioManager.isSpeakerphoneOn();
            this.savedIsMicrophoneMute = this.audioManager.isMicrophoneMute();
            this.hasWiredHeadset = this.hasWiredHeadset();
            this.audioFocusChangeListener = new OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int var1) {
                }
            };
            this.audioManager.requestAudioFocus(this.audioFocusChangeListener, 0, 2);
            this.audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            this.setMicrophoneMute(false);
            this.userSelectedAudioDevice = MesiboCall.AudioDevice.DEFAULT;
            this.selectedAudioDevice = MesiboCall.AudioDevice.DEFAULT;
            this.audioDevices.clear();
            this.bluetoothManager.start();
            this.updateAudioDeviceState();
            this.registerReceiver(this.wiredHeadsetReceiver, new IntentFilter("android.intent.action.HEADSET_PLUG"));
        }
    }

    @SuppressLint("WrongConstant")
    public void stop() {
        if (this.amState != AudioManagerState.RUNNING) {
            (new StringBuilder("Trying to stop AudioManager in incorrect state: ")).append(this.amState);
        } else {
            this.amState = AudioManagerState.UNINITIALIZED;

            try {
                this.unregisterReceiver(this.wiredHeadsetReceiver);
            } catch (Exception var1) {
            }

            this.bluetoothManager.stop();
            this.setSpeakerphoneOn(this.savedIsSpeakerPhoneOn);
            this.setMicrophoneMute(this.savedIsMicrophoneMute);
            this.audioManager.setMode(this.savedAudioMode);
            this.audioManager.abandonAudioFocus(this.audioFocusChangeListener);
            this.audioFocusChangeListener = null;
            ProximityReceiver.removeListener(this);
            this.mListener = null;
        }
    }

    private void setAudioDeviceInternal(MesiboCall.AudioDevice var1) {
        (new StringBuilder("setAudioDeviceInternal(device=")).append(var1).append(")");
        Utilss.assertIsTrue(this.audioDevices.contains(var1));
        switch(var1) {
            case SPEAKER:
                this.setSpeakerphoneOn(true);
                break;
            case EARPIECE:
                this.setSpeakerphoneOn(false);
                break;
            case HEADSET:
                this.setSpeakerphoneOn(false);
                break;
            case BLUETOOTH:
                this.setSpeakerphoneOn(false);
        }

        this.selectedAudioDevice = var1;
    }

    public boolean setDefaultAudioDevice(MesiboCall.AudioDevice var1) {
        if (var1 == MesiboCall.AudioDevice.EARPIECE && this.hasEarpiece()) {
            this.defaultAudioDevice = MesiboCall.AudioDevice.EARPIECE;
        } else {
            this.defaultAudioDevice = MesiboCall.AudioDevice.SPEAKER;
        }

        this.updateAudioDeviceState();
        return true;
    }

    public void selectAudioDevice(MesiboCall.AudioDevice var1) {
        if (var1 == MesiboCall.AudioDevice.DEFAULT || !this.audioDevices.contains(var1)) {
            var1 = MesiboCall.AudioDevice.DEFAULT;
        }

        this.userSelectedAudioDevice = var1;
        this.updateAudioDeviceState();
    }

    public void selectSpeaker(boolean var1) {
        this.selectAudioDevice(var1 ? MesiboCall.AudioDevice.SPEAKER : MesiboCall.AudioDevice.DEFAULT);
    }

    public Set<MesiboCall.AudioDevice> getAudioDevices() {
        return Collections.unmodifiableSet(new HashSet(this.audioDevices));
    }

    public MesiboCall.AudioDevice getSelectedAudioDevice() {
        return this.selectedAudioDevice;
    }

    private void registerReceiver(BroadcastReceiver var1, IntentFilter var2) {
        this.apprtcContext.registerReceiver(var1, var2);
    }

    private void unregisterReceiver(BroadcastReceiver var1) {
        try {
            this.apprtcContext.unregisterReceiver(var1);
        } catch (Exception var2) {
        }
    }

    private void setSpeakerphoneOn(boolean var1) {
        try {
            if (this.audioManager.isSpeakerphoneOn() != var1) {
                this.audioManager.setSpeakerphoneOn(var1);
            }
        } catch (Exception var2) {
        }
    }

    private void setMicrophoneMute(boolean var1) {
        if (this.audioManager.isMicrophoneMute() != var1) {
            this.audioManager.setMicrophoneMute(var1);
        }
    }

    private boolean hasEarpiece() {
        return this.apprtcContext.getPackageManager().hasSystemFeature("android.hardware.telephony");
    }

    @Deprecated
    private boolean hasWiredHeadset() {
        if (VERSION.SDK_INT < 23) {
            return this.audioManager.isWiredHeadsetOn();
        } else {
            AudioDeviceInfo[] var1;
            int var2 = (var1 = this.audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS)).length;

            for(int var3 = 0; var3 < var2; ++var3) {
                int var4;
                if ((var4 = var1[var3].getType()) == 3) {
                    return true;
                }

                if (var4 == 11) {
                    return true;
                }
            }

            return false;
        }
    }

    public void updateAudioDeviceState() {
        (new StringBuilder("--- updateAudioDeviceState: wired headset=")).append(this.hasWiredHeadset).append(", BT state=").append(this.bluetoothManager.getState());
        (new StringBuilder("Device status: available=")).append(this.audioDevices).append(", selected=").append(this.selectedAudioDevice).append(", user selected=").append(this.userSelectedAudioDevice);
        if (this.bluetoothManager.getState() == RtcBluetoothManager.State.HEADSET_AVAILABLE || this.bluetoothManager.getState() == RtcBluetoothManager.State.HEADSET_UNAVAILABLE || this.bluetoothManager.getState() == RtcBluetoothManager.State.SCO_DISCONNECTING) {
            this.bluetoothManager.updateDevice();
        }

        HashSet var1 = new HashSet();
        if (this.bluetoothManager.getState() == RtcBluetoothManager.State.SCO_CONNECTED || this.bluetoothManager.getState() == RtcBluetoothManager.State.SCO_CONNECTING || this.bluetoothManager.getState() == RtcBluetoothManager.State.HEADSET_AVAILABLE) {
            var1.add(MesiboCall.AudioDevice.BLUETOOTH);
        }

        var1.add(MesiboCall.AudioDevice.SPEAKER);
        if (this.hasWiredHeadset) {
            var1.add(MesiboCall.AudioDevice.HEADSET);
        } else if (this.hasEarpiece()) {
            var1.add(MesiboCall.AudioDevice.EARPIECE);
        }

        boolean var2 = !this.audioDevices.equals(var1);
        this.audioDevices = var1;
        if (this.bluetoothManager.getState() == RtcBluetoothManager.State.HEADSET_UNAVAILABLE && this.userSelectedAudioDevice == MesiboCall.AudioDevice.BLUETOOTH) {
            this.userSelectedAudioDevice = MesiboCall.AudioDevice.DEFAULT;
        }

        if (!this.hasWiredHeadset && this.userSelectedAudioDevice == MesiboCall.AudioDevice.HEADSET) {
            this.userSelectedAudioDevice = MesiboCall.AudioDevice.SPEAKER;
        }

        boolean var4 = this.bluetoothManager.getState() == RtcBluetoothManager.State.HEADSET_AVAILABLE && (this.userSelectedAudioDevice == MesiboCall.AudioDevice.DEFAULT || this.userSelectedAudioDevice == MesiboCall.AudioDevice.BLUETOOTH);
        boolean var3 = (this.bluetoothManager.getState() == RtcBluetoothManager.State.SCO_CONNECTED || this.bluetoothManager.getState() == RtcBluetoothManager.State.SCO_CONNECTING) && this.userSelectedAudioDevice != MesiboCall.AudioDevice.DEFAULT && this.userSelectedAudioDevice != MesiboCall.AudioDevice.BLUETOOTH;
        if (this.bluetoothManager.getState() == RtcBluetoothManager.State.HEADSET_AVAILABLE || this.bluetoothManager.getState() == RtcBluetoothManager.State.SCO_CONNECTING || this.bluetoothManager.getState() == RtcBluetoothManager.State.SCO_CONNECTED) {
            (new StringBuilder("Need BT audio: start=")).append(var4).append(", stop=").append(var3).append(", BT state=").append(this.bluetoothManager.getState());
        }

        if (var3) {
            this.bluetoothManager.stopScoAudio();
            this.bluetoothManager.updateDevice();
        }

        if (var4 && !var3 && !this.bluetoothManager.startScoAudio()) {
            this.audioDevices.remove(MesiboCall.AudioDevice.BLUETOOTH);
            var2 = true;
        }

        MesiboCall.AudioDevice var5;
        if (MesiboCall.AudioDevice.SPEAKER == this.userSelectedAudioDevice) {
            var5 = MesiboCall.AudioDevice.SPEAKER;
        } else if (this.bluetoothManager.getState() == RtcBluetoothManager.State.SCO_CONNECTED) {
            var5 = MesiboCall.AudioDevice.BLUETOOTH;
        } else if (this.hasWiredHeadset) {
            var5 = MesiboCall.AudioDevice.HEADSET;
        } else if (!this.hasEarpiece()) {
            var5 = MesiboCall.AudioDevice.SPEAKER;
        } else if (MesiboCall.AudioDevice.DEFAULT == this.userSelectedAudioDevice) {
            var5 = this.defaultAudioDevice;
        } else {
            var5 = this.userSelectedAudioDevice;
        }

        if (var5 != this.selectedAudioDevice || var2) {
            this.setAudioDeviceInternal(var5);
            (new StringBuilder("New device status: available=")).append(this.audioDevices).append(", selected=").append(var5);
            if (this.mListener != null) {
                this.mListener.onAudioDeviceChanged(this.selectedAudioDevice, this.audioDevices);
            }
        }

    }

    private class WiredHeadsetReceiver extends BroadcastReceiver {
        private static final int STATE_UNPLUGGED = 0;
        private static final int STATE_PLUGGED = 1;
        private static final int HAS_NO_MIC = 0;
        private static final int HAS_MIC = 1;

        private WiredHeadsetReceiver() {
        }

        public void onReceive(Context var1, Intent var2) {
            int var5 = var2.getIntExtra("state", 0);
            int var3 = var2.getIntExtra("microphone", 0);
            String var4 = var2.getStringExtra("name");
            (new StringBuilder("WiredHeadsetReceiver.onReceive")).append(Utilss.getThreadInfo()).append(": a=").append(var2.getAction()).append(", s=").append(var5 == 0 ? "unplugged" : "plugged").append(", m=").append(var3 == 1 ? "mic" : "no mic").append(", n=").append(var4).append(", sb=").append(this.isInitialStickyBroadcast());
            RtcAudioManager.this.hasWiredHeadset = var5 == 1;
            RtcAudioManager.this.updateAudioDeviceState();
        }
    }

    public interface AudioManagerListener {
        void onAudioDeviceChanged(MesiboCall.AudioDevice var1, Set<MesiboCall.AudioDevice> var2);
    }

    public static enum AudioManagerState {
        UNINITIALIZED,
        PREINITIALIZED,
        RUNNING;

        private AudioManagerState() {
        }
    }
}

