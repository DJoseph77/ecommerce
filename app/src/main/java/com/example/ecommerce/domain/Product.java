package com.example.ecommerce.domain;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.ecommerce.Helper.SharedPreferencesManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class Product implements Serializable {
    private String id;
    private String title;
    private String picUrl;
    private int review;
    private double score;
    private double price;
    private String description;
    private Boolean isPopular;
    private ArrayList<String> categories;
    private ArrayList<String> idUserRated;

    public Product(){
        this.idUserRated=new ArrayList<>();
    }
    public Product(String title, String picUrl, int review, double score,
                   double price, String description, Boolean isPopular) {
        this.title = title;
        this.picUrl = picUrl;
        this.review = review;
        this.score = score;
        this.price = price;
        this.description = description;
        this.isPopular = isPopular;
        this.idUserRated=new ArrayList<>();
    }

    public Product(String title, String photoUrl, Double price, String description, ArrayList<String> category, Boolean isPopular) {
        this.title = title;
        this.picUrl = photoUrl;
        this.price = price;
        this.description = description;
        this.categories = category;
        this.isPopular = isPopular;
        this.idUserRated=new ArrayList<>();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Boolean getPopular() {
        return isPopular;
    }

    public void setPopular(Boolean popular) {
        isPopular = popular;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getIdUserRated() {
        return idUserRated;
    }

    public void setIdUserRated(ArrayList<String> idUserRated) {
        this.idUserRated = idUserRated;
    }

}
