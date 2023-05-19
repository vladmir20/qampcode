/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 3:15 PM
 *
 */

package com.qamp.app.qampcallss.api;

import android.content.Intent;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.lang.ref.WeakReference;

public class ParticipantBase implements MesiboCall.MesiboParticipant, MesiboVideoViewInternal.VideoViewListener, RtcCall.Listener {
    private static final String TAG = "ParticipantBase";
    private float aspectRatio = 1.0f;
    private String mAddress = "";
    private MesiboCall.MesiboParticipantAdmin mAdmin = null;
    private boolean mAdminAudioMute = false;
    private boolean mAdminVideoMute = false;
    private boolean mAudioEnabled = true;
    private boolean mAudioMute = false;
    private boolean mAudioReceived = false;
    /* access modifiers changed from: private */
    public boolean mConnected = false;
    protected long mFlags = 0;
    private GroupCall mGc = null;
    private int mHeight = 0;
    private MesiboProfile mLastAdmin = null;
    private boolean mLeft = false;
    private WeakReference<MesiboCall.GroupCallInProgressListener> mListener = null;
    private String mName = "";
    private boolean mOfferSent = false;
    private boolean mPublisher = true;
    private long mResolution = 0;
    private boolean mRtcAudioMute = false;
    private RtcCall mRtcCall = null;
    private boolean mRtcVideoMute = false;
    private boolean mSentAdminAudioMute = false;
    private boolean mSentAdminVideoMute = false;
    protected long mSid = 0;
    private int mSource = 1;
    private int mSourceIndex = 0;
    private String mStatus = "";
    private long mTalkTs = 0;
    private boolean mTalking = false;
    protected long mUid = 0;
    private boolean mVideoEnabled = true;
    private boolean mVideoMute = false;
    private boolean mVideoMutedForNullView = false;
    private boolean mVideoReceived = false;
    private MesiboVideoView mVideoView = null;
    private int mWidth = 0;
    private long pid = 0;
    private Object userData = null;

    public static class Members {
    }

    public ParticipantBase(long j, long j2, boolean z, String str, String str2, long j3) {
        this.mPublisher = z;
        this.mUid = j;
        this.mSid = j2;
        this.mName = str2;
        this.mAddress = str;
        this.mFlags = j3;
        this.pid = (j2 << 32) | j;
    }

    private void cleanup() {
        if (this.mRtcCall != null) {
            setVideoView((MesiboVideoView) null);
            this.mRtcCall.hangup();
            this.mRtcCall = null;
        }
    }

    private int heightFromResolution(long j) {
        return (int) ((j >> 16) & 65535);
    }

    private long makeResolution(long j, long j2) {
        return (j2 << 16) | j;
    }

    private void make_call(boolean z, long j, int i, boolean z2, boolean z3) {
        PeerConnectionClient.PeerConnectionParameters peerConnectionParameters = new PeerConnectionClient.PeerConnectionParameters();
        this.mResolution = j;
        int widthFromResolution = widthFromResolution(j);
        int heightFromResolution = heightFromResolution(j);
        peerConnectionParameters.videoHeight = widthFromResolution;
        peerConnectionParameters.videoWidth = heightFromResolution;
        peerConnectionParameters.videoFps = i;
        peerConnectionParameters.videoCallEnabled = z3;
        peerConnectionParameters.audioCallEnabled = z2;
        peerConnectionParameters.unifiedPlan = false;
        peerConnectionParameters.tcpCandidates = false;
        peerConnectionParameters.offerToSend = z;
        peerConnectionParameters.offerToRecv = !z;
        this.mRtcCall = new RtcCall(this.mGc.getActivity(), peerConnectionParameters, this.mSource, this);
        this.mRtcCall.createCall(this.mGc.getActivity(), false, false);
        if (this.mVideoView != null && z3) {
            setVideoView(this.mVideoView);
        }
    }

    private void registerPublisher() {
        this.mGc.setActivePublisher(this);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x004d, code lost:
        if (r7.mVideoMute != false) goto L_0x0015;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0013, code lost:
        if (r7.mRtcVideoMute != false) goto L_0x0015;
     */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0019 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0023  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0027  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0034  */
    /* JADX WARNING: Removed duplicated region for block: B:39:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void rtcMute(boolean r8, boolean r9, boolean r10) {
        /*
            r7 = this;
            r1 = 0
            RtcCall r0 = r7.mRtcCall
            if (r0 != 0) goto L_0x0006
        L_0x0005:
            return
        L_0x0006:
            if (r10 == 0) goto L_0x003a
            if (r8 == 0) goto L_0x0054
            boolean r0 = r7.mRtcAudioMute
            if (r0 == 0) goto L_0x0054
            r0 = r1
        L_0x000f:
            if (r9 == 0) goto L_0x004f
            boolean r2 = r7.mRtcVideoMute
            if (r2 == 0) goto L_0x004f
        L_0x0015:
            r4 = r1
            r5 = r0
        L_0x0017:
            if (r5 != 0) goto L_0x001b
            if (r4 == 0) goto L_0x0005
        L_0x001b:
            if (r4 == 0) goto L_0x0021
            if (r10 == 0) goto L_0x0021
            r7.mVideoMutedForNullView = r1
        L_0x0021:
            if (r5 == 0) goto L_0x0025
            r7.mRtcAudioMute = r10
        L_0x0025:
            if (r4 == 0) goto L_0x0029
            r7.mRtcVideoMute = r10
        L_0x0029:
            long r0 = r7.mUid
            long r2 = r7.mSid
            r6 = r10
            int r0 = com.mesibo.api.Mesibo.groupcall_mute(r0, r2, r4, r5, r6)
            if (r0 <= 0) goto L_0x0005
            RtcCall r0 = r7.mRtcCall
            r0.mute(r5, r4, r10)
            goto L_0x0005
        L_0x003a:
            if (r8 == 0) goto L_0x0052
            boolean r0 = r7.mAdminAudioMute
            if (r0 != 0) goto L_0x0044
            boolean r0 = r7.mAudioMute
            if (r0 == 0) goto L_0x0052
        L_0x0044:
            r0 = r1
        L_0x0045:
            if (r9 == 0) goto L_0x004f
            boolean r2 = r7.mAdminVideoMute
            if (r2 != 0) goto L_0x0015
            boolean r2 = r7.mVideoMute
            if (r2 != 0) goto L_0x0015
        L_0x004f:
            r4 = r9
            r5 = r0
            goto L_0x0017
        L_0x0052:
            r0 = r8
            goto L_0x0045
        L_0x0054:
            r0 = r8
            goto L_0x000f
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mesibo.calls.api.ParticipantBase.rtcMute(boolean, boolean, boolean):void");
    }

    private int widthFromResolution(long j) {
        return (int) (65535 & j);
    }

    public void MesiboRtcCall_OnCaptureStarted(boolean z) {
        if (z) {
            this.mVideoReceived = true;
            getListener().MesiboGroupcall_OnVideoSourceChanged(this, getVideoSource(), 0);
            if (this.mSource != getVideoSource()) {
                this.mSource = getVideoSource();
                Mesibo.groupcall_fyi(getSid(), this.mSource, this.mStatus);
            }
            if (this.mWidth == 0) {
                this.mWidth = widthFromResolution(this.mResolution);
                this.mHeight = heightFromResolution(this.mResolution);
                invokeOnVideo();
            }
        }
    }

    public void MesiboRtcCall_OnIceStatus(boolean z) {
        this.mConnected = z;
        CallManager.getInstance().getUiHandler().post(new Runnable() {
            public void run() {
                ParticipantBase.this.getListener().MesiboGroupcall_OnConnected(ParticipantBase.this, ParticipantBase.this.mConnected);
            }
        });
    }

    public void MesiboRtcCall_OnRemoteMedia(boolean z) {
        if (z) {
            if (!this.mVideoReceived) {
                this.mVideoReceived = true;
                invokeOnVideo();
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

    public void MesiboRtcCall_OnSendCandidate(IceCandidate iceCandidate) {
        Mesibo.groupcall_sdp(this.mUid, this.mSid, 0, 3, iceCandidate.sdp, iceCandidate.sdpMid, iceCandidate.sdpMLineIndex);
    }

    public void MesiboRtcCall_OnSendRemoveCandidate(IceCandidate[] iceCandidateArr) {
    }

    public void MesiboRtcCall_OnSendSdp(SessionDescription sessionDescription, boolean z) {
        int i = 1;
        long j = 0;
        if (SessionDescription.Type.OFFER == sessionDescription.type) {
            j = makeResolution((long) this.mWidth, (long) this.mHeight);
            this.mOfferSent = true;
        } else {
            i = 2;
        }
        Mesibo.groupcall_sdp(this.mUid, this.mSid, j, i, sessionDescription.description, (String) null, 0);
    }

    public void MesiboVideoView_OnVideoResolutionChanged(int i, int i2, int i3) {
        int i4 = this.mWidth;
        int i5 = this.mHeight;
        float aspectRatio2 = getAspectRatio();
        this.mWidth = i;
        this.mHeight = i2;
        if (i3 == 90 || i3 == 270) {
            this.mHeight = i;
            this.mWidth = i2;
        }
        if (0 == this.mUid) {
            this.mVideoReceived = true;
            if (this.mOfferSent) {
                Mesibo.groupcall_set_media(this.mUid, this.mSid, makeResolution((long) this.mWidth, (long) this.mHeight), false);
            }
        }
        if (this.mWidth > 0 && this.mHeight > 0) {
            if (!(i4 == this.mWidth && i5 == this.mHeight) && Math.abs(getAspectRatio() - aspectRatio2) > 0.1f) {
                invokeOnVideo();
            }
        }
    }

    public void MesiboVideoView_OnViewSizeChanged(int i, int i2) {
        if (0 != this.mUid) {
            Mesibo.groupcall_set_media(this.mUid, this.mSid, makeResolution((long) i, (long) i2), false);
        }
    }

    public void OnBackground() {
    }

    /* access modifiers changed from: protected */
    public void OnConfCallStatus(int i, long j, int i2, long j2, long j3, String str, String str2, int i3) {
        if (2 == i) {
            make_call(true, j, i2, (((2 & j3) > 0 ? 1 : ((2 & j3) == 0 ? 0 : -1)) > 0) && this.mAudioEnabled, this.mVideoEnabled && (((4 & j3) > 0 ? 1 : ((4 & j3) == 0 ? 0 : -1)) > 0));
            this.mRtcCall.createOffer(true);
            return;
        }
        if (25 == i) {
            this.mWidth = widthFromResolution(j);
            this.mHeight = heightFromResolution(j);
            processSdp(true, str, (String) null, 0);
            this.mRtcCall.createAnswer();
        }
        if (26 == i) {
            processSdp(false, str, (String) null, 0);
        }
        if (27 == i) {
            processSdp(false, str, str2, i3);
        }
        if (5 == i) {
            this.mGc.hangup_internal(this);
        }
    }

    public void OnForeground() {
    }

    public void adminMute(boolean z, boolean z2, boolean z3) {
        if (z) {
            this.mAdminAudioMute = z3;
        }
        if (z2) {
            this.mAdminVideoMute = z3;
        }
        rtcMute(z, z2, z3);
        getListener().MesiboGroupcall_OnMute(this, this.mAdminAudioMute, this.mAdminVideoMute, true);
    }

    public boolean call(boolean z, boolean z2, MesiboCall.GroupCallInProgressListener groupCallInProgressListener) {
        if (this.mRtcCall != null || this.mLeft) {
            return false;
        }
        if (!z && !z2) {
            return false;
        }
        this.mVideoEnabled = z2;
        this.mAudioEnabled = z;
        if (!z) {
            this.mFlags &= -3;
        }
        if (!z2) {
            this.mFlags &= -5;
        }
        this.mFlags |= 1;
        if (groupCallInProgressListener != null) {
            setListener(groupCallInProgressListener);
        }
        if (0 == this.mUid) {
            Mesibo.groupcall_call(0, 0, this.mFlags, 0, false);
            return true;
        }
        make_call(false, 0, 0, z, z2);
        Mesibo.groupcall_call(this.mUid, this.mSid, this.mFlags, makeResolution((long) this.mWidth, (long) this.mHeight), false);
        return true;
    }

    public void changeVideoFormat(int i, int i2, int i3) {
        this.mRtcCall.changeVideoFormat(i, i2, i3);
    }

    public String getAddress() {
        return this.mAddress;
    }

    public MesiboCall.MesiboParticipantAdmin getAdmin() {
        if (isMe()) {
            return null;
        }
        if (this.mAdmin != null) {
            return this.mAdmin;
        }
        synchronized (this) {
            if (this.mAdmin == null) {
                this.mAdmin = new ParticipantAdmin(this.mGc, this);
            }
        }
        return this.mAdmin;
    }

    public float getAspectRatio() {
        if (this.mWidth <= 0 || this.mHeight <= 0) {
            return 1.0f;
        }
        return ((float) this.mWidth) / ((float) this.mHeight);
    }

    public long getId() {
        return this.pid;
    }

    public MesiboProfile getLastAdminProfile() {
        return this.mLastAdmin;
    }

    /* access modifiers changed from: protected */
    public MesiboCall.GroupCallInProgressListener getListener() {
        MesiboCall.GroupCallInProgressListener groupCallInProgressListener = null;
        if (this.mListener != null) {
            groupCallInProgressListener = (MesiboCall.GroupCallInProgressListener) this.mListener.get();
        }
        return groupCallInProgressListener == null ? (MesiboCall.GroupCallInProgressListener) CallManager.getInstance().getDummyListener() : groupCallInProgressListener;
    }

    public boolean getMuteStatus(boolean z) {
        if (z) {
            if (!this.mVideoEnabled) {
                return true;
            }
            return this.mVideoMute;
        } else if (this.mAudioEnabled) {
            return this.mAudioMute;
        } else {
            return true;
        }
    }

    @Deprecated
    public String getName() {
        return this.mName;
    }

    public MesiboProfile getProfile() {
        return Mesibo.getProfile(this.mAddress);
    }

    public boolean getSentAdminMuteStatus(boolean z) {
        if (z) {
            if (!this.mVideoEnabled) {
                return true;
            }
            return this.mSentAdminVideoMute;
        } else if (this.mAudioEnabled) {
            return this.mSentAdminAudioMute;
        } else {
            return true;
        }
    }

    public long getSid() {
        return this.mSid;
    }

    public long getTalkTimestamp() {
        return this.mTalkTs;
    }

    public long getUid() {
        return this.mUid;
    }

    public Object getUserData() {
        return this.userData;
    }

    public int getVideoSource() {
        if (this.mRtcCall == null) {
            return 0;
        }
        return this.mRtcCall.getVideoSource();
    }

    public MesiboVideoView getVideoView() {
        return this.mVideoView;
    }

    public void hangup() {
        if (this.mRtcCall != null) {
            this.mGc.hangup_internal(this);
            cleanup();
        }
    }

    public boolean hasAudio() {
        return this.mAudioReceived;
    }

    public boolean hasVideo() {
        return this.mVideoReceived && this.mWidth > 0 && this.mHeight > 0;
    }

    public void invokeOnVideo() {
        CallManager.getInstance().getUiHandler().post(new Runnable() {
            public void run() {
                ParticipantBase.this.getListener().MesiboGroupcall_OnVideo(ParticipantBase.this, ParticipantBase.this.getAspectRatio(), ParticipantBase.this.isVideoLandscape());
            }
        });
    }

    public boolean isCallConnected() {
        return this.mConnected;
    }

    public boolean isCallInProgress() {
        return this.mRtcCall != null;
    }

    public boolean isFrontCamera() {
        return getVideoSource() == 1;
    }

    public boolean isMe() {
        return this.mUid == 0;
    }

    public boolean isPublisher() {
        return this.mPublisher;
    }

    public boolean isTalking() {
        return this.mTalking;
    }

    public boolean isVideoCall() {
        return this.mVideoEnabled && (this.mFlags & 4) > 0;
    }

    public boolean isVideoLandscape() {
        return hasVideo() && this.mWidth > this.mHeight;
    }

    public void mute(boolean z, boolean z2, boolean z3) {
        if (z) {
            this.mAudioMute = z3;
        }
        if (z2) {
            this.mVideoMute = z3;
        }
        rtcMute(z, z2, z3);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (this.mRtcCall != null) {
            this.mRtcCall.onActivityResult(i, i2, intent);
        }
    }

    /* access modifiers changed from: protected */
    public void onGroupCallStopped() {
        hangup();
        cleanup();
    }

    /* access modifiers changed from: protected */
    public void onParticipantLeft() {
        cleanup();
        getListener().MesiboGroupcall_OnHangup(this, 0);
        this.mLeft = true;
    }

    /* access modifiers changed from: protected */
    public void onUpdate(long j, long j2) {
        boolean z = true;
        boolean z2 = (2 & j2) == 0;
        boolean z3 = (4 & j2) == 0;
        if (!(z2 == this.mAudioMute && z3 == this.mVideoMute)) {
            this.mAudioMute = z2;
            this.mVideoMute = z3;
            getListener().MesiboGroupcall_OnMute(this, z2, z3, true);
        }
        if (!z2) {
            this.mSentAdminAudioMute = false;
        }
        if (!z3) {
            this.mSentAdminVideoMute = false;
        }
        this.mAudioMute = z2;
        this.mVideoMute = z3;
        if ((4096 & j) <= 0) {
            z = false;
        }
        if (z != this.mTalking) {
            if (z) {
                this.mTalkTs = Mesibo.getTimestamp();
            }
            this.mTalking = z;
            getListener().MesiboGroupcall_OnTalking(this, z);
        }
    }

    /* access modifiers changed from: protected */
    public void onUpdateFyi(int i, String str) {
        if (i != this.mSource) {
            this.mSource = i;
            getListener().MesiboGroupcall_OnVideoSourceChanged(this, i, 0);
        }
        if (Utils.hasStringChanged(this.mStatus, str)) {
            this.mStatus = str;
        }
    }

    public void processSdp(boolean z, String str, String str2, int i) {
        if (str2 != null) {
            this.mRtcCall.addRemoteIceCandidate(new IceCandidate(str2, i, str));
        } else {
            this.mRtcCall.setRemoteDescription(new SessionDescription(z ? SessionDescription.Type.OFFER : SessionDescription.Type.ANSWER, str));
        }
    }

    public void remoteHangup() {
        if (this.mRtcCall != null) {
            getListener().MesiboGroupcall_OnHangup(this, 0);
            hangup();
        }
    }

    public void sentAdminMute(boolean z, boolean z2, boolean z3) {
        if (z) {
            this.mSentAdminAudioMute = z3;
        }
        if (z2) {
            this.mSentAdminVideoMute = z3;
        }
    }

    /* access modifiers changed from: protected */
    public void setGroupCall(GroupCall groupCall) {
        this.mGc = groupCall;
    }

    public void setListener(MesiboCall.GroupCallInProgressListener groupCallInProgressListener) {
        if (groupCallInProgressListener == null) {
            this.mListener = null;
        } else {
            this.mListener = new WeakReference<>(groupCallInProgressListener);
        }
    }

    @Deprecated
    public void setName(String str) {
        this.mName = str;
    }

    public void setUserData(Object obj) {
        this.userData = obj;
    }

    public void setVideoSource(int i, int i2) {
        if (this.mUid <= 0) {
            if (this.mRtcCall == null) {
                this.mSource = i;
                this.mSourceIndex = i2;
                return;
            }
            registerPublisher();
            this.mSource = i;
            this.mRtcCall.setVideoSource(i, i2);
        }
    }

    public void setVideoView(MesiboVideoView mesiboVideoView) {
        MesiboCall.MesiboParticipant participant;
        if (this.mVideoEnabled && mesiboVideoView != this.mVideoView) {
            if (mesiboVideoView != null) {
                MesiboCall.MesiboParticipant participant2 = mesiboVideoView.getParticipant();
                if (!(participant2 == null || participant2 == this || participant2.getVideoView() != mesiboVideoView)) {
                    participant2.setVideoView((MesiboVideoView) null);
                }
                mesiboVideoView.setParticipant(this);
            } else if (!(this.mVideoView == null || (participant = this.mVideoView.getParticipant()) == null || participant != this)) {
                this.mVideoView.setParticipant((MesiboCall.MesiboParticipant) null);
            }
            this.mVideoView = mesiboVideoView;
            if (this.mRtcCall != null) {
                this.mRtcCall.setVideoView(mesiboVideoView, this.mUid != 0);
                if (mesiboVideoView != null) {
                    if (this.mVideoMutedForNullView) {
                        mute(false, true, false);
                        this.mVideoMutedForNullView = false;
                    }
                    mesiboVideoView.setListener(this);
                }
            }
        }
    }

    public void startScreenCapturerFromServiceOrActivityResult() {
        this.mRtcCall.startScreenCapturerFromServiceOrActivityResult();
    }

    public void switchCamera() {
        if (this.mUid <= 0 && this.mRtcCall != null) {
            this.mRtcCall.switchCamera();
        }
    }

    public void switchSource() {
        if (0 == this.mUid && this.mRtcCall != null) {
            registerPublisher();
            this.mRtcCall.switchSource();
        }
    }

    public boolean toggleAudioMute() {
        mute(true, false, !this.mAudioMute);
        return this.mAudioMute;
    }

    public boolean toggleVideoMute() {
        mute(false, true, !this.mVideoMute);
        return this.mVideoMute;
    }
}
