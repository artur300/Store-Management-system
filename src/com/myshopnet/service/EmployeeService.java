package com.myshopnet.service;

import com.myshopnet.errors.EntityAlreadyExistsException;
import com.myshopnet.models.Employee;
import com.myshopnet.logs.*;
import com.myshopnet.repository.EmployeeRepository;

import java.util.*;
import java.util.stream.Collectors;

public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee addEmployee(Employee e) {
        if (employeeRepository.get(e.getId()) != null)
        {
            LoggerImpl.getInstance().log(new LogEvent(
                    LogType.EMPLOYEE_REGISTERED, "employeeId=" + e.getId() +
                    ", branch=" + e.getBranchId()));
            throw new EntityAlreadyExistsException(Employee.class.getName());
        }
        else {
           return employeeRepository.create(e);
        }
    }

    public Employee get(String id) {
        return employeeRepository.get(id);
    }

    public List<Employee> listByBranch(String branchId) {
        return employeeRepository.getAll().stream()
                .filter(e -> e.getBranchId().equals(branchId))
                .collect(Collectors.toList());
    }

    public List<Employee> getAll() {
        return employeeRepository.getAll();
    }
}

