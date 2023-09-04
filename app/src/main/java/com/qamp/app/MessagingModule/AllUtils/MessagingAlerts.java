/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.MessagingModule.AllUtils;

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
