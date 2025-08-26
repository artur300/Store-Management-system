package com.myshopnet.controller;

import com.myshopnet.service.BranchService;
import com.myshopnet.service.UserService;

public class BranchController {
    private BranchService branchService = new BranchService();
    private UserService userService = new UserService();

    public void createBranch(String userId, String branchName) {
        //if ()
        branchService.createNewBranch(branchName);
    }
}
