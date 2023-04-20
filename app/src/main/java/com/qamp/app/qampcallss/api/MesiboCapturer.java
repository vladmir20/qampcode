/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:31 PM
 *
 */

package com.qamp.app.qampcallss.api;

import org.webrtc.CameraVideoCapturer;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSink;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public class MesiboCapturer implements CameraVideoCapturer.CameraSwitchHandler {
    private static final String TAG = "MesiboCapturer";
    VideoCapturer mCapturer;
    private boolean mEnabled;
    private int mFps;
    private boolean mFrontCamera;
    private int mHeight;
    private MesiboCapturerListener mListner;
    private VideoSink mRenderer;
    private boolean mStarted;
    private int mSwitchRequests;
    private int mSwitchRetries;
    private VideoTrack mVideoTrack;
    VideoSource mVideosource;
    private int mWidth;
    PeerConnectionClient m_pcc;

    /**public interface MesiboCapturerListener {
        void MesiboCapturer_OnStarted(MesiboCapturer mesiboCapturer, boolean z);

        void MesiboCapturer_OnSwitchCamera(MesiboCapturer mesiboCapturer, boolean z);
    }*/


    /**public MesiboCapturer(MesiboCapturer.MesiboCapturerListener r1, PeerConnectionClient r2, VideoCapturer r3, VideoSink r4, int r5, int r6, int r7, boolean r8) {
        /*
        // Can't load method instructions: Load method exception: Unknown instruction: 'invoke-custom' in method: com.mesibo.calls.api.MesiboCapturer.<init>(com.mesibo.calls.api.MesiboCapturer$MesiboCapturerListener, com.mesibo.calls.api.PeerConnectionClient, org.webrtc.VideoCapturer, org.webrtc.VideoSink, int, int, int, boolean):void, dex: classes.jar
        */
        /**throw new UnsupportedOperationException("Method not decompiled: com.mesibo.calls.api.MesiboCapturer.<init>(com.mesibo.calls.api.MesiboCapturer$MesiboCapturerListener, com.mesibo.calls.api.PeerConnectionClient, org.webrtc.VideoCapturer, org.webrtc.VideoSink, int, int, int, boolean):void");
    }*/

        public MesiboCapturer(MesiboCapturerListener var1, PeerConnectionClient var2, VideoCapturer var3, VideoSink var4, int var5, int var6, int var7, boolean var8) {
            this.mListner = var1;
            this.m_pcc = var2;
            this.mCapturer = var3;
            this.mRenderer = var4;
            this.mWidth = var5;
            this.mHeight = var6;
            this.mFps = var7;
            this.mFrontCamera = var8;
            SurfaceTextureHelper var9 = this.m_pcc.getSurfaceTextureHelper();
            PeerConnectionFactory var10 = this.m_pcc.getFactory();
            this.mVideosource = var10.createVideoSource(var3.isScreencast());
            this.mCapturer.initialize(var9, this.m_pcc.getAppContext(), this.mVideosource.getCapturerObserver());
            this.mVideoTrack = var10.createVideoTrack(this.m_pcc.getVideoTrackId(), this.mVideosource);
            this.m_pcc.getExecutor().execute(() -> {
                this.setEnabledInternal(true);
                this.startInternal(this.mRenderer);
            });
        }

    public VideoTrack getTrack() {
        return this.mVideoTrack;
    }

    public void setRendererInternal(VideoSink var1) {
        if (var1 == null) {
            if (this.mRenderer != null) {
                this.mVideoTrack.removeSink(this.mRenderer);
            }

        } else if (this.mVideoTrack != null) {
            this.mVideoTrack.addSink(var1);
            this.mRenderer = var1;
        }
    }

    public void setRenderer(VideoSink var1) {
        if (var1 != this.mRenderer) {
            this.m_pcc.getExecutor().execute(() -> {
                this.setRendererInternal(var1);
            });
        }
    }

    public void startInternal(VideoSink var1) {
        if (!this.mStarted) {
            this.mStarted = true;
            this.mCapturer.startCapture(this.mWidth, this.mHeight, this.mFps);
            this.setRendererInternal(var1);
            this.mListner.MesiboCapturer_OnStarted(this, true);
        }
    }

    public void restart(VideoSink var1) {
        if (!this.mStarted) {
            this.m_pcc.getExecutor().execute(() -> {
                this.startInternal(var1);
            });
        }
    }

    public void stop() {
        if (this.mStarted) {
            this.m_pcc.getExecutor().execute(() -> {
                try {
                    this.mCapturer.stopCapture();
                    this.mListner.MesiboCapturer_OnStarted(this, false);
                } catch (InterruptedException var1) {
                }

                this.mStarted = false;
            });
        }
    }

    public void destroy() {
        this.m_pcc.getExecutor().execute(() -> {
            if (this.mStarted) {
                try {
                    this.mCapturer.stopCapture();
                } catch (Exception var1) {
                }
            }

            this.mCapturer.dispose();
            this.mStarted = false;
        });
    }

    public void setEnabledInternal(boolean var1) {
        this.mEnabled = var1;
    }

    public void setEnabled1(boolean var1) {
        this.m_pcc.getExecutor().execute(() -> {
            this.setEnabledInternal(var1);
        });
    }

    public void switchCamera() {
        this.m_pcc.getExecutor().execute(() -> {
            if (this.mCapturer instanceof CameraVideoCapturer) {
                ((CameraVideoCapturer)this.mCapturer).switchCamera(this);
            }

        });
    }

    public void changeFormat(int var1, int var2, int var3) {
        this.m_pcc.getExecutor().execute(() -> {
            this.mVideosource.adaptOutputFormat(var1, var2, var3);
        });
    }

    public void onCameraSwitchDone(boolean var1) {
        if (this.mFrontCamera && var1 && this.mSwitchRetries < 2) {
            ++this.mSwitchRetries;
            this.switchCamera();
        } else {
            this.mSwitchRetries = 0;
            this.mFrontCamera = var1;
            if (this.mStarted) {
                this.mListner.MesiboCapturer_OnSwitchCamera(this, var1);
            }

        }
    }

    public void onCameraSwitchError(String var1) {
    }

    public interface MesiboCapturerListener {
        void MesiboCapturer_OnStarted(MesiboCapturer var1, boolean var2);

        void MesiboCapturer_OnSwitchCamera(MesiboCapturer var1, boolean var2);
    }
}
