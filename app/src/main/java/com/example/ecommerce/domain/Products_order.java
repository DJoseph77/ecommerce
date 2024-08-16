package com.example.ecommerce.domain;

public class Products_order {
    private String name;
    private String number;

    public Products_order(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public Products_order() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
