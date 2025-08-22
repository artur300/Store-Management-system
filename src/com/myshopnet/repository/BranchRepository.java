package com.myshopnet.repository;

import com.myshopnet.models.branches.Branch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BranchRepository {
    private final Map<String, Branch> byId = new ConcurrentHashMap<>();
    public void register(Branch b) { byId.put(b.getId(), b); }
    public Branch get(String id) { return byId.get(id); }
}

