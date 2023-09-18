/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.LoginModule.MesiboApiClasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.qamp.app.MainApplication;

public class AutoStart extends BroadcastReceiver {
    //Alarm alarm = new Alarm();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                intent.getAction().equals(MainApplication.getRestartIntent())) {
            StartUpActivity.newInstance(context, true);
        }
    }
}
