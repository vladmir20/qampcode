/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 3:15 PM
 *
 */

package com.qamp.app.qampcallss.api;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RtcAudioManager implements ProximityReceiver.ProximityListener {
    private static final String TAG = "RtcAudioManager";
    private AudioManagerState amState;
    private final Context apprtcContext;
    private Set<MesiboCall.AudioDevice> audioDevices = new HashSet();
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener;
    private AudioManager audioManager;
    private final RtcBluetoothManager bluetoothManager;
    private MesiboCall.AudioDevice defaultAudioDevice = MesiboCall.AudioDevice.EARPIECE;
    /* access modifiers changed from: private */
    public boolean hasWiredHeadset = false;
    private boolean mDisableSpeakerOnProximity = true;
    private AudioManagerListener mListener;
    private boolean proximityDeviceChanged = false;
    private int savedAudioMode = -2;
    private boolean savedIsMicrophoneMute = false;
    private boolean savedIsSpeakerPhoneOn = false;
    private MesiboCall.AudioDevice selectedAudioDevice = MesiboCall.AudioDevice.DEFAULT;
    private MesiboCall.AudioDevice userSelectedAudioDevice = MesiboCall.AudioDevice.DEFAULT;
    private BroadcastReceiver wiredHeadsetReceiver;

    public void selectAudioDevice(MesiboCall.AudioDevice speaker) {
        if (speaker == MesiboCall.AudioDevice.DEFAULT || !this.audioDevices.contains(speaker)) {
            speaker = MesiboCall.AudioDevice.DEFAULT;
        }
        this.userSelectedAudioDevice = speaker;
        updateAudioDeviceState();
    }

    public interface AudioManagerListener {
        void onAudioDeviceChanged(MesiboCall.AudioDevice audioDevice, Set<MesiboCall.AudioDevice> set);
    }

    public enum AudioManagerState {
        UNINITIALIZED,
        PREINITIALIZED,
        RUNNING
    }

    private class WiredHeadsetReceiver extends BroadcastReceiver {
        private static final int HAS_MIC = 1;
        private static final int HAS_NO_MIC = 0;
        private static final int STATE_PLUGGED = 1;
        private static final int STATE_UNPLUGGED = 0;

        private WiredHeadsetReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            int intExtra = intent.getIntExtra("state", 0);
            new StringBuilder("WiredHeadsetReceiver.onReceive").append(Utils.getThreadInfo()).append(": a=").append(intent.getAction()).append(", s=").append(intExtra == 0 ? "unplugged" : "plugged").append(", m=").append(intent.getIntExtra("microphone", 0) == 1 ? "mic" : "no mic").append(", n=").append(intent.getStringExtra("name")).append(", sb=").append(isInitialStickyBroadcast());
            boolean unused = RtcAudioManager.this.hasWiredHeadset = intExtra == 1;
            RtcAudioManager.this.updateAudioDeviceState();
        }
    }

    private RtcAudioManager(Context context, boolean z, boolean z2) {
        this.mDisableSpeakerOnProximity = z2;
        this.apprtcContext = context;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.bluetoothManager = RtcBluetoothManager.create(context, this);
        this.wiredHeadsetReceiver = new WiredHeadsetReceiver();
        this.amState = AudioManagerState.UNINITIALIZED;
        this.defaultAudioDevice = z ? MesiboCall.AudioDevice.SPEAKER : MesiboCall.AudioDevice.EARPIECE;
        ProximityReceiver.addListener(this);
    }

    static RtcAudioManager create(Context context, boolean z, boolean z2) {
        return new RtcAudioManager(context, z, z2);
    }

    private boolean hasEarpiece() {
        return this.apprtcContext.getPackageManager().hasSystemFeature("android.hardware.telephony");
    }

    @Deprecated
    private boolean hasWiredHeadset() {
        if (Build.VERSION.SDK_INT < 23) {
            return this.audioManager.isWiredHeadsetOn();
        }
        for (AudioDeviceInfo type : this.audioManager.getDevices(3)) {
            int type2 = type.getType();
            if (type2 == 3 || type2 == 11) {
                return true;
            }
        }
        return false;
    }

    private void registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        this.apprtcContext.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setAudioDeviceInternal(MesiboCall.AudioDevice audioDevice) {
        new StringBuilder("setAudioDeviceInternal(device=").append(audioDevice).append(")");
        if (this.audioDevices.contains(audioDevice)) {
            switch (audioDevice) {
                case SPEAKER:
                    setSpeakerphoneOn(true);
                    break;
                case EARPIECE:
                    setSpeakerphoneOn(false);
                    break;
                case HEADSET:
                    setSpeakerphoneOn(false);
                    break;
                case BLUETOOTH:
                    setSpeakerphoneOn(false);
                    break;
            }
            this.selectedAudioDevice = audioDevice;
        }
    }

    private void setMicrophoneMute(boolean z) {
        if (this.audioManager.isMicrophoneMute() != z) {
            this.audioManager.setMicrophoneMute(z);
        }
    }

    private void setSpeakerphoneOn(boolean z) {
        try {
            if (this.audioManager.isSpeakerphoneOn() != z) {
                this.audioManager.setSpeakerphoneOn(z);
            }
        } catch (Exception e) {
        }
    }

    private void unregisterReceiver(BroadcastReceiver broadcastReceiver) {
        try {
            this.apprtcContext.unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
        }
    }

    public Set<MesiboCall.AudioDevice> getAudioDevices() {
        return Collections.unmodifiableSet(new HashSet(this.audioDevices));
    }

    public MesiboCall.AudioDevice getSelectedAudioDevice() {
        return this.selectedAudioDevice;
    }

    public void onProximity(boolean z) {
        if (CallManager.getInstance().isAudioCallInProgress() && this.mDisableSpeakerOnProximity && this.selectedAudioDevice == MesiboCall.AudioDevice.SPEAKER && this.audioDevices.size() == 2 && this.audioDevices.contains(MesiboCall.AudioDevice.EARPIECE) && this.audioDevices.contains(MesiboCall.AudioDevice.SPEAKER)) {
            if (z) {
                selectAudioDevice(MesiboCall.AudioDevice.EARPIECE);
                this.proximityDeviceChanged = true;
                return;
            }
            selectAudioDevice(MesiboCall.AudioDevice.SPEAKER);
        }
    }

    /**public void selectAudioDevice(MesiboCall.AudioDevice audioDevice) {
        if (audioDevice == MesiboCall.AudioDevice.DEFAULT || !this.audioDevices.contains(audioDevice)) {
            audioDevice = MesiboCall.AudioDevice.DEFAULT;
        }
        this.userSelectedAudioDevice = audioDevice;
        updateAudioDeviceState();
    }*/

    public void selectSpeaker(boolean z) {
        selectAudioDevice(z ? MesiboCall.AudioDevice.SPEAKER : MesiboCall.AudioDevice.DEFAULT);
    }

    public boolean setDefaultAudioDevice(MesiboCall.AudioDevice audioDevice) {
        if (audioDevice != MesiboCall.AudioDevice.EARPIECE || !hasEarpiece()) {
            this.defaultAudioDevice = MesiboCall.AudioDevice.SPEAKER;
        } else {
            this.defaultAudioDevice = MesiboCall.AudioDevice.EARPIECE;
        }
        updateAudioDeviceState();
        return true;
    }

    public void start(AudioManagerListener audioManagerListener) {
        if (this.amState != AudioManagerState.RUNNING) {
            this.mListener = audioManagerListener;
            this.amState = AudioManagerState.RUNNING;
            this.savedAudioMode = this.audioManager.getMode();
            this.savedIsSpeakerPhoneOn = this.audioManager.isSpeakerphoneOn();
            this.savedIsMicrophoneMute = this.audioManager.isMicrophoneMute();
            this.hasWiredHeadset = hasWiredHeadset();
            this.audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int i) {
                }
            };
            this.audioManager.requestAudioFocus(this.audioFocusChangeListener, 0, 2);
            this.audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            setMicrophoneMute(false);
            this.userSelectedAudioDevice = MesiboCall.AudioDevice.DEFAULT;
            this.selectedAudioDevice = MesiboCall.AudioDevice.DEFAULT;
            this.audioDevices.clear();
            this.bluetoothManager.start();
            updateAudioDeviceState();
            registerReceiver(this.wiredHeadsetReceiver, new IntentFilter("android.intent.action.HEADSET_PLUG"));
        }
    }

    @SuppressLint("WrongConstant")
    public void stop() {
        if (this.amState != AudioManagerState.RUNNING) {
            new StringBuilder("Trying to stop AudioManager in incorrect state: ").append(this.amState);
            return;
        }
        this.amState = AudioManagerState.UNINITIALIZED;
        try {
            unregisterReceiver(this.wiredHeadsetReceiver);
        } catch (Exception e) {
        }
        this.bluetoothManager.stop();
        setSpeakerphoneOn(this.savedIsSpeakerPhoneOn);
        setMicrophoneMute(this.savedIsMicrophoneMute);
        this.audioManager.setMode(this.savedAudioMode);
        this.audioManager.abandonAudioFocus(this.audioFocusChangeListener);
        this.audioFocusChangeListener = null;
        ProximityReceiver.removeListener(this);
        this.mListener = null;
    }

    public void updateAudioDeviceState() {
        boolean z = false;
        new StringBuilder("--- updateAudioDeviceState: wired headset=").append(this.hasWiredHeadset).append(", BT state=").append(this.bluetoothManager.getState());
        new StringBuilder("Device status: available=").append(this.audioDevices).append(", selected=").append(this.selectedAudioDevice).append(", user selected=").append(this.userSelectedAudioDevice);
        if (this.bluetoothManager.getState() == RtcBluetoothManager.State.HEADSET_AVAILABLE || this.bluetoothManager.getState() == RtcBluetoothManager.State.HEADSET_UNAVAILABLE || this.bluetoothManager.getState() == RtcBluetoothManager.State.SCO_DISCONNECTING) {
            this.bluetoothManager.updateDevice();
        }
        HashSet hashSet = new HashSet();
        if (this.bluetoothManager.getState() == RtcBluetoothManager.State.SCO_CONNECTED || this.bluetoothManager.getState() == RtcBluetoothManager.State.SCO_CONNECTING || this.bluetoothManager.getState() == RtcBluetoothManager.State.HEADSET_AVAILABLE) {
            hashSet.add(MesiboCall.AudioDevice.BLUETOOTH);
        }
        hashSet.add(MesiboCall.AudioDevice.SPEAKER);
        if (this.hasWiredHeadset) {
            hashSet.add(MesiboCall.AudioDevice.HEADSET);
        } else if (hasEarpiece()) {
            hashSet.add(MesiboCall.AudioDevice.EARPIECE);
        }
        boolean z2 = !this.audioDevices.equals(hashSet);
        this.audioDevices = hashSet;
        if (this.bluetoothManager.getState() == RtcBluetoothManager.State.HEADSET_UNAVAILABLE && this.userSelectedAudioDevice == MesiboCall.AudioDevice.BLUETOOTH) {
            this.userSelectedAudioDevice = MesiboCall.AudioDevice.DEFAULT;
        }
        if (!this.hasWiredHeadset && this.userSelectedAudioDevice == MesiboCall.AudioDevice.HEADSET) {
            this.userSelectedAudioDevice = MesiboCall.AudioDevice.SPEAKER;
        }
        boolean z3 = this.bluetoothManager.getState() == RtcBluetoothManager.State.HEADSET_AVAILABLE && (this.userSelectedAudioDevice == MesiboCall.AudioDevice.DEFAULT || this.userSelectedAudioDevice == MesiboCall.AudioDevice.BLUETOOTH);
        if (!((this.bluetoothManager.getState() != RtcBluetoothManager.State.SCO_CONNECTED && this.bluetoothManager.getState() != RtcBluetoothManager.State.SCO_CONNECTING) || this.userSelectedAudioDevice == MesiboCall.AudioDevice.DEFAULT || this.userSelectedAudioDevice == MesiboCall.AudioDevice.BLUETOOTH)) {
            z = true;
        }
        if (this.bluetoothManager.getState() == RtcBluetoothManager.State.HEADSET_AVAILABLE || this.bluetoothManager.getState() == RtcBluetoothManager.State.SCO_CONNECTING || this.bluetoothManager.getState() == RtcBluetoothManager.State.SCO_CONNECTED) {
            new StringBuilder("Need BT audio: start=").append(z3).append(", stop=").append(z).append(", BT state=").append(this.bluetoothManager.getState());
        }
        if (z) {
            this.bluetoothManager.stopScoAudio();
            this.bluetoothManager.updateDevice();
        }
        if (z3 && !z && !this.bluetoothManager.startScoAudio()) {
            this.audioDevices.remove(MesiboCall.AudioDevice.BLUETOOTH);
            z2 = true;
        }
        MesiboCall.AudioDevice audioDevice = MesiboCall.AudioDevice.SPEAKER == this.userSelectedAudioDevice ? MesiboCall.AudioDevice.SPEAKER : this.bluetoothManager.getState() == RtcBluetoothManager.State.SCO_CONNECTED ? MesiboCall.AudioDevice.BLUETOOTH : this.hasWiredHeadset ? MesiboCall.AudioDevice.HEADSET : !hasEarpiece() ? MesiboCall.AudioDevice.SPEAKER : MesiboCall.AudioDevice.DEFAULT == this.userSelectedAudioDevice ? this.defaultAudioDevice : this.userSelectedAudioDevice;
        if (audioDevice != this.selectedAudioDevice || z2) {
            setAudioDeviceInternal(audioDevice);
            new StringBuilder("New device status: available=").append(this.audioDevices).append(", selected=").append(audioDevice);
            if (this.mListener != null) {
                this.mListener.onAudioDeviceChanged(this.selectedAudioDevice, this.audioDevices);
            }
        }
    }
}
