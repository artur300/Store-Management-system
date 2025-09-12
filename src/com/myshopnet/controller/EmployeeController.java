package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.data.Data;
import com.myshopnet.logs.LogEvent;
import com.myshopnet.logs.LogType;
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

    public String addEmployee(String currentUserID, String fullName, String phoneNumber, Long accountNumber, String branchId,
                              String employeeType, Long employeeNumber, String username, String password)
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

            Employee employee = employeeService.addEmployee(username, password, fullName, phoneNumber,
                    accountNumber, branchId, EmployeeType.valueOf(employeeType), employeeNumber);
            UserAccount userAccount = userAccountService.getUserAccount(employee.getUserId());

            Singletons.LOGGER.log(new LogEvent(LogType.EMPLOYEE_REGISTERED,"Employee Created"));
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

    public String getAllEmployees(String currentUserID) {
        Response response = new Response();

        try {
            UserAccount currentUserAccount = userAccountService.getUserAccount(currentUserID);

            if ( currentUserAccount == null || !(currentUserAccount.getUser() instanceof Admin)){
                throw new SecurityException("User is not Admin");
            }
            response.setSuccess(true);
            response.setMessage(gson.toJson(employeeService.getAll()));
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return gson.toJson(response);
    }

    public String updateEmployee(String currentUserID, Employee updated) {
        Response response = new Response();

        try {
            UserAccount currentUserAccount = userAccountService.getUserAccount(currentUserID);

            if ( currentUserAccount == null || !(currentUserAccount.getUser() instanceof Admin)){
                throw new SecurityException("User is not Admin");
            }

            Employee saved = employeeService.update(updated.getUserId(), updated);
            response.setSuccess(true);
            response.setMessage(gson.toJson(saved));
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return gson.toJson(response);
    }

    public String deleteEmployee(String currentUserID, String employeeId) {
        Response response = new Response();

        try {
            UserAccount currentUserAccount = userAccountService.getUserAccount(currentUserID);
            if ( currentUserAccount == null || !(currentUserAccount.getUser() instanceof Admin)){
                throw new SecurityException("User is not Admin");
            }
            employeeService.delete(employeeId);
            response.setSuccess(true);
            response.setMessage("Employee deleted");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return gson.toJson(response);
    }
}
