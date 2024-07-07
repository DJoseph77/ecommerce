package com.example.ecommerce.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class Product implements Serializable {
    private String title;
    private String picUrl;
    private int review;
    private double score;
    private double price;
    private String description;
    private Boolean isPopular;
    private ArrayList<String> categories;
    public Product(){
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
    }

    public Product(String title, String photoUrl, Double price, String description, ArrayList<String> category, Boolean isPopular) {
        this.title = title;
        this.picUrl = photoUrl;
        this.price = price;
        this.description = description;
        this.categories = category;
        this.isPopular = isPopular;
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
}
