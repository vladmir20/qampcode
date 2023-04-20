/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:31 PM
 *
 */

package com.qamp.app.fcm;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.qamp.app.MainApplication;

public class MesiboGcmListenerService extends FirebaseMessagingService {

    private static final String TAG = "FcmListenerService";


    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

            Bundle data = new Bundle();
            data.putString("body","newPushNotification");


            MesiboRegistrationIntentService.sendMessageToListener( false);

            Intent intent = new Intent("com.qamp.app");
            intent.putExtras(data);
            MesiboJobIntentService.enqueueWork(MainApplication.getAppContext(), intent);

    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("newToken", s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply();

    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }
}
