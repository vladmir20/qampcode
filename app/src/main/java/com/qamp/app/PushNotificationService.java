/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 04/05/23, 1:44 AM
 *
 */

package com.qamp.app;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationService extends FirebaseMessagingService{
//  @Override
//    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
//        String title = remoteMessage.getNotification().getTitle();
//        String text = remoteMessage.getNotification().getBody();
//        String CHANNEL_ID = "MESSAGE";
//        CharSequence name;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    CHANNEL_ID, "Message Notification", NotificationManager.IMPORTANCE_HIGH);
//            getSystemService(NotificationManager.class).createNotificationChannel(channel);
//            Context context;
//            Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
//                    .setSmallIcon(R.mipmap.qamp_mini_logo)
//                    .setContentTitle(title)
//                    .setContentText(remoteMessage.getData().toString())
//                    .setColor(ContextCompat.getColor(PushNotificationService.this, R.color.colorPrimary))
//                    .setShowWhen(true)
//                    .setPriority(Notification.PRIORITY_HIGH)
//                    .setDefaults(Notification.DEFAULT_ALL)
//                    .setAutoCancel(true);
//            NotificationManagerCompat.from(this).notify(1, notification.build());
//        }
//        super.onMessageReceived(remoteMessage);
//    }
}
