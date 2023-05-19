/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 3:25 AM
 *
 */

//
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.qamp.app.R;
import com.qamp.app.Utils.PhoneVerificationFragment;
import com.qamp.app.Utils.ActivityListener;
import com.qamp.app.Utils.WelcomeFragment;

public class LoginActivity extends AppCompatActivity implements WelcomeFragment.OnFragmentInteractionListener {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private final int RC_HINT = 111;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_welcome_login);
        final Bundle bundle = this.getIntent().getExtras();
        int type = 0;
        if (null != bundle) {
            type = bundle.getInt("type", 0);
        }
        if (savedInstanceState == null) {
            if (0 == type) {
                this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_place, (Fragment) new WelcomeFragment(), "welcome").commit();
            } else {
                this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_place, (Fragment) new PhoneVerificationFragment(), "verification").addToBackStack((String) null).commit();
            }
        }
    }

    public void onFragmentInteraction(final Uri uri) {
        this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_place, (Fragment) new PhoneVerificationFragment(), "verification").addToBackStack((String) null).commit();
    }

    public void onBackPressed() {
        if (this.getSupportFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            for (final Fragment fragment : this.getSupportFragmentManager().getFragments()) {
                if (fragment instanceof PhoneVerificationFragment && fragment.isVisible()) {
                    ((PhoneVerificationFragment) fragment).onBackKeyPressed();
                    return;
                }
            }
            this.getSupportFragmentManager().popBackStack();
        }
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (final Fragment fragment : this.getSupportFragmentManager().getFragments()) {
            if (fragment instanceof ActivityListener && fragment.isVisible()) {
                ((ActivityListener) fragment).onActivityResultPrivate(requestCode, resultCode, data);
            }
        }
    }
}
