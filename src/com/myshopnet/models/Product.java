package com.myshopnet.models;

public class Product {
    private final String sku;
    private final String name;
    private final Category category;
    private final double price;

    public Product(String sku, String name, Category category, double basePrice) {
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.price = basePrice;
    }

    public String getSku() { return sku; }
    public String getName() { return name; }
    public Category getCategory() { return category; }
    public double getPrice() { return price; }
}
