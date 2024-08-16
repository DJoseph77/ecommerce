package com.example.ecommerce.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.databinding.ViewholderCategoriesAddBinding;
import com.example.ecommerce.databinding.ViewholderPupListBinding;

import java.util.ArrayList;

public class AddCategoryAdapter extends RecyclerView.Adapter<AddCategoryAdapter.ViewHolder> {
    private ViewholderCategoriesAddBinding binding;
    Context context;
    ArrayList<String> categories;

    public AddCategoryAdapter(ArrayList<String> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public AddCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding= ViewholderCategoriesAddBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        context=parent.getContext();
        return new AddCategoryAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddCategoryAdapter.ViewHolder holder, int position) {
        binding.categoryItem.setText(categories.get(position));
        binding.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categories.remove(position);
                notifyItemRemoved(position); // Notify adapter about item removal
                notifyItemRangeChanged(position, categories.size()); // Refresh remaining items
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(ViewholderCategoriesAddBinding binding) {
            super(binding.getRoot());
        }
    }

}
