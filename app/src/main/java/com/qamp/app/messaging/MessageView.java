package com.qamp.app.messaging;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboFile;
import com.mesibo.emojiview.EmojiconTextView;
import com.qamp.app.R;

public class MessageView extends RelativeLayout {
    private static int mThumbailWidth = 0;
    private boolean hasImage = false;
    ImageView mAudioVideoLayer;
    private MessageData mData = null;
    TextView mHeadingView = null;
    LayoutInflater mInflater = null;
    EmojiconTextView mMessageTextView = null;
    View mMessageView = null;
    LayoutParams mMsgParams;
    RelativeLayout mPTTlayout = null;
    FrameLayout mPicLayout = null;
    LayoutParams mPicLayoutParam;
    ThumbnailProgressView mPictureThumbnail = null;
    FrameLayout mReplayContainer = null;
    ImageView mReplyImage;
    RelativeLayout mReplyLayout = null;
    TextView mReplyMessage;
    TextView mReplyUserName;
    TextView mSubTitleView = null;
    FrameLayout.LayoutParams mThumbnailParams;
    LinearLayout mTitleLayout = null;
    LayoutParams mTitleParams;
    TextView mTitleView = null;

    public MessageView(Context context) {
        super(context);
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    public MessageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    public void init() {
        View v = this.mInflater.inflate(R.layout.message_view, this, true);
        this.mMessageView = v;
        this.mPicLayout = (FrameLayout) v.findViewById(R.id.m_piclayout);
        this.mTitleLayout = (LinearLayout) v.findViewById(R.id.m_titlelayout);
        this.mTitleView = (TextView) v.findViewById(R.id.m_ptitle);
        this.mSubTitleView = (TextView) v.findViewById(R.id.m_psubtitle);
        this.mHeadingView = (TextView) v.findViewById(R.id.m_pheading);
        this.mMessageTextView = v.findViewById(R.id.m_pmessage);
        this.mPTTlayout = (RelativeLayout) v.findViewById(R.id.message_layout);
        this.mReplayContainer = (FrameLayout) v.findViewById(R.id.reply_container);
        this.mPicLayoutParam = (LayoutParams) this.mPicLayout.getLayoutParams();
        this.mTitleParams = (LayoutParams) this.mTitleLayout.getLayoutParams();
        this.mMsgParams = (LayoutParams) this.mMessageTextView.getLayoutParams();
    }

    public void loadImageView() {
        if (this.mPictureThumbnail == null) {
            this.mPictureThumbnail = (ThumbnailProgressView) this.mInflater.inflate(R.layout.thumbnail_progress_view_layout, this.mPicLayout, true).findViewById(R.id.m_picture);
            this.mAudioVideoLayer = (ImageView) this.mPictureThumbnail.findViewById(R.id.audio_video_layer);
            this.mAudioVideoLayer.setVisibility(8);
            if (this.mThumbnailParams == null) {
                this.mThumbnailParams = (FrameLayout.LayoutParams) this.mPictureThumbnail.getLayoutParams();
            }
        }
    }

    public void loadReplyView() {
        if (this.mReplyLayout == null) {
            View v = this.mInflater.inflate(R.layout.reply_layout, this.mReplayContainer, true);
            this.mReplyLayout = (RelativeLayout) v.findViewById(R.id.reply_layout);
            this.mReplyImage = (ImageView) v.findViewById(R.id.reply_image);
            this.mReplyUserName = (TextView) v.findViewById(R.id.reply_name);
            this.mReplyMessage = (TextView) v.findViewById(R.id.reply_text);
        }
    }

    public void setData(MessageData data) {
        this.mData = data;
        ViewGroup.LayoutParams PTTParams = getLayoutParams();
        String title = this.mData.getTitle();
        String subtitle = this.mData.getSubTitle();
        String message = this.mData.getMessage();
        Bitmap thumbnail = this.mData.getImage();
        if (thumbnail != null) {
            loadImageView();
        } else {
            if (this.mPictureThumbnail != null) {
                this.mPictureThumbnail.setVisibility(8);
            }
            if (this.mAudioVideoLayer != null) {
                this.mAudioVideoLayer.setVisibility(8);
            }
            this.mPicLayout.setVisibility(8);
        }
        if (this.mAudioVideoLayer != null) {
            this.mAudioVideoLayer.setVisibility(8);
        }
        MesiboFile file = this.mData.getMesiboMessage().getFile();
        if (!(file == null || this.mAudioVideoLayer == null || (3 != file.type && 2 != file.type))) {
            this.mAudioVideoLayer.setVisibility(0);
        }
        if (this.mData.isReply()) {
            loadReplyView();
            this.mReplayContainer.setVisibility(0);
            this.mReplyLayout.setVisibility(0);
            if (this.mData.getReplyString() != null) {
                this.mReplyMessage.setText(this.mData.getReplyString());
            } else {
                this.mReplyMessage.setText("");
            }
            this.mReplyUserName.setText(this.mData.getReplyName());
            if (this.mData.getReplyBitmap() != null) {
                this.mReplyImage.setVisibility(0);
                this.mReplyImage.setImageBitmap(this.mData.getReplyBitmap());
            } else {
                this.mReplyImage.setVisibility(8);
            }
        } else if (this.mReplyLayout != null) {
            this.mReplyLayout.setVisibility(8);
            this.mReplayContainer.setVisibility(8);
        }
        if (thumbnail != null) {
            int width = MesiboUI.getConfig().mHorizontalImageWidth;
            if (thumbnail.getHeight() > thumbnail.getWidth()) {
                width = MesiboUI.getConfig().mVerticalImageWidth;
            }
            mThumbailWidth = (Mesibo.getDisplayWidthInPixel() * width) / 100;
            PTTParams.width = mThumbailWidth;
            LayoutParams picLayoutParam = (LayoutParams) this.mPicLayout.getLayoutParams();
            FrameLayout.LayoutParams thumbnailParams = (FrameLayout.LayoutParams) this.mPictureThumbnail.getLayoutParams();
            this.mTitleLayout.setLayoutParams(this.mTitleParams);
            this.mMessageTextView.setLayoutParams(this.mMsgParams);
            thumbnailParams.width = mThumbailWidth;
            thumbnailParams.height = (mThumbailWidth * thumbnail.getHeight()) / thumbnail.getWidth();
            if (!this.mData.hasThumbnail() || (!this.mData.isImageVideo() && thumbnail.getWidth() < 200 && thumbnail.getHeight() < 200)) {
                thumbnailParams.height = mThumbailWidth / 4;
                thumbnailParams.width = mThumbailWidth / 4;
                if (Build.VERSION.SDK_INT >= 19) {
                    LayoutParams titleParams = new LayoutParams(this.mTitleParams);
                    LayoutParams msgParams = new LayoutParams(this.mMsgParams);
                    if (message == null || message.length() < 32) {
                        titleParams.addRule(1, R.id.m_piclayout);
                        titleParams.addRule(6, R.id.m_piclayout);
                        msgParams.addRule(3, R.id.m_piclayout);
                    } else {
                        titleParams.addRule(1, R.id.m_piclayout);
                        titleParams.addRule(6, R.id.m_piclayout);
                        titleParams.topMargin = thumbnailParams.topMargin + (thumbnailParams.height / 4);
                        msgParams.addRule(3, R.id.m_titlelayout);
                    }
                    this.mTitleLayout.setLayoutParams(titleParams);
                    this.mMessageTextView.setLayoutParams(msgParams);
                }
            }
            this.mTitleView.requestLayout();
            this.mMessageTextView.requestLayout();
            picLayoutParam.height = thumbnailParams.height;
            picLayoutParam.width = thumbnailParams.width;
            this.mPicLayout.setLayoutParams(picLayoutParam);
            this.mPicLayout.requestLayout();
            this.mPictureThumbnail.setLayoutParams(thumbnailParams);
            this.mPictureThumbnail.requestLayout();
            this.mPictureThumbnail.setData(this.mData);
            this.mPicLayout.setVisibility(0);
            this.mPictureThumbnail.setVisibility(0);
            this.mMessageTextView.setTextColor(MesiboConfiguration.TOPIC_TEXT_COLOR_WITH_PICTURE);
            this.hasImage = true;
        } else {
            if (this.mData.isDeleted()) {
                this.mMessageTextView.setTextColor(MesiboConfiguration.DELETEDTOPIC_TEXT_COLOR_WITHOUT_PICTURE);
            } else {
                this.mMessageTextView.setTextColor(MesiboConfiguration.TOPIC_TEXT_COLOR_WITHOUT_PICTURE);
            }
            if (this.hasImage) {
            }
            this.hasImage = false;
        }
        if (!this.mData.isForwarded() || TextUtils.isEmpty(MesiboUI.getConfig().forwardedTitle)) {
            this.mHeadingView.setVisibility(8);
        } else {
            this.mHeadingView.setVisibility(0);
            this.mHeadingView.setText(MesiboUI.getConfig().forwardedTitle);
        }
        if (this.mData.isDeleted() || TextUtils.isEmpty(title)) {
            this.mTitleView.setVisibility(8);
        } else {
            this.mTitleView.setVisibility(0);
            this.mTitleView.setText(title);
        }
        if (this.mData.isDeleted() || TextUtils.isEmpty(subtitle)) {
            this.mSubTitleView.setVisibility(8);
        } else {
            this.mSubTitleView.setVisibility(0);
            this.mSubTitleView.setText(subtitle);
        }
        if (!TextUtils.isEmpty(message)) {
            this.mMessageTextView.setVisibility(0);
            boolean incoming = data.getStatus() == 19 || data.getStatus() == 18;
            if (data.getFavourite().booleanValue()) {
                if (incoming) {
                    this.mMessageTextView.setText(message + " " + "              ");
                } else {
                    this.mMessageTextView.setText(message + " " + MesiboConfiguration.FAVORITED_OUTGOING_MESSAGE_DATE_View);
                }
            } else if (incoming) {
                this.mMessageTextView.setText(message + " " + "              ");
            } else {
                this.mMessageTextView.setText(message + " " + MesiboConfiguration.NORMAL_OUTGOING_MESSAGE_DATE_View);
            }
        } else {
            this.mMessageTextView.setVisibility(8);
        }
        if (thumbnail == null) {
            PTTParams.width = -2;
        }
        setLayoutParams(PTTParams);
        ViewGroup.LayoutParams PTTParams2 = this.mMessageTextView.getLayoutParams();
        if (thumbnail != null) {
            PTTParams2.width = -1;
        } else {
            PTTParams2.width = -2;
        }
        this.mMessageTextView.setLayoutParams(PTTParams2);
    }

    public void setImage(Bitmap image) {
        loadImageView();
        this.mPictureThumbnail.setImage(image);
    }
}
