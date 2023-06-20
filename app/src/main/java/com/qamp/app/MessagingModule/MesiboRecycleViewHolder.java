/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.MessagingModule;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mesibo.api.MesiboMessage;

public class MesiboRecycleViewHolder extends RecyclerView.ViewHolder {
    public static final int TYPE_CUSTOM = 100;
    public static final int TYPE_DATETIME = 3;
    public static final int TYPE_E2E = 6;
    public static final int TYPE_HEADER = 4;
    public static final int TYPE_INCOMING = 1;
    public static final int TYPE_INVISIBLE = 7;
    public static final int TYPE_MISSEDCALL = 5;
    public static final int TYPE_NONE = 0;
    public static final int TYPE_OUTGOING = 2;
    private MessageAdapter mAdapter = null;
    private boolean mCustom = false;
    private int mPosition = -1;
    private int mType = 0;

    public interface Listener {
        void Mesibo_oUpdateViewHolder(MesiboRecycleViewHolder mesiboRecycleViewHolder, MesiboMessage mesiboMessage);

        void Mesibo_onBindViewHolder(MesiboRecycleViewHolder mesiboRecycleViewHolder, int i, boolean z, MesiboMessage mesiboMessage);

        MesiboRecycleViewHolder Mesibo_onCreateViewHolder(ViewGroup viewGroup, int i);

        int Mesibo_onGetItemViewType(MesiboMessage mesiboMessage);

        void Mesibo_onViewRecycled(MesiboRecycleViewHolder mesiboRecycleViewHolder);
    }

    public static class MesiboViewData {
        String activityStatus;
        String message;
        MesiboMessage params;
        int screenType;
        boolean selected;
        int viewType;
    }

    public void reset() {
    }

    public MesiboRecycleViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    /* access modifiers changed from: protected */
    public int getItemPosition() {
        return this.mPosition;
    }

    /* access modifiers changed from: protected */
    public void setItemPosition(int pos) {
        this.mPosition = pos;
    }

    public int getType() {
        return this.mType;
    }

    public void refresh() {
        if (this.mAdapter != null && this.mPosition > 0) {
            this.mAdapter.notifyItemChanged(this.mPosition);
        }
    }

    /* access modifiers changed from: protected */
    public void setAdapter(MessageAdapter adapter) {
        this.mAdapter = adapter;
    }

    /* access modifiers changed from: protected */
    public void setType(int type) {
        this.mType = type;
    }

    /* access modifiers changed from: protected */
    public void setCustom(boolean custom) {
        this.mCustom = custom;
    }

    /* access modifiers changed from: protected */
    public boolean getCustom() {
        return this.mCustom;
    }
}
