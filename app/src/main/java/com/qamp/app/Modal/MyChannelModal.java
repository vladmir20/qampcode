package com.qamp.app.Modal;

public class MyChannelModal {
    private String uid;
    private String type;
    private String title;
    private String description;
    private String creationDate;
    private String isActive;
    private String isDeleted;
    private String haveGeoLocation;
    private String latitude;
    private String longitude;
    private String updateDate;
    private String emailId;
    private String domain;
    private String mobileNumber;
    private String logoImageUrl;

    public MyChannelModal(String uid, String type, String title, String description, String creationDate, String isActive, String isDeleted, String haveGeoLocation, String latitude, String longitude, String updateDate, String emailId, String domain, String mobileNumber, String logoImageUrl) {
        this.uid = uid;
        this.type = type;
        this.title = title;
        this.description = description;
        this.creationDate = creationDate;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
        this.haveGeoLocation = haveGeoLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.updateDate = updateDate;
        this.emailId = emailId;
        this.domain = domain;
        this.mobileNumber = mobileNumber;
        this.logoImageUrl = logoImageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getHaveGeoLocation() {
        return haveGeoLocation;
    }

    public void setHaveGeoLocation(String haveGeoLocation) {
        this.haveGeoLocation = haveGeoLocation;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getLogoImageUrl() {
        return logoImageUrl;
    }

    public void setLogoImageUrl(String logoImageUrl) {
        this.logoImageUrl = logoImageUrl;
    }
}
