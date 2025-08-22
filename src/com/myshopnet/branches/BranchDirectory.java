package com.myshopnet.branches;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BranchDirectory {
    private final Map<String, Branch> byId = new ConcurrentHashMap<>();
    public void register(Branch b) { byId.put(b.getId(), b); }
    public Branch get(String id) { return byId.get(id); }
}

