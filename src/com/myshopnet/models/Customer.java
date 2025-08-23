package com.myshopnet.models;

public abstract class Customer implements User {
    private final Role role = Role.CUSTOMER;
    private final String id;       // ת"ז/מזהה
    private final String fullName;
    private final String phone;

    protected Customer(String id, String fullName, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
    }

    public String getId() { return id; }

    /** מחיר סופי לפי סוג הלקוח */
    public abstract double calcBuyingStrategy(double baseTotal);
}

