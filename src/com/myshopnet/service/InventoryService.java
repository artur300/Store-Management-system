package com.myshopnet.service;

import com.myshopnet.models.inventory.Inventory;
import com.myshopnet.models.logs.LogEvent;
import com.myshopnet.models.logs.LogType;
import com.myshopnet.models.logs.Logger;

public class InventoryService {
    public void restock(Inventory inv, String branchId, String sku, int qty) {
        inv.restock(sku, qty);
        Logger.getInstance().log(new LogEvent(
                LogType.RESTOCK, "branch=" + branchId + ", sku=" + sku + ", qty=" + qty));
    }
    public void sell(Inventory inv, String branchId, String sku, int qty) {
        inv.sell(sku, qty);
        Logger.getInstance().log(new LogEvent(
                LogType.PURCHASE, "branch=" + branchId + ", sku=" + sku + ", qty=" + qty));
    }
}


