package com.myshopnet.utils;

import com.myshopnet.models.Admin;
import com.myshopnet.models.Branch;
import com.myshopnet.models.EmployeeType;

public class PopulateUsers {
    public static void populate() {
        Branch tlv = Singletons.BRANCH_SERVICE.createNewBranch(
                "TLV"
        );
        Branch haifa = Singletons.BRANCH_SERVICE.createNewBranch(
                "HAIFA"
        );
        Branch holon = Singletons.BRANCH_SERVICE.createNewBranch(
                "HOLON"
        );


        Singletons.AUTH_SERVICE.registerAccount("admin", "admin1234",
                new Admin("1234", 1L, holon.getId(), EmployeeType.SHIFT_MANAGER, 123L));
    }
}
