package com.qamp.app.qampCalls;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection.Callback;
import android.media.projection.MediaProjectionManager;

import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.CameraVideoCapturer.CameraEventsHandler;
import org.webrtc.FileVideoCapturer;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.VideoCapturer;

import java.io.IOException;

class MesiboVideoCapturerFactory {
    public static final String TAG = "VideoCapturer";
    private VideoCapturer mCapturer = null;

    public MesiboVideoCapturerFactory() {
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator var1, boolean var2) {
        String[] var3;
        String[] var4;
        int var5 = (var4 = var3 = var1.getDeviceNames()).length;

        int var6;
        String var7;
        CameraVideoCapturer var8;
        for(var6 = 0; var6 < var5; ++var6) {
            var7 = var4[var6];
            if (var1.isFrontFacing(var7) && (var8 = var1.createCapturer(var7, (CameraEventsHandler)null)) != null) {
                return var8;
            }
        }

        if (var2) {
            return null;
        } else {
            var4 = var3;
            var5 = var3.length;

            for(var6 = 0; var6 < var5; ++var6) {
                var7 = var4[var6];
                if (!var1.isFrontFacing(var7) && (var8 = var1.createCapturer(var7, (CameraEventsHandler)null)) != null) {
                    return var8;
                }
            }

            return null;
        }
    }

    @TargetApi(21)
    public void startScreenCapture(Activity var1) {
        MediaProjectionManager var2 = (MediaProjectionManager)var1.getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        var1.startActivityForResult(var2.createScreenCaptureIntent(), 4097);
    }

    @TargetApi(21)
    protected VideoCapturer createScreenCapturer(int var1, Intent var2) {
        return var1 != -1 ? null : new ScreenCapturerAndroid(var2, new Callback() {
            public void onStop() {
            }
        });
    }

    protected VideoCapturer create(Context var1, boolean var2, boolean var3, boolean var4, String var5) {
        this.stop();
        if (var5 != null) {
            try {
                this.mCapturer = new FileVideoCapturer(var5);
            } catch (IOException var6) {
                return null;
            }

            return this.mCapturer;
        } else {
            Object var7;
            if (var3 && !Camera2Enumerator.isSupported(var1)) {
                if (!var4) {
                    return null;
                }

                var7 = new Camera2Enumerator(var1);
            } else {
                var7 = new Camera1Enumerator(var4);
            }

            this.mCapturer = this.createCameraCapturer((CameraEnumerator)var7, var2);
            return this.mCapturer;
        }
    }

    public void resume() {
        if (this.mCapturer != null) {
            ;
        }
    }

    public void pause() {
        if (this.mCapturer != null) {
            try {
                this.mCapturer.stopCapture();
            } catch (Exception var1) {
            }
        }
    }

    public void stop() {
        if (this.mCapturer != null) {
            this.pause();
            this.mCapturer = null;
        }
    }
}

