/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.MessagingModule;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.qamp.app.R;

public class MessageViewHolder extends MesiboRecycleViewHolder implements View.OnClickListener, View.OnLongClickListener {
    protected FrameLayout mBubble;
    protected MessageData mData;
    protected ImageView mFavourite;
    protected MessageView mMview;
    protected View mSelectedOverlay;
    protected ImageView mStatus;
    protected TextView mTime;
    protected TextView otherUserName;
    private ClickListener listener;
    private LinearLayout m_titleLayout;

    public MessageViewHolder(int type, View v, ClickListener listener2) {
        super(v);
        this.mData = null;
        this.mData = null;
        this.mTime = (TextView) v.findViewById(R.id.m_time);
        this.mStatus = (ImageView) v.findViewById(R.id.m_status);
        this.mFavourite = (ImageView) v.findViewById(R.id.m_star);
        this.m_titleLayout = (LinearLayout) v.findViewById(R.id.m_titlelayout);
        this.mMview = (MessageView) v.findViewById(R.id.mesibo_message_view);
        int bubbleid = R.id.outgoing_layout_bubble;
        if (1 == type) {
            this.otherUserName = (TextView) v.findViewById(R.id.m_user_name);
            bubbleid = R.id.incoming_layout_bubble;
        }
        this.mBubble = (FrameLayout) v.findViewById(bubbleid);
        this.mSelectedOverlay = v.findViewById(R.id.selected_overlay);
        this.listener = listener2;
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
    }

    public void setData(MessageData m, int position, boolean selected) {
        int i = 8;
        int i2 = 0;
        reset();
        setItemPosition(position);
        this.mData = m;
        this.mData.setViewHolder(this);
        if (1 == getType()) {
            if (m.getGroupId() == 0 || !m.isShowName()) {
                this.otherUserName.setVisibility(8);
            } else {
                this.otherUserName.setVisibility(0);
                this.otherUserName.setTextColor(m.getNameColor());
                this.otherUserName.setText(m.getUsername());
            }
            setupBackgroundBubble(this.mBubble, -1, MesiboUI.getConfig().messageBackgroundColorForPeer);
            this.m_titleLayout.setBackgroundColor(MesiboUI.getConfig().titleBackgroundColorForPeer);
            if (!MesiboUI.getConfig().e2eeIndicator || !this.mData.isEncrypted()) {
                this.mStatus.setVisibility(View.GONE);
            } else {
                this.mStatus.setVisibility(View.VISIBLE);
            }
        } else {
            setupBackgroundBubble(this.mBubble, -1, MesiboUI.getConfig().messageBackgroundColorForMe);
            this.m_titleLayout.setBackgroundColor(MesiboUI.getConfig().titleBackgroundColorForMe);
            setupMessageStatus(this.mStatus, m.getStatus());
        }
        this.mTime.setText(m.getTimestamp());
        if (!TextUtils.isEmpty(m.getTitle()) || !TextUtils.isEmpty(m.getSubTitle())) {
            this.m_titleLayout.setVisibility(0);
        } else {
            this.m_titleLayout.setVisibility(8);
        }
        ImageView imageView = this.mFavourite;
        if (m.getFavourite().booleanValue()) {
            i = 0;
        }
        imageView.setVisibility(i);
        if ((this.mData.getTitle() == null || this.mData.getTitle().isEmpty()) && (this.mData.getMessage() == null || this.mData.getMessage().isEmpty())) {
            this.mTime.setTextColor(Color.parseColor(MesiboConfiguration.STATUS_COLOR_OVER_PICTURE));
            this.mFavourite.setColorFilter(Color.parseColor(MesiboConfiguration.STATUS_COLOR_OVER_PICTURE));
        } else {
            this.mFavourite.setColorFilter(Color.parseColor(MesiboConfiguration.STATUS_COLOR_WITHOUT_PICTURE));
            this.mTime.setTextColor(Color.parseColor(MesiboConfiguration.STATUS_COLOR_WITHOUT_PICTURE));
        }
        this.mMview.setData(this.mData);
        View view = this.mSelectedOverlay;
        if (!selected) {
            i2 = 4;
        }
        view.setVisibility(i2);
    }

    public void removeData() {
        if (this.mData != null) {
            setItemPosition(-1);
            this.mData = null;
        }
    }

    /* access modifiers changed from: protected */
    public void updateThumbnail(MessageData data) {
        if (this.mMview.mPictureThumbnail != null) {
            this.mMview.mPictureThumbnail.setData(data);
        }
    }

    public void reset() {
        if (this.mData != null) {
            MessageData d = this.mData;
            this.mData = null;
            setItemPosition(-1);
            d.setViewHolder((MesiboRecycleViewHolder) null);
        }
    }

    public void setImage(Bitmap image) {
        this.mMview.setImage(image);
    }

    public void setupBackgroundBubble(FrameLayout fm, int resource, int color) {
        fm.setPadding(fm.getPaddingLeft(), fm.getPaddingTop(), fm.getPaddingRight(), fm.getPaddingBottom());
        ((CardView) fm).setCardBackgroundColor(color);
    }

    public void setupMessageStatus(ImageView im, int status) {
        im.setVisibility(this.mData.isDeleted() ? 8 : 0);
        if (!this.mData.isDeleted()) {
            im.setImageBitmap(MesiboImages.getStatusImage(status));
        }
    }

    public void onClick(View v) {
        if (this.listener != null) {
            this.listener.onItemClicked(getAdapterPosition());
        }
    }

    public boolean onLongClick(View v) {
        if (this.listener != null) {
            return this.listener.onItemLongClicked(getAdapterPosition());
        }
        return false;
    }

    public interface ClickListener {
        void onItemClicked(int i);

        boolean onItemLongClicked(int i);
    }
}
