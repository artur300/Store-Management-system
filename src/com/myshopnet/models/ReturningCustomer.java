package com.myshopnet.models;

public class ReturningCustomer extends Customer {
    public ReturningCustomer(String id,String passportId, String fullName, String phone) {
        super(id, passportId ,fullName, phone, CustomerType.RETURNING_CUSTOMER);
    }

    public ReturningCustomer(Customer customer) {
        super(customer);
    }

    @Override
    public double calcBuyingStrategy(double baseTotal) {
        return Math.max(0, baseTotal - 10.0);
    }

    @Override
    public Role getRole() {
        return role;
    }
}

