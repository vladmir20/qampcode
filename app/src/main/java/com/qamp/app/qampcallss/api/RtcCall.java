package com.qamp.app.qampcallss.api;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.qamp.app.qampcallss.api.p000ui.QampCallsActivity;

import org.webrtc.AudioTrack;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSink;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

public class RtcCall implements MesiboCapturer.MesiboCapturerListener, PeerConnectionClient.PeerConnectionEvents {
    public static final String TAG = "RtcCall";
    /* access modifiers changed from: private */
    public static int mediaProjectionPermissionResultCode;
    /* access modifiers changed from: private */
    public static Intent mediaProjectionPermissionResultData;
    private boolean createAudioMute;
    private boolean createVideoMute;
    private boolean initiator = false;
    /* access modifiers changed from: private */
    public ProxyVideoSink localVideoSink = new ProxyVideoSink();
    private QampCallsActivity mActivity = null;
    private AudioTrack mAudioTrack = null;
    private MesiboCapturer mCameraCapturer = null;
    private int mCameraSource = this.mVideoSource;
    private MesiboCapturer mCapturer = null;
    private int mDefaultCameraSource = 1;
    /* access modifiers changed from: private */
    public Listener mListener = null;
    private MesiboVideoView mLocalVideoView = null;
    /* access modifiers changed from: private */
    public PeerConnectionClient.PeerConnectionParameters mParams;
    private MesiboVideoView mRemoteVideoView = null;
    /* access modifiers changed from: private */
    public MesiboCapturer mScreenCapturer = null;
    private boolean mScreenCapturerStartEnabled = false;
    private boolean mScreenCapturerStarted = false;
    private boolean mSdpPending = false;
    private boolean mVideoMutePending = false;
    private int mVideoSource = 1;
    private VideoTrack mVideoTrack = null;
    /* access modifiers changed from: private */
    public PeerConnectionClient m_pc = null;
    private MesiboCall.MesiboIceServer m_server = null;
    /* access modifiers changed from: private */
    public MesiboVideoCapturerFactory m_vcf = new MesiboVideoCapturerFactory();
    private List<VideoSink> remoteRenderers = new ArrayList();
    private ProxyVideoSink remoteVideoSink = new ProxyVideoSink();
    private boolean renegotiateOnMediaChanged = false;

    public interface Listener {
        void MesiboRtcCall_OnCaptureStarted(boolean z);

        void MesiboRtcCall_OnIceStatus(boolean z);

        void MesiboRtcCall_OnRemoteMedia(boolean z);

        void MesiboRtcCall_OnSendCandidate(IceCandidate iceCandidate);

        void MesiboRtcCall_OnSendRemoveCandidate(IceCandidate[] iceCandidateArr);

        void MesiboRtcCall_OnSendSdp(SessionDescription sessionDescription, boolean z);
    }

    RtcCall(QampCallsActivity mesiboCallActivity, PeerConnectionClient.PeerConnectionParameters peerConnectionParameters, int i, Listener listener) {
        EglBase eglBase = null;
        this.mActivity = mesiboCallActivity;
        this.mParams = peerConnectionParameters;
        this.mListener = listener;
        this.mVideoSource = i;
        this.remoteRenderers.add(this.remoteVideoSink);
        this.m_pc = new PeerConnectionClient(CallManager.getAppContext(), peerConnectionParameters.videoCallEnabled ? EglBase.create() : eglBase, peerConnectionParameters, this);
        this.m_pc.createPeerConnectionFactory(new PeerConnectionFactory.Options());
    }

    /* JADX WARNING: type inference failed for: r1v2, types: [android.content.Context, com.mesibo.calls.api.MesiboCallActivity] */
    /* JADX WARNING: type inference failed for: r1v5, types: [com.mesibo.calls.api.MesiboCallActivity, android.app.Activity] */
    private boolean createCapturer(int i) {
        if ((4 != i || this.mScreenCapturerStarted) && this.mCapturer != null) {
            this.mCapturer.setRenderer((VideoSink) null);
            this.mCapturer.stop();
        }
        if (4 != this.mVideoSource) {
            if (this.mCameraCapturer == null) {
                boolean z = 1 == this.mVideoSource;
                this.mCameraCapturer = new MesiboCapturer(this, this.m_pc, this.m_vcf.create(this.mActivity, z, this.mParams.useCamera2, this.mParams.captureToTexture, (String) null), this.localVideoSink, this.mParams.videoWidth, this.mParams.videoHeight, this.mParams.videoFps, z);
            } else {
                this.mCameraCapturer.restart(this.localVideoSink);
            }
            setCurrentCapturer(this.mCameraCapturer);
        } else if (this.mScreenCapturer != null || this.mScreenCapturerStarted) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                public void run() {
                    if (RtcCall.this.m_pc != null) {
                        if (RtcCall.this.mScreenCapturer == null) {
                            VideoCapturer createScreenCapturer = RtcCall.this.m_vcf.createScreenCapturer(RtcCall.mediaProjectionPermissionResultCode, RtcCall.mediaProjectionPermissionResultData);
                            if (createScreenCapturer != null) {
                                MesiboCapturer unused = RtcCall.this.mScreenCapturer = new MesiboCapturer(RtcCall.this, RtcCall.this.m_pc, createScreenCapturer, RtcCall.this.localVideoSink, RtcCall.this.mParams.videoHeight, RtcCall.this.mParams.videoWidth, RtcCall.this.mParams.videoFps, true);
                            }
                        } else {
                            RtcCall.this.mScreenCapturer.restart(RtcCall.this.localVideoSink);
                        }
                        if (RtcCall.this.mScreenCapturer != null) {
                            RtcCall.this.setCurrentCapturer(RtcCall.this.mScreenCapturer);
                        }
                    }
                }
            }, 1000);
        } else {
            this.m_vcf.startScreenCapture(this.mActivity);
            this.mScreenCapturerStarted = true;
            this.mScreenCapturerStartEnabled = true;
            return false;
        }
        if (this.mCapturer == null) {
            return false;
        }
        this.mVideoSource = i;
        return true;
    }

    private void reportError(String str) {
    }

    private void setCameraSource(int i) {
        if (i == 0) {
            i = this.mDefaultCameraSource;
        }
        this.mDefaultCameraSource = i;
        this.mVideoSource = i;
        createCapturer(i);
    }

    /* access modifiers changed from: private */
    public void setCurrentCapturer(MesiboCapturer mesiboCapturer) {
        if (this.m_pc != null) {
            this.mCapturer = mesiboCapturer;
            this.mVideoTrack = this.mCapturer.getTrack();
            this.m_pc.setTrack(this.mVideoTrack);
            if (this.mVideoTrack != null && this.mVideoMutePending) {
                this.m_pc.setTrackEnabled(this.mVideoTrack, true);
            }
            if (this.mSdpPending) {
                this.mSdpPending = false;
                if (this.initiator) {
                    this.m_pc.createOffer();
                } else {
                    this.m_pc.createAnswer();
                }
            } else {
                renegotiate();
            }
        }
    }

    private void setScreenSource() {
        this.mVideoSource = 4;
        createCapturer(4);
    }

    public void MesiboCapturer_OnStarted(MesiboCapturer mesiboCapturer, final boolean z) {
        if (z) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    RtcCall.this.mListener.MesiboRtcCall_OnCaptureStarted(z);
                }
            });
        }
    }

    public void MesiboCapturer_OnSwitchCamera(MesiboCapturer mesiboCapturer, boolean z) {
        if (z) {
            this.mVideoSource = 1;
        } else {
            this.mVideoSource = 2;
        }
        MesiboCapturer_OnStarted(mesiboCapturer, true);
    }

    public void OnForeground(boolean z) {
        if (this.remoteVideoSink != null) {
            this.remoteVideoSink.setForeground(z);
            this.localVideoSink.setForeground(z);
        }
    }

    public void addRemoteIceCandidate(IceCandidate iceCandidate) {
        this.m_pc.addRemoteIceCandidate(iceCandidate);
    }

    public void addTurnServer(MesiboCall.MesiboIceServer mesiboIceServer) {
        this.m_pc.addIceServers(PeerConnection.IceServer.builder(mesiboIceServer.urls).setUsername(mesiboIceServer.username).setPassword(mesiboIceServer.credential).createIceServer());
    }

    public void addTurnServer(String str, String str2, String str3) {
        if (TextUtils.isEmpty(str) || this.m_server == null || !this.m_server.username.equals(str2) || !this.m_server.credential.equals(str3)) {
            if (this.m_server != null) {
                addTurnServer(this.m_server);
            }
            if (!TextUtils.isEmpty(str)) {
                this.m_server = new MesiboCall.MesiboIceServer();
                this.m_server.credential = str3;
                this.m_server.username = str2;
                this.m_server.urls.add(str);
                return;
            }
            return;
        }
        this.m_server.urls.add(str);
    }

    public boolean changeVideoFormat(int i, int i2, int i3) {
        if (!this.mParams.videoCallEnabled || this.mCapturer == null) {
            return false;
        }
        this.mParams.videoWidth = i;
        this.mParams.videoHeight = i2;
        this.mParams.videoFps = i3;
        this.mCapturer.changeFormat(i, i2, i3);
        return true;
    }

    public void createAnswer() {
        if (!this.mParams.videoCallEnabled || this.mVideoTrack != null || !this.mParams.offerToSend) {
            this.m_pc.createAnswer();
        } else {
            this.mSdpPending = true;
        }
    }

    public void createCall(QampCallsActivity mesiboCallActivity, boolean z, boolean z2) {
        this.mActivity = mesiboCallActivity;
        this.createAudioMute = z;
        this.createVideoMute = z2;
        this.m_pc.createPeerConnection(this.mParams.offerToRecv);
        if (this.mParams.offerToSend) {
            if (this.mParams.audioCallEnabled) {
                this.mAudioTrack = this.m_pc.createAudioTrack();
                this.m_pc.setTrack(this.mAudioTrack);
            }
            if (this.mParams.videoCallEnabled) {
                createCapturer(this.mVideoSource);
            }
        }
    }

    public void createOffer(boolean z) {
        this.renegotiateOnMediaChanged = z;
        this.initiator = true;
        if (!this.mParams.videoCallEnabled || this.mVideoTrack != null || !this.mParams.offerToSend) {
            this.m_pc.createOffer();
        } else {
            this.mSdpPending = true;
        }
    }

    public int getVideoSource() {
        return this.mVideoSource;
    }

    public MesiboVideoView getVideoView(boolean z) {
        if (!this.mParams.videoCallEnabled || this.m_pc == null) {
            return null;
        }
        return z ? this.remoteVideoSink.getTarget() : this.localVideoSink.getTarget();
    }

    /* JADX WARNING: type inference failed for: r0v5, types: [android.content.Context, com.mesibo.calls.api.MesiboCallActivity] */
    public void hangup() {
        if (this.mCameraCapturer != null) {
            this.mCameraCapturer.destroy();
        }
        if (this.mScreenCapturer != null) {
            this.mScreenCapturer.destroy();
        }
        if (this.remoteVideoSink != null) {
            this.remoteVideoSink.stop();
            this.localVideoSink.stop();
        }
        if (this.m_pc != null) {
            this.m_pc.close();
            this.m_pc = null;
        }
        if (this.m_vcf != null) {
            this.m_vcf.stop();
        }
        MesiboScreenSharingService.stop(this.mActivity);
    }

    public void mute(boolean z, boolean z2, boolean z3) {
        boolean z4 = true;
        if (this.m_pc != null) {
            if (z && this.mAudioTrack != null) {
                this.m_pc.setTrackEnabled(this.mAudioTrack, !z3);
            }
            if (z2 && this.mVideoTrack != null) {
                PeerConnectionClient peerConnectionClient = this.m_pc;
                VideoTrack videoTrack = this.mVideoTrack;
                if (z3) {
                    z4 = false;
                }
                peerConnectionClient.setTrackEnabled(videoTrack, z4);
                this.mVideoMutePending = false;
            } else if (z2 && this.mVideoTrack == null) {
                this.mVideoMutePending = z3;
            }
            renegotiate();
        }
    }

    /* JADX WARNING: type inference failed for: r0v1, types: [android.content.Context, com.mesibo.calls.api.MesiboCallActivity] */
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 4097) {
            mediaProjectionPermissionResultCode = i2;
            mediaProjectionPermissionResultData = intent;
            MesiboScreenSharingService.start(this.mActivity, this, mediaProjectionPermissionResultData);
        }
    }

    public void onIceCandidate(IceCandidate iceCandidate) {
        this.mListener.MesiboRtcCall_OnSendCandidate(iceCandidate);
    }

    public void onIceCandidatesRemoved(IceCandidate[] iceCandidateArr) {
        this.mListener.MesiboRtcCall_OnSendRemoveCandidate(iceCandidateArr);
    }

    public void onIceConnected() {
        this.mListener.MesiboRtcCall_OnIceStatus(true);
    }

    public void onIceDisconnected() {
        this.mListener.MesiboRtcCall_OnIceStatus(false);
    }

    public void onLocalDescription(SessionDescription sessionDescription) {
        this.mListener.MesiboRtcCall_OnSendSdp(sessionDescription, !this.initiator);
        if (this.mParams.videoCallEnabled && this.mParams.videoMaxBitrate > 0) {
            new StringBuilder("Set video maximum bitrate: ").append(this.mParams.videoMaxBitrate);
            this.m_pc.setVideoMaxBitrate(Integer.valueOf(this.mParams.videoMaxBitrate));
        }
    }

    public void onPeerConnectionClosed(int i) {
    }

    public void onPeerConnectionCreated(boolean z) {
        if (!z) {
        }
    }

    public void onPeerConnectionError(String str) {
        reportError(str);
    }

    public void onPeerConnectionStatsReady(StatsReport[] statsReportArr) {
    }

    public void onRemoteAudioTrack(AudioTrack audioTrack) {
        this.mListener.MesiboRtcCall_OnRemoteMedia(false);
    }

    public void onRemoteVideoTrack(VideoTrack videoTrack) {
        this.mListener.MesiboRtcCall_OnRemoteMedia(true);
        for (VideoSink addSink : this.remoteRenderers) {
            videoTrack.addSink(addSink);
        }
    }

    public void removeRemoteIceCandidates(IceCandidate[] iceCandidateArr) {
        this.m_pc.removeRemoteIceCandidates(iceCandidateArr);
    }

    public void renegotiate() {
        if (this.initiator && this.renegotiateOnMediaChanged) {
            this.m_pc.createOffer();
        }
    }

    public void setRemoteDescription(SessionDescription sessionDescription) {
        this.m_pc.setRemoteDescription(sessionDescription);
    }

    public void setVideoSource(int i, int i2) {
        if (this.mParams.videoCallEnabled && this.m_pc != null) {
            if (4 == i) {
                setScreenSource();
            } else {
                setCameraSource(i);
            }
        }
    }

    public void setVideoView(MesiboVideoView mesiboVideoView, boolean z) {
        if (this.mParams.videoCallEnabled && this.m_pc != null) {
            EglBase eglBase = this.m_pc.getEglBase();
            if (mesiboVideoView != null) {
                mesiboVideoView._init(eglBase.getEglBaseContext());
            }
            if (z) {
                this.mRemoteVideoView = mesiboVideoView;
                this.remoteVideoSink.setTarget(mesiboVideoView);
                return;
            }
            this.mLocalVideoView = mesiboVideoView;
            this.localVideoSink.setTarget(mesiboVideoView);
        }
    }

    public void startScreenCapturerFromServiceOrActivityResult() {
        createCapturer(4);
    }

    public void switchCamera() {
        int i = 1;
        if (this.mParams.videoCallEnabled && this.m_pc != null && 4 != this.mVideoSource) {
            if (this.mVideoSource == 1) {
                i = 2;
            }
            this.mVideoSource = i;
            this.mCameraSource = this.mVideoSource;
            if (this.m_pc != null && this.mCapturer != null) {
                this.mCapturer.switchCamera();
            }
        }
    }

    public void switchSource() {
        if (this.mParams.videoCallEnabled && this.m_pc != null) {
            if (this.mVideoSource == 4) {
                setVideoSource(this.mCameraSource, 0);
            } else {
                setVideoSource(4, 0);
            }
        }
    }
}
