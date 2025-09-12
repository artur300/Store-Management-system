package com.myshopnet.utils;

import com.myshopnet.controller.ProductController;
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

        Employee employee1 = Singletons.EMPLOYEE_SERVICE.addEmployee(
                "royemployee1234_1",
                "admin1234",
                "roy dadon"
                , "0543535345",
                5211111111L,
                haifa.getId()
                , EmployeeType.SHIFT_MANAGER
                ,
                123L);

        Employee employee2 = Singletons.EMPLOYEE_SERVICE.addEmployee(
                "royemployee1234_2",
                "admin1234",
                "roy dadon"
                , "0543535345",
                5211111111L,
                tlv.getId()
                , EmployeeType.SHIFT_MANAGER
                ,
                123L);

        Employee employee3 = Singletons.EMPLOYEE_SERVICE.addEmployee(
                "royemployee1234_3",
                "admin1234",
                "roy dadon"
                , "0543535345",
                5211111111L,
                holon.getId()
                , EmployeeType.SHIFT_MANAGER
                ,
                123L);

        Singletons.AUTH_SERVICE.registerAccount(
                "admin",
                "admin1234",
                new Admin("1234",
                        "Admin",
                        "052-11111111",
                        1L,
                        holon.getId(),
                        EmployeeType.SHIFT_MANAGER,
                        123L));

        Singletons.PRODUCT_CONTROLLER.createProduct(
                "admin",
                "T-Shirt",
                "T-Shirt",
                "SHIRTS",
                "50");

    }

}
