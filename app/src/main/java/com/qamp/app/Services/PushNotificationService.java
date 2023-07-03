

package com.qamp.app.Services;


import static com.qamp.app.Activity.QampContactScreen.PROJECTION;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.qamp.app.MessagingModule.Contact;
import com.qamp.app.MessagingModule.MesiboMessagingActivity;
import com.qamp.app.R;

import java.util.HashSet;
import java.util.Map;

public class PushNotificationService extends FirebaseMessagingService{


    public String  getContactList(String cName, String cNumber) {
        ContentResolver cr = getApplicationContext().getContentResolver();

        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor != null) {

            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                for(int i = 0; i<=12;i++) {
                    String name, number;
                    Log.e("num",cNumber);
                    while (cursor.moveToNext()) {
                        name = cursor.getString(nameIndex);
                        number = cursor.getString(numberIndex);


                        number = number.replace(" ", "");
                        number = number.replace("(", "");
                        number = number.replace(")", "");
                        number = number.replace("-", "");

                        if (number.equals(cNumber.substring(2))) {
                            //contactList.add(new Contact(name, number));
                            cName = name;
                            //Log.e("notificatioon_name", "Phone Number: name = " + name
                            //      + " No = " + number + cName + cNumber.substring(2));
                            break;
                        }


                        //mobileNoSet.add(number);

                        //if (!mobileNoSet.contains(number)) {}
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return cName;
    }
  @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        //String title = remoteMessage.getNotification().getTitle();
      String title = remoteMessage.getData().get("title");
      String text = remoteMessage.getData().get("body");
      //String notificationTye = remoteMessage.getData().get("notificationType");
      //String text = remoteMessage.getNotification().getBody();
      String groupIId = remoteMessage.getData().get("groupid");
      String destination = remoteMessage.getData().get("destinationId");
      Log.e("title",new Gson().toJson(remoteMessage));
      Log.e("text",new Gson().toJson(text));
      Log.e("GroupID",new Gson().toJson(remoteMessage.getData().get("groupid")));
      //Map <String, Object> map = new Gson().fromJson(text, Map.class);
      //Log.e("Message",new Gson().toJson(map.get("message")));
      //Log.e("notificationType", new Gson().toJson(map.get("notificationType")));


      String titleFinal = getContactList(title,destination);
      Log.e("titleFinal",titleFinal);
      Intent intent = new Intent(getApplicationContext(), MesiboMessagingActivity.class);
        //Long group = Long.parseLong(groupIId);
      if(remoteMessage.getData().get("notificationType").equals("MESSAGE_GROUP"))
          intent.putExtra("groupid",Long.valueOf(remoteMessage.getData().get("destinationId")));
      else
          intent.putExtra("peer",remoteMessage.getData().get("destinationId"));
      //intent.putExtra("groupid",Long.valueOf(1007));
      PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
              PendingIntent.FLAG_UPDATE_CURRENT);




        String CHANNEL_ID = "MESSAGE";
      Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        CharSequence name;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Message Notification", NotificationManager.IMPORTANCE_HIGH);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
                    channel.setSound(uri,audioAttributes);
                    channel.enableVibration(true);
                    channel.setVibrationPattern(new long[]{0, 300, 250, 300, 250, 300});
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Context context;
            Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.qamp_mini_logo)
                    .setContentTitle(titleFinal)
                    .setContentText(text)//remoteMessage.getData().toString()
                    .setColor(ContextCompat.getColor(PushNotificationService.this, R.color.colorPrimary))
                    .setShowWhen(true)
                    //.setVibrate(new long[]{0, 500, 1000})
                    .setPriority(Notification.PRIORITY_HIGH)
                    //.setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true).setContentIntent(pIntent);

            //notification.setSound(DEF)
            NotificationManagerCompat.from(this).notify(1, notification.build());
        }
        super.onMessageReceived(remoteMessage);
    }
}
