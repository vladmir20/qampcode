package com.qamp.app.Activity.ChannelActivities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.Utils.Utilss;

public class BusinessDetailsSettingsActivity extends AppCompatActivity {

    FrameLayout frameLayout;
     @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Utilss.setLanguage(BusinessDetailsSettingsActivity.this);
        setContentView(R.layout.business_details_edit_layout);
        AppUtils.setStatusBarColor(BusinessDetailsSettingsActivity.this, R.color.colorAccent);
        initViews();
    }

    private void initViews() {
        frameLayout = findViewById(R.id.frameLayout);
     }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainActivity = new Intent(BusinessDetailsSettingsActivity.this, ChannelAdminSettingsActivity.class);
        startActivity(mainActivity);
    }
}
