package com.myshopnet.controller;
import com.myshopnet.service.CustomerService;
import com.myshopnet.models.Customer;
import java.util.List;
import com.myshopnet.errors.EntityNotFoundException;

public class CustomerController {
    private CustomerService customerService = new CustomerService();

    public Customer createCustomer(String fullName, String passportId, String phoneNumber) {
        try {
            return customerService.createCustomer(fullName, passportId, phoneNumber);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create customer");
        }
    }

    public Customer getCustomer(String customerId) {
        try {
            return customerService.getCustomer(customerId);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Customer not found with ID: " + customerId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get customer");
        }
    }

    public List<Customer> getAllCustomers() {
        try {
            return customerService.getAllCustomers();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get customers");
        }
    }
}
