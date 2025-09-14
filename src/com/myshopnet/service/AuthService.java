package com.myshopnet.service;

import com.myshopnet.auth.PasswordPolicy;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.data.Data;
import com.myshopnet.errors.AuthException;
import com.myshopnet.logs.LogEvent;
import com.myshopnet.logs.LogType;
import com.myshopnet.logs.LoggerImpl;
import com.myshopnet.models.User;
import com.myshopnet.repository.UserAccountRepository;
import com.myshopnet.utils.PasswordUtil;
import com.myshopnet.utils.Singletons;

public class AuthService {
    private UserAccountRepository userAccountRepository = Singletons.USER_ACCOUNT_REPO;
    private UserAccountService userAccountService = Singletons.USER_ACCOUNT_SERVICE;

    public synchronized UserAccount registerAccount(String username, String password, User user) {
        System.out.println("[DEBUG] registerAccount -> username=" + username);

        if (userAccountService.usernameTaken(username)) {
            System.out.println("[DEBUG] registerAccount -> username already taken");
            throw new IllegalStateException("Username already in use, please try again with a different one");
        }

        UserAccount userAccount = null;

        try {
            if (Data.getPasswordPolicy().isValid(password)) {
                userAccount = new UserAccount(username, password, user);
                userAccountRepository.create(userAccount);
                System.out.println("[DEBUG] registerAccount -> account created successfully for " + username);
            } else {
                System.out.println("[DEBUG] registerAccount -> password does not meet policy");
                throw new IllegalArgumentException("Password doesn't meet the password policy.");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] registerAccount -> " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

        return userAccount;
    }

    public synchronized UserAccount loginUser(String username, String password) {
        System.out.println("[DEBUG] loginUser -> attempting login for " + username);

        UserAccount userAccount = userAccountRepository.get(username);

        if (userAccount == null) {
            System.out.println("[DEBUG] loginUser -> user not found: " + username);
            throw new AuthException("User not found");
        }

        if (isLoggedIn(userAccount)) {
            System.out.println("[DEBUG] loginUser -> user already logged in: " + username);
            throw new AuthException("User already logged in from another device");
        }

        if (PasswordUtil.checkPassword(password, userAccount.getPassword())) {
            // חשוב: מפתח לפי username (!)
            Data.getOnlineAccounts().put(userAccount.getUsername(), userAccount);
            System.out.println("[DEBUG] loginUser -> login success: " + username);
        } else {
            System.out.println("[DEBUG] loginUser -> invalid password for " + username);
            throw new AuthException("Invalid password");
        }

        LoggerImpl.getInstance().log(new LogEvent(LogType.LOGIN, "user=" + username));

        if (userAccount.getUser() instanceof com.myshopnet.models.Employee emp) {
            System.out.println("[DEBUG] login -> employee=" + emp.getFullName()
                    + ", branchId=" + emp.getBranchId()
                    + ", status=" + emp.getEmployeeStatus());
        }

        return userAccount;
    }

    public synchronized void logout(UserAccount userAccount) {
        System.out.println("[DEBUG] logout -> user=" + userAccount.getUsername());

        if (!userAccountService.accountAlreadyExists(userAccount)) {
            System.out.println("[DEBUG] logout -> user not found");
            throw new AuthException("User not found");
        }

        if (!isLoggedIn(userAccount)) {
            System.out.println("[DEBUG] logout -> user not logged in");
            throw new AuthException("Not logged in");
        }

        // חשוב: להסיר לפי username (!)
        Data.getOnlineAccounts().remove(userAccount.getUsername());
        System.out.println("[DEBUG] logout -> success for user=" + userAccount.getUsername());
    }

    public synchronized boolean isLoggedIn(UserAccount userAccount) {
        boolean loggedIn = Data.getOnlineAccounts().get(userAccount.getUsername()) != null;
        System.out.println("[DEBUG] isLoggedIn -> user=" + userAccount.getUsername() + ", loggedIn=" + loggedIn);
        return loggedIn;
    }

    public synchronized PasswordPolicy updatePasswordPolicy(
            Integer minChars, Integer maxChars, Integer minNumbers, Integer maxNumbers, boolean includeSpecialChars) {
        System.out.println("[DEBUG] updatePasswordPolicy -> minChars=" + minChars
                + ", maxChars=" + maxChars
                + ", minNumbers=" + minNumbers
                + ", maxNumbers=" + maxNumbers
                + ", includeSpecialChars=" + includeSpecialChars);

        Data.setPasswordPolicy(new PasswordPolicy(minChars, maxChars, minNumbers, maxNumbers, includeSpecialChars));
        return Data.getPasswordPolicy();
    }

    public synchronized PasswordPolicy viewPasswordPolicy() {
        System.out.println("[DEBUG] viewPasswordPolicy -> returning policy");
        return Data.getPasswordPolicy();
    }
}
