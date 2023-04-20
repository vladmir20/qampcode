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

import androidx.fragment.app.Fragment;

import java.util.List;

public interface FragmentListener
{
    void onFragmentLoaded(final Fragment p0, final Class p1, final boolean p2);
    
    List<Class> onGetSlidingTabs();
}
