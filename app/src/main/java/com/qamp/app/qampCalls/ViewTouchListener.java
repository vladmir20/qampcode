package com.qamp.app.qampCalls;


import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class ViewTouchListener implements OnTouchListener {
    float dX;
    float dY;
    float x;
    float y;

    public ViewTouchListener() {
    }

    public boolean onTouch(View var1, MotionEvent var2) {
        switch(var2.getAction()) {
            case 0:
                this.dX = var1.getX() - var2.getRawX();
                this.dY = var1.getY() - var2.getRawY();
                this.x = var2.getRawX();
                this.y = var2.getRawY();
                break;
            case 1:
                int var3 = (int)(var2.getRawX() - this.x);
                int var4 = (int)(var2.getRawY() - this.y);
                if (var2.getEventTime() - var2.getDownTime() < 100L && var3 < 100 && var4 < 100) {
                    var1.performClick();
                    return true;
                }
            default:
                return false;
            case 2:
                var1.animate().x(var2.getRawX() + this.dX).y(var2.getRawY() + this.dY).setDuration(0L).start();
        }

        return true;
    }
}