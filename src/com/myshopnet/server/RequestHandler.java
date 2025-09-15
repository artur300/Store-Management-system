package com.myshopnet.server;

import com.google.gson.*;
import com.myshopnet.controller.*;
import com.myshopnet.logs.LogEvent;
import com.myshopnet.logs.LogType;
import com.myshopnet.models.Chat;
import com.myshopnet.models.ChatMessage;
import com.myshopnet.models.Employee;
import com.myshopnet.utils.GsonSingleton;
import com.myshopnet.utils.Singletons;

import java.io.OutputStream;
import java.io.PrintWriter;
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

    public static void handleMessage(ChatMessage message) {
        chatController.sendMessage(message);
    }

    public static String handleChat(Request request, PrintWriter out) {
        String response = "";
        String action = request.getAction();
        String data = request.getData();

        try {
            JsonObject json = null;

            if (!action.equals("chatMember")) {
                json = JsonParser.parseString(data).getAsJsonObject();
            }

            switch (request.getAction()) {
                case "chatStart": {
                    String userIdRequesting = json.get("userIdRequesting").getAsString();
                    String branchId = json.get("branchId").getAsString();

                    response = chatController.startChat(userIdRequesting, branchId, out);
                    break;
                }

                case "chatInitiate": {
                    String chatId = json.get("chatId").getAsString();

                    response = chatController.initiateChat(chatId);
                    break;
                }

                case "chatDeclined": {
                    String fromUser = json.get("fromUser").getAsString();
                    String chatId = json.get("chatId").getAsString();

                    response = chatController.endChat(fromUser, chatId);
                    break;
                }

                case "chatMember": {
                    String username = data;

                    Server.getAllPrintWriters().put(username, out);
                    Singletons.LOGGER.log(new LogEvent(LogType.REQUEST_RECIEVED, "User " + username + " added to chat listeners"));
                    break;
                }
                case "chatSendMessage":
                    ChatMessage chatMessage = gson.fromJson(data, ChatMessage.class);

                    chatController.sendMessage(chatMessage);
                    break;
                case "leaveChat":

                    break;
                case "chatEnd": {
                    String userId = json.get("userId").getAsString();
                    String chatId = json.get("chatId").getAsString();

                    response = chatController.endChat(userId, chatId);
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
        } catch (Exception e) {
            return gson.toJson(new Response(false, "Error: " + e.getMessage()));
        }
    }
}
