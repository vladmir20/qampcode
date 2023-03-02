package com.qamp.app.qampCalls;



public class DummyListener implements MesiboCall.GroupCallInProgressListener, MesiboCall.InProgressListener, MesiboVideoViewInternal.VideoViewListener {
    public DummyListener() {
    }

    public void MesiboCall_OnSetCall(QampCallActivity var1, MesiboCall.Call var2) {
    }

    public void MesiboCall_OnMute(MesiboCall.CallProperties var1, boolean var2, boolean var3, boolean var4) {
    }

    public boolean MesiboCall_OnPlayInCallSound(MesiboCall.CallProperties var1, int var2, boolean var3) {
        return false;
    }

    public void MesiboCall_OnHangup(MesiboCall.CallProperties var1, int var2) {
    }

    public void MesiboCall_OnStatus(MesiboCall.CallProperties var1, int var2, boolean var3) {
    }

    public void MesiboCall_OnAudioDeviceChanged(MesiboCall.CallProperties var1, MesiboCall.AudioDevice var2, MesiboCall.AudioDevice var3) {
    }

    public void MesiboCall_OnVideoSourceChanged(MesiboCall.CallProperties var1, int var2, int var3) {
    }

    public void MesiboCall_OnVideo(MesiboCall.CallProperties var1, MesiboCall.VideoProperties var2, boolean var3) {
    }

    public void MesiboCall_OnUpdateUserInterface(MesiboCall.CallProperties var1, int var2, boolean var3, boolean var4) {
    }

    public void MesiboCall_OnOrientationChanged(MesiboCall.CallProperties var1, boolean var2, boolean var3) {
    }

    public void MesiboCall_OnBatteryStatus(MesiboCall.CallProperties var1, boolean var2, boolean var3) {
    }

    public void MesiboCall_OnDTMF(MesiboCall.CallProperties var1, int var2) {
    }

    public void MesiboGroupcall_OnMute(MesiboCall.MesiboParticipant var1, boolean var2, boolean var3, boolean var4) {
    }

    public void MesiboGroupcall_OnHangup(MesiboCall.MesiboParticipant var1, int var2) {
    }

    public void MesiboGroupcall_OnConnected(MesiboCall.MesiboParticipant var1, boolean var2) {
    }

    public void MesiboGroupcall_OnTalking(MesiboCall.MesiboParticipant var1, boolean var2) {
    }

    public void MesiboGroupcall_OnVideoSourceChanged(MesiboCall.MesiboParticipant var1, int var2, int var3) {
    }

    public void MesiboGroupcall_OnVideo(MesiboCall.MesiboParticipant var1, float var2, boolean var3) {
    }

    public void MesiboGroupcall_OnAudio(MesiboCall.MesiboParticipant var1) {
    }

    public void MesiboVideoView_OnVideoResolutionChanged(int var1, int var2, int var3) {
    }

    public void MesiboVideoView_OnViewSizeChanged(int var1, int var2) {
    }
}

