package com.myshopnet.repository;

import java.util.List;

public interface Repository<T> {
    T create(T t);
    T update(String id, T t);
    void delete(String id);
    T get(String id);
    List<T> getAll();
}
