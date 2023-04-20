/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:31 PM
 *
 */

package com.qamp.app.messaging;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboMessage;
import com.qamp.app.R;
import com.qamp.app.messaging.AllUtils.MyTrace;

import java.lang.ref.WeakReference;
import java.util.List;

public class MessageAdapter extends SelectableAdapter<RecyclerView.ViewHolder> {
    private MessageViewHolder.ClickListener clickListener = null;
    private List<MessageData> mChatList = null;
    private Context mContext = null;
    private WeakReference<MesiboRecycleViewHolder.Listener> mCustomViewListener = null;
    private String mDateCoin = null;
    private int mDisplayMsgCnt = 0;
    private ImageView mImageVu = null;
    private MessagingAdapterListener mListener = null;
    int mOriginalId = 0;
    private ProgressBar mProgress = null;
    private int mTotalMessages = 0;
    private int mcellHeight = 0;

    public interface MessagingAdapterListener {
        boolean isMoreMessage();

        void loadMoreMessages();

        void showMessageInvisible();
    }

    public MessageAdapter(Context context, MessagingAdapterListener listener, List<MessageData> ChatList, MessageViewHolder.ClickListener cl1, MesiboRecycleViewHolder.Listener customViewListner) {
        this.mContext = context;
        this.mChatList = ChatList;
        this.mListener = listener;
        this.clickListener = cl1;
        setListener(customViewListner);
        this.mDisplayMsgCnt = 30;
        this.mDateCoin = "";
        this.mTotalMessages = this.mChatList.size();
        this.mDisplayMsgCnt = this.mTotalMessages;
        this.mcellHeight = 0;
    }

    public int getItemViewType(int position) {
        MesiboRecycleViewHolder.Listener l;
        int viewType;
        MesiboMessage m = this.mChatList.get(position).getMesiboMessage();
        if (m.isDate()) {
            return 3;
        }
        if (37 == m.getStatus()) {
            return 4;
        }
        if (m.isCustom()) {
            return 100;
        }
        if (this.mCustomViewListener != null && (l = (MesiboRecycleViewHolder.Listener) this.mCustomViewListener.get()) != null && (viewType = l.Mesibo_onGetItemViewType(m)) >= 100) {
            return viewType;
        }
        int status = this.mChatList.get(position).getStatus();
        if (21 == status) {
            return 5;
        }
        if (35 == status) {
            return 6;
        }
        if (18 == status || 19 == status) {
            return 1;
        }
        return 2;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        MyTrace.start("Messaging-CVH");
        MesiboRecycleViewHolder holder = null;
        if (this.mCustomViewListener != null) {
            MesiboRecycleViewHolder.Listener l = (MesiboRecycleViewHolder.Listener) this.mCustomViewListener.get();
            if (l != null) {
                holder = l.Mesibo_onCreateViewHolder(viewGroup, viewType);
            }
            if (holder != null) {
                holder.setCustom(true);
            }
        }
        if (holder == null) {
            if (viewType == 7) {
                holder = new EmptyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_empty_view, viewGroup, false));
            } else if (viewType == 2) {
                holder = new MessageViewHolder(viewType, LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.me_user, viewGroup, false), this.clickListener);
            } else if (viewType == 1) {
                holder = new MessageViewHolder(viewType, LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.other_user, viewGroup, false), this.clickListener);
            } else if (viewType == 3) {
                holder = new DateViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_date_view, viewGroup, false));
            } else if (viewType == 4) {
                holder = new SystemMessageViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_systemmessage_view, viewGroup, false), this.mContext, MesiboConfiguration.headerCellBackgroundColor, false);
            } else if (viewType == 5) {
                holder = new SystemMessageViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_systemmessage_view, viewGroup, false), this.mContext, MesiboConfiguration.otherCellsBackgroundColor, true);
            } else if (viewType == 6) {
                holder = new SystemMessageViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_systemmessage_view, viewGroup, false), this.mContext, MesiboConfiguration.headerCellBackgroundColor, true);
            } else if (viewType >= 100) {
                holder = new SystemMessageViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_systemmessage_view, viewGroup, false), this.mContext, MesiboConfiguration.otherCellsBackgroundColor, false);
            }
        }
        if (holder != null) {
            holder.setType(viewType);
        }
        MyTrace.stop();
        if (holder != null) {
            holder.setAdapter(this);
        }
        return holder;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        String msg;
        MesiboUI.Config opts = MesiboUI.getConfig();
        MyTrace.start("Messaging-BVH");
        if (i >= 20) {
            this.mListener.showMessageInvisible();
        } else if (this.mListener.isMoreMessage()) {
            this.mListener.loadMoreMessages();
        }
        MesiboRecycleViewHolder h = (MesiboRecycleViewHolder) holder;
        int type = h.getType();
        h.setItemPosition(i);
        MessageData cm = this.mChatList.get(i);
        cm.setDirty(false);
        if (h.getCustom()) {
            MesiboRecycleViewHolder.Listener l = (MesiboRecycleViewHolder.Listener) this.mCustomViewListener.get();
            cm.setViewHolder(h);
            if (l != null) {
                l.Mesibo_onBindViewHolder(h, type, isSelected(i), cm.getMesiboMessage());
            }
            MyTrace.stop();
            return;
        }
        if (i > 0 && cm.getGroupId() > 0) {
            cm.checkPreviousData(this.mChatList.get(i - 1));
        }
        if (7 != type) {
            if (3 == type) {
                ((DateViewHolder) holder).mDate.setText(cm.getDateStamp());
            } else if (1 == type || 2 == type) {
                ((MessageViewHolder) holder).setData(cm, i, isSelected(i));
            } else if (4 == type) {
                ((SystemMessageViewHolder) holder).setText(opts.headerTitle, MesiboImages.getHeaderImage());
            } else if (6 == type) {
                SystemMessageViewHolder smvh = (SystemMessageViewHolder) holder;
                int messageType = cm.getMessageType();
                if (messageType == 1) {
                    msg = opts.e2eeActive;
                } else if (messageType == 3) {
                    msg = opts.e2eeIdentityChanged;
                } else {
                    msg = opts.e2eeInactive;
                }
                smvh.setText(msg.replaceAll("AppName", Mesibo.getAppName()), MesiboImages.getE2EEImage());
            } else if (5 == type) {
                SystemMessageViewHolder smvh2 = (SystemMessageViewHolder) holder;
                if ((cm.getMessageType() & 1) > 0) {
                    smvh2.setText(opts.missedVideoCallTitle + " " + opts.f0at + " " + cm.getTimestamp(), MesiboImages.getMissedVideoCallImage());
                } else {
                    smvh2.setText(opts.missedVoiceCallTitle + " " + opts.f0at + " " + cm.getTimestamp(), MesiboImages.getMissedVoiceCallImage());
                }
            }
        }
        MyTrace.stop();
    }

    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        MyTrace.start("Messaging-RVH");
        if (holder == null) {
            super.onViewRecycled(holder);
            MyTrace.stop();
        } else if (holder instanceof DateViewHolder) {
            super.onViewRecycled(holder);
            MyTrace.stop();
        } else if (holder instanceof SystemMessageViewHolder) {
            super.onViewRecycled(holder);
            MyTrace.stop();
        } else if (holder instanceof EmptyViewHolder) {
            super.onViewRecycled(holder);
            MyTrace.stop();
        } else {
            MesiboRecycleViewHolder h = (MesiboRecycleViewHolder) holder;
            if (h.getCustom()) {
                MesiboRecycleViewHolder.Listener l = (MesiboRecycleViewHolder.Listener) this.mCustomViewListener.get();
                if (l != null) {
                    l.Mesibo_onViewRecycled(h);
                }
                MyTrace.stop();
                return;
            }
            MessageViewHolder mvh = (MessageViewHolder) holder;
            if (mvh != null) {
                mvh.reset();
            }
            super.onViewRecycled(holder);
            MyTrace.stop();
        }
    }

    public static class DateViewHolder extends MesiboRecycleViewHolder {
        protected TextView mDate;

        public DateViewHolder(View v) {
            super(v);
            this.mDate = (TextView) v.findViewById(R.id.chat_date);
        }
    }

    public class VerticalImageSpan extends ImageSpan {
        public VerticalImageSpan(Drawable drawable) {
            super(drawable);
        }

        public VerticalImageSpan(Context context, Bitmap image) {
            super(context, image);
        }

        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fontMetricsInt) {
            Drawable drawable = getDrawable();
            Rect rect = drawable.getBounds();
            if (fontMetricsInt != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.descent - fmPaint.ascent;
                drawable.setBounds(0, 0, fontHeight, fontHeight);
                rect = drawable.getBounds();
                int drHeight = rect.bottom - rect.top;
                int centerY = fmPaint.ascent + (fontHeight / 2);
                fontMetricsInt.ascent = centerY - (drHeight / 2);
                fontMetricsInt.top = fontMetricsInt.ascent;
                fontMetricsInt.bottom = (drHeight / 2) + centerY;
                fontMetricsInt.descent = fontMetricsInt.bottom;
            }
            return rect.right;
        }

        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            Drawable drawable = getDrawable();
            canvas.save();
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.descent - fmPaint.ascent;
            drawable.setBounds(0, 0, fontHeight, fontHeight);
            canvas.translate(x, (float) (((fmPaint.descent + y) - (fontHeight / 2)) - ((drawable.getBounds().bottom - drawable.getBounds().top) / 2)));
            drawable.draw(canvas);
            canvas.restore();
        }
    }

    public class SystemMessageViewHolder extends MesiboRecycleViewHolder {
        private Context mContext = null;
        private Bitmap mImage = null;
        protected ImageView mImageView = null;
        private String mText = null;
        protected TextView mTextView = null;

        public SystemMessageViewHolder(View v, Context context, int color, boolean showImage) {
            super(v);
            this.mTextView = (TextView) v.findViewById(R.id.system_msg_text);
            this.mImageView = (ImageView) v.findViewById(R.id.system_msg_icon);
            Utils.createRoundDrawable(context, v.findViewById(R.id.system_msg_layout), color, 9.0f);
        }

        /* access modifiers changed from: package-private */
        public void setSpannableText() {
            this.mTextView.setVisibility(0);
            if (this.mImage == null) {
                this.mTextView.setText(this.mText);
                return;
            }
            SpannableStringBuilder ssb = new SpannableStringBuilder(this.mText);
            ssb.setSpan(new VerticalImageSpan(this.mContext, this.mImage), 0, 1, 17);
            this.mTextView.setText(ssb, TextView.BufferType.SPANNABLE);
        }

        public void setText(String text, Bitmap image) {
            this.mText = "  " + text;
            this.mImage = image;
            setSpannableText();
        }

        public void setText(String text, int drawable) {
            setText(text, BitmapFactory.decodeResource(this.mContext.getResources(), drawable));
        }
    }

    public class EmptyViewHolder extends MesiboRecycleViewHolder {
        public EmptyViewHolder(View v) {
            super(v);
        }
    }

    public void addRow() {
        this.mDisplayMsgCnt++;
        this.mTotalMessages = this.mChatList.size();
    }

    public float getItemHeight() {
        return (float) this.mcellHeight;
    }

    public void clearSelections() {
        List<Integer> selection = getSelectedItems();
        clearSelectedItems();
        for (Integer i : selection) {
            notifyItemChanged(i.intValue());
        }
    }

    public String copyData() {
        String copiedData = "";
        for (Integer i : getSelectedItems()) {
            copiedData = (copiedData + this.mChatList.get(i.intValue()).getDisplayMessage()) + "\n";
        }
        return copiedData;
    }

    public int globalPosition(int position) {
        return position;
    }

    public void updateStatus(int index) {
        notifyItemChanged(index);
    }

    public int getItemCount() {
        if (this.mChatList != null) {
            return this.mChatList.size();
        }
        return 0;
    }

    public void setListener(MesiboRecycleViewHolder.Listener listener) {
        this.mCustomViewListener = new WeakReference<>(listener);
    }

    public MesiboRecycleViewHolder.Listener getListener() {
        if (this.mCustomViewListener == null) {
            return null;
        }
        return (MesiboRecycleViewHolder.Listener) this.mCustomViewListener.get();
    }
}
