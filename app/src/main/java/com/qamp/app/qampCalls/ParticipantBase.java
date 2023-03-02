package com.qamp.app.qampCalls;


import android.content.Intent;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.Utilss;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;
import org.webrtc.SessionDescription.Type;

import java.lang.ref.WeakReference;

public class ParticipantBase implements MesiboCall.MesiboParticipant, MesiboVideoViewInternal.VideoViewListener, RtcCall.Listener {
    private static final String TAG = "ParticipantBase";
    private long pid = 0L;
    protected long mUid = 0L;
    protected long mSid = 0L;
    protected long mFlags = 0L;
    private String mName = "";
    private String mAddress = "";
    private boolean mPublisher = true;
    private GroupCall mGc = null;
    private MesiboVideoView mVideoView = null;
    private RtcCall mRtcCall = null;
    private Object userData = null;
    private WeakReference<MesiboCall.GroupCallInProgressListener> mListener = null;
    private boolean mAudioMute = false;
    private boolean mVideoMute = false;
    private boolean mAdminAudioMute = false;
    private boolean mAdminVideoMute = false;
    private boolean mSentAdminAudioMute = false;
    private boolean mSentAdminVideoMute = false;
    private boolean mRtcAudioMute = false;
    private boolean mRtcVideoMute = false;
    private boolean mVideoMutedForNullView = false;
    private boolean mConnected = false;
    private boolean mVideoEnabled = true;
    private boolean mAudioEnabled = true;
    private int mWidth = 0;
    private int mHeight = 0;
    private long mResolution = 0L;
    private float aspectRatio = 1.0F;
    private boolean mOfferSent = false;
    private boolean mAudioReceived = false;
    private boolean mVideoReceived = false;
    private boolean mLeft = false;
    private int mSource = 1;
    private int mSourceIndex = 0;
    private boolean mTalking = false;
    private long mTalkTs = 0L;
    private String mStatus = "";
    private MesiboProfile mLastAdmin = null;
    private MesiboCall.MesiboParticipantAdmin mAdmin = null;

    public ParticipantBase(long var1, long var3, boolean var5, String var6, String var7, long var8) {
        this.mPublisher = var5;
        this.mUid = var1;
        this.mSid = var3;
        this.mName = var7;
        this.mAddress = var6;
        this.mFlags = var8;
        this.pid = var3 << 32 | var1;
    }

    public long getId() {
        return this.pid;
    }

    public long getUid() {
        return this.mUid;
    }

    public long getSid() {
        return this.mSid;
    }

    public Object getUserData() {
        return this.userData;
    }

    public void setUserData(Object var1) {
        this.userData = var1;
    }

    @Deprecated
    public String getName() {
        return this.mName;
    }

    @Deprecated
    public void setName(String var1) {
        this.mName = var1;
    }

    public String getAddress() {
        return this.mAddress;
    }

    public MesiboProfile getProfile() {
        return Mesibo.getProfile(this.mAddress);
    }

    public MesiboProfile getLastAdminProfile() {
        return this.mLastAdmin;
    }

    public boolean isMe() {
        return this.mUid == 0L;
    }

    public boolean isPublisher() {
        return this.mPublisher;
    }

    public MesiboCall.MesiboParticipantAdmin getAdmin() {
        if (this.isMe()) {
            return null;
        } else if (this.mAdmin != null) {
            return this.mAdmin;
        } else {
            synchronized(this) {
                if (this.mAdmin == null) {
                    this.mAdmin = new ParticipantAdmin(this.mGc, this);
                }
            }

            return this.mAdmin;
        }
    }

    protected void setGroupCall(GroupCall var1) {
        this.mGc = var1;
    }

    private long makeResolution(long var1, long var3) {
        return var1 | var3 << 16;
    }

    private int widthFromResolution(long var1) {
        return (int)(var1 & 65535L);
    }

    private int heightFromResolution(long var1) {
        return (int)(var1 >> 16 & 65535L);
    }

    private void make_call(boolean var1, long var2, int var4, boolean var5, boolean var6) {
        PeerConnectionClient.PeerConnectionParameters var7 = new PeerConnectionClient.PeerConnectionParameters();
        this.mResolution = var2;
        int var8 = this.widthFromResolution(var2);
        int var9 = this.heightFromResolution(var2);
        var7.videoHeight = var8;
        var7.videoWidth = var9;
        var7.videoFps = var4;
        var7.videoCallEnabled = var6;
        var7.audioCallEnabled = var5;
        var7.unifiedPlan = false;
        var7.tcpCandidates = false;
        var7.offerToSend = var1;
        var7.offerToRecv = !var1;
        this.mRtcCall = new RtcCall(this.mGc.getActivity(), var7, this.mSource, this);
        this.mRtcCall.createCall(this.mGc.getActivity(), false, false);
        if (this.mVideoView != null && var6) {
            this.setVideoView(this.mVideoView);
        }

    }

    public void setListener(MesiboCall.GroupCallInProgressListener var1) {
        if (var1 == null) {
            this.mListener = null;
        } else {
            this.mListener = new WeakReference(var1);
        }
    }

    protected MesiboCall.GroupCallInProgressListener getListener() {
        MesiboCall.GroupCallInProgressListener var1 = null;
        if (this.mListener != null) {
            var1 = (MesiboCall.GroupCallInProgressListener)this.mListener.get();
        }

        return (MesiboCall.GroupCallInProgressListener)(var1 == null ? CallManager.getInstance().getDummyListener() : var1);
    }

    public boolean call(boolean var1, boolean var2, MesiboCall.GroupCallInProgressListener var3) {
        if (this.mRtcCall == null && !this.mLeft) {
            if (!var1 && !var2) {
                return false;
            } else {
                this.mVideoEnabled = var2;
                this.mAudioEnabled = var1;
                if (!var1) {
                    this.mFlags &= -3L;
                }

                if (!var2) {
                    this.mFlags &= -5L;
                }

                this.mFlags |= 1L;
                if (var3 != null) {
                    this.setListener(var3);
                }

                if (0L == this.mUid) {
                    Mesibo.groupcall_call(0L, 0L, this.mFlags, 0L, false);
                    return true;
                } else {
                    this.make_call(false, 0L, 0, var1, var2);
                    Mesibo.groupcall_call(this.mUid, this.mSid, this.mFlags, this.makeResolution((long)this.mWidth, (long)this.mHeight), false);
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    public void hangup() {
        if (this.mRtcCall != null) {
            this.mGc.hangup_internal(this);
            this.cleanup();
        }
    }

    public void remoteHangup() {
        if (this.mRtcCall != null) {
            this.getListener().MesiboGroupcall_OnHangup(this, 0);
            this.hangup();
        }
    }

    public void setVideoView(MesiboVideoView var1) {
        if (this.mVideoEnabled) {
            if (var1 != this.mVideoView) {
                MesiboCall.MesiboParticipant var2;
                if (var1 != null) {
                    if ((var2 = var1.getParticipant()) != null && var2 != this && var2.getVideoView() == var1) {
                        var2.setVideoView((MesiboVideoView)null);
                    }

                    var1.setParticipant(this);
                } else if (this.mVideoView != null && (var2 = this.mVideoView.getParticipant()) != null && var2 == this) {
                    this.mVideoView.setParticipant((MesiboCall.MesiboParticipant)null);
                }

                this.mVideoView = var1;
                if (this.mRtcCall != null) {
                    this.mRtcCall.setVideoView(var1, this.mUid != 0L);
                    if (var1 != null) {
                        if (this.mVideoMutedForNullView) {
                            this.mute(false, true, false);
                            this.mVideoMutedForNullView = false;
                        }

                        var1.setListener(this);
                    }
                }
            }
        }
    }

    public MesiboVideoView getVideoView() {
        return this.mVideoView;
    }

    public void invokeOnVideo() {
        CallManager.getInstance().getUiHandler().post(new Runnable() {
            public void run() {
                ParticipantBase.this.getListener().MesiboGroupcall_OnVideo(ParticipantBase.this, ParticipantBase.this.getAspectRatio(), ParticipantBase.this.isVideoLandscape());
            }
        });
    }

    public void MesiboVideoView_OnVideoResolutionChanged(int var1, int var2, int var3) {
        int var4 = this.mWidth;
        int var5 = this.mHeight;
        float var6 = this.getAspectRatio();
        this.mWidth = var1;
        this.mHeight = var2;
        if (var3 == 90 || var3 == 270) {
            this.mHeight = var1;
            this.mWidth = var2;
        }

        if (0L == this.mUid) {
            this.mVideoReceived = true;
            if (this.mOfferSent) {
                long var7 = this.makeResolution((long)this.mWidth, (long)this.mHeight);
                Mesibo.groupcall_set_media(this.mUid, this.mSid, var7, false);
            }
        }

        if (this.mWidth > 0 && this.mHeight > 0 && (var4 != this.mWidth || var5 != this.mHeight) && Math.abs(this.getAspectRatio() - var6) > 0.1F) {
            this.invokeOnVideo();
        }

    }

    public void MesiboVideoView_OnViewSizeChanged(int var1, int var2) {
        if (0L != this.mUid) {
            long var3 = this.makeResolution((long)var1, (long)var2);
            Mesibo.groupcall_set_media(this.mUid, this.mSid, var3, false);
        }
    }

    public void processSdp(boolean var1, String var2, String var3, int var4) {
        if (var3 != null) {
            IceCandidate var6 = new IceCandidate(var3, var4, var2);
            this.mRtcCall.addRemoteIceCandidate(var6);
        } else {
            SessionDescription var5 = new SessionDescription(var1 ? Type.OFFER : Type.ANSWER, var2);
            this.mRtcCall.setRemoteDescription(var5);
        }
    }

    protected void OnConfCallStatus(int var1, long var2, int var4, long var5, long var7, String var9, String var10, int var11) {
        if (2 == var1) {
            boolean var12 = (var7 & 4L) > 0L;
            boolean var13 = (var7 & 2L) > 0L;
            this.make_call(true, var2, var4, var13 && this.mAudioEnabled, this.mVideoEnabled && var12);
            this.mRtcCall.createOffer(true);
        } else {
            if (25 == var1) {
                this.mWidth = this.widthFromResolution(var2);
                this.mHeight = this.heightFromResolution(var2);
                this.processSdp(true, var9, (String)null, 0);
                this.mRtcCall.createAnswer();
            }

            if (26 == var1) {
                this.processSdp(false, var9, (String)null, 0);
            }

            if (27 == var1) {
                this.processSdp(false, var9, var10, var11);
            }

            if (5 == var1) {
                this.mGc.hangup_internal(this);
            }

        }
    }

    private void cleanup() {
        if (this.mRtcCall != null) {
            this.setVideoView((MesiboVideoView)null);
            this.mRtcCall.hangup();
            this.mRtcCall = null;
        }

    }

    protected void onUpdateFyi(int var1, String var2) {
        if (var1 != this.mSource) {
            this.mSource = var1;
            this.getListener().MesiboGroupcall_OnVideoSourceChanged(this, var1, 0);
        }

        if (Utilss.hasStringChanged(this.mStatus, var2)) {
            this.mStatus = var2;
        }

    }

    protected void onUpdate(long var1, long var3) {
        boolean var5 = (var3 & 2L) == 0L;
        boolean var7 = (var3 & 4L) == 0L;
        if (var5 != this.mAudioMute || var7 != this.mVideoMute) {
            this.mAudioMute = var5;
            this.mVideoMute = var7;
            this.getListener().MesiboGroupcall_OnMute(this, var5, var7, true);
        }

        if (!var5) {
            this.mSentAdminAudioMute = false;
        }

        if (!var7) {
            this.mSentAdminVideoMute = false;
        }

        this.mAudioMute = var5;
        this.mVideoMute = var7;
        boolean var6;
        if ((var6 = (var1 & 4096L) > 0L) != this.mTalking) {
            if (var6) {
                this.mTalkTs = Mesibo.getTimestamp();
            }

            this.mTalking = var6;
            this.getListener().MesiboGroupcall_OnTalking(this, var6);
        }

    }

    protected void onParticipantLeft() {
        this.cleanup();
        this.getListener().MesiboGroupcall_OnHangup(this, 0);
        this.mLeft = true;
    }

    protected void onGroupCallStopped() {
        this.hangup();
        this.cleanup();
    }

    public void OnForeground() {
    }

    public void OnBackground() {
    }

    public void onActivityResult(int var1, int var2, Intent var3) {
        if (this.mRtcCall != null) {
            this.mRtcCall.onActivityResult(var1, var2, var3);
        }

    }

    public void startScreenCapturerFromServiceOrActivityResult() {
        this.mRtcCall.startScreenCapturerFromServiceOrActivityResult();
    }

    private void registerPublisher() {
        this.mGc.setActivePublisher(this);
    }

    public void switchCamera() {
        if (this.mUid <= 0L && this.mRtcCall != null) {
            this.mRtcCall.switchCamera();
        }
    }

    public void switchSource() {
        if (0L == this.mUid) {
            if (this.mRtcCall != null) {
                this.registerPublisher();
                this.mRtcCall.switchSource();
            }

        }
    }

    public boolean isFrontCamera() {
        return this.getVideoSource() == 1;
    }

    public void changeVideoFormat(int var1, int var2, int var3) {
        this.mRtcCall.changeVideoFormat(var1, var2, var3);
    }

    public void setVideoSource(int var1, int var2) {
        if (this.mUid <= 0L) {
            if (this.mRtcCall == null) {
                this.mSource = var1;
                this.mSourceIndex = var2;
            } else {
                this.registerPublisher();
                this.mSource = var1;
                this.mRtcCall.setVideoSource(var1, var2);
            }
        }
    }

    public int getVideoSource() {
        return this.mRtcCall.getVideoSource();
    }

    private void rtcMute(boolean var1, boolean var2, boolean var3) {
        label63: {
            if (var3) {
                if (var1 && this.mRtcAudioMute) {
                    var1 = false;
                }

                if (!var2 || !this.mRtcVideoMute) {
                    break label63;
                }
            } else {
                if (var1 && (this.mAdminAudioMute || this.mAudioMute)) {
                    var1 = false;
                }

                if (!var2 || !this.mAdminVideoMute && !this.mVideoMute) {
                    break label63;
                }
            }

            var2 = false;
        }

        if (var1 || var2) {
            if (var2 && var3) {
                this.mVideoMutedForNullView = false;
            }

            if (var1) {
                this.mRtcAudioMute = var3;
            }

            if (var2) {
                this.mRtcVideoMute = var3;
            }

            if (Mesibo.groupcall_mute(this.mUid, this.mSid, var2, var1, var3) > 0) {
                this.mRtcCall.mute(var1, var2, var3);
            }
        }
    }

    public void mute(boolean var1, boolean var2, boolean var3) {
        if (var1) {
            this.mAudioMute = var3;
        }

        if (var2) {
            this.mVideoMute = var3;
        }

        this.rtcMute(var1, var2, var3);
    }

    public boolean toggleAudioMute() {
        this.mute(true, false, !this.mAudioMute);
        return this.mAudioMute;
    }

    public boolean toggleVideoMute() {
        this.mute(false, true, !this.mVideoMute);
        return this.mVideoMute;
    }

    public void adminMute(boolean var1, boolean var2, boolean var3) {
        if (var1) {
            this.mAdminAudioMute = var3;
        }

        if (var2) {
            this.mAdminVideoMute = var3;
        }

        this.rtcMute(var1, var2, var3);
        this.getListener().MesiboGroupcall_OnMute(this, this.mAdminAudioMute, this.mAdminVideoMute, true);
    }

    public void sentAdminMute(boolean var1, boolean var2, boolean var3) {
        if (var1) {
            this.mSentAdminAudioMute = var3;
        }

        if (var2) {
            this.mSentAdminVideoMute = var3;
        }

    }

    public boolean getSentAdminMuteStatus(boolean var1) {
        if (var1) {
            return !this.mVideoEnabled ? true : this.mSentAdminVideoMute;
        } else {
            return !this.mAudioEnabled ? true : this.mSentAdminAudioMute;
        }
    }

    public boolean isVideoCall() {
        return this.mVideoEnabled && (this.mFlags & 4L) > 0L;
    }

    public boolean hasVideo() {
        return this.mVideoReceived && this.mWidth > 0 && this.mHeight > 0;
    }

    public boolean hasAudio() {
        return this.mAudioReceived;
    }

    public boolean isVideoLandscape() {
        return this.hasVideo() && this.mWidth > this.mHeight;
    }

    public float getAspectRatio() {
        return this.mWidth > 0 && this.mHeight > 0 ? (float)this.mWidth / (float)this.mHeight : 1.0F;
    }

    public boolean isCallInProgress() {
        return this.mRtcCall != null;
    }

    public boolean isCallConnected() {
        return this.mConnected;
    }

    public boolean getMuteStatus(boolean var1) {
        if (var1) {
            return !this.mVideoEnabled ? true : this.mVideoMute;
        } else {
            return !this.mAudioEnabled ? true : this.mAudioMute;
        }
    }

    public boolean isTalking() {
        return this.mTalking;
    }

    public long getTalkTimestamp() {
        return this.mTalkTs;
    }

    public void MesiboRtcCall_OnCaptureStarted(boolean var1) {
        if (var1) {
            this.mVideoReceived = true;
            this.getListener().MesiboGroupcall_OnVideoSourceChanged(this, this.getVideoSource(), 0);
            if (this.mSource != this.getVideoSource()) {
                this.mSource = this.getVideoSource();
                Mesibo.groupcall_fyi(this.getSid(), this.mSource, this.mStatus);
            }

            if (this.mWidth == 0) {
                this.mWidth = this.widthFromResolution(this.mResolution);
                this.mHeight = this.heightFromResolution(this.mResolution);
                this.invokeOnVideo();
            }

        }
    }

    public void MesiboRtcCall_OnIceStatus(boolean var1) {
        this.mConnected = var1;
        CallManager.getInstance().getUiHandler().post(new Runnable() {
            public void run() {
                ParticipantBase.this.getListener().MesiboGroupcall_OnConnected(ParticipantBase.this, ParticipantBase.this.mConnected);
            }
        });
    }

    public void MesiboRtcCall_OnSendSdp(SessionDescription var1, boolean var2) {
        byte var5 = 2;
        long var3 = 0L;
        if (Type.OFFER == var1.type) {
            var5 = 1;
            var3 = this.makeResolution((long)this.mWidth, (long)this.mHeight);
            this.mOfferSent = true;
        }

        Mesibo.groupcall_sdp(this.mUid, this.mSid, var3, var5, var1.description, (String)null, 0);
    }

    public void MesiboRtcCall_OnSendCandidate(IceCandidate var1) {
        Mesibo.groupcall_sdp(this.mUid, this.mSid, 0L, 3, var1.sdp, var1.sdpMid, var1.sdpMLineIndex);
    }

    public void MesiboRtcCall_OnSendRemoveCandidate(IceCandidate[] var1) {
    }

    public void MesiboRtcCall_OnRemoteMedia(boolean var1) {
        if (var1) {
            if (!this.mVideoReceived) {
                this.mVideoReceived = true;
                this.invokeOnVideo();
                return;
            }
        } else if (!this.mAudioReceived) {
            this.mAudioReceived = true;
            CallManager.getInstance().getUiHandler().post(new Runnable() {
                public void run() {
                    ParticipantBase.this.getListener().MesiboGroupcall_OnAudio(ParticipantBase.this);
                }
            });
        }

    }

    public static class Members {
        public Members() {
        }
    }
}

