package com.myshopnet.models;
public class CartItem {
    private final String sku;
    private final Long quantity;
    public CartItem(String sku, Long quantity) {
        this.sku = sku; this.quantity = quantity;
    }
    public String getSku() {
        return sku;
    }
    public Long getQuantity() {
        return quantity;
    }
}

