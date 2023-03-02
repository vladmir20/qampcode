package com.qamp.app.qampCalls;


import android.content.Context;
import android.media.MediaPlayer;

import com.mesibo.api.Mesibo;
import com.mesibo.api.Mesibo.ConfListener;
import com.mesibo.api.Mesibo.GroupListener;
import com.mesibo.api.MesiboGroupProfile.GroupPin;
import com.mesibo.api.MesiboGroupProfile.GroupSettings;
import com.mesibo.api.MesiboGroupProfile.Member;
import com.mesibo.api.MesiboGroupProfile.MemberPermissions;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.Utilss;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class GroupCall implements ConfListener, GroupListener, MesiboCall.MesiboGroupCall, MesiboCall.MesiboGroupCallAdmin {
    private static final String TAG = "GroupCall";
    private HashMap<Long, ParticipantBase> mPublishers = new HashMap();
    private HashMap<Long, ParticipantBase> mSubscribers = new HashMap();
    private WeakReference<MesiboCall.GroupCallListener> mListener = null;
    private WeakReference<MesiboCall.GroupCallAdminListener> mAdminListener = null;
    private long mGid = 0L;
    private QampCallActivity mActivity = null;
    private Utilss.PowerAndWifiLock mWakeLock = null;
    private RtcAudioManager m_am = null;
    private boolean mStarted = false;
    private ParticipantBase mActivePublisher = null;
    private GroupSettings mSettings = null;
    private MemberPermissions mPermissions = null;
    private GroupPin[] mPins = null;
    private MesiboProfile mProfile = null;
    private MediaPlayer mMediaPlayer = null;

    protected GroupCall(QampCallActivity var1, long var2) {
        this.mActivity = var1;
        this.mGid = var2;
        this.mProfile = Mesibo.getProfile(var2);
    }

    protected QampCallActivity getActivity() {
        return this.mActivity;
    }

    protected void setActivePublisher(ParticipantBase var1) {
        this.mActivePublisher = var1;
    }

    protected ParticipantBase getActivePublisher() {
        return this.mActivePublisher;
    }

    public void join(MesiboCall.GroupCallListener var1, MesiboCall.GroupCallAdminListener var2) {
        this.mListener = new WeakReference(var1);
        if (var2 != null) {
            this.mAdminListener = new WeakReference(var2);
        }

        if (!this.mStarted && 0L != this.mGid) {
            this.mStarted = true;
            this.mProfile.getGroupProfile().getSettings(this);
            Mesibo.addListener(this);
            this.mWakeLock = Utilss.createPowerAndWifiLock(this.mActivity);
            this.startAudioManager();
            Mesibo.groupcall_start(this.mGid);
        }
    }

    public void join(MesiboCall.GroupCallListener var1) {
        this.join(var1, (MesiboCall.GroupCallAdminListener)null);
    }

    public void leave() {
        if (this.mStarted) {
            Mesibo.groupcall_stop(this.mGid);
            ParticipantBase[] var1 = new ParticipantBase[this.mPublishers.size() + 5];
            int var2 = 0;

            ParticipantBase var4;
            for(Iterator var3 = this.mPublishers.entrySet().iterator(); var3.hasNext(); var1[var2++] = var4) {
                var4 = (ParticipantBase)((Entry)var3.next()).getValue();
            }

            for(int var5 = 0; var5 < var2; ++var5) {
                var1[var5].onGroupCallStopped();
            }

            this.mGid = 0L;
            this.mPublishers.clear();
            this.mSubscribers.clear();
            this.mListener = null;
            this.mAdminListener = null;
            if (this.m_am != null) {
                this.m_am.stop();
                this.m_am = null;
            }

            Utilss.releasePowerAndWifiLock(this.mWakeLock);
            CallManager.getInstance().groupCallStopped();
        }
    }

    private void startAudioManager() {
        this.m_am = RtcAudioManager.create(CallManager.getAppContext(), true, false);
        this.m_am.setDefaultAudioDevice(MesiboCall.AudioDevice.SPEAKER);
        this.m_am.start(new RtcAudioManager.AudioManagerListener() {
            public void onAudioDeviceChanged(MesiboCall.AudioDevice var1, Set<MesiboCall.AudioDevice> var2) {
            }
        });
    }

    public void setAudioDevice(MesiboCall.AudioDevice var1, boolean var2) {
    }

    public MesiboCall.MesiboParticipant createPublisher(long var1) {
        if (this.getPublisher(0L, var1) != null) {
            return null;
        } else {
            ParticipantBase var3;
            (var3 = new ParticipantBase(0L, var1, true, "", "", 7L)).setGroupCall(this);
            this.addParticipant(0L, var1, true, var3);
            Mesibo.groupcall_create_participant(var1, 7L);
            return var3;
        }
    }

    public void stoppedPublishing(long var1) {
        if (this.getPublisher(0L, var1) != null) {
            this.mPublishers.remove(this.key(0L, var1));
        }
    }

    protected void hangup_internal(ParticipantBase var1) {
        Mesibo.groupcall_hangup(var1.mUid, var1.mSid);
        if (0L == var1.mUid) {
            this.stoppedPublishing(var1.mSid);
        }

    }

    private Long key(long var1, long var3) {
        return new Long(var3 << 32 | var1);
    }

    private ParticipantBase getPublisher(long var1, long var3) {
        return (ParticipantBase)this.mPublishers.get(this.key(var1, var3));
    }

    private ParticipantBase getSubscriber(long var1, long var3) {
        return (ParticipantBase)this.mSubscribers.get(this.key(var1, var3));
    }

    private ParticipantBase getParticipant(long var1, long var3, boolean var5) {
        return var5 ? this.getPublisher(var1, var3) : this.getSubscriber(var1, var3);
    }

    private void removeParticipant(long var1, long var3, boolean var5) {
        ParticipantBase var6;
        if (var5 && (var6 = this.getParticipant(var1, var3, true)) != null) {
            var6.onParticipantLeft();
        }

        if (var5) {
            this.mPublishers.remove(this.key(var1, var3));
        } else {
            this.mSubscribers.remove(this.key(var1, var3));
        }
    }

    private void addParticipant(long var1, long var3, boolean var5, ParticipantBase var6) {
        if (var5) {
            this.mPublishers.put(this.key(var1, var3), var6);
        } else {
            this.mSubscribers.put(this.key(var1, var3), var6);
        }
    }

    private MesiboCall.GroupCallListener getListener() {
        if (this.mListener == null) {
            return null;
        } else {
            MesiboCall.GroupCallListener var1;
            if ((var1 = (MesiboCall.GroupCallListener)this.mListener.get()) == null) {
                this.leave();
                return null;
            } else {
                return var1;
            }
        }
    }

    private MesiboCall.GroupCallAdminListener getAdminListener() {
        MesiboCall.GroupCallAdminListener var1 = null;
        if (this.mAdminListener != null) {
            var1 = (MesiboCall.GroupCallAdminListener)this.mAdminListener.get();
        }

        return var1;
    }

    private void onAdmin(int var1, String var2, long var3, long var5, long var7, long var9, long var11, int var13, long var14, int var16) {
        boolean var18 = (var9 & 32768L) > 0L;
        boolean var4 = (var9 & 16L) > 0L;
        boolean var20 = (var9 & 32L) > 0L;
        boolean var19 = (var9 & 1L) > 0L;
        MesiboProfile var17 = Mesibo.getProfile(var2);
        if (var5 == Mesibo.getUid()) {
            var5 = 0L;
        }

        ParticipantBase var10;
        if ((var10 = this.getPublisher(var5, var7)) != null) {
            MesiboCall.GroupCallAdminListener var15 = this.getAdminListener();
            boolean var21 = !var18;
            if (101 == var1) {
                if (var15 != null) {
                    var21 = var15.MesiboGroupcallAdmin_OnMute(var17, var18, var10, var4, var20, var19);
                }

                if (!var21) {
                    var10.adminMute(var4, var20, var19);
                }
            } else if (102 == var1) {
                if (var19) {
                    if (var15 != null) {
                        var21 = var15.MesiboGroupcallAdmin_OnStartPublishing(var17, var18, var7, var4, var20, var11, var13);
                    }

                    if (!var21) {
                        if (var10 != null) {
                            var10.adminMute(var4, var20, !var19);
                        }
                    }
                } else if (var10 != null) {
                    if (var15 != null) {
                        var21 = var15.MesiboGroupcallAdmin_OnStopPublishing(var17, var18, var10);
                    }

                    if (!var21) {
                        this.mPublishers.remove(this.key(var5, var7));
                        var10.remoteHangup();
                        if (this.getListener() != null) {
                            ;
                        }
                    }
                }
            } else if (120 == var1) {
                if (var15 != null) {
                    var21 = var15.MesiboGroupcallAdmin_OnLeave(var17, var18);
                }

                if (!var21) {
                    this.leave();
                }
            } else {
                var10 = null;
                if (var5 > 0L) {
                    var10 = this.getPublisher(var5, var7);
                }

                if (var10 != null) {
                    if (103 == var1) {
                        if (var19) {
                            if (var10.isCallInProgress()) {
                                return;
                            }

                            if (var15 != null) {
                                var21 = var15.MesiboGroupcallAdmin_OnSubscribe(var17, var18, var10, var4, var20);
                            }

                            if (var21) {
                                return;
                            }
                        }

                    } else if (104 == var1) {
                        if (var10 != null && var15 != null) {
                            var15.MesiboGroupcallAdmin_OnMakeFullScreen(var17, var18, var10);
                        }

                    }
                }
            }
        }
    }

    private void onConfParitcipant(long var1, long var3, String var5, String var6, long var7, long var9) {
        boolean var11 = (var7 & 256L) > 0L;
        boolean var12 = (var7 & 1L) > 0L;
        ParticipantBase var13 = this.getParticipant(var1, var3, var11);
        if (var12 || var13 != null) {
            boolean var14 = var13 != null;
            boolean var15 = false;
            if (var12) {
                if (var13 == null) {
                    var15 = true;
                    (var13 = new ParticipantBase(var1, var3, var11, var5, var6, var9)).setGroupCall(this);
                    this.addParticipant(var1, var3, var11, var13);
                }

                var13.onUpdate(var7, var9);
                if (var14) {
                    return;
                }
            } else {
                this.removeParticipant(var1, var3, var11);
            }

            MesiboCall.GroupCallListener var16;
            if ((var16 = this.getListener()) != null) {
                if (var11) {
                    var16.MesiboGroupcall_OnPublisher(var13, var15);
                } else {
                    var16.MesiboGroupcall_OnSubscriber(var13, var15);
                }
            }
        }
    }

    public void Mesibo_onConfParitcipant(long var1, long var3, String var5, String var6, long var7, long var9) {
        if (0L != this.mGid) {
            this.onConfParitcipant(var1, var3, var5, var6, var7, var9);
        }
    }

    public void Mesibo_onConfCall(long var1, long var3, int var5, long var6, long var8, int var10, long var11, long var13, String var15, String var16, int var17) {
        if (0L != this.mGid) {
            if (var5 > 100) {
                this.onAdmin(var5, var16, var11, var1, var3, var13, var6, var10, var8, var17);
            } else {
                ParticipantBase var18;
                if (11 == var5) {
                    if ((var18 = this.getParticipant(var1, var3, true)) != null) {
                        var18.onUpdateFyi((int)var6, var16);
                    }

                } else if (41 != var5) {
                    if ((var18 = this.getPublisher(var1, var3)) != null) {
                        var18.OnConfCallStatus(var5, var8, var10, var11, var13, var15, var16, var17);
                    }
                }
            }
        }
    }

    public void playInCallSound(Context var1, int var2, boolean var3) {
        this.stopInCallSound();
        this.mMediaPlayer = CallUtils.playResource(var1, var2, var3);
    }

    public void stopInCallSound() {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.stop();
            this.mMediaPlayer = null;
        }

    }

    public ArrayList<? extends Object> sort(MesiboCall.MesiboParticipantSortListener var1, ArrayList<?> var2, float var3, float var4, int var5, int var6, MesiboCall.MesiboParticipantSortParams var7) {
        return SortUtils.sortStreams(var1, var2, var3, var4, var5, var6, var7);
    }

    public MesiboCall.MesiboGroupCallAdmin getAdmin() {
        return this.hasAdminPermissions(false) ? this : null;
    }

    public boolean hasAdminPermissions(boolean var1) {
        if (this.mPermissions == null) {
            return false;
        } else if (!var1) {
            return (this.mPermissions.adminFlags & 1024L) > 0L;
        } else {
            return (this.mPermissions.adminFlags & 2048L) > 0L;
        }
    }

    protected void admin(int var1, MesiboCall.MesiboParticipant[] var2, long var3, long var5, long var7, int var9, boolean var10, boolean var11, boolean var12) {
        if (120 == var1) {
            if (!this.hasAdminPermissions(true)) {
                return;
            }
        } else if (104 != var1 && !this.hasAdminPermissions(false)) {
            return;
        }

        long var13 = 0L;
        if (var2 == null) {
            var13 = 2L;
        }

        if (var10) {
            var13 |= 16L;
        }

        if (var11) {
            var13 |= 32L;
        }

        if (var12) {
            var13 |= 1L;
        }

        int var15 = 0;
        if (var2 != null) {
            var15 = var2.length;
        }

        long[] var16 = new long[6 + var15 * 2];
        byte var17 = 0;
        int var19 = var17 + 1;
        var16[0] = (long)var1;
        ++var19;
        var16[1] = var13;
        ++var19;
        var16[2] = var3;
        ++var19;
        var16[3] = var5;
        ++var19;
        var16[4] = var7;
        ++var19;
        var16[5] = (long)var9;

        for(int var18 = 0; var18 < var15; ++var18) {
            if (!var2[var18].isMe()) {
                var16[var19++] = var2[var18].getUid();
                var16[var19++] = var2[var18].getSid();
                if (var1 == 101) {
                    ((ParticipantBase)var2[var18]).sentAdminMute(var10, var11, var12);
                }
            }
        }

        Mesibo.groupcall_admin(var16);
    }

    protected void admin(int var1, MesiboCall.MesiboParticipant[] var2, boolean var3, boolean var4, boolean var5) {
        this.admin(var1, var2, 0L, 0L, 0L, 0, var3, var4, var5);
    }

    public void mute(MesiboCall.MesiboParticipant[] var1, boolean var2, boolean var3, boolean var4) {
        this.admin(101, var1, var2, var3, var4);
    }

    public void muteAll(boolean var1, boolean var2, boolean var3) {
        this.admin(101, (MesiboCall.MesiboParticipant[])null, var1, var2, var3);
    }

    public void hangup(MesiboCall.MesiboParticipant[] var1) {
        this.hangup(var1);
    }

    public void hangupAll() {
        this.admin(120, (MesiboCall.MesiboParticipant[])null, false, false, false);
    }

    public void fullscreen(MesiboCall.MesiboParticipant var1) {
        this.admin(104, (MesiboCall.MesiboParticipant[])null, var1.getUid(), var1.getSid(), 0L, 0, false, false, false);
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
    }

    public void Mesibo_onGroupError(MesiboProfile var1, long var2) {
    }
}

