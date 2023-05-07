/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 04/05/23, 5:18 PM
 *
 */

package com.qamp.app;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ContactCommunityData {
    private String name;
    private String number;
    private Bitmap profilePic;

    public ContactCommunityData(String name, String number, Bitmap profilePic){
        this.name = name;
        this.number = number;
        this.profilePic = profilePic;
    }
    public ContactCommunityData(String name, String number){
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }
}
