/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:40 AM
 *
 */

package com.qamp.app.qampcallss.api.p000ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.calls.api.MesiboCall;
import com.mesibo.calls.api.MesiboVideoView;
import com.mesibo.calls.api.R;

/* renamed from: com.mesibo.calls.api.ui.MesiboParticipantViewHolder */
public class MesiboParticipantViewHolder implements View.OnClickListener {
    ImageButton hangupButton;
    /* access modifiers changed from: private */
    public boolean mAdminMode = false;
    private boolean mAudio = false;
    /* access modifiers changed from: private */
    public final RelativeLayout mControls;
    boolean mFullScreen = false;
    /* access modifiers changed from: private */
    public MesiboCall.MesiboGroupCall mGroupcall = null;
    private float mHeight;
    private ImageView mIndicatorView;
    private Listener mListener = null;
    MesiboCall.MesiboParticipant mStream = null;
    private boolean mVideo = false;
    private final MesiboVideoView mVideoView;
    private final View mView;
    private float mWidth;

    /* renamed from: mX */
    private float f6mX;

    /* renamed from: mY */
    private float f7mY;
    ImageButton messageButton;
    private final TextView nameView;
    ImageButton toggleAudioMuteButton;
    ImageButton toggleFullScreenButton;
    ImageButton toggleVideoMuteButton;

    /* renamed from: com.mesibo.calls.api.ui.MesiboParticipantViewHolder$Listener */
    public interface Listener {
        void ParticipantViewHolder_onFullScreen(MesiboCall.MesiboParticipant mesiboParticipant, boolean z);

        void ParticipantViewHolder_onHangup(MesiboCall.MesiboParticipant mesiboParticipant);

        int ParticipantViewHolder_onStreamCount();
    }

    public MesiboParticipantViewHolder(Context context, Listener listener, MesiboCall.MesiboGroupCall mesiboGroupCall) {
        this.mGroupcall = mesiboGroupCall;
        this.mListener = listener;
        this.mView = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.view_mesiboparticipant, (ViewGroup) null);
        this.nameView = (TextView) this.mView.findViewById(R.id.participant_name);
        this.mVideoView = (MesiboVideoView) this.mView.findViewById(R.id.participant_stream_view);
        this.mControls = (RelativeLayout) this.mView.findViewById(R.id.stream_controls);
        this.mIndicatorView = (ImageView) this.mView.findViewById(R.id.participant_indicator);
        this.toggleAudioMuteButton = (ImageButton) this.mView.findViewById(R.id.button_stream_toggle_audio);
        this.toggleVideoMuteButton = (ImageButton) this.mView.findViewById(R.id.button_stream_toggle_video);
        this.toggleFullScreenButton = (ImageButton) this.mView.findViewById(R.id.button_stream_toggle_fullscreen);
        this.messageButton = (ImageButton) this.mView.findViewById(R.id.button_stream_message);
        this.hangupButton = (ImageButton) this.mView.findViewById(R.id.button_stream_hangup);
        this.toggleAudioMuteButton.setOnClickListener(this);
        this.toggleVideoMuteButton.setOnClickListener(this);
        this.toggleFullScreenButton.setOnClickListener(this);
        this.messageButton.setOnClickListener(this);
        this.hangupButton.setOnClickListener(this);
        this.mVideoView.scaleToFill(true);
    }

    public static boolean deleteParticipantProfile(MesiboCall.MesiboParticipant mesiboParticipant) {
        return Mesibo.getProfile(mesiboParticipant.getAddress()) != null;
    }

    private void onStreamHangup(View view) {
        if (this.mAdminMode) {
            MesiboCall.MesiboParticipantAdmin admin = this.mStream.getAdmin();
            if (admin != null) {
                admin.hangup();
                return;
            }
            return;
        }
        this.mListener.ParticipantViewHolder_onHangup(getParticipant());
    }

    private void onToggleStreamAudioMute(View view) {
        boolean muteStatus;
        if (this.mAdminMode) {
            MesiboCall.MesiboParticipantAdmin admin = this.mStream.getAdmin();
            if (admin != null) {
                muteStatus = admin.toggleAudioMute();
            } else {
                return;
            }
        } else {
            this.mStream.toggleAudioMute();
            muteStatus = this.mStream.getMuteStatus(false);
        }
        view.setBackgroundResource(muteStatus ? R.drawable.ic_mesibo_mic_off : R.drawable.ic_mesibo_mic);
    }

    private void onToggleStreamFullScreen(View view) {
        this.mFullScreen = !this.mFullScreen;
        this.mListener.ParticipantViewHolder_onFullScreen(getParticipant(), this.mFullScreen);
    }

    private void onToggleStreamVideoMute(View view) {
        boolean muteStatus;
        if (this.mAdminMode) {
            MesiboCall.MesiboParticipantAdmin admin = this.mStream.getAdmin();
            if (admin != null) {
                muteStatus = admin.toggleVideoMute();
            } else {
                return;
            }
        } else {
            this.mStream.toggleVideoMute();
            muteStatus = this.mStream.getMuteStatus(false);
        }
        view.setBackgroundResource(muteStatus ? R.drawable.ic_mesibo_videocam_off : R.drawable.ic_mesibo_videocam);
    }

    private void reset() {
    }

    /* access modifiers changed from: private */
    public void setButtonColors(boolean z) {
        if (z) {
            int argb = Color.argb(200, 200, 0, 0);
            this.toggleAudioMuteButton.setColorFilter(argb);
            this.toggleVideoMuteButton.setColorFilter(argb);
            this.hangupButton.setColorFilter(argb);
            this.toggleFullScreenButton.setColorFilter(argb);
            return;
        }
        this.toggleAudioMuteButton.clearColorFilter();
        this.toggleVideoMuteButton.clearColorFilter();
        this.hangupButton.clearColorFilter();
        this.toggleFullScreenButton.clearColorFilter();
    }

    public static void setParticipantProfile(MesiboCall.MesiboParticipant mesiboParticipant) {
        Mesibo.getProfile(mesiboParticipant.getAddress());
    }

    private void setStreamIndicators() {
        boolean muteStatus = this.mStream.getMuteStatus(false);
        boolean muteStatus2 = this.mStream.getMuteStatus(true);
        boolean isTalking = this.mStream.isTalking();
        int i = -1;
        int argb = Color.argb(200, 200, 0, 0);
        if (muteStatus) {
            i = R.drawable.ic_mesibo_mic_off;
        }
        if (muteStatus2) {
            i = R.drawable.ic_mesibo_videocam_off;
        }
        if (muteStatus && muteStatus2) {
            i = R.drawable.ic_mesibo_tv_off;
        }
        if (isTalking) {
            i = R.drawable.ic_mesibo_volume_up;
            argb = Color.argb(200, 0, 200, 0);
        }
        if (i >= 0) {
            this.mIndicatorView.setColorFilter(argb);
            this.mIndicatorView.setImageResource(i);
            this.mIndicatorView.setVisibility(0);
            return;
        }
        this.mIndicatorView.setVisibility(8);
    }

    private void setStreamView() {
        if (this.mVideo && this.mStream.isVideoCall() && this.mStream.hasVideo()) {
            this.mStream.setVideoView(this.mVideoView);
            if (this.mStream.isMe() && this.mStream.getVideoSource() != 4) {
                this.mVideoView.enableMirror(true);
            }
        }
        setStreamControls();
    }

    public MesiboCall.MesiboParticipant getParticipant() {
        return this.mStream;
    }

    public void layout(FrameLayout frameLayout) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
        layoutParams.setMargins((int) this.f6mX, (int) this.f7mY, 0, 0);
        layoutParams.height = (int) this.mHeight;
        layoutParams.width = (int) this.mWidth;
        frameLayout.addView(this.mView, layoutParams);
    }

    public void onClick(View view) {
        if (this.mStream != null) {
            int id = view.getId();
            if (id == R.id.button_stream_toggle_audio) {
                onToggleStreamAudioMute(view);
            } else if (id == R.id.button_stream_toggle_video) {
                onToggleStreamVideoMute(view);
            } else if (id == R.id.button_stream_toggle_fullscreen) {
                onToggleStreamFullScreen(view);
            } else if (id == R.id.button_stream_message) {
                onLaunchMessagingUi(view);
            } else if (id == R.id.button_stream_hangup) {
                onStreamHangup(view);
            }
        }
    }

    public void onLaunchMessagingUi(View view) {
    }

    public void refresh() {
        setStreamIndicators();
    }

    public void setAudio(boolean z) {
        this.mAudio = z;
    }

    public void setCoordinates(int i, float f, float f2, float f3, float f4) {
        this.f6mX = f;
        this.f7mY = f2;
        this.mWidth = f3;
        this.mHeight = f4;
    }

    public void setFullScreen(boolean z) {
        this.mFullScreen = false;
    }

    public void setHeight(float f) {
        if (f >= 0.0f) {
            ViewGroup.LayoutParams layoutParams = this.mView.getLayoutParams();
            layoutParams.height = (int) f;
            this.mView.setLayoutParams(layoutParams);
        }
    }

    public void setParticipant(MesiboCall.MesiboParticipant mesiboParticipant) {
        reset();
        this.mStream = mesiboParticipant;
        if (this.mStream != null) {
            MesiboParticipantViewHolder mesiboParticipantViewHolder = (MesiboParticipantViewHolder) mesiboParticipant.getUserData();
            if (!(mesiboParticipantViewHolder == null || mesiboParticipantViewHolder == this || mesiboParticipantViewHolder.getParticipant() != this.mStream)) {
                mesiboParticipantViewHolder.setParticipant((MesiboCall.MesiboParticipant) null);
            }
            mesiboParticipant.setUserData(this);
            String name = mesiboParticipant.getName();
            if (mesiboParticipant.isMe()) {
                name = "You";
            }
            if (!name.isEmpty()) {
                this.nameView.setText(name);
            }
            setStreamIndicators();
            setStreamView();
            setParticipantProfile(getParticipant());
        }
    }

    public void setStreamControls() {
        if (!this.mFullScreen) {
            if (this.mStream.isMe()) {
                this.hangupButton.setVisibility(8);
                this.messageButton.setVisibility(8);
            }
            if (this.mListener.ParticipantViewHolder_onStreamCount() < 2) {
                this.toggleFullScreenButton.setVisibility(8);
            } else {
                this.toggleFullScreenButton.setVisibility(0);
            }
        }
        int i = this.mFullScreen ? R.drawable.ic_mesibo_fullscreen_exit : R.drawable.ic_mesibo_fullscreen;
        if (i >= 0) {
            this.toggleFullScreenButton.setImageResource(i);
        }
        this.mVideoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                boolean unused = MesiboParticipantViewHolder.this.mAdminMode = false;
                if (MesiboParticipantViewHolder.this.mControls.getVisibility() == 8) {
                    MesiboParticipantViewHolder.this.setButtonColors(false);
                    MesiboParticipantViewHolder.this.mControls.setVisibility(0);
                    return;
                }
                MesiboParticipantViewHolder.this.mControls.setVisibility(8);
            }
        });
        this.mVideoView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                if (MesiboParticipantViewHolder.this.mGroupcall.hasAdminPermissions(false) && !MesiboParticipantViewHolder.this.mStream.isMe()) {
                    boolean unused = MesiboParticipantViewHolder.this.mAdminMode = true;
                    MesiboParticipantViewHolder.this.setButtonColors(true);
                    MesiboParticipantViewHolder.this.mControls.setVisibility(0);
                }
                return true;
            }
        });
    }

    public void setVideo(boolean z) {
        this.mVideo = z;
        setStreamView();
    }

    /* access modifiers changed from: protected */
    public void stopVideoView() {
        this.mVideoView.stop();
    }
}
