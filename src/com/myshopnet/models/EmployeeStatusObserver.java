package com.myshopnet.models;

public interface EmployeeStatusObserver {
    void onStatusChanged(Employee employee, EmployeeStatus oldStatus, EmployeeStatus newStatus);
}
