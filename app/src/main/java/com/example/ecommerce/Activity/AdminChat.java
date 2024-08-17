package com.example.ecommerce.Activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.Adapter.MessageAdminAdapater;
import com.example.ecommerce.Helper.SharedPreferencesManager;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityAdminChatBinding;
import com.example.ecommerce.domain.BaseMessage;
import com.example.ecommerce.domain.MessageAdmin;
import com.example.ecommerce.domain.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminChat extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ArrayList<String> userChatsId;
    private HashMap<String, ArrayList<BaseMessage>> chatList;

    private ArrayList<MessageAdmin> messageAdmins;
    private SharedPreferencesManager sharedPreferencesManager;
    private ActivityAdminChatBinding binding;
    private MessageAdminAdapater adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminChatBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sharedPreferencesManager=new SharedPreferencesManager(AdminChat.this);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        userChatsId = new ArrayList<>();
        chatList = new HashMap<>();
        messageAdmins = new ArrayList<>();

        binding.usersChat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new MessageAdminAdapater(messageAdmins, this);
        binding.usersChat.setAdapter(adapter);
        bottomBarAction();

        chercheUsersWithMessages();
    }

    private void chercheUsersWithMessages() {
        DatabaseReference chatRef = databaseReference.child("chats");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userChatsId.clear();
                chatList.clear();
                messageAdmins.clear();

                for (DataSnapshot idUser : snapshot.getChildren()) {
                    String chatId = idUser.getKey();
                    if (chatId == null) continue;

                    ArrayList<BaseMessage> messages = new ArrayList<>();
                    for (DataSnapshot idChat : idUser.getChildren()) {
                        BaseMessage message = idChat.getValue(BaseMessage.class);
                        if (message != null) {
                            messages.add(message);
                        }
                    }

                    if (!messages.isEmpty()) {
                        orderMessages(messages);
                        chatList.put(chatId, messages);
                        userChatsId.add(chatId);
                    }
                }

                orderChats();
                populateMessageAdmins();
                adapter.notifyDataSetChanged();  // Notify adapter about data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void orderMessages(ArrayList<BaseMessage> messages) {
        messages.sort((m1, m2) -> Long.compare(m2.getCreatedAt(), m1.getCreatedAt())); // Sort in descending order
    }

    private void orderChats() {
        userChatsId.sort((id1, id2) -> {
            long time1 = chatList.get(id1).get(0).getCreatedAt();
            long time2 = chatList.get(id2).get(0).getCreatedAt();
            return Long.compare(time2, time1); // Sort in descending order
        });
    }

    private void populateMessageAdmins() {
        final ArrayList<MessageAdmin> updatedMessageAdmins = new ArrayList<>();
        final int[] pendingNameRequests = {userChatsId.size()};

        for (String chatId : userChatsId) {
            ArrayList<BaseMessage> messages = chatList.get(chatId);
            if (messages != null && !messages.isEmpty()) {
                BaseMessage latestMessage = messages.get(0);
                DatabaseReference userPath = FirebaseDatabase.getInstance().getReference().child("users").child(chatId).child("name");

                userPath.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.getValue(String.class);
                        if (name != null) {
                            MessageAdmin messageAdmin = new MessageAdmin();
                            messageAdmin.setName(name);
                            messageAdmin.setMessage(latestMessage.getMessage());
                            messageAdmin.setTime(latestMessage.getCreatedAt());
                            messageAdmin.setId(chatId);

                            updatedMessageAdmins.add(messageAdmin);
                        }

                        // Check if all names have been fetched
                        if (--pendingNameRequests[0] == 0) {
                            messageAdmins.clear();
                            messageAdmins.addAll(updatedMessageAdmins);
                            adapter.notifyDataSetChanged();  // Notify adapter about data changes
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
            } else {
                pendingNameRequests[0]--;  // Decrement counter if no messages
                if (pendingNameRequests[0] == 0) {
                    messageAdmins.clear();
                    messageAdmins.addAll(updatedMessageAdmins);
                    adapter.notifyDataSetChanged();  // Notify adapter about data changes
                }
            }
        }
    }
    private void bottomBarAction(){

        binding.profileLogoMain.setOnClickListener(v -> {
            ActivityOptions options = ActivityOptions.makeScaleUpAnimation(
                    v, // View that we are animating from
                    (int) v.getWidth() / 2, // X coordinate to start scaling from (button center)
                    (int) v.getHeight() / 2, // Y coordinate to start scaling from (button center)
                    v.getWidth(), // Final width of the animation
                    v.getHeight() // Final height of the animation
            );
            if (sharedPreferencesManager.isLoggedIn()) {
                Intent intent = sharedPreferencesManager.getIsAdmin() ?
                        new Intent(AdminChat.this, DashBoardActivity.class) :
                        new Intent(AdminChat.this, Dashboard_user.class);
                startActivity(intent,options.toBundle());
            } else {
                startActivity(new Intent(AdminChat.this, Login.class),options.toBundle());
            }
        });
        binding.cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptions options = ActivityOptions.makeScaleUpAnimation(
                        v, // View that we are animating from
                        (int) v.getWidth() / 2, // X coordinate to start scaling from (button center)
                        (int) v.getHeight() / 2, // Y coordinate to start scaling from (button center)
                        v.getWidth(), // Final width of the animation
                        v.getHeight() // Final height of the animation
                );
                if (sharedPreferencesManager.isLoggedIn()){
                    startActivity(new Intent(AdminChat.this, CartActivity.class),options.toBundle());
                }else {
                    startActivity(new Intent(AdminChat.this, Login.class),options.toBundle());
                }
            }
        });
        binding.wichList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptions options = ActivityOptions.makeScaleUpAnimation(
                        v, // View that we are animating from
                        (int) v.getWidth() / 2, // X coordinate to start scaling from (button center)
                        (int) v.getHeight() / 2, // Y coordinate to start scaling from (button center)
                        v.getWidth(), // Final width of the animation
                        v.getHeight() // Final height of the animation
                );
                if (sharedPreferencesManager.isLoggedIn()) {
                    navigateToCategory("favorites",options);
                } else {
                    startActivity(new Intent(AdminChat.this, Login.class),options.toBundle());
                }
            }
        });
        binding.home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptions options = ActivityOptions.makeScaleUpAnimation(
                        v, // View that we are animating from
                        (int) v.getWidth() / 2, // X coordinate to start scaling from (button center)
                        (int) v.getHeight() / 2, // Y coordinate to start scaling from (button center)
                        v.getWidth(), // Final width of the animation
                        v.getHeight() // Final height of the animation
                );
                Intent intentHome=new Intent(AdminChat.this,MainActivity.class);
                startActivity(intentHome,options.toBundle());
            }
        });

    }
    private void navigateToCategory(String category,ActivityOptions options) {

        Intent intent = new Intent(AdminChat.this, Categories.class);
        intent.putExtra("categoryType", category);
        startActivity(intent,options.toBundle());
    }

}
