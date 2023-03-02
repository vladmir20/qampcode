package com.qamp.app.qampCalls;

import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;

class ProxyVideoSink implements VideoSink {
    private MesiboVideoView mTarget = null;
    private boolean mForeground = true;

    ProxyVideoSink() {
    }

    public synchronized void onFrame(VideoFrame var1) {
        if (this.mTarget != null) {
            if (this.mForeground) {
                this.mTarget.onFrame(var1);
            }
        }
    }

    public void setForeground(boolean var1) {
        this.mForeground = var1;
    }

    public void stop() {
        if (this.mTarget != null) {
            this.mTarget.release();
        }

    }

    public synchronized void setTarget(MesiboVideoView var1) {
        this.mTarget = var1;
    }

    public MesiboVideoView getTarget() {
        return this.mTarget;
    }
}

