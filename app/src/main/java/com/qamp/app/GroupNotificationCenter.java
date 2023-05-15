/*
 * *
 *  * Created by Shivam Tiwari on 08/05/23, 4:02 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 08/05/23, 4:02 PM
 *
 */

package com.qamp.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupNotificationCenter extends Fragment implements Backpressedlistener {
    public static Backpressedlistener backpressedlistener;
    CircleImageView circleImageView3;
    TextView textView28,textView29,textView31;

    public GroupNotificationCenter() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.group_notification_center_layout, container, false);
        circleImageView3 = view.findViewById(R.id.circleImageView3);
        textView28 = view.findViewById(R.id.textView28);
        textView29 = view.findViewById(R.id.textView29);
        textView31 = view.findViewById(R.id.textView31);
        circleImageView3.setImageDrawable(getResources().getDrawable(R.drawable.demoqamp));
        textView28.setText("Qamp Awesome");
        textView29.setText("Sankalp invited you to join the gorup");
        textView31.setText("View Members");
        return view;
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