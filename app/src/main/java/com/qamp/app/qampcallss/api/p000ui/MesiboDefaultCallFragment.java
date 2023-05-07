/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:40 AM
 *
 */

package com.qamp.app.qampcallss.api.p000ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

import com.bumptech.glide.load.ImageHeaderParser;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboUtils;
import com.mesibo.calls.api.R.raw;
import com.mesibo.calls.api.ui.ViewTouchListener;
import com.qamp.app.R;
import com.qamp.app.R.drawable;
import com.qamp.app.qampcallss.api.MesiboCall;
import com.qamp.app.qampcallss.api.MesiboCall.AudioDevice;
import com.qamp.app.qampcallss.api.MesiboCall.Call;
import com.qamp.app.qampcallss.api.MesiboCall.CallProperties;
import com.qamp.app.qampcallss.api.MesiboCall.InProgressListener;
import com.qamp.app.qampcallss.api.MesiboCall.VideoProperties;
import com.qamp.app.qampcallss.api.MesiboVideoView;
import com.qamp.app.qampcallss.api.Utils;

public class MesiboDefaultCallFragment extends Fragment implements OnClickListener, InProgressListener {
    public static final String TAG = "MesiboDefaultCallFragment";
    protected Call mCall = null;
    protected CallProperties mCp = null;
    protected QampCallsActivity mActivity = null;
    private boolean mConnected = false;
    boolean speakerFirst = true;
    boolean muteFirst = true;
    protected CallUserInterface ui = new CallUserInterface();
    private boolean callControlFragmentVisible = true;
    protected long autoHideVideoControlsTimeout = 7000L;
    private Thread mControlHidingThread = null;

    public MesiboDefaultCallFragment() {
    }

    public View onCreateView(LayoutInflater var1, ViewGroup var2, Bundle var3) {
       /** //int var6 = layout.fragment_mesibocall;
        int var6 = R.layout.audio_call_fragmentt;
        if (this.mCp.f2ui.layout_id > 0) {
            var6 = this.mCp.f2ui.layout_id;
        }

        View var4;
        TextView var5 = (TextView)(var4 = var1.inflate(var6, var2, false)).findViewById(id.title);
        String var7;
        if (TextUtils.isEmpty(var7 = this.mCp.f2ui.title)) {
            var7 = Mesibo.getAppName();
        }

        var5.setText(var7);
        this.ui.controlLayout = var4.findViewById(id.control_container);
        this.ui.backgroundView = var4.findViewById(id.renderer_container);
        this.ui.pipVideo = (MesiboVideoView)var4.findViewById(id.pip_video_view);
        this.ui.fullscreenVideo = (MesiboVideoView)var4.findViewById(id.fullscreen_video_view);
        this.ui.contactView = (TextView)var4.findViewById(id.call_name);
        this.ui.status = (Chronometer)var4.findViewById(id.call_status);
        this.ui.disconnectButton = (ImageButton)var4.findViewById(id.button_call_disconnect);
        this.ui.cameraSwitchButton = (ImageButton)var4.findViewById(id.button_call_switch_camera);
        this.ui.sourceSwitchButton = (ImageButton)var4.findViewById(id.button_call_switch_source);
        this.ui.toggleSpeakerButton = (ImageButton)var4.findViewById(id.button_call_toggle_speaker);
        this.ui.toggleCameraButton = (ImageButton)var4.findViewById(id.button_call_toggle_camera);
        this.ui.toggleMuteButton = (ImageButton)var4.findViewById(id.button_call_toggle_mic);
        this.ui.acceptButton = (ImageButton)var4.findViewById(id.incoming_call_connect);
        this.ui.acceptAudioButton = (ImageButton)var4.findViewById(id.incoming_audio_call_connect);
        this.ui.declineButton = (ImageButton)var4.findViewById(id.incoming_call_disconnect);
        this.ui.cameraToggleLayout = var4.findViewById(id.layout_toggle_camera);
        this.ui.sourceSwitchLayout = var4.findViewById(id.layout_switch_source);
        this.ui.cameraSwitchLayout = var4.findViewById(id.layout_switch_camera);
        this.ui.incomingView = var4.findViewById(id.incoming_call_container);
        this.ui.inprogressView = var4.findViewById(id.outgoing_call_container);
        this.ui.incomingAudioAcceptLayout = var4.findViewById(id.incoming_audio_accept_container);
        this.ui.incomingVideoAcceptLayout = var4.findViewById(id.incoming_video_accept_container);
        this.ui.remoteMute = (ImageView)var4.findViewById(id.remote_mute);
        this.ui.remoteMute.setColorFilter(Color.argb(200, 200, 0, 0));
        this.ui.disconnectButton.setOnClickListener(this);
        this.ui.cameraSwitchButton.setOnClickListener(this);
        this.ui.sourceSwitchButton.setOnClickListener(this);
        this.ui.toggleSpeakerButton.setOnClickListener(this);
        this.ui.toggleCameraButton.setOnClickListener(this);
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

        this.ui.background = (ImageView)var4.findViewById(id.userImage);
        if (!this.mCp.f2ui.showScreenSharing) {
            this.ui.sourceSwitchLayout.setVisibility(View.GONE);
        }

        this.ui.thumbnailLayout = var4.findViewById(id.photo_layout);
        var5 = (TextView)var4.findViewById(id.call_name);
        ImageView var8 = (ImageView)var4.findViewById(id.photo_image);
        this.setUserDetails(var5, var8);
        this.setStatusView(0);
        this.setSwappedFeeds(this.mCall.isVideoViewsSwapped());
        this.ui.pipVideo.setVisibility(this.mCall.isAnswered() ? View.VISIBLE : View.GONE);
        if (this.mCp.f2ui.backgroundColor != 0) {
            this.ui.backgroundView.setBackgroundColor(this.mCp.f2ui.backgroundColor);
        }

        this.mCall.start((QampCallsActivity) this.getActivity(), this);
        return var4;*/

        int var6 = R.layout.mesibo_qamp_call;
        if (this.mCp.f2ui.layout_id > 0) {
            var6 = this.mCp.f2ui.layout_id;
        }

        View var4;
        TextView var5 = (TextView)(var4 = var1.inflate(var6, var2, false)).findViewById(R.id.titlee);
        String var7;
        if (TextUtils.isEmpty(var7 = this.mCp.f2ui.title)) {
            var7 = Mesibo.getAppName();
        }

        //var5.setText(var7);
        this.ui.backgroundRelative = var4 .findViewById(R.id.background);
        this.ui.callHold = var4.findViewById(R.id.textView23);
        this.ui.controlLayout = var4.findViewById(R.id.control_container);
        this.ui.contactOngoinName = var4.findViewById(R.id.textView4);
        this.ui.backArrow = (ImageView) var4.findViewById(R.id.imageView4);
        this.ui.chatOngoing = (ImageView) var4.findViewById(R.id.imageView5);
        this.ui.speaker = (ImageView) var4.findViewById(R.id.one);
        this.ui.mute = (ImageView) var4.findViewById(R.id.four);
        this.ui.switchS = (ImageView) var4.findViewById(R.id.two);
        ui.pipVideo =  (MesiboVideoView) var4.findViewById(R.id.pip_video_view);
        ui.fullscreenVideo =  (MesiboVideoView)var4.findViewById(R.id.fullscreen_video_view);
        this.ui.contactView = (TextView)var4.findViewById(R.id.call_name);
        this.ui.status = (Chronometer)var4.findViewById(R.id.call_status);
        //this.ui.switchSource = (ImageView) var4.findViewById(R.id.switch_source);
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
        //this.ui.switchSource.setOnClickListener(this);
        this.ui.star.setOnClickListener(this);
        this.ui.speaker.setOnClickListener(this);
        this.ui.switchS.setOnClickListener(this);
        this.ui.mute.setOnClickListener(this);
        this.ui.toggleMuteButton.setOnClickListener(this);
        this.ui.acceptButton.setOnClickListener(this);
        this.ui.acceptAudioButton.setOnClickListener(this);
        this.ui.declineButton.setOnClickListener(this);
        Log.e("dc Vs rcb", String.valueOf(this.mCall.isAnswered()));
        this.ui.incomingView.setVisibility(View.INVISIBLE);
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


        this.ui.background = (ImageView)var4.findViewById(R.id.onGoing_image);
        if (!this.mCp.f2ui.showScreenSharing) {
            this.ui.sourceSwitchLayout.setVisibility(View.GONE);
        }


        this.ui.thumbnailLayout = var4.findViewById(R.id.photo_layout);
        var5 = (TextView)var4.findViewById(R.id.call_name);
        ImageView var8 = (ImageView)var4.findViewById(R.id.photo_image);
        ImageView var9 = (ImageView) var4.findViewById(R.id.onGoing_image);
        TextView var21 = var4.findViewById(R.id.textView4);
        this.setUserOngoingDetails(var21,var9);
        this.setUserDetails(var5, var8);
        this.setStatusView(View.VISIBLE);
        this.setSwappedFeeds(this.mCall.isVideoViewsSwapped());
        this.ui.pipVideo.setVisibility(this.mCall.isAnswered() ? View.VISIBLE : View.GONE);
        this.mCall.start((QampCallsActivity) this.getActivity(), this);
        if(this.mCall.isVideoCall()){

            this.ui.star.setVisibility(View.VISIBLE);
            this.ui.speaker.setVisibility(View.GONE);
        }

        return var4;
    }

    void answer(boolean var1) {
        if (!this.mCall.isAnswered() && this.mCall.isIncoming()) {
            this.mCall.answer(var1);
            this.setSwappedFeeds(false);
            if (this.mCall.isVideoCall() && var1) {
                this.ui.pipVideo.setVisibility(View.VISIBLE);

            }

            this.setStatusView(5, (String)null);
        }
    }

    public void onClick(View var1) {
        /**int var2;
        if ((var2 = var1.getId()) == id.pip_video_view) {
            this.setSwappedFeeds(!this.mCall.isVideoViewsSwapped());
        } else if (var2 == id.fullscreen_video_view) {
            this.toggleCallControlsVisibility();
        } else if (var2 != id.incoming_call_disconnect && var2 != id.button_call_disconnect) {
            if (var2 == id.incoming_call_connect) {
                this.answer(true);
            } else if (var2 == id.incoming_audio_call_connect) {
                this.answer(false);
            } else if (var2 == id.button_call_toggle_speaker) {
                this.mCall.toggleAudioDevice(AudioDevice.SPEAKER);
            } else {
                boolean var3;
                if (var2 == id.button_call_toggle_mic) {
                    var3 = this.mCall.toggleAudioMute();
                    this.setButtonAlpha(this.ui.toggleMuteButton, var3);
                } else if (var2 == id.button_call_switch_camera) {
                    this.mCall.switchCamera();
                } else if (var2 == id.button_call_switch_source) {
                    this.mCall.switchSource();
                } else {
                    if (var2 == id.button_call_toggle_camera) {
                        var3 = this.mCall.toggleVideoMute();
                        this.setButtonAlpha(this.ui.toggleCameraButton, var3);
                    }

                }
            }
        } else {
            this.mCall.hangup();
            this.setStatusView(64);
            this.mActivity.delayedFinish(500L);
        }*/
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
                this.mCall.toggleAudioDevice(AudioDevice.SPEAKER);
                if(speakerFirst == true){
                    this.ui.speaker.setImageResource(drawable.speakerlow);
                    speakerFirst = false;
                }else {
                    this.ui.speaker.setImageResource(drawable.ic_speakerhigh);
                    speakerFirst = true;}

            }

            else {
                boolean var3;
                if (var2 == R.id.four) {
                    var3 = this.mCall.toggleAudioMute();
                    if(muteFirst == true){
                        this.ui.mute.setImageResource(drawable.mute);
                        muteFirst = false;
                    }else {
                        this.ui.mute.setImageResource(drawable.microphone_off__2_);
                        muteFirst = true;
                    }

                    this.setButtonAlpha(this.ui.toggleMuteButton, var3);
                }
                else if (var2 == R.id.switch_source) {
                    this.mCall.switchCamera();
                } else if (var2 == R.id.two) {
                    //this.mCall.switchSource();
                    //this.mCall.toggleVideoMute();
                    var3 = this.mCall.toggleVideoMute();
                    this.setButtonAlpha(this.ui.switchS, var3);
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
            var2.setImageDrawable(MesiboUtils.getRoundImageDrawable(this.mCp.f2ui.userImageSmall));
        }

    }

    public void setUserOngoingDetails(TextView textView, ImageView imageView){
        if (!TextUtils.isEmpty(this.mCp.user.getName())) {
            textView.setText(this.mCp.user.getName());
        } else {
            textView.setText(this.mCp.user.address);
        }

        if (imageView != null) {
            imageView.setImageDrawable(MesiboUtils.getRoundImageDrawable(this.mCp.f2ui.userImage));
        }

    }

    public void onResume() {
        super.onResume();
        if (this.mCp.autoAnswer) {
            (new Handler()).postDelayed(new Runnable() {
                public void run() {
                    MesiboDefaultCallFragment.this.answer(MesiboDefaultCallFragment.this.mCp.video.enabled);
                }
            }, 1000L);
        }

    }

    public void setStatusView(int var1, String var2) {
        if (this.mCall.isAnswered() && this.mCall.isCallInProgress() && this.mCall.isCallConnected()) {

            this.ui.status.setFormat((String)null);
            this.ui.status.setText("");
            this.ui.status.setBase(this.mCall.getAnswerTime());
            this.ui.status.start();
            if(this.mCall.isVideoCall()){
                this.ui.thumbnailLayout.setVisibility(View.GONE);
                this.ui.contactView.setVisibility(View.GONE);
            }else  {
                //this.ui.background.setVisibility(View.VISIBLE);
            }

            //this.ui.thumbnailLayout.setVisibility(View.GONE);
            //this.ui.contactView.setVisibility(View.GONE);
        } else {
            this.ui.status.stop();
            if (var2 != null) {
                this.ui.mStatusText = var2;
            } else {
                this.ui.mStatusText = this.statusToString(var1, this.ui.mStatusText);
            }

            this.ui.status.setText(this.ui.mStatusText);
        }
    }

    public void setStatusView(int var1) {
        this.setStatusView(var1, (String)null);
    }

    /**public void updateRemoteMuteButtons() {
        boolean var1 = this.mCall.getMuteStatus(true, false, true);
        boolean var2 = this.mCall.getMuteStatus(false, true, true);
        if (!var1 && !var2) {
            this.ui.remoteMute.setVisibility(View.GONE);
        } else {
            int var3 = drawable.ic_mesibo_mic_off;
            if (var2) {
                var3 = drawable.ic_mesibo_videocam_off;
            }

            if (var2 && var1) {
                var3 = drawable.ic_mesibo_tv_off;
            }

            this.ui.remoteMute.setImageResource(var3);
            this.ui.remoteMute.setVisibility(View.VISIBLE);
        }
    }*/

    private void callConnected() {
        if (!this.mConnected) {
            this.mConnected = true;
            this.ui.thumbnailLayout.setVisibility(View.GONE);
            this.ui.contactView.setVisibility(View.GONE);
            this.ui.background.setVisibility(View.VISIBLE);
            this.ui.contactOngoinName.setVisibility(View.VISIBLE);
            this.ui.backArrow.setVisibility(View.VISIBLE);
            this.ui.chatOngoing.setVisibility(View.VISIBLE);
            if (this.mCall.isVideoCall()) {
                this.ui.pipVideo.setVisibility(View.VISIBLE);
                this.ui.star.setVisibility(View.VISIBLE);
                this.ui.speaker.setVisibility(View.GONE);
                this.ui.background.setVisibility(View.GONE);
                this.ui.contactOngoinName.setVisibility(View.GONE);
                this.ui.backArrow.setImageResource(drawable.arrow_left);
                this.ui.chatOngoing.setImageResource(drawable.vector__2_);
                this.ui.status.setTextColor(Color.WHITE);
                this.setSwappedFeeds(false);
            }

        }
    }

    public void MesiboCall_OnVideoSourceChanged(CallProperties var1, int var2, int var3) {
        //this.setButtonAlpha(this.ui.sourceSwitchButton, var2 == 4);
    }

    public void MesiboCall_OnVideo(CallProperties var1, VideoProperties var2, boolean var3) {
    }

    public void MesiboCall_OnUpdateUserInterface(CallProperties var1, int var2, boolean var3, boolean var4) {
        /**if (var2 == 3) {
            if (var4 || this.mCp.f2ui.autoHideControls) {
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
                this.ui.background.setImageBitmap(this.mCp.f2ui.userImage);
            }

            this.ui.pipVideo.setVisibility(var7);
            this.ui.fullscreenVideo.setVisibility(var7);
            this.ui.cameraToggleLayout.setVisibility(var7);
            this.ui.cameraSwitchLayout.setVisibility(var7);
            if (this.mCp.f2ui.showScreenSharing) {
                this.ui.sourceSwitchLayout.setVisibility(var7);
            }

            this.ui.thumbnailLayout.setVisibility(var7);
            this.ui.incomingVideoAcceptLayout.setVisibility(var6);
        }*/

        if (var2 == 3) {
            if (var4 || this.mCp.f2ui.autoHideControls) {
                this.setCallControlsVisibility(var4, false);
            }
        } else {


            Log.e("hold", String.valueOf(this.mCp.holdOnCellularIncoming));
            if (this.mCp.holdOnCellularIncoming) {
                //this.ui.callHold.setVisibility(View.VISIBLE);

            }
            Log.e("Video Prop", var1.video.toString());
            boolean var5 = var2 == 1;

                this.ui.thumbnailLayout.setVisibility(View.VISIBLE);
                this.ui.incomingView.setVisibility(var5 ? View.VISIBLE : View.GONE);

                this.ui.inprogressView.setVisibility(var5 ? View.GONE : View.VISIBLE);
                var2 = var3 ? View.GONE : View.VISIBLE;
                int var7 = var3 ? View.VISIBLE : View.GONE;
                int var6 = var5 && var3 ? View.VISIBLE : View.GONE;
                this.ui.background.setVisibility(View.GONE);
                //this.ui.speaker.setVisibility(View.GONE);
                this.ui.speaker.setVisibility(View.VISIBLE);
                if (var1.video != null) {
                    this.ui.speaker.setVisibility(View.GONE);
                    this.ui.star.setVisibility(View.VISIBLE);
                    //this.ui.thumbnailLayout.setVisibility(View.GONE);

                }
                this.ui.star.setVisibility(View.GONE);
                this.ui.speaker.setVisibility(View.VISIBLE);

                if (!var3) {
                    this.ui.background.setImageBitmap(this.mCp.f2ui.userImage);
                }

                this.ui.pipVideo.setVisibility(var7);
                this.ui.fullscreenVideo.setVisibility(var7);
                this.ui.cameraToggleLayout.setVisibility(var7);
                this.ui.cameraSwitchLayout.setVisibility(var7);
                if (this.mCp.f2ui.showScreenSharing) {
                    this.ui.sourceSwitchLayout.setVisibility(var7);
                }

                //this.ui.thumbnailLayout.setVisibility(View.VISIBLE);
                //this.ui.thumbnailLayout.setVisibility(var7);
                this.ui.incomingVideoAcceptLayout.setVisibility(var6);

            }



    }

    public void MesiboCall_OnStatus(CallProperties var1, int var2, boolean var3) {
        this.mCp.f2ui.callStatusText = null;
        if (null != this.mCp.f2ui.inProgressListener) {
            this.mCp.f2ui.inProgressListener.MesiboCall_OnStatus(var1, var2, var3);
        }

        this.setStatusView(var2, this.mCp.f2ui.callStatusText);
        if ((var2 & 64) > 0) {
            if (var2 == 76 && !Mesibo.isAccountPaid()) {
                String var4 = "mesibo free accounts have a limit on call duration. Please upgrade your mesibo account to remove this limitation";
                Utils.alert(this.mActivity, "Free Accounts Limit", var4, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface var1, int var2) {
                        MesiboDefaultCallFragment.this.mActivity.finish();
                    }
                });
            } else {
                this.mActivity.delayedFinish(3000L);
            }
        } else {
            switch(var2) {
                case 48:
                    this.callConnected();
                default:
            }
        }
    }

    /**protected void setButtonAlpha(ImageButton var1, boolean var2) {
        var1.setAlpha((float)(var2 ? this.ui.buttonAlphaOn : this.ui.buttonAlphaOff) / 255.0F);
    }*/
    protected void setButtonAlpha(ImageView var1, boolean var2) {
        var1.setAlpha((float)(var2 ? this.ui.buttonAlphaOn : this.ui.buttonAlphaOff) / 255.0F);
    }
    /**public void MesiboCall_OnSetCall(QampCallsActivity var1, Call var2) {
        this.mActivity = var1;
        this.mCall = var2;
        this.mCp = this.mCall.getCallProperties();
        this.mCp.activity = var1;
        if (null != this.mCp.f2ui.inProgressListener) {
            this.mCp.ui.inProgressListener.MesiboCall_OnSetCall(var1, this.mCall);
        }

    }*/

    public void MesiboCall_OnMute(CallProperties var1, boolean var2, boolean var3, boolean var4) {
        if (null != this.mCp.f2ui.inProgressListener) {
            this.mCp.f2ui.inProgressListener.MesiboCall_OnMute(var1, var2, var3, var4);
        }

        if (var4) {
           // this.updateRemoteMuteButtons();
        } else {
            //this.setButtonAlpha(this.ui.toggleMuteButton, this.mCall.getMuteStatus(true, false, false));
            //this.setButtonAlpha(this.ui.toggleCameraButton, this.mCall.getMuteStatus(false, true, false));
        }
    }

    public boolean MesiboCall_OnPlayInCallSound(CallProperties var1, int var2, boolean var3) {
        if (!var3) {
            this.mCall.stopInCallSound();
            return true;
        } else {
            this.mCall.playInCallSound(this.mActivity.getApplicationContext(), var2 == 0 ? raw.mesibo_ring : raw.mesibo_busy, true);
            return true;
        }
    }

    @Override
    public void MesiboCall_OnSetCall(QampCallsActivity mesiboCallActivity, Call call) {
        this.mActivity = mesiboCallActivity;
        this.mCall = call;
        this.mCp = this.mCall.getCallProperties();
        this.mCp.activity = mesiboCallActivity;
        if (null != this.mCp.f2ui.inProgressListener) {
            this.mCp.f2ui.inProgressListener.MesiboCall_OnSetCall(mesiboCallActivity, this.mCall);
        }
    }

    public void MesiboCall_OnHangup(CallProperties var1, int var2) {
        if (null != this.mCp.f2ui.inProgressListener) {
            this.mCp.f2ui.inProgressListener.MesiboCall_OnHangup(var1, var2);
        }

    }

    public void MesiboCall_OnAudioDeviceChanged(CallProperties var1, AudioDevice var2, AudioDevice var3) {
        //this.setButtonAlpha(this.ui.toggleSpeakerButton, var2 == AudioDevice.SPEAKER);
    }

    public void MesiboCall_OnOrientationChanged(CallProperties var1, boolean var2, boolean var3) {
    }

    public void MesiboCall_OnBatteryStatus(CallProperties var1, boolean var2, boolean var3) {
        /**if (var3 && var2) {
            this.ui.remoteMute.setImageResource(drawable.ic_mesibo_battery_1bar);
            this.ui.remoteMute.setVisibility(View.VISIBLE);
        } else {
            this.updateRemoteMuteButtons();
        }*/
    }

    public void MesiboCall_OnDTMF(CallProperties var1, int var2) {
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
            case 71:
                var2 = this.getStatusText(var1, "Another Call In Progress");
                break;
            case 72:
                var2 = this.getStatusText(var1, "Calls Not Supported");
                break;
            case 74:
                var2 = this.getStatusText(var1, "Not Allowed");
                break;
            case 75:
                var2 = this.getStatusText(var1, "User Blocked");
                break;
            case 76:
                var2 = this.getStatusText(var1, "Call Duration Exceeded");
                break;
            case 97:
                var2 = this.getStatusText(var1, "Peer has Device Error");
                break;
            case 98:
                var2 = this.getStatusText(var1, "Network Error");
                break;
            default:
                if ((var1 & 64) > 0) {
                    var2 = this.getStatusText(var1, "Call Completed");
                }
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
                SystemClock.sleep(MesiboDefaultCallFragment.this.autoHideVideoControlsTimeout);
                if (!Thread.currentThread().isInterrupted()) {
                    MesiboDefaultCallFragment.this.setCallControlsVisibility(false, false);
                }
            }
        });
        this.mControlHidingThread.start();
    }

    public static class CallUserInterface {
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
        public View backgroundView;
        public String mStatusText = "";
        public int buttonAlphaOff = 127;
        public int buttonAlphaMid = 200;
        public int buttonAlphaOn = 255;*/
        public Chronometer status = null;
        public MesiboVideoView pipVideo;
        public MesiboVideoView fullscreenVideo;
        public TextView contactView;
        public TextView contactOngoinName;
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
        public ImageView backArrow;
        public ImageView chatOngoing;
        public ImageView disconnectButton;
        public ImageView buttonDisconnect;
        public ImageView background;
        public TextView callHold;
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
    }
}
