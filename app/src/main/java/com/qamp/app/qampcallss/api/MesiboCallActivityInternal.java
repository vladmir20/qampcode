package com.qamp.app.qampcallss.api;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.qamp.app.qampcallss.api.p000ui.QampCallsActivity;

public class MesiboCallActivityInternal extends HardwareAwareActivity {
    private static final String TAG = "MesiboCallActivity";
    private static int mediaProjectionPermissionResultCode;
    private static Intent mediaProjectionPermissionResultData;
    private boolean callControlFragmentVisible = true;
    /* access modifiers changed from: private */
    public Thread closerThread = null;
    private boolean isError;
    private CallPrivate mCall = null;
    private CallContext mCallCtx = null;
    protected MesiboCall.CallProperties mCp = null;
    private boolean mGroupCallActivity = false;
    MesiboCall.MesiboParticipant mPublisher;

    /* access modifiers changed from: private */
    public void done() {
        CallManager.getInstance().creatingCallActivity(false);
        finish();
    }

    public void MesiboCall_Init(MesiboCall.InProgressListener inProgressListener) {
    }

    public void delayedFinish(final long j) {
        CallManager.getInstance().stopIncomingNotification((MesiboCall.CallProperties) null);
        if (this.mCall != null && this.closerThread == null) {
            this.closerThread = new Thread(new Runnable() {
                public void run() {
                    SystemClock.sleep(j);
                    if (!MesiboCallActivityInternal.this.closerThread.isInterrupted()) {
                        MesiboCallActivityInternal.this.runOnUiThread(new Runnable() {
                            public void run() {
                                MesiboCallActivityInternal.this.done();
                            }
                        });
                    }
                }
            });
            this.closerThread.start();
        }
    }

    /* access modifiers changed from: protected */
    public void initCall(MesiboCall.Call call) {
        this.mCall = (CallPrivate) call;
        this.mCallCtx = this.mCall.getCallContext();
        this.mCp = this.mCall.getCallProperties();
        this.mCallCtx.f0cp.autoAnswer = this.mCp.autoAnswer;
        this.mCallCtx.f0cp.activity = (QampCallsActivity) this;
        if (!this.mCallCtx.f0cp.video.enabled) {
            setVolumeControlStream(0);
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        GroupCall groupCall;
        ParticipantBase activePublisher;
        if (i == 4097) {
            if (this.mCall != null) {
                this.mCall.onActivityResult(i, i2, intent);
            } else if (this.mGroupCallActivity && (groupCall = (GroupCall) CallManager.getInstance().getActiveGroupCall()) != null && (activePublisher = groupCall.getActivePublisher()) != null) {
                activePublisher.onActivityResult(i, i2, intent);
            }
        }
    }

    public void onBackPressed() {
        done();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Utils.enableSecureScreen(this);
        if (!this.mGroupCallActivity) {
            this.mCp = CallManager.getInstance().getCallPropertiesForUi();
            if (this.mCp == null) {
                finish();
            } else if (!CallManager.getInstance().creatingCallActivity(true)) {
                finish();
            } else {
                this.mCp.activity = (QampCallsActivity) this;
                this.mCp.uiInitializedOnce = true;
                Bundle extras = getIntent().getExtras();
                if (extras != null && this.mCp.incoming) {
                    if (!this.mCp.autoAnswer) {
                        this.mCp.autoAnswer = extras.getBoolean("autoAnswer", false);
                    }
                    if (this.mCp.autoAnswer) {
                        int i = extras.getInt("nid");
                        if (i >= 0) {
                            CallNotify.cancel(CallManager.getAppContext(), i);
                        }
                        if (this.mCp.callid != ((long) i)) {
                            CallNotify.cancel(CallManager.getAppContext(), this.mCp);
                        }
                    }
                }
                if (TextUtils.isEmpty(this.mCp.user.address)) {
                    done();
                    return;
                }
                this.mCall = (CallPrivate) CallManager.getInstance().getActiveCall();
                if (this.mCall == null && this.mCp.incoming) {
                    done();
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        CallManager.getInstance().creatingCallActivity(false);
        if (this.closerThread != null) {
            this.closerThread.interrupt();
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (this.mCall == null) {
            return super.onKeyDown(i, keyEvent);
        }
        if (!this.mCallCtx.f0cp.incoming || this.mCallCtx.answered) {
            return super.onKeyDown(i, keyEvent);
        }
        if (25 != i && 24 != i) {
            return super.onKeyDown(i, keyEvent);
        }
        CallManager.getInstance().stopIncomingNotification(this.mCallCtx.f0cp);
        return true;
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (iArr.length <= 0 || iArr[0] != 0) {
            done();
        }
    }

    public void onStart() {
        super.onStart();
        if (this.mCall != null) {
            this.mCall.OnForeground();
        }
    }

    public void onStop() {
        super.onStop();
        if (this.mCall != null) {
            this.mCall.OnBackground();
        }
    }

    public void setGroupCallActivity() {
        this.mGroupCallActivity = true;
    }

    public void setPublisher(MesiboCall.MesiboParticipant mesiboParticipant) {
    }
}
