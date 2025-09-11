package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.data.Data;
import com.myshopnet.models.EmployeeType;
import com.myshopnet.service.EmployeeService;
import com.myshopnet.service.AuthService;
import com.myshopnet.service.UserAccountService;
import com.myshopnet.models.Employee;
import com.myshopnet.service.BranchService;
import com.myshopnet.models.Admin;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.server.Response;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.utils.GsonSingleton;
import com.myshopnet.utils.Singletons;

import java.util.List;

public class EmployeeController {
    private EmployeeService employeeService = com.myshopnet.utils.Singletons.EMPLOYEE_SERVICE;
    private Gson gson = GsonSingleton.getInstance();
    private BranchService branchService = com.myshopnet.utils.Singletons.BRANCH_SERVICE;
    private UserAccountService userAccountService = com.myshopnet.utils.Singletons.USER_ACCOUNT_SERVICE;

    public String addEmployee(String currentUserID, Long accountNumber, String branchId,
                               String employeeType,Long employeeNumber,String username,String password)
    {
        Response response = new Response();

        try {
            UserAccount currentUserAccount = userAccountService.getUserAccount(currentUserID);

            if (Data.getOnlineAccounts().get(currentUserAccount.getUsername()) == null) {
                throw new SecurityException("Not logged in");
            }

            if (!(currentUserAccount.getUser() instanceof Admin)) {
                throw new SecurityException("No permission to access this operation");
            }

            Employee employee = employeeService.addEmployee(username, password,
                    accountNumber, branchId, EmployeeType.valueOf(employeeType), employeeNumber);
            UserAccount userAccount = userAccountService.getUserAccount(employee.getUserId());

            response.setSuccess(true);
            response.setMessage(gson.toJson(userAccount));
        }
        catch(Exception e) {
            response.setSuccess(false);
            response.setMessage("Error adding employee");
        }
        return gson.toJson(response);
    }


    public String getEmployee(String employeeId) {
        Response response = new Response();

        try{
            Employee employee = employeeService.get(employeeId);

            if (employee == null)
            {
                throw new SecurityException("Employee not found");
            }

            response.setSuccess(true);
            response.setMessage(gson.toJson(employee));
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return gson.toJson(response);
    }


    public String getAllEmployeesByBranch(String branchId) {
        Response response = new Response();

        try {
            List<Employee> employees = branchService.getAllEmployeesInBranch(branchId);

            response.setSuccess(true);
            response.setMessage(gson.toJson(employees));
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to set employees");
        }
        return gson.toJson(response);
    }
}
