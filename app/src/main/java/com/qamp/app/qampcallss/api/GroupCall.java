package com.qamp.app.qampcallss.api;

import android.content.Context;
import android.media.MediaPlayer;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboGroupProfile;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.qampcallss.api.p000ui.QampCallsActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GroupCall implements Mesibo.ConfListener, Mesibo.GroupListener, MesiboCall.MesiboGroupCall, MesiboCall.MesiboGroupCallAdmin {
    private static final String TAG = "GroupCall";
    private ParticipantBase mActivePublisher = null;
    private QampCallsActivity mActivity = null;
    private WeakReference<MesiboCall.GroupCallAdminListener> mAdminListener = null;
    private long mGid = 0;
    private WeakReference<MesiboCall.GroupCallListener> mListener = null;
    private MediaPlayer mMediaPlayer = null;
    private MesiboGroupProfile.MemberPermissions mPermissions = null;
    private MesiboGroupProfile.GroupPin[] mPins = null;
    private MesiboProfile mProfile = null;
    private HashMap<Long, ParticipantBase> mPublishers = new HashMap<>();
    private MesiboGroupProfile.GroupSettings mSettings = null;
    private boolean mStarted = false;
    private HashMap<Long, ParticipantBase> mSubscribers = new HashMap<>();
    private Utils.PowerAndWifiLock mWakeLock = null;
    private RtcAudioManager m_am = null;

    protected GroupCall(QampCallsActivity mesiboCallActivity, long j) {
        this.mActivity = mesiboCallActivity;
        this.mGid = j;
        this.mProfile = Mesibo.getProfile(j);
    }

    private void addParticipant(long j, long j2, boolean z, ParticipantBase participantBase) {
        if (z) {
            this.mPublishers.put(key(j, j2), participantBase);
        } else {
            this.mSubscribers.put(key(j, j2), participantBase);
        }
    }

    private MesiboCall.GroupCallAdminListener getAdminListener() {
        if (this.mAdminListener != null) {
            return (MesiboCall.GroupCallAdminListener) this.mAdminListener.get();
        }
        return null;
    }

    private MesiboCall.GroupCallListener getListener() {
        if (this.mListener == null) {
            return null;
        }
        MesiboCall.GroupCallListener groupCallListener = (MesiboCall.GroupCallListener) this.mListener.get();
        if (groupCallListener != null) {
            return groupCallListener;
        }
        leave();
        return null;
    }

    private ParticipantBase getParticipant(long j, long j2, boolean z) {
        return z ? getPublisher(j, j2) : getSubscriber(j, j2);
    }

    private ParticipantBase getPublisher(long j, long j2) {
        return this.mPublishers.get(key(j, j2));
    }

    private ParticipantBase getSubscriber(long j, long j2) {
        return this.mSubscribers.get(key(j, j2));
    }

    private Long key(long j, long j2) {
        return new Long((j2 << 32) | j);
    }

    private void onAdmin(int i, String str, long j, long j2, long j3, long j4, long j5, int i2, long j6, int i3) {
        boolean z = (32768 & j4) > 0;
        boolean z2 = (16 & j4) > 0;
        boolean z3 = (32 & j4) > 0;
        boolean z4 = (1 & j4) > 0;
        MesiboProfile profile = Mesibo.getProfile(str);
        if (j2 == Mesibo.getUid()) {
            j2 = 0;
        }
        ParticipantBase publisher = getPublisher(j2, j3);
        if (publisher != null) {
            MesiboCall.GroupCallAdminListener adminListener = getAdminListener();
            boolean z5 = !z;
            if (101 == i) {
                if (adminListener != null) {
                    z5 = adminListener.MesiboGroupcallAdmin_OnMute(profile, z, publisher, z2, z3, z4);
                }
                if (!z5) {
                    publisher.adminMute(z2, z3, z4);
                }
            } else if (102 == i) {
                if (z4) {
                    if (adminListener != null) {
                        z5 = adminListener.MesiboGroupcallAdmin_OnStartPublishing(profile, z, j3, z2, z3, j5, i2);
                    }
                    if (!z5 && publisher != null) {
                        publisher.adminMute(z2, z3, !z4);
                    }
                } else if (publisher != null) {
                    if (adminListener != null) {
                        z5 = adminListener.MesiboGroupcallAdmin_OnStopPublishing(profile, z, publisher);
                    }
                    if (!z5) {
                        this.mPublishers.remove(key(j2, j3));
                        publisher.remoteHangup();
                        if (getListener() == null) {
                        }
                    }
                }
            } else if (120 == i) {
                if (adminListener != null) {
                    z5 = adminListener.MesiboGroupcallAdmin_OnLeave(profile, z);
                }
                if (!z5) {
                    leave();
                }
            } else {
                ParticipantBase participantBase = null;
                if (j2 > 0) {
                    participantBase = getPublisher(j2, j3);
                }
                if (participantBase == null) {
                    return;
                }
                if (103 == i) {
                    if (z4 && !participantBase.isCallInProgress()) {
                        if (adminListener != null ? adminListener.MesiboGroupcallAdmin_OnSubscribe(profile, z, participantBase, z2, z3) : z5) {
                        }
                    }
                } else if (104 == i && participantBase != null && adminListener != null) {
                    adminListener.MesiboGroupcallAdmin_OnMakeFullScreen(profile, z, participantBase);
                }
            }
        }
    }

    private void onConfParitcipant(long j, long j2, String str, String str2, long j3, long j4) {
        boolean z;
        ParticipantBase participantBase;
        boolean z2 = (256 & j3) > 0;
        boolean z3 = (1 & j3) > 0;
        ParticipantBase participant = getParticipant(j, j2, z2);
        if (z3 || participant != null) {
            boolean z4 = participant != null;
            if (z3) {
                if (participant == null) {
                    z = true;
                    participantBase = new ParticipantBase(j, j2, z2, str, str2, j4);
                    participantBase.setGroupCall(this);
                    addParticipant(j, j2, z2, participantBase);
                } else {
                    z = false;
                    participantBase = participant;
                }
                participantBase.onUpdate(j3, j4);
                if (z4) {
                    return;
                }
            } else {
                removeParticipant(j, j2, z2);
                z = false;
                participantBase = participant;
            }
            MesiboCall.GroupCallListener listener = getListener();
            if (listener == null) {
                return;
            }
            if (z2) {
                listener.MesiboGroupcall_OnPublisher(participantBase, z);
            } else {
                listener.MesiboGroupcall_OnSubscriber(participantBase, z);
            }
        }
    }

    private void removeParticipant(long j, long j2, boolean z) {
        ParticipantBase participant;
        if (z && (participant = getParticipant(j, j2, true)) != null) {
            participant.onParticipantLeft();
        }
        if (z) {
            this.mPublishers.remove(key(j, j2));
        } else {
            this.mSubscribers.remove(key(j, j2));
        }
    }

    private void startAudioManager() {
        this.m_am = RtcAudioManager.create(CallManager.getAppContext(), true, false);
        this.m_am.setDefaultAudioDevice(MesiboCall.AudioDevice.SPEAKER);
        this.m_am.start(new RtcAudioManager.AudioManagerListener() {
            @Override
            public void onAudioDeviceChanged(MesiboCall.AudioDevice audioDevice, Set<MesiboCall.AudioDevice> set) {

            }


        });
    }

    public void Mesibo_onConfCall(long j, long j2, int i, long j3, long j4, int i2, long j5, long j6, String str, String str2, int i3) {
        ParticipantBase publisher;
        if (0 != this.mGid) {
            if (i > 100) {
                onAdmin(i, str2, j5, j, j2, j6, j3, i2, j4, i3);
            } else if (11 == i) {
                ParticipantBase participant = getParticipant(j, j2, true);
                if (participant != null) {
                    participant.onUpdateFyi((int) j3, str2);
                }
            } else if (41 != i && (publisher = getPublisher(j, j2)) != null) {
                publisher.OnConfCallStatus(i, j4, i2, j5, j6, str, str2, i3);
            }
        }
    }

    public void Mesibo_onConfParitcipant(long j, long j2, String str, String str2, long j3, long j4) {
        if (0 != this.mGid) {
            onConfParitcipant(j, j2, str, str2, j3, j4);
        }
    }

    public void Mesibo_onGroupCreated(MesiboProfile mesiboProfile) {
    }

    public void Mesibo_onGroupError(MesiboProfile mesiboProfile, long j) {
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
    }

    /* access modifiers changed from: protected */
    public void admin(int i, MesiboCall.MesiboParticipant[] mesiboParticipantArr, long j, long j2, long j3, int i2, boolean z, boolean z2, boolean z3) {
        if (120 == i) {
            if (!hasAdminPermissions(true)) {
                return;
            }
        } else if (104 != i && !hasAdminPermissions(false)) {
            return;
        }
        long j4 = 0;
        if (mesiboParticipantArr == null) {
            j4 = 2;
        }
        if (z) {
            j4 |= 16;
        }
        if (z2) {
            j4 |= 32;
        }
        long j5 = z3 ? j4 | 1 : j4;
        int length = mesiboParticipantArr != null ? mesiboParticipantArr.length : 0;
        long[] jArr = new long[((length * 2) + 6)];
        jArr[0] = (long) i;
        jArr[1] = j5;
        jArr[2] = j;
        jArr[3] = j2;
        jArr[4] = j3;
        int i3 = 6;
        jArr[5] = (long) i2;
        for (int i4 = 0; i4 < length; i4++) {
            if (!mesiboParticipantArr[i4].isMe()) {
                int i5 = i3 + 1;
                jArr[i3] = mesiboParticipantArr[i4].getUid();
                int i6 = i5 + 1;
                jArr[i5] = mesiboParticipantArr[i4].getSid();
                if (i == 101) {
                    ((ParticipantBase)mesiboParticipantArr[i4]).sentAdminMute(z, z2, z3);
                }
                i3 = i6;
            }
        }
        Mesibo.groupcall_admin(jArr);
    }

    /* access modifiers changed from: protected */
    public void admin(int i, MesiboCall.MesiboParticipant[] mesiboParticipantArr, boolean z, boolean z2, boolean z3) {
        admin(i, mesiboParticipantArr, 0, 0, 0, 0, z, z2, z3);
    }

    public MesiboCall.MesiboParticipant createPublisher(long j) {
        if (getPublisher(0, j) != null) {
            return null;
        }
        ParticipantBase participantBase = new ParticipantBase(0, j, true, "", "", 7);
        participantBase.setGroupCall(this);
        addParticipant(0, j, true, participantBase);
        Mesibo.groupcall_create_participant(j, 7);
        return participantBase;
    }

    public void fullscreen(MesiboCall.MesiboParticipant mesiboParticipant) {
        admin(104, (MesiboCall.MesiboParticipant[]) null, mesiboParticipant.getUid(), mesiboParticipant.getSid(), 0, 0, false, false, false);
    }

    /* access modifiers changed from: protected */
    public ParticipantBase getActivePublisher() {
        return this.mActivePublisher;
    }

    /* access modifiers changed from: protected */
    public QampCallsActivity getActivity() {
        return this.mActivity;
    }

    public MesiboCall.MesiboGroupCallAdmin getAdmin() {
        if (hasAdminPermissions(false)) {
            return this;
        }
        return null;
    }

    public void hangup(MesiboCall.MesiboParticipant[] mesiboParticipantArr) {
        hangup(mesiboParticipantArr);
    }

    public void hangupAll() {
        admin(120, (MesiboCall.MesiboParticipant[]) null, false, false, false);
    }

    /* access modifiers changed from: protected */
    public void hangup_internal(ParticipantBase participantBase) {
        Mesibo.groupcall_hangup(participantBase.mUid, participantBase.mSid);
        if (0 == participantBase.mUid) {
            stoppedPublishing(participantBase.mSid);
        }
    }

    public boolean hasAdminPermissions(boolean z) {
        if (this.mPermissions == null) {
            return false;
        }
        return !z ? (this.mPermissions.adminFlags & 1024) > 0 : (this.mPermissions.adminFlags & 2048) > 0;
    }

    public void join(MesiboCall.GroupCallListener groupCallListener) {
        join(groupCallListener, (MesiboCall.GroupCallAdminListener) null);
    }

    /* JADX WARNING: type inference failed for: r0v7, types: [android.content.Context, com.mesibo.calls.api.MesiboCallActivity] */
    public void join(MesiboCall.GroupCallListener groupCallListener, MesiboCall.GroupCallAdminListener groupCallAdminListener) {
        this.mListener = new WeakReference<>(groupCallListener);
        if (groupCallAdminListener != null) {
            this.mAdminListener = new WeakReference<>(groupCallAdminListener);
        }
        if (!this.mStarted && 0 != this.mGid) {
            this.mStarted = true;
            this.mProfile.getGroupProfile().getSettings(this);
            Mesibo.addListener(this);
            this.mWakeLock = Utils.createPowerAndWifiLock(this.mActivity);
            startAudioManager();
            Mesibo.groupcall_start(this.mGid);
        }
    }

    public void leave() {
        if (!this.mStarted) {
            CallManager.getInstance().groupCallStopped(this);
            return;
        }
        Mesibo.groupcall_stop(this.mGid);
        ParticipantBase[] participantBaseArr = new ParticipantBase[(this.mPublishers.size() + 5)];
        int i = 0;
        for (Map.Entry<Long, ParticipantBase> value : this.mPublishers.entrySet()) {
            participantBaseArr[i] = (ParticipantBase) value.getValue();
            i++;
        }
        for (int i2 = 0; i2 < i; i2++) {
            participantBaseArr[i2].onGroupCallStopped();
        }
        this.mGid = 0;
        this.mPublishers.clear();
        this.mSubscribers.clear();
        this.mListener = null;
        this.mAdminListener = null;
        if (this.m_am != null) {
            this.m_am.stop();
            this.m_am = null;
        }
        Utils.releasePowerAndWifiLock(this.mWakeLock);
        CallManager.getInstance().groupCallStopped();
    }

    public void mute(MesiboCall.MesiboParticipant[] mesiboParticipantArr, boolean z, boolean z2, boolean z3) {
        admin(101, mesiboParticipantArr, z, z2, z3);
    }

    public void muteAll(boolean z, boolean z2, boolean z3) {
        admin(101, (MesiboCall.MesiboParticipant[]) null, z, z2, z3);
    }

    public void playInCallSound(Context context, int i, boolean z) {
        stopInCallSound();
        this.mMediaPlayer = CallUtils.playResource(context, i, z);
    }

    /* access modifiers changed from: protected */
    public void setActivePublisher(ParticipantBase participantBase) {
        this.mActivePublisher = participantBase;
    }

    public void setAudioDevice(MesiboCall.AudioDevice audioDevice, boolean z) {
    }

    public ArrayList<? extends Object> sort(MesiboCall.MesiboParticipantSortListener mesiboParticipantSortListener, ArrayList<?> arrayList, float f, float f2, int i, int i2, MesiboCall.MesiboParticipantSortParams mesiboParticipantSortParams) {
        return SortUtils.sortStreams(mesiboParticipantSortListener, arrayList, f, f2, i, i2, mesiboParticipantSortParams);
    }

    public void stopInCallSound() {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.stop();
            this.mMediaPlayer = null;
        }
    }

    public void stoppedPublishing(long j) {
        if (getPublisher(0, j) != null) {
            this.mPublishers.remove(key(0, j));
        }
    }
}
