package com.qamp.app.qampCalls;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build.VERSION;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import androidx.core.app.NotificationCompat.Builder;
import androidx.core.app.NotificationManagerCompat;

import com.qamp.app.R;

//import R.drawable;

public class CallNotify {
    public static final String TAG = "CallNotify";
    private static String channelId = "MesiboCallNotification9";
    private static Context lastContext = null;
    private static int lastNid = -1;
    private static NotificationChannel mChannel = null;
    public static int fixedNid = 1000;

    public CallNotify() {
    }

    private static void setIntentExtras(Intent var0, int var1, MesiboCall.CallProperties var2, boolean var3, boolean var4) {
        var0.putExtra("nid", var1);
        var0.putExtra("video", var2.video.enabled);
        var0.putExtra("incoming", var2.incoming);
        var0.putExtra("address", var2.user.address);
        var0.putExtra("autoAnswer", var3);
        var0.putExtra("screelock", var4);
    }

    private static Spannable getActionText(String var0, int var1) {
        SpannableString var2 = new SpannableString(var0);
        if (VERSION.SDK_INT >= 25) {
            var2.setSpan(new ForegroundColorSpan(var1), 0, var2.length(), 0);
        }

        return var2;
    }

    public static void showCallNotification(Context var0, MesiboCall.CallProperties var1) {
        cancel();
        int var2;
        lastNid = var2 = fixedNid;
        lastContext = var0;
        if (null == var1.notify.ringtoneUri) {
            var1.notify.ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(var0, 1);
        }

        if (VERSION.SDK_INT >= 26 && mChannel == null) {
            createNotificationChannel(var0, var1);
            if (channelId != null) {
                channelId = mChannel.getId();
            }
        }

        PendingIntent var3 = null;
        if (var1.className != null) {
            Intent var4;
            setIntentExtras(var4 = new Intent(var0, var1.className), var2, var1, false, true);
            var3 = PendingIntent.getActivity(var0, 0, var4, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Builder var9;
        (var9 = new Builder(var0, channelId)).setPriority(1);
        var9.setCategory("call");
        if (var3 != null) {
            var9.setFullScreenIntent(var3, true);
        }

        var9.setDefaults(1);
        if (var1.notify.icon != 0) {
            var9.setSmallIcon(var1.notify.icon);
        } else {
            var9.setSmallIcon(R.drawable.ic_action_missedcall); //======== incoming call image
        }

        var9.setColor(var1.notify.color);
        var9.setSound(var1.notify.ringtoneUri);
        if (!TextUtils.isEmpty(var1.notify.title)) {
            var9.setContentTitle(var1.notify.title);
        }

        if (!TextUtils.isEmpty(var1.notify.message)) {
            var9.setContentText(var1.notify.message);
        }

        var9.setAutoCancel(true);
        var9.setWhen(System.currentTimeMillis());
        var9.setOngoing(true);
        Intent var8;
        (var8 = new Intent(var0, CallsReceiver.class)).setAction("USER_ACTION_ANSWER");
        var8.putExtra("action", "answer");
        setIntentExtras(var8, var2, var1, true, false);
        var8.putExtra("answer", true);
        PendingIntent var5 = PendingIntent.getBroadcast(var0, 1, var8, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent var6;
        (var6 = new Intent(var0, CallsReceiver.class)).setAction("USER_ACTION_HANGUP");
        var6.putExtra("action", "hangup");
        var6.putExtra("nid", var2);
        var8.putExtra("answer", false);
        var3 = PendingIntent.getBroadcast(var0, 2, var6, PendingIntent.FLAG_UPDATE_CURRENT);
        var9.addAction(var1.notify.icon_hangup, getActionText(var1.notify.hangup, -3407872), var3);
        var9.addAction(var1.notify.icon_answer, var1.notify.answer, var5);
        var9.setChannelId(channelId);
        Notification var7;
        Notification var10000 = var7 = var9.build();
        var10000.flags |= 4;
        ((NotificationManager)var0.getSystemService(Context.NOTIFICATION_SERVICE)).notify(var2, var7);
    }

    public static void cancel(Context var0, int var1) {
        NotificationManagerCompat.from(var0).cancel(var1);
        lastContext = null;
    }

    public static void cancel() {
        if (lastContext != null && lastNid >= 0) {
            cancel(lastContext, lastNid);
        }
    }

    private static void createNotificationChannel(Context var0, MesiboCall.CallProperties var1) {
        if (VERSION.SDK_INT >= 26) {
            NotificationManager var3;
            if ((mChannel = (var3 = (NotificationManager)lastContext.getSystemService(Context.NOTIFICATION_SERVICE)).getNotificationChannel(channelId)) == null) {
                String var2 = "Incoming Call Notifications";
                (mChannel = new NotificationChannel(channelId, var2, NotificationManager.IMPORTANCE_HIGH)).setDescription("None");
                mChannel.setLockscreenVisibility(1);
                AudioAttributes var4 = (new AudioAttributes.Builder()).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION).build();
                mChannel.setSound(var1.notify.ringtoneUri, var4);
                var3.createNotificationChannel(mChannel);
            }
        }
    }

    protected static void createNotificationChannelForService(Context var0, MesiboCall.CallProperties var1, String var2) {
        if (VERSION.SDK_INT >= 26) {
            NotificationManager var3;
            if ((var3 = (NotificationManager)var0.getSystemService(Context.NOTIFICATION_SERVICE)).getNotificationChannel(var2) == null) {
                String var4 = "Incoming Call Notifications";
                NotificationChannel var5;
                (var5 = new NotificationChannel(var2, var4, NotificationManager.IMPORTANCE_HIGH)).setDescription("None");
                var5.setLockscreenVisibility(1);
                var3.createNotificationChannel(var5);
            }
        }
    }
}

