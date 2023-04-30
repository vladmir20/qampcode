package com.qamp.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class CreateCommunityThree extends Fragment {

    Button next;
    ImageView cancel;
    TextView channelName;
    String channelTitle, lat, lng, channelDescription;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.add_channel_three, container, false);

        next = view.findViewById(R.id.buttonNext);
        cancel = view.findViewById(R.id.cancel_1);
        channelName = view.findViewById(R.id.channelName);


        Bundle bundle = getArguments();
        if (bundle != null) {
            channelTitle = bundle.getString("ChannlName");
            lat = bundle.getString("Latitude");
            lng = bundle.getString("Longitude");
            channelDescription = bundle.getString("ChannelDescription");
            channelName.setText(channelTitle);
        }


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment createCommunityFour = new CreateCommunityFour();
                final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle bundle1 = new Bundle();
                bundle1.putString("ChannelName",channelTitle);
                bundle1.putString("Latitude",lat);
                bundle1.putString("Longitude",lng);
                bundle1.putString("ChannelDescription", channelDescription);
                createCommunityFour.setArguments(bundle1);
                transaction.replace(R.id.frameLayout, createCommunityFour);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }
}