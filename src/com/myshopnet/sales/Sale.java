package com.myshopnet.sales;

import com.myshopnet.inventory.Category;
import java.time.Instant;
import java.util.List;

public class Sale {
    public static final class Line {
        private final String sku;
        private final int qty;
        private final double unitPrice;
        private final Category category;

        public Line(String sku, int qty, double unitPrice, Category category) {
            this.sku = sku;
            this.qty = qty;
            this.unitPrice = unitPrice;
            this.category = category;
        }
        public String getSku() { return sku; }
        public int getQty() { return qty; }
        public double getUnitPrice() { return unitPrice; }
        public Category getCategory() { return category; }
        public double lineTotal() { return unitPrice * qty; }
    }

    private final String id;
    private final String branchId;
    private final String customerId;
    private final List<Line> lines;
    private final double baseTotal;
    private final double finalTotal;
    private final Instant timestamp;

    public Sale(String id, String branchId, String customerId,
                List<Line> lines, double baseTotal, double finalTotal) {
        this.id = id;
        this.branchId = branchId;
        this.customerId = customerId;
        this.lines = List.copyOf(lines);
        this.baseTotal = baseTotal;
        this.finalTotal = finalTotal;
        this.timestamp = Instant.now();
    }

    public String getId() { return id; }
    public String getBranchId() { return branchId; }
    public String getCustomerId() { return customerId; }
    public List<Line> getLines() { return lines; }
    public double getBaseTotal() { return baseTotal; }
    public double getFinalTotal() { return finalTotal; }
    public Instant getTimestamp() { return timestamp; }
}

