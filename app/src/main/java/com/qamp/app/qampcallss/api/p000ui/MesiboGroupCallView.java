/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 3:15 PM
 *
 */

package com.qamp.app.qampcallss.api.p000ui;

import android.app.Activity;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mesibo.calls.api.MesiboCall;

import java.util.ArrayList;

/* renamed from: com.mesibo.calls.api.ui.MesiboGroupCallView */
public class MesiboGroupCallView implements MesiboCall.MesiboParticipantSortListener {
    public static final String TAG = "GroupCallAdapter";
    private FrameLayout mFrameLayout;
    private boolean mFullScreenMode = false;
    private MesiboCall.MesiboGroupCall mGroupCall = null;
    private float mHeight = 0.0f;
    private float mWidth = 0.0f;

    public MesiboGroupCallView(Activity activity, FrameLayout frameLayout, MesiboCall.MesiboGroupCall mesiboGroupCall) {
        DisplayMetrics displayMetrics;
        ViewGroup.LayoutParams layoutParams = frameLayout.getLayoutParams();
        this.mWidth = (float) layoutParams.width;
        this.mHeight = (float) layoutParams.height;
        if ((this.mWidth < 0.0f || this.mHeight < 0.0f) && (displayMetrics = getDisplayMetrics(activity)) != null) {
            if (this.mWidth < 0.0f) {
                this.mWidth = (float) displayMetrics.widthPixels;
            }
            if (this.mHeight < 0.0f) {
                this.mHeight = (float) displayMetrics.heightPixels;
            }
        }
        this.mFrameLayout = frameLayout;
        this.mGroupCall = mesiboGroupCall;
    }

    private DisplayMetrics getDisplayMetrics(Activity activity) {
        if (activity == null) {
            return null;
        }
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= 17) {
            defaultDisplay.getRealMetrics(displayMetrics);
            return displayMetrics;
        }
        defaultDisplay.getMetrics(displayMetrics);
        return displayMetrics;
    }

    public MesiboCall.MesiboParticipant ParticipantSort_onGetParticipant(Object obj) {
        return ((MesiboParticipantViewHolder) obj).getParticipant();
    }

    public void ParticipantSort_onSetCoordinates(Object obj, int i, float f, float f2, float f3, float f4) {
        ((MesiboParticipantViewHolder) obj).setCoordinates(i, f, f2, f3, f4);
    }

    public void setStreams(ArrayList<MesiboParticipantViewHolder> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            new StringBuilder("setStreams ").append(arrayList.get(i).toString());
        }
        this.mFrameLayout.removeAllViewsInLayout();
        ArrayList<? extends Object> sort = this.mGroupCall.sort(this, arrayList, this.mWidth, this.mHeight, 0, 8, (MesiboCall.MesiboParticipantSortParams) null);
        for (int i2 = 0; i2 < sort.size(); i2++) {
            MesiboParticipantViewHolder mesiboParticipantViewHolder = (MesiboParticipantViewHolder) sort.get(i2);
            mesiboParticipantViewHolder.setStreamControls();
            mesiboParticipantViewHolder.layout(this.mFrameLayout);
        }
    }
}
