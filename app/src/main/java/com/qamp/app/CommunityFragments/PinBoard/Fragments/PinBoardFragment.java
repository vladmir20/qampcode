package com.qamp.app.CommunityFragments.PinBoard.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qamp.app.CommunityFragments.Feed.Model.PostModel;
import com.qamp.app.R;

import java.util.ArrayList;

public class PinBoardFragment extends Fragment {

    RecyclerView pinBoardRecycle;
    ArrayList<PostModel> data= new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pin_board, container, false);

        pinBoardRecycle = view.findViewById(R.id.pinBoardRecyl);
        addData();


        PinBoardAdapter pinBoardAdapter = new PinBoardAdapter(data);
        pinBoardRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
        pinBoardRecycle.setAdapter(pinBoardAdapter);


        return view;
    }

    private void addData() {
        data.clear();
        data.add(new PostModel("Aditya","New Delhi,India","What an amazing Day"));
        data.add(new PostModel("Rahul","Patna,Bihar","I love the smell of the rain"));
    }

    public class PinBoardAdapter extends RecyclerView.Adapter<PinBoardAdapter.ViewHolder> {

        ArrayList<PostModel> data;
        public PinBoardAdapter(ArrayList<PostModel> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_post,parent,false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.titlePerson.setText(data.get(position).getTitlePerson());
            holder.locationPost.setText(data.get(position).getLocationPost());
            holder.caption.setText(data.get(position).getCaption());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView titlePerson;
            TextView locationPost;
            TextView caption;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                this.titlePerson = itemView.findViewById(R.id.titleProfile);
                this.locationPost = itemView.findViewById(R.id.locationPost);
                this.caption = itemView.findViewById(R.id.caption);
            }
        }
    }
}