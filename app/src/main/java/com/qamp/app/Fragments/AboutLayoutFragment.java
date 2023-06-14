/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 3:25 AM
 *
 */

package com.qamp.app.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.qamp.app.Listener.Backpressedlistener;
import com.qamp.app.Activity.ChannelAdminSettingsActivity;
import com.qamp.app.R;

public class AboutLayoutFragment extends Fragment implements Backpressedlistener {
    public static Backpressedlistener backpressedlistener;
    CardView channelLayoutEdit;
    public AboutLayoutFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.aboutlayoutfragment, container, false);
        channelLayoutEdit = view.findViewById(R.id.channelLayoutEdit);
        channelLayoutEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChannelAdminSettingsActivity.class);
                startActivity(intent);
            }
        });
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