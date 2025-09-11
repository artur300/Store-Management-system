package com.myshopnet.utils;

import com.myshopnet.models.Admin;
import com.myshopnet.models.Branch;
import com.myshopnet.models.Employee;
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

        Employee employee1 = Singletons.EMPLOYEE_SERVICE.addEmployee("royemployee1234", "admin1234",
       12345L, tlv.getId(), EmployeeType.CASHIER, 1234L);

        Employee employee2 = Singletons.EMPLOYEE_SERVICE.addEmployee("royemployee1234_2", "admin1234",
                123457L, haifa.getId(), EmployeeType.SELLER, 1234L);

        Employee employee3 = Singletons.EMPLOYEE_SERVICE.addEmployee("royemployee1234_3", "admin1234",
                123457L, holon.getId(), EmployeeType.SELLER, 1234L);

        Singletons.AUTH_SERVICE.registerAccount("admin", "admin1234",
                new Admin("1234", 1L, holon.getId(), EmployeeType.SHIFT_MANAGER, 123L));
    }
}
