package com.myshopnet.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.myshopnet.controller.*;
import com.myshopnet.utils.GsonSingleton;
import com.myshopnet.utils.Singletons;

import java.util.HashMap;
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

                    response = authController.login(username,password);
                    break;
                }

                case "register":{
                    String username = json.get("username").getAsString();
                    String password = json.get("password").getAsString();
                    String userId = json.get("userId").getAsString();

                    response = authController.register(username,password, userId);
                    break;
                }

                case "logout": {
                    String userId = json.get("userId").getAsString();

                    response = authController.logout(userId);
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
                    String fullName = json.get("fullName").getAsString();
                    String passportId = json.get("passportId").getAsString();
                    String phoneNumber = json.get("phoneNumber").getAsString();
                    response = customerController.createCustomer(fullName, passportId, phoneNumber);
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

                    response = employeeController.addEmployee(currentUserId, accountNumber, branchId,employeeType, employeeNumber, username, password);
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

                case "performOrder": {
                    String branchId = json.get("branchId").getAsString();
                    String customerId = json.get("customerId").getAsString();
                    JsonObject productsJson = json.getAsJsonObject("products");
                    Map<String, Long> productsMap = new HashMap<>();

                    for (String key : productsJson.keySet()) {
                        productsMap.put(key, productsJson.get(key).getAsLong());
                    }

                    response = orderController.performOrder(productsMap, branchId, customerId);
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

                default:
                    response = gson.toJson(new Response(false, "Unknown action: " + action));
            }

            return response;
        }
        catch (Exception e) {
            return gson.toJson(new Response(false, "Error: " + e.getMessage()));
        }
    }
}
