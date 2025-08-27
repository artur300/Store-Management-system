package com.myshopnet.models;

public class Employee implements User {
    private final Role role = Role.SELLER;
    private String id;
    private Long accountNumber;
    private String branchId;
    private EmployeeType employeeType;
    private Long employeeNumber;
    private EmployeeStatus employeeStatus;

    public Employee(String id, Long accountNumber, String branchId, EmployeeType employeeType, Long employeeNumber) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.branchId = branchId;
        this.employeeType = employeeType;
        this.employeeNumber = employeeNumber;
        this.employeeStatus = EmployeeStatus.AVAILABLE;
    }

    @Override
    public String getId() {
        return id;
    }

    public EmployeeStatus getEmployeeStatus() {
        return employeeStatus;
    }

    public void setEmployeeStatus(EmployeeStatus employeeStatus) {
        this.employeeStatus = employeeStatus;
    }

    public void setId(String id) {
        this.id = id;
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

