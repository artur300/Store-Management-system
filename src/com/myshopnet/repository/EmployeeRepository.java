package com.myshopnet.repository;

import com.myshopnet.auth.UserAccount;
import com.myshopnet.data.Data;
import com.myshopnet.models.Employee;
import com.myshopnet.models.User;
import com.myshopnet.service.AuthService;

import java.util.List;

public class EmployeeRepository implements Repository<Employee> {
    @Override
    public Employee create(Employee employee) {
        Data.getAllAccounts().get(employee.getId()).setUser(employee);

        return employee;
    }

    @Override
    public Employee update(String id, Employee employee) {
        Data.getAllAccounts().get(id).setUser(employee);

        return (Employee) Data.getAllAccounts().get(id).getUser();
    }

    @Override
    public void delete(String id) {
        Data.getAllAccounts().remove(id);
    }

    @Override
    public Employee get(String id) {
        return getAll().stream()
                .filter(employee -> employee.getId().equals(id))
                .toList().getFirst();
    }

    @Override
    public List<Employee> getAll() {
        return Data.getAllAccounts().values().stream()
                .map(userAccount -> (Employee) userAccount.getUser())
                .toList();
    }
}
