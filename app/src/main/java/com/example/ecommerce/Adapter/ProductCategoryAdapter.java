package com.example.ecommerce.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.ecommerce.Activity.DetailActivity;
import com.example.ecommerce.databinding.ViewholderProductManagerBinding;
import com.example.ecommerce.databinding.ViewholderPupListBinding;
import com.example.ecommerce.domain.Product;

import java.util.ArrayList;

public  class ProductCategoryAdapter extends RecyclerView.Adapter<ProductCategoryAdapter.ViewHolder> {
    ArrayList<Product> items;
    Context context;
    ViewholderProductManagerBinding binding;

    public ProductCategoryAdapter(ArrayList<Product> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ProductCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=ViewholderProductManagerBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        context=parent.getContext();
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCategoryAdapter.ViewHolder holder, int position) {
        binding.titleTxt.setText(items.get(position).getTitle());


        int drawableResourced=holder.itemView.getResources().getIdentifier(items.get(position).getPicUrl()
                ,"drawable",holder.itemView.getContext().getPackageName());
        Glide.with(context)
                .load(drawableResourced)
                .transform(new GranularRoundedCorners(30,30,0,0))
                .into(binding.pic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent=new Intent(context, DetailActivity.class);
            intent.putExtra("object",items.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(ViewholderProductManagerBinding binding) {
            super(binding.getRoot());
        }
    }
}
