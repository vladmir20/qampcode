/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.MessagingModule;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    /* access modifiers changed from: private */
    @Nullable
    public View childView;
    /* access modifiers changed from: private */
    public int childViewPosition;
    private GestureDetector gestureDetector;
    protected OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View view, int i);

        void onItemLongPress(View view, int i);
    }

    public RecyclerItemClickListener(Context context, OnItemClickListener listener2) {
        this.gestureDetector = new GestureDetector(context, new GestureListener());
        this.listener = listener2;
    }

    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent event) {
        this.childView = view.findChildViewUnder(event.getX(), event.getY());
        this.childViewPosition = view.getChildPosition(this.childView);
        return this.childView != null && this.gestureDetector.onTouchEvent(event);
    }

    public void onTouchEvent(RecyclerView view, MotionEvent event) {
    }

    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public static abstract class SimpleOnItemClickListener implements OnItemClickListener {
        public void onItemClick(View childView, int position) {
        }

        public void onItemLongPress(View childView, int position) {
        }
    }

    protected class GestureListener extends GestureDetector.SimpleOnGestureListener {
        protected GestureListener() {
        }

        public boolean onSingleTapUp(MotionEvent event) {
            if (RecyclerItemClickListener.this.childView == null) {
                return true;
            }
            RecyclerItemClickListener.this.listener.onItemClick(RecyclerItemClickListener.this.childView, RecyclerItemClickListener.this.childViewPosition);
            return true;
        }

        public void onLongPress(MotionEvent event) {
            if (RecyclerItemClickListener.this.childView != null) {
                RecyclerItemClickListener.this.listener.onItemLongPress(RecyclerItemClickListener.this.childView, RecyclerItemClickListener.this.childViewPosition);
            }
        }

        public boolean onDown(MotionEvent event) {
            return true;
        }
    }
}
