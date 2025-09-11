package com.myshopnet.models;

public class VipCustomer extends Customer {
    public VipCustomer(String id, String passpoortId, String fullName, String phone) {
        super(id, passpoortId,fullName, phone, CustomerType.VIP_CUSTOMER);
    }

    public VipCustomer(Customer customer) {
        super(customer);
    }

    @Override
    public double calcBuyingStrategy(double baseTotal) {
        return baseTotal * 0.80;
    }

    @Override
    public Role getRole() {
        return role;
    }
}

