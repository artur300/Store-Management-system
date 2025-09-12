package com.myshopnet.repository;

import com.myshopnet.auth.UserAccount;
import com.myshopnet.data.Data;
import com.myshopnet.models.Employee;

import java.util.List;

public class EmployeeRepository implements Repository<Employee> {
    @Override
    public Employee create(Employee employee) {
        UserAccount employeeUserAccount = new UserAccount(employee);
        Data.getAllAccounts().put(employee.getUserId(), employeeUserAccount);

        return employee;
    }

    @Override
    public Employee update(String id, Employee employee) {
        UserAccount account = Data.getAllAccounts().get(id);
        if (account == null){
            account = Data.getAllAccounts().values().stream()
                    .filter(ua ->ua.getUser() != null && ua.getUser().getUserId().equals(id))
                    .findFirst()
                    .orElse(null);
        }

        if (account == null){
            throw new IllegalArgumentException("Employee account not found for id: " + id);
        }

        account.setUser(employee);
        return (Employee) account.getUser();
    }

    @Override
    public void delete(String id) {
        String keyToRemove = Data.getAllAccounts().entrySet().stream()
                .filter(e -> e.getValue().getUser() != null && e.getValue().getUser().getUserId().equals(id))
                .map(e-> e.getKey())
                .findFirst()
                .orElse(null);

        if (keyToRemove != null){
            Data.getAllAccounts().remove(keyToRemove);
        } else { // if it's id
            Data.getAllAccounts().remove(id);
        }
    }

    @Override
    public Employee get(String id) {
        return getAll().stream()
                .filter(employee -> employee.getUserId().equals(id))
                .toList().getFirst();
    }

    @Override
    public List<Employee> getAll() {
        return Data.getAllAccounts().values().stream()
                .map(userAccount -> (Employee) userAccount.getUser())
                .toList();
    }
}
