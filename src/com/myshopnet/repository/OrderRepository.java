package com.myshopnet.repository;


import com.myshopnet.data.Data;
import com.myshopnet.models.Employee;
import com.myshopnet.models.Order;

import java.util.List;

public class OrderRepository implements Repository<Order> {

    @Override
    public Order create(Order order) {
        return Data.getOrders().put(order.getId(), order);
    }

    @Override
    public Order update(String id, Order order) {
        Order updatedOrder = null;

        if (Data.getOrders().containsKey(id)) {
            updatedOrder = Data.getOrders().put(id, order);
        }

        return updatedOrder;
    }

    @Override
    public void delete(String id) {
        Data.getOrders().remove(id);
    }

    @Override
    public Order get(String id) {
        return Data.getOrders().get(id);
    }

    @Override
    public List<Order> getAll() {
        return Data.getOrders().values().stream().toList();
    }
}

