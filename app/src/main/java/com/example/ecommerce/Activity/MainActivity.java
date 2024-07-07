package com.example.ecommerce.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.Adapter.ProductAdapter;
import com.example.ecommerce.Helper.SharedPreferencesManager;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityMainBinding;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.ProductCart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private SharedPreferencesManager sharedPreferencesManager;
    private ActivityMainBinding binding;
    private ArrayList<Product> allItems;
    private ArrayList<Product> popularItems;
    private DatabaseReference databaseReference;

    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        databaseReference = FirebaseDatabase.getInstance().getReference().child("products");

        sharedPreferencesManager = new SharedPreferencesManager(this);

        allItems = new ArrayList<>();
        popularItems = new ArrayList<>();
        initAllItems(allItems);
        fetchPopularItemsFromFirebase();

        setupCategoryClickListeners(); // Setup click listeners for categories

        binding.profileLogoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferencesManager.isLoggedIn()) {
                    if (sharedPreferencesManager.getUserId().equals("w4liQ3Y23eW66bOoZ1w4T5d4uhv2")){
                        Intent intent = new Intent(MainActivity.this, DashBoardActivity.class);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(MainActivity.this, Dashboard_user.class);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                }
            }
        });

        binding.cartBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));
    }
    private void fetchPopularItemsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                popularItems.clear(); // Clear previous data

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null && product.getPopular()) {
                        popularItems.add(product);
                    }
                }

                // Update RecyclerView with popular items
                initRecyclerViewPopular();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void initAllItems(ArrayList<Product> allItems) {
        allItems.add(new Product("Black T-shirt", "item_1", 15, 4, 500,
                "This black t-shirt, crafted from premium cotton, offers a perfect blend of comfort and style. " +
                        "Its sleek, minimalist design makes it a versatile wardrobe staple, suitable for both casual and semi-formal " +
                        "occasions. The fabric is soft and breathable, ensuring all-day comfort, while the classic crew neck " +
                        "and tailored fit provide a flattering silhouette. Ideal for layering or wearing on its own, this black " +
                        "t-shirt is a timeless addition to any wardrobe.\n", true));
        allItems.add(new Product("Phone", "item_3", 3, 4.9, 800, "test", true));
        allItems.add(new Product("Smart TV", "item_4", 10, 4.5, 450, "test", true));
    }


    // Update RecyclerView with popular items
    private void initRecyclerViewPopular() {
        binding.PopularView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ProductAdapter adapter = new ProductAdapter(popularItems);
        binding.PopularView.setAdapter(adapter);
    }


    private void setupCategoryClickListeners() {
        binding.category1.setOnClickListener(v -> navigateToCategory("category1"));
        binding.category2.setOnClickListener(v -> navigateToCategory("category2"));
        binding.category3.setOnClickListener(v -> navigateToCategory("category3"));
        binding.category4.setOnClickListener(v -> navigateToCategory("category4"));
    }

    private void navigateToCategory(String category) {
        Intent intent = new Intent(MainActivity.this, Categories.class);
        intent.putExtra("categoryType", category);
        startActivity(intent);
    }
}
