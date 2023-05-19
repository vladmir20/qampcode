/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 3:15 PM
 *
 */

package com.qamp.app.qampcallss.api.p000ui;

import com.qamp.app.qampcallss.api.MesiboCall;
import com.qamp.app.qampcallss.api.MesiboCallActivityInternal;
import com.qamp.app.qampcallss.api.Utils;

public class QampCallsActivity extends MesiboCallActivityInternal {

    public QampCallsActivity() {}

    public int checkPermissions(boolean z) {
        /**ArrayList arrayList = new ArrayList();
        arrayList.add("android.permission.RECORD_AUDIO");
        arrayList.add("android.permission.READ_PHONE_STATE");
        if (z) {
            arrayList.add(0, "android.permission.CAMERA");
        }
        if (Build.VERSION.SDK_INT >= 31) {
            arrayList.add("android.permission.BLUETOOTH_CONNECT");
        }
        String[] strArr = new String[arrayList.size()];
        arrayList.toArray(strArr);*/
        String[] var2 = new String[]{"android.permission.RECORD_AUDIO", "android.permission.READ_PHONE_STATE"};
        if (z) {
            var2 = new String[]{"android.permission.CAMERA", "android.permission.RECORD_AUDIO", "android.permission.READ_PHONE_STATE"};
        }
        return Utils.checkPermissions(0, this, var2, false);
    }

    public void setPublisher(MesiboCall.MesiboParticipant mesiboParticipant) {
    }
}
