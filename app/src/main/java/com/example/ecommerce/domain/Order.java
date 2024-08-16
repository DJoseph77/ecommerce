package com.example.ecommerce.domain;

import java.util.ArrayList;

public class Order {
    private String DateOrder;
    private ArrayList<Product> nameProducts;
    private String status;
    private String totalPrice;
    private String userAddress;
    private String userId;
    private String userName;
    private String userPhoneNumber;

    public Order() {
        // Default constructor required for calls to DataSnapshot.getValue(Order.class)
    }

    public Order(String DateOrder, ArrayList<Product> nameProducts, String status, String totalPrice, String userAddress, String userId, String userName, String userPhoneNumber) {
        this.DateOrder = DateOrder;
        this.nameProducts = nameProducts;
        this.status = status;
        this.totalPrice = totalPrice;
        this.userAddress = userAddress;
        this.userId = userId;
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getDateOrder() {
        return DateOrder;
    }

    public void setDateOrder(String dateOrder) {
        DateOrder = dateOrder;
    }

    public ArrayList<Product> getNameProducts() {
        return nameProducts;
    }

    public void setNameProducts(ArrayList<Product> nameProducts) {
        this.nameProducts = nameProducts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }
}
