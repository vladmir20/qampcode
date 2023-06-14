/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.MessagingModule.AllUtils;

import android.os.Build;
import android.os.Trace;

public class MyTrace {
    public static boolean enabled = false;

    public static void start(String name) {
        if (enabled && Build.VERSION.SDK_INT >= 18) {
            Trace.beginSection(name);
        }
    }

    public static void stop() {
        if (enabled && Build.VERSION.SDK_INT >= 18) {
            Trace.endSection();
        }
    }

    public static void setEnable(boolean enable) {
        enabled = enable;
    }
}
