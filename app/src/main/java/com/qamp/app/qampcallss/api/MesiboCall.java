/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 15/05/23, 6:12 AM
 *
 */

package com.qamp.app.qampcallss.api;

import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.mesibo.api.MesiboProfile;
import com.qamp.app.qampcallss.api.p000ui.MesiboDefaultCallActivity;
import com.qamp.app.qampcallss.api.p000ui.QampCallsActivity;

import java.util.ArrayList;
import java.util.List;

public class MesiboCall {
    public static final int MESIBOCALL_CODEC_H264 = 4;
    public static final int MESIBOCALL_CODEC_H265 = 8;
    public static final int MESIBOCALL_CODEC_OPUS = 256;
    public static final int MESIBOCALL_CODEC_VP8 = 1;
    public static final int MESIBOCALL_CODEC_VP9 = 2;
    public static final int MESIBOCALL_ERROR_BUSY = 1;
    public static final int MESIBOCALL_HANGUP_REASON_BACKGROUND = 4;
    public static final int MESIBOCALL_HANGUP_REASON_DURATION = 5;
    public static final int MESIBOCALL_HANGUP_REASON_ERROR = 3;
    public static final int MESIBOCALL_HANGUP_REASON_REMOTE = 2;
    public static final int MESIBOCALL_HANGUP_REASON_USER = 1;
    public static final int MESIBOCALL_NOTIFY_INCOMING = 0;
    public static final int MESIBOCALL_NOTIFY_MISSED = 4;
    public static final int MESIBOCALL_PERMISSION_REQUEST_CODE = 4097;
    public static final int MESIBOCALL_SOUND_BUSY = 1;
    public static final int MESIBOCALL_SOUND_RINGING = 0;
    public static final int MESIBOCALL_UI_STATE_ANSWERED = 4;
    public static final int MESIBOCALL_UI_STATE_SHOWCONTROLS = 3;
    public static final int MESIBOCALL_UI_STATE_SHOWINCOMING = 1;
    public static final int MESIBOCALL_UI_STATE_SHOWINPROGRESS = 2;
    public static final int MESIBOCALL_VIDEOSOURCE_CAMERADEFAULT = 0;
    public static final int MESIBOCALL_VIDEOSOURCE_CAMERAFRONT = 1;
    public static final int MESIBOCALL_VIDEOSOURCE_CAMERAREAR = 2;
    public static final int MESIBOCALL_VIDEOSOURCE_SCREEN = 4;
    private static CallManager _cmInstance = null;
    private static MesiboCall _instance = null;
    protected UiProperties mDefaultUiProperies = new UiProperties();
    private int mMinimumVersionForForegroundService = 26;

    public enum AudioDevice {
        SPEAKER,
        HEADSET,
        EARPIECE,
        BLUETOOTH,
        DEFAULT
    }

    public class AudioProperties {
        public int bitrate = 0;
        public int codec = MesiboCall.MESIBOCALL_CODEC_OPUS;
        public boolean enabled = true;
        public int quality = 0;
        public boolean speaker = false;

        public AudioProperties() {
        }
    }

    public interface Call {
        void answer();

        void answer(boolean z);

        void changeVideoFormat(int i, int i2, int i3);

        AudioDevice getActiveAudioDevice();

        long getAnswerTime();

        CallProperties getCallProperties();

        boolean getMuteStatus(boolean z, boolean z2, boolean z3);

        VideoScalingType getVideoScalingType();

        int getVideoSource();

        MesiboVideoView getVideoView(boolean z);

        void hangup();

        boolean isAnswered();

        boolean isCallConnected();

        boolean isCallInProgress();

        boolean isFrontCamera();

        boolean isIncoming();

        boolean isLinkUp();

        boolean isVideoCall();

        boolean isVideoViewsSwapped();

        void mute(boolean z, boolean z2, boolean z3);

        void playInCallSound(Context context, int i, boolean z);

        void sendDTMF(int i);

        void setAudioDevice(AudioDevice audioDevice, boolean z);

        void setListener(InProgressListener inProgressListener);

        void setVideoScaling(VideoScalingType videoScalingType);

        void setVideoSource(int i, int i2);

        void setVideoView(MesiboVideoView mesiboVideoView, boolean z);

        void setVideoViewsSwapped(boolean z);

        void start(QampCallsActivity mesiboCallActivity, InProgressListener inProgressListener);

        void stopInCallSound();

        void switchCamera();

        void switchSource();

        boolean toggleAudioDevice(AudioDevice audioDevice);

        boolean toggleAudioMute();

        boolean toggleVideoMute();
    }

    public class CallProperties {
        public QampCallsActivity activity = null;
        public AudioProperties audio = null;
        public boolean autoAnswer = false;
        public boolean autoDetectAppState = false;
        public boolean autoSwapVideoViews = true;
        public int batteryLowThreshold = 10;
        protected long callid = 0;
        private boolean checkNetworkConnection = true;
        public Class<?> className = MesiboDefaultCallActivity.class;
        public boolean disableSpeakerOnProximity = false;
        public boolean enableCameraAtStart = true;
        public boolean hideOnProximity = false;
        public boolean holdOnCellularIncoming = true;
        public MesiboIceServer[] iceServers = null;
        protected boolean incoming = false;
        public int intentFlags = -1;
        public Notification notify = new Notification();
        public Object other = null;
        public Context parent = null;
        protected boolean fullscreen = true;
        public VideoProperties record = null;
        public boolean runInBackground = true;
        public boolean stopVideoInBackground = true;

        /* renamed from: ui */
        public UiProperties f2ui = MesiboCall.this.new UiProperties();
        protected boolean uiInitializedOnce = false;
        public boolean useConnectService = false;
        public MesiboProfile user = null;
        public VideoProperties video = null;

        public class Notification {
            public String answer = "Answer";
            public String category = null;
            public String channelName = "Incoming Call Notification";
            public int color = 2130740875;
            public boolean dnd = false;
            public String hangup = "Hang up";
            public int icon = com.mesibo.calls.api.R.drawable.ic_notification_call_incoming;
            public int icon_answer = com.mesibo.calls.api.R.drawable.ic_notification_call;
            public int icon_hangup = com.mesibo.calls.api.R.drawable.ic_notification_call_hangup;
            public String incomingCall = "Incoming Call";
            public String message = null;
            protected CallUtils.IncomingNotification ring = null;
            public Uri ringtoneUri = null;
            public String screenSharing = "Sharing Screen";
            protected Service service = null;
            public boolean sound = true;
            public String title = null;
            public boolean vibrate = true;

            public Notification() {
            }
        }

        public CallProperties(boolean z) {
            this.video = new VideoProperties();
            this.video.enabled = z;
            this.audio = new AudioProperties();
            this.hideOnProximity = !z;
            if (z) {
                this.notify.message = "Incoming Video Call";
                this.audio.speaker = true;
                this.disableSpeakerOnProximity = false;
                return;
            }
            this.notify.message = "Incoming Voice Call";
            this.audio.speaker = false;
            this.f2ui.autoHideControls = false;
        }

        public boolean isIncoming() {
            return this.incoming;
        }
    }

    public interface GroupCallAdminListener {
        boolean MesiboGroupcallAdmin_OnLeave(MesiboProfile mesiboProfile, boolean z);

        boolean MesiboGroupcallAdmin_OnMakeFullScreen(MesiboProfile mesiboProfile, boolean z, MesiboParticipant mesiboParticipant);

        boolean MesiboGroupcallAdmin_OnMute(MesiboProfile mesiboProfile, boolean z, MesiboParticipant mesiboParticipant, boolean z2, boolean z3, boolean z4);

        boolean MesiboGroupcallAdmin_OnStartPublishing(MesiboProfile mesiboProfile, boolean z, long j, boolean z2, boolean z3, long j2, int i);

        boolean MesiboGroupcallAdmin_OnStopPublishing(MesiboProfile mesiboProfile, boolean z, MesiboParticipant mesiboParticipant);

        boolean MesiboGroupcallAdmin_OnSubscribe(MesiboProfile mesiboProfile, boolean z, MesiboParticipant mesiboParticipant, boolean z2, boolean z3);
    }

    public interface GroupCallInProgressListener {
        void MesiboGroupcall_OnAudio(MesiboParticipant mesiboParticipant);

        void MesiboGroupcall_OnConnected(MesiboParticipant mesiboParticipant, boolean z);

        void MesiboGroupcall_OnHangup(MesiboParticipant mesiboParticipant, int i);

        void MesiboGroupcall_OnMute(MesiboParticipant mesiboParticipant, boolean z, boolean z2, boolean z3);

        void MesiboGroupcall_OnTalking(MesiboParticipant mesiboParticipant, boolean z);

        void MesiboGroupcall_OnVideo(MesiboParticipant mesiboParticipant, float f, boolean z);

        void MesiboGroupcall_OnVideoSourceChanged(MesiboParticipant mesiboParticipant, int i, int i2);
    }

    public interface GroupCallIncomingListener {
        void MesiboGroupcall_OnIncoming(MesiboProfile mesiboProfile, int i, MesiboProfile mesiboProfile2);
    }

    public interface GroupCallListener {
        void MesiboGroupcall_OnAudioDeviceChanged(AudioDevice audioDevice, AudioDevice audioDevice2);

        void MesiboGroupcall_OnPublisher(MesiboParticipant mesiboParticipant, boolean z);

        void MesiboGroupcall_OnSubscriber(MesiboParticipant mesiboParticipant, boolean z);
    }

    public interface InProgressListener {
        void MesiboCall_OnAudioDeviceChanged(CallProperties callProperties, AudioDevice audioDevice, AudioDevice audioDevice2);

        void MesiboCall_OnBatteryStatus(CallProperties callProperties, boolean z, boolean z2);

        void MesiboCall_OnDTMF(CallProperties callProperties, int i);

        void MesiboCall_OnHangup(CallProperties callProperties, int i);

        void MesiboCall_OnMute(CallProperties callProperties, boolean z, boolean z2, boolean z3);

        void MesiboCall_OnOrientationChanged(CallProperties callProperties, boolean z, boolean z2);

        boolean MesiboCall_OnPlayInCallSound(CallProperties callProperties, int i, boolean z);

        void MesiboCall_OnSetCall(QampCallsActivity mesiboCallActivity, Call call);

        void MesiboCall_OnStatus(CallProperties callProperties, int i, boolean z);

        void MesiboCall_OnUpdateUserInterface(CallProperties callProperties, int i, boolean z, boolean z2);

        void MesiboCall_OnVideo(CallProperties callProperties, VideoProperties videoProperties, boolean z);

        void MesiboCall_OnVideoSourceChanged(CallProperties callProperties, int i, int i2);
    }

    public interface IncomingListener {
        void MesiboCall_OnError(CallProperties callProperties, int i);

        CallProperties MesiboCall_OnIncoming(MesiboProfile mesiboProfile, boolean z);

        boolean MesiboCall_OnShowUserInterface(Call call, CallProperties callProperties);

        boolean MesiboCall_onNotify(int i, MesiboProfile mesiboProfile, boolean z);
    }

    public static class KeepResources {
        int missed = com.mesibo.calls.api.R.drawable.ic_mesibo_call_missed;
    }

    public interface MesiboGroupCall {
        MesiboParticipant createPublisher(long j);

        MesiboGroupCallAdmin getAdmin();

        boolean hasAdminPermissions(boolean z);

        void join(GroupCallListener groupCallListener);

        void leave();

        void playInCallSound(Context context, int i, boolean z);

        void setAudioDevice(AudioDevice audioDevice, boolean z);

        ArrayList<? extends Object> sort(MesiboParticipantSortListener mesiboParticipantSortListener, ArrayList<? extends Object> arrayList, float f, float f2, int i, int i2, MesiboParticipantSortParams mesiboParticipantSortParams);

        void stopInCallSound();
    }

    public interface MesiboGroupCallAdmin {
        void fullscreen(MesiboParticipant mesiboParticipant);

        void hangup(MesiboParticipant[] mesiboParticipantArr);

        void hangupAll();

        void mute(MesiboParticipant[] mesiboParticipantArr, boolean z, boolean z2, boolean z3);

        void muteAll(boolean z, boolean z2, boolean z3);
    }

    public static class MesiboIceServer {
        String credential = null;
        List<String> urls = new ArrayList();
        String username = null;
    }

    public interface MesiboParticipant {
        void adminMute(boolean z, boolean z2, boolean z3);

        boolean call(boolean z, boolean z2, GroupCallInProgressListener groupCallInProgressListener);

        void changeVideoFormat(int i, int i2, int i3);

        String getAddress();

        MesiboParticipantAdmin getAdmin();

        float getAspectRatio();

        long getId();

        MesiboProfile getLastAdminProfile();

        boolean getMuteStatus(boolean z);

        String getName();

        long getSid();

        long getTalkTimestamp();

        long getUid();

        Object getUserData();

        int getVideoSource();

        MesiboVideoView getVideoView();

        void hangup();

        boolean hasAudio();

        boolean hasVideo();

        boolean isCallConnected();

        boolean isCallInProgress();

        boolean isFrontCamera();

        boolean isMe();

        boolean isPublisher();

        boolean isTalking();

        boolean isVideoCall();

        boolean isVideoLandscape();

        void mute(boolean z, boolean z2, boolean z3);

        void setListener(GroupCallInProgressListener groupCallInProgressListener);

        void setName(String str);

        void setUserData(Object obj);

        void setVideoSource(int i, int i2);

        void setVideoView(MesiboVideoView mesiboVideoView);

        void switchCamera();

        void switchSource();

        boolean toggleAudioMute();

        boolean toggleVideoMute();

    }

    public interface MesiboParticipantAdmin {
        void hangup();

        void mute(boolean z, boolean z2, boolean z3);

        void publish(long j, long j2, int i, boolean z, boolean z2);

        void subscribe(MesiboParticipant mesiboParticipant, boolean z, boolean z2);

        boolean toggleAudioMute();

        boolean toggleVideoMute();
    }

    public interface MesiboParticipantSortListener {
        MesiboParticipant ParticipantSort_onGetParticipant(Object obj);

        void ParticipantSort_onSetCoordinates(Object obj, int i, float f, float f2, float f3, float f4);
    }

    public static class MesiboParticipantSortParams {
        boolean flexibleOrientation = true;
        boolean forceAspectRatio = true;
        float maxHorzAspectRatio = 1.3333f;
        float minVertAspectRation = 0.8f;
        float multiplier = 1.0f;
        boolean orderedByName = true;
        boolean orderedBySelf = true;
        boolean orderedByTalking = true;
        boolean orderedByVideo = true;
    }

    public class UiProperties {
        public boolean autoHideControls = true;
        public int backgroundColor = -16742773;
        public String callStatusText = null;
        public InProgressListener inProgressListener = null;
        public int layout_id = 0;
        public boolean showScreenSharing = true;
        public String title = "";
        public Bitmap userImage = null;
        public Bitmap userImageSmall = null;

        UiProperties() {
            if (MesiboCall.this.mDefaultUiProperies != null) {
                this.title = MesiboCall.this.mDefaultUiProperies.title;
                this.userImage = MesiboCall.this.mDefaultUiProperies.userImage;
                this.userImageSmall = MesiboCall.this.mDefaultUiProperies.userImageSmall;
                this.layout_id = MesiboCall.this.mDefaultUiProperies.layout_id;
                this.showScreenSharing = MesiboCall.this.mDefaultUiProperies.showScreenSharing;
                this.autoHideControls = MesiboCall.this.mDefaultUiProperies.autoHideControls;
                if (MesiboCall.this.mDefaultUiProperies.backgroundColor != 0) {
                    this.backgroundColor = MesiboCall.this.mDefaultUiProperies.backgroundColor;
                }
            }
        }
    }

    public class VideoProperties {
        public int bitrate = 0;
        public int codec = 1;
        public boolean enabled = true;
        public String fileName = null;
        public boolean fitZoom = true;
        public int fps = 0;
        public boolean hardwareAcceleration = true;
        public int height = 0;
        public int quality = 0;
        public int source = 1;
        public int width = 0;
        public float zoom = 1.0f;

        public VideoProperties() {
        }
    }

    public enum VideoScalingType {
        FIT,
        FILL,
        AUTO
    }

    protected MesiboCall() {
    }

    public static MesiboCall getInstance() {
        if (_instance == null) {
            synchronized (MesiboCall.class) {
                if (_instance == null) {
                    _instance = new MesiboCall();
                    _cmInstance = CallManager.getInstance();
                }
            }
        }
        return _instance;
    }

    public Call call(CallProperties callProperties) {
        callProperties.incoming = false;
        return CallManager.getInstance().call(callProperties);
    }

    public boolean callUi(Context context, MesiboProfile mesiboProfile, boolean z) {
        if (CallManager.getInstance().isCallActivityRunning()) {
            return false;
        }
        if (mesiboProfile.isGroup()) {
            return groupCallUi(context, mesiboProfile, z, true);
        }
        CallProperties createCallProperties = createCallProperties(z);
        createCallProperties.user = mesiboProfile;
        createCallProperties.parent = context;
        return CallManager.getInstance().callUi(createCallProperties);
    }

    public boolean callUi(CallProperties callProperties) {
        return CallManager.getInstance().callUi(callProperties);
    }

    public boolean callUiForExistingCall(Context context) {
        Log.e("Adityanand","reacged");
        return CallManager.getInstance().callUiForExistingCall(context);
    }

    public CallProperties createCallProperties(boolean z) {
        return new CallProperties(z);
    }

    public Call getActiveCall() {
        return CallManager.getInstance().getActiveCall();
    }

    public int getMinimumVersionForForegroundService() {
        return this.mMinimumVersionForForegroundService;
    }

    public String getStatusText(int i, String str) {
        return CallManager.getInstance().getStatusText(i, str);
    }

    public MesiboGroupCall groupCall(QampCallsActivity mesiboCallActivity, long j) {
        return CallManager.getInstance().groupCall(mesiboCallActivity, j);
    }

    public boolean groupCallJoinRoomUi(Context context, String str) {
        return CallManager.getInstance().groupCallJoinRoomUi(context, str);
    }

    public boolean groupCallUi(Context context, MesiboProfile mesiboProfile, boolean z, boolean z2) {
        return CallManager.getInstance().groupCallUi(context, mesiboProfile.getGroupId(), z, z2);
    }

    public void init(Context context) {
        CallManager.getInstance().init(context);
        if (context instanceof IncomingListener) {
            setListener((IncomingListener) context);
        }
    }

    public boolean isCallInProgress() {
        return CallManager.getInstance().isCallInProgress();
    }

    public void setDefaultUiProperties(UiProperties uiProperties) {
        if (uiProperties != null) {
            this.mDefaultUiProperies = uiProperties;
        }
    }

    public void setGroupCallListener(GroupCallIncomingListener groupCallIncomingListener) {
        CallManager.getInstance().setGroupCallListener(groupCallIncomingListener);
    }

    public void setHoldOnCellularIncoming(boolean z) {
        CallManager.getInstance().setHoldOnCellularIncoming(z);
    }

    public void setListener(IncomingListener incomingListener) {
        CallManager.getInstance().setListener(incomingListener);
    }

    public void setMinimumVersionForForegroundService(int i) {
        if (i < 26) {
            i = 26;
        }
        this.mMinimumVersionForForegroundService = i;
    }

    public void setStatusText(int i, String str) {
        CallManager.getInstance().setStatusText(i, str);
    }

    public void testNotify(Context context, CallContext callContext) {
    }
}
