package com.myshopnet.sales;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart {
    private final List<CartItem> items = new ArrayList<>();
    public void add(String sku, int qty) { items.add(new CartItem(sku, qty)); }
    public List<CartItem> items() { return Collections.unmodifiableList(items); }
}

