/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 2:11 PM
 *
 */

package com.qamp.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class FeedLayoutFragment extends Fragment implements Backpressedlistener {
    public static Backpressedlistener backpressedlistener;
    private CardView postCardButton;
    public FeedLayoutFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.feedlayoutfragment, container, false);
        initViews(view);
        postCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AddPostActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void initViews(View view) {
        postCardButton = view.findViewById(R.id.postCardButton);
    }

    @Override
    public void onBackPressed() {

    }
    @Override
    public void onPause() {
        backpressedlistener=null;
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        backpressedlistener=this;
    }
}