package com.myshopnet.models;

public abstract class Customer implements User {
    private final Role role = Role.CUSTOMER;
    private final String id;
    private final String passportId;
    private final String fullName;
    private final String phone;

    protected Customer(String id, String passpoortId, String fullName, String phone) {
        this.id = id;
        this.passportId = passpoortId;
        this.fullName = fullName;
        this.phone = phone;
    }

    public Customer(Customer customer) {
        this(customer.id, customer.passportId, customer.fullName, customer.phone);
    }

    public String getId() {
        return id;
    }

    /** מחיר סופי לפי סוג הלקוח */
    public abstract double calcBuyingStrategy(double baseTotal);
}

