/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.MessagingModule;

import android.content.Intent;

public interface MessagingActivityListener {
    boolean Mesibo_onActionItemClicked(int i);

    boolean Mesibo_onActivityResult(int i, int i2, Intent intent);

    boolean Mesibo_onBackPressed();

    int Mesibo_onGetEnabledActionItems();

    void Mesibo_onInContextUserInterfaceClosed();

    void Mesibo_onRequestPermissionsResult(int i, String[] strArr, int[] iArr);
}
