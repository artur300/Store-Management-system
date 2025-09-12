package com.myshopnet.models;

public class Admin extends Employee {
    public Admin(String id, String fullName, String phoneNumber, Long accountNumber, String branchId, EmployeeType employeeType, Long employeeNumber) {
        super(id, fullName, phoneNumber, accountNumber, branchId, employeeType, employeeNumber);

        role = Role.ADMIN;
    }
}