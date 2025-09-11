package com.myshopnet.service;

import com.myshopnet.auth.UserAccount;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.models.Branch;
import com.myshopnet.models.Employee;
import com.myshopnet.models.EmployeeStatus;
import com.myshopnet.models.EmployeeType;
import com.myshopnet.repository.BranchRepository;
import com.myshopnet.repository.EmployeeRepository;
import com.myshopnet.repository.UserAccountRepository;
import com.myshopnet.utils.Singletons;

import java.util.*;

public class EmployeeService {
    private final EmployeeRepository employeeRepository = Singletons.EMPLOYEE_REPO;
    private final BranchRepository branchRepository = Singletons.BRANCH_REPO;
    private final UserAccountRepository userAccountRepository = Singletons.USER_ACCOUNT_REPO;
    private final BranchService branchService = Singletons.BRANCH_SERVICE;
    private final AuthService authService = Singletons.AUTH_SERVICE;

    public Employee addEmployee(String username, String password, Long accountNumber, String branchId, EmployeeType employeeType, Long employeeNumber) {
        Branch branch = branchRepository.get(branchId);

        if (branch == null) {
            throw new EntityNotFoundException("Branch not found");
        }

        Employee employee = new Employee(UUID.randomUUID().toString(),
                accountNumber, branchId, employeeType, employeeNumber);
        authService.registerAccount(username, password, employee);

        return employee;
    }

    public void changeStatus(UserAccount userAccount, EmployeeStatus status) {
        // going from busy -> available
        if (((Employee)(userAccount.getUser())).getEmployeeStatus() == EmployeeStatus.BUSY &&
                status == EmployeeStatus.AVAILABLE) {
            branchService.notifyAndPollWaitingEmployeeToChat(userAccount);
        }

        ((Employee)(userAccount.getUser())).setEmployeeStatus(status);
        userAccountRepository.update(userAccount.getUser().getUserId(), userAccount);
    }

    public Employee get(String id) {
        return employeeRepository.get(id);
    }

    public List<Employee> getAll() {
        return employeeRepository.getAll();
    }
}

