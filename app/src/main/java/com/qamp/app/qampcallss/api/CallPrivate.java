/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:40 AM
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
