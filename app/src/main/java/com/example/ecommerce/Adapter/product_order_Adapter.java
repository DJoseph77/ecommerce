package com.example.ecommerce.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.databinding.ViewholderProductOrderBinding;
import com.example.ecommerce.domain.Products_order;

import java.util.ArrayList;

public class product_order_Adapter extends RecyclerView.Adapter<product_order_Adapter.ViewHolder> {
    private ArrayList<Products_order> productsOrders;
    private Context context;

    public product_order_Adapter(ArrayList<Products_order> productsOrders) {
        this.productsOrders = productsOrders;
    }

    @NonNull
    @Override
    public product_order_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderProductOrderBinding binding = ViewholderProductOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        context = parent.getContext();
        return new product_order_Adapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull product_order_Adapter.ViewHolder holder, int position) {
        Products_order item = productsOrders.get(position);
        holder.binding.productName.setText(item.getName());
        holder.binding.NombreOfProduct.setText(item.getNumber());
    }

    @Override
    public int getItemCount() {
        return productsOrders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderProductOrderBinding binding;

        public ViewHolder(ViewholderProductOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
