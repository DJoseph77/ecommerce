package com.example.ecommerce.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.ecommerce.Activity.Categories;
import com.example.ecommerce.Adapter.CategoryAdapter;
import com.example.ecommerce.databinding.ViewholderCategoryBinding;
import com.example.ecommerce.domain.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    // You can replace this with your actual data model
    private final ArrayList<Category> items;
    private Context context;

    public CategoryAdapter(ArrayList<Category> items, Context context) {
        this.items = items;
        this.context=context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item view using ViewBinding
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewholderCategoryBinding binding = ViewholderCategoryBinding.inflate(inflater, parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category=items.get(position);
        holder.binding.textView72.setText(category.getName());
        Glide.with(context)
                .load(items.get(position).getImgUrl())
                .into(holder.binding.imageView32);
        holder.binding.category2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, Categories.class);
                intent.putExtra("categoryType",category.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder class that holds the ViewBinding instance
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderCategoryBinding binding;

        public CategoryViewHolder(ViewholderCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Access views directly through binding
        public ViewholderCategoryBinding getBinding() {
            return binding;
        }
    }
}
