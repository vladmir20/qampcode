package com.qamp.app.qampCalls;



import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

public class CallUtils {
    private static final String TAG = "CallUtils";

    public CallUtils() {
    }

    public static void enableSpeakerPhone(AudioManager var0, boolean var1) {
        try {
            var0.setStreamSolo(0, !var1);
        } catch (Exception var2) {
        }

        var0.setSpeakerphoneOn(var1);
    }

    public static void vibratePhone(Context var0, IncomingNotification var1) {
        var1.vibrator = (Vibrator)var0.getSystemService(Context.VIBRATOR_SERVICE);
        if (var1.vibrator.hasVibrator()) {
            long[] var2 = new long[]{0L, 500L, 300L};
            var1.vibrator.vibrate(var2, 0);
        }
    }

    public static IncomingNotification notifyIncomingCall(Context var0, AudioManager var1, boolean var2) {
        IncomingNotification var3;
        (var3 = new IncomingNotification()).ringMode = var1.getRingerMode();
        if (var3.ringMode == 0) {
            return null;
        } else if (1 == var3.ringMode) {
            vibratePhone(var0, var3);
            return var3;
        } else {
            if (var2) {
                enableSpeakerPhone(var1, true);
            }

            try {
                Uri var5 = RingtoneManager.getDefaultUri(1);
                var3.mediaPlayer = new MediaPlayer();
                var3.mediaPlayer.setDataSource(var0, var5);
                if (var1.getStreamVolume(2) != 0) {
                    var1.getStreamVolume(2);
                    var1.getStreamMaxVolume(2);
                    var3.mediaPlayer.setAudioStreamType(2);
                    var3.mediaPlayer.setLooping(true);
                    var3.mediaPlayer.prepare();
                    var3.mediaPlayer.start();
                }
            } catch (Exception var4) {
                (new StringBuilder("Ringtone exception: ")).append(var4);
            }

            return var3;
        }
    }

    public static MediaPlayer playResource(Context var0, int var1, boolean var2) {
        MediaPlayer var3 = new MediaPlayer();
        Uri var5 = Uri.parse("android.resource://" + var0.getPackageName() + "/" + var1);

        try {
            var3.setDataSource(var0, var5);
            var3.setAudioStreamType(0);
            var3.setLooping(var2);
            var3.prepare();
            var3.start();
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return var3;
    }

    public static class IncomingNotification {
        private MediaPlayer mediaPlayer = null;
        private Vibrator vibrator = null;
        public int ringMode = 2;

        public IncomingNotification() {
        }

        public void stop() {
            if (this.mediaPlayer != null) {
                this.mediaPlayer.stop();
            }

            if (this.vibrator != null) {
                this.vibrator.cancel();
            }

        }
    }
}

