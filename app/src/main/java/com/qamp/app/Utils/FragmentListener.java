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

import androidx.fragment.app.Fragment;

import java.util.List;

public interface FragmentListener
{
    void onFragmentLoaded(final Fragment p0, final Class p1, final boolean p2);
    
    List<Class> onGetSlidingTabs();
}
