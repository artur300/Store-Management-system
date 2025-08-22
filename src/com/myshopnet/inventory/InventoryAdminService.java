package com.myshopnet.inventory;

import com.myshopnet.auth.AuthService;
import com.myshopnet.employees.Role;
import com.myshopnet.branches.Branch;
import com.myshopnet.logs.LogEvent;
import com.myshopnet.logs.LogType;
import com.myshopnet.logs.Logger;

public class InventoryAdminService {

    private final AuthService auth;

    public InventoryAdminService(AuthService auth) {
        this.auth = auth;
    }

    /** פעולה ניהולית: ריסטוק לסניף – מותר רק למנהל משמרת של אותו סניף */
    public void restockAuthorized(String token, Branch branch, String sku, int qty) {
        // מאשר שרק SHIFT_MANAGER של אותו סניף יכול
        auth.requireRoleInBranch(token, branch.getId(), Role.SHIFT_MANAGER);

        branch.getInventory().restock(sku, qty);

        Logger.getInstance().log(new LogEvent(
                LogType.RESTOCK, "branch=" + branch.getId() + ", sku=" + sku + ", qty=" + qty));
    }

    /** דוגמה: צפייה במלאי – מותר לכל עובד אבל רק בסניף שלו */
    public int getQtyAuthorized(String token, Branch branch, String sku) {
        auth.requireRoleInBranch(token, branch.getId(), Role.SHIFT_MANAGER, Role.CASHIER, Role.SELLER);
        return branch.getInventory().getQty(sku);
    }
}

