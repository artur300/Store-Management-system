package com.myshopnet.repository;

import com.myshopnet.models.customers.Customer;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerRepository {
    private final Map<String, Customer> byId = new ConcurrentHashMap<>();

    public void upsert(Customer c) { byId.put(c.getId(), c); }
    public Customer get(String id) { return byId.get(id); }
    public boolean exists(String id) { return byId.containsKey(id); }
    public Collection<Customer> list() { return byId.values(); }
}

