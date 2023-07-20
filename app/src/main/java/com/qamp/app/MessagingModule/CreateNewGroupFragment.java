/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.MessagingModule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboGroupProfile;
import com.mesibo.api.MesiboProfile;
import com.mesibo.api.Profile;
import com.mesibo.emojiview.EmojiconGridView;
import com.mesibo.emojiview.EmojiconTextView;
import com.mesibo.emojiview.EmojiconsPopup;
import com.mesibo.emojiview.emoji.Emojicon;
import com.mesibo.mediapicker.MediaPicker;
import com.qamp.app.Listener.Backpressedlistener;
import com.qamp.app.R;

import java.util.ArrayList;

public class CreateNewGroupFragment extends Fragment implements MediaPicker.ImageEditorListener,
        Mesibo.GroupListener, MesiboProfile.Listener, Backpressedlistener {
    static final int CAMERA_PERMISSION_CODE = 102;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static Backpressedlistener backpressedlistener;
    RecyclerView.Adapter mAdapter;
    //TextView mCharCounter;
    TextView mCreateGroupBtn;
    TextView members_list;
    boolean mDone = false;
    // ImageView mEmojiButton;
    Bundle mGroupEditBundle = null;
    long mGroupId = 0;
    Bitmap mGroupImage = null;
    int mGroupMode;
    ImageView mGroupPicture;
    ImageView backButton;
    EditText mGroupSubjectEditor;
    MesiboProfile mProfile = null;
    RecyclerView mRecyclerView;
    private String mParam1;
    private String mParam2;
    private boolean isLessMembers = false;

    public static CreateNewGroupFragment newInstance(Bundle bundle) {
        CreateNewGroupFragment fragment = new CreateNewGroupFragment();
        Bundle args = new Bundle();
        args.putBundle(ARG_PARAM1, bundle);
        fragment.setArguments(args);
        return fragment;
    }

    public void onImageEdit(int i, String s, String s1, Bitmap bitmap, int i1) {
        this.mGroupImage = bitmap;
        setGroupImage(bitmap);
        if (this.mProfile != null) {
            this.mProfile.setImage(this.mGroupImage);
            this.mProfile.save();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != CAMERA_PERMISSION_CODE) {
            return;
        }
        if (grantResults.length <= 0 || grantResults[0] != 0) {
            Utils.showAlert(getActivity(), "Permission Denied", MesiboConfiguration.MSG_PERMISON_CAMERA_FAIL);
        } else {
            MediaPicker.launchPicker(getActivity(), MediaPicker.TYPE_CAMERAIMAGE);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        CreateNewGroupFragment.super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mGroupEditBundle = getArguments().getBundle(ARG_PARAM1);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_new_group, container, false);
        this.mGroupSubjectEditor = v.findViewById(R.id.nugroup_editor);
        this.backButton = v.findViewById(R.id.imageView2);
        this.mGroupId = 0;
        Mesibo.addListener(this);
        if (this.mGroupEditBundle != null) {
            this.mGroupId = this.mGroupEditBundle.getLong(MesiboUI.GROUP_ID);
            if (this.mGroupId > 0) {
                this.mProfile = Mesibo.getProfile(this.mGroupId);
                this.mGroupSubjectEditor.setText(this.mProfile.getName());
                this.mProfile.getGroupProfile().getMembers(256, true, this);
            }
        }

        this.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        this.mCreateGroupBtn = v.findViewById(R.id.nugroup_create_btn);
        this.members_list = v.findViewById(R.id.members_list);
        this.mCreateGroupBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
//                if (UserListFragment.mMemberProfiles.size() == 0 &&
//                        CreateNewGroupFragment.this.mGroupMode == 0 &&
//                        (UserListFragment.mMemberProfiles.size() == 1 && CreateNewGroupFragment.this.mGroupMode == 1)) {
                if (isLessMembers) {
                    Utils.showAlert(CreateNewGroupFragment.this.getActivity(), MesiboConfiguration.CREATE_GROUP_NOMEMEBER_TITLE_STRING, MesiboConfiguration.CREATE_GROUP_NOMEMEBER_MESSAGE_STRING);
                } else if (CreateNewGroupFragment.this.mGroupSubjectEditor.getText().toString().length() < 2) {
                    Utils.showAlert(CreateNewGroupFragment.this.getActivity(), null, getActivity().getResources().getString(R.string.group_valid_text));
                } else {
                    CreateNewGroupFragment.this.mCreateGroupBtn.setEnabled(false);
                    if (0 == CreateNewGroupFragment.this.mGroupId) {
                        MesiboGroupProfile.GroupSettings gs = new MesiboGroupProfile.GroupSettings();
                        gs.name = CreateNewGroupFragment.this.mGroupSubjectEditor.getText().toString();
                        gs.flags = 0;
                        Mesibo.createGroup(gs, CreateNewGroupFragment.this);
                        ContactsBottomSheetFragment.groupmaker = 0;
                        UserListFragment.isSheetOpen = false;
                        return;
                    }
                    Mesibo.getProfile(CreateNewGroupFragment.this.mGroupId).setName(CreateNewGroupFragment.this.mGroupSubjectEditor.getText().toString());
                    CreateNewGroupFragment.this.setGroupInfo();
                    Activity a = CreateNewGroupFragment.this.getActivity();
                    if (a != null) {
                        a.finish();
                    }

                }
            }
        });
        this.mGroupPicture = v.findViewById(R.id.nugroup_picture);
        setGroupImageFile();
        this.mGroupPicture.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            public void onClick(View v) {
                @SuppressLint("RestrictedApi") MenuBuilder menuBuilder = new MenuBuilder(CreateNewGroupFragment.this.getActivity());
                new MenuInflater(CreateNewGroupFragment.this.getActivity()).inflate(R.menu.image_source_menu, menuBuilder);
                @SuppressLint("RestrictedApi") MenuPopupHelper optionsMenu = new MenuPopupHelper(CreateNewGroupFragment.this.getActivity(), menuBuilder, v);
                optionsMenu.setForceShowIcon(true);
                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                        if (item.getItemId() == R.id.popup_camera) {
                            if (!Utils.aquireUserPermission(CreateNewGroupFragment.this.getActivity(), "android.permission.CAMERA", CreateNewGroupFragment.CAMERA_PERMISSION_CODE)) {
                                return true;
                            }
                            MediaPicker.launchPicker(CreateNewGroupFragment.this.getActivity(), MediaPicker.TYPE_CAMERAIMAGE);
                            return true;
                        } else if (item.getItemId() == R.id.popup_gallery) {
                            MediaPicker.launchPicker(CreateNewGroupFragment.this.getActivity(), MediaPicker.TYPE_FILEIMAGE);
                            return true;
                        } else {
                            if (item.getItemId() == R.id.popup_remove) {
                                if (CreateNewGroupFragment.this.mProfile != null) {
                                    CreateNewGroupFragment.this.mProfile.setImage(null);
                                    CreateNewGroupFragment.this.mProfile.save();
                                }
                                CreateNewGroupFragment.this.mGroupImage = null;
                                CreateNewGroupFragment.this.setGroupImage(null);
                            }
                            return false;
                        }
                    }

                    public void onMenuModeChange(MenuBuilder menu) {
                    }
                });
                optionsMenu.show();
            }
        });
        this.mRecyclerView = v.findViewById(R.id.nugroup_members);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this.mRecyclerView.getContext()));
        this.mAdapter = new GroupMemeberAdapter(getActivity(), UserListFragment.mMemberProfiles);
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.members_list.setText(UserListFragment.mMemberProfiles.size()+""+"members");

//        this.mCharCounter = v.findViewById(R.id.nugroup_counter);
//        this.mCharCounter.setText(String.valueOf(50));
        this.mGroupSubjectEditor.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        this.mGroupSubjectEditor.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                //  CreateNewGroupFragment.this.mCharCounter.setText(String.valueOf(50 - CreateNewGroupFragment.this.mGroupSubjectEditor.getText().length()));
            }
        });
        final EmojiconsPopup popup = new EmojiconsPopup(v.findViewById(R.id.nugroup_root_layout), getActivity());
        popup.setSizeForSoftKeyboard();
//        this.mEmojiButton = v.findViewById(R.id.nugroup_smile_btn);
//        this.mEmojiButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (popup.isShowing()) {
//                    popup.dismiss();
//                } else if (popup.isKeyBoardOpen().booleanValue()) {
//                    popup.showAtBottom();
//                    CreateNewGroupFragment.this.changeEmojiKeyboardIcon(CreateNewGroupFragment.this.mEmojiButton, R.drawable.ic_keyboard);
//                } else {
//                    CreateNewGroupFragment.this.mGroupSubjectEditor.setFocusableInTouchMode(true);
//                    CreateNewGroupFragment.this.mGroupSubjectEditor.requestFocus();
//                    popup.showAtBottomPending();
//                    ((InputMethodManager) CreateNewGroupFragment.this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(CreateNewGroupFragment.this.mGroupSubjectEditor, 1);
//                    CreateNewGroupFragment.this.changeEmojiKeyboardIcon(CreateNewGroupFragment.this.mEmojiButton, R.drawable.ic_keyboard);
//                }
//            }
//        });
//        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            public void onDismiss() {
//                CreateNewGroupFragment.this.changeEmojiKeyboardIcon(CreateNewGroupFragment.this.mEmojiButton, R.drawable.ic_sentiment_satisfied_black_24dp);
//            }
//        });
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {
            public void onKeyboardOpen(int keyBoardHeight) {
            }

            public void onKeyboardClose() {
                if (popup.isShowing()) {
                    popup.dismiss();
                }
            }
        });
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {
            public void onEmojiconClicked(Emojicon emojicon) {
                if (CreateNewGroupFragment.this.mGroupSubjectEditor != null && emojicon != null) {
                    int start = CreateNewGroupFragment.this.mGroupSubjectEditor.getSelectionStart();
                    int end = CreateNewGroupFragment.this.mGroupSubjectEditor.getSelectionEnd();
                    if (start < 0) {
                        CreateNewGroupFragment.this.mGroupSubjectEditor.append(emojicon.getEmoji());
                    } else {
                        CreateNewGroupFragment.this.mGroupSubjectEditor.getText().replace(Math.min(start, end), Math.max(start, end), emojicon.getEmoji(), 0, emojicon.getEmoji().length());
                    }
                }
            }
        });
//        popup.setOnEmojiconBackViewClickedListener(new EmojiconsPopup.OnEmojiconBackViewClickedListener() {
//            public void onEmojiconBackViewClicked(View v) {
//                CreateNewGroupFragment.this.mGroupSubjectEditor.dispatchKeyEvent(new KeyEvent(0, 0, 0, 67, 0, 0, 0, 0, 6));
//            }
//        });
        return v;
    }


    private void lessMembers(int size) {
        if (size <= 2) {
            isLessMembers = true;
        } else {
            isLessMembers = false;
        }
    }

    @Override
    public void onPause() {
        backpressedlistener = null;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        backpressedlistener = this;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getContext(), "back button pressed", Toast.LENGTH_LONG).show();
    }

    /* access modifiers changed from: package-private */
    public boolean isExistingMember(Profile p) {
        if (UserListFragment.mExistingMembers == null) {
            return false;
        }
        for (MesiboGroupProfile.Member m : UserListFragment.mExistingMembers) {
            if (p == m.getProfile()) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void addGroupMembers() {
        ArrayList<String> members = new ArrayList<>();
        for (int i = 0; i < UserListFragment.mMemberProfiles.size(); i++) {
            MesiboProfile mp = UserListFragment.mMemberProfiles.get(i);
            if (!isExistingMember(mp)) {
                members.add(mp.getAddress());
            }
        }
        if (members.size() > 0) {
            String[] m = new String[members.size()];
            members.toArray(m);
            MesiboGroupProfile.MemberPermissions permissions = new MesiboGroupProfile.MemberPermissions();
            permissions.flags = 31;
            permissions.adminFlags = 0;
            this.mProfile.getGroupProfile().addMembers(m, permissions);
        }
    }

    /* access modifiers changed from: package-private */
    public void setGroupInfo() {
        if (this.mGroupImage != null) {
            this.mProfile.addListener(this);
            this.mProfile.setImage(this.mGroupImage);
            this.mProfile.save();
            return;
        }
        addGroupMembers();
        launchMessaging();
    }

    /* access modifiers changed from: package-private */
    public void launchMessaging() {
        MesiboUIManager.launchMessagingActivity(getActivity(), 0, null, this.mProfile.getGroupId());
        Activity a = getActivity();
        if (a != null) {
            a.finish();
        }
    }

    public void Mesibo_onGroupCreated(MesiboProfile mesiboProfile) {
        this.mProfile = mesiboProfile;
        this.mGroupId = mesiboProfile.getGroupId();
        setGroupInfo();
    }

    public void MesiboProfile_onUpdate(MesiboProfile profile) {
        if (!this.mDone) {
            this.mDone = true;
            this.mProfile.removeListener(this);
            addGroupMembers();
            launchMessaging();
        }
    }

    public void MesiboProfile_onEndToEndEncryption(MesiboProfile mesiboProfile, int i) {
    }

    public void Mesibo_onGroupJoined(MesiboProfile mesiboProfile) {
    }

    public void Mesibo_onGroupLeft(MesiboProfile mesiboProfile) {
    }

    public void Mesibo_onGroupMembers(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] members) {
    }

    public void Mesibo_onGroupMembersJoined(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] members) {
    }

    public void Mesibo_onGroupMembersRemoved(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] members) {
    }

    public void Mesibo_onGroupSettings(MesiboProfile mesiboProfile, MesiboGroupProfile.GroupSettings groupSettings, MesiboGroupProfile.MemberPermissions memberPermissions, MesiboGroupProfile.GroupPin[] groupPins) {
    }

    public void Mesibo_onGroupError(MesiboProfile mesiboProfile, long error) {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String fileName;
        if (-1 == resultCode && (fileName = MediaPicker.processOnActivityResult(getActivity(), requestCode, resultCode, data)) != null) {
            MesiboUIManager.launchImageEditor(getActivity(), MediaPicker.TYPE_CAMERAIMAGE, 0, null, fileName, false, false, true, true, 600, this);
        }
    }

    public void setGroupImage(Bitmap bmp) {
        if (bmp == null) {
//            bmp = BitmapFactory.decodeResource(getContext().getResources(),
//                    R.drawable.cam_icon);
            bmp = MesiboImages.getDefaultGroupBitmap();
        }
        this.mGroupPicture.setImageDrawable(new RoundImageDrawable(bmp));
    }

    public void setGroupImageFile() {
        if (this.mProfile == null) {
            setGroupImage(null);
        } else {
            setGroupImage(this.mProfile.getThumbnail());
        }
    }

    /* access modifiers changed from: private */
    public void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }

    public class GroupMemeberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final int mBackground = 0;
        private Context mContext = null;
        private ArrayList<MesiboProfile> mDataList = null;
        private UserListFragment mHost;


        public GroupMemeberAdapter(Context context, ArrayList<MesiboProfile> list) {
            this.mContext = context;
            this.mDataList = list;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GroupMembersCellsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.group_memeber_rv_item, parent, false));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holderr, @SuppressLint("RecyclerView") final int position) {
            int i = position;
            MesiboProfile user = this.mDataList.get(position);
            GroupMembersCellsViewHolder holder = (GroupMembersCellsViewHolder) holderr;
            if (user.other == null) {
                user.other = new UserData(user);
            }
            UserData data = (UserData) user.other;
            holder.mContactsName.setText(data.getUserName());
            Bitmap b = data.getImage();
            String filePath = data.getImagePath();
            if (b != null) {
                holder.mContactsProfile.setImageDrawable(new RoundImageDrawable(b));
            } else if (filePath != null) {
                holder.mContactsProfile.setImageDrawable(new RoundImageDrawable(BitmapFactory.decodeFile(filePath)));
            } else {
                holder.mContactsProfile.setImageDrawable(MesiboImages.getDefaultRoundedDrawable());
            }
            if (user != null) {
                if (!TextUtils.isEmpty(user.getStatus())) {
                    holder.mContactsStatus.setText(user.getStatus());
                } else {
                    holder.mContactsStatus.setText("");
                }
            }
            holder.mDeleteContact.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    GroupMemeberAdapter.this.removeItem(position);

                }
            });
        }

        public int getItemCount() {
            return this.mDataList.size();
        }

        public void removeItem(int position) {
            this.mDataList.remove(position);
            lessMembers(this.mDataList.size());
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }

        public class GroupMembersCellsViewHolder extends RecyclerView.ViewHolder {
            public TextView mContactsName = null;
            public ImageView mContactsProfile = null;
            public EmojiconTextView mContactsStatus = null;
            public ImageView mDeleteContact;
            public View mView = null;

            public GroupMembersCellsViewHolder(View view) {
                super(view);
                this.mView = view;
                this.mContactsProfile = view.findViewById(R.id.nu_rv_profile);
                this.mContactsName = view.findViewById(R.id.nu_rv_name);
                this.mContactsStatus = view.findViewById(R.id.nu_memeber_status);
                this.mDeleteContact = view.findViewById(R.id.nu_delete_btn);
                this.mDeleteContact = view.findViewById(R.id.nu_delete_btn);
            }
        }
    }


}
