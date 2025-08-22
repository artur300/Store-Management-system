package com.myshopnet.models;

public class NewCustomer extends Customer {
    public NewCustomer(String id, String fullName, String phone) { super(id, fullName, phone); }
    @Override public double calcPrice(double baseTotal) { return baseTotal * 0.95; } // דוגמה: 5% הנחה
}
