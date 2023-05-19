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

package com.qamp.app.Utils;

import android.content.Context;
import android.view.View;

import com.qamp.app.Activity.WelcomeScreen;

public interface IProductTourListener
{
    void onProductTourViewLoaded(final View p0, final int p1, final WelcomeScreen p2);
    
    void onProductTourCompleted(final Context p0);
}
