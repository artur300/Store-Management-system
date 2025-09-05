package com.myshopnet.data;

import com.myshopnet.auth.PasswordPolicy;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.models.Chat;
import com.myshopnet.models.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Data {
    private static final Map<String, Branch> branches = new ConcurrentHashMap<>();
    private static final Map<String, Customer> customers = new ConcurrentHashMap<>();
    private static final Map<String, Product> products = new ConcurrentHashMap<>();
    private static final Map<String, Order> orders = new ConcurrentHashMap<>();
    private static final Map<String, Chat> ongoingChats = new ConcurrentHashMap<>();
    private static final Map<String, UserAccount> onlineAccounts = new ConcurrentHashMap<>();
    private static final Map<String, UserAccount> allAccounts = new ConcurrentHashMap<>();
    private static PasswordPolicy passwordPolicy = PasswordPolicy.defaultPolicy();

    private Data() { }

    public static Map<String, Branch> getBranches() {
        return branches;
    }

    public static Map<String, Customer> getCustomers() {
        return customers;
    }

    public static Map<String, Product> getProducts() {
        return products;
    }

    public static Map<String, Order> getOrders() {
        return orders;
    }

    public static Map<String, Chat> getOngoingChats() {
        return ongoingChats;
    }

    public static Map<String, UserAccount> getOnlineAccounts() {
        return onlineAccounts;
    }

    public static Map<String, UserAccount> getAllAccounts() {
        return allAccounts;
    }

    public static PasswordPolicy getPasswordPolicy() {
        return passwordPolicy;
    }

    public static void setPasswordPolicy(PasswordPolicy passwordPolicy) {
        Data.passwordPolicy = passwordPolicy;
    }
}
