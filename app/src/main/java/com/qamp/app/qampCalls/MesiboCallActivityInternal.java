package com.qamp.app.qampCalls;



import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;

public class MesiboCallActivityInternal extends HardwareAwareActivity {
    private static final String TAG = "QampCallActivity";
    private boolean isError;
    private boolean callControlFragmentVisible = true;
    private static Intent mediaProjectionPermissionResultData;
    private static int mediaProjectionPermissionResultCode;
    private CallPrivate mCall = null;
    private CallContext mCallCtx = null;
    protected MesiboCall.CallProperties mCp = null;
    private Thread closerThread = null;
    private boolean mGroupCallActivity = false;
    MesiboCall.MesiboParticipant mPublisher;

    public MesiboCallActivityInternal() {
    }

    public void setGroupCallActivity() {
        this.mGroupCallActivity = true;
    }

    public void onCreate(Bundle var1) {
        super.onCreate(var1);
        if (!this.mGroupCallActivity) {
            this.mCp = CallManager.getInstance().getCallPropertiesForUi();
            this.mCp.activity = (QampCallActivity)this;
            int var2;
            if ((var1 = this.getIntent().getExtras()) != null && this.mCp.incoming && (var2 = var1.getInt("nid")) >= 0) {
                CallNotify.cancel(CallManager.getAppContext(), var2);
            }

            if (TextUtils.isEmpty(this.mCp.user.address)) {
                this.finish();
            } else {
                this.mCall = (CallPrivate) CallManager.getInstance().getActiveCall();
                if (this.mCall == null && this.mCp.incoming) {
                    this.finish();
                }
            }
        }
    }

    protected void initCall(MesiboCall.Call var1) {
        this.mCall = (CallPrivate)var1;
        this.mCallCtx = this.mCall.getCallContext();
        this.mCp = this.mCall.getCallProperties();
        this.mCallCtx.cp.autoAnswer = this.mCp.autoAnswer;
        this.mCallCtx.cp.activity = (QampCallActivity)this;
        if (!this.mCallCtx.cp.video.enabled) {
            this.setVolumeControlStream(0);
        }

    }

    public void onRequestPermissionsResult(int var1, String[] var2, int[] var3) {
        if (var3.length <= 0 || var3[0] != 0) {
            this.finish();
        }

    }

    public void onActivityResult(int var1, int var2, Intent var3) {
        super.onActivityResult(var1, var2, var3);
        if (var1 == 4097) {
            if (this.mCall != null) {
                this.mCall.onActivityResult(var1, var2, var3);
            } else if (this.mGroupCallActivity) {
                GroupCall var4;
                if ((var4 = (GroupCall) CallManager.getInstance().getActiveGroupCall()) != null) {
                    ParticipantBase var5;
                    if ((var5 = var4.getActivePublisher()) != null) {
                        var5.onActivityResult(var1, var2, var3);
                    }
                }
            }
        }
    }

    public void onBackPressed() {
        this.finish();
    }

    public void setPublisher(MesiboCall.MesiboParticipant var1) {
    }

    public void onStop() {
        super.onStop();
        if (this.mCall != null) {
            this.mCall.OnBackground();
        }

    }

    public void onStart() {
        super.onStart();
        if (this.mCall != null) {
            this.mCall.OnForeground();
        }

    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.closerThread != null) {
            this.closerThread.interrupt();
        }

    }

    public boolean onKeyDown(int var1, KeyEvent var2) {
        if (this.mCall == null) {
            return super.onKeyDown(var1, var2);
        } else if (this.mCallCtx.cp.incoming && !this.mCallCtx.answered) {
            if (25 != var1 && 24 != var1) {
                return super.onKeyDown(var1, var2);
            } else {
                CallManager.getInstance().stopIncomingNotification();
                return true;
            }
        } else {
            return super.onKeyDown(var1, var2);
        }
    }

    public void MesiboCall_Init(MesiboCall.InProgressListener var1) {
    }

    public void delayedFinish(final long var1) {
        CallManager.getInstance().stopIncomingNotification();
        CallManager.getInstance().stopRing();
        if (this.mCall != null) {
            if (this.closerThread == null) {
                this.closerThread = new Thread(new Runnable() {
                    public void run() {
                        SystemClock.sleep(var1);
                        if (!MesiboCallActivityInternal.this.closerThread.isInterrupted()) {
                            MesiboCallActivityInternal.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    MesiboCallActivityInternal.this.finish();
                                }
                            });
                        }
                    }
                });
                this.closerThread.start();
            }
        }
    }
}

