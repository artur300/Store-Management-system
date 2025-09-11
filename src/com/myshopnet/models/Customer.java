package com.myshopnet.models;

public abstract class Customer implements User {
    protected final Role role = Role.CUSTOMER;
    private final String userId;
    private final String passportId;
    private final String fullName;
    private final String phone;
    protected CustomerType customerType;

    protected Customer(String id, String passpoortId, String fullName, String phone, CustomerType customerType) {
        this.userId = id;
        this.passportId = passpoortId;
        this.fullName = fullName;
        this.phone = phone;
        this.customerType = customerType;
    }

    public Customer(Customer customer) {
        this(customer.userId, customer.passportId, customer.fullName, customer.phone, customer.customerType);
    }

    public String getUserId() {
        return userId;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public String getPassportId() {
        return passportId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    /** מחיר סופי לפי סוג הלקוח */
    public abstract double calcBuyingStrategy(double baseTotal);
}

