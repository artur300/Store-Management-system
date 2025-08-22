package com.myshopnet.models;

import java.util.HashSet;
import java.util.Set;

public class Branch {
    private final String id;
    private final String name;
    private final Stock productsStock;
    private final Set<Employee> employees;

    public Branch(String id, String name) {
        this.id = id; this.name = name;
        productsStock = new Stock();
        employees = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public Stock getProductsStock() {
        return productsStock;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public String getName() { return name; }
//    public Inventory getInventory() { return inventory; }
//    public Set<Employee> getEmployees() { return employees; }
//    public void addEmployee(Employee e) { employees.add(e); }
//    public void removeEmployee(Employee e) { employees.remove(e); }
}
