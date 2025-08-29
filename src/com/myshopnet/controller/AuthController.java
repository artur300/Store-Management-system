package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.models.User;
import com.myshopnet.server.Response;
import com.myshopnet.service.AuthService;
import com.myshopnet.service.UserAccountService;
import com.myshopnet.utils.GsonSingleton;

public class AuthController {
    private Gson gson = GsonSingleton.getInstance();
    private AuthService authService = new AuthService();
    private UserAccountService userAccountService = new UserAccountService();

    public String login(String username, String password) {
        Response response = new Response();

        try {
            UserAccount userAccount = authService.loginUser(username, password);

            response.setSuccess(true);
            response.setMessage(gson.toJson(userAccount.getUser()));
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

    public String register(String username, String password, User user) {
        Response response = new Response();

        try {
            authService.registerAccount(username, password, user);

            response.setSuccess(true);
            response.setMessage(gson.toJson(user));
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

    public String logout(String userId) {
        Response response = new Response();
        UserAccount user = userAccountService.getUserAccount(userId);

        try {
            authService.logout(user);

            response.setSuccess(true);
            response.setMessage("Logged out successfully");
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }
}
