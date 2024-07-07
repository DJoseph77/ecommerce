package com.example.ecommerce.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Adapter.ProductAdapter;
import com.example.ecommerce.Adapter.ProductCartAdapter;
import com.example.ecommerce.Helper.CartManager;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityCartBinding;
import com.example.ecommerce.domain.ProductCart;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    ActivityCartBinding binding;
    private CartManager cartManager;
    private ArrayList<ProductCart> products;
    private ProductCartAdapter productCartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cartManager = new CartManager(this);
        products = cartManager.getCartItems();
        productCartAdapter = new ProductCartAdapter(products, this);
        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(productCartAdapter);
        binding.backBtn.setOnClickListener(v -> finish());
        updateCartUI();

        productCartAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateCartUI();

            }
        });


    }

    private void updateCartUI() {
        double subTotal = 0;
        for (ProductCart product : products) {
            subTotal += product.getNbrInCart() * product.getPrice();
        }

        binding.totalFeeTxt.setText(String.valueOf((int) subTotal) + " DT");
        binding.totalTxt.setText(String.valueOf((int) subTotal + 7) + " DT");

        showDiseppear();
    }

    private void showDiseppear() {
        if (cartManager.getCartItems().isEmpty()) {
            binding.scroll.setVisibility(View.GONE);
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.deliveryTxt.setText("-");
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scroll.setVisibility(View.VISIBLE);
            binding.deliveryTxt.setText("7 DT");
        }
    }
}
