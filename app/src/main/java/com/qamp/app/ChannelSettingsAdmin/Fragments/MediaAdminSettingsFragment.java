/*
 * *
 *  * Created by Shivam Tiwari on 15/05/23, 4:04 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 15/05/23, 4:04 AM
 *
 */

package com.qamp.app.ChannelSettingsAdmin.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.qamp.app.Backpressedlistener;
import com.qamp.app.R;

public class MediaAdminSettingsFragment extends Fragment implements Backpressedlistener {
    public static Backpressedlistener backpressedlistener;
    public MediaAdminSettingsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_setting_media, container, false);
        return view;
    }

    @Override
    public void onBackPressed() {

    }
    @Override
    public void onPause() {
        backpressedlistener=null;
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        backpressedlistener=this;
    }
}
