package com.myshopnet.service;

import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.models.*;
import com.myshopnet.repository.CustomerRepository;
import com.myshopnet.repository.OrderRepository;

import java.util.List;

public class CustomerService {
    private OrderRepository orderRepository = new OrderRepository();
    private CustomerRepository customerRepository = new CustomerRepository();

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
