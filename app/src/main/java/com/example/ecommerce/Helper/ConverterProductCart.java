package com.example.ecommerce.Helper;

import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.ProductCart;

public class ConverterProductCart {

    // Method to convert Product to ProductCart
    public static ProductCart convertProductToCart(Product product) {
        // Assuming you want to create a ProductCart with 0 quantity in cart initially
        return new ProductCart(product.getTitle(), product.getPicUrl(), product.getReview(),
                product.getScore(), product.getPrice(), product.getDescription(), product.getPopular(), 1);
    }

    // Method to convert ProductCart to Product
    public static Product convertCartToProduct(ProductCart productCart) {
        // Since ProductCart inherits from Product, it can directly return ProductCart as Product
        return productCart;
    }
}
