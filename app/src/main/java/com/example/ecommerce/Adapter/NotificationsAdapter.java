package com.example.ecommerce.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.domain.Notification;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private ArrayList<Notification> notifications;
    private DatabaseReference notificationsRef;

    public NotificationsAdapter(ArrayList<Notification> notifications, DatabaseReference notificationsRef) {
        this.notifications = notifications;
        this.notificationsRef = notificationsRef;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.messageTextView.setText(notification.getMessage());
        holder.timestampTextView.setText(notification.getTimestamp());

        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the notification from Firebase
                notificationsRef.child(notification.getKey()).removeValue();

                // Remove the notification from the local list
                notifications.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, notifications.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView timestampTextView;
        public ImageView removeButton;

        public ViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text_view);
            timestampTextView = itemView.findViewById(R.id.timestamp_text_view);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }
}
