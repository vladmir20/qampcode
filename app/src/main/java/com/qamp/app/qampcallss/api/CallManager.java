package com.qamp.app.qampcallss.api;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Range;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.mesibo.api.MesiboUtils;
import com.qamp.app.R;
import com.qamp.app.qampcallss.api.p000ui.MesiboJoinRoomActivity;
import com.qamp.app.qampcallss.api.p000ui.QampCallsActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.WeakHashMap;

public class CallManager implements AudioManager.OnAudioFocusChangeListener, Mesibo.CallListener, Mesibo.ConfListener, CallStateReceiver.CallStateListener {
    protected static int APICONFIG_CALLANSWERMODE = 257;
    protected static int APICONFIG_CALLFLAGS = MesiboCall.MESIBOCALL_CODEC_OPUS;
    protected static final int CALLFLAG_FORCEVP8 = 2048;
    protected static final int CALLFLAG_RTC = 1073741824;
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
    private static CallManager _instance = null;
    public final String TAG = "MesiboCallManager";
    private boolean callstatsInitDone = false;
    private boolean holdOnCellularIncoming = true;
    private Context mAppContext = null;
    private boolean mBusy = false;
    private boolean mCallActivityRunning = false;
    private CallP2P mCallp2p = null;
    private MesiboCall.CallProperties mCpForUi = null;
    private DummyListener mDummyListener = new DummyListener();
    private AudioFocusRequest mFocusRequest;
    private MesiboCall.MesiboGroupCall mGroupCall = null;
    private WeakReference<MesiboCall.GroupCallIncomingListener> mGroupCallListener = null;
    private Handler mHandler = null;
    private WeakHashMap<Long, CallP2P> mIncomingCalls = new WeakHashMap<>();
    private long mLastCallId = 0;
    private WeakReference<MesiboCall.IncomingListener> mListener = null;
    private int mPlayCount = 0;
    protected HashMap<Integer, String> mStatusMap = new HashMap<>();
    private boolean mUseLegacySoundNotification = false;

    protected static Context getAppContext() {
        return getInstance().mAppContext;
    }

    public static CallManager getInstance() {
        if (_instance == null) {
            synchronized (CallManager.class) {
                if (_instance == null) {
                    _instance = new CallManager();
                }
            }
        }
        return _instance;
    }

    private void isH264Supported() {
        int codecCount = MediaCodecList.getCodecCount();
        for (int i = 0; i < codecCount; i++) {
            MediaCodecInfo codecInfoAt = MediaCodecList.getCodecInfoAt(i);
            new StringBuilder("  codec: ").append(codecInfoAt.getName());
            if (codecInfoAt.getName().startsWith("OMX.google.")) {
                new StringBuilder("  skipping...").append(codecInfoAt.getName());
            } else {
                String[] supportedTypes = codecInfoAt.getSupportedTypes();
                for (int i2 = 0; i2 < supportedTypes.length; i2++) {
                    if (supportedTypes[i2].equalsIgnoreCase("android.media.mediaplayer.video.mime")) {
                        new StringBuilder().append(i + 1).append(": ").append(codecInfoAt.getName());
                        new StringBuilder("  isEncoder = ").append(codecInfoAt.isEncoder());
                        new StringBuilder("calling getCapabilitiesForType ").append(supportedTypes[i2]);
                        MediaCodecInfo.VideoCapabilities videoCapabilities = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            videoCapabilities = codecInfoAt.getCapabilitiesForType(supportedTypes[i2]).getVideoCapabilities();
                        }
                        Range<Integer> supportedHeights = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            supportedHeights = videoCapabilities.getSupportedHeights();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            new StringBuilder("max supported width =  ").append(videoCapabilities.getSupportedWidthsFor(supportedHeights.getUpper().intValue()).getUpper()).append(" :: height = ").append(supportedHeights.getUpper());
                        }
                    }
                }
            }
        }
    }

    public static void test() {
    }

    public boolean Mesibo_onCall(long j, long j2, MesiboProfile mesiboProfile, int i) {
        boolean z = false;
        MesiboCall.IncomingListener listener = getListener();
        if (this.mBusy) {
            Mesibo.hangup(0);
            if (listener != null) {
                if ((i & 2) > 0) {
                    z = true;
                }
                listener.MesiboCall_onNotify(4, mesiboProfile, z);
            }
        } else {
            if ((i & 2) > 0) {
                z = true;
            }
            MesiboCall.CallProperties MesiboCall_OnIncoming = listener != null ? listener.MesiboCall_OnIncoming(mesiboProfile, z) : MesiboCall.getInstance().createCallProperties(z);
            if (MesiboCall_OnIncoming == null) {
                Mesibo.hangup(0);
            } else {
                MesiboCall_OnIncoming.incoming = true;
                MesiboCall_OnIncoming.user = mesiboProfile;
                MesiboCall_OnIncoming.callid = j2;
                this.mLastCallId = j2;
                call(MesiboCall_OnIncoming);
            }
        }
        return true;
    }

    public void Mesibo_onCallServer(int i, String str, String str2, String str3) {
    }

    public boolean Mesibo_onCallStatus(long j, long j2, int i, long j3, long j4, long j5, String str) {
        if ((i & RTCFLAG_VIDEOMUTE) <= 0) {
            return true;
        }
        CallP2P callP2P = this.mCallp2p;
        if (callP2P != null && callP2P.isIncoming()) {
            clearNotification(this.mCallp2p.getCallProperties());
            stopIncomingNotification(this.mCallp2p.getCallProperties());
        }
        removeP2PCall();
        return true;
    }

    public void Mesibo_onConfCall(long j, long j2, int i, long j3, long j4, int i2, long j5, long j6, String str, String str2, int i3) {
    }

    public void Mesibo_onConfParitcipant(long j, long j2, String str, String str2, long j3, long j4) {
    }

    public MesiboCall.Call call(MesiboCall.CallProperties callProperties) {
        CallP2P callP2P = null;
        if (this.mBusy) {
            onerror(callProperties, 1);
        } else {
            if (callProperties.parent == null) {
                callProperties.parent = getAppContext();
            }
            if (callProperties.incoming || Mesibo.call((String) null, callProperties.video.enabled) == 0) {
                synchronized (getInstance()) {
                    if (this.mCallp2p == null || !this.mCallp2p.isCallInProgress()) {
                        getAudioFocus(callProperties.parent);
                        setImage(callProperties);
                        this.mCallp2p = new CallP2P();
                        if (this.mCallp2p.setup(callProperties)) {
                            if (!callProperties.incoming) {
                                clearNotification((MesiboCall.CallProperties) null);
                                callP2P = this.mCallp2p;
                            } else {
                                this.mIncomingCalls.put(Long.valueOf(callProperties.callid), this.mCallp2p);
                                this.mCpForUi = callProperties;
                                if (TextUtils.isEmpty(callProperties.notify.title)) {
                                    callProperties.notify.title = callProperties.user.getNameOrAddress("+");
                                }
                                this.mCpForUi = callProperties;
                                CallNotify.showCallNotification(callProperties.parent, callProperties);
                                if (this.mUseLegacySoundNotification) {
                                    playIncomingNotification(callProperties);
                                }
                                callP2P = this.mCallp2p;
                            }
                        }
                    }
                }
            } else {
                onerror(callProperties, 1);
            }
        }
        return callP2P;
    }

    public boolean callUi(MesiboCall.CallProperties callProperties) {
        callProperties.incoming = false;
        if (callProperties.parent == null) {
            callProperties.parent = getAppContext();
        }
        if (isCallInProgress()) {
            return false;
        }
        launchCallActivity(callProperties);
        return true;
    }

    public boolean callUiForExistingCall(Context context) {
        Log.e("aditya","reached11");
        MesiboCall.Call activeCall;
        if (getInstance().isCallActivityRunning() || (activeCall = getActiveCall()) == null) {
            return false;
        }
        MesiboCall.CallProperties callProperties = activeCall.getCallProperties();
        if (!callProperties.uiInitializedOnce) {
            return false;
        }
        callProperties.parent = context;
        launchCallActivity(callProperties);
        return true;
    }

    public synchronized void clearNotification(MesiboCall.CallProperties callProperties) {
        if (callProperties == null) {
            CallNotify.cancel();
        } else {
            CallNotify.cancel(this.mAppContext, callProperties);
        }
    }

    /* access modifiers changed from: protected */
    public synchronized boolean creatingCallActivity(boolean z) {
        boolean z2;
        if (z) {
            if (this.mCallActivityRunning) {
                z2 = false;
            }
        }
        this.mCallActivityRunning = z;
        z2 = true;
        return z2;
    }

    /* access modifiers changed from: protected */
    public void forceVP8() {
        Mesibo.setConfig((long) APICONFIG_CALLFLAGS, 2048);
    }

    /* access modifiers changed from: protected */
    public MesiboCall.Call getActiveCall() {
        return getP2PCall();
    }

    /* access modifiers changed from: protected */
    public MesiboCall.Call getActiveCall(long j) {
        return this.mIncomingCalls.get(Long.valueOf(j));
    }

    /* access modifiers changed from: protected */
    public CallPrivate getActiveCallPrivate() {
        return getP2PCall();
    }

    /* access modifiers changed from: protected */
    public MesiboCall.CallProperties getActiveCallProperties() {
        MesiboCall.Call activeCall = getActiveCall();
        if (activeCall == null) {
            return null;
        }
        return activeCall.getCallProperties();
    }

    /* access modifiers changed from: protected */
    public MesiboCall.CallProperties getActiveCallProperties(long j) {
        MesiboCall.Call activeCall = getActiveCall(j);
        if (activeCall == null) {
            return null;
        }
        return activeCall.getCallProperties();
    }

    /* access modifiers changed from: protected */
    public MesiboCall.MesiboGroupCall getActiveGroupCall() {
        return this.mGroupCall;
    }

    public void getAudioFocus(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(this, 2, 1);
        audioManager.requestAudioFocus(this, 0, 1);
    }

    /* access modifiers changed from: protected */
    public MesiboCall.CallProperties getCallPropertiesForUi() {
        return this.mCpForUi;
    }

    /* access modifiers changed from: protected */
    public DummyListener getDummyListener() {
        return  this.mDummyListener;
    }

    /* access modifiers changed from: protected */
    public MesiboCall.GroupCallIncomingListener getGroupCallListener() {
        if (this.mGroupCallListener == null) {
            return null;
        }
        return (MesiboCall.GroupCallIncomingListener) this.mGroupCallListener.get();
    }

    /* access modifiers changed from: protected */
    public Icon getIcon() {
        return null;
    }

    /* access modifiers changed from: protected */
    public MesiboCall.IncomingListener getListener() {
        if (this.mListener == null) {
            return null;
        }
        return (MesiboCall.IncomingListener) this.mListener.get();
    }

    /* access modifiers changed from: protected */
    public CallP2P getP2PCall() {
        if (this.mCallp2p == null || !this.mCallp2p.isCallInProgress()) {
            return null;
        }
        return this.mCallp2p;
    }

    /* access modifiers changed from: protected */
    public String getStatusText(int i, String str) {
        if (this.mStatusMap.size() == 0) {
            return str;
        }
        String str2 = this.mStatusMap.get(Integer.valueOf(i));
        return !TextUtils.isEmpty(str2) ? str2 : str;
    }

    /* access modifiers changed from: protected */
    public Handler getUiHandler() {
        return this.mHandler;
    }

    /* access modifiers changed from: protected */
    public MesiboCall.MesiboGroupCall groupCall(QampCallsActivity mesiboCallActivity, long j) {
        if (this.mGroupCall != null) {
            return null;
        }
        this.mGroupCall = new GroupCall(mesiboCallActivity, j);
        return this.mGroupCall;
    }

    /* access modifiers changed from: protected */
    public boolean groupCallJoinRoomUi(Context context, String str) {
        Intent intent = new Intent(context, MesiboJoinRoomActivity.class);
        intent.putExtra("title", str);
        context.startActivity(intent);
        return false;
    }

    /* access modifiers changed from: protected */
    public void groupCallStopped() {
        this.mGroupCall = null;
    }

    /* access modifiers changed from: protected */
    public void groupCallStopped(MesiboCall.MesiboGroupCall mesiboGroupCall) {
        if (mesiboGroupCall == this.mGroupCall) {
            this.mGroupCall = null;
        }
    }

    /* access modifiers changed from: protected */
    public boolean groupCallUi(Context context, long j, boolean z, boolean z2) {
        if (j < 1000) {
            return false;
        }
        /**Intent intent = new Intent(context, MesiboDefaultGroupCallActivity.class);
        intent.putExtra(PeerConnectionClient.VIDEO_TRACK_TYPE, z);
        intent.putExtra("audio", z2);
        intent.putExtra("gid", j);
        context.startActivity(intent);*/
        return true;
    }

    public void hangupIncoming() {
        Mesibo.hangup(0);
        stopIncomingNotification((MesiboCall.CallProperties) null);
        clearNotification((MesiboCall.CallProperties) null);
        this.mCallp2p = null;
    }

    public void init(Context context) {
        Mesibo.log("MesiboCall Version: 2.0.36");
        this.mAppContext = context;
        this.mHandler = new Handler(Looper.getMainLooper());
        CallNotify.cancel(this.mAppContext, CallNotify.fixedNid);
        CallNotify.cancelCallNotifications(this.mAppContext);
        Mesibo.addListener(this);
        Mesibo.setCallInterface(0, 0);
        Mesibo.setCallInterface(1, 0);
        Mesibo.setConfig((long) APICONFIG_CALLANSWERMODE, 1);
        if (context == null) {
            proguard_safe();
        }
    }

    public synchronized void initCallStateListener() {
        if (!this.callstatsInitDone) {
            this.callstatsInitDone = true;
            CallStateReceiver.addListener(this);
        }
    }

    public boolean isAudioCallInProgress() {
        return this.mCallp2p != null && this.mCallp2p.isAudioCallInProgress();
    }

    /* access modifiers changed from: protected */
    public synchronized boolean isCallActivityRunning() {
        return this.mCallActivityRunning;
    }

    public boolean isCallInProgress() {
        return this.mCallp2p != null && this.mCallp2p.isCallInProgress();
    }

    /* access modifiers changed from: protected */
    public boolean isLatestCallInProgress_not_i_use(long j) {
        return isCallInProgress() && (0 == j || j == this.mLastCallId);
    }

    /* access modifiers changed from: protected */
    public void launchCallActivity(MesiboCall.CallProperties callProperties) {
        Log.e("Adi","reached33");
        if (!getInstance().isCallActivityRunning()) {
            this.mCpForUi = callProperties;
            if (callProperties.parent == null) {
                callProperties.parent = getAppContext();
            }
            if (callProperties.parent != null) {
                if (callProperties.intentFlags < 0) {
                    callProperties.intentFlags = 268566528;
                }
                Intent intent = new Intent(callProperties.parent, callProperties.className);
                intent.setFlags(callProperties.intentFlags);
                intent.putExtra(PeerConnectionClient.VIDEO_TRACK_TYPE, callProperties.video.enabled);
                intent.putExtra("incoming", callProperties.incoming);
                intent.putExtra("autoAnswer", callProperties.autoAnswer);
                intent.putExtra("address", callProperties.user.address);
                intent.putExtra("nid", -1);
                callProperties.parent.startActivity(intent);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void launchIncomingActivity(MesiboCall.CallProperties callProperties, boolean z, Context context) {
        Log.e("aditya","Launching here");
        MesiboCall.IncomingListener listener = getListener();
        if (callProperties == null) {
            callProperties = this.mCallp2p.getCallProperties();
            callProperties.autoAnswer = z;
        }
        if (listener == null || !listener.MesiboCall_OnShowUserInterface(this.mCallp2p, callProperties)) {
            if (callProperties.parent == null) {
                callProperties.parent = getAppContext();
            }
            if (callProperties.parent == null) {
                callProperties.parent = context;
            }
            launchCallActivity(callProperties);
        }
    }

    public void onAudioFocusChange(int i) {
    }

    public void onCallStateChanged(boolean z, boolean z2, String str) {
        this.mBusy = z;
        if (!z) {
            Mesibo.setCallProcessing(0, this.holdOnCellularIncoming ? 12 : 0);
        } else if (z2) {
            Mesibo.setCallProcessing(67, (!this.holdOnCellularIncoming || !isAudioCallInProgress()) ? 67 : 11);
        }
    }

    /* access modifiers changed from: protected */
    public void onerror(MesiboCall.CallProperties callProperties, int i) {
        MesiboCall.IncomingListener listener = getListener();
        if (listener != null) {
            listener.MesiboCall_OnError(callProperties, i);
        }
    }

    public void playIncomingNotification(MesiboCall.CallProperties callProperties) {
        if (this.mUseLegacySoundNotification) {
      //      MesiboRingtoneService.start(getAppContext(), callProperties);
        }
    }

    public synchronized void playIncomingNotificationInternal(MesiboCall.CallProperties callProperties) throws IOException {
        Looper.getMainLooper().getThread();
        Thread.currentThread();
        if (callProperties != null) {
            if (callProperties.notify.ringtoneUri == null) {
                callProperties.notify.ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(this.mAppContext, 1);
            }
            callProperties.notify.ring = CallUtils.notifyIncomingCall(getAppContext(), (AudioManager) this.mAppContext.getSystemService(Context.AUDIO_SERVICE), true, callProperties.notify.ringtoneUri);
            this.mPlayCount++;
        }
    }

    /* access modifiers changed from: protected */
    public void proguard_safe() {
        int i = R.drawable.ic_mesibo_call_missed;
        int i2 = R.drawable.ic_mesibo_call_received;
        int i3 = R.drawable.ic_mesibo_call_made;
    }

    /* access modifiers changed from: protected */
    public void removeP2PCall() {
        this.mCallp2p = null;
    }

    /* access modifiers changed from: protected */
    public void setGroupCallListener(MesiboCall.GroupCallIncomingListener groupCallIncomingListener) {
        this.mGroupCallListener = new WeakReference<>(groupCallIncomingListener);
    }

    public void setHoldOnCellularIncoming(boolean z) {
        this.holdOnCellularIncoming = z;
    }

    /* access modifiers changed from: protected */
    public void setImage(MesiboCall.CallProperties callProperties) {
        if (callProperties.f2ui.userImage != null && callProperties.f2ui.userImageSmall != null) {
            return;
        }
        if (callProperties.f2ui.userImage != null) {
            callProperties.f2ui.userImageSmall = MesiboUtils.scaleBitmap(callProperties.f2ui.userImage, 90, true);
            return;
        }
        String imageOrThumbnailPath = callProperties.user.getImageOrThumbnailPath();
        if (imageOrThumbnailPath != null) {
            callProperties.f2ui.userImage = MesiboUtils.loadBitmapFromFile(imageOrThumbnailPath, 400, true);
            callProperties.f2ui.userImageSmall = MesiboUtils.loadBitmapFromFile(imageOrThumbnailPath, 90, true);
            return;
        }
        callProperties.f2ui.userImage = BitmapFactory.decodeResource(this.mAppContext.getResources(), R.drawable.default_user_image);
        callProperties.f2ui.userImageSmall = MesiboUtils.scaleBitmap(callProperties.f2ui.userImage, 90, true);
    }

    /* access modifiers changed from: protected */
    public void setListener(MesiboCall.IncomingListener incomingListener) {
        this.mListener = new WeakReference<>(incomingListener);
    }

    /* access modifiers changed from: protected */
    public void setStatusText(int i, String str) {
        this.mStatusMap.put(Integer.valueOf(i), str);
    }

    public synchronized void stopIncomingNotification(MesiboCall.CallProperties callProperties) {
        if (this.mUseLegacySoundNotification) {
            if (callProperties == null) {
                callProperties = getActiveCallProperties();
            }
            if (callProperties != null) {
                //MesiboRingtoneService.stop(getAppContext(), callProperties);
            }
        }
    }

    public synchronized void stopIncomingNotificationInternal(MesiboCall.CallProperties callProperties) {
        if (callProperties != null) {
            if (callProperties.notify.ring != null) {
                callProperties.notify.ring.stop();
                callProperties.notify.ring = null;
                this.mPlayCount = 0;
            }
        }
    }
}
