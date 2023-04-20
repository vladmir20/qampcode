/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:31 PM
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
