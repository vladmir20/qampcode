/*
 * *
 *  * Created by Shivam Tiwari on 28/04/23, 4:49 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 28/04/23, 4:49 PM
 *
 */

package com.qamp.app;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.qamp.app.Utils.AppUtils;

public class AddChannelActivity extends AppCompatActivity {
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilss.setLanguage(AddChannelActivity.this);
        setContentView(R.layout.activity_add_channel);
        AppUtils.setStatusBarColor(AddChannelActivity.this, R.color.colorAccent);
        frameLayout = findViewById(R.id.frameLayout);
        CreateCommunityFragment createCommunity = new CreateCommunityFragment();
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, createCommunity);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
