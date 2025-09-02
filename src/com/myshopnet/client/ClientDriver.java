package com.myshopnet.client;
import com.myshopnet.models.DefaultBranches;
import com.myshopnet.models.Branch;
import com.myshopnet.repository.BranchRepository;
import com.myshopnet.models.Employee;
import com.myshopnet.service.AuthService;
import com.myshopnet.service.EmployeeService;
import com.myshopnet.models.Role;
import com.myshopnet.auth.*;

import java.util.List;
import java.util.Scanner;

public class ClientDriver {
    public static void main(String[] args) {
        Client client = new Client();

        client.run();
    }
}
