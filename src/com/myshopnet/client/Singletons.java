package com.myshopnet.client;

import com.google.gson.Gson;

public class Singletons {
    public static final Gson GSON = new Gson();

    public static final ChatSession CHAT_SESSION = new ChatSession();
    public static final ChatClient CHAT_CLIENT = new ChatClient();
    public static final Client CLIENT = new Client();

    public static final LogMenu LOG_MENU = new LogMenu();
    public static final LoginMenu LOGIN_MENU = new LoginMenu();
    public static final Menu ADMIN_MENU = new AdminMenu();
    public static final Menu CUSTOMER_MENU = new CustomerMenu();
    public static final Menu EMPLOYEE_MENU = new EmployeeMenu();
    public static final Menu STOCK_MENU = new StockMenu();
    public static final Menu REGISTER_MENU = new RegisterMenu();
    public static final Menu PASSWORD_POLICY_MENU = new PasswordPolicyMenu();
    public static final Menu BRANCH_MENU = new BranchMenu();
    public static final Menu MAIN_MENU = new MainMenu();
}
