package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.service.EmployeeService;
import com.myshopnet.utils.GsonSingleton;

public class EmployeeController {
    private Gson gson = GsonSingleton.getInstance();
    private EmployeeService employeeService = new EmployeeService();

    public String createEmployee(String userId, String branchId, String employeeFullName, String employeePhone,
                                 Long employeeAccountNumber, String employeeNumber, String employeeType) {
        return "Create Employee";
    }

    public String editEmployee(String userId, String branchId, String employeeFullName, String employeePhone,
                                 Long employeeAccountNumber, String employeeNumber, String employeeType) {
        return "Edit Employee";
    }

    public String deleteEmployee(String userId, String employeeId) {
        return "Delete Employee";
    }

    public String requestToChatWithBranch(String userId, String branchId) {
        return "Request To Chat With Branch";
    }

    public String exitChat(String userId, String branchId) {
        return "Exit Chat";
    }
}
