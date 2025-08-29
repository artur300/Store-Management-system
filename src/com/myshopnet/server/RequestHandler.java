package com.myshopnet.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.myshopnet.controller.*;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.errors.StockException;
import com.myshopnet.models.*;
import com.myshopnet.utils.GsonSingleton;

import java.util.HashMap;
import java.util.Map;

public class RequestHandler {
    private final Gson gson = GsonSingleton.getInstance();

    private final AuthController authController = new AuthController();
    private final BranchController branchController = new BranchController();
    private final CustomerController customerController = new CustomerController();
    private final EmployeeController employeeController = new EmployeeController();
    private final OrderController orderController = new OrderController();
    private final ProductController productController = new ProductController();
    private final ChatController chatController = new ChatController();

    public String HandleRequest(Request request){
        String action = request.getAction();
        String data = request.getData();

        try {
            JsonObject json = data != null && !data.isEmpty()?
                    gson.fromJson(data, JsonObject.class)
                    : new JsonObject();

            switch (action){

                //Auth:
                case "login":{
                    String username = json.get("username").getAsString();
                    String password = json.get("password").getAsString();
                    return authController.login(username,password);
                }

                case "register":{
                    String username = json.get("username").getAsString();
                    String password = json.get("password").getAsString();
                    String type = json.get("type").getAsString();

                    User user;
                    switch (type) {
                        case "Admin":
                            user = gson.fromJson(json.get("user"), Admin.class);
                            break;
                        case "Employee":
                            user = gson.fromJson(json.get("user"), Employee.class);
                            break;
                        case "Customer":
                        default:
                            user = gson.fromJson(json.get("user"), Customer.class);
                            break;
                    }
                    return authController.register(username,password,user);
                }

                case "logout":{
                    String userId = json.get("userId").getAsString();
                    return authController.logout(userId);
                }



                //Branch:
                case "createBranch":{
                    String userId = json.get("userId").getAsString();
                    String branchName = json.get("branchName").getAsString();
                    return branchController.createBranch(userId,branchName);
                }

                case "updateBranchStock":{
                    String branchId = json.get("branchId").getAsString();
                    String userId = json.get("userId").getAsString();
                    String productId = json.get("productId").getAsString();
                    Long stock = json.get("stock").getAsLong();
                    return branchController.updateBranchStock(branchId, userId, productId, stock);
                }

                //Customer:
                case "createCustomer":{
                    String fullName = json.get("fullName").getAsString();
                    String passportId = json.get("passportId").getAsString();
                    String phoneNumber = json.get("phoneNumber").getAsString();
                    return customerController.createCustomer(fullName, passportId, phoneNumber);
                }

                case "getCustomer":{
                    String customerId = json.get("customerId").getAsString();
                    return customerController.getCustomer(customerId);
                }

                case "getAllCustomers":{
                    return customerController.getAllCustomers();
                }


                //Employee:
                case "addEmployee": {
                    String currentUserId = json.get("currentUserId").getAsString();
                    Long accountNumber = json.get("accountNumber").getAsLong();
                    String branchId = json.get("branchId").getAsString();
                    String employeeType = json.get("employeeType").getAsString();
                    Long employeeNumber = json.get("employeeNumber").getAsLong();
                    String username = json.get("username").getAsString();
                    String password = json.get("password").getAsString();
                    return employeeController.addEmployee(currentUserId, accountNumber, branchId,employeeType, employeeNumber, username, password);
                }

                case "getEmployee":{
                    String employeeId = json.get("employeeId").getAsString();
                    return employeeController.getEmployee(employeeId);
                }

                case "getAllEmployeesByBranch":{
                    String branchId = json.get("branchId").getAsString();
                    return employeeController.getAllEmployeesByBranch(branchId);
                }


                //Order:
                case "performOrder": {
                    String branchId = json.get("branchId").getAsString();
                    String customerId = json.get("customerId").getAsString();
                    JsonObject productsJson = json.getAsJsonObject("products");
                    Map<String, Long> productsMap = new HashMap<>();
                    for (String key : productsJson.keySet()) {
                        productsMap.put(key, productsJson.get(key).getAsLong());
                    }

                    return orderController.performOrder(productsMap, branchId, customerId);
                }


                //Chat:
                case "startChat":{
                    String userIdRequesting = json.get("userIdRequesting").getAsString();
                    String branchId = json.get("branchId").getAsString();
                    return chatController.startChat(userIdRequesting, branchId);
                }

                //Default:
                default:
                    return gson.toJson(new Response(false, "Unknown action: " + action));
            }

        } catch (Exception e){
            return gson.toJson(new Response(false, "Error: " + e.getMessage()));
        }
    }
}
