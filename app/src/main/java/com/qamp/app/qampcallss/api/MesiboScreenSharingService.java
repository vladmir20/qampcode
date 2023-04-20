/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:32 PM
 *
 */

package com.qamp.app.qampcallss.api;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.R;

public class MesiboScreenSharingService extends Service {
    private static String CHANNEL_DEFAULT_IMPORTANCE = "MesiboScreenCaptureChannel";
    private static int ONGOING_NOTIFICATION_ID = 100;
    public static final String START = "start";
    public static final String STOP = "stop";
    private static RtcCall mCall = null;
    private static Notification notification = null;

    public static void start(Context context, RtcCall rtcCall, Intent intent) {
        if (Build.VERSION.SDK_INT < MesiboCall.getInstance().getMinimumVersionForForegroundService()) {
            rtcCall.startScreenCapturerFromServiceOrActivityResult();
            return;
        }
        mCall = rtcCall;
        Intent intent2 = new Intent(context, MesiboScreenSharingService.class);
        intent2.setAction("start");
        intent2.putExtra("screenIntent", intent);
        ContextCompat.startForegroundService(context, intent2);
    }

    public static void stop(Context context) {
        if (Build.VERSION.SDK_INT >= MesiboCall.getInstance().getMinimumVersionForForegroundService()) {
            Intent intent = new Intent(context, MesiboScreenSharingService.class);
            intent.setAction("stop");
            context.stopService(intent);
        }
    }

    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @TargetApi(26)
    public void onCreate() {
        MesiboCall.CallProperties callProperties;
        super.onCreate();
        CallPrivate callPrivate = (CallPrivate) CallManager.getInstance().getActiveCall();
        if (callPrivate != null) {
            callProperties = callPrivate.getCallProperties();
        } else {
            MesiboCall.CallProperties createCallProperties = MesiboCall.getInstance().createCallProperties(true);
            createCallProperties.user = new MesiboProfile();
            String str = MesiboCall.getInstance().mDefaultUiProperies.title;
            if (TextUtils.isEmpty(str)) {
                str = Mesibo.getAppName();
            }
            createCallProperties.user.setName(str);
            callProperties = createCallProperties;
        }
        CallNotify.createNotificationChannelForService(this, callProperties, CHANNEL_DEFAULT_IMPORTANCE);
        Notification.Builder builder = new Notification.Builder(this, CHANNEL_DEFAULT_IMPORTANCE);
        if (!TextUtils.isEmpty(callProperties.notify.title)) {
            builder.setContentTitle(callProperties.notify.title);
        } else {
            builder.setContentTitle(callProperties.user.getNameOrAddress("+"));
        }
        if (!TextUtils.isEmpty(callProperties.notify.screenSharing)) {
            builder.setContentText(callProperties.notify.screenSharing);
        }
        builder.setSmallIcon(R.drawable.ic_mesibo_screen_sharing);
        notification = builder.build();
        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        intent.getStringExtra("inputExtra");
        if (intent.getAction() != "start") {
            return Service.START_NOT_STICKY;
        }
        mCall.startScreenCapturerFromServiceOrActivityResult();
        startForeground(ONGOING_NOTIFICATION_ID, notification);
        return Service.START_STICKY;
    }
}
