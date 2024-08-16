package com.example.ecommerce.Adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Activity.channel;
import com.example.ecommerce.databinding.ViewholderMessageAdminBinding;
import com.example.ecommerce.domain.MessageAdmin;

import java.util.ArrayList;

public class MessageAdminAdapater extends RecyclerView.Adapter<MessageAdminAdapater.ViewHolder> {
    private final ArrayList<MessageAdmin> messageAdmins;
    private final Context context;

    public MessageAdminAdapater(ArrayList<MessageAdmin> messageAdmins, Context context) {
        this.messageAdmins = messageAdmins;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderMessageAdminBinding binding = ViewholderMessageAdminBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageAdmin messageAdmin = messageAdmins.get(position);

        holder.binding.name.setText(messageAdmin.getName());
        holder.binding.lastMessage.setText(messageAdmin.getMessage());
        int flags = DateUtils.FORMAT_SHOW_TIME;
        holder.binding.time.setText(DateUtils.formatDateTime(context, messageAdmin.getTime(), flags));
        holder.binding.allMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.notifMessage.setVisibility(View.GONE);
                holder.binding.allMessage.setElevation(0f);
                Intent intent=new Intent(context, channel.class);
                intent.putExtra("id",messageAdmin.getId());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return messageAdmins.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderMessageAdminBinding binding;

        public ViewHolder(ViewholderMessageAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
