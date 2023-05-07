/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:40 AM
 *
 */

package com.qamp.app.qampcallss.api.p000ui;

import android.view.MotionEvent;
import android.view.View;

/* renamed from: com.mesibo.calls.api.ui.ViewTouchListener */
public class ViewTouchListener implements View.OnTouchListener {

    /* renamed from: dX */
    float f8dX;

    /* renamed from: dY */
    float f9dY;

    /* renamed from: x */
    float f10x;

    /* renamed from: y */
    float f11y;

    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0:
                this.f8dX = view.getX() - motionEvent.getRawX();
                this.f9dY = view.getY() - motionEvent.getRawY();
                this.f10x = motionEvent.getRawX();
                this.f11y = motionEvent.getRawY();
                return true;
            case 1:
                int rawX = (int) (motionEvent.getRawX() - this.f10x);
                int rawY = (int) (motionEvent.getRawY() - this.f11y);
                if (motionEvent.getEventTime() - motionEvent.getDownTime() < 100 && rawX < 100 && rawY < 100) {
                    view.performClick();
                    return true;
                }
            case 2:
                view.animate().x(motionEvent.getRawX() + this.f8dX).y(motionEvent.getRawY() + this.f9dY).setDuration(0).start();
                return true;
        }
        return false;
    }
}
