package com.example.ecommerce.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.ecommerce.Helper.CartManager;
import com.example.ecommerce.Helper.ConverterProductCart;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityDetailBinding;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.ProductCart;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private Product product;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Enable edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize CartManager
        cartManager = new CartManager(this);

        // Set click listener for back button
        binding.backbtn.setOnClickListener(v -> finish());

        // Get product details from Intent
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("object");

        // Display product details
        displayProductDetails();

        // Handle click on Add to Cart button
        binding.addToCartBtn.setOnClickListener(v -> addToCart());
    }

    private void displayProductDetails() {
        // Load product image using Glide
        String drawableResourceId = product.getPicUrl();
        Glide.with(this)
                .load(drawableResourceId)
                .into(binding.itemPic);

        // Set product details in UI
        binding.titletxt.setText(product.getTitle());
        binding.priceTxt.setText(product.getPrice() + "DT");
        binding.descriptiontxt.setText(product.getDescription());
        binding.reviewTxt.setText(String.valueOf(product.getReview()));
        binding.ratingTxt.setText(String.valueOf(product.getScore()));
    }

    private void addToCart() {
        // Convert Product to ProductCart and add to cart
        ProductCart productCart = ConverterProductCart.convertProductToCart(product);
        cartManager.addItem(productCart);

        // Show success message
        Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
    }
}
