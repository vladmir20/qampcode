package com.qamp.app.messaging;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboMessage;

public class MessageData {
    private Bitmap image = null;
    private boolean imageDecoded = false;
    private Context mContext = null;
    private boolean mDeleted = false;
    private boolean mDirty = false;
    private boolean mFavourite = false;
    private Mesibo.MessageListener mMessageListener = null;
    private int mNameColor = -8947849;
    private boolean mSelected = false;
    private boolean mShowName = true;
    private MesiboRecycleViewHolder mViewHolder = null;
    private boolean mVisible = true;
    private MesiboMessage msg = null;
    private MesiboMessage repliedTo = null;

    MessageData(Context context, MesiboMessage msg2) {
        this.msg = msg2;
        this.mContext = context;
        if (msg2.isDate()) {
            MesiboUI.Config opts = MesiboUI.getConfig();
            msg2.message = msg2.getDate(true, opts.today, opts.yesterday);
        }
    }

    /* access modifiers changed from: package-private */
    public void setMessageListener(Mesibo.MessageListener listener) {
        this.mMessageListener = listener;
    }

    /* access modifiers changed from: package-private */
    public void setViewHolder(MesiboRecycleViewHolder vh) {
        MesiboRecycleViewHolder pv = this.mViewHolder;
        this.mViewHolder = null;
        if (pv != null) {
            pv.reset();
        }
        this.mViewHolder = vh;
    }

    public MesiboMessage getMesiboMessage() {
        return this.msg;
    }

    public int getPosition() {
        if (this.mViewHolder != null) {
            return this.mViewHolder.getItemPosition();
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public MesiboRecycleViewHolder getViewHolder() {
        return this.mViewHolder;
    }

    public boolean hasThumbnail() {
        if (this.msg == null || isDeleted()) {
            return false;
        }
        return this.msg.hasThumbnail();
    }

    public boolean isImageVideo() {
        if (this.msg == null) {
            return false;
        }
        if (this.msg.hasImage() || this.msg.hasVideo()) {
            return true;
        }
        return false;
    }

    public boolean isForwarded() {
        return this.msg.isForwarded();
    }

    public boolean isSelected() {
        return this.mSelected;
    }

    public void setSelected(boolean selected) {
        this.mSelected = selected;
        setDirty(true);
    }

    public void toggleSelected() {
        this.mSelected = !this.mSelected;
        setDirty(true);
    }

    public boolean isDirty() {
        return this.mDirty;
    }

    public void setDirty(boolean dirty) {
        this.mDirty = dirty;
    }

    public long getGroupId() {
        return this.msg.groupid;
    }

    public String getPeer() {
        return this.msg.peer;
    }

    public String getUsername() {
        return this.msg.profile.getNameOrAddress("+");
    }

    public long getMid() {
        if (this.msg == null) {
            return -1;
        }
        return this.msg.mid;
    }

    public int getMessageType() {
        if (this.msg == null) {
            return -1;
        }
        return this.msg.type;
    }

    public String getTitle() {
        if (this.msg == null) {
            return null;
        }
        if (!TextUtils.isEmpty(this.msg.title)) {
            return this.msg.title;
        }
        if (this.msg.isDocument() || this.msg.isAudio()) {
            return this.msg.getFileName();
        }
        return null;
    }

    public String getSubTitle() {
        if (this.msg == null) {
            return null;
        }
        if (!TextUtils.isEmpty(this.msg.subtitle)) {
            return this.msg.subtitle;
        }
        if (this.msg.isDocument() || this.msg.isAudio()) {
            return Utils.getFileSizeString(this.msg.getFileSize());
        }
        return null;
    }

    public String getMessage() {
        if (this.msg == null) {
            return null;
        }
        if (isDeleted()) {
            return MesiboUI.getConfig().deletedMessageTitle;
        }
        return this.msg.message;
    }

    public String getDisplayMessage() {
        String message = getMessage();
        if (TextUtils.isEmpty(message)) {
            message = this.msg.title;
        }
        if (TextUtils.isEmpty(message)) {
            message = this.msg.subtitle;
        }
        if (TextUtils.isEmpty(message)) {
            return "";
        }
        return message;
    }

    public String getTimestamp() {
        return this.msg.getTime(false);
    }

    public int getStatus() {
        if (this.msg == null) {
            return -1;
        }
        return this.msg.getStatus();
    }

    public boolean isDeleted() {
        return this.mDeleted || this.msg.isDeleted();
    }

    public void setDeleted(boolean deleted) {
        this.mDeleted = deleted;
    }

    public boolean isVisible() {
        return this.mVisible;
    }

    public void setVisible(boolean visible) {
        this.mVisible = visible;
    }

    public boolean isEncrypted() {
        return this.msg.isEndToEndEncrypted();
    }

    public Bitmap getImage() {
        if (this.msg == null || isDeleted()) {
            return null;
        }
        if (this.msg.hasThumbnail()) {
            return this.msg.getThumbnail();
        }
        if (this.image != null) {
            return this.image;
        }
        if (this.msg.openExternally) {
            return null;
        }
        if ((this.msg.isFilePending() || this.msg.isFileReady()) && !this.imageDecoded) {
            this.imageDecoded = true;
            int drawableid = MesiboImages.getFileDrawable(this.msg.getFilePath());
            if (drawableid > 0) {
                this.image = BitmapFactory.decodeResource(this.mContext.getApplicationContext().getResources(), drawableid);
            }
        }
        return this.image;
    }

    public String getDateStamp() {
        MesiboUI.Config opts = MesiboUI.getConfig();
        return this.msg.getDate(true, opts.today, opts.yesterday);
    }

    public void setStaus(int status) {
        if (this.msg != null) {
            this.msg.setStatus(status);
        }
        setDirty(true);
    }

    public void setFavourite(Boolean favourite) {
        this.mFavourite = favourite.booleanValue();
        setDirty(true);
    }

    public Boolean getFavourite() {
        return Boolean.valueOf(this.mFavourite);
    }

    public boolean isReply() {
        if (!this.msg.isReply()) {
            return false;
        }
        this.repliedTo = this.msg.getRepliedToMessage();
        if (this.repliedTo != null) {
            return true;
        }
        return false;
    }

    public String getReplyString() {
        if (!isReply()) {
            return "";
        }
        return this.repliedTo.message;
    }

    public Bitmap getReplyBitmap() {
        if (!isReply()) {
            return null;
        }
        return this.repliedTo.getThumbnail();
    }

    public String getReplyName() {
        if (!isReply()) {
            return null;
        }
        if (this.repliedTo.isIncoming()) {
            return this.repliedTo.profile.getNameOrAddress("+");
        }
        return MesiboConfiguration.YOU_STRING_IN_REPLYVIEW;
    }

    public void setNameColor(int color) {
        this.mNameColor = color;
        setDirty(true);
    }

    public int getNameColor() {
        return this.mNameColor;
    }

    public void checkPreviousData(MessageData pd) {
        if (this.msg.isOutgoing() || !this.msg.isGroupMessage()) {
            this.mShowName = false;
        } else if (!pd.getMesiboMessage().isIncoming() || pd.hasThumbnail()) {
            this.mShowName = true;
        } else {
            String prevPeer = pd.getPeer();
            if (prevPeer == null || this.msg.peer == null || !prevPeer.equalsIgnoreCase(this.msg.peer)) {
                this.mShowName = true;
            } else {
                this.mShowName = false;
            }
        }
    }

    public boolean isShowName() {
        return this.mShowName;
    }
}
