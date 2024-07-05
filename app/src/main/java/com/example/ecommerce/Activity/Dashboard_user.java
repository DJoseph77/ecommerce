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

public class Dashboard_user extends AppCompatActivity {
    ActivityDashboardUserBinding binding;
    SharedPreferencesManager sharedPreferencesManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding=ActivityDashboardUserBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sharedPreferencesManager=new SharedPreferencesManager(this);
        binding.layoutLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferencesManager.clear();
                Intent intent17=new Intent(Dashboard_user.this, Login.class);
                startActivity(intent17);
                finish();
            }
        });
        binding.homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent19=new Intent(Dashboard_user.this, MainActivity.class);
                startActivity(intent19);
                finish();
            }
        });
    }
}