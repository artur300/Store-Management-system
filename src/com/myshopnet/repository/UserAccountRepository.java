package com.myshopnet.repository;

import com.myshopnet.auth.UserAccount;
import com.myshopnet.data.Data;
import com.myshopnet.utils.PasswordUtil;

import java.util.List;

public class UserAccountRepository implements Repository<UserAccount> {

    @Override
    public UserAccount create(UserAccount userAccount) {
        System.out.println("[DEBUG] UserAccountRepository.create -> username="
                + userAccount.getUsername() + ", userId=" + userAccount.getUser().getUserId());

        // שמירה לפי username
        Data.getAllAccounts().put(userAccount.getUsername(), userAccount);

        // שמירה גם לפי userId (כדי שנוכל לחפש גם לפי UUID)
        Data.getAllAccounts().put(userAccount.getUser().getUserId(), userAccount);

        return userAccount;
    }

    @Override
    public UserAccount update(String id, UserAccount userAccount) {
        System.out.println("[DEBUG] UserAccountRepository.update -> id=" + id
                + ", username=" + userAccount.getUsername());

        UserAccount oldUserAccount = Data.getAllAccounts().get(id);

        if (oldUserAccount != null) {
            Data.getAllAccounts().put(id, userAccount);
            System.out.println("[DEBUG] update -> success for id=" + id);
        } else {
            System.out.println("[DEBUG] update -> ❌ oldUserAccount not found for id=" + id);
        }

        return oldUserAccount;
    }

    @Override
    public void delete(String id) {
        System.out.println("[DEBUG] UserAccountRepository.delete -> id=" + id);
        Data.getAllAccounts().remove(id);
    }

    @Override
    public UserAccount get(String id) {
        UserAccount ua = Data.getAllAccounts().get(id);
        System.out.println("[DEBUG] UserAccountRepository.get -> id=" + id + ", found=" + (ua != null));
        return ua;
    }

    public UserAccount get(String username, String password) {
        System.out.println("[DEBUG] UserAccountRepository.get(username,password) -> username=" + username);

        return getAll().stream()
                .filter(userAccount -> PasswordUtil.checkPassword(password, userAccount.getPassword())
                        && userAccount.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<UserAccount> getAll() {
        List<UserAccount> accounts = Data.getAllAccounts().values().stream().toList();
        System.out.println("[DEBUG] UserAccountRepository.getAll -> total=" + accounts.size());
        return accounts;
    }

    public UserAccount getByUsername(String username) {
        System.out.println("[DEBUG] UserAccountRepository.getByUsername -> username=" + username);
        return getAll().stream()
                .filter(userAccount -> userAccount.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    // 🔹 שליפה לפי userId (UUID)
    public UserAccount getByUserId(String userId) {
        System.out.println("[DEBUG] UserAccountRepository.getByUserId -> searching userId=" + userId);
        return getAll().stream()
                .filter(userAccount -> userAccount.getUser().getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}
