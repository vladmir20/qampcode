package com.qamp.app.qampcallss.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MesiboCallsReceiver extends BroadcastReceiver {
    private static final String TAG = "CallReceiver";

    public void onReceive(Context context, Intent intent) {
        new StringBuilder("onReceive extras").append(intent.getIntExtra("nid", 0)).append(" action ").append(intent.getAction());
        CallNotify.cancel(CallManager.getAppContext(), intent.getIntExtra("nid", 0));
        boolean booleanExtra = intent.getBooleanExtra("answer", false);
        boolean booleanExtra2 = intent.getBooleanExtra("autoAnswer", false);
        if (booleanExtra) {
            CallManager.getInstance().launchIncomingActivity((MesiboCall.CallProperties) null, booleanExtra2, context);
        } else {
            CallManager.getInstance().hangupIncoming();
        }
    }
}
