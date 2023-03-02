package com.qamp.app.qampCalls;


import android.os.ParcelFileDescriptor;

import org.webrtc.PeerConnection;

import java.io.File;
import java.io.IOException;

public class RtcEventLog {
    private static final String TAG = "RtcEventLog";
    private static final int OUTPUT_FILE_MAX_BYTES = 10000000;
    private final PeerConnection peerConnection;
    private RtcEventLogState state;

    public RtcEventLog(PeerConnection var1) {
        this.state = RtcEventLogState.INACTIVE;
        if (var1 == null) {
            throw new NullPointerException("The peer connection is null.");
        } else {
            this.peerConnection = var1;
        }
    }

    public void start(File var1) {
        if (this.state != RtcEventLogState.STARTED) {
            ParcelFileDescriptor var3;
            try {
                var3 = ParcelFileDescriptor.open(var1, 1006632960);
            } catch (IOException var2) {
                return;
            }

            if (this.peerConnection.startRtcEventLog(var3.detachFd(), 10000000)) {
                this.state = RtcEventLogState.STARTED;
            }
        }
    }

    public void stop() {
        if (this.state == RtcEventLogState.STARTED) {
            this.peerConnection.stopRtcEventLog();
            this.state = RtcEventLogState.STOPPED;
        }
    }

    static enum RtcEventLogState {
        INACTIVE,
        STARTED,
        STOPPED;

        private RtcEventLogState() {
        }
    }
}

