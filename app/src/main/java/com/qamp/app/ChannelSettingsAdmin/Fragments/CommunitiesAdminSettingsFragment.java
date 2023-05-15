/*
 * *
 *  * Created by Shivam Tiwari on 15/05/23, 4:10 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 15/05/23, 4:10 AM
 *
 */

package com.qamp.app.ChannelSettingsAdmin.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.qamp.app.Backpressedlistener;
import com.qamp.app.ChannelSettingsAdmin.ChannelAdminSettingsActivity;
import com.qamp.app.R;

public class CommunitiesAdminSettingsFragment extends Fragment implements Backpressedlistener {
    public static Backpressedlistener backpressedlistener;
    public CommunitiesAdminSettingsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_setting_communities, container, false);
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