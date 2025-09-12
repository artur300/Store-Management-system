package com.myshopnet.models;

/**
 * Observer for receiving notifications when an Employee's status changes.
 */
public interface EmployeeStatusObserver {
    /**
     * Called when an employee's status changes.
     * @param employee the employee whose status changed
     * @param oldStatus the previous status
     * @param newStatus the new status
     */
    void onStatusChanged(Employee employee, EmployeeStatus oldStatus, EmployeeStatus newStatus);
}
