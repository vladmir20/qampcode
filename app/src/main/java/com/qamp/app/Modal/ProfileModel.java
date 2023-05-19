/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.Modal;

import org.json.JSONArray;


public class ProfileModel {
    private int id;
    private String name;
    private String imageName;
    private JSONArray imageData;
    private String uid;


    public ProfileModel(String name, int id, String imageName, JSONArray imageData, String uid) {
        this.name = name;
        this.id = id;
        this.imageName = imageName;
        this.imageData = imageData;
        this.uid = uid;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setImageData(JSONArray imageData) {
        this.imageData = imageData;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getImageName() {
        return this.imageName;
    }

    public JSONArray getImageDate() {
        return this.imageData;
    }

    public String getUid() {
        return this.uid;
    }
}

