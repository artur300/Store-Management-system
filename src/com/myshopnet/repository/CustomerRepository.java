package com.myshopnet.repository;

import com.myshopnet.data.Data;
import com.myshopnet.models.Customer;

import java.util.List;

public class CustomerRepository implements Repository<Customer> {

    @Override
    public Customer create(Customer customer) {
        return Data.getCustomers().put(customer.getUserId(), customer);
    }

    @Override
    public Customer update(String id, Customer customer) {
        Customer updatedCustomer = null;

        if(Data.getCustomers().containsKey(id)) {
            updatedCustomer = Data.getCustomers().put(id, customer);
        }

        return updatedCustomer;
    }

    @Override
    public void delete(String id) {
        Data.getCustomers().remove(id);
    }

    @Override
    public Customer get(String id) {
        return (Customer) Data.getAllAccounts().get(id).getUser();
    }

    @Override
    public List<Customer> getAll() {
        return Data.getCustomers().values().stream().toList();
    }



}

