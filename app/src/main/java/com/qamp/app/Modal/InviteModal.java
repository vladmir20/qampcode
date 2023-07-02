package com.qamp.app.Modal;

public class InviteModal {
    private String uid;
    private String channelId;
    private String countryCode;
    private String invitedMobileNumber;
    private String state;
    private boolean readed;
    private String channelImageUrl;
    private String inviterUserName;
    private String channelTitle;
    private long creationDate;
    private long updatedDate;
    private String invitedRegion;

    public InviteModal() {

    }

    public InviteModal(String uid, String channelId, String countryCode, String invitedMobileNumber, String state, boolean readed, String channelImageUrl, String inviterUserName, String channelTitle, long creationDate, long updatedDate, String invitedRegion) {
        this.uid = uid;
        this.channelId = channelId;
        this.countryCode = countryCode;
        this.invitedMobileNumber = invitedMobileNumber;
        this.state = state;
        this.readed = readed;
        this.channelImageUrl = channelImageUrl;
        this.inviterUserName = inviterUserName;
        this.channelTitle = channelTitle;
        this.creationDate = creationDate;
        this.updatedDate = updatedDate;
        this.invitedRegion = invitedRegion;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getInvitedMobileNumber() {
        return invitedMobileNumber;
    }

    public void setInvitedMobileNumber(String invitedMobileNumber) {
        this.invitedMobileNumber = invitedMobileNumber;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }

    public String getChannelImageUrl() {
        return channelImageUrl;
    }

    public void setChannelImageUrl(String channelImageUrl) {
        this.channelImageUrl = channelImageUrl;
    }

    public String getInviterUserName() {
        return inviterUserName;
    }

    public void setInviterUserName(String inviterUserName) {
        this.inviterUserName = inviterUserName;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(long updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getInvitedRegion() {
        return invitedRegion;
    }

    public void setInvitedRegion(String invitedRegion) {
        this.invitedRegion = invitedRegion;
    }
}
