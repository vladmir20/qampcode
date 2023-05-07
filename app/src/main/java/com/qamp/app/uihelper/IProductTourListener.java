/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:40 AM
 *
 */

//
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.uihelper;

import android.content.Context;
import android.view.View;

public interface IProductTourListener
{
    void onProductTourViewLoaded(final View p0, final int p1, final WelcomeScreen p2);
    
    void onProductTourCompleted(final Context p0);
}
