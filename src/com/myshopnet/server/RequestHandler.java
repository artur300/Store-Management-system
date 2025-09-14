package com.myshopnet.server;

import com.google.gson.*;
import com.myshopnet.controller.*;
import com.myshopnet.models.Employee;
import com.myshopnet.utils.GsonSingleton;
import com.myshopnet.utils.Singletons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHandler {
    private static final Gson gson = GsonSingleton.getInstance();

    private static final AuthController authController = Singletons.AUTH_CONTROLLER;
    private static final BranchController branchController = Singletons.BRANCH_CONTROLLER;
    private static final CustomerController customerController = Singletons.CUSTOMER_CONTROLLER;
    private static final EmployeeController employeeController = Singletons.EMPLOYEE_CONTROLLER;
    private static final OrderController orderController = Singletons.ORDER_CONTROLLER;
    private static final ProductController productController = Singletons.PRODUCT_CONTROLLER;
    private static final UserAccountController userAccountController = Singletons.USER_ACCOUNT_CONTROLLER;
    private static final ChatController chatController = Singletons.CHAT_CONTROLLER;

    public static String handleRequest(Request request) {
        String response = "";
        String action = request.getAction();
        String data = request.getData();

        try {
            JsonObject json = data != null && !data.isEmpty() ? gson.fromJson(data, JsonObject.class) : new JsonObject();

            switch (action) {
                case "login": {
                    String username = json.get("username").getAsString();
                    String password = json.get("password").getAsString();

                    response = authController.login(username, password);
                    break;
                }

                case "register": {
                    String username = json.get("username").getAsString();
                    String password = json.get("password").getAsString();
                    String userId = json.get("userId").getAsString();

                    response = authController.register(username, password, userId);
                    break;
                }

                case "logout": {
                    String username = json.get("username").getAsString();

                    response = authController.logout(username);
                    break;
                }

                case "getAllUserAccounts": {
                    String userId = json.get("userId").getAsString();

                    response = userAccountController.getAllUserAccounts(userId);
                    break;
                }

                case "getAllBranches": {
                    String userId = json.get("userId").getAsString();

                    response = branchController.getAllBranches(userId);
                    break;
                }

                case "getBranchByBranchId": {
                    String branchId = json.get("branchId").getAsString();

                    response = branchController.getBranchByBranchId(branchId);
                    break;
                }

                case "createBranch": {
                    String userId = json.get("userId").getAsString();
                    String branchName = json.get("branchName").getAsString();

                    response = branchController.createBranch(userId, branchName);
                    break;
                }

                case "updateBranchStock": {
                    String branchId = json.get("branchId").getAsString();
                    String userId = json.get("userId").getAsString();
                    String productId = json.get("productId").getAsString();
                    Long stock = json.get("stock").getAsLong();

                    response = branchController.updateBranchStock(branchId, userId, productId, stock);
                    break;
                }

                case "createCustomer": {
                    String username = json.get("username").getAsString();
                    String password = json.get("password").getAsString();
                    String fullName = json.get("fullName").getAsString();
                    String passportId = json.get("passportId").getAsString();
                    String phoneNumber = json.get("phoneNumber").getAsString();

                    response = customerController.createCustomer(username, password, fullName, passportId, phoneNumber);
                    break;
                }

                case "deleteCustomer": {
                    String currentUserId = json.get("userId").getAsString();
                    String username = json.get("username").getAsString();

                    response = customerController.deleteCustomer(currentUserId, username);
                    break;
                }

                case "updateCustomer": {
                    String currentUserId = json.get("userId").getAsString();
                    String username = json.get("username").getAsString();
                    String fullName = json.get("fullName").getAsString();
                    String passportId = json.get("passportId").getAsString();
                    String phoneNumber = json.get("phoneNumber").getAsString();

                    response = customerController.updateCustomer(currentUserId, username, fullName, passportId, phoneNumber);
                    break;
                }

                case "getCustomer": {
                    String customerId = json.get("customerId").getAsString();

                    response = customerController.getCustomer(customerId);
                    break;
                }

                case "getAllCustomers": {
                    response = customerController.getAllCustomers();
                    break;
                }

                case "addEmployee": {
                    String currentUserId = json.get("currentUserId").getAsString();
                    Long accountNumber = json.get("accountNumber").getAsLong();
                    String branchId = json.get("branchId").getAsString();
                    String employeeType = json.get("employeeType").getAsString();
                    Long employeeNumber = json.get("employeeNumber").getAsLong();
                    String username = json.get("username").getAsString();
                    String password = json.get("password").getAsString();
                    String fullName = json.get("fullName").getAsString();
                    String phoneNumber = json.get("phoneNumber").getAsString();

                    response = employeeController.addEmployee(currentUserId, fullName, phoneNumber, accountNumber, branchId, employeeType, employeeNumber, username, password);
                    break;
                }

                case "getAllEmployees": {
                    String currentUserId = json.get("currentUserId").getAsString();

                    response = employeeController.getAllEmployees(currentUserId);
                    break;
                }

                case "updateEmployee": {
                    String currentUserId = json.get("currentUserId").getAsString();
                    Employee updated = gson.fromJson(json.get("employee").getAsJsonObject(), Employee.class);

                    response = employeeController.updateEmployee(currentUserId, updated);
                    break;
                }

                case "deleteEmployee": {
                    String currentUserId = json.get("currentUserId").getAsString();
                    String employeeId = json.get("employeeId").getAsString();

                    response = employeeController.deleteEmployee(currentUserId, employeeId);
                    break;
                }

                case "getEmployee": {
                    String employeeId = json.get("employeeId").getAsString();

                    response = employeeController.getEmployee(employeeId);
                    break;
                }

                case "getAllEmployeesByBranch": {
                    String branchId = json.get("branchId").getAsString();

                    response = employeeController.getAllEmployeesByBranch(branchId);
                    break;
                }

                case "checkIfProductInStockInBranch": {
                    String sku = json.get("sku").getAsString();
                    Long quantity = json.get("quantity").getAsLong();
                    String branchId = json.get("branchId").getAsString();

                    response = branchController.checkIfProductInStockInBranch(branchId, sku, quantity);
                    break;
                }

                case "performOrder": {
                    String customerId = json.get("customerId").getAsString();
                    JsonArray productsJson = JsonParser.parseString(json.get("products").getAsString()).getAsJsonArray();
                    List<Map<String, String>> productsStockBranches = new ArrayList<>();

                    for (JsonElement el : productsJson) {
                        Map<String, String> productStockBranch = new HashMap<>();

                        JsonObject product = el.getAsJsonObject();
                        String productSku = product.get("sku").getAsString();
                        String branchId = product.get("branchId").getAsString();
                        String quantity = product.get("quantity").getAsString();

                        productStockBranch.put("productSku", productSku);
                        productStockBranch.put("branchId", branchId);
                        productStockBranch.put("quantity", quantity);

                        productsStockBranches.add(productStockBranch);
                    }

                    response = orderController.performOrder(productsStockBranches, customerId);
                    break;
                }

                case "getAllProducts": {
                    response = productController.getAllProducts();
                    break;
                }

                case "updatePasswordPolicy": {
                    String userId = json.get("userId").getAsString();
                    Integer minChars = json.get("minChars").getAsInt();
                    Integer maxChars = json.get("maxChars").getAsInt();
                    Integer minNumbers = json.get("minNumbers").getAsInt();
                    Integer maxNumbers = json.get("maxNumbers").getAsInt();
                    boolean hasSpecialCharacters = json.get("hasSpecialCharacters").getAsBoolean();

                    response = authController.updatePasswordPolicy(userId, minChars, maxChars, minNumbers, maxNumbers, hasSpecialCharacters);
                    break;
                }

                case "resetUserPassword": {
                    String userId = json.get("userId").getAsString();
                    String username = json.get("username").getAsString();
                    String newPassword = json.get("newPassword").getAsString();

                    response = authController.resetPassword(userId, username, newPassword);
                    break;
                }

                case "viewPasswordPolicy": {
                    String userId = json.get("userId").getAsString();

                    response = authController.viewPasswordPolicy(userId);
                    break;
                }

                case "viewCustomerPlan": {
                    String userId = json.get("userId").getAsString();

                    response = customerController.getCustomerPlan(userId);
                    break;
                }

                case "startChat": {
                    String userIdRequesting = json.get("userIdRequesting").getAsString();
                    String branchId = json.get("branchId").getAsString();

                    response = chatController.startChat(userIdRequesting, branchId);
                    break;
                }

                case "endChat": {
                    String userId = json.get("userId").getAsString();
                    String chatId = json.get("chatId").getAsString();

                    response = chatController.endChat(userId, chatId);
                    break;
                }

                case "createProduct": {
                    String userId = json.get("userId").getAsString();
                    String productSku = json.get("productSku").getAsString();
                    String productName = json.get("productName").getAsString();
                    String productCategory = json.get("productCategory").getAsString();
                    String price = json.get("price").getAsString();

                    response = productController.createProduct(userId, productSku, productName, productCategory, price);
                    break;
                }

                case "updateEmployeeStatus": {
                    System.out.println("===== [DEBUG] Entered updateEmployeeStatus case =====");
                    System.out.println("[DEBUG] Raw JSON: " + json);

                    try {
                        String username = json.has("username") ? json.get("username").getAsString() : null;
                        String userId   = json.has("userId")   ? json.get("userId").getAsString()   : null;
                        String status   = json.has("status")   ? json.get("status").getAsString()   : null;

                        System.out.println("[DEBUG] Parsed input -> username=" + username + ", userId=" + userId + ", status=" + status);

                        if ((username == null && userId == null) || status == null) {
                            response = gson.toJson(new Response(false, "Missing username/userId or status in request"));
                            System.out.println("[ERROR] Missing data. Response=" + response);
                            break;
                        }

                        // שליפת החשבון לפי מה שיש
                        var userAccount = (username != null)
                                ? Singletons.USER_ACCOUNT_REPO.getByUsername(username)
                                : Singletons.USER_ACCOUNT_REPO.getByUserId(userId);

                        System.out.println("[DEBUG] userAccount from repo = " + userAccount);

                        if (userAccount == null) {
                            response = gson.toJson(new Response(false, "User not found"));
                            System.out.println("[ERROR] User not found. Response=" + response);
                            break;
                        }

                        com.myshopnet.models.EmployeeStatus newStatus =
                                com.myshopnet.models.EmployeeStatus.valueOf(status);

                        System.out.println("[DEBUG] Converting status to enum: " + newStatus);

                        Singletons.EMPLOYEE_SERVICE.changeStatus(userAccount, newStatus);
                        System.out.println("[DEBUG] Status changed successfully");

                        response = gson.toJson(new Response(true, "Status updated to " + status));
                        System.out.println("[DEBUG] Final Response=" + response);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        response = gson.toJson(new Response(false, "Failed to update status: " + ex.getMessage()));
                        System.out.println("[DEBUG] Response after exception=" + response);
                    }

                    System.out.println("===== [DEBUG] Exiting updateEmployeeStatus case =====");
                    break;
                }


                case "sendMessage": {
                    String chatId = json.get("chatId").getAsString();
                    String senderId = json.get("senderId").getAsString();
                    String message = json.get("message").getAsString();

                    response = chatController.sendMessage(chatId, senderId, message);
                    break;
                }


                default:
                    response = gson.toJson(new Response(false, "Unknown action: " + action));
            }

            return response;
        } catch (Exception e) {
            return gson.toJson(new Response(false, "Error: " + e.getMessage()));
        }
    }
}
