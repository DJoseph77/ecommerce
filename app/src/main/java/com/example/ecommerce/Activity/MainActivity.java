package com.example.ecommerce.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Adapter.CategoryAdapter;
import com.example.ecommerce.Adapter.ProductAdapter;
import com.example.ecommerce.Helper.SharedPreferencesManager;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityMainBinding;
import com.example.ecommerce.domain.Category;
import com.example.ecommerce.domain.Notification;
import com.example.ecommerce.domain.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private SharedPreferencesManager sharedPreferencesManager;
    private ActivityMainBinding binding;
    private ArrayList<Product> categoryItems;
    private ArrayList<Product> popularItems;
    private DatabaseReference databaseReference;
    private ArrayList<Product> allItems;
    private ProductAdapter productAdapter; // Adapter for RecyclerView

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
        categoryItems = new ArrayList<>();
        popularItems = new ArrayList<>();

        // Initialize the adapter and set it to the RecyclerView
        productAdapter = new ProductAdapter(allItems, false);
        binding.recycleMainRecherche.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recycleMainRecherche.setAdapter(productAdapter);

        fetchAllItemsFromFirebase();
        updateUi();
        setupCategories();
        setupCategoryClickListeners(); // Setup click listeners for categories
        binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent97=new Intent(MainActivity.this, Speen.class);
                startActivity(intent97);
            }
        });
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferencesManager.isLoggedIn()) {
                    if (sharedPreferencesManager.getIsAdmin()){
                        Intent intent0=new Intent(MainActivity.this, AdminChat.class);
                        startActivity(intent0);
                    }else{
                        Intent intentMessage=new Intent(MainActivity.this, channel.class);
                        intentMessage.putExtra("id",sharedPreferencesManager.getUserId());
                        startActivity(intentMessage);
                    }
                }else {
                    startActivity(new Intent(MainActivity.this, Login.class));
                }
            }
        });

        binding.editTextCherche.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    binding.anythingElse.setVisibility(View.GONE);
                    binding.recycleMainRecherche.setVisibility(View.VISIBLE);
                } else {
                    binding.anythingElse.setVisibility(View.VISIBLE);
                    binding.recycleMainRecherche.setVisibility(View.GONE);
                }
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text is changed
            }
        });

        binding.profileLogoMain.setOnClickListener(v -> {
            if (sharedPreferencesManager.isLoggedIn()) {
                Intent intent = sharedPreferencesManager.getIsAdmin() ?
                        new Intent(MainActivity.this, DashBoardActivity.class) :
                        new Intent(MainActivity.this, Dashboard_user.class);
                startActivity(intent);
            } else {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });

        binding.cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferencesManager.isLoggedIn()){
                    startActivity(new Intent(MainActivity.this, CartActivity.class));
                }else {
                    startActivity(new Intent(MainActivity.this, Login.class));
                }
            }
        });
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent127=new Intent(MainActivity.this, NotificationsActivity.class);
                startActivity(intent127);
                markAllNotificationsAsSeen();
            }
        });
    }

    private void setupCategoryClickListeners() {
        binding.category1.setOnClickListener(v -> navigateToCategory("Category 1"));
        binding.category2.setOnClickListener(v -> navigateToCategory("Category 2"));
        binding.category3.setOnClickListener(v -> navigateToCategory("Category 3"));
        binding.category4.setOnClickListener(v -> navigateToCategory("Category 4"));
        binding.wichList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferencesManager.isLoggedIn()) {
                    navigateToCategory("favorites");
                } else {
                    startActivity(new Intent(MainActivity.this, Login.class));
                }
            }
        });
    }

    private void fetchAllItemsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allItems.clear(); // Clear previous data
                popularItems.clear(); // Clear previous data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        allItems.add(product);
                        if (product.getPopular()) {
                            popularItems.add(product);
                        }
                    }
                }

                // Update RecyclerView with popular items
                initRecyclerViewPopular();
                productAdapter.notifyDataSetChanged(); // Notify the adapter about data changes
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void initRecyclerViewPopular() {
        binding.PopularView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ProductAdapter adapter = new ProductAdapter(popularItems, false);
        binding.PopularView.setAdapter(adapter);
    }

    private void navigateToCategory(String category) {
        Intent intent = new Intent(MainActivity.this, Categories.class);
        intent.putExtra("categoryType", category);
        startActivity(intent);
    }

    private void filterProducts(String query) {
        ArrayList<Product> filteredProducts = new ArrayList<>();
        for (Product product : allItems) {
            if (product.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredProducts.add(product);
            }
        }
        // Update RecyclerView with filtered items
        initRecyclerViewAll(filteredProducts);
    }

    private void initRecyclerViewAll(ArrayList<Product> filteredArray) {
        ProductAdapter adapter = new ProductAdapter(filteredArray, false);
        binding.recycleMainRecherche.setAdapter(adapter);
    }

    private void updateUi() {
        if (sharedPreferencesManager.isLoggedIn()) {
            String userId = sharedPreferencesManager.getUserId();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("name");
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    binding.textView4.setText(snapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        updateUnseenNotificationCount();
    }

    private void setupCategories() {
        binding.categoriesRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        ArrayList<Category> categories = new ArrayList<>();
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("categories");
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories.clear(); // Clear the list to avoid duplication
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Category category = categorySnapshot.getValue(Category.class);
                    categories.add(category);
                }
                // Notify the adapter that data has changed
                CategoryAdapter categoryAdapter = new CategoryAdapter(categories, MainActivity.this);
                binding.categoriesRecycler.setAdapter(categoryAdapter);
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(MainActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUnseenNotificationCount() {
        if (sharedPreferencesManager.isLoggedIn()){
            String userId = sharedPreferencesManager.getUserId();
            DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference().child("notifications").child(userId);

            notificationsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int unseenCount = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Notification notification = snapshot.getValue(Notification.class);
                        if (notification != null && !notification.getSeen()) {
                            unseenCount++;
                        }
                    }
                    if (unseenCount > 0) {
                        binding.textView2.setText(String.valueOf(unseenCount));
                        binding.textView2.setVisibility(View.VISIBLE);
                        binding.imageView4.setVisibility(View.VISIBLE);
                    } else {
                        binding.textView2.setVisibility(View.GONE);
                        binding.imageView4.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors.
                    Toast.makeText(getApplicationContext(), "Error retrieving notifications: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    private void markAllNotificationsAsSeen() {
        String userId = sharedPreferencesManager.getUserId();
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference().child("notifications").child(userId);

        notificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notification notification = snapshot.getValue(Notification.class);
                    if (notification != null && !notification.getSeen()) {
                        snapshot.getRef().child("seen").setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
                Toast.makeText(getApplicationContext(), "Error marking notifications as seen: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
