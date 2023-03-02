package com.qamp.app.qampCalls;

import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboUtils;
import com.mesibo.calls.api.MesiboCall;
import com.mesibo.calls.api.ui.ViewTouchListener;
import com.qamp.app.R;


public class QampMesiboDefaultFragment extends Fragment implements View.OnClickListener,  com.qamp.app.qampCalls.MesiboCall.InProgressListener {
    public static final String TAG = "QampMesiboDefaultFragment";
    protected com.qamp.app.qampCalls.MesiboCall.Call mCall = null;
    protected com.qamp.app.qampCalls.MesiboCall.CallProperties mCp = null;
    protected QampCallActivity mActivity = null;
    boolean speakerFirst = true;
    boolean muteFirst = true;
    private boolean mConnected = false;
    protected CallUserInterface ui = new CallUserInterface();
    private boolean callControlFragmentVisible = true;
    protected long autoHideVideoControlsTimeout = 7000L;
    private Thread mControlHidingThread = null;

    public QampMesiboDefaultFragment() {
    }

    public View onCreateView(LayoutInflater var1, ViewGroup var2, Bundle var3) {
        int var6 = R.layout.mesibo_qamp_call;
        if (this.mCp.ui.layout_id > 0) {
            var6 = this.mCp.ui.layout_id;
        }

        View var4;
        TextView var5 = (TextView)(var4 = var1.inflate(var6, var2, false)).findViewById(R.id.titlee);
        String var7;
        if (TextUtils.isEmpty(var7 = this.mCp.ui.title)) {
            var7 = Mesibo.getAppName();
        }

        //var5.setText(var7);
        this.ui.backgroundRelative = var4 .findViewById(R.id.background);
        this.ui.controlLayout = var4.findViewById(R.id.control_container);
        this.ui.speaker = (ImageView) var4.findViewById(R.id.one);
        this.ui.mute = (ImageView) var4.findViewById(R.id.four);
        this.ui.switchS = (ImageView) var4.findViewById(R.id.two);
        ui.pipVideo =  (MesiboVideoView) var4.findViewById(R.id.pip_video_view);
        ui.fullscreenVideo =  (MesiboVideoView)var4.findViewById(R.id.fullscreen_video_view);
        this.ui.contactView = (TextView)var4.findViewById(R.id.call_name);
        this.ui.status = (Chronometer)var4.findViewById(R.id.call_status);
        this.ui.switchSource = (ImageView) var4.findViewById(R.id.switch_source);
        this.ui.disconnectButton = (ImageView) var4.findViewById(R.id.button_call_disconnectt);
        this.ui.buttonDisconnect = (ImageView) var4.findViewById(R.id.three);
        this.ui.cameraSwitchButton = (ImageView)var4.findViewById(R.id.button_call_switch_cameraa);
        this.ui.sourceSwitchButton = (ImageView)var4.findViewById(R.id.button_call_switch_source);
        this.ui.toggleSpeakerButton = (ImageView) var4.findViewById(R.id.button_call_toggle_speaker);
        this.ui.toggleCameraButton = (ImageView) var4.findViewById(R.id.button_call_toggle_camera);
        this.ui.toggleMuteButton = (ImageView) var4.findViewById(R.id.button_call_toggle_mic);
        this.ui.acceptButton = (ImageView) var4.findViewById(R.id.incoming_call_connect);
        this.ui.acceptAudioButton = (ImageView) var4.findViewById(R.id.incoming_audio_call_connect);
        this.ui.declineButton = (ImageView) var4.findViewById(R.id.incoming_call_disconnect);
        this.ui.cameraToggleLayout = var4.findViewById(R.id.layout_toggle_camera);
        this.ui.sourceSwitchLayout = var4.findViewById(R.id.layout_switch_source);
        this.ui.cameraSwitchLayout = var4.findViewById(R.id.layout_switch_camera);
        this.ui.incomingView = var4.findViewById(R.id.incoming_call_container);
        this.ui.inprogressView = var4.findViewById(R.id.outgoing_call_container);
        this.ui.incomingAudioAcceptLayout = var4.findViewById(R.id.incoming_audio_accept_container);
        this.ui.incomingVideoAcceptLayout = var4.findViewById(R.id.incoming_video_accept_container);
        this.ui.star = (ImageView)var4.findViewById(R.id.switch_source);
        this.ui.remoteMute = (ImageView)var4.findViewById(R.id.remote_mute);
        //this.ui.remoteMute.setColorFilter(Color.argb(200, 200, 0, 0));
        this.ui.disconnectButton.setOnClickListener(this);
        this.ui.buttonDisconnect.setOnClickListener(this);
        this.ui.cameraSwitchButton.setOnClickListener(this);
        this.ui.sourceSwitchButton.setOnClickListener(this);
        this.ui.toggleSpeakerButton.setOnClickListener(this);
        this.ui.toggleCameraButton.setOnClickListener(this);
        this.ui.switchSource.setOnClickListener(this);
        this.ui.star.setOnClickListener(this);
        this.ui.speaker.setOnClickListener(this);
        this.ui.switchS.setOnClickListener(this);
        this.ui.mute.setOnClickListener(this);
        this.ui.toggleMuteButton.setOnClickListener(this);
        this.ui.acceptButton.setOnClickListener(this);
        this.ui.acceptAudioButton.setOnClickListener(this);
        this.ui.declineButton.setOnClickListener(this);
        if (null != this.ui.pipVideo) {

            this.ui.pipVideo.setOnClickListener(this);
            this.ui.pipVideo.enablePip(true);
            this.ui.pipVideo.setOnTouchListener(new ViewTouchListener());
        }

        if (null != this.ui.fullscreenVideo) {
            this.ui.fullscreenVideo.setOnClickListener(this);
            this.ui.fullscreenVideo.scaleToFill(true);
            this.ui.fullscreenVideo.enableHardwareScaler(false);
        }


        this.ui.background = (ImageView)var4.findViewById(R.id.userImage);
        if (!this.mCp.ui.showScreenSharing) {
            this.ui.sourceSwitchLayout.setVisibility(View.GONE);
        }

        this.ui.thumbnailLayout = var4.findViewById(R.id.photo_layout);
        var5 = (TextView)var4.findViewById(R.id.call_name);
        ImageView var8 = (ImageView)var4.findViewById(R.id.photo_image);
        this.setUserDetails(var5, var8);
        this.setStatusView(View.VISIBLE);
        this.setSwappedFeeds(this.mCall.isVideoViewsSwapped());
        this.ui.pipVideo.setVisibility(this.mCall.isAnswered() ? View.VISIBLE : View.GONE);
        this.mCall.start((QampCallActivity) this.getActivity(), this);
        return var4;
    }

    void answer(boolean var1) {
        if (!this.mCall.isAnswered() && this.mCall.isIncoming()) {
            this.mCall.answer(var1);
            this.setSwappedFeeds(false);
            if (this.mCall.isVideoCall() && var1) {
                this.ui.pipVideo.setVisibility(View.VISIBLE);
            }

        }
    }

    public void onClick(View var1) {
        int var2;
        if ((var2 = var1.getId()) == R.id.pip_video_view) {
            this.setSwappedFeeds(!this.mCall.isVideoViewsSwapped());
        } else if (var2 == R.id.fullscreen_video_view) {
            this.toggleCallControlsVisibility();
        } else if (var2 != R.id.incoming_call_disconnect && var2 != R.id.three) {
            if (var2 == R.id.incoming_call_connect) {
                this.answer(true);
            } else if (var2 == R.id.incoming_audio_call_connect) {
                this.answer(false);
            } else if (var2 == R.id.one) {
                this.mCall.toggleAudioDevice(com.qamp.app.qampCalls.MesiboCall.AudioDevice.SPEAKER);
                if(speakerFirst == true){
                    this.ui.speaker.setImageResource(R.drawable.ic_attach);
                    speakerFirst = false;
                }else {
                    this.ui.speaker.setImageResource(R.drawable.ic_speakerhigh);
                    speakerFirst = true;}

            }

            else {
                boolean var3;
                if (var2 == R.id.four) {
                    var3 = this.mCall.toggleAudioMute();
                    if(muteFirst == true){
                        this.ui.mute.setImageResource(R.drawable.ic_microphone);
                        muteFirst = false;
                    }else {
                        this.ui.mute.setImageResource(R.drawable.ic_microphone_off);
                        muteFirst = true;
                    }

                    this.setButtonAlpha(this.ui.toggleMuteButton, var3);
                }
                else if (var2 == R.id.switch_source) {
                    this.mCall.switchCamera();
                } else if (var2 == R.id.two) {
                    this.mCall.switchSource();
                } else {
                    if (var2 == R.id.swipe_to_button) {
                        var3 = this.mCall.toggleVideoMute();
                        this.setButtonAlpha(this.ui.toggleCameraButton, var3);
                    }

                }
            }
        } else {
            this.mCall.hangup();
            this.setStatusView(64);
            this.mActivity.delayedFinish(500L);
        }
    }

    private void setSwappedFeeds(boolean var1) {
        if (this.mCall.isVideoCall()) {
            this.mCall.setVideoViewsSwapped(var1);
            this.mCall.setVideoView(this.ui.fullscreenVideo, !var1);
            this.mCall.setVideoView(this.ui.pipVideo, var1);
        }
    }

    public void setUserDetails(TextView var1, ImageView var2) {
        if (!TextUtils.isEmpty(this.mCp.user.getName())) {
            var1.setText(this.mCp.user.getName());
        } else {
            var1.setText(this.mCp.user.address);
        }

        if (var2 != null) {
            var2.setImageDrawable(MesiboUtils.getRoundImageDrawable(this.mCp.ui.userImageSmall));
        }

    }

    public void onResume() {
        super.onResume();
        if (this.mCp.autoAnswer) {
            this.answer(this.mCall.isVideoCall());
        }

    }

    public void setStatusView(int var1) {
        if (this.mCall.isAnswered() && this.mCall.isCallInProgress() && this.mCall.isCallConnected()) {
            this.ui.status.setFormat((String)null);
            this.ui.status.setText("");
            this.ui.status.setBase(this.mCall.getAnswerTime());
            this.ui.status.start();
        } else {
            this.ui.status.stop();
            this.ui.mStatusText = this.statusToString(var1, this.ui.mStatusText);
            this.ui.status.setText(this.ui.mStatusText);
        }
    }

    public void updateRemoteMuteButtons() {
        boolean var1 = this.mCall.getMuteStatus(true, false, true);
        boolean var2 = this.mCall.getMuteStatus(false, true, true);
        if (!var1 && !var2) {
            this.ui.remoteMute.setVisibility(View.GONE);
        } else {
           // int var3 = drawable.ic_mesibo_mic_off;
            if (var2) {
                //var3 = drawable.ic_mesibo_videocam_off;
            }

            if (var2 && var1) {
                //var3 = drawable.ic_mesibo_tv_off;
            }

            //this.ui.remoteMute.setImageResource(var3);
            this.ui.remoteMute.setVisibility(View.VISIBLE);
        }
    }

    private void callConnected() {
        if (!this.mConnected) {
            this.mConnected = true;
            if (this.mCall.isVideoCall()) {
                this.ui.pipVideo.setVisibility(View.VISIBLE);
                this.setSwappedFeeds(false);
            }

        }
    }

    public void MesiboCall_OnVideoSourceChanged(MesiboCall.CallProperties var1, int var2, int var3) {
        this.setButtonAlpha(this.ui.sourceSwitchButton, var2 == 4);
    }

    public void MesiboCall_OnVideo(MesiboCall.CallProperties var1, MesiboCall.VideoProperties var2, boolean var3) {
    }

    /**public void MesiboCall_OnUpdateUserInterface(com.mesibo.calls.api.MesiboCall.CallProperties var1, int var2, boolean var3, boolean var4) {
        if (var2 == 3) {
            if (var4 || this.mCp.ui.autoHideControls) {
                this.setCallControlsVisibility(var4, false);
            }
        } else {
            boolean var5 = var2 == 1;
            this.ui.incomingView.setVisibility(var5 ? View.VISIBLE : View.GONE);
            this.ui.inprogressView.setVisibility(var5 ? View.GONE : View.VISIBLE);
            var2 = var3 ? View.GONE : View.VISIBLE;
            int var7 = var3 ? View.VISIBLE : View.GONE;
            int var6 = var5 && var3 ? View.VISIBLE : View.GONE;
            this.ui.background.setVisibility(var2);
            if (!var3) {
                this.ui.background.setImageBitmap(this.mCp.ui.userImage);
            }

            this.ui.pipVideo.setVisibility(var7);
            this.ui.fullscreenVideo.setVisibility(var7);
            this.ui.cameraToggleLayout.setVisibility(var7);
            this.ui.cameraSwitchLayout.setVisibility(var7);
            if (this.mCp.ui.showScreenSharing) {
                this.ui.sourceSwitchLayout.setVisibility(var7);
            }

            this.ui.thumbnailLayout.setVisibility(var7);
            this.ui.incomingVideoAcceptLayout.setVisibility(var6);
        }
    }**/

    public void MesiboCall_OnStatus(com.qamp.app.qampCalls.MesiboCall.CallProperties var1, int var2, boolean var3) {
        if (null != this.mCp.ui.inProgressListener) {
            this.mCp.ui.inProgressListener.MesiboCall_OnStatus(var1, var2, var3);
        }

        this.setStatusView(var2);
        if ((var2 & 64) > 0) {
            this.mActivity.delayedFinish(3000L);
        } else {
            switch(var2) {
                case 48:
                    this.callConnected();
                default:
            }
        }
    }

    protected void setButtonAlpha(ImageView var1, boolean var2) {
        var1.setAlpha((float)(var2 ? this.ui.buttonAlphaOn : this.ui.buttonAlphaOff) / 255.0F);
    }


    public void MesiboCall_OnMute(com.qamp.app.qampCalls.MesiboCall.CallProperties var1, boolean var2, boolean var3, boolean var4) {
        if (null != this.mCp.ui.inProgressListener) {
            this.mCp.ui.inProgressListener.MesiboCall_OnMute(var1, var2, var3, var4);
        }

        if (var4) {
            this.updateRemoteMuteButtons();
        } else {
            this.setButtonAlpha(this.ui.toggleMuteButton, this.mCall.getMuteStatus(true, false, false));
            this.setButtonAlpha(this.ui.toggleCameraButton, this.mCall.getMuteStatus(false, true, false));
        }
    }

    public boolean MesiboCall_OnPlayInCallSound(MesiboCall.CallProperties var1, int var2, boolean var3) {
        if (!var3) {
            this.mCall.stopInCallSound();
            return true;
        } else {
            //this.mCall.playInCallSound(this.mActivity.getApplicationContext(), var2 == 0 ? raw.mesibo_ring : raw.mesibo_busy, true);
            return true;
        }
    }

    public void MesiboCall_OnHangup(com.qamp.app.qampCalls.MesiboCall.CallProperties var1, int var2) {
        if (null != this.mCp.ui.inProgressListener) {
            this.mCp.ui.inProgressListener.MesiboCall_OnHangup(var1, var2);
        }

    }


    private String getStatusText(int var1, String var2) {
        return MesiboCall.getInstance().getStatusText(var1, var2);
    }

    public String statusToString(int var1, String var2) {
        switch(var1) {
            case 0:
                var2 = this.getStatusText(var1, "Initiating Call");
                if (this.mCall.isIncoming()) {
                    var2 = this.getStatusText(1, "Incoming Call");
                }
                break;
            case 2:
                var2 = this.getStatusText(var1, "Calling");
                break;
            case 3:
                var2 = this.getStatusText(var1, "Ringing");
                break;
            case 5:
                if (!this.mCall.isLinkUp()) {
                    var2 = this.getStatusText(48, "Connecting");
                }
                break;
            case 11:
                if (this.mCall.isAnswered()) {
                    var2 = this.getStatusText(var1, "On Hold");
                }
                break;
            case 50:
                var2 = this.getStatusText(var1, "Reconnecting");
                break;
            case 64:
                var2 = this.getStatusText(var1, "Call Completed");
                break;
            case 66:
                var2 = this.getStatusText(var1, "No Answer");
                break;
            case 67:
                var2 = this.getStatusText(var1, "Busy");
                break;
            case 68:
                var2 = this.getStatusText(var1, "Not Reachable");
                break;
            case 70:
                var2 = this.getStatusText(var1, "Invalid Destination");
                break;
            case 72:
                var2 = this.getStatusText(var1, "Calls Not Supported");
                break;
            case 74:
                var2 = this.getStatusText(var1, "Not Allowed");
                break;
            case 98:
                var2 = this.getStatusText(var1, "Network Error");
        }

        return var2;
    }

    protected void toggleCallControlsVisibility() {
        if (this.mCall.isAnswered()) {
            this.setCallControlsVisibility(!this.callControlFragmentVisible, true);
        }
    }

    protected void setCallControlsVisibility(boolean var1, boolean var2) {
        if (this.mCall.isVideoCall()) {
            try {
                this.callControlFragmentVisible = var1;
                this.ui.controlLayout.setVisibility(var1 ? View.VISIBLE : View.GONE);
                if (var2 && var1 && this.autoHideVideoControlsTimeout > 0L) {
                    this.triggerDelayedAutoHideControls();
                }

            } catch (Exception var3) {
            }
        }
    }

    protected void triggerDelayedAutoHideControls() {
        if (this.mControlHidingThread != null) {
            this.mControlHidingThread.interrupt();
        }

        this.mControlHidingThread = new Thread(new Runnable() {
            public void run() {
                SystemClock.sleep(QampMesiboDefaultFragment.this.autoHideVideoControlsTimeout);
                if (!Thread.currentThread().isInterrupted()) {
                    QampMesiboDefaultFragment.this.setCallControlsVisibility(false, false);
                }
            }
        });
        this.mControlHidingThread.start();
    }

    @Override
    public void MesiboCall_OnSetCall(QampCallActivity var1, com.qamp.app.qampCalls.MesiboCall.Call var2) {
        this.mActivity = var1;
        this.mCall = var2;
        this.mCp = this.mCall.getCallProperties();
        this.mCp.activity = var1;
        if (null != this.mCp.ui.inProgressListener) {
            this.mCp.ui.inProgressListener.MesiboCall_OnSetCall(var1, this.mCall);
        }

    }


    @Override
    public boolean MesiboCall_OnPlayInCallSound(com.qamp.app.qampCalls.MesiboCall.CallProperties var1, int var2, boolean var3) {
        return false;
    }


    @Override
    public void MesiboCall_OnAudioDeviceChanged(com.qamp.app.qampCalls.MesiboCall.CallProperties var1, com.qamp.app.qampCalls.MesiboCall.AudioDevice var2, com.qamp.app.qampCalls.MesiboCall.AudioDevice var3) {
        this.setButtonAlpha(this.ui.toggleSpeakerButton, var2 == com.qamp.app.qampCalls.MesiboCall.AudioDevice.SPEAKER);
    }

    @Override
    public void MesiboCall_OnVideoSourceChanged(com.qamp.app.qampCalls.MesiboCall.CallProperties var1, int var2, int var3) {

    }

    @Override
    public void MesiboCall_OnVideo(com.qamp.app.qampCalls.MesiboCall.CallProperties var1, com.qamp.app.qampCalls.MesiboCall.VideoProperties var2, boolean var3) {

    }

    @Override
    public void MesiboCall_OnUpdateUserInterface(com.qamp.app.qampCalls.MesiboCall.CallProperties var1, int var2, boolean var3, boolean var4) {
        if (var2 == 3) {
            if (var4 || this.mCp.ui.autoHideControls) {
                this.setCallControlsVisibility(var4, false);
            }
        } else {
            Log.e("Video Prop",var1.video.toString());
            boolean var5 = var2 == 1;
            this.ui.incomingView.setVisibility(var5 ? View.VISIBLE : View.GONE);
            this.ui.inprogressView.setVisibility(var5 ? View.GONE : View.VISIBLE);
            var2 = var3 ? View.GONE : View.VISIBLE;
            int var7 = var3 ? View.VISIBLE : View.GONE;
            int var6 = var5 && var3 ? View.VISIBLE : View.GONE;
            this.ui.background.setVisibility(View.GONE);
            //this.ui.speaker.setVisibility(View.GONE);
            if(var1.video != null){
                //this.ui.speaker.setVisibility(View.GONE);
                this.ui.star.setVisibility(View.VISIBLE);

            }
            //this.ui.star.setVisibility(View.GONE);
            if (!var3) {
                this.ui.background.setImageBitmap(this.mCp.ui.userImage);
            }

            this.ui.pipVideo.setVisibility(var7);
            this.ui.fullscreenVideo.setVisibility(var7);
            this.ui.cameraToggleLayout.setVisibility(var7);
            this.ui.cameraSwitchLayout.setVisibility(var7);
            if (this.mCp.ui.showScreenSharing) {
                this.ui.sourceSwitchLayout.setVisibility(var7);
            }

            this.ui.thumbnailLayout.setVisibility(View.VISIBLE);
            //this.ui.thumbnailLayout.setVisibility(var7);
            this.ui.incomingVideoAcceptLayout.setVisibility(var6);
        }
    }

    @Override
    public void MesiboCall_OnOrientationChanged(com.qamp.app.qampCalls.MesiboCall.CallProperties var1, boolean var2, boolean var3) {

    }

    @Override
    public void MesiboCall_OnBatteryStatus(com.qamp.app.qampCalls.MesiboCall.CallProperties var1, boolean var2, boolean var3) {

    }

    @Override
    public void MesiboCall_OnDTMF(com.qamp.app.qampCalls.MesiboCall.CallProperties var1, int var2) {

    }

    /**public void MesiboCall_OnSetCall(QampDefaultCallActivity qampDefaultCallActivity, com.qamp.app.qampCalls.MesiboCall.Call mCall) {
        this.mActivity = qampDefaultCallActivity;
        this.mCall = var2;
        this.mCp = this.mCall.getCallProperties();
        this.mCp.activity = var1;
        if (null != this.mCp.ui.inProgressListener) {
            this.mCp.ui.inProgressListener.MesiboCall_OnSetCall(var1, this.mCall);
        }

    }**/

    public static class CallUserInterface {
        public Chronometer status = null;
        public MesiboVideoView pipVideo;
        public MesiboVideoView fullscreenVideo;
        public TextView contactView;
        public ImageView cameraSwitchButton;
        public ImageView sourceSwitchButton;
        public ImageView toggleCameraButton;
        public ImageView toggleMuteButton;
        public ImageView toggleSpeakerButton;
        public ImageView remoteMute;
        public ImageView acceptButton;
        public ImageView acceptAudioButton;
        public ImageView declineButton;
        public ImageView switchSource;
        public ImageView speaker;
        public ImageView mute;
        public ImageView disconnectButton;
        public ImageView buttonDisconnect;
        public ImageView background;
        public ImageView star;
        public ImageView switchS;
        public View incomingView;
        public View backgroundRelative;
        public View inprogressView;
        public View controlLayout;
        public View cameraToggleLayout;
        public View cameraSwitchLayout;
        public View thumbnailLayout;
        public View sourceSwitchLayout;
        public View incomingVideoAcceptLayout;
        public View incomingAudioAcceptLayout;
        public String mStatusText = "";
        public int buttonAlphaOff = 127;
        public int buttonAlphaMid = 200;
        public int buttonAlphaOn = 255;

        public CallUserInterface() {
        }
    }}