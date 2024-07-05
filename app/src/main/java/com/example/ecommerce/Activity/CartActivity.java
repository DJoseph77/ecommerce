package com.example.ecommerce.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.Adapter.CartAdapter;
import com.example.ecommerce.Helper.ChangeNumberItemsListener;
import com.example.ecommerce.Helper.ManagmentCart;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityCartBinding;

public class CartActivity extends AppCompatActivity {
    private ManagmentCart managmentCart;
    ActivityCartBinding binding;
    double tax;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCartBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        managmentCart=new ManagmentCart(this);
        setVariable();
        initlist();
        calculatorCart();
        check();
        
    }

    private void initlist() {
        if(managmentCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scroll.setVisibility(View.GONE);
        }else{
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scroll.setVisibility(View.VISIBLE);
        }
        binding.cartView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        binding.cartView.setAdapter(new CartAdapter(managmentCart.getListCart(), () -> calculatorCart()));
    }
    private void calculatorCart(){
        double percentTax=0.02;
        double delivery=7;
        tax=Math.round(managmentCart.getTotalFee() * percentTax * 100) / 100;
        double total=Math.round((managmentCart.getTotalFee()+tax+delivery) *100) / 100;
        double itemTotal=Math.round(managmentCart.getTotalFee()*100)/100;
        binding.totalFeeTxt.setText(itemTotal+"TD");
        binding.taxTxt.setText(tax+"TD");
        binding.deliveryTxt.setText(delivery+"TD");
        binding.totalTxt.setText(total+"TD");
    }
    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
    }
    private void check(){
        if(managmentCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scroll.setVisibility(View.GONE);
        }else{
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scroll.setVisibility(View.VISIBLE);
        }

    }
}