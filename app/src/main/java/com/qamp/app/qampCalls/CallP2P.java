package com.qamp.app.qampCalls;


import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.mesibo.api.Mesibo;
import com.mesibo.api.Mesibo.CallListener;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.R;
import com.qamp.app.Utilss;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Set;

public class CallP2P implements CallListener, CallPrivate, RtcCall.Listener {
    public static final String TAG = "CallP2P";
    private CallContext mCallCtx = null;
    private MesiboCall.CallProperties mCp = null;
    private RtcAudioManager m_am = null;
    private int mPendingRtcMuteFlag = 0;
    private boolean mDetached = true;
    private boolean mConnectedFirstTime = false;
    private boolean mMutedDueToBackground = false;
    private int mHangupReason = 3;
    private boolean mBatteryLow = false;
    private boolean mLandscape = false;
    private boolean mLastRemoteMirror = false;
    private boolean mRemoteLandscape = false;
    private boolean mRemoteLowBattery = false;
    private long mLastInfo = 0L;
    private int mLastWidth = 0;
    private int mLastHeight = 0;
    private Set<MesiboCall.AudioDevice> mAvailableAudioDevices = null;
    private MesiboCall.AudioDevice mActiveAudioDevice;
    private MesiboCall.InProgressListener mDefaultListener;
    private WeakReference<MesiboCall.InProgressListener> mListener;
    protected static final int RTCACTION_CREATESTREAMS = 0;
    protected static final int RTCACTION_OFFER = 1;
    protected static final int RTCACTION_ANSWER = 2;
    protected static final int RTCACTION_SDP = 3;
    protected static final int RTCACTION_PEERCONNECTION = 4;
    protected static final int RTCACTION_STREAM = 5;
    protected static final int RTCACTION_VIDEOCAPTURER = 6;
    protected static final int RTCACTION_MUTE = 7;
    protected static final int RTCFLAG_AUDIO = 1;
    protected static final int RTCFLAG_VIDEO = 2;
    protected static final int RTCFLAG_VIDEOHWACCL = 4;
    protected static final int RTCFLAG_USETURN = 8;
    protected static final int RTCFLAG_AUDIOMUTE = 16;
    protected static final int RTCFLAG_AUDIOUNMUTE = 32;
    protected static final int RTCFLAG_VIDEOMUTE = 64;
    protected static final int RTCFLAG_VIDEOUNMUTE = 128;
    protected static final int RTCSTATUS_INPROGRESS = 1;
    protected static final int RTCSTATUS_CONNECTED = 2;
    protected static final int RTCSTATUS_RECONNECTING = 3;
    protected static final int RTCSTATUS_ENDED = 4;
    protected static final int RTCSTATUS_CLEANUP = 16;
    protected static final int CALLINFO_REARCAMERA = 1;
    protected static final int CALLINFO_SCREEN = 2;
    protected static final int CALLINFO_LANDSCAPE = 4;
    protected static final int CALLINFO_MIRRORED = 8;
    protected static final int CALLINFO_STREAMCHANGING = 256;
    protected static final int CALLINFO_VIDEOREQUEST = 512;
    protected static final int CALLINFO_LOWBATTERY = 4096;
    private MesiboVideoView mRemoteVideoView;
    private MesiboVideoView mLocalVideoView;
    private Utilss.PowerAndWifiLock mWakeLock;
    private RtcCall mRtcCall;
    private boolean mIceServersSet;
    private boolean mStarted;
    private boolean hangedUp;
    private boolean cleanedUp;
    private SetupCallViews setupViews;
    private MediaPlayer mRingPlayer;

    public CallP2P() {
        this.mActiveAudioDevice = MesiboCall.AudioDevice.DEFAULT;
       this.mDefaultListener = CallManager.getInstance().getDummyListener();
        this.mListener = null;
        this.mRemoteVideoView = null;
        this.mLocalVideoView = null;
        this.mWakeLock = null;
        this.mRtcCall = null;
        this.mIceServersSet = false;
        this.mStarted = false;
        this.hangedUp = false;
        this.cleanedUp = false;
        this.setupViews = new SetupCallViews();
        this.mRingPlayer = null;
    }

    public CallContext getCallContext() {
        return this.mCallCtx;
    }

    public MesiboCall.CallProperties getCallProperties() {
        return this.mCp;
    }

    protected boolean setup(MesiboCall.CallProperties var1) {
        this.mCallCtx = new CallContext(var1);
        this.mCp = var1;
        if (var1.video.enabled && var1.video.source == 4 && VERSION.SDK_INT < 21) {
            return false;
        } else if (var1.parent == null) {
            return false;
        } else {
            this.mWakeLock = Utilss.createPowerAndWifiLock(var1.parent);
            PeerConnectionClient.PeerConnectionParameters var2;
            (var2 = new PeerConnectionClient.PeerConnectionParameters()).videoCallEnabled = this.mCp.video.enabled;
            if (var1.video.height > 0) {
                var2.videoHeight = var1.video.height;
            }

            if (var1.video.width > 0) {
                var2.videoWidth = var1.video.width;
            }

            if (var1.video.fps > 0) {
                var2.videoFps = var1.video.fps;
            }

            var2.videoMaxBitrate = var1.video.bitrate;
            var2.offerToRecv = true;
            var2.offerToSend = true;
            int var3;
            if ((var3 = var1.video.source) > 4) {
                var3 = 1;
            }

            this.mRtcCall = new RtcCall(var1.activity, var2, var3, this);
            return true;
        }
    }

    void onOrientationChanged() {
        if (this.mLandscape) {
            this.mLandscape = false;
            this.getListener().MesiboCall_OnOrientationChanged(this.mCp, false, false);
            this.sendCallInfo();
        }
    }

    void batteryLevelChanged() {
        if (this.mBatteryLow) {
            this.sendCallInfo();
            this.mBatteryLow = false;
            this.getListener().MesiboCall_OnBatteryStatus(this.mCp, this.mBatteryLow, false);
        } else {
            if (!this.mBatteryLow && 100 <= this.mCp.batteryLowThreshold) {
                this.sendCallInfo();
                this.mBatteryLow = true;
            } else if (this.mBatteryLow && 100 > this.mCp.batteryLowThreshold) {
                this.sendCallInfo();
                this.mBatteryLow = false;
            }

            this.getListener().MesiboCall_OnBatteryStatus(this.mCp, this.mBatteryLow, false);
        }
    }

    private void firstUiUpdate() {
        this.recommendViews();
        if (MesiboCall.AudioDevice.DEFAULT != this.mActiveAudioDevice) {
            this.notifiAudioDeviceChanged(this.mActiveAudioDevice, MesiboCall.AudioDevice.DEFAULT);
        }

        this.getListener().MesiboCall_OnStatus(this.mCp, this.mCallCtx.status, this.mCp.video.enabled);
        this.getListener().MesiboCall_OnMute(this.mCp, this.mCallCtx.remoteAudioMute, this.mCallCtx.remoteVideoMute, true);
        this.getListener().MesiboCall_OnMute(this.mCp, this.mCallCtx.audioMute, this.mCallCtx.videoMute, false);
    }

    public void start(QampCallActivity var1, MesiboCall.InProgressListener var2) {
        MesiboCall.CallProperties var3 = this.mCallCtx.cp;
        if (this.mStarted) {
            this.mDetached = false;
            this.setupViews.firstTime = true;
            var3.activity = var1;
            this.setListener(var2);
            if (this.mCallCtx.videoMutedDueToBackground) {
                this.mute(false, true, false);
            }

            this.firstUiUpdate();
        } else {
            this.mStarted = true;
            this.mDetached = false;
            var3.activity = var1;
            this.setListener(var2);
            if (var3.video.source == 4 && var3.video.width == 0 && var3.video.height == 0) {
                DisplayMetrics var4 = DeviceUtils.getDisplayMetrics(var3.activity);
                var3.video.width = var4.widthPixels;
                var3.video.height = var4.heightPixels;
            }

            Mesibo.addListener(this);
            if (this.mCallCtx.firstTimeVisible) {
                this.mCallCtx.firstTimeVisible = false;
                if (!this.mCp.incoming) {
                    if (0 != Mesibo.call(var3.user.address, this.mCp.video.enabled)) {
                        this.mCallCtx.status = 74;
                        this.recommendViews();
                        this.getListener().MesiboCall_OnHangup(this.mCp, 3);
                        return;
                    }
                } else {
                    Mesibo.call_ack(null != this.mCp.iceServers && this.mCp.iceServers.length > 0);
                    this.sendCallInfo();
                }
            }

            if (!this.mCp.incoming) {
                this.startAudioManager();
            }

            this.firstUiUpdate();
        }
    }

    public void OnForeground() {
        this.mRtcCall.OnForeground(true);
        this.mCallCtx.inForeground = true;
        if (this.mPendingRtcMuteFlag > 0) {
            this.rtcMute(this.mPendingRtcMuteFlag);
        }

        this.setHideOnProximity();
    }

    public void OnBackground() {
        MesiboCall.CallProperties var1;
        if (!(var1 = this.mCallCtx.cp).runInBackground) {
            this.mHangupReason = 4;
            this.hangup(true);
        } else {
            this.mRtcCall.OnForeground(false);
            this.mCallCtx.inForeground = false;
            if (this.mRtcCall != null && this.getVideoSource() != 4 && var1.stopVideoInBackground && this.mCp.video.enabled && !this.mCallCtx.videoMute) {
                this.mute(false, true, true);
                this.mCallCtx.videoMutedDueToBackground = true;
            }

        }
    }

    public void onActivityResult(int var1, int var2, Intent var3) {
        this.mRtcCall.onActivityResult(var1, var2, var3);
    }

    public void startScreenCapturerFromServiceOrActivityResult() {
        this.mRtcCall.startScreenCapturerFromServiceOrActivityResult();
    }

    public void detach() {
        this.mDetached = true;
    }

    public boolean isDetached() {
        return this.mDetached;
    }

    void setupMirroring(boolean var1) {
        if (var1) {
            if (this.mRemoteVideoView != null) {
                this.mRemoteVideoView.enableMirror(this.mLastRemoteMirror);
                return;
            }
        } else if (this.mLocalVideoView != null) {
            this.mLocalVideoView.enableMirror(this.isFrontCamera());
        }

    }

    public void setVideoView(MesiboVideoView var1, boolean var2) {
        if (this.mCp.video.enabled) {
            this.mRtcCall.setVideoView(var1, var2);
            if (var2) {
                this.mRemoteVideoView = var1;
            } else {
                this.mLocalVideoView = var1;
            }

            if (var1 != null) {
                this.setupMirroring(var2);
            }

        }
    }

    public MesiboVideoView getVideoView(boolean var1) {
        return !this.mCp.video.enabled ? null : this.mRtcCall.getVideoView(var1);
    }

    public void setVideoViewsSwapped(boolean var1) {
        this.mCallCtx.videoSwapped = var1;
    }

    public boolean isVideoViewsSwapped() {
        return this.mCallCtx.videoSwapped;
    }

    public void answer(boolean var1) {
        if (!this.mCallCtx.answered && this.mCp.incoming) {
            this.mCallCtx.answered = true;
            this.mCallCtx.answeredVideo = var1;
            if (this.mCp.video.enabled) {
                this.mCp.video.enabled = var1;
            }

            CallManager.getInstance().stopIncomingNotification();
            this.startAudioManager();
            Mesibo.answer(this.mCp.video.enabled && var1);
            this.recommendViews();
        }
    }

    public void answer() {
        this.answer(this.mCp.video.enabled);
    }

    private void hangup(boolean var1) {
        if (!this.hangedUp && var1 && 0 == (64 & this.mCallCtx.status)) {
            Mesibo.hangup(0L);
            this.hangedUp = true;
        }

        this.mCallCtx.status = 64;
        this.recommendViews();
        this.cleanup(false);
    }

    public void hangup() {
        this.mHangupReason = 1;
        this.hangup(true);
    }

    private void cleanup(boolean var1) {
        if (!this.cleanedUp) {
            this.cleanedUp = true;
            if (!this.mCallCtx.answered) {
                CallNotify.cancel();
            }

            Mesibo.removeListener(this);
            this.mCallCtx.inForeground = false;
            this.playInCallSound(0, false);
            CallManager.getInstance().stopIncomingNotification();
            this.mRtcCall.hangup();
            if (this.m_am != null) {
                this.m_am.stop();
                this.m_am = null;
            }

            if (var1) {
                Mesibo.hangup(0L);
            }

            this.getListener().MesiboCall_OnHangup(this.mCp, this.mHangupReason);
            CallManager.getInstance().removeP2PCall();
            Utilss.releasePowerAndWifiLock(this.mWakeLock);
        }
    }

    public boolean isVideoCall() {
        return this.mCp.video.enabled && this.mCallCtx.answeredVideo;
    }

    public boolean isCallInProgress() {
        return this.mCallCtx != null && 0 == (this.mCallCtx.status & 64);
    }

    public boolean isIncoming() {
        return this.mCp.incoming;
    }

    public boolean isLinkUp() {
        return this.mCallCtx != null && this.mCallCtx.p2pConnected;
    }

    public boolean isCallConnected() {
        return this.mCallCtx != null && this.mCallCtx.p2pConnected && this.mCallCtx.answered && this.isCallInProgress();
    }

    protected boolean isAudioCallInProgress() {
        return this.mCallCtx != null && this.isCallInProgress() && !this.mCp.video.enabled;
    }

    protected boolean isVideoCallInProgress() {
        return this.mCallCtx != null && this.isCallInProgress() && this.mCp.video.enabled;
    }

    public long getAnswerTime() {
        return this.mCallCtx.answerTs;
    }

    public boolean isAnswered() {
        return this.mCallCtx != null ? this.mCallCtx.answered : false;
    }

    public boolean isFrontCamera() {
        return this.getVideoSource() == 1;
    }

    public void changeVideoFormat(int var1, int var2, int var3) {
        if (this.mCp.video.enabled) {
            this.mRtcCall.changeVideoFormat(var1, var2, var3);
        }
    }

    private void startAudioManager() {
        this.m_am = RtcAudioManager.create(CallManager.getAppContext(), this.mCallCtx.cp.audio.speaker, this.mCallCtx.cp.disableSpeakerOnProximity);
        this.m_am.setDefaultAudioDevice(this.mCp.audio.speaker ? MesiboCall.AudioDevice.SPEAKER : MesiboCall.AudioDevice.EARPIECE);
        this.m_am.start(new RtcAudioManager.AudioManagerListener() {
            public void onAudioDeviceChanged(MesiboCall.AudioDevice var1, Set<MesiboCall.AudioDevice> var2) {
                CallP2P.this.onAudioManagerDevicesChanged(var1, var2);
            }
        });
    }

    private void notifiAudioDeviceChanged(final MesiboCall.AudioDevice var1, final MesiboCall.AudioDevice var2) {
        if (Mesibo.isUiThread()) {
            this.getListener().MesiboCall_OnAudioDeviceChanged(this.mCp, var1, var2);
        } else {
            (new Handler(Looper.getMainLooper())).post(new Runnable() {
                public void run() {
                    CallP2P.this.getListener().MesiboCall_OnAudioDeviceChanged(CallP2P.this.mCp, var1, var2);
                }
            });
        }
    }

    private void setHideOnProximity() {
        if (this.mCp.hideOnProximity && null != this.mCp.activity) {
            this.mCp.activity.setHideOnProximity(this.mActiveAudioDevice != MesiboCall.AudioDevice.SPEAKER && this.mCallCtx.inForeground);
        }

    }

    private void onAudioManagerDevicesChanged(MesiboCall.AudioDevice var1, Set<MesiboCall.AudioDevice> var2) {
        (new StringBuilder("onAudioManagerDevicesChanged: ")).append(var2).append(", selected: ").append(var1);
        this.mAvailableAudioDevices = var2;
        if (this.mActiveAudioDevice != var1) {
            this.notifiAudioDeviceChanged(var1, this.mActiveAudioDevice);
            this.mActiveAudioDevice = var1;
            this.setHideOnProximity();
        }
    }

    public void mute(boolean var1, boolean var2, boolean var3) {
        if (var1) {
            this.mCallCtx.audioMute = var3;
        }

        if (var2) {
            this.mCallCtx.videoMute = var3;
        }

        Mesibo.mute(var1, var2, var3);
        this.mCallCtx.videoMutedDueToBackground = false;
    }

    public boolean getMuteStatus(boolean var1, boolean var2, boolean var3) {
        if (!var3) {
            return var1 ? this.mCallCtx.audioMute : this.mCallCtx.videoMute;
        } else {
            return var1 ? this.mCallCtx.remoteAudioMute : this.mCallCtx.remoteVideoMute;
        }
    }

    public boolean toggleAudioMute() {
        this.mCallCtx.audioMute = !this.mCallCtx.audioMute;
        this.mute(true, false, this.mCallCtx.audioMute);
        return this.mCallCtx.audioMute;
    }

    public boolean toggleVideoMute() {
        this.mCallCtx.videoMute = !this.mCallCtx.videoMute;
        this.mute(false, true, this.mCallCtx.videoMute);
        return this.mCallCtx.videoMute;
    }

    void cameraSourceChanged() {
        this.sendCallInfo();
        this.setupMirroring(false);
    }

    public void setVideoSource(int var1, int var2) {
        this.mRtcCall.setVideoSource(var1, var2);
        this.cameraSourceChanged();
    }

    public int getVideoSource() {
        return this.mRtcCall.getVideoSource();
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

    void sendCallInfo() {
        long var1 = 0L;
        if (!this.isFrontCamera()) {
            var1 = 1L;
        }

        this.mBatteryLow = false;
        if (var1 != this.mLastInfo || this.mLastWidth != 0 || this.mLastHeight != 0) {
            this.mLastInfo = var1;
            Mesibo.call_info(var1, 0, 0);
        }
    }

    protected void rtcMute(int var1) {
        if (this.mRtcCall != null) {
            this.mPendingRtcMuteFlag = 0;
            if ((var1 & 16) > 0) {
                this.mRtcCall.mute(true, false, true);
            } else if ((var1 & 32) > 0) {
                if (this.mCallCtx.inForeground) {
                    this.mRtcCall.mute(true, false, false);
                } else {
                    this.mPendingRtcMuteFlag |= 32;
                }
            }

            if ((var1 & 64) > 0) {
                this.mRtcCall.mute(false, true, true);
            } else {
                if ((var1 & 128) > 0) {
                    if (this.mCallCtx.inForeground) {
                        this.mRtcCall.mute(false, true, false);
                        return;
                    }

                    this.mPendingRtcMuteFlag |= 128;
                }

            }
        }
    }

    private void createStreams(int var1) {
        boolean var2 = (var1 & 16) > 0;
        boolean var3 = (var1 & 64) > 0;
        this.mRtcCall.createCall(this.mCp.activity, var2, var3);
        this.isAnswered();
    }

    public void setAnswerTime() {
        if (this.mCallCtx.answered) {
            if (0L == this.mCallCtx.answerTs) {
                this.mCallCtx.answerTs = SystemClock.elapsedRealtime() - 1000L;
                this.m_am.setDefaultAudioDevice(this.mCp.video.enabled ? MesiboCall.AudioDevice.SPEAKER : MesiboCall.AudioDevice.EARPIECE);
            }

        }
    }

    private void setRemoteCallInfo(long var1, long var3) {
        if (this.mCp.video.enabled && this.mRemoteVideoView != null) {
            this.mRemoteVideoView.enableMirror((var1 & 8L) > 0L);
        }

        boolean var6 = (var1 & 4L) > 0L;
        boolean var5 = (var1 & 4096L) > 0L;
        if (var6 != this.mRemoteLandscape) {
            this.getListener().MesiboCall_OnOrientationChanged(this.mCp, var6, true);
            this.mRemoteLandscape = var6;
        }

        if (var5 != this.mRemoteLowBattery) {
            this.getListener().MesiboCall_OnBatteryStatus(this.mCp, var5, true);
        }

    }

    private void playInCallSound(int var1, boolean var2) {
        if (var2 && this.mCallCtx.inCallSound) {
            this.getListener().MesiboCall_OnPlayInCallSound(this.mCp, 0, false);
            this.stopInCallSound();
        }

        if (var2 || this.mCallCtx.inCallSound) {
            this.mCallCtx.inCallSound = var2;
            if (!this.getListener().MesiboCall_OnPlayInCallSound(this.mCp, var1, var2)) {
                int var4 = R.drawable.ic_action_missedcall;// =========
                boolean var3 = true;
                if (var1 == 1) {
                    var4 = R.drawable.ic_action_missedcall;// =========
                    var3 = false;
                }

                this.playInCallSound(CallManager.getAppContext(), var4, var3);
            }

        }
    }

    private void notifyMissedCall() {
        MesiboCall.IncomingListener var1;
        if ((var1 = CallManager.getInstance().getListener()) != null) {
            var1.MesiboCall_onNotify(4, this.mCp.user, this.mCp.video.enabled);
        }

    }

    private void recommendViews() {
        boolean var1 = false;
        boolean var2 = false;
        boolean var3 = false;
        if (this.mCp.incoming && !this.mCallCtx.answered) {
            var1 = true;
        }

        if (this.mCp.video.enabled && (!this.mCallCtx.answered || this.mCallCtx.answeredVideo)) {
            var2 = true;
        }

        if (this.mCp.video.enabled && this.mCallCtx.answered && this.mCallCtx.p2pConnected && this.isCallInProgress()) {
            var3 = true;
        }

        if (this.setupViews.firstTime) {
            this.setupViews.showIncoming = !var1;
            this.setupViews.showVideoControls = !var2;
            this.setupViews.hideControls = false;
        }

        if (var1 != this.setupViews.showIncoming || var2 != this.setupViews.showVideoControls) {
            final int var4 = var1 ? 1 : 2;
            if (Mesibo.isUiThread()) {
                this.getListener().MesiboCall_OnUpdateUserInterface(this.mCp, var4, var2, true);
            } else {
                boolean finalVar = var2;
                (new Handler(Looper.getMainLooper())).post(new Runnable() {
                    public void run() {
                        CallP2P.this.getListener().MesiboCall_OnUpdateUserInterface(CallP2P.this.mCp, var4, finalVar, true);
                    }
                });
            }
        }

        if (var3 != this.setupViews.hideControls) {
            if (Mesibo.isUiThread()) {
                this.getListener().MesiboCall_OnUpdateUserInterface(this.mCp, 3, var2, !var3);
            } else {
                boolean finalVar1 = var2;
                boolean finalVar2 = var3;
                (new Handler(Looper.getMainLooper())).post(new Runnable() {
                    public void run() {
                        CallP2P.this.getListener().MesiboCall_OnUpdateUserInterface(CallP2P.this.mCp, 3, finalVar1, !finalVar2);
                    }
                });
            }
        }

        this.setupViews.showIncoming = var1;
        this.setupViews.hideControls = var3;
        this.setupViews.showVideoControls = var2;
        this.setupViews.firstTime = false;
    }

    public boolean Mesibo_onCall(long var1, long var3, MesiboProfile var5, int var6) {
        return false;
    }

    public void onRtcStatus(long var1, long var3, int var5, int var6, String var7) {
        (new StringBuilder("onRtcStatus: type ")).append(var5).append(" flags: ").append(var6).append(" peerid: ").append(var1);
        if (var5 == 0) {
            this.createStreams(var6);
        } else if (!TextUtils.isEmpty(var7)) {
            SdpInterface.onSdp(var7, this.mRtcCall);
        } else if (1 == var5) {
            this.mRtcCall.createOffer(false);
        } else if (2 == var5) {
            this.mRtcCall.createAnswer();
        } else if (7 == var5) {
            this.rtcMute(var6);
        }
    }

    public boolean Mesibo_onCallStatus(long var1, long var3, int var5, long var6, long var8, long var10, String var12) {
        (new StringBuilder("Mesibo_onCallStatus: status: ")).append(var5).append(" flags: ").append(var6);
        if ((var6 & 1073741824L) > 0L) {
            this.onRtcStatus(var1, var3, var5, (int)var6, var12);
            return true;
        } else {
            boolean var13 = (var6 & 1L) > 0L;
            boolean var2 = (var6 & 2L) > 0L;
            if (3 == var5) {
                this.playInCallSound(0, true);
                this.sendCallInfo();
            } else if (5 == var5 || (var5 & 64) > 0) {
                this.playInCallSound(0, false);
            }

            if ((var5 & 64) > 0) {
                this.mHangupReason = 2;
                if (var5 != 64 && var5 != 65 && !this.mCallCtx.inForeground) {
                    this.playInCallSound(1, true);
                }

                if (this.mCp.incoming && 65 == var5) {
                    this.notifyMissedCall();
                }
            }

            switch(var5) {
                case 5:
                    this.mCallCtx.answered = true;
                    this.mCallCtx.answeredVideo = var2;
                    if (this.mCallCtx.p2pConnected) {
                        this.setAnswerTime();
                    }

                    if (!var2) {
                        this.mCallCtx.videoMute = true;
                    }
                    break;
                case 9:
                case 10:
                    if (var13) {
                        this.mCallCtx.remoteAudioMute = var5 == 9;
                    }

                    if (var2) {
                        this.mCallCtx.remoteVideoMute = var5 == 9;
                    }

                    this.getListener().MesiboCall_OnMute(this.mCp, this.mCallCtx.remoteAudioMute, this.mCallCtx.remoteVideoMute, true);
                    break;
                case 11:
                    this.mCallCtx.hold = true;
                    break;
                case 12:
                    this.mCallCtx.hold = false;
                    break;
                case 35:
                    this.setRemoteCallInfo(var8, var10);
                    break;
                case 48:
                    this.mCallCtx.p2pConnected = true;
                    if (this.mCallCtx.answered) {
                        this.setAnswerTime();
                    }
                    break;
                case 50:
                    this.mCallCtx.p2pConnected = false;
                    if (this.mCallCtx.answered) {
                    }
            }

            if (48 != var5 && 50 != var5 && 35 != var5) {
                this.mCallCtx.status = var5;
            }

            if (48 == var5 && !this.mConnectedFirstTime) {
                if (!this.mCallCtx.answered) {
                    return true;
                }

                this.mConnectedFirstTime = true;
            }

            this.getListener().MesiboCall_OnStatus(this.mCp, var5, var2);
            if (5 == var5 && this.mCallCtx.p2pConnected && !this.mConnectedFirstTime) {
                this.mConnectedFirstTime = true;
                this.getListener().MesiboCall_OnStatus(this.mCp, 48, var2);
            }

            this.recommendViews();
            if ((var5 & 64) > 0) {
                this.cleanup(false);
            }

            return true;
        }
    }

    public void Mesibo_onCallServer(int var1, String var2, String var3, String var4) {
        if (1 == var1) {
            if (null != this.mCp.iceServers && this.mCp.iceServers.length > 0) {
                if (this.mIceServersSet) {
                    return;
                }

                this.mIceServersSet = true;

                for(var1 = 0; var1 < this.mCp.iceServers.length; ++var1) {
                    this.mRtcCall.addTurnServer(this.mCp.iceServers[var1]);
                }

                return;
            }

            this.mRtcCall.addTurnServer(var2, var3, var4);
        }

    }

    public void MesiboRtcCall_OnCaptureStarted(boolean var1) {
        if (var1) {
            this.getListener().MesiboCall_OnVideoSourceChanged(this.mCp, this.getVideoSource(), 0);
        }
    }

    public void MesiboRtcCall_OnIceStatus(boolean var1) {
        Mesibo.setCallStatus(var1 ? 2 : 3, (String)null);
        this.mCallCtx.iceConnected = var1;
    }

    public void MesiboRtcCall_OnSendSdp(SessionDescription var1, boolean var2) {
        SdpInterface.sendSdp(var1, var2);
    }

    public void MesiboRtcCall_OnSendCandidate(IceCandidate var1) {
        SdpInterface.sendLocalIceCandidate(var1);
    }

    public void MesiboRtcCall_OnSendRemoveCandidate(IceCandidate[] var1) {
        SdpInterface.sendLocalIceCandidateRemovals(var1);
    }

    public void MesiboRtcCall_OnRemoteMedia(boolean var1) {
    }

    public void sendDTMF(int var1) {
    }

    public MesiboCall.AudioDevice getActiveAudioDevice() {
        return this.mActiveAudioDevice;
    }

    public void setAudioDevice(MesiboCall.AudioDevice var1, boolean var2) {
        if (var2) {
            this.m_am.selectAudioDevice(var1);
        } else if (this.mAvailableAudioDevices != null) {
            if (this.mCp.video.enabled && var1 != MesiboCall.AudioDevice.SPEAKER) {
                this.m_am.selectAudioDevice(MesiboCall.AudioDevice.SPEAKER);
            } else {
                Iterator var7 = this.mAvailableAudioDevices.iterator();
                boolean var3 = false;
                boolean var4 = false;
                boolean var5 = false;

                while(var7.hasNext()) {
                    MesiboCall.AudioDevice var6;
                    if ((var6 = (MesiboCall.AudioDevice)var7.next()) != var1) {
                        if (var6 == MesiboCall.AudioDevice.HEADSET) {
                            this.m_am.selectAudioDevice(var1);
                            return;
                        }

                        if (var6 == MesiboCall.AudioDevice.BLUETOOTH) {
                            var3 = true;
                        } else if (var6 == MesiboCall.AudioDevice.SPEAKER) {
                            var4 = true;
                        } else if (var6 == MesiboCall.AudioDevice.EARPIECE) {
                            var5 = true;
                        }
                    }
                }

                if (var3) {
                    this.m_am.selectAudioDevice(MesiboCall.AudioDevice.BLUETOOTH);
                } else if (var5) {
                    this.m_am.selectAudioDevice(MesiboCall.AudioDevice.EARPIECE);
                } else if (var4) {
                    this.m_am.selectAudioDevice(MesiboCall.AudioDevice.EARPIECE);
                }
            }
        }
    }

    public boolean toggleAudioDevice(MesiboCall.AudioDevice var1) {
        boolean var2 = var1 != this.mActiveAudioDevice;
        this.setAudioDevice(var1, var2);
        return var2;
    }

    public void setListener(MesiboCall.InProgressListener var1) {
        this.mListener = new WeakReference(var1);
    }

    private MesiboCall.InProgressListener getListener() {
        if (this.mListener == null) {
            return this.mDefaultListener;
        } else {
            MesiboCall.InProgressListener var1;
            return (var1 = (MesiboCall.InProgressListener)this.mListener.get()) == null ? this.mDefaultListener : var1;
        }
    }

    public void setVideoScaling(MesiboCall.VideoScalingType var1) {
    }

    public MesiboCall.VideoScalingType getVideoScalingType() {
        return null;
    }

    public void playInCallSound(Context var1, int var2, boolean var3) {
        this.stopInCallSound();
        this.mRingPlayer = CallUtils.playResource(var1, var2, var3);
    }

    public void stopInCallSound() {
        if (this.mRingPlayer != null) {
            this.mRingPlayer.stop();
            this.mRingPlayer = null;
        }

    }

    public static class SetupCallViews {
        boolean showIncoming = true;
        boolean showVideoControls = true;
        boolean hideControls = true;
        boolean firstTime = true;

        public SetupCallViews() {
        }
    }
}

