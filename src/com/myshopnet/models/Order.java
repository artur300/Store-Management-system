package com.myshopnet.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Order {
    private final String id;
    private final String branchId;
    private final String customerId;
    private final double baseTotal;
    private final double finalTotal;
    private final LocalDateTime timestamp;
    private final Map<String, Long> productsOrdered;

    public Order(String id, String branchId, String customerId, double baseTotal, double finalTotal, Map<String, Long> productsOrdered) {
        this.id = id;
        this.branchId = branchId;
        this.customerId = customerId;
        this.baseTotal = baseTotal;
        this.finalTotal = finalTotal;
        this.productsOrdered = productsOrdered;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getBranchId() {
        return branchId;
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

    public Map<String, Long> getProductsOrdered() {
        return productsOrdered;
    }
}

