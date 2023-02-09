// 
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.uihelper;

import android.content.Intent;
import android.app.Activity;
import android.view.View;
import com.qamp.app.uihelper.Utils.ActivityListener;
import androidx.fragment.app.DialogFragment;

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
