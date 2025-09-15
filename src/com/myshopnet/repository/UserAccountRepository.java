package com.myshopnet.repository;

import com.myshopnet.auth.UserAccount;
import com.myshopnet.data.Data;
import com.myshopnet.utils.PasswordUtil;

import java.util.List;

public class UserAccountRepository implements Repository<UserAccount> {
    @Override
    public UserAccount create(UserAccount userAccount) {
        Data.getAllAccounts().put(userAccount.getUsername(), userAccount);

        return userAccount;
    }

    @Override
    public UserAccount update(String id, UserAccount userAccount) {
        UserAccount oldUserAccount = Data.getAllAccounts().get(id);

        if (oldUserAccount != null) {
            Data.getAllAccounts().put(id, userAccount);
        }

        return oldUserAccount;
    }

    @Override
    public void delete(String id) {
        Data.getAllAccounts().remove(id);
    }

    @Override
    public UserAccount get(String id) {
        UserAccount ua = Data.getAllAccounts().get(id);
        return ua;
    }

    public UserAccount get(String username, String password) {
        return getAll().stream()
                .filter(userAccount -> PasswordUtil.checkPassword(password, userAccount.getPassword())
                        && userAccount.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<UserAccount> getAll() {
        List<UserAccount> accounts = Data.getAllAccounts().values().stream().toList();
        return accounts;
    }

    public UserAccount getByUsername(String username) {
        return getAll().stream()
                .filter(userAccount -> userAccount.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public UserAccount getByUserId(String userId) {
        return getAll().stream()
                .filter(userAccount -> userAccount.getUser().getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}
