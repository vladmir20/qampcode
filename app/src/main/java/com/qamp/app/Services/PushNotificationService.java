

package com.qamp.app.Services;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.qamp.app.R;

import java.util.Map;

public class PushNotificationService extends FirebaseMessagingService{
  @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        //String title = remoteMessage.getNotification().getTitle();
      String title = remoteMessage.getData().get("title");
      String text = remoteMessage.getData().get("body");
      //String notificationTye = remoteMessage.getData().get("notificationType");
      //String text = remoteMessage.getNotification().getBody();
      Log.e("title",new Gson().toJson(remoteMessage));
      Log.e("text",new Gson().toJson(text));
      //Map <String, Object> map = new Gson().fromJson(text, Map.class);
      //Log.e("Message",new Gson().toJson(map.get("message")));
      //Log.e("notificationType", new Gson().toJson(map.get("notificationType")));



        String CHANNEL_ID = "MESSAGE";
        CharSequence name;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Message Notification", NotificationManager.IMPORTANCE_HIGH);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Context context;
            Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.qamp_mini_logo)
                    .setContentTitle(title)
                    .setContentText(text)//remoteMessage.getData().toString()
                    .setColor(ContextCompat.getColor(PushNotificationService.this, R.color.colorPrimary))
                    .setShowWhen(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true);
            //notification.setSound(DEF)
            NotificationManagerCompat.from(this).notify(1, notification.build());
        }
        super.onMessageReceived(remoteMessage);
    }
}
