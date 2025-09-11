package com.myshopnet.models;

public class NewCustomer extends Customer {
    public NewCustomer(String id,String passportId, String fullName, String phone) {
        super(id,passportId, fullName, phone, CustomerType.NEW_CUSTOMER);
    }

    @Override
    public double calcBuyingStrategy(double baseTotal) {
        return baseTotal * 0.95;
    }

    @Override
    public Role getRole() {
        return role;
    }
}
