package com.myshopnet.service;

import com.myshopnet.auth.UserAccount;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.models.*;
import com.myshopnet.repository.CustomerRepository;
import com.myshopnet.repository.OrderRepository;
import com.myshopnet.repository.UserAccountRepository;
import com.myshopnet.utils.Singletons;

import java.util.List;
import java.util.UUID;

public class CustomerService {
    private OrderRepository orderRepository = Singletons.ORDER_REPO;
    private CustomerRepository customerRepository = Singletons.CUSTOMER_REPO;
    private AuthService authService = Singletons.AUTH_SERVICE;
    private UserAccountRepository userAccountRepository = Singletons.USER_ACCOUNT_REPO;

    public Customer createCustomer(String username, String password, String fullName, String passportId, String phoneNumber) {
        Customer newCustomer = new NewCustomer(UUID.randomUUID().toString(), passportId, phoneNumber, fullName);

        authService.registerAccount(username, password, newCustomer);

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

    public void deleteCustomer(UserAccount customer) {
        customerRepository.delete(customer.getUsername());
    }

    public Customer updateCustomer(String username,String fullName, String passportId, String phoneNumber) {
        UserAccount userAccount = userAccountRepository.get(username);

        if (userAccount == null) {
            throw new EntityNotFoundException("Customer");
        }

        if (!(userAccount.getUser() instanceof Customer)) {
            throw new EntityNotFoundException("Customer");
        }

        ((Customer)(userAccount.getUser())).setFullName(fullName);
        ((Customer)(userAccount.getUser())).setPassportId(passportId);
        ((Customer)(userAccount.getUser())).setPhone(phoneNumber);

        return customerRepository.update(username, (Customer) userAccount.getUser());
    }

    public String getCustomerPlan(UserAccount userAccount) {
        return userAccount.getUser().toString();
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
            customer.setCustomerType(CustomerType.RETURNING_CUSTOMER);
            customer = new ReturningCustomer(customer);
        }

        if (amountOfOrders > 10 && customer instanceof ReturningCustomer) {
            customer.setCustomerType(CustomerType.VIP_CUSTOMER);
            customer = new VipCustomer(customer);
        }

        customerRepository.update(customerId, customer);
    }
}
