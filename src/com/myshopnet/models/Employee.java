package com.myshopnet.models;

public class Employee {
    private final String id;
    private final String fullName;
    private final String nationalId;
    private final String phone;
    private final String accountNumber;
    private final Role role;
    private final String branchId;

    public Employee(String id, String fullName, String nationalId, String phone,
                    String accountNumber, Role role, String branchId) {
        this.id = id; this.fullName = fullName; this.nationalId = nationalId;
        this.phone = phone; this.accountNumber = accountNumber; this.role = role; this.branchId = branchId;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getNationalId() { return nationalId; }
    public String getPhone() { return phone; }
    public String getAccountNumber() { return accountNumber; }
    public Role getRole() { return role; }
    public String getBranchId() { return branchId; }
}

