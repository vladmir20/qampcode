package com.qamp.app.messaging;

import android.content.Intent;

public interface MessagingActivityListener {
    boolean Mesibo_onActionItemClicked(int i);

    boolean Mesibo_onActivityResult(int i, int i2, Intent intent);

    boolean Mesibo_onBackPressed();

    int Mesibo_onGetEnabledActionItems();

    void Mesibo_onInContextUserInterfaceClosed();

    void Mesibo_onRequestPermissionsResult(int i, String[] strArr, int[] iArr);
}
