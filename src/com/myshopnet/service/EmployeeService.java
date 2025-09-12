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

    public Employee addEmployee(String username, String password,
            String fullName, String phoneNumber, Long accountNumber, String branchId, EmployeeType employeeType, Long employeeNumber) {
        Branch branch = branchRepository.get(branchId);

        if (branch == null) {
            throw new EntityNotFoundException("Branch not found");
        }

        Employee employee = new Employee(UUID.randomUUID().toString(), fullName,
                phoneNumber , accountNumber, branchId, employeeType, employeeNumber);

        employee.registerObserver(branchService);
        authService.registerAccount(username, password, employee);

        return employee;
    }

    public void changeStatus(UserAccount userAccount, EmployeeStatus status) {
        Employee emp = (Employee) userAccount.getUser();
        emp.setEmployeeStatus(status);
        userAccountRepository.update(userAccount.getUser().getUserId(), userAccount);
    }

    public Employee get(String id) {
        return employeeRepository.get(id);
    }

    public List<Employee> getAll() {
        return employeeRepository.getAll();
    }

    public Employee update(String id, Employee updated)
    {
        return employeeRepository.update(id, updated);
    }

    public void delete(String id){ employeeRepository.delete(id); }
}

