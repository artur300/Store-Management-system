package com.myshopnet.models;

public class Admin extends Employee {
    public Admin(String id, Long accountNumber, String branchId, EmployeeType employeeType, Long employeeNumber) {
        super(id, accountNumber, branchId, employeeType, employeeNumber);

        role = Role.ADMIN;
    }
}
