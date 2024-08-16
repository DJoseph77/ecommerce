package com.example.ecommerce.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String Email="email";
    private static final String isadmin="isadmin";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setLogin(boolean isLoggedIn, String userId,String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(Email,email);
        editor.apply();
    }
    public void setAdmin(Boolean isAdmin){
        editor.putBoolean(isadmin,isAdmin);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }
    public String getEmail() {
        return sharedPreferences.getString(Email,null);
    }
    public Boolean getIsAdmin() {
        return sharedPreferences.getBoolean(isadmin, false);
    }

    public void clear() {
        editor.clear();
        editor.apply();
    }


}
