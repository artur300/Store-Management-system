package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.service.ProductService;
import com.myshopnet.service.UserAccountService;
import com.myshopnet.models.Admin;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.server.Response;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.utils.GsonSingleton;

public class ProductController {
    private Gson gson = GsonSingleton.getInstance();
    private ProductService productService = new ProductService();
    private UserAccountService userAccountService = new UserAccountService();


    public String addProductToAllBranches(String currentUserId, String productId) {
        Response response = new Response();

        try {
            UserAccount currentUserAccount = userAccountService.getUserAccount(currentUserId);
            if (currentUserAccount == null || !(currentUserAccount.getUser() instanceof Admin)) {
                response.setSuccess(false);
                response.setMessage("Only admin can add products to branches");
            } else {
                productService.addProductToAllBranches(productId);
                response.setSuccess(true);
                response.setMessage("Product added to all branches successfully");
            }
        } catch (EntityNotFoundException e) {
            response.setSuccess(false);
            response.setMessage("Failed to add product");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to add product to branches");
        }

        return gson.toJson(response);
    }


    public String removeProductFromAllBranches(String currentUserId, String productId) {
        Response response = new Response();

        try {
            UserAccount currentUserAccount = userAccountService.getUserAccount(currentUserId);
            if (currentUserAccount == null || !(currentUserAccount.getUser() instanceof Admin)) {
                response.setSuccess(false);
                response.setMessage("Only admin can remove products from branches");
            } else {
                productService.removeProductFromAllBranches(productId);
                response.setSuccess(true);
                response.setMessage("Product removed from all branches successfully");
            }
        } catch (EntityNotFoundException e) {
            response.setSuccess(false);
            response.setMessage("Failed to remove product");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to remove product from branches");
        }

        return gson.toJson(response);
    }


    public String updateProductInAllBranches(String currentUserId, String productId) {
        Response response = new Response();

        try {
            UserAccount currentUserAccount = userAccountService.getUserAccount(currentUserId);
            if (currentUserAccount == null || !(currentUserAccount.getUser() instanceof Admin)) {
                response.setSuccess(false);
                response.setMessage("Only admin can update products in branches");
            } else {
                productService.updateProductInAllBranches(productId);
                response.setSuccess(true);
                response.setMessage("Product updated in all branches successfully");
            }
        } catch (EntityNotFoundException e) {
            response.setSuccess(false);
            response.setMessage("Failed to update product");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to update product in branches");
        }

        return gson.toJson(response);
    }


    public String deleteProduct(String currentUserId, String productId) {
        Response response = new Response();

        try {
            UserAccount currentUserAccount = userAccountService.getUserAccount(currentUserId);
            if (currentUserAccount == null || !(currentUserAccount.getUser() instanceof Admin)) {
                response.setSuccess(false);
                response.setMessage("Only admin can delete products");
            } else {
                productService.deleteProduct(productId);
                response.setSuccess(true);
                response.setMessage("Product deleted successfully");
            }
        } catch (EntityNotFoundException e) {
            response.setSuccess(false);
            response.setMessage("Failed to delete product");
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to delete product");
        }

        return gson.toJson(response);
    }
}