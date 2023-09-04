/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 3:15 PM
 *
 */

package com.qamp.app.qampcallss.api;

import android.os.ParcelFileDescriptor;

import org.webrtc.PeerConnection;

import java.io.File;
import java.io.IOException;

public class RtcEventLog {
    private static final int OUTPUT_FILE_MAX_BYTES = 10000000;
    private static final String TAG = "RtcEventLog";
    private final PeerConnection peerConnection;
    private RtcEventLogState state = RtcEventLogState.INACTIVE;

    enum RtcEventLogState {
        INACTIVE,
        STARTED,
        STOPPED
    }

    public RtcEventLog(PeerConnection peerConnection2) {
        if (peerConnection2 == null) {
            throw new NullPointerException("The peer connection is null.");
        }
        this.peerConnection = peerConnection2;
    }

    public void start(File file) {
        if (this.state != RtcEventLogState.STARTED) {
            try {
                if (this.peerConnection.startRtcEventLog(ParcelFileDescriptor.open(file, 1006632960).detachFd(), OUTPUT_FILE_MAX_BYTES)) {
                    this.state = RtcEventLogState.STARTED;
                }
            } catch (IOException e) {
            }
        }
    }

    public void stop() {
        if (this.state == RtcEventLogState.STARTED) {
            this.peerConnection.stopRtcEventLog();
            this.state = RtcEventLogState.STOPPED;
        }
    }
}
