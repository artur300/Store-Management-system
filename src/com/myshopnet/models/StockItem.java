package com.myshopnet.models;

public class StockItem {
    private final Product product;
    private int quantity;

    public StockItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void increase(int qty) { quantity += qty; }
    public void decrease(int qty) {
        if (qty > quantity) throw new IllegalArgumentException("Not enough stock");
        quantity -= qty;
    }
}
