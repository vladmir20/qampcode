package com.qamp.app.qampCalls;

import android.content.Context;

public class CallContext {
    protected MesiboCall.CallProperties cp;
    protected Context context;
    protected int runTimeMs = 0;
    protected long callid = 0L;
    protected int status = 0;
    protected boolean answered = false;
    protected boolean answeredVideo = false;
    protected long answerTs = 0L;
    protected boolean firstTimeVisible = true;
    protected boolean p2pConnected = false;
    protected boolean hold = false;
    protected boolean inCallSound = false;
    protected boolean iceConnected = false;
    protected boolean inForeground = false;
    protected boolean videoSwapped = true;
    protected boolean remoteAudioMute = false;
    protected boolean remoteVideoMute = false;
    protected boolean audioMute = false;
    protected boolean videoMute = false;
    protected boolean videoMutedDueToBackground = false;

    public CallContext(MesiboCall.CallProperties var1) {
        this.cp = var1;
        this.answeredVideo = var1.video.enabled;
        this.context = var1.parent;
        if (var1.activity != null) {
            this.context = var1.activity;
        }

        if (this.context == null) {
            this.context = CallManager.getAppContext();
        }

    }

    public long getAnswerTimestamp() {
        return this.answerTs;
    }

    public boolean isVideoCall() {
        return this.cp.video.enabled;
    }

    public boolean isIncoming() {
        return this.cp.incoming;
    }

    public boolean isAnswered() {
        return this.answered;
    }

    public boolean isFirstTimeVisible() {
        return this.firstTimeVisible;
    }
}

