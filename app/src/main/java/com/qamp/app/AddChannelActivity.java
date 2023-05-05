/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 04/05/23, 8:37 PM
 *
 */

package com.qamp.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.qamp.app.Utils.AppUtils;
import com.qamp.app.messaging.MesiboUserListActivityNew;

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
        transaction.replace(R.id.frameLayout, createCommunity,"CreateCommunityFragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().findFragmentByTag("CreateCommunityFragment") != null &&
                getSupportFragmentManager().findFragmentByTag("CreateCommunityFragment").isVisible()) {
            CreateCommunityFragment.backpressedlistener.onBackPressed();
         }else if (getSupportFragmentManager().findFragmentByTag("CommunityLocationFragment") != null &&
                getSupportFragmentManager().findFragmentByTag("CommunityLocationFragment").isVisible()) {
            CommunityLocationFragment.backpressedlistener.onBackPressed();
         }else if (getSupportFragmentManager().findFragmentByTag("CreateCommunityThree") != null &&
                getSupportFragmentManager().findFragmentByTag("CreateCommunityThree").isVisible()) {
            CreateCommunityThree.backpressedlistener.onBackPressed();
         }else if (getSupportFragmentManager().findFragmentByTag("CreateCommunityFour") != null &&
                getSupportFragmentManager().findFragmentByTag("CreateCommunityFour").isVisible()) {
            CreateCommunityFour.backpressedlistener.onBackPressed();
         }else{
            Intent mainActivity = new Intent(AddChannelActivity.this, MesiboUserListActivityNew.class);
            startActivity(mainActivity);
        }
    }
}
