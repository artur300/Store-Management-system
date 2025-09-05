package com.myshopnet.repository;

import com.myshopnet.auth.UserAccount;
import com.myshopnet.data.Data;
import com.myshopnet.utils.PasswordUtil;

import java.util.List;

public class UserAccountRepository implements Repository<UserAccount> {

    @Override
    public UserAccount create(UserAccount userAccount) {
        return update(userAccount.getUser().getUserId(), userAccount);
    }

    @Override
    public UserAccount update(String id, UserAccount userAccount) {
        UserAccount oldUserAccount = Data.getOnlineAccounts().get(userAccount.getUser().getUserId());

        if (oldUserAccount != null) {
            Data.getAllAccounts().replace(id, userAccount);
        }

        return oldUserAccount;
    }

    @Override
    public void delete(String id) {
        Data.getAllAccounts().remove(id);
    }

    @Override
    public UserAccount get(String id) {
        return Data.getAllAccounts().get(id);
    }

    public UserAccount get(String username, String password) {
        return getAll().stream()
                .filter(userAccount -> PasswordUtil.checkPassword(password, userAccount.getPassword()) && userAccount.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<UserAccount> getAll() {
        return Data.getAllAccounts().values().stream().toList();
    }
}
