/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:19 AM
 *
 */

package com.qamp.app;


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

public class PushNotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String text = remoteMessage.getNotification().getBody();
        String CHANNEL_ID = "MESSAGE";
        CharSequence name;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Message Notification",
                    NotificationManager.IMPORTANCE_HIGH);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Context context;
            Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
//                    .setContentTitle(title)
//                    .setContentText(text)
//                    .setSmallIcon(R.drawable.ic_logo_gradient)
//                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_logo_gradient)
                    .setContentTitle(title)
                    .setContentText(text)
//                    .setLargeIcon(R.drawable.ic_logo_gradient)
                    .setColor(ContextCompat.getColor(PushNotificationService.this, R.color.colorPrimary))
//                    .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
//                    .setStyle(new NotificationCompat.BigTextStyle().bigText(text).setSummaryText("#hashtag"))
                    .setShowWhen(true)
                    .setAutoCancel(true);
            NotificationManagerCompat.from(this).notify(1, notification.build());

        }
        super.onMessageReceived(remoteMessage);
    }
}
