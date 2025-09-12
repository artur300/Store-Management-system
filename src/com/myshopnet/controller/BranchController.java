package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.errors.InsufficientPermissionsException;
import com.myshopnet.models.Admin;
import com.myshopnet.models.Branch;
import com.myshopnet.models.Employee;
import com.myshopnet.models.Product;
import com.myshopnet.server.Response;
import com.myshopnet.service.AuthService;
import com.myshopnet.service.BranchService;
import com.myshopnet.service.StockService;
import com.myshopnet.service.UserAccountService;
import com.myshopnet.utils.GsonSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchController {
    private Gson gson = GsonSingleton.getInstance();
    private BranchService branchService = com.myshopnet.utils.Singletons.BRANCH_SERVICE;
    private AuthService authService = com.myshopnet.utils.Singletons.AUTH_SERVICE;
    private UserAccountService userAccountService = com.myshopnet.utils.Singletons.USER_ACCOUNT_SERVICE;
    private StockService stockService = com.myshopnet.utils.Singletons.STOCK_SERVICE;

    public String getAllBranches(String userId) {
        Response response = new Response();

        try {
            UserAccount userAccount = userAccountService.getUserAccount(userId);

            if (userAccount == null) {
                throw new SecurityException("User not found");
            }

            if (!authService.isLoggedIn(userAccount)) {
                throw new SecurityException("User is not logged");
            }

            List<Branch> branches = branchService.getAllBranches();

            response.setSuccess(true);
            response.setMessage(gson.toJson(branches));
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

    public String getBranchByBranchId(String branchId) {
        Response response = new Response();

        try {
            Branch branch = branchService.getBranchById(branchId);

            if (branch != null) {
                Map<String, Object> branchData = new HashMap<>();
                branchData.put("id", branch.getId());
                branchData.put("name", branch.getName());


                List<Map<String, Object>> stockList = new ArrayList<>();

                for (Map.Entry<Product, Long> entry : branch.getProductsStock().getStockOfProducts().entrySet()) {
                    Product product = entry.getKey();
                    Long quantity = entry.getValue();

                    Map<String, Object> stockItem = new HashMap<>();
                    stockItem.put("sku", product.getSku());
                    stockItem.put("name", product.getName());
                    stockItem.put("price", product.getPrice());
                    stockItem.put("quantity", quantity);

                    stockList.add(stockItem);
                }
                branchData.put("stock", stockList);
                response.setSuccess(true);
                response.setMessage(gson.toJson(branchData));
            } else {
                response.setSuccess(false);
                response.setMessage("Branch not found");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return gson.toJson(response);
    }

    public String checkIfProductInStockInBranch(String branchId, String sku, Long quantity) {
        Response response = new Response();

        try {
            boolean inStock = branchService.checkIfProductInStockInBranch(branchId, sku, quantity);

            response.setSuccess(true);
            response.setMessage(gson.toJson(inStock));
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

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
