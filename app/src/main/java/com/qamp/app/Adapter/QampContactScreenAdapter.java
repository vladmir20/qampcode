package com.qamp.app.Adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.CustomClasses.CheckboxImageView;
import com.qamp.app.MesiboImpModules.ContactSyncClass;
import com.qamp.app.Modal.QampContactScreenModel;
import com.qamp.app.R;

import java.util.ArrayList;

public class QampContactScreenAdapter extends RecyclerView.Adapter<QampContactScreenAdapter.QampContactViewHolder> {
    public static ArrayList<MesiboProfile> slectedgtoup = new ArrayList<>();
    private ArrayList<QampContactScreenModel> contactList;
    private ArrayList<QampContactScreenModel> filteredContactsList;
    private Context context;

    private boolean isGroupMaking;

    private ImageView next_group;
    private TextView selectedContacts;

    public void filterContacts(String text) {
        //text = text.toLowerCase();
        filteredContactsList.clear();
        if (text.isEmpty()) {
            filteredContactsList.addAll(contactList);
        } else {
            for (int i = 0; i < contactList.size(); i++) {
                if (contactList.get(i).getMes_rv_name().contains(text.toString())
                        || contactList.get(i).getMes_rv_phone().contains(text.toString())) {
                    filteredContactsList.add(contactList.get(i));
                }
            }
        }
        Log.d("Filtered Contacts", filteredContactsList.toString());
        notifyDataSetChanged();
    }

    public QampContactScreenAdapter(Context context, ArrayList<QampContactScreenModel> contactList, boolean isGroupMaking, ImageView next_group, TextView selectedContacts) {
        this.context = context;
        this.contactList = contactList;
        this.filteredContactsList = new ArrayList<>(contactList);
        this.isGroupMaking = isGroupMaking;
        this.next_group = next_group;
        this.selectedContacts = selectedContacts;
    }

    @NonNull
    @Override
    public QampContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact_screen, parent, false);
        return new QampContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull QampContactViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            QampContactScreenModel contact = contactList.get(position);
            holder.mes_rv_name.setText(contact.getMes_rv_name());
            holder.mes_rv_phone.setText(contact.getMes_rv_phone());
            holder.mes_rv_profile.setImageBitmap(contact.getmUserImage());
            // Set the checkbox state based on the isChecked field
            //holder.isChecked.setOnClickListener(null); // Avoid triggering listener during recycling
            holder.isChecked.setChecked(contact.isChecked());
            if (contact.isMesiboProfile()) {
                holder.link.setVisibility(View.GONE);
                holder.message.setVisibility(View.VISIBLE);
                holder.video_call.setVisibility(View.VISIBLE);
                holder.audio_call.setVisibility(View.VISIBLE);

                if (isGroupMaking) {
                    holder.isChecked.setVisibility(View.VISIBLE);
                    holder.link.setVisibility(View.GONE);
                    holder.message.setVisibility(View.GONE);
                    holder.video_call.setVisibility(View.GONE);
                    holder.audio_call.setVisibility(View.GONE);
                    holder.isChecked.setOnCheckedChangeListener(new CheckboxImageView.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CheckboxImageView checkboxImageView, boolean isChecked) {
                            contact.setChecked(isChecked);
                            MesiboProfile newMesiboProfile = Mesibo.getProfile(contactList.get(position).getMes_rv_phone());
                            if (isChecked) {
                                slectedgtoup.add(newMesiboProfile);
                            } else {
                                slectedgtoup.remove(newMesiboProfile);
                            }
                            if (slectedgtoup.size() > 0) {
                                selectedContacts.setText(slectedgtoup.size() + " Contacts Selected");
                            } else {
                                selectedContacts.setText(ContactSyncClass.deviceContactList.size() + " Contacts");
                            }
                            // Apply the animation
                            float scale = isChecked ? 1.2f : 1.0f;
                            ObjectAnimator scaleX = ObjectAnimator.ofFloat(checkboxImageView, View.SCALE_X, scale);
                            ObjectAnimator scaleY = ObjectAnimator.ofFloat(checkboxImageView, View.SCALE_Y, scale);

                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.play(scaleX).with(scaleY);
                            animatorSet.setDuration(100); // Duration of the animation in milliseconds
                            animatorSet.start();
                        }
                    });

                } else {
                    holder.isChecked.setVisibility(View.GONE);
                    holder.link.setVisibility(View.GONE);
                    holder.message.setVisibility(View.VISIBLE);
                    holder.video_call.setVisibility(View.VISIBLE);
                    holder.audio_call.setVisibility(View.VISIBLE);
                    holder.audio_call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MesiboProfile mUserr = Mesibo.getProfile(contact.getMes_rv_phone());
//                            if (!MesiboCall.getInstance().callUi(v.getContext(), mUserr, false))
//                                MesiboCall.getInstance().callUiForExistingCall(v.getContext());
                            Toast.makeText(context, "call", Toast.LENGTH_SHORT).show();
                        }
                    });
                    holder.video_call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MesiboProfile mUserr = Mesibo.getProfile(contact.getMes_rv_phone());
//                            if (!MesiboCall.getInstance().callUi(v.getContext(), mUserr, true))
//                                MesiboCall.getInstance().callUiForExistingCall(v.getContext());
                            Toast.makeText(context, "video call", Toast.LENGTH_SHORT).show();
                        }
                    });
                    holder.mes_rv_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (contact.isMesiboProfile()) {
//                                Intent intent = new Intent(context, MesiboMessagingActivity.class);
//                                intent.putExtra(MesiboUI.MESSAGE_ID, "");
//                                intent.putExtra(MesiboUI.PEER, contact.getMes_rv_phone());
//                                intent.putExtra(MesiboUI.GROUP_ID, "");
//                                context.startActivity(intent);
                                Toast.makeText(context, "messaging", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    holder.mes_rv_phone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (contact.isMesiboProfile()) {
//                                Intent intent = new Intent(context, MesiboMessagingActivity.class);
//                                intent.putExtra(MesiboUI.MESSAGE_ID, "");
//                                intent.putExtra(MesiboUI.PEER, contact.getMes_rv_phone());
//                                intent.putExtra(MesiboUI.GROUP_ID, "");
//                                context.startActivity(intent);
                                Toast.makeText(context, "phone", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    holder.message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Intent intent = new Intent(context, MesiboMessagingActivity.class);
//                            intent.putExtra(MesiboUI.MESSAGE_ID, "");
//                            intent.putExtra(MesiboUI.PEER, contact.getMes_rv_phone());
//                            intent.putExtra(MesiboUI.GROUP_ID, "");
//                            context.startActivity(intent);
                            Toast.makeText(context, "message", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } else {
                holder.isChecked.setVisibility(View.GONE);
                holder.link.setVisibility(View.VISIBLE);
                holder.message.setVisibility(View.GONE);
                holder.video_call.setVisibility(View.GONE);
                holder.audio_call.setVisibility(View.GONE);
                holder.mes_rv_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                holder.mes_rv_phone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
        } catch (IndexOutOfBoundsException e) {
            Log.e("error", e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return filteredContactsList.size();
    }



    static class QampContactViewHolder extends RecyclerView.ViewHolder {
        private CheckboxImageView isChecked;
        private TextView mes_rv_name;
        private TextView mes_rv_phone;
        private TextView link;
        private ImageView message;
        private ImageView video_call;
        private ImageView audio_call;
        private ShapeableImageView mes_rv_profile;

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