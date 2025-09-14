package com.myshopnet.models;

public abstract class Customer implements User {
    protected final Role role = Role.CUSTOMER;
    private final String userId;
    private String passportId;
    private String fullName;
    private String phone;
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

    public abstract double calcBuyingStrategy(double baseTotal);

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

