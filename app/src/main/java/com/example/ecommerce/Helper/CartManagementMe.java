package com.example.ecommerce.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ecommerce.domain.PopularDomain;

public class CartManagementMe {
    private Context context;
    private static final String PREF_NAME = "Cart_Pref";
    private PopularDomain object;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public CartManagementMe(PopularDomain object,Context context) {
        this.object = object;
        sharedPreferences=context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }
    public void insertItem(){
        editor.putString("title",object.getTitle());
        editor.putString("pucUrl",object.getPicUrl());
        editor.putInt("review",object.getReview());
        editor.putInt("numberIncart",object.getNumberInchart());
    }
}
