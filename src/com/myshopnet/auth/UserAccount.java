package com.myshopnet.auth;

public class UserAccount {
    private final String username;
    private final String password;   // בפועל עדיף hash
    private final String userId;

    public UserAccount(String username, String password,
                       String userId) {
        this.username = username;
        this.password = password;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUserId() {
        return userId;
    }
}

