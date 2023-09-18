package com.qamp.app.Activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboGroupProfile;
import com.mesibo.api.MesiboProfile;
import com.mesibo.mediapicker.MediaPicker;
import com.qamp.app.Fragment.OnBoardingFromCreateGroup;
import com.qamp.app.LoginModule.MesiboApiClasses.MesiboUI;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;

import java.util.ArrayList;


public class CreateGroupActivity extends AppCompatActivity implements MediaPicker.ImageEditorListener,
        Mesibo.GroupListener, MesiboProfile.Listener {
    public static ArrayList<MesiboProfile> mMemberProfiles = new ArrayList<>();
    Bundle mGroupEditBundle = null;
    int mGroupMode;

    ImageView backBtn, nugroup_picture, imageView6, imageView10;
    EditText nugroup_editor1, nugroup_editor;
    CardView nextButton;

    TextView members_list, nu_rv_name;

    ShapeableImageView nu_rv_profile;

    RecyclerView nugroup_members;

    private void openOnboardingBottomSheet(boolean isPhoto) {
        FragmentManager fm = getSupportFragmentManager();
        OnBoardingFromCreateGroup bottomSheetFragment = new OnBoardingFromCreateGroup(isPhoto);
        bottomSheetFragment.show(fm, bottomSheetFragment.getTag());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setStatusBarColor(CreateGroupActivity.this, R.color.colorAccent);
        setContentView(R.layout.activity_create_group);
        initviews();
        this.mGroupMode = getIntent().getIntExtra(MesiboUI.GROUP_MODE, 0);
        this.mGroupEditBundle = getIntent().getBundleExtra(MesiboUI.BUNDLE);
        nugroup_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOnboardingBottomSheet(true);
            }
        });
    }

    public void openCamera() {
//        if (!AppUtils.aquireUserPermission(CreateGroupActivity.this, "android.permission.CAMERA", CreateNewGroupFragment.CAMERA_PERMISSION_CODE)) {
//            return true;
//        }
        MediaPicker.launchPicker(CreateGroupActivity.this, MediaPicker.TYPE_CAMERAIMAGE);
    }

     public void openGallery() {
         MediaPicker.launchPicker(CreateGroupActivity.this, MediaPicker.TYPE_FILEIMAGE);

     }

     public void removeProfilePic() {

//             if (CreateGroupActivity.this.mProfile != null) {
//                 CreateGroupActivity.this.mProfile.setImage(null);
//                 CreateGroupActivity.this.mProfile.save();
//             }
//         CreateGroupActivity.this.mGroupImage = null;
         //CreateGroupActivity.this.setGroupImage(null);

     }


    private void initviews() {
        backBtn = findViewById(R.id.backBtn);
        nugroup_picture = findViewById(R.id.nugroup_picture);
        imageView6 = findViewById(R.id.imageView6);
        imageView10 = findViewById(R.id.imageView10);
        nugroup_editor1 = findViewById(R.id.nugroup_editor1);
        nugroup_editor = findViewById(R.id.nugroup_editor);
        nextButton = findViewById(R.id.nextButton);
        members_list = findViewById(R.id.members_list);
        nu_rv_name = findViewById(R.id.nu_rv_name);
        nu_rv_profile = findViewById(R.id.nu_rv_profile);
        nugroup_members = findViewById(R.id.nugroup_members);
    }

    @Override
    public void Mesibo_onGroupCreated(MesiboProfile mesiboProfile) {

    }

    @Override
    public void Mesibo_onGroupJoined(MesiboProfile mesiboProfile) {

    }

    @Override
    public void Mesibo_onGroupLeft(MesiboProfile mesiboProfile) {

    }

    @Override
    public void Mesibo_onGroupMembers(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] members) {

    }

    @Override
    public void Mesibo_onGroupMembersJoined(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] members) {

    }

    @Override
    public void Mesibo_onGroupMembersRemoved(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] members) {

    }

    @Override
    public void Mesibo_onGroupSettings(MesiboProfile mesiboProfile, MesiboGroupProfile.GroupSettings groupSettings, MesiboGroupProfile.MemberPermissions memberPermissions, MesiboGroupProfile.GroupPin[] groupPins) {

    }

    @Override
    public void Mesibo_onGroupError(MesiboProfile mesiboProfile, long l) {

    }

    @Override
    public void MesiboProfile_onUpdate(MesiboProfile mesiboProfile) {

    }

    @Override
    public void MesiboProfile_onEndToEndEncryption(MesiboProfile mesiboProfile, int i) {

    }

    @Override
    public void onImageEdit(int type, String caption, String filePath, Bitmap bmp, int result) {

    }
}
