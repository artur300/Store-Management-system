package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.models.Admin;
import com.myshopnet.models.Branch;
import com.myshopnet.server.Response;
import com.myshopnet.service.BranchService;
import com.myshopnet.service.UserAccountService;
import com.myshopnet.utils.GsonSingleton;

public class BranchController {
    private Gson gson = GsonSingleton.getInstance();
    private BranchService branchService = new BranchService();
    private UserAccountService userAccountService = new UserAccountService();

    public String createBranch(String userId, String branchName) {
        Response response = new Response();

        try {
            UserAccount userAccount = userAccountService.getUserAccount(userId);
            Branch branch = null;

            if (userAccount != null && userAccount.getUser() instanceof Admin) {
                branch = branchService.createNewBranch(branchName);
            }

            response.setSuccess(true);
            response.setMessage(gson.toJson(branch));
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

    public String updateBranchStock(String branchId, String userId, String productId, Long stock) {

    }

    public String buyProductFromBranch(String branchId, String userId, String productId) {

    }
}
