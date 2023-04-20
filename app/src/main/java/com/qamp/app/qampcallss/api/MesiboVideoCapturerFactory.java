/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:32 PM
 *
 */

package com.qamp.app.qampcallss.api;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;

import com.mesibo.calls.api.MesiboCall;

import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.FileVideoCapturer;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.VideoCapturer;

import java.io.IOException;
import java.util.Iterator;

public class MesiboVideoCapturerFactory {
    public static final String TAG = "VideoCapturer";
    private static boolean mDumped = false;
    private VideoCapturer mCapturer = null;

    private VideoCapturer createCameraCapturer(CameraEnumerator cameraEnumerator, boolean z) {
        CameraVideoCapturer createCapturer;
        CameraVideoCapturer createCapturer2;
        String[] deviceNames = cameraEnumerator.getDeviceNames();
        for (String str : deviceNames) {
            if (cameraEnumerator.isFrontFacing(str) && (createCapturer2 = cameraEnumerator.createCapturer(str, (CameraVideoCapturer.CameraEventsHandler) null)) != null) {
                return createCapturer2;
            }
        }
        if (z) {
            return null;
        }
        for (String str2 : deviceNames) {
            if (!cameraEnumerator.isFrontFacing(str2) && (createCapturer = cameraEnumerator.createCapturer(str2, (CameraVideoCapturer.CameraEventsHandler) null)) != null) {
                return createCapturer;
            }
        }
        return null;
    }

    private static void dumpDevices(CameraEnumerator cameraEnumerator) {
        if (!mDumped) {
            mDumped = true;
            for (String str : cameraEnumerator.getDeviceNames()) {
                cameraEnumerator.isFrontFacing(str);
                Iterator it = cameraEnumerator.getSupportedFormats(str).iterator();
                while (it.hasNext()) {
                    it.next();
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public VideoCapturer create(Context context, boolean z, boolean z2, boolean z3, String str) {
        Object camera1Enumerator;
        stop();
        if (str != null) {
            try {
                this.mCapturer = new FileVideoCapturer(str);
                return this.mCapturer;
            } catch (IOException e) {
                return null;
            }
        } else {
            if (!z2 || Camera2Enumerator.isSupported(context)) {
                camera1Enumerator = new Camera1Enumerator(z3);
            } else if (!z3) {
                return null;
            } else {
                camera1Enumerator = new Camera2Enumerator(context);
            }
            this.mCapturer = createCameraCapturer((Camera1Enumerator)camera1Enumerator, z);
            return this.mCapturer;
        }
    }

    /* access modifiers changed from: protected */
    @TargetApi(21)
    public VideoCapturer createScreenCapturer(int i, Intent intent) {
        if (i != -1) {
            return null;
        }
        return new ScreenCapturerAndroid(intent, new MediaProjection.Callback() {
            public void onStop() {
            }
        });
    }

    public void pause() {
        if (this.mCapturer != null) {
            try {
                this.mCapturer.stopCapture();
            } catch (Exception e) {
            }
        }
    }

    public void resume() {
        if (this.mCapturer == null) {
        }
    }

    @TargetApi(21)
    public void startScreenCapture(Activity activity) {
        activity.startActivityForResult(((MediaProjectionManager) activity.getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE)).createScreenCaptureIntent(), MesiboCall.MESIBOCALL_PERMISSION_REQUEST_CODE);
    }

    public void stop() {
        if (this.mCapturer != null) {
            pause();
            this.mCapturer = null;
        }
    }
}
