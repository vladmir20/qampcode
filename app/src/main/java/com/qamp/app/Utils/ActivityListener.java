/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 3:25 AM
 *
 */

//
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.Utils;

import android.content.Intent;

public interface ActivityListener
{
    boolean onBackPressed();
    
    void onActivityResultPrivate(final int p0, final int p1, final Intent p2);
}
