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
