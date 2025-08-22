package com.myshopnet.models;

public class Employee implements User {
    private final String id;
    private final String branchId;

    public Employee(String id, String fullName, String nationalId, String phone,
                    String accountNumber, String branchId) {
        this.id = id;
        this.branchId = branchId;
    }

    public String getId() { return id; }
    public String getBranchId() { return branchId; }
}

