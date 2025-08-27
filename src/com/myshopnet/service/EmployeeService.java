package com.myshopnet.service;

import com.myshopnet.auth.UserAccount;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.models.Branch;
import com.myshopnet.models.Employee;
import com.myshopnet.models.EmployeeStatus;
import com.myshopnet.models.EmployeeType;
import com.myshopnet.repository.BranchRepository;
import com.myshopnet.repository.EmployeeRepository;

import java.util.*;

public class EmployeeService {
    private final EmployeeRepository employeeRepository = new EmployeeRepository();
    private final BranchRepository branchRepository = new BranchRepository();
    private final BranchService branchService = new BranchService();

    public Employee addEmployee(Long accountNumber, String branchId, String employeeType, Long employeeNumber) {
        Branch branch = branchRepository.get(branchId);

        if (branch == null) {
            throw new EntityNotFoundException("Branch not found");
        }

        Employee employee = new Employee(UUID.randomUUID().toString(),
                accountNumber, branchId, EmployeeType.valueOf(employeeType), employeeNumber);

        return employeeRepository.create(employee);
    }

    public void changeStatus(UserAccount userAccount, EmployeeStatus status) {
        // going from busy -> available
        if (((Employee)(userAccount.getUser())).getEmployeeStatus() == EmployeeStatus.BUSY &&
                status == EmployeeStatus.AVAILABLE) {
            branchService.notifyAndPollWaitingEmployeeToChat(userAccount);
        }

        ((Employee)(userAccount.getUser())).setEmployeeStatus(status);
    }

    public Employee get(String id) {
        return employeeRepository.get(id);
    }

    public List<Employee> getAll() {
        return employeeRepository.getAll();
    }
}

