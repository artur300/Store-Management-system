package com.myshopnet.models.customers;

public class VipCustomer extends Customer {
    public VipCustomer(String id, String fullName, String phone) { super(id, fullName, phone); }
    @Override public double calcPrice(double baseTotal) { return baseTotal * 0.88; } // 12% הנחה
}

