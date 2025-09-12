package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.models.Category;
import com.myshopnet.models.Product;
import com.myshopnet.models.User;
import com.myshopnet.service.ProductService;
import com.myshopnet.service.UserAccountService;
import com.myshopnet.models.Admin;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.server.Response;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.utils.GsonSingleton;
import com.myshopnet.utils.Singletons;

import java.util.List;

public class ProductController {
    private Gson gson = GsonSingleton.getInstance();
    private ProductService productService = Singletons.PRODUCT_SERVICE;
    private UserAccountService userAccountService = Singletons.USER_ACCOUNT_SERVICE;

    public String createProduct(String currentLoggedInUser,String productSku, String productName, String productCategory, String price) {
        Response response = new Response();

        try {
            User user = userAccountService.getUserAccount(currentLoggedInUser).getUser();

            if (!(user instanceof Admin)) {
                throw new SecurityException("User is not an Admin");
            }

            Category category = Category.valueOf(productCategory);
            Double priceDouble = Double.valueOf(price);
            Product product = productService.createProduct(productSku, productName, category, priceDouble);

            addProductToAllBranches(currentLoggedInUser, product.getSku());

            response.setSuccess(true);
            response.setMessage(gson.toJson(product));
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

    public String getAllProducts() {
        Response response = new Response();

        try {
            List<Product> products = productService.getAllProducts();

            response.setSuccess(true);
            response.setMessage(gson.toJson(products));
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

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