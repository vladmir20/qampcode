/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:31 PM
 *
 */

//
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.uihelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.qamp.app.LoginQampActivity;
import com.qamp.app.SplashScreenActivity;
import com.qamp.app.uihelper.Utils.Alert;

import java.lang.ref.WeakReference;

public class MesiboUiHelper {
    public static MesiboUiHelperConfig mMesiboUiHelperConfig;
    public static WeakReference<ILoginInterface> mLoginInterface;
    public static IProductTourListener mProductTourListener;

    static {
        MesiboUiHelper.mMesiboUiHelperConfig = new MesiboUiHelperConfig();
        MesiboUiHelper.mLoginInterface = null;
        MesiboUiHelper.mProductTourListener = null;
    }

    @SuppressLint("WrongConstant")
    public static void launchTour(final Context context, final boolean newTask, final IProductTourListener listener) {
        MesiboUiHelper.mProductTourListener = listener;
        final Intent intent = new Intent(context, (Class) SplashScreenActivity.class);//ProductTourActivity.class);
        if (newTask) {
            intent.setFlags(268468224);
        }
        context.startActivity(intent);
    }

    public static void launchWelcome(final Context context, final boolean newTask, final ILoginInterface iLogin) {
        MesiboUiHelper.mLoginInterface = new WeakReference<ILoginInterface>(iLogin);
        launch(context, newTask, 0);
    }

    public static void launchLogin(final Context context, final boolean newTask, final int type, final ILoginInterface iLogin) {
        MesiboUiHelper.mLoginInterface = new WeakReference<ILoginInterface>(iLogin);
        launch(context, newTask, type);
    }

    @SuppressLint("WrongConstant")
    private static void launch(final Context context, final boolean newTask, final int type) {
        final Intent intent = new Intent(context, (Class) LoginQampActivity.class);
        if (newTask) {
            intent.setFlags(268468224);
        }
        final Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static MesiboUiHelperConfig getConfig() {
        return MesiboUiHelper.mMesiboUiHelperConfig;
    }

    public static void setConfig(final MesiboUiHelperConfig mesiboUiHelperConfig) {
        MesiboUiHelper.mMesiboUiHelperConfig = mesiboUiHelperConfig;
    }

    public static ILoginInterface getLoginInterface() {
        if (null == MesiboUiHelper.mLoginInterface) {
            return null;
        }
        return MesiboUiHelper.mLoginInterface.get();
    }

    public static void showChoicesDialog(final Activity context, final String title, final String[] items) {
        Alert.showChoicesDialog(context, title, items);
    }
}
