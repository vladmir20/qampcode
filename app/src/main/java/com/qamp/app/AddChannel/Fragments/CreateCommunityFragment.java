package com.qamp.app.AddChannel.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.qamp.app.R;


public class CreateCommunityFragment extends Fragment {


    public CreateCommunityFragment() {
        // Required empty public constructor
    }

    Button next ;
    ImageView cancel;
    EditText channelName;
    EditText channelDescr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.create_community, container, false);

        next = view.findViewById(R.id.buttonNext);
        cancel = view.findViewById(R.id.cancel_1);
        channelName = view.findViewById(R.id.editTextTextPersonName);
        channelDescr = view.findViewById(R.id.editTextTextPersonName2);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment createCommunityTwo = new CreateCommunityTwo();

                Bundle bundle = new Bundle();
                bundle.putString("ChannelName",channelName.getText().toString());
                bundle.putString("ChannelDescription", channelDescr.getText().toString());
                final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                createCommunityTwo.setArguments(bundle);
                transaction.replace(R.id.frameLayout, createCommunityTwo);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }
}