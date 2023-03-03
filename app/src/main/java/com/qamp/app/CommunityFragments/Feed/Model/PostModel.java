package com.qamp.app.CommunityFragments.Feed.Model;

public class PostModel {

    private String titlePerson;
    private String locationPost;
    private String caption;

    public PostModel(String titlePerson, String locationPost, String caption) {
        this.titlePerson = titlePerson;
        this.locationPost = locationPost;
        this.caption = caption;
    }

    public String getLocationPost() {
        return locationPost;
    }

    public String getTitlePerson() {
        return titlePerson;
    }

    public void setTitlePerson(String titlePerson) {
        this.titlePerson = titlePerson;
    }

    public void setLocationPost(String locationPost) {
        this.locationPost = locationPost;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
