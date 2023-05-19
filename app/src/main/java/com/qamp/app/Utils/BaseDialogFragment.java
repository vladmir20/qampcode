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

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.fragment.app.DialogFragment;

import com.qamp.app.Utils.ActivityListener;

public class BaseDialogFragment extends DialogFragment implements ActivityListener, View.OnClickListener
{
    protected Activity mActivity;
    
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }
    
    public boolean onBackPressed() {
        return false;
    }
    
    public void onClick(final View view) {
        this.onClick(view.getId());
    }
    
    public void onClick(final int id) {
    }
    
    public void onActivityResultPrivate(final int requestCode, final int resultCode, final Intent data) {
    }
}
