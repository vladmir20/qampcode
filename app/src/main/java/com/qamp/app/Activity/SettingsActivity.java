/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 3:25 AM
 *
 */
package com.qamp.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mesibo.api.Mesibo;
import com.qamp.app.Fragments.BasicSettingsFragment;
import com.qamp.app.Fragments.EditProfileFragment;
import com.qamp.app.R;
import com.qamp.app.MessagingModule.MesiboUI;


public class SettingsActivity extends AppCompatActivity {

    Fragment mRequestingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);

        MesiboUI.Config opt = MesiboUI.getConfig();

        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setBackgroundDrawable(new ColorDrawable(opt.mToolbarColor));

        ab.setTitle("Settings");

        BasicSettingsFragment accountFragment = new BasicSettingsFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.settings_fragment_place, accountFragment, "null");
        ft.addToBackStack("account");
        ft.commit();


    }

    @Override
    public void onResume() {
        super.onResume();

        Mesibo.setForegroundContext(this, 0x100, true);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Settings");
    }


    @Override
    public void onBackPressed() {

        //InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        // imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        if (getSupportFragmentManager().getBackStackEntryCount() > 1)

            getSupportFragmentManager().popBackStackImmediate();
        else
            finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//mEmojiEditText.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof EditProfileFragment /*&& fragment.isVisible() */)
                fragment.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void setRequestingFragment(Fragment mRequestingFragment) {
        this.mRequestingFragment = mRequestingFragment;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mRequestingFragment != null)
            mRequestingFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
