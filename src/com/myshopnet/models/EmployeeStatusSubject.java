package com.myshopnet.models;

public interface EmployeeStatusSubject {
    void registerObserver(EmployeeStatusObserver observer);
    void removeObserver(EmployeeStatusObserver observer);
    void notifyObservers(EmployeeStatus oldStatus, EmployeeStatus newStatus);
}
