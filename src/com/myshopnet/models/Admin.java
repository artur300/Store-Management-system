package com.myshopnet.models;

public class Admin extends Employee {
    private final Role role = Role.ADMIN;

    public Admin(String id, Long accountNumber, String branchId, EmployeeType employeeType, Long employeeNumber) {
        super(id, accountNumber, branchId, employeeType, employeeNumber);
    }
}
