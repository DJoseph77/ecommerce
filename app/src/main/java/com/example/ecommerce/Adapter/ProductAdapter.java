package com.example.ecommerce.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.ecommerce.Activity.DetailActivity;
import com.example.ecommerce.Helper.SharedPreferencesManager;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ViewholderPupListBinding;
import com.example.ecommerce.domain.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private ViewholderPupListBinding binding;
    private List<Product> items;
    private List<String> ids; // List to store ids
    private Context context;
    private DatabaseReference databaseReference;
    private boolean isAdmin;

    public ProductAdapter(List<Product> items, boolean isAdmin, List<String> ids) {
        this.items = items;
        this.isAdmin = isAdmin;
        this.ids = ids;
        this.databaseReference = FirebaseDatabase.getInstance().getReference().child("products"); // Firebase node path
    }
    public ProductAdapter(List<Product> items, boolean isAdmin) {
        this.items = items;
        this.isAdmin = isAdmin;
        this.ids = ids;
        this.databaseReference = FirebaseDatabase.getInstance().getReference().child("products"); // Firebase node path
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderPupListBinding binding = ViewholderPupListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        context = parent.getContext();
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = items.get(position);

        // Bind data to UI components using View Binding
        holder.binding.titleTxt.setText(product.getTitle());
        holder.binding.feeTxt.setText(product.getPrice() + "DT");
        holder.binding.scoreTxt.setText(String.format("%.1f",product.getScore()));
        holder.binding.reviewTxt.setText(String.valueOf(product.getReview()));

        // Load image using Glide with rounded corners
        Glide.with(context)
                .load(product.getPicUrl())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.binding.pic);

        // Handle click on item to open DetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", product); // Pass product object to DetailActivity
            context.startActivity(intent);
        });

        // Display remove button only for admins
        if (isAdmin) {
            holder.binding.removeBtn.setVisibility(View.VISIBLE);
            holder.binding.removeBtn.setOnClickListener(v -> {
                // Remove item from RecyclerView
                items.remove(position);
                notifyItemRemoved(position);

                // Remove item from Firebase Database
                String productId = product.getId(); // Get product id
                databaseReference.child(productId).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Item removed successfully", Toast.LENGTH_SHORT).show();
                            ids.remove(productId); // Remove id from ids list
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to remove item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            // Add removed item back to list if removal from Firebase fails
                            items.add(position, product);
                            notifyItemInserted(position);
                        });
            });
        } else {
            holder.binding.removeBtn.setVisibility(View.GONE);
        }

        // Check if product is in the favorites list and set image accordingly


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewholderPupListBinding binding;

        public ViewHolder(ViewholderPupListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
