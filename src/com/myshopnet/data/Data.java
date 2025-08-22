package com.myshopnet.data;

import com.myshopnet.auth.SessionRegistry;
import com.myshopnet.models.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Data {
    private static final Map<String, SessionRegistry.SessionInfo> userSessions = new ConcurrentHashMap<>();
    private static final Map<String, String> userIds = new ConcurrentHashMap<>();
    private static final Map<String, Branch> branches = new ConcurrentHashMap<>();
    private static final Map<String, ? extends User> users = new ConcurrentHashMap<>();
    private static final Map<String, Product> products = new ConcurrentHashMap<>();
    private static final Map<String, Stock> stocks = new ConcurrentHashMap<>();

    private Data() { }

    public static Map<String, SessionRegistry.SessionInfo> getUserSessions() {
        return userSessions;
    }

    public static Map<String, String> getUserIds() {
        return userIds;
    }

    public static Map<String, Branch> getBranches() {
        return branches;
    }

    public static Map<String, Customer> getCustomers() {
        return customers;
    }

    public static Map<String, Product> getProducts() {
        return products;
    }

    public static Map<String, Employee> getEmployees() {
        return employees;
    }

    public static Map<String, Stock> getStocks() {
        return stocks;
    }
}
