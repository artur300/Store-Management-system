package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.auth.PasswordPolicy;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.models.Admin;
import com.myshopnet.models.User;
import com.myshopnet.server.Response;
import com.myshopnet.service.AuthService;
import com.myshopnet.service.UserAccountService;
import com.myshopnet.utils.GsonSingleton;
import com.myshopnet.utils.Singletons;

public class AuthController {
    private Gson gson = GsonSingleton.getInstance();
    private AuthService authService = Singletons.AUTH_SERVICE;
    private UserAccountService userAccountService = Singletons.USER_ACCOUNT_SERVICE;

    public String login(String username, String password) {
        Response response = new Response();

        try {
            UserAccount userAccount = authService.loginUser(username, password);

            response.setSuccess(true);
            response.setMessage(gson.toJson(userAccount));
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

    public String register(String username, String password, String userId) {
        Response response = new Response();

        try {
            User user = userAccountService.getUserAccount(userId).getUser();
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

    public String logout(String username) {
        Response response = new Response();

        try {
            UserAccount user = userAccountService.getUserAccount(username);

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

    public String viewPasswordPolicy(String userId) {
        Response response = new Response();

        try {
            UserAccount user = userAccountService.getUserAccount(userId);

            if (user == null) {
                throw new SecurityException("User not found");
            }

            if (!authService.isLoggedIn(user)) {
                throw new SecurityException("User not logged in");
            }

            if (!(user.getUser() instanceof Admin)) {
                throw new SecurityException("User not admin");
            }

            PasswordPolicy passwordPolicy = authService.viewPasswordPolicy();

            response.setSuccess(true);
            response.setMessage(gson.toJson(passwordPolicy));
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

    public String resetPassword(String userId, String username, String newPassword) {
        Response response = new Response();

        try {
            UserAccount user = userAccountService.getUserAccount(userId);

            if (user == null) {
                throw new SecurityException("User not found");
            }

            if (!authService.isLoggedIn(user)) {
                throw new SecurityException("User not logged in");
            }

            if (!(user.getUser() instanceof Admin)) {
                throw new SecurityException("User not admin");
            }

            UserAccount userToChangePassword = userAccountService.getUserAccountByUsername(userId);

            if (userToChangePassword == null) {
                throw new SecurityException("User to change password not found");
            }

            if (user.equals(userToChangePassword)) {
                throw new IllegalArgumentException("Can't change password to yourself!");
            }

            userAccountService.resetPassword(userToChangePassword, newPassword);
            response.setSuccess(true);
            response.setMessage("Password changed successfully");
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

    public String updatePasswordPolicy(String userId, Integer minChars, Integer maxChars, Integer minNumbers, Integer maxNumbers, boolean hasSpecialCharacters) {
        Response response = new Response();

        try {
            UserAccount user = userAccountService.getUserAccount(userId);

            if (user == null) {
                throw new SecurityException("User not found");
            }

            if (!authService.isLoggedIn(user)) {
                throw new SecurityException("User not logged in");
            }

            if (!(user.getUser() instanceof Admin)) {
                throw new SecurityException("User not admin");
            }

            PasswordPolicy passwordPolicy = authService.updatePasswordPolicy(minChars, maxChars, minNumbers, maxNumbers,hasSpecialCharacters);

            response.setSuccess(true);
            response.setMessage(gson.toJson(passwordPolicy));
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }
}
