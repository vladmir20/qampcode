package com.qamp.app.qampCalls;

import android.app.Activity;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;



public class MesiboGroupCallView implements MesiboCall.MesiboParticipantSortListener {
    public static final String TAG = "GroupCallAdapter";
    private FrameLayout mFrameLayout;
    private float mWidth = 0.0F;
    private float mHeight = 0.0F;
    private boolean mFullScreenMode = false;
    private MesiboCall.MesiboGroupCall mGroupCall = null;

    public MesiboGroupCallView(Activity var1, FrameLayout var2, MesiboCall.MesiboGroupCall var3) {
        ViewGroup.LayoutParams var4 = var2.getLayoutParams();
        this.mWidth = (float)var4.width;
        this.mHeight = (float)var4.height;
        DisplayMetrics var5;
        if ((this.mWidth < 0.0F || this.mHeight < 0.0F) && (var5 = this.getDisplayMetrics(var1)) != null) {
            if (this.mWidth < 0.0F) {
                this.mWidth = (float)var5.widthPixels;
            }

            if (this.mHeight < 0.0F) {
                this.mHeight = (float)var5.heightPixels;
            }
        }

        this.mFrameLayout = var2;
        this.mGroupCall = var3;
    }

    public void setStreams(ArrayList<MesiboParticipantViewHolder> var1) {
        for(int var2 = 0; var2 < var1.size(); ++var2) {
            (new StringBuilder("setStreams ")).append(((MesiboParticipantViewHolder)var1.get(var2)).toString());
        }

        this.mFrameLayout.removeAllViewsInLayout();
        ArrayList var5 = this.mGroupCall.sort(this, var1, this.mWidth, this.mHeight, 0, 8, (MesiboCall.MesiboParticipantSortParams)null);

        for(int var4 = 0; var4 < var5.size(); ++var4) {
            MesiboParticipantViewHolder var3;
            (var3 = (MesiboParticipantViewHolder)var5.get(var4)).setStreamControls();
            var3.layout(this.mFrameLayout);
        }

    }

    public MesiboCall.MesiboParticipant ParticipantSort_onGetParticipant(Object var1) {
        return ((MesiboParticipantViewHolder)var1).getParticipant();
    }

    public void ParticipantSort_onSetCoordinates(Object var1, int var2, float var3, float var4, float var5, float var6) {
        ((MesiboParticipantViewHolder)var1).setCoordinates(var2, var3, var4, var5, var6);
    }

    private DisplayMetrics getDisplayMetrics(Activity var1) {
        if (var1 == null) {
            return null;
        } else {
            Display var3 = var1.getWindowManager().getDefaultDisplay();
            DisplayMetrics var2 = new DisplayMetrics();
            if (Build.VERSION.SDK_INT >= 17) {
                var3.getRealMetrics(var2);
            } else {
                var3.getMetrics(var2);
            }

            return var2;
        }
    }
}
