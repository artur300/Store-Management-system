package com.myshopnet.service;

import com.myshopnet.models.Product;
import com.myshopnet.logs.LogEvent;
import com.myshopnet.logs.LogType;
import com.myshopnet.logs.Logger;
import com.myshopnet.repository.OrderRepository;

import java.util.ArrayList;
import java.util.UUID;

public class OrderService {
    private final OrderRepository repo;

    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }

    public double performOrder() {
        double base = 0.0;
        List<Sale.Line> lines = new ArrayList<>();

        // לבנות שורות כולל קטגוריה
        for (CartItem it : cart.items()) {
            Product p = branch.getInventory().getProduct(it.getSku());
            double unitPrice = p.getPrice();
            base += unitPrice * it.getQty();
            lines.add(new Sale.Line(it.getSku(), it.getQty(), unitPrice, p.getCategory()));
        }

        // הורדת מלאי
        for (CartItem it : cart.items()) {
            branch.getInventory().sell(it.getSku(), it.getQty());
        }

        double finalPrice = customer.calcBuyingStrategy(base);

        // לשמור מכירה
        String saleId = UUID.randomUUID().toString();
        repo.add(new Sale(saleId, branch.getId(), customer.getId(), lines, base, finalPrice));

        // לוג
        Logger.getInstance().log(new LogEvent(
                LogType.SALE, "branch=" + branch.getId() + ", customer=" + customer.getId()
                + ", base=" + base + ", final=" + finalPrice + ", items=" + cart.items().size()));

        return finalPrice;
    }
}
