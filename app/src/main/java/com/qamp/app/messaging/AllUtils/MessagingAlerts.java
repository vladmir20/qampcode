package com.qamp.app.messaging.AllUtils;

import android.app.ProgressDialog;
import android.content.Context;

public class MessagingAlerts {
    public static ProgressDialog getProgressDialog(Context c, String message) {
        ProgressDialog progressDialog = new ProgressDialog(c);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        return progressDialog;
    }
}
