package com.example.ecommerce.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.ecommerce.Helper.CartManager;
import com.example.ecommerce.databinding.ViewholderCartBinding;
import com.example.ecommerce.domain.ProductCart;

import java.util.ArrayList;

public class ProductCartAdapter extends RecyclerView.Adapter<ProductCartAdapter.ViewHolder> {
    private ArrayList<ProductCart> productsForBuy;
    private Context context;


    public ProductCartAdapter(ArrayList<ProductCart> productsForBuy, Context context) {
        this.productsForBuy = productsForBuy;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderCartBinding binding = ViewholderCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductCart product = productsForBuy.get(position);

        holder.binding.titleTxt.setText(product.getTitle());
        holder.binding.feeEachItem.setText(product.getPrice() + "DT");
        holder.binding.numberItemTxt.setText(String.valueOf(product.getNbrInCart()));
        holder.binding.totalEachItem.setText(String.valueOf(product.getNbrInCart() * product.getPrice()));

        String drawableResource = product.getPicUrl();

        Glide.with(context)
                .load(drawableResource)
                .transform(new GranularRoundedCorners(30, 30, 0, 0))
                .into(holder.binding.pic);

        // Decrease button click listener
        holder.binding.minusCartBtn.setOnClickListener(v -> {
            int newQuantity = product.getNbrInCart() - 1;
            if (newQuantity > 0) {
                product.setNbrInCart(newQuantity);
                notifyItemChanged(position);
                notifyDataSetChanged();
                CartManager cartManager = new CartManager(context);
                cartManager.updateCartItem(product); // Update cart in SharedPreferences
            } else {
                // Remove the item from the cart if quantity becomes zero
                productsForBuy.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, productsForBuy.size());
                CartManager cartManager = new CartManager(context);
                cartManager.removeItem(product);
                notifyDataSetChanged();
            }
        });

        // Increase button click listener
        holder.binding.PluscartBtn.setOnClickListener(v -> {
            product.setNbrInCart(product.getNbrInCart() + 1);
            notifyItemChanged(position);
            CartManager cartManager = new CartManager(context);
            notifyDataSetChanged();
            cartManager.updateCartItem(product); // Update cart in SharedPreferences
        });
    }

    @Override
    public int getItemCount() {
        return productsForBuy.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderCartBinding binding;

        public ViewHolder(ViewholderCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
