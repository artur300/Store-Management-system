package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.models.Admin;
import com.myshopnet.models.User;
import com.myshopnet.server.Response;
import com.myshopnet.service.AuthService;
import com.myshopnet.service.UserAccountService;
import com.myshopnet.utils.GsonSingleton;

import java.util.List;

public class UserAccountController {
    private Gson gson = GsonSingleton.getInstance();
    private AuthService authService = new AuthService();
    private UserAccountService userAccountService = new UserAccountService();

    public String getAllUserAccounts(String userId) {
        Response response = new Response();

        try {
            UserAccount user = userAccountService.getUserAccount(userId);

            if (user == null) {
                throw new EntityNotFoundException("User not found");
            }

            if (!authService.isLoggedIn(user)) {
                throw new SecurityException("You are not logged in");
            }

            if (!(user.getUser() instanceof Admin)) {
                throw new SecurityException("You are not admin");
            }

            List<UserAccount> allUserAccounts = userAccountService.getAllUserAccounts();

            response.setSuccess(true);
            response.setMessage(gson.toJson(allUserAccounts));
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }
}
