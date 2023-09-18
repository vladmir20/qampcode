package com.qamp.app.CustomClasses;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.qamp.app.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class OnlineStatusImageView extends AppCompatImageView {
    private Drawable onlineStatusDrawable;
    private boolean isOnline = false;
    private int onlineStatusSize; // Size of the online status indicator

    public OnlineStatusImageView(Context context) {
        super(context);
        init();
    }

    public OnlineStatusImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OnlineStatusImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Load the online status drawable from resources
        onlineStatusDrawable = getResources().getDrawable(R.drawable.avatar_active_online);

        // Set the default size of the online status indicator (you can adjust this as needed)
        onlineStatusSize = (int) getResources().getDimension(R.dimen.default_online_status_size);
    }

    public void setOnlineStatus(boolean isOnline) {
        this.isOnline = isOnline;
        // Request a redraw to update the view
        invalidate();
    }

    public void setOnlineStatusSize(int size) {
        this.onlineStatusSize = size;
        // Request a redraw to update the view
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the online status indicator if the user is online
        if (isOnline && onlineStatusDrawable != null) {
            int width = getWidth();
            int height = getHeight();

            // Calculate the position and size of the online status indicator
            int left = width - onlineStatusSize;
            int top = height - onlineStatusSize;
            int right = width;
            int bottom = height;

            onlineStatusDrawable.setBounds(left, top, right, bottom);
            onlineStatusDrawable.draw(canvas);
        }
    }
}

