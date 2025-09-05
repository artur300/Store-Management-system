package com.myshopnet.models;

public abstract class Customer implements User {
    protected final Role role = Role.CUSTOMER;
    private final String userId;
    private final String passportId;
    private final String fullName;
    private final String phone;

    protected Customer(String id, String passpoortId, String fullName, String phone) {
        this.userId = id;
        this.passportId = passpoortId;
        this.fullName = fullName;
        this.phone = phone;
    }

    public Customer(Customer customer) {
        this(customer.userId, customer.passportId, customer.fullName, customer.phone);
    }

    public String getUserId() {
        return userId;
    }

    /** מחיר סופי לפי סוג הלקוח */
    public abstract double calcBuyingStrategy(double baseTotal);
}

