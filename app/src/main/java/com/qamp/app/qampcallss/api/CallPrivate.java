package com.qamp.app.qampcallss.api;

import android.content.Intent;

public interface CallPrivate extends MesiboCall.Call {
    void OnBackground();

    void OnForeground();

    void detach();

    boolean toggleAudioDevice(MesiboCall.AudioDevice audioDevice);

    CallContext getCallContext();

    boolean isDetached();

    void onActivityResult(int i, int i2, Intent intent);

    void startScreenCapturerFromServiceOrActivityResult();
}
