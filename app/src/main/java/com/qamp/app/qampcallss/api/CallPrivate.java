/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 3:15 PM
 *
 */

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
