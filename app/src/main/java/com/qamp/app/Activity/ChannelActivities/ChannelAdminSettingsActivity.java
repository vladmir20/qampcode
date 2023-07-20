/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 3:25 AM
 *
 */

package com.qamp.app.Activity.ChannelActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.Utils.Utilss;

public class ChannelAdminSettingsActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    ImageView businessDetailsSettingsBtn, businessLocationSettingsBtn, channelTypeSettingBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Utilss.setLanguage(ChannelAdminSettingsActivity.this);
        setContentView(R.layout.channel_admin_settings_activity);
        AppUtils.setStatusBarColor(ChannelAdminSettingsActivity.this, R.color.colorAccent);
        initViews();
        businessDetailsSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(ChannelAdminSettingsActivity.this, BusinessDetailsSettingsActivity.class);
                startActivity(mainActivity);
            }
        });
        businessLocationSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(ChannelAdminSettingsActivity.this, BusinessLocationSettingsActivity.class);
                startActivity(mainActivity);
            }
        });
        channelTypeSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBusinessDialog();
            }
        });
    }


    private void initViews() {
        frameLayout = findViewById(R.id.frameLayout);
        businessDetailsSettingsBtn = findViewById(R.id.businessDetailsSettingsBtn);
        businessLocationSettingsBtn = findViewById(R.id.businessLocationSettingsBtn);
        channelTypeSettingBtn = findViewById(R.id.channelTypeSettingBtn);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainActivity = new Intent(ChannelAdminSettingsActivity.this, CommunityDashboard.class);
        startActivity(mainActivity);
    }

    private void editBusinessDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(ChannelAdminSettingsActivity.this);
        dialog.setContentView(R.layout.editbusinesstype);

        ConstraintLayout socialProfileLayout = (ConstraintLayout) dialog.findViewById(R.id.socialProfileLayout);
        ConstraintLayout communityProfileLayout = (ConstraintLayout) dialog.findViewById(R.id.communityProfileLayout);
        ConstraintLayout localBusinessLayout = (ConstraintLayout) dialog.findViewById(R.id.localBusinessLayout);
        socialProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChannelAdminSettingsActivity.this, "SocialProfileLayout", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();

    }
}
