package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.service.EmployeeService;
import com.myshopnet.utils.GsonSingleton;

public class EmployeeController {
    private Gson gson = GsonSingleton.getInstance();
    private EmployeeService employeeService = new EmployeeService();

    public String createEmployee(String userId, String branchId, String employeeFullName, String employeePhone,
                                 Long employeeAccountNumber, String employeeNumber, String employeeType) {

    }

    public String editEmployee(String userId, String branchId, String employeeFullName, String employeePhone,
                                 Long employeeAccountNumber, String employeeNumber, String employeeType) {

    }

    public String deleteEmployee(String userId, String employeeId) {

    }

    public String requestToChatWithBranch(String userId, String branchId) {

    }

    public String exitChat(String userId, String branchId) {

    }
}
