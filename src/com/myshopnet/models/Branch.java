package com.myshopnet.models;

import com.myshopnet.auth.UserAccount;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

public class Branch {
    private final String id;
    private final String name;
    private final Stock productsStock;
    private final Queue<UserAccount> employeesWaitingToChat;

    public Branch(String id, String name) {
        this.id = id;
        this.name = name;
        productsStock = new Stock();
        employeesWaitingToChat = new LinkedBlockingDeque<>( );
    }

    public String getId() {
        return id;
    }

    public Stock getProductsStock() {
        return productsStock;
    }

    public String getName() {
        return name;
    }

    public Queue<UserAccount> getEmployeesWaitingToChat() {
        return employeesWaitingToChat;
    }
}
