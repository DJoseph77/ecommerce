package com.example.ecommerce.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.ecommerce.Activity.DetailActivity;
import com.example.ecommerce.databinding.ViewholderPupListBinding;
import com.example.ecommerce.domain.Product;

import java.util.ArrayList;

public  class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    ArrayList<Product> items;
    Context context;
    ViewholderPupListBinding binding;

    public ProductAdapter(ArrayList<Product> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=ViewholderPupListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        context=parent.getContext();
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        binding.titleTxt.setText(items.get(position).getTitle());
        binding.feeTxt.setText(items.get(position).getPrice()+"DT");
        binding.scoreTxt.setText(""+items.get(position).getScore());
        binding.reviewTxt.setText(""+items.get(position).getReview());

        String drawableResourced=items.get(position).getPicUrl();
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
        public ViewHolder(ViewholderPupListBinding binding) {
            super(binding.getRoot());
        }
    }
}
