package com.qamp.app.qampCalls;

import android.content.Intent;


interface CallPrivate extends MesiboCall.Call {
    void OnForeground();

    void OnBackground();

    void detach();

    boolean isDetached();

    void onActivityResult(int var1, int var2, Intent var3);

    void startScreenCapturerFromServiceOrActivityResult();

    CallContext getCallContext();
}

