package com.qamp.app.qampCalls;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.R;
import com.qamp.app.uihelper.Utils.Log;

import java.util.ArrayList;
import java.util.List;

public class MesiboCall {
    private static MesiboCall _instance = null;
    private static CallManager _cmInstance = null;
    public static final int MESIBOCALL_PERMISSION_REQUEST_CODE = 4097;
    public static final int MESIBOCALL_NOTIFY_INCOMING = 0;
    public static final int MESIBOCALL_NOTIFY_MISSED = 4;
    public static final int MESIBOCALL_SOUND_RINGING = 0;
    public static final int MESIBOCALL_SOUND_BUSY = 1;
    public static final int MESIBOCALL_ERROR_BUSY = 1;
    public static final int MESIBOCALL_UI_STATE_SHOWINCOMING = 1;
    public static final int MESIBOCALL_UI_STATE_SHOWINPROGRESS = 2;
    public static final int MESIBOCALL_UI_STATE_SHOWCONTROLS = 3;
    public static final int MESIBOCALL_UI_STATE_ANSWERED = 4;
    public static final int MESIBOCALL_HANGUP_REASON_USER = 1;
    public static final int MESIBOCALL_HANGUP_REASON_REMOTE = 2;
    public static final int MESIBOCALL_HANGUP_REASON_ERROR = 3;
    public static final int MESIBOCALL_HANGUP_REASON_BACKGROUND = 4;
    public static final int MESIBOCALL_CODEC_VP8 = 1;
    public static final int MESIBOCALL_CODEC_VP9 = 2;
    public static final int MESIBOCALL_CODEC_H264 = 4;
    public static final int MESIBOCALL_CODEC_H265 = 8;
    public static final int MESIBOCALL_CODEC_OPUS = 256;
    public static final int MESIBOCALL_VIDEOSOURCE_CAMERADEFAULT = 0;
    public static final int MESIBOCALL_VIDEOSOURCE_CAMERAFRONT = 1;
    public static final int MESIBOCALL_VIDEOSOURCE_CAMERAREAR = 2;
    public static final int MESIBOCALL_VIDEOSOURCE_SCREEN = 4;
    private int mMinimumVersionForForegroundService = 26;
    protected UiProperties mDefaultUiProperies = new UiProperties();

    public static MesiboCall getInstance() {
        if (_instance == null) {
            Class var0 = MesiboCall.class;
            synchronized(MesiboCall.class) {
                if (_instance == null) {
                    _instance = new MesiboCall();
                    _cmInstance = CallManager.getInstance();
                }
            }
        }

        return _instance;
    }

    protected MesiboCall() {
    }

    public void init(Context var1) {
        CallManager.getInstance().init(var1);
        if (var1 instanceof IncomingListener) {
            this.setListener((IncomingListener)var1);
        }

    }

    public void setDefaultUiProperties(UiProperties var1) {
        if (var1 != null) {
            this.mDefaultUiProperies = var1;
        }
    }

    public void setListener(IncomingListener var1) {
        CallManager.getInstance().setListener(var1);
    }

    public void setGroupCallListener(GroupCallIncomingListener var1) {
        CallManager.getInstance().setGroupCallListener(var1);
    }

    public boolean callUiForExistingCall(Context var1) {
        Log.e("Aditya","one");
        return CallManager.getInstance().callUiForExistingCall(var1);
    }

    public boolean callUi(CallProperties var1) {
        return CallManager.getInstance().callUi(var1);
    }

    public boolean callUi(Context var1, String var2, boolean var3) {
        CallProperties var5 = this.createCallProperties(var3);
        MesiboProfile var4 = Mesibo.getProfile(var2);
        var5.user = var4;
        var5.parent = var1;
        return CallManager.getInstance().callUi(var5);
    }

    public Call call(CallProperties var1) {
        var1.incoming = false;
        return CallManager.getInstance().call(var1);
    }

    public Call getActiveCall() {
        return CallManager.getInstance().getActiveCall();
    }

    public boolean isCallInProgress() {
        return CallManager.getInstance().isCallInProgress();
    }

    public CallProperties createCallProperties(boolean var1) {
        return new CallProperties(var1);
    }

    public boolean groupCallUi(Context var1, MesiboProfile var2, boolean var3, boolean var4) {
        return CallManager.getInstance().groupCallUi(var1, var2.getGroupId(), var3, var4);
    }

    public boolean groupCallJoinRoomUi(Context var1, String var2) {
        return CallManager.getInstance().groupCallJoinRoomUi(var1, var2);
    }

    public MesiboGroupCall groupCall(QampCallActivity var1, long var2) {
        return CallManager.getInstance().groupCall(var1, var2);
    }

    public void setHoldOnCellularIncoming(boolean var1) {
        CallManager.getInstance().setHoldOnCellularIncoming(var1);
    }

    public void setStatusText(int var1, String var2) {
        CallManager.getInstance().setStatusText(var1, var2);
    }

    public String getStatusText(int var1, String var2) {
        return CallManager.getInstance().getStatusText(var1, var2);
    }

    public void setMinimumVersionForForegroundService(int var1) {
        if (var1 < 26) {
            var1 = 26;
        }

        this.mMinimumVersionForForegroundService = var1;
    }

    public int getMinimumVersionForForegroundService() {
        return this.mMinimumVersionForForegroundService;
    }

    public void testNotify(Context var1, CallContext var2) {
    }

    public interface MesiboParticipantSortListener {
        MesiboParticipant ParticipantSort_onGetParticipant(Object var1);

        void ParticipantSort_onSetCoordinates(Object var1, int var2, float var3, float var4, float var5, float var6);
    }


    public static class MesiboParticipantSortParams {
        boolean orderedBySelf = true;
        boolean orderedByTalking = true;
        boolean orderedByName = true;
        boolean orderedByVideo = true;
        boolean forceAspectRatio = true;
        boolean flexibleOrientation = true;
        float maxHorzAspectRatio = 1.3333F;
        float minVertAspectRation = 0.8F;
        float multiplier = 1.0F;

        public MesiboParticipantSortParams() {
        }
    }

    public interface MesiboParticipant {
        void setListener(GroupCallInProgressListener var1);

        boolean call(boolean var1, boolean var2, GroupCallInProgressListener var3);

        void hangup();

        void switchCamera();

        void switchSource();

        boolean isFrontCamera();

        void changeVideoFormat(int var1, int var2, int var3);

        void setVideoSource(int var1, int var2);

        int getVideoSource();

        void mute(boolean var1, boolean var2, boolean var3);

        boolean toggleAudioMute();

        boolean toggleVideoMute();

        boolean getMuteStatus(boolean var1);

        void adminMute(boolean var1, boolean var2, boolean var3);

        void setVideoView(MesiboVideoView var1);

        MesiboVideoView getVideoView();

        boolean isCallInProgress();

        boolean isCallConnected();

        boolean isVideoCall();

        boolean hasVideo();

        boolean hasAudio();

        boolean isTalking();

        long getTalkTimestamp();

        boolean isVideoLandscape();

        float getAspectRatio();

        long getId();

        long getSid();

        long getUid();

        MesiboParticipantAdmin getAdmin();

        Object getUserData();

        void setUserData(Object var1);

        String getName();

        void setName(String var1);

        String getAddress();

        boolean isMe();

        boolean isPublisher();

        MesiboProfile getLastAdminProfile();
    }

    public interface MesiboGroupCallAdmin {
        void mute(MesiboParticipant[] var1, boolean var2, boolean var3, boolean var4);

        void muteAll(boolean var1, boolean var2, boolean var3);

        void hangup(MesiboParticipant[] var1);

        void hangupAll();

        void fullscreen(MesiboParticipant var1);
    }

    public interface MesiboParticipantAdmin {
        void mute(boolean var1, boolean var2, boolean var3);

        boolean toggleAudioMute();

        boolean toggleVideoMute();

        void hangup();

        void publish(long var1, long var3, int var5, boolean var6, boolean var7);

        void subscribe(MesiboParticipant var1, boolean var2, boolean var3);
    }

    public interface MesiboGroupCall {
        MesiboParticipant createPublisher(long var1);

        void join(GroupCallListener var1);

        void leave();

        boolean hasAdminPermissions(boolean var1);

        void setAudioDevice(AudioDevice var1, boolean var2);

        void playInCallSound(Context var1, int var2, boolean var3);

        void stopInCallSound();

        ArrayList<? extends Object> sort(MesiboParticipantSortListener var1, ArrayList<? extends Object> var2, float var3, float var4, int var5, int var6, MesiboParticipantSortParams var7);

        MesiboGroupCallAdmin getAdmin();
    }

    public interface GroupCallInProgressListener {
        void MesiboGroupcall_OnMute(MesiboParticipant var1, boolean var2, boolean var3, boolean var4);

        void MesiboGroupcall_OnHangup(MesiboParticipant var1, int var2);

        void MesiboGroupcall_OnConnected(MesiboParticipant var1, boolean var2);

        void MesiboGroupcall_OnTalking(MesiboParticipant var1, boolean var2);

        void MesiboGroupcall_OnVideoSourceChanged(MesiboParticipant var1, int var2, int var3);

        void MesiboGroupcall_OnVideo(MesiboParticipant var1, float var2, boolean var3);

        void MesiboGroupcall_OnAudio(MesiboParticipant var1);
    }

    public interface GroupCallListener {
        void MesiboGroupcall_OnPublisher(MesiboParticipant var1, boolean var2);

        void MesiboGroupcall_OnSubscriber(MesiboParticipant var1, boolean var2);

        void MesiboGroupcall_OnAudioDeviceChanged(AudioDevice var1, AudioDevice var2);
    }

    public interface GroupCallAdminListener {
        boolean MesiboGroupcallAdmin_OnStartPublishing(MesiboProfile var1, boolean var2, long var3, boolean var5, boolean var6, long var7, int var9);

        boolean MesiboGroupcallAdmin_OnStopPublishing(MesiboProfile var1, boolean var2, MesiboParticipant var3);

        boolean MesiboGroupcallAdmin_OnSubscribe(MesiboProfile var1, boolean var2, MesiboParticipant var3, boolean var4, boolean var5);

        boolean MesiboGroupcallAdmin_OnMakeFullScreen(MesiboProfile var1, boolean var2, MesiboParticipant var3);

        boolean MesiboGroupcallAdmin_OnMute(MesiboProfile var1, boolean var2, MesiboParticipant var3, boolean var4, boolean var5, boolean var6);

        boolean MesiboGroupcallAdmin_OnLeave(MesiboProfile var1, boolean var2);
    }

    public interface GroupCallIncomingListener {
        void MesiboGroupcall_OnIncoming(MesiboProfile var1, int var2, MesiboProfile var3);
    }

    public static class KeepResources {
        int missed;

        public KeepResources() {
            this.missed = R.drawable.ic_mesibo_call_missed; //==========
        }
    }

    public interface Call {
        void setListener(InProgressListener var1);

        CallProperties getCallProperties();

        void start(QampCallActivity var1, InProgressListener var2);

        void answer(boolean var1);

        void answer();

        void sendDTMF(int var1);

        void hangup();

        void switchCamera();

        void switchSource();

        boolean isFrontCamera();

        void changeVideoFormat(int var1, int var2, int var3);

        void setVideoSource(int var1, int var2);

        int getVideoSource();

        void mute(boolean var1, boolean var2, boolean var3);

        boolean toggleAudioMute();

        boolean toggleVideoMute();

        void setAudioDevice(AudioDevice var1, boolean var2);

        AudioDevice getActiveAudioDevice();

        boolean toggleAudioDevice(AudioDevice var1);

        void setVideoView(MesiboVideoView var1, boolean var2);

        void setVideoViewsSwapped(boolean var1);

        boolean isVideoViewsSwapped();

        MesiboVideoView getVideoView(boolean var1);

        void setVideoScaling(VideoScalingType var1);

        VideoScalingType getVideoScalingType();

        long getAnswerTime();

        boolean isVideoCall();

        boolean isIncoming();

        boolean isCallInProgress();

        boolean isLinkUp();

        boolean isCallConnected();

        boolean isAnswered();

        boolean getMuteStatus(boolean var1, boolean var2, boolean var3);

        void playInCallSound(Context var1, int var2, boolean var3);

        void stopInCallSound();
    }

    public class CallProperties {
        public Notification notify = new Notification();
        public UiProperties ui = MesiboCall.this.new UiProperties();
        public VideoProperties video = null;
        public AudioProperties audio = null;
        public VideoProperties record = null;
        public Context parent = null;
        public QampCallActivity activity = null;
        public Class<?> className = QampDefaultCallActivity.class;
        public int intentFlags = -1;
        public boolean autoDetectAppState = true;
        public int batteryLowThreshold = 10;
        public boolean autoAnswer = false;
        public boolean enableCameraAtStart = true;
        public boolean disableSpeakerOnProximity = false;
        public boolean hideOnProximity = false;
        public boolean runInBackground = true;
        public boolean stopVideoInBackground = true;
        public boolean autoSwapVideoViews = true;
        public MesiboIceServer[] iceServers = null;
        public MesiboProfile user = null;
        public boolean useConnectService = false;
        private boolean holdOnCellularIncoming = true;
        private boolean checkNetworkConnection = true;
        protected boolean incoming = false;
        public Object other = null;

        public CallProperties(boolean var2) {
            this.video = MesiboCall.this.new VideoProperties();
            this.video.enabled = var2;
            this.audio = MesiboCall.this.new AudioProperties();
            this.hideOnProximity = !var2;
            if (var2) {
                this.notify.message = "Incoming Video Call";
                this.audio.speaker = true;
                this.disableSpeakerOnProximity = false;
            } else {
                this.notify.message = "Incoming Voice Call";
                this.audio.speaker = false;
                this.ui.autoHideControls = false;
            }
        }

        public boolean isIncoming() {
            return this.incoming;
        }

        public class Notification {
            public String channelName = "Incoming Call Notification";
            public String title = null;
            public String message = null;
            public String screenSharing = "Sharing Screen";
            public String answer = "Answer";
            public String hangup = "Hang up";
            public boolean vibrate = true;
            public boolean sound = true;
            public int icon;
            public int icon_answer;
            public int icon_hangup;
            public int color;
            public Uri ringtoneUri;

            public Notification() {
                this.icon = R.drawable.ic_notification_call_incoming;
                this.icon_answer = R.drawable.ic_notification_call;
                this.icon_hangup = R.drawable.ic_notification_call_hangup;
                this.color = 2130740875;
                this.ringtoneUri = null;
            }
        }
    }

    public static class MesiboIceServer {
        List<String> urls = new ArrayList();
        String username = null;
        String credential = null;

        public MesiboIceServer() {
        }
    }

    public interface InProgressListener {
        void MesiboCall_OnSetCall(QampCallActivity var1, Call var2);

        void MesiboCall_OnMute(CallProperties var1, boolean var2, boolean var3, boolean var4);

        boolean MesiboCall_OnPlayInCallSound(CallProperties var1, int var2, boolean var3);

        void MesiboCall_OnHangup(CallProperties var1, int var2);

        void MesiboCall_OnStatus(CallProperties var1, int var2, boolean var3);

        void MesiboCall_OnAudioDeviceChanged(CallProperties var1, AudioDevice var2, AudioDevice var3);

        void MesiboCall_OnVideoSourceChanged(CallProperties var1, int var2, int var3);

        void MesiboCall_OnVideo(CallProperties var1, VideoProperties var2, boolean var3);

        void MesiboCall_OnUpdateUserInterface(CallProperties var1, int var2, boolean var3, boolean var4);

        void MesiboCall_OnOrientationChanged(CallProperties var1, boolean var2, boolean var3);

        void MesiboCall_OnBatteryStatus(CallProperties var1, boolean var2, boolean var3);

        void MesiboCall_OnDTMF(CallProperties var1, int var2);
    }

    public interface IncomingListener {
        CallProperties MesiboCall_OnIncoming(MesiboProfile var1, boolean var2);

        boolean MesiboCall_OnShowUserInterface(Call var1, CallProperties var2);

        void MesiboCall_OnError(CallProperties var1, int var2);

        boolean MesiboCall_onNotify(int var1, MesiboProfile var2, boolean var3);
    }

    public class UiProperties {
        public String title = "";
        public Bitmap userImage = null;
        public Bitmap userImageSmall = null;
        public int layout_id = 0;
        public boolean showScreenSharing = true;
        public boolean autoHideControls = true;
        public InProgressListener inProgressListener = null;

        UiProperties() {
            if (MesiboCall.this.mDefaultUiProperies != null) {
                this.title = MesiboCall.this.mDefaultUiProperies.title;
                this.userImage = MesiboCall.this.mDefaultUiProperies.userImage;
                this.userImageSmall = MesiboCall.this.mDefaultUiProperies.userImageSmall;
                this.layout_id = MesiboCall.this.mDefaultUiProperies.layout_id;
                this.showScreenSharing = MesiboCall.this.mDefaultUiProperies.showScreenSharing;
                this.autoHideControls = MesiboCall.this.mDefaultUiProperies.autoHideControls;
            }

        }
    }

    public class AudioProperties {
        public boolean enabled = true;
        public int bitrate = 0;
        public int quality = 0;
        public int codec = 256;
        public boolean speaker = false;

        public AudioProperties() {
        }
    }

    public class VideoProperties {
        public boolean enabled = true;
        public int width = 0;
        public int height = 0;
        public int fps = 0;
        public int bitrate = 0;
        public int quality = 0;
        public int codec = 1;
        public int source = 1;
        public float zoom = 1.0F;
        public boolean fitZoom = true;
        public boolean hardwareAcceleration = true;
        public String fileName = null;

        public VideoProperties() {
        }
    }

    public static enum VideoScalingType {
        FIT,
        FILL,
        AUTO;

        private VideoScalingType() {
        }
    }

    public static enum AudioDevice {
        SPEAKER,
        HEADSET,
        EARPIECE,
        BLUETOOTH,
        DEFAULT;

        private AudioDevice() {
        }
    }
}

