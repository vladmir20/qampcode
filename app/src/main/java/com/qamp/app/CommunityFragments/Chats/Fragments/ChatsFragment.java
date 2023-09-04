package com.qamp.app.CommunityFragments.Chats.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qamp.app.CommunityFragments.Chats.Model.ChatsModel;
import com.qamp.app.R;

import java.util.ArrayList;

public class ChatsFragment extends Fragment {


    ArrayList<ChatsModel> data = new ArrayList<>();
    private RecyclerView chatsRecycle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        chatsRecycle = view.findViewById(R.id.chats_recyl);
        addData();

        //Log.v("data Test",data.get(0).getPersonName());

        ChatsAdapter chatsAdapter = new ChatsAdapter(data);
        chatsRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
        chatsRecycle.setAdapter(chatsAdapter);

        return view;
    }

    private void addData() {
        data.clear();
        data.add(new ChatsModel("Aditya", "How Are You", "10M", "1"));
        //data.add(new ChatsModel("Rahul","What time You Arrive","20M","2"));
    }


    public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

        private ArrayList<ChatsModel> data;

        public ChatsAdapter(ArrayList<ChatsModel> data) {
            this.data = data;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_chat, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.personName.setText("Aditya");
            holder.lastMessage.setText("Hello how are you");
            holder.time.setText("10M");
            holder.messageCount.setText("1");

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView personName;
            TextView lastMessage;
            TextView time;
            TextView messageCount;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                this.personName = itemView.findViewById(R.id.personName);
                this.lastMessage = itemView.findViewById(R.id.lastMessage);
                this.time = itemView.findViewById(R.id.time);
                this.messageCount = itemView.findViewById(R.id.messageCount);

            }
        }
    }
}
