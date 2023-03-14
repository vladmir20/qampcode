package com.qamp.app.qampcallss.api;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.ViewGroup;

import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

import java.lang.ref.WeakReference;

public class MesiboVideoViewInternal extends SurfaceViewRenderer {
    private static final String TAG = "MesiboVideoViewInternal";
    private boolean mAutoResize = false;
    private int mBottom = 0;
    private Context mContext = null;
    private boolean mHardwareScaler = false;
    private int mHeight = 0;
    private boolean mInitDone = false;
    private ViewGroup.LayoutParams mLayoutParams = null;
    private int mLeft = 0;
    private WeakReference<VideoViewListener> mListener = null;
    private boolean mMirror = false;
    private boolean mOverlay = false;
    private MesiboCall.MesiboParticipant mParticipant = null;
    private int mRight = 0;
    private boolean mScaleFit = false;
    private boolean mStopped = false;
    private int mTop = 0;
    private int mVideoHeight = 0;
    private int mVideoRotation = 0;
    private int mVideoWidth = 0;
    private int mWidth = 0;
    private RendererCommon.ScalingType scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;

    public static class RendererListener implements RendererCommon.RendererEvents {

        /* renamed from: v */
        MesiboVideoViewInternal f3v;

        RendererListener(MesiboVideoViewInternal mesiboVideoViewInternal) {
            this.f3v = mesiboVideoViewInternal;
        }

        public void onFirstFrameRendered() {
        }

        public void onFrameResolutionChanged(int i, int i2, int i3) {
            this.f3v.MesiboView_OnFrameResolutionChanged(i, i2, i3);
        }
    }

    public interface VideoViewListener {
        void MesiboVideoView_OnVideoResolutionChanged(int i, int i2, int i3);

        void MesiboVideoView_OnViewSizeChanged(int i, int i2);
    }

    public MesiboVideoViewInternal(Context context) {
        super(context);
        this.mContext = context;
    }

    public MesiboVideoViewInternal(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    public static int dpToPx(int i) {
        return (int) (((float) i) * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int i) {
        return (int) (((float) i) / Resources.getSystem().getDisplayMetrics().density);
    }

    /* access modifiers changed from: private */
    public void resizeIfNeeded() {
        getListener().MesiboVideoView_OnVideoResolutionChanged(this.mVideoWidth, this.mVideoHeight, this.mVideoRotation);
        setScalingType(this.scalingType);
        if (this.mAutoResize && this.mVideoHeight != 0 && this.mVideoWidth != 0) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = this.mRight - this.mLeft;
            layoutParams.height = (layoutParams.width * this.mVideoHeight) / this.mVideoWidth;
            setLayoutParams(layoutParams);
        }
    }

    /* access modifiers changed from: protected */
    public void MesiboView_OnFrameResolutionChanged(int i, int i2, int i3) {
        this.mVideoWidth = i;
        this.mVideoHeight = i2;
        this.mVideoRotation = i3;
        CallManager.getInstance().getUiHandler().post(new Runnable() {
            public void run() {
                MesiboVideoViewInternal.this.resizeIfNeeded();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void _init(EglBase.Context context) {
        if (!this.mInitDone) {
            this.mInitDone = true;
            MesiboVideoViewInternal.super.init(context, new RendererListener(this));
            setScalingType(this.scalingType);
            setEnableHardwareScaler(this.mHardwareScaler);
            if (this.mOverlay) {
                setZOrderMediaOverlay(true);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void enableAutoResize(boolean z) {
        this.mAutoResize = z;
    }

    /* access modifiers changed from: protected */
    public void enableHardwareScaler(boolean z) {
        this.mHardwareScaler = z;
        if (this.mInitDone) {
            setEnableHardwareScaler(this.mHardwareScaler);
        }
    }

    /* access modifiers changed from: protected */
    public void enableMirror(boolean z) {
        if (this.mMirror != z) {
            this.mMirror = z;
            setMirror(z);
        }
    }

    /* access modifiers changed from: protected */
    public void enableOverlay(boolean z) {
        this.mOverlay = z;
        if (this.mInitDone) {
            setZOrderMediaOverlay(this.mOverlay);
        }
    }

    /* access modifiers changed from: protected */
    public void enablePip(boolean z) {
        enableHardwareScaler(z);
        enableOverlay(z);
        scaleToFit(z);
    }

    /* access modifiers changed from: protected */
    protected DummyListener getListener() {
        VideoViewListener videoViewListener = null;
        if (this.mListener != null) {
            videoViewListener = (VideoViewListener) this.mListener.get();
        }
        return (videoViewListener == null ? CallManager.getInstance().getDummyListener() : (DummyListener) videoViewListener);    }

    /* access modifiers changed from: protected */
    public MesiboCall.MesiboParticipant getParticipant() {
        return this.mParticipant;
    }

    /* access modifiers changed from: protected */
    public boolean isInitDone() {
        return this.mInitDone;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.mTop = i2;
        this.mLeft = i;
        this.mRight = i3;
        this.mBottom = i4;
        this.mWidth = this.mRight - this.mLeft;
        this.mHeight = this.mBottom - this.mTop;
        new StringBuilder("onLayout ").append(this.mWidth).append(",").append(this.mHeight);
        if (this.mLayoutParams == null) {
            this.mLayoutParams = getLayoutParams();
            if (this.mLayoutParams.height > 0) {
                pxToDp(this.mLayoutParams.height);
            }
        }
        MesiboVideoViewInternal.super.onLayout(z, i, i2, i3, i4);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        MesiboVideoViewInternal.super.onMeasure(i, i2);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        MesiboVideoViewInternal.super.onSizeChanged(i, i2, i3, i4);
        new StringBuilder("onSizeChanged ").append(i).append(",").append(i2).append(",").append(i3).append(",").append(i4);
        getListener().MesiboVideoView_OnViewSizeChanged(i, i2);
    }

    /* access modifiers changed from: protected */
    public void scaleToFill(boolean z) {
        scaleToFit(!z);
    }

    /* access modifiers changed from: protected */
    public void scaleToFit(boolean z) {
        this.mScaleFit = z;
        this.scalingType = z ? RendererCommon.ScalingType.SCALE_ASPECT_FIT : RendererCommon.ScalingType.SCALE_ASPECT_FILL;
        if (this.mInitDone) {
            setScalingType(this.scalingType);
        }
    }

    /* access modifiers changed from: protected */
    public void setListener(VideoViewListener videoViewListener) {
        this.mListener = new WeakReference<>(videoViewListener);
    }

    /* access modifiers changed from: protected */
    public void setParticipant(MesiboCall.MesiboParticipant mesiboParticipant) {
        this.mParticipant = mesiboParticipant;
        if (mesiboParticipant == null) {
            stop();
        }
    }

    public void setVisibility(int i) {
        MesiboVideoViewInternal.super.setVisibility(i);
    }

    /* access modifiers changed from: protected */
    public void stop() {
        if (!this.mStopped) {
            this.mStopped = true;
            this.mInitDone = false;
            release();
        }
    }

    /* access modifiers changed from: protected */
    public boolean toggleScaling() {
        scaleToFit(!this.mScaleFit);
        return this.mScaleFit;
    }
}
