package com.qamp.app.qampCalls;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
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
import com.mesibo.api.Mesibo.GroupListener;
import com.mesibo.api.MesiboGroupProfile.GroupPin;
import com.mesibo.api.MesiboGroupProfile.GroupSettings;
import com.mesibo.api.MesiboGroupProfile.Member;
import com.mesibo.api.MesiboGroupProfile.MemberPermissions;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.R;

import java.util.ArrayList;

public class MesiboJoinRoomActivity extends AppCompatActivity implements GroupListener {
    private static final String TAG = "JoinRoomActivity";
    private EditText mEnterRoomId = null;
    private EditText mEnterRoomPin = null;
    private EditText mEnterRoomName = null;
    private View mEnterRoomView = null;
    private View mMyRoomsView = null;
    private TextView mErrorEnterRoomView = null;
    private View mCreateRoomView = null;
    private Spinner mCreateRoomsResolutions = null;
    private int mSelectedResolution = 0;
    private ArrayList<MesiboProfile> mRoomsArray = new ArrayList();
    private ArrayList<String> mRoomsList = new ArrayList();
    private ArrayAdapter<String> mAdapter = null;
    private ListView mRoomsListView;
    private boolean mAudio = true;
    private boolean mVideo = true;
    private boolean mShowMyRooms = false;
    private static final String RESOLUTION_DEFAULT_STRING = "Default";
    private static final String RESOLUTION_STANDARD_STRING = "Standard - 640x480";
    private static final String RESOLUTION_HD_STRING = "HD - 1280x720";
    private static final String RESOLUTION_FULL_HD_STRING = "Full HD - 1920x1080";
    private static final String RESOLUTION_4K_STRING = "4K - 3840x2160";
    private static final String RESOLUTION_QVGA_STRING = "QVGA - 320x240";

    public MesiboJoinRoomActivity() {
    }

    protected void onCreate(@Nullable Bundle var1) {
        super.onCreate(var1);
        this.setContentView(R.layout.activity_mesibojoinroom);//==========
        var1 = this.getIntent().getExtras();
        String var2 = null;
        if (var1 != null) {
            var2 = var1.getString("title");
        }

        if (TextUtils.isEmpty(var2)) {
            var2 = "Mesibo Conferencing";
        }

        ((TextView)this.findViewById(R.id.titlee)).setText(var2);//=========
        this.mEnterRoomId = (EditText)this.findViewById(R.id.room_id);//=========
        this.mEnterRoomPin = (EditText)this.findViewById(R.id.room_pin);//=========
        this.mEnterRoomName = (EditText)this.findViewById(R.id.room_name);//=========
        this.mEnterRoomView = this.findViewById(R.id.existing_room);//=========
        this.mCreateRoomView = this.findViewById(R.id.create_room);//=========
        this.mMyRoomsView = this.findViewById(R.id.my_rooms);//=========
        this.mErrorEnterRoomView = (TextView)this.findViewById(R.id.error_enter_room);//=========
        this.hideError();
        this.mRoomsListView = (ListView)this.findViewById(R.id.list_my_rooms);//=========
        this.mAdapter = new ArrayAdapter(this, 17367043, this.mRoomsList);
        this.mRoomsListView.setAdapter(this.mAdapter);
        this.mCreateRoomsResolutions = (Spinner)this.findViewById(R.id.choose_resolution_options);//=========
        final String[] var3 = new String[]{"Default", "Standard - 640x480", "HD - 1280x720", "Full HD - 1920x1080", "4K - 3840x2160", "QVGA - 320x240"};
        ArrayAdapter var4 = new ArrayAdapter(this, 17367049, var3);
        this.mCreateRoomsResolutions.setAdapter(var4);
        this.mCreateRoomsResolutions.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> var1, View var2, int var3x, long var4) {
                String var6 = var3[var3x];
                byte var7 = -1;
                switch(var6.hashCode()) {
                    case -1492200336:
                        if (var6.equals("Standard - 640x480")) {
                            var7 = 1;
                        }
                        break;
                    case -1085510111:
                        if (var6.equals("Default")) {
                            var7 = 0;
                        }
                        break;
                    case -951506316:
                        if (var6.equals("4K - 3840x2160")) {
                            var7 = 4;
                        }
                        break;
                    case -812437043:
                        if (var6.equals("HD - 1280x720")) {
                            var7 = 2;
                        }
                        break;
                    case -17935917:
                        if (var6.equals("QVGA - 320x240")) {
                            var7 = 5;
                        }
                        break;
                    case 1014414179:
                        if (var6.equals("Full HD - 1920x1080")) {
                            var7 = 3;
                        }
                }

                switch(var7) {
                    case 0:
                        MesiboJoinRoomActivity.this.mSelectedResolution = 0;
                        return;
                    case 1:
                        MesiboJoinRoomActivity.this.mSelectedResolution = 2;
                        return;
                    case 2:
                        MesiboJoinRoomActivity.this.mSelectedResolution = 3;
                        return;
                    case 3:
                        MesiboJoinRoomActivity.this.mSelectedResolution = 4;
                        return;
                    case 4:
                        MesiboJoinRoomActivity.this.mSelectedResolution = 5;
                        return;
                    case 5:
                        MesiboJoinRoomActivity.this.mSelectedResolution = 1;
                    default:
                }
            }

            public void onNothingSelected(AdapterView<?> var1) {
            }
        });
    }

    public void onBackPressed() {
        if (this.mShowMyRooms) {
            this.showJoinCreateRoom(false);
        } else {
            super.onBackPressed();
            this.finish();
        }
    }

    public void onEnterRoom(View var1) {
        String var7 = this.mEnterRoomId.getText().toString();
        String var2 = this.mEnterRoomPin.getText().toString();
        if (!var7.isEmpty() && !var2.isEmpty()) {
            long var3 = (long)Integer.parseInt(var7);
            long var5 = (long)Integer.parseInt(var2);
            Mesibo.getProfile(var3).getGroupProfile().join(var5, this);
        }
    }

    public void onCreateRoom(View var1) {
        String var3;
        if (!(var3 = this.mEnterRoomName.getText().toString()).isEmpty()) {
            GroupSettings var2;
            (var2 = new GroupSettings()).name = var3;
            var2.videoResolution = (long)this.mSelectedResolution;
            if (this.mVideo) {
                var2.callFlags = 6L;
            } else if (this.mAudio) {
                var2.callFlags = 2L;
            }

            var2.callFlags |= 33024L;
            Mesibo.createGroup(var2, this);
        }
    }

    public void showJoinCreateRoom(boolean var1) {
        if (!var1) {
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

    public void onRoomRadioButtonClicked(View var1) {
        boolean var2 = ((RadioButton)var1).isChecked();
        int var3;
        if ((var3 = var1.getId()) == R.id.radio_enter_room) {//=========
            if (var2) {
                this.showJoinCreateRoom(false);
                return;
            }
        } else if (var3 == R.id.radio_create_room && var2) {//=========
            this.showJoinCreateRoom(true);
        }

    }

    public void onShowRooms(View var1) {
        this.mShowMyRooms = true;
        this.mEnterRoomView.setVisibility(8);
        this.mMyRoomsView.setVisibility(0);
        this.setAvailableRooms();
    }

    private void setAvailableRooms() {
        ArrayList var1 = Mesibo.getSortedUserProfiles();
        this.mRoomsArray.clear();
        this.mRoomsList.clear();

        for(int var2 = 0; var2 < var1.size(); ++var2) {
            MesiboProfile var3;
            if ((var3 = (MesiboProfile)var1.get(var2)).isGroup()) {
                String var4 = "Room #" + var3.getGroupId() + ": " + var3.getName();
                this.mRoomsList.add(var4);
                this.mRoomsArray.add(var3);
            }
        }

        this.mAdapter.notifyDataSetChanged();
        this.mRoomsListView.setOnItemClickListener((var1x, var2x, var3x, var4x) -> {
            MesiboProfile var6 = (MesiboProfile)this.mRoomsArray.get(var3x);
            this.joinConferenceRoom(var6);
        });
    }

    private void showError(String var1) {
        this.mErrorEnterRoomView.setVisibility(0);
        this.mErrorEnterRoomView.setText(var1);
    }

    private void hideError() {
        this.mErrorEnterRoomView.setVisibility(8);
    }

    private void joinConferenceRoom(MesiboProfile var1) {
        MesiboCall.getInstance().groupCallUi(this, var1, this.mVideo, this.mAudio);
    }

    public void onStreamCheckboxClicked(View var1) {
        boolean var2 = ((CheckBox)var1).isChecked();
        int var3;
        if ((var3 = var1.getId()) == R.id.checkbox_audio) {//=========
            this.mAudio = var2;
        } else {
            if (var3 == R.id.checkbox_video) {//=========
                this.mVideo = var2;
            }

        }
    }

    public void Mesibo_onGroupCreated(MesiboProfile var1) {
        MemberPermissions var2;
        (var2 = new MemberPermissions()).flags = 31L;
        var1.getGroupProfile().addPin(var2, this);
    }

    public void Mesibo_onGroupJoined(MesiboProfile var1) {
        this.joinConferenceRoom(var1);
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
        this.joinConferenceRoom(var1);
    }

    public void Mesibo_onGroupError(MesiboProfile var1, long var2) {
        if (3L == var2) {
            this.showError("Incorrect RoomId or the PIN");
        }

    }
}

