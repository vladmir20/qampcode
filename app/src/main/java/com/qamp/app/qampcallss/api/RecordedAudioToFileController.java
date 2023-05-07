/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:40 AM
 *
 */

package com.qamp.app.qampcallss.api;

import android.os.Environment;

import androidx.annotation.Nullable;

import org.webrtc.audio.JavaAudioDeviceModule;
import org.webrtc.voiceengine.WebRtcAudioRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;

public class RecordedAudioToFileController implements JavaAudioDeviceModule.SamplesReadyCallback, WebRtcAudioRecord.WebRtcAudioRecordSamplesReadyCallback {
    private static final long MAX_FILE_SIZE_IN_BYTES = 58348800;
    private static final String TAG = "RecordedAudioToFile";
    private final ExecutorService executor;
    private long fileSizeInBytes = 0;
    private boolean isRunning;
    private final Object lock = new Object();
    @Nullable
    private OutputStream rawAudioFileOutputStream = null;

    public RecordedAudioToFileController(ExecutorService executorService) {
        this.executor = executorService;
    }

    private boolean isExternalStorageWritable() {
        return "mounted".equals(Environment.getExternalStorageState());
    }

    private /* synthetic */ void lambda$onWebRtcAudioRecordSamplesReady$0(JavaAudioDeviceModule.AudioSamples audioSamples) {
        if (this.rawAudioFileOutputStream != null) {
            try {
                if (this.fileSizeInBytes < MAX_FILE_SIZE_IN_BYTES) {
                    this.rawAudioFileOutputStream.write(audioSamples.getData());
                    this.fileSizeInBytes += (long) audioSamples.getData().length;
                }
            } catch (IOException e) {
                new StringBuilder("Failed to write audio to file: ").append(e.getMessage());
            }
        }
    }

    private void openRawAudioOutputFile(int i, int i2) {
        try {
            this.rawAudioFileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "recorded_audio_16bits_" + String.valueOf(i) + "Hz" + (i2 == 1 ? "_mono" : "_stereo") + ".pcm"));
        } catch (FileNotFoundException e) {
            new StringBuilder("Failed to open audio output file: ").append(e.getMessage());
        }
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: Unknown instruction: 'invoke-custom' in method: com.mesibo.calls.api.RecordedAudioToFileController.onWebRtcAudioRecordSamplesReady(org.webrtc.audio.JavaAudioDeviceModule$AudioSamples):void, dex: classes.jar
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:151)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:286)
        	at jadx.core.ProcessClass.process(ProcessClass.java:36)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:59)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
        Caused by: jadx.core.utils.exceptions.DecodeException: Unknown instruction: 'invoke-custom'
        	at jadx.core.dex.instructions.InsnDecoder.decode(InsnDecoder.java:588)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:78)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:136)
        	... 5 more
        */
    public void onWebRtcAudioRecordSamplesReady(JavaAudioDeviceModule.AudioSamples r1) {
        /*
        // Can't load method instructions: Load method exception: Unknown instruction: 'invoke-custom' in method: com.mesibo.calls.api.RecordedAudioToFileController.onWebRtcAudioRecordSamplesReady(org.webrtc.audio.JavaAudioDeviceModule$AudioSamples):void, dex: classes.jar
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mesibo.calls.api.RecordedAudioToFileController.onWebRtcAudioRecordSamplesReady(org.webrtc.audio.JavaAudioDeviceModule$AudioSamples):void");
    }

    public void onWebRtcAudioRecordSamplesReady(WebRtcAudioRecord.AudioSamples audioSamples) {
        onWebRtcAudioRecordSamplesReady(new JavaAudioDeviceModule.AudioSamples(audioSamples.getAudioFormat(), audioSamples.getChannelCount(), audioSamples.getSampleRate(), audioSamples.getData()));
    }

    public boolean start() {
        if (!isExternalStorageWritable()) {
            return false;
        }
        synchronized (this.lock) {
            this.isRunning = true;
        }
        return true;
    }

    public void stop() {
        synchronized (this.lock) {
            this.isRunning = false;
            if (this.rawAudioFileOutputStream != null) {
                try {
                    this.rawAudioFileOutputStream.close();
                } catch (IOException e) {
                    new StringBuilder("Failed to close file with saved input audio: ").append(e);
                }
                this.rawAudioFileOutputStream = null;
            }
            this.fileSizeInBytes = 0;
        }
    }
}
