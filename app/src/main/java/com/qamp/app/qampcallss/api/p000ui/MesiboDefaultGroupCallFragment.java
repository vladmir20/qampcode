/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 3:15 PM
 *
 */

package com.qamp.app.qampcallss.api.p000ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboGroupProfile;
import com.mesibo.api.MesiboMessage;
import com.mesibo.api.MesiboProfile;
import com.mesibo.calls.api.MesiboCall;
import com.mesibo.calls.api.MesiboCallActivity;
import com.mesibo.calls.api.R;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/* renamed from: com.mesibo.calls.api.ui.MesiboDefaultGroupCallFragment */
public class MesiboDefaultGroupCallFragment extends Fragment implements View.OnClickListener, Mesibo.GroupListener, Mesibo.MessageListener, MesiboCall.GroupCallInProgressListener, MesiboCall.GroupCallListener, MesiboParticipantViewHolder.Listener {
    public static final String TAG = "MesiboDefaultCallFragment";
    private boolean isGroupCallStarted = false;
    private boolean mAudio = true;
    private MesiboCall.MesiboParticipant mFullScreenParticipant = null;
    private long mGid = 0;
    private MesiboGroupCallView mGridView = null;
    private MesiboProfile mGroupProfile = null;
    private MesiboCall.MesiboGroupCall mGroupcall = null;
    private MesiboCall.MesiboParticipant mLocalPublisher = null;
    private MesiboGroupProfile.MemberPermissions mPermissions = null;
    private MesiboGroupProfile.GroupPin[] mPins = null;
    public ArrayList<MesiboCall.MesiboParticipant> mPublishers = new ArrayList<>();
    private boolean mResumed = false;
    private MesiboGroupProfile.GroupSettings mSettings = null;
    private ArrayList<MesiboParticipantViewHolder> mStreams = new ArrayList<>();
    private long mTalkTs = 0;
    private boolean mVideo = true;
    private View mView = null;
    private ConcurrentLinkedQueue<MesiboParticipantViewHolder> mViewHolders = new ConcurrentLinkedQueue<>();

    private String getInviteText() {
        return (this.mGroupProfile == null || this.mPins == null || this.mPins.length == 0) ? "" : "Hey, join my open-source mesibo conference room (" + this.mGroupProfile.getName() + ") from the Web or your Android or iPhone mobile phone. Use the following credentials: Room ID: " + this.mGid + ", Pin: " + this.mPins[0].pin;
    }

    public static int getParticipantPosition(ArrayList<MesiboCall.MesiboParticipant> arrayList, MesiboCall.MesiboParticipant mesiboParticipant) {
        if (arrayList == null || arrayList.isEmpty() || mesiboParticipant == null) {
            return -1;
        }
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= arrayList.size()) {
                return -1;
            }
            if (arrayList.get(i2).getId() == mesiboParticipant.getId()) {
                return i2;
            }
            i = i2 + 1;
        }
    }

    private MesiboParticipantViewHolder getViewHolder(MesiboCall.MesiboParticipant mesiboParticipant) {
        return (MesiboParticipantViewHolder) mesiboParticipant.getUserData();
    }

    private void onListParticipants(View view) {
    }

    private void onLocalHangup(View view) {
        if (this.mLocalPublisher != null) {
            new AlertDialog.Builder(getContext()).setMessage("Do you want to exit the room?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    MesiboDefaultGroupCallFragment.this.getActivity().onBackPressed();
                }
            }).setNegativeButton("No", (DialogInterface.OnClickListener) null).show();
        }
    }

    private void onLocalSwitchCamera(View view) {
        if (this.mLocalPublisher != null) {
            this.mLocalPublisher.switchCamera();
        }
    }

    private void onLocalSwitchSource(View view) {
        if (this.mLocalPublisher != null) {
            this.mLocalPublisher.switchSource();
            ((ImageButton) view).setImageResource(4 == this.mLocalPublisher.getVideoSource() ? R.drawable.ic_mesibo_camera_alt : R.drawable.ic_mesibo_screen_sharing);
        }
    }

    private void onLocalToggleAudioMute(View view) {
        if (this.mLocalPublisher != null) {
            this.mLocalPublisher.toggleAudioMute();
            view.setAlpha(this.mLocalPublisher.getMuteStatus(false) ? 1.0f : 0.3f);
            MesiboGroupcall_OnMute(this.mLocalPublisher, this.mLocalPublisher.getMuteStatus(false), this.mLocalPublisher.getMuteStatus(true), false);
        }
    }

    private void onLocalToggleVideoMute(View view) {
        if (this.mLocalPublisher != null) {
            this.mLocalPublisher.toggleVideoMute();
            view.setAlpha(this.mLocalPublisher.getMuteStatus(true) ? 1.0f : 0.3f);
            MesiboGroupcall_OnMute(this.mLocalPublisher, this.mLocalPublisher.getMuteStatus(false), this.mLocalPublisher.getMuteStatus(true), false);
        }
    }

    private void setupButtons(View view) {
        if (view != null) {
            ImageButton imageButton = (ImageButton) view.findViewById(R.id.button_list_participants);
            ((ImageButton) view.findViewById(R.id.button_hangup)).setOnClickListener(this);
            ((ImageButton) view.findViewById(R.id.button_toggle_audio)).setOnClickListener(this);
            ((ImageButton) view.findViewById(R.id.button_toggle_video)).setOnClickListener(this);
            ((ImageButton) view.findViewById(R.id.button_switch_camera)).setOnClickListener(this);
            ((ImageButton) view.findViewById(R.id.button_switch_source)).setOnClickListener(this);
            ((ImageButton) view.findViewById(R.id.button_invite_participant)).setOnClickListener(this);
            ((ImageButton) view.findViewById(R.id.button_group_message)).setOnClickListener(this);
            imageButton.setOnClickListener(this);
            imageButton.setEnabled(false);
        }
    }

    private void showParticipantNotification(MesiboCall.MesiboParticipant mesiboParticipant, boolean z) {
        showToastNotification(z ? mesiboParticipant.getSid() > 0 ? mesiboParticipant.getName() + " is sharing the screen " + mesiboParticipant.getSid() : mesiboParticipant.getName() + " has joined the room" : mesiboParticipant.getSid() > 0 ? mesiboParticipant.getName() + " has stopped sharing the screen " + mesiboParticipant.getSid() : mesiboParticipant.getName() + " has left the room", "Notification");
    }

    private void showToastNotification(String str, String str2) {
        if (str != null && !str.isEmpty()) {
            View inflate = getLayoutInflater().inflate(R.layout.view_mesibotoast, (ViewGroup) getView().findViewById(R.id.message_toast_container));
            if (str2 != null && !str2.isEmpty()) {
                ((TextView) inflate.findViewById(R.id.title)).setText(str2);
            }
            ((TextView) inflate.findViewById(R.id.text)).setText(str);
            Toast toast = new Toast(getContext());
            toast.setView(inflate);
            toast.setDuration(1);
            toast.setGravity(81, 0, 200);
            toast.show();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x001d, code lost:
        if (r9.mGroupcall == null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0021, code lost:
        if (r9.mPins == null) goto L_0x0028;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0026, code lost:
        if (r9.mPins.length != 0) goto L_0x0035;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0028, code lost:
        r9.mView.findViewById(com.mesibo.calls.api.R.id.layout_invite_participant).setVisibility(8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x003b, code lost:
        if (r9.mPermissions.callDuration <= 0) goto L_0x0054;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x003d, code lost:
        new java.lang.StringBuilder("The call Duration is limited to ").append(r9.mPermissions.callDuration / 60).append(" minutes. You can upgrade your mesibo account to remove this limitation.");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0054, code lost:
        r9.mGroupcall.join(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0062, code lost:
        if (0 == (r9.mPermissions.flags & 4)) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0064, code lost:
        r9.mLocalPublisher = r9.mGroupcall.createPublisher(0);
        r9.mLocalPublisher.setVideoSource(1, 0);
        r9.mLocalPublisher.call(r9.mAudio, r9.mVideo, r9);
        r9.mLocalPublisher.setName(com.mesibo.api.Mesibo.getSelfProfile().getName());
        r9.mPublishers.add(r9.mLocalPublisher);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void startGroupCall() {
        /*
            r9 = this;
            r8 = 1
            r6 = 0
            monitor-enter(r9)
            boolean r0 = r9.mResumed     // Catch:{ all -> 0x0014 }
            if (r0 == 0) goto L_0x000c
            com.mesibo.api.MesiboGroupProfile$GroupSettings r0 = r9.mSettings     // Catch:{ all -> 0x0014 }
            if (r0 != 0) goto L_0x000e
        L_0x000c:
            monitor-exit(r9)     // Catch:{ all -> 0x0014 }
        L_0x000d:
            return
        L_0x000e:
            boolean r0 = r9.isGroupCallStarted     // Catch:{ all -> 0x0014 }
            if (r0 == 0) goto L_0x0017
            monitor-exit(r9)     // Catch:{ all -> 0x0014 }
            goto L_0x000d
        L_0x0014:
            r0 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x0014 }
            throw r0
        L_0x0017:
            r0 = 1
            r9.isGroupCallStarted = r0     // Catch:{ all -> 0x0014 }
            monitor-exit(r9)     // Catch:{ all -> 0x0014 }
            com.mesibo.calls.api.MesiboCall$MesiboGroupCall r0 = r9.mGroupcall
            if (r0 == 0) goto L_0x000d
            com.mesibo.api.MesiboGroupProfile$GroupPin[] r0 = r9.mPins
            if (r0 == 0) goto L_0x0028
            com.mesibo.api.MesiboGroupProfile$GroupPin[] r0 = r9.mPins
            int r0 = r0.length
            if (r0 != 0) goto L_0x0035
        L_0x0028:
            android.view.View r0 = r9.mView
            int r1 = com.mesibo.calls.api.R.id.layout_invite_participant
            android.view.View r0 = r0.findViewById(r1)
            r1 = 8
            r0.setVisibility(r1)
        L_0x0035:
            com.mesibo.api.MesiboGroupProfile$MemberPermissions r0 = r9.mPermissions
            long r0 = r0.callDuration
            int r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r0 <= 0) goto L_0x0054
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r1 = "The call Duration is limited to "
            r0.<init>(r1)
            com.mesibo.api.MesiboGroupProfile$MemberPermissions r1 = r9.mPermissions
            long r2 = r1.callDuration
            r4 = 60
            long r2 = r2 / r4
            java.lang.StringBuilder r0 = r0.append(r2)
            java.lang.String r1 = " minutes. You can upgrade your mesibo account to remove this limitation."
            r0.append(r1)
        L_0x0054:
            com.mesibo.calls.api.MesiboCall$MesiboGroupCall r0 = r9.mGroupcall
            r0.join(r9)
            com.mesibo.api.MesiboGroupProfile$MemberPermissions r0 = r9.mPermissions
            long r0 = r0.flags
            r2 = 4
            long r0 = r0 & r2
            int r0 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r0 == 0) goto L_0x000d
            com.mesibo.calls.api.MesiboCall$MesiboGroupCall r0 = r9.mGroupcall
            com.mesibo.calls.api.MesiboCall$MesiboParticipant r0 = r0.createPublisher(r6)
            r9.mLocalPublisher = r0
            com.mesibo.calls.api.MesiboCall$MesiboParticipant r0 = r9.mLocalPublisher
            r1 = 0
            r0.setVideoSource(r8, r1)
            com.mesibo.calls.api.MesiboCall$MesiboParticipant r0 = r9.mLocalPublisher
            boolean r1 = r9.mAudio
            boolean r2 = r9.mVideo
            r0.call(r1, r2, r9)
            com.mesibo.calls.api.MesiboCall$MesiboParticipant r0 = r9.mLocalPublisher
            com.mesibo.api.MesiboSelfProfile r1 = com.mesibo.api.Mesibo.getSelfProfile()
            java.lang.String r1 = r1.getName()
            r0.setName(r1)
            java.util.ArrayList<com.mesibo.calls.api.MesiboCall$MesiboParticipant> r0 = r9.mPublishers
            com.mesibo.calls.api.MesiboCall$MesiboParticipant r1 = r9.mLocalPublisher
            r0.add(r1)
            goto L_0x000d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mesibo.calls.api.p000ui.MesiboDefaultGroupCallFragment.startGroupCall():void");
    }

    public void MesiboGroupcall_OnAudio(MesiboCall.MesiboParticipant mesiboParticipant) {
        new StringBuilder("MesiboGroupcall_OnAudio Name: ").append(mesiboParticipant.getName()).append(" isMe:").append(mesiboParticipant.isMe()).append(" id: ").append(mesiboParticipant.getId()).append(" Address: ").append(mesiboParticipant.getAddress()).append(" hasVideo: ").append(mesiboParticipant.hasVideo()).append(" landscape: ").append(mesiboParticipant.isVideoLandscape()).append(" videoMuted: ").append(mesiboParticipant.getMuteStatus(true)).append(" audioMuted: ").append(mesiboParticipant.getMuteStatus(false));
        MesiboParticipantViewHolder viewHolder = getViewHolder(mesiboParticipant);
        if (viewHolder == null) {
            createViewHolder(mesiboParticipant).setAudio(true);
            this.mGridView.setStreams(this.mStreams);
            return;
        }
        viewHolder.setAudio(true);
    }

    public void MesiboGroupcall_OnAudioDeviceChanged(MesiboCall.AudioDevice audioDevice, MesiboCall.AudioDevice audioDevice2) {
    }

    public void MesiboGroupcall_OnConnected(MesiboCall.MesiboParticipant mesiboParticipant, boolean z) {
        new StringBuilder("MesiboGroupcall_OnConnected Name: ").append(mesiboParticipant.getName()).append(" Address: ").append(mesiboParticipant.getAddress());
    }

    public void MesiboGroupcall_OnHangup(MesiboCall.MesiboParticipant mesiboParticipant, int i) {
        if (2 == i) {
            showParticipantNotification(mesiboParticipant, false);
        }
        removeParticipant(mesiboParticipant);
        if (this.mFullScreenParticipant == null) {
            this.mGridView.setStreams(this.mStreams);
        }
    }

    public void MesiboGroupcall_OnMute(MesiboCall.MesiboParticipant mesiboParticipant, boolean z, boolean z2, boolean z3) {
        new StringBuilder("MesiboGroupcall_OnMute").append(mesiboParticipant.toString()).append(" audio: ").append(z).append(" video: ").append(z2).append("remote: ").append(z3).append(" isUiThread: ").append(Mesibo.isUiThread());
        MesiboParticipantViewHolder viewHolder = getViewHolder(mesiboParticipant);
        if (viewHolder != null) {
            viewHolder.refresh();
        }
    }

    public void MesiboGroupcall_OnPublisher(MesiboCall.MesiboParticipant mesiboParticipant, boolean z) {
        new StringBuilder("MesiboGroupcall_OnPublisher Name: ").append(mesiboParticipant.getName()).append(" id: ").append(mesiboParticipant.getId()).append(" action ").append(z);
        showParticipantNotification(mesiboParticipant, z);
        if (z) {
            this.mGroupcall.playInCallSound(getContext(), R.raw.mesibo_join, false);
            int participantPosition = getParticipantPosition(this.mPublishers, mesiboParticipant);
            if (participantPosition >= 0) {
                this.mPublishers.set(participantPosition, mesiboParticipant);
            } else {
                this.mPublishers.add(mesiboParticipant);
            }
            mesiboParticipant.call(this.mAudio, this.mVideo, this);
            return;
        }
        removeParticipant(mesiboParticipant);
    }

    public void MesiboGroupcall_OnSubscriber(MesiboCall.MesiboParticipant mesiboParticipant, boolean z) {
        new StringBuilder("MesiboGroupcall_OnSubscriber").append(mesiboParticipant.toString()).append(" action: ").append(z);
        if (z) {
            MesiboParticipantViewHolder.setParticipantProfile(mesiboParticipant);
        } else {
            MesiboParticipantViewHolder.deleteParticipantProfile(mesiboParticipant);
        }
    }

    public void MesiboGroupcall_OnTalking(MesiboCall.MesiboParticipant mesiboParticipant, boolean z) {
        MesiboParticipantViewHolder viewHolder = getViewHolder(mesiboParticipant);
        if (this.mStreams.size() > 6 && Mesibo.getTimestamp() - this.mTalkTs > 5000) {
            this.mTalkTs = Mesibo.getTimestamp();
            this.mGridView.setStreams(this.mStreams);
        } else if (viewHolder != null) {
            viewHolder.refresh();
        }
    }

    public void MesiboGroupcall_OnVideo(MesiboCall.MesiboParticipant mesiboParticipant, float f, boolean z) {
        new StringBuilder("MesiboGroupcall_OnVideo Name: ").append(mesiboParticipant.getName()).append(" isMe:").append(mesiboParticipant.isMe()).append(" id: ").append(mesiboParticipant.getId()).append(" Address: ").append(mesiboParticipant.getAddress()).append(" aspect ratio: ").append(f).append(" hasVideo: ").append(mesiboParticipant.hasVideo()).append(" landscape: ").append(mesiboParticipant.isVideoLandscape()).append(" isVideoCall: ").append(mesiboParticipant.isVideoCall());
        createViewHolder(mesiboParticipant).setVideo(true);
        this.mGridView.setStreams(this.mStreams);
    }

    public void MesiboGroupcall_OnVideoSourceChanged(MesiboCall.MesiboParticipant mesiboParticipant, int i, int i2) {
    }

    public void Mesibo_onGroupCreated(MesiboProfile mesiboProfile) {
    }

    public void Mesibo_onGroupError(MesiboProfile mesiboProfile, long j) {
        String str = "Group Error";
        if (3 == j) {
            str = "Incorrect group ID or the PIN";
        } else if (11 == j) {
            str = "Group Does Not Exist";
        } else if (1 == j) {
            str = "You are not a group member";
        }
        alertAndExit(str);
    }

    public void Mesibo_onGroupJoined(MesiboProfile mesiboProfile) {
    }

    public void Mesibo_onGroupLeft(MesiboProfile mesiboProfile) {
    }

    public void Mesibo_onGroupMembers(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] memberArr) {
    }

    public void Mesibo_onGroupMembersJoined(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] memberArr) {
    }

    public void Mesibo_onGroupMembersRemoved(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] memberArr) {
    }

    public void Mesibo_onGroupSettings(MesiboProfile mesiboProfile, MesiboGroupProfile.GroupSettings groupSettings, MesiboGroupProfile.MemberPermissions memberPermissions, MesiboGroupProfile.GroupPin[] groupPinArr) {
        this.mSettings = groupSettings;
        this.mPermissions = memberPermissions;
        this.mPins = groupPinArr;
        if (0 == (this.mPermissions.flags & 12)) {
            alertAndExit("You do not have permissions to publish and subscribe to this conference");
        } else {
            startGroupCall();
        }
    }

    public void Mesibo_onMessage(MesiboMessage mesiboMessage) {
        try {
            String str = mesiboMessage.message;
            if (str != null && !str.isEmpty()) {
                MesiboProfile profile = Mesibo.getProfile(mesiboMessage.peer);
                String str2 = mesiboMessage.isGroupMessage() ? "New Group Message from " + profile.getName() : "New Message from " + profile.getName();
                if (mesiboMessage.groupid == this.mGid) {
                    showToastNotification(str, str2);
                }
            }
        } catch (Exception e) {
        }
    }

    public void Mesibo_onMessageStatus(MesiboMessage mesiboMessage) {
    }

    public void Mesibo_onMessageUpdate(MesiboMessage mesiboMessage) {
    }

    public void ParticipantViewHolder_onFullScreen(MesiboCall.MesiboParticipant mesiboParticipant, boolean z) {
        if (!z) {
            this.mFullScreenParticipant = null;
            this.mGridView.setStreams(this.mStreams);
            return;
        }
        this.mFullScreenParticipant = mesiboParticipant;
        ArrayList arrayList = new ArrayList();
        arrayList.add(getViewHolder(mesiboParticipant));
        this.mGridView.setStreams(arrayList);
    }

    public void ParticipantViewHolder_onHangup(MesiboCall.MesiboParticipant mesiboParticipant) {
        mesiboParticipant.hangup();
        MesiboGroupcall_OnHangup(mesiboParticipant, 1);
    }

    public int ParticipantViewHolder_onStreamCount() {
        return this.mStreams.size();
    }

    /* access modifiers changed from: package-private */
    public void alertAndExit(String str) {
        new AlertDialog.Builder(getActivity()).setMessage(str).setCancelable(false).setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                MesiboDefaultGroupCallFragment.this.getActivity().onBackPressed();
            }
        }).show();
    }

    public MesiboParticipantViewHolder createViewHolder(MesiboCall.MesiboParticipant mesiboParticipant) {
        MesiboParticipantViewHolder viewHolder = getViewHolder(mesiboParticipant);
        if (viewHolder != null) {
            return viewHolder;
        }
        MesiboParticipantViewHolder mesiboParticipantViewHolder = new MesiboParticipantViewHolder(getActivity(), this, this.mGroupcall);
        this.mStreams.add(mesiboParticipantViewHolder);
        mesiboParticipantViewHolder.setParticipant(mesiboParticipant);
        return mesiboParticipantViewHolder;
    }

    public void onBackPressed() {
        if (this.mGroupcall != null) {
            this.mGroupcall.leave();
        }
    }

    @SuppressLint({"NonConstantResourceId"})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.button_hangup) {
            onLocalHangup(view);
        } else if (id == R.id.button_toggle_audio) {
            onLocalToggleAudioMute(view);
        } else if (id == R.id.button_toggle_video) {
            onLocalToggleVideoMute(view);
        } else if (id == R.id.button_switch_camera) {
            onLocalSwitchCamera(view);
        } else if (id == R.id.button_switch_source) {
            onLocalSwitchSource(view);
        } else if (id == R.id.button_group_message) {
            onLaunchGroupMessagingUi(view);
        } else if (id == R.id.button_invite_participant) {
            onInvite(view);
        } else if (id == R.id.button_list_participants) {
            onListParticipants(view);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mView = layoutInflater.inflate(R.layout.fragment_mesibogroupcall_weight, viewGroup, false);
        FrameLayout frameLayout = (FrameLayout) this.mView.findViewById(R.id.streams_grid);
        this.mGroupcall = MesiboCall.getInstance().groupCall((MesiboCallActivity) getActivity(), this.mGid);
        if (this.mGroupcall == null) {
            return null;
        }
        if (this.mGroupProfile == null) {
            return null;
        }
        Mesibo.addListener(this);
        this.mGroupProfile.getGroupProfile().getSettings(this);
        this.mGridView = new MesiboGroupCallView(getActivity(), frameLayout, this.mGroupcall);
        setupButtons(this.mView);
        if (this.mVideo) {
            this.mAudio = true;
        }
        return this.mView;
    }

    public void onInvite(View view) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.SUBJECT", "Invite Participant");
        intent.putExtra("android.intent.extra.TEXT", getInviteText());
        getContext().startActivity(Intent.createChooser(intent, "Invite"));
    }

    public void onLaunchGroupMessagingUi(View view) {
    }

    public void onPause() {
        MesiboDefaultGroupCallFragment.super.onPause();
    }

    public void onResume() {
        MesiboDefaultGroupCallFragment.super.onResume();
        this.mResumed = true;
        startGroupCall();
    }

    public MesiboCall.MesiboParticipant removeParticipant(MesiboCall.MesiboParticipant mesiboParticipant) {
        MesiboParticipantViewHolder viewHolder = getViewHolder(mesiboParticipant);
        int participantPosition = getParticipantPosition(this.mPublishers, mesiboParticipant);
        if (participantPosition >= 0) {
            this.mPublishers.remove(participantPosition);
            MesiboParticipantViewHolder.deleteParticipantProfile(mesiboParticipant);
        }
        if (mesiboParticipant == this.mFullScreenParticipant) {
            this.mFullScreenParticipant = null;
        }
        if (viewHolder != null) {
            this.mStreams.remove(viewHolder);
        }
        return mesiboParticipant;
    }

    /* access modifiers changed from: protected */
    public void setGroup(long j, boolean z, boolean z2) {
        this.mGid = j;
        this.mVideo = z;
        this.mAudio = z2;
        this.mGroupProfile = Mesibo.getProfile(j);
    }
}
