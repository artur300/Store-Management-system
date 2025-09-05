package com.myshopnet.models;

public class Employee implements User {
    private final Role role = Role.EMPLOYEE;
    private String userId;
    private Long accountNumber;
    private String branchId;
    private EmployeeType employeeType;
    private Long employeeNumber;
    private EmployeeStatus employeeStatus;

    public Employee(String id, Long accountNumber, String branchId, EmployeeType employeeType, Long employeeNumber) {
        this.userId = id;
        this.accountNumber = accountNumber;
        this.branchId = branchId;
        this.employeeType = employeeType;
        this.employeeNumber = employeeNumber;
        this.employeeStatus = EmployeeStatus.AVAILABLE;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public Role getRole() {
        return role;
    }

    public EmployeeStatus getEmployeeStatus() {
        return employeeStatus;
    }

    public void setEmployeeStatus(EmployeeStatus employeeStatus) {
        this.employeeStatus = employeeStatus;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(EmployeeType employeeType) {
        this.employeeType = employeeType;
    }

    public Long getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(Long employeeNumber) {
        this.employeeNumber = employeeNumber;
    }
}

