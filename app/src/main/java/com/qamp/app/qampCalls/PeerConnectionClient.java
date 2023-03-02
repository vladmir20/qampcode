package com.qamp.app.qampCalls;


import android.content.Context;
import android.os.Build.VERSION;
import android.os.Environment;

import androidx.annotation.Nullable;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.DataChannel.Buffer;
import org.webrtc.DataChannel.Init;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.Logging.Severity;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaConstraints.KeyValuePair;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.MediaStreamTrack.MediaType;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnection.BundlePolicy;
import org.webrtc.PeerConnection.ContinualGatheringPolicy;
import org.webrtc.PeerConnection.IceConnectionState;
import org.webrtc.PeerConnection.IceGatheringState;
import org.webrtc.PeerConnection.IceServer;
import org.webrtc.PeerConnection.KeyType;
import org.webrtc.PeerConnection.Observer;
import org.webrtc.PeerConnection.RTCConfiguration;
import org.webrtc.PeerConnection.RtcpMuxPolicy;
import org.webrtc.PeerConnection.SdpSemantics;
import org.webrtc.PeerConnection.SignalingState;
import org.webrtc.PeerConnection.TcpCandidatePolicy;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.PeerConnectionFactory.InitializationOptions;
import org.webrtc.PeerConnectionFactory.Options;
import org.webrtc.RtpParameters;
import org.webrtc.RtpParameters.Encoding;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpSender;
import org.webrtc.RtpTransceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.SoftwareVideoEncoderFactory;
import org.webrtc.StatsObserver;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCodecInfo;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoTrack;
import org.webrtc.audio.AudioDeviceModule;
import org.webrtc.audio.JavaAudioDeviceModule;
import org.webrtc.audio.JavaAudioDeviceModule.AudioRecordErrorCallback;
import org.webrtc.audio.JavaAudioDeviceModule.AudioRecordStartErrorCode;
import org.webrtc.audio.JavaAudioDeviceModule.AudioTrackErrorCallback;
import org.webrtc.audio.JavaAudioDeviceModule.AudioTrackStartErrorCode;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PeerConnectionClient {
    public static final String VIDEO_TRACK_ID = "mesibo_av0";
    public static final String AUDIO_TRACK_ID = "mesibo_aa0";
    public static final String VIDEO_TRACK_TYPE = "video";
    private static final String TAG = "MesiboPC";
    public static final String VIDEO_CODEC_VP8 = "VP8";
    public static final String VIDEO_CODEC_VP9 = "VP9";
    public static final String VIDEO_CODEC_H264 = "H264";
    private static final String VIDEO_CODEC_H264_BASELINE = "H264 Baseline";
    private static final String VIDEO_CODEC_H264_HIGH = "H264 High";
    public static final String AUDIO_CODEC_OPUS = "opus";
    public static final String AUDIO_CODEC_ISAC = "ISAC";
    private static final String VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate";
    private static final String VIDEO_FLEXFEC_FIELDTRIAL = "WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/";
    private static final String VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL = "WebRTC-IntelVP8/Enabled/";
    private static final String DISABLE_WEBRTC_AGC_FIELDTRIAL = "WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/";
    private static final String ENABLE_WEBRTC_MEDIATEK_FIELDTRIAL = "WebRTC-MediaTekH264/Enabled/";
    private static final String AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate";
    private static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
    private static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
    private static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
    private static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";
    private static final String DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement";
    private static final int BPS_IN_KBPS = 1000;
    private static final String RTCEVENTLOG_OUTPUT_DIR_NAME = "rtc_event_log";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final PCObserver pcObserver = new PCObserver();
    private final SDPObserver sdpObserver = new SDPObserver();
    private final Timer statsTimer = new Timer();
    private final EglBase rootEglBase;
    private final Context appContext;
    private final PeerConnectionParameters peerConnectionParameters;
    private final PeerConnectionEvents events;
    @Nullable
    private PeerConnectionFactory factory;
    @Nullable
    private PeerConnection peerConnection = null;
    @Nullable
    private AudioSource audioSource;
    @Nullable
    private SurfaceTextureHelper surfaceTextureHelper = null;
    private boolean preferIsac;
    private boolean isError = false;
    private MediaConstraints sdpMediaConstraints;
    @Nullable
    private List<IceCandidate> queuedRemoteCandidates;
    private boolean isInitiator;
    @Nullable
    private SessionDescription localSdp;
    @Nullable
    private RtpSender localVideoSender;
    @Nullable
    private AudioTrack localAudioTrack;
    @Nullable
    private DataChannel dataChannel;
    private final boolean dataChannelEnabled;
    @Nullable
    private RtcEventLog rtcEventLog;
    @Nullable
    private RecordedAudioToFileController saveRecordedAudioToFile = null;
    private boolean audioMute = false;
    private boolean videoMute = false;
    private List<IceServer> mIceServers = new ArrayList();
    private static boolean mHardwareEncodingChecked = false;
    private static boolean mHardwareEncodingSupported = true;
    private static boolean mH264Supported = false;

    private void initStunServers() {
        IceServer var1 = IceServer.builder("stun:stun.l.google.com:19302").setUsername("").setPassword("").createIceServer();
        this.mIceServers.add(var1);
        var1 = IceServer.builder("stun:stun1.l.google.com:19302").setUsername("").setPassword("").createIceServer();
        this.mIceServers.add(var1);
        var1 = IceServer.builder("stun:stun2.l.google.com:19302").setUsername("").setPassword("").createIceServer();
        this.mIceServers.add(var1);
    }

    public PeerConnectionClient(Context var1, EglBase var2, PeerConnectionParameters var3, PeerConnectionEvents var4) {
        this.rootEglBase = var2;
        this.appContext = var1;
        this.events = var4;
        this.peerConnectionParameters = var3;
        this.dataChannelEnabled = var3.dataChannelParameters != null;
        this.initStunServers();
        if (var3.videoCallEnabled) {
            if (var3.videoCodec == null) {
                var3.videoCodec = "VP8";
            }

            (new StringBuilder("Preferred video codec: ")).append(getSdpVideoCodecName(var3));
        }

        String var5 = getFieldTrials(var3);
        PeerConnectionFactory.initialize(InitializationOptions.builder(var1).setFieldTrials(var5).setEnableInternalTracer(true).createInitializationOptions());
    }

    public void createPeerConnectionFactory(Options var1) {
        if (this.factory == null) {
            this.createPeerConnectionFactoryInternal(var1);
        }
    }

    public PeerConnectionFactory getFactory() {
        return this.factory;
    }

    public Context getAppContext() {
        return this.appContext;
    }

    public String getVideoTrackId() {
        return "mesibo_av0";
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    @Nullable
    public SurfaceTextureHelper getSurfaceTextureHelper() {
        if (this.surfaceTextureHelper == null) {
            this.surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", this.rootEglBase.getEglBaseContext());
        }

        return this.surfaceTextureHelper;
    }

    public void createPeerConnection(boolean var1) {
        if (this.peerConnectionParameters != null) {
            executor.execute(() -> {
                try {
                    this.createPeerConnectionInternal(var1);
                    this.maybeCreateAndStartRtcEventLog();
                    this.events.onPeerConnectionCreated(true);
                } catch (Exception var2) {
                    this.events.onPeerConnectionCreated(false);
                    this.reportError("Failed to create peer connection: " + var2.getMessage());
                    throw var2;
                }
            });
        }
    }

    public EglBase getEglBase() {
        return this.rootEglBase;
    }

    public void addIceServers(IceServer var1) {
        this.mIceServers.add(var1);
    }

    public void close() {
        executor.execute(this::closeInternal);
    }

    private boolean isVideoCallEnabled() {
        return this.peerConnectionParameters.videoCallEnabled;
    }

    private boolean isAudioCallEnabled() {
        return this.peerConnectionParameters.audioCallEnabled;
    }

    public boolean isH264Supported() {
        this.checkHardwareCodecs();
        return mH264Supported;
    }

    public void checkHardwareCodecs() {
        if (!mHardwareEncodingChecked) {
            mHardwareEncodingChecked = true;
            if (VERSION.SDK_INT < 19) {
                mHardwareEncodingSupported = false;
                mH264Supported = false;
                CallManager.getInstance().forceVP8();
            } else {
                DefaultVideoEncoderFactory var1 = new DefaultVideoEncoderFactory(this.rootEglBase.getEglBaseContext(), true, false);
                boolean var2 = false;
                VideoCodecInfo[] var5 = var1.getSupportedCodecs();

                for(int var3 = 0; var3 < var5.length; ++var3) {
                    VideoCodecInfo var4;
                    if ((var4 = var5[var3]).name.toUpperCase().contains("VP8")) {
                        var2 = true;
                    } else if (var4.name.toUpperCase().contains("H264")) {
                        mH264Supported = true;
                    }
                }

                if (!var2) {
                    mHardwareEncodingSupported = false;
                    mH264Supported = false;
                }

                if (!mH264Supported) {
                    CallManager.getInstance().forceVP8();
                }

            }
        }
    }

    private void createPeerConnectionFactoryInternal(Options var1) {
        this.isError = false;
        if (this.peerConnectionParameters.tracing) {
            PeerConnectionFactory.startInternalTracingCapture(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "webrtc-trace.txt");
        }

        this.preferIsac = this.peerConnectionParameters.audioCodec != null && this.peerConnectionParameters.audioCodec.equals("ISAC");
        if (this.peerConnectionParameters.saveInputAudioToFile && !this.peerConnectionParameters.useOpenSLES) {
            this.saveRecordedAudioToFile = new RecordedAudioToFileController(executor);
        }

        AudioDeviceModule var2 = this.createJavaAudioDevice();
        if (var1 != null) {
            (new StringBuilder("Factory networkIgnoreMask option: ")).append(var1.networkIgnoreMask);
        }

        if (!this.peerConnectionParameters.videoCallEnabled) {
            this.factory = PeerConnectionFactory.builder().setOptions(var1).setAudioDeviceModule(var2).createPeerConnectionFactory();
            var2.release();
        } else {
            boolean var3 = "H264 High".equals(this.peerConnectionParameters.videoCodec);
            this.checkHardwareCodecs();
            Object var4;
            Object var5;
            if (mHardwareEncodingSupported) {
                var5 = new DefaultVideoEncoderFactory(this.rootEglBase.getEglBaseContext(), true, var3);
                var4 = new DefaultVideoDecoderFactory(this.rootEglBase.getEglBaseContext());
            } else {
                var5 = new SoftwareVideoEncoderFactory();
                var4 = new SoftwareVideoDecoderFactory();
            }

            this.factory = PeerConnectionFactory.builder().setOptions(var1).setAudioDeviceModule(var2).setVideoEncoderFactory((VideoEncoderFactory)var5).setVideoDecoderFactory((VideoDecoderFactory)var4).createPeerConnectionFactory();
            var2.release();
        }
    }

    AudioDeviceModule createJavaAudioDevice() {
        AudioRecordErrorCallback var1 = new AudioRecordErrorCallback() {
            public void onWebRtcAudioRecordInitError(String var1) {
                PeerConnectionClient.this.reportError(var1);
            }

            public void onWebRtcAudioRecordStartError(AudioRecordStartErrorCode var1, String var2) {
                (new StringBuilder("onWebRtcAudioRecordStartError: ")).append(var1).append(". ").append(var2);
                PeerConnectionClient.this.reportError(var2);
            }

            public void onWebRtcAudioRecordError(String var1) {
                PeerConnectionClient.this.reportError(var1);
            }
        };
        AudioTrackErrorCallback var2 = new AudioTrackErrorCallback() {
            public void onWebRtcAudioTrackInitError(String var1) {
                PeerConnectionClient.this.reportError(var1);
            }

            public void onWebRtcAudioTrackStartError(AudioTrackStartErrorCode var1, String var2) {
                (new StringBuilder("onWebRtcAudioTrackStartError: ")).append(var1).append(". ").append(var2);
                PeerConnectionClient.this.reportError(var2);
            }

            public void onWebRtcAudioTrackError(String var1) {
                PeerConnectionClient.this.reportError(var1);
            }
        };
        return JavaAudioDeviceModule.builder(this.appContext).setSamplesReadyCallback(this.saveRecordedAudioToFile).setUseHardwareAcousticEchoCanceler(!this.peerConnectionParameters.disableBuiltInAEC).setUseHardwareNoiseSuppressor(!this.peerConnectionParameters.disableBuiltInNS).setAudioRecordErrorCallback(var1).setAudioTrackErrorCallback(var2).createAudioDeviceModule();
    }

    private void checkTransceiver() {
        this.peerConnection.getTransceivers();
        Iterator var1 = this.peerConnection.getTransceivers().iterator();

        while(var1.hasNext()) {
            RtpTransceiver var2;
            (var2 = (RtpTransceiver)var1.next()).getDirection();
            var2.getMid();
            var2.getMediaType();
            MediaType var10000 = MediaType.MEDIA_TYPE_AUDIO;
            RtpSender var3;
            (var3 = var2.getSender()).track();
            var3.getParameters();
        }

    }

    private RTCConfiguration initRtcConfig(List<IceServer> var1) {
        RTCConfiguration var2;
        (var2 = new RTCConfiguration(var1)).tcpCandidatePolicy = this.peerConnectionParameters.tcpCandidates ? TcpCandidatePolicy.ENABLED : TcpCandidatePolicy.DISABLED;
        var2.bundlePolicy = BundlePolicy.MAXBUNDLE;
        var2.rtcpMuxPolicy = RtcpMuxPolicy.REQUIRE;
        var2.continualGatheringPolicy = ContinualGatheringPolicy.GATHER_CONTINUALLY;
        var2.keyType = KeyType.ECDSA;
        var2.enableDtlsSrtp = !this.peerConnectionParameters.loopback;
        var2.sdpSemantics = this.peerConnectionParameters.unifiedPlan ? SdpSemantics.UNIFIED_PLAN : SdpSemantics.PLAN_B;
        return var2;
    }

    private void createPeerConnectionInternal(boolean var1) {
        if (this.factory != null && !this.isError) {
            this.queuedRemoteCandidates = new ArrayList();
            this.sdpMediaConstraints = new MediaConstraints();
            this.sdpMediaConstraints.mandatory.add(new KeyValuePair("OfferToReceiveAudio", Boolean.toString(this.isAudioCallEnabled() && var1)));
            this.sdpMediaConstraints.mandatory.add(new KeyValuePair("OfferToReceiveVideo", Boolean.toString(this.isVideoCallEnabled() && var1)));
            RTCConfiguration var2 = this.initRtcConfig(this.mIceServers);
            this.peerConnection = this.factory.createPeerConnection(var2, this.pcObserver);
            if (this.dataChannelEnabled) {
                Init var3;
                (var3 = new Init()).ordered = this.peerConnectionParameters.dataChannelParameters.ordered;
                var3.negotiated = this.peerConnectionParameters.dataChannelParameters.negotiated;
                var3.maxRetransmits = this.peerConnectionParameters.dataChannelParameters.maxRetransmits;
                var3.maxRetransmitTimeMs = this.peerConnectionParameters.dataChannelParameters.maxRetransmitTimeMs;
                var3.id = this.peerConnectionParameters.dataChannelParameters.id;
                var3.protocol = this.peerConnectionParameters.dataChannelParameters.protocol;
                this.dataChannel = this.peerConnection.createDataChannel("mesibo data", var3);
            }

            this.isInitiator = false;
            Logging.enableLogToDebugOutput(Severity.LS_INFO);
            if (this.saveRecordedAudioToFile != null) {
                this.saveRecordedAudioToFile.start();
            }

        }
    }

    private File createRtcEventLogOutputFile() {
        SimpleDateFormat var1 = new SimpleDateFormat("yyyyMMdd_hhmm_ss", Locale.getDefault());
        Date var2 = new Date();
        String var3 = "event_log_" + var1.format(var2) + ".log";
        return new File(this.appContext.getDir("rtc_event_log", 0), var3);
    }

    private void maybeCreateAndStartRtcEventLog() {
        if (this.appContext != null && this.peerConnection != null) {
            if (this.peerConnectionParameters.enableRtcEventLog) {
                this.rtcEventLog = new RtcEventLog(this.peerConnection);
                this.rtcEventLog.start(this.createRtcEventLogOutputFile());
            }
        }
    }

    private void closeInternal() {
        try {
            this.statsTimer.cancel();
            if (this.dataChannel != null) {
                this.dataChannel.dispose();
                this.dataChannel = null;
            }

            if (this.rtcEventLog != null) {
                this.rtcEventLog.stop();
                this.rtcEventLog = null;
            }

            if (this.peerConnection != null) {
                this.peerConnection.dispose();
                this.peerConnection = null;
            }

            if (this.audioSource != null) {
                this.audioSource.dispose();
                this.audioSource = null;
            }

            if (this.surfaceTextureHelper != null) {
                this.surfaceTextureHelper.dispose();
                this.surfaceTextureHelper = null;
            }

            if (this.saveRecordedAudioToFile != null) {
                this.saveRecordedAudioToFile.stop();
                this.saveRecordedAudioToFile = null;
            }

            if (this.factory != null) {
                this.factory.dispose();
                this.factory = null;
            }

            if (this.rootEglBase != null) {
                this.rootEglBase.release();
            }

            if (this.events != null) {
                this.events.onPeerConnectionClosed(0);
            }

            PeerConnectionFactory.stopInternalTracingCapture();
            PeerConnectionFactory.shutdownInternalTracer();
        } catch (Exception var1) {
        }
    }

    private void getStats() {
        if (this.peerConnection != null && !this.isError) {
            this.peerConnection.getStats(new StatsObserver() {
                public void onComplete(StatsReport[] var1) {
                    PeerConnectionClient.this.events.onPeerConnectionStatsReady(var1);
                }
            }, (MediaStreamTrack)null);
        }
    }

    public void enableStatsEvents(boolean var1, int var2) {
        if (var1) {
            try {
                this.statsTimer.schedule(new TimerTask() {
                    public void run() {
                        PeerConnectionClient.executor.execute(() -> {
                            PeerConnectionClient.this.getStats();
                        });
                    }
                }, 0L, (long)var2);
            } catch (Exception var3) {
            }
        } else {
            this.statsTimer.cancel();
        }
    }

    public void setTrackEnabled(MediaStreamTrack var1, boolean var2) {
        executor.execute(() -> {
            if (var1 != null) {
                var1.setEnabled(var2);
            }

        });
    }

    public void createOffer() {
        executor.execute(() -> {
            if (this.peerConnection != null && !this.isError) {
                this.isInitiator = true;
                this.peerConnection.createOffer(this.sdpObserver, this.sdpMediaConstraints);
            }

        });
    }

    public void createAnswer() {
        executor.execute(() -> {
            if (this.peerConnection != null && !this.isError) {
                this.isInitiator = false;
                this.peerConnection.createAnswer(this.sdpObserver, this.sdpMediaConstraints);
            }

        });
    }

    public void addRemoteIceCandidate(IceCandidate var1) {
        executor.execute(() -> {
            if (this.peerConnection != null && !this.isError) {
                if (this.queuedRemoteCandidates != null) {
                    this.queuedRemoteCandidates.add(var1);
                    return;
                }

                this.peerConnection.addIceCandidate(var1);
            }

        });
    }

    public void removeRemoteIceCandidates(IceCandidate[] var1) {
        executor.execute(() -> {
            if (this.peerConnection != null && !this.isError) {
                this.drainCandidates();
                this.peerConnection.removeIceCandidates(var1);
            }
        });
    }

    public void setRemoteDescription(SessionDescription var1) {
        final SessionDescription tempVar1 = var1;
        executor.execute(() -> {
            if (this.peerConnection != null && !this.isError) {
                String var2 = tempVar1.description;
                if (this.preferIsac) {
                    var2 = preferCodec(var2, "ISAC", true);
                }

                if (this.isVideoCallEnabled()) {
                    var2 = preferCodec(var2, getSdpVideoCodecName(this.peerConnectionParameters), false);
                }

                if (this.peerConnectionParameters.audioStartBitrate > 0) {
                    var2 = setStartBitrate("opus", false, var2, this.peerConnectionParameters.audioStartBitrate);
                }
                final SessionDescription tempVar2 = new SessionDescription(tempVar1.type, var2);
                this.peerConnection.setRemoteDescription(this.sdpObserver, tempVar2);
            }
        });
    }

    public void setVideoMaxBitrate(@Nullable Integer var1) {
        executor.execute(() -> {
            if (this.peerConnection != null && this.localVideoSender != null && !this.isError) {
                (new StringBuilder("Requested max video bitrate: ")).append(var1);
                if (this.localVideoSender != null) {
                    RtpParameters var2;
                    if ((var2 = this.localVideoSender.getParameters()).encodings.size() != 0) {
                        for(Iterator var3 = var2.encodings.iterator(); var3.hasNext(); ((Encoding)var3.next()).maxBitrateBps = var1 == null ? null : var1 * 1000) {
                        }

                        this.localVideoSender.setParameters(var2);
                        (new StringBuilder("Configured max video bitrate to: ")).append(var1);
                    }
                }
            }
        });
    }

    private void reportError(String var1) {
        executor.execute(() -> {
            if (!this.isError) {
                this.events.onPeerConnectionError(var1);
                this.isError = true;
            }

        });
    }

    @Nullable
    protected AudioTrack createAudioTrack() {
        MediaConstraints var1 = new MediaConstraints();
        if (this.peerConnectionParameters.noAudioProcessing) {
            var1.mandatory.add(new KeyValuePair("googEchoCancellation", "false"));
            var1.mandatory.add(new KeyValuePair("googAutoGainControl", "false"));
            var1.mandatory.add(new KeyValuePair("googHighpassFilter", "false"));
            var1.mandatory.add(new KeyValuePair("googNoiseSuppression", "false"));
        }

        this.audioSource = this.factory.createAudioSource(var1);
        this.localAudioTrack = this.factory.createAudioTrack("mesibo_aa0", this.audioSource);
        this.localAudioTrack.setEnabled(true);
        return this.localAudioTrack;
    }

    public void setTrack(MediaStreamTrack var1) {
        executor.execute(() -> {
            Iterator var2 = this.peerConnection.getSenders().iterator();

            RtpSender var3;
            do {
                if (!var2.hasNext()) {
                    this.peerConnection.addTrack(var1);
                    return;
                }
            } while((var3 = (RtpSender)var2.next()).track() == null || !var3.track().kind().equals(var1.kind()));

            var3.setTrack(var1, false);
        });
    }

    public void replaceVideoTrack(MesiboCapturer var1) {
        if (this.localVideoSender == null) {
            this.findVideoSender();
            if (this.localVideoSender == null) {
                this.peerConnection.addTrack(var1.getTrack());
                return;
            }
        }

        this.localVideoSender.setTrack(var1.getTrack(), false);
    }

    private void findVideoSender() {
        Iterator var1 = this.peerConnection.getSenders().iterator();

        while(var1.hasNext()) {
            RtpSender var2;
            if ((var2 = (RtpSender)var1.next()).track() != null && var2.track().kind().equals("video")) {
                this.localVideoSender = var2;
            }
        }

    }

    @Nullable
    private VideoTrack getRemoteVideoTrack() {
        Iterator var1 = this.peerConnection.getTransceivers().iterator();

        MediaStreamTrack var2;
        do {
            if (!var1.hasNext()) {
                return null;
            }
        } while(!((var2 = ((RtpTransceiver)var1.next()).getReceiver().track()) instanceof VideoTrack));

        return (VideoTrack)var2;
    }

    private static String getSdpVideoCodecName(PeerConnectionParameters var0) {
        String var2 = var0.videoCodec;
        byte var1 = -1;
        switch(var2.hashCode()) {
            case -2140422726:
                if (var2.equals("H264 High")) {
                    var1 = 2;
                }
                break;
            case -1031013795:
                if (var2.equals("H264 Baseline")) {
                    var1 = 3;
                }
                break;
            case 85182:
                if (var2.equals("VP8")) {
                    var1 = 0;
                }
                break;
            case 85183:
                if (var2.equals("VP9")) {
                    var1 = 1;
                }
        }

        switch(var1) {
            case 0:
                return "VP8";
            case 1:
                return "VP9";
            case 2:
            case 3:
                return "H264";
            default:
                return "VP8";
        }
    }

    private static String getFieldTrials(PeerConnectionParameters var0) {
        String var1 = "";
        if (var0.videoFlexfecEnabled) {
            var1 = var1 + "WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/";
        }

        var1 = var1 + "WebRTC-IntelVP8/Enabled/";
        if (var0.disableWebRtcAGCAndHPF) {
            var1 = var1 + "WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/";
        }

        return var1;
    }

    private static String setStartBitrate(String var0, boolean var1, String var2, int var3) {
        String[] var4 = var2.split("\r\n");
        int var5 = -1;
        boolean var6 = false;
        String var7 = null;
        Pattern var8 = Pattern.compile("^a=rtpmap:(\\d+) " + var0 + "(/\\d+)+[\r]?$");

        int var9;
        for(var9 = 0; var9 < var4.length; ++var9) {
            Matcher var10;
            if ((var10 = var8.matcher(var4[var9])).matches()) {
                var7 = var10.group(1);
                var5 = var9;
                break;
            }
        }

        if (var7 == null) {
            (new StringBuilder("No rtpmap for ")).append(var0).append(" codec");
            return var2;
        } else {
            (new StringBuilder("Found ")).append(var0).append(" rtpmap ").append(var7).append(" at ").append(var4[var5]);
            var8 = Pattern.compile("^a=fmtp:" + var7 + " \\w+=\\d+.*[\r]?$");

            for(var9 = 0; var9 < var4.length; ++var9) {
                if (var8.matcher(var4[var9]).matches()) {
                    (new StringBuilder("Found ")).append(var0).append(" ").append(var4[var9]);
                    if (var1) {
                        var4[var9] = var4[var9] + "; x-google-start-bitrate=" + var3;
                    } else {
                        var4[var9] = var4[var9] + "; maxaveragebitrate=" + var3 * 1000;
                    }

                    (new StringBuilder("Update remote SDP line: ")).append(var4[var9]);
                    var6 = true;
                    break;
                }
            }

            StringBuilder var11 = new StringBuilder();

            for(int var12 = 0; var12 < var4.length; ++var12) {
                var11.append(var4[var12]).append("\r\n");
                if (!var6 && var12 == var5) {
                    if (var1) {
                        var0 = "a=fmtp:" + var7 + " x-google-start-bitrate=" + var3;
                    } else {
                        var0 = "a=fmtp:" + var7 + " maxaveragebitrate=" + var3 * 1000;
                    }

                    var11.append(var0).append("\r\n");
                }
            }

            return var11.toString();
        }
    }

    private static int findMediaDescriptionLine(boolean var0, String[] var1) {
        String var3 = var0 ? "m=audio " : "m=video ";

        for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2].startsWith(var3)) {
                return var2;
            }
        }

        return -1;
    }

    private static String joinString(Iterable<? extends CharSequence> var0, String var1, boolean var2) {
        Iterator var4;
        if (!(var4 = var0.iterator()).hasNext()) {
            return "";
        } else {
            StringBuilder var3 = new StringBuilder((CharSequence)var4.next());

            while(var4.hasNext()) {
                var3.append(var1).append((CharSequence)var4.next());
            }

            if (var2) {
                var3.append(var1);
            }

            return var3.toString();
        }
    }

    @Nullable
    private static String movePayloadTypesToFront(List<String> var0, String var1) {
        List var2;
        if ((var2 = Arrays.asList(var1.split(" "))).size() <= 3) {
            return null;
        } else {
            List var4 = var2.subList(0, 3);
            ArrayList var5;
            (var5 = new ArrayList(var2.subList(3, var2.size()))).removeAll(var0);
            ArrayList var3;
            (var3 = new ArrayList()).addAll(var4);
            var3.addAll(var0);
            var3.addAll(var5);
            return joinString(var3, " ", false);
        }
    }

    private static String preferCodec(String var0, String var1, boolean var2) {
        String[] var3 = var0.split("\r\n");
        int var10;
        if ((var10 = findMediaDescriptionLine(var2, var3)) == -1) {
            return var0;
        } else {
            ArrayList var4 = new ArrayList();
            Pattern var9 = Pattern.compile("^a=rtpmap:(\\d+) " + var1 + "(/\\d+)+[\r]?$");
            String[] var5 = var3;
            int var6 = var3.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String var8 = var5[var7];
                Matcher var12;
                if ((var12 = var9.matcher(var8)).matches()) {
                    var4.add(var12.group(1));
                }
            }

            if (var4.isEmpty()) {
                return var0;
            } else {
                String var11;
                if ((var11 = movePayloadTypesToFront(var4, var3[var10])) == null) {
                    return var0;
                } else {
                    (new StringBuilder("Change media description from: ")).append(var3[var10]).append(" to ").append(var11);
                    var3[var10] = var11;
                    return joinString(Arrays.asList(var3), "\r\n", true);
                }
            }
        }
    }

    private void drainCandidates() {
        if (this.queuedRemoteCandidates != null) {
            (new StringBuilder("Add ")).append(this.queuedRemoteCandidates.size()).append(" remote candidates");
            Iterator var1 = this.queuedRemoteCandidates.iterator();

            while(var1.hasNext()) {
                IceCandidate var2 = (IceCandidate)var1.next();
                this.peerConnection.addIceCandidate(var2);
            }

            this.queuedRemoteCandidates = null;
        }

    }

    private class SDPObserver implements SdpObserver {
        private SDPObserver() {
        }

        public void onCreateSuccess(SessionDescription var1) {
            //PeerConnectionClient.this.localSdp;//==============

            String var2 = var1.description;
            if (PeerConnectionClient.this.preferIsac) {
                var2 = PeerConnectionClient.preferCodec(var2, "ISAC", true);
            }

            if (PeerConnectionClient.this.isVideoCallEnabled()) {
                var2 = PeerConnectionClient.preferCodec(var2, PeerConnectionClient.getSdpVideoCodecName(PeerConnectionClient.this.peerConnectionParameters), false);
            }

            SessionDescription tempVar1 = new SessionDescription(var1.type, var2);
            PeerConnectionClient.this.localSdp = tempVar1;
            PeerConnectionClient.this.events.onLocalDescription(PeerConnectionClient.this.localSdp);
            PeerConnectionClient.executor.execute(() -> {
                if (PeerConnectionClient.this.peerConnection != null && !PeerConnectionClient.this.isError) {
                    (new StringBuilder("Set local SDP from ")).append(tempVar1.type);
                    PeerConnectionClient.this.peerConnection.setLocalDescription(PeerConnectionClient.this.sdpObserver, tempVar1);
                }

            });
        }

        public void onSetSuccess() {
            PeerConnectionClient.executor.execute(() -> {
                if (PeerConnectionClient.this.peerConnection != null && !PeerConnectionClient.this.isError) {
                    if (PeerConnectionClient.this.isInitiator) {
                        if (PeerConnectionClient.this.peerConnection.getRemoteDescription() != null) {
                            PeerConnectionClient.this.drainCandidates();
                            return;
                        }
                    } else if (PeerConnectionClient.this.peerConnection.getLocalDescription() != null) {
                        PeerConnectionClient.this.drainCandidates();
                    }

                }
            });
        }

        public void onCreateFailure(String var1) {
            PeerConnectionClient.this.reportError("createSDP error: ".concat(String.valueOf(var1)));
        }

        public void onSetFailure(String var1) {
            PeerConnectionClient.this.reportError("setSDP error: ".concat(String.valueOf(var1)));
        }
    }

    private class PCObserver implements Observer {
        private PCObserver() {
        }

        public void onIceCandidate(IceCandidate var1) {
            PeerConnectionClient.executor.execute(() -> {
                PeerConnectionClient.this.events.onIceCandidate(var1);
            });
        }

        public void onIceCandidatesRemoved(IceCandidate[] var1) {
            PeerConnectionClient.executor.execute(() -> {
                PeerConnectionClient.this.events.onIceCandidatesRemoved(var1);
            });
        }

        public void onSignalingChange(SignalingState var1) {
            (new StringBuilder("SignalingState: ")).append(var1);
        }

        public void onIceConnectionChange(IceConnectionState var1) {
            PeerConnectionClient.executor.execute(() -> {
                (new StringBuilder("IceConnectionState: ")).append(var1);
                if (var1 == IceConnectionState.CONNECTED) {
                    PeerConnectionClient.this.events.onIceConnected();
                } else if (var1 == IceConnectionState.DISCONNECTED) {
                    PeerConnectionClient.this.events.onIceDisconnected();
                } else {
                    if (var1 == IceConnectionState.FAILED) {
                        PeerConnectionClient.this.reportError("ICE connection failed.");
                    }

                }
            });
        }

        public void onIceGatheringChange(IceGatheringState var1) {
            (new StringBuilder("IceGatheringState: ")).append(var1);
        }

        public void onIceConnectionReceivingChange(boolean var1) {
        }

        public void onAddStream(MediaStream var1) {
        }

        public void onRemoveStream(MediaStream var1) {
        }

        public void onDataChannel(final DataChannel var1) {
            (new StringBuilder("New Data channel ")).append(var1.label());
            if (PeerConnectionClient.this.dataChannelEnabled) {
                var1.registerObserver(new DataChannel.Observer() {
                    public void onBufferedAmountChange(long var1x) {
                        (new StringBuilder("Data channel buffered amount changed: ")).append(var1.label()).append(": ").append(var1.state());
                    }

                    public void onStateChange() {
                        (new StringBuilder("Data channel state changed: ")).append(var1.label()).append(": ").append(var1.state());
                    }

                    public void onMessage(Buffer var1x) {
                        if (var1x.binary) {
                            (new StringBuilder("Received binary msg over ")).append(var1);
                        } else {
                            ByteBuffer var3;
                            byte[] var2 = new byte[(var3 = var1x.data).capacity()];
                            var3.get(var2);
                            String var4 = new String(var2, Charset.forName("UTF-8"));
                            (new StringBuilder("Got msg: ")).append(var4).append(" over ").append(var1);
                        }
                    }
                });
            }
        }

        public void onRenegotiationNeeded() {
        }

        public void onAddTrack(RtpReceiver var1, MediaStream[] var2) {
            MediaStreamTrack var3;
            if ((var3 = var1.track()) instanceof VideoTrack) {
                PeerConnectionClient.this.events.onRemoteVideoTrack((VideoTrack)var3);
            } else {
                if (var3 instanceof AudioTrack) {
                    PeerConnectionClient.this.events.onRemoteAudioTrack((AudioTrack)var3);
                }

            }
        }

        public void onTrack(RtpTransceiver var1) {
        }
    }

    public interface PeerConnectionEvents {
        void onPeerConnectionCreated(boolean var1);

        void onLocalDescription(SessionDescription var1);

        void onIceCandidate(IceCandidate var1);

        void onIceCandidatesRemoved(IceCandidate[] var1);

        void onIceConnected();

        void onIceDisconnected();

        void onPeerConnectionClosed(int var1);

        void onPeerConnectionStatsReady(StatsReport[] var1);

        void onRemoteVideoTrack(VideoTrack var1);

        void onRemoteAudioTrack(AudioTrack var1);

        void onPeerConnectionError(String var1);
    }

    public static class PeerConnectionParameters {
        public boolean videoCallEnabled = true;
        public boolean audioCallEnabled = true;
        public boolean audioMute = false;
        public boolean videoMute = false;
        public boolean loopback = false;
        public boolean tracing = false;
        public int videoWidth = 960;
        public int videoHeight = 540;
        public int videoFps = 24;
        public int videoMaxBitrate = 0;
        public String videoCodec = "VP8";
        public boolean videoFlexfecEnabled = true;
        public int audioStartBitrate = 0;
        public String audioCodec = null;
        public boolean noAudioProcessing = false;
        public boolean saveInputAudioToFile = false;
        public boolean useOpenSLES = true;
        public boolean disableBuiltInAEC = false;
        public boolean disableBuiltInAGC = false;
        public boolean disableBuiltInNS = false;
        public boolean disableWebRtcAGCAndHPF = false;
        public boolean enableRtcEventLog = false;
        public boolean unifiedPlan = true;
        public boolean tcpCandidates = true;
        public boolean offerToSend = true;
        public boolean offerToRecv = true;
        protected boolean useCamera2 = true;
        protected boolean captureToTexture = true;
        private DataChannelParameters dataChannelParameters = null;

        public PeerConnectionParameters() {
        }
    }

    public static class DataChannelParameters {
        public final boolean ordered;
        public final int maxRetransmitTimeMs;
        public final int maxRetransmits;
        public final String protocol;
        public final boolean negotiated;
        public final int id;

        public DataChannelParameters(boolean var1, int var2, int var3, String var4, boolean var5, int var6) {
            this.ordered = var1;
            this.maxRetransmitTimeMs = var2;
            this.maxRetransmits = var3;
            this.protocol = var4;
            this.negotiated = var5;
            this.id = var6;
        }
    }
}

