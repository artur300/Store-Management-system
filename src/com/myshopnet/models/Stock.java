package com.myshopnet.models;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Stock {
    private final Map<Product, Long> stockOfProducts;

    public Stock() {
        stockOfProducts = new ConcurrentHashMap<>();
    }

    public Map<Product, Long> getStockOfProducts() {
        return stockOfProducts;
    }
}
