package com.qamp.app.qampCalls;


import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import org.webrtc.AudioTrack;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection.IceServer;
import org.webrtc.PeerConnectionFactory.Options;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSink;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RtcCall implements MesiboCapturer.MesiboCapturerListener, PeerConnectionClient.PeerConnectionEvents {
    public static final String TAG = "RtcCall";
    private PeerConnectionClient m_pc = null;
    private MesiboVideoCapturerFactory m_vcf = new MesiboVideoCapturerFactory();
    private static Intent mediaProjectionPermissionResultData;
    private static int mediaProjectionPermissionResultCode;
    private MesiboCapturer mCameraCapturer = null;
    private MesiboCapturer mScreenCapturer = null;
    private MesiboCapturer mCapturer = null;
    private QampCallActivity mActivity = null;
    private PeerConnectionClient.PeerConnectionParameters mParams;
    private List<VideoSink> remoteRenderers = new ArrayList();
    private ProxyVideoSink remoteVideoSink = new ProxyVideoSink();
    private ProxyVideoSink localVideoSink = new ProxyVideoSink();
    private Listener mListener = null;
    private MesiboCall.MesiboIceServer m_server = null;
    private MesiboVideoView mRemoteVideoView = null;
    private MesiboVideoView mLocalVideoView = null;
    private int mVideoSource = 1;
    private int mCameraSource;
    private int mDefaultCameraSource;
    private boolean createAudioMute;
    private boolean createVideoMute;
    private AudioTrack mAudioTrack;
    private VideoTrack mVideoTrack;
    private boolean renegotiateOnMediaChanged;
    private boolean mSdpPending;
    private boolean mVideoMutePending;
    private boolean initiator;
    private boolean mScreenCapturerStarted;
    private boolean mScreenCapturerStartEnabled;

    RtcCall(QampCallActivity var1, PeerConnectionClient.PeerConnectionParameters var2, int var3, Listener var4) {
        this.mCameraSource = this.mVideoSource;
        this.mDefaultCameraSource = 1;
        this.mAudioTrack = null;
        this.mVideoTrack = null;
        this.renegotiateOnMediaChanged = false;
        this.mSdpPending = false;
        this.mVideoMutePending = false;
        this.initiator = false;
        this.mScreenCapturerStarted = false;
        this.mScreenCapturerStartEnabled = false;
        this.mActivity = var1;
        this.mParams = var2;
        this.mListener = var4;
        this.mVideoSource = var3;
        this.remoteRenderers.add(this.remoteVideoSink);
        EglBase var5 = null;
        if (var2.videoCallEnabled) {
            var5 = EglBase.create();
        }

        this.m_pc = new PeerConnectionClient(CallManager.getAppContext(), var5, var2, this);
        this.m_pc.createPeerConnectionFactory(new Options());
    }

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

        ScreenSharingService.stop(this.mActivity);
    }

    public void addTurnServer(MesiboCall.MesiboIceServer var1) {
        IceServer var2 = IceServer.builder(var1.urls).setUsername(var1.username).setPassword(var1.credential).createIceServer();
        this.m_pc.addIceServers(var2);
    }

    public void addTurnServer(String var1, String var2, String var3) {
        if (!TextUtils.isEmpty(var1) && this.m_server != null && this.m_server.username.equals(var2) && this.m_server.credential.equals(var3)) {
            this.m_server.urls.add(var1);
        } else {
            if (this.m_server != null) {
                this.addTurnServer(this.m_server);
            }

            if (!TextUtils.isEmpty(var1)) {
                this.m_server = new MesiboCall.MesiboIceServer();
                this.m_server.credential = var3;
                this.m_server.username = var2;
                this.m_server.urls.add(var1);
            }
        }
    }

    public void OnForeground(boolean var1) {
        if (this.remoteVideoSink != null) {
            this.remoteVideoSink.setForeground(var1);
            this.localVideoSink.setForeground(var1);
        }

    }

    public void createOffer(boolean var1) {
        this.renegotiateOnMediaChanged = var1;
        this.initiator = true;
        if (this.mParams.videoCallEnabled && this.mVideoTrack == null && this.mParams.offerToSend) {
            this.mSdpPending = true;
        } else {
            this.m_pc.createOffer();
        }
    }

    public void createAnswer() {
        if (this.mParams.videoCallEnabled && this.mVideoTrack == null && this.mParams.offerToSend) {
            this.mSdpPending = true;
        } else {
            this.m_pc.createAnswer();
        }
    }

    public void createCall(QampCallActivity var1, boolean var2, boolean var3) {
        this.mActivity = var1;
        this.createAudioMute = var2;
        this.createVideoMute = var3;
        this.m_pc.createPeerConnection(this.mParams.offerToRecv);
        if (this.mParams.offerToSend) {
            if (this.mParams.audioCallEnabled) {
                this.mAudioTrack = this.m_pc.createAudioTrack();
                this.m_pc.setTrack(this.mAudioTrack);
            }

            if (this.mParams.videoCallEnabled) {
                this.createCapturer(this.mVideoSource);
            }
        }

    }

    public void renegotiate() {
        if (this.initiator && this.renegotiateOnMediaChanged) {
            this.m_pc.createOffer();
        }

    }

    public void mute(boolean var1, boolean var2, boolean var3) {
        if (this.m_pc != null) {
            if (var1 && this.mAudioTrack != null) {
                this.m_pc.setTrackEnabled(this.mAudioTrack, !var3);
            }

            if (var2 && this.mVideoTrack != null) {
                this.m_pc.setTrackEnabled(this.mVideoTrack, !var3);
                this.mVideoMutePending = false;
            } else if (var2 && this.mVideoTrack == null) {
                this.mVideoMutePending = var3;
            }

            this.renegotiate();
        }
    }

    public void setVideoView(MesiboVideoView var1, boolean var2) {
        if (this.mParams.videoCallEnabled) {
            EglBase var3 = this.m_pc.getEglBase();
            if (var1 != null) {
                var1._init(var3.getEglBaseContext());
            }

            if (var2) {
                this.mRemoteVideoView = var1;
                this.remoteVideoSink.setTarget(var1);
            } else {
                this.mLocalVideoView = var1;
                this.localVideoSink.setTarget(var1);
            }
        }
    }

    public MesiboVideoView getVideoView(boolean var1) {
        if (!this.mParams.videoCallEnabled) {
            return null;
        } else {
            return var1 ? this.remoteVideoSink.getTarget() : this.localVideoSink.getTarget();
        }
    }

    public void onActivityResult(int var1, int var2, Intent var3) {
        if (var1 == 4097) {
            mediaProjectionPermissionResultCode = var2;
            mediaProjectionPermissionResultData = var3;
            ScreenSharingService.start(this.mActivity, this, mediaProjectionPermissionResultData);
        }
    }

    public void startScreenCapturerFromServiceOrActivityResult() {
        this.createCapturer(4);
    }

    public boolean changeVideoFormat(int var1, int var2, int var3) {
        if (this.mParams.videoCallEnabled && this.mCapturer != null) {
            this.mParams.videoWidth = var1;
            this.mParams.videoHeight = var2;
            this.mParams.videoFps = var3;
            this.mCapturer.changeFormat(var1, var2, var3);
            return true;
        } else {
            return false;
        }
    }

    private void setCurrentCapturer(MesiboCapturer var1) {
        this.mCapturer = var1;
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
            this.renegotiate();
        }
    }

    private boolean createCapturer(int var1) {
        if ((4 != var1 || this.mScreenCapturerStarted) && this.mCapturer != null) {
            this.mCapturer.setRenderer((VideoSink)null);
            this.mCapturer.stop();
        }

        if (4 == this.mVideoSource) {
            if (this.mScreenCapturer == null && !this.mScreenCapturerStarted) {
                this.m_vcf.startScreenCapture(this.mActivity);
                this.mScreenCapturerStarted = true;
                this.mScreenCapturerStartEnabled = true;
                return false;
            }

            (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
                public void run() {
                    if (null == RtcCall.this.mScreenCapturer) {
                        VideoCapturer var1;
                        if ((var1 = RtcCall.this.m_vcf.createScreenCapturer(RtcCall.mediaProjectionPermissionResultCode, RtcCall.mediaProjectionPermissionResultData)) != null) {
                            RtcCall.this.mScreenCapturer = new MesiboCapturer(RtcCall.this, RtcCall.this.m_pc, var1, RtcCall.this.localVideoSink, RtcCall.this.mParams.videoHeight, RtcCall.this.mParams.videoWidth, RtcCall.this.mParams.videoFps);
                        }
                    } else {
                        RtcCall.this.mScreenCapturer.restart(RtcCall.this.localVideoSink);
                    }

                    if (null != RtcCall.this.mScreenCapturer) {
                        RtcCall.this.setCurrentCapturer(RtcCall.this.mScreenCapturer);
                    }

                }
            }, 1000L);
        } else {
            if (this.mCameraCapturer == null) {
                VideoCapturer var2 = this.m_vcf.create(this.mActivity, 1 == this.mVideoSource, this.mParams.useCamera2, this.mParams.captureToTexture, (String)null);
                this.mCameraCapturer = new MesiboCapturer(this, this.m_pc, var2, this.localVideoSink, this.mParams.videoWidth, this.mParams.videoHeight, this.mParams.videoFps);
            } else {
                this.mCameraCapturer.restart(this.localVideoSink);
            }

            this.setCurrentCapturer(this.mCameraCapturer);
        }

        if (this.mCapturer == null) {
            return false;
        } else {
            this.mVideoSource = var1;
            return true;
        }
    }

    private void setCameraSource(int var1) {
        if (var1 == 0) {
            var1 = this.mDefaultCameraSource;
        }

        this.mDefaultCameraSource = var1;
        this.mVideoSource = var1;
        this.createCapturer(var1);
    }

    private void setScreenSource() {
        this.mVideoSource = 4;
        this.createCapturer(4);
    }

    public void setVideoSource(int var1, int var2) {
        if (this.mParams.videoCallEnabled) {
            if (4 == var1) {
                this.setScreenSource();
            } else {
                this.setCameraSource(var1);
            }
        }
    }

    public int getVideoSource() {
        return this.mVideoSource;
    }

    public void switchCamera() {
        if (this.mParams.videoCallEnabled) {
            if (4 != this.mVideoSource) {
                this.mVideoSource = this.mVideoSource == 1 ? 2 : 1;
                this.mCameraSource = this.mVideoSource;
                if (this.m_pc != null && this.mCapturer != null) {
                    this.mCapturer.switchCamera();
                }

            }
        }
    }

    public void switchSource() {
        if (this.mParams.videoCallEnabled) {
            if (this.mVideoSource == 4) {
                this.setVideoSource(this.mCameraSource, 0);
            } else {
                this.setVideoSource(4, 0);
            }
        }
    }

    public void MesiboCapturer_OnSwitchCamera(MesiboCapturer var1, boolean var2) {
        if (var2) {
            this.mVideoSource = 1;
        } else {
            this.mVideoSource = 2;
        }

        this.MesiboCapturer_OnStarted(var1, true);
    }

    public void MesiboCapturer_OnStarted(MesiboCapturer var1, final boolean var2) {
        if (var2) {
            (new Handler(Looper.getMainLooper())).post(new Runnable() {
                public void run() {
                    RtcCall.this.mListener.MesiboRtcCall_OnCaptureStarted(var2);
                }
            });
        }
    }

    public void addRemoteIceCandidate(IceCandidate var1) {
        this.m_pc.addRemoteIceCandidate(var1);
    }

    public void removeRemoteIceCandidates(IceCandidate[] var1) {
        this.m_pc.removeRemoteIceCandidates(var1);
    }

    public void setRemoteDescription(SessionDescription var1) {
        this.m_pc.setRemoteDescription(var1);
    }

    public void onPeerConnectionCreated(boolean var1) {
        if (var1) {
            ;
        }
    }

    public void onLocalDescription(SessionDescription var1) {
        this.mListener.MesiboRtcCall_OnSendSdp(var1, !this.initiator);
        if (this.mParams.videoCallEnabled && this.mParams.videoMaxBitrate > 0) {
            (new StringBuilder("Set video maximum bitrate: ")).append(this.mParams.videoMaxBitrate);
            this.m_pc.setVideoMaxBitrate(this.mParams.videoMaxBitrate);
        }

    }

    public void onIceCandidate(IceCandidate var1) {
        this.mListener.MesiboRtcCall_OnSendCandidate(var1);
    }

    public void onIceCandidatesRemoved(IceCandidate[] var1) {
        this.mListener.MesiboRtcCall_OnSendRemoveCandidate(var1);
    }

    public void onIceConnected() {
        this.mListener.MesiboRtcCall_OnIceStatus(true);
    }

    public void onIceDisconnected() {
        this.mListener.MesiboRtcCall_OnIceStatus(false);
    }

    public void onPeerConnectionStatsReady(StatsReport[] var1) {
    }

    public void onRemoteAudioTrack(AudioTrack var1) {
        this.mListener.MesiboRtcCall_OnRemoteMedia(false);
    }

    public void onRemoteVideoTrack(VideoTrack var1) {
        this.mListener.MesiboRtcCall_OnRemoteMedia(true);
        Iterator var2 = this.remoteRenderers.iterator();

        while(var2.hasNext()) {
            VideoSink var3 = (VideoSink)var2.next();
            var1.addSink(var3);
        }

    }

    public void onPeerConnectionError(String var1) {
        this.reportError(var1);
    }

    public void onPeerConnectionClosed(int var1) {
    }

    private void reportError(String var1) {
    }

    public interface Listener {
        void MesiboRtcCall_OnCaptureStarted(boolean var1);

        void MesiboRtcCall_OnIceStatus(boolean var1);

        void MesiboRtcCall_OnRemoteMedia(boolean var1);

        void MesiboRtcCall_OnSendSdp(SessionDescription var1, boolean var2);

        void MesiboRtcCall_OnSendCandidate(IceCandidate var1);

        void MesiboRtcCall_OnSendRemoveCandidate(IceCandidate[] var1);
    }
}

