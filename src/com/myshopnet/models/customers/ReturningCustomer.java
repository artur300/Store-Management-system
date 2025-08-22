package com.myshopnet.models.customers;

public class ReturningCustomer extends Customer {
    public ReturningCustomer(String id, String fullName, String phone) { super(id, fullName, phone); }
    @Override
    public double calcPrice(double baseTotal) { return Math.max(0, baseTotal - 10.0); } // קופון 10₪
}

