package com.myshopnet.client.models;

import com.myshopnet.models.Category;

public class Product {
    private final String sku;
    private final String name;
    private final double price;
    private final String branchId;
    private int quantity;

    public Product(String sku, String name, double basePrice, String branchId, int quantity) {
        this.sku = sku;
        this.price = basePrice;
        this.quantity = quantity;
        this.name = name;
        this.branchId = branchId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }
    public int getQuantity() {
        return quantity;
    }
    public String getSku() { return sku; }
    public double getPrice() { return price; }
}

