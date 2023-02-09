// 
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.uihelper.Utils;

import java.util.List;
import androidx.fragment.app.Fragment;

public interface FragmentListener
{
    void onFragmentLoaded(final Fragment p0, final Class p1, final boolean p2);
    
    List<Class> onGetSlidingTabs();
}
