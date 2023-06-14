/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.MessagingModule;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    public Activity mActivity = null;

    public void onAttach(Context context) {
        BaseFragment.super.onAttach(context);
        if (context instanceof Activity) {
            this.mActivity = (Activity) context;
        }
    }

    public final void onDetach() {
        BaseFragment.super.onDetach();
        this.mActivity = null;
    }

    public Activity myActivity() {
        if (this.mActivity != null) {
            return this.mActivity;
        }
        return getActivity();
    }

    public void waitForContext(boolean finishIfFailed) {
    }
}
