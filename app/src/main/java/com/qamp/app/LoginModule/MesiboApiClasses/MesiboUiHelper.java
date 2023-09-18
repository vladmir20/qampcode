package com.qamp.app.LoginModule.MesiboApiClasses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.qamp.app.Activity.LoginActivity;
import com.qamp.app.Activity.SplashScreenActivity;

import java.lang.ref.WeakReference;

public class MesiboUiHelper {
    public static MesiboUiHelperConfig mMesiboUiHelperConfig;
    public static WeakReference<ILoginInterface> mLoginInterface;


    static {
        MesiboUiHelper.mMesiboUiHelperConfig = new MesiboUiHelperConfig();
        MesiboUiHelper.mLoginInterface = null;
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
        final Intent intent = new Intent(context, (Class) LoginActivity.class);
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

}