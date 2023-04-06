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
