package com.example.ecommerce.Adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Helper.SharedPreferencesManager;
import com.example.ecommerce.databinding.ViewholderFirstBinding;
import com.example.ecommerce.databinding.ViewholderSecondBinding;
import com.example.ecommerce.domain.BaseMessage;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private Context mContext;
    private List<BaseMessage> mMessageList;
    DateUtils Utils=new DateUtils();

    public MessageListAdapter(Context context, List<BaseMessage> messageList) {
        mContext = context;
        mMessageList = messageList;
    }
    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        BaseMessage message =  mMessageList.get(position);
        SharedPreferencesManager sharedPreferencesManager=new SharedPreferencesManager(mContext);
        if (message.getSender().getAdmin() && sharedPreferencesManager.getIsAdmin()){
            return VIEW_TYPE_MESSAGE_SENT;
        }else {
            if (message.getSender().getEmail().equals(sharedPreferencesManager.getEmail())) {
                // If the current user is the sender of the message
                return VIEW_TYPE_MESSAGE_SENT;
            } else {
                // If some other user sent the message
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
        }

    }
    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            ViewholderFirstBinding binding = ViewholderFirstBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new SentMessageHolder(binding);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            ViewholderSecondBinding binding = ViewholderSecondBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ReceivedMessageHolder(binding);
        }
        return null;
    }
    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseMessage message =  mMessageList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }
    private class SentMessageHolder extends RecyclerView.ViewHolder {
        ViewholderFirstBinding binding;

        SentMessageHolder(ViewholderFirstBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(BaseMessage message) {
            binding.textGchatMessageMe.setText(message.getMessage());
            long messageTime = message.getCreatedAt(); // Use the actual message timestamp
            int flags = DateUtils.FORMAT_SHOW_TIME;
            int flags2 = DateUtils.FORMAT_SHOW_DATE;
            binding.textGchatTimestampMe.setText(DateUtils.formatDateTime(mContext, messageTime, flags));
            binding.textGchatDateMe.setText(DateUtils.formatDateTime(mContext, messageTime, flags2));
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        ViewholderSecondBinding binding;

        ReceivedMessageHolder(ViewholderSecondBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(BaseMessage message) {
            binding.textGchatMessageOther.setText(message.getMessage());
            binding.textGchatUserOther.setText(message.getSender().getName());
            // Load profile image into ImageView if needed
            long messageTime = message.getCreatedAt(); // Use the actual message timestamp
            int flags = DateUtils.FORMAT_SHOW_TIME;
            int flags2 = DateUtils.FORMAT_SHOW_DATE;
            binding.textgchattimestampother.setText(DateUtils.formatDateTime(mContext, messageTime, flags));
            binding.textgchatdateother.setText(DateUtils.formatDateTime(mContext, messageTime, flags2));
        }
    }

    public void updateMessages(List<BaseMessage> messages) {
        this.mMessageList = messages;
        notifyDataSetChanged();
    }
}