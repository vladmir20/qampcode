/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:26 AM
 *
 */

package com.qamp.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mesibo.api.Mesibo;
import com.qamp.app.Fragments.EditProfileFragment;
import com.qamp.app.R;


public class EditProfileActivity extends AppCompatActivity {

    Fragment mRequestingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_profile);

        Bundle args = getIntent().getExtras();
        if (null == args) {
            return;
        }

        long groupid = args.getLong("groupid", 0);
        boolean launchMesibo = args.getBoolean("launchMesibo", false);

        EditProfileFragment registerNewProfileFragment = new EditProfileFragment();
        registerNewProfileFragment.setGroupId(groupid);
        registerNewProfileFragment.setLaunchMesibo(launchMesibo);
        mRequestingFragment = registerNewProfileFragment;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.register_new_profile_fragment_place, registerNewProfileFragment, "null");
        ft.addToBackStack("registerNewProfileFragment");
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        Mesibo.setForegroundContext(this, 0x101, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Mesibo.setForegroundContext(this, 0x101, false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStackImmediate();
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
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof EditProfileFragment && fragment.isVisible())
                fragment.onActivityResult(requestCode, resultCode, data);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mRequestingFragment != null)
            mRequestingFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
