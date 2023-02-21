package com.qamp.app;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.qamp.app.messaging.MesiboUserListFragment.MODE_EDITGROUP;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboGroupProfile;
import com.mesibo.api.MesiboMessage;
import com.mesibo.api.MesiboProfile;
import com.mesibo.api.MesiboReadSession;
import com.mesibo.emojiview.EmojiconTextView;
import com.mesibo.mediapicker.AlbumListData;
import com.mesibo.mediapicker.AlbumPhotosData;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.messaging.MesiboUI;
import com.qamp.app.messaging.RoundImageDrawable;

import java.util.ArrayList;

public class ShowProfileActivityNew extends AppCompatActivity implements MesiboProfile.Listener, Mesibo.MessageListener, Mesibo.GroupListener {

    private static final int MAX_THUMBNAIL_GALERY_SIZE = 35;
    private static MesiboProfile mUser;
    private static int VIDEO_FILE = 2;
    private static int IMAGE_FILE = 1;
    private static int OTHER_FILE = 2;
    private static Bitmap mDefaultProfileBmp;
    ImageView mUsermageView;
    MesiboProfile mUserProfile;
    long mGroupId = 0;
    String mPeer = null;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    LinearLayout mAddMemebers, mEditGroup;
    ArrayList<MesiboGroupProfile.Member> mGroupMemberList = new ArrayList<>();
    MesiboGroupProfile.Member mSelfMember;
    LinearLayout mll;
    TextView mStatus;
    TextView mStatusTime;
    TextView mMobileNumber;
    TextView mPhoneType;
    private ShowProfileFragment.OnFragmentInteractionListener mListener;
    private ArrayList<String> mThumbnailMediaFiles;
    private LinearLayout mGallery;
    private int mMediaFilesCounter = 0;
    private TextView mMediaCounterView;
    private ArrayList<AlbumListData> mGalleryData;
    private ImageView mMessageBtn;
    private CardView mMediaCardView;
    private CardView mStatusPhoneCard;
    private CardView mGroupMembersCard;
    private CardView mExitGroupCard;
    private TextView mExitGroupText;
    private MesiboReadSession mReadSession = null;

//    public static ShowProfileActivityNew newInstance(MesiboProfile userdata) {
//        ShowProfileActivityNew fragment = new ShowProfileActivityNew();
//        mUser = userdata;
//        mUser.addListener(fragment);
//        return fragment;
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_profile_new);
        AppUtils.setStatusBarColor(ShowProfileActivityNew.this, R.color.colorAccent);
        Bundle args = getIntent().getExtras();
        if (null == args) {
            return;
        }


        mPeer = args.getString("peer");
        mGroupId = args.getLong("groupid");

        mUserProfile = null;

        if (mGroupId > 0) {
            mUserProfile = Mesibo.getProfile(mGroupId);
        } else {
            mUserProfile = Mesibo.getProfile(mPeer);
        }

        mUserProfile.addListener(ShowProfileActivityNew.this);

        mUsermageView = (ImageView) findViewById(R.id.up_image_profile);

        Mesibo.addListener(this);

        mUsermageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIManager.launchImageViewer(ShowProfileActivityNew.this, mUserProfile.getImageOrThumbnailPath());
            }

        });

        TextView userName = (TextView) findViewById(R.id.up_user_name);
        TextView userstatus = (TextView) findViewById(R.id.up_current_status);

        userName.setText(mUserProfile.getName());
        long lastSeen = mUserProfile.getLastSeen();
        userstatus.setVisibility(View.VISIBLE);
        if (0 == lastSeen) {
            userstatus.setText("Online");
        } else if (lastSeen < 0) {
            // never seen or group
            userstatus.setVisibility(View.GONE);
        } else {
            String seenStatus = "";
            if (lastSeen >= 2 * 3600 * 24) {
                seenStatus = (int) (lastSeen / (3600 * 24)) + " days ago";
            } else if (lastSeen >= 24 * 3600) {
                seenStatus = "yesterday";
            } else if (lastSeen >= 2 * 3600) {
                seenStatus = (int) (lastSeen / (3600)) + " hours ago";
            } else if (lastSeen >= 3600) {
                seenStatus = "an hour ago";
            } else if (lastSeen >= 120) {
                seenStatus = (int) (lastSeen / 60) + " minutes ago";
            } else {
                seenStatus = "a few moments ago";
            }

            userstatus.setText("Last seen " + seenStatus);
        }
        fragmentFunctions();
    }

    private void fragmentFunctions() {
        mDefaultProfileBmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.default_user_image);
        mThumbnailMediaFiles = new ArrayList<>();
        mGalleryData = new ArrayList<>();

        AlbumListData Images = new AlbumListData();
        Images.setmAlbumName("Images");
        AlbumListData Video = new AlbumListData();
        Video.setmAlbumName("Videos");
        AlbumListData Documents = new AlbumListData();
        Documents.setmAlbumName("Documents");
        mGalleryData.add(Images);
        mGalleryData.add(Video);
        mGalleryData.add(Documents);

        mMediaCardView = (CardView) findViewById(R.id.up_media_layout);
        mMediaCardView.setVisibility(GONE);
        Mesibo.addListener(this);

        mReadSession = mUserProfile.createReadSession(this);
        mReadSession.enableFiles(true);
        mReadSession.enableReadReceipt(true);
        mReadSession.read(100);

//        mMessageBtn = (ImageView) findViewById(R.id.up_message_btn);
//        mMessageBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        mRecyclerView = (RecyclerView) findViewById(R.id.showprofile_memebers_rview);

        // change in file
        mAddMemebers = (LinearLayout) findViewById(R.id.showprofile_add_member);
        mAddMemebers.setVisibility(GONE);

        mEditGroup = (LinearLayout) findViewById(R.id.showprofile_editgroup);
        mEditGroup.setVisibility(GONE);


        mll = (LinearLayout) findViewById(R.id.up_status_card);
        mStatus = (TextView) findViewById(R.id.up_status_text);
        mStatusTime = (TextView) findViewById(R.id.up_status_update_time);
        mMobileNumber = (TextView) findViewById(R.id.up_number);
        mPhoneType = (TextView) findViewById(R.id.up_phone_type);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mAdapter = new GroupMemeberAdapter(ShowProfileActivityNew.this, mGroupMemberList);
        mRecyclerView.setAdapter(mAdapter);
        ///
        mGallery = (LinearLayout) findViewById(R.id.up_gallery);
        mMediaCounterView = (TextView) findViewById(R.id.up_media_counter);
        mMediaCounterView.setText(String.valueOf(mMediaFilesCounter) + "\u3009 ");

        mStatusPhoneCard = (CardView) findViewById(R.id.status_phone_card);
        mGroupMembersCard = (CardView) findViewById(R.id.showprofile_members_card);
        mExitGroupCard = (CardView) findViewById(R.id.group_exit_card);
        mExitGroupText = (TextView) findViewById(R.id.group_exit_text);
        mExitGroupCard.setVisibility(GONE);
        mExitGroupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelfMember.isOwner())
                    mUserProfile.getGroupProfile().deleteGroup();
                else
                    mUserProfile.getGroupProfile().leave();

                finish();
            }
        });

        CardView e2ecard = (CardView) findViewById(R.id.e2ee_card);
        e2ecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MesiboUI.showEndToEndEncryptionInfo(ShowProfileActivityNew.this, mUserProfile.getAddress(), mUserProfile.getGroupId());
            }
        });


//        if (mUser.isGroup()) {
//         //   findViewById(R.id.block_layout).setVisibility(GONE);
//        }

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.up_open_media);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mGalleryData.size() > 0) {
                    for (int i = mGalleryData.size() - 1; i >= 0; i--) {
                        AlbumListData tempdata = mGalleryData.get(i);
                        if (tempdata.getmPhotoCount() == 0)
                            mGalleryData.remove(tempdata);
                    }

                    UIManager.launchAlbum(ShowProfileActivityNew.this, mGalleryData);
                }
            }
        });
    }


    private void addThumbnailToGallery(MesiboMessage msg) {
        View thumbnailView = null;
        String path = msg.getFilePath();
        mThumbnailMediaFiles.add(path);
        if (mThumbnailMediaFiles.size() < MAX_THUMBNAIL_GALERY_SIZE) {
            if (null != path) {
                thumbnailView = getThumbnailView(msg.getThumbnail(), msg.hasVideo());
                if (null != thumbnailView) {
                    thumbnailView.setClickable(true);
                    thumbnailView.setTag(mMediaFilesCounter - 1);
                    thumbnailView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int index = (int) v.getTag();
                            //String path = (String) mThumbnailMediaFiles.get(index);
                            UIManager.launchImageViewer(ShowProfileActivityNew.this, mThumbnailMediaFiles, index);
                        }
                    });
                    mGallery.addView(thumbnailView);
                }
            }
        }
    }


    View getThumbnailView(Bitmap bm, Boolean isVideo) {
        Context activity = ShowProfileActivityNew.this;
        if (null == activity) return null;
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.video_layer_layout_horizontal_gallery, null, false);
        ImageView thumbpic = (ImageView) view.findViewById(R.id.mp_thumbnail);
        thumbpic.setImageBitmap(bm);
        //thumbpic.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageView layer = (ImageView) view.findViewById(R.id.video_layer);
        layer.setVisibility(isVideo ? VISIBLE : GONE);
        DisplayMetrics metrics = new DisplayMetrics();
        ShowProfileActivityNew.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = (int) ((metrics.widthPixels - 50) / (5)); //number of pics in media view
        view.setLayoutParams(new ViewGroup.LayoutParams(width, width));
        return view;
    }

    private void setUserPicture() {
        Bitmap b = mUserProfile.getImageOrThumbnail();
        if (null != b) {
            mUsermageView.setImageBitmap(b);
        }
    }

    @Override
    public void Mesibo_onMessage(MesiboMessage msg) {
        if (!msg.hasFile()) return;
        mMediaCardView.setVisibility(VISIBLE);
        mMediaFilesCounter++;
        mMediaCounterView.setText(String.valueOf(mMediaFilesCounter) + "\u3009 ");
        AlbumPhotosData newPhoto = new AlbumPhotosData();
        newPhoto.setmPictueUrl(msg.getFilePath());
        newPhoto.setmSourceUrl(msg.getFilePath());
        AlbumListData tempAlbum;
        int index = 0;
        if (msg.hasVideo())
            index = 1;
        else if (!msg.hasImage())
            index = 2;
        tempAlbum = mGalleryData.get(index);

        if (tempAlbum.getmPhotosList() == null) {
            ArrayList<AlbumPhotosData> newPhotoList = new ArrayList<>();
            tempAlbum.setmPhotosList(newPhotoList);
        }
        if (tempAlbum.getmPhotosList().size() == 0) {
            tempAlbum.setmAlbumPictureUrl(msg.getFilePath());
        }
        tempAlbum.getmPhotosList().add(newPhoto);
        tempAlbum.setmPhotoCount(tempAlbum.getmPhotosList().size());
        addThumbnailToGallery(msg);
        return;
    }

    @Override
    public void Mesibo_onMessageStatus(MesiboMessage msg) {

    }

    @Override
    public void Mesibo_onMessageUpdate(MesiboMessage mesiboMessage) {

    }

    public boolean parseGroupMembers(MesiboGroupProfile.Member[] users) {
        if (null == users) return false;

        String phone = SampleAPI.getPhone();
        if (TextUtils.isEmpty(phone))
            return false;

        mGroupMemberList.clear();

        for (int i = 0; i < users.length; i++) {
            String peer = users[i].getAddress();
            if (phone.equalsIgnoreCase(peer)) {
                mSelfMember = users[i];
            }

            mGroupMemberList.add(users[i]);
        }

        if (null == mSelfMember) {
            mExitGroupText.setVisibility(GONE);
            mAddMemebers.setVisibility(GONE);
            mEditGroup.setVisibility(GONE);
            mAdapter.notifyDataSetChanged();
            return true;
        }

        //only owner can delete group
        mExitGroupText.setText(mSelfMember.isOwner() ? "Delete Group" : "Exit Group");

        if (mUserProfile.groupid > 0) {
            mAddMemebers.setVisibility(mSelfMember.isAdmin() && mUserProfile.isActive() ? VISIBLE : GONE);
            mEditGroup.setVisibility(mUserProfile.getGroupProfile().canModify() ? VISIBLE : GONE);
        }

        mAdapter.notifyDataSetChanged();
        return true;
    }

    public void updateMember(MesiboGroupProfile.Member m) {
        for (int i = 0; i < mGroupMemberList.size(); i++) {
            MesiboGroupProfile.Member em = mGroupMemberList.get(i);
            if (em.getAddress().equalsIgnoreCase(m.getAddress())) {
                mGroupMemberList.remove(em);
                mGroupMemberList.add(i, m);
                break;
            }
        }
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
        parseGroupMembers(members);
    }

    @Override
    public void Mesibo_onGroupMembersJoined(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] members) {
        if (null == members) return;

        for (MesiboGroupProfile.Member m : members) {
            updateMember(m);
        }
        mAdapter.notifyDataSetChanged();
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
    public void MesiboProfile_onUpdate(MesiboProfile userProfile) {
        if (null != mAdapter)
            mAdapter.notifyDataSetChanged();
    }

    @Override
    public void MesiboProfile_onEndToEndEncryption(MesiboProfile mesiboProfile, int i) {

    }


    @Override
    public void onResume() {
        super.onResume();


        Mesibo.setForegroundContext(this, 0x102, true);

        if (mUserProfile.groupid > 0) {
            TextView userName = (TextView) findViewById(R.id.up_user_name);
            if (null != mUserProfile.getName())
                userName.setText(mUserProfile.getName());
        }

        setUserPicture();

        if (mUserProfile.groupid > 0) {
            boolean isActive = mUserProfile.isActive();
            mExitGroupCard.setVisibility(isActive ? VISIBLE : GONE);
            mAddMemebers.setVisibility(isActive ? VISIBLE : GONE);
            mGroupMembersCard.setVisibility(VISIBLE);
//            mStatusPhoneCard.setVisibility(GONE);
            mAddMemebers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putLong("groupid", mUserProfile.groupid);
                    UIManager.launchMesiboContacts(ShowProfileActivityNew.this, 0, MODE_EDITGROUP, 0, bundle);
                    ShowProfileActivityNew.this.finish();
                }
            });

            mEditGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIManager.launchEditProfile(ShowProfileActivityNew.this, 0, mUserProfile.groupid, false);
                    //UIManager.launchMesiboContacts(getActivity(), 0, MODE_EDITGROUP, 0,bundle);
                    ShowProfileActivityNew.this.finish();
                }
            });


            mUserProfile.getGroupProfile().getMembers(100, true, this);

        } else {
            mExitGroupCard.setVisibility(GONE);
            mGroupMembersCard.setVisibility(GONE);
            //mStatusPhoneCard.setVisibility(VISIBLE);

            if (TextUtils.isEmpty(mUserProfile.getStatus())) {
//                mll.setVisibility(GONE);
            } else {
                //  mll.setVisibility(VISIBLE);
                mStatus.setText(mUserProfile.getStatus());
            }

//            mStatusTime.setText((""));
//            mMobileNumber.setText((mUserProfile.address));
//            mPhoneType.setText("Mobile");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Mesibo.setForegroundContext(this, 0x102, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Mesibo.setForegroundContext(this, 0x102, false);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    public class GroupMemeberAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context mContext = null;
        private ArrayList<MesiboGroupProfile.Member> mDataList = null;

        public GroupMemeberAdapter(Context context, ArrayList<MesiboGroupProfile.Member> list) {
            this.mContext = context;
            mDataList = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.showprofile_group_member_rv_item, parent, false);
            return new GroupMemeberAdapter.GroupMembersCellsViewHolder(view);

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holderr, @SuppressLint("RecyclerView") final int position) {
            final int pos = position;
            final MesiboGroupProfile.Member member = mDataList.get(position);
            final MesiboProfile user = member.getProfile();
            final GroupMemeberAdapter.GroupMembersCellsViewHolder holder = (GroupMemeberAdapter.GroupMembersCellsViewHolder) holderr;
            holder.mContactsName.setText(user.getNameOrAddress("+"));

            Bitmap memberImage = user.getImage();
            if (null != memberImage)
                holder.mContactsProfile.setImageDrawable(new RoundImageDrawable(memberImage));
            else
                holder.mContactsProfile.setImageDrawable(new RoundImageDrawable(mDefaultProfileBmp));

            if (member.isAdmin()) {
                holder.mAdminTextView.setVisibility(VISIBLE);
            } else {
                holder.mAdminTextView.setVisibility(GONE);
            }

            String status = user.getStatus();
            if (TextUtils.isEmpty(user.getStatus())) {
                status = "";
            }

            holder.mContactsStatus.setText(status);

            // only admin can have menu, also owner can't be deleted

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final MesiboGroupProfile.Member member = mDataList.get(position);
                    final MesiboProfile profile = member.getProfile();

                    if (!mSelfMember.isAdmin()) {
                        if (profile.isSelfProfile()) {
                            return;
                        }

                        MesiboUI.launchMessageView(ShowProfileActivityNew.this, profile);
                        ShowProfileActivityNew.this.finish();
                        return;
                    }

                    ArrayList<String> items = new ArrayList<String>();

                    if (!member.isAdmin()) {
                        items.add("Make Admin");

                    } else {
                        items.add("Remove Admin");
                    }

                    // don't allow self messaging or self delete member
                    if (!profile.isSelfProfile()) {
                        items.add("Delete member");
                        items.add("Message");
                    }

                    CharSequence[] cs = items.toArray(new CharSequence[items.size()]);

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    //builder.setTitle("Select The Action");
                    builder.setItems(cs, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            //Delete member
                            if (item == 1) {
                                String[] members = new String[1];
                                members[0] = mDataList.get(position).getAddress();
                                mUserProfile.getGroupProfile().removeMembers(members);
                                mDataList.remove(position);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();

                            } else if (item == 0) {
                                String[] members = new String[1];
                                members[0] = mDataList.get(position).getAddress();
                                mUserProfile.getGroupProfile().addMembers(members, MesiboGroupProfile.MEMBERFLAG_ALL, member.isAdmin() ? 0 : MesiboGroupProfile.ADMINFLAG_ALL);
                            } else if (2 == item) {
                                MesiboUI.launchMessageView(ShowProfileActivityNew.this, profile);
                                ShowProfileActivityNew.this.finish();
                                return;
                            }
                        }
                    });
                    builder.show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        public class GroupMembersCellsViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString = null;
            public View mView = null;
            public ImageView mContactsProfile = null;
            public TextView mContactsName = null;
            public TextView mAdminTextView = null;
            public EmojiconTextView mContactsStatus = null;

            public GroupMembersCellsViewHolder(View view) {
                super(view);
                mView = view;
                mContactsProfile = (ImageView) view.findViewById(R.id.sp_rv_profile);
                mContactsName = (TextView) view.findViewById(R.id.sp_rv_name);
                mContactsStatus = (EmojiconTextView) view.findViewById(R.id.sp_memeber_status);
                mAdminTextView = (TextView) view.findViewById(R.id.admin_info);
            }
        }

    }
}
