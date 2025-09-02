package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.errors.InsufficientPermissionsException;
import com.myshopnet.models.Admin;
import com.myshopnet.models.Branch;
import com.myshopnet.models.Employee;
import com.myshopnet.server.Response;
import com.myshopnet.service.AuthService;
import com.myshopnet.service.BranchService;
import com.myshopnet.service.StockService;
import com.myshopnet.service.UserAccountService;
import com.myshopnet.utils.GsonSingleton;

public class BranchController {
    private Gson gson = GsonSingleton.getInstance();
    private BranchService branchService = new BranchService();
    private AuthService authService = new AuthService();
    private UserAccountService userAccountService = new UserAccountService();
    private StockService stockService = new StockService();

    public String createBranch(String userId, String branchName) {
        Response response = new Response();

        try {
            UserAccount userAccount = userAccountService.getUserAccount(userId);
            Branch branch = null;

            if (userAccount != null
                    && authService.isLoggedIn(userAccount)
                    && userAccount.getUser() instanceof Admin) {
                branch = branchService.createNewBranch(branchName);

                response.setSuccess(true);
                response.setMessage(gson.toJson(branch));
            }
            else {
                response.setSuccess(false);
                response.setMessage("No permission");
            }
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

    public String updateBranchStock(String branchId, String userId, String productId, Long stock) {
        Response response = new Response();

        try {
            UserAccount userAccount = userAccountService.getUserAccount(userId);

            if (userAccount != null && authService.isLoggedIn(userAccount) && userAccount.getUser() instanceof Employee) {
                stockService.updateProductStock(branchId, productId, stock);
            }
            else {
                throw new InsufficientPermissionsException("You are not an Employee");
            }

            response.setSuccess(true);
            response.setMessage(gson.toJson(response));
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }
}
