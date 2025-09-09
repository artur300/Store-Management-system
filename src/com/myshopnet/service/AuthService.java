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
        if (userAccountService.usernameTaken(username)) {
            throw new IllegalStateException("Username already in use, please try again with a different one");
        }

        UserAccount userAccount = null;

        try {
            if (Data.getPasswordPolicy().isValid(password)) {
                userAccount = new UserAccount(username, password, user);

                userAccountRepository.create(userAccount);
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        return userAccount;
    }

    public synchronized UserAccount loginUser(String username, String password) {
        UserAccount userAccount = userAccountRepository.get(username, password);

        if (userAccount == null) {
            throw new AuthException("User not found");
        }

        if (isLoggedIn(userAccount)) {
            throw new AuthException("Already Logged In");
        }

        if (PasswordUtil.checkPassword(password, userAccount.getPassword())) {
            Data.getOnlineAccounts().put(userAccount.getUsername(), userAccount);
        }

        LoggerImpl.getInstance().log(new LogEvent(LogType.LOGIN, "user=" + username));
        return userAccount;
    }

    public synchronized void logout(UserAccount userAccount) {
        if(!userAccountService.accountAlreadyExists(userAccount)) {
            throw new AuthException("User not found");
        }

        if (isLoggedIn(userAccount)) {
            throw new AuthException("Not logged in");
        }

        Data.getOnlineAccounts().remove(userAccount.getUser().getUserId());
    }

    public synchronized boolean isLoggedIn(UserAccount userAccount) {
        UserAccount loggedUserAccount = Data.getOnlineAccounts().get(String.valueOf(userAccount.getUser().getUserId()));

        return loggedUserAccount != null;
    }

    public synchronized PasswordPolicy updatePasswordPolicy(Integer minChars, Integer maxChars, Integer minNumbers, Integer maxNumbers, boolean includeSpecialChars) {
        Data.setPasswordPolicy(new PasswordPolicy(minChars, maxChars, minNumbers, maxNumbers, includeSpecialChars));

        return Data.getPasswordPolicy();
    }

    public synchronized PasswordPolicy viewPasswordPolicy() {
        return Data.getPasswordPolicy();
    }
}

