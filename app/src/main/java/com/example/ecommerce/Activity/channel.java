package com.example.ecommerce.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.Adapter.MessageListAdapter;
import com.example.ecommerce.Helper.SharedPreferencesManager;
import com.example.ecommerce.databinding.ActivityChannelBinding;
import com.example.ecommerce.domain.BaseMessage;
import com.example.ecommerce.domain.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class channel extends AppCompatActivity {
    ActivityChannelBinding binding;
    private List<BaseMessage> messageList = new ArrayList<>();
    private MessageListAdapter adapter;
    private DatabaseReference messagesRef;
    private DatabaseReference usersRef;
    private SharedPreferencesManager sharedPreferencesManager;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChannelBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        userId=intent.getStringExtra("id");

        sharedPreferencesManager=new SharedPreferencesManager(this);

        // Initialize Firebase references
        messagesRef = FirebaseDatabase.getInstance().getReference("chats");
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        fetchChat();
        // Initialize RecyclerView adapter
        adapter = new MessageListAdapter(this, messageList);
        binding.recyclerGchat.setLayoutManager(new LinearLayoutManager(channel.this));
        binding.recyclerGchat.setAdapter(adapter);


        // Send button click listener
        binding.buttonGchatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void fetchChat() {
        DatabaseReference userChatsRef = messagesRef.child(userId);  // Reference to the user's chat messages

        // Listen for changes in the chat messages
        userChatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageList.clear();  // Clear the existing messages
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    try {
                        BaseMessage message = chatSnapshot.getValue(BaseMessage.class);
                        if (message != null) {
                            messageList.add(message);
                        }
                    } catch (DatabaseException e) {
                        Log.e(TAG, "Failed to parse message", e);
                    }
                }
                adapter.notifyDataSetChanged();  // Notify the adapter to refresh the RecyclerView
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(channel.this, "Failed to load messages: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String messageText = binding.editGchatMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            BaseMessage message = new BaseMessage();
            message.setMessage(messageText);

            // Fetch current user details and set sender
            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (sharedPreferencesManager.getIsAdmin()){
                            user.setEmail(sharedPreferencesManager.getEmail());
                            user.setName("Admin");
                            user.setAdmin(true);
                        }
                        if (user != null) {
                            message.setSender(user); // Set sender in message
                            long currentTimeMillis = System.currentTimeMillis();
                            message.setCreatedAt(currentTimeMillis);

                            // Push message to Firebase
                            messagesRef.child(userId).push().setValue(message)
                                    .addOnSuccessListener(aVoid -> {
                                        // Clear input field after sending
                                        binding.editGchatMessage.setText("");
                                        Toast.makeText(channel.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(channel.this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(channel.this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(channel.this, "Failed to retrieve user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(channel.this, "Please enter a message", Toast.LENGTH_SHORT).show();
        }
    }
}
