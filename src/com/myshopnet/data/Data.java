package com.myshopnet.data;

import com.myshopnet.auth.SessionRegistry;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.models.Chat;
import com.myshopnet.models.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Data {
    private static final Map<String, SessionRegistry.SessionInfo> userSessions = new ConcurrentHashMap<>();
    private static final Map<String, String> userIds = new ConcurrentHashMap<>();
    private static final Map<String, Branch> branches = new ConcurrentHashMap<>();
    private static final Map<String, ? extends User> users = new ConcurrentHashMap<>();
    private static final Map<String, Product> products = new ConcurrentHashMap<>();
    private static final Map<String, Order> orders = new ConcurrentHashMap<>();
    private static final Map<String, Chat> ongoingChats = new ConcurrentHashMap<>();
    private static final Map<String, UserAccount> accounts = new ConcurrentHashMap<>();

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
        Map<String, Customer> customers = new ConcurrentHashMap<>();

        users.forEach((key, user) -> {
              if (user instanceof Customer) {
                   customers.put(key, (Customer) user);
              }
        });

        return customers;
    }

    public static Map<String, Product> getProducts() {
        return products;
    }

    public static Map<String, Employee> getEmployees() {
        Map<String, Employee> employees = new ConcurrentHashMap<>();

        users.forEach((key, user) -> {
            if (user instanceof Employee) {
                employees.put(key, (Employee) user);
            }
        });

        return employees;
    }

    public static Map<String, Order> getOrders() {
        return orders;
    }

    public static Map<String, Chat> getOngoingChats() {
        return ongoingChats;
    }

    public static Map<String, UserAccount> getAccounts() {
        return accounts;
    }
}
