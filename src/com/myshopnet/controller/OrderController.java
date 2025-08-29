package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.service.OrderService;
import com.myshopnet.models.Order;
import com.myshopnet.server.Response;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.errors.StockException;
import com.myshopnet.utils.GsonSingleton;
import java.util.Map;

public class OrderController {
    private Gson gson = GsonSingleton.getInstance();
    private OrderService orderService = new OrderService();

    public String performOrder(Map<String, Long> mapOfProductsAndQuantities, String branchId, String customerId) {
        Response response = new Response();

        try {
            Order order = orderService.performOrder(mapOfProductsAndQuantities, branchId, customerId);

            response.setSuccess(true);
            response.setMessage(gson.toJson(order));
        } catch (EntityNotFoundException e) {
            response.setSuccess(false);
            response.setMessage("Order failed: " + e.getMessage());
        } catch (StockException e) {
            response.setSuccess(false);
            response.setMessage("Insufficient stock for: " + e.getMessage());
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to process order: " + e.getMessage());
        }

        return gson.toJson(response);
    }
}