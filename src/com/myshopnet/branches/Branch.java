package com.myshopnet.branches;

import com.myshopnet.inventory.Inventory;
import com.myshopnet.employees.Employee;
import java.util.HashSet;
import java.util.Set;

public class Branch {
    private final String id;
    private final String name;
    private final Inventory inventory = new Inventory();
    private final Set<Employee> employees = new HashSet<>();

    public Branch(String id, String name) { this.id = id; this.name = name; }

    public String getId() { return id; }
    public String getName() { return name; }
    public Inventory getInventory() { return inventory; }
    public Set<Employee> getEmployees() { return employees; }
    public void addEmployee(Employee e) { employees.add(e); }
    public void removeEmployee(Employee e) { employees.remove(e); }
}
