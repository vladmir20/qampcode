/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 3:15 PM
 *
 */

package com.qamp.app.qampcallss.api;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import java.io.IOException;

public class CallUtils {
    private static final String TAG = "CallUtils";

    public static class IncomingNotification {
        /* access modifiers changed from: private */
        public MediaPlayer mediaPlayer = null;
        public int ringMode = 2;
        /* access modifiers changed from: private */
        public Vibrator vibrator = null;

        public void stop() {
            if (this.mediaPlayer != null) {
                this.mediaPlayer.stop();
            }
            if (this.vibrator != null) {
                this.vibrator.cancel();
            }
        }
    }

    public static void enableSpeakerPhone(AudioManager audioManager, boolean z) {
        boolean z2 = false;
        if (!z) {
            z2 = true;
        }
        try {
            audioManager.setStreamSolo(0, z2);
        } catch (Exception e) {
        }
        audioManager.setSpeakerphoneOn(z);
    }

    public static IncomingNotification notifyIncomingCall(Context context, AudioManager audioManager, boolean z, Uri uri) throws IOException {
        IncomingNotification incomingNotification = new IncomingNotification();
        incomingNotification.ringMode = audioManager.getRingerMode();
        if (incomingNotification.ringMode == 0) {
            return null;
        }
        if (1 == incomingNotification.ringMode) {
            vibratePhone(context, incomingNotification);
            return incomingNotification;
        }
        if (z) {
            enableSpeakerPhone(audioManager, true);
        }
        if (uri == null) {
            try {
                uri = RingtoneManager.getDefaultUri(1);
            } catch (Exception e) {
                new StringBuilder("Ringtone exception: ").append(e);
                return incomingNotification;
            }
        }
        MediaPlayer unused = incomingNotification.mediaPlayer = new MediaPlayer();
        incomingNotification.mediaPlayer.setWakeMode(context, 1);
        incomingNotification.mediaPlayer.setDataSource(context, uri);
        if (audioManager.getStreamVolume(2) == 0) {
            return incomingNotification;
        }
        audioManager.getStreamVolume(2);
        audioManager.getStreamMaxVolume(2);
        incomingNotification.mediaPlayer.setAudioStreamType(2);
        incomingNotification.mediaPlayer.setLooping(true);
        incomingNotification.mediaPlayer.prepare();
        incomingNotification.mediaPlayer.start();
        return incomingNotification;
    }

    public static MediaPlayer playResource(Context context, int i, boolean z) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(context, Uri.parse("android.resource://" + context.getPackageName() + "/" + i));
            mediaPlayer.setAudioStreamType(0);
            mediaPlayer.setLooping(z);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaPlayer;
    }

    public static void vibratePhone(Context context, IncomingNotification incomingNotification) {
        Vibrator unused = incomingNotification.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (incomingNotification.vibrator.hasVibrator()) {
            incomingNotification.vibrator.vibrate(new long[]{0, 500, 300}, 0);
        }
    }
}
