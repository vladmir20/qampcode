package com.qamp.app.qampCalls;

import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboUtils;
import com.qamp.app.R;

public class QampDefaultCallFragment extends Fragment implements OnClickListener, MesiboCall.InProgressListener {
    public static final String TAG = "QampDefaultCallFragment";
    protected MesiboCall.Call mCall = null;
    protected MesiboCall.CallProperties mCp = null;
    protected QampCallActivity mActivity = null;
    private boolean mConnected = false;
    protected CallUserInterface ui = new CallUserInterface();
    private boolean callControlFragmentVisible = true;
    protected long autoHideVideoControlsTimeout = 7000L;
    private Thread mControlHidingThread = null;

    public QampDefaultCallFragment() {
    }

    public View onCreateView(LayoutInflater var1, ViewGroup var2, Bundle var3) {
        int var6 = R.layout.audio_call_fragmentt;
        if (this.mCp.ui.layout_id > 0) {
            var6 = this.mCp.ui.layout_id;
        }

        View var4;
        var4 = var1.inflate(var6, var2, false);
        //TextView var5 = (TextView)(var4).findViewById(R.id.title);//=========
        String var7;
        if (TextUtils.isEmpty(var7 = this.mCp.ui.title)) {
            var7 = Mesibo.getAppName();
        }

        //var5.setText(var7);
        ui.controlLayout = var4.findViewById(R.id.controler);
        ui.pipVideo = (MesiboVideoView) var4.findViewById(R.id.pip_video);
        ui.fullscreenVideo = (MesiboVideoView) var4.findViewById(R.id.full_screen_video);
        //ui.contactView = (TextView)var4.findViewById(R.id.call_name);//=========
        ui.status = var4.findViewById(R.id.call_stat);
        ui.speakerOff = var4.findViewById(R.id.one);
        ui.videoOff = var4.findViewById(R.id.two);
        ui.mute = var4.findViewById(R.id.four);
        ui.more = var4.findViewById(R.id.more);
        ui.name = var4.findViewById(R.id.name);
        ui.userName = var4.findViewById(R.id.user_name);
        ui.callDecline = var4.findViewById(R.id.three);
        ui.userImage = var4.findViewById(R.id.user_image);
        ui.profileImage = var4.findViewById(R.id.profile_image);
        ui.afterConnection = var4.findViewById(R.id.after_connected);
        ui.beforeConnection = var4.findViewById(R.id.while_connecting);
        ui.incomingView = var4.findViewById(R.id.incoming_call_container);
        ui.outgoingView = var4.findViewById(R.id.outgoing_call_container);
        ui.incomingVideoAcceptLayout = var4.findViewById(R.id.incoming_video_accept_container);
        ui.incomingAudioAcceptLayout = var4.findViewById(R.id.incoming_audio_accept_container);

        ui.status.setOnClickListener(this);
        ui.speakerOff.setOnClickListener(this);
        ui.videoOff.setOnClickListener(this);
        ui.mute.setOnClickListener(this);
        ui.more.setOnClickListener(this);


        /**ui.disconnectButton = (ImageButton)var4.findViewById(R.id.button_call_disconnect);//=========
         ui.cameraSwitchButton = (ImageButton)var4.findViewById(R.id.button_call_switch_camera);//=========
         ui.sourceSwitchButton = (ImageButton)var4.findViewById(R.id.button_call_switch_source);//=========
         ui.toggleSpeakerButton = (ImageButton)var4.findViewById(R.id.button_call_toggle_speaker);//=========
         ui.toggleCameraButton = (ImageButton)var4.findViewById(R.id.button_call_toggle_camera);//=========
         ui.toggleMuteButton = (ImageButton)var4.findViewById(R.id.button_call_toggle_mic);//=========
         ui.acceptButton = (ImageButton)var4.findViewById(R.id.incoming_call_connect);//=========
         ui.acceptAudioButton = (ImageButton)var4.findViewById(R.id.incoming_audio_call_connect);//=========
         ui.declineButton = (ImageButton)var4.findViewById(R.id.incoming_call_disconnect);//=========
         ui.cameraToggleLayout = var4.findViewById(R.id.layout_toggle_camera);//=========
         ui.sourceSwitchLayout = var4.findViewById(R.id.layout_switch_source);//=========
         ui.cameraSwitchLayout = var4.findViewById(R.id.layout_switch_camera);//=========
         ui.incomingView = var4.findViewById(R.id.incoming_call_container);//=========
         ui.inprogressView = var4.findViewById(R.id.outgoing_call_container);//=========
         ui.incomingAudioAcceptLayout = var4.findViewById(R.id.incoming_audio_accept_container);//=========
         ui.incomingVideoAcceptLayout = var4.findViewById(R.id.incoming_video_accept_container);//=========
         ui.remoteMute = (ImageView)var4.findViewById(R.id.remote_mute);//=========
         ui.remoteMute.setColorFilter(Color.argb(200, 200, 0, 0));
         ui.disconnectButton.setOnClickListener(this);
         ui.cameraSwitchButton.setOnClickListener(this);
         ui.sourceSwitchButton.setOnClickListener(this);
         ui.toggleSpeakerButton.setOnClickListener(this);
         ui.toggleCameraButton.setOnClickListener(this);
         ui.toggleMuteButton.setOnClickListener(this);
         ui.acceptButton.setOnClickListener(this);
         ui.acceptAudioButton.setOnClickListener(this);
         ui.declineButton.setOnClickListener(this);**/
        if (null != ui.pipVideo) {
            ui.pipVideo.setOnClickListener(this);
            ui.pipVideo.enablePip(true);
            ui.pipVideo.setOnTouchListener(new ViewTouchListener());
        }
        //ui.sourceSwitchLayout = var4.findViewById(R.id.layout_switch_source);
        if (null != ui.fullscreenVideo) {
            ui.fullscreenVideo.setOnClickListener(this);
            ui.fullscreenVideo.scaleToFill(true);
            ui.fullscreenVideo.enableHardwareScaler(false);
        }

        //ui.background = (ImageView)var4.findViewById(R.id.profile_image);
        //if (!this.mCp.ui.showScreenSharing) {
        //  ui.sourceSwitchLayout.setVisibility(View.GONE);
        //}

        //ui.thumbnailLayout = var4.findViewById(R.id.photo_layout);//=========
        //var5 = (TextView)var4.findViewById(R.id.textView2);//=========
        //ImageView var8 = (ImageView)var4.findViewById(R.id.photo_image);//=========
        Log.e("testing to be here", "hi adi");
        ui.name.setText("aditya");
        this.setUserDetails(ui.name, ui.userImage);
        ui.name.setText("aditya");
        this.setStatusView(0);
        this.setSwappedFeeds(this.mCall.isVideoViewsSwapped());
        ui.pipVideo.setVisibility(this.mCall.isAnswered() ? View.VISIBLE : View.GONE);
        this.mCall.start((QampCallActivity) getActivity(), this);
        return var1.inflate(var6, var2, false);
    }

    void answer(boolean var1) {
        if (!this.mCall.isAnswered() && this.mCall.isIncoming()) {
            this.mCall.answer(var1);
            this.setSwappedFeeds(false);
            if (this.mCall.isVideoCall() && var1) {
                ui.pipVideo.setVisibility(View.VISIBLE);
            }

        }
    }

    private void setSwappedFeeds(boolean var1) {
        if (this.mCall.isVideoCall()) {
            this.mCall.setVideoViewsSwapped(var1);
            this.mCall.setVideoView(ui.fullscreenVideo, !var1);
            this.mCall.setVideoView(ui.pipVideo, var1);
        }
    }


    public void onClick(View var1) {
        int var2;
        if ((var2 = var1.getId()) == R.id.pip_video_view) {//=========
            // this.setSwappedFeeds(!this.mCall.isVideoViewsSwapped());
        } else if (var2 == R.id.fullscreen_video_view) {//=========
            //this.toggleCallControlsVisibility();
        } else if (var2 != R.id.incoming_call_disconnect && var2 != R.id.button_call_disconnectt) {//=========
            if (var2 == R.id.incoming_call_connect) {//=========
                this.answer(true);
            } else if (var2 == R.id.incoming_audio_call_connect) {//=========
                this.answer(false);
            } else if (var2 == R.id.button_call_toggle_speaker) {//=========
                this.mCall.toggleAudioDevice(MesiboCall.AudioDevice.SPEAKER);
            } else {
                boolean var3;
                if (var2 == R.id.button_call_toggle_mic) {//=========
                    var3 = this.mCall.toggleAudioMute();
                    //this.setButtonAlpha(ui.toggleMuteButton, var3);
                } else if (var2 == R.id.button_call_switch_cameraa) {//=========
                    this.mCall.switchCamera();
                } else if (var2 == R.id.button_call_switch_source) {//=========
                    this.mCall.switchSource();
                } else {
                    if (var2 == R.id.button_call_toggle_camera) {//=========
                        var3 = this.mCall.toggleVideoMute();
                        //this.setButtonAlpha(ui.toggleCameraButton, var3);
                    }

                }
            }
        } else {
            this.mCall.hangup();
            this.setStatusView(64);
            this.mActivity.delayedFinish(500L);
        }
    }


    public void setUserDetails(TextView var1, ImageView var2) {
        if (!TextUtils.isEmpty(this.mCp.user.getName())) {
            Log.e("userNameee", this.mCp.user.getName());
            //ui.name.refres
            ui.name.setText(this.mCp.user.getName());
            var1.setText(this.mCp.user.getName());
        } else {
            Log.e("userNameAdd", this.mCp.user.address);
            var1.setText(this.mCp.user.address);
        }

        if (var2 != null) {
            //var2.setImageDrawable(R.drawable.ic_camerarotate);
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
            ui.status.setFormat((String) null);
            ui.status.setText("");
            ui.status.setBase(this.mCall.getAnswerTime());
            ui.status.start();
        } else {
            ui.status.stop();
            ui.mStatusText = this.statusToString(var1, ui.mStatusText);
            ui.status.setText("nh ho rha");
        }
    }

    private String getStatusText(int var1, String var2) {
        return MesiboCall.getInstance().getStatusText(var1, var2);
    }

    public String statusToString(int var1, String var2) {
        switch (var1) {
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

    private void callConnected() {
        if (!this.mConnected) {
            this.mConnected = true;
            if (this.mCall.isVideoCall()) {
                ui.pipVideo.setVisibility(View.VISIBLE);
                this.setSwappedFeeds(false);
            }

        }
    }


    @Override
    public void MesiboCall_OnSetCall(QampCallActivity var1, MesiboCall.Call var2) {
        this.mActivity = var1;
        this.mCall = var2;
        this.mCp = this.mCall.getCallProperties();
        Log.e("Call properties", String.valueOf((MesiboCall.CallProperties)this.mCp));
        this.mCp.activity = var1;
        if (null != this.mCp.ui.inProgressListener) {
            this.mCp.ui.inProgressListener.MesiboCall_OnSetCall(var1, this.mCall);
        }
    }

    @Override
    public void MesiboCall_OnMute(MesiboCall.CallProperties var1, boolean var2, boolean var3, boolean var4) {
        if (null != this.mCp.ui.inProgressListener) {
            this.mCp.ui.inProgressListener.MesiboCall_OnMute(var1, var2, var3, var4);
        }

        if (var4) {
            //this.updateRemoteMuteButtons();
        } else {
            //this.setButtonAlpha(ui.toggleMuteButton, this.mCall.getMuteStatus(true, false, false));
            //this.setButtonAlpha(ui.toggleCameraButton, this.mCall.getMuteStatus(false, true, false));
        }
    }

    @Override
    public boolean MesiboCall_OnPlayInCallSound(MesiboCall.CallProperties var1, int var2, boolean var3) {
        if (!var3) {
            this.mCall.stopInCallSound();
            return true;
        } else {
            this.mCall.playInCallSound(this.mActivity.getApplicationContext(), var2 == 0 ? R.raw.mesibo_ring : R.raw.mesibo_busy, true);
            return true;
        }
    }

    @Override
    public void MesiboCall_OnHangup(MesiboCall.CallProperties var1, int var2) {
        if (null != this.mCp.ui.inProgressListener) {
            this.mCp.ui.inProgressListener.MesiboCall_OnHangup(var1, var2);
        }
    }

    @Override
    public void MesiboCall_OnStatus(MesiboCall.CallProperties var1, int var2, boolean var3) {
        if (null != this.mCp.ui.inProgressListener) {
            this.mCp.ui.inProgressListener.MesiboCall_OnStatus(var1, var2, var3);
        }

        this.setStatusView(var2);
        if ((var2 & 64) > 0) {
            this.mActivity.delayedFinish(3000L);
        } else {
            switch (var2) {
                case 48:
                    this.callConnected();
                default:
            }
        }
    }

    @Override
    public void MesiboCall_OnAudioDeviceChanged(MesiboCall.CallProperties var1, MesiboCall.AudioDevice var2, MesiboCall.AudioDevice var3) {

    }

    @Override
    public void MesiboCall_OnVideoSourceChanged(MesiboCall.CallProperties var1, int var2, int var3) {

    }

    @Override
    public void MesiboCall_OnVideo(MesiboCall.CallProperties var1, MesiboCall.VideoProperties var2, boolean var3) {

    }

    @Override
    public void MesiboCall_OnUpdateUserInterface(MesiboCall.CallProperties var1, int var2, boolean var3, boolean var4) {


        if (var2 == 3) {
            if (var4 || this.mCp.ui.autoHideControls) {
                this.setCallControlsVisibility(var4, false);
            }
        } else {
            boolean var5 = var2 == 1;
            ui.incomingView.setVisibility(var5 ? View.VISIBLE : View.GONE);
            ui.outgoingView.setVisibility(var5 ? View.GONE : View.VISIBLE);
            var2 = var3 ? View.GONE : View.VISIBLE;
            int var7 = var3 ? View.VISIBLE : View.GONE;
            int var6 = var5 && var3 ? View.VISIBLE : View.GONE;
            ui.profileImage.setVisibility(var2);
            if (!var3) {
                ui.profileImage.setImageBitmap(this.mCp.ui.userImage);
            }

            ui.pipVideo.setVisibility(var7);
            ui.fullscreenVideo.setVisibility(var7);
            //ui.cameraToggleLayout.setVisibility(var7);
            //ui.cameraSwitchLayout.setVisibility(var7);
            if (this.mCp.ui.showScreenSharing) {
                //ui.sourceSwitchLayout.setVisibility(var7);
            }

            //ui.thumbnailLayout.setVisibility(var7);
            ui.userImage.setVisibility(var7);
            ui.incomingVideoAcceptLayout.setVisibility(var6);


            //boolean showIncoming = (state == MESIBOCALL_UI_STATE_SHOWINCOMING);
            /**boolean showIncoming = var2 == 1;

             ui.incomingView.setVisibility(showIncoming?View.VISIBLE:View.GONE);
             ui.inprogressView.setVisibility(showIncoming?View.GONE:View.VISIBLE);

             // audio call controls visibility
             int acVisibility = video?View.GONE:View.VISIBLE;
             // video call controls visibility
             int vcVisibility = video?View.VISIBLE:View.GONE;
             int vciVisibility = (showIncoming && video)?View.VISIBLE:View.GONE;

             //audio controls
             ui.background.setVisibility(acVisibility);
             if(!video)
             ui.background.setImageBitmap(mCp.ui.userImage);

             //video controls
             ui.pipVideo.setVisibility(vcVisibility);
             ui.fullscreenVideo.setVisibility(vcVisibility);
             ui.cameraToggleLayout.setVisibility(vcVisibility);
             ui.cameraSwitchLayout.setVisibility(vcVisibility);
             if(mCp.ui.showScreenSharing)
             ui.sourceSwitchLayout.setVisibility(vcVisibility);
             ui.thumbnailLayout.setVisibility(vcVisibility);
             ui.incomingVideoAcceptLayout.setVisibility(vciVisibility);**/
        }
    }

    @Override
    public void MesiboCall_OnOrientationChanged(MesiboCall.CallProperties var1, boolean var2, boolean var3) {

    }

    @Override
    public void MesiboCall_OnBatteryStatus(MesiboCall.CallProperties var1, boolean var2, boolean var3) {

    }

    @Override
    public void MesiboCall_OnDTMF(MesiboCall.CallProperties var1, int var2) {

    }

    protected void setCallControlsVisibility(boolean var1, boolean var2) {
        if (this.mCall.isVideoCall()) {
            try {
                this.callControlFragmentVisible = var1;
                ui.controlLayout.setVisibility(var1 ? View.VISIBLE : View.GONE);
                if (var2 && var1 && this.autoHideVideoControlsTimeout > 0L) {
                    this.triggerDelayedAutoHideControls();
                }

            } catch (Exception var3) {
            }
        }
        ui.controlLayout.setVisibility(var1 ? View.VISIBLE : View.GONE);
    }

    protected void triggerDelayedAutoHideControls() {
        if (this.mControlHidingThread != null) {
            this.mControlHidingThread.interrupt();
        }

        this.mControlHidingThread = new Thread(new Runnable() {
            public void run() {
                SystemClock.sleep(QampDefaultCallFragment.this.autoHideVideoControlsTimeout);
                if (!Thread.currentThread().isInterrupted()) {
                    QampDefaultCallFragment.this.setCallControlsVisibility(false, false);
                }
            }
        });
        this.mControlHidingThread.start();
    }


    public static class CallUserInterface {

        public MesiboVideoView pipVideo;
        public MesiboVideoView fullscreenVideo;
        public View controlLayout;
        public TextView name;
        public Chronometer status;
        public TextView userName;
        public ImageView mute;
        public ImageView more;
        public ImageView videoOff;
        public ImageView callDecline;
        public ImageView speakerOff;
        public ImageView userImage;
        public ImageView profileImage;
        public View beforeConnection;
        public View afterConnection;
        public View incomingView;
        public View outgoingView;
        public View incomingVideoAcceptLayout;
        public View incomingAudioAcceptLayout;
        public String mStatusText = "";

        /**public Chronometer status = null;
        public MesiboVideoView pipVideo;
        public MesiboVideoView fullscreenVideo;
        public TextView contactView;
        public ImageButton cameraSwitchButton;
        public ImageButton sourceSwitchButton;
        public ImageButton toggleCameraButton;
        public ImageButton toggleMuteButton;
        public ImageButton toggleSpeakerButton;
        public ImageView remoteMute;
        public ImageButton acceptButton;
        public ImageButton acceptAudioButton;
        public ImageButton declineButton;
        public ImageButton disconnectButton;
        public ImageView background;
        public View incomingView;
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
        public int buttonAlphaOn = 255;**/

        public CallUserInterface() {
        }
    }
}
