package com.myshopnet.service;

import com.myshopnet.auth.UserAccount;
import com.myshopnet.repository.UserAccountRepository;

public class UserAccountService {
    private final UserAccountRepository userAccountRepository = new UserAccountRepository();

    public UserAccount getUserAccount(String userId) {
        return userAccountRepository.get(userId);
    }

    public synchronized boolean accountAlreadyExists(UserAccount userAccount) {
        return userAccountRepository.get(userAccount.getUser().getId()) != null;
    }

    public synchronized boolean usernameTaken(String username) {
        return userAccountRepository.getAll().stream()
                .anyMatch(userAccount -> userAccount.getUsername().equals(username));
    }
}

