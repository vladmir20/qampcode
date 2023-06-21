package com.qamp.app.Modal;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.MessagingModule.UserData;

public class ContactModel {
    private MesiboProfile userData;
    private int viewValue;
    private int viewValueNot;

    public int getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(int isGroup) {
        this.isGroup = isGroup;
    }

    private int isGroup;

    public MesiboProfile getUserData() {
        return userData;
    }

    public void setUserData(MesiboProfile userData) {
        this.userData = userData;
    }

    public int getViewValue() {
        return viewValue;
    }

    public void setViewValue(int viewValue) {
        this.viewValue = viewValue;
    }

    public int getViewValueNot() {
        return viewValueNot;
    }

    public void setViewValueNot(int viewValueNot) {
        this.viewValueNot = viewValueNot;
    }
}
