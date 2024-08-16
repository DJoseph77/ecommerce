package com.example.ecommerce.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.ecommerce.Helper.CartManager;
import com.example.ecommerce.Helper.ConverterProductCart;
import com.example.ecommerce.Helper.SharedPreferencesManager;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityDetailBinding;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.ProductCart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private Product product;
    private CartManager cartManager;
    private DatabaseReference userRef;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupEdgeToEdgeDisplay();
        initializeFields();
        setBackButtonClickListener();
        getProductFromIntent();
        displayProductDetails();
        setAddToCartButtonClickListener();
        setupFavoriteButtonSize();
        checkIfUserHasRated();
        setupRateButtons();
        initializeFavoriteListener();
        setFavoriteButtonClickListener();
    }

    private void setupEdgeToEdgeDisplay() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeFields() {
        sharedPreferencesManager = new SharedPreferencesManager(this);
        if (sharedPreferencesManager.isLoggedIn()){
            String userId = sharedPreferencesManager.getUserId();
            userRef = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(userId)
                    .child("favorisProducts");

            cartManager = new CartManager(this);
        }

    }

    private void setBackButtonClickListener() {
        binding.backbtn.setOnClickListener(v -> finish());
    }

    private void getProductFromIntent() {
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("object");
    }

    private void displayProductDetails() {
        String drawableResourceId = product.getPicUrl();
        Glide.with(this)
                .load(drawableResourceId)
                .into(binding.itemPic);

        binding.titletxt.setText(product.getTitle());
        binding.priceTxt.setText(String.format("%sDT", product.getPrice()));
        binding.descriptiontxt.setText(product.getDescription());
        binding.reviewTxt.setText(String.valueOf(product.getReview()));
        binding.ratingTxt.setText(String.format("%.2f", product.getScore()));
    }

    private void setAddToCartButtonClickListener() {
        if (sharedPreferencesManager.isLoggedIn()){
            binding.addToCartBtn.setOnClickListener(v -> addToCart());
        }else {
            binding.addToCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent2=new Intent(DetailActivity.this, Login.class);
                    startActivity(intent2);
                }
            });


        }
    }

    private void addToCart() {
        ProductCart productCart = ConverterProductCart.convertProductToCart(product);
        cartManager.addItem(productCart);

        Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
    }

    private void setupFavoriteButtonSize() {
        binding.imageView5.getLayoutParams().width = 100; // Width in pixels
        binding.imageView5.getLayoutParams().height = 100; // Height in pixels
        binding.imageView5.requestLayout(); // Request to apply the changes
    }

    private void setupRateButtons() {
        binding.rate1.setOnClickListener(v -> updateRating(1));
        binding.rate2.setOnClickListener(v -> updateRating(2));
        binding.rate3.setOnClickListener(v -> updateRating(3));
        binding.rate4.setOnClickListener(v -> updateRating(4));
        binding.rate5.setOnClickListener(v -> updateRating(5));
    }

    private void updateRating(int newRating) {
        synchronized (this) {
            int currentReview = product.getReview();
            double currentScore = product.getScore();

            if(sharedPreferencesManager.isLoggedIn()){
                String userId = new SharedPreferencesManager(DetailActivity.this).getUserId();

                product.setReview(currentReview + 1);
                product.setScore(((currentScore * currentReview) + newRating) / product.getReview());
                if (product.getIdUserRated() == null) {
                    product.setIdUserRated(new ArrayList<>());
                }
                product.getIdUserRated().add(userId);

                displayProductDetails();

                DatabaseReference productRef = FirebaseDatabase.getInstance().getReference()
                        .child("products")
                        .child(product.getId());
                productRef.child("review").setValue(product.getReview());
                productRef.child("score").setValue(product.getScore())
                        .addOnSuccessListener(aVoid -> Toast.makeText(DetailActivity.this, "Rating updated successfully!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(DetailActivity.this, "Failed to update rating: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                productRef.child("idUserRated").setValue(product.getIdUserRated());
                binding.rateLayout.setVisibility(View.GONE);
                notifyAll();
            }else {
                Intent intentLogin=new Intent(DetailActivity.this, Login.class);
                startActivity(intentLogin);
            }
        }
    }

    private void checkIfUserHasRated() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("products");
        String productId = product.getId();
        productsRef.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> idUserRated = (List<String>) dataSnapshot.child("idUserRated").getValue();
                    if (idUserRated != null && idUserRated.contains(new SharedPreferencesManager(DetailActivity.this).getUserId())) {
                        showRateLayout(false);
                    } else {
                        showRateLayout(true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void showRateLayout(boolean show) {
        binding.rateLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void initializeFavoriteListener() {
        if (sharedPreferencesManager.isLoggedIn()){
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> favorisList = new ArrayList<>();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String productId = snapshot.getValue(String.class);
                            favorisList.add(productId);
                        }
                    }

                    if (favorisList.contains(product.getId())) {
                        binding.imageView5.setImageResource(R.drawable.save_icon_foreground);
                    } else {
                        binding.imageView5.setImageResource(R.drawable.bookmark);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(DetailActivity.this, "Failed to retrieve favorites: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            binding.imageView5.setImageResource(R.drawable.bookmark);
        }

    }

    private void setFavoriteButtonClickListener() {
        if (sharedPreferencesManager.isLoggedIn()) {
            binding.imageView5.setOnClickListener(v -> userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> favorisList = new ArrayList<>();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String productId = snapshot.getValue(String.class);
                            favorisList.add(productId);
                        }
                    }

                    if (favorisList.contains(product.getId())) {
                        favorisList.remove(product.getId());
                        binding.imageView5.setImageResource(R.drawable.bookmark);
                        Toast.makeText(DetailActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        favorisList.add(product.getId());
                        binding.imageView5.setImageResource(R.drawable.save_icon_foreground);
                        Toast.makeText(DetailActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    }

                    userRef.setValue(favorisList)
                            .addOnSuccessListener(aVoid -> {
                                // Successfully updated
                            })
                            .addOnFailureListener(e -> Toast.makeText(DetailActivity.this, "Failed to update favorites: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(DetailActivity.this, "Failed to retrieve favorites: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }));
        }else {
            binding.imageView5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent login=new Intent(DetailActivity.this, Login.class);
                    startActivity(login);
                }
            });
        }
    }
}
