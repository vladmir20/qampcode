package com.qamp.app.qampCalls;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.R;


public class ScreenSharingService extends Service {
    public static final String START = "start";
    public static final String STOP = "stop";
    private static int ONGOING_NOTIFICATION_ID = 100;
    private static String CHANNEL_DEFAULT_IMPORTANCE = "MesiboScreenCaptureChannel";
    private static Notification notification = null;
    private static RtcCall mCall = null;

    public ScreenSharingService() {
    }

    @TargetApi(26)
    public void onCreate() {
        super.onCreate();
        CallPrivate var1;
        MesiboCall.CallProperties var3;
        if ((var1 = (CallPrivate) CallManager.getInstance().getActiveCall()) != null) {
            var3 = var1.getCallProperties();
        } else {
            (var3 = MesiboCall.getInstance().createCallProperties(true)).user = new MesiboProfile();
            String var2;
            if (TextUtils.isEmpty(var2 = MesiboCall.getInstance().mDefaultUiProperies.title)) {
                var2 = Mesibo.getAppName();
            }

            var3.user.setName(var2);
        }

        CallNotify.createNotificationChannelForService(this, var3, CHANNEL_DEFAULT_IMPORTANCE);
        Builder var4 = new Builder(this, CHANNEL_DEFAULT_IMPORTANCE);
        if (!TextUtils.isEmpty(var3.notify.title)) {
            var4.setContentTitle(var3.notify.title);
        } else {
            var4.setContentTitle(var3.user.getName());
        }

        if (!TextUtils.isEmpty(var3.notify.screenSharing)) {
            var4.setContentText(var3.notify.screenSharing);
        }

        var4.setSmallIcon(R.drawable.ic_mesibo_screen_sharing);//=========
        notification = var4.build();
        this.startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    public int onStartCommand(Intent var1, int var2, int var3) {
        var1.getStringExtra("inputExtra");
        if (var1.getAction() == "start") {
            mCall.startScreenCapturerFromServiceOrActivityResult();
            this.startForeground(ONGOING_NOTIFICATION_ID, notification);
            return Service.START_STICKY;
        } else {
            return Service.START_NOT_STICKY;
        }
    }

    public IBinder onBind(Intent var1) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static void start(Context var0, RtcCall var1, Intent var2) {
        if (VERSION.SDK_INT < MesiboCall.getInstance().getMinimumVersionForForegroundService()) {
            var1.startScreenCapturerFromServiceOrActivityResult();
        } else {
            mCall = var1;
            Intent var3;
            (var3 = new Intent(var0, ScreenSharingService.class)).setAction("start");
            var3.putExtra("screenIntent", var2);
            ContextCompat.startForegroundService(var0, var3);
        }
    }

    public static void stop(Context var0) {
        if (VERSION.SDK_INT >= MesiboCall.getInstance().getMinimumVersionForForegroundService()) {
            Intent var1;
            (var1 = new Intent(var0, ScreenSharingService.class)).setAction("stop");
            var0.stopService(var1);
        }
    }
}

