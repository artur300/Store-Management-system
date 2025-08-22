package com.myshopnet.auth;

import com.myshopnet.models.Role;

public class UserAccount {
    private final String username;
    private final String password;   // בפועל עדיף hash
    private final String employeeId;
    private final String branchId;
    private final Role role;

    public UserAccount(String username, String password,
                       String employeeId, String branchId, Role role) {
        this.username = username;
        this.password = password;
        this.employeeId = employeeId;
        this.branchId = branchId;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmployeeId() { return employeeId; }
    public String getBranchId() { return branchId; }
    public Role getRole() { return role; }
}

