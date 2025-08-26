package com.myshopnet.controller;
import com.myshopnet.service.CustomerService;
import com.myshopnet.models.Customer;
import java.util.List;
import com.myshopnet.errors.EntityNotFoundException;

public class CustomerController {
    private CustomerService customerService = new CustomerService();


    public Customer createCustomer(Customer customer) {
        try {
            return customerService.createCustomer(customer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create customer");
        }
    }


    public Customer getCustomer(String customerId) {
        try {
            return customerService.readCustomer(customerId);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Customer not found with ID: " + customerId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get customer");
        }
    }


    public List<Customer> getAllCustomers() {
        try {
            return customerService.readAllCustomers();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get customers");
        }
    }


    public Customer updateCustomer(String customerId, Customer updatedCustomer) {
        try {
            return customerService.updateCustomer(customerId, updatedCustomer);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Customer not found with ID");
        } catch (Exception e) {
            throw new RuntimeException("Failed to update customer");
        }
    }



    public boolean customerExists(String customerId) {
        try {
            customerService.readCustomer(customerId);
            return true;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }
}
