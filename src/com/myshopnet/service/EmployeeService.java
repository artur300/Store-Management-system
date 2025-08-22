package com.myshopnet.service;

import com.myshopnet.models.employees.Employee;
import com.myshopnet.models.logs.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EmployeeService {
    private final Map<String, Employee> byId = new ConcurrentHashMap<>();

    /** יצירה בלבד; ייכשל אם כבר קיים */
    public void add(Employee e) {
        if (byId.putIfAbsent(e.getId(), e) != null)
            throw new IllegalArgumentException("Employee exists: " + e.getId());
        Logger.getInstance().log(new LogEvent(
                LogType.EMPLOYEE_REGISTERED, "employeeId=" + e.getId() +
                ", branch=" + e.getBranchId() + ", role=" + e.getRole()));
    }

    /** יצירה או החלפה (לעריכה) */
    public void upsert(Employee e) {
        byId.put(e.getId(), e);
        Logger.getInstance().log(new LogEvent(
                LogType.EMPLOYEE_REGISTERED, "employeeId=" + e.getId() +
                " (upsert), branch=" + e.getBranchId() + ", role=" + e.getRole()));
    }

    public Employee get(String id) { return byId.get(id); }

    public List<Employee> listByBranch(String branchId) {
        return byId.values().stream()
                .filter(e -> e.getBranchId().equals(branchId))
                .collect(Collectors.toList());
    }

    public List<Employee> listAll() {
        return new ArrayList<>(byId.values());
    }
}

