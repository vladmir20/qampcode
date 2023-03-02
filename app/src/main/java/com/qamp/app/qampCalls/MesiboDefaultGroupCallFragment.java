package com.qamp.app.qampCalls;

import androidx.fragment.app.Fragment;

public class MesiboDefaultGroupCallFragment extends Fragment{
    public void onBackPressed() {
    }
}
/**

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog.Builder;
import androidx.fragment.app.Fragment;

import com.mesibo.api.Mesibo;
import com.mesibo.api.Mesibo.FileInfo;
import com.mesibo.api.Mesibo.GroupListener;
import com.mesibo.api.Mesibo.Location;
import com.mesibo.api.Mesibo.MessageListener;
import com.mesibo.api.Mesibo.MessageParams;
import com.mesibo.api.MesiboGroupProfile.GroupPin;
import com.mesibo.api.MesiboGroupProfile.GroupSettings;
import com.mesibo.api.MesiboGroupProfile.Member;
import com.mesibo.api.MesiboGroupProfile.MemberPermissions;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.R;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MesiboDefaultGroupCallFragment extends Fragment implements OnClickListener, GroupListener, MessageListener, MesiboCall.GroupCallInProgressListener, MesiboCall.GroupCallListener, MesiboParticipantViewHolder.Listener {
    public static final String TAG = "MesiboDefaultCallFragment";
    private long mGid = 0L;
    private boolean mVideo = true;
    private boolean mAudio = true;
    private boolean mResumed = false;
    private MesiboProfile mGroupProfile = null;
    private MesiboCall.MesiboGroupCall mGroupcall = null;
    private MesiboCall.MesiboParticipant mLocalPublisher = null;
    public ArrayList<MesiboCall.MesiboParticipant> mPublishers = new ArrayList();
    private ConcurrentLinkedQueue<MesiboParticipantViewHolder> mViewHolders = new ConcurrentLinkedQueue();
    private ArrayList<MesiboParticipantViewHolder> mStreams = new ArrayList();
    private MesiboGroupCallView mGridView = null;
    private GroupSettings mSettings = null;
    private MemberPermissions mPermissions = null;
    private GroupPin[] mPins = null;
    private View mView = null;
    private boolean isGroupCallStarted = false;
    private long mTalkTs = 0L;
    private MesiboCall.MesiboParticipant mFullScreenParticipant = null;

    public MesiboDefaultGroupCallFragment() {
    }

    protected void setGroup(long var1, boolean var3, boolean var4) {
        this.mGid = var1;
        this.mVideo = var3;
        this.mAudio = var4;
        this.mGroupProfile = Mesibo.getProfile(var1);
    }

    public View onCreateView(LayoutInflater var1, ViewGroup var2, Bundle var3) {
        int var5 = R.layout.fragment_mesibogroupcall_weight;//=========
        this.mView = var1.inflate(var5, var2, false);
        FrameLayout var4 = (FrameLayout)this.mView.findViewById(R.id.streams_grid);//======
        this.mGroupcall = MesiboCall.getInstance().groupCall((QampCallActivity)this.getActivity(), this.mGid);
        if (this.mGroupcall == null) {
            return null;
        } else {
            Mesibo.addListener(this);
            this.mGroupProfile.getGroupProfile().getSettings(this);
            this.mGridView = new MesiboGroupCallView(this.getActivity(), var4, this.mGroupcall);
            this.setupButtons(this.mView);
            if (this.mVideo) {
                this.mAudio = true;
            }

            return this.mView;
        }
    }

    @SuppressLint({"NonConstantResourceId"})
    public void onClick(View var1) {//============= Complet block commented
        int var2;
//        if ((var2 = var1.getId()) == id.button_hangup) {
//            this.onLocalHangup(var1);
//        } else if (var2 == id.button_toggle_audio) {
//            this.onLocalToggleAudioMute(var1);
//        } else if (var2 == id.button_toggle_video) {
//            this.onLocalToggleVideoMute(var1);
//        } else if (var2 == id.button_switch_camera) {
//            this.onLocalSwitchCamera(var1);
//        } else if (var2 == id.button_switch_source) {
//            this.onLocalSwitchSource(var1);
//        } else if (var2 == id.button_group_message) {
//            this.onLaunchGroupMessagingUi(var1);
//        } else if (var2 == id.button_invite_participant) {
//            this.onInvite(var1);
//        } else {
//            if (var2 == id.button_list_participants) {
//                this.onListParticipants(var1);
//            }
//
//        }
    }

    private void setupButtons(View var1) {
        if (var1 != null) {
            ImageButton var2 = (ImageButton)var1.findViewById(R.id.button_hangup);
            ImageButton var3 = (ImageButton)var1.findViewById(R.id.button_toggle_audio);
            ImageButton var4 = (ImageButton)var1.findViewById(R.id.button_toggle_video);
            ImageButton var5 = (ImageButton)var1.findViewById(R.id.button_switch_camera);
            ImageButton var6 = (ImageButton)var1.findViewById(R.id.button_switch_source);
            ImageButton var7 = (ImageButton)var1.findViewById(R.id.button_invite_participant);
            ImageButton var8 = (ImageButton)var1.findViewById(R.id.button_group_message);
            ImageButton var9 = (ImageButton)var1.findViewById(R.id.button_list_participants);
            var2.setOnClickListener(this);
            var3.setOnClickListener(this);
            var4.setOnClickListener(this);
            var5.setOnClickListener(this);
            var6.setOnClickListener(this);
            var7.setOnClickListener(this);
            var8.setOnClickListener(this);
            var9.setOnClickListener(this);
            var9.setEnabled(false);
        }
    }

    private void onLocalHangup(View var1) {
        if (this.mLocalPublisher != null) {
            (new Builder(this.getContext())).setMessage("Do you want to exit the room?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface var1, int var2) {
                    MesiboDefaultGroupCallFragment.this.getActivity().onBackPressed();
                }
            }).setNegativeButton("No", (DialogInterface.OnClickListener)null).show();
        }
    }

    private void onLocalToggleAudioMute(View var1) {
        if (this.mLocalPublisher != null) {
            this.mLocalPublisher.toggleAudioMute();
            var1.setAlpha(this.mLocalPublisher.getMuteStatus(false) ? 1.0F : 0.3F);
            this.MesiboGroupcall_OnMute(this.mLocalPublisher, this.mLocalPublisher.getMuteStatus(false), this.mLocalPublisher.getMuteStatus(true), false);
        }
    }

    private void onLocalToggleVideoMute(View var1) {
        if (this.mLocalPublisher != null) {
            this.mLocalPublisher.toggleVideoMute();
            var1.setAlpha(this.mLocalPublisher.getMuteStatus(true) ? 1.0F : 0.3F);
            this.MesiboGroupcall_OnMute(this.mLocalPublisher, this.mLocalPublisher.getMuteStatus(false), this.mLocalPublisher.getMuteStatus(true), false);
        }
    }

    private void onLocalSwitchCamera(View var1) {
        if (this.mLocalPublisher != null) {
            this.mLocalPublisher.switchCamera();
        }
    }

    private void onLocalSwitchSource(View var1) {
        if (this.mLocalPublisher != null) {
            this.mLocalPublisher.switchSource();
            int var2 = this.mLocalPublisher.getVideoSource();
            if (4 == var2) {
                var2 = R.drawable.ic_mesibo_camera_alt;//============ Image ic_mesibo_camera_alt
            } else {
                var2 = R.drawable.ic_mesibo_screen_sharing;//============ Image ic_mesibo_screen_sharing
            }

            ((ImageButton)var1).setImageResource(var2);
        }
    }

    public void onLaunchGroupMessagingUi(View var1) {
    }

    public void onInvite(View var1) {
        Intent var2;
        (var2 = new Intent("android.intent.action.SEND")).setType("text/plain");
        var2.putExtra("android.intent.extra.SUBJECT", "Invite Participant");
        var2.putExtra("android.intent.extra.TEXT", this.getInviteText());
        this.getContext().startActivity(Intent.createChooser(var2, "Invite"));
    }

    private void onListParticipants(View var1) {
    }

    private String getInviteText() {
        return "Hey, join my open-source mesibo conference room (" + this.mGroupProfile.getName() + ") from the Web or your Android or iPhone mobile phone. Use the following credentials: Room ID: " + this.mGid + ", Pin: " + this.mPins[0].pin;
    }

    private void showToastNotification(String var1, String var2) {
        if (var1 != null && !var1.isEmpty()) {
            View var3 = this.getLayoutInflater().inflate(R.layout.view_mesibotoast, (ViewGroup)this.getView().findViewById(R.id.message_toast_container));//======
            if (var2 != null && !var2.isEmpty()) {
                ((TextView)var3.findViewById(R.id.titlee)).setText(var2);//=========
            }

            ((TextView)var3.findViewById(R.id.text)).setText(var1);//===========
            Toast var4;
            (var4 = new Toast(this.getContext())).setView(var3);
            var4.setDuration(Toast.LENGTH_LONG);
            var4.setGravity(81, 0, 200);
            var4.show();
        }
    }

    private void startGroupCall() {
        synchronized(this) {
            if (!this.mResumed || this.mSettings == null) {
                return;
            }

            if (this.isGroupCallStarted) {
                return;
            }

            this.isGroupCallStarted = true;
        }

        if (this.mGroupcall != null) {
            if (this.mPins == null || 0 == this.mPins.length) {
                //this.mView.findViewById(R.id.layout_invite_participant).setVisibility(View.GONE);//=============
            }

            if (this.mPermissions.callDuration > 0L) {
                (new StringBuilder("The call Duration is limited to ")).append(this.mPermissions.callDuration / 60L).append(" minutes. You can upgrade your mesibo account to remove this limitation.");
            }

            this.mGroupcall.join(this);
            if (0L != (this.mPermissions.flags & 4L)) {
                this.mLocalPublisher = this.mGroupcall.createPublisher(0L);
                this.mLocalPublisher.setVideoSource(1, 0);
                this.mLocalPublisher.call(this.mAudio, this.mVideo, this);
                this.mLocalPublisher.setName(Mesibo.getSelfProfile().getName());
                this.mPublishers.add(this.mLocalPublisher);
            }
        }
    }

    public void onResume() {
        super.onResume();
        this.mResumed = true;
        this.startGroupCall();
    }

    public void onPause() {
        super.onPause();
    }

    public void onBackPressed() {
        if (this.mGroupcall != null) {
            this.mGroupcall.leave();
        }

    }

    public static int getParticipantPosition(ArrayList<MesiboCall.MesiboParticipant> var0, MesiboCall.MesiboParticipant var1) {
        if (var0 != null && !var0.isEmpty() && var1 != null) {
            for(int var2 = 0; var2 < var0.size(); ++var2) {
                if (((MesiboCall.MesiboParticipant)var0.get(var2)).getId() == var1.getId()) {
                    return var2;
                }
            }

            return -1;
        } else {
            return -1;
        }
    }

    public MesiboCall.MesiboParticipant removeParticipant(MesiboCall.MesiboParticipant var1) {
        MesiboParticipantViewHolder var2 = this.getViewHolder(var1);
        int var3;
        if ((var3 = getParticipantPosition(this.mPublishers, var1)) >= 0) {
            this.mPublishers.remove(var3);
            MesiboParticipantViewHolder.deleteParticipantProfile(var1);
        }

        if (var1 == this.mFullScreenParticipant) {
            this.mFullScreenParticipant = null;
        }

        if (var2 != null) {
            this.mStreams.remove(var2);
        }

        return var1;
    }

    private MesiboParticipantViewHolder getViewHolder(MesiboCall.MesiboParticipant var1) {
        return (MesiboParticipantViewHolder)var1.getUserData();
    }

    public MesiboParticipantViewHolder createViewHolder(MesiboCall.MesiboParticipant var1) {
        MesiboParticipantViewHolder var2;
        if ((var2 = this.getViewHolder(var1)) != null) {
            return var2;
        } else {
            var2 = new MesiboParticipantViewHolder(this.getActivity(), this, this.mGroupcall);
            this.mStreams.add(var2);
            var2.setParticipant(var1);
            return var2;
        }
    }

    private void showParticipantNotification(MesiboCall.MesiboParticipant var1, boolean var2) {
        String var3;
        if (var2) {
            if (var1.getSid() > 0L) {
                var3 = var1.getName() + " is sharing the screen " + var1.getSid();
            } else {
                var3 = var1.getName() + " has joined the room";
            }
        } else if (var1.getSid() > 0L) {
            var3 = var1.getName() + " has stopped sharing the screen " + var1.getSid();
        } else {
            var3 = var1.getName() + " has left the room";
        }

        this.showToastNotification(var3, "Notification");
    }

    public void MesiboGroupcall_OnPublisher(MesiboCall.MesiboParticipant var1, boolean var2) {
        (new StringBuilder("MesiboGroupcall_OnPublisher Name: ")).append(var1.getName()).append(" id: ").append(var1.getId()).append(" action ").append(var2);
        this.showParticipantNotification(var1, var2);
        if (var2) {
            this.mGroupcall.playInCallSound(this.getContext(), R.raw.mesibo_join, false);//=========
            int var3;
            if ((var3 = getParticipantPosition(this.mPublishers, var1)) >= 0) {
                this.mPublishers.set(var3, var1);
            } else {
                this.mPublishers.add(var1);
            }

            var1.call(this.mAudio, this.mVideo, this);
        } else {
            this.removeParticipant(var1);
        }
    }

    public void MesiboGroupcall_OnSubscriber(MesiboCall.MesiboParticipant var1, boolean var2) {
        (new StringBuilder("MesiboGroupcall_OnSubscriber")).append(var1.toString()).append(" action: ").append(var2);
        if (var2) {
            MesiboParticipantViewHolder.setParticipantProfile(var1);
        } else {
            MesiboParticipantViewHolder.deleteParticipantProfile(var1);
        }
    }

    public void MesiboGroupcall_OnAudioDeviceChanged(MesiboCall.AudioDevice var1, MesiboCall.AudioDevice var2) {
    }

    public void MesiboGroupcall_OnMute(MesiboCall.MesiboParticipant var1, boolean var2, boolean var3, boolean var4) {
        (new StringBuilder("MesiboGroupcall_OnMute")).append(var1.toString()).append(" audio: ").append(var2).append(" video: ").append(var3).append("remote: ").append(var4).append(" isUiThread: ").append(Mesibo.isUiThread());
        MesiboParticipantViewHolder var5;
        if ((var5 = this.getViewHolder(var1)) != null) {
            var5.refresh();
        }

    }

    public void MesiboGroupcall_OnHangup(MesiboCall.MesiboParticipant var1, int var2) {
        if (2 == var2) {
            this.showParticipantNotification(var1, false);
        }

        this.removeParticipant(var1);
        if (this.mFullScreenParticipant == null) {
            this.mGridView.setStreams(this.mStreams);
        }

    }

    public void ParticipantViewHolder_onFullScreen(MesiboCall.MesiboParticipant var1, boolean var2) {
        if (!var2) {
            this.mFullScreenParticipant = null;
            this.mGridView.setStreams(this.mStreams);
        } else {
            this.mFullScreenParticipant = var1;
            ArrayList var3;
            (var3 = new ArrayList()).add(this.getViewHolder(var1));
            this.mGridView.setStreams(var3);
        }
    }

    public void ParticipantViewHolder_onHangup(MesiboCall.MesiboParticipant var1) {
        var1.hangup();
        this.MesiboGroupcall_OnHangup(var1, 1);
    }

    public int ParticipantViewHolder_onStreamCount() {
        return this.mStreams.size();
    }

    public void MesiboGroupcall_OnConnected(MesiboCall.MesiboParticipant var1, boolean var2) {
        (new StringBuilder("MesiboGroupcall_OnConnected Name: ")).append(var1.getName()).append(" Address: ").append(var1.getAddress());
    }

    public void MesiboGroupcall_OnTalking(MesiboCall.MesiboParticipant var1, boolean var2) {
        MesiboParticipantViewHolder var3 = this.getViewHolder(var1);
        if (this.mStreams.size() > 6 && Mesibo.getTimestamp() - this.mTalkTs > 5000L) {
            this.mTalkTs = Mesibo.getTimestamp();
            this.mGridView.setStreams(this.mStreams);
        } else {
            if (var3 != null) {
                var3.refresh();
            }

        }
    }

    public void MesiboGroupcall_OnVideoSourceChanged(MesiboCall.MesiboParticipant var1, int var2, int var3) {
    }

    public void MesiboGroupcall_OnVideo(MesiboCall.MesiboParticipant var1, float var2, boolean var3) {
        (new StringBuilder("MesiboGroupcall_OnVideo Name: ")).append(var1.getName()).append(" isMe:").append(var1.isMe()).append(" id: ").append(var1.getId()).append(" Address: ").append(var1.getAddress()).append(" aspect ratio: ").append(var2).append(" hasVideo: ").append(var1.hasVideo()).append(" landscape: ").append(var1.isVideoLandscape()).append(" isVideoCall: ").append(var1.isVideoCall());
        this.createViewHolder(var1).setVideo(true);
        this.mGridView.setStreams(this.mStreams);
    }

    public void MesiboGroupcall_OnAudio(MesiboCall.MesiboParticipant var1) {
        (new StringBuilder("MesiboGroupcall_OnAudio Name: ")).append(var1.getName()).append(" isMe:").append(var1.isMe()).append(" id: ").append(var1.getId()).append(" Address: ").append(var1.getAddress()).append(" hasVideo: ").append(var1.hasVideo()).append(" landscape: ").append(var1.isVideoLandscape()).append(" videoMuted: ").append(var1.getMuteStatus(true)).append(" audioMuted: ").append(var1.getMuteStatus(false));
        MesiboParticipantViewHolder var2;
        if ((var2 = this.getViewHolder(var1)) == null) {
            this.createViewHolder(var1).setAudio(true);
            this.mGridView.setStreams(this.mStreams);
        } else {
            var2.setAudio(true);
        }
    }

    public void Mesibo_onGroupCreated(MesiboProfile var1) {
    }

    public void Mesibo_onGroupJoined(MesiboProfile var1) {
    }

    public void Mesibo_onGroupLeft(MesiboProfile var1) {
    }

    public void Mesibo_onGroupMembers(MesiboProfile var1, Member[] var2) {
    }

    public void Mesibo_onGroupMembersJoined(MesiboProfile var1, Member[] var2) {
    }

    public void Mesibo_onGroupMembersRemoved(MesiboProfile var1, Member[] var2) {
    }

    public void Mesibo_onGroupSettings(MesiboProfile var1, GroupSettings var2, MemberPermissions var3, GroupPin[] var4) {
        this.mSettings = var2;
        this.mPermissions = var3;
        this.mPins = var4;
        this.startGroupCall();
    }

    public void Mesibo_onGroupError(MesiboProfile var1, long var2) {
    }

    public boolean Mesibo_onMessage(MessageParams var1, byte[] var2) {
        try {
            String var5;
            if ((var5 = new String(var2, "UTF-8")).isEmpty()) {
                return false;
            }

            MesiboProfile var3 = Mesibo.getProfile(var1.peer);
            String var6;
            if (var1.groupid > 0L) {
                var6 = "New Group Message from " + var3.getName();
            } else {
                var6 = "New Message from " + var3.getName();
            }

            if (var1.groupid != this.mGid) {
                return false;
            }

            this.showToastNotification(var5, var6);
        } catch (Exception var4) {
        }

        return true;
    }

    public void Mesibo_onMessageStatus(MessageParams var1) {
        if (!var1.peer.isEmpty() && var1.mid != 0L) {
            (new StringBuilder("Mesibo_onMessageStatus: ")).append(var1.getStatus()).append(" id: ").append(var1.mid).append(" peer: ").append(var1.peer);
        }
    }

    public void Mesibo_onActivity(MessageParams var1, int var2) {
    }

    public void Mesibo_onLocation(MessageParams var1, Location var2) {
    }

    public void Mesibo_onFile(MessageParams var1, FileInfo var2) {
    }
}
*/
