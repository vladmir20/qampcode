package com.qamp.app.qampCalls;


import android.os.Environment;

import androidx.annotation.Nullable;

import org.webrtc.audio.JavaAudioDeviceModule.SamplesReadyCallback;
import org.webrtc.voiceengine.WebRtcAudioRecord.AudioSamples;
import org.webrtc.voiceengine.WebRtcAudioRecord.WebRtcAudioRecordSamplesReadyCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;

public class RecordedAudioToFileController implements SamplesReadyCallback, WebRtcAudioRecordSamplesReadyCallback {
    private static final String TAG = "RecordedAudioToFile";
    private static final long MAX_FILE_SIZE_IN_BYTES = 58348800L;
    private final Object lock = new Object();
    private final ExecutorService executor;
    @Nullable
    private OutputStream rawAudioFileOutputStream = null;
    private boolean isRunning;
    private long fileSizeInBytes = 0L;

    public RecordedAudioToFileController(ExecutorService var1) {
        this.executor = var1;
    }

    public boolean start() {
        if (!this.isExternalStorageWritable()) {
            return false;
        } else {
            synchronized(this.lock) {
                this.isRunning = true;
                return true;
            }
        }
    }

    public void stop() {
        synchronized(this.lock) {
            this.isRunning = false;
            if (this.rawAudioFileOutputStream != null) {
                try {
                    this.rawAudioFileOutputStream.close();
                } catch (IOException var3) {
                    (new StringBuilder("Failed to close file with saved input audio: ")).append(var3);
                }

                this.rawAudioFileOutputStream = null;
            }

            this.fileSizeInBytes = 0L;
        }
    }

    private boolean isExternalStorageWritable() {
        String var1 = Environment.getExternalStorageState();
        return "mounted".equals(var1);
    }

    private void openRawAudioOutputFile(int var1, int var2) {
        String var4 = Environment.getExternalStorageDirectory().getPath() + File.separator + "recorded_audio_16bits_" + var1 + "Hz" + (var2 == 1 ? "_mono" : "_stereo") + ".pcm";
        File var5 = new File(var4);

        try {
            this.rawAudioFileOutputStream = new FileOutputStream(var5);
        } catch (FileNotFoundException var3) {
            (new StringBuilder("Failed to open audio output file: ")).append(var3.getMessage());
        }
    }

    public void onWebRtcAudioRecordSamplesReady(AudioSamples var1) {
        this.onWebRtcAudioRecordSamplesReady(new org.webrtc.audio.JavaAudioDeviceModule.AudioSamples(var1.getAudioFormat(), var1.getChannelCount(), var1.getSampleRate(), var1.getData()));
    }

    public void onWebRtcAudioRecordSamplesReady(org.webrtc.audio.JavaAudioDeviceModule.AudioSamples var1) {
        if (var1.getAudioFormat() == 2) {
            synchronized(this.lock) {
                if (!this.isRunning) {
                    return;
                }

                if (this.rawAudioFileOutputStream == null) {
                    this.openRawAudioOutputFile(var1.getSampleRate(), var1.getChannelCount());
                    this.fileSizeInBytes = 0L;
                }
            }

            this.executor.execute(() -> {
                if (this.rawAudioFileOutputStream != null) {
                    try {
                        if (this.fileSizeInBytes < 58348800L) {
                            this.rawAudioFileOutputStream.write(var1.getData());
                            this.fileSizeInBytes += (long)var1.getData().length;
                        }

                        return;
                    } catch (IOException var2) {
                        (new StringBuilder("Failed to write audio to file: ")).append(var2.getMessage());
                    }
                }

            });
        }
    }
}

