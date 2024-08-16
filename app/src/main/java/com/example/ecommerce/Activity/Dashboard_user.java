package com.example.ecommerce.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecommerce.Helper.SharedPreferencesManager;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityDashboardUserBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dashboard_user extends AppCompatActivity {
    ActivityDashboardUserBinding binding;
    SharedPreferencesManager sharedPreferencesManager;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityDashboardUserBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferencesManager = new SharedPreferencesManager(this);
        binding.relCartInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.layoutLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferencesManager.clear();
                Intent intent17 = new Intent(Dashboard_user.this, Login.class);
                startActivity(intent17);
                finish();
            }
        });

        binding.homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent19 = new Intent(Dashboard_user.this, MainActivity.class);
                startActivity(intent19);
                finish();
            }
        });

        updateUiUser();
    }

    private void updateUiUser() {
        String userId = sharedPreferencesManager.getUserId();
        updateName(userId);
        updateCountry(userId);

    }
    private void updateName(String userId){
        DatabaseReference userName = databaseReference.child("users").child(userId).child("name");

        userName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.getValue(String.class);
                    binding.textView17.setText(name);
                } else {
                    binding.textView17.setText("User not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                binding.textView17.setText("Error: " + databaseError.getMessage());
            }
        });
    }
    private void updateCountry(String userId){
        DatabaseReference userName = databaseReference.child("users").child(userId).child("country");

        userName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.getValue(String.class);
                    binding.textView26.setText(name);
                } else {
                    binding.textView26.setText("User not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                binding.textView26.setText("Error: " + databaseError.getMessage());
            }
        });
    }

}
