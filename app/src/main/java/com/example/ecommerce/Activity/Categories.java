package com.example.ecommerce.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.ecommerce.Adapter.ProductCategoryAdapter;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityCategoriesBinding;
import com.example.ecommerce.domain.Product;

import java.util.ArrayList;

public class Categories extends AppCompatActivity {
    ActivityCategoriesBinding binding;
    ArrayList<Product> Products;
    private String Category;
    ArrayList<Product> ProductsDependCategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityCategoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent=getIntent();
        Category=intent.getStringExtra("categoryType");
        GridLayoutManager gridLayoutManager =new GridLayoutManager(this,2);
        binding.recycler.setLayoutManager(gridLayoutManager);

    }
}