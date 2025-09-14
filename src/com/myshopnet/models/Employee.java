package com.myshopnet.models;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Employee implements User, EmployeeStatusSubject {
    protected Role role;
    private String fullName;
    private String userId;
    private String phoneNumber;
    private Long accountNumber;
    private String branchId;
    private EmployeeType employeeType;
    private Long employeeNumber;
    private EmployeeStatus employeeStatus;

    private final List<EmployeeStatusObserver> observers = new CopyOnWriteArrayList<>();

    public Employee(String id, String fullName, String phoneNumber,
                    Long accountNumber, String branchId, EmployeeType employeeType, Long employeeNumber) {
        this.userId = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.accountNumber = accountNumber;
        this.branchId = branchId;
        this.employeeType = employeeType;
        this.employeeNumber = employeeNumber;
        this.employeeStatus = EmployeeStatus.BUSY;
        this.role = Role.EMPLOYEE;
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
        EmployeeStatus old = this.employeeStatus;
        this.employeeStatus = employeeStatus;
        notifyObservers(old, employeeStatus);
    }

    @Override
    public void registerObserver(EmployeeStatusObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(EmployeeStatusObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(EmployeeStatus oldStatus, EmployeeStatus newStatus) {
        for (EmployeeStatusObserver observer : observers) {
            observer.onStatusChanged(this, oldStatus, newStatus);
        }
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

    public String getFullName(){return fullName;}

    public void setFullName(String fullName){this.fullName = fullName;}

    public String getPhoneNumber(){ return phoneNumber;}

    public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}
}