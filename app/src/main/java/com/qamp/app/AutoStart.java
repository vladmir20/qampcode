/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:40 AM
 *
 */

package com.qamp.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
