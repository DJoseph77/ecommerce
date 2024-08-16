package com.example.ecommerce.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerce.Activity.CartsInProgress;
import com.example.ecommerce.Activity.CategoriesAndManage;
import com.example.ecommerce.Activity.Login;
import com.example.ecommerce.Activity.ProductsManager;
import com.example.ecommerce.Activity.RouletteSettings;
import com.example.ecommerce.Helper.SharedPreferencesManager;
import com.example.ecommerce.databinding.ViewholderSettingAdminBinding;
import com.example.ecommerce.domain.Setting;

import java.util.ArrayList;

public class settingAdminAdapter extends RecyclerView.Adapter<settingAdminAdapter.ViewHolder> {
    ArrayList<Setting> settings;
    Context context;
    private SharedPreferencesManager sharedPreferencesManager;

    public settingAdminAdapter(ArrayList<Setting> settings,Context context) {
        this.settings = settings;
        this.context=context;
    }

    @NonNull
    @Override
    public settingAdminAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderSettingAdminBinding binding=ViewholderSettingAdminBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull settingAdminAdapter.ViewHolder holder, int position) {
        Setting setting=settings.get(position);
        holder.binding.textView162.setText(setting.getTitle());
        Glide.with(context)
                .load(setting.getImgUrl())
                .into(holder.binding.imageView162);

        holder.binding.relSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence text = holder.binding.textView162.getText();
                Intent intent77=new Intent(context, CartsInProgress.class);
                if (text.equals("products")) {
                    Intent intentProducts = new Intent(context, ProductsManager.class);
                    context.startActivity(intentProducts);
                } else if (text.equals("categories & management")) {
                    Intent intentCategoryManage = new Intent(context, CategoriesAndManage.class);
                    context.startActivity(intentCategoryManage);
                } else if (text.equals("order In progress")  ) {
                    intent77.putExtra("status","in progress");
                    context.startActivity(intent77);
                }else if (text.equals("order Confirmed")) {
                    intent77.putExtra("status","confirmed");
                    context.startActivity(intent77);
                }else if (text.equals("order History")) {
                    intent77.putExtra("status","delivred");
                    context.startActivity(intent77);
                }else if (text.equals("log Out")) {
                    sharedPreferencesManager=new SharedPreferencesManager(context);
                    sharedPreferencesManager.clear();
                    Intent intent17 = new Intent(context, Login.class);
                    context.startActivity(intent17);
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                }else if (text.equals("Roulette")){
                    Intent intent57=new Intent(context, RouletteSettings.class);
                    context.startActivity(intent57);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return settings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ViewholderSettingAdminBinding binding;
        public ViewHolder(ViewholderSettingAdminBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
