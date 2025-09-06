package com.myshopnet.service;

import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.models.*;
import com.myshopnet.repository.CustomerRepository;
import com.myshopnet.repository.OrderRepository;
import com.myshopnet.utils.Singletons;

import java.util.List;
import java.util.UUID;

public class CustomerService {
    private OrderRepository orderRepository = Singletons.ORDER_REPO;
    private CustomerRepository customerRepository = Singletons.CUSTOMER_REPO;

    public Customer createCustomer(String fullName, String passportId, String phoneNumber) {
        Customer newCustomer = new NewCustomer(UUID.randomUUID().toString(), passportId, phoneNumber, fullName);

        customerRepository.create(newCustomer);
        return newCustomer;
    }
    public Customer getCustomer(String customerId) {
        Customer customer = customerRepository.get(customerId);
        if (customer == null) {
            throw new EntityNotFoundException("Customer");
        }
        return customer;
    }
    public List<Customer> getAllCustomers() {
        return customerRepository.getAll();
    }
    public Customer updateCustomer(String customerId, Customer updatedCustomer) {
        Customer existingCustomer = customerRepository.get(customerId);
        if (existingCustomer == null) {
            throw new EntityNotFoundException("Customer");
        }
        customerRepository.update(customerId, updatedCustomer);
        return updatedCustomer;
    }


    public void checkCustomerStatus(String customerId) {
        Customer customer = customerRepository.get(customerId);

        if (customer == null) {
            throw new EntityNotFoundException("Customer");
        }

        List<Order> allCustomerOrders = orderRepository.getAll().stream()
                .filter(order -> order.getCustomerId().equals(customerId))
                .toList();
        Integer amountOfOrders = allCustomerOrders.size();

        if(allCustomerOrders.isEmpty()) {
            return;
        }

        if (amountOfOrders > 1 && amountOfOrders < 10 && customer instanceof NewCustomer) {
            customer = new ReturningCustomer(customer);
        }

        if (amountOfOrders > 10 && customer instanceof ReturningCustomer) {
            customer = new VipCustomer(customer);
        }

        customerRepository.update(customerId, customer);
    }
}
