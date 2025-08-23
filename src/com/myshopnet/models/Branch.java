package com.myshopnet.models;

import java.util.HashSet;
import java.util.Set;

public class Branch {
    private final String id;
    private final String name;
    private final Stock productsStock;

    public Branch(String id, String name) {
        this.id = id;
        this.name = name;
        productsStock = new Stock();
    }

    public String getId() {
        return id;
    }

    public Stock getProductsStock() {
        return productsStock;
    }

    public String getName() {
        return name;
    }
}
