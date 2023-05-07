/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:32 PM
 *
 */

package com.qamp.app.qampcallss.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mesibo.api.Mesibo;
import com.qamp.app.qampcallss.api.CallManager;
import com.qamp.app.qampcallss.api.CallNotify;
import com.qamp.app.qampcallss.api.MesiboCall;

public class MesiboCallsReceiver extends BroadcastReceiver {
    private static final String TAG = "CallReceiver";

    public MesiboCallsReceiver() {
    }

    public void onReceive(Context var1, Intent var2) {
        (new StringBuilder("onReceive extras")).append(var2.getIntExtra("nid", 0)).append(" action ").append(var2.getAction());
        CallNotify.cancel(CallManager.getAppContext(), var2.getIntExtra("nid", 0));
        boolean var3 = var2.getBooleanExtra("answer", false);
        boolean var4 = var2.getBooleanExtra("autoAnswer", false);
        if (var2.getLongExtra("gid", 0L) <= 0L) {
            if (var3) {
                Log.e("hangup","hhh");
                CallManager.getInstance().launchIncomingActivity((MesiboCall.CallProperties)null, var4, var1);
            } else {

                //CallManager.getInstance().hangupIncoming();
                CallManager.getInstance().hangupIncoming();
                Mesibo.hangup(0);
                //stopIncomingNotification((MesiboCall.CallProperties) null);
                //clearNotification((MesiboCall.CallProperties) null);
                //this.mCallp2p = null;
            }
        }
    }
}
