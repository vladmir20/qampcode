/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 2:08 AM
 *
 */

package com.qamp.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class AboutLayoutFragment extends Fragment implements Backpressedlistener {
    public static Backpressedlistener backpressedlistener;
    public AboutLayoutFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.aboutlayoutfragment, container, false);

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