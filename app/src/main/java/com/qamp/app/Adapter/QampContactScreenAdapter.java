package com.qamp.app.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.MessagingModule.MesiboMessagingActivity;
import com.qamp.app.MessagingModule.MesiboUI;
import com.qamp.app.Modal.QampContactScreenModel;
import com.qamp.app.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class QampContactScreenAdapter extends RecyclerView.Adapter<QampContactScreenAdapter.QampContactViewHolder> implements Filterable {
    public static ArrayList<MesiboProfile> slectedgtoup = new ArrayList<>();
    private ArrayList<QampContactScreenModel> contactList;
    private ArrayList<QampContactScreenModel> filteredContactsList;
    private Context context;

    private boolean isGroupMaking;

    private LinearLayout next_group;

    private Filter contactFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String searchString = constraint.toString().toLowerCase().trim();
            filteredContactsList.clear();

            if (searchString.isEmpty()) {
                filteredContactsList.addAll(contactList);
            } else {
                for (QampContactScreenModel contact : contactList) {
                    if (contact.getMes_rv_name().toLowerCase().contains(searchString) ||
                            contact.getMes_rv_phone().contains(searchString)) {
                        filteredContactsList.add(contact);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredContactsList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notifyDataSetChanged();
        }
    };

    public QampContactScreenAdapter(Context context, ArrayList<QampContactScreenModel> contactList, boolean isGroupMaking, LinearLayout next_group) {
        this.context = context;
        this.contactList = contactList;
        this.filteredContactsList = new ArrayList<>(contactList);
        this.isGroupMaking = isGroupMaking;
        this.next_group = next_group;
    }

    @NonNull
    @Override
    public QampContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_new_user_list, parent, false);
        return new QampContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull QampContactViewHolder holder, @SuppressLint("RecyclerView") int position) {
        QampContactScreenModel contact = contactList.get(position);
        holder.mes_rv_name.setText(contact.getMes_rv_name());
        holder.mes_rv_phone.setText(contact.getMes_rv_phone());
        holder.mes_rv_profile.setImageBitmap(contact.getmUserImage());
        // Set the checkbox state based on the isChecked field
        holder.isChecked.setOnCheckedChangeListener(null); // Avoid triggering listener during recycling
        holder.isChecked.setChecked(contact.isChecked());
        if (contact.isMesiboProfile()) {
            holder.link.setVisibility(View.GONE);
            holder.message.setVisibility(View.VISIBLE);
            holder.video_call.setVisibility(View.VISIBLE);
            holder.audio_call.setVisibility(View.VISIBLE);

            if (isGroupMaking) {
                holder.isChecked.setVisibility(View.VISIBLE);

                holder.isChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        contact.setChecked(isChecked);
                        MesiboProfile newMesiboProfile = Mesibo.getProfile(contactList.get(position).getMes_rv_phone());
                        if (isChecked) {
                            slectedgtoup.add(newMesiboProfile);
                         } else {
                            slectedgtoup.remove(newMesiboProfile);
                        }
                    }
                });
            }
            holder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MesiboMessagingActivity.class);
                    intent.putExtra(MesiboUI.MESSAGE_ID, "");
                    intent.putExtra(MesiboUI.PEER, contact.getMes_rv_phone());
                    intent.putExtra(MesiboUI.GROUP_ID, "");
                    context.startActivity(intent);
                }
            });
            holder.mes_rv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MesiboMessagingActivity.class);
                    intent.putExtra(MesiboUI.MESSAGE_ID, "");
                    intent.putExtra(MesiboUI.PEER, contact.getMes_rv_phone());
                    intent.putExtra(MesiboUI.GROUP_ID, "");
                    context.startActivity(intent);
                }
            });
            holder.mes_rv_phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MesiboMessagingActivity.class);
                    intent.putExtra(MesiboUI.MESSAGE_ID, "");
                    intent.putExtra(MesiboUI.PEER, contact.getMes_rv_phone());
                    intent.putExtra(MesiboUI.GROUP_ID, "");
                    context.startActivity(intent);
                }
            });


        } else {
            holder.isChecked.setVisibility(View.GONE);
            holder.link.setVisibility(View.VISIBLE);
            holder.message.setVisibility(View.GONE);
            holder.video_call.setVisibility(View.GONE);
            holder.audio_call.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return filteredContactsList.size();
    }

    @Override
    public Filter getFilter() {
        return contactFilter;
    }

    static class QampContactViewHolder extends RecyclerView.ViewHolder {
        private CheckBox isChecked;
        private TextView mes_rv_name;
        private TextView mes_rv_phone;
        private TextView link;
        private ImageView message;
        private ImageView video_call;
        private ImageView audio_call;

        private CircleImageView mes_rv_profile;

        QampContactViewHolder(View itemView) {
            super(itemView);
            isChecked = itemView.findViewById(R.id.isChecked);
            mes_rv_name = itemView.findViewById(R.id.mes_rv_name);
            mes_rv_phone = itemView.findViewById(R.id.mes_rv_phone);
            link = itemView.findViewById(R.id.link);
            message = itemView.findViewById(R.id.message);
            video_call = itemView.findViewById(R.id.video_call);
            audio_call = itemView.findViewById(R.id.audio_call);
            mes_rv_profile = itemView.findViewById(R.id.mes_rv_profile);
        }
    }
}