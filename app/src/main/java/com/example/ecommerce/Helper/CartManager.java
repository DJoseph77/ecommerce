package com.example.ecommerce.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.example.ecommerce.domain.ProductCart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CartManager {
    private static final String PREFS_NAME = "shopping_cart";
    private static final String CART_KEY = "cart_items";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public CartManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void addItem(ProductCart product) {
        ArrayList<ProductCart> cartItems = getCartItems();
        boolean itemExists = false;
        for (ProductCart cartItem : cartItems) {
            if (cartItem.getTitle().equals(product.getTitle())) {
                // Item already exists in the cart, update quantity
                cartItem.setNbrInCart(cartItem.getNbrInCart() + product.getNbrInCart());
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            // Item does not exist in the cart, add it
            cartItems.add(product);
        }

        saveCartItems(cartItems);
    }

    public void updateCartItem(ProductCart product) {
        ArrayList<ProductCart> cartItems = getCartItems();
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getTitle().equals(product.getTitle())) {
                cartItems.set(i, product); // Update the existing product in cart
                break;
            }
        }
        saveCartItems(cartItems);
    }

    public void removeItem(ProductCart product) {
        ArrayList<ProductCart> cartItems = getCartItems();
        cartItems.removeIf(item -> item.getTitle().equals(product.getTitle()));
        saveCartItems(cartItems);
    }

    public ArrayList<ProductCart> getCartItems() {
        String json = sharedPreferences.getString(CART_KEY, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<ProductCart>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }

    private void saveCartItems(ArrayList<ProductCart> cartItems) {
        String json = gson.toJson(cartItems);
        sharedPreferences.edit().putString(CART_KEY, json).apply();
    }
    public void clearCart() {
        sharedPreferences.edit().remove(CART_KEY).apply();
    }

}
