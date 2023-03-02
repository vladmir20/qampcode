package com.qamp.app.qampCalls;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CallsReceiver extends BroadcastReceiver {
    private static final String TAG = "CallReceiver";

    public CallsReceiver() {
    }

    public void onReceive(Context var1, Intent var2) {
        (new StringBuilder("onReceive extras")).append(var2.getIntExtra("nid", 0)).append(" action ").append(var2.getAction());
        CallNotify.cancel(CallManager.getAppContext(), var2.getIntExtra("nid", 0));
        boolean var3 = var2.getBooleanExtra("answer", false);
        boolean var4 = var2.getBooleanExtra("autoAnswer", false);
        if (var3) {
            CallManager.getInstance().launchIncomingActivity((MesiboCall.CallProperties)null, var4);
        } else {
            CallManager.getInstance().hangupIncoming();
        }
    }
}

