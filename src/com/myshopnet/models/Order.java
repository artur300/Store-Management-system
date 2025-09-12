package com.myshopnet.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Order {
    private final String id;
    private final String customerId;
    private final double baseTotal;
    private final double finalTotal;
    private final LocalDateTime timestamp;
    private final List<Map<String,String>> productsQuantityBranches;

    public Order(String id, String customerId, double baseTotal, double finalTotal,
                 List<Map<String,String>> productsQuantityBranches) {
        this.id = id;
        this.customerId = customerId;
        this.baseTotal = baseTotal;
        this.finalTotal = finalTotal;
        this.productsQuantityBranches = productsQuantityBranches;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public double getBaseTotal() {
        return baseTotal;
    }

    public double getFinalTotal() {
        return finalTotal;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<Map<String,String>> getProductsQuantityBranches() {
        return productsQuantityBranches;
    }
}

