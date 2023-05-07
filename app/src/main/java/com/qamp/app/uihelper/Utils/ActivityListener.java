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

package com.qamp.app.uihelper.Utils;

import android.content.Intent;

public interface ActivityListener
{
    boolean onBackPressed();
    
    void onActivityResultPrivate(final int p0, final int p1, final Intent p2);
}
