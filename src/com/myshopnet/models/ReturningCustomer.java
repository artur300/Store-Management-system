package com.myshopnet.models;

public class ReturningCustomer extends Customer {
    public ReturningCustomer(String id, String fullName, String phone) {
        super(id, fullName, phone);
    }

    public ReturningCustomer(Customer customer) {
        super(customer);
    }

    @Override
    public double calcBuyingStrategy(double baseTotal) {
        return Math.max(0, baseTotal - 10.0);
    } // קופון 10₪
}

