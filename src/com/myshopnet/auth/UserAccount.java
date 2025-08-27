package com.myshopnet.auth;

import com.google.gson.Gson;
import com.myshopnet.models.User;

public class UserAccount {
    private final String username;
    private final String password;   // בפועל עדיף hash
    private User user;

    public UserAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public UserAccount(String username, String password, User user) {
        this.username = username;
        this.password = password;
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

