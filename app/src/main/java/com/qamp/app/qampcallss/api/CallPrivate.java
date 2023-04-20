/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:31 PM
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
