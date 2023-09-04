package com.qamp.app.Modal;

import com.mesibo.api.MesiboProfile;

public class MessageTimeDate {
    private MesiboProfile userData;
    private String lastTime;

    public MesiboProfile getUserData() {
        return userData;
    }

    public void setUserData(MesiboProfile userData) {
        this.userData = userData;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

}
