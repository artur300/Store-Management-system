package com.myshopnet.models.customers;

public abstract class Customer {
    private final String id;       // ת"ז/מזהה
    private final String fullName;
    private final String phone;

    protected Customer(String id, String fullName, String phone) {
        this.id = id; this.fullName = fullName; this.phone = phone;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }

    /** מחיר סופי לפי סוג הלקוח */
    public abstract double calcPrice(double baseTotal);
}

