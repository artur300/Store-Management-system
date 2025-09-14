package com.myshopnet.utils;

import com.myshopnet.controller.ProductController;
import com.myshopnet.models.*;

public class
PopulateUsers {
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

        Customer customer1 = Singletons.CUSTOMER_SERVICE.createCustomer(
                "customer1",
                "admin1234",
                "roy dadon",
                "23141412",
                "4324232304234"
        );

        Employee employee1 = Singletons.EMPLOYEE_SERVICE.addEmployee(
                "employee1",
                "admin1234",
                "employee1"
                , "0543535345",
                5211111111L,
                haifa.getId()
                , EmployeeType.SELLER
                ,
                123L);

        Employee employee2 = Singletons.EMPLOYEE_SERVICE.addEmployee(
                "employee2",
                "admin1234",
                "employee2"
                , "0543535345",
                5211111111L,
                tlv.getId()
                , EmployeeType.CASHIER
                ,
                123L);

        Employee employee3 = Singletons.EMPLOYEE_SERVICE.addEmployee(
                "employee3",
                "admin1234",
                "employee3"
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
