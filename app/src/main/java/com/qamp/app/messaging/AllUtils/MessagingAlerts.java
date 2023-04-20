/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:31 PM
 *
 */

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
