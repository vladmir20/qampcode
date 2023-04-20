/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:31 PM
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
            pendingIntent = PendingIntent.getActivity(context, FULLSCREEN_REQUEST_CODE, intent, 134217728 | i2);
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
        intent3.putExtra("action", "hangup");
        intent3.putExtra("nid", i);
        intent2.putExtra("answer", false);
        builder.addAction(callProperties.notify.icon_hangup, getActionText(callProperties.notify.hangup, -3407872), PendingIntent.getBroadcast(context, 2, intent3, i2 | PendingIntent.FLAG_UPDATE_CURRENT));
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
        ((NotificationManager) context.getSystemService("notification")).notify(callTag, callId, buildNotification);
    }
}
