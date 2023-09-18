package com.qamp.app.Utils;

import android.view.View;
import android.view.ViewPropertyAnimator;

import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;

public class AnimationUtils {

    public static void animateViewVisibility(final View view, final boolean isVisible) {
        if (view == null) {
            return;
        }

        float startAlpha = isVisible ? 0f : 1f;
        float endAlpha = isVisible ? 1f : 0f;

        view.setAlpha(startAlpha); // Set the initial alpha

        view.setVisibility(View.VISIBLE);

        ViewPropertyAnimator animator = view.animate()
                .alpha(endAlpha)
                .setDuration(500); // Adjust the duration as needed

        animator.withEndAction(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            }
        });

        animator.start();
    }

    public static void changeImageWithAnimation(final ImageView imageView, final int newImageResource) {
        if (imageView == null) {
            return;
        }

        imageView.animate()
                .alpha(0f)
                .setDuration(300) // Adjust the duration as needed
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageResource(newImageResource);
                        imageView.animate()
                                .alpha(1f)
                                .setDuration(300) // Adjust the duration as needed
                                .start();
                    }
                })
                .start();
    }

}
