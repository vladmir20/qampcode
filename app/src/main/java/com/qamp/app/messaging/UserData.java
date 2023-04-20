/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:31 PM
 *
 */

package com.qamp.app.messaging;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.mesibo.api.MesiboMessage;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.messaging.AllUtils.LetterTileProvider;

public class UserData {
    private Drawable icon = null;
    private boolean mDeleted = false;
    private boolean mFixedImage = false;
    private String mLastMessage;
    private MesiboProfile mTypingProfile = null;
    private MesiboProfile mUser;
    private Bitmap mUserImage = null;
    private Bitmap mUserImageThumbnail = null;
    private int mUserListPosition = -1;
    private MesiboMessage msg = null;

    public UserData(MesiboProfile user) {
        this.mUser = user;
        this.mUserImage = null;
        this.mUserImageThumbnail = null;
        this.mLastMessage = "";
    }

    public static UserData getUserData(MesiboMessage params) {
        if (params == null || params.profile == null) {
            return null;
        }
        return getUserData(params.profile);
    }

    public static UserData getUserData(MesiboProfile profile) {
        if (profile == null) {
            return null;
        }
        UserData d = (UserData) profile.other;
        if (d != null) {
            return d;
        }
        UserData d2 = new UserData(profile);
        profile.other = d2;
        return d2;
    }

    private String appendNameToMessage(MesiboMessage params, String message) {
        String name = params.peer;
        if (!(params.profile == null || params.profile.getFirstName() == null)) {
            name = params.profile.getFirstName();
        }
        if (TextUtils.isEmpty(name)) {
            return message;
        }
        if (name.length() > 12) {
            name = name.substring(0, 12);
        }
        return name + ": " + message;
    }

    public String getLastMessage() {
        if (TextUtils.isEmpty(this.mLastMessage)) {
            return "";
        }
        if (this.mLastMessage.length() >= 36) {
            return this.mLastMessage.substring(0, 33) + "...";
        }
        return this.mLastMessage;
    }

    public MesiboMessage getMessage() {
        return this.msg;
    }

    public void setMessage(MesiboMessage message) {
        this.msg = message;
        String str = this.msg.message;
        if (this.msg.isDeleted()) {
            setMessage(MesiboUI.getConfig().deletedMessageTitle);
            return;
        }
        if (TextUtils.isEmpty(str)) {
            str = this.msg.title;
        }
        if (TextUtils.isEmpty(str)) {
            if (this.msg.hasImage()) {
                str = MesiboConfiguration.IMAGE_STRING;
            } else if (this.msg.hasVideo()) {
                str = MesiboConfiguration.VIDEO_STRING;
            } else if (this.msg.hasAudio()) {
                str = MesiboConfiguration.AUDIO_STRING;
            } else if (this.msg.hasFile()) {
                str = MesiboConfiguration.ATTACHMENT_STRING;
            } else if (this.msg.hasLocation()) {
                str = MesiboConfiguration.LOCATION_STRING;
            }
        }
        if (this.msg.isGroupMessage() && this.msg.isIncoming()) {
            str = appendNameToMessage(this.msg, str);
        }
        if (this.msg.isMissedCall()) {
            str = MesiboUI.getConfig().missedVideoCallTitle;
            if (this.msg.isVoiceCall()) {
                str = MesiboUI.getConfig().missedVoiceCallTitle;
            }
        }
        setMessage(str);
    }

    public void setMessage(String message) {
        this.mLastMessage = message;
    }

    public String getPeer() {
        return this.mUser.address;
    }

    public long getGroupId() {
        return this.mUser.groupid;
    }

    public long getmid() {
        if (this.msg == null) {
            return 0;
        }
        return this.msg.mid;
    }

    public boolean isDeletedMessage() {
        if (this.msg == null) {
            return this.mDeleted;
        }
        return this.mDeleted || this.msg.isDeleted();
    }

    public void setDeletedMessage(boolean deleted) {
        this.mDeleted = deleted;
    }

    public void setTypingUser(MesiboProfile user) {
        this.mTypingProfile = user;
    }

    public void setUser(MesiboProfile user) {
        this.mUser = user;
    }

    public void setFixedImage(boolean fixed) {
        this.mFixedImage = fixed;
    }

    public Integer getUnreadCount() {
        return Integer.valueOf(this.mUser.getUnreadMessageCount());
    }

    public void setImageThumbnail(Bitmap b) {
        this.mUserImageThumbnail = b;
    }

    public Bitmap getImage() {
        return this.mUserImage;
    }

    public void setImage(Bitmap b) {
        this.mUserImage = b;
    }

    public Bitmap getThumbnail(LetterTileProvider tileProvider) {
        Bitmap tn = this.mUser.getThumbnail();
        if (tn != null) {
            return tn;
        }
        if (this.mUserImageThumbnail != null) {
            return this.mUserImageThumbnail;
        }
        if (MesiboUI.getConfig().useLetterTitleImage && tileProvider != null) {
            this.mUserImageThumbnail = tileProvider.getLetterTile(getUserName(), true);
        } else if (this.mUser.groupid > 0) {
            this.mUserImageThumbnail = MesiboImages.getDefaultGroupBitmap();
        } else {
            this.mUserImageThumbnail = MesiboImages.getDefaultUserBitmap();
        }
        return this.mUserImageThumbnail;
    }

    public Bitmap getThumbnail() {
        return getThumbnail((LetterTileProvider) null);
    }

    public String getImagePath() {
        return this.mUser.getImageOrThumbnailPath();
    }

    public Integer getStatus() {
        if (this.msg != null) {
            return Integer.valueOf(this.msg.getStatus());
        }
        return 19;
    }

    public String getTime() {
        if (this.msg != null) {
            return this.msg.getTime(false);
        }
        return "";
    }

    public String getUserName() {
        String name = mUser.getName();
        if (TextUtils.isEmpty(name))
            name = mUser.address;

        if (name.length() > 15)
         name = name.substring(0, 10) + "...";
        return name;
    }

    public boolean isTyping() {
        if (this.mTypingProfile != null) {
            return this.mTypingProfile.isTyping(this.mUser.getGroupId());
        }
        return this.mUser.isTyping(this.mUser.getGroupId());
    }

    public MesiboProfile getTypingProfile() {
        return this.mTypingProfile;
    }

    public int getUserListPosition() {
        return this.mUserListPosition;
    }

    public void setUserListPosition(int position) {
        this.mUserListPosition = position;
    }
}
