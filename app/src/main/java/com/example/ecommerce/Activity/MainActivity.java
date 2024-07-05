package com.example.ecommerce.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.Adapter.PopularAdapter;
import com.example.ecommerce.Helper.SharedPreferencesManager;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityMainBinding;
import com.example.ecommerce.domain.PopularDomain;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private SharedPreferencesManager sharedPreferencesManager;
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sharedPreferencesManager = new SharedPreferencesManager(this);
        initRecyclerView();
        bottomNavigation();
        binding.profileLogoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferencesManager.isLoggedIn()){
                    String userId = sharedPreferencesManager.getUserId();
                    Intent intent21=new Intent(MainActivity.this, Dashboard_user.class);
                    startActivity(intent21);
                }
                else{
                    Intent intent=new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void bottomNavigation() {
        binding.cartBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,CartActivity.class)));
    }

    private void initRecyclerView() {
        ArrayList<PopularDomain> items=new ArrayList<>();
        items.add(new PopularDomain("Black T-shirt","item_1",15,4,500,"This black" +
                " t-shirt, crafted from premium cotton, offers a perfect blend of comfort and style. Its sleek," +
                " minimalist design makes it a versatile wardrobe staple, suitable for both casual and semi-formal" +
                " occasions. The fabric is soft and breathable, ensuring all-day comfort, while the classic crew neck " +
                "and tailored fit provide a flattering silhouette. Ideal for layering or wearing on its own, this black " +
                "t-shirt is a timeless addition to any wardrobe.\n" ));
        items.add(new PopularDomain("Phone","item_3",3,4.9,800,"test"));
        items.add(new PopularDomain("Smart TV","item_4",10,4.5,450,"test"));

        binding.PopularView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        binding.PopularView.setAdapter(new PopularAdapter(items));
    }
}