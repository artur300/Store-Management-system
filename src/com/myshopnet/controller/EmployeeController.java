package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.service.EmployeeService;
import com.myshopnet.service.AuthService;
import com.myshopnet.service.UserAccountService;
import com.myshopnet.models.Employee;
import com.myshopnet.service.BranchService;
import com.myshopnet.models.EmployeeStatus;
import com.myshopnet.models.Admin;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.server.Response;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.utils.GsonSingleton;
import java.util.List;

public class EmployeeController {

    private EmployeeService employeeService = new EmployeeService();
    private Gson gson = GsonSingleton.getInstance();
    private BranchService branchService = new BranchService();
    private AuthService authService = new AuthService();
    private UserAccountService userAccountService = new UserAccountService();


    public String addEEmployee(String currentUserID, Long accountNumber, String branchId,
                               String employeeType,Long employeeNumber,String username,String password)
    {
        Response response = new Response();

        try {
            UserAccount currentUserAccount = userAccountService.getUserAccount(currentUserID);
            if (currentUserAccount == null || !(currentUserAccount.getUser() instanceof Admin)) {
                response.setSuccess(false);
                response.setMessage("User is not Admin");
            }
            else
            { Employee employee = employeeService.addEmployee(accountNumber, branchId, employeeType, employeeNumber);

                authService.register(username,password,employee);

                response.setSuccess(true);
                response.setMessage("Employee added successfully");}

        } catch(EntityNotFoundException e)
        {
            response.setSuccess(false);
            response.setMessage("Employee not found");
        } catch(Exception e) {
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
                response.setSuccess(false);
                response.setMessage("Employee not found");
            } else {
                response.setSuccess(true);
                response.setMessage(gson.toJson(employee));
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to set employee");
        }
        return gson.toJson(response);
    }


    public String getAllEmployeesByBranch(String branchId) {
        Response response = new Response();

        try{
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
