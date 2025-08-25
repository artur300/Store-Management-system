package com.myshopnet.models;

public class VipCustomer extends Customer {
    public VipCustomer(String id, String fullName, String phone) {
        super(id, fullName, phone);
    }

    public VipCustomer(Customer customer) {
        super(customer);
    }

    @Override
    public double calcBuyingStrategy(double baseTotal) {
        return baseTotal * 0.80;
    }
}

