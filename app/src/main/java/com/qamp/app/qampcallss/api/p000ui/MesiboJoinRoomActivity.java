/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 3:15 PM
 *
 */

package com.qamp.app.qampcallss.api.p000ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboGroupProfile;
import com.mesibo.api.MesiboProfile;
import com.mesibo.calls.api.MesiboCall;
import com.mesibo.calls.api.R;

import java.util.ArrayList;

/* renamed from: com.mesibo.calls.api.ui.MesiboJoinRoomActivity */
public class MesiboJoinRoomActivity extends AppCompatActivity implements Mesibo.GroupListener {
    private static final String RESOLUTION_4K_STRING = "4K - 3840x2160";
    private static final String RESOLUTION_DEFAULT_STRING = "Default";
    private static final String RESOLUTION_FULL_HD_STRING = "Full HD - 1920x1080";
    private static final String RESOLUTION_HD_STRING = "HD - 1280x720";
    private static final String RESOLUTION_QVGA_STRING = "QVGA - 320x240";
    private static final String RESOLUTION_STANDARD_STRING = "Standard - 640x480";
    private static final String TAG = "JoinRoomActivity";
    private ArrayAdapter<String> mAdapter = null;
    private boolean mAudio = true;
    private View mCreateRoomView = null;
    private Spinner mCreateRoomsResolutions = null;
    private EditText mEnterRoomId = null;
    private EditText mEnterRoomName = null;
    private EditText mEnterRoomPin = null;
    private View mEnterRoomView = null;
    private TextView mErrorEnterRoomView = null;
    private View mMyRoomsView = null;
    private ArrayList<MesiboProfile> mRoomsArray = new ArrayList<>();
    private ArrayList<String> mRoomsList = new ArrayList<>();
    private ListView mRoomsListView;
    /* access modifiers changed from: private */
    public int mSelectedResolution = 0;
    private boolean mShowMyRooms = false;
    private boolean mVideo = true;

    private void hideError() {
        this.mErrorEnterRoomView.setVisibility(8);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.mesibo.calls.api.ui.MesiboJoinRoomActivity] */
    private void joinConferenceRoom(MesiboProfile mesiboProfile) {
        MesiboCall.getInstance().groupCallUi(this, mesiboProfile, this.mVideo, this.mAudio);
    }

    private /* synthetic */ void lambda$setAvailableRooms$0(AdapterView adapterView, View view, int i, long j) {
        joinConferenceRoom(this.mRoomsArray.get(i));
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: Unknown instruction: 'invoke-custom' in method: com.mesibo.calls.api.ui.MesiboJoinRoomActivity.setAvailableRooms():void, dex: classes.jar
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:151)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:286)
        	at jadx.core.ProcessClass.process(ProcessClass.java:36)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:58)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
        Caused by: jadx.core.utils.exceptions.DecodeException: Unknown instruction: 'invoke-custom'
        	at jadx.core.dex.instructions.InsnDecoder.decode(InsnDecoder.java:588)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:78)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:136)
        	... 4 more
        */
    private void setAvailableRooms() {
        /*
        // Can't load method instructions: Load method exception: Unknown instruction: 'invoke-custom' in method: com.mesibo.calls.api.ui.MesiboJoinRoomActivity.setAvailableRooms():void, dex: classes.jar
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mesibo.calls.api.p000ui.MesiboJoinRoomActivity.setAvailableRooms():void");
    }

    private void showError(String str) {
        this.mErrorEnterRoomView.setVisibility(0);
        this.mErrorEnterRoomView.setText(str);
    }

    public void Mesibo_onGroupCreated(MesiboProfile mesiboProfile) {
        MesiboGroupProfile.MemberPermissions memberPermissions = new MesiboGroupProfile.MemberPermissions();
        memberPermissions.flags = 31;
        mesiboProfile.getGroupProfile().addPin(memberPermissions, this);
    }

    public void Mesibo_onGroupError(MesiboProfile mesiboProfile, long j) {
        if (3 == j) {
            showError("Incorrect RoomId or the PIN");
        } else if (11 == j) {
            showError("Group Does Not Exist");
        } else if (1 == j) {
            showError("You are not a group member");
        }
    }

    public void Mesibo_onGroupJoined(MesiboProfile mesiboProfile) {
        joinConferenceRoom(mesiboProfile);
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
        joinConferenceRoom(mesiboProfile);
    }

    public void onBackPressed() {
        if (this.mShowMyRooms) {
            showJoinCreateRoom(false);
            return;
        }
        MesiboJoinRoomActivity.super.onBackPressed();
        finish();
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.mesibo.calls.api.ui.MesiboJoinRoomActivity, androidx.appcompat.app.AppCompatActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        MesiboJoinRoomActivity.super.onCreate(bundle);
        setContentView(R.layout.activity_mesibojoinroom);
        Bundle extras = getIntent().getExtras();
        String str = null;
        if (extras != null) {
            str = extras.getString("title");
        }
        ((TextView) findViewById(R.id.title)).setText(TextUtils.isEmpty(str) ? "Mesibo Conferencing" : str);
        this.mEnterRoomId = (EditText) findViewById(R.id.room_id);
        this.mEnterRoomPin = (EditText) findViewById(R.id.room_pin);
        this.mEnterRoomName = (EditText) findViewById(R.id.room_name);
        this.mEnterRoomView = findViewById(R.id.existing_room);
        this.mCreateRoomView = findViewById(R.id.create_room);
        this.mMyRoomsView = findViewById(R.id.my_rooms);
        this.mErrorEnterRoomView = (TextView) findViewById(R.id.error_enter_room);
        hideError();
        this.mRoomsListView = (ListView) findViewById(R.id.list_my_rooms);
        this.mAdapter = new ArrayAdapter<>(this, 17367043, this.mRoomsList);
        this.mRoomsListView.setAdapter(this.mAdapter);
        this.mCreateRoomsResolutions = (Spinner) findViewById(R.id.choose_resolution_options);
        final String[] strArr = {RESOLUTION_DEFAULT_STRING, RESOLUTION_STANDARD_STRING, RESOLUTION_HD_STRING, RESOLUTION_FULL_HD_STRING, RESOLUTION_4K_STRING, RESOLUTION_QVGA_STRING};
        this.mCreateRoomsResolutions.setAdapter(new ArrayAdapter(this, 17367049, strArr));
        this.mCreateRoomsResolutions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                String str = strArr[i];
                char c = 65535;
                switch (str.hashCode()) {
                    case -1492200336:
                        if (str.equals(MesiboJoinRoomActivity.RESOLUTION_STANDARD_STRING)) {
                            c = 1;
                            break;
                        }
                        break;
                    case -1085510111:
                        if (str.equals(MesiboJoinRoomActivity.RESOLUTION_DEFAULT_STRING)) {
                            c = 0;
                            break;
                        }
                        break;
                    case -951506316:
                        if (str.equals(MesiboJoinRoomActivity.RESOLUTION_4K_STRING)) {
                            c = 4;
                            break;
                        }
                        break;
                    case -812437043:
                        if (str.equals(MesiboJoinRoomActivity.RESOLUTION_HD_STRING)) {
                            c = 2;
                            break;
                        }
                        break;
                    case -17935917:
                        if (str.equals(MesiboJoinRoomActivity.RESOLUTION_QVGA_STRING)) {
                            c = 5;
                            break;
                        }
                        break;
                    case 1014414179:
                        if (str.equals(MesiboJoinRoomActivity.RESOLUTION_FULL_HD_STRING)) {
                            c = 3;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        int unused = MesiboJoinRoomActivity.this.mSelectedResolution = 0;
                        return;
                    case 1:
                        int unused2 = MesiboJoinRoomActivity.this.mSelectedResolution = 2;
                        return;
                    case 2:
                        int unused3 = MesiboJoinRoomActivity.this.mSelectedResolution = 3;
                        return;
                    case 3:
                        int unused4 = MesiboJoinRoomActivity.this.mSelectedResolution = 4;
                        return;
                    case 4:
                        int unused5 = MesiboJoinRoomActivity.this.mSelectedResolution = 5;
                        return;
                    case MesiboCall.MESIBOCALL_HANGUP_REASON_DURATION:
                        int unused6 = MesiboJoinRoomActivity.this.mSelectedResolution = 1;
                        return;
                    default:
                        return;
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void onCreateRoom(View view) {
        String obj = this.mEnterRoomName.getText().toString();
        if (!obj.isEmpty()) {
            MesiboGroupProfile.GroupSettings groupSettings = new MesiboGroupProfile.GroupSettings();
            groupSettings.name = obj;
            groupSettings.videoResolution = (long) this.mSelectedResolution;
            if (this.mVideo) {
                groupSettings.callFlags = 6;
            } else if (this.mAudio) {
                groupSettings.callFlags = 2;
            }
            groupSettings.callFlags |= 33024;
            Mesibo.createGroup(groupSettings, this);
        }
    }

    public void onEnterRoom(View view) {
        String obj = this.mEnterRoomId.getText().toString();
        String obj2 = this.mEnterRoomPin.getText().toString();
        if (!obj.isEmpty() && !obj2.isEmpty()) {
            Mesibo.getProfile((long) Integer.parseInt(obj)).getGroupProfile().join((long) Integer.parseInt(obj2), this);
        }
    }

    public void onRoomRadioButtonClicked(View view) {
        boolean isChecked = ((RadioButton) view).isChecked();
        int id = view.getId();
        if (id == R.id.radio_enter_room) {
            if (isChecked) {
                showJoinCreateRoom(false);
            }
        } else if (id == R.id.radio_create_room && isChecked) {
            showJoinCreateRoom(true);
        }
    }

    public void onShowRooms(View view) {
        this.mShowMyRooms = true;
        this.mEnterRoomView.setVisibility(8);
        this.mMyRoomsView.setVisibility(0);
        setAvailableRooms();
    }

    public void onStreamCheckboxClicked(View view) {
        boolean isChecked = ((CheckBox) view).isChecked();
        int id = view.getId();
        if (id == R.id.checkbox_audio) {
            this.mAudio = isChecked;
        } else if (id == R.id.checkbox_video) {
            this.mVideo = isChecked;
        }
    }

    public void showJoinCreateRoom(boolean z) {
        if (!z) {
            this.mEnterRoomView.setVisibility(0);
            this.mMyRoomsView.setVisibility(8);
            this.mEnterRoomId.requestFocus();
            this.mCreateRoomView.setVisibility(8);
        } else {
            this.mEnterRoomView.setVisibility(8);
            this.mMyRoomsView.setVisibility(8);
            this.mCreateRoomView.setVisibility(0);
            this.mEnterRoomName.requestFocus();
        }
        this.mShowMyRooms = false;
    }
}
