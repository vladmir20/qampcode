/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 15/05/23, 6:12 AM
 *
 */

package com.qamp.app.qampcallss.api;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;



public class CallNotify {
    public static final String TAG = "CallNotify";
    private static String channelId = "MesiboCallNotification9";
    private static Context lastContext = null;
    private static int lastNid = -1;
    private static NotificationChannel mChannel = null;
    public static int fixedNid = 1000;
    private static String callTag = "mesibo-call";
    private static int FULLSCREEN_REQUEST_CODE = 2;
    private static int ANSWER_REQUEST_CODE = 0;

    public CallNotify() {
    }

    private static void setIntentExtras(Intent var0, int var1, MesiboCall.CallProperties var2, boolean var3, boolean var4) {
        var0.putExtra("nid", var1);
        var0.putExtra("video", var2.video.enabled);
        var0.putExtra("audio", var2.audio.enabled);
        var0.putExtra("incoming", var2.incoming);
        var0.putExtra("address", var2.user.address);
        var0.putExtra("autoAnswer", var3);
        var0.putExtra("screelock", var4);
        //if (var2.groupProfile != null) {
          // var0.putExtra("gid", var2.groupProfile.groupid);
        //}

    }

    private static Spannable getActionText(String var0, int var1) {
        SpannableString var2 = new SpannableString(var0);
        if (Build.VERSION.SDK_INT >= 25) {
            var2.setSpan(new ForegroundColorSpan(var1), 0, var2.length(), 0);
        }

        return var2;
    }

    public static int getCallId(MesiboCall.CallProperties var0) {
        if (var0 != null && Build.VERSION.SDK_INT >= 23) {
            long var1;
            if ((var1 = var0.callid) > 2147483647L) {
                var1 >>= 1;
            }

            return (int)var1;
        } else {
            return lastNid > 0 ? lastNid : fixedNid;
        }
    }

    private static String getDefaultCategory(Context var0, MesiboCall.CallProperties var1) {
        if (null != var1.notify.category) {
            return var1.notify.category;
        } else if (Build.VERSION.SDK_INT >= 23 && !var1.notify.dnd) {
            return ((NotificationManager)var0.getSystemService("notification")).getCurrentInterruptionFilter() == 1 ? "call" : "alarm";
        } else {
            return "call";
        }
    }

    public static Notification buildNotification(Context var0, MesiboCall.CallProperties var1, int var2) {
        cancel();
        lastNid = var2;
        lastContext = var0;
        if (null == var1.notify.ringtoneUri && var1.notify.sound) {
            var1.notify.ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(var0, 1);
        }

        if (Build.VERSION.SDK_INT >= 26 && mChannel == null) {
            createNotificationChannel(var0, var1);
            if (channelId != null) {
                channelId = mChannel.getId();
            }
        }

        int var3 = 0;
        if (Build.VERSION.SDK_INT >= 31) {
            var3 = 67108864;
        }

        PendingIntent var4 = null;
        if (var1.className != null && var1.fullscreen) {
            Intent var5;
            setIntentExtras(var5 = new Intent(var0, var1.className), var2, var1, false, true);
            var4 = PendingIntent.getActivity(var0, FULLSCREEN_REQUEST_CODE, var5, 134217728 | var3);
        }

        NotificationCompat.Builder var12;
        (var12 = new NotificationCompat.Builder(var0, channelId)).setPriority(1);
        var12.setCategory(getDefaultCategory(var0, var1));
        if (var4 != null) {
            var12.setFullScreenIntent(var4, true);
        }

        var12.setDefaults(1);
        if (var1.notify.icon != 0) {
            var12.setSmallIcon(var1.notify.icon);
        } else {
            var12.setSmallIcon(android.R.drawable.ic_delete);
        }

        var12.setColor(var1.notify.color);
        if (null != var1.notify.ringtoneUri) {
            var12.setSound(var1.notify.ringtoneUri);
        }

        /**if (TextUtils.isEmpty(var1.notify.title)) {
           if (var1.groupProfile != null) {
                var1.notify.title = var1.groupProfile.getName();
            } else {
                var1.notify.title = var1.user.getNameOrAddress("+");
            }
        }*/

        var12.setContentTitle(var1.notify.title);
        if (!TextUtils.isEmpty(var1.notify.message)) {
            var12.setContentText(var1.notify.message);
        }

        var12.setAutoCancel(true);
        var12.setWhen(System.currentTimeMillis());
        var12.setOngoing(true);
        long var6 = 0L;
        /**if (var1.groupProfile != null) {
            var6 = var1.groupProfile.groupid;
        }*/

        Intent var11;
        (var11 = new Intent(var0, var1.className)).putExtra("action", "answer");
        setIntentExtras(var11, var2, var1, true, false);
        var11.putExtra("answer", true);
        var11.putExtra("publish", true);
        TaskStackBuilder.create(var0).addNextIntentWithParentStack(var11);
        var11.setFlags(268566528);
        var4 = PendingIntent.getActivity(var0, ANSWER_REQUEST_CODE, var11, 134217728 | var3);
        PendingIntent var8 = null;
        Intent var9;
        /**if (!TextUtils.isEmpty(var1.notify.silentJoin)) {
            (var9 = new Intent(var0, var1.className)).putExtra("action", "answer");
            setIntentExtras(var9, var2, var1, false, false);
            var9.putExtra("answer", true);
            var9.putExtra("publish", false);
            var9.setFlags(268566528);
            var8 = PendingIntent.getActivity(var0, ANSWER_REQUEST_CODE + 1, var9, 134217728 | var3);
        }*/

        (var9 = new Intent(var0, MesiboCallsReceiver.class)).putExtra("action", "hangup");
        var9.putExtra("nid", var2);
        var9.putExtra("answer", false);
        var9.putExtra("gid", var6);
        PendingIntent var10 = PendingIntent.getBroadcast(var0, 2, var9, 134217728 | var3);
        var12.addAction(var1.notify.icon_hangup, getActionText(var1.notify.hangup, -3407872), var10);
        /**if (var8 != null) {
            var12.addAction(var1.notify.icon_answer, var1.notify.silentJoin, var8);
        }*/

        var12.addAction(var1.notify.icon_answer, var1.notify.answer, var4);
        var12.setChannelId(channelId);
        return var12.build();
    }

    public static void showCallNotification(Context var0, MesiboCall.CallProperties var1) {
        int var2 = getCallId(var1);
        Notification var3;
        Notification var10000 = var3 = buildNotification(var0, var1, var2);
        var10000.flags |= 4;
        ((NotificationManager)var0.getSystemService("notification")).notify(callTag, var2, var3);
    }

    public static void cancel(Context var0, int var1) {
        if (var1 == 0) {
            var1 = lastNid;
        }

        if (var1 != 0) {
            NotificationManagerCompat var2;
            (var2 = NotificationManagerCompat.from(var0)).cancel(callTag, var1);
            var2.cancel(var1);
            lastContext = null;
        }
    }

    public static void cancel(Context var0, MesiboCall.CallProperties var1) {
        int var2 = getCallId(var1);
        cancel(var0, var2);
    }

    public static void cancel() {
        if (lastContext != null && lastNid >= 0) {
            cancel(lastContext, lastNid);
        }

        if (lastContext != null) {
            cancelCallNotifications(lastContext);
        }

    }

    private static void createNotificationChannel(Context var0, MesiboCall.CallProperties var1) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager var3;
            if ((mChannel = (var3 = (NotificationManager)lastContext.getSystemService("notification")).getNotificationChannel(channelId)) == null) {
                String var2 = "Incoming Call Notifications";
                (mChannel = new NotificationChannel(channelId, var2, 4)).setDescription("None");
                mChannel.setLockscreenVisibility(1);
                AudioAttributes var4 = (new AudioAttributes.Builder()).setContentType(4).setUsage(2).build();
                if (null != var1.notify.ringtoneUri) {
                    mChannel.setSound(var1.notify.ringtoneUri, var4);
                }

                var3.createNotificationChannel(mChannel);
            }
        }
    }

    protected static void createNotificationChannelForService(Context var0, MesiboCall.CallProperties var1, String var2) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager var3;
            if ((var3 = (NotificationManager)var0.getSystemService("notification")).getNotificationChannel(var2) == null) {
                String var4 = "Incoming Call Notifications";
                NotificationChannel var5;
                (var5 = new NotificationChannel(var2, var4, 4)).setDescription("None");
                var5.setLockscreenVisibility(1);
                var3.createNotificationChannel(var5);
            }
        }
    }

    @TargetApi(23)
    public static void cancelCallNotifications(Context var0) {
        if (Build.VERSION.SDK_INT >= 23) {
            StatusBarNotification[] var1;
            NotificationManager var6;
            int var2 = (var1 = (var6 = (NotificationManager)var0.getSystemService("notification")).getActiveNotifications()).length;

            for(int var3 = 0; var3 < var2; ++var3) {
                StatusBarNotification var4;
                String var5;
                if ((var5 = (var4 = var1[var3]).getTag()) != null && var5.equals(callTag)) {
                    var6.cancel(var5, var4.getId());
                }
            }

        }
    }
}


/**public class CallNotify {
    private static int ANSWER_REQUEST_CODE = 0;
    private static int FULLSCREEN_REQUEST_CODE = 2;
    public static final String TAG = "CallNotify";
    private static String callTag = "mesibo-call";
    private static String channelId = "MesiboCallNotification9";
    public static int fixedNid = 1000;
    private static Context lastContext = null;
    private static int lastNid = -1;
    private static NotificationChannel mChannel = null;

    public static Notification buildNotification(Context context, MesiboCall.CallProperties callProperties, int i) {
        cancel();
        lastNid = i;
        lastContext = context;
        if (callProperties.notify.ringtoneUri == null) {
            callProperties.notify.ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, 1);
        }
        if (Build.VERSION.SDK_INT >= 26 && mChannel == null) {
            createNotificationChannel(context, callProperties);
            if (channelId != null) {
                channelId = mChannel.getId();
            }
        }
        int i2 = Build.VERSION.SDK_INT >= 31 ? 67108864 : 0;
        PendingIntent pendingIntent = null;
        if (callProperties.className != null) {
            Intent intent = new Intent(context, callProperties.className);
            setIntentExtras(intent, i, callProperties, false, true);
            pendingIntent = PendingIntent.getActivity(context, FULLSCREEN_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | i2);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        builder.setPriority(1);
        builder.setCategory(getDefaultCategory(context, callProperties));
        if (pendingIntent != null) {
            builder.setFullScreenIntent(pendingIntent, true);
        }
        builder.setDefaults(1);
        if (callProperties.notify.icon != 0) {
            builder.setSmallIcon(callProperties.notify.icon);
        } else {
            builder.setSmallIcon(com.mesibo.calls.api.R.drawable.ic_notification_call_incoming);
        }
        builder.setColor(callProperties.notify.color);
        builder.setSound(callProperties.notify.ringtoneUri);
        if (!TextUtils.isEmpty(callProperties.notify.title)) {
            builder.setContentTitle(callProperties.notify.title);
        }
        if (!TextUtils.isEmpty(callProperties.notify.message)) {
            builder.setContentText(callProperties.notify.message);
        }
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setOngoing(true);
        Intent intent2 = new Intent(context, callProperties.className);
        intent2.putExtra("action", "answer");
        setIntentExtras(intent2, i, callProperties, true, false);
        intent2.putExtra("answer", true);
        TaskStackBuilder.create(context).addNextIntentWithParentStack(intent2);
        intent2.setFlags(268566528);
        PendingIntent activity = PendingIntent.getActivity(context, ANSWER_REQUEST_CODE, intent2, PendingIntent.FLAG_UPDATE_CURRENT | i2);
        Intent intent3 = new Intent(context, MesiboCallsReceiver.class);
        //Intent intent3 = new Intent(context, callProperties.className);
        intent3.putExtra("action", "hangup");
        //intent3.putExtra("hangup",true);
        intent3.putExtra("nid", i);
        intent3.putExtra("answer", false);
        PendingIntent var10 = PendingIntent.getBroadcast(context, 2, intent3, PendingIntent.FLAG_UPDATE_CURRENT | i2);
        builder.addAction(callProperties.notify.icon_hangup, getActionText(callProperties.notify.hangup, -3407872), var10);
        builder.addAction(callProperties.notify.icon_answer, callProperties.notify.answer, activity);
        builder.setChannelId(channelId);
        return builder.build();
    }

    public static void cancel() {
        if (lastContext != null && lastNid >= 0) {
            cancel(lastContext, lastNid);
        }
        if (lastContext != null) {
            cancelCallNotifications(lastContext);
        }
    }

    public static void cancel(Context context, int i) {
        if (i == 0) {
            i = lastNid;
        }
        if (i != 0) {
            NotificationManagerCompat from = NotificationManagerCompat.from(context);
            from.cancel(callTag, i);
            from.cancel(i);
            lastContext = null;
        }
    }

    public static void cancel(Context context, MesiboCall.CallProperties callProperties) {
        cancel(context, getCallId(callProperties));
    }

    @TargetApi(23)
    public static void cancelCallNotifications(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            for (StatusBarNotification statusBarNotification : notificationManager.getActiveNotifications()) {
                String tag = statusBarNotification.getTag();
                if (tag != null && tag.equals(callTag)) {
                    notificationManager.cancel(tag, statusBarNotification.getId());
                }
            }
        }
    }

    private static void createNotificationChannel(Context context, MesiboCall.CallProperties callProperties) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager = (NotificationManager) lastContext.getSystemService("notification");
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);
            mChannel = notificationChannel;
            if (notificationChannel == null) {
                NotificationChannel notificationChannel2 = new NotificationChannel(channelId, "Incoming Call Notifications", 4);
                mChannel = notificationChannel2;
                notificationChannel2.setDescription("None");
                mChannel.setLockscreenVisibility(1);
                mChannel.setSound(callProperties.notify.ringtoneUri, new AudioAttributes.Builder().setContentType(4).setUsage(2).build());
                notificationManager.createNotificationChannel(mChannel);
            }
        }
    }

    protected static void createNotificationChannelForService(Context context, MesiboCall.CallProperties callProperties, String str) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
            if (notificationManager.getNotificationChannel(str) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(str, "Incoming Call Notifications", 4);
                notificationChannel.setDescription("None");
                notificationChannel.setLockscreenVisibility(1);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    private static Spannable getActionText(String str, int i) {
        SpannableString spannableString = new SpannableString(str);
        if (Build.VERSION.SDK_INT >= 25) {
            spannableString.setSpan(new ForegroundColorSpan(i), 0, spannableString.length(), 0);
        }
        return spannableString;
    }

    public static int getCallId(MesiboCall.CallProperties callProperties) {
        if (callProperties == null || Build.VERSION.SDK_INT < 23) {
            return lastNid > 0 ? lastNid : fixedNid;
        }
        long j = callProperties.callid;
        if (j > 2147483647L) {
            j >>= 1;
        }
        return (int) j;
    }

    private static String getDefaultCategory(Context context, MesiboCall.CallProperties callProperties) {
        return callProperties.notify.category != null ? callProperties.notify.category : (Build.VERSION.SDK_INT < 23 || callProperties.notify.dnd || ((NotificationManager) context.getSystemService("notification")).getCurrentInterruptionFilter() == 1) ? "call" : "alarm";
    }

    private static void setIntentExtras(Intent intent, int i, MesiboCall.CallProperties callProperties, boolean z, boolean z2) {
        intent.putExtra("nid", i);
        intent.putExtra(PeerConnectionClient.VIDEO_TRACK_TYPE, callProperties.video.enabled);
        intent.putExtra("incoming", callProperties.incoming);
        intent.putExtra("address", callProperties.user.address);
        intent.putExtra("autoAnswer", z);
        intent.putExtra("screelock", z2);
    }

    public static void showCallNotification(Context context, MesiboCall.CallProperties callProperties) {
        int callId = getCallId(callProperties);
        Notification buildNotification = buildNotification(context, callProperties, callId);
        buildNotification.flags |= 4;
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(callTag, callId, buildNotification);
    }*/

