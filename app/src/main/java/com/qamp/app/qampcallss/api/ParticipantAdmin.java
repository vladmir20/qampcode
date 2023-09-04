/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 3:15 PM
 *
 */

package com.qamp.app.qampcallss.api;

import com.mesibo.api.Mesibo;

public class ParticipantAdmin implements MesiboCall.MesiboParticipantAdmin {
    GroupCall mGc;
    ParticipantBase mParticipant;

    protected ParticipantAdmin(GroupCall groupCall, ParticipantBase participantBase) {
        this.mGc = groupCall;
        this.mParticipant = participantBase;
    }

    public void hangup() {
        if (!this.mParticipant.isMe()) {
            MesiboCall.MesiboParticipant[] mesiboParticipantArr = {this.mParticipant};
            if (this.mParticipant.isPublisher()) {
                this.mGc.admin(102, mesiboParticipantArr, 0, this.mParticipant.getSid(), 0, 0, false, false, false);
            } else {
                this.mGc.admin(101, mesiboParticipantArr, Mesibo.getUid(), this.mParticipant.getSid(), 0, 0, false, false, false);
            }
        }
    }

    public void mute(boolean z, boolean z2, boolean z3) {
        if (!this.mParticipant.isMe()) {
            MesiboCall.MesiboParticipant[] mesiboParticipantArr = {this.mParticipant};
            if (this.mParticipant.isPublisher()) {
                this.mGc.admin(101, mesiboParticipantArr, 0, 0, 0, 0, z, z2, z3);
            } else {
                this.mGc.admin(101, mesiboParticipantArr, Mesibo.getUid(), this.mParticipant.getSid(), 0, 0, z, z2, z3);
            }
        }
    }

    public void publish(long j, long j2, int i, boolean z, boolean z2) {
        if (!this.mParticipant.isMe()) {
            this.mGc.admin(102, new MesiboCall.MesiboParticipant[]{this.mParticipant}, 0, j, j2, i, z, z2, true);
        }
    }

    public void subscribe(MesiboCall.MesiboParticipant mesiboParticipant, boolean z, boolean z2) {
        if (!this.mParticipant.isMe()) {
            this.mGc.admin(103, new MesiboCall.MesiboParticipant[]{this.mParticipant}, mesiboParticipant.getUid(), mesiboParticipant.getSid(), 0, 0, z, z2, true);
        }
    }

    public boolean toggleAudioMute() {
        mute(true, false, !this.mParticipant.getSentAdminMuteStatus(false));
        return this.mParticipant.getSentAdminMuteStatus(false);
    }

    public boolean toggleVideoMute() {
        mute(false, true, !this.mParticipant.getSentAdminMuteStatus(true));
        return this.mParticipant.getSentAdminMuteStatus(true);
    }
}
