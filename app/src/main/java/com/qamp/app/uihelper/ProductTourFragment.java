// 
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.uihelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.qamp.app.R;

public class ProductTourFragment extends Fragment
{
    static final String SCREEN_INDEX = "layoutid";
    
    public static ProductTourFragment newInstance(final int screenIndex) {
        final ProductTourFragment pane = new ProductTourFragment();
        final Bundle args = new Bundle();
        args.putInt("layoutid", screenIndex);
        pane.setArguments(args);
        return pane;
    }
    
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final MesiboUiHelperConfig config = MesiboUiHelper.getConfig();
        if (null == config) {
            return null;
        }
        final int index = this.getArguments().getInt("layoutid", -1);
        if (index < 0) {
            return null;
        }
        final WelcomeScreen screen = MesiboUiHelperConfig.mScreens.get(index);
        int layoutId = screen.getLayoutId();
        if (layoutId <= 0) {
            layoutId = R.layout.tour_fragment;
        }
        final ViewGroup rootView = (ViewGroup)inflater.inflate(layoutId, container, false);
        if (layoutId == R.layout.tour_fragment) {
            final View v = rootView.findViewById(R.id.welcome_fragment);
            if (null != v && screen.getBackgroundColor() != 0) {
                v.setBackgroundColor(screen.getBackgroundColor());
            }
            final ImageView image = (ImageView)rootView.findViewById(R.id.welcomeImage);
            if (null != image) {
                image.setImageResource(screen.getResourceId());
            }
            final AutoResizeTextView heading = (AutoResizeTextView)rootView.findViewById(R.id.heading);
            if (null != heading) {
                heading.setMaxLines(1);
                heading.setText((CharSequence)screen.getTitle());
            }
            final AutoResizeTextView content = (AutoResizeTextView)rootView.findViewById(R.id.content);
            if (null != content) {
                content.setMaxLines(3);
                content.setText((CharSequence)screen.getDescription());
            }
        }
        if (MesiboUiHelper.mProductTourListener != null) {
            MesiboUiHelper.mProductTourListener.onProductTourViewLoaded((View)rootView, index, screen);
        }
        return (View)rootView;
    }
}
