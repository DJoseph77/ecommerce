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
import com.example.ecommerce.Helper.ChangeNumberItemsListener;
import com.example.ecommerce.Helper.ManagmentCart;
import com.example.ecommerce.databinding.ViewholderCartBinding;
import com.example.ecommerce.databinding.ViewholderPupListBinding;
import com.example.ecommerce.domain.PopularDomain;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    ArrayList<PopularDomain> items;
    Context context;
    ViewholderCartBinding binding;
    ChangeNumberItemsListener changeNumberItemsListener;
    ManagmentCart managmentCart;

    public CartAdapter(ArrayList<PopularDomain> items, ChangeNumberItemsListener changeNumberItemsListener) {
        this.items = items;
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=ViewholderCartBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        context=parent.getContext();
        managmentCart=new ManagmentCart(context);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        binding.titleTxt.setText(items.get(position).getTitle());
        binding.feeEachItem.setText(items.get(position).getPrice()+"DT");
        binding.totalEachItem.setText(Math.round(items.get(position).getNumberInchart()*items.get(position).getPrice())+"DT");
        binding.numberItemTxt.setText(String.valueOf(items.get(position).getNumberInchart()));

        int drawableResourced=holder.itemView.getResources().getIdentifier(items.get(position).getPicUrl()
                ,"drawable",holder.itemView.getContext().getPackageName());
        Glide.with(context)
                .load(drawableResourced)
                .transform(new GranularRoundedCorners(30,30,0,0))
                .into(binding.pic);

        binding.PluscartBtn.setOnClickListener(v -> managmentCart.plusNumberItem(items, position, () -> {
            notifyDataSetChanged();
            changeNumberItemsListener.change();
        }));
        binding.minusCartBtn.setOnClickListener(v -> managmentCart.minusNumberItem(items,position, () -> {
            notifyDataSetChanged();
            changeNumberItemsListener.change();
        }));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(ViewholderCartBinding binding) {
            super(binding.getRoot());
        }
    }
}
