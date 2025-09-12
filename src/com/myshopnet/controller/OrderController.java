package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.service.OrderService;
import com.myshopnet.models.Order;
import com.myshopnet.server.Response;
import com.myshopnet.utils.GsonSingleton;

import java.util.List;
import java.util.Map;

public class OrderController {
    private Gson gson = GsonSingleton.getInstance();
    private OrderService orderService = com.myshopnet.utils.Singletons.ORDER_SERVICE;

    public String performOrder(List<Map<String, String>> mapOfProductsQuantitiesBranches, String customerId) {
        Response response = new Response();

        try {
            Order order = orderService.performOrder(mapOfProductsQuantitiesBranches, customerId);

            response.setSuccess(true);
            response.setMessage(gson.toJson(order));
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to process order: " + e.getMessage());
        }

        return gson.toJson(response);
    }
}