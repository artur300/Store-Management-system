package com.myshopnet.controller;
import com.google.gson.Gson;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.logs.LogEvent;
import com.myshopnet.logs.LogType;
import com.myshopnet.models.Admin;
import com.myshopnet.server.Response;
import com.myshopnet.service.AuthService;
import com.myshopnet.service.CustomerService;
import com.myshopnet.models.Customer;
import java.util.List;

import com.myshopnet.service.UserAccountService;
import com.myshopnet.utils.GsonSingleton;
import com.myshopnet.utils.Singletons;

public class CustomerController {
    private Gson gson = GsonSingleton.getInstance();
    private CustomerService customerService = Singletons.CUSTOMER_SERVICE;
    private UserAccountService userAccountService = Singletons.USER_ACCOUNT_SERVICE;

    public String createCustomer(String username, String password, String fullName, String passportId, String phoneNumber) {
        Response response = new Response();

        try {
            customerService.createCustomer(username, password, fullName, passportId, phoneNumber);
            UserAccount userAccount = userAccountService.getUserAccount(username);
            Singletons.LOGGER.log(new LogEvent(LogType.CUSTOMER_REGISTERED,"Customer Created"));

            response.setSuccess(true);
            response.setMessage(gson.toJson(userAccount));
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

    public String getCustomer(String customerId) {
        Response response = new Response();

        try {
            Customer customer = customerService.getCustomer(customerId);

            response.setSuccess(true);
            response.setMessage(gson.toJson(customer));
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

    public String deleteCustomer(String currentUserId, String customerId) {
        Response response = new Response();

        try {
            UserAccount userAccount = userAccountService.getUserAccount(currentUserId);

            if (userAccount == null) {
                throw new EntityNotFoundException("Admin");
            }

            if (!(userAccount.getUser() instanceof Admin)) {
                throw new SecurityException("You are not allowed to delete a customer");
            }

            UserAccount customerToDelete = userAccountService.getUserAccount(customerId);

            if (customerToDelete == null) {
                throw new EntityNotFoundException("Customer");
            }

            if (!(customerToDelete.getUser() instanceof Customer)) {
                throw new SecurityException("Selected user is not a customer");
            }

            customerService.deleteCustomer(customerToDelete);

            response.setSuccess(true);
            response.setMessage("Deleted Customer Successfully");
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

    public String updateCustomer(String currentUserId, String username,String fullName, String passportId, String phoneNumber) {
        Response response = new Response();

        try {
            UserAccount userAccount = userAccountService.getUserAccount(currentUserId);

            if (userAccount == null) {
                throw new EntityNotFoundException("Admin");
            }

            if (!(userAccount.getUser() instanceof Admin)) {
                throw new SecurityException("You are not allowed to delete a customer");
            }

            UserAccount customerToEdit = userAccountService.getUserAccount(username);

            if (customerToEdit == null) {
                throw new EntityNotFoundException("Customer");
            }

            if (!(customerToEdit.getUser() instanceof Customer)) {
                throw new SecurityException("Selected user is not a customer");
            }

            customerService.updateCustomer(username, fullName, passportId, phoneNumber);

            response.setSuccess(true);
            response.setMessage("Deleted Customer Successfully");
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

    public String getCustomerPlan(String userId) {
        Response response = new Response();

        try {
            UserAccount userAccount = userAccountService.getUserAccount(userId);

            if (userAccount == null) {
                throw new EntityNotFoundException("Customer");
            }

            if(!(userAccount.getUser() instanceof Customer)) {
                throw new SecurityException("User is not a Customer");
            }

            response.setSuccess(true);
            response.setMessage(customerService.getCustomerPlan(userAccount));
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            e.printStackTrace();
        }

        return gson.toJson(response);
    }

    public String getAllCustomers() {
        Response response = new Response();

        try {
            List<Customer> customers = customerService.getAllCustomers();

            response.setSuccess(true);
            response.setMessage(gson.toJson(customers));
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }
}
