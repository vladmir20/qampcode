/*
 * *
 *  * Created by Shivam Tiwari on 08/05/23, 4:02 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 08/05/23, 4:02 PM
 *
 */

package com.qamp.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class SettingNotificationCenter extends Fragment implements Backpressedlistener {
    public static Backpressedlistener backpressedlistener;
    public SettingNotificationCenter() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.setting_notification_center_layout, container, false);

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