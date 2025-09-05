package com.myshopnet.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.myshopnet.controller.*;
import com.myshopnet.utils.GsonSingleton;

import java.util.HashMap;
import java.util.Map;

public class RequestHandler {
    private static final Gson gson = GsonSingleton.getInstance();

    private static final AuthController authController = new AuthController();
    private static final BranchController branchController = new BranchController();
    private static final CustomerController customerController = new CustomerController();
    private static final EmployeeController employeeController = new EmployeeController();
    private static final OrderController orderController = new OrderController();
    private static final ProductController productController = new ProductController();
    private static final UserAccountController userAccountController = new UserAccountController();
    private static final ChatController chatController = new ChatController();

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
                }

                case "register":{
                    String username = json.get("username").getAsString();
                    String password = json.get("password").getAsString();
                    String userId = json.get("userId").getAsString();

                    response = authController.register(username,password, userId);
                }

                case "logout": {
                    String userId = json.get("userId").getAsString();

                    response = authController.logout(userId);
                }

                case "getAllUserAccounts": {
                    String userId = json.get("userId").getAsString();

                    response = userAccountController.getAllUserAccounts(userId);
                }

                case "getAllBranches": {
                    String userId = json.get("userId").getAsString();

                    response = branchController.getAllBranches(userId);
                }

                case "getBranchByBranchId": {
                    String branchId = json.get("branchId").getAsString();

                    response = branchController.getBranchByBranchId(branchId);
                }

                case "createBranch": {
                    String userId = json.get("userId").getAsString();
                    String branchName = json.get("branchName").getAsString();

                    response = branchController.createBranch(userId, branchName);
                }

                case "updateBranchStock": {
                    String branchId = json.get("branchId").getAsString();
                    String userId = json.get("userId").getAsString();
                    String productId = json.get("productId").getAsString();
                    Long stock = json.get("stock").getAsLong();

                    response = branchController.updateBranchStock(branchId, userId, productId, stock);
                }

                case "createCustomer": {
                    String fullName = json.get("fullName").getAsString();
                    String passportId = json.get("passportId").getAsString();
                    String phoneNumber = json.get("phoneNumber").getAsString();
                    response = customerController.createCustomer(fullName, passportId, phoneNumber);
                }

                case "getCustomer": {
                    String customerId = json.get("customerId").getAsString();

                    response = customerController.getCustomer(customerId);
                }

                case "getAllCustomers": {
                    response = customerController.getAllCustomers();
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
                }

                case "getEmployee": {
                    String employeeId = json.get("employeeId").getAsString();

                    response = employeeController.getEmployee(employeeId);
                }

                case "getAllEmployeesByBranch": {
                    String branchId = json.get("branchId").getAsString();

                    response = employeeController.getAllEmployeesByBranch(branchId);
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
                }

                case "updatePasswordPolicy": {
                    String userId = json.get("userId").getAsString();
                    Integer minChars = json.get("minChars").getAsInt();
                    Integer maxChars = json.get("maxChars").getAsInt();
                    Integer minNumbers = json.get("minNumbers").getAsInt();
                    Integer maxNumbers = json.get("maxNumbers").getAsInt();
                    boolean hasSpecialCharacters = json.get("hasSpecialCharacters").getAsBoolean();

                    response = authController.updatePasswordPolicy(userId, minChars, maxChars, minNumbers, maxNumbers, hasSpecialCharacters);
                }

                case "resetUserPassword": {
                    String userId = json.get("userId").getAsString();
                    String username = json.get("username").getAsString();
                    String newPassword = json.get("newPassword").getAsString();

                    response = authController.resetPassword(userId, username, newPassword);
                }

                case "viewPasswordPolicy": {
                    String userId = json.get("userId").getAsString();

                    response = authController.viewPasswordPolicy(userId);
                }

                case "startChat": {
                    String userIdRequesting = json.get("userIdRequesting").getAsString();
                    String branchId = json.get("branchId").getAsString();

                    response = chatController.startChat(userIdRequesting, branchId);
                }

                case "endChat": {
                    String userId = json.get("userId").getAsString();
                    String chatId = json.get("chatId").getAsString();

                    response = chatController.endChat(userId, chatId);
                }

                case "createProduct": {
                    String userId = json.get("userId").getAsString();
                    String productSku = json.get("productSku").getAsString();
                    String productName = json.get("productName").getAsString();
                    String productCategory = json.get("productCategory").getAsString();
                    String price = json.get("price").getAsString();

                    response = productController.createProduct(userId, productSku, productName, productCategory, price);
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
