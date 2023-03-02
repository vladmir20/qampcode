package com.qamp.app.qampCalls;

import com.mesibo.api.Mesibo;

public class ParticipantAdmin implements MesiboCall.MesiboParticipantAdmin {
    ParticipantBase mParticipant;
    GroupCall mGc;

    protected ParticipantAdmin(GroupCall var1, ParticipantBase var2) {
        this.mGc = var1;
        this.mParticipant = var2;
    }

    public void mute(boolean var1, boolean var2, boolean var3) {
        if (!this.mParticipant.isMe()) {
            MesiboCall.MesiboParticipant[] var4;
            (var4 = new MesiboCall.MesiboParticipant[1])[0] = this.mParticipant;
            if (this.mParticipant.isPublisher()) {
                this.mGc.admin(101, var4, 0L, 0L, 0L, 0, var1, var2, var3);
            } else {
                this.mGc.admin(101, var4, Mesibo.getUid(), this.mParticipant.getSid(), 0L, 0, var1, var2, var3);
            }
        }
    }

    public boolean toggleAudioMute() {
        boolean var1 = this.mParticipant.getSentAdminMuteStatus(false);
        this.mute(true, false, !var1);
        return this.mParticipant.getSentAdminMuteStatus(false);
    }

    public boolean toggleVideoMute() {
        boolean var1 = this.mParticipant.getSentAdminMuteStatus(true);
        this.mute(false, true, !var1);
        return this.mParticipant.getSentAdminMuteStatus(true);
    }

    public void hangup() {
        if (!this.mParticipant.isMe()) {
            MesiboCall.MesiboParticipant[] var1;
            (var1 = new MesiboCall.MesiboParticipant[1])[0] = this.mParticipant;
            if (this.mParticipant.isPublisher()) {
                this.mGc.admin(102, var1, 0L, this.mParticipant.getSid(), 0L, 0, false, false, false);
            } else {
                this.mGc.admin(101, var1, Mesibo.getUid(), this.mParticipant.getSid(), 0L, 0, false, false, false);
            }
        }
    }

    public void publish(long var1, long var3, int var5, boolean var6, boolean var7) {
        if (!this.mParticipant.isMe()) {
            MesiboCall.MesiboParticipant[] var8;
            (var8 = new MesiboCall.MesiboParticipant[1])[0] = this.mParticipant;
            this.mGc.admin(102, var8, 0L, var1, var3, var5, var6, var7, true);
        }
    }

    public void subscribe(MesiboCall.MesiboParticipant var1, boolean var2, boolean var3) {
        if (!this.mParticipant.isMe()) {
            MesiboCall.MesiboParticipant[] var4;
            (var4 = new MesiboCall.MesiboParticipant[1])[0] = this.mParticipant;
            this.mGc.admin(103, var4, var1.getUid(), var1.getSid(), 0L, 0, var2, var3, true);
        }
    }
}

