package com.example.ecommerce.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.ecommerce.Adapter.ProductAdapter;
import com.example.ecommerce.Helper.SharedPreferencesManager;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityCategoriesBinding;
import com.example.ecommerce.domain.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Categories extends AppCompatActivity {

    private ActivityCategoriesBinding binding;
    private String category;
    private List<Product> allItems = new ArrayList<>();
    private List<Product> productsDependCategory = new ArrayList<>();
    private DatabaseReference databaseReference;
    private SharedPreferencesManager sharedPreferencesManager;
    private List<String> idProductsFavoris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCategoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupWindowInsets();
        setupSharedPreferencesManager();
        setupListeners();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        category = getIntent().getStringExtra("categoryType");

        fetchAllItemsFromFirebase();
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupSharedPreferencesManager() {
        sharedPreferencesManager = new SharedPreferencesManager(this);
    }

    private void setupListeners() {
        binding.profileLogoMain.setOnClickListener(v -> {
            Intent intent = sharedPreferencesManager.isLoggedIn() ?
                    (sharedPreferencesManager.getIsAdmin() ?
                            new Intent(Categories.this, DashBoardActivity.class) :
                            new Intent(Categories.this, Dashboard_user.class)) :
                    new Intent(Categories.this, Login.class);
            startActivity(intent);
        });

        binding.cartBtn.setOnClickListener(v -> startActivity(new Intent(Categories.this, CartActivity.class)));

        binding.backBtn.setOnClickListener(v -> {
            startActivity(new Intent(Categories.this, MainActivity.class));
            finish();
        });

        binding.editTextCherche.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text is changed
            }
        });
    }

    private void fetchAllItemsFromFirebase() {
        databaseReference.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        allItems.add(product);
                    }
                }
                filterProducts(binding.editTextCherche.getText().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Error fetching products: " + databaseError.getMessage());
            }
        });
    }

    private void filterProducts(String query) {
        productsDependCategory.clear();

        if (!"favorites".equals(category)) {
            for (Product product : allItems) {
                if (product.getCategories() != null && product.getCategories().contains(category) &&
                        product.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    productsDependCategory.add(product);
                }
            }
        } else {
            fetchFavoriteProducts(query);
        }

        initRecyclerView();
    }

    private void fetchFavoriteProducts(String query) {
        String userId = sharedPreferencesManager.getUserId();
        DatabaseReference userRefFav = databaseReference.child("users").child(userId).child("favorisProducts");

        userRefFav.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idProductsFavoris.clear();
                for (DataSnapshot idSnapChot : snapshot.getChildren()) {
                    idProductsFavoris.add(idSnapChot.getValue(String.class));
                }
                fetchFavoriteProductsFromDatabase(query);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error fetching favorite products: " + error.getMessage());
            }
        });
    }

    private void fetchFavoriteProductsFromDatabase(String query) {
        databaseReference.child("products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsDependCategory.clear();
                for (DataSnapshot id : snapshot.getChildren()) {
                    if (idProductsFavoris.contains(id.getKey())) {
                        Product product = id.getValue(Product.class);
                        if (product != null && product.getTitle().toLowerCase().contains(query.toLowerCase())) {
                            productsDependCategory.add(product);
                        }
                    }
                }
                initRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error fetching favorite products from database: " + error.getMessage());
            }
        });
    }

    private void initRecyclerView() {
        binding.recycler.setLayoutManager(new GridLayoutManager(this, 2));
        ProductAdapter adapter = new ProductAdapter(productsDependCategory, false);
        binding.recycler.setAdapter(adapter);
    }
}
