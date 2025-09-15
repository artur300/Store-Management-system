package com.myshopnet.utils;

import com.myshopnet.controller.*;
import com.myshopnet.logs.Logger;
import com.myshopnet.logs.LoggerImpl;
import com.myshopnet.repository.*;
import com.myshopnet.server.ChatServer;
import com.myshopnet.server.Server;
import com.myshopnet.service.*;

public final class Singletons {
    private Singletons() {}

    public static final Logger LOGGER = LoggerImpl.getInstance();

    public static ChatServer CHAT_SERVER = new ChatServer();

    // --- Repositories ---
    public static final BranchRepository BRANCH_REPO = new BranchRepository();
    public static final ChatRepository CHAT_REPO = new ChatRepository();
    public static final CustomerRepository CUSTOMER_REPO = new CustomerRepository();
    public static final EmployeeRepository EMPLOYEE_REPO = new EmployeeRepository();
    public static final OrderRepository ORDER_REPO = new OrderRepository();
    public static final ProductRepository PRODUCT_REPO = new ProductRepository();
    public static final UserAccountRepository USER_ACCOUNT_REPO = new UserAccountRepository();

    // --- Services ---
    public static final UserAccountService USER_ACCOUNT_SERVICE = new UserAccountService();
    public static final AuthService AUTH_SERVICE = new AuthService();
    public static final BranchService BRANCH_SERVICE = new BranchService();
    public static final EmployeeService EMPLOYEE_SERVICE = new EmployeeService();
    public static final ChatService CHAT_SERVICE = new ChatService();
    public static final CustomerService CUSTOMER_SERVICE = new CustomerService();
    public static final OrderService ORDER_SERVICE = new OrderService();
    public static final ProductService PRODUCT_SERVICE = new ProductService();
    public static final StockService STOCK_SERVICE = new StockService();

    // --- Controllers ---
    public static final AuthController AUTH_CONTROLLER = new AuthController();
    public static final BranchController BRANCH_CONTROLLER = new BranchController();
    public static final CustomerController CUSTOMER_CONTROLLER = new CustomerController();
    public static final EmployeeController EMPLOYEE_CONTROLLER = new EmployeeController();
    public static final OrderController ORDER_CONTROLLER = new OrderController();
    public static final ProductController PRODUCT_CONTROLLER = new ProductController();
    public static final UserAccountController USER_ACCOUNT_CONTROLLER = new UserAccountController();
    public static final ChatController CHAT_CONTROLLER = new ChatController();

    public static final Server SERVER = new Server();
}

