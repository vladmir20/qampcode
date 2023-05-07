/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:40 AM
 *
 */

package com.qamp.app.qampcallss.api;

import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;

class ProxyVideoSink implements VideoSink {
    private boolean mForeground = true;
    private MesiboVideoView mTarget = null;

    ProxyVideoSink() {
    }

    public MesiboVideoView getTarget() {
        return this.mTarget;
    }

    public synchronized void onFrame(VideoFrame videoFrame) {
        if (this.mTarget != null) {
            if (this.mForeground) {
                this.mTarget.onFrame(videoFrame);
            }
        }
    }

    public void setForeground(boolean z) {
        this.mForeground = z;
    }

    public synchronized void setTarget(MesiboVideoView mesiboVideoView) {
        this.mTarget = mesiboVideoView;
    }

    public void stop() {
        if (this.mTarget != null) {
            this.mTarget.release();
        }
    }
}
