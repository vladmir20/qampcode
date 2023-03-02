package com.qamp.app.qampCalls;


import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;

import org.webrtc.RendererCommon.RendererEvents;
import org.webrtc.RendererCommon.ScalingType;
import org.webrtc.SurfaceViewRenderer;

import java.lang.ref.WeakReference;

public class MesiboVideoViewInternal extends SurfaceViewRenderer {
    private static final String TAG = "MesiboVideoViewInternal";
    private Context mContext = null;
    private boolean mInitDone = false;
    private boolean mStopped = false;
    private boolean mHardwareScaler = false;
    private boolean mOverlay = false;
    private boolean mScaleFit = false;
    private boolean mMirror = false;
    private ScalingType scalingType;
    private int mWidth;
    private int mHeight;
    private int mLeft;
    private int mRight;
    private int mTop;
    private int mBottom;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoRotation;
    private boolean mAutoResize;
    private LayoutParams mLayoutParams;
    private WeakReference<VideoViewListener> mListener;
    private MesiboCall.MesiboParticipant mParticipant;

    public MesiboVideoViewInternal(Context var1) {
        super(var1);
        this.scalingType = ScalingType.SCALE_ASPECT_FILL;
        this.mWidth = 0;
        this.mHeight = 0;
        this.mLeft = 0;
        this.mRight = 0;
        this.mTop = 0;
        this.mBottom = 0;
        this.mVideoWidth = 0;
        this.mVideoHeight = 0;
        this.mVideoRotation = 0;
        this.mAutoResize = false;
        this.mLayoutParams = null;
        this.mListener = null;
        this.mParticipant = null;
        this.mContext = var1;
    }

    public MesiboVideoViewInternal(Context var1, AttributeSet var2) {
        super(var1, var2);
        this.scalingType = ScalingType.SCALE_ASPECT_FILL;
        this.mWidth = 0;
        this.mHeight = 0;
        this.mLeft = 0;
        this.mRight = 0;
        this.mTop = 0;
        this.mBottom = 0;
        this.mVideoWidth = 0;
        this.mVideoHeight = 0;
        this.mVideoRotation = 0;
        this.mAutoResize = false;
        this.mLayoutParams = null;
        this.mListener = null;
        this.mParticipant = null;
        this.mContext = var1;
    }

    protected boolean isInitDone() {
        return this.mInitDone;
    }

    protected void setListener(VideoViewListener var1) {
        this.mListener = new WeakReference(var1);
    }

    protected VideoViewListener getListener() {
        VideoViewListener var1 = null;
        if (this.mListener != null) {
            var1 = (VideoViewListener)this.mListener.get();
        }

        return (VideoViewListener)(var1 == null ? CallManager.getInstance().getDummyListener() : var1);
    }

    protected void onMeasure(int var1, int var2) {
        super.onMeasure(var1, var2);
    }

    public static int pxToDp(int var0) {
        return (int)((float)var0 / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int var0) {
        return (int)((float)var0 * Resources.getSystem().getDisplayMetrics().density);
    }

    protected void onLayout(boolean var1, int var2, int var3, int var4, int var5) {
        this.mTop = var3;
        this.mLeft = var2;
        this.mRight = var4;
        this.mBottom = var5;
        this.mWidth = this.mRight - this.mLeft;
        this.mHeight = this.mBottom - this.mTop;
        (new StringBuilder("onLayout ")).append(this.mWidth).append(",").append(this.mHeight);
        if (this.mLayoutParams == null) {
            this.mLayoutParams = this.getLayoutParams();
            if (this.mLayoutParams.height > 0) {
                pxToDp(this.mLayoutParams.height);
            }
        }

        super.onLayout(var1, var2, var3, var4, var5);
    }

    protected void onSizeChanged(int var1, int var2, int var3, int var4) {
        super.onSizeChanged(var1, var2, var3, var4);
        (new StringBuilder("onSizeChanged ")).append(var1).append(",").append(var2).append(",").append(var3).append(",").append(var4);
        this.getListener().MesiboVideoView_OnViewSizeChanged(var1, var2);
    }

    protected void stop() {
        if (!this.mStopped) {
            this.mStopped = true;
            this.mInitDone = false;
            this.release();
        }
    }

    protected void _init(org.webrtc.EglBase.Context var1) {
        if (!this.mInitDone) {
            this.mInitDone = true;
            super.init(var1, new RendererListener(this));
            this.setScalingType(this.scalingType);
            this.setEnableHardwareScaler(this.mHardwareScaler);
            if (this.mOverlay) {
                this.setZOrderMediaOverlay(true);
            }

        }
    }

    protected void enableHardwareScaler(boolean var1) {
        this.mHardwareScaler = var1;
        if (this.mInitDone) {
            this.setEnableHardwareScaler(this.mHardwareScaler);
        }

    }

    protected void enableOverlay(boolean var1) {
        this.mOverlay = var1;
        if (this.mInitDone) {
            this.setZOrderMediaOverlay(this.mOverlay);
        }

    }

    protected void enablePip(boolean var1) {
        this.enableHardwareScaler(var1);
        this.enableOverlay(var1);
        this.scaleToFit(var1);
    }

    protected void scaleToFit(boolean var1) {
        this.mScaleFit = var1;
        this.scalingType = var1 ? ScalingType.SCALE_ASPECT_FIT : ScalingType.SCALE_ASPECT_FILL;
        if (this.mInitDone) {
            this.setScalingType(this.scalingType);
        }

    }

    protected void scaleToFill(boolean var1) {
        this.scaleToFit(!var1);
    }

    protected boolean toggleScaling() {
        this.scaleToFit(!this.mScaleFit);
        return this.mScaleFit;
    }

    protected void enableMirror(boolean var1) {
        if (this.mMirror != var1) {
            this.mMirror = var1;
            this.setMirror(var1);
        }
    }

    protected void enableAutoResize(boolean var1) {
        this.mAutoResize = var1;
    }

    private void resizeIfNeeded() {
        this.getListener().MesiboVideoView_OnVideoResolutionChanged(this.mVideoWidth, this.mVideoHeight, this.mVideoRotation);
        this.setScalingType(this.scalingType);
        if (this.mAutoResize && this.mVideoHeight != 0 && this.mVideoWidth != 0) {
            LayoutParams var1;
            (var1 = this.getLayoutParams()).width = this.mRight - this.mLeft;
            var1.height = var1.width * this.mVideoHeight / this.mVideoWidth;
            this.setLayoutParams(var1);
        }
    }

    public void setVisibility(int var1) {
        super.setVisibility(var1);
    }

    protected void MesiboView_OnFrameResolutionChanged(int var1, int var2, int var3) {
        this.mVideoWidth = var1;
        this.mVideoHeight = var2;
        this.mVideoRotation = var3;
        CallManager.getInstance().getUiHandler().post(new Runnable() {
            public void run() {
                MesiboVideoViewInternal.this.resizeIfNeeded();
            }
        });
    }

    protected void setParticipant(MesiboCall.MesiboParticipant var1) {
        this.mParticipant = var1;
        if (var1 == null) {
            this.stop();
        }

    }

    protected MesiboCall.MesiboParticipant getParticipant() {
        return this.mParticipant;
    }

    public static class RendererListener implements RendererEvents {
        MesiboVideoViewInternal v;

        RendererListener(MesiboVideoViewInternal var1) {
            this.v = var1;
        }

        public void onFirstFrameRendered() {
        }

        public void onFrameResolutionChanged(int var1, int var2, int var3) {
            this.v.MesiboView_OnFrameResolutionChanged(var1, var2, var3);
        }
    }

    public interface VideoViewListener {
        void MesiboVideoView_OnVideoResolutionChanged(int var1, int var2, int var3);

        void MesiboVideoView_OnViewSizeChanged(int var1, int var2);
    }
}

