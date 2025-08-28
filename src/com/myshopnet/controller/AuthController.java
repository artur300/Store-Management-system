package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.server.Response;
import com.myshopnet.service.AuthService;
import com.myshopnet.utils.GsonSingleton;

public class AuthController {
    private Gson gson = GsonSingleton.getInstance();
    private AuthService authService = new AuthService();

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

    public String register(String username, String password) {
        return "Registered";
    }

    public String logout() {
        Response response = new Response();

        return "Hey";
    }
}
