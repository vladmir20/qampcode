package com.qamp.app.qampCalls;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.VideoCapabilities;
import android.media.MediaCodecList;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Range;

import com.mesibo.api.Mesibo;
import com.mesibo.api.Mesibo.CallListener;
import com.mesibo.api.Mesibo.ConfListener;
import com.mesibo.api.MesiboProfile;
import com.mesibo.api.MesiboUtils;
import com.qamp.app.R;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class CallManager implements OnAudioFocusChangeListener, CallListener, ConfListener, CallStateReceiver.CallStateListener {
    public final String TAG = "MesiboCallManager";
    protected static final int CALLFLAG_RTC = 1073741824;
    protected static final int CALLFLAG_FORCEVP8 = 2048;
    private CallP2P mCallp2p = null;
    private Context mAppContext = null;
    private boolean mBusy = false;
    private CallUtils.IncomingNotification incomingNotification = null;
    private WeakReference<MesiboCall.IncomingListener> mListener = null;
    private WeakReference<MesiboCall.GroupCallIncomingListener> mGroupCallListener = null;
    private MesiboCall.CallProperties mCpForUi = null;
    private Handler mHandler = null;
    private MesiboCall.MesiboGroupCall mGroupCall = null;
    private DummyListener mDummyListener = new DummyListener();
    protected HashMap<Integer, String> mStatusMap = new HashMap();
    protected static int APICONFIG_CALLANSWERMODE = 257;
    protected static int APICONFIG_CALLFLAGS = 256;
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
    private static CallManager _instance = null;
    private MediaPlayer mRingPlayer = null;
    private boolean holdOnCellularIncoming = true;
    private AudioFocusRequest mFocusRequest;

    public CallManager() {
    }

    protected DummyListener getDummyListener() {
        return this.mDummyListener;
    }

    protected Handler getUiHandler() {
        return this.mHandler;
    }

    private void isH264Supported() {
        int var1 = MediaCodecList.getCodecCount();

        for(int var2 = 0; var2 < var1; ++var2) {
            MediaCodecInfo var3 = MediaCodecList.getCodecInfoAt(var2);
            (new StringBuilder("  codec: ")).append(var3.getName());
            if (var3.getName().startsWith("OMX.google.")) {
                (new StringBuilder("  skipping...")).append(var3.getName());
            } else {
                String[] var4 = var3.getSupportedTypes();

                for(int var5 = 0; var5 < var4.length; ++var5) {
                    if (var4[var5].equalsIgnoreCase("android.media.mediaplayer.video.mime")) {
                        (new StringBuilder()).append(var2 + 1).append(": ").append(var3.getName());
                        (new StringBuilder("  isEncoder = ")).append(var3.isEncoder());
                        (new StringBuilder("calling getCapabilitiesForType ")).append(var4[var5]);
                        VideoCapabilities var6;
                        Range var7 = (var6 = var3.getCapabilitiesForType(var4[var5]).getVideoCapabilities()).getSupportedHeights();
                        Range var8 = var6.getSupportedWidthsFor((Integer)var7.getUpper());
                        (new StringBuilder("max supported width =  ")).append(var8.getUpper()).append(" :: height = ").append(var7.getUpper());
                    }
                }
            }
        }

    }

    public void init(Context var1) {
        this.mAppContext = var1;
        this.mHandler = new Handler(Looper.getMainLooper());
        CallNotify.cancel(this.mAppContext, CallNotify.fixedNid);
        Mesibo.addListener(this);
        CallStateReceiver.addListener(this);
        Mesibo.setCallInterface(0, 0L);
        Mesibo.setCallInterface(1, 0L);
        Mesibo.setConfig((long)APICONFIG_CALLANSWERMODE, 1L);
        if (var1 == null) {
            this.proguard_safe();
        }

    }

    protected void forceVP8() {
        Mesibo.setConfig((long)APICONFIG_CALLFLAGS, 2048L);
    }

    protected void proguard_safe() {
//        int var10000 = drawable.ic_mesibo_call_missed;
//        var10000 = drawable.ic_mesibo_call_received;
//        var10000 = drawable.ic_mesibo_call_made;
    }

    protected Icon getIcon() {
        return null;
    }

    protected static Context getAppContext() {
        return getInstance().mAppContext;
    }

    public static CallManager getInstance() {
        if (_instance == null) {
            Class var0 = CallManager.class;
            synchronized(CallManager.class) {
                if (_instance == null) {
                    _instance = new CallManager();
                }
            }
        }

        return _instance;
    }

    protected CallP2P getP2PCall() {
        return this.mCallp2p != null && this.mCallp2p.isCallInProgress() ? this.mCallp2p : null;
    }

    protected MesiboCall.Call getActiveCall() {
        return this.getP2PCall();
    }

    protected CallPrivate getActiveCallPrivate() {
        return this.getP2PCall();
    }

    protected MesiboCall.MesiboGroupCall getActiveGroupCall() {
        return this.mGroupCall;
    }

    protected MesiboCall.MesiboGroupCall groupCall(QampCallActivity var1, long var2) {
        if (this.mGroupCall != null) {
            return null;
        } else {
            this.mGroupCall = new GroupCall(var1, var2);
            return this.mGroupCall;
        }
    }

    protected void groupCallStopped() {
        this.mGroupCall = null;
    }

    protected void removeP2PCall() {
        this.mCallp2p = null;
    }

    protected void setListener(MesiboCall.IncomingListener var1) {
        this.mListener = new WeakReference(var1);
    }

    protected void setGroupCallListener(MesiboCall.GroupCallIncomingListener var1) {
        this.mGroupCallListener = new WeakReference(var1);
    }

    protected MesiboCall.IncomingListener getListener() {
        return this.mListener == null ? null : (MesiboCall.IncomingListener)this.mListener.get();
    }

    protected MesiboCall.GroupCallIncomingListener getGroupCallListener() {
        return this.mGroupCallListener == null ? null : (MesiboCall.GroupCallIncomingListener)this.mGroupCallListener.get();
    }

    public boolean isCallInProgress() {
        return this.mCallp2p != null && this.mCallp2p.isCallInProgress();
    }

    public boolean isAudioCallInProgress() {
        return this.mCallp2p != null && this.mCallp2p.isAudioCallInProgress();
    }

    protected void onerror(MesiboCall.CallProperties var1, int var2) {
        MesiboCall.IncomingListener var3;
        if ((var3 = this.getListener()) != null) {
            var3.MesiboCall_OnError(var1, var2);
        }

    }

    protected void setImage(MesiboCall.CallProperties var1) {
        if (null == var1.ui.userImage || null == var1.ui.userImageSmall) {
            if (null != var1.ui.userImage) {
                var1.ui.userImageSmall = MesiboUtils.scaleBitmap(var1.ui.userImage, 90, true);
            } else {
                String var2;
                if ((var2 = var1.user.getImageOrThumbnailPath()) != null) {
                    var1.ui.userImage = MesiboUtils.loadBitmapFromFile(var2, 400, true);
                    var1.ui.userImageSmall = MesiboUtils.loadBitmapFromFile(var2, 90, true);
                } else {
                    Drawable bmpDrawable = (Drawable) this.mAppContext.getDrawable(R.drawable.flag_transparent);
                    var1.ui.userImage = drawableToBitmap(bmpDrawable); //=======
                    var1.ui.userImageSmall = MesiboUtils.scaleBitmap(var1.ui.userImage, 90, true);
                }
            }
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    protected MesiboCall.CallProperties getCallPropertiesForUi() {
        return this.mCpForUi;
    }

    protected boolean groupCallUi(Context var1, long var2, boolean var4, boolean var5) {
        if (var2 < 1000L) {
            return false;
        } else {
            Intent var6;
            (var6 = new Intent(var1, MesiboDefaultGroupCallActivity.class)).putExtra("video", var4);
            var6.putExtra("audio", var5);
            var6.putExtra("gid", var2);
            var1.startActivity(var6);
            return true;
        }
    }

    protected boolean groupCallJoinRoomUi(Context var1, String var2) {
        Intent var3;
        (var3 = new Intent(var1, MesiboJoinRoomActivity.class)).putExtra("title", var2);
        var1.startActivity(var3);
        return false;
    }

    protected void launchCallActivity(MesiboCall.CallProperties var1) {
        this.mCpForUi = var1;
        if (var1.intentFlags < 0) {
            var1.intentFlags = 268566528;
        }

        Intent var2;
        System.out.println(var1.video.enabled+"Adityanand");
        System.out.println(var1.incoming);
        System.out.println(var1.autoAnswer);
        System.out.println(var1.user.address);
        (var2 = new Intent(var1.parent, var1.className)).setFlags(var1.intentFlags);
        var2.putExtra("video", var1.video.enabled);
        var2.putExtra("incoming", var1.incoming);
        var2.putExtra("autoAnswer", var1.autoAnswer);
        var2.putExtra("address", var1.user.address);
        var2.putExtra("nid", -1);
        var1.parent.startActivity(var2);
    }

    protected void launchIncomingActivity(MesiboCall.CallProperties var1, boolean var2) {
        MesiboCall.IncomingListener var3 = this.getListener();
        if (var1 == null) {
            (var1 = this.mCallp2p.getCallProperties()).autoAnswer = var2;
        }

        if (var3 == null || !var3.MesiboCall_OnShowUserInterface(this.mCallp2p, var1)) {
            this.launchCallActivity(var1);
        }

    }

    public boolean callUiForExistingCall(Context var1) {
        MesiboCall.Call var2;
        if ((var2 = this.getActiveCall()) == null) {
            return false;
        } else {
            MesiboCall.CallProperties var3;
            (var3 = var2.getCallProperties()).parent = var1;
            this.launchCallActivity(var3);
            return true;
        }
    }

    public boolean callUi(MesiboCall.CallProperties var1) {
        var1.incoming = false;
        if (var1.parent == null) {
            var1.parent = getAppContext();
        }

        if (this.isCallInProgress()) {
            return false;
        } else {
            this.launchCallActivity(var1);
            return true;
        }
    }

    public MesiboCall.Call call(MesiboCall.CallProperties var1) {
        if (this.mBusy) {
            this.onerror(var1, 1);
            return null;
        } else {
            if (var1.parent == null) {
                var1.parent = getAppContext();
            }

            if (!var1.incoming && 0 != Mesibo.call((String)null, var1.video.enabled)) {
                this.onerror(var1, 1);
                return null;
            } else {
                synchronized(getInstance()) {
                    if (this.mCallp2p != null && this.mCallp2p.isCallInProgress()) {
                        return null;
                    } else {
                        this.getAudioFocus(var1.parent);
                        this.stopRing();
                        this.setImage(var1);
                        this.mCallp2p = new CallP2P();
                        if (!this.mCallp2p.setup(var1)) {
                            return null;
                        } else if (!var1.incoming) {
                            CallNotify.cancel();
                            return this.mCallp2p;
                        } else {
                            this.mCpForUi = var1;
                            if (var1.autoDetectAppState && Mesibo.getLifecycleState()) {
                                this.launchIncomingActivity(var1, false);
                                this.playIncomingNotification();
                                return this.mCallp2p;
                            } else {
                                if (TextUtils.isEmpty(var1.notify.title)) {
                                    var1.notify.title = var1.user.getName();
                                }

                                this.mCpForUi = var1;
                                CallNotify.showCallNotification(var1.parent, var1);
                                return this.mCallp2p;
                            }
                        }
                    }
                }
            }
        }
    }

    public void hangupIncoming() {
        Mesibo.hangup(0L);
        this.mCallp2p = null;
    }

    public boolean Mesibo_onCall(long var1, long var3, MesiboProfile var5, int var6) {
        MesiboCall.IncomingListener var7 = this.getListener();
        if (this.mBusy) {
            Mesibo.hangup(0L);
            if (var7 != null) {
                var7.MesiboCall_onNotify(4, var5, (var6 & 2) > 0);
            }

            return true;
        } else {
            boolean var2 = (var6 & 2) > 0;
            MesiboCall.CallProperties var8;
            if (var7 != null) {
                var8 = var7.MesiboCall_OnIncoming(var5, var2);
            } else {
                var8 = MesiboCall.getInstance().createCallProperties(var2);
            }

            if (var8 == null) {
                Mesibo.hangup(0L);
                return true;
            } else {
                var8.incoming = true;
                var8.user = var5;
                this.call(var8);
                return true;
            }
        }
    }

    public boolean Mesibo_onCallStatus(long var1, long var3, int var5, long var6, long var8, long var10, String var12) {
        if ((var5 & 64) > 0) {
            CallNotify.cancel();
            this.removeP2PCall();
        }

        return true;
    }

    public void Mesibo_onCallServer(int var1, String var2, String var3, String var4) {
    }

    public void setHoldOnCellularIncoming(boolean var1) {
        this.holdOnCellularIncoming = var1;
    }

    public void onCallStateChanged(int var1, String var2) {
        this.mBusy = var1 != 0;
        if (var1 == 0) {
            Mesibo.setCallProcessing(0, this.holdOnCellularIncoming ? 12 : 0);
        } else {
            if (1 == var1) {
                Mesibo.setCallProcessing(67, this.holdOnCellularIncoming && this.isAudioCallInProgress() ? 11 : 67);
            }

        }
    }

    public void Mesibo_onConfParitcipant(long var1, long var3, String var5, String var6, long var7, long var9) {
    }

    public void Mesibo_onConfCall(long var1, long var3, int var5, long var6, long var8, int var10, long var11, long var13, String var15, String var16, int var17) {
    }

    public static void test() {
    }

    public void getAudioFocus(Context var1) {
        AudioManager var2;
        (var2 = (AudioManager)var1.getSystemService(Context.AUDIO_SERVICE)).requestAudioFocus(this, 2, 1);
        var2.requestAudioFocus(this, 0, 1);
    }

    public void onAudioFocusChange(int var1) {
    }

    public void stopRing() {
        if (this.mRingPlayer != null) {
            this.mRingPlayer.stop();
            this.mRingPlayer = null;
        }

    }

    public void playIncomingNotification() {
        AudioManager var1 = (AudioManager)this.mAppContext.getSystemService(Context.AUDIO_SERVICE);
        this.incomingNotification = CallUtils.notifyIncomingCall(getAppContext(), var1, true);
    }

    public void stopIncomingNotification() {
        if (this.incomingNotification != null) {
            this.incomingNotification.stop();
            this.incomingNotification = null;
        }

    }

    protected void setStatusText(int var1, String var2) {
        this.mStatusMap.put(var1, var2);
    }

    protected String getStatusText(int var1, String var2) {
        if (0 == this.mStatusMap.size()) {
            return var2;
        } else {
            String var3;
            return !TextUtils.isEmpty(var3 = (String)this.mStatusMap.get(var1)) ? var3 : var2;
        }
    }
}

