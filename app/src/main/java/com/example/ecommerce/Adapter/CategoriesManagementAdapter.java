package com.example.ecommerce.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.databinding.ViewholderUsersBinding;

import java.util.ArrayList;

public class CategoriesManagementAdapter extends RecyclerView.Adapter<CategoriesManagementAdapter.ViewHolder> {

    private ArrayList<String> categories;
    private int selectedPosition = -1;
    private OnItemClickListener1 listener;
    private Context context;

    public interface OnItemClickListener1 {
        void onItemClick1(int position);
    }
    public CategoriesManagementAdapter(ArrayList<String> categories, Context context, OnItemClickListener1 listener) {
        this.categories = categories;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderUsersBinding binding = ViewholderUsersBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String category = categories.get(position);
        holder.binding.textView27.setText(category);

        // Set the background color based on selection
        if (selectedPosition == position) {
            holder.binding.allLayout.setBackgroundColor(Color.parseColor("#B4E380"));
        } else {
            holder.binding.allLayout.setBackgroundColor(Color.parseColor("#c7cffb"));
        }

        // Set the click listener for the item
        holder.binding.allLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Notify the change for the previously selected item
                notifyItemChanged(selectedPosition);

                // Update the selected position and notify the change
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(selectedPosition);

                // Notify the listener
                if (listener != null) {
                    listener.onItemClick1(selectedPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ViewholderUsersBinding binding;

        public ViewHolder(ViewholderUsersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
