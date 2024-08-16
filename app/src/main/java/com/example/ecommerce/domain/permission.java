package com.example.ecommerce.domain;

public class permission {
    private Boolean products,orders,categoriesManagement;

    public permission() {
    }

    public Boolean getProducts() {
        return products;
    }

    public void setProducts(Boolean products) {
        this.products = products;
    }

    public Boolean getOrders() {
        return orders;
    }

    public void setOrders(Boolean orders) {
        this.orders = orders;
    }

    public Boolean getCategoriesManagement() {
        return categoriesManagement;
    }

    public void setCategoriesManagement(Boolean categoriesManagement) {
        this.categoriesManagement = categoriesManagement;
    }
}
