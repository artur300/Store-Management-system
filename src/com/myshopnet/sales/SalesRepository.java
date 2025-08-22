package com.myshopnet.sales;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SalesRepository {
    private final List<Sale> all = Collections.synchronizedList(new ArrayList<>());
    public void add(Sale s) { all.add(s); }
    public List<Sale> list() { return List.copyOf(all); }
    public void clear() { all.clear(); }
}

