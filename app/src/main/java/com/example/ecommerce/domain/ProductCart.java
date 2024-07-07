package com.example.ecommerce.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductCart extends Product implements Serializable {

    private int nbrInCart;
    public ProductCart(){
        super();
    }
    public ProductCart(String title, String picUrl, int review, double score,
                       double price, String description, Boolean isPopular, int nbrInCart) {
        super(title, picUrl, review, score, price, description, isPopular);
        this.nbrInCart = nbrInCart;
    }

    public ProductCart(String title, String photoUrl, Double price, String description, ArrayList<String> category, Boolean isPopular) {
        super(title, photoUrl, price, description, category, isPopular);
        this.nbrInCart = 0; // Default to 0 when creating without cart quantity
    }

    public int getNbrInCart() {
        return nbrInCart;
    }

    public void setNbrInCart(int nbrInCart) {
        this.nbrInCart = nbrInCart;
    }
}
