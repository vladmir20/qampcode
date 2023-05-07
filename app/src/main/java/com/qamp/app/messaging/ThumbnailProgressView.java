/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:40 AM
 *
 */

package com.qamp.app.messaging;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mesibo.api.MesiboMessage;
import com.qamp.app.R;
import com.qamp.app.messaging.AllUtils.MyTrace;

public class ThumbnailProgressView extends FrameLayout {
    public static final int STATE_DISPLAY = 0;
    public static final int STATE_INPROGRESS = 1;
    public static final int STATE_PROMPT = 2;
    private int mCurrentState = -1;
    private MessageData mData = null;
    private String mFileSize = null;
    FrameLayout mFrameLayout = null;
    LayoutInflater mInflater = null;
    ImageView mPictureView = null;
    ProgressBar mProgressBar = null;
    Button mTransferButton = null;

    public ThumbnailProgressView(Context context) {
        super(context);
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    public ThumbnailProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    public ThumbnailProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    public void init() {
        MyTrace.start("TPV-Inflate");
        this.mFrameLayout = (FrameLayout) this.mInflater.inflate(R.layout.thumbnail_progress_view, this, true);
        this.mPictureView = (ImageView) this.mFrameLayout.findViewById(R.id.imageView);
        this.mProgressBar = (ProgressBar) this.mFrameLayout.findViewById(R.id.progressBar);
        if (Build.VERSION.SDK_INT > 21) {
            this.mProgressBar.setProgressTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimary)));
        }
        MyTrace.stop();
        MyTrace.start("TPV-ProgressBarFilter");
        this.mProgressBar.getIndeterminateDrawable().setColorFilter(MesiboUI.getConfig().mToolbarColor, PorterDuff.Mode.MULTIPLY);
        MyTrace.stop();
        this.mTransferButton = (Button) this.mFrameLayout.findViewById(R.id.transferButton);
        setState(0);
    }

    public void setData(MessageData data) {
        boolean changed;
        if (this.mData != data) {
            changed = true;
        } else {
            changed = false;
        }
        this.mData = data;
        Bitmap image = this.mData.getImage();
        if (image != null) {
            MesiboMessage m = this.mData.getMesiboMessage();
            if (!m.isFileTransferInProgress() || changed) {
                this.mPictureView.setImageBitmap(image);
            }
            if (m.isFileReady() || m.openExternally) {
                setState(0);
            } else if (m.isFileTransferInProgress()) {
                setState(1);
                this.mProgressBar.setMax(100);
                int progress = m.getProgress();
                if (progress <= 0) {
                    this.mProgressBar.setIndeterminate(true);
                    return;
                }
                this.mProgressBar.setIndeterminate(false);
                this.mProgressBar.setProgress(progress);
            } else if (m.isFileTransferRequired()) {
                this.mFileSize = Utils.getFileSizeString(m.getFileSize());
                if (m.isFileTransferFailed()) {
                    this.mFileSize += " (Retry)";
                }
                Drawable img = null;
                if (m.isDownloadRequired()) {
                    img = getContext().getResources().getDrawable(MesiboConfiguration.PROGRESSVIEW_DOWNLOAD_SYMBOL);
                } else if (m.isUploadRequired()) {
                    img = getContext().getResources().getDrawable(MesiboConfiguration.PROGRESSVIEW_UPLOAD_SYMBOL);
                }
                this.mTransferButton.setCompoundDrawablesWithIntrinsicBounds(img, (Drawable) null, (Drawable) null, (Drawable) null);
                this.mTransferButton.setText(this.mFileSize);
                setState(2);
            }
        }
    }

    public void setState(int state) {
        if (state != this.mCurrentState) {
            this.mCurrentState = state;
            if (state == 0) {
                this.mTransferButton.setVisibility(8);
                this.mProgressBar.setVisibility(8);
            } else if (1 == state) {
                if (this.mCurrentState != state) {
                }
                this.mProgressBar.setVisibility(0);
                this.mTransferButton.setVisibility(8);
            } else {
                this.mTransferButton.setVisibility(0);
                this.mProgressBar.setVisibility(8);
            }
        }
    }

    public void setImage(Bitmap bmp) {
        this.mPictureView.setImageBitmap(bmp);
    }
}
