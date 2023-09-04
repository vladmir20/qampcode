/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 3:15 PM
 *
 */

package com.qamp.app.qampcallss.api;

import com.mesibo.calls.api.MesiboVideoViewInternal;
import com.qamp.app.qampcallss.api.p000ui.QampCallsActivity;

public class DummyListener implements MesiboCall.GroupCallInProgressListener, MesiboCall.InProgressListener, MesiboVideoViewInternal.VideoViewListener {
    public void MesiboCall_OnAudioDeviceChanged(MesiboCall.CallProperties callProperties, MesiboCall.AudioDevice audioDevice, MesiboCall.AudioDevice audioDevice2) {
    }

    public void MesiboCall_OnBatteryStatus(MesiboCall.CallProperties callProperties, boolean z, boolean z2) {
    }

    public void MesiboCall_OnDTMF(MesiboCall.CallProperties callProperties, int i) {
    }

    public void MesiboCall_OnHangup(MesiboCall.CallProperties callProperties, int i) {
    }

    public void MesiboCall_OnMute(MesiboCall.CallProperties callProperties, boolean z, boolean z2, boolean z3) {
    }

    public void MesiboCall_OnOrientationChanged(MesiboCall.CallProperties callProperties, boolean z, boolean z2) {
    }

    public boolean MesiboCall_OnPlayInCallSound(MesiboCall.CallProperties callProperties, int i, boolean z) {
        return false;
    }

    @Override
    public void MesiboCall_OnSetCall(QampCallsActivity mesiboCallActivity, MesiboCall.Call call) {

    }

    public void MesiboCall_OnStatus(MesiboCall.CallProperties callProperties, int i, boolean z) {
    }

    public void MesiboCall_OnUpdateUserInterface(MesiboCall.CallProperties callProperties, int i, boolean z, boolean z2) {
    }

    public void MesiboCall_OnVideo(MesiboCall.CallProperties callProperties, MesiboCall.VideoProperties videoProperties, boolean z) {
    }

    public void MesiboCall_OnVideoSourceChanged(MesiboCall.CallProperties callProperties, int i, int i2) {
    }

    public void MesiboGroupcall_OnAudio(MesiboCall.MesiboParticipant mesiboParticipant) {
    }

    public void MesiboGroupcall_OnConnected(MesiboCall.MesiboParticipant mesiboParticipant, boolean z) {
    }

    public void MesiboGroupcall_OnHangup(MesiboCall.MesiboParticipant mesiboParticipant, int i) {
    }

    public void MesiboGroupcall_OnMute(MesiboCall.MesiboParticipant mesiboParticipant, boolean z, boolean z2, boolean z3) {
    }

    public void MesiboGroupcall_OnTalking(MesiboCall.MesiboParticipant mesiboParticipant, boolean z) {
    }

    public void MesiboGroupcall_OnVideo(MesiboCall.MesiboParticipant mesiboParticipant, float f, boolean z) {
    }

    public void MesiboGroupcall_OnVideoSourceChanged(MesiboCall.MesiboParticipant mesiboParticipant, int i, int i2) {
    }

    public void MesiboVideoView_OnVideoResolutionChanged(int i, int i2, int i3) {
    }

    public void MesiboVideoView_OnViewSizeChanged(int i, int i2) {
    }
}
