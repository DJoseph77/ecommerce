package com.example.ecommerce.domain;

import java.util.ArrayList;

public class User {
    private String email;
    private String password;
    private String name;
    private String LastName;
    private String phoneNumber;
    private String country;
    private String adress;
    private ArrayList<String> favorisProducts=null;
    private Boolean isAdmin;
    public User(String country, String password, String name, String phoneNumber,String adress,String email,ArrayList<String> favorisProducts) {
        this.adress=adress;
        this.country = country;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email=email;
        this.favorisProducts=favorisProducts;
    }
    public User(String country, String password, String name,String lastName, String phoneNumber,String adress,String email,ArrayList<String> favorisProducts,Boolean isAdmin) {
        this.adress=adress;
        this.country = country;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email=email;
        this.favorisProducts=favorisProducts;
        this.isAdmin=isAdmin;
        this.LastName=lastName;
    }

    public User() {

    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getEmail() {
        return email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ArrayList<String> getFavorisProducts() {
        return favorisProducts;
    }

    public void setFavorisProducts(ArrayList<String> favorisProducts) {
        this.favorisProducts = favorisProducts;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }
}
