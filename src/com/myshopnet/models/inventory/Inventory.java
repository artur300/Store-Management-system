package com.myshopnet.models.inventory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Inventory {
    private final Map<String, StockItem> bySku = new ConcurrentHashMap<>();

    public synchronized void addProduct(Product p, int qty) {
        bySku.compute(p.getSku(), (sku, oldItem) -> {
            if (oldItem == null) return new StockItem(p, qty);
            oldItem.increase(qty);
            return oldItem;
        });
    }

    public synchronized void restock(String sku, int qty) {
        getRequired(sku).increase(qty);
    }

    public synchronized void sell(String sku, int qty) {
        getRequired(sku).decrease(qty);
    }

    public synchronized int getQty(String sku) {
        return getRequired(sku).getQuantity();
    }

    public synchronized Product getProduct(String sku) {
        return getRequired(sku).getProduct();
    }

    private StockItem getRequired(String sku) {
        StockItem it = bySku.get(sku);
        if (it == null) throw new IllegalArgumentException("SKU not found: " + sku);
        return it;
    }
}

