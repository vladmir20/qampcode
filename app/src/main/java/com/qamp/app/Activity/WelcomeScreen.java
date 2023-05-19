/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 3:25 AM
 *
 */

//
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.Activity;

public class WelcomeScreen
{
    private String mTitle;
    private String mDescription;
    private int mLayoutId;
    private int mResourceId;
    private int mColor;
    
    public WelcomeScreen(final String title, final String description, final int layoutId, final int imageResourceId, final int backgroundColor) {
        this.mTitle = null;
        this.mDescription = null;
        this.mLayoutId = 0;
        this.mResourceId = 0;
        this.mColor = 0;
        this.mTitle = title;
        this.mDescription = description;
        this.mLayoutId = layoutId;
        this.mResourceId = imageResourceId;
        this.mColor = backgroundColor;
    }
    
    public String getDescription() {
        return this.mDescription;
    }
    
    public String getTitle() {
        return this.mTitle;
    }
    
    public void setDescription(final String description) {
        this.mDescription = description;
    }
    
    public void setTitle(final String title) {
        this.mTitle = title;
    }
    
    public int getResourceId() {
        return this.mResourceId;
    }
    
    public void setResourceId(final int resourceId) {
        this.mResourceId = resourceId;
    }
    
    public int getLayoutId() {
        return this.mLayoutId;
    }
    
    public int getBackgroundColor() {
        return this.mColor;
    }
}
