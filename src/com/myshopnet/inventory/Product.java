package com.myshopnet.inventory;

public class Product {
    private final String sku;
    private final String name;
    private final Category category;
    private final double basePrice;

    public Product(String sku, String name, Category category, double basePrice) {
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.basePrice = basePrice;
    }

    public String getSku() { return sku; }
    public String getName() { return name; }
    public Category getCategory() { return category; }
    public double getBasePrice() { return basePrice; }
}
