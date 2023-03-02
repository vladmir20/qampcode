package com.qamp.app.qampCalls;

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
import com.qamp.app.R;


public class MesiboParticipantViewHolder implements View.OnClickListener {
    private final TextView nameView;
    private ImageView mIndicatorView;
    private final MesiboVideoView mVideoView;
    private final RelativeLayout mControls;
    MesiboCall.MesiboParticipant mStream = null;
    boolean mFullScreen = false;
    private Listener mListener = null;
    private boolean mVideo = false;
    private boolean mAudio = false;
    private float mX;
    private float mY;
    private float mWidth;
    private float mHeight;
    ImageButton toggleAudioMuteButton;
    ImageButton toggleVideoMuteButton;
    ImageButton toggleFullScreenButton;
    ImageButton messageButton;
    ImageButton hangupButton;
    private MesiboCall.MesiboGroupCall mGroupcall = null;
    private boolean mAdminMode = false;
    private final View mView;

    public MesiboParticipantViewHolder(Context var1, Listener var2, MesiboCall.MesiboGroupCall var3) {
        LayoutInflater var4 = (LayoutInflater)var1.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mGroupcall = var3;
        this.mListener = var2;
        this.mView = var4.inflate(R.layout.view_mesiboparticipant, (ViewGroup)null);//============
        this.nameView = (TextView)this.mView.findViewById(R.id.participant_name);//============
        this.mVideoView = (MesiboVideoView)this.mView.findViewById(R.id.participant_stream_view);//============
        this.mControls = (RelativeLayout)this.mView.findViewById(R.id.stream_controls);//============
        this.mIndicatorView = (ImageView)this.mView.findViewById(R.id.participant_indicator);//============
        this.toggleAudioMuteButton = (ImageButton)this.mView.findViewById(R.id.button_stream_toggle_audio);//============
        this.toggleVideoMuteButton = (ImageButton)this.mView.findViewById(R.id.button_stream_toggle_video);//============
        this.toggleFullScreenButton = (ImageButton)this.mView.findViewById(R.id.button_stream_toggle_fullscreen);//============
        this.messageButton = (ImageButton)this.mView.findViewById(R.id.button_stream_message);//============
        this.hangupButton = (ImageButton)this.mView.findViewById(R.id.button_stream_hangup);//============
        this.toggleAudioMuteButton.setOnClickListener(this);
        this.toggleVideoMuteButton.setOnClickListener(this);
        this.toggleFullScreenButton.setOnClickListener(this);
        this.messageButton.setOnClickListener(this);
        this.hangupButton.setOnClickListener(this);
        this.mVideoView.scaleToFill(true);
    }

    private void reset() {
    }

    public MesiboCall.MesiboParticipant getParticipant() {
        return this.mStream;
    }

    public void setParticipant(MesiboCall.MesiboParticipant var1) {
        this.reset();
        this.mStream = var1;
        if (this.mStream != null) {
            MesiboParticipantViewHolder var2;
            if ((var2 = (MesiboParticipantViewHolder)var1.getUserData()) != null && var2 != this && var2.getParticipant() == this.mStream) {
                var2.setParticipant((MesiboCall.MesiboParticipant)null);
            }

            var1.setUserData(this);
            String var3 = var1.getName();
            if (var1.isMe()) {
                var3 = "You";
            }

            if (!var3.isEmpty()) {
                this.nameView.setText(var3);
            }

            this.setStreamIndicators();
            this.setStreamView();
            setParticipantProfile(this.getParticipant());
        }
    }

    public static void setParticipantProfile(MesiboCall.MesiboParticipant var0) {
        Mesibo.getProfile(var0.getAddress());
    }

    public static boolean deleteParticipantProfile(MesiboCall.MesiboParticipant var0) {
        return Mesibo.getProfile(var0.getAddress()) != null;
    }

    public void setAudio(boolean var1) {
        this.mAudio = var1;
    }

    public void setVideo(boolean var1) {
        this.mVideo = var1;
        this.setStreamView();
    }

    public void refresh() {
        this.setStreamIndicators();
    }

    public void setFullScreen(boolean var1) {
        this.mFullScreen = false;
    }

    public void onClick(View var1) {
        if (this.mStream != null) {
            int var2;
            if ((var2 = var1.getId()) == R.id.button_stream_toggle_audio) { //==========
                this.onToggleStreamAudioMute(var1);
            } else if (var2 == R.id.button_stream_toggle_video) {//==========
                this.onToggleStreamVideoMute(var1);
            } else if (var2 == R.id.button_stream_toggle_fullscreen) {//==========
                this.onToggleStreamFullScreen(var1);
            } else if (var2 == R.id.button_stream_message) {//==========
                this.onLaunchMessagingUi(var1);
            } else {
                if (var2 == R.id.button_stream_hangup) {//==========
                    this.onStreamHangup(var1);
                }

            }
        }
    }

    public void setCoordinates(int var1, float var2, float var3, float var4, float var5) {
        this.mX = var2;
        this.mY = var3;
        this.mWidth = var4;
        this.mHeight = var5;
    }

    public void layout(FrameLayout var1) {
        FrameLayout.LayoutParams var2;
        (var2 = new FrameLayout.LayoutParams(-2, -2)).setMargins((int)this.mX, (int)this.mY, 0, 0);
        var2.height = (int)this.mHeight;
        var2.width = (int)this.mWidth;
        var1.addView(this.mView, var2);
    }

    private void onToggleStreamAudioMute(View var1) {
        boolean var3;
        if (this.mAdminMode) {
            MesiboCall.MesiboParticipantAdmin var2;
            if ((var2 = this.mStream.getAdmin()) == null) {
                return;
            }

            var3 = var2.toggleAudioMute();
        } else {
            this.mStream.toggleAudioMute();
            var3 = this.mStream.getMuteStatus(false);
        }

        int var4 = var3 ? R.drawable.ic_mesibo_mic_off : R.drawable.ic_mesibo_mic;//==========
        var1.setBackgroundResource(var4);
    }

    private void onToggleStreamVideoMute(View var1) {
        boolean var3;
        if (this.mAdminMode) {
            MesiboCall.MesiboParticipantAdmin var2;
            if ((var2 = this.mStream.getAdmin()) == null) {
                return;
            }

            var3 = var2.toggleVideoMute();
        } else {
            this.mStream.toggleVideoMute();
            var3 = this.mStream.getMuteStatus(false);
        }

        int var4 = var3 ? R.drawable.ic_mesibo_videocam_off : R.drawable.ic_mesibo_videocam;//==========
        var1.setBackgroundResource(var4);
    }

    private void onToggleStreamFullScreen(View var1) {
        this.mFullScreen = !this.mFullScreen;
        this.mListener.ParticipantViewHolder_onFullScreen(this.getParticipant(), this.mFullScreen);
    }

    public void onLaunchMessagingUi(View var1) {
    }

    private void onStreamHangup(View var1) {
        if (this.mAdminMode) {
            MesiboCall.MesiboParticipantAdmin var2;
            if ((var2 = this.mStream.getAdmin()) != null) {
                var2.hangup();
            }
        } else {
            this.mListener.ParticipantViewHolder_onHangup(this.getParticipant());
        }
    }

    protected void stopVideoView() {
        this.mVideoView.stop();
    }

    public void setHeight(float var1) {
        if (var1 >= 0.0F) {
            ViewGroup.LayoutParams var2;
            (var2 = this.mView.getLayoutParams()).height = (int)var1;
            this.mView.setLayoutParams(var2);
        }
    }

    private void setStreamView() {
        if (this.mVideo && this.mStream.isVideoCall() && this.mStream.hasVideo()) {
            this.mStream.setVideoView(this.mVideoView);
            if (this.mStream.isMe() && this.mStream.getVideoSource() != 4) {
                this.mVideoView.enableMirror(true);
            }
        }

        this.setStreamControls();
    }

    private void setStreamIndicators() {
        boolean var1 = this.mStream.getMuteStatus(false);
        boolean var2 = this.mStream.getMuteStatus(true);
        boolean var3 = this.mStream.isTalking();
        int var4 = -1;
        int var5 = Color.argb(200, 200, 0, 0);
        if (var1) {
            var4 = R.drawable.ic_mesibo_mic_off;//==========
        }

        if (var2) {
            var4 = R.drawable.ic_mesibo_videocam_off;//==========
        }

        if (var1 && var2) {
            var4 = R.drawable.ic_mesibo_tv_off;//==========
        }

        if (var3) {
            var4 = R.drawable.ic_mesibo_volume_up;//==========
            var5 = Color.argb(200, 0, 200, 0);
        }

        if (var4 >= 0) {
            this.mIndicatorView.setColorFilter(var5);
            this.mIndicatorView.setImageResource(var4);
            this.mIndicatorView.setVisibility(View.VISIBLE);
        } else {
            this.mIndicatorView.setVisibility(View.GONE);
        }
    }

    private void setButtonColors(boolean var1) {
        if (var1) {
            int var2 = Color.argb(200, 200, 0, 0);
            this.toggleAudioMuteButton.setColorFilter(var2);
            this.toggleVideoMuteButton.setColorFilter(var2);
            this.hangupButton.setColorFilter(var2);
            this.toggleFullScreenButton.setColorFilter(var2);
        } else {
            this.toggleAudioMuteButton.clearColorFilter();
            this.toggleVideoMuteButton.clearColorFilter();
            this.hangupButton.clearColorFilter();
            this.toggleFullScreenButton.clearColorFilter();
        }
    }

    public void setStreamControls() {
        if (!this.mFullScreen) {
            if (this.mStream.isMe()) {
                this.hangupButton.setVisibility(View.GONE);
                this.messageButton.setVisibility(View.GONE);
            }

            if (this.mListener.ParticipantViewHolder_onStreamCount() < 2) {
                this.toggleFullScreenButton.setVisibility(View.GONE);
            } else {
                this.toggleFullScreenButton.setVisibility(View.VISIBLE);
            }
        }

        int var1;
        if (this.mFullScreen) {
            var1 = R.drawable.ic_mesibo_fullscreen_exit;//==========
        } else {
            var1 = R.drawable.ic_mesibo_fullscreen;//==========
        }

        if (var1 >= 0) {
            this.toggleFullScreenButton.setImageResource(var1);
        }

        this.mVideoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View var1) {
                MesiboParticipantViewHolder.this.mAdminMode = false;
                if (MesiboParticipantViewHolder.this.mControls.getVisibility() == View.GONE) {
                    MesiboParticipantViewHolder.this.setButtonColors(false);
                    MesiboParticipantViewHolder.this.mControls.setVisibility(View.VISIBLE);
                } else {
                    MesiboParticipantViewHolder.this.mControls.setVisibility(View.GONE);
                }
            }
        });
        this.mVideoView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View var1) {
                if (!MesiboParticipantViewHolder.this.mGroupcall.hasAdminPermissions(false)) {
                    return true;
                } else if (MesiboParticipantViewHolder.this.mStream.isMe()) {
                    return true;
                } else {
                    MesiboParticipantViewHolder.this.mAdminMode = true;
                    MesiboParticipantViewHolder.this.setButtonColors(true);
                    MesiboParticipantViewHolder.this.mControls.setVisibility(View.VISIBLE);
                    return true;
                }
            }
        });
    }

    public interface Listener {
        void ParticipantViewHolder_onFullScreen(MesiboCall.MesiboParticipant var1, boolean var2);

        void ParticipantViewHolder_onHangup(MesiboCall.MesiboParticipant var1);

        int ParticipantViewHolder_onStreamCount();
    }
}
