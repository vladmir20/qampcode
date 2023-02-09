// 
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.uihelper.Utils;

import android.content.Intent;
import android.app.Activity;
import android.view.View;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment implements ActivityListener, View.OnClickListener
{
    protected FragmentListener mFragmentListener;
    protected Activity mActivity;
    protected String TABNAME;
    
    public BaseFragment() {
        this.TABNAME = "";
    }
    
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
        try {
            this.mFragmentListener = (FragmentListener)activity;
        }
        catch (ClassCastException e) {
            this.mFragmentListener = null;
        }
    }
    
    public boolean onBackPressed() {
        return false;
    }
    
    public void onClick(final View view) {
        this.onClick(view.getId());
    }
    
    public void onClick(final int id) {
    }
    
    public String getName() {
        return this.TABNAME;
    }
    
    public void onActivityResultPrivate(final int requestCode, final int resultCode, final Intent data) {
    }
}
