package com.myshopnet.repository;

import com.myshopnet.data.Data;
import com.myshopnet.models.Employee;
import com.myshopnet.models.User;

import java.util.List;

public class EmployeeRepository implements Repository<Employee> {
    @Override
    public Employee create(Employee employee) {
        return Data.getEmployees().put(employee.getId(), employee);
    }

    @Override
    public Employee update(String id, Employee employee) {
        Employee updatedEmployee = null;

        if(Data.getEmployees().containsKey(id)) {
            updatedEmployee = Data.getEmployees().put(id, employee);
        }

        return updatedEmployee;
    }

    @Override
    public void delete(String id) {
        Data.getEmployees().remove(id);
    }

    @Override
    public Employee get(String id) {
        return Data.getEmployees().get(id);
    }

    @Override
    public List<Employee> getAll() {
        return Data.getEmployees().values().stream().toList();
    }
}
