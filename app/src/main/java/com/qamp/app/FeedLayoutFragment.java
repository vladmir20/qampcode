/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 2:00 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 2:00 AM
 *
 */

package com.qamp.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class FeedLayoutFragment extends Fragment implements Backpressedlistener {
    public static Backpressedlistener backpressedlistener;
    public FeedLayoutFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.feedlayoutfragment, container, false);

        return view;
    }

    @Override
    public void onBackPressed() {

    }
}