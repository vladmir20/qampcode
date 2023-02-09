// 
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.uihelper.Utils;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AlphaAnimation;
import android.view.View;

public class AnimationUtils
{
    public static void animateView(final View view, final boolean show, final int duration) {
        view.clearAnimation();
        float startalpha;
        final float maxAlpha = startalpha = 0.6f;
        float endAlpha = 0.0f;
        if (show) {
            startalpha = 0.0f;
            endAlpha = maxAlpha;
        }
        final AlphaAnimation animation = new AlphaAnimation(startalpha, endAlpha);
        animation.setDuration((long)duration);
        animation.setFillAfter(true);
        view.setVisibility(0);
        view.startAnimation((Animation)animation);
        animation.setAnimationListener((Animation.AnimationListener)new Animation.AnimationListener() {
            public void onAnimationEnd(final Animation arg0) {
                view.setVisibility(show ? 0 : 8);
                if (!show) {
                    view.clearAnimation();
                    view.setVisibility(8);
                }
            }
            
            public void onAnimationRepeat(final Animation arg0) {
            }
            
            public void onAnimationStart(final Animation arg0) {
            }
        });
    }
    
    private void animateImage(final ImageView iv) {
        final int vy = iv.getLayoutParams().height;
        final int x = iv.getDrawable().getBounds().width() / 2;
        final int y = iv.getDrawable().getBounds().height() / 2;
        final float animation_range = -100.0f;
        final int duration = 8000;
        final TranslateAnimation _translateAnimation = new TranslateAnimation(0, 0.0f, 0, animation_range, 0, 0.0f, 0, 0.0f);
        _translateAnimation.setDuration((long)duration);
        _translateAnimation.setRepeatCount(-1);
        _translateAnimation.setRepeatMode(2);
        _translateAnimation.setInterpolator((Interpolator)new LinearInterpolator());
        iv.startAnimation((Animation)_translateAnimation);
    }
}
