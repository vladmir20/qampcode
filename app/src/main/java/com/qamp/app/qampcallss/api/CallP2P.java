/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:31 PM
 *
 */

package com.qamp.app.qampcallss.api;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.qampcallss.api.MesiboCall.AudioDevice;
import com.qamp.app.qampcallss.api.p000ui.QampCallsActivity;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Set;

public class CallP2P implements Mesibo.CallListener, CallPrivate, RtcCall.Listener {
    protected static final int CALLINFO_LANDSCAPE = 4;
    protected static final int CALLINFO_LOWBATTERY = 4096;
    protected static final int CALLINFO_MIRRORED = 8;
    protected static final int CALLINFO_REARCAMERA = 1;
    protected static final int CALLINFO_SCREEN = 2;
    protected static final int CALLINFO_STREAMCHANGING = 256;
    protected static final int CALLINFO_VIDEOREQUEST = 512;
    protected static final int RTCACTION_ANSWER = 2;
    protected static final int RTCACTION_CREATESTREAMS = 0;
    protected static final int RTCACTION_MUTE = 7;
    protected static final int RTCACTION_OFFER = 1;
    protected static final int RTCACTION_PEERCONNECTION = 4;
    protected static final int RTCACTION_SDP = 3;
    protected static final int RTCACTION_STREAM = 5;
    protected static final int RTCACTION_VIDEOCAPTURER = 6;
    protected static final int RTCFLAG_AUDIO = 1;
    protected static final int RTCFLAG_AUDIOMUTE = 16;
    protected static final int RTCFLAG_AUDIOUNMUTE = 32;
    protected static final int RTCFLAG_USETURN = 8;
    protected static final int RTCFLAG_VIDEO = 2;
    protected static final int RTCFLAG_VIDEOHWACCL = 4;
    protected static final int RTCFLAG_VIDEOMUTE = 64;
    protected static final int RTCFLAG_VIDEOUNMUTE = 128;
    protected static final int RTCSTATUS_CLEANUP = 16;
    protected static final int RTCSTATUS_CONNECTED = 2;
    protected static final int RTCSTATUS_ENDED = 4;
    protected static final int RTCSTATUS_INPROGRESS = 1;
    protected static final int RTCSTATUS_RECONNECTING = 3;
    public static final String TAG = "CallP2P";
    private boolean cleanedUp = false;
    private boolean hangedUp = false;
    private AudioDevice mActiveAudioDevice = AudioDevice.DEFAULT;
    private Set<AudioDevice> mAvailableAudioDevices = null;
    private boolean mBatteryLow = false;
    private CallContext mCallCtx = null;
    private boolean mConnectedFirstTime = false;
    /* access modifiers changed from: private */
    public MesiboCall.CallProperties mCp = null;
    private MesiboCall.InProgressListener mDefaultListener = (MesiboCall.InProgressListener) CallManager.getInstance().getDummyListener();
    private boolean mDetached = true;
    private int mHangupReason = 3;
    private boolean mIceServersSet = false;
    private boolean mLandscape = false;
    private int mLastHeight = 0;
    private long mLastInfo = 0;
    private boolean mLastRemoteMirror = false;
    private int mLastWidth = 0;
    private WeakReference<MesiboCall.InProgressListener> mListener = null;
    private MesiboVideoView mLocalVideoView = null;
    private boolean mMutedDueToBackground = false;
    private int mPendingRtcMuteFlag = 0;
    private boolean mRemoteLandscape = false;
    private boolean mRemoteLowBattery = false;
    private MesiboVideoView mRemoteVideoView = null;
    private MediaPlayer mRingPlayer = null;
    private RtcCall mRtcCall = null;
    private boolean mStarted = false;
    private Utils.PowerAndWifiLock mWakeLock = null;
    private RtcAudioManager m_am = null;
    private SetupCallViews setupViews = new SetupCallViews();

    public static class SetupCallViews {
        boolean firstTime = true;
        boolean hideControls = true;
        boolean showIncoming = true;
        boolean showVideoControls = true;
    }

    private void cleanup(boolean z) {
        if (!this.cleanedUp) {
            this.cleanedUp = true;
            Mesibo.removeListener(this);
            this.mCallCtx.inForeground = false;
            playInCallSound(0, false);
            CallManager.getInstance().stopIncomingNotification(this.mCp);
            CallManager.getInstance().clearNotification(this.mCp);
            this.mRtcCall.hangup();
            if (this.m_am != null) {
                this.m_am.stop();
                this.m_am = null;
            }
            if (z) {
                Mesibo.hangup(0);
            }
            getListener().MesiboCall_OnHangup(this.mCp, this.mHangupReason);
            CallManager.getInstance().removeP2PCall();
            Utils.releasePowerAndWifiLock(this.mWakeLock);
        }
    }

    private void createStreams(int i) {
        boolean z = true;
        boolean z2 = (i & 16) > 0;
        if ((i & RTCFLAG_VIDEOMUTE) <= 0) {
            z = false;
        }
        this.mRtcCall.createCall(this.mCp.activity, z2, z);
        isAnswered();
    }

    private void firstUiUpdate() {
        recommendViews();
        if (AudioDevice.DEFAULT != this.mActiveAudioDevice) {
            notifiAudioDeviceChanged(this.mActiveAudioDevice, AudioDevice.DEFAULT);
        }
        getListener().MesiboCall_OnStatus(this.mCp, this.mCallCtx.status, this.mCp.video.enabled);
        getListener().MesiboCall_OnMute(this.mCp, this.mCallCtx.remoteAudioMute, this.mCallCtx.remoteVideoMute, true);
        getListener().MesiboCall_OnMute(this.mCp, this.mCallCtx.audioMute, this.mCallCtx.videoMute, false);
    }

    /* access modifiers changed from: private */
    public MesiboCall.InProgressListener getListener() {
        if (this.mListener == null) {
            return this.mDefaultListener;
        }
        MesiboCall.InProgressListener inProgressListener = (MesiboCall.InProgressListener) this.mListener.get();
        return inProgressListener == null ? this.mDefaultListener : inProgressListener;
    }

    private void hangup(boolean z) {
        if (!this.hangedUp && z && (this.mCallCtx.status & RTCFLAG_VIDEOMUTE) == 0) {
            Mesibo.hangup(0);
            this.hangedUp = true;
        }
        this.mCallCtx.status = RTCFLAG_VIDEOMUTE;
        recommendViews();
        cleanup(false);
    }

    private void notifiAudioDeviceChanged(final AudioDevice audioDevice, final AudioDevice audioDevice2) {
        if (Mesibo.isUiThread()) {
            getListener().MesiboCall_OnAudioDeviceChanged(this.mCp, audioDevice, audioDevice2);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    CallP2P.this.getListener().MesiboCall_OnAudioDeviceChanged(CallP2P.this.mCp, audioDevice, audioDevice2);
                }
            });
        }
    }

    private void notifyMissedCall() {
        MesiboCall.IncomingListener listener = CallManager.getInstance().getListener();
        if (listener != null) {
            listener.MesiboCall_onNotify(4, this.mCp.user, this.mCp.video.enabled);
        }
    }

    /* access modifiers changed from: private */
    public void onAudioManagerDevicesChanged(AudioDevice audioDevice, Set<AudioDevice> set) {
        new StringBuilder("onAudioManagerDevicesChanged: ").append(set).append(", selected: ").append(audioDevice);
        this.mAvailableAudioDevices = set;
        if (this.mActiveAudioDevice != audioDevice) {
            notifiAudioDeviceChanged(audioDevice, this.mActiveAudioDevice);
            this.mActiveAudioDevice = audioDevice;
            setHideOnProximity();
        }
    }

    private void playInCallSound(int i, boolean z) {
        boolean z2 = false;
        if (z && this.mCallCtx.inCallSound) {
            getListener().MesiboCall_OnPlayInCallSound(this.mCp, 0, false);
            stopInCallSound();
        }
        if (z || this.mCallCtx.inCallSound) {
            this.mCallCtx.inCallSound = z;
            if (!getListener().MesiboCall_OnPlayInCallSound(this.mCp, i, z)) {
                int i2 = com.mesibo.calls.api.R.raw.mesibo_ring;
                if (i == 1) {
                    i2 = com.mesibo.calls.api.R.raw.mesibo_busy;
                } else {
                    z2 = true;
                }
                playInCallSound(CallManager.getAppContext(), i2, z2);
            }
        }
    }

    private void recommendViews() {
        boolean z = true;
        boolean z2 = this.mCp.incoming && !this.mCallCtx.answered;
        final boolean z3 = this.mCp.video.enabled && (!this.mCallCtx.answered || this.mCallCtx.answeredVideo);
        final boolean z4 = this.mCp.video.enabled && this.mCallCtx.answered && this.mCallCtx.p2pConnected && isCallInProgress();
        if (this.setupViews.firstTime) {
            this.setupViews.showIncoming = !z2;
            this.setupViews.showVideoControls = !z3;
            this.setupViews.hideControls = false;
        }
        if (!(z2 == this.setupViews.showIncoming && z3 == this.setupViews.showVideoControls)) {
            final int i = z2 ? 1 : 2;
            if (Mesibo.isUiThread()) {
                getListener().MesiboCall_OnUpdateUserInterface(this.mCp, i, z3, true);
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        CallP2P.this.getListener().MesiboCall_OnUpdateUserInterface(CallP2P.this.mCp, i, z3, true);
                    }
                });
            }
        }
        if (z4 != this.setupViews.hideControls) {
            if (Mesibo.isUiThread()) {
                MesiboCall.InProgressListener listener = getListener();
                MesiboCall.CallProperties callProperties = this.mCp;
                if (z4) {
                    z = false;
                }
                listener.MesiboCall_OnUpdateUserInterface(callProperties, 3, z3, z);
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        CallP2P.this.getListener().MesiboCall_OnUpdateUserInterface(CallP2P.this.mCp, 3, z3, !z4);
                    }
                });
            }
        }
        this.setupViews.showIncoming = z2;
        this.setupViews.hideControls = z4;
        this.setupViews.showVideoControls = z3;
        this.setupViews.firstTime = false;
    }

    private void setHideOnProximity() {
        if (this.mCp.hideOnProximity && this.mCp.activity != null && !isVideoCall()) {
            this.mCp.activity.setHideOnProximity(this.mActiveAudioDevice != AudioDevice.SPEAKER && this.mCallCtx.inForeground);
        }
    }

    private void setRemoteCallInfo(long j, long j2) {
        boolean z = false;
        if (this.mCp.video.enabled && this.mRemoteVideoView != null) {
            this.mRemoteVideoView.enableMirror((8 & j) > 0);
        }
        boolean z2 = (4 & j) > 0;
        if ((4096 & j) > 0) {
            z = true;
        }
        if (z2 != this.mRemoteLandscape) {
            getListener().MesiboCall_OnOrientationChanged(this.mCp, z2, true);
            this.mRemoteLandscape = z2;
        }
        if (z != this.mRemoteLowBattery) {
            getListener().MesiboCall_OnBatteryStatus(this.mCp, z, true);
        }
    }

    private void startAudioManager() {
        this.m_am = RtcAudioManager.create(CallManager.getAppContext(), this.mCallCtx.f0cp.audio.speaker, this.mCallCtx.f0cp.disableSpeakerOnProximity);
        this.m_am.setDefaultAudioDevice(this.mCp.audio.speaker ? AudioDevice.SPEAKER : AudioDevice.EARPIECE);
        this.m_am.start(new RtcAudioManager.AudioManagerListener() {
            public void onAudioDeviceChanged(AudioDevice audioDevice, Set<AudioDevice> set) {
                CallP2P.this.onAudioManagerDevicesChanged(audioDevice, set);
            }
        });
    }

    public void MesiboRtcCall_OnCaptureStarted(boolean z) {
        if (z) {
            getListener().MesiboCall_OnVideoSourceChanged(this.mCp, getVideoSource(), 0);
        }
    }

    public void MesiboRtcCall_OnIceStatus(boolean z) {
        Mesibo.setCallStatus(z ? 2 : 3, (String) null);
        this.mCallCtx.iceConnected = z;
    }

    public void MesiboRtcCall_OnRemoteMedia(boolean z) {
    }

    public void MesiboRtcCall_OnSendCandidate(IceCandidate iceCandidate) {
        SdpInterface.sendLocalIceCandidate(iceCandidate);
    }

    public void MesiboRtcCall_OnSendRemoveCandidate(IceCandidate[] iceCandidateArr) {
        SdpInterface.sendLocalIceCandidateRemovals(iceCandidateArr);
    }

    public void MesiboRtcCall_OnSendSdp(SessionDescription sessionDescription, boolean z) {
        SdpInterface.sendSdp(sessionDescription, z);
    }

    public boolean Mesibo_onCall(long j, long j2, MesiboProfile mesiboProfile, int i) {
        return false;
    }

    public void Mesibo_onCallServer(int i, String str, String str2, String str3) {
        if (1 != i) {
            return;
        }
        if (this.mCp.iceServers == null || this.mCp.iceServers.length <= 0) {
            this.mRtcCall.addTurnServer(str, str2, str3);
        } else if (!this.mIceServersSet) {
            this.mIceServersSet = true;
            for (MesiboCall.MesiboIceServer addTurnServer : this.mCp.iceServers) {
                this.mRtcCall.addTurnServer(addTurnServer);
            }
        }
    }

    public boolean Mesibo_onCallStatus(long j, long j2, int i, long j3, long j4, long j5, String str) {
        new StringBuilder("Mesibo_onCallStatus: status: ").append(i).append(" flags: ").append(j3);
        if ((1073741824 & j3) > 0) {
            onRtcStatus(j, j2, i, (int) j3, str);
            return true;
        }
        boolean z = (1 & j3) > 0;
        boolean z2 = (2 & j3) > 0;
        if (3 == i) {
            playInCallSound(0, true);
            sendCallInfo();
        } else if (5 == i || (i & RTCFLAG_VIDEOMUTE) > 0) {
            playInCallSound(0, false);
        }
        if ((i & RTCFLAG_VIDEOMUTE) > 0) {
            this.mHangupReason = 2;
            if (76 == i) {
                this.mHangupReason = 5;
            }
            if (!(i == RTCFLAG_VIDEOMUTE || i == 65 || this.mCallCtx.inForeground)) {
                playInCallSound(1, true);
            }
            if (this.mCp.incoming && 65 == i) {
                notifyMissedCall();
            }
        }
        switch (i) {
            case 5:
                this.mCallCtx.answered = true;
                this.mCallCtx.answeredVideo = z2;
                if (this.mCallCtx.p2pConnected) {
                    setAnswerTime();
                }
                if (!z2) {
                    this.mCallCtx.videoMute = true;
                    break;
                }
                break;
            case 9:
            case 10:
                if (z) {
                    this.mCallCtx.remoteAudioMute = i == 9;
                }
                if (z2) {
                    this.mCallCtx.remoteVideoMute = i == 9;
                }
                getListener().MesiboCall_OnMute(this.mCp, this.mCallCtx.remoteAudioMute, this.mCallCtx.remoteVideoMute, true);
                break;
            case 11:
                this.mCallCtx.hold = true;
                break;
            case 12:
                this.mCallCtx.hold = false;
                break;
            case 35:
                setRemoteCallInfo(j4, j5);
                break;
            case 48:
                this.mCallCtx.p2pConnected = true;
                if (this.mCallCtx.answered) {
                    setAnswerTime();
                    break;
                }
                break;
            case 50:
                this.mCallCtx.p2pConnected = false;
                if (this.mCallCtx.answered) {
                }
                break;
        }
        if (!(48 == i || 50 == i || 35 == i)) {
            this.mCallCtx.status = i;
        }
        if (48 == i && !this.mConnectedFirstTime) {
            if (!this.mCallCtx.answered) {
                return true;
            }
            this.mConnectedFirstTime = true;
        }
        getListener().MesiboCall_OnStatus(this.mCp, i, z2);
        if (5 == i && this.mCallCtx.p2pConnected && !this.mConnectedFirstTime) {
            this.mConnectedFirstTime = true;
            getListener().MesiboCall_OnStatus(this.mCp, 48, z2);
        }
        recommendViews();
        if ((i & RTCFLAG_VIDEOMUTE) > 0) {
            cleanup(false);
        }
        return true;
    }

    public void OnBackground() {
        MesiboCall.CallProperties callProperties = this.mCallCtx.f0cp;
        if (!callProperties.runInBackground) {
            this.mHangupReason = 4;
            hangup(true);
            return;
        }
        this.mRtcCall.OnForeground(false);
        this.mCallCtx.inForeground = false;
        if (this.mRtcCall != null && getVideoSource() != 4 && callProperties.stopVideoInBackground && this.mCp.video.enabled && !this.mCallCtx.videoMute) {
            mute(false, true, true);
            this.mCallCtx.videoMutedDueToBackground = true;
        }
    }

    public void OnForeground() {
        this.mRtcCall.OnForeground(true);
        this.mCallCtx.inForeground = true;
        if (this.mRtcCall != null && this.mCallCtx.videoMutedDueToBackground) {
            mute(false, true, false);
        }
        if (this.mPendingRtcMuteFlag > 0) {
            rtcMute(this.mPendingRtcMuteFlag);
        }
        setHideOnProximity();
    }

    public void answer() {
        answer(this.mCp.video.enabled);
    }

    public void answer(boolean z) {
        boolean z2 = true;
        if (!this.mCallCtx.answered && this.mCp.incoming) {
            this.mCallCtx.answered = true;
            this.mCallCtx.answeredVideo = z;
            if (this.mCp.video.enabled) {
                this.mCp.video.enabled = z;
            }
            CallManager.getInstance().stopIncomingNotification(this.mCp);
            CallManager.getInstance().clearNotification(this.mCp);
            startAudioManager();
            if (!this.mCp.video.enabled || !z) {
                z2 = false;
            }
            Mesibo.answer(z2);
            recommendViews();
        }
    }

    /* access modifiers changed from: package-private */
    public void batteryLevelChanged() {
        if (this.mBatteryLow) {
            sendCallInfo();
            this.mBatteryLow = false;
            getListener().MesiboCall_OnBatteryStatus(this.mCp, this.mBatteryLow, false);
            return;
        }
        if (!this.mBatteryLow && 100 <= this.mCp.batteryLowThreshold) {
            sendCallInfo();
            this.mBatteryLow = true;
        } else if (this.mBatteryLow && 100 > this.mCp.batteryLowThreshold) {
            sendCallInfo();
            this.mBatteryLow = false;
        }
        getListener().MesiboCall_OnBatteryStatus(this.mCp, this.mBatteryLow, false);
    }

    /* access modifiers changed from: package-private */
    public void cameraSourceChanged() {
        sendCallInfo();
        setupMirroring(false);
    }

    public void changeVideoFormat(int i, int i2, int i3) {
        if (this.mCp.video.enabled) {
            this.mRtcCall.changeVideoFormat(i, i2, i3);
        }
    }

    public void detach() {
        this.mDetached = true;
    }

    public AudioDevice getActiveAudioDevice() {
        return this.mActiveAudioDevice;
    }


    @Override
    public boolean toggleAudioDevice(AudioDevice audioDevice) {
        boolean z = audioDevice != this.mActiveAudioDevice;
        setAudioDevice(audioDevice, z);
        return z;
    }


    public long getAnswerTime() {
        return this.mCallCtx.answerTs;
    }

    public CallContext getCallContext() {
        return this.mCallCtx;
    }


    public MesiboCall.CallProperties getCallProperties() {
        return this.mCp;
    }



    public boolean getMuteStatus(boolean z, boolean z2, boolean z3) {
        return !z3 ? z ? this.mCallCtx.audioMute : this.mCallCtx.videoMute : z ? this.mCallCtx.remoteAudioMute : this.mCallCtx.remoteVideoMute;
    }

    public MesiboCall.VideoScalingType getVideoScalingType() {
        return null;
    }

    public int getVideoSource() {
        return this.mRtcCall.getVideoSource();
    }

    public MesiboVideoView getVideoView(boolean z) {
        if (!this.mCp.video.enabled) {
            return null;
        }
        return this.mRtcCall.getVideoView(z);
    }



    public void hangup() {
        this.mHangupReason = 1;
        hangup(true);
    }

    public boolean isAnswered() {
        if (this.mCallCtx != null) {
            return this.mCallCtx.answered;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isAudioCallInProgress() {
        return this.mCallCtx != null && isCallInProgress() && !this.mCp.video.enabled;
    }

    public boolean isCallConnected() {
        return this.mCallCtx != null && this.mCallCtx.p2pConnected && this.mCallCtx.answered && isCallInProgress();
    }

    public boolean isCallInProgress() {
        return this.mCallCtx != null && !this.cleanedUp && !this.hangedUp && (this.mCallCtx.status & RTCFLAG_VIDEOMUTE) == 0;
    }

    public boolean isDetached() {
        return this.mDetached;
    }

    public boolean isFrontCamera() {
        return getVideoSource() == 1;
    }

    public boolean isIncoming() {
        return this.mCp.incoming;
    }

    public boolean isLinkUp() {
        return this.mCallCtx != null && this.mCallCtx.p2pConnected;
    }

    public boolean isVideoCall() {
        return this.mCp.video.enabled && this.mCallCtx.answeredVideo;
    }

    /* access modifiers changed from: protected */
    public boolean isVideoCallInProgress() {
        return this.mCallCtx != null && isCallInProgress() && this.mCp.video.enabled;
    }

    public boolean isVideoViewsSwapped() {
        return this.mCallCtx.videoSwapped;
    }

    public void mute(boolean z, boolean z2, boolean z3) {
        if (z) {
            this.mCallCtx.audioMute = z3;
        }
        if (z2) {
            this.mCallCtx.videoMute = z3;
        }
        Mesibo.mute(z, z2, z3);
        this.mCallCtx.videoMutedDueToBackground = false;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        this.mRtcCall.onActivityResult(i, i2, intent);
    }

    /* access modifiers changed from: package-private */
    public void onOrientationChanged() {
        if (this.mLandscape) {
            this.mLandscape = false;
            getListener().MesiboCall_OnOrientationChanged(this.mCp, false, false);
            sendCallInfo();
        }
    }

    public void onRtcStatus(long j, long j2, int i, int i2, String str) {
        new StringBuilder("onRtcStatus: type ").append(i).append(" flags: ").append(i2).append(" peerid: ").append(j);
        if (i == 0) {
            createStreams(i2);
        } else if (!TextUtils.isEmpty(str)) {
            SdpInterface.onSdp(str, this.mRtcCall);
        } else if (1 == i) {
            this.mRtcCall.createOffer(false);
        } else if (2 == i) {
            this.mRtcCall.createAnswer();
        } else if (RTCACTION_MUTE == i) {
            rtcMute(i2);
        }
    }

    public void playInCallSound(Context context, int i, boolean z) {
        stopInCallSound();
        this.mRingPlayer = CallUtils.playResource(context, i, z);
    }

    /* access modifiers changed from: protected */
    public void rtcMute(int i) {
        if (this.mRtcCall != null) {
            this.mPendingRtcMuteFlag = 0;
            if ((i & 16) > 0) {
                this.mRtcCall.mute(true, false, true);
            } else if ((i & RTCFLAG_AUDIOUNMUTE) > 0) {
                if (this.mCallCtx.inForeground) {
                    this.mRtcCall.mute(true, false, false);
                } else {
                    this.mPendingRtcMuteFlag |= RTCFLAG_AUDIOUNMUTE;
                }
            }
            if ((i & RTCFLAG_VIDEOMUTE) > 0) {
                this.mRtcCall.mute(false, true, true);
            } else if ((i & RTCFLAG_VIDEOUNMUTE) <= 0) {
            } else {
                if (this.mCallCtx.inForeground) {
                    this.mRtcCall.mute(false, true, false);
                } else {
                    this.mPendingRtcMuteFlag |= RTCFLAG_VIDEOUNMUTE;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void sendCallInfo() {
        long j = 0;
        if (!isFrontCamera()) {
            j = 1;
        }
        this.mBatteryLow = false;
        if (j != this.mLastInfo || this.mLastWidth != 0 || this.mLastHeight != 0) {
            this.mLastInfo = j;
            Mesibo.call_info(j, 0, 0);
        }
    }

    public void sendDTMF(int i) {
    }

    public void setAnswerTime() {
        if (this.mCallCtx.answered && 0 == this.mCallCtx.answerTs) {
            this.mCallCtx.answerTs = SystemClock.elapsedRealtime() - 1000;
            this.m_am.setDefaultAudioDevice(this.mCp.video.enabled ? AudioDevice.SPEAKER : AudioDevice.EARPIECE);
        }
    }

    public void setAudioDevice(AudioDevice audioDevice, boolean z) {
        if (this.m_am != null) {
            if (z) {
                this.m_am.selectAudioDevice(audioDevice);
            } else if (this.mAvailableAudioDevices == null) {
            } else {
                if (!this.mCp.video.enabled || audioDevice == AudioDevice.SPEAKER) {
                    Iterator<AudioDevice> it = this.mAvailableAudioDevices.iterator();
                    boolean z2 = false;
                    boolean z3 = false;
                    boolean z4 = false;
                    while (it.hasNext()) {
                        AudioDevice next = it.next();
                        if (next != audioDevice) {
                            if (next == AudioDevice.HEADSET) {
                                this.m_am.selectAudioDevice(audioDevice);
                                return;
                            } else if (next == AudioDevice.BLUETOOTH) {
                                z4 = true;
                            } else if (next == AudioDevice.SPEAKER) {
                                z3 = true;
                            } else {
                                z2 = next == AudioDevice.EARPIECE ? true : z2;
                            }
                        }
                    }
                    if (z4) {
                        this.m_am.selectAudioDevice(AudioDevice.BLUETOOTH);
                    } else if (z2) {
                        this.m_am.selectAudioDevice(AudioDevice.EARPIECE);
                    } else if (z3) {
                        this.m_am.selectAudioDevice(AudioDevice.EARPIECE);
                    }
                } else {
                    this.m_am.selectAudioDevice(AudioDevice.SPEAKER);
                }
            }
        }
    }

    public void setListener(MesiboCall.InProgressListener inProgressListener) {
        this.mListener = new WeakReference<>(inProgressListener);
    }

    public void setVideoScaling(MesiboCall.VideoScalingType videoScalingType) {
    }

    public void setVideoSource(int i, int i2) {
        this.mRtcCall.setVideoSource(i, i2);
        cameraSourceChanged();
    }

    public void setVideoView(MesiboVideoView mesiboVideoView, boolean z) {
        if (this.mCp.video.enabled) {
            this.mRtcCall.setVideoView(mesiboVideoView, z);
            if (z) {
                this.mRemoteVideoView = mesiboVideoView;
            } else {
                this.mLocalVideoView = mesiboVideoView;
            }
            if (mesiboVideoView != null) {
                setupMirroring(z);
            }
        }
    }

    public void setVideoViewsSwapped(boolean z) {
        this.mCallCtx.videoSwapped = z;
    }

    /* access modifiers changed from: protected */
    public boolean setup(MesiboCall.CallProperties callProperties) {
        this.mCallCtx = new CallContext(callProperties);
        this.mCp = callProperties;
        if (callProperties.video.enabled && callProperties.video.source == 4 && Build.VERSION.SDK_INT < 21) {
            return false;
        }
        if (callProperties.parent == null) {
            return false;
        }
        this.mWakeLock = Utils.createPowerAndWifiLock(callProperties.parent);
        PeerConnectionClient.PeerConnectionParameters peerConnectionParameters = new PeerConnectionClient.PeerConnectionParameters();
        peerConnectionParameters.videoCallEnabled = this.mCp.video.enabled;
        if (callProperties.video.height > 0) {
            peerConnectionParameters.videoHeight = callProperties.video.height;
        }
        if (callProperties.video.width > 0) {
            peerConnectionParameters.videoWidth = callProperties.video.width;
        }
        if (callProperties.video.fps > 0) {
            peerConnectionParameters.videoFps = callProperties.video.fps;
        }
        peerConnectionParameters.videoMaxBitrate = callProperties.video.bitrate;
        peerConnectionParameters.offerToRecv = true;
        peerConnectionParameters.offerToSend = true;
        int i = callProperties.video.source;
        if (i > 4) {
            i = 1;
        }
        this.mRtcCall = new RtcCall(callProperties.activity, peerConnectionParameters, i, this);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void setupMirroring(boolean z) {
        if (z) {
            if (this.mRemoteVideoView != null) {
                this.mRemoteVideoView.enableMirror(this.mLastRemoteMirror);
            }
        } else if (this.mLocalVideoView != null) {
            this.mLocalVideoView.enableMirror(isFrontCamera());
        }
    }

    /* JADX WARNING: type inference failed for: r3v12, types: [com.mesibo.calls.api.MesiboCallActivity, android.app.Activity] */
    public void start(QampCallsActivity mesiboCallActivity, MesiboCall.InProgressListener inProgressListener) {
        boolean z = true;
        MesiboCall.CallProperties callProperties = this.mCallCtx.f0cp;
        if (this.mStarted) {
            this.mDetached = false;
            this.setupViews.firstTime = true;
            callProperties.activity = mesiboCallActivity;
            setListener(inProgressListener);
            if (this.mCallCtx.videoMutedDueToBackground) {
                mute(false, true, false);
            }
            firstUiUpdate();
            return;
        }
        this.mStarted = true;
        this.mDetached = false;
        callProperties.activity = mesiboCallActivity;
        setListener(inProgressListener);
        if (callProperties.video.source == 4 && callProperties.video.width == 0 && callProperties.video.height == 0) {
            DisplayMetrics displayMetrics = DeviceUtils.getDisplayMetrics(callProperties.activity);
            callProperties.video.width = displayMetrics.widthPixels;
            callProperties.video.height = displayMetrics.heightPixels;
        }
        Mesibo.addListener(this);
        if (this.mCallCtx.firstTimeVisible) {
            this.mCallCtx.firstTimeVisible = false;
            if (this.mCp.incoming) {
                if (this.mCp.iceServers == null || this.mCp.iceServers.length <= 0) {
                    z = false;
                }
                Mesibo.call_ack(z);
                sendCallInfo();
            } else if (Mesibo.call(callProperties.user.address, this.mCp.video.enabled) != 0) {
                this.mCallCtx.status = 74;
                recommendViews();
                getListener().MesiboCall_OnHangup(this.mCp, 3);
                return;
            }
        }
        if (!this.mCp.incoming) {
            startAudioManager();
        }
        firstUiUpdate();
    }

    public void startScreenCapturerFromServiceOrActivityResult() {
        this.mRtcCall.startScreenCapturerFromServiceOrActivityResult();
    }

    public void stopInCallSound() {
        if (this.mRingPlayer != null) {
            this.mRingPlayer.stop();
            this.mRingPlayer = null;
        }
    }

    public void switchCamera() {
        if (this.mCp.video.enabled) {
            this.mRtcCall.switchCamera();
        }
    }

    public void switchSource() {
        if (this.mCp.video.enabled) {
            this.mRtcCall.switchSource();
        }
    }

    /**public boolean toggleAudioDevice(MesiboCall.AudioDevice audioDevice) {
        boolean z = audioDevice != this.mActiveAudioDevice;
        setAudioDevice(audioDevice, z);
        return z;
    }**/

    public boolean toggleAudioMute() {
        this.mCallCtx.audioMute = !this.mCallCtx.audioMute;
        mute(true, false, this.mCallCtx.audioMute);
        return this.mCallCtx.audioMute;
    }

    public boolean toggleVideoMute() {
        this.mCallCtx.videoMute = !this.mCallCtx.videoMute;
        mute(false, true, this.mCallCtx.videoMute);
        return this.mCallCtx.videoMute;
    }

}
