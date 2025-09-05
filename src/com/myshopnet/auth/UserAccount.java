package com.myshopnet.auth;

import com.google.gson.Gson;
import com.myshopnet.models.User;
import com.myshopnet.utils.PasswordUtil;

public class UserAccount {
    private String username;
    private String password;   // בפועל עדיף hash
    private User user;

    public UserAccount(User user) {
        this.user = user;
    }

    public UserAccount(String username, String password) {
        this.username = username;
        this.password = PasswordUtil.hashPassword(password);
    }
    
    public UserAccount(String username, String password, User user) {
        this(username, password);
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

