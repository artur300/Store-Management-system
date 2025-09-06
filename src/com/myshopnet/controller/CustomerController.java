package com.myshopnet.controller;
import com.google.gson.Gson;
import com.myshopnet.server.Response;
import com.myshopnet.service.CustomerService;
import com.myshopnet.models.Customer;
import java.util.List;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.utils.GsonSingleton;

public class CustomerController {
    private Gson gson = GsonSingleton.getInstance();
    private CustomerService customerService = com.myshopnet.utils.Singletons.CUSTOMER_SERVICE;

    public String createCustomer(String fullName, String passportId, String phoneNumber) {
        Response response = new Response();

        try {
            Customer customer = customerService.createCustomer(fullName, passportId, phoneNumber);

            response.setSuccess(true);
            response.setMessage(gson.toJson(customer));
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
