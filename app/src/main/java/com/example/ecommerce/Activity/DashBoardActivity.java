package com.example.ecommerce.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.ecommerce.Adapter.settingAdminAdapter;
import com.example.ecommerce.Helper.SharedPreferencesManager;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityDashBoardBinding;
import com.example.ecommerce.domain.Setting;
import com.example.ecommerce.domain.permission;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashBoardActivity extends AppCompatActivity {
    ActivityDashBoardBinding binding;
    private SharedPreferencesManager sharedPreferencesManager;
    private DatabaseReference databaseReference;
    ArrayList<Setting> settings;
    Intent intent77;
    private settingAdminAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sharedPreferencesManager=new SharedPreferencesManager(this);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        intent77=new Intent(DashBoardActivity.this, CartsInProgress.class);
        updateUiUser();
        setupRecycler();
    }

    private void setupRecycler() {
        settings = new ArrayList<>();
        adapter = new settingAdminAdapter(settings, DashBoardActivity.this);
        binding.settings.setLayoutManager(new GridLayoutManager(DashBoardActivity.this, 2));
        binding.settings.setAdapter(adapter);  // Set the adapter here

        String userId = new SharedPreferencesManager(DashBoardActivity.this).getUserId();
        DatabaseReference permissionRef = FirebaseDatabase.getInstance().getReference().child("permissions").child(userId);

        permissionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                settings.clear();  // Clear previous data

                Log.d("setupRecycler", "DataSnapshot: " + snapshot.toString());

                permission permission = snapshot.getValue(permission.class);
                if (permission != null) {
                    Log.d("setupRecycler", "Permission data retrieved");

                    if (permission.getCategoriesManagement()) {
                        Setting settingManagement = new Setting();
                        settingManagement.setImgUrl(R.drawable.manage_icon_foreground);
                        settingManagement.setTitle("categories & management");
                        settings.add(settingManagement);
                        Log.d("setupRecycler", "Added categoriesManagement setting");
                    }

                    if (permission.getProducts()) {
                        Setting settingProducts = new Setting();
                        settingProducts.setImgUrl(R.drawable.product12);
                        settingProducts.setTitle("products");
                        settings.add(settingProducts);
                        Log.d("setupRecycler", "Added products setting");
                    }

                    if (permission.getOrders()) {
                        Setting settingCartInProgress = new Setting();
                        settingCartInProgress.setImgUrl(R.drawable.order_icon);
                        settingCartInProgress.setTitle("order In progress");
                        Setting settingCartConfirmed = new Setting();
                        settingCartConfirmed.setImgUrl(R.drawable.order_icon);
                        settingCartConfirmed.setTitle("order Confirmed");
                        Setting settingHistory = new Setting();
                        settingHistory.setImgUrl(R.drawable.order_icon);
                        settingHistory.setTitle("order History");
                        settings.add(settingCartInProgress);
                        settings.add(settingCartConfirmed);
                        settings.add(settingHistory);
                        Log.d("setupRecycler", "Added orders settings");
                    }
                } else {
                    Log.d("setupRecycler", "Permission data is null");
                }

                Setting settingLogOut = new Setting();
                settingLogOut.setImgUrl(R.drawable.log_out_foreground);
                settingLogOut.setTitle("log Out");
                settings.add(settingLogOut);

                Setting settingRolette=new Setting();
                settingRolette.setImgUrl(R.drawable.spinner);
                settingRolette.setTitle("Roulette");
                settings.add(settingRolette);


                // Notify the adapter of data change
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("setupRecycler", "Database error: " + error.getMessage());
            }
        });
    }
    private void updateUiUser() {
        String userId = sharedPreferencesManager.getUserId();
        updateName(userId);

    }
    private void updateName(String userId){
        DatabaseReference userName = databaseReference.child("users").child(userId).child("name");

        userName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.getValue(String.class);
                    binding.textView12.setText(name);
                } else {
                    binding.textView12.setText("User not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                binding.textView12.setText("Error: " + databaseError.getMessage());
            }
        });
    }
}