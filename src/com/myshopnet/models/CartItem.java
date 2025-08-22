package com.myshopnet.models;
public class CartItem {
    private final String sku;
    private final int qty;
    public CartItem(String sku, int qty) { this.sku = sku; this.qty = qty; }
    public String getSku() { return sku; }
    public int getQty() { return qty; }
}

