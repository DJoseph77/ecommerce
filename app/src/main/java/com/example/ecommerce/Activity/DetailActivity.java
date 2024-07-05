package com.example.ecommerce.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.ecommerce.Helper.ManagmentCart;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityDetailBinding;
import com.example.ecommerce.databinding.ActivityMainBinding;
import com.example.ecommerce.domain.PopularDomain;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private PopularDomain object;
    private int numberOrder=1 ;
    private ManagmentCart managmentCart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityDetailBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getBundles();aaa
        managmentCart=new ManagmentCart(this);
    }

    private void getBundles() {
        object=(PopularDomain) getIntent().getSerializableExtra("object");
        int drawableResourceId=this.getResources().getIdentifier(object.getPicUrl(),"drawable",this.getPackageName());
        Glide.with(this)
                .load(drawableResourceId)
                .into(binding.itemPic);
        binding.titletxt.setText(object.getTitle());
        binding.priceTxt.setText(object.getPrice()+"DT");
        binding.descriptiontxt.setText(object.getDescription());
        binding.reviewTxt.setText(object.getReview()+"");
        binding.ratingTxt.setText(""+object.getScore());
        binding.addToCartBtn.setOnClickListener(v -> {
            object.setNumberInchart(numberOrder);
            managmentCart.insertFood(object);
        });
        binding.backbtn.setOnClickListener(v -> finish());
    }
}