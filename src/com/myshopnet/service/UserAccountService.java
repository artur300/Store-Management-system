package com.myshopnet.service;

import com.myshopnet.auth.UserAccount;
import com.myshopnet.data.Data;
import com.myshopnet.repository.UserAccountRepository;
import com.myshopnet.utils.PasswordUtil;

import java.util.List;

public class UserAccountService {
    private final UserAccountRepository userAccountRepository = new UserAccountRepository();

    public List<UserAccount> getAllUserAccounts() {
        return userAccountRepository.getAll();
    }

    public UserAccount getUserAccount(String userId) {
        return userAccountRepository.get(userId);
    }

    public synchronized boolean accountAlreadyExists(UserAccount userAccount) {
        return userAccountRepository.get(userAccount.getUser().getUserId()) != null;
    }

    public synchronized boolean usernameTaken(String username) {
        return userAccountRepository.getAll().stream()
                .anyMatch(userAccount -> userAccount.getUsername().equals(username));
    }

    public synchronized UserAccount getUserAccountByUsername(String username) {
        return userAccountRepository.getAll().stream()
                .filter(userAccount -> userAccount.getUsername().equals(username))
                .toList()
                .getFirst();
    }

    public synchronized UserAccount resetPassword(UserAccount userAccount, String newPassword) {
        if (Data.getPasswordPolicy().isValid(newPassword)) {
            userAccount.setPassword(PasswordUtil.hashPassword(newPassword));

            userAccountRepository.update(userAccount.getUser().getUserId(), userAccount);
            return userAccount;
        }

        throw new SecurityException("Password doesn't meet the password policy.");
    }
}

