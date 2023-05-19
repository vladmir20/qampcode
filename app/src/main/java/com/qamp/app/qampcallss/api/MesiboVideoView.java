/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 3:15 PM
 *
 */

package com.qamp.app.qampcallss.api;

import android.content.Context;
import android.util.AttributeSet;

public class MesiboVideoView extends MesiboVideoViewInternal {
    public MesiboVideoView(Context context) {
        super(context);
    }

    public MesiboVideoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void enableAutoResize(boolean z) {
        super.enableAutoResize(z);
    }

    public void enableHardwareScaler(boolean z) {
        super.enableHardwareScaler(z);
    }

    public void enableMirror(boolean z) {
        super.enableMirror(z);
    }

    public void enableOverlay(boolean z) {
        super.enableOverlay(z);
    }

    public void enablePip(boolean z) {
        super.enablePip(z);
    }

    public void scaleToFill(boolean z) {
        super.scaleToFill(z);
    }

    public void scaleToFit(boolean z) {
        super.scaleToFit(z);
    }

    public void stop() {
        super.stop();
    }

    public boolean toggleScaling() {
        return super.toggleScaling();
    }
}
