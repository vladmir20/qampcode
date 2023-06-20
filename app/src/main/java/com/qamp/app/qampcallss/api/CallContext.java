/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 3:15 PM
 *
 */

package com.qamp.app.qampcallss.api;

import android.content.Context;

public class CallContext {
    protected long answerTs = 0;
    protected boolean answered = false;
    protected boolean answeredVideo = false;
    protected boolean audioMute = false;
    protected long callid = 0;
    protected Context context;

    /* renamed from: cp */
    protected MesiboCall.CallProperties f0cp;
    protected boolean firstTimeVisible = true;
    protected boolean hold = false;
    protected boolean iceConnected = false;
    protected boolean inCallSound = false;
    protected boolean inForeground = false;
    protected boolean p2pConnected = false;
    protected boolean remoteAudioMute = false;
    protected boolean remoteVideoMute = false;
    protected int runTimeMs = 0;
    protected int status = 0;
    protected boolean videoMute = false;
    protected boolean videoMutedDueToBackground = false;
    protected boolean videoSwapped = true;

    /* JADX WARNING: type inference failed for: r0v7, types: [android.content.Context, com.mesibo.calls.api.MesiboCallActivity] */
    public CallContext(MesiboCall.CallProperties callProperties) {
        this.f0cp = callProperties;
        this.answeredVideo = callProperties.video.enabled;
        this.context = callProperties.parent;
        if (callProperties.activity != null) {
            this.context = callProperties.activity;
        }
        if (this.context == null) {
            this.context = CallManager.getAppContext();
        }
    }

    public long getAnswerTimestamp() {
        return this.answerTs;
    }

    public boolean isAnswered() {
        return this.answered;
    }

    public boolean isFirstTimeVisible() {
        return this.firstTimeVisible;
    }

    public boolean isIncoming() {
        return this.f0cp.incoming;
    }

    public boolean isVideoCall() {
        return this.f0cp.video.enabled;
    }
}
